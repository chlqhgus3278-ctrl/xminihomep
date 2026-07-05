<template>
  <div class="resume-view">
    <div v-if="tags.length" class="tag-list">
      <span v-for="tag in tags" :key="tag" class="skill-tag">{{ tag }}</span>
    </div>
    <p v-else class="hint">등록된 기술이 없습니다.</p>
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SkillsView',
  props: {
    data: { type: Object, required: true }
  },
  computed: {
    tags() {
      return this.data.tags || []
    }
  }
})
</script>
