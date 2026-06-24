--liquibase formatted sql

--changeset system:005-add-auth0-sub-to-accounts
ALTER TABLE accounts ADD COLUMN auth0_sub VARCHAR(255);
ALTER TABLE accounts ADD CONSTRAINT uk_accounts_auth0_sub UNIQUE (auth0_sub);

--changeset system:005-relax-account-columns
-- Auth0-provisioned users have no local password and may not have provided a passport yet.
ALTER TABLE accounts ALTER COLUMN password_hash DROP NOT NULL;
ALTER TABLE accounts ALTER COLUMN passport_no DROP NOT NULL;
