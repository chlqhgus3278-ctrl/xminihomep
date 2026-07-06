<template>
  <div class="visitor-counter">
    <p class="section-title">방문자</p>
    <div class="counts">
      <div class="count-item">
        <span class="count-label">오늘</span>
        <span class="count-value">{{ today }}</span>
      </div>
      <div class="count-item">
        <span class="count-label">전체</span>
        <span class="count-value">{{ total }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useProfileStore } from '../../stores/useProfileStore'

export default defineComponent({
  name: 'VisitorCounter',
  props: {
    username: { type: String, required: true }
  },
  setup() {
    return { profileStore: useProfileStore() }
  },
  data() {
    return {
      today: 0,
      total: 0
    }
  },
  watch: {
    username: {
      immediate: true,
      handler() {
        this.fetchCount()
      }
    },
    // 방문 기록(POST /visit)이 끝나면 최신 카운트로 다시 조회한다
    'profileStore.visitVersion'() {
      this.fetchCount()
    }
  },
  methods: {
    async fetchCount() {
      try {
        const res = await axios.get(`/api/public/${this.username}/visit-count`)
        this.today = res.data.data.today
        this.total = res.data.data.total
      } catch (e) {
        this.today = 0
        this.total = 0
      }
    }
  }
})
</script>

<style scoped>
.section-title {
  font-weight: 700;
  margin: 0 0 0.5rem;
  color: var(--text);
}

.counts {
  display: flex;
  justify-content: space-around;
}

.count-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.15rem;
}

.count-label {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.count-value {
  font-weight: 700;
  color: var(--counter-color, var(--primary));
}
</style>
