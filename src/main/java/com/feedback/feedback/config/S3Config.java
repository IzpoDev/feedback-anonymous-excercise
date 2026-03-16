package com.feedback.feedback.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${supabase.bucket.access-key}")
    private String accessKey;

    @Value("${supabase.bucket.secret-key}")
    private String secretKey;

    @Value("${supabase.bucket.endpoint}")
    private String endpoint;

    @Value("${supabase.bucket.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        // Creamos las credenciales
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Retornamos la instancia de S3Client.
        // Spring ejecutará esto UNA SOLA VEZ y guardará el objeto en memoria (Singleton).
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint)) // Vital para Supabase (sobrescribe la URL de AWS)
                .region(Region.of(region))
                .forcePathStyle(true)
                .build();
    }
}
