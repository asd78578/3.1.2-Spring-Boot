DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;


CREATE TABLE roles (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       type VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(30) NOT NULL,
                       surname VARCHAR(30) NOT NULL,
                       age TINYINT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE users_roles (
                             user_id INT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                             FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

INSERT INTO roles (id, type)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');

INSERT INTO users (id, name, surname, age, username, password)
VALUES
    (1, 'Ron', 'Uizli', 55, 'user@mail.com', '$2a$10$sk3IJtFZFOFSWyh.3j7Xfe.6X2.RnPB8dEFglVT6Jzd2g9boRvoTG'),
    (2, 'Garri', 'Potter', 56, 'admin@mail.com', '$2a$10$mY2SfJLjdbAvMj8e1Zsaaug9XgRg3W7jUsueCHNZLU2jYxg6I8X7y');


INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 1),       -- user@mail.com → ROLE_USER
    (2, 1),       -- admin@mail.com → ROLE_USER
    (2, 2);       -- admin@mail.com → ROLE_ADMIN