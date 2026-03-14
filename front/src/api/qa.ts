import type {
  AskQuestionPayload,
  CreateQaSessionPayload,
  QaAskResponse,
  QaHistoryResponse,
  QaSession,
} from '@/types/qa'
import request from '@/utils/request'

const LONG_RUNNING_TIMEOUT = 1000 * 60 * 5

export function createQaSessionApi(payload: CreateQaSessionPayload) {
  return request.post<QaSession | Record<string, unknown>, CreateQaSessionPayload>(
    '/api/qa/sessions',
    payload,
  )
}

export function askQuestionApi(sessionId: number, payload: AskQuestionPayload) {
  return request.post<QaAskResponse, AskQuestionPayload>(
    `/api/qa/sessions/${sessionId}/ask`,
    payload,
    {
      timeout: LONG_RUNNING_TIMEOUT,
    },
  )
}

export function getQaHistoryApi(sessionId: number) {
  return request.get<QaHistoryResponse>(`/api/qa/sessions/${sessionId}/history`)
}
