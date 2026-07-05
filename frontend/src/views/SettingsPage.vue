<template>
  <div class="settings-page">
    <router-link to="/">← 뒤로</router-link>
    <h1>설정</h1>

    <section>
      <h2>테마</h2>
      <div class="theme-options">
        <label class="theme-option">
          <input type="radio" value="retro" v-model="selectedTheme" @change="applyTheme" />
          <div class="theme-preview theme-retro">
            <div class="preview-header" />
            <div class="preview-body" />
          </div>
          <span>레트로</span>
        </label>
        <label class="theme-option">
          <input type="radio" value="modern" v-model="selectedTheme" @change="applyTheme" />
          <div class="theme-preview theme-modern">
            <div class="preview-header" />
            <div class="preview-body" />
          </div>
          <span>모던</span>
        </label>
      </div>
    </section>

    <section>
      <h2>커스텀 색상</h2>
      <div class="color-pickers">
        <label class="color-field">
          포인트 색상
          <input type="color" v-model="skinPrimary" @change="applySkin" />
        </label>
        <label class="color-field">
          배경 색상
          <input type="color" v-model="skinBg" @change="applySkin" />
        </label>
        <button type="button" @click="resetSkin">기본값으로</button>
      </div>
    </section>

    <section>
      <h2>배경음악(BGM)</h2>
      <div v-if="profileStore.profile?.bgmUrl" class="bgm-current">
        <span>{{ profileStore.profile.bgmTitle }}</span>
        <button type="button" @click="handleDeleteBgm">삭제</button>
      </div>
      <input v-else type="file" accept="audio/*" @change="handleUploadBgm" />
    </section>

    <section>
      <h2>섹션별 공개 여부</h2>
      <div class="section-visibility">
        <label class="section-toggle" v-for="type in boardTypes" :key="type.value">
          <input type="checkbox" :checked="isSectionPublic(type.value)" @change="toggleSection(type.value)" />
          {{ type.label }}
        </label>
      </div>

      <label class="overall-visibility">
        <input type="checkbox" v-model="overallPublic" @change="applyVisibility" />
        홈피 전체 공개
      </label>
    </section>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useProfileStore } from '../stores/useProfileStore'
import { useBoardStore } from '../stores/useBoardStore'

const BOARD_TYPES = [
  { value: 'CAREER_HISTORY', label: '경력사항' },
  { value: 'CAREER_DESC', label: '경력기술서' },
  { value: 'INTRO', label: '자기소개서' },
  { value: 'EDUCATION', label: '학력' },
  { value: 'CERT', label: '자격증' },
  { value: 'LANGUAGE', label: '어학' },
  { value: 'SKILLS', label: '기술스택' }
]

export default defineComponent({
  name: 'SettingsPage',
  setup() {
    return {
      profileStore: useProfileStore(),
      boardStore: useBoardStore()
    }
  },
  data() {
    return {
      boardTypes: BOARD_TYPES,
      selectedTheme: 'retro',
      skinPrimary: '#3a86c0',
      skinBg: '#b8cfe8',
      overallPublic: true
    }
  },
  async created() {
    const res = await axios.get('/api/settings')
    const settings = res.data.data
    this.selectedTheme = settings.theme || 'retro'
    this.overallPublic = settings.isPublic
    if (settings.skinConfig?.['--primary']) this.skinPrimary = settings.skinConfig['--primary']
    if (settings.skinConfig?.['--bg']) this.skinBg = settings.skinConfig['--bg']
    this.profileStore.skinConfig = settings.skinConfig || {}

    if (!this.profileStore.hasProfile) {
      await this.profileStore.fetchMine()
    }
    await this.boardStore.fetchMyPosts()
  },
  methods: {
    async applyTheme() {
      await this.profileStore.updateTheme(this.selectedTheme)
    },
    async applySkin() {
      await this.profileStore.updateSkin({ '--primary': this.skinPrimary, '--bg': this.skinBg })
    },
    async resetSkin() {
      this.skinPrimary = '#3a86c0'
      this.skinBg = '#b8cfe8'
      await this.profileStore.updateSkin({})
    },
    async handleUploadBgm(event) {
      const file = event.target.files[0]
      if (!file) return
      await this.profileStore.uploadBgm(file)
    },
    async handleDeleteBgm() {
      await this.profileStore.deleteBgm()
    },
    isSectionPublic(type) {
      const posts = this.boardStore.postsByType(type)
      return posts.length === 0 || posts.every((post) => post.isPublic)
    },
    async toggleSection(type) {
      const shouldBePublic = !this.isSectionPublic(type)
      const posts = this.boardStore.postsByType(type)
      await Promise.all(
        posts
          .filter((post) => post.isPublic !== shouldBePublic)
          .map((post) => this.boardStore.toggleVisibility(post.id))
      )
    },
    async applyVisibility() {
      await axios.put('/api/settings/visibility', { isPublic: this.overallPublic })
    }
  }
})
</script>

<style scoped>
.settings-page {
  max-width: 480px;
  margin: 0 auto;
  padding: 2rem;
}

section {
  margin-bottom: 2rem;
}

.theme-options {
  display: flex;
  gap: 1.5rem;
}

.theme-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.4rem;
  cursor: pointer;
}

.theme-preview {
  width: 80px;
  height: 50px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid var(--border, #ccc);
  background: var(--bg);
}

.preview-header {
  height: 30%;
  background: var(--header-gradient, var(--primary));
}

.preview-body {
  height: 70%;
  background: var(--surface2, var(--surface));
}

.color-pickers {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.color-field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.875rem;
}

.bgm-current {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.section-visibility {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  margin-bottom: 1rem;
}

.section-toggle {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.overall-visibility {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
}
</style>
