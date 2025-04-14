# Architecture du socle technique pour sécuriser des ressources REST

## Vue d'ensemble

Ce document présente l'architecture complète du socle technique développé pour sécuriser des ressources REST. Cette solution est conçue pour être déployée on-premise et offre une gestion centralisée des droits et des paramètres.

## Composants principaux

L'architecture se compose des éléments suivants :

### 1. API Gateway (Spring Cloud Gateway)

Point d'entrée unique pour toutes les requêtes API, l'API Gateway assure :
- Le routage des requêtes vers les services appropriés
- La limitation de débit (rate limiting) avec Redis
- La journalisation des requêtes pour audit
- La détection et le blocage des attaques (injection SQL, XSS, etc.)
- La configuration CORS sécurisée

### 2. Service d'authentification personnalisé

Service central qui gère :
- L'authentification des utilisateurs
- La génération et validation des jetons d'accès
- La gestion des utilisateurs et de leurs rôles

### 3. Service de gestion centralisée des droits et paramètres

Service qui centralise :
- Les politiques d'autorisation pour toutes les ressources
- Les paramètres de configuration du système
- La vérification des droits d'accès

### 4. Pods sidecar pour la sécurisation des ressources REST

Conteneurs déployés à côté des services de ressources qui :
- Interceptent toutes les requêtes entrantes
- Vérifient l'authentification et l'autorisation
- Appliquent les politiques de sécurité
- Transmettent les requêtes légitimes au service de ressources

### 5. Services de ressources REST

Services métier qui exposent les ressources REST et qui :
- Se concentrent uniquement sur la logique métier
- Délèguent la sécurité aux composants spécialisés
- Utilisent le client d'autorisation pour les vérifications de droits spécifiques

## Flux d'authentification et d'autorisation

1. L'utilisateur s'authentifie auprès du service d'authentification et reçoit un jeton d'accès
2. L'utilisateur inclut ce jeton dans l'en-tête Authorization de ses requêtes
3. L'API Gateway valide le format du jeton et applique les limitations de débit
4. Le pod sidecar intercepte la requête, vérifie l'authenticité du jeton auprès du service d'authentification
5. Le pod sidecar consulte le service de gestion des droits pour vérifier les autorisations
6. Si l'utilisateur est autorisé, la requête est transmise au service de ressources
7. Le service de ressources peut effectuer des vérifications d'autorisation supplémentaires pour des cas spécifiques

## Sécurité

Le socle technique implémente plusieurs niveaux de sécurité :

- **Authentification** : Service personnalisé avec gestion des jetons
- **Autorisation** : Gestion centralisée des droits avec vérification à plusieurs niveaux
- **Protection contre les attaques** : Filtres de sécurité pour détecter et bloquer les attaques courantes
- **Limitation de débit** : Protection contre les attaques par déni de service
- **Journalisation** : Audit complet des accès et des actions

## Déploiement

L'architecture est conçue pour un déploiement on-premise avec Kubernetes :

- Chaque composant est packagé dans un conteneur Docker
- Les pods sidecar sont déployés avec le pattern sidecar de Kubernetes
- La communication entre les services est sécurisée
- La configuration est centralisée et peut être modifiée sans redéploiement

## Extensibilité

Le socle technique est conçu pour être facilement extensible :

- Ajout de nouveaux services de ressources sans modification du code de sécurité
- Ajout de nouvelles politiques de sécurité de manière centralisée
- Intégration possible avec des systèmes d'authentification externes
- Support pour différents types d'environnements (développement, test, production)
