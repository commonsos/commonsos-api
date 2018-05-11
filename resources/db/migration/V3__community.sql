CREATE TABLE communities (
  id                  BIGSERIAL,
  name                TEXT NOT NULL,
  token_contract_id   TEXT,
  PRIMARY KEY (id)
);