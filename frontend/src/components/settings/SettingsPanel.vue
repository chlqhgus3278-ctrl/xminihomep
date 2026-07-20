<template>
  <div class="settings-panel">
    <div class="tabs">
      <button
        type="button"
        class="tab"
        :class="{ active: activeTab === 'design' }"
        @click="activeTab = 'design'"
      >
        디자인
      </button>
      <button
        type="button"
        class="tab"
        :class="{ active: activeTab === 'layout' }"
        @click="activeTab = 'layout'"
      >
        레이아웃
      </button>
    </div>

    <template v-if="activeTab === 'design'">
      <section>
        <h3>테마</h3>
        <div class="theme-options">
          <label class="theme-option">
            <input type="radio" value="retro" v-model="selectedTheme" />
            <div class="theme-preview theme-retro">
              <div class="preview-header" />
              <div class="preview-body" />
            </div>
            <span>레트로</span>
          </label>
          <label class="theme-option">
            <input type="radio" value="modern" v-model="selectedTheme" />
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
            <input type="color" v-model="skinPrimary" />
          </label>
          <label class="color-field">
            배경 색상
            <input type="color" v-model="skinBg" />
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

      <div class="save-row">
        <button type="button" class="save-button" :disabled="isSavingDesign" @click="saveDesign">
          {{ isSavingDesign ? '저장 중…' : '저장' }}
        </button>
      </div>
    </template>

    <template v-else>
      <section>
        <h3>메인 화면 순서</h3>
        <p class="layout-hint">
          메인 화면에는 모든 게시판의 내용이 아래 순서대로 표시됩니다. ≡ 를 잡고 끌어서 순서를 바꿔 보세요.
        </p>
        <ul class="order-list">
          <li
            v-for="(type, index) in orderedSections"
            :key="type"
            class="order-item"
            :class="{ dragging: dragIndex === index }"
            draggable="true"
            @dragstart="onDragStart($event, index)"
            @dragover.prevent="onDragOver(index)"
            @drop.prevent
            @dragend="onDragEnd"
          >
            <span class="drag-handle" aria-hidden="true">≡</span>
            <span class="order-number">{{ index + 1 }}</span>
            <span class="order-label">{{ labelOf(type) }}</span>
          </li>
        </ul>
        <button type="button" class="layout-reset" @click="resetLayout">기본값으로</button>
      </section>

      <div class="save-row">
        <button type="button" class="save-button" :disabled="isSavingLayout" @click="saveLayout">
          {{ isSavingLayout ? '저장 중…' : '저장' }}
        </button>
      </div>
    </template>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useProfileStore } from '../../stores/useProfileStore'
import { DEFAULT_MAIN_SECTIONS, SECTION_LABELS, normalizeMainSections } from '../../utils/resume'
import { showAlert } from '../../utils/dialog'

export default defineComponent({
  name: 'SettingsPanel',
  setup() {
    return {
      profileStore: useProfileStore()
    }
  },
  data() {
    return {
      activeTab: 'design',
      selectedTheme: 'retro',
      skinPrimary: '#3a86c0',
      skinBg: '#b8cfe8',
      // 메인 화면에 표시될 게시판 순서 (모든 게시판이 항상 포함된다)
      orderedSections: normalizeMainSections(DEFAULT_MAIN_SECTIONS),
      // 드래그 중인 항목의 현재 인덱스 (드래그 없으면 null)
      dragIndex: null,
      isSavingDesign: false,
      isSavingLayout: false
    }
  },
  async created() {
    const res = await axios.get('/api/settings')
    const settings = res.data.data
    this.selectedTheme = settings.theme || 'retro'
    if (settings.skinConfig?.['--primary']) this.skinPrimary = settings.skinConfig['--primary']
    if (settings.skinConfig?.['--bg']) this.skinBg = settings.skinConfig['--bg']
    this.profileStore.skinConfig = settings.skinConfig || {}

    this.orderedSections = normalizeMainSections(settings.layoutConfig?.sections)

    if (!this.profileStore.hasProfile) {
      await this.profileStore.fetchMine()
    }
  },
  methods: {
    labelOf(type) {
      return SECTION_LABELS[type] || type
    },
    onDragStart(event, index) {
      // Firefox는 setData를 호출해야 드래그가 시작된다
      event.dataTransfer.setData('text/plain', '')
      event.dataTransfer.effectAllowed = 'move'
      this.dragIndex = index
    },
    /** 드래그 중인 항목이 다른 항목 위를 지나면 그 자리로 실시간 이동 */
    onDragOver(index) {
      if (this.dragIndex === null || this.dragIndex === index) return
      const next = [...this.orderedSections]
      const [moved] = next.splice(this.dragIndex, 1)
      next.splice(index, 0, moved)
      this.orderedSections = next
      this.dragIndex = index
    },
    onDragEnd() {
      this.dragIndex = null
    },
    resetLayout() {
      this.orderedSections = normalizeMainSections(DEFAULT_MAIN_SECTIONS)
    },
    resetSkin() {
      this.skinPrimary = '#3a86c0'
      this.skinBg = '#b8cfe8'
    },
    async saveDesign() {
      this.isSavingDesign = true
      try {
        await this.profileStore.updateTheme(this.selectedTheme)
        await this.profileStore.updateSkin({ '--primary': this.skinPrimary, '--bg': this.skinBg })
        await showAlert('저장되었습니다.')
      } finally {
        this.isSavingDesign = false
      }
    },
    async saveLayout() {
      this.isSavingLayout = true
      try {
        await this.profileStore.updateLayout([...this.orderedSections])
        await showAlert('저장되었습니다.')
      } finally {
        this.isSavingLayout = false
      }
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
.tabs {
  display: flex;
  gap: 0.4rem;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--border);
}

.tab {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border);
  border-bottom: none;
  border-radius: 6px 6px 0 0;
  background: var(--surface);
  color: var(--text-muted, var(--text));
  font-size: 0.875rem;
  cursor: pointer;
}

.tab.active {
  color: var(--primary);
  font-weight: 700;
  border-color: var(--primary);
}

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

.layout-hint {
  margin: 0 0 0.75rem;
  font-size: 0.82rem;
  color: var(--text-muted);
}

.order-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-width: 360px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.6rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--surface2, var(--surface));
  font-size: 0.85rem;
  cursor: grab;
  user-select: none;
}

.order-item:active {
  cursor: grabbing;
}

.order-item.dragging {
  opacity: 0.5;
  border-style: dashed;
  border-color: var(--primary);
}

.drag-handle {
  color: var(--text-muted);
  font-size: 1.1rem;
  line-height: 1;
  flex-shrink: 0;
}

.order-number {
  width: 1.4rem;
  height: 1.4rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  font-size: 0.72rem;
  font-weight: 700;
  flex-shrink: 0;
}

.order-label {
  flex: 1;
  font-weight: 600;
}


.layout-reset {
  margin-top: 0.75rem;
  font-size: 0.8rem;
}

.save-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border);
}

.save-button {
  padding: 0.5rem 1.5rem;
  font-size: 0.875rem;
  border: 1px solid var(--primary);
  border-radius: 6px;
  background: var(--primary);
  color: #fff;
  cursor: pointer;
}

.save-button:disabled {
  opacity: 0.6;
  cursor: default;
}
</style>
