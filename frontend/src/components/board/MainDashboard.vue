<template>
  <div class="main-dashboard">
    <section v-for="type in sectionTypes" :key="type" class="dash-cell">
      <h3 class="dash-title">{{ labelOf(type) }}</h3>
      <div class="dash-body">
        <BoardDetail v-if="postOf(type)" :post="postOf(type)" />
        <p v-else class="dash-empty">등록된 {{ labelOf(type) }}이(가) 없습니다.</p>
      </div>
      <button type="button" class="dash-more" @click="goSection(type)">전체 보기 →</button>
    </section>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { useBoardStore } from '../../stores/useBoardStore'
import { useProfileStore } from '../../stores/useProfileStore'
import { SECTION_LABELS, DEFAULT_MAIN_SECTIONS } from '../../utils/resume'
import BoardDetail from './BoardDetail.vue'

export default defineComponent({
  name: 'MainDashboard',
  components: { BoardDetail },
  props: {
    posts: { type: Array, default: () => [] }
  },
  setup() {
    return {
      boardStore: useBoardStore(),
      profileStore: useProfileStore()
    }
  },
  computed: {
    // 설정([레이아웃] 탭)에서 고른 구획 목록. 없으면 기본 4구획.
    // 과거 저장분에 남아있을 수 있는 폐지된 섹션(예: CAREER_DESC)은 걸러낸다.
    sectionTypes() {
      const configured = this.profileStore.profile?.layoutConfig?.sections
      const valid = Array.isArray(configured)
        ? configured.filter((type) => SECTION_LABELS[type])
        : []
      return valid.length > 0 ? valid : DEFAULT_MAIN_SECTIONS
    }
  },
  methods: {
    labelOf(type) {
      return SECTION_LABELS[type] || type
    },
    postOf(type) {
      return this.posts.find((post) => post.boardType === type) || null
    },
    goSection(type) {
      this.boardStore.setActiveType(type)
    }
  }
})
</script>

<style scoped>
.main-dashboard {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.dash-cell {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--surface);
  padding: 0.9rem;
  min-height: 180px;
}

.dash-title {
  margin: 0 0 0.6rem;
  font-size: 0.95rem;
  color: var(--primary);
  border-bottom: 1px dashed var(--border);
  padding-bottom: 0.4rem;
}

.dash-body {
  flex: 1;
  overflow: hidden;
  max-height: 260px;
  font-size: 0.85rem;
}

.dash-empty {
  color: var(--text-muted);
  font-size: 0.85rem;
  margin: 0.5rem 0;
}

.dash-more {
  align-self: flex-end;
  margin-top: 0.6rem;
  font-size: 0.75rem;
  background: transparent;
  border: none;
  color: var(--primary);
  cursor: pointer;
  padding: 0.2rem 0.3rem;
}

@media (max-width: 900px) {
  .main-dashboard {
    grid-template-columns: 1fr;
  }
}
</style>
