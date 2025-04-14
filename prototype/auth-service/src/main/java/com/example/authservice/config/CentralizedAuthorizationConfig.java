package com.example.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration pour la gestion centralisée des droits et paramètres
 */
@Configuration
public class CentralizedAuthorizationConfig {

    @Value("${authorization.server.url:http://localhost:8081}")
    private String authorizationServerUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CentralizedAuthorizationManager centralizationAuthorizationManager(RestTemplate restTemplate) {
        return new CentralizedAuthorizationManager(restTemplate, authorizationServerUrl);
    }
}
