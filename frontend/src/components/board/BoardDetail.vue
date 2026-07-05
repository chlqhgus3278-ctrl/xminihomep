<template>
  <article class="board-detail">
    <h3 v-if="post.title">{{ post.title }}</h3>
    <div class="content" v-html="sanitizedContent" />
  </article>
</template>

<script>
import { defineComponent } from 'vue'
import DOMPurify from 'dompurify'

export default defineComponent({
  name: 'BoardDetail',
  props: {
    post: { type: Object, required: true }
  },
  computed: {
    sanitizedContent() {
      return DOMPurify.sanitize(this.post.content || '')
    }
  }
})
</script>

<style scoped>
.board-detail h3 {
  margin: 0 0 0.5rem;
}

.content :deep(p) {
  margin: 0.25rem 0;
}
</style>
