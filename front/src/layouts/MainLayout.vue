<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import AppHeader from '@/components/AppHeader.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

const mobileSidebarVisible = ref(false)
const isMobile = ref(window.innerWidth < 1080)

const pageTitle = computed(() => route.meta.title ?? 'AI 学习助手')

function handleResize(): void {
  isMobile.value = window.innerWidth < 1080

  if (isMobile.value) {
    appStore.setSidebarCollapsed(false)
  } else {
    mobileSidebarVisible.value = false
  }
}

function toggleMenu(): void {
  if (isMobile.value) {
    mobileSidebarVisible.value = !mobileSidebarVisible.value
    return
  }

  appStore.toggleSidebar()
}

function closeMobileMenu(): void {
  if (isMobile.value) {
    mobileSidebarVisible.value = false
  }
}

function handleLogout(): void {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <div class="main-layout">
    <aside
      class="main-layout__sidebar"
      :class="{
        'is-collapsed': appStore.sidebarCollapsed && !isMobile,
        'is-mobile-open': mobileSidebarVisible,
      }"
    >
      <AppSidebar
        :collapsed="appStore.sidebarCollapsed && !isMobile"
        @select="closeMobileMenu"
      />
    </aside>

    <div
      v-if="isMobile && mobileSidebarVisible"
      class="main-layout__overlay"
      @click="closeMobileMenu"
    />

    <div class="main-layout__main">
      <AppHeader
        :title="pageTitle"
        :user="authStore.user"
        :mobile="isMobile"
        @toggle-menu="toggleMenu"
        @logout="handleLogout"
      />

      <main class="main-layout__content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  gap: 24px;
  padding: 24px;
}

.main-layout__sidebar {
  position: sticky;
  top: 24px;
  align-self: flex-start;
  width: 284px;
  min-height: calc(100vh - 48px);
  border-radius: 30px;
  background: linear-gradient(180deg, #10203b 0%, #17335f 100%);
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.22);
  transition:
    width 0.24s ease,
    transform 0.24s ease;
  z-index: 20;
}

.main-layout__sidebar.is-collapsed {
  width: 88px;
}

.main-layout__main {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-layout__content {
  min-height: 0;
}

.main-layout__overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.42);
  z-index: 15;
}

@media (max-width: 1080px) {
  .main-layout {
    padding: 16px;
    gap: 0;
  }

  .main-layout__sidebar {
    position: fixed;
    top: 16px;
    left: 16px;
    width: min(284px, calc(100vw - 32px));
    min-height: calc(100vh - 32px);
    transform: translateX(calc(-100% - 20px));
  }

  .main-layout__sidebar.is-mobile-open {
    transform: translateX(0);
  }
}
</style>
