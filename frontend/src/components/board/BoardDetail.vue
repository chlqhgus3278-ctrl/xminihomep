<template>
  <article class="board-detail">
    <!-- 구조화 게시판은 전용 뷰로 렌더링 -->
    <component v-if="structuredView && structuredData" :is="structuredView" :data="structuredData" :dashboard="dashboard" />

    <!-- 일반 게시판(경력기술서/자기소개서)은 Quill HTML 렌더링 -->
    <template v-else>
      <h3 v-if="post.title">{{ post.title }}</h3>
      <div class="content" v-html="sanitizedContent" />
    </template>
  </article>
</template>

<script>
import { defineComponent } from 'vue'
import DOMPurify from 'dompurify'
import { STRUCTURED_TYPES, parseStructured } from '../../utils/resume'
import CareerHistoryView from './views/CareerHistoryView.vue'
import IntroView from './views/IntroView.vue'
import EducationView from './views/EducationView.vue'
import SkillsView from './views/SkillsView.vue'
import CertLanguageView from './views/CertLanguageView.vue'

const VIEW_COMPONENTS = {
  CAREER_HISTORY: CareerHistoryView,
  INTRO: IntroView,
  EDUCATION: EducationView,
  SKILLS: SkillsView,
  CERT: CertLanguageView
}

export default defineComponent({
  name: 'BoardDetail',
  props: {
    post: { type: Object, required: true },
    dashboard: { type: Boolean, default: false }
  },
  computed: {
    structuredView() {
      return STRUCTURED_TYPES.includes(this.post.boardType)
        ? VIEW_COMPONENTS[this.post.boardType]
        : null
    },
    structuredData() {
      return parseStructured(this.post.content)
    },
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
