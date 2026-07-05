# folio

싸이월드 미니홈피 스타일의 개발자/직장인 포트폴리오 SaaS.
Google 로그인 후 `https://도메인/{username}` 형태의 개인 포트폴리오 홈피를 만들 수 있다.

## 기술 스택

**Backend**
- Java 17 / Spring Boot 3
- Spring Security 6 + OAuth2 (Google 로그인)
- Spring Data JPA / PostgreSQL / Flyway
- AWS S3 SDK v2 (Cloudflare R2 연동)
- JJWT (JWT 발급/검증)

**Frontend**
- Vue 3 + Vite
- Vue Router 4
- Pinia (Options Store)
- Axios
- @vueup/vue-quill (게시판 리치 에디터)
- html2pdf.js (PDF 저장)

**Infra**
- Railway (Spring Boot + PostgreSQL 배포)
- Cloudflare R2 (이미지 / BGM 파일 저장)

## 프로젝트 구조

```
folio/
├── backend/   # Spring Boot 3 (Java 17, Maven)
└── frontend/  # Vue 3 + Vite
```

## 개발 환경 실행

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## 환경 변수

Backend는 `DATABASE_URL`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`,
`R2_ENDPOINT`, `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_BUCKET`, `R2_PUBLIC_URL` 등의
환경 변수가 필요하다. 로컬 개발 시 `backend/src/main/resources/application-local.yml`에서 관리한다.
