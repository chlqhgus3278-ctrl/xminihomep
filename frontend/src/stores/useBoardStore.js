import { defineStore } from 'pinia'
import axios from 'axios'
import { showConfirm } from '../utils/dialog'

export const useBoardStore = defineStore('board', {
  state: () => ({
    posts: [],
    currentPost: null,
    isLoading: false,
    // 현재 보고 있는 섹션. 우측 메뉴(SidebarRight)와 메인(BoardList)이 공유한다.
    activeType: 'MAIN',
    // 게시판 에디터가 열려 있는지 여부. 섹션 이동 시 작성 중 내용 유실 확인에 사용한다.
    isEditing: false,
    // 방명록 변경 신호. 메인 방명록에서 글/댓글 작성·삭제 시 증가하고, 우측 미리보기가 감지해 갱신한다.
    guestbookVersion: 0
  }),
  getters: {
    postsByType: (state) => (type) => state.posts.filter((post) => post.boardType === type)
  },
  actions: {
    async setActiveType(type) {
      if (type === this.activeType) return
      if (this.isEditing) {
        const ok = await showConfirm('작성 중인 내용이 사라집니다.\n이동하시겠습니까?')
        if (!ok) return
      }
      this.activeType = type
    },
    setEditing(value) {
      this.isEditing = value
    },
    bumpGuestbookVersion() {
      this.guestbookVersion += 1
    },
    async fetchMyPosts(type) {
      this.isLoading = true
      try {
        const res = await axios.get('/api/board', { params: type ? { type } : {} })
        this.posts = res.data.data
      } finally {
        this.isLoading = false
      }
    },
    async fetchPost(id) {
      const res = await axios.get(`/api/board/${id}`)
      this.currentPost = res.data.data
      return this.currentPost
    },
    async createPost(payload) {
      const res = await axios.post('/api/board', payload)
      this.posts.push(res.data.data)
      return res.data.data
    },
    async updatePost(id, payload) {
      const res = await axios.put(`/api/board/${id}`, payload)
      const index = this.posts.findIndex((post) => post.id === id)
      if (index !== -1) {
        this.posts.splice(index, 1, res.data.data)
      }
      return res.data.data
    },
    async deletePost(id) {
      await axios.delete(`/api/board/${id}`)
      this.posts = this.posts.filter((post) => post.id !== id)
    }
  }
})
