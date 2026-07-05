import { defineStore } from 'pinia'
import axios from 'axios'

export const useProfileStore = defineStore('profile', {
  state: () => ({
    profile: null,    // HashMap 형태로 그대로 사용
    theme: 'retro',
    skinConfig: {}
  }),
  getters: {
    currentTheme: (state) => state.theme,
    hasProfile: (state) => !!state.profile
  },
  actions: {
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
