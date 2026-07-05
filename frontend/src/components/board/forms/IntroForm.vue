<template>
  <div class="resume-form">
    <div v-for="(entry, index) in modelValue.entries" :key="index" class="entry-card">
      <div class="entry-head">
        <button
          v-if="modelValue.entries.length > 1"
          type="button"
          class="remove-button"
          @click="removeEntry(index)"
        >
          삭제
        </button>
      </div>

      <div class="field">
        <label>제목 <span class="required">*</span></label>
        <input v-model="entry.title" placeholder="예: 성장 과정, 지원 동기, 입사 후 포부" />
      </div>

      <div class="field">
        <label>
          내용
          <span class="byte-counter">{{ bytesOf(entry.content) }} bytes</span>
        </label>
        <textarea
          v-model="entry.content"
          rows="8"
          placeholder="내용을 입력하세요"
        />
      </div>
    </div>

    <button type="button" class="add-button" @click="addEntry">+ 자기소개서 추가</button>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { calcBytes } from '../../../utils/resume'

function emptyEntry() {
  return {
    title: '',
    content: ''
  }
}

export default defineComponent({
  name: 'IntroForm',
  props: {
    modelValue: { type: Object, required: true }
  },
  created() {
    if (!Array.isArray(this.modelValue.entries)) {
      this.modelValue.entries = []
    }
    if (this.modelValue.entries.length === 0) {
      this.modelValue.entries.push(emptyEntry())
    }
  },
  methods: {
    bytesOf(text) {
      return calcBytes(text)
    },
    addEntry() {
      this.modelValue.entries.push(emptyEntry())
    },
    removeEntry(index) {
      this.modelValue.entries.splice(index, 1)
    }
  }
})
</script>
