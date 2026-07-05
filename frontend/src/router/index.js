import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/useAuthStore'
import MyPage from '../views/MyPage.vue'
import LoginPage from '../views/LoginPage.vue'
import SettingsPage from '../views/SettingsPage.vue'
import PublicPage from '../views/PublicPage.vue'
import AuthCallback from '../views/AuthCallback.vue'

const routes = [
  { path: '/', name: 'my-page', component: MyPage, meta: { requiresAuth: true } },
  { path: '/auth/callback', name: 'auth-callback', component: AuthCallback },
  { path: '/login', name: 'login', component: LoginPage },
  { path: '/settings', name: 'settings', component: SettingsPage, meta: { requiresAuth: true } },
  { path: '/:username', name: 'public-page', component: PublicPage, props: true }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'login' }
  }
  return true
})

export default router
