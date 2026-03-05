package com.feedback.feedback.modules.privilege.repository;

import com.feedback.feedback.modules.privilege.entity.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    Boolean existsByName(String name);
    Optional<PrivilegeEntity> findByName(String name);

}
