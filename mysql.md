CREATE DATABASE spring_security_db;
USE spring_security_db;

CREATE TABLE roles (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
type VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(50) NOT NULL UNIQUE,
surname VARCHAR(50) NOT NULL UNIQUE,
age INT,
username VARCHAR(50) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL

);

CREATE TABLE users_roles (
user_id BIGINT NOT NULL,
role_id BIGINT NOT NULL,
PRIMARY KEY (user_id, role_id),
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO roles (type) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

INSERT INTO users (name, surname, age, username, password)
VALUES ('admin','Admin',35,'Admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuD7.7FML6C3.6Z7C5c7dL.5aVqDxK.');

INSERT INTO users (name, surname, age, username, password)
VALUES ('user','User',28,'User', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuD7.7FML6C3.6Z7C5c7dL.5aVqDxK.');

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1), (1, 2); -- ADMIN и USER

INSERT INTO users_roles (user_id, role_id)
VALUES (2, 2); -- USER

-- $2a$12$uiO8avUXXHyTi9zEcajhBO5kH4JrZ/pir1FT24FaEC4gHEnzf0i3C

UPDATE users
SET password = '$2a$12$uiO8avUXXHyTi9zEcajhBO5kH4JrZ/pir1FT24FaEC4gHEnzf0i3C'
WHERE id = 1;

UPDATE users
SET password = '$2a$12$uiO8avUXXHyTi9zEcajhBO5kH4JrZ/pir1FT24FaEC4gHEnzf0i3C'
WHERE id = 2;

SELECT * FROM users;

UPDATE users
SET password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuD7.7FML6C3.6Z7C5c7dL.5aVqDxK.'
WHERE id = 1;