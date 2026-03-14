export interface CreateQaSessionPayload {
  documentId: number
  title?: string
}

export interface AskQuestionPayload {
  question: string
}

export interface ChunkReference {
  chunkId: number
  chunkIndex: number
  content: string
  score: number
}

export interface QaSession {
  id: number
  documentId: number
  title: string
  createdAt: string
  updatedAt: string
}

export interface QaMessage {
  id: number
  role: 'USER' | 'ASSISTANT'
  content: string
  references: ChunkReference[]
  createdAt: string
}

export interface QaAskResponse {
  sessionId: number
  documentId: number
  question: string
  answer: string
  references: ChunkReference[]
  answeredAt: string
}

export interface QaHistoryResponse {
  session: QaSession
  messages: QaMessage[]
}

export interface ChatMessageItem {
  id: string | number
  role: 'USER' | 'ASSISTANT'
  content: string
  references: ChunkReference[]
  createdAt?: string
}
