Feature: Sécurisation des ressources REST

  En tant qu'administrateur système
  Je veux sécuriser mes ressources REST
  Afin de protéger les données sensibles et prévenir les accès non autorisés

  Scenario: Accès à une ressource publique sans authentification
    Given l'utilisateur n'est pas authentifié
    When l'utilisateur accède à l'URL "/public"
    Then le statut de la réponse est 200
    And le message de la réponse contient "Ressource publique accessible"

  Scenario: Accès à une ressource protégée sans authentification
    Given l'utilisateur n'est pas authentifié
    When l'utilisateur accède à l'URL "/resource"
    Then le statut de la réponse est 401

  Scenario: Accès à une ressource protégée avec authentification valide
    Given l'utilisateur est authentifié avec le rôle "USER"
    When l'utilisateur accède à l'URL "/resource"
    Then le statut de la réponse est 200
    And le message de la réponse contient "Ressource protégée accessible"

  Scenario: Accès à une ressource admin sans autorisation suffisante
    Given l'utilisateur est authentifié avec le rôle "USER"
    When l'utilisateur accède à l'URL "/admin/resource"
    Then le statut de la réponse est 403

  Scenario: Accès à une ressource admin avec autorisation suffisante
    Given l'utilisateur est authentifié avec le rôle "ADMIN"
    When l'utilisateur accède à l'URL "/admin/resource"
    Then le statut de la réponse est 200

  Scenario: Limitation de débit pour les requêtes excessives
    Given l'utilisateur est authentifié avec le rôle "USER"
    When l'utilisateur envoie 15 requêtes en moins d'une seconde
    Then au moins une requête reçoit un statut 429

  Scenario: Détection d'attaque par injection SQL
    Given l'utilisateur est authentifié avec le rôle "USER"
    When l'utilisateur envoie une requête avec le paramètre "query=SELECT * FROM users"
    Then le statut de la réponse est 400

  Scenario: Détection d'attaque XSS
    Given l'utilisateur est authentifié avec le rôle "USER"
    When l'utilisateur envoie une requête avec le paramètre "query=<script>alert('XSS')</script>"
    Then le statut de la réponse est 400
