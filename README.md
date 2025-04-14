# myAPIManager

## Socle technique pour sécuriser des ressources REST

Ce projet fournit un socle technique complet pour sécuriser des ressources REST, avec une approche on-premise et une gestion centralisée des droits et paramètres.

## Caractéristiques principales

- **API Gateway** : Point d'entrée unique avec routage, limitation de débit et détection d'attaques
- **Authentification OAuth 2.0 personnalisée** : Service d'authentification complet sans dépendances cloud
- **Gestion centralisée des droits** : Politiques d'autorisation et paramètres centralisés
- **Architecture avec pods sidecar** : Sécurisation des ressources REST via des conteneurs sidecar
- **Haute scalabilité** : Architecture conçue pour supporter une charge importante
- **Déploiement on-premise** : Configuration Kubernetes complète pour un déploiement sur site

## Structure du projet

```
myAPIManager/
├── docs/                           # Documentation complète
│   ├── architecture-finale.md      # Architecture détaillée
│   ├── implementation.md           # Guide d'implémentation
│   └── resume.md                   # Résumé de la solution
├── prototype/                      # Code source du prototype
│   ├── api-gateway/                # API Gateway (Spring Cloud Gateway)
│   ├── auth-service/               # Service d'authentification et d'autorisation
│   ├── resource-service/           # Service de ressources REST (exemple)
│   ├── sidecar-proxy/              # Proxy sidecar pour la sécurisation
│   └── deployment/                 # Scripts et configurations de déploiement
│       └── kubernetes/             # Fichiers de configuration Kubernetes
```

## Technologies utilisées

- **Java Spring** : Framework principal pour tous les composants
- **Spring Security** : Pour l'authentification et l'autorisation
- **Spring Cloud Gateway** : Pour l'API Gateway
- **Kubernetes** : Pour le déploiement et l'orchestration des conteneurs
- **Redis** : Pour la limitation de débit
- **JUnit & Cucumber** : Pour les tests BDD/TDD

## Démarrage rapide

### Prérequis

- JDK 11 ou supérieur
- Maven 3.6 ou supérieur
- Docker et Kubernetes
- Redis

### Installation

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/votre-organisation/myAPIManager.git
   cd myAPIManager
   ```

2. Compiler le projet :
   ```bash
   mvn clean package
   ```

3. Déployer sur Kubernetes :
   ```bash
   kubectl apply -f prototype/deployment/kubernetes/namespace.yaml
   kubectl apply -f prototype/deployment/kubernetes/configmap.yaml
   kubectl apply -f prototype/deployment/kubernetes/secrets.yaml
   kubectl apply -f prototype/deployment/kubernetes/redis.yaml
   kubectl apply -f prototype/deployment/kubernetes/auth-service.yaml
   kubectl apply -f prototype/deployment/kubernetes/api-gateway.yaml
   kubectl apply -f prototype/deployment/kubernetes/resource-service.yaml
   ```

## Documentation

Pour une documentation complète, consultez les fichiers dans le répertoire `docs/` :

- [Architecture détaillée](docs/architecture-finale.md)
- [Guide d'implémentation](docs/implementation.md)
- [Résumé de la solution](docs/resume.md)

## Fonctionnalités de sécurité

Le socle technique implémente plusieurs niveaux de sécurité :

- **Authentification robuste** : Service OAuth 2.0 personnalisé avec gestion des jetons
- **Autorisation centralisée** : Vérification des droits d'accès à plusieurs niveaux
- **Limitation de débit** : Protection contre les attaques par déni de service
- **Détection d'attaques** : Filtres pour bloquer les injections SQL, XSS, etc.
- **Journalisation sécurisée** : Audit complet des accès et des actions

## Extensibilité

Le socle technique est conçu pour être facilement extensible :

- Ajout de nouveaux services de ressources sans modification du code de sécurité
- Configuration centralisée des politiques de sécurité
- Architecture modulaire permettant le remplacement de composants

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à soumettre des pull requests ou à ouvrir des issues pour améliorer ce projet.

## Licence

Ce projet est sous licence [insérer votre licence ici].
