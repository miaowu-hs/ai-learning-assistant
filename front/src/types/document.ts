import type { PageQuery } from './common'

export type DocumentParseStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
export type DocumentSummaryStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

export interface LearningDocument {
  id: number
  userId: number
  title: string
  fileName: string
  fileUrl: string
  fileType: 'pdf' | 'docx' | 'txt' | 'md'
  fileSize: number
  parseStatus: DocumentParseStatus
  summaryStatus: DocumentSummaryStatus
  createdAt: string
  updatedAt: string
}

export interface DocumentSummary {
  documentId: number
  shortSummary: string
  outline: string[]
  keyPoints: string[]
  createdAt: string
  updatedAt: string
}

export interface DocumentListQuery extends PageQuery {
  title?: string
}

export interface DocumentUploadPayload {
  title?: string
  file: File
}
