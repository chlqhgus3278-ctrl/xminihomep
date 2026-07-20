<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-box">
        <div class="modal-head">
          <h3>직업 선택</h3>
          <button type="button" class="modal-close" @click="$emit('close')">×</button>
        </div>

        <div class="job-groups">
          <div v-for="group in groups" :key="group.label" class="job-group">
            <p class="job-group-label">{{ group.label }}</p>
            <div class="job-options">
              <button
                v-for="job in group.jobs"
                :key="job"
                type="button"
                class="job-option"
                :class="{ active: job === current }"
                @click="$emit('select', job)"
              >
                {{ job }}
              </button>
            </div>
          </div>
        </div>

        <div class="job-custom">
          <p class="job-group-label">직접 입력</p>
          <div class="job-custom-row">
            <input
              v-model="customJob"
              maxlength="40"
              placeholder="예: Solutions Architect"
              @keydown.enter.prevent="applyCustom"
            />
            <button type="button" :disabled="!customJob.trim()" @click="applyCustom">적용</button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script>
import { defineComponent } from 'vue'
import { JOB_GROUPS } from '../../utils/jobs'

export default defineComponent({
  name: 'JobPickerModal',
  props: {
    // 현재 선택된 직업 (목록에서 강조 표시)
    current: { type: String, default: '' }
  },
  emits: ['select', 'close'],
  data() {
    return {
      groups: JOB_GROUPS,
      customJob: ''
    }
  },
  methods: {
    applyCustom() {
      const value = this.customJob.trim()
      if (value) this.$emit('select', value)
    }
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  padding: 1rem;
}

.modal-box {
  width: 100%;
  max-width: 440px;
  max-height: 85vh;
  overflow-y: auto;
  background: var(--surface, #fff);
  color: var(--text, inherit);
  border: 1px solid var(--border, #ccc);
  border-radius: 8px;
  padding: 1rem 1.25rem 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-head h3 {
  margin: 0;
  font-size: 1rem;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.2rem;
  line-height: 1;
  padding: 0.2rem;
  color: var(--text-muted, #888);
  cursor: pointer;
}

.job-groups {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.job-group-label {
  margin: 0 0 0.35rem;
  font-size: 0.72rem;
  font-weight: 700;
  color: var(--text-muted, #888);
}

.job-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.job-option {
  padding: 0.3rem 0.7rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 14px;
  background: var(--surface2, transparent);
  color: var(--text, inherit);
  font-size: 0.8rem;
  cursor: pointer;
}

.job-option:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.job-option.active {
  border-color: var(--primary);
  background: var(--primary);
  color: #fff;
}

.job-custom {
  border-top: 1px dashed var(--border);
  padding-top: 0.75rem;
}

.job-custom-row {
  display: flex;
  gap: 0.4rem;
}

.job-custom-row input {
  flex: 1;
  min-width: 0;
  padding: 0.4rem 0.5rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  font-size: 0.85rem;
  font-family: inherit;
  background: var(--surface, #fff);
  color: var(--text, inherit);
}

.job-custom-row button {
  padding: 0.4rem 0.9rem;
  border: 1px solid var(--primary);
  border-radius: 4px;
  background: var(--primary);
  color: #fff;
  font-size: 0.85rem;
  cursor: pointer;
}

.job-custom-row button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
