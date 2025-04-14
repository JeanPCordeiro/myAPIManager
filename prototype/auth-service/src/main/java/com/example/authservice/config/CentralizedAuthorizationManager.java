package com.example.authservice.config;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire centralisé des autorisations et paramètres
 * Cette classe permet de centraliser la gestion des droits et des paramètres
 * pour tous les services du socle technique.
 */
public class CentralizedAuthorizationManager {

    private final RestTemplate restTemplate;
    private final String authorizationServerUrl;
    
    // Cache local pour les politiques d'autorisation
    private final ConcurrentHashMap<String, Object> policyCache = new ConcurrentHashMap<>();
    
    // Cache local pour les paramètres de configuration
    private final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();

    public CentralizedAuthorizationManager(RestTemplate restTemplate, String authorizationServerUrl) {
        this.restTemplate = restTemplate;
        this.authorizationServerUrl = authorizationServerUrl;
    }

    /**
     * Vérifie si un utilisateur a l'autorisation pour une ressource spécifique
     * @param userId Identifiant de l'utilisateur
     * @param resource Ressource à accéder
     * @param action Action à effectuer (read, write, delete, etc.)
     * @return true si l'utilisateur est autorisé, false sinon
     */
    public boolean isAuthorized(String userId, String resource, String action) {
        String cacheKey = userId + ":" + resource + ":" + action;
        
        // Vérifier d'abord dans le cache local
        if (policyCache.containsKey(cacheKey)) {
            return (boolean) policyCache.get(cacheKey);
        }
        
        // Si non trouvé dans le cache, interroger le serveur d'autorisation
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("resource", resource);
        requestBody.put("action", action);
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    authorizationServerUrl + "/authorize",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            boolean isAuthorized = (boolean) response.getBody().get("authorized");
            
            // Mettre en cache le résultat
            policyCache.put(cacheKey, isAuthorized);
            
            return isAuthorized;
        } catch (Exception e) {
            // En cas d'erreur, refuser l'accès par défaut
            return false;
        }
    }
    
    /**
     * Récupère un paramètre de configuration centralisé
     * @param paramName Nom du paramètre
     * @param defaultValue Valeur par défaut si le paramètre n'est pas trouvé
     * @return La valeur du paramètre
     */
    public String getConfigParameter(String paramName, String defaultValue) {
        // Vérifier d'abord dans le cache local
        if (configCache.containsKey(paramName)) {
            return configCache.get(paramName);
        }
        
        // Si non trouvé dans le cache, interroger le serveur de configuration
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    authorizationServerUrl + "/config/" + paramName,
                    Map.class
            );
            
            String value = (String) response.getBody().get("value");
            
            // Mettre en cache le résultat
            configCache.put(paramName, value);
            
            return value;
        } catch (Exception e) {
            // En cas d'erreur, retourner la valeur par défaut
            return defaultValue;
        }
    }
    
    /**
     * Invalide le cache des politiques d'autorisation
     */
    public void invalidatePolicyCache() {
        policyCache.clear();
    }
    
    /**
     * Invalide le cache des paramètres de configuration
     */
    public void invalidateConfigCache() {
        configCache.clear();
    }
}
