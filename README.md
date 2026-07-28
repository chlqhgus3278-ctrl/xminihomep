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

**Infra** (전부 무료 티어)
- Cloudflare Pages (Vue 정적 빌드 배포)
- Render (Spring Boot 컨테이너 배포)
- Neon (PostgreSQL)
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

Frontend는 `VITE_API_URL` 하나만 쓴다. 로컬은 `frontend/.env.local`,
배포는 Cloudflare Pages 환경 변수에서 설정한다.

## 배포

무료 티어만 조합한 구성이다. 프론트를 정적 호스팅에 분리해 두면 백엔드가 잠들어 있어도
페이지는 즉시 뜨고 API 응답만 기다리면 된다.

| 구성요소 | 서비스 | 설정 파일 |
|---|---|---|
| 프론트 | Cloudflare Pages | `frontend/public/_redirects` |
| 백엔드 | Render (Docker) | `render.yaml`, `backend/Dockerfile` |
| DB | Neon | — |
| 파일 | Cloudflare R2 | — |

### 1. Neon (PostgreSQL)

프로젝트를 만들고 접속 정보를 받는다. `DATABASE_URL`은 **JDBC 형식**이어야 한다
(Neon이 기본 제공하는 `postgresql://user:pass@host/db`를 그대로 넣으면 드라이버가 못 읽는다).

```
DATABASE_URL=jdbc:postgresql://ep-xxxx.ap-southeast-1.aws.neon.tech/neondb?sslmode=require
DATABASE_USERNAME=<neon user>
DATABASE_PASSWORD=<neon password>
```

Flyway가 기동 시 `backend/src/main/resources/db/migration`의 마이그레이션을 자동 실행한다.

### 2. Render (백엔드)

Render 대시보드에서 **New > Blueprint**로 이 저장소를 연결하면 `render.yaml`대로 서비스가 생성된다.
`JWT_SECRET`은 자동 생성되고, 나머지(`sync: false`)는 대시보드에서 직접 입력한다.
`APP_URL`은 생성된 자기 주소(`https://<서비스명>.onrender.com`), `FRONTEND_URL`은 Pages 주소를 넣는다.

### 3. Cloudflare Pages (프론트)

- Build command: `npm run build`
- Build output directory: `dist`
- Root directory: `frontend`
- 환경 변수: `VITE_API_URL` = Render 백엔드 주소

`public/_redirects`가 모든 경로를 `index.html`로 보내므로 `/{username}` 같은
vue-router history 모드 경로가 새로고침에도 404가 나지 않는다.

### 4. Google OAuth 리다이렉트 URI

Google Cloud Console > 사용자 인증 정보에서 **승인된 리디렉션 URI**에 아래를 추가한다.
프론트 주소가 아니라 **백엔드 주소**라는 점에 주의.

```
https://<서비스명>.onrender.com/login/oauth2/code/google
```

### 콜드 스타트

Render 무료 인스턴스는 15분간 요청이 없으면 잠들고, 다시 깨는 데 1분 안팎이 걸린다.
무료 한도가 월 750 인스턴스 시간이라 한 달(744시간)을 상시 가동해도 한도 안에 들어오므로,
UptimeRobot 등으로 `/actuator/health`를 10분 간격 핑하면 콜드 스타트를 없앨 수 있다.
