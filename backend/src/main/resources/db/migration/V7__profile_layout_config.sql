-- 메인(대시보드) 레이아웃 설정. 예: {"sections": ["CAREER_HISTORY", "CAREER_DESC", "INTRO", "EDUCATION"]}
ALTER TABLE profiles
    ADD COLUMN layout_config JSONB NOT NULL DEFAULT '{}';
