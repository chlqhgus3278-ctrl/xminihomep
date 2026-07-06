<template>
  <div class="theme-retro retro-page">
    <div class="retro-shell">
      <!-- 책 본체: 왼쪽 페이지 + 스프링 제본 + 오른쪽 페이지 -->
      <div id="portfolio-content" class="retro-frame" :style="skinConfig">
        <header class="retro-header">
          <div class="header-left">
            <span class="logo-badge">♥</span>
            <EditableTitle
              class="header-title"
              :profile="profile"
              :username="username"
              :is-owner="isOwner"
              :fallback-title="`${profile?.displayName || username}의 홈피`"
            />
          </div>
          <div class="header-actions">
            <BgmPlayer :bgm-url="profile?.bgmUrl" :bgm-title="profile?.bgmTitle" />
            <PdfExportButton class="header-btn" :username="username" />
            <ShareButton class="header-share" />
          </div>
        </header>

        <div class="retro-body">
          <SidebarLeft
            class="retro-col retro-col--left"
            :profile="profile"
            :username="username"
            :is-owner="isOwner"
            :posts="boardPosts"
          />

          <div class="retro-spine" aria-hidden="true" />

          <main class="retro-col retro-col--main">
            <slot />
          </main>
        </div>
      </div>

      <!-- 책 오른쪽에 붙은 인덱스 탭 + 위젯 레일 -->
      <SidebarRight class="retro-rail" :is-owner="isOwner" :username="username" />
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import SidebarLeft from './SidebarLeft.vue'
import SidebarRight from './SidebarRight.vue'
import EditableTitle from './EditableTitle.vue'
import BgmPlayer from '../bgm/BgmPlayer.vue'
import PdfExportButton from '../common/PdfExportButton.vue'
import ShareButton from '../common/ShareButton.vue'

export default defineComponent({
  name: 'RetroLayout',
  components: { SidebarLeft, SidebarRight, EditableTitle, BgmPlayer, PdfExportButton, ShareButton },
  props: {
    profile: { type: Object, default: null },
    username: { type: String, required: true },
    isOwner: { type: Boolean, default: false },
    skinConfig: { type: Object, default: () => ({}) },
    boardPosts: { type: Array, default: () => [] }
  }
})
</script>

<style scoped>
/* 모눈종이 배경 */
.retro-page {
  background-color: var(--page-bg);
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.5) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.5) 1px, transparent 1px);
  background-size: 22px 22px;
  height: 100vh;
  overflow: hidden;
  padding: 32px 16px;
  display: flex;
  justify-content: center;
  color: var(--text);
}

.retro-shell {
  display: flex;
  align-items: stretch;
  width: 100%;
  max-width: 1160px;
  height: 100%;
}

.retro-frame {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--surface);
  border: 2px dashed var(--spine);
  border-radius: 18px;
  box-shadow: 0 8px 20px rgba(47, 79, 111, 0.18);
}

.retro-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 24px;
  border-bottom: 2px dashed var(--border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.logo-badge {
  flex-shrink: 0;
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

.retro-header .header-title {
  font-size: 1.1rem;
  color: var(--text);
}

.retro-header .header-title :deep(.title-input) {
  border: 1px solid var(--border);
  background: var(--surface2);
  color: var(--text);
}

.retro-header .header-title :deep(.title-button) {
  border-color: var(--border);
  color: var(--text-muted);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.header-actions :deep(.header-btn),
.header-actions :deep(.header-share button) {
  background: var(--surface2);
  border: 1px solid var(--border);
  color: var(--text-muted);
  font-size: 0.75rem;
  padding: 0.3rem 0.6rem;
  border-radius: 999px;
  cursor: pointer;
}

/* 왼쪽 페이지 | 스프링 | 오른쪽 페이지 */
.retro-body {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 220px 26px 1fr;
  grid-template-rows: minmax(0, 1fr);
}

/* 화면 높이 고정: 내용이 길면 페이지(칼럼) 안에서만 스크롤 */
.retro-col {
  padding: 16px;
  overflow-y: auto;
}

/* 페이지가 스프링 쪽으로 말려 들어가는 그림자 (책 넘김 느낌) */
.retro-col--left {
  box-shadow: inset -8px 0 10px -8px rgba(47, 79, 111, 0.35);
}

.retro-col--main {
  box-shadow: inset 8px 0 10px -8px rgba(47, 79, 111, 0.35);
}

/* 세로 스프링 제본: 링 모양 SVG를 세로로 반복 */
.retro-spine {
  background-color: #e9eff5;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='26' height='26'%3E%3Crect x='1.5' y='9' width='23' height='8' rx='4' fill='%23dfe9f2' stroke='%238fa3b8' stroke-width='1.5'/%3E%3C/svg%3E");
  background-repeat: repeat-y;
  background-position: center top;
  background-size: 26px 26px;
}

/* 우측 레일: 프레임 밖 모눈종이 위에 떠 있는 탭/위젯 */
.retro-rail {
  width: 148px;
  flex-shrink: 0;
  margin-left: -2px;
  padding-top: 80px;
  position: relative;
  z-index: 1;
  overflow-y: auto;
}

@media (max-width: 768px) {
  /* 모바일은 세로 스택이라 화면 고정 대신 일반 문서 흐름으로 */
  .retro-page {
    padding: 0;
    height: auto;
    min-height: 100vh;
    overflow: visible;
  }

  .retro-shell {
    flex-direction: column;
    height: auto;
  }

  .retro-frame {
    border-radius: 0;
    border-left: none;
    border-right: none;
  }

  .retro-body {
    grid-template-columns: 1fr;
    grid-template-rows: none;
  }

  .retro-spine {
    display: none;
  }

  .retro-col {
    min-height: auto;
    box-shadow: none;
    overflow: visible;
  }

  .retro-col--left {
    border-bottom: 2px dashed var(--border);
  }

  .retro-rail {
    width: 100%;
    margin-left: 0;
    padding: 8px;
    position: sticky;
    bottom: 0;
    z-index: 5;
    overflow-y: visible;
  }
}
</style>
