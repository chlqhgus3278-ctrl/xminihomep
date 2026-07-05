<template>
  <div class="my-page">
    <div class="app-topbar">
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
      home-link="/"
    >
      <BoardList editable />
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
    }
  },
  async created() {
    try {
      await this.authStore.fetchMe()
      await this.profileStore.fetchMine()
      this.profileStore.applySkinToDOM()
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
.app-topbar {
  display: flex;
  justify-content: flex-end;
  padding: 0.5rem 1rem;
}
</style>
