<template>
  <Teleport to="body">
    <div class="toast-stack" :class="themeClass">
      <TransitionGroup name="toast">
        <div
          v-for="item in toastState.items"
          :key="item.id"
          class="toast-item"
          :class="`toast-item--${item.type}`"
        >
          {{ item.message }}
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script>
import { defineComponent } from 'vue'
import { toastState } from '../../utils/toast'
import { useProfileStore } from '../../stores/useProfileStore'

export default defineComponent({
  name: 'AppToast',
  setup() {
    return { toastState, profileStore: useProfileStore() }
  },
  computed: {
    themeClass() {
      return this.profileStore.currentTheme === 'modern' ? 'theme-modern' : 'theme-retro'
    }
  }
})
</script>

<style scoped>
.toast-stack {
  position: fixed;
  right: 16px;
  bottom: 16px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.toast-item {
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 0.85rem;
  color: #fff;
  background: var(--text, #333);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.toast-item--success {
  background: var(--primary, #3a86c0);
}

.toast-item--error {
  background: #e05252;
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
