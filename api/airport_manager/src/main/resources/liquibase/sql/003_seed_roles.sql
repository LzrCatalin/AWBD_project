--liquibase formatted sql
--changeset system:003-seed-roles

INSERT INTO roles (id, name, is_active)
VALUES
    (gen_random_uuid(), 'ADMIN', true),
    (gen_random_uuid(), 'STAFF', true),
    (gen_random_uuid(), 'PASSENGER', true)
ON CONFLICT (name) DO NOTHING;
