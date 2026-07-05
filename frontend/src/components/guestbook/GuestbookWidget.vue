<template>
  <div class="guestbook-widget">
    <h3>방명록</h3>

    <ul class="entries">
      <li v-for="entry in entries" :key="entry.id">
        <div class="entry-header">
          <strong>{{ entry.authorName }}</strong>
          <button v-if="canDelete(entry)" type="button" class="delete-btn" @click="handleDelete(entry)">
            삭제
          </button>
        </div>
        <p class="entry-message">{{ entry.message }}</p>
      </li>
      <li v-if="entries.length === 0" class="empty">아직 방명록이 없습니다.</li>
    </ul>

    <form v-if="authStore.isLoggedIn" class="write-form" @submit.prevent="handleSubmit">
      <textarea v-model="message" placeholder="방명록을 남겨보세요" rows="2" />
      <button type="submit" :disabled="!message.trim()">등록</button>
    </form>
    <p v-else class="login-hint">
      <router-link to="/login">로그인</router-link> 후 방명록을 남길 수 있습니다.
    </p>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../../stores/useAuthStore'

export default defineComponent({
  name: 'GuestbookWidget',
  props: {
    username: { type: String, required: true }
  },
  setup() {
    return { authStore: useAuthStore() }
  },
  data() {
    return {
      entries: [],
      message: ''
    }
  },
  watch: {
    username: {
      immediate: true,
      handler() {
        this.fetchEntries()
      }
    }
  },
  methods: {
    async fetchEntries() {
      try {
        const res = await axios.get(`/api/public/${this.username}/guestbook`)
        this.entries = res.data.data
      } catch (e) {
        this.entries = []
      }
    },
    canDelete(entry) {
      const myId = this.authStore.user?.id
      if (!myId) return false
      return myId === entry.authorId || myId === entry.ownerId
    },
    async handleSubmit() {
      const text = this.message.trim()
      if (!text) return
      await axios.post(`/api/public/${this.username}/guestbook`, { message: text })
      this.message = ''
      await this.fetchEntries()
    },
    async handleDelete(entry) {
      if (!confirm('삭제하시겠습니까?')) return
      await axios.delete(`/api/guestbook/${entry.id}`)
      await this.fetchEntries()
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
  max-height: 180px;
  overflow-y: auto;
}

.entries li {
  padding: 0.4rem 0;
  border-bottom: 1px solid var(--border, #ccc);
  font-size: 0.8rem;
}

.entry-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.entry-message {
  margin: 0.15rem 0 0;
  color: var(--text-muted);
  word-break: break-word;
}

.delete-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0;
}

.write-form {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.write-form textarea {
  resize: vertical;
  font-family: inherit;
  font-size: 0.8rem;
  padding: 0.4rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
}

.login-hint {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.empty {
  color: var(--text-muted);
  border-bottom: none;
}
</style>
