# Task Manager – Backend API (Java 21)

## 📌 Description

**Task Manager** est une application backend complète développée avec **Spring Boot 3** et **Java 21** pour la gestion de tâches et de budgets, intégrant des fonctionnalités d’**Intelligence Artificielle**.  
L’API propose un système d’authentification sécurisé basé sur **JWT**, une gestion avancée des tâches, un suivi budgétaire détaillé et des analyses intelligentes via l’IA.

---

## 🚀 Fonctionnalités principales

### 🔐 Authentification & Sécurité
- Inscription et connexion avec **JWT**
- Gestion des rôles (**ADMIN / USER**)
- Protection des endpoints avec **Spring Security**
- Validation et expiration des tokens JWT
- Configuration **CORS**
- Sécurisation des routes sensibles

---

### 📋 Gestion des Tâches
- CRUD complet des tâches
- Attribution des tâches aux utilisateurs
- Suivi des dates limites et des coûts
- Statistiques de productivité
- Catégorisation automatique des tâches via l’IA

---

### 💰 Gestion Budgétaire
- Budgets mensuels par utilisateur
- Suivi et historique des dépenses
- Catégorisation intelligente des dépenses
- Statistiques par catégorie et par jour
- Analyse de la progression budgétaire

---

### 🤖 Intelligence Artificielle
- Analyse automatique des tâches (priorité, catégorie, recommandations)
- Catégorisation intelligente des dépenses
- Génération de plannings optimisés
- Intégration avec **Hugging Face API**

---

### 🌐 WebSocket
- Salles de discussion en temps réel
- Notifications en temps réel
- Signalisation pour communications peer-to-peer
- Gestion des participants

---

## 🛠️ Technologies utilisées

- **Java 21**
- **Spring Boot 3.x**
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- PostgreSQL (production)
- H2 (développement)
- Lombok
- MapStruct (mappers)
- WebSocket (STOMP)
- Swagger / OpenAPI
- Docker & Docker Compose
- Hugging Face API (IA)

---

## 📁 Structure du projet

```text
src/main/java/com/formation/task/
├── controllers/        # Contrôleurs REST
├── entities/           # Entités JPA
├── repository/         # Repositories Spring Data
├── services/           # Logique métier
├── security/           # Sécurité et JWT
├── config/             # Configuration Spring
├── dto/                # Data Transfer Objects
├── mappers/            # Mappers DTO <-> Entity
├── exceptions/         # Gestion des exceptions
└── websocket/          # WebSocket & handlers
