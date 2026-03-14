<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getPracticeRecordApi } from '@/api/practice'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { PracticeResult } from '@/types/practice'
import { formatDateTime, getQuestionTypeLabel } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const recordId = computed(() => Number(route.params.recordId))
const loading = ref(false)
const result = ref<PracticeResult | null>(null)

async function loadRecord(): Promise<void> {
  loading.value = true

  try {
    result.value = await getPracticeRecordApi(recordId.value)
  } finally {
    loading.value = false
  }
}

void loadRecord()
</script>

<template>
  <PageContainer
    :title="result?.title || '练习结果'"
    description="展示总分、分项得分与逐题判定结果，直接来自练习结果接口。"
  >
    <template #actions>
      <el-button @click="router.back()">返回上一页</el-button>
      <el-button v-if="result" type="primary" @click="router.push(`/documents/${result.documentId}`)">
        返回文档
      </el-button>
    </template>

    <el-skeleton :loading="loading" animated :rows="12">
      <template v-if="result">
        <div class="record-grid">
          <el-card class="record-card record-card--summary app-card" shadow="never">
            <div class="record-summary">
              <div>
                <span>总分</span>
                <strong>{{ result.totalScore }}</strong>
              </div>
              <div>
                <span>客观题得分</span>
                <strong>{{ result.objectiveScore }}</strong>
              </div>
              <div>
                <span>主观题得分</span>
                <strong>{{ result.subjectiveScore }}</strong>
              </div>
              <div>
                <span>提交时间</span>
                <strong>{{ formatDateTime(result.submittedAt) }}</strong>
              </div>
            </div>
          </el-card>

          <div class="record-answer-list">
            <el-card
              v-for="answer in result.answers"
              :key="answer.questionId"
              class="record-card app-card"
              shadow="never"
            >
              <div class="record-card__header">
                <div class="record-card__meta">
                  <span class="app-tag">{{ getQuestionTypeLabel(answer.questionType) }}</span>
                  <el-tag :type="answer.correct ? 'success' : 'danger'" round>
                    {{ answer.correct ? '回答正确' : '回答错误' }}
                  </el-tag>
                </div>
                <span class="record-card__score">{{ answer.score }}/{{ answer.maxScore }}</span>
              </div>

              <h3 class="record-card__stem">{{ answer.stem }}</h3>

              <div v-if="answer.options?.length" class="record-card__options">
                <div v-for="option in answer.options" :key="option.key">
                  {{ option.key }}. {{ option.content }}
                </div>
              </div>

              <div class="record-card__body">
                <div>
                  <span>你的答案</span>
                  <p>{{ answer.userAnswer || '未作答' }}</p>
                </div>
                <div>
                  <span>正确答案</span>
                  <p>{{ answer.correctAnswer || '--' }}</p>
                </div>
                <div>
                  <span>AI 判题分析</span>
                  <p>{{ answer.judgeAnalysis || '暂无判题分析' }}</p>
                </div>
                <div>
                  <span>题目解析</span>
                  <p>{{ answer.explanation || '暂无题目解析' }}</p>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </template>
      <EmptyState v-else title="暂无练习结果" description="请确认练习记录 ID 是否有效。" />
    </el-skeleton>
  </PageContainer>
</template>

<style scoped>
.record-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-card {
  border-radius: 24px;
}

.record-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.record-summary > div {
  padding: 16px 18px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}

.record-summary span,
.record-card__body span {
  display: block;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.record-summary strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: var(--color-secondary);
}

.record-answer-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.record-card__header,
.record-card__meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.record-card__header {
  justify-content: space-between;
}

.record-card__score {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
}

.record-card__stem {
  margin: 18px 0 14px;
  color: var(--color-secondary);
  line-height: 1.7;
}

.record-card__options {
  display: grid;
  gap: 8px;
  margin-bottom: 16px;
  color: var(--color-text-secondary);
}

.record-card__body {
  display: grid;
  gap: 14px;
}

.record-card__body > div {
  padding: 14px 16px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}

.record-card__body p {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
  line-height: 1.75;
  white-space: pre-wrap;
}

@media (max-width: 960px) {
  .record-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .record-summary {
    grid-template-columns: 1fr;
  }
}
</style>
