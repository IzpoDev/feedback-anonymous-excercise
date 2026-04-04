package com.feedback.feedback.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Objects;

@Service
public class DeepgramService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    public DeepgramService(
            @Value("${deepgram.api.url}") String apiUrl,
            @Value("${deepgram.api.key}") String apiKey) {

        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Token " + apiKey)
                .build();
    }

    public String transcribeAudio(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de audio no puede estar vacío");
        }
        try {
            //1. Hacemos la peticion Post
            String response = restClient.post()
                    .header("Content-Type", Objects.requireNonNull(audioFile.getContentType()))
                    .body(audioFile.getBytes())
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode transcriptNode = rootNode.path("results")
                    .path("channels").path(0)
                    .path("alternatives").path(0)
                    .path("transcript");

            if (transcriptNode.isMissingNode()) {
                throw new RuntimeException("No se pudo extraer la transcripción de la respuesta de Deepgram");
            }

            return transcriptNode.asText();

        } catch (IOException e){
            throw new RuntimeException("Error al cargar el buffer del audio", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al transcribir el audio", e);
        }
    }
}
