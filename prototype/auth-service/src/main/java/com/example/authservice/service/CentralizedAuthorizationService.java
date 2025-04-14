package com.example.authservice.service;

import com.example.authservice.model.Permission;
import com.example.authservice.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service centralisé pour la gestion des droits et des paramètres
 */
@Service
public class CentralizedAuthorizationService {

    @Autowired
    private PermissionRepository permissionRepository;
    
    // Cache des paramètres de configuration
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    /**
     * Vérifie si un utilisateur a l'autorisation pour une ressource spécifique
     * @param username Nom d'utilisateur
     * @param resource Ressource à accéder
     * @param action Action à effectuer (read, write, delete, etc.)
     * @return true si l'utilisateur est autorisé, false sinon
     */
    public boolean isAuthorized(String username, String resource, String action) {
        // Vérifier si l'utilisateur a une permission spécifique pour cette ressource et cette action
        Permission permission = permissionRepository.findByUsernameAndResourceAndAction(username, resource, action);
        
        if (permission != null) {
            return permission.isAllowed();
        }
        
        // Vérifier si l'utilisateur a une permission générale pour cette ressource
        permission = permissionRepository.findByUsernameAndResourceAndAction(username, resource, "*");
        
        if (permission != null) {
            return permission.isAllowed();
        }
        
        // Vérifier si l'utilisateur a une permission générale pour toutes les ressources
        permission = permissionRepository.findByUsernameAndResourceAndAction(username, "*", "*");
        
        if (permission != null) {
            return permission.isAllowed();
        }
        
        // Par défaut, refuser l'accès
        return false;
    }
    
    /**
     * Ajoute ou met à jour une permission pour un utilisateur
     * @param username Nom d'utilisateur
     * @param resource Ressource concernée
     * @param action Action concernée
     * @param allowed Si l'accès est autorisé ou non
     * @return La permission créée ou mise à jour
     */
    public Permission setPermission(String username, String resource, String action, boolean allowed) {
        Permission permission = permissionRepository.findByUsernameAndResourceAndAction(username, resource, action);
        
        if (permission == null) {
            permission = new Permission();
            permission.setUsername(username);
            permission.setResource(resource);
            permission.setAction(action);
        }
        
        permission.setAllowed(allowed);
        return permissionRepository.save(permission);
    }
    
    /**
     * Récupère toutes les permissions pour un utilisateur
     * @param username Nom d'utilisateur
     * @return Liste des permissions
     */
    public List<Permission> getPermissionsForUser(String username) {
        return permissionRepository.findByUsername(username);
    }
    
    /**
     * Récupère un paramètre de configuration
     * @param key Clé du paramètre
     * @param defaultValue Valeur par défaut si le paramètre n'existe pas
     * @return Valeur du paramètre
     */
    public String getConfigParameter(String key, String defaultValue) {
        // Vérifier d'abord dans le cache
        if (configCache.containsKey(key)) {
            return configCache.get(key);
        }
        
        // Sinon, récupérer depuis le référentiel
        String value = permissionRepository.getConfigParameter(key);
        
        if (value != null) {
            // Mettre en cache
            configCache.put(key, value);
            return value;
        }
        
        return defaultValue;
    }
    
    /**
     * Définit un paramètre de configuration
     * @param key Clé du paramètre
     * @param value Valeur du paramètre
     */
    public void setConfigParameter(String key, String value) {
        permissionRepository.setConfigParameter(key, value);
        
        // Mettre à jour le cache
        configCache.put(key, value);
    }
    
    /**
     * Invalide le cache des paramètres de configuration
     */
    public void invalidateConfigCache() {
        configCache.clear();
    }
}
