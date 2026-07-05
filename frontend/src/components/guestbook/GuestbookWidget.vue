<template>
  <div class="guestbook-widget">
    <h3>방명록</h3>

    <!-- 스크롤 없이 상위 몇 개만 미리보기. 본문은 한 줄로 잘라서 표시한다. -->
    <ul class="entries">
      <li v-for="entry in entries" :key="entry.id">
        <div class="entry-header">
          <strong>{{ entry.authorName }}</strong>
          <span v-if="entry.commentCount > 0" class="comment-count">(댓글 {{ entry.commentCount }})</span>
        </div>
        <p class="entry-message">{{ entry.message }}</p>
      </li>
      <li v-if="entries.length === 0" class="empty">아직 방명록이 없습니다.</li>
    </ul>

    <button type="button" class="goto-button" @click="goToGuestbook">방명록 바로가기 →</button>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useBoardStore } from '../../stores/useBoardStore'

const PREVIEW_SIZE = 3

export default defineComponent({
  name: 'GuestbookWidget',
  props: {
    username: { type: String, required: true }
  },
  setup() {
    return { boardStore: useBoardStore() }
  },
  data() {
    return {
      entries: []
    }
  },
  watch: {
    username: {
      immediate: true,
      handler() {
        this.fetchEntries()
      }
    },
    // 메인 방명록 화면에서 글/댓글을 쓰거나 삭제하면 미리보기를 갱신한다
    'boardStore.guestbookVersion'() {
      this.fetchEntries()
    }
  },
  methods: {
    async fetchEntries() {
      try {
        const res = await axios.get(`/api/public/${this.username}/guestbook`, {
          params: { page: 0, size: PREVIEW_SIZE }
        })
        this.entries = res.data.data.content
      } catch (e) {
        this.entries = []
      }
    },
    goToGuestbook() {
      this.boardStore.setActiveType('GUESTBOOK')
    }
  }
})
</script>

<style scoped>
.guestbook-widget h3 {
  margin: 0 0 0.5rem;
  font-size: 0.95rem;
}

.entries {
  list-style: none;
  padding: 0;
  margin: 0 0 0.75rem;
}

.entries li {
  padding: 0.4rem 0;
  border-bottom: 1px solid var(--border, #ccc);
  font-size: 0.8rem;
}

.entry-header {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  min-width: 0;
}

.entry-header strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comment-count {
  flex-shrink: 0;
  font-size: 0.72rem;
  color: var(--primary, #3a86c0);
}

.entry-message {
  margin: 0.15rem 0 0;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty {
  color: var(--text-muted);
  border-bottom: none;
}

.goto-button {
  width: 100%;
  font-size: 0.8rem;
  padding: 0.4rem;
  border: 1px solid var(--border, #ccc);
  background: var(--surface2, transparent);
  color: var(--text);
}

.goto-button:hover {
  border-color: var(--primary);
  color: var(--primary);
}
</style>
