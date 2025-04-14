package com.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString();
        
        // Journalisation de la requête entrante
        logger.info("Requête entrante - ID: {}, Méthode: {}, URI: {}, Adresse IP: {}, Timestamp: {}",
                requestId,
                request.getMethod(),
                request.getURI(),
                request.getRemoteAddress(),
                LocalDateTime.now());
        
        // Ajout d'un en-tête pour le suivi
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();
        
        // Mesure du temps de traitement
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logger.info("Requête terminée - ID: {}, Statut: {}, Durée: {} ms",
                            requestId,
                            exchange.getResponse().getStatusCode(),
                            duration);
                }));
    }

    @Override
    public int getOrder() {
        // Exécution en premier dans la chaîne de filtres
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
