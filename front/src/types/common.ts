export interface Result<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

// 后端未给出分页 data 的精确结构，这里仅做兼容性类型声明与解析。
export interface PageResult<T> {
  [key: string]: unknown
  records?: T[]
  list?: T[]
  items?: T[]
  content?: T[]
  total?: number
  pageNum?: number
  pageSize?: number
  current?: number
  size?: number
  pages?: number
}

export interface NormalizedPageResult<T> {
  items: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface PageQuery {
  pageNum: number
  pageSize: number
}
