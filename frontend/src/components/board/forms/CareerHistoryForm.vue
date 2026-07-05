<template>
  <div class="resume-form">
    <div class="field career-summary-field">
      <label>
        커리어 요약
        <span class="byte-counter">{{ bytesOf(modelValue.summary) }} bytes</span>
      </label>
      <textarea
        v-model="modelValue.summary"
        rows="3"
        placeholder="예: 10년차 백엔드 개발자 · 트래픽 1억 규모 커머스 설계 · 팀 리딩 경험"
      />
    </div>

    <p v-if="totalPeriod" class="total-summary">
      총 {{ modelValue.entries.length }}개사 · 총 경력 {{ totalPeriod }}
    </p>

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

      <div class="field">
        <label>사용 기술</label>
        <input v-model="entry.skills" placeholder="예: Java, Spring Boot, MySQL (쉼표로 구분)" />
      </div>

      <details
        class="optional-fields"
        :open="optionalOpen[index]"
        @toggle="optionalOpen[index] = $event.target.open"
      >
        <summary>선택 입력 (연봉 · 근무지역 · 퇴사사유)</summary>
        <div class="field-row">
          <div class="field grow">
            <label>연봉</label>
            <div class="salary-input">
              <input v-model="entry.salary" placeholder="예: 4,000" />
              <span class="salary-unit">만원</span>
            </div>
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
import { showAlert } from '../../../utils/dialog'

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
    skills: '',
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
  data() {
    return {
      // 경력별 선택 입력(details) 열림 상태. 저장 데이터에 섞이지 않도록 별도로 관리한다.
      optionalOpen: []
    }
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
    if (typeof this.modelValue.summary !== 'string') {
      this.modelValue.summary = ''
    }
    if (!Array.isArray(this.modelValue.entries)) {
      this.modelValue.entries = []
    }
    if (this.modelValue.entries.length === 0) {
      this.modelValue.entries.push(emptyEntry())
    }
    // 기존 저장분에는 skills 필드가 없을 수 있다
    this.modelValue.entries.forEach((entry) => {
      if (typeof entry.skills !== 'string') entry.skills = ''
    })
    // 선택 입력에 이미 값이 있으면 토글을 열린 상태로 시작한다
    this.optionalOpen = this.modelValue.entries.map(
      (entry) => !!(entry.salary || entry.location || entry.leaveReason)
    )
  },
  methods: {
    bytesOf(text) {
      return calcBytes(text)
    },
    periodOf(entry) {
      return calcPeriod(entry.startYm, entry.current ? null : entry.endYm)
    },
    async onCurrentChange(entry) {
      if (entry.current) {
        const other = this.modelValue.entries.find((e) => e !== entry && e.current)
        if (other) {
          entry.current = false
          await showAlert('재직 중인 회사는 한 곳만 선택할 수 있습니다.')
          return
        }
        entry.endYm = ''
      }
    },
    addEntry() {
      this.modelValue.entries.push(emptyEntry())
      this.optionalOpen.push(false)
    },
    removeEntry(index) {
      this.modelValue.entries.splice(index, 1)
      this.optionalOpen.splice(index, 1)
    }
  }
})
</script>

<style scoped>
.career-summary-field {
  padding: 0.75rem 0.85rem;
  border: 1px solid var(--primary, #3a86c0);
  border-radius: 6px;
  background: var(--surface2, transparent);
}

.entry-head {
  display: flex;
  justify-content: flex-end;
  min-height: 0;
}

.entry-head:empty {
  display: none;
}

.salary-input {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.salary-input input {
  flex: 1;
  min-width: 0;
}

.salary-unit {
  font-size: 0.82rem;
  color: var(--text-muted);
  white-space: nowrap;
}
</style>
