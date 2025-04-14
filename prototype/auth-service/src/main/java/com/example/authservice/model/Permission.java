package com.example.authservice.model;

/**
 * Modèle représentant une permission dans le système
 */
public class Permission {
    private Long id;
    private String username;
    private String resource;
    private String action;
    private boolean allowed;

    public Permission() {
    }

    public Permission(String username, String resource, String action, boolean allowed) {
        this.username = username;
        this.resource = resource;
        this.action = action;
        this.allowed = allowed;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
