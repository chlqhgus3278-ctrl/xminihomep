<template>
  <div class="guestbook-board">
    <!-- 작성 폼 -->
    <template v-if="authStore.isLoggedIn">
      <button v-if="!showWriteForm" type="button" class="write-toggle" @click="showWriteForm = true">
        ✏️ 방명록 작성
      </button>
      <Transition name="slide-down">
        <form v-if="showWriteForm" class="write-form" @submit.prevent="handleSubmit">
          <textarea v-model="message" placeholder="방명록을 남겨보세요" rows="2" autofocus />
          <div class="write-form-actions">
            <button type="button" class="text-btn" @click="showWriteForm = false">취소</button>
            <button type="submit" :disabled="!message.trim()">등록</button>
          </div>
        </form>
      </Transition>
    </template>
    <p v-else class="login-hint">
      <router-link to="/login">로그인</router-link> 후 방명록을 남길 수 있습니다.
    </p>

    <!-- 관리자(홈피 주인) 일괄 삭제 -->
    <div v-if="editable && entries.length" class="bulk-bar">
      <label class="select-all">
        <input type="checkbox" :checked="allSelected" @change="toggleSelectAll" />
        전체선택
      </label>
      <button type="button" class="bulk-delete" :disabled="selectedIds.length === 0" @click="handleBulkDelete">
        선택 삭제{{ selectedIds.length ? ` (${selectedIds.length})` : '' }}
      </button>
    </div>

    <!-- 방명록 목록 -->
    <div v-if="loading" class="loading-row"><span class="spinner" /> 불러오는 중...</div>
    <ul v-else class="entries">
      <li v-for="entry in entries" :key="entry.id" class="entry">
        <div class="entry-header">
          <label class="entry-title">
            <input
              v-if="editable"
              type="checkbox"
              :value="entry.id"
              v-model="selectedIds"
            />
            <strong>{{ entry.authorName }}</strong>
            <span class="entry-date">{{ formatDate(entry.createdAt) }}</span>
          </label>
          <button v-if="canDelete(entry)" type="button" class="text-btn" @click="handleDelete(entry)">
            삭제
          </button>
        </div>
        <p class="entry-message">{{ entry.message }}</p>

        <!-- 댓글: 바로가기로 들어온 이 화면에서만 확인/작성 가능 -->
        <div class="comment-area">
          <button type="button" class="text-btn comment-toggle" @click="toggleComments(entry.id)">
            댓글 {{ entry.commentCount }}{{ expandedIds.includes(entry.id) ? ' 접기' : ' 보기' }}
          </button>

          <template v-if="expandedIds.includes(entry.id)">
            <ul v-if="entry.comments.length" class="comments">
              <li v-for="comment in entry.comments" :key="comment.id">
                <strong>{{ comment.authorName }}</strong>
                <span class="entry-date">{{ formatDate(comment.createdAt) }}</span>
                <p>{{ comment.message }}</p>
              </li>
            </ul>
            <p v-else class="no-comments">아직 댓글이 없습니다.</p>

            <form v-if="authStore.isLoggedIn" class="comment-form" @submit.prevent="handleComment(entry)">
              <input v-model="commentDrafts[entry.id]" placeholder="댓글을 입력하세요" />
              <button type="submit" :disabled="!(commentDrafts[entry.id] || '').trim()">등록</button>
            </form>
          </template>
        </div>
      </li>
      <li v-if="entries.length === 0" class="empty">아직 방명록이 없습니다.</li>
    </ul>

    <!-- 페이징: 한 페이지 최대 10개 -->
    <nav v-if="totalPages > 1" class="pagination">
      <button type="button" :disabled="page === 0" @click="fetchEntries(page - 1)">‹</button>
      <button
        v-for="p in totalPages"
        :key="p"
        type="button"
        :class="{ current: p - 1 === page }"
        @click="fetchEntries(p - 1)"
      >
        {{ p }}
      </button>
      <button type="button" :disabled="page >= totalPages - 1" @click="fetchEntries(page + 1)">›</button>
    </nav>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../../stores/useAuthStore'
import { useBoardStore } from '../../stores/useBoardStore'
import { showAlert, showConfirm } from '../../utils/dialog'
import { formatDateTime } from '../../utils/datetime'

const PAGE_SIZE = 10

