CREATE TABLE IF NOT EXISTS users (
    id BIG SERIAL PRIMARY KEY,
    login VARCHAR(64) UNIQUE NOT NULL,
    password_hash CHAR(64) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS dragon_id_seq START 1;

CREATE TABLE IF NOT EXISTS dragons (
    id BIGINT PRIMARY KEY DEFAULT nextval('dragon_id_seq'),
    name VARCHAR(255) NOT NULL,
    coord_x DOUBLE PRECISION NOT NULL,
    coord_y REAL NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    age BIGINT,
    color VARCHAR(32),
    type VARCHAR(32),
    character VARCHAR(32),
    killer_name VARCHAR(32),
    killer_coordinate_x INTEGER,
    killer_coordinate_y INTEGER,
    killer_coordinate_z DOUBLE PRECISION,
    killer_location_name VARCHAR(315),
    owner_id BIGINT NOT NULL REFERENCES users(id)
);