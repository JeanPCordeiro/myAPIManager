package com.example.authservice.filter;

import com.example.authservice.service.CustomAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre d'authentification pour valider les jetons d'accès
 */
@Component
@Order(1)
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomAuthenticationService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Ignorer les requêtes d'authentification
        if (request.getRequestURI().startsWith("/auth/login") || 
            request.getRequestURI().startsWith("/auth/validate") ||
            request.getRequestURI().equals("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraire le jeton de l'en-tête Authorization
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Jeton d'authentification manquant ou invalide\"}");
            return;
        }
        
        String token = authHeader.substring(7);
        
        // Valider le jeton
        CustomAuthenticationService.TokenInfo tokenInfo = authService.validateToken(token);
        
        if (tokenInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Jeton d'authentification expiré ou invalide\"}");
            return;
        }
        
        // Ajouter les informations d'utilisateur à la requête
        request.setAttribute("username", tokenInfo.getUsername());
        request.setAttribute("role", tokenInfo.getRole());
        
        filterChain.doFilter(request, response);
    }
}
