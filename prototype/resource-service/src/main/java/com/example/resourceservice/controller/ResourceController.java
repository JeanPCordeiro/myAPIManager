package com.example.resourceservice.controller;

import com.example.resourceservice.client.AuthorizationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour les ressources protégées
 * Utilise le client d'autorisation centralisé pour vérifier les droits d'accès
 */
@RestController
public class ResourceController {

    @Autowired
    private AuthorizationClient authorizationClient;

    /**
     * Point d'entrée pour une ressource publique
     * @return Données de la ressource publique
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicResource() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ressource publique accessible");
        return ResponseEntity.ok(response);
    }

    /**
     * Point d'entrée pour une ressource protégée
     * @param username Nom d'utilisateur (injecté par le filtre d'authentification)
     * @return Données de la ressource protégée
     */
    @GetMapping("/resource")
    public ResponseEntity<Map<String, Object>> getProtectedResource(@RequestAttribute("username") String username) {
        // Vérifier si l'utilisateur a le droit d'accéder à cette ressource
        boolean isAuthorized = authorizationClient.isAuthorized(username, "resource", "read");
        
        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ressource protégée accessible");
        response.put("username", username);
        
        // Récupérer un paramètre de configuration centralisé
        String welcomeMessage = authorizationClient.getConfigParameter("welcome.message", "Bienvenue sur l'API sécurisée");
        response.put("welcomeMessage", welcomeMessage);
        
        return ResponseEntity.ok(response);
    }
}
