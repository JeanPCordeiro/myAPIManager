package com.example.authservice.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant un utilisateur dans le système
 */
public class User {
    private String username;
    private String password;
    private String role;
    private boolean enabled;
    private List<String> permissions;

    public User() {
        this.permissions = new ArrayList<>();
        this.enabled = true;
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.permissions = new ArrayList<>();
        this.enabled = true;
    }

    // Getters et setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }
}
