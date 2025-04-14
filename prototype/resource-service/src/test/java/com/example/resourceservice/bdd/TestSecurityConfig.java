package com.example.resourceservice.bdd;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        // Pour les tests, nous utilisons un décodeur JWT qui accepte nos jetons de test
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
