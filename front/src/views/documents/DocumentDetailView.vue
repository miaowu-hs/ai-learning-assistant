<script setup lang="ts">
import axios from 'axios'
import { computed, onBeforeUnmount, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getDocumentDetailApi, getDocumentSummaryApi, parseDocumentApi } from '@/api/document'
import { generatePracticeApi } from '@/api/practice'
import DocumentSummaryCard from '@/components/DocumentSummaryCard.vue'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import PracticeGenerateDialog from '@/components/PracticeGenerateDialog.vue'
import type { DocumentSummary, LearningDocument } from '@/types/document'
import {
  formatDateTime,
  formatFileSize,
  getParseStatusConfig,
  getSummaryStatusConfig,
} from '@/utils/format'
import { extractPracticeSetId } from '@/utils/normalize'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const parsing = ref(false)
const summaryLoading = ref(false)
const practiceDialogVisible = ref(false)
const practiceSubmitting = ref(false)

const documentDetail = ref<LearningDocument | null>(null)
const summary = ref<DocumentSummary | null>(null)
const pollTimer = ref<number | null>(null)

const documentId = computed(() => Number(route.params.id))

function shouldPollStatus(document: LearningDocument | null): boolean {
  if (!document) {
    return false
  }

  return document.parseStatus === 'PROCESSING' || document.summaryStatus === 'PROCESSING'
}

function stopPolling(): void {
  if (pollTimer.value !== null) {
    window.clearInterval(pollTimer.value)
    pollTimer.value = null
  }
}

function startPolling(): void {
  if (pollTimer.value !== null) {
    return
  }

  pollTimer.value = window.setInterval(() => {
    if (!shouldPollStatus(documentDetail.value)) {
      stopPolling()
      return
    }

    void loadDocumentDetail(false)
  }, 4000)
}

async function loadSummary(): Promise<void> {
  if (!documentDetail.value || documentDetail.value.summaryStatus !== 'COMPLETED') {
    summary.value = null
    return
  }

  summaryLoading.value = true

  try {
    summary.value = await getDocumentSummaryApi(documentDetail.value.id)
  } finally {
    summaryLoading.value = false
  }
}

async function loadDocumentDetail(showLoading = true): Promise<void> {
  if (showLoading) {
    loading.value = true
  }

  try {
    documentDetail.value = await getDocumentDetailApi(documentId.value)
    await loadSummary()

    if (shouldPollStatus(documentDetail.value)) {
      startPolling()
    } else {
      stopPolling()
    }
  } finally {
    if (showLoading) {
      loading.value = false
    }
  }
}

async function handleParse(): Promise<void> {
  if (!documentDetail.value) {
    return
  }

  parsing.value = true

  try {
    await parseDocumentApi(documentDetail.value.id)
    ElMessage.success('已触发文档解析，正在刷新状态')
    await loadDocumentDetail(false)
    startPolling()
  } catch (error) {
    if (axios.isAxiosError(error) && error.code === 'ECONNABORTED') {
      if (documentDetail.value) {
        documentDetail.value = {
          ...documentDetail.value,
          parseStatus: 'PROCESSING',
        }
      }

      startPolling()
      void loadDocumentDetail(false)
      return
    }
    return
  } finally {
    parsing.value = false
  }
}

async function handleGeneratePractice(payload: { questionCount: number; title?: string }): Promise<void> {
  if (!documentDetail.value) {
    return
  }

  practiceSubmitting.value = true

  try {
    const result = await generatePracticeApi({
      documentId: documentDetail.value.id,
      questionCount: payload.questionCount,
      title: payload.title,
    })
    const practiceSetId = extractPracticeSetId(result)

    if (!practiceSetId) {
      throw new Error('后端未返回练习集 ID，无法进入作答页')
    }

    ElMessage.success('练习生成成功')
    practiceDialogVisible.value = false
    await router.push(`/practices/${practiceSetId}`)
  } finally {
    practiceSubmitting.value = false
  }
}

