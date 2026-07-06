<template>
  <div class="my-page">
    <div class="app-topbar" :class="themeClass">
      <button @click="handleLogout">로그아웃</button>
    </div>

    <component
      :is="layoutComponent"
      v-if="profileStore.profile && authStore.myUsername"
      :profile="profileStore.profile"
      :username="authStore.myUsername"
      :is-owner="true"
      :skin-config="profileStore.skinConfig"
      :board-posts="boardStore.posts"
    >
      <BoardList editable :username="authStore.myUsername" />
    </component>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import { useAuthStore } from '../stores/useAuthStore'
import { useProfileStore } from '../stores/useProfileStore'
import { useBoardStore } from '../stores/useBoardStore'
import RetroLayout from '../components/layout/RetroLayout.vue'
import ModernLayout from '../components/layout/ModernLayout.vue'
import BoardList from '../components/board/BoardList.vue'

export default defineComponent({
  name: 'MyPage',
  components: { RetroLayout, ModernLayout, BoardList },
  setup() {
    return {
      authStore: useAuthStore(),
      profileStore: useProfileStore(),
      boardStore: useBoardStore()
    }
  },
  computed: {
    layoutComponent() {
      return this.profileStore.currentTheme === 'modern' ? 'ModernLayout' : 'RetroLayout'
    },
    // 로그아웃 버튼도 현재 테마의 CSS 변수/폰트를 따라가게 한다
    themeClass() {
      return this.profileStore.currentTheme === 'modern' ? 'theme-modern' : 'theme-retro'
    }
  },
  async created() {
    try {
      await this.authStore.fetchMe()
      await this.profileStore.fetchMine()
      this.profileStore.applySkinToDOM()
      // 내 홈피 방문도 집계한다 (같은 IP는 하루 1회만 반영)
      this.profileStore.recordVisit(this.authStore.myUsername)
    } catch (e) {
      // 401은 main.js의 전역 axios 인터셉터가 로그아웃 + /login 이동까지 처리한다.
      // 그 외 에러는 여기서 화면을 깨뜨리지 않도록 조용히 무시한다.
    }
  },
  methods: {
    handleLogout() {
      this.authStore.logout()
      this.$router.push('/login')
    }
  }
})
</script>

<style scoped>
.my-page {
  position: relative;
}

.app-topbar {
  display: flex;
  justify-content: flex-end;
  padding: 0.5rem 1rem;
}

/* 레트로: 별도 띠 대신 모눈종이 배경 위에 떠 있는 버튼으로 */
.app-topbar.theme-retro {
  position: absolute;
  top: 8px;
  right: 12px;
  z-index: 20;
  padding: 0;
}

.app-topbar.theme-retro button {
  font-size: 0.78rem;
  padding: 6px 14px;
  border-color: var(--spine);
}
</style>
