import { defineStore } from 'pinia'
import axios from 'axios'

export const useProfileStore = defineStore('profile', {
  state: () => ({
    profile: null,    // HashMap 형태로 그대로 사용
    theme: 'retro',
    skinConfig: {},
    // 방문 기록(POST /visit)이 끝났음을 알리는 신호. VisitorCounter가 감지해 다시 조회한다.
    visitVersion: 0
  }),
  getters: {
    currentTheme: (state) => state.theme,
    hasProfile: (state) => !!state.profile
  },
  actions: {
    // 방문을 기록하고, 성공하면 카운터가 갱신되도록 신호를 보낸다
    async recordVisit(username) {
      try {
        await axios.post(`/api/public/${username}/visit`)
        this.visitVersion += 1
      } catch (e) {
        // 방문 집계 실패는 화면 동작에 영향을 주지 않는다
      }
    },
    async fetchPublic(username) {
      const res = await axios.get(`/api/public/${username}`)
      this.profile = res.data.data.profile
      this.theme = this.profile.theme || 'retro'
      this.skinConfig = this.profile.skinConfig || {}
    },
    async fetchMine() {
      const res = await axios.get('/api/profile')
      this.profile = res.data.data
      this.theme = this.profile.theme || 'retro'
      this.skinConfig = this.profile.skinConfig || {}
    },
    async updateProfile(updates) {
      const res = await axios.put('/api/profile', updates)
      this.profile = res.data.data
    },
    async updateTheme(theme) {
      await axios.put('/api/settings/theme', { theme })
      this.theme = theme
    },
    async updateSkin(skinConfig) {
      await axios.put('/api/settings/skin', skinConfig)
      this.skinConfig = skinConfig
    },
    async updateLayout(sections) {
      const res = await axios.put('/api/settings/layout', { sections })
      if (this.profile) {
        this.profile.layoutConfig = res.data.data.layoutConfig
      }
    },
    async uploadImage(file) {
      const formData = new FormData()
      formData.append('file', file)
      const res = await axios.post('/api/profile/image', formData)
      this.profile = res.data.data
    },
    async deleteImage() {
      await axios.delete('/api/profile/image')
      if (this.profile) {
        this.profile.profileImgUrl = null
      }
    },
    async uploadBgm(file) {
      const formData = new FormData()
      formData.append('file', file)
      const res = await axios.post('/api/profile/bgm', formData)
      this.profile = res.data.data
    },
    async deleteBgm() {
      await axios.delete('/api/profile/bgm')
      if (this.profile) {
        this.profile.bgmUrl = null
        this.profile.bgmTitle = null
      }
    },
    applySkinToDOM() {
      const root = document.documentElement
      Object.entries(this.skinConfig).forEach(([key, value]) => {
        root.style.setProperty(key, value)
      })
    }
  }
})
