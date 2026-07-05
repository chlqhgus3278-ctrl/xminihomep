<template>
  <div class="login-page">
    <h1>folio</h1>
    <p>싸이월드 스타일 개발자 포트폴리오</p>
    <a class="google-login-button" :href="googleLoginUrl">Google로 로그인</a>
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'LoginPage',
  computed: {
    googleLoginUrl() {
      return `${import.meta.env.VITE_API_URL}/oauth2/authorization/google`
    }
  }
})
</script>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  gap: 1rem;
}

.google-login-button {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  background: #4285f4;
  color: #fff;
  text-decoration: none;
}
</style>
