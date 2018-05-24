ALTER TABLE message_thread_parties ADD COLUMN id BIGSERIAL;
ALTER TABLE message_thread_parties ADD COLUMN visited_at TIMESTAMP;