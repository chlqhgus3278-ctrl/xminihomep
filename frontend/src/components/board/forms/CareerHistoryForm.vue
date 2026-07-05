<template>
  <div class="resume-form">
    <p v-if="totalPeriod" class="total-summary">
      총 {{ modelValue.entries.length }}개사 · 총 경력 {{ totalPeriod }}
    </p>

    <div v-for="(entry, index) in modelValue.entries" :key="index" class="entry-card">
      <div class="entry-head">
        <strong>경력 {{ index + 1 }}</strong>
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
        <div class="field grow">
          <label>회사명 <span class="required">*</span></label>
          <input v-model="entry.company" placeholder="회사명" />
        </div>
        <label class="inline-check">
          <input type="checkbox" v-model="entry.hideCompany" /> 회사명 숨기기
        </label>
      </div>

      <div class="field-row">
        <div class="field">
          <label>입사년월 <span class="required">*</span></label>
          <input type="month" v-model="entry.startYm" />
        </div>
        <div class="field">
          <label>퇴사년월</label>
          <input type="month" v-model="entry.endYm" :disabled="entry.current" />
        </div>
        <label class="inline-check">
          <input type="checkbox" v-model="entry.current" @change="onCurrentChange(entry)" /> 재직중
        </label>
        <span v-if="periodOf(entry)" class="period-badge">{{ periodOf(entry) }}</span>
      </div>

      <div class="field-row">
        <div class="field grow">
          <label>직무</label>
          <input v-model="entry.jobTitle" placeholder="예: 백엔드 개발" />
        </div>
        <div class="field grow">
          <label>근무부서</label>
          <input v-model="entry.department" placeholder="예: 서비스개발팀" />
        </div>
        <div class="field grow">
          <label>직급/직책</label>
          <input v-model="entry.position" placeholder="예: 대리 / 파트리더" />
        </div>
      </div>

      <div class="field">
        <label>
          담당업무
          <span class="byte-counter">{{ bytesOf(entry.duties) }} bytes</span>
        </label>
        <textarea
          v-model="entry.duties"
          rows="4"
          placeholder="담당했던 업무와 성과를 입력하세요"
        />
      </div>

      <details class="optional-fields">
        <summary>선택 입력 (연봉 · 근무지역 · 퇴사사유)</summary>
        <div class="field-row">
          <div class="field grow">
            <label>연봉</label>
            <input v-model="entry.salary" placeholder="예: 4,000만원" />
          </div>
          <div class="field grow">
            <label>근무지역</label>
            <input v-model="entry.location" placeholder="예: 서울 강남구" />
          </div>
        </div>
        <div class="field">
          <label>퇴사사유</label>
          <input v-model="entry.leaveReason" placeholder="예: 이직, 계약만료" />
        </div>
      </details>
    </div>

    <button type="button" class="add-button" @click="addEntry">+ 경력 추가</button>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { calcBytes, calcPeriod, formatMonths, monthsBetween } from '../../../utils/resume'

function emptyEntry() {
  return {
    company: '',
    hideCompany: false,
    startYm: '',
    endYm: '',
    current: false,
    jobTitle: '',
    department: '',
    position: '',
    duties: '',
    salary: '',
    location: '',
    leaveReason: ''
  }
}

export default defineComponent({
  name: 'CareerHistoryForm',
  props: {
    modelValue: { type: Object, required: true }
  },
  computed: {
    totalPeriod() {
      const total = this.modelValue.entries.reduce(
        (sum, entry) => sum + monthsBetween(entry.startYm, entry.current ? null : entry.endYm),
        0
      )
      return formatMonths(total)
    }
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
    periodOf(entry) {
      return calcPeriod(entry.startYm, entry.current ? null : entry.endYm)
    },
    onCurrentChange(entry) {
      if (entry.current) entry.endYm = ''
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
