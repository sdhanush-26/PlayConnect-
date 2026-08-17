-- =====================================================
-- Day 7 — Complete Database Design
-- Full schema for every entity in PlayConnect.
--
-- Builds on Day 6's users/sports/player_sport tables and adds:
-- grounds, matches, match_players, messages, notifications, ratings
--
-- Run in MySQL Workbench, DBeaver, or: mysql -u root -p < full_schema.sql
-- =====================================================

CREATE DATABASE IF NOT EXISTS playconnect;
USE playconnect;

-- Drop in dependency order (children before parents) so re-runs are clean
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS match_players;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS grounds;
DROP TABLE IF EXISTS player_sport;
DROP TABLE IF EXISTS sports;
DROP TABLE IF EXISTS users;


-- -----------------------------------------------------
-- USERS
-- -----------------------------------------------------
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(255) NOT NULL,   -- stores BCrypt hash, never plain text
    phone       VARCHAR(20),
    latitude    DECIMAL(9,6),
    longitude   DECIMAL(9,6),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (email)
);

-- -----------------------------------------------------
-- SPORTS
-- -----------------------------------------------------
CREATE TABLE sports (
    id      BIGINT AUTO_INCREMENT,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

-- -----------------------------------------------------
-- PLAYER_SPORT (many-to-many: a user can play multiple sports)
-- -----------------------------------------------------
CREATE TABLE player_sport (
    id          BIGINT AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    sport_id    BIGINT NOT NULL,
    skill_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'PRO') NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE CASCADE,
    UNIQUE (user_id, sport_id)
);

-- -----------------------------------------------------
-- GROUNDS (physical play areas, e.g. a specific cricket ground)
-- -----------------------------------------------------
CREATE TABLE grounds (
    id          BIGINT AUTO_INCREMENT,
    name        VARCHAR(150) NOT NULL,
    location    VARCHAR(255),
    latitude    DECIMAL(9,6),
    longitude   DECIMAL(9,6),
    sport_id    BIGINT,                  -- primary sport this ground supports
    PRIMARY KEY (id),
    FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- MATCHES
-- -----------------------------------------------------
CREATE TABLE matches (
    id           BIGINT AUTO_INCREMENT,
    title        VARCHAR(150) NOT NULL,
    creator_id   BIGINT NOT NULL,
    sport_id     BIGINT NOT NULL,
    ground_id    BIGINT,
    match_date   DATE NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    max_players  INT NOT NULL,
    status       ENUM('OPEN', 'FULL', 'STARTED', 'COMPLETED', 'CANCELLED')
                 NOT NULL DEFAULT 'OPEN',
    PRIMARY KEY (id),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE RESTRICT,
    FOREIGN KEY (ground_id) REFERENCES grounds(id) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- MATCH_PLAYERS (who joined which match, and their status)
-- -----------------------------------------------------
CREATE TABLE match_players (
    id           BIGINT AUTO_INCREMENT,
    match_id     BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    join_status  ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    joined_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (match_id, user_id)   -- can't join the same match twice
);

-- -----------------------------------------------------
-- MESSAGES (chat within a match)
-- -----------------------------------------------------
CREATE TABLE messages (
    id         BIGINT AUTO_INCREMENT,
    match_id   BIGINT NOT NULL,
    sender_id  BIGINT NOT NULL,
    content    TEXT NOT NULL,
    sent_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- NOTIFICATIONS
-- -----------------------------------------------------
CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(50) NOT NULL,     -- e.g. MATCH_INVITE, JOIN_REQUEST, REMINDER
    message     VARCHAR(255) NOT NULL,
    is_read     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- RATINGS (players rate each other after a match)
-- -----------------------------------------------------
CREATE TABLE ratings (
    id             BIGINT AUTO_INCREMENT,
    match_id       BIGINT NOT NULL,
    rater_id       BIGINT NOT NULL,       -- who gave the rating
    rated_user_id  BIGINT NOT NULL,       -- who received the rating
    stars          INT NOT NULL,
    comment        VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (rater_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (rated_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (stars BETWEEN 1 AND 5),
    UNIQUE (match_id, rater_id, rated_user_id)  -- one rating per pair per match
);


-- =====================================================
-- SAMPLE DATA — enough to exercise every relationship
-- =====================================================

INSERT INTO users (name, email, password, phone, latitude, longitude) VALUES
('Dhanush', 'dhanush@example.com', '$2a$10$hashedpassword1', '9999900001', 14.6800, 77.6000),
('Ravi',    'ravi@example.com',    '$2a$10$hashedpassword2', '9999900002', 14.6900, 77.6100),
('Kiran',   'kiran@example.com',   '$2a$10$hashedpassword3', '9999900003', 14.7000, 77.6200);

INSERT INTO sports (name) VALUES
('Cricket'), ('Football'), ('Badminton'), ('Volleyball'), ('Basketball');

INSERT INTO player_sport (user_id, sport_id, skill_level) VALUES
(1, 1, 'INTERMEDIATE'), (2, 1, 'ADVANCED'), (3, 2, 'PRO');

INSERT INTO grounds (name, location, latitude, longitude, sport_id) VALUES
('City Cricket Ground', 'Anantapur', 14.6810, 77.6005, 1);

INSERT INTO matches (title, creator_id, sport_id, ground_id, match_date, start_time, end_time, max_players, status) VALUES
('Sunday Cricket', 1, 1, 1, '2026-08-16', '09:00:00', '12:00:00', 11, 'OPEN');

INSERT INTO match_players (match_id, user_id, join_status) VALUES
(1, 2, 'ACCEPTED');

INSERT INTO messages (match_id, sender_id, content) VALUES
(1, 1, 'Looking forward to the match, see everyone at 9am!'),
(1, 2, 'Count me in, bringing my own bat.');

INSERT INTO notifications (user_id, type, message) VALUES
(2, 'JOIN_REQUEST_ACCEPTED', 'Your request to join Sunday Cricket was accepted.');

INSERT INTO ratings (match_id, rater_id, rated_user_id, stars, comment) VALUES
(1, 1, 2, 5, 'Great sportsmanship, would play again.');


-- =====================================================
-- SAMPLE QUERIES exercising the full schema
-- =====================================================

-- Full match roster with player names and join status
SELECT m.title, u.name AS player, mp.join_status
FROM match_players mp
JOIN matches m ON mp.match_id = m.id
JOIN users u ON mp.user_id = u.id;

-- All messages in a match, newest first
SELECT u.name AS sender, msg.content, msg.sent_at
FROM messages msg
JOIN users u ON msg.sender_id = u.id
WHERE msg.match_id = 1
ORDER BY msg.sent_at DESC;

-- Average rating per user
SELECT u.name, ROUND(AVG(r.stars), 1) AS avg_rating, COUNT(r.id) AS rating_count
FROM ratings r
JOIN users u ON r.rated_user_id = u.id
GROUP BY u.id, u.name;
