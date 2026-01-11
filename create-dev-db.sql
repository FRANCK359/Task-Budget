-- Créer la base 'dev' pour éviter l'erreur "database dev does not exist"
CREATE DATABASE IF NOT EXISTS dev;

-- Donner les permissions à l'utilisateur 'dev'
GRANT ALL PRIVILEGES ON DATABASE dev TO dev;

-- Se connecter à 'dev' et créer les extensions
\c dev;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Créer les mêmes tables que dans taskdb (optionnel)
-- Note: pgAdmin ou d'autres services peuvent essayer de se connecter à 'dev'