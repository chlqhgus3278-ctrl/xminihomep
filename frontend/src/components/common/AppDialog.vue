<template>
  <Teleport to="body">
    <div v-if="dialogState.visible" class="dialog-overlay" :class="themeClass" @click.self="onOverlay">
      <div class="dialog-box" role="dialog" aria-modal="true">
        <p class="dialog-message">{{ dialogState.message }}</p>
        <div class="dialog-actions">
          <button
            v-if="dialogState.type === 'confirm'"
            type="button"
            class="dialog-button"
            @click="closeDialog(false)"
          >
            취소
          </button>
          <button type="button" class="dialog-button dialog-button--primary" @click="closeDialog(true)">
            확인
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script>
import { defineComponent } from 'vue'
import { dialogState, closeDialog } from '../../utils/dialog'
import { useProfileStore } from '../../stores/useProfileStore'

export default defineComponent({
  name: 'AppDialog',
  setup() {
    return { dialogState, closeDialog, profileStore: useProfileStore() }
  },
  computed: {
    // 현재 테마의 CSS 변수를 팝업에도 적용한다
    themeClass() {
      return this.profileStore.currentTheme === 'modern' ? 'theme-modern' : 'theme-retro'
    }
  },
  methods: {
    onOverlay() {
      // 오버레이 클릭은 취소로 처리 (alert는 확인과 동일)
      closeDialog(this.dialogState.type === 'alert')
    }
  }
})
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
}

.dialog-box {
  min-width: 280px;
  max-width: 90vw;
  padding: 1.25rem 1.4rem 1rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 10px;
  background: var(--surface, #fff);
  color: var(--text, #213547);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
}

.dialog-message {
  margin: 0 0 1.1rem;
  font-size: 0.92rem;
  line-height: 1.5;
  white-space: pre-line;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.dialog-button {
  padding: 0.4rem 1.1rem;
  font-size: 0.85rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 6px;
  background: var(--surface2, #f4f4f4);
  color: var(--text, #213547);
  cursor: pointer;
}

.dialog-button--primary {
  background: var(--primary, #3a86c0);
  border-color: var(--primary, #3a86c0);
  color: #fff;
}

.dialog-button:hover {
  filter: brightness(1.08);
}
</style>
