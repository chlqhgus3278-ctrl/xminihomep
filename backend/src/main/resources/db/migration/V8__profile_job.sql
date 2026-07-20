-- 프로필 이름 옆에 표시하는 직군 라벨. 기본값 Dev, 목록 선택 또는 직접 입력.
ALTER TABLE profiles
    ADD COLUMN job VARCHAR(40) NOT NULL DEFAULT 'Dev';
