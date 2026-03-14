import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'

import { getCurrentUserApi, loginApi, registerApi } from '@/api/auth'
import type { AuthResponse, LoginPayload, RegisterPayload } from '@/types/auth'
import type { UserProfile } from '@/types/user'
import {
  clearAuthStorage,
  getStoredUser,
  getToken,
  setStoredUser,
  setToken,
} from '@/utils/storage'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(getToken())
  const user = ref<UserProfile | null>(getStoredUser())
  const isAuthenticated = computed(() => Boolean(token.value))

  function applyAuthState(authResponse: AuthResponse): void {
    token.value = authResponse.token
    user.value = authResponse.user
    setToken(authResponse.token)
    setStoredUser(authResponse.user)
  }

  function restoreLoginState(): void {
    token.value = getToken()
    user.value = getStoredUser()
  }

  async function login(payload: LoginPayload): Promise<AuthResponse> {
    const authResponse = await loginApi(payload)
    applyAuthState(authResponse)
    return authResponse
  }

  async function register(payload: RegisterPayload): Promise<void> {
    await registerApi(payload)
  }

  async function fetchCurrentUser(): Promise<UserProfile> {
    const currentUser = await getCurrentUserApi()
    user.value = currentUser
    setStoredUser(currentUser)
    return currentUser
  }

  function logout(showMessage = true): void {
    token.value = ''
    user.value = null
    clearAuthStorage()

    if (showMessage) {
      ElMessage.success('已退出登录')
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    fetchCurrentUser,
    restoreLoginState,
  }
})
