package com.example.authservice.repository;

import com.example.authservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Référentiel pour stocker les utilisateurs
 * Dans une implémentation réelle, cela serait remplacé par une base de données
 */
@Repository
public class UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * @param username Nom d'utilisateur
     * @return Utilisateur trouvé ou null
     */
    public User findByUsername(String username) {
        return users.get(username);
    }

    /**
     * Sauvegarde un utilisateur
     * @param user Utilisateur à sauvegarder
     * @return Utilisateur sauvegardé
     */
    public User save(User user) {
        users.put(user.getUsername(), user);
        return user;
    }

    /**
     * Vérifie si un utilisateur existe
     * @param username Nom d'utilisateur
     * @return true si l'utilisateur existe, false sinon
     */
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }
}
