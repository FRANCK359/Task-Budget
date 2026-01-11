-- init.sql - Version optimisée
SET client_encoding = 'UTF8';
SET TIME ZONE 'Europe/Paris';

-- Activer les extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Vérifier si nous sommes dans la bonne base
DO $$
BEGIN
    IF current_database() != 'taskdb' THEN
        RAISE EXCEPTION 'Mauvaise base de données: % (devrait être taskdb)', current_database();
    END IF;
END $$;

-- Fonction pour mettre à jour updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Table users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Table monthly_budgets
CREATE TABLE IF NOT EXISTS monthly_budgets (
    id BIGSERIAL PRIMARY KEY,
    year_value INTEGER NOT NULL CHECK (year_value >= 2020 AND year_value <= 2100),
    month_value INTEGER NOT NULL CHECK (month_value >= 1 AND month_value <= 12),
    budget_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (budget_amount >= 0),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, year_value, month_value)
);

-- Table expenses
CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL DEFAULT CURRENT_DATE CHECK (date <= CURRENT_DATE),
    category VARCHAR(100) NOT NULL,
    description TEXT,
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    ai_category VARCHAR(100),
    ai_advice TEXT,
    essential BOOLEAN DEFAULT FALSE,
    budget_id BIGINT NOT NULL REFERENCES monthly_budgets(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_expenses_date (date)
);

-- Table tasks
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    date_debut_estimee DATE,
    date_fin_estimee DATE,
    date_fin_reelle DATE,
    ecart BIGINT,
    cout_estime DECIMAL(15,2) CHECK (cout_estime >= 0),
    cout_reel DECIMAL(15,2) CHECK (cout_reel >= 0),
    ecart_cout DECIMAL(15,2),
    priority_score INTEGER CHECK (priority_score >= 0 AND priority_score <= 100),
    ai_advice TEXT,
    ai_category VARCHAR(100),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tasks_completed (completed),
    INDEX idx_tasks_priority (priority_score DESC NULLS LAST)
);

-- Table rooms
CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    task_id BIGINT REFERENCES tasks(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    persistent BOOLEAN DEFAULT FALSE,
    INDEX idx_rooms_public_id (public_id)
);

-- Triggers pour updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_budgets_updated_at BEFORE UPDATE ON monthly_budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Créer les indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_budgets_user_year_month ON monthly_budgets(user_id, year_value, month_value);
CREATE INDEX IF NOT EXISTS idx_expenses_budget_id ON expenses(budget_id);
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_rooms_task_id ON rooms(task_id);

-- Insertion d'un utilisateur par défaut (optionnel)
INSERT INTO users (username, email, password, role)
VALUES ('admin', 'admin@taskapp.com', crypt('admin123', gen_salt('bf')), 'ROLE_ADMIN')
ON CONFLICT (email) DO NOTHING;

RAISE NOTICE 'Base de données initialisée avec succès dans %', current_database();