<script setup lang="ts">
import { computed } from 'vue'
import { Menu } from '@element-plus/icons-vue'

import type { UserProfile } from '@/types/user'
import { getInitialText } from '@/utils/format'

const props = defineProps<{
  title: string
  user: UserProfile | null
  mobile: boolean
}>()

const emit = defineEmits<{
  toggleMenu: []
  logout: []
}>()

const displayName = computed(() => props.user?.nickname || props.user?.username || '学习者')
const initials = computed(() => getInitialText(displayName.value))
</script>

<template>
  <header class="app-header app-card">
    <div class="app-header__left">
      <el-button
        v-if="mobile"
        circle
        :icon="Menu"
        @click="emit('toggleMenu')"
      />
      <div>
        <h2 class="app-header__title">{{ title }}</h2>
        <p class="app-header__subtitle">围绕文档学习、问答、练习与错题闭环。</p>
      </div>
    </div>

    <div class="app-header__right">
      <div class="app-header__user">
        <div class="app-header__avatar">
          {{ initials }}
        </div>
        <div class="app-header__meta">
          <strong>{{ displayName }}</strong>
          <span>{{ props.user?.email || props.user?.phone || '当前已登录' }}</span>
        </div>
      </div>
      <el-button text type="primary" @click="emit('logout')">退出登录</el-button>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 18px 22px;
  border-radius: 24px;
}

.app-header__left,
.app-header__right,
.app-header__user {
  display: flex;
  align-items: center;
  gap: 14px;
}

.app-header__title {
  margin: 0;
  font-size: 24px;
  color: var(--color-secondary);
}

.app-header__subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.app-header__avatar {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  font-weight: 700;
  color: var(--color-primary);
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.16), rgba(37, 99, 235, 0.08));
}

.app-header__meta {
  display: flex;
  flex-direction: column;
}

.app-header__meta strong {
  color: var(--color-secondary);
}

.app-header__meta span {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

@media (max-width: 768px) {
  .app-header {
    padding: 16px;
    flex-direction: column;
    align-items: stretch;
  }

  .app-header__left,
  .app-header__right {
    justify-content: space-between;
  }

  .app-header__title {
    font-size: 20px;
  }
}
</style>
