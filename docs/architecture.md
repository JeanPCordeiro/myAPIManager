# Architecture du socle technique pour sécuriser des ressources REST

## Vue d'ensemble

L'architecture proposée vise à créer un socle technique hautement scalable pour sécuriser des ressources REST en utilisant Java Spring, OAuth 2.0 et le pattern sidecar. Cette architecture s'appuie sur les meilleures pratiques de sécurisation des API et intègre des mécanismes de limitation de débit pour protéger les ressources contre les surcharges.

## Composants principaux

### 1. API Gateway (Spring Cloud Gateway)

Le composant central de l'architecture est une API Gateway basée sur Spring Cloud Gateway qui agit comme point d'entrée unique pour toutes les requêtes vers les ressources REST. Ce composant est responsable de :

- Routage des requêtes vers les services appropriés
- Authentification des requêtes via OAuth 2.0
- Application des politiques de limitation de débit (rate limiting)
- Journalisation des requêtes pour audit et monitoring

### 2. Service d'authentification OAuth 2.0

Un service dédié à l'authentification et à l'autorisation basé sur Spring Security OAuth 2.0, responsable de :

- Gestion des jetons d'accès (émission, validation, révocation)
- Gestion des clients et des scopes
- Support des différents flux OAuth 2.0 (client credentials, authorization code, etc.)
- Intégration avec des fournisseurs d'identité externes si nécessaire

### 3. Pods Sidecar pour les ressources REST

Chaque ressource REST est déployée avec un conteneur sidecar qui agit comme un proxy de sécurité local, responsable de :

- Validation des jetons d'accès
- Application des politiques d'autorisation fine
- Filtrage des requêtes malveillantes
- Monitoring local des performances et de la sécurité

### 4. Service de limitation de débit (Rate Limiter)

Un service basé sur Redis pour implémenter la limitation de débit selon l'algorithme Token Bucket, responsable de :

- Définition des quotas par client/utilisateur
- Suivi de la consommation des API
- Application des politiques de limitation
- Génération de métriques de consommation

### 5. Service de monitoring et d'alerte

Un service dédié à la surveillance de l'ensemble du système, responsable de :

- Collecte des métriques de performance et de sécurité
- Détection des anomalies et des tentatives d'intrusion
- Génération d'alertes en cas de problème
- Visualisation des données de monitoring

## Flux d'authentification OAuth 2.0

1. Le client s'authentifie auprès du service d'authentification OAuth 2.0 et obtient un jeton d'accès.
2. Le client inclut ce jeton dans l'en-tête Authorization de ses requêtes vers l'API Gateway.
3. L'API Gateway valide le jeton auprès du service d'authentification.
4. Si le jeton est valide, la requête est transmise au service approprié via le conteneur sidecar.
5. Le conteneur sidecar effectue une validation supplémentaire du jeton et des autorisations avant de transmettre la requête au service REST.

## Mécanisme de limitation de débit

1. Chaque requête reçue par l'API Gateway est soumise au service de limitation de débit.
2. Le service utilise Redis pour stocker et gérer les compteurs de requêtes par client/utilisateur.
3. L'algorithme Token Bucket est utilisé pour déterminer si une requête peut être traitée ou si elle doit être rejetée.
4. Les paramètres de limitation (taux de remplissage, capacité maximale) sont configurables par client/utilisateur.
5. Les requêtes dépassant les limites reçoivent une réponse HTTP 429 (Too Many Requests).

## Architecture des pods sidecar

Chaque pod Kubernetes contient deux conteneurs :

1. **Conteneur principal** : Contient l'application REST développée avec Spring Boot.
2. **Conteneur sidecar** : Implémente un proxy de sécurité basé sur Spring Cloud Gateway.

Les deux conteneurs partagent le même réseau, ce qui permet au sidecar d'intercepter tout le trafic entrant destiné au conteneur principal. Le sidecar peut ainsi :

- Valider les jetons d'accès
- Appliquer des politiques d'autorisation fine
- Collecter des métriques de sécurité et de performance
- Filtrer les requêtes malveillantes

## Déploiement sur AWS Serverless

L'architecture est conçue pour être déployée sur AWS en mode serverless, en utilisant :

- AWS Lambda pour les services d'authentification et de limitation de débit
- Amazon API Gateway comme point d'entrée externe
- Amazon EKS (Elastic Kubernetes Service) pour les pods avec sidecars
- Amazon ElastiCache (Redis) pour la gestion de la limitation de débit
- AWS CloudWatch pour le monitoring et les alertes

## Considérations de scalabilité

L'architecture est conçue pour être hautement scalable grâce à :

- L'utilisation de services stateless qui peuvent être facilement répliqués
- La séparation des préoccupations entre les différents composants
- L'utilisation de Redis comme stockage distribué pour la limitation de débit
- L'architecture en microservices qui permet de scaler indépendamment chaque composant
- L'utilisation de Kubernetes pour l'orchestration des conteneurs

## Diagramme d'architecture

```
┌─────────────┐     ┌─────────────────┐     ┌───────────────────────────┐
│             │     │                 │     │ Pod Kubernetes            │
│   Client    │────▶│  API Gateway    │────▶│ ┌─────────┐  ┌─────────┐  │
│             │     │  (Spring Cloud  │     │ │ Sidecar │  │ Service │  │
└─────────────┘     │   Gateway)      │     │ │ Proxy   │─▶│ REST    │  │
                    └────────┬────────┘     │ └─────────┘  └─────────┘  │
                             │              └───────────────────────────┘
                             │
                             ▼
          ┌─────────────────────────────────┐
          │                                 │
          │  Service d'authentification     │
          │  OAuth 2.0                      │
          │  (Spring Security)              │
          │                                 │
          └───────────────┬─────────────────┘
                          │
                          ▼
          ┌─────────────────────────────────┐
          │                                 │
          │  Service de limitation de débit │
          │  (Redis + Token Bucket)         │
          │                                 │
          └─────────────────────────────────┘
```

## Prochaines étapes

1. Développer un prototype de base avec Spring Boot et Spring Security OAuth 2.0
2. Implémenter le mécanisme de limitation de débit avec Spring Cloud Gateway et Redis
3. Configurer les pods sidecar avec Kubernetes
4. Développer les tests BDD/TDD pour valider le fonctionnement du socle technique
5. Préparer le déploiement sur AWS en mode serverless
