<template>
  <aside class="sidebar-right">
    <nav class="menu">
      <button
        type="button"
        class="menu-item"
        :class="{ active: boardStore.activeType === 'MAIN' }"
        @click="selectSection('MAIN')"
      >
        Main
      </button>
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
        <button
          type="button"
          class="menu-item"
          :class="{ active: boardStore.activeType === 'SETTINGS' }"
          @click="selectSection('SETTINGS')"
        >
          설정
        </button>
      </template>
    </nav>

    <div class="menu-divider" />

    <VisitorCounter :username="username" />

    <div class="menu-divider" />

    <GuestbookWidget :username="username" />
  </aside>
</template>

<script>
import { defineComponent } from 'vue'
import { useBoardStore } from '../../stores/useBoardStore'
import { MENU_SECTIONS } from '../../utils/resume'
import VisitorCounter from './VisitorCounter.vue'
import GuestbookWidget from '../guestbook/GuestbookWidget.vue'

export default defineComponent({
  name: 'SidebarRight',
  components: { VisitorCounter, GuestbookWidget },
  props: {
    isOwner: { type: Boolean, default: false },
    username: { type: String, required: true }
  },
  setup() {
    return { boardStore: useBoardStore() }
  },
  data() {
    return { sections: MENU_SECTIONS }
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

.sidebar-right > .menu-divider {
  margin: 1rem 0;
}
</style>
