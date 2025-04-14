package com.example.resourceservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ConfigurableApplicationContext applicationContext;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        if (applicationContext == null) {
            // Initialisation de l'application Spring Boot
            applicationContext = SpringApplication.run(ResourceServiceApplication.class);
        }

        // Traitement de la requête par le contrôleur Spring
        // Dans une implémentation réelle, il faudrait adapter la requête Lambda pour Spring
        // et convertir la réponse Spring en réponse Lambda

        // Exemple de réponse simple
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        
        response.setBody("{\"message\":\"Resource Service Lambda Handler\"}");
        
        return response;
    }
}
