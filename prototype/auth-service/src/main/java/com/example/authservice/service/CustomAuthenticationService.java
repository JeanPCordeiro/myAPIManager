package com.example.authservice.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service d'authentification personnalisé qui remplace Cognito
 * Cette classe gère les utilisateurs, l'authentification et la génération de jetons
 */
@Service
public class CustomAuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Stockage en mémoire des utilisateurs (à remplacer par une base de données en production)
    private final Map<String, UserDetails> users = new ConcurrentHashMap<>();
    
    // Stockage en mémoire des jetons actifs
    private final Map<String, TokenInfo> activeTokens = new ConcurrentHashMap<>();

    /**
     * Initialise le service avec quelques utilisateurs par défaut
     */
    public void init() {
        // Ajouter des utilisateurs par défaut
        createUser("admin", "admin123", "ADMIN");
        createUser("user", "user123", "USER");
    }

    /**
     * Crée un nouvel utilisateur
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param role Rôle de l'utilisateur
     * @return true si l'utilisateur a été créé, false si le nom d'utilisateur existe déjà
     */
    public boolean createUser(String username, String password, String role) {
        if (users.containsKey(username)) {
            return false;
        }
        
        UserDetails user = new UserDetails();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        
        users.put(username, user);
        return true;
    }

    /**
     * Authentifie un utilisateur et génère un jeton d'accès
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return Jeton d'accès ou null si l'authentification échoue
     */
    public String authenticate(String username, String password) {
        UserDetails user = users.get(username);
        
        if (user == null || !user.isEnabled() || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        
        // Générer un jeton unique
        String token = UUID.randomUUID().toString();
        
        // Stocker les informations du jeton
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUsername(username);
        tokenInfo.setRole(user.getRole());
        tokenInfo.setCreationTime(System.currentTimeMillis());
        tokenInfo.setExpirationTime(System.currentTimeMillis() + (30 * 60 * 1000)); // 30 minutes
        
        activeTokens.put(token, tokenInfo);
        
        return token;
    }

    /**
     * Valide un jeton d'accès
     * @param token Jeton à valider
     * @return Informations sur le jeton ou null si le jeton est invalide
     */
    public TokenInfo validateToken(String token) {
        TokenInfo tokenInfo = activeTokens.get(token);
        
        if (tokenInfo == null) {
            return null;
        }
        
        // Vérifier si le jeton a expiré
        if (System.currentTimeMillis() > tokenInfo.getExpirationTime()) {
            activeTokens.remove(token);
            return null;
        }
        
        return tokenInfo;
    }

    /**
     * Révoque un jeton d'accès
     * @param token Jeton à révoquer
     */
    public void revokeToken(String token) {
        activeTokens.remove(token);
    }

    /**
     * Classe interne pour stocker les détails d'un utilisateur
     */
    public static class UserDetails {
        private String username;
        private String password;
        private String role;
        private boolean enabled;
        
        // Getters et setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    /**
     * Classe interne pour stocker les informations d'un jeton
     */
    public static class TokenInfo {
        private String username;
        private String role;
        private long creationTime;
        private long expirationTime;
        
        // Getters et setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public long getCreationTime() { return creationTime; }
        public void setCreationTime(long creationTime) { this.creationTime = creationTime; }
        
        public long getExpirationTime() { return expirationTime; }
        public void setExpirationTime(long expirationTime) { this.expirationTime = expirationTime; }
    }
}
