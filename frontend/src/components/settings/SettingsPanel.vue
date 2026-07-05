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
        <h3>게시판 목록</h3>
        <p class="layout-hint">아래 게시판을 메인 화면 영역으로 끌어와 배치하세요.</p>
        <div
          class="board-palette"
          @dragover.prevent
          @drop.prevent="dropToPalette"
        >
          <div
            v-for="section in availableSections"
            :key="section.value"
            class="board-chip"
            draggable="true"
            @dragstart="startDrag('palette', section.value)"
            @dragend="endDrag"
          >
            {{ section.label }}
          </div>
          <p v-if="availableSections.length === 0" class="palette-empty">모든 게시판이 배치되었습니다.</p>
        </div>
      </section>

      <section>
        <h3>메인 화면 배치</h3>
        <div
          class="layout-canvas"
          :class="{ 'drop-ready': !!drag }"
          @dragover.prevent
          @drop.prevent="dropToCanvas()"
        >
          <div
            v-for="(type, index) in placedSections"
            :key="type"
            class="placed-cell"
            draggable="true"
            @dragstart="startDrag('canvas', type, index)"
            @dragend="endDrag"
            @dragover.prevent
            @drop.prevent.stop="dropToCanvas(index)"
          >
            <span class="placed-label">{{ labelOf(type) }}</span>
            <button type="button" class="remove-button" title="배치 해제" @click="removeSection(index)">✕</button>
          </div>
          <p v-if="placedSections.length === 0" class="canvas-empty">
            여기로 게시판을 끌어다 놓으세요.
          </p>
        </div>
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
import { MAIN_SECTION_CHOICES, DEFAULT_MAIN_SECTIONS, SECTION_LABELS } from '../../utils/resume'
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
      // 메인에 배치된 게시판 순서
      placedSections: [...DEFAULT_MAIN_SECTIONS],
      // 진행 중인 드래그 정보 { from: 'palette' | 'canvas', value, index }
      drag: null,
      isSavingDesign: false,
      isSavingLayout: false
    }
  },
  computed: {
    // 아직 배치되지 않은 게시판만 목록에 노출
    availableSections() {
      return MAIN_SECTION_CHOICES.filter((s) => !this.placedSections.includes(s.value))
    }
  },
  async created() {
    const res = await axios.get('/api/settings')
    const settings = res.data.data
    this.selectedTheme = settings.theme || 'retro'
    if (settings.skinConfig?.['--primary']) this.skinPrimary = settings.skinConfig['--primary']
    if (settings.skinConfig?.['--bg']) this.skinBg = settings.skinConfig['--bg']
    this.profileStore.skinConfig = settings.skinConfig || {}

    // 과거 저장분의 폐지된 섹션(예: CAREER_DESC)은 걸러낸다
    const savedSections = (settings.layoutConfig?.sections || []).filter((type) => SECTION_LABELS[type])
    if (savedSections.length > 0) {
      this.placedSections = savedSections
    }

    if (!this.profileStore.hasProfile) {
      await this.profileStore.fetchMine()
    }
  },
  methods: {
    labelOf(type) {
      return SECTION_LABELS[type] || type
    },
    startDrag(from, value, index = null) {
      this.drag = { from, value, index }
    },
    endDrag() {
      this.drag = null
    },
    /** 목록/배치 영역에서 끌어온 게시판을 targetIndex 위치(없으면 맨 뒤)에 놓는다 */
    dropToCanvas(targetIndex = null) {
      if (!this.drag) return
      const { from, value } = this.drag
      const next = this.placedSections.filter((v) => v !== value)
      if (from === 'palette' && this.placedSections.includes(value)) {
        this.drag = null
        return
      }
      const insertAt = targetIndex === null ? next.length : Math.min(targetIndex, next.length)
      next.splice(insertAt, 0, value)
      this.placedSections = next
      this.drag = null
    },
    /** 배치된 게시판을 목록 영역에 놓으면 배치 해제 */
    dropToPalette() {
      if (!this.drag || this.drag.from !== 'canvas') {
        this.drag = null
        return
      }
      this.placedSections = this.placedSections.filter((v) => v !== this.drag.value)
      this.drag = null
    },
    removeSection(index) {
      this.placedSections.splice(index, 1)
    },
    resetLayout() {
      this.placedSections = [...DEFAULT_MAIN_SECTIONS]
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
        await this.profileStore.updateLayout([...this.placedSections])
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

.board-palette {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  min-height: 52px;
  padding: 0.6rem;
  border: 1px dashed var(--border);
  border-radius: 8px;
  background: var(--surface);
}

.board-chip {
  padding: 0.45rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--surface2, var(--surface));
  font-size: 0.85rem;
  cursor: grab;
  user-select: none;
}

.board-chip:active {
  cursor: grabbing;
}

.palette-empty,
.canvas-empty {
  margin: 0.3rem;
  font-size: 0.82rem;
  color: var(--text-muted);
}

.layout-canvas {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.6rem;
  min-height: 120px;
  padding: 0.75rem;
  border: 2px dashed var(--border);
  border-radius: 8px;
  background: var(--surface);
}

.layout-canvas.drop-ready {
  border-color: var(--primary);
}

.layout-canvas .canvas-empty {
  grid-column: 1 / -1;
  align-self: center;
  text-align: center;
}

.placed-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  padding: 0.7rem 0.8rem;
  border: 1px solid var(--primary);
  border-radius: 6px;
  background: var(--surface2, var(--surface));
  font-size: 0.85rem;
  cursor: grab;
  user-select: none;
}

.placed-cell:active {
  cursor: grabbing;
}

.placed-label {
  font-weight: 600;
}

.remove-button {
  border: none;
  background: transparent;
  color: var(--text-muted);
  font-size: 0.8rem;
  cursor: pointer;
  padding: 0.1rem 0.3rem;
}

.remove-button:hover {
  color: var(--primary);
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
