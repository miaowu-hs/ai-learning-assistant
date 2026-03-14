import { createRouter, createWebHistory } from 'vue-router'

import { pinia } from '@/stores'
import { useAuthStore } from '@/stores/auth'

const APP_TITLE = 'AI 学习助手'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: {
        title: '登录',
        requiresAuth: false,
      },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: {
        title: '注册',
        requiresAuth: false,
      },
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/dashboard',
      meta: {
        title: '首页',
        requiresAuth: true,
      },
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: {
            title: '学习概览',
            requiresAuth: true,
            menuKey: '/dashboard',
          },
        },
        {
          path: 'documents',
          name: 'documents',
          component: () => import('@/views/documents/DocumentsView.vue'),
          meta: {
            title: '文档管理',
            requiresAuth: true,
            menuKey: '/documents',
          },
        },
        {
          path: 'documents/:id',
          name: 'document-detail',
          component: () => import('@/views/documents/DocumentDetailView.vue'),
          meta: {
            title: '文档详情',
            requiresAuth: true,
            menuKey: '/documents',
          },
        },
        {
          path: 'documents/:id/chat',
          name: 'document-chat',
          component: () => import('@/views/chat/DocumentChatView.vue'),
          meta: {
            title: '文档问答',
            requiresAuth: true,
            menuKey: '/documents',
          },
        },
        {
          path: 'practices/:practiceSetId',
          name: 'practice',
          component: () => import('@/views/practice/PracticeView.vue'),
          meta: {
            title: '练习作答',
            requiresAuth: true,
          },
        },
        {
          path: 'practice-records/:recordId',
          name: 'practice-record',
          component: () => import('@/views/practice/PracticeRecordView.vue'),
          meta: {
            title: '练习结果',
            requiresAuth: true,
          },
        },
        {
          path: 'wrongbook',
          name: 'wrongbook',
          component: () => import('@/views/wrongbook/WrongbookView.vue'),
          meta: {
            title: '错题本',
            requiresAuth: true,
            menuKey: '/wrongbook',
          },
        },
      ],
    },
    {
      path: '/404',
      name: 'not-found',
      component: () => import('@/views/not-found/NotFoundView.vue'),
      meta: {
        title: '页面不存在',
        requiresAuth: false,
      },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404',
    },
  ],
})

router.beforeEach(async (to) => {
  document.title = `${to.meta.title || APP_TITLE} - ${APP_TITLE}`

  const authStore = useAuthStore(pinia)
  authStore.restoreLoginState()

  if (to.meta.requiresAuth && !authStore.token) {
    return {
      path: '/login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  if (!to.meta.requiresAuth && authStore.token && ['/login', '/register'].includes(to.path)) {
    return '/dashboard'
  }

  if (to.meta.requiresAuth && authStore.token && !authStore.user) {
    try {
      await authStore.fetchCurrentUser()
    } catch {
      authStore.logout(false)
      return {
        path: '/login',
        query: {
          redirect: to.fullPath,
        },
      }
    }
  }

  return true
})

export default router
