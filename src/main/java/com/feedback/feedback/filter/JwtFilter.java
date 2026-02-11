package com.feedback.feedback.filter;

import com.feedback.feedback.config.CustomerUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import com.feedback.feedback.util.JwtUtil;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class JwtFilter extends OncePerRequestFilter {

    private final CustomerUserDetailService userCustomerDetailService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userCustomerDetailService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Usuario autenticado correctamente: {}", username);
                    } else {
                        log.error("Token JWT inválido para el usuario: {}", username);
                        handleJwtException(response, "Token JWT inválido", HttpStatus.UNAUTHORIZED, request.getRequestURI());
                        return;
                    }
                } else {
                    if (username == null) {
                        log.error("No se pudo extraer el nombre de usuario del token");
                    } else {
                        log.debug("El usuario ya está autenticado: {}", username);
                    }
                }

            } catch (ExpiredJwtException e) {
                log.error("Token JWT expirado: {}", e.getMessage());
                handleJwtException(response, "Token JWT expirado", HttpStatus.UNAUTHORIZED, request.getRequestURI());
                return;

            } catch (JwtException e) {
                log.error("Error procesando token JWT: {}", e.getMessage());
                handleJwtException(response, "Token JWT inválido: " + e.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
                return;

            } catch (Exception e) {
                log.error("Error inesperado en el filtro JWT: {}", e.getMessage());
                handleJwtException(response, "Error de autenticación: " + e.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(HttpServletResponse response, String message, HttpStatus status, String path) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("SalvaFoodAppAuthError", "JWT_ERROR");

        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                now, status.value(), status.getReasonPhrase(), message, path
        );

        response.getWriter().write(jsonResponse);
    }
}

