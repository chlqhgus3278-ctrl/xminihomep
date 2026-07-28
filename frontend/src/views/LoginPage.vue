<template>
  <div class="theme-retro login-page">
    <div class="login-shell">
      <!-- 다이어리 책 본체: 왼쪽 표지 페이지 + 스프링 + 오른쪽 소개 페이지 -->
      <div class="login-frame">
        <header class="login-header">
          <div class="header-left">
            <span class="logo-badge">♥</span>
            <span class="header-title">folio 미니홈피</span>
          </div>
          <span class="header-date">{{ todayLabel }}</span>
        </header>

        <div class="login-body">
          <!-- 왼쪽 페이지: 로고 + 폴라로이드 -->
          <section class="login-col login-col--left">
            <div class="polaroid">
              <div class="polaroid-photo">
                <span class="polaroid-face">^_^</span>
              </div>
              <p class="polaroid-caption">my mini homepage</p>
            </div>

            <h1 class="brand">folio</h1>
            <p class="brand-tagline">싸이월드 감성 포트폴리오 홈피</p>

            <div class="counter-card">
              <div class="count-row">
                <span class="count-label">TODAY</span>
                <span class="count-value">is</span>
              </div>
              <div class="count-row">
                <span class="count-label">THE DAY</span>
                <span class="count-value">★</span>
              </div>
            </div>
          </section>

          <div class="login-spine" aria-hidden="true" />

          <!-- 오른쪽 페이지: 사이트 소개 + 로그인 -->
          <section class="login-col login-col--main">
            <h2 class="page-title">이 홈피는요 ...</h2>
            <p class="page-desc">
              <strong>folio</strong>는 2000년대 미니홈피 감성으로 꾸미는
              <strong>온라인 포트폴리오</strong>예요. 다이어리를 쓰듯 경력과 이력을
              한 장 한 장 채우면, 나만의 주소로 누구에게나 보여줄 수 있어요.
            </p>

            <ul class="feature-list">
              <li>
                <span class="feature-dot">✎</span>
                경력사항 · 자기소개서 · 학력 · 자격증까지 게시판으로 정리
              </li>
              <li>
                <span class="feature-dot">♬</span>
                프로필 · 기술스택 · BGM으로 나만의 홈피 꾸미기
              </li>
              <li>
                <span class="feature-dot">✉</span>
                방명록으로 방문자와 한 줄 인사 주고받기
              </li>
              <li>
                <span class="feature-dot">↗</span>
                링크 공유는 물론, 완성된 포트폴리오는 PDF로 저장
              </li>
            </ul>

            <div class="login-cta">
              <p class="cta-hint">구글 계정으로 3초 만에 내 홈피 만들기</p>
              <a class="google-login-button" :href="googleLoginUrl">
                <span class="google-badge">G</span>
                Google로 로그인
              </a>
              <p class="cta-address">로그인하면 <code>folio.com/나의아이디</code> 주소가 생겨요</p>
            </div>
          </section>
        </div>
      </div>

      <!-- 책 오른쪽에 붙은 장식용 인덱스 탭 -->
      <aside class="login-rail" aria-hidden="true">
        <span class="rail-tab rail-tab--active">Main</span>
        <span v-for="tab in railTabs" :key="tab" class="rail-tab">{{ tab }}</span>
      </aside>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { MENU_SECTIONS } from '../utils/resume'

export default defineComponent({
  name: 'LoginPage',
  computed: {
    googleLoginUrl() {
      return `${import.meta.env.VITE_API_URL}/oauth2/authorization/google`
    },
    railTabs() {
      return [...MENU_SECTIONS.map((s) => s.label), '방명록']
    },
    todayLabel() {
      const now = new Date()
      const pad = (n) => String(n).padStart(2, '0')
      return `${now.getFullYear()}.${pad(now.getMonth() + 1)}.${pad(now.getDate())}`
    }
  }
})
</script>

<style scoped>
/* 모눈종이 배경 (RetroLayout과 동일한 톤) */
.login-page {
  background-color: var(--page-bg);
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.5) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.5) 1px, transparent 1px);
  background-size: 22px 22px;
  min-height: 100vh;
  padding: 48px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text);
}

.login-shell {
  display: flex;
  align-items: stretch;
  width: 100%;
  max-width: 880px;
}

.login-frame {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--surface);
  border: 2px dashed var(--spine);
  border-radius: 18px;
  box-shadow: 0 8px 20px rgba(47, 79, 111, 0.18);
}

.login-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 24px;
  border-bottom: 2px dashed var(--border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--accent-orange);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
}

.header-title {
  font-size: 1.05rem;
}

.header-date {
  font-size: 0.78rem;
  color: var(--text-muted);
}

/* 왼쪽 페이지 | 스프링 | 오른쪽 페이지 */
.login-body {
  flex: 1;
  display: grid;
  grid-template-columns: 240px 26px 1fr;
}

