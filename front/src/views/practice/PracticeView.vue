<script setup lang="ts">
import axios from 'axios'
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getPracticeSetDetailApi, submitPracticeApi } from '@/api/practice'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import PracticeQuestionCard from '@/components/PracticeQuestionCard.vue'
import type { PracticeSetDetail } from '@/types/practice'
import { extractPracticeRecordId } from '@/utils/normalize'

const route = useRoute()
const router = useRouter()

const practiceSetId = computed(() => Number(route.params.practiceSetId))
const loading = ref(false)
const submitting = ref(false)
const submitTimedOut = ref(false)
const practiceDetail = ref<PracticeSetDetail | null>(null)
const answers = ref<Record<number, string>>({})

async function loadPracticeDetail(): Promise<void> {
  loading.value = true

  try {
    const detail = await getPracticeSetDetailApi(practiceSetId.value)
    practiceDetail.value = detail

    const nextAnswers: Record<number, string> = {}
    detail.questions.forEach((question) => {
      nextAnswers[question.id] = answers.value[question.id] ?? ''
    })
    answers.value = nextAnswers
  } finally {
    loading.value = false
  }
}

function updateAnswer(questionId: number, value: string): void {
  answers.value = {
    ...answers.value,
    [questionId]: value,
  }
}

async function handleSubmit(): Promise<void> {
  if (!practiceDetail.value) {
    return
  }

  const unanswered = practiceDetail.value.questions.find((question) => {
    const rawAnswer = answers.value[question.id] ?? ''
    return !rawAnswer.trim()
  })

  if (unanswered) {
    ElMessage.warning(`请先完成第 ${unanswered.sortOrder} 题`)
    return
  }

  await ElMessageBox.confirm('确认提交本次练习答案吗？提交后将进入结果页。', '提交确认', {
    type: 'warning',
    confirmButtonText: '确认提交',
    cancelButtonText: '取消',
  })

  submitting.value = true
  submitTimedOut.value = false

  try {
    const result = await submitPracticeApi(practiceDetail.value.id, {
      answers: practiceDetail.value.questions.map((question) => ({
        questionId: question.id,
        answer: (answers.value[question.id] ?? '').trim(),
      })),
    })
    const recordId = extractPracticeRecordId(result)

    if (!recordId) {
      throw new Error('后端未返回练习记录 ID，无法跳转结果页')
    }

    ElMessage.success('提交成功')
    await router.push(`/practice-records/${recordId}`)
  } catch (error) {
    if (axios.isAxiosError(error) && error.code === 'ECONNABORTED') {
      submitTimedOut.value = true
      return
    }

    return
  } finally {
    submitting.value = false
  }
}

void loadPracticeDetail()
</script>

<template>
  <PageContainer
    :title="practiceDetail?.title || '练习作答'"
    description="支持单选题、判断题和简答题三种题型，答案提交后跳转结果页。"
  >
    <template #actions>
      <el-button @click="$router.back()">返回上一页</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">提交答案</el-button>
    </template>

    <el-skeleton :loading="loading" animated :rows="10">
      <template v-if="practiceDetail">
        <el-alert
          v-if="submitTimedOut"
          title="本次提交等待超时，后端可能已经完成判题。由于当前后端没有按练习集查询最近提交记录的接口，前端无法自动恢复结果页，请先不要重复提交。"
          type="warning"
          :closable="false"
          show-icon
        />

        <el-card class="practice-meta app-card" shadow="never">
          <div class="practice-meta__list">
            <div>
              <span>题目数量</span>
              <strong>{{ practiceDetail.questionCount }}</strong>
            </div>
            <div>
              <span>总分</span>
              <strong>{{ practiceDetail.totalScore }}</strong>
            </div>
            <div>
              <span>练习集 ID</span>
              <strong>{{ practiceDetail.id }}</strong>
            </div>
          </div>
        </el-card>

        <div class="practice-list">
          <PracticeQuestionCard
            v-for="question in practiceDetail.questions"
            :key="question.id"
            :question="question"
            :model-value="answers[question.id] ?? ''"
            @update:model-value="updateAnswer(question.id, $event)"
          />
        </div>
      </template>
      <EmptyState v-else title="未找到练习" description="请确认练习集 ID 是否有效。" />
    </el-skeleton>
  </PageContainer>
</template>

<style scoped>
.practice-meta {
  border-radius: 24px;
}

.practice-meta__list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.practice-meta__list > div {
  padding: 16px 18px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}

.practice-meta__list span {
  display: block;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.practice-meta__list strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
  color: var(--color-secondary);
}

.practice-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

@media (max-width: 768px) {
  .practice-meta__list {
    grid-template-columns: 1fr;
  }
}
</style>
