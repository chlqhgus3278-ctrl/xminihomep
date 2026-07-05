-- username: 영문 소문자, 숫자, 하이픈만 허용, 3~20자
ALTER TABLE users
    ADD CONSTRAINT chk_username_format CHECK (username ~ '^[a-z0-9-]{3,20}$');
