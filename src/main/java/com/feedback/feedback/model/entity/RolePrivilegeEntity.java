package com.feedback.feedback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_privileges")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class RolePrivilegeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    @ManyToOne
    @JoinColumn(name = "privilege_id")
    private PrivilegeEntity privilege;
    @Column(name = "active")
    private Boolean active = true;
    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

}
