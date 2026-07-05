<template>
  <div class="bgm-bar" v-if="bgmUrl">
    <audio ref="audioRef" :src="bgmUrl" loop @canplay="onCanPlay" />
    <span class="bgm-label">♪ BGM</span>
    <button type="button" @click="toggle">{{ isPlaying ? '■' : '▶' }}</button>
    <span class="bgm-title">{{ bgmTitle }}</span>
    <input type="range" min="0" max="1" step="0.05"
           v-model.number="volume" @input="applyVolume" />
  </div>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'BgmPlayer',
  props: {
    bgmUrl: String,
    bgmTitle: String
  },
  data() {
    return {
      isPlaying: false,
      volume: 0.5
    }
  },
  methods: {
    toggle() {
      const audio = this.$refs.audioRef
      if (!audio) return
      if (this.isPlaying) { audio.pause(); this.isPlaying = false }
      else { audio.play(); this.isPlaying = true }
    },
    applyVolume() {
      if (this.$refs.audioRef) this.$refs.audioRef.volume = this.volume
    },
    onCanPlay() {
      if (this.$refs.audioRef) this.$refs.audioRef.volume = this.volume
    }
    // 브라우저 정책상 자동재생 불가 → 유저가 직접 ▶ 클릭해야 재생됨
  }
})
</script>

<style scoped>
.bgm-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.8rem;
}

.bgm-bar button {
  padding: 0.2rem 0.5rem;
}

.bgm-bar input[type='range'] {
  width: 80px;
}
</style>
