<template>
  <div class="auth-callback">로그인 처리 중...</div>
</template>

<script>
import { defineComponent } from 'vue'
import { useAuthStore } from '../stores/useAuthStore'

export default defineComponent({
  name: 'AuthCallback',
  created() {
    const token = this.$route.query.token
    if (token) {
      useAuthStore().setToken(token)
    }
    this.$router.replace('/')
  }
})
</script>

<style scoped>
.auth-callback {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
