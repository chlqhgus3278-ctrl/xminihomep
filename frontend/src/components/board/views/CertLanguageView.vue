<template>
  <div class="resume-view">
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
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { formatYm } from '../../../utils/resume'

export default defineComponent({
  name: 'CertLanguageView',
  props: {
    data: { type: Object, required: true }
  },
  computed: {
    groups() {
      const entries = this.data.entries || []
      return [
        { label: '자격증', entries: entries.filter((entry) => entry.kind !== 'LANGUAGE') },
        { label: '어학', entries: entries.filter((entry) => entry.kind === 'LANGUAGE') }
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
</style>
