package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.time.Duration;

@Configuration
public class JwtConfig {

    @Bean
    public OAuth2TokenValidator<Jwt> jwtTimeValidator() {
        // Réduire la tolérance d'horloge à 30 secondes pour renforcer la sécurité
        return new JwtTimestampValidator(Duration.ofSeconds(30));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:8081/.well-known/jwks.json")
                .build();
        
        // Ajouter des validateurs personnalisés
        decoder.setJwtValidator(jwtTimeValidator());
        
        return decoder;
    }
}
