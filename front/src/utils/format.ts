import type { DocumentParseStatus, DocumentSummaryStatus } from '@/types/document'
import type { QuestionType } from '@/types/practice'

export function formatDateTime(value?: string): string {
  if (!value) {
    return '--'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date)
}

export function formatFileSize(fileSize?: number): string {
  if (!fileSize || fileSize <= 0) {
    return '0 B'
  }

  const units = ['B', 'KB', 'MB', 'GB']
  let size = fileSize
  let unitIndex = 0

  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024
    unitIndex += 1
  }

  return `${size.toFixed(size >= 10 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`
}

export function getParseStatusConfig(status: DocumentParseStatus): {
  type: 'info' | 'warning' | 'success' | 'danger'
  label: string
} {
  switch (status) {
    case 'PROCESSING':
      return { type: 'warning', label: '解析中' }
    case 'COMPLETED':
      return { type: 'success', label: '已完成' }
    case 'FAILED':
      return { type: 'danger', label: '解析失败' }
    default:
      return { type: 'info', label: '待解析' }
  }
}

export function getSummaryStatusConfig(status: DocumentSummaryStatus): {
  type: 'info' | 'warning' | 'success' | 'danger'
  label: string
} {
  switch (status) {
    case 'PROCESSING':
      return { type: 'warning', label: '生成中' }
    case 'COMPLETED':
      return { type: 'success', label: '已生成' }
    case 'FAILED':
      return { type: 'danger', label: '生成失败' }
    default:
      return { type: 'info', label: '待生成' }
  }
}

export function getQuestionTypeLabel(type: QuestionType): string {
  switch (type) {
    case 'SINGLE_CHOICE':
      return '单选题'
    case 'TRUE_FALSE':
      return '判断题'
    case 'SHORT_ANSWER':
      return '简答题'
    default:
      return type
  }
}

export function getInitialText(value?: string): string {
  return (value?.trim()?.slice(0, 1) ?? '学').toUpperCase()
}

export function formatPercent(value: number): string {
  return `${(value * 100).toFixed(0)}%`
}
