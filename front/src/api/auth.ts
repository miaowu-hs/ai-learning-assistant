import type { AuthResponse, LoginPayload, RegisterPayload } from '@/types/auth'
import type { UserProfile } from '@/types/user'
import request from '@/utils/request'

export function registerApi(payload: RegisterPayload) {
  return request.post<null, RegisterPayload>('/api/auth/register', payload)
}

export function loginApi(payload: LoginPayload) {
  return request.post<AuthResponse, LoginPayload>('/api/auth/login', payload)
}

export function getCurrentUserApi() {
  return request.get<UserProfile>('/api/users/current')
}
