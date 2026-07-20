<template>
  <div class="main-dashboard">
    <section v-for="type in sectionTypes" :key="type" class="dash-section">
      <h3 class="dash-title">{{ labelOf(type) }}</h3>
      <div class="dash-body">
        <BoardDetail v-if="postOf(type)" :post="postOf(type)" />
        <p v-else class="dash-empty">등록된 {{ labelOf(type) }}이(가) 없습니다.</p>
      </div>
    </section>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { useProfileStore } from '../../stores/useProfileStore'
import { SECTION_LABELS, normalizeMainSections } from '../../utils/resume'
import BoardDetail from './BoardDetail.vue'

export default defineComponent({
  name: 'MainDashboard',
  components: { BoardDetail },
  props: {
    posts: { type: Array, default: () => [] }
  },
  setup() {
    return {
      profileStore: useProfileStore()
    }
  },
  computed: {
    // 설정([레이아웃] 탭)에서 정한 순서. 모든 섹션을 순서대로 전부 보여준다.
    sectionTypes() {
      return normalizeMainSections(this.profileStore.profile?.layoutConfig?.sections)
    }
  },
  methods: {
    labelOf(type) {
      return SECTION_LABELS[type] || type
    },
    postOf(type) {
      return this.posts.find((post) => post.boardType === type) || null
    }
  }
})
</script>

<style scoped>
.main-dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dash-section {
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--surface);
  padding: 16px;
}

.dash-title {
  margin: 0 0 12px;
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--primary);
  border-bottom: 1px dashed var(--border);
  padding-bottom: 8px;
}

.dash-body {
  font-size: 0.9rem;
}

.dash-empty {
  color: var(--text-muted);
  font-size: 0.85rem;
  margin: 0.5rem 0;
}
</style>
