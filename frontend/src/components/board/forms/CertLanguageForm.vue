<template>
  <div class="resume-form">
    <div v-for="(entry, index) in modelValue.entries" :key="index" class="entry-card">
      <div class="entry-head">
        <strong>{{ kindLabel(entry.kind) }} {{ index + 1 }}</strong>
        <button
          v-if="modelValue.entries.length > 1"
          type="button"
          class="remove-button"
          @click="removeEntry(index)"
        >
          삭제
        </button>
      </div>

      <div class="field-row">
        <div class="field">
          <label>구분</label>
          <select v-model="entry.kind">
            <option v-for="kind in kinds" :key="kind.value" :value="kind.value">{{ kind.label }}</option>
          </select>
        </div>
        <div class="field grow">
          <label>{{ entry.kind === 'LANGUAGE' ? '시험명' : '자격증명' }} <span class="required">*</span></label>
          <input
            v-model="entry.name"
            :placeholder="entry.kind === 'LANGUAGE' ? '예: TOEIC, OPIc, JLPT' : '예: 정보처리기사'"
          />
        </div>
      </div>

      <div class="field-row">
        <div class="field grow">
          <label>발행처/기관 <span class="required">*</span></label>
          <input
            v-model="entry.issuer"
            :placeholder="entry.kind === 'LANGUAGE' ? '예: ETS, ACTFL' : '예: 한국산업인력공단'"
          />
        </div>
        <div class="field">
          <label>취득일자 <span class="required">*</span></label>
          <input type="month" v-model="entry.acquiredYm" />
        </div>
        <div class="field">
          <label>점수/급수</label>
          <input v-model="entry.grade" placeholder="예: 900점, 1급, IH" />
        </div>
      </div>
    </div>

    <button type="button" class="add-button" @click="addEntry">+ 자격증/어학 추가</button>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { CERT_KINDS } from '../../../utils/resume'

function emptyEntry() {
  return {
    kind: 'CERT',
    name: '',
    issuer: '',
    acquiredYm: '',
    grade: ''
  }
}

export default defineComponent({
  name: 'CertLanguageForm',
  props: {
    modelValue: { type: Object, required: true }
  },
  data() {
    return { kinds: CERT_KINDS }
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
    kindLabel(kind) {
      return kind === 'LANGUAGE' ? '어학' : '자격증'
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
