# Kubernetes Deployment Files

Ce répertoire contient les fichiers de configuration Kubernetes pour déployer le socle technique de sécurisation d'API REST dans un environnement on-premise.

## Structure des fichiers

- `api-gateway.yaml` : Déploiement de l'API Gateway
- `auth-service.yaml` : Déploiement du service d'authentification
- `resource-service.yaml` : Déploiement du service de ressources avec sidecar
- `redis.yaml` : Déploiement de Redis pour la limitation de débit
- `namespace.yaml` : Création du namespace Kubernetes
- `configmap.yaml` : Configuration centralisée
- `secrets.yaml` : Secrets pour les mots de passe et clés

## Déploiement

Pour déployer l'ensemble de la solution :

```bash
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secrets.yaml
kubectl apply -f redis.yaml
kubectl apply -f auth-service.yaml
kubectl apply -f api-gateway.yaml
kubectl apply -f resource-service.yaml
```

## Configuration

Les variables d'environnement et autres paramètres de configuration sont définis dans `configmap.yaml`. Vous pouvez les modifier selon vos besoins avant le déploiement.

## Sécurité

Les informations sensibles (mots de passe, clés de signature, etc.) sont stockées dans `secrets.yaml`. Dans un environnement de production, vous devriez utiliser un gestionnaire de secrets comme HashiCorp Vault ou Kubernetes Secrets.

## Monitoring

Des annotations pour Prometheus sont incluses dans les déploiements pour permettre la surveillance des métriques. Vous pouvez configurer Prometheus et Grafana pour visualiser ces métriques.

## Haute disponibilité

Les déploiements sont configurés avec plusieurs réplicas pour assurer la haute disponibilité. Vous pouvez ajuster le nombre de réplicas selon vos besoins.
