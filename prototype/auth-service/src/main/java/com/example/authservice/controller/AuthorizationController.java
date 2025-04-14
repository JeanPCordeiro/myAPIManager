package com.example.authservice.controller;

import com.example.authservice.model.Permission;
import com.example.authservice.service.CentralizedAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion centralisée des droits et paramètres
 */
@RestController
@RequestMapping("/authorization")
public class AuthorizationController {

    @Autowired
    private CentralizedAuthorizationService authorizationService;

    /**
     * Vérifie si un utilisateur est autorisé à accéder à une ressource
     * @param request Requête d'autorisation
     * @return Résultat de l'autorisation
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuthorization(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String resource = request.get("resource");
        String action = request.get("action");
        
        if (username == null || resource == null || action == null) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean isAuthorized = authorizationService.isAuthorized(username, resource, action);
        
        Map<String, Object> response = new HashMap<>();
        response.put("authorized", isAuthorized);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Définit une permission pour un utilisateur
     * @param request Requête de définition de permission
     * @return Permission créée ou mise à jour
     */
    @PostMapping("/permissions")
    public ResponseEntity<Permission> setPermission(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String resource = (String) request.get("resource");
        String action = (String) request.get("action");
        Boolean allowed = (Boolean) request.get("allowed");
        
        if (username == null || resource == null || action == null || allowed == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Permission permission = authorizationService.setPermission(username, resource, action, allowed);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    /**
     * Récupère toutes les permissions pour un utilisateur
     * @param username Nom d'utilisateur
     * @return Liste des permissions
     */
    @GetMapping("/permissions/{username}")
    public ResponseEntity<List<Permission>> getPermissionsForUser(@PathVariable String username) {
        List<Permission> permissions = authorizationService.getPermissionsForUser(username);
        
        return ResponseEntity.ok(permissions);
    }

    /**
     * Récupère un paramètre de configuration
     * @param key Clé du paramètre
     * @return Valeur du paramètre
     */
    @GetMapping("/config/{key}")
    public ResponseEntity<Map<String, String>> getConfigParameter(@PathVariable String key) {
        String value = authorizationService.getConfigParameter(key, null);
        
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Définit un paramètre de configuration
     * @param request Requête de définition de paramètre
     * @return Confirmation de la définition
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, String>> setConfigParameter(@RequestBody Map<String, String> request) {
        String key = request.get("key");
        String value = request.get("value");
        
        if (key == null || value == null) {
            return ResponseEntity.badRequest().build();
        }
        
        authorizationService.setConfigParameter(key, value);
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Invalide le cache des paramètres de configuration
     * @return Confirmation de l'invalidation
     */
    @PostMapping("/config/invalidate-cache")
    public ResponseEntity<Map<String, String>> invalidateConfigCache() {
        authorizationService.invalidateConfigCache();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache invalidé avec succès");
        
        return ResponseEntity.ok(response);
    }
}
