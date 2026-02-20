package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Boolean existsByName(String name);

    Optional<RoleEntity> findByName(String name);
}
