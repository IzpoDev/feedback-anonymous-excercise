package com.feedback.feedback.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, name = "username")
    private String username;
    @Column(unique = true, name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "active")
    private Boolean active;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
}
