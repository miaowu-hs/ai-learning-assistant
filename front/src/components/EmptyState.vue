<script setup lang="ts">
defineProps<{
  title?: string
  description?: string
}>()
</script>

<template>
  <div class="empty-state app-card">
    <el-empty :description="description || title || '暂无数据'">
      <template #image>
        <div class="empty-state__icon">
          <el-icon :size="34">
            <Reading />
          </el-icon>
        </div>
      </template>
      <div v-if="$slots.default" class="empty-state__content">
        <slot />
      </div>
      <div v-if="$slots.action" class="empty-state__action">
        <slot name="action" />
      </div>
    </el-empty>
  </div>
</template>

<style scoped>
.empty-state {
  border-radius: 24px;
}

.empty-state__icon {
  width: 72px;
  height: 72px;
  display: grid;
  place-items: center;
  border-radius: 22px;
  color: var(--color-primary);
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.18), rgba(37, 99, 235, 0.08));
}

.empty-state__content,
.empty-state__action {
  margin-top: 12px;
}
</style>