function goToChat(): void {
  if (!documentDetail.value) {
    return
  }

  void router.push(`/documents/${documentDetail.value.id}/chat`)
}

void loadDocumentDetail()

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<template>
  <PageContainer
    :title="documentDetail?.title || '文档详情'"
    description="查看文档基础信息、摘要状态，并从当前文档直接发起问答或练习。"
  >
    <template #actions>
      <el-button @click="$router.push('/documents')">返回列表</el-button>
      <el-button type="warning" :loading="parsing" @click="handleParse">解析文档</el-button>
      <el-button type="success" @click="goToChat">开始问答</el-button>
      <el-button type="primary" @click="practiceDialogVisible = true">生成练习</el-button>
    </template>

    <el-skeleton :loading="loading" animated :rows="10">
      <template v-if="documentDetail">
        <div class="page-grid page-grid--2">
          <el-card class="detail-card app-card" shadow="never">
            <template #header>
              <div class="detail-card__header">
                <div>
                  <h3>基础信息</h3>
                  <p>字段与后端文档详情接口保持一致。</p>
                </div>
              </div>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="标题">{{ documentDetail.title }}</el-descriptions-item>
              <el-descriptions-item label="文件名">{{ documentDetail.fileName }}</el-descriptions-item>
              <el-descriptions-item label="文件类型">{{ documentDetail.fileType }}</el-descriptions-item>
              <el-descriptions-item label="文件大小">
                {{ formatFileSize(documentDetail.fileSize) }}
              </el-descriptions-item>
              <el-descriptions-item label="解析状态">
                <el-tag :type="getParseStatusConfig(documentDetail.parseStatus).type" round>
                  {{ getParseStatusConfig(documentDetail.parseStatus).label }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="摘要状态">
                <el-tag :type="getSummaryStatusConfig(documentDetail.summaryStatus).type" round>
                  {{ getSummaryStatusConfig(documentDetail.summaryStatus).label }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ formatDateTime(documentDetail.createdAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="更新时间">
                {{ formatDateTime(documentDetail.updatedAt) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <el-card class="detail-card app-card" shadow="never">
            <template #header>
              <div class="detail-card__header">
                <div>
                  <h3>功能说明</h3>
                  <p>当前页面仅接入后端已提供能力。</p>
                </div>
              </div>
            </template>
            <el-alert
              title="后端未提供文档下载或在线预览接口，因此当前前端不展示预览与下载能力。"
              type="info"
              :closable="false"
              show-icon
            />
            <div class="detail-notes">
              <div class="detail-note">
                <strong>1. 先解析</strong>
                <span>解析完成后，摘要与文档问答体验会更完整。</span>
              </div>
              <div class="detail-note">
                <strong>2. 再问答</strong>
                <span>从当前文档直接创建会话，无需额外的会话列表接口。</span>
              </div>
              <div class="detail-note">
                <strong>3. 生成练习</strong>
                <span>练习生成成功后将直接跳转作答页。</span>
              </div>
            </div>
          </el-card>
        </div>

        <DocumentSummaryCard
          :summary="summary"
          :loading="summaryLoading"
          :parse-status="documentDetail.parseStatus"
          :summary-status="documentDetail.summaryStatus"
        />
      </template>
      <EmptyState v-else title="未找到文档" description="请确认文档 ID 是否有效。" />
    </el-skeleton>

    <PracticeGenerateDialog
      v-model="practiceDialogVisible"
      :loading="practiceSubmitting"
      @confirm="handleGeneratePractice"
    />
  </PageContainer>
</template>

<style scoped>
.detail-card {
  border-radius: 24px;
}

.detail-card__header h3 {
  margin: 0;
  color: var(--color-secondary);
}

.detail-card__header p {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
}

.detail-notes {
  display: grid;
  gap: 14px;
  margin-top: 18px;
}

.detail-note {
  padding: 16px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}

.detail-note strong,
.detail-note span {
  display: block;
}

.detail-note span {
  margin-top: 6px;
  color: var(--color-text-secondary);
  line-height: 1.75;
}
</style>
