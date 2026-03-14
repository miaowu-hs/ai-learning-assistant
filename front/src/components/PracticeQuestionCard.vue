<script setup lang="ts">
import { computed } from 'vue'

import type { PracticeQuestion } from '@/types/practice'
import { getQuestionTypeLabel } from '@/utils/format'

const props = defineProps<{
  question: PracticeQuestion
  modelValue: string
  readonly?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const sortedOptions = computed(() =>
  [...(props.question.options || [])].sort((a, b) => a.key.localeCompare(b.key)),
)

function updateValue(value: string): void {
  emit('update:modelValue', value)
}
</script>

<template>
  <el-card class="question-card app-card" shadow="never">
    <div class="question-card__header">
      <div class="question-card__meta">
        <span class="app-tag">{{ getQuestionTypeLabel(question.questionType) }}</span>
        <span class="app-tag">分值 {{ question.score }}</span>
      </div>
      <span class="question-card__order">第 {{ question.sortOrder }} 题</span>
    </div>

    <h3 class="question-card__stem">{{ question.stem }}</h3>

    <el-radio-group
      v-if="question.questionType === 'SINGLE_CHOICE'"
      :model-value="modelValue"
      class="question-card__radio-group"
      @update:model-value="updateValue"
    >
      <el-radio
        v-for="option in sortedOptions"
        :key="option.key"
        :label="option.key"
        :disabled="readonly"
        class="question-card__option"
      >
        {{ option.key }}. {{ option.content }}
      </el-radio>
    </el-radio-group>

    <el-radio-group
      v-else-if="question.questionType === 'TRUE_FALSE'"
      :model-value="modelValue"
      class="question-card__radio-group"
      @update:model-value="updateValue"
    >
      <template v-if="sortedOptions.length">
        <el-radio
          v-for="option in sortedOptions"
          :key="option.key"
          :label="option.key"
          :disabled="readonly"
          class="question-card__option"
        >
          {{ option.content }}
        </el-radio>
      </template>
      <template v-else>
        <!-- 后端未声明判断题答案字面量，这里默认使用 true / false 字符串提交。 -->
        <el-radio :label="'true'" :disabled="readonly" class="question-card__option">正确</el-radio>
        <el-radio :label="'false'" :disabled="readonly" class="question-card__option">错误</el-radio>
      </template>
    </el-radio-group>

    <el-input
      v-else
      :model-value="modelValue"
      type="textarea"
      :rows="6"
      maxlength="1000"
      show-word-limit
      resize="vertical"
      :disabled="readonly"
      placeholder="请输入你的答案"
      @update:model-value="updateValue"
    />
  </el-card>
</template>

<style scoped>
.question-card {
  border-radius: 24px;
}

.question-card__header,
.question-card__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.question-card__header {
  justify-content: space-between;
}

.question-card__order {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-tertiary);
}

.question-card__stem {
  margin: 18px 0;
  font-size: 18px;
  line-height: 1.65;
  color: var(--color-secondary);
}

.question-card__radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-card__option {
  margin-right: 0;
  padding: 14px 16px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}
</style>
