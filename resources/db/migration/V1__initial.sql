CREATE TABLE users (
  id            BIGSERIAL,
  admin         BOOLEAN NOT NULL,
  avatar_url    VARCHAR(255),
  description   VARCHAR(255),
  first_name    VARCHAR(255),
  last_name     VARCHAR(255),
  location      VARCHAR(255),
  password_hash VARCHAR(255),
  username      VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE ads (
  id          BIGSERIAL,
  created_at  TIMESTAMP,
  created_by  BIGINT,
  description VARCHAR(255),
  location    VARCHAR(255),
  photo_url   VARCHAR(255),
  points      DECIMAL(19, 2),
  title       VARCHAR(255),
  type        VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE messages (
  id         BIGSERIAL,
  created_at TIMESTAMP,
  created_by BIGINT,
  text       VARCHAR(255),
  thread_id  BIGINT,
  PRIMARY KEY (id)
);

CREATE TABLE message_threads (
  id         BIGSERIAL,
  ad_id      BIGINT,
  created_by BIGINT,
  title      VARCHAR(255),
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
  id             BIGSERIAL,
  created_at     TIMESTAMP,
  ad_id          BIGINT,
  remitter_id    BIGINT,
  beneficiary_id BIGINT,
  amount         DECIMAL(19, 2),
  description    VARCHAR(255),
  PRIMARY KEY (id)
);


