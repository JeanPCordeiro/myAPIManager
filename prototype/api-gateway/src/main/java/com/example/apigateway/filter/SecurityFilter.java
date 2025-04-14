package com.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class SecurityFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    
    // Modèles de détection d'attaques courantes
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("('(''|[^'])*')|(--)|(\\b(SELECT|UPDATE|INSERT|DELETE|FROM|WHERE|DROP|ALTER|CREATE|TABLE|OR|AND|UNION)\\b)", Pattern.CASE_INSENSITIVE);
    private static final Pattern XSS_PATTERN = Pattern.compile("<script>(.*?)</script>|<.*?javascript:.*?>|<.*?\\s+on.*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile("\\.\\./|\\.\\.\\\\");
    
    // Liste d'adresses IP bloquées (à remplacer par une solution de base de données en production)
    private static final List<String> BLOCKED_IPS = List.of("192.168.1.100", "10.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIP = request.getRemoteAddress().getAddress().getHostAddress();
        
        // Vérification des adresses IP bloquées
        if (BLOCKED_IPS.contains(clientIP)) {
            logger.warn("Tentative d'accès depuis une adresse IP bloquée: {}", clientIP);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        
        // Vérification des paramètres de requête pour détecter des attaques
        String query = request.getURI().getQuery();
        if (query != null) {
            if (SQL_INJECTION_PATTERN.matcher(query).find()) {
                logger.warn("Tentative d'injection SQL détectée depuis IP: {}, Query: {}", clientIP, query);
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            
            if (XSS_PATTERN.matcher(query).find()) {
                logger.warn("Tentative d'attaque XSS détectée depuis IP: {}, Query: {}", clientIP, query);
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            
            if (PATH_TRAVERSAL_PATTERN.matcher(query).find()) {
                logger.warn("Tentative de path traversal détectée depuis IP: {}, Query: {}", clientIP, query);
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
        }
        
        // Vérification des en-têtes HTTP
        List<String> userAgents = request.getHeaders().get("User-Agent");
        if (userAgents != null && !userAgents.isEmpty()) {
            String userAgent = userAgents.get(0);
            if (userAgent.contains("sqlmap") || userAgent.contains("nikto") || userAgent.contains("nmap")) {
                logger.warn("Outil de scan de sécurité détecté depuis IP: {}, User-Agent: {}", clientIP, userAgent);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Exécution après le filtre de journalisation
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
