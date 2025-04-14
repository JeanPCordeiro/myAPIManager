package com.example.authservice.repository;

import com.example.authservice.model.Permission;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Référentiel pour stocker les permissions et les paramètres de configuration
 * Dans une implémentation réelle, cela serait remplacé par une base de données
 */
@Repository
public class PermissionRepository {

    private final Map<Long, Permission> permissions = new ConcurrentHashMap<>();
    private final Map<String, String> configParameters = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Trouve une permission par utilisateur, ressource et action
     * @param username Nom d'utilisateur
     * @param resource Ressource concernée
     * @param action Action concernée
     * @return Permission trouvée ou null
     */
    public Permission findByUsernameAndResourceAndAction(String username, String resource, String action) {
        return permissions.values().stream()
                .filter(p -> p.getUsername().equals(username) && 
                             p.getResource().equals(resource) && 
                             p.getAction().equals(action))
                .findFirst()
                .orElse(null);
    }

    /**
     * Trouve toutes les permissions pour un utilisateur
     * @param username Nom d'utilisateur
     * @return Liste des permissions
     */
    public List<Permission> findByUsername(String username) {
        return permissions.values().stream()
                .filter(p -> p.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    /**
     * Sauvegarde une permission
     * @param permission Permission à sauvegarder
     * @return Permission sauvegardée
     */
    public Permission save(Permission permission) {
        if (permission.getId() == null) {
            permission.setId(idGenerator.getAndIncrement());
        }
        permissions.put(permission.getId(), permission);
        return permission;
    }

    /**
     * Supprime une permission
     * @param id Identifiant de la permission à supprimer
     */
    public void deleteById(Long id) {
        permissions.remove(id);
    }

    /**
     * Récupère un paramètre de configuration
     * @param key Clé du paramètre
     * @return Valeur du paramètre ou null
     */
    public String getConfigParameter(String key) {
        return configParameters.get(key);
    }

    /**
     * Définit un paramètre de configuration
     * @param key Clé du paramètre
     * @param value Valeur du paramètre
     */
    public void setConfigParameter(String key, String value) {
        configParameters.put(key, value);
    }

    /**
     * Supprime un paramètre de configuration
     * @param key Clé du paramètre à supprimer
     */
    public void deleteConfigParameter(String key) {
        configParameters.remove(key);
    }

    /**
     * Récupère tous les paramètres de configuration
     * @return Map des paramètres de configuration
     */
    public Map<String, String> getAllConfigParameters() {
        return new ConcurrentHashMap<>(configParameters);
    }
}
