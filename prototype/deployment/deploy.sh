#!/bin/bash

# Script de déploiement du socle technique de sécurisation d'API REST sur AWS

# Vérification des prérequis
if ! command -v aws &> /dev/null; then
    echo "AWS CLI n'est pas installé. Veuillez l'installer avant de continuer."
    exit 1
fi

if ! command -v sam &> /dev/null; then
    echo "AWS SAM CLI n'est pas installé. Veuillez l'installer avant de continuer."
    exit 1
fi

# Configuration des variables
STACK_NAME="api-security-stack"
ENVIRONMENT=${1:-dev}  # Utiliser le premier argument comme environnement, sinon 'dev' par défaut
S3_BUCKET="api-security-deployment-bucket"
REGION="eu-west-1"

echo "Déploiement du socle technique de sécurisation d'API REST sur AWS..."
echo "Environnement: $ENVIRONMENT"
echo "Région: $REGION"

# Création du bucket S3 s'il n'existe pas
aws s3api head-bucket --bucket $S3_BUCKET 2>/dev/null || aws s3 mb s3://$S3_BUCKET --region $REGION

# Construction des packages Maven
echo "Construction des packages Maven..."
cd ..
mvn clean package

# Déploiement avec SAM
echo "Déploiement avec AWS SAM..."
cd deployment
sam package --template-file template.yaml --output-template-file packaged.yaml --s3-bucket $S3_BUCKET
sam deploy --template-file packaged.yaml --stack-name $STACK_NAME-$ENVIRONMENT --capabilities CAPABILITY_IAM --parameter-overrides Environment=$ENVIRONMENT

# Récupération des informations de sortie
echo "Récupération des informations de déploiement..."
API_URL=$(aws cloudformation describe-stacks --stack-name $STACK_NAME-$ENVIRONMENT --query "Stacks[0].Outputs[?OutputKey=='ApiGatewayUrl'].OutputValue" --output text)
USER_POOL_ID=$(aws cloudformation describe-stacks --stack-name $STACK_NAME-$ENVIRONMENT --query "Stacks[0].Outputs[?OutputKey=='CognitoUserPoolId'].OutputValue" --output text)
CLIENT_ID=$(aws cloudformation describe-stacks --stack-name $STACK_NAME-$ENVIRONMENT --query "Stacks[0].Outputs[?OutputKey=='CognitoUserPoolClientId'].OutputValue" --output text)

echo "Déploiement terminé avec succès!"
echo "URL de l'API: $API_URL"
echo "ID du User Pool Cognito: $USER_POOL_ID"
echo "ID du Client Cognito: $CLIENT_ID"
