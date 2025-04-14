package com.example.resourceservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/resource")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getAdminResource(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ressource admin accessible");
        response.put("principal", jwt.getSubject());
        response.put("authorities", jwt.getClaims());
        return response;
    }
}
