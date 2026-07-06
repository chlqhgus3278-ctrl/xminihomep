<template>
  <div class="guestbook-widget">
    <div class="widget-head">
      <h3>방명록</h3>
      <button type="button" class="goto-button" @click="goToGuestbook">바로가기 →</button>
    </div>

    <!-- 배너에서 바로 방명록 남기기 -->
    <form v-if="authStore.isLoggedIn" class="write-form" @submit.prevent="handleSubmit">
      <textarea v-model="message" placeholder="방명록을 남겨보세요" rows="2" />
      <button type="submit" :disabled="!message.trim() || submitting">등록</button>
    </form>

    <!-- 스크롤 없이 상위 몇 개만 미리보기. 본문은 한 줄로 잘라서 표시한다. -->
    <ul class="entries">
      <li v-for="entry in entries" :key="entry.id">
        <div class="entry-header">
          <strong>{{ entry.authorName }}</strong>
          <span v-if="entry.commentCount > 0" class="comment-count">(댓글 {{ entry.commentCount }})</span>
        </div>
        <p class="entry-date">{{ formatDate(entry.createdAt) }}</p>
        <p class="entry-message">{{ entry.message }}</p>
      </li>
      <li v-if="entries.length === 0" class="empty">아직 방명록이 없습니다.</li>
    </ul>

  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../../stores/useAuthStore'
import { useBoardStore } from '../../stores/useBoardStore'
import { formatDateTime } from '../../utils/datetime'

const PREVIEW_SIZE = 3

export default defineComponent({
  name: 'GuestbookWidget',
  props: {
    username: { type: String, required: true }
  },
  setup() {
    return { authStore: useAuthStore(), boardStore: useBoardStore() }
  },
  data() {
    return {
      entries: [],
      message: '',
      submitting: false
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
    formatDate(dateTime) {
      return formatDateTime(dateTime)
    },
    async handleSubmit() {
      const text = this.message.trim()
      if (!text || this.submitting) return
      this.submitting = true
      try {
        await axios.post(`/api/public/${this.username}/guestbook`, { message: text })
        this.message = ''
        // 미리보기(이 컴포넌트)와 열려있는 방명록 게시판이 함께 갱신된다
        this.boardStore.bumpGuestbookVersion()
      } finally {
        this.submitting = false
      }
    },
    goToGuestbook() {
      this.boardStore.setActiveType('GUESTBOOK')
    }
  }
})
</script>

<style scoped>
.widget-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.guestbook-widget h3 {
  margin: 0;
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

.write-form {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 0.6rem;
}

.write-form textarea {
  resize: vertical;
  font-family: inherit;
  font-size: 0.78rem;
  padding: 0.4rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  background: var(--surface2, transparent);
  color: var(--text);
}

.write-form button {
  align-self: flex-end;
  font-size: 0.75rem;
  padding: 0.25rem 0.7rem;
}

.write-form button:disabled {
  opacity: 0.5;
  cursor: default;
}

.entry-date {
  margin: 0.1rem 0 0;
  font-size: 0.68rem;
  color: var(--text-muted);
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
  flex-shrink: 0;
  font-size: 0.7rem;
  line-height: 1;
  padding: 0.25rem 0.45rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  background: var(--surface2, transparent);
  color: var(--text);
  cursor: pointer;
}

.goto-button:hover {
  border-color: var(--primary);
  color: var(--primary);
}
</style>
