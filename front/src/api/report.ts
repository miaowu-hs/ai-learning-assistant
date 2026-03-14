import type { LearningReport } from '@/types/report'
import request from '@/utils/request'

export function getLearningReportApi() {
  return request.get<LearningReport>('/api/reports/me')
}
