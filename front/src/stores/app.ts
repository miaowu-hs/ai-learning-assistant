import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)

  function setSidebarCollapsed(value: boolean): void {
    sidebarCollapsed.value = value
  }

  function toggleSidebar(): void {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return {
    sidebarCollapsed,
    setSidebarCollapsed,
    toggleSidebar,
  }
})
