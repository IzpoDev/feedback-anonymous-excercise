package com.feedback.feedback.modules.auth.repository;

import com.feedback.feedback.modules.auth.entity.TokenPasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenPasswordResetRepository extends JpaRepository<TokenPasswordResetEntity,Long> {
    @Query("SELECT tpr FROM TokenPasswordResetEntity tpr WHERE tpr.token=:token and tpr.used=false")
    Optional<TokenPasswordResetEntity> findByToken(String token);
}
