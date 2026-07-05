import { defineStore } from 'pinia'
import axios from 'axios'

export const useBoardStore = defineStore('board', {
  state: () => ({
    posts: [],
    currentPost: null,
    isLoading: false,
    // 현재 보고 있는 섹션. 우측 메뉴(SidebarRight)와 메인(BoardList)이 공유한다.
    activeType: 'CAREER_HISTORY'
  }),
  getters: {
    postsByType: (state) => (type) => state.posts.filter((post) => post.boardType === type)
  },
  actions: {
    setActiveType(type) {
      this.activeType = type
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
    },
    async toggleVisibility(id) {
      const res = await axios.patch(`/api/board/${id}/visibility`)
      const index = this.posts.findIndex((post) => post.id === id)
      if (index !== -1) {
        this.posts.splice(index, 1, res.data.data)
      }
      return res.data.data
    }
  }
})
