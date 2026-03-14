import type { PageResult } from '@/types/common'
import type {
  DocumentListQuery,
  DocumentSummary,
  LearningDocument,
} from '@/types/document'
import request from '@/utils/request'

const LONG_RUNNING_TIMEOUT = 1000 * 60 * 5

export function uploadDocumentApi(formData: FormData) {
  return request.upload<LearningDocument>('/api/documents/upload', formData)
}

export function getDocumentListApi(params: DocumentListQuery) {
  return request.get<PageResult<LearningDocument>>('/api/documents', {
    params,
  })
}

export function getDocumentDetailApi(id: number) {
  return request.get<LearningDocument>(`/api/documents/${id}`)
}

export function parseDocumentApi(id: number) {
  return request.post<null>(`/api/documents/${id}/parse`, undefined, {
    timeout: LONG_RUNNING_TIMEOUT,
  })
}

export function getDocumentSummaryApi(id: number) {
  return request.get<DocumentSummary>(`/api/documents/${id}/summary`)
}

export function deleteDocumentApi(id: number) {
  return request.delete<null>(`/api/documents/${id}`)
}
