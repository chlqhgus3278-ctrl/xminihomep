// 이력서(구조화 게시판) 공통 상수/유틸
// 경력사항·학력·기술스택·자격증/어학 게시판은 content 컬럼에 JSON 문자열을 저장한다.

/** 구조화 폼을 쓰는 게시판 타입 (나머지는 Quill 에디터 유지) */
export const STRUCTURED_TYPES = ['CAREER_HISTORY', 'INTRO', 'EDUCATION', 'SKILLS', 'CERT']

/** 사이드바/설정/목록에서 공유하는 섹션 정의. 어학은 자격증과 합쳐 CERT 하나로 표시한다.
 *  경력기술서(CAREER_DESC)는 경력사항의 담당업무/커리어 요약으로 일원화되어 메뉴에서 제외한다. */
export const SECTIONS = [
  { value: 'CAREER_HISTORY', label: '경력사항' },
  { value: 'INTRO', label: '자기소개서' },
  { value: 'EDUCATION', label: '학력' },
  { value: 'SKILLS', label: '기술스택' },
  { value: 'CERT', label: '자격증/어학' }
]

export const SECTION_LABELS = Object.fromEntries(SECTIONS.map((s) => [s.value, s.label]))

/** 우측 메뉴에 노출할 섹션. 기술스택은 좌측 프로필 아래에 상시 표시되므로 제외한다. */
export const MENU_SECTIONS = SECTIONS.filter((s) => s.value !== 'SKILLS')

/** 메인 대시보드 구획에 넣을 수 있는 섹션 */
export const MAIN_SECTION_CHOICES = MENU_SECTIONS

/** 메인 대시보드 기본 4구획 */
export const DEFAULT_MAIN_SECTIONS = ['CAREER_HISTORY', 'INTRO', 'EDUCATION', 'CERT']

/** 사람인/잡코리아 방식 byte 계산: 한글 등 non-ASCII 2byte, 영문/숫자/공백 1byte */
export function calcBytes(text) {
  let bytes = 0
  for (const ch of text || '') {
    bytes += ch.charCodeAt(0) > 127 ? 2 : 1
  }
  return bytes
}

/** 'YYYY-MM' → 'YYYY.MM' */
export function formatYm(ym) {
  return ym ? ym.replace('-', '.') : ''
}

/** 입사년월~퇴사년월 개월 수 (입사월 포함, 재직중이면 현재 월 기준). 계산 불가면 0 */
export function monthsBetween(startYm, endYm) {
  if (!startYm) return 0
  const [sy, sm] = startYm.split('-').map(Number)
  let ey
  let em
  if (endYm) {
    ;[ey, em] = endYm.split('-').map(Number)
  } else {
    const now = new Date()
    ey = now.getFullYear()
    em = now.getMonth() + 1
  }
  const months = (ey - sy) * 12 + (em - sm) + 1
  return months > 0 ? months : 0
}

/** 개월 수 → '2년 3개월' 형태 */
export function formatMonths(months) {
  if (!months) return ''
  const y = Math.floor(months / 12)
  const m = months % 12
  return [y > 0 ? `${y}년` : '', m > 0 ? `${m}개월` : ''].filter(Boolean).join(' ')
}

/** 재직기간 표시 문자열 */
export function calcPeriod(startYm, endYm) {
  return formatMonths(monthsBetween(startYm, endYm))
}

/** content(JSON 문자열) 파싱. 구조화 데이터가 아니면 null */
export function parseStructured(content) {
  if (!content) return null
  try {
    const value = JSON.parse(content)
    return typeof value === 'object' && value !== null ? value : null
  } catch {
    return null
  }
}

export const EDUCATION_LEVELS = ['고등학교 졸업', '대학교 졸업', '대학원 졸업']

export const EDUCATION_STATUS = ['졸업', '졸업예정', '재학중', '휴학', '중퇴', '수료']

export const GPA_SCALES = ['4.5', '4.3', '4.0', '100']

export const MAJOR_TYPES = ['주전공', '복수전공', '부전공', '연계전공']

export const CERT_KINDS = [
  { value: 'CERT', label: '자격증' },
  { value: 'LANGUAGE', label: '어학' }
]

