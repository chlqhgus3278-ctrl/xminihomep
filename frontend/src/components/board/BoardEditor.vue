<template>
  <div class="board-editor">
    <input v-model="title" placeholder="제목" class="title-input" />
    <QuillEditor
      v-model:content="content"
      content-type="html"
      :options="quillOptions"
      theme="snow"
    />
    <div class="editor-actions">
      <label>
        <input type="checkbox" v-model="isPublic" /> 공개
      </label>
      <button type="button" @click="save">저장</button>
      <button type="button" @click="cancel">취소</button>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

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
      isPublic: this.post ? this.post.isPublic : true,
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
  methods: {
    save() {
      this.$emit('save', {
        boardType: this.boardType,
        title: this.title,
        content: this.content, // Quill이 생성한 HTML 문자열 그대로 서버로 전송
        isPublic: this.isPublic
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
