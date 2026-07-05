-- 홈피 상단에 표시되는 커스텀 제목 (없으면 프론트에서 "{이름}의 홈피"로 대체)
ALTER TABLE profiles
    ADD COLUMN homepage_title VARCHAR(100);
