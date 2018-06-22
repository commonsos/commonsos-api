CREATE INDEX message_thread_parties_message_thread_id_index ON message_thread_parties (message_thread_id);

CREATE INDEX messages__threadid_index ON messages (thread_id);

CREATE INDEX messages__createdat_index ON messages (CREATED_AT DESC);

