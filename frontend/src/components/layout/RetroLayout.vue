<template>
  <div id="portfolio-content" class="theme-retro retro-layout" :style="skinConfig">
    <header class="retro-header">
      <h1>{{ profile?.homepageTitle || `${profile?.displayName || username}의 홈피` }}</h1>
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
      />

      <main class="retro-col retro-col--main">
        <slot />
      </main>

      <SidebarRight
        class="retro-col retro-col--right"
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
  name: 'RetroLayout',
  components: { SidebarLeft, SidebarRight, BgmPlayer, PdfExportButton, ShareButton },
  props: {
    profile: { type: Object, default: null },
    username: { type: String, required: true },
    isOwner: { type: Boolean, default: false },
    homeLink: { type: String, required: true },
    skinConfig: { type: Object, default: () => ({}) }
  }
})
</script>

<style scoped>
.retro-layout {
  background: var(--bg);
  color: var(--text);
  min-height: 100vh;
  font-family: 'Dotum', 'Malgun Gothic', sans-serif;
}

.retro-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  background: var(--header-gradient);
  color: #fff;
  padding: 1rem 1.5rem;
  border-bottom: 3px solid var(--border);
}

.retro-header h1 {
  margin: 0;
  font-size: 1.25rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.header-actions :deep(.header-btn),
.header-actions :deep(.header-share button) {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: #fff;
  font-size: 0.75rem;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  cursor: pointer;
}

.retro-body {
  display: grid;
  grid-template-columns: 220px 1fr 200px;
  align-items: start;
}

.retro-col {
  background: var(--surface);
  border-right: 2px dashed var(--border);
  padding: 1rem;
  min-height: 70vh;
}

.retro-col--main {
  background: var(--surface2);
}

.retro-col--right {
  border-right: none;
}
</style>
