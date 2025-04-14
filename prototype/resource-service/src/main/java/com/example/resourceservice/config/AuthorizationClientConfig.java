package com.example.resourceservice.config;

import com.example.resourceservice.client.AuthorizationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration pour l'intégration avec le service d'autorisation centralisé
 */
@Configuration
public class AuthorizationClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public AuthorizationClient authorizationClient() {
        return new AuthorizationClient();
    }
}
