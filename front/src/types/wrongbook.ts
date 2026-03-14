import type { PracticeOption, QuestionType } from './practice'

export interface WrongQuestion {
  id: number
  documentId: number
  practiceSetId: number
  questionId: number
  questionType: QuestionType
  stem: string
  options: PracticeOption[]
  correctAnswer: string
  explanation: string
  knowledgePoint: string
  wrongCount: number
  lastUserAnswer: string
  updatedAt: string
}

export interface RegenerateWrongPracticePayload {
  documentId: number
  wrongQuestionIds: number[]
  title?: string
}
