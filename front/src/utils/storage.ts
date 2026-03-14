import type { UserProfile } from '@/types/user'

const TOKEN_KEY = 'ai-learning-assistant-token'
const USER_KEY = 'ai-learning-assistant-user'

export function getToken(): string {
  return window.localStorage.getItem(TOKEN_KEY) ?? ''
}

export function setToken(token: string): void {
  window.localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken(): void {
  window.localStorage.removeItem(TOKEN_KEY)
}

export function getStoredUser(): UserProfile | null {
  const rawValue = window.localStorage.getItem(USER_KEY)

  if (!rawValue) {
    return null
  }

  try {
    return JSON.parse(rawValue) as UserProfile
  } catch {
    window.localStorage.removeItem(USER_KEY)
    return null
  }
}

export function setStoredUser(user: UserProfile): void {
  window.localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearStoredUser(): void {
  window.localStorage.removeItem(USER_KEY)
}

export function clearAuthStorage(): void {
  clearToken()
  clearStoredUser()
}
