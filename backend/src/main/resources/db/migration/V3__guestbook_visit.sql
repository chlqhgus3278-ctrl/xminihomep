CREATE TABLE guestbook (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    author_id   BIGINT REFERENCES users(id) ON DELETE SET NULL,
    author_name VARCHAR(50),
    message     TEXT NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE visit_logs (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    visitor_ip  VARCHAR(45),
    visit_date  DATE DEFAULT CURRENT_DATE,
    visited_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE visit_counts (
    user_id     BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    total       BIGINT DEFAULT 0,
    today       BIGINT DEFAULT 0,
    date_reset  DATE DEFAULT CURRENT_DATE
);
