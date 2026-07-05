<template>
  <div class="public-page">
    <p v-if="notFound" class="not-found">존재하지 않거나 비공개인 홈피입니다.</p>

    <component
      :is="layoutComponent"
      v-else-if="profileStore.profile"
      :profile="profileStore.profile"
      :username="$route.params.username"
      :is-owner="isOwner"
      :skin-config="profileStore.skinConfig"
      :board-posts="boardPosts"
      :home-link="`/${$route.params.username}`"
    >
      <BoardList :posts="boardPosts" :editable="false" />
    </component>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import axios from 'axios'
import { useAuthStore } from '../stores/useAuthStore'
import { useProfileStore } from '../stores/useProfileStore'
import RetroLayout from '../components/layout/RetroLayout.vue'
import ModernLayout from '../components/layout/ModernLayout.vue'
import BoardList from '../components/board/BoardList.vue'

export default defineComponent({
  name: 'PublicPage',
  components: { RetroLayout, ModernLayout, BoardList },
  setup() {
    return {
      authStore: useAuthStore(),
      profileStore: useProfileStore()
    }
  },
  data() {
    return {
      boardPosts: [],
      notFound: false
    }
  },
  computed: {
    layoutComponent() {
      return this.profileStore.currentTheme === 'modern' ? 'ModernLayout' : 'RetroLayout'
    },
    isOwner() {
      return this.authStore.myUsername === this.$route.params.username
    }
  },
  async created() {
    if (this.authStore.isLoggedIn && !this.authStore.user) {
      await this.authStore.fetchMe().catch(() => {})
    }
    await this.load()
  },
  watch: {
    '$route.params.username'() {
      this.load()
    }
  },
  methods: {
    async load() {
      this.notFound = false
      try {
        const res = await axios.get(`/api/public/${this.$route.params.username}`)
        this.profileStore.profile = res.data.data.profile
        this.profileStore.theme = this.profileStore.profile.theme || 'retro'
        this.profileStore.skinConfig = this.profileStore.profile.skinConfig || {}
        this.boardPosts = res.data.data.boardPosts
        axios.post(`/api/public/${this.$route.params.username}/visit`).catch(() => {})
      } catch (e) {
        this.notFound = true
      }
    }
  }
})
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>
