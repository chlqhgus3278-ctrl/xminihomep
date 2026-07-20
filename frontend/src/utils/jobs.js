// 프로필 이름 옆에 표시하는 직군 라벨. 간결한 표기(Dev 등)로 통일한다.
// 목록에서 고르거나 직접 입력할 수 있으며, 기본값은 Dev.

export const DEFAULT_JOB = 'Dev'

/** 직업 선택 팝업에 노출할 그룹별 목록 */
export const JOB_GROUPS = [
  {
    label: '개발',
    jobs: ['Dev', 'Frontend Dev', 'Backend Dev', 'Fullstack Dev', 'Mobile Dev', 'Game Dev', 'Embedded Dev']
  },
  {
    label: '데이터 / AI',
    jobs: ['Data Engineer', 'Data Analyst', 'Data Scientist', 'ML Engineer', 'AI Engineer', 'DBA']
  },
  {
    label: '인프라 / 운영',
    jobs: ['DevOps', 'SRE', 'Cloud Engineer', 'Infra Engineer', 'Network Engineer']
  },
  {
    label: '보안',
    jobs: ['Security Engineer', 'Pentester']
  },
  {
    label: 'QA',
    jobs: ['QA Engineer']
  },
  {
    label: '기획 / 관리 / 디자인',
    jobs: ['PM', 'PO', 'Tech Lead', 'CTO', 'Planner', 'UI/UX Designer']
  }
]
