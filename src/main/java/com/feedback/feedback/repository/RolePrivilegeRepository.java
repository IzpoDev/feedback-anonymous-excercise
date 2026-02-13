package com.feedback.feedback.repository;

import com.feedback.feedback.model.entity.PrivilegeEntity;
import com.feedback.feedback.model.entity.RoleEntity;
import com.feedback.feedback.model.entity.RolePrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePrivilegeRepository extends JpaRepository<RolePrivilegeEntity, Long> {

    List<RolePrivilegeEntity> findByRoleAndActive(RoleEntity role, Boolean active);
    Optional<RolePrivilegeEntity> findByRoleAndPrivilegeAndActive(RoleEntity role, PrivilegeEntity privilege, Boolean active);
    @Query("SELECT rp.privilege FROM RolePrivilegeEntity rp WHERE rp.role.id = :roleId AND rp.active = true")
    List<PrivilegeEntity> findPrivilegesByRoleId(@Param("roleId") Long roleId);
}
