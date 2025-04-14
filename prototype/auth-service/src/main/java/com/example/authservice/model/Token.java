package com.example.authservice.model;

/**
 * Modèle représentant un jeton d'authentification
 */
public class Token {
    private String tokenId;
    private String username;
    private String role;
    private long creationTime;
    private long expirationTime;

    public Token() {
    }

    public Token(String tokenId, String username, String role, long creationTime, long expirationTime) {
        this.tokenId = tokenId;
        this.username = username;
        this.role = role;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    // Getters et setters
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}
