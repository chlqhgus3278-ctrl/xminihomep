<template>
  <div class="resume-view">
    <div v-if="dashboard" class="cert-grid">
      <div v-for="(entry, index) in dashboardEntries" :key="index" class="view-card cert-card">
        <div class="view-head">
          <strong class="view-title">{{ entry.name }}</strong>
          <span v-if="entry.grade" class="current-badge">{{ entry.grade }}</span>
        </div>
        <p class="view-sub">
          {{ [entry.issuer, formatYm(entry.acquiredYm)].filter(Boolean).join(' · ') }}
        </p>
      </div>
    </div>
    <template v-else>
      <template v-for="group in groups" :key="group.label">
        <template v-if="group.entries.length">
          <h4 class="group-title">{{ group.label }}</h4>
          <div v-for="(entry, index) in group.entries" :key="index" class="view-card">
            <div class="view-head">
              <strong class="view-title">{{ entry.name }}</strong>
              <span v-if="entry.grade" class="current-badge">{{ entry.grade }}</span>
            </div>
            <p class="view-sub">
              {{ [entry.issuer, formatYm(entry.acquiredYm)].filter(Boolean).join(' · ') }}
            </p>
          </div>
        </template>
      </template>
    </template>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { formatYm } from '../../../utils/resume'

export default defineComponent({
  name: 'CertLanguageView',
  props: {
    data: { type: Object, required: true },
    dashboard: { type: Boolean, default: false }
  },
  computed: {
    entries() {
      return this.data.entries || []
    },
    dashboardEntries() {
      return this.entries.filter((entry) => entry.kind !== 'LANGUAGE').slice(0, 4)
    },
    groups() {
      return [
        { label: '자격증', entries: this.entries.filter((entry) => entry.kind !== 'LANGUAGE') },
        { label: '어학', entries: this.entries.filter((entry) => entry.kind === 'LANGUAGE') }
      ]
    }
  },
  methods: { formatYm }
})
</script>

<style scoped>
.group-title {
  margin: 0.75rem 0 0.25rem;
  font-size: 0.9rem;
  color: var(--text-muted, #666);
}

.group-title:first-of-type {
  margin-top: 0;
}

.cert-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
}

.cert-card {
  min-width: 0;
}

.cert-card .view-title,
.cert-card .view-sub {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
