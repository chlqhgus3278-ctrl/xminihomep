<template>
  <div id="portfolio-content" class="theme-modern modern-layout" :style="skinConfig">
    <header class="modern-header">
      <h1>{{ profile?.homepageTitle || profile?.displayName || username }}</h1>
      <div class="header-actions">
        <BgmPlayer :bgm-url="profile?.bgmUrl" :bgm-title="profile?.bgmTitle" />
        <PdfExportButton class="header-btn" :username="username" />
        <ShareButton class="header-share" />
      </div>
    </header>

    <div class="modern-body">
      <SidebarLeft
        class="modern-col modern-col--left"
        :profile="profile"
        :username="username"
        :is-owner="isOwner"
        :posts="boardPosts"
      />

      <main class="modern-col modern-col--main">
        <slot />
      </main>

      <SidebarRight
        class="modern-col modern-col--right"
        :is-owner="isOwner"
        :home-link="homeLink"
        :username="username"
      />
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import SidebarLeft from './SidebarLeft.vue'
import SidebarRight from './SidebarRight.vue'
import BgmPlayer from '../bgm/BgmPlayer.vue'
import PdfExportButton from '../common/PdfExportButton.vue'
import ShareButton from '../common/ShareButton.vue'

export default defineComponent({
  name: 'ModernLayout',
  components: { SidebarLeft, SidebarRight, BgmPlayer, PdfExportButton, ShareButton },
  props: {
    profile: { type: Object, default: null },
    username: { type: String, required: true },
    isOwner: { type: Boolean, default: false },
    homeLink: { type: String, required: true },
    skinConfig: { type: Object, default: () => ({}) },
    boardPosts: { type: Array, default: () => [] }
  }
})
</script>

<style scoped>
.modern-layout {
  background: var(--bg);
  color: var(--text);
  min-height: 100vh;
}

.modern-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid var(--border);
  background: var(--surface);
}

.modern-header h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
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
  color: var(--text);
  font-size: 0.75rem;
  padding: 0.3rem 0.6rem;
  border-radius: 6px;
  cursor: pointer;
}

.modern-body {
  display: grid;
  grid-template-columns: 240px 1fr 220px;
  gap: 1rem;
  padding: 1.5rem;
}

.modern-col {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.25rem;
  min-height: 70vh;
}

.modern-col--main {
  background: var(--surface2);
  box-shadow: 0 0 24px var(--primary-glow, transparent);
}
</style>
