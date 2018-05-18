CREATE TABLE users (
  id             BIGSERIAL,
  community_id   BIGINT,
  admin          BOOLEAN NOT NULL,
  avatar_url     TEXT,
  description    TEXT,
  first_name     TEXT,
  last_name      TEXT,
  location       TEXT,
  password_hash  TEXT,
  username       TEXT,
  wallet         TEXT,
  wallet_address TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE ads (
  id           BIGSERIAL,
  created_at   TIMESTAMP,
  created_by   BIGINT,
  community_id BIGINT,
  description  TEXT,
  location     TEXT,
  photo_url    TEXT,
  points       DECIMAL(19, 2),
  title        TEXT,
  type         TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE messages (
  id         BIGSERIAL,
  created_at TIMESTAMP,
  created_by BIGINT,
  text       TEXT,
  thread_id  BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE message_threads (
  id         BIGSERIAL,
  ad_id      BIGINT,
  created_by BIGINT,
  title      TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE message_thread_parties (
  message_thread_id BIGINT,
  user_id           BIGINT
);

ALTER TABLE message_thread_parties
  ADD CONSTRAINT fk_message_thread_party_user_id
FOREIGN KEY (user_id) REFERENCES users;

ALTER TABLE message_thread_parties
  ADD CONSTRAINT fk_message_thread_party_message_thread_id
FOREIGN KEY (message_thread_id) REFERENCES message_threads;

CREATE TABLE transactions (
  id                        BIGSERIAL,
  created_at                TIMESTAMP,
  blockchain_transaction_id TEXT,
  ad_id                     BIGINT,
  remitter_id               BIGINT,
  beneficiary_id            BIGINT,
  amount                    DECIMAL(19, 2),
  description               TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE communities (
  id                BIGSERIAL,
  name              TEXT NOT NULL,
  token_contract_id TEXT,
  PRIMARY KEY (id)
);