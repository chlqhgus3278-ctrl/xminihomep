// 저장 성공/실패 등을 우하단에 3초간 알려주는 토스트 유틸.
// 사용법: showToast('저장되었습니다.') / showToast('저장에 실패했습니다.', 'error')
import { reactive } from 'vue'

const DURATION = 3000

export const toastState = reactive({
  items: []
})

let nextId = 1

export function showToast(message, type = 'success') {
  const id = nextId++
  toastState.items.push({ id, message, type })
  setTimeout(() => {
    toastState.items = toastState.items.filter((item) => item.id !== id)
  }, DURATION)
}
