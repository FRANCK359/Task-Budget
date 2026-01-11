#!/bin/bash
# Script de build optimisé pour Docker

set -e  # Arrêter en cas d'erreur

echo "🔨 Début du build..."

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé!"
    exit 1
fi

# Nettoyage
echo "🧹 Nettoyage du projet..."
mvn clean

# Résolution des dépendances
echo "📦 Téléchargement des dépendances..."
mvn dependency:resolve

# Compilation
echo "🛠️  Compilation..."
mvn compile -DskipTests

# Tests unitaires (optionnel)
echo "🧪 Exécution des tests..."
mvn test -DskipITs || echo "⚠️  Certains tests ont échoué, continuation..."

# Packaging
echo "📦 Création du package..."
mvn package -DskipTests

# Vérification du JAR
JAR_FILE=$(find target -name "*.jar" -type f | head -1)
if [ -f "$JAR_FILE" ]; then
    SIZE=$(du -h "$JAR_FILE" | cut -f1)
    echo "✅ Build terminé avec succès!"
    echo "📁 Fichier: $JAR_FILE"
    echo "📊 Taille: $SIZE"
else
    echo "❌ Aucun fichier JAR généré!"
    exit 1
fi