<template>
  <div class="board-list">
    <h2 class="section-title">{{ activeLabel }}</h2>

    <Transition name="fade" mode="out-in">
      <div :key="activeType">
        <SettingsPanel v-if="isSettings && editable" />

        <MainDashboard v-else-if="isMain" :posts="sourcePosts" />

        <GuestbookBoard v-else-if="isGuestbook" :username="username" :editable="editable" />

        <BoardEditor
          v-else-if="showEditor"
          :board-type="activeType"
          :post="currentPost"
          @save="handleSave"
          @cancel="closeEditor"
        />

        <template v-else-if="currentPost">
          <div v-if="editable" class="owner-actions">
            <button type="button" @click="startEdit">수정하기</button>
            <button type="button" @click="handleDelete">삭제</button>
          </div>
          <BoardDetail :post="currentPost" />
        </template>

        <template v-else>
          <p class="empty">등록된 {{ activeLabel }}이 없습니다.</p>
          <button v-if="editable" type="button" class="register-button" @click="startCreate">등록하기</button>
        </template>
      </div>
    </Transition>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { useBoardStore } from '../../stores/useBoardStore'
import BoardEditor from './BoardEditor.vue'
import BoardDetail from './BoardDetail.vue'
import SettingsPanel from '../settings/SettingsPanel.vue'
import GuestbookBoard from '../guestbook/GuestbookBoard.vue'
import MainDashboard from './MainDashboard.vue'
import { SECTION_LABELS } from '../../utils/resume'
import { showAlert, showConfirm } from '../../utils/dialog'
import { showToast } from '../../utils/toast'

export default defineComponent({
  name: 'BoardList',
  components: { BoardEditor, BoardDetail, SettingsPanel, GuestbookBoard, MainDashboard },
  props: {
    editable: { type: Boolean, default: false },
    // 지정하면 이 목록을 그대로 쓰고(공개 페이지), 지정 안 하면 내 글 목록을 직접 불러온다(마이페이지).
    posts: { type: Array, default: null },
    // 방명록(GuestbookBoard)이 어느 홈피의 방명록인지 알기 위해 필요하다
    username: { type: String, default: '' }
  },
  setup() {
    return { boardStore: useBoardStore() }
  },
  data() {
    return {
      showEditor: false,
      syncingRoute: false
    }
  },
  computed: {
    activeType() {
      return this.boardStore.activeType
    },
    isSettings() {
      return this.activeType === 'SETTINGS'
    },
    isMain() {
      return this.activeType === 'MAIN'
    },
    isGuestbook() {
      return this.activeType === 'GUESTBOOK'
    },
    activeLabel() {
      if (this.isSettings) return '설정'
      if (this.isMain) return 'Main'
      if (this.isGuestbook) return '방명록'
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
    activeType(type) {
      this.showEditor = false
      this.syncRouteFromActive(type)
    },
    '$route.query.section': {
      immediate: true,
      handler(section) {
        this.syncActiveFromRoute(section)
      }
    },
    // 에디터 열림 상태를 스토어에 알려 섹션 이동 시 이탈 확인에 쓴다
    showEditor(value) {
      this.boardStore.setEditing(value)
    }
  },
  beforeUnmount() {
    this.boardStore.setEditing(false)
  },
  async created() {
    if (this.posts === null) {
      await this.boardStore.fetchMyPosts()
    }
  },
  methods: {
    isValidMenuType(type) {
      return ['MAIN', 'SETTINGS', 'GUESTBOOK', ...Object.keys(SECTION_LABELS)].includes(type)
    },
    normalizeSection(section) {
      const type = Array.isArray(section) ? section[0] : section
      return this.isValidMenuType(type) ? type : 'MAIN'
    },
    async syncActiveFromRoute(section) {
      const nextType = this.normalizeSection(section)
      if (nextType === this.activeType) return

      this.syncingRoute = true
      await this.boardStore.setActiveType(nextType)
      this.syncingRoute = false

      if (this.activeType !== nextType) {
        this.syncRouteFromActive(this.activeType, true)
      }
    },
    syncRouteFromActive(type, replace = false) {
      if (this.syncingRoute) return

      const query = { ...this.$route.query }
      if (type === 'MAIN') {
        delete query.section
      } else {
        query.section = type
      }

      const currentSection = this.$route.query.section
      const nextSection = query.section
      if (currentSection === nextSection || (!currentSection && !nextSection)) return

      const navigate = replace ? this.$router.replace : this.$router.push
      navigate.call(this.$router, { path: this.$route.path, query, hash: this.$route.hash }).catch(() => {})
    },
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
      const isUpdate = !!this.currentPost
      try {
        if (isUpdate) {
          await this.boardStore.updatePost(this.currentPost.id, payload)
        } else {
          await this.boardStore.createPost(payload)
        }
        this.closeEditor()
        showToast(isUpdate ? '수정되었습니다.' : '저장되었습니다.')
      } catch (e) {
        showToast('저장에 실패했습니다.', 'error')
        throw e
      }
    },
    async handleDelete() {
      if (!(await showConfirm('삭제하시겠습니까?'))) return
      await this.boardStore.deletePost(this.currentPost.id)
      await showAlert('삭제되었습니다.')
    }
  }
})
</script>

<style scoped>
.section-title {
  margin: 0 0 16px;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text);
  border-bottom: 2px solid var(--primary);
  padding-bottom: 8px;
}

.owner-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.4rem;
  margin-bottom: 16px;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
