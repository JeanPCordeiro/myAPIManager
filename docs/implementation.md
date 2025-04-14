# Guide d'implémentation du socle technique pour sécuriser des ressources REST

Ce document présente les détails d'implémentation du socle technique développé pour sécuriser des ressources REST. Il complète le document d'architecture en fournissant des informations techniques sur l'implémentation des différents composants.

## Structure du projet

Le projet est organisé en plusieurs modules Maven :

```
api-security-project/
├── api-gateway/            # API Gateway basée sur Spring Cloud Gateway
├── auth-service/           # Service d'authentification et d'autorisation
├── resource-service/       # Service de ressources REST (exemple)
├── sidecar-proxy/          # Proxy sidecar pour la sécurisation des ressources
└── deployment/             # Scripts et configurations de déploiement
```

## Composants techniques

### 1. API Gateway

Implémentée avec Spring Cloud Gateway, l'API Gateway inclut :

- **LoggingFilter** : Filtre pour la journalisation des requêtes et réponses
- **SecurityFilter** : Filtre pour la détection et le blocage des attaques
- **RateLimitingConfiguration** : Configuration de la limitation de débit avec Redis
- **SecurityConfig** : Configuration de sécurité globale

### 2. Service d'authentification

Service personnalisé qui gère l'authentification et les jetons :

- **CustomAuthenticationService** : Service principal pour l'authentification
- **AuthenticationController** : API REST pour l'authentification
- **UserRepository** : Stockage des utilisateurs
- **TokenRepository** : Gestion des jetons d'accès
- **AuthenticationFilter** : Filtre pour valider les jetons

### 3. Service de gestion des droits

Service centralisé pour la gestion des droits et paramètres :

- **CentralizedAuthorizationService** : Service principal pour la gestion des droits
- **AuthorizationController** : API REST pour la gestion des droits
- **PermissionRepository** : Stockage des permissions
- **Permission** : Modèle représentant une permission

### 4. Client d'autorisation

Client utilisé par les services pour accéder au service d'autorisation :

- **AuthorizationClient** : Client pour vérifier les droits et récupérer les paramètres
- **AuthorizationClientConfig** : Configuration du client

### 5. Proxy sidecar

Conteneur déployé à côté des services de ressources :

- **SidecarProxyApplication** : Application principale du proxy
- **SidecarSecurityConfig** : Configuration de sécurité du proxy
- **TokenValidationFilter** : Filtre pour valider les jetons
- **AuthorizationFilter** : Filtre pour vérifier les autorisations

## Flux de données

### Authentification

1. L'utilisateur envoie ses identifiants à `/auth/login`
2. Le service d'authentification valide les identifiants et génère un jeton
3. Le jeton est retourné à l'utilisateur
4. L'utilisateur inclut ce jeton dans l'en-tête `Authorization` des requêtes suivantes

### Autorisation

1. Le proxy sidecar intercepte la requête et extrait le jeton
2. Le proxy valide le jeton auprès du service d'authentification
3. Le proxy vérifie les droits auprès du service d'autorisation
4. Si l'utilisateur est autorisé, la requête est transmise au service de ressources

## Sécurité

### Protection contre les attaques

Le filtre de sécurité de l'API Gateway détecte et bloque :

- Injections SQL : Détection de motifs suspects dans les paramètres
- Attaques XSS : Filtrage des balises script et événements JavaScript
- Path traversal : Blocage des tentatives d'accès aux répertoires parents
- Adresses IP malveillantes : Liste noire d'adresses IP

### Limitation de débit

La limitation de débit est configurée dans l'API Gateway :

- Utilisation de l'algorithme Token Bucket
- Configuration par utilisateur/client
- Stockage des compteurs dans Redis

## Déploiement

### Prérequis

- Kubernetes cluster
- Redis
- Base de données (PostgreSQL recommandée pour la production)

### Configuration Kubernetes

Les fichiers de configuration Kubernetes sont fournis dans le répertoire `deployment/kubernetes` :

- `api-gateway.yaml` : Déploiement de l'API Gateway
- `auth-service.yaml` : Déploiement du service d'authentification
- `resource-service.yaml` : Déploiement du service de ressources avec sidecar

### Variables d'environnement

Chaque service peut être configuré via des variables d'environnement :

- `SPRING_PROFILES_ACTIVE` : Profil Spring actif (dev, test, prod)
- `AUTH_SERVICE_URL` : URL du service d'authentification
- `REDIS_HOST` : Hôte Redis pour la limitation de débit
- `LOG_LEVEL` : Niveau de journalisation

## Extensibilité

### Ajout d'un nouveau service de ressources

Pour ajouter un nouveau service de ressources :

1. Créer un nouveau module Maven pour le service
2. Ajouter la dépendance au client d'autorisation
3. Configurer le client dans le service
4. Déployer le service avec le proxy sidecar

### Ajout de nouvelles politiques de sécurité

Pour ajouter de nouvelles politiques de sécurité :

1. Ajouter les nouvelles règles dans le service d'autorisation
2. Mettre à jour l'API d'autorisation si nécessaire
3. Configurer les nouvelles politiques via l'API de gestion des droits

## Bonnes pratiques

- Utiliser HTTPS pour toutes les communications
- Renouveler régulièrement les clés de signature des jetons
- Configurer des délais d'expiration courts pour les jetons
- Mettre en place une rotation des logs
- Effectuer des audits de sécurité réguliers
