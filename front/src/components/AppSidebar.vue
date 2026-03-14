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

    <div v-if="!props.collapsed" class="sidebar__footer">
      <p>只展示后端已支持主功能</p>
      <span>问答会话列表与练习列表接口当前未开放</span>
    </div>
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

.sidebar__footer {
  margin-top: auto;
  padding: 16px;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.18);
}

.sidebar__footer p,
.sidebar__footer span {
  margin: 0;
}

.sidebar__footer p {
  font-size: 13px;
  font-weight: 700;
  color: #f8fbff;
}

.sidebar__footer span {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.6;
  color: rgba(219, 234, 254, 0.72);
}

.is-collapsed .sidebar__brand {
  justify-content: center;
}
</style>
