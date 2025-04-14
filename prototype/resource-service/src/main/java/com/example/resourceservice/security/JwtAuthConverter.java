package com.example.resourceservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> defaultAuthorities = defaultConverter.convert(jwt);
        
        // Extraction des rôles depuis les claims du JWT
        Collection<GrantedAuthority> customAuthorities = extractResourceRoles(jwt);
        
        // Fusion des autorités par défaut et personnalisées
        Set<GrantedAuthority> allAuthorities = Stream.concat(
                defaultAuthorities.stream(),
                customAuthorities.stream()
        ).collect(Collectors.toSet());
        
        return new JwtAuthenticationToken(jwt, allAuthorities, getPrincipalClaimName(jwt));
    }
    
    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = "sub";
        return jwt.getClaim(claimName);
    }
    
    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        
        if (resourceAccess == null) {
            return Set.of();
        }
        
        // Récupération des rôles pour le client "api-client"
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("api-client");
        if (clientAccess == null) {
            return Set.of();
        }
        
        Collection<String> roles = (Collection<String>) clientAccess.get("roles");
        if (roles == null) {
            return Set.of();
        }
        
        // Conversion des rôles en autorités
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
