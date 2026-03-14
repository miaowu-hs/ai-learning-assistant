import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

import type { Result } from '@/types/common'
import { clearAuthStorage, getToken } from './storage'

const service = axios.create({
  // 开发环境默认走 Vite 代理，避免浏览器直接跨域请求后端。
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 20000,
})

let handlingUnauthorized = false

function redirectToLogin(message?: string): void {
  clearAuthStorage()

  if (handlingUnauthorized) {
    return
  }

  handlingUnauthorized = true

  if (message) {
    ElMessage.error(message)
  }

  const currentPath = `${window.location.pathname}${window.location.search}`
  const target =
    currentPath && currentPath !== '/login'
      ? `/login?redirect=${encodeURIComponent(currentPath)}`
      : '/login'

  window.location.replace(target)

  window.setTimeout(() => {
    handlingUnauthorized = false
  }, 500)
}

service.interceptors.request.use((config) => {
  const token = getToken()

  if (token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

service.interceptors.response.use(
  (response) => {
    const result = response.data as Result<unknown>

    if (!result || typeof result.code !== 'number') {
      ElMessage.error('服务响应格式不正确')
      return Promise.reject(new Error('服务响应格式不正确'))
    }

    if (result.code !== 200) {
      if (result.code === 401) {
        redirectToLogin(result.message || '登录已过期，请重新登录')
      } else if (result.message) {
        ElMessage.error(result.message)
      }

      return Promise.reject(new Error(result.message || '请求失败'))
    }

    return response
  },
  (error) => {
    if (axios.isAxiosError(error)) {
      if (error.code === 'ECONNABORTED') {
        ElMessage.warning('请求等待超时，后端可能仍在处理中，请稍后刷新状态')
      } else if (error.response?.status === 401) {
        redirectToLogin('登录已过期，请重新登录')
      } else {
        ElMessage.error(
          (error.response?.data as { message?: string } | undefined)?.message ||
            error.message ||
            '网络请求失败',
        )
      }
    } else {
      ElMessage.error('网络请求失败')
    }

    return Promise.reject(error)
  },
)

const request = {
  get<T>(url: string, config?: AxiosRequestConfig) {
    return service.get<Result<T>>(url, config).then((response) => response.data.data)
  },
  post<T, D = unknown>(url: string, data?: D, config?: AxiosRequestConfig<D>) {
    return service.post<Result<T>>(url, data, config).then((response) => response.data.data)
  },
  put<T, D = unknown>(url: string, data?: D, config?: AxiosRequestConfig<D>) {
    return service.put<Result<T>>(url, data, config).then((response) => response.data.data)
  },
  delete<T>(url: string, config?: AxiosRequestConfig) {
    return service.delete<Result<T>>(url, config).then((response) => response.data.data)
  },
  upload<T>(url: string, data: FormData, config?: AxiosRequestConfig<FormData>) {
    return service
      .post<Result<T>>(url, data, {
        ...config,
        headers: {
          ...config?.headers,
          'Content-Type': 'multipart/form-data',
        },
      })
      .then((response) => response.data.data)
  },
}

export default request
