package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0  THEN true ELSE false END FROM RoleEntity r WHERE r.name = :name")
    Boolean existsByName(@Param("name") String name);


}
