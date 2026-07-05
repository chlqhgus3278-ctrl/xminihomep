import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
    isLoading: false,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    myUsername: (state) => state.user?.username,
  },
  actions: {
    async fetchMe() {
      const res = await axios.get('/api/auth/me')
      this.user = res.data.data
    },
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
    }
  }
})
