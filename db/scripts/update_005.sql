CREATE TABLE IF NOT EXISTS candidates (
    ID SERIAL PRIMARY KEY,
    name TEXT,
    description TEXT,
    date TIMESTAMP,
    photo BYTEA
);
