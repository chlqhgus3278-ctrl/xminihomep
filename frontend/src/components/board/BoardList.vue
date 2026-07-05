<template>
  <div class="board-list">
    <h2 class="section-title">{{ activeLabel }}</h2>

    <BoardEditor
      v-if="showEditor"
      :key="activeType"
      :board-type="activeType"
      :post="currentPost"
      @save="handleSave"
      @cancel="closeEditor"
    />

    <template v-else-if="currentPost">
      <div v-if="editable" class="owner-actions">
        <button type="button" @click="startEdit">수정하기</button>
        <button type="button" @click="handleToggleVisibility">
          {{ currentPost.isPublic ? '공개' : '비공개' }}
        </button>
        <button type="button" @click="handleDelete">삭제</button>
      </div>
      <BoardDetail :post="currentPost" />
    </template>

    <template v-else>
      <p class="empty">등록된 {{ activeLabel }}이 없습니다.</p>
      <button v-if="editable" type="button" class="register-button" @click="startCreate">등록하기</button>
    </template>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { useBoardStore } from '../../stores/useBoardStore'
import BoardEditor from './BoardEditor.vue'
import BoardDetail from './BoardDetail.vue'

const SECTION_LABELS = {
  CAREER_HISTORY: '경력사항',
  CAREER_DESC: '경력기술서',
  INTRO: '자기소개서',
  EDUCATION: '학력',
  CERT: '자격증',
  LANGUAGE: '어학',
  SKILLS: '기술스택'
}

export default defineComponent({
  name: 'BoardList',
  components: { BoardEditor, BoardDetail },
  props: {
    editable: { type: Boolean, default: false },
    // 지정하면 이 목록을 그대로 쓰고(공개 페이지), 지정 안 하면 내 글 목록을 직접 불러온다(마이페이지).
    posts: { type: Array, default: null }
  },
  setup() {
    return { boardStore: useBoardStore() }
  },
  data() {
    return {
      showEditor: false
    }
  },
  computed: {
    activeType() {
      return this.boardStore.activeType
    },
    activeLabel() {
      return SECTION_LABELS[this.activeType] || this.activeType
    },
    sourcePosts() {
      return this.posts !== null ? this.posts : this.boardStore.posts
    },
    // 섹션(게시판 타입)당 글은 1개만 유지한다
    currentPost() {
      return this.sourcePosts.find((post) => post.boardType === this.activeType) || null
    }
  },
  watch: {
    activeType() {
      this.showEditor = false
    }
  },
  async created() {
    if (this.posts === null) {
      await this.boardStore.fetchMyPosts()
    }
  },
  methods: {
    startCreate() {
      this.showEditor = true
    },
    startEdit() {
      this.showEditor = true
    },
    closeEditor() {
      this.showEditor = false
    },
    async handleSave(payload) {
      if (this.currentPost) {
        await this.boardStore.updatePost(this.currentPost.id, payload)
      } else {
        await this.boardStore.createPost(payload)
      }
      this.closeEditor()
    },
    async handleDelete() {
      if (!confirm('삭제하시겠습니까?')) return
      await this.boardStore.deletePost(this.currentPost.id)
    },
    async handleToggleVisibility() {
      await this.boardStore.toggleVisibility(this.currentPost.id)
    }
  }
})
</script>

<style scoped>
.section-title {
  margin: 0 0 1rem;
  font-size: 1.1rem;
  color: var(--text);
  border-bottom: 2px solid var(--primary);
  padding-bottom: 0.4rem;
}

.owner-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.4rem;
  margin-bottom: 0.75rem;
}

.owner-actions button {
  font-size: 0.78rem;
  padding: 0.3rem 0.6rem;
}

.empty {
  color: var(--text-muted);
  margin: 1.5rem 0 1rem;
}

.register-button {
  font-size: 0.85rem;
}
</style>
