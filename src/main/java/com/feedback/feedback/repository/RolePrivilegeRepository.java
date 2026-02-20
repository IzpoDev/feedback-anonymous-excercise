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
    @Query("SELECT rp FROM RolePrivilegeEntity rp WHERE rp.role=:role AND rp.privilege=:privilege AND rp.active=:active")
    Optional<RolePrivilegeEntity> findByRoleAndPrivilegeAndActive(@Param("role") RoleEntity role,@Param("privilege") PrivilegeEntity privilege,@Param("active") Boolean active);
    @Query("SELECT rp FROM RolePrivilegeEntity rp WHERE rp.role.id = :role_id AND rp.privilege.id = :priv_id")
    Optional<RolePrivilegeEntity> findByRoleIdAndPrivilegesId(@Param("role_id") Long roleId,@Param("priv_id") Long privilegeId);
    @Query("SELECT rp.privilege FROM RolePrivilegeEntity rp WHERE rp.role.id = :roleId AND rp.active = true")
    List<PrivilegeEntity> findPrivilegesByRoleId(@Param("roleId") Long roleId);
}