export default defineComponent({
  name: 'GuestbookBoard',
  props: {
    username: { type: String, required: true },
    // 홈피 주인(관리자) 여부: 체크박스 일괄 삭제 노출
    editable: { type: Boolean, default: false }
  },
  setup() {
    return { authStore: useAuthStore(), boardStore: useBoardStore() }
  },
  data() {
    return {
      entries: [],
      page: 0,
      totalPages: 0,
      message: '',
      commentDrafts: {},
      expandedIds: [],
      selectedIds: [],
      loading: false,
      showWriteForm: false
    }
  },
  computed: {
    allSelected() {
      return this.entries.length > 0 && this.selectedIds.length === this.entries.length
    }
  },
  watch: {
    username: {
      immediate: true,
      handler() {
        this.fetchEntries(0)
      }
    },
    // 이 화면의 작성/삭제뿐 아니라 우측 배너에서 글을 남겨도 목록을 갱신한다
    'boardStore.guestbookVersion'() {
      this.fetchEntries(this.page)
    }
  },
  methods: {
    async fetchEntries(page) {
      this.loading = true
      try {
        const res = await axios.get(`/api/public/${this.username}/guestbook`, {
          params: { page, size: PAGE_SIZE }
        })
        this.entries = res.data.data.content
        this.page = res.data.data.page
        this.totalPages = res.data.data.totalPages
        this.selectedIds = []
      } catch (e) {
        this.entries = []
        this.totalPages = 0
      } finally {
        this.loading = false
      }
    },
    formatDate(dateTime) {
      return formatDateTime(dateTime)
    },
    canDelete(entry) {
      const myId = this.authStore.user?.id
      if (!myId) return false
      return myId === entry.authorId || myId === entry.ownerId
    },
    toggleComments(entryId) {
      if (this.expandedIds.includes(entryId)) {
        this.expandedIds = this.expandedIds.filter((id) => id !== entryId)
      } else {
        this.expandedIds.push(entryId)
      }
    },
    toggleSelectAll() {
      this.selectedIds = this.allSelected ? [] : this.entries.map((entry) => entry.id)
    },
    notifyChanged() {
      // 우측 미리보기 위젯이 다시 불러가도록 신호를 보낸다
      this.boardStore.bumpGuestbookVersion()
    },
    async handleSubmit() {
      const text = this.message.trim()
      if (!text) return
      await axios.post(`/api/public/${this.username}/guestbook`, { message: text })
      this.message = ''
      this.showWriteForm = false
      this.page = 0
      this.notifyChanged() // guestbookVersion 감시자가 목록을 다시 불러온다
    },
    async handleComment(entry) {
      const text = (this.commentDrafts[entry.id] || '').trim()
      if (!text) return
      await axios.post(`/api/public/${this.username}/guestbook/${entry.id}/comments`, { message: text })
      this.commentDrafts[entry.id] = ''
      this.notifyChanged()
    },
    async handleDelete(entry) {
      if (!(await showConfirm('삭제하시겠습니까?'))) return
      await axios.delete(`/api/guestbook/${entry.id}`)
      this.notifyChanged()
      await showAlert('삭제되었습니다.')
    },
    async handleBulkDelete() {
      if (this.selectedIds.length === 0) return
      if (!(await showConfirm(`선택한 방명록 ${this.selectedIds.length}개를 삭제하시겠습니까?`))) return
      await axios.post('/api/guestbook/bulk-delete', { ids: this.selectedIds })
      this.page = 0
      this.notifyChanged()
      await showAlert('삭제되었습니다.')
    }
  }
})
</script>

<style scoped>
.guestbook-board {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.write-toggle {
  align-self: flex-start;
  font-size: 0.85rem;
}

.write-form {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  overflow: hidden;
}

.write-form textarea {
  resize: vertical;
  font-family: inherit;
  font-size: 0.85rem;
  padding: 0.5rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
}

.write-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.4rem;
}

.write-form-actions button {
  font-size: 0.8rem;
  padding: 0.35rem 0.9rem;
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: max-height 0.2s ease, opacity 0.2s ease;
  max-height: 160px;
}

.slide-down-enter-from,
.slide-down-leave-to {
  max-height: 0;
  opacity: 0;
}

.login-hint {
  margin: 0;
  font-size: 0.85rem;
  color: var(--text-muted);
}

.bulk-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.4rem 0.6rem;
  border: 1px dashed var(--border, #ccc);
  border-radius: 4px;
  font-size: 0.8rem;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.bulk-delete {
  font-size: 0.78rem;
  padding: 0.25rem 0.6rem;
  color: #e05252;
}

.bulk-delete:disabled {
  opacity: 0.4;
  cursor: default;
}

.entries {
  list-style: none;
  padding: 0;
  margin: 0;
}

.entry {
  padding: 0.6rem 0.2rem;
  border-bottom: 1px solid var(--border, #ccc);
  font-size: 0.85rem;
}

.entry-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.entry-title {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.entry-date {
  font-size: 0.72rem;
  color: var(--text-muted);
}

.entry-message {
  margin: 0.25rem 0 0;
  word-break: break-word;
  white-space: pre-wrap;
}

.text-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0;
}

.comment-area {
  margin-top: 0.4rem;
}

.comment-toggle {
  color: var(--primary, #3a86c0);
}

.comments {
  list-style: none;
  padding: 0.4rem 0 0 0.9rem;
  margin: 0;
  border-left: 2px solid var(--border, #ccc);
}

.comments li {
  padding: 0.25rem 0;
  font-size: 0.8rem;
}

.comments li p {
  margin: 0.1rem 0 0;
  color: var(--text-muted);
  word-break: break-word;
}

.no-comments {
  margin: 0.3rem 0 0 0.9rem;
  font-size: 0.78rem;
  color: var(--text-muted);
}

.comment-form {
  display: flex;
  gap: 0.4rem;
  margin: 0.4rem 0 0 0.9rem;
}

.comment-form input {
  flex: 1;
  padding: 0.35rem 0.5rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  font-size: 0.8rem;
}

.comment-form button {
  font-size: 0.75rem;
  padding: 0.25rem 0.6rem;
}

.empty {
  padding: 1rem 0;
  color: var(--text-muted);
}

.loading-row {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 1rem 0;
  color: var(--text-muted);
  font-size: 0.85rem;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 0.25rem;
}

.pagination button {
  min-width: 2rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
}

.pagination button.current {
  border-color: var(--primary);
  color: var(--primary);
  font-weight: 700;
}

.pagination button:disabled {
  opacity: 0.4;
  cursor: default;
}
</style>
