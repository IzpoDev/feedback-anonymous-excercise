package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0  THEN true ELSE false END FROM PrivilegeEntity p WHERE p.name = :name")
    Boolean existByName(@Param("name") String name);
    @Query("select p from PrivilegeEntity p where p.name=:name")
    Optional<PrivilegeEntity> findByName(@Param("name") String name);

}
