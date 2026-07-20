<template>
  <div class="resume-form">
    <SkillTagInput :tags="modelValue.tags" label="기술 검색" />
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import SkillTagInput from './SkillTagInput.vue'

export default defineComponent({
  name: 'SkillsForm',
  components: { SkillTagInput },
  props: {
    modelValue: { type: Object, required: true }
  },
  created() {
    if (!Array.isArray(this.modelValue.tags)) {
      this.modelValue.tags = []
    }
  }
})
</script>
