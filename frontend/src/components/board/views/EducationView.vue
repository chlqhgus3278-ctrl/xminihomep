<template>
  <div class="resume-view">
    <div v-for="(entry, index) in entries" :key="index" class="view-card">
      <div class="view-head">
        <strong class="view-title">{{ entry.school }}</strong>
        <span class="level-badge">{{ entry.level }}</span>
        <span v-if="entry.status && entry.status !== '졸업'" class="current-badge">{{ entry.status }}</span>
      </div>
      <p class="view-sub">
        {{ formatYm(entry.startYm) }} ~ {{ formatYm(entry.endYm) }}
        <template v-if="entry.gpa"> · 학점 {{ entry.gpa }}/{{ entry.gpaMax }}</template>
      </p>
      <p v-if="majorText(entry)" class="view-sub">{{ majorText(entry) }}</p>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { formatYm } from '../../../utils/resume'

export default defineComponent({
  name: 'EducationView',
  props: {
    data: { type: Object, required: true }
  },
  computed: {
    entries() {
      return this.data.entries || []
    }
  },
  methods: {
    formatYm,
    majorText(entry) {
      return (entry.majors || [])
        .filter((major) => major.name)
        .map((major) => (major.type === '주전공' ? major.name : `${major.name} (${major.type})`))
        .join(' · ')
    }
  }
})
</script>

<style scoped>
.level-badge {
  font-size: 0.75rem;
  color: var(--text-muted, #888);
  border: 1px solid var(--border, #ccc);
  border-radius: 10px;
  padding: 0.05rem 0.5rem;
}
</style>
