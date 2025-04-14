package com.example.resourceservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Client pour communiquer avec le service d'autorisation centralisé
 */
@Component
public class AuthorizationClient {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;
    
    /**
     * Vérifie si un utilisateur est autorisé à accéder à une ressource
     * @param username Nom d'utilisateur
     * @param resource Ressource à accéder
     * @param action Action à effectuer (read, write, delete, etc.)
     * @return true si l'utilisateur est autorisé, false sinon
     */
    public boolean isAuthorized(String username, String resource, String action) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("resource", resource);
        requestBody.put("action", action);
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    authServiceUrl + "/authorization/check",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            return (boolean) response.getBody().get("authorized");
        } catch (Exception e) {
            // En cas d'erreur, refuser l'accès par défaut
            return false;
        }
    }
    
    /**
     * Récupère un paramètre de configuration
     * @param key Clé du paramètre
     * @param defaultValue Valeur par défaut si le paramètre n'est pas trouvé
     * @return Valeur du paramètre
     */
    public String getConfigParameter(String key, String defaultValue) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    authServiceUrl + "/authorization/config/" + key,
                    Map.class
            );
            
            return (String) response.getBody().get("value");
        } catch (Exception e) {
            // En cas d'erreur, retourner la valeur par défaut
            return defaultValue;
        }
    }
}
