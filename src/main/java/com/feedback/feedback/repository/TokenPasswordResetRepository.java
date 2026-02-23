package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.TokenPasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenPasswordResetRepository extends JpaRepository<TokenPasswordResetEntity,Long> {
    Optional<TokenPasswordResetEntity> findByToken(String token);
}
