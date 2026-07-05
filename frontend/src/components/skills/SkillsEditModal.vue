<template>
  <Teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-box">
        <div class="modal-head">
          <h3>기술스택 수정</h3>
          <button type="button" class="modal-close" @click="$emit('close')">×</button>
        </div>

        <SkillsForm v-model="draft" />

        <div class="modal-actions">
          <button type="button" :disabled="saving" @click="save">
            {{ saving ? '저장 중' : '저장' }}
          </button>
          <button type="button" :disabled="saving" @click="$emit('close')">취소</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script>
import { defineComponent } from 'vue'
import { useBoardStore } from '../../stores/useBoardStore'
import { SECTION_LABELS, parseStructured } from '../../utils/resume'
import SkillsForm from '../board/forms/SkillsForm.vue'

export default defineComponent({
  name: 'SkillsEditModal',
  components: { SkillsForm },
  props: {
    // 기존 SKILLS 게시글 (없으면 새로 생성)
    post: { type: Object, default: null }
  },
  emits: ['close'],
  setup() {
    return { boardStore: useBoardStore() }
  },
  data() {
    return {
      draft: parseStructured(this.post?.content) || { tags: [] },
      saving: false
    }
  },
  methods: {
    async save() {
      this.saving = true
      try {
        const payload = {
          boardType: 'SKILLS',
          title: SECTION_LABELS.SKILLS,
          content: JSON.stringify(this.draft),
          isPublic: true // 공개/비공개 구분 없이 항상 공개
        }
        if (this.post) {
          await this.boardStore.updatePost(this.post.id, payload)
        } else {
          await this.boardStore.createPost(payload)
        }
        this.$emit('close')
      } finally {
        this.saving = false
      }
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
}

.modal-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  font-size: 0.85rem;
}
</style>
