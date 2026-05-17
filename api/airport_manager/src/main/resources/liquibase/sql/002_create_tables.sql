--liquibase formatted sql
--changeset system:002-create-table-accounts

CREATE TABLE accounts
(
    id                 UUID         NOT NULL PRIMARY KEY,
    email              VARCHAR(255) NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    first_name         VARCHAR(255) NOT NULL,
    last_name          VARCHAR(255) NOT NULL,
    passport_no        VARCHAR(20)  NOT NULL UNIQUE,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE roles
(
    id                 UUID        NOT NULL PRIMARY KEY,
    name               VARCHAR(50) NOT NULL UNIQUE,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE account_roles
(
    id                 UUID    NOT NULL PRIMARY KEY,
    account_id         UUID    NOT NULL,
    role_id            UUID    NOT NULL,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    CONSTRAINT fk_account_roles_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_account_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE aircrafts
(
    id                 UUID         NOT NULL PRIMARY KEY,
    model              VARCHAR(255) NOT NULL,
    capacity           INTEGER      NOT NULL,
    plane_no           VARCHAR(50),
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE gates
(
    id                 UUID        NOT NULL PRIMARY KEY,
    gate_no            VARCHAR(50) NOT NULL UNIQUE,
    terminal           VARCHAR(100),
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP
);

CREATE TABLE flights
(
    id                 UUID         NOT NULL PRIMARY KEY,
    flight_no          VARCHAR(20)  NOT NULL UNIQUE,
    departure_time     TIMESTAMP,
    arrival_time       TIMESTAMP,
    departure_city     VARCHAR(255) NOT NULL,
    arrival_city       VARCHAR(255) NOT NULL,
    aircraft_id        UUID         NOT NULL,
    gate_id            UUID,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    CONSTRAINT fk_flights_aircraft FOREIGN KEY (aircraft_id) REFERENCES aircrafts (id),
    CONSTRAINT fk_flights_gate FOREIGN KEY (gate_id) REFERENCES gates (id)
);

CREATE TABLE seats
(
    id                 UUID        NOT NULL PRIMARY KEY,
    seat_no            VARCHAR(10) NOT NULL,
    type               VARCHAR(50),
    is_available       BOOLEAN     NOT NULL DEFAULT TRUE,
    flight_id          UUID,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    CONSTRAINT fk_seats_flight FOREIGN KEY (flight_id) REFERENCES flights (id)
);

CREATE TABLE bookings
(
    id                 UUID        NOT NULL PRIMARY KEY,
    booking_no         VARCHAR(50) NOT NULL,
    flight_id          UUID,
    account_id         UUID,
    seat_id            UUID,
    booking_date       TIMESTAMP,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN     NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    CONSTRAINT fk_bookings_flight  FOREIGN KEY (flight_id)  REFERENCES flights (id),
    CONSTRAINT fk_bookings_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_bookings_seat    FOREIGN KEY (seat_id)    REFERENCES seats (id)
);

CREATE TABLE hist.revinfo
(
    rev      SERIAL NOT NULL PRIMARY KEY,
    revtstmp BIGINT NOT NULL
);

CREATE TABLE hist.accounts_hist
(
    id                 UUID         NOT NULL,
    rev                INTEGER      NOT NULL,
    revtype            SMALLINT,
    email              VARCHAR(255),
    password_hash      VARCHAR(255),
    first_name         VARCHAR(255),
    last_name          VARCHAR(255),
    passport_no        VARCHAR(20),
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_accounts_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.roles_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    name               VARCHAR(50),
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_roles_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.account_roles_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    account_id         UUID,
    role_id            UUID,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_account_roles_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.aircrafts_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    model              VARCHAR(255),
    capacity           INTEGER,
    plane_no           VARCHAR(50),
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_aircrafts_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.gates_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    gate_no            VARCHAR(50),
    terminal           VARCHAR(100),
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_gates_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.flights_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    flight_no          VARCHAR(20),
    departure_time     TIMESTAMP,
    arrival_time       TIMESTAMP,
    departure_city     VARCHAR(255),
    arrival_city       VARCHAR(255),
    aircraft_id        UUID,
    gate_id            UUID,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_flights_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.seats_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    seat_no            VARCHAR(10),
    type               VARCHAR(50),
    is_available       BOOLEAN,
    flight_id          UUID,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_seats_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);

CREATE TABLE hist.bookings_hist
(
    id                 UUID    NOT NULL,
    rev                INTEGER NOT NULL,
    revtype            SMALLINT,
    booking_no         VARCHAR(50),
    flight_id          UUID,
    account_id         UUID,
    seat_id            UUID,
    booking_date       TIMESTAMP,
    start_date         DATE,
    end_date           DATE,
    is_active          BOOLEAN,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP,

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_bookings_hist_rev FOREIGN KEY (rev) REFERENCES hist.revinfo (rev)
);
