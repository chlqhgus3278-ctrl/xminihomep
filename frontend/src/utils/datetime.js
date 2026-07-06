/** ISO LocalDateTime 문자열('2026-07-05T12:34:56') → '2026.07.05 12:34' */
export function formatDateTime(dateTime) {
  if (!dateTime) return ''
  const date = dateTime.slice(0, 10).replaceAll('-', '.')
  const time = dateTime.slice(11, 16)
  return time ? `${date} ${time}` : date
}
