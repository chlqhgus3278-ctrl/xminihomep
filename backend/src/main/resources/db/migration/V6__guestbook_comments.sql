CREATE TABLE guestbook_comments (
    id          BIGSERIAL PRIMARY KEY,
    entry_id    BIGINT NOT NULL REFERENCES guestbook(id) ON DELETE CASCADE,
    author_id   BIGINT REFERENCES users(id) ON DELETE SET NULL,
    author_name VARCHAR(50),
    message     TEXT NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_guestbook_comments_entry ON guestbook_comments(entry_id);
