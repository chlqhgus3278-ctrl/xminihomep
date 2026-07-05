// 공통 팝업(알림/확인) 유틸.
// 브라우저 기본 alert/confirm 대신 디자인된 팝업(AppDialog)을 띄운다.
// 사용법: await showAlert('저장되었습니다.') / if (await showConfirm('삭제하시겠습니까?')) { ... }
import { reactive } from 'vue'

export const dialogState = reactive({
  visible: false,
  type: 'alert', // 'alert' | 'confirm'
  message: '',
  resolve: null
})

function open(type, message) {
  return new Promise((resolve) => {
    dialogState.type = type
    dialogState.message = message
    dialogState.resolve = resolve
    dialogState.visible = true
  })
}

/** 확인 버튼만 있는 알림 팝업. 닫히면 resolve된다. */
export function showAlert(message) {
  return open('alert', message)
}

/** 확인/취소 팝업. 확인=true, 취소=false */
export function showConfirm(message) {
  return open('confirm', message)
}

/** AppDialog에서 버튼 클릭 시 호출 */
export function closeDialog(result) {
  dialogState.visible = false
  const resolve = dialogState.resolve
  dialogState.resolve = null
  if (resolve) resolve(result)
}
