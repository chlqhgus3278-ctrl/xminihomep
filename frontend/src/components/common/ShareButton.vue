<template>
  <div class="share-button-wrapper">
    <button type="button" @click="handleCopy">🔗 링크 복사</button>
    <span v-if="copied" class="toast">복사되었습니다!</span>
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'ShareButton',
  data() {
    return {
      copied: false,
      timer: null
    }
  },
  methods: {
    async handleCopy() {
      try {
        await navigator.clipboard.writeText(window.location.href)
      } catch (e) {
        const input = document.createElement('input')
        input.value = window.location.href
        document.body.appendChild(input)
        input.select()
        document.execCommand('copy')
        document.body.removeChild(input)
      }
      this.copied = true
      clearTimeout(this.timer)
      this.timer = setTimeout(() => {
        this.copied = false
      }, 2000)
    }
  },
  beforeUnmount() {
    clearTimeout(this.timer)
  }
})
</script>

<style scoped>
.share-button-wrapper {
  position: relative;
  display: inline-block;
}

.toast {
  position: absolute;
  top: -1.6rem;
  left: 50%;
  transform: translateX(-50%);
  background: var(--primary, #333);
  color: #fff;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.7rem;
  white-space: nowrap;
}
</style>