.login-col {
  padding: 28px 24px;
}

/* 페이지가 스프링 쪽으로 말려 들어가는 그림자 */
.login-col--left {
  box-shadow: inset -8px 0 10px -8px rgba(47, 79, 111, 0.35);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-align: center;
}

.login-col--main {
  box-shadow: inset 8px 0 10px -8px rgba(47, 79, 111, 0.35);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 세로 스프링 제본 (RetroLayout과 동일한 링 SVG) */
.login-spine {
  background-color: #e9eff5;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='26' height='26'%3E%3Crect x='1.5' y='9' width='23' height='8' rx='4' fill='%23dfe9f2' stroke='%238fa3b8' stroke-width='1.5'/%3E%3C/svg%3E");
  background-repeat: repeat-y;
  background-position: center top;
  background-size: 26px 26px;
}

/* 폴라로이드 액자 */
.polaroid {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 8px 8px 6px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transform: rotate(-2deg);
}

.polaroid-photo {
  width: 132px;
  height: 108px;
  border-radius: 4px;
  background: var(--surface2);
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
}

.polaroid-face {
  font-size: 1.4rem;
  color: var(--tab-bg);
}

.polaroid-caption {
  margin: 6px 0 0;
  font-size: 0.68rem;
  color: var(--text-muted);
}

.brand {
  margin: 8px 0 0;
  font-size: 2.2rem;
  font-weight: 400;
  color: var(--tab-bg);
  letter-spacing: 2px;
}

.brand-tagline {
  margin: 0;
  font-size: 0.8rem;
  color: var(--text-muted);
}

/* 방문자 카운터 모양의 장식 카드 */
.counter-card {
  margin-top: auto;
  width: 100%;
  background: var(--surface);
  border: 1px solid var(--spine);
  border-radius: 10px;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.count-row {
  display: flex;
  justify-content: space-between;
}

.count-label {
  font-size: 0.68rem;
  color: var(--text-muted);
}

.count-value {
  font-size: 0.9rem;
  color: var(--counter-color);
}

/* 오른쪽 페이지: 소개 글 */
.page-title {
  margin: 0;
  font-size: 1.15rem;
  font-weight: 400;
  color: var(--accent-pink);
  border-bottom: 2px dashed var(--border);
  padding-bottom: 10px;
}

.page-desc {
  margin: 0;
  font-size: 0.88rem;
  line-height: 1.7;
}

.page-desc strong {
  color: var(--tab-bg);
}

.feature-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 0.8rem;
  line-height: 1.5;
}

.feature-list li {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.feature-dot {
  flex-shrink: 0;
  color: var(--accent-orange);
}

/* 로그인 영역 */
.login-cta {
  margin-top: auto;
  padding-top: 14px;
  border-top: 2px dashed var(--border);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  text-align: center;
}

.cta-hint {
  margin: 0;
  font-size: 0.78rem;
  color: var(--text-muted);
}

.google-login-button {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 22px;
  border-radius: 999px;
  background: var(--tab-bg);
  border: 1px solid var(--tab-border);
  color: #fff;
  font-size: 0.9rem;
  text-decoration: none;
  box-shadow: 1px 2px 3px rgba(31, 58, 86, 0.25);
  transition: background 0.12s;
}

.google-login-button:hover {
  background: #557a9f;
}

.google-badge {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #fff;
  color: #4285f4;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
}

.cta-address {
  margin: 0;
  font-size: 0.72rem;
  color: var(--text-muted);
}

.cta-address code {
  font-family: inherit;
  color: var(--accent-pink);
}

/* 책 오른쪽 장식용 인덱스 탭 (실제 홈피 메뉴 미리보기) */
.login-rail {
  width: 108px;
  flex-shrink: 0;
  margin-left: -2px;
  padding-top: 72px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.rail-tab {
  background: var(--tab-bg);
  border: 1px solid var(--tab-border);
  border-left: none;
  border-radius: 0 12px 12px 0;
  color: #fff;
  font-size: 0.72rem;
  font-weight: 600;
  padding: 7px 12px;
  box-shadow: 1px 2px 3px rgba(31, 58, 86, 0.25);
}

.rail-tab--active {
  background: var(--accent-orange);
  border-color: #e0862a;
}

@media (max-width: 768px) {
  .login-page {
    padding: 24px 12px;
  }

  .login-shell {
    flex-direction: column;
  }

  .login-rail {
    display: none;
  }

  .login-body {
    grid-template-columns: 1fr;
  }

  .login-spine {
    display: none;
  }

  .login-col--left,
  .login-col--main {
    box-shadow: none;
  }

  .login-col--left {
    border-bottom: 2px dashed var(--border);
  }

  .counter-card {
    display: none;
  }
}
</style>
