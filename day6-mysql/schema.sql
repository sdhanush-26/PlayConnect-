-- =====================================================
-- Day 6 — Java + MySQL
-- SQL fundamentals: CREATE, INSERT, SELECT, UPDATE, DELETE,
-- PRIMARY KEY, FOREIGN KEY, JOIN
--
-- This is a small starter schema (users + sports + player_sport)
-- to practice the fundamentals. The FULL schema with all entities
-- (Match, MatchPlayer, Message, Notification, Rating, Ground)
-- comes on Day 7 as the complete ER diagram + schema.
--
-- Run in MySQL Workbench, DBeaver, or: mysql -u root -p < schema.sql
-- =====================================================

CREATE DATABASE IF NOT EXISTS playconnect;
USE playconnect;

-- -----------------------------------------------------
-- CREATE: users table
-- -----------------------------------------------------
DROP TABLE IF EXISTS player_sport;
DROP TABLE IF EXISTS sports;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    phone       VARCHAR(20),
    latitude    DECIMAL(9,6),
    longitude   DECIMAL(9,6),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),          -- uniquely identifies each user
    UNIQUE (email)             -- no two users can share an email
);

-- -----------------------------------------------------
-- CREATE: sports table
-- -----------------------------------------------------
CREATE TABLE sports (
    id      BIGINT AUTO_INCREMENT,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

-- -----------------------------------------------------
-- CREATE: player_sport (join table)
-- A user can play multiple sports, and a sport can have many
-- players -> this is a many-to-many relationship, modeled with
-- a join table holding two FOREIGN KEYs.
-- -----------------------------------------------------
CREATE TABLE player_sport (
    id          BIGINT AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    sport_id    BIGINT NOT NULL,
    skill_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'PRO') NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE CASCADE,
    UNIQUE (user_id, sport_id)  -- a user can't have two skill levels for the same sport
);


-- =====================================================
-- INSERT: sample data
-- =====================================================
INSERT INTO users (name, email, phone, latitude, longitude) VALUES
('Dhanush', 'dhanush@example.com', '9999900001', 14.6800, 77.6000),
('Ravi',    'ravi@example.com',    '9999900002', 14.6900, 77.6100),
('Kiran',   'kiran@example.com',   '9999900003', 14.7000, 77.6200);

INSERT INTO sports (name) VALUES
('Cricket'), ('Football'), ('Badminton'), ('Volleyball'), ('Basketball');

INSERT INTO player_sport (user_id, sport_id, skill_level) VALUES
(1, 1, 'INTERMEDIATE'),  -- Dhanush plays Cricket
(1, 3, 'BEGINNER'),      -- Dhanush also plays Badminton
(2, 1, 'ADVANCED'),      -- Ravi plays Cricket
(3, 2, 'PRO');           -- Kiran plays Football


-- =====================================================
-- SELECT: basic queries
-- =====================================================

-- All users
SELECT * FROM users;

-- Users within a specific search (by name)
SELECT name, email FROM users WHERE name = 'Dhanush';

-- Sports ordered alphabetically
SELECT * FROM sports ORDER BY name ASC;


-- =====================================================
-- JOIN: combine users with the sports they play
-- =====================================================

-- INNER JOIN: only users who have at least one sport
SELECT u.name AS player_name, s.name AS sport_name, ps.skill_level
FROM player_sport ps
INNER JOIN users u ON ps.user_id = u.id
INNER JOIN sports s ON ps.sport_id = s.id
ORDER BY u.name;

-- Expected result:
-- +-------------+-------------+---------------+
-- | player_name | sport_name  | skill_level   |
-- +-------------+-------------+---------------+
-- | Dhanush     | Cricket     | INTERMEDIATE  |
-- | Dhanush     | Badminton   | BEGINNER      |
-- | Kiran       | Football    | PRO           |
-- | Ravi        | Cricket     | ADVANCED      |
-- +-------------+-------------+---------------+

-- LEFT JOIN: all users, even ones with NO sport yet
-- (useful later for "players who haven't set up their profile")
SELECT u.name, s.name AS sport_name
FROM users u
LEFT JOIN player_sport ps ON u.id = ps.user_id
LEFT JOIN sports s ON ps.sport_id = s.id;


-- =====================================================
-- UPDATE: modify existing rows
-- =====================================================

-- Dhanush improves from BEGINNER to INTERMEDIATE in Badminton
UPDATE player_sport
SET skill_level = 'INTERMEDIATE'
WHERE user_id = 1 AND sport_id = 3;

-- Ravi updates his phone number
UPDATE users
SET phone = '9999900099'
WHERE id = 2;


-- =====================================================
-- DELETE: remove rows
-- =====================================================

-- Remove a sport a user no longer plays
DELETE FROM player_sport
WHERE user_id = 1 AND sport_id = 3;

-- Note: because of "ON DELETE CASCADE" on the foreign keys,
-- deleting a user automatically deletes their player_sport rows too:
-- DELETE FROM users WHERE id = 3;   -- (commented out, just for reference)
