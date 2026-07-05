import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'
import './style.css'
import './assets/styles/theme-retro.css'
import './assets/styles/theme-modern.css'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/useAuthStore'

axios.defaults.baseURL = import.meta.env.VITE_API_URL

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

const authStore = useAuthStore()
if (authStore.token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${authStore.token}`
}

// JWT가 만료/무효화되면 어떤 화면에서 API를 호출했든 한곳에서 일관되게 로그아웃 처리한다.
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && authStore.isLoggedIn) {
      authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

app.mount('#app')
