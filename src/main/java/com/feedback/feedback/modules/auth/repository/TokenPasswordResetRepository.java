package com.feedback.feedback.modules.auth.repository;

import com.feedback.feedback.modules.auth.entity.TokenPasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenPasswordResetRepository extends JpaRepository<TokenPasswordResetEntity,Long> {
    Optional<TokenPasswordResetEntity> findByToken(String token);
}
