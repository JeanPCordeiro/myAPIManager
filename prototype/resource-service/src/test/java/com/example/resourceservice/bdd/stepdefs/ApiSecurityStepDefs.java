package com.example.resourceservice.bdd.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApiSecurityStepDefs {

    @LocalServerPort
    private int port;

    private String jwtToken;
    private Response response;
    private List<Response> responses = new ArrayList<>();

    @Given("l'utilisateur n'est pas authentifié")
    public void utilisateurNonAuthentifie() {
        jwtToken = null;
    }

    @Given("l'utilisateur est authentifié avec le rôle {string}")
    public void utilisateurAuthentifieAvecRole(String role) {
        // Dans un environnement de test, nous simulons l'obtention d'un jeton JWT
        // En production, cela impliquerait une requête réelle au serveur d'autorisation
        if ("USER".equals(role)) {
            jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiVVNFUiJdfQ.8oVUGjSRKUrgWS_61xqP7xnGUKP5KIGqIv6pYhN9D8o";
        } else if ("ADMIN".equals(role)) {
            jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlVTRVIiLCJBRE1JTiJdfQ.9dRrzs6aw9y0c1NfUMbF1WFkMxJO9RvxmGf1uYaLtAU";
        }
    }

    @When("l'utilisateur accède à l'URL {string}")
    public void utilisateurAccedeURL(String url) {
        RequestSpecification request = RestAssured.given().baseUri("http://localhost:" + port);
        
        if (jwtToken != null) {
            request.header("Authorization", "Bearer " + jwtToken);
        }
        
        response = request.get(url);
    }

    @When("l'utilisateur envoie {int} requêtes en moins d'une seconde")
    public void utilisateurEnvoieRequetesRapides(int nombreRequetes) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(nombreRequetes);
        List<Future<Response>> futures = new ArrayList<>();
        
        for (int i = 0; i < nombreRequetes; i++) {
            futures.add(executorService.submit(() -> {
                RequestSpecification request = RestAssured.given().baseUri("http://localhost:" + port);
                
                if (jwtToken != null) {
                    request.header("Authorization", "Bearer " + jwtToken);
                }
                
                return request.get("/resource");
            }));
        }
        
        responses.clear();
        for (Future<Response> future : futures) {
            responses.add(future.get());
        }
        
        executorService.shutdown();
    }

    @When("l'utilisateur envoie une requête avec le paramètre {string}")
    public void utilisateurEnvoieRequeteAvecParametre(String parametre) {
        RequestSpecification request = RestAssured.given().baseUri("http://localhost:" + port);
        
        if (jwtToken != null) {
            request.header("Authorization", "Bearer " + jwtToken);
        }
        
        String[] parts = parametre.split("=", 2);
        if (parts.length == 2) {
            request.queryParam(parts[0], parts[1]);
        }
        
        response = request.get("/resource");
    }

    @Then("le statut de la réponse est {int}")
    public void statutReponseEst(int statut) {
        Assertions.assertEquals(statut, response.getStatusCode());
    }

    @Then("le message de la réponse contient {string}")
    public void messageReponseContient(String message) {
        Assertions.assertTrue(response.getBody().asString().contains(message));
    }

    @Then("au moins une requête reçoit un statut {int}")
    public void auMoinsUneRequeteRecoit(int statut) {
        boolean auMoinsUne = responses.stream()
                .anyMatch(r -> r.getStatusCode() == statut);
        Assertions.assertTrue(auMoinsUne, "Aucune requête n'a reçu le statut " + statut);
    }
}
