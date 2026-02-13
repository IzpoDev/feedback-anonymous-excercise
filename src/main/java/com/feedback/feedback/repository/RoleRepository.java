package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0  THEN true ELSE false END FROM RoleEntity r WHERE r.name = :name")
    Boolean existsByName(@Param("name") String name);
    @Query("select r from RoleEntity r where r.name=:name")
    Optional<RoleEntity> findByName(@Param("name") String name);
}
