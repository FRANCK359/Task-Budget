# Étape 1: Build avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copier les fichiers de configuration Maven d'abord
COPY pom.xml .
# Télécharger les dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build l'application
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Étape 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Installer curl pour healthcheck
RUN apk add --no-cache curl

# Créer d'abord le dossier pour les logs
RUN mkdir -p /app/logs

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Changer les permissions après création des dossiers
RUN chown -R spring:spring /app

# Passer à l'utilisateur non-root
USER spring:spring

# Copier le JAR
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Point d'entrée avec optimisations JVM
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=docker", \
    "-jar", "app.jar"]