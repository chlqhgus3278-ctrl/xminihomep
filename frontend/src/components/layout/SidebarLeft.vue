<template>
  <aside class="sidebar-left">
    <div class="profile-photo" :class="{ editable: isEditing }" @click="isEditing && openFilePicker()">
      <img v-if="profile?.profileImgUrl" :src="profile.profileImgUrl" alt="프로필 사진" />
      <div v-else class="profile-photo-placeholder">📷</div>
      <div v-if="isEditing" class="photo-overlay">사진 변경</div>
    </div>
    <input ref="fileInput" type="file" accept="image/*" class="hidden-input" @change="handlePhotoChange" />
    <button
      v-if="isEditing && profile?.profileImgUrl"
      type="button"
      class="photo-delete"
      @click="handlePhotoDelete"
    >
      사진 삭제
    </button>

    <div class="profile-info">
      <!-- 수정 버튼 자리에 동일한 크기로 저장/취소 버튼이 나타난다 -->
      <div class="edit-toggle-row">
        <template v-if="isOwner && !isEditing">
          <button type="button" class="edit-toggle" @click="startEdit">✏️ 수정</button>
        </template>
        <template v-else-if="isOwner && isEditing">
          <button type="button" class="edit-toggle" :disabled="saving" @click="handleSave">
            {{ saving ? '저장 중' : '💾 저장' }}
          </button>
          <button type="button" class="edit-toggle" :disabled="saving" @click="cancelEdit">✖ 취소</button>
        </template>
      </div>

      <form v-if="isEditing" class="edit-form" @submit.prevent="handleSave">
        <label>홈피 제목<input v-model="form.homepageTitle" placeholder="OO의 홈피" /></label>
        <label>이름<input v-model="form.displayName" placeholder="이름" /></label>
        <label>위치<input v-model="form.location" placeholder="위치" /></label>
        <label>이메일<input v-model="form.emailPublic" type="email" placeholder="공개 이메일" /></label>
        <label>연락처<input v-model="form.phone" placeholder="연락처" /></label>
        <label>소개<textarea v-model="form.intro" placeholder="소개" rows="2" /></label>
      </form>

      <template v-else>
        <p class="display-name">{{ profile?.displayName || username }}</p>
        <p v-if="profile?.location" class="info-row">📍 {{ profile.location }}</p>
        <p v-if="profile?.emailPublic" class="info-row">✉️ {{ profile.emailPublic }}</p>
        <p v-if="profile?.phone" class="info-row">📞 {{ profile.phone }}</p>
        <p v-if="profile?.intro" class="info-row intro">{{ profile.intro }}</p>
      </template>
    </div>

    <div class="divider" />

    <VisitorCounter :username="username" />

    <div class="divider" />

    <GuestbookWidget :username="username" />
  </aside>
</template>

<script>
import { defineComponent } from 'vue'
import { useProfileStore } from '../../stores/useProfileStore'
import VisitorCounter from './VisitorCounter.vue'
import GuestbookWidget from '../guestbook/GuestbookWidget.vue'

export default defineComponent({
  name: 'SidebarLeft',
  components: { VisitorCounter, GuestbookWidget },
  props: {
    profile: { type: Object, default: null },
    username: { type: String, required: true },
    isOwner: { type: Boolean, default: false }
  },
  setup() {
    return { profileStore: useProfileStore() }
  },
  data() {
    return {
      isEditing: false,
      saving: false,
      form: {
        homepageTitle: '',
        displayName: '',
        location: '',
        emailPublic: '',
        phone: '',
        intro: ''
      }
    }
  },
  methods: {
    startEdit() {
      this.form.homepageTitle = this.profile?.homepageTitle || ''
      this.form.displayName = this.profile?.displayName || ''
      this.form.location = this.profile?.location || ''
      this.form.emailPublic = this.profile?.emailPublic || ''
      this.form.phone = this.profile?.phone || ''
      this.form.intro = this.profile?.intro || ''
      this.isEditing = true
    },
    cancelEdit() {
      this.isEditing = false
    },
    async handleSave() {
      this.saving = true
      try {
        await this.profileStore.updateProfile({ ...this.form })
        this.isEditing = false
      } finally {
        this.saving = false
      }
    },
    openFilePicker() {
      this.$refs.fileInput.click()
    },
    async handlePhotoChange(event) {
      const file = event.target.files[0]
      event.target.value = ''
      if (!file) return
      this.saving = true
      try {
        await this.profileStore.uploadImage(file)
      } catch (e) {
        alert(e.response?.data?.message || '사진 업로드에 실패했습니다.')
      } finally {
        this.saving = false
      }
    },
    async handlePhotoDelete() {
      this.saving = true
      try {
        await this.profileStore.deleteImage()
      } finally {
        this.saving = false
      }
    }
  }
})
</script>

<style scoped>
.sidebar-left {
  color: var(--text);
  font-size: 0.875rem;
}

.profile-photo {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto 0.4rem;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface2, transparent);
}

.profile-photo.editable {
  cursor: pointer;
}

.profile-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-photo-placeholder {
  font-size: 2rem;
  opacity: 0.4;
}

.photo-overlay {
  position: absolute;
  inset: auto 0 0 0;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 0.7rem;
  text-align: center;
  padding: 0.2rem 0;
}

.photo-delete {
  display: block;
  margin: 0 auto 0.5rem;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.72rem;
  padding: 0;
}

.hidden-input {
  display: none;
}

.profile-info {
  text-align: center;
}

.edit-toggle-row {
  display: flex;
  justify-content: center;
  gap: 0.4rem;
  margin-bottom: 0.4rem;
  min-height: 1.2rem;
}

.edit-toggle {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0;
  width: 3.5rem;
}

.edit-toggle:disabled {
  opacity: 0.5;
  cursor: default;
}

.display-name {
  font-weight: 700;
  font-size: 1rem;
  margin: 0 0 0.5rem;
}

.info-row {
  margin: 0.25rem 0;
  color: var(--text-muted);
}

.info-row.intro {
  white-space: pre-wrap;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  text-align: left;
}

.edit-form label {
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
  font-size: 0.72rem;
  color: var(--text-muted);
}

.edit-form input,
.edit-form textarea {
  width: 100%;
  padding: 0.35rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  font-size: 0.8rem;
  font-family: inherit;
  box-sizing: border-box;
}

.divider {
  border-top: 1px dashed var(--border);
  margin: 1rem 0;
}
</style>
