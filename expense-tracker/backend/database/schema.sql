-- Expense Tracker - MySQL schema (matches Spring Data JPA entities in this project)
-- Submit this file with your zip as the database schema.
-- Run from MySQL CLI:  SOURCE path/to/schema.sql;
-- Or:              mysql -u root -p < path/to/schema.sql

CREATE DATABASE IF NOT EXISTS expense_tracker
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE expense_tracker;

-- Application uses spring.jpa.hibernate.ddl-auto=update; this script documents / bootstraps the same structure.

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DOUBLE NOT NULL,
    category VARCHAR(32) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255) NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_expenses_user_id (user_id),
    KEY idx_expenses_user_date (user_id, date),
    CONSTRAINT fk_expenses_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- role: USER | ADMIN (matches com.example.backend.entity.Role)
-- category: FOOD, TRAVEL, BILLS, ENTERTAINMENT, HEALTH, SHOPPING, OTHER (matches ExpenseCategory)
