# Guide de déploiement serverless sur AWS

Ce document décrit le processus de déploiement du socle technique de sécurisation d'API REST sur AWS en mode serverless.

## Prérequis

Avant de commencer le déploiement, assurez-vous d'avoir installé les outils suivants :

- AWS CLI (version 2.0 ou supérieure)
- AWS SAM CLI (version 1.0 ou supérieure)
- Java 11 ou supérieur
- Maven 3.6 ou supérieur

Vous devez également avoir configuré vos identifiants AWS avec les autorisations nécessaires pour créer et gérer les ressources suivantes :
- AWS Lambda
- Amazon API Gateway
- Amazon Cognito
- Amazon ElastiCache
- Amazon VPC et ressources associées
- AWS CloudFormation

## Architecture du déploiement

Le déploiement serverless utilise les services AWS suivants :

1. **AWS Lambda** : Héberge les fonctions pour l'API Gateway, le service d'authentification et le service de ressources.
2. **Amazon API Gateway** : Point d'entrée pour toutes les requêtes API, gère l'authentification et la limitation de débit.
3. **Amazon Cognito** : Fournit l'authentification et l'autorisation OAuth 2.0.
4. **Amazon ElastiCache (Redis)** : Utilisé pour la limitation de débit.
5. **Amazon VPC** : Fournit l'isolation réseau pour les composants.

## Structure du projet

```
prototype/
├── api-gateway/
├── auth-service/
├── resource-service/
├── sidecar-proxy/
└── deployment/
    ├── template.yaml    # Template CloudFormation/SAM
    └── deploy.sh        # Script de déploiement
```

## Processus de déploiement

### 1. Préparation de l'environnement

Assurez-vous que vos identifiants AWS sont configurés correctement :

```bash
aws configure
```

### 2. Construction des packages

Exécutez la commande Maven pour construire tous les modules :

```bash
cd prototype
mvn clean package
```

### 3. Déploiement avec AWS SAM

Utilisez le script de déploiement fourni :

```bash
cd deployment
chmod +x deploy.sh
./deploy.sh [environment]
```

Où `[environment]` est l'environnement cible (dev, test, prod). Si non spécifié, l'environnement par défaut est "dev".

Le script effectue les opérations suivantes :
- Vérifie les prérequis
- Crée un bucket S3 pour le déploiement si nécessaire
- Construit les packages Maven
- Empaquette et déploie l'application avec AWS SAM
- Affiche les informations de sortie (URL de l'API, ID du User Pool Cognito, etc.)

### 4. Vérification du déploiement

Une fois le déploiement terminé, vous pouvez vérifier que tout fonctionne correctement en utilisant les commandes suivantes :

```bash
# Vérifier le statut de la pile CloudFormation
aws cloudformation describe-stacks --stack-name api-security-stack-[environment]

# Tester l'API
curl -X GET https://[api-id].execute-api.[region].amazonaws.com/[environment]/public
```

### 5. Obtention d'un jeton d'accès

Pour accéder aux ressources protégées, vous devez obtenir un jeton d'accès auprès de Cognito :

```bash
aws cognito-idp initiate-auth \
  --client-id [client-id] \
  --auth-flow USER_PASSWORD_AUTH \
  --auth-parameters USERNAME=[username],PASSWORD=[password]
```

### 6. Accès aux ressources protégées

Utilisez le jeton d'accès pour accéder aux ressources protégées :

```bash
curl -X GET \
  https://[api-id].execute-api.[region].amazonaws.com/[environment]/resource \
  -H "Authorization: Bearer [access-token]"
```

## Nettoyage des ressources

Pour supprimer toutes les ressources créées par le déploiement :

```bash
aws cloudformation delete-stack --stack-name api-security-stack-[environment]
```

## Dépannage

### Problèmes courants

1. **Erreur de déploiement CloudFormation** : Vérifiez les journaux CloudFormation pour identifier la cause de l'erreur.
2. **Erreur d'authentification** : Assurez-vous que le jeton d'accès est valide et n'a pas expiré.
3. **Erreur de limitation de débit** : Si vous recevez une erreur 429, cela signifie que vous avez dépassé la limite de requêtes autorisées.

### Journaux

Pour consulter les journaux des fonctions Lambda :

```bash
aws logs filter-log-events --log-group-name /aws/lambda/api-security-stack-[environment]-[function-name]
```

## Personnalisation

Vous pouvez personnaliser le déploiement en modifiant les fichiers suivants :

- `template.yaml` : Modifiez les paramètres de l'infrastructure AWS.
- `deploy.sh` : Ajustez les paramètres de déploiement selon vos besoins.
- Fichiers de configuration des services : Modifiez les fichiers `application.yml` dans chaque module pour ajuster les paramètres des services.
