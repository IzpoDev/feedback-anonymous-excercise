package com.feedback.feedback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
// Con este atributo, le decimos a Spring que use nuestro bean personalizado
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class JpaAuditingConfig {
}