<template>
  <div class="settings-panel">
    <section>
      <h3>테마</h3>
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
      <h3>커스텀 색상</h3>
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
      <h3>배경음악(BGM)</h3>
      <div v-if="profileStore.profile?.bgmUrl" class="bgm-current">
        <span>{{ profileStore.profile.bgmTitle }}</span>
        <button type="button" @click="handleDeleteBgm">삭제</button>
      </div>
      <input v-else type="file" accept="audio/*" @change="handleUploadBgm" />
    </section>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useProfileStore } from '../../stores/useProfileStore'

export default defineComponent({
  name: 'SettingsPanel',
  setup() {
    return {
      profileStore: useProfileStore()
    }
  },
  data() {
    return {
      selectedTheme: 'retro',
      skinPrimary: '#3a86c0',
      skinBg: '#b8cfe8'
    }
  },
  async created() {
    const res = await axios.get('/api/settings')
    const settings = res.data.data
    this.selectedTheme = settings.theme || 'retro'
    if (settings.skinConfig?.['--primary']) this.skinPrimary = settings.skinConfig['--primary']
    if (settings.skinConfig?.['--bg']) this.skinBg = settings.skinConfig['--bg']
    this.profileStore.skinConfig = settings.skinConfig || {}

    if (!this.profileStore.hasProfile) {
      await this.profileStore.fetchMine()
    }
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
    }
  }
})
</script>

<style scoped>
section {
  margin-bottom: 2rem;
}

section h3 {
  margin: 0 0 0.75rem;
  font-size: 0.95rem;
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
</style>
