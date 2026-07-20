<template>
  <div class="resume-view">
    <div v-if="data.summary" class="career-summary">
      <p class="career-summary-label">커리어 요약</p>
      <p class="career-summary-text">{{ data.summary }}</p>
    </div>

    <p v-if="totalPeriod" class="total-summary">
      총 {{ entries.length }}개사 · 총 경력 {{ totalPeriod }}
    </p>

    <div v-for="(entry, index) in entries" :key="index" class="view-card">
      <div class="view-head">
        <strong class="view-title">{{ displayCompany(entry) }}</strong>
        <span v-if="entry.current" class="current-badge">재직중</span>
      </div>
      <p class="view-sub">
        {{ formatYm(entry.startYm) }} ~ {{ entry.current ? '' : formatYm(entry.endYm) }}
        <span v-if="periodOf(entry)" class="period-badge">{{ periodOf(entry) }}</span>
      </p>
      <p v-if="entry.jobTitle || entry.department || entry.position" class="view-sub">
        {{ [entry.jobTitle, entry.department, entry.position].filter(Boolean).join(' · ') }}
      </p>
      <div v-if="skillsOf(entry).length" class="skill-tags">
        <span v-for="skill in skillsOf(entry)" :key="skill" class="skill-tag">{{ skill }}</span>
      </div>
      <p v-if="entry.duties" class="view-body">{{ entry.duties }}</p>
      <p v-if="entry.salary || entry.location || entry.leaveReason" class="view-optional">
        <span v-if="entry.salary">연봉 {{ displaySalary(entry.salary) }}</span>
        <span v-if="entry.location">근무지역 {{ entry.location }}</span>
        <span v-if="entry.leaveReason">퇴사사유 {{ entry.leaveReason }}</span>
      </p>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { calcPeriod, formatMonths, formatYm, monthsBetween } from '../../../utils/resume'

export default defineComponent({
  name: 'CareerHistoryView',
  props: {
    data: { type: Object, required: true }
  },
  computed: {
    entries() {
      return this.data.entries || []
    },
    totalPeriod() {
      const total = this.entries.reduce(
        (sum, entry) => sum + monthsBetween(entry.startYm, entry.current ? null : entry.endYm),
        0
      )
      return formatMonths(total)
    }
  },
  methods: {
    formatYm,
    displayCompany(entry) {
      return entry.hideCompany ? '(회사명 비공개)' : entry.company
    },
    periodOf(entry) {
      return calcPeriod(entry.startYm, entry.current ? null : entry.endYm)
    },
    skillsOf(entry) {
      // 신규 저장분은 배열, 기존 저장분은 쉼표로 구분된 문자열일 수 있다.
      const skills = Array.isArray(entry.skills)
        ? entry.skills
        : (entry.skills || '').split(',')
      return skills.map((s) => (s || '').trim()).filter(Boolean)
    },
    // 예전 저장분은 '4,000만원'처럼 단위까지 입력되어 있을 수 있어 중복을 피한다
    displaySalary(salary) {
      return salary.endsWith('만원') ? salary : `${salary}만원`
    }
  }
})
</script>

<style scoped>
.career-summary {
  padding: 0.9rem 1rem;
  border-left: 4px solid var(--primary, #3a86c0);
  border-radius: 0 6px 6px 0;
  background: var(--surface2, rgba(0, 0, 0, 0.03));
}

.career-summary-label {
  margin: 0 0 0.35rem;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: var(--primary, #3a86c0);
}

.career-summary-text {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  line-height: 1.5;
  white-space: pre-line;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
  margin-top: 0.5rem;
}

.skill-tag {
  font-size: 0.72rem;
  padding: 0.15rem 0.5rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 10px;
  color: var(--text-muted, #666);
}

.view-optional span + span::before {
  content: ' · ';
  color: var(--text-muted, #888);
}
</style>
