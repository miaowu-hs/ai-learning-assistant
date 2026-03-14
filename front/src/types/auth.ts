import type { UserProfile } from './user'

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  password: string
  nickname?: string
  email?: string
  phone?: string
}

export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  user: UserProfile
}
