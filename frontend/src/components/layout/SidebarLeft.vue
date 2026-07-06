<template>
  <aside class="sidebar-left">
    <div class="profile-photo" :class="{ editable: isEditing }" @click="isEditing && openFilePicker()">
      <img v-if="profile?.profileImgUrl" :src="profile.profileImgUrl" alt="프로필 사진" />
      <div v-else class="profile-photo-placeholder">{{ initials }}</div>
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
        <label class="form-row">
          <span class="form-label">이름 <em class="required-mark">*</em></span>
          <input v-model="form.displayName" placeholder="이름" />
        </label>
        <label class="form-row">
          <span class="form-label">위치</span>
          <input v-model="form.location" placeholder="위치" />
        </label>
        <label class="form-row">
          <span class="form-label">이메일</span>
          <input v-model="form.emailPublic" type="email" placeholder="공개 이메일" />
        </label>
        <label class="form-row">
          <span class="form-label">연락처</span>
          <input v-model="form.phone" type="tel" placeholder="연락처" />
        </label>
        <label class="form-row form-row--top">
          <span class="form-label">소개 <em class="required-mark">*</em></span>
          <textarea v-model="form.intro" placeholder="소개" rows="3" />
        </label>
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

    <div class="skills-section">
      <div class="skills-head">
        <p class="skills-title">기술스택</p>
        <button v-if="isOwner" type="button" class="edit-toggle" @click="showSkillsModal = true">
          ✏️ 수정
        </button>
      </div>
      <div v-if="skillTags.length" class="tag-list">
        <span v-for="tag in skillTags" :key="tag" class="skill-tag">{{ tag }}</span>
      </div>
      <p v-else class="skills-empty">등록된 기술이 없습니다.</p>
    </div>

    <SkillsEditModal v-if="showSkillsModal" :post="skillsPost" @close="showSkillsModal = false" />
  </aside>
</template>

<script>
import { defineComponent } from 'vue'
import { useProfileStore } from '../../stores/useProfileStore'
import { parseStructured } from '../../utils/resume'
import { showAlert } from '../../utils/dialog'
import { showToast } from '../../utils/toast'
import SkillsEditModal from '../skills/SkillsEditModal.vue'

export default defineComponent({
  name: 'SidebarLeft',
  components: { SkillsEditModal },
  props: {
    profile: { type: Object, default: null },
    username: { type: String, required: true },
    isOwner: { type: Boolean, default: false },
    // 기술스택 표시용 게시글 목록 (마이페이지: 내 글, 공개페이지: 공개 글)
    posts: { type: Array, default: () => [] }
  },
  setup() {
    return { profileStore: useProfileStore() }
  },
  computed: {
    skillsPost() {
      return this.posts.find((post) => post.boardType === 'SKILLS') || null
    },
    skillTags() {
      return parseStructured(this.skillsPost?.content)?.tags || []
    },
    initials() {
      const name = this.profile?.displayName || this.username || '?'
      return name.slice(0, 2)
    }
  },
  data() {
    return {
      isEditing: false,
      showSkillsModal: false,
      saving: false,
      form: {
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
      const payload = {
        displayName: this.form.displayName.trim(),
        location: this.form.location.trim(),
        emailPublic: this.form.emailPublic.trim(),
        phone: this.form.phone.trim(),
        intro: this.form.intro.trim()
      }

      if (!payload.displayName) {
        await showAlert('이름을 입력해 주세요.')
        return
      }
      if (payload.emailPublic && !this.isValidEmail(payload.emailPublic)) {
        await showAlert('이메일 형식을 확인해 주세요.')
        return
      }
      if (payload.phone && !this.isValidPhone(payload.phone)) {
        await showAlert('연락처 형식을 확인해 주세요.')
        return
      }
      if (!payload.intro) {
        await showAlert('소개를 입력해 주세요.')
        return
      }
      this.saving = true
      try {
        await this.profileStore.updateProfile(payload)
        this.isEditing = false
        showToast('저장되었습니다.')
      } catch (e) {
        showToast('저장에 실패했습니다.', 'error')
      } finally {
        this.saving = false
      }
    },
    isValidEmail(email) {
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
    },
    isValidPhone(phone) {
      return /^(01[016789]-?\d{3,4}-?\d{4}|0\d{1,2}-?\d{3,4}-?\d{4}|1[568]\d{2}-?\d{4})$/.test(phone)
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
        await showAlert(e.response?.data?.message || '사진 업로드에 실패했습니다.')
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
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, var(--primary), var(--text-muted));
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
  gap: 0.45rem;
  text-align: left;
}

/* 라벨 칸 고정폭 + 입력 칸 나머지 폭으로 줄을 딱 맞춘다 */
.form-row {
  display: grid;
  grid-template-columns: 3.2rem 1fr;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.72rem;
  color: var(--text-muted);
}

.form-row--top {
  align-items: start;
}

.form-row--top .form-label {
  padding-top: 0.4rem;
}

.form-label {
  text-align: right;
}

.required-mark {
  color: #e05252;
  font-style: normal;
}

.edit-form input,
.edit-form textarea {
  width: 100%;
  padding: 0.35rem 0.45rem;
  border: 1px solid var(--border, #ccc);
  border-radius: 4px;
  font-size: 0.8rem;
  font-family: inherit;
  box-sizing: border-box;
  min-width: 0;
}

.edit-form textarea {
  resize: vertical;
}

.divider {
  border-top: 1px dashed var(--border);
  margin: 1rem 0;
}

.skills-section {
  text-align: left;
}

.skills-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.skills-title {
  margin: 0;
  font-weight: 700;
}

.skills-empty {
  margin: 0;
  font-size: 0.78rem;
  color: var(--text-muted);
}
</style>
