<template>
  <div class="resume-form">
    <div class="field">
      <label>기술 검색</label>
      <div class="skill-search">
        <input
          v-model="query"
          placeholder="예: 백엔드, 프론트엔드, Java, Vue…"
          @keydown.enter.prevent="addFromQuery"
          @keydown.down.prevent="moveHighlight(1)"
          @keydown.up.prevent="moveHighlight(-1)"
        />
        <ul v-if="suggestions.length" class="suggestion-list">
          <li
            v-for="(item, index) in suggestions"
            :key="item.name"
            :class="{ highlighted: index === highlightIndex }"
            @mousedown.prevent="addTag(item.name)"
          >
            <span>{{ item.name }}</span>
            <span class="suggestion-category">{{ item.category }}</span>
          </li>
        </ul>
      </div>
      <p class="hint">목록에 없는 기술은 입력 후 Enter로 직접 추가할 수 있습니다.</p>
    </div>

    <div v-if="modelValue.tags.length" class="tag-list">
      <span
        v-for="(tag, index) in modelValue.tags"
        :key="tag"
        class="skill-tag draggable-tag"
        :class="{ dragging: dragIndex === index }"
        :data-tag-index="index"
        @pointerdown="onPointerDown(index, $event)"
      >
        {{ tag }}
        <button type="button" class="tag-remove" @click="removeTag(tag)">×</button>
      </span>
    </div>
    <p v-else class="hint">아직 추가된 기술이 없습니다.</p>
    <p v-if="modelValue.tags.length > 1" class="hint">태그를 꾹 누른 채 끌면 순서를 바꿀 수 있습니다.</p>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { SKILL_SUGGESTIONS } from '../../../utils/resume'

// 꾹 누르기(long-press)로 판정하기까지의 시간과, 그 전에 취소로 간주할 이동 거리
const LONG_PRESS_MS = 250
const CANCEL_MOVE_PX = 12

export default defineComponent({
  name: 'SkillsForm',
  props: {
    modelValue: { type: Object, required: true }
  },
  data() {
    return {
      query: '',
      highlightIndex: 0,
      dragIndex: null,
      pressTimer: null,
      pressStart: null
    }
  },
  computed: {
    suggestions() {
      const keyword = this.query.trim().toLowerCase()
      if (!keyword) return []
      return SKILL_SUGGESTIONS.filter(
        (item) =>
          !this.modelValue.tags.includes(item.name) &&
          (item.name.toLowerCase().includes(keyword) || item.category.includes(keyword))
      ).slice(0, 8)
    }
  },
  watch: {
    query() {
      this.highlightIndex = 0
    }
  },
  created() {
    if (!Array.isArray(this.modelValue.tags)) {
      this.modelValue.tags = []
    }
  },
  beforeUnmount() {
    this.endDrag()
  },
  methods: {
    addTag(name) {
      const trimmed = (name || '').trim()
      if (trimmed && !this.modelValue.tags.includes(trimmed)) {
        this.modelValue.tags.push(trimmed)
      }
      this.query = ''
    },
    addFromQuery() {
      if (this.suggestions.length > 0) {
        this.addTag(this.suggestions[this.highlightIndex]?.name || this.suggestions[0].name)
      } else {
        this.addTag(this.query)
      }
    },
    moveHighlight(delta) {
      if (!this.suggestions.length) return
      const size = this.suggestions.length
      this.highlightIndex = (this.highlightIndex + delta + size) % size
    },
    removeTag(tag) {
      this.modelValue.tags = this.modelValue.tags.filter((item) => item !== tag)
    },
    // ----- 꾹 눌러서 순서 변경 -----
    onPointerDown(index, event) {
      if (event.target.closest('.tag-remove')) return
      this.pressStart = { x: event.clientX, y: event.clientY }
      this.pressTimer = setTimeout(() => {
        this.dragIndex = index
      }, LONG_PRESS_MS)
      window.addEventListener('pointermove', this.onPointerMove)
      window.addEventListener('pointerup', this.endDrag)
      window.addEventListener('pointercancel', this.endDrag)
    },
    onPointerMove(event) {
      // 롱프레스 판정 전에 크게 움직이면(스크롤 등) 드래그를 취소한다
      if (this.dragIndex === null) {
        if (
          this.pressStart &&
          Math.hypot(event.clientX - this.pressStart.x, event.clientY - this.pressStart.y) > CANCEL_MOVE_PX
        ) {
          this.clearPress()
        }
        return
      }
      event.preventDefault()
      const target = document
        .elementFromPoint(event.clientX, event.clientY)
        ?.closest('[data-tag-index]')
      if (!target) return
      const overIndex = Number(target.dataset.tagIndex)
      if (overIndex !== this.dragIndex) {
        const tags = this.modelValue.tags
        const [moved] = tags.splice(this.dragIndex, 1)
        tags.splice(overIndex, 0, moved)
        this.dragIndex = overIndex
      }
    },
    clearPress() {
      clearTimeout(this.pressTimer)
      this.pressTimer = null
      this.pressStart = null
      window.removeEventListener('pointermove', this.onPointerMove)
      window.removeEventListener('pointerup', this.endDrag)
      window.removeEventListener('pointercancel', this.endDrag)
    },
    endDrag() {
      this.clearPress()
      this.dragIndex = null
    }
  }
})
</script>

<style scoped>
.skill-search {
  position: relative;
}

.skill-search input {
  width: 100%;
}

.suggestion-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: 10;
  margin: 2px 0 0;
  padding: 0.25rem;
  list-style: none;
  background: var(--surface, #fff);
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  max-height: 240px;
  overflow-y: auto;
}

.suggestion-list li {
  display: flex;
  justify-content: space-between;
  gap: 0.5rem;
  padding: 0.35rem 0.5rem;
  border-radius: 3px;
  cursor: pointer;
  font-size: 0.85rem;
}

.suggestion-list li:hover,
.suggestion-list li.highlighted {
  background: var(--surface2, rgba(0, 0, 0, 0.06));
  color: var(--primary);
}

.suggestion-category {
  color: var(--text-muted, #888);
  font-size: 0.75rem;
}

.draggable-tag {
  cursor: grab;
  touch-action: none;
  user-select: none;
}

.draggable-tag.dragging {
  opacity: 0.5;
  cursor: grabbing;
  border-style: dashed;
}
</style>
