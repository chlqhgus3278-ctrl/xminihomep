<template>
  <div class="resume-form">
    <div v-for="(entry, index) in modelValue.entries" :key="index" class="entry-card">
      <div class="entry-head">
        <strong>학력 {{ index + 1 }}</strong>
        <button
          v-if="modelValue.entries.length > 1"
          type="button"
          class="remove-button"
          @click="removeEntry(index)"
        >
          삭제
        </button>
      </div>

      <div class="field-row">
        <div class="field">
          <label>학력구분 <span class="required">*</span></label>
          <select v-model="entry.level">
            <option v-for="level in levels" :key="level" :value="level">{{ level }}</option>
          </select>
        </div>
        <div class="field grow">
          <label>학교명 <span class="required">*</span></label>
          <input
            v-model="entry.school"
            :list="isHighSchool(entry) ? undefined : schoolListId"
            placeholder="학교 이름 검색 또는 직접 입력"
          />
        </div>
        <div class="field">
          <label>졸업여부</label>
          <select v-model="entry.status" @change="onStatusChange(entry)">
            <option v-for="status in statusOptions" :key="status" :value="status">{{ status }}</option>
          </select>
        </div>
      </div>

      <div class="field-row">
        <div class="field">
          <label>입학년월</label>
          <input type="month" v-model="entry.startYm" />
        </div>
        <div class="field">
          <label>{{ entry.status === '재학중' ? '졸업예정년월' : '졸업년월' }}</label>
          <input type="month" v-model="entry.endYm" />
        </div>
        <div class="field">
          <label>학점</label>
          <input
            v-model="entry.gpa"
            :disabled="entry.status === '재학중'"
            placeholder="예: 3.8"
            class="gpa-input"
          />
        </div>
        <div class="field">
          <label>만점기준</label>
          <select v-model="entry.gpaMax" :disabled="entry.status === '재학중'">
            <option v-for="scale in gpaScales" :key="scale" :value="scale">{{ scale }}</option>
          </select>
        </div>
      </div>

      <template v-if="!isHighSchool(entry)">
        <div v-for="(major, majorIndex) in entry.majors" :key="majorIndex" class="field-row">
          <div class="field">
            <label v-if="majorIndex === 0">전공구분</label>
            <select v-if="majorIndex > 0" v-model="major.type">
              <option v-for="type in majorTypes.slice(1)" :key="type" :value="type">{{ type }}</option>
            </select>
            <input v-else value="주전공" disabled />
          </div>
          <div class="field grow">
            <label v-if="majorIndex === 0">전공명</label>
            <input v-model="major.name" placeholder="예: 컴퓨터공학과" />
          </div>
          <button
            v-if="majorIndex > 0"
            type="button"
            class="remove-button"
            @click="removeMajor(entry, majorIndex)"
          >
            삭제
          </button>
        </div>
        <button type="button" class="add-sub-button" @click="addMajor(entry)">+ 추가전공 (복수/부전공)</button>
      </template>
    </div>

    <button type="button" class="add-button" @click="addEntry">+ 학력 추가</button>

    <datalist :id="schoolListId">
      <option v-for="school in schoolList" :key="school" :value="school" />
    </datalist>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import {
  EDUCATION_LEVELS,
  EDUCATION_STATUS,
  GPA_SCALES,
  MAJOR_TYPES,
  SCHOOL_LIST
} from '../../../utils/resume'

let uid = 0

function emptyEntry() {
  return {
    level: '대학교 졸업',
    school: '',
    status: '졸업',
    startYm: '',
    endYm: '',
    gpa: '',
    gpaMax: '4.5',
    majors: [{ type: '주전공', name: '' }]
  }
}

export default defineComponent({
  name: 'EducationForm',
  props: {
    modelValue: { type: Object, required: true }
  },
  data() {
    return {
      levels: EDUCATION_LEVELS,
      statusOptions: EDUCATION_STATUS,
      gpaScales: GPA_SCALES,
      majorTypes: MAJOR_TYPES,
      schoolList: SCHOOL_LIST,
      schoolListId: `school-list-${uid++}`
    }
  },
  created() {
    if (!Array.isArray(this.modelValue.entries)) {
      this.modelValue.entries = []
    }
    if (this.modelValue.entries.length === 0) {
      this.modelValue.entries.push(emptyEntry())
    }
  },
  methods: {
    isHighSchool(entry) {
      return entry.level === '고등학교 졸업'
    },
    onStatusChange(entry) {
      if (entry.status === '재학중') entry.gpa = ''
    },
    addEntry() {
      this.modelValue.entries.push(emptyEntry())
    },
    removeEntry(index) {
      this.modelValue.entries.splice(index, 1)
    },
    addMajor(entry) {
      entry.majors.push({ type: '복수전공', name: '' })
    },
    removeMajor(entry, majorIndex) {
      entry.majors.splice(majorIndex, 1)
    }
  }
})
</script>

<style scoped>
.gpa-input {
  max-width: 90px;
}

.add-sub-button {
  align-self: flex-start;
  font-size: 0.78rem;
  padding: 0.25rem 0.6rem;
}
</style>
