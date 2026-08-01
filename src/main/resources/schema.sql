CREATE TABLE IF NOT EXISTS user_facts (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    fact TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS user_facts_username_idx ON user_facts (username);
