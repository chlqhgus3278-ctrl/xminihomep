<template>
  <div class="board-editor">
    <!-- 구조화 게시판(경력/학력/기술스택/자격증·어학)은 전용 폼 사용 -->
    <component v-if="isStructured" :is="structuredForm" v-model="structuredData" />

    <!-- 경력기술서/자기소개서는 기존 Quill 에디터 유지 -->
    <template v-else>
      <input v-model="title" placeholder="제목" class="title-input" />
      <QuillEditor
        v-model:content="content"
        content-type="html"
        :options="quillOptions"
        theme="snow"
      />
    </template>

    <div class="editor-actions">
      <button type="button" @click="save">저장</button>
      <button type="button" @click="cancel">취소</button>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { STRUCTURED_TYPES, SECTION_LABELS, parseStructured } from '../../utils/resume'
import CareerHistoryForm from './forms/CareerHistoryForm.vue'
import EducationForm from './forms/EducationForm.vue'
import SkillsForm from './forms/SkillsForm.vue'
import CertLanguageForm from './forms/CertLanguageForm.vue'

const FORM_COMPONENTS = {
  CAREER_HISTORY: CareerHistoryForm,
  EDUCATION: EducationForm,
  SKILLS: SkillsForm,
  CERT: CertLanguageForm
}

function defaultData(boardType) {
  return boardType === 'SKILLS' ? { tags: [] } : { entries: [] }
}

export default defineComponent({
  name: 'BoardEditor',
  components: { QuillEditor },
  props: {
    boardType: { type: String, required: true },
    post: { type: Object, default: null }
  },
  data() {
    return {
      title: this.post?.title || '',
      content: this.post?.content || '',
      structuredData:
        parseStructured(this.post?.content) || defaultData(this.boardType),
      quillOptions: {
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline'],
            [{ list: 'ordered' }, { list: 'bullet' }],
            [{ header: [1, 2, 3, false] }],
            ['link'],
            ['clean']
          ]
        }
      }
    }
  },
  computed: {
    isStructured() {
      return STRUCTURED_TYPES.includes(this.boardType)
    },
    structuredForm() {
      return FORM_COMPONENTS[this.boardType]
    }
  },
  methods: {
    save() {
      this.$emit('save', {
        boardType: this.boardType,
        title: this.isStructured ? SECTION_LABELS[this.boardType] : this.title,
        content: this.isStructured
          ? JSON.stringify(this.structuredData)
          : this.content, // Quill이 생성한 HTML 문자열 그대로 서버로 전송
        isPublic: true // 공개/비공개 구분 없이 항상 공개
      })
    },
    cancel() {
      this.$emit('cancel')
    }
  }
})
</script>

<style scoped>
.board-editor {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.title-input {
  padding: 0.5rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  font-size: 1rem;
}

.editor-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
</style>
