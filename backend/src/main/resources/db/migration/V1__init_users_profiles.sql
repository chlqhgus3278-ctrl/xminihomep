CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    google_sub  VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE profiles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    display_name    VARCHAR(100),
    profile_img_url TEXT,
    birth_date      DATE,
    location        VARCHAR(100),
    email_public    VARCHAR(255),
    phone           VARCHAR(20),
    intro           TEXT,
    -- 테마: 'retro' | 'modern'
    theme           VARCHAR(20) DEFAULT 'retro',
    -- 테마 위에 올라가는 커스텀 색상 오버라이드 (JSON)
    skin_config     JSONB DEFAULT '{}',
    bgm_url         TEXT,
    bgm_title       VARCHAR(200),
    is_public       BOOLEAN DEFAULT TRUE,
    updated_at      TIMESTAMP DEFAULT NOW()
);
