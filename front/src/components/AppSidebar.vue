<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps<{
  collapsed: boolean
}>()

const emit = defineEmits<{
  select: []
}>()

const router = useRouter()
const route = useRoute()

const menuItems = [
  {
    label: '学习概览',
    path: '/dashboard',
    icon: 'DataAnalysis',
  },
  {
    label: '文档管理',
    path: '/documents',
    icon: 'FolderOpened',
  },
  {
    label: '错题本',
    path: '/wrongbook',
    icon: 'CollectionTag',
  },
]

const activePath = computed(() => route.meta.menuKey ?? route.path)

function handleSelect(path: string): void {
  router.push(path)
  emit('select')
}
</script>

<template>
  <div class="sidebar" :class="{ 'is-collapsed': props.collapsed }">
    <div class="sidebar__brand">
      <div class="sidebar__logo">
        <span>AI</span>
      </div>
      <div v-if="!props.collapsed" class="sidebar__meta">
        <strong>学习助手</strong>
        <span>文档问答与练习平台</span>
      </div>
    </div>

    <el-menu
      :default-active="activePath"
      :collapse="props.collapsed"
      class="sidebar__menu"
      @select="handleSelect"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.path"
        :index="item.path"
      >
        <el-icon>
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.label }}</span>
      </el-menu-item>
    </el-menu>

  </div>
</template>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px 16px;
  color: #dbeafe;
}

.sidebar__brand {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 8px 8px 0;
}

.sidebar__logo {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  font-weight: 800;
  letter-spacing: 0.06em;
  color: #eff6ff;
  background: linear-gradient(180deg, rgba(96, 165, 250, 0.9), rgba(37, 99, 235, 0.92));
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.35);
}

.sidebar__meta {
  display: flex;
  flex-direction: column;
}

.sidebar__meta strong {
  font-size: 18px;
  color: #f8fbff;
}

.sidebar__meta span {
  font-size: 12px;
  color: rgba(219, 234, 254, 0.7);
}

.sidebar__menu {
  border: none;
  background: transparent;
}

.sidebar__menu :deep(.el-menu-item) {
  margin-bottom: 8px;
  border-radius: 16px;
  color: rgba(219, 234, 254, 0.85);
}

.sidebar__menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}

.sidebar__menu :deep(.el-menu-item.is-active) {
  color: #f8fbff;
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.32), rgba(59, 130, 246, 0.08));
}

.is-collapsed .sidebar__brand {
  justify-content: center;
}
</style>
