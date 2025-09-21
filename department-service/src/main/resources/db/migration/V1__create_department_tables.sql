CREATE SCHEMA IF NOT EXISTS department;

CREATE TABLE IF NOT EXISTS department.departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT
);
