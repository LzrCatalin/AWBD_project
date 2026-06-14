--liquibase formatted sql
--changeset system:004-seed-data

-- ── Accounts ────────────────────────────────────────────────────────────────
-- Passwords sunt bcrypt pentru "Test1234!" (cost 10)
INSERT INTO accounts (id, email, password_hash, first_name, last_name, passport_no, is_active, created_date)
VALUES
    ('a1000000-0000-0000-0000-000000000001', 'admin@airport.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'Admin', 'User', 'RO1234567', true, NOW()),

    ('a1000000-0000-0000-0000-000000000002', 'staff@airport.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'Staff', 'Member', 'RO2345678', true, NOW()),

    ('a1000000-0000-0000-0000-000000000003', 'john.doe@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'John', 'Doe', 'GB3456789', true, NOW()),

    ('a1000000-0000-0000-0000-000000000004', 'maria.pop@example.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'Maria', 'Pop', 'RO4567890', true, NOW());

-- ── Account Roles ────────────────────────────────────────────────────────────
INSERT INTO account_roles (id, account_id, role_id, is_active, created_date)
VALUES
    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000001',
     (SELECT id FROM roles WHERE name = 'ADMIN'), true, NOW()),

    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000002',
     (SELECT id FROM roles WHERE name = 'STAFF'), true, NOW()),

    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000003',
     (SELECT id FROM roles WHERE name = 'PASSENGER'), true, NOW()),

    (gen_random_uuid(), 'a1000000-0000-0000-0000-000000000004',
     (SELECT id FROM roles WHERE name = 'PASSENGER'), true, NOW());

-- ── Aircraft ─────────────────────────────────────────────────────────────────
INSERT INTO aircrafts (id, model, capacity, plane_no, is_active, created_date)
VALUES
    ('b1000000-0000-0000-0000-000000000001', 'Boeing 737-800',    189, 'YR-BAA', true, NOW()),
    ('b1000000-0000-0000-0000-000000000002', 'Airbus A320',        180, 'YR-ABC', true, NOW()),
    ('b1000000-0000-0000-0000-000000000003', 'Boeing 777-300ER',  396, 'YR-XYZ', true, NOW()),
    ('b1000000-0000-0000-0000-000000000004', 'Airbus A350-900',   315, 'YR-DEF', true, NOW());

-- ── Gates ────────────────────────────────────────────────────────────────────
INSERT INTO gates (id, gate_no, terminal, is_active, created_date)
VALUES
    ('c1000000-0000-0000-0000-000000000001', 'A1', 'Terminal 1', true, NOW()),
    ('c1000000-0000-0000-0000-000000000002', 'A2', 'Terminal 1', true, NOW()),
    ('c1000000-0000-0000-0000-000000000003', 'B1', 'Terminal 2', true, NOW()),
    ('c1000000-0000-0000-0000-000000000004', 'B2', 'Terminal 2', true, NOW()),
    ('c1000000-0000-0000-0000-000000000005', 'C1', 'Terminal 3', true, NOW()),
    ('c1000000-0000-0000-0000-000000000006', 'C2', 'Terminal 3', true, NOW());

-- ── Flights ───────────────────────────────────────────────────────────────────
INSERT INTO flights (id, flight_no, departure_city, arrival_city, departure_time, arrival_time,
                     aircraft_id, gate_id, is_active, created_date)
VALUES
    ('d1000000-0000-0000-0000-000000000001',
     'RO101', 'Bucharest', 'London',
     NOW() + INTERVAL '2 days', NOW() + INTERVAL '2 days 3 hours',
     'b1000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001',
     true, NOW()),

    ('d1000000-0000-0000-0000-000000000002',
     'RO202', 'Bucharest', 'Paris',
     NOW() + INTERVAL '3 days', NOW() + INTERVAL '3 days 2 hours 30 minutes',
     'b1000000-0000-0000-0000-000000000002', 'c1000000-0000-0000-0000-000000000003',
     true, NOW()),

    ('d1000000-0000-0000-0000-000000000003',
     'RO303', 'London', 'New York',
     NOW() + INTERVAL '5 days', NOW() + INTERVAL '5 days 8 hours',
     'b1000000-0000-0000-0000-000000000003', 'c1000000-0000-0000-0000-000000000005',
     true, NOW()),

    ('d1000000-0000-0000-0000-000000000004',
     'RO404', 'Paris', 'Rome',
     NOW() + INTERVAL '4 days', NOW() + INTERVAL '4 days 1 hour 45 minutes',
     'b1000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000002',
     true, NOW()),

    ('d1000000-0000-0000-0000-000000000005',
     'RO505', 'Bucharest', 'Frankfurt',
     NOW() + INTERVAL '6 days', NOW() + INTERVAL '6 days 2 hours',
     'b1000000-0000-0000-0000-000000000004', 'c1000000-0000-0000-0000-000000000004',
     true, NOW());

-- ── Seats — RO101 (Boeing 737-800) ───────────────────────────────────────────
INSERT INTO seats (id, seat_no, type, is_available, flight_id, is_active, created_date)
VALUES
    (gen_random_uuid(), '1A', 'First Class', true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '1B', 'First Class', true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '10A', 'Business',   true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '10B', 'Business',   true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '20A', 'Economy',    true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '20B', 'Economy',    true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '20C', 'Economy',    false, 'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '21A', 'Economy',    true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '21B', 'Economy',    true,  'd1000000-0000-0000-0000-000000000001', true, NOW()),
    (gen_random_uuid(), '21C', 'Economy',    true,  'd1000000-0000-0000-0000-000000000001', true, NOW());

-- ── Seats — RO202 (Airbus A320) ──────────────────────────────────────────────
INSERT INTO seats (id, seat_no, type, is_available, flight_id, is_active, created_date)
VALUES
    (gen_random_uuid(), '1A', 'Business',  true,  'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '1B', 'Business',  true,  'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '5A', 'Economy',   true,  'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '5B', 'Economy',   true,  'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '5C', 'Economy',   false, 'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '6A', 'Economy',   true,  'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '6B', 'Economy',   true,  'd1000000-0000-0000-0000-000000000002', true, NOW()),
    (gen_random_uuid(), '6C', 'Economy',   true,  'd1000000-0000-0000-0000-000000000002', true, NOW());

-- ── Seats — RO303 (Boeing 777) ───────────────────────────────────────────────
INSERT INTO seats (id, seat_no, type, is_available, flight_id, is_active, created_date)
VALUES
    (gen_random_uuid(), '1A', 'First Class', true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '1B', 'First Class', true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '1C', 'First Class', true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '8A', 'Business',    true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '8B', 'Business',    true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '30A', 'Economy',    true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '30B', 'Economy',    true, 'd1000000-0000-0000-0000-000000000003', true, NOW()),
    (gen_random_uuid(), '30C', 'Economy',    true, 'd1000000-0000-0000-0000-000000000003', true, NOW());

-- ── Seats — RO404 ─────────────────────────────────────────────────────────────
INSERT INTO seats (id, seat_no, type, is_available, flight_id, is_active, created_date)
VALUES
    (gen_random_uuid(), '1A', 'Business', true, 'd1000000-0000-0000-0000-000000000004', true, NOW()),
    (gen_random_uuid(), '1B', 'Business', true, 'd1000000-0000-0000-0000-000000000004', true, NOW()),
    (gen_random_uuid(), '5A', 'Economy',  true, 'd1000000-0000-0000-0000-000000000004', true, NOW()),
    (gen_random_uuid(), '5B', 'Economy',  true, 'd1000000-0000-0000-0000-000000000004', true, NOW()),
    (gen_random_uuid(), '5C', 'Economy',  true, 'd1000000-0000-0000-0000-000000000004', true, NOW());

-- ── Seats — RO505 ─────────────────────────────────────────────────────────────
INSERT INTO seats (id, seat_no, type, is_available, flight_id, is_active, created_date)
VALUES
    (gen_random_uuid(), '2A', 'Business', true, 'd1000000-0000-0000-0000-000000000005', true, NOW()),
    (gen_random_uuid(), '2B', 'Business', true, 'd1000000-0000-0000-0000-000000000005', true, NOW()),
    (gen_random_uuid(), '10A', 'Economy', true, 'd1000000-0000-0000-0000-000000000005', true, NOW()),
    (gen_random_uuid(), '10B', 'Economy', true, 'd1000000-0000-0000-0000-000000000005', true, NOW()),
    (gen_random_uuid(), '10C', 'Economy', true, 'd1000000-0000-0000-0000-000000000005', true, NOW());
