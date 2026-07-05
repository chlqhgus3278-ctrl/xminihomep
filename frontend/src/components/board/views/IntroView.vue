<template>
  <div class="resume-view">
    <div v-for="(entry, index) in entries" :key="index" class="view-card">
      <div class="view-head">
        <strong class="view-title">{{ entry.title }}</strong>
      </div>
      <p v-if="entry.content" class="view-body">{{ entry.content }}</p>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'IntroView',
  props: {
    data: { type: Object, required: true }
  },
  computed: {
    entries() {
      return this.data.entries || []
    }
  }
})
</script>
