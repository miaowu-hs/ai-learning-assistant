import type {
  RegenerateWrongPracticePayload,
  WrongQuestion,
} from '@/types/wrongbook'
import request from '@/utils/request'

const LONG_RUNNING_TIMEOUT = 1000 * 60 * 5

export function getWrongbookApi() {
  return request.get<WrongQuestion[]>('/api/wrongbook')
}

export function deleteWrongQuestionApi(id: number) {
  return request.delete<null>(`/api/wrongbook/${id}`)
}

export function regenerateWrongPracticeApi(payload: RegenerateWrongPracticePayload) {
  return request.post<Record<string, unknown>, RegenerateWrongPracticePayload>(
    '/api/wrongbook/regenerate',
    payload,
    {
      timeout: LONG_RUNNING_TIMEOUT,
    },
  )
}
