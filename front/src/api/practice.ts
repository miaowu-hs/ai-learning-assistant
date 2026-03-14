import type {
  GeneratePracticePayload,
  PracticeResult,
  PracticeSetDetail,
  SubmitPracticePayload,
} from '@/types/practice'
import request from '@/utils/request'

const LONG_RUNNING_TIMEOUT = 1000 * 60 * 5

export function generatePracticeApi(payload: GeneratePracticePayload) {
  return request.post<Record<string, unknown>, GeneratePracticePayload>('/api/practices/generate', payload, {
    timeout: LONG_RUNNING_TIMEOUT,
  })
}

export function getPracticeSetDetailApi(practiceSetId: number) {
  return request.get<PracticeSetDetail>(`/api/practices/sets/${practiceSetId}`)
}

export function submitPracticeApi(practiceSetId: number, payload: SubmitPracticePayload) {
  return request.post<Record<string, unknown> | PracticeResult, SubmitPracticePayload>(
    `/api/practices/sets/${practiceSetId}/submit`,
    payload,
    {
      timeout: LONG_RUNNING_TIMEOUT,
    },
  )
}

export function getPracticeRecordApi(recordId: number) {
  return request.get<PracticeResult>(`/api/practices/records/${recordId}`)
}