/** 학교명 검색용 목록 (자동완성 후보, 직접 입력도 가능) */
export const SCHOOL_LIST = [
  '서울대학교', '연세대학교', '고려대학교', '카이스트(KAIST)', '포항공과대학교(POSTECH)',
  '성균관대학교', '한양대학교', '중앙대학교', '경희대학교', '한국외국어대학교',
  '서강대학교', '이화여자대학교', '서울시립대학교', '건국대학교', '동국대학교',
  '홍익대학교', '국민대학교', '숭실대학교', '세종대학교', '단국대학교',
  '숙명여자대학교', '성신여자대학교', '광운대학교', '명지대학교', '상명대학교',
  '가천대학교', '가톨릭대학교', '아주대학교', '인하대학교', '경기대학교',
  '부산대학교', '경북대학교', '전남대학교', '전북대학교', '충남대학교',
  '충북대학교', '강원대학교', '제주대학교', '경상국립대학교', '부경대학교',
  '울산대학교', '영남대학교', '계명대학교', '동아대학교', '조선대학교',
  '한국항공대학교', '한국기술교육대학교', '서울과학기술대학교', '금오공과대학교', '한밭대학교',
  '한국방송통신대학교', '사이버대학교'
]

/** 기술스택 자동완성 후보 */
export const SKILL_SUGGESTIONS = [
  // 백엔드
  { name: 'Java', category: '백엔드' },
  { name: 'Spring Boot', category: '백엔드' },
  { name: 'Spring Framework', category: '백엔드' },
  { name: 'JPA/Hibernate', category: '백엔드' },
  { name: 'Node.js', category: '백엔드' },
  { name: 'Express', category: '백엔드' },
  { name: 'NestJS', category: '백엔드' },
  { name: 'Python', category: '백엔드' },
  { name: 'Django', category: '백엔드' },
  { name: 'FastAPI', category: '백엔드' },
  { name: 'Flask', category: '백엔드' },
  { name: 'Kotlin', category: '백엔드' },
  { name: 'Go', category: '백엔드' },
  { name: 'C#', category: '백엔드' },
  { name: '.NET', category: '백엔드' },
  { name: 'PHP', category: '백엔드' },
  { name: 'Laravel', category: '백엔드' },
  { name: 'Ruby on Rails', category: '백엔드' },
  // 프론트엔드
  { name: 'JavaScript', category: '프론트엔드' },
  { name: 'TypeScript', category: '프론트엔드' },
  { name: 'Vue.js', category: '프론트엔드' },
  { name: 'React', category: '프론트엔드' },
  { name: 'Next.js', category: '프론트엔드' },
  { name: 'Nuxt.js', category: '프론트엔드' },
  { name: 'Angular', category: '프론트엔드' },
  { name: 'Svelte', category: '프론트엔드' },
  { name: 'HTML', category: '프론트엔드' },
  { name: 'CSS', category: '프론트엔드' },
  { name: 'Sass/SCSS', category: '프론트엔드' },
  { name: 'Tailwind CSS', category: '프론트엔드' },
  { name: 'jQuery', category: '프론트엔드' },
  { name: 'Pinia', category: '프론트엔드' },
  { name: 'Redux', category: '프론트엔드' },
  { name: 'Vite', category: '프론트엔드' },
  { name: 'Webpack', category: '프론트엔드' },
  // 모바일
  { name: 'Android', category: '모바일' },
  { name: 'iOS', category: '모바일' },
  { name: 'Swift', category: '모바일' },
  { name: 'Flutter', category: '모바일' },
  { name: 'React Native', category: '모바일' },
  // 데이터베이스
  { name: 'MySQL', category: 'DB' },
  { name: 'PostgreSQL', category: 'DB' },
  { name: 'Oracle', category: 'DB' },
  { name: 'MariaDB', category: 'DB' },
  { name: 'MongoDB', category: 'DB' },
  { name: 'Redis', category: 'DB' },
  { name: 'MS-SQL', category: 'DB' },
  { name: 'Elasticsearch', category: 'DB' },
  // 데브옵스/인프라
  { name: 'AWS', category: '데브옵스' },
  { name: 'GCP', category: '데브옵스' },
  { name: 'Azure', category: '데브옵스' },
  { name: 'Docker', category: '데브옵스' },
  { name: 'Kubernetes', category: '데브옵스' },
  { name: 'Jenkins', category: '데브옵스' },
  { name: 'GitHub Actions', category: '데브옵스' },
  { name: 'Nginx', category: '데브옵스' },
  { name: 'Linux', category: '데브옵스' },
  { name: 'Terraform', category: '데브옵스' },
  // 협업/기타
  { name: 'Git', category: '협업/기타' },
  { name: 'GitHub', category: '협업/기타' },
  { name: 'GitLab', category: '협업/기타' },
  { name: 'Jira', category: '협업/기타' },
  { name: 'Confluence', category: '협업/기타' },
  { name: 'Slack', category: '협업/기타' },
  { name: 'Notion', category: '협업/기타' },
  { name: 'Figma', category: '협업/기타' },
  { name: 'Kafka', category: '협업/기타' },
  { name: 'RabbitMQ', category: '협업/기타' },
  { name: 'GraphQL', category: '협업/기타' },
  { name: 'REST API', category: '협업/기타' }
]
