ALTER SYSTEM SET listen_addresses = '*';

CREATE TYPE TicketType AS ENUM ('VIP', 'USUAL', 'BUDGETARY', 'CHEAP');
CREATE TYPE Status AS ENUM ('admin', 'simple');


CREATE TABLE locations (
                           id SERIAL PRIMARY KEY,
                           x FLOAT NOT NULL,
                           y FLOAT NOT NULL,
                           z BIGINT NOT NULL
);

CREATE TABLE coordinates (
                             id SERIAL PRIMARY KEY,
                             x INTEGER NOT NULL CHECK (x > -47),
                             y FLOAT NOT NULL CHECK (y > -69)
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       login VARCHAR(30) NOT NULL UNIQUE CHECK (login <> ''),
                       password_hash TEXT NOT NULL CHECK (password_hash <> ''),
                       status Status NOT NULL DEFAULT 'simple'
);

CREATE TABLE persons (
                         id SERIAL PRIMARY KEY,
                         birthday DATE NOT NULL,
                         weight DOUBLE PRECISION NOT NULL CHECK (weight > 0),
                         passport_id VARCHAR(30) NOT NULL CHECK (passport_id <> ''),
                         location_id INTEGER NOT NULL,
                         FOREIGN KEY (location_id) REFERENCES locations(id)
                             ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE tickets (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(30) NOT NULL,
                         creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         coordinates_id INTEGER NOT NULL,
                         price INTEGER NOT NULL CHECK (price > 0),
                         refundable BOOLEAN NOT NULL,
                         type TicketType NOT NULL,
                         person_id INTEGER NOT NULL,
                         user_id INTEGER NOT NULL,
                         FOREIGN KEY (coordinates_id) REFERENCES coordinates(id)
                             ON UPDATE CASCADE ON DELETE CASCADE,
                         FOREIGN KEY (person_id) REFERENCES persons(id)
                             ON UPDATE CASCADE ON DELETE CASCADE,
                         FOREIGN KEY (user_id) REFERENCES users(id)
                             ON UPDATE CASCADE ON DELETE CASCADE
);