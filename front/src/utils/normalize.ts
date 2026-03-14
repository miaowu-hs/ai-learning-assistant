import type { NormalizedPageResult, PageResult } from '@/types/common'

function isRecord(value: unknown): value is Record<string, unknown> {
  return Object.prototype.toString.call(value) === '[object Object]'
}

function pickNumber(source: Record<string, unknown>, keys: string[]): number | null {
  for (const key of keys) {
    const value = source[key]

    if (typeof value === 'number') {
      return value
    }
  }

  return null
}

export function normalizePageResult<T>(
  payload: PageResult<T> | T[] | null | undefined,
  fallbackPageNum = 1,
  fallbackPageSize = 10,
): NormalizedPageResult<T> {
  if (Array.isArray(payload)) {
    return {
      items: payload,
      total: payload.length,
      pageNum: fallbackPageNum,
      pageSize: fallbackPageSize,
    }
  }

  if (!payload) {
    return {
      items: [],
      total: 0,
      pageNum: fallbackPageNum,
      pageSize: fallbackPageSize,
    }
  }

  const items =
    payload.records ??
    payload.list ??
    payload.items ??
    payload.content ??
    []

  const total =
    payload.total ??
    payload.records?.length ??
    payload.list?.length ??
    payload.items?.length ??
    payload.content?.length ??
    0

  return {
    items,
    total,
    pageNum: payload.pageNum ?? payload.current ?? fallbackPageNum,
    pageSize: payload.pageSize ?? payload.size ?? fallbackPageSize,
  }
}

function extractId(
  payload: unknown,
  candidates: string[],
): number | null {
  if (typeof payload === 'number') {
    return payload
  }

  if (!isRecord(payload)) {
    return null
  }

  const matched = pickNumber(payload, candidates)
  return matched ?? null
}

// 后端未提供创建问答会话的完整返回结构，前端只读取最小必需标识符。
export function extractQaSessionId(payload: unknown): number | null {
  return extractId(payload, ['sessionId', 'id'])
}

// 后端未提供生成练习接口的完整返回结构，前端只读取最小必需标识符。
export function extractPracticeSetId(payload: unknown): number | null {
  return extractId(payload, ['practiceSetId', 'id'])
}

// 后端未提供提交练习接口的完整返回结构，前端只读取最小必需标识符。
export function extractPracticeRecordId(payload: unknown): number | null {
  return extractId(payload, ['recordId', 'id'])
}
