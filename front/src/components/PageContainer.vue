<script setup lang="ts">
defineProps<{
  title: string
  description?: string
}>()
</script>

<template>
  <section class="page-container">
    <header class="page-container__header">
      <div>
        <p class="page-container__eyebrow">AI 学习助手</p>
        <h1 class="page-container__title">{{ title }}</h1>
        <p v-if="description" class="page-container__description">
          {{ description }}
        </p>
      </div>
      <div v-if="$slots.actions" class="page-container__actions">
        <slot name="actions" />
      </div>
    </header>
    <div class="page-container__content">
      <slot />
    </div>
  </section>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-container__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-container__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: var(--color-primary);
  text-transform: uppercase;
}

.page-container__title {
  margin: 0;
  font-size: 32px;
  line-height: 1.15;
  color: var(--color-secondary);
}

.page-container__description {
  margin: 10px 0 0;
  color: var(--color-text-secondary);
}

.page-container__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.page-container__content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

@media (max-width: 768px) {
  .page-container__header {
    flex-direction: column;
  }

  .page-container__title {
    font-size: 26px;
  }
}
</style>
