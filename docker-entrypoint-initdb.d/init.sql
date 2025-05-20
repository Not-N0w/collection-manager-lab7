CREATE TYPE TicketType AS ENUM (
    'VIP',
    'USUAL',
    'BUDGETARY',
    'CHEAP'
);

CREATE TABLE Location (
    id SERIAL PRIMARY KEY,
    x FLOAT NOT NULL,
    y FLOAT NOT NULL,
    z BIGINT NOT NULL
);

CREATE TABLE Coordinates (
     id SERIAL PRIMARY KEY,
     x INTEGER NOT NULL CHECK (x > -47),
     y FLOAT NOT NULL CHECK (y > -69)
);

CREATE TABLE Person (
    id SERIAL PRIMARY KEY,
    birthday DATE NOT NULL,
    weight INTEGER NOT NULL CHECK (weight > 0),
    passport_id VARCHAR(30) NOT NULL CHECK (passport_id <> ''),
    location_id INTEGER NOT NULL,
    FOREIGN KEY (location_id) REFERENCES Location(id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Ticket (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    coordinates_id INTEGER NOT NULL,
    price INTEGER NOT NULL CHECK (price > 0),
    refundable BOOLEAN NOT NULL,
    type TicketType NOT NULL,
    person_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (coordinates_id) REFERENCES Coordinates(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (person_id) REFERENCES Person(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
);


-- User part

CREATE TYPE Status AS ENUM ("admin", "simple");

CREATE TABLE User (
    id SERIAL PRIMARY KEY,
    login VARCHAR(30) NOT NULL CHECK (login <> ''),
    password_hash TEXT NOT NULL CHECK (password_hash <> ''),
    status Status NOT NULL
);
