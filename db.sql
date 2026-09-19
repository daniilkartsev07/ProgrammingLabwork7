CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) UNIQUE NOT NULL,
    password_hash VARCHAR(140) NOT NULL
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
    killer_name VARCHAR(255),
    killer_height DOUBLE PRECISION,
    killer_location_x INTEGER,
    killer_location_y INTEGER,
    killer_location_z DOUBLE PRECISION,
    killer_location_name VARCHAR(315),
    owner_id BIGINT NOT NULL REFERENCES users(id)
);