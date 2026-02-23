package com.feedback.feedback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_password_reset")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenPasswordResetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, name = "token")
    private String token;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Column(name = "expire_date")
    private LocalDateTime expireDate;
    @Column(name = "used")
    private Boolean used;
}
