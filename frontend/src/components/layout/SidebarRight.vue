<template>
  <aside class="sidebar-right">
    <nav class="menu">
      <router-link :to="homeLink" class="menu-item">Home</router-link>
      <button
        v-for="section in sections"
        :key="section.value"
        type="button"
        class="menu-item"
        :class="{ active: boardStore.activeType === section.value }"
        @click="selectSection(section.value)"
      >
        {{ section.label }}
      </button>
      <template v-if="isOwner">
        <div class="menu-divider" />
        <router-link to="/settings" class="menu-item">설정</router-link>
      </template>
    </nav>
  </aside>
</template>

<script>
import { defineComponent } from 'vue'
import { useBoardStore } from '../../stores/useBoardStore'

const SECTIONS = [
  { value: 'CAREER_HISTORY', label: '경력사항' },
  { value: 'CAREER_DESC', label: '경력기술서' },
  { value: 'INTRO', label: '자기소개서' },
  { value: 'EDUCATION', label: '학력' },
  { value: 'CERT', label: '자격증' },
  { value: 'LANGUAGE', label: '어학' },
  { value: 'SKILLS', label: '기술스택' }
]

export default defineComponent({
  name: 'SidebarRight',
  props: {
    isOwner: { type: Boolean, default: false },
    homeLink: { type: String, required: true },
    username: { type: String, required: true }
  },
  setup() {
    return { boardStore: useBoardStore() }
  },
  data() {
    return { sections: SECTIONS }
  },
  methods: {
    selectSection(type) {
      this.boardStore.setActiveType(type)
    }
  }
})
</script>

<style scoped>
.menu {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.menu-item {
  display: block;
  width: 100%;
  padding: 0.5rem 0.75rem;
  border-radius: 4px;
  border: 1px solid var(--border);
  background: var(--surface2, transparent);
  color: var(--text);
  font-size: 0.875rem;
  font-family: inherit;
  box-shadow: var(--btn-shadow, none);
  text-decoration: none;
  text-align: center;
  cursor: pointer;
  box-sizing: border-box;
}

.menu-item:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.menu-item.active {
  border-color: var(--primary);
  color: var(--primary);
  font-weight: 700;
}

.menu-divider {
  border-top: 1px dashed var(--border);
  margin: 0.5rem 0;
}
</style>
