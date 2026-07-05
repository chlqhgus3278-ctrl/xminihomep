<template>
  <div class="resume-view">
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
      <p v-if="entry.duties" class="view-body">{{ entry.duties }}</p>
      <p v-if="entry.salary || entry.location || entry.leaveReason" class="view-optional">
        <span v-if="entry.salary">연봉 {{ entry.salary }}</span>
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
    }
  }
})
</script>

<style scoped>
.view-optional span + span::before {
  content: ' · ';
  color: var(--text-muted, #888);
}
</style>
