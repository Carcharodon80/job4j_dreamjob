CREATE TABLE candidates (
    ID SERIAL PRIMARY KEY,
    name TEXT
);
ALTER TABLE candidates ADD description text;
ALTER TABLE candidates ADD date timestamp;
ALTER TABLE candidates ADD photo bytea;