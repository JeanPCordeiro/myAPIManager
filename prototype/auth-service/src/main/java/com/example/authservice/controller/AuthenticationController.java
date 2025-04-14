package com.example.authservice.controller;

import com.example.authservice.service.CustomAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour le service d'authentification personnalisé
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private CustomAuthenticationService authService;

    /**
     * Point d'entrée pour l'authentification des utilisateurs
     * @param credentials Informations d'identification (nom d'utilisateur et mot de passe)
     * @return Jeton d'accès si l'authentification réussit
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().build();
        }
        
        String token = authService.authenticate(username, password);
        
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        
        CustomAuthenticationService.TokenInfo tokenInfo = authService.validateToken(token);
        response.put("username", tokenInfo.getUsername());
        response.put("role", tokenInfo.getRole());
        response.put("expiresIn", (tokenInfo.getExpirationTime() - System.currentTimeMillis()) / 1000);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Point d'entrée pour la validation des jetons
     * @param token Jeton à valider
     * @return Informations sur le jeton si valide
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody Map<String, String> tokenRequest) {
        String token = tokenRequest.get("token");
        
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        
        CustomAuthenticationService.TokenInfo tokenInfo = authService.validateToken(token);
        
        if (tokenInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("username", tokenInfo.getUsername());
        response.put("role", tokenInfo.getRole());
        response.put("expiresIn", (tokenInfo.getExpirationTime() - System.currentTimeMillis()) / 1000);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Point d'entrée pour la déconnexion (révocation de jeton)
     * @param token Jeton à révoquer
     * @return Confirmation de la révocation
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, String> tokenRequest) {
        String token = tokenRequest.get("token");
        
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        
        authService.revokeToken(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Point d'entrée pour la création d'un nouvel utilisateur
     * @param userDetails Détails de l'utilisateur à créer
     * @return Confirmation de la création
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> userDetails) {
        String username = userDetails.get("username");
        String password = userDetails.get("password");
        String role = userDetails.get("role");
        
        if (username == null || password == null || role == null) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean created = authService.createUser(username, password, role);
        
        Map<String, Object> response = new HashMap<>();
        
        if (created) {
            response.put("message", "Utilisateur créé avec succès");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("message", "L'utilisateur existe déjà");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}
