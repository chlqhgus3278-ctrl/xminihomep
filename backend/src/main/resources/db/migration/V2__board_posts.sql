-- board_type: CAREER_HISTORY | CAREER_DESC | INTRO | EDUCATION | CERT | LANGUAGE | SKILLS
CREATE TABLE board_posts (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    board_type  VARCHAR(30) NOT NULL,
    title       VARCHAR(200),
    content     TEXT,           -- Quill.js가 생성하는 HTML 문자열 저장
    is_public   BOOLEAN DEFAULT TRUE,
    sort_order  INT DEFAULT 0,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);
