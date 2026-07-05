<template>
  <div class="editable-title">
    <template v-if="isEditing">
      <input
        ref="input"
        v-model="draft"
        class="title-input"
        placeholder="OO의 홈피"
        @keyup.enter="save"
        @keyup.esc="cancel"
      />
      <button type="button" class="title-button" :disabled="saving" @click="save">
        {{ saving ? '저장 중' : '저장' }}
      </button>
      <button type="button" class="title-button" :disabled="saving" @click="cancel">취소</button>
    </template>
    <template v-else>
      <h1>{{ displayTitle }}</h1>
      <button v-if="isOwner" type="button" class="title-button" title="제목 수정" @click="startEdit">
        ✏️ edit
      </button>
    </template>
  </div>
</template>

<script>
import { defineComponent, nextTick } from 'vue'
import { useProfileStore } from '../../stores/useProfileStore'
import { showAlert } from '../../utils/dialog'

export default defineComponent({
  name: 'EditableTitle',
  props: {
    profile: { type: Object, default: null },
    username: { type: String, required: true },
    isOwner: { type: Boolean, default: false },
    // 제목이 비어있을 때의 기본 표시 문자열 (테마별로 다르다)
    fallbackTitle: { type: String, required: true }
  },
  setup() {
    return { profileStore: useProfileStore() }
  },
  data() {
    return {
      isEditing: false,
      saving: false,
      draft: '',
    }
  },
  computed: {
    displayTitle() {
      return this.profile?.homepageTitle || this.fallbackTitle
    }
  },
  methods: {
    async startEdit() {
      this.draft = this.profile?.homepageTitle || ''
      this.isEditing = true
      await nextTick()
      this.$refs.input?.focus()
    },
    cancel() {
      this.isEditing = false
    },
    async save() {
      this.saving = true
      try {
        await this.profileStore.updateProfile({ homepageTitle: this.draft.trim() })
        this.isEditing = false
        await showAlert('수정되었습니다.')
      } finally {
        this.saving = false
      }
    }
  }
})
</script>

<style scoped>
.editable-title {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  min-width: 0;
}

.editable-title h1 {
  margin: 0;
  font-size: inherit;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.title-input {
  font-size: 1rem;
  padding: 0.3rem 0.5rem;
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.15);
  color: inherit;
  min-width: 0;
  width: 16rem;
  max-width: 50vw;
  font-family: inherit;
}

.title-input::placeholder {
  color: currentColor;
  opacity: 0.6;
}

.title-button {
  background: transparent;
  border: 1px solid currentColor;
  color: inherit;
  font-size: 0.72rem;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  opacity: 0.85;
  white-space: nowrap;
}

.title-button:hover {
  opacity: 1;
}

.title-button:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
