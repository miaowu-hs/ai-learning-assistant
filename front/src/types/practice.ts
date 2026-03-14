export type QuestionType = 'SINGLE_CHOICE' | 'TRUE_FALSE' | 'SHORT_ANSWER'

export interface PracticeOption {
  key: string
  content: string
}

export interface GeneratePracticePayload {
  documentId: number
  questionCount: number
  title?: string
}

export interface PracticeQuestion {
  id: number
  questionType: QuestionType
  stem: string
  options: PracticeOption[]
  score: number
  sortOrder: number
}

export interface PracticeSetDetail {
  id: number
  documentId: number
  title: string
  questionCount: number
  totalScore: number
  createdAt: string
  questions: PracticeQuestion[]
}

export interface SubmitPracticeAnswer {
  questionId: number
  answer: string
}

export interface SubmitPracticePayload {
  answers: SubmitPracticeAnswer[]
}

export interface PracticeAnswerResult {
  questionId: number
  questionType: QuestionType
  stem: string
  options: PracticeOption[]
  userAnswer: string
  correctAnswer: string
  correct: boolean
  score: number
  maxScore: number
  judgeAnalysis: string
  explanation: string
}

export interface PracticeResult {
  recordId: number
  practiceSetId: number
  documentId: number
  title: string
  totalScore: number
  objectiveScore: number
  subjectiveScore: number
  status: 'SUBMITTED'
  submittedAt: string
  answers: PracticeAnswerResult[]
}
