--liquibase formatted sql

--changeset system:007-add-flight-pricing
ALTER TABLE flights ADD COLUMN base_fare NUMERIC(10, 2) NOT NULL DEFAULT 0;
ALTER TABLE flights ADD COLUMN taxes NUMERIC(10, 2) NOT NULL DEFAULT 0;
-- Give existing flights a sensible non-zero fare so the booking UI is populated.
UPDATE flights SET base_fare = 180, taxes = 35 WHERE base_fare = 0;

--changeset system:007-add-booking-total-price
ALTER TABLE bookings ADD COLUMN total_price NUMERIC(10, 2);
