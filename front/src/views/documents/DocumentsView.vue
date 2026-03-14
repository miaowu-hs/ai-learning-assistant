<script setup lang="ts">
import axios from 'axios'
import { onBeforeUnmount, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import { deleteDocumentApi, getDocumentListApi, parseDocumentApi } from '@/api/document'
import { generatePracticeApi } from '@/api/practice'
import DocumentUploadDialog from '@/components/DocumentUploadDialog.vue'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import PracticeGenerateDialog from '@/components/PracticeGenerateDialog.vue'
import type { DocumentListQuery, LearningDocument } from '@/types/document'
import {
  formatDateTime,
  formatFileSize,
  getParseStatusConfig,
  getSummaryStatusConfig,
} from '@/utils/format'
import { extractPracticeSetId, normalizePageResult } from '@/utils/normalize'

const router = useRouter()

const loading = ref(false)
const uploadVisible = ref(false)
const practiceDialogVisible = ref(false)
const practiceSubmitting = ref(false)
const currentPracticeDocument = ref<LearningDocument | null>(null)
const parsingId = ref<number | null>(null)
const deletingId = ref<number | null>(null)
const pollTimer = ref<number | null>(null)

const documents = ref<LearningDocument[]>([])
const total = ref(0)

const query = reactive<DocumentListQuery>({
  pageNum: 1,
  pageSize: 10,
  title: '',
})

function hasProcessingDocument(): boolean {
  return documents.value.some(
    (item) => item.parseStatus === 'PROCESSING' || item.summaryStatus === 'PROCESSING',
  )
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
    if (!hasProcessingDocument()) {
      stopPolling()
      return
    }

    void loadDocuments(false)
  }, 4000)
}

async function loadDocuments(showLoading = true): Promise<void> {
  if (showLoading) {
    loading.value = true
  }

  try {
    const pageData = await getDocumentListApi({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      title: query.title?.trim() || undefined,
    })
    const normalized = normalizePageResult(pageData, query.pageNum, query.pageSize)
    documents.value = normalized.items
    total.value = normalized.total

    if (hasProcessingDocument()) {
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

function handleSearch(): void {
  query.pageNum = 1
  void loadDocuments()
}

function handleReset(): void {
  query.title = ''
  query.pageNum = 1
  void loadDocuments()
}

function handlePageChange(page: number): void {
  query.pageNum = page
  void loadDocuments()
}

function handleSizeChange(size: number): void {
  query.pageSize = size
  query.pageNum = 1
  void loadDocuments()
}

function handleUploaded(): void {
  query.pageNum = 1
  void loadDocuments()
}

async function handleDelete(document: LearningDocument): Promise<void> {
  await ElMessageBox.confirm(`确认删除文档「${document.title}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
  })

  deletingId.value = document.id

  try {
    await deleteDocumentApi(document.id)
    ElMessage.success('文档已删除')

    if (documents.value.length === 1 && query.pageNum > 1) {
      query.pageNum -= 1
    }

    await loadDocuments()
  } finally {
    deletingId.value = null
  }
}

async function handleParse(document: LearningDocument): Promise<void> {
  parsingId.value = document.id

  try {
    await parseDocumentApi(document.id)
    ElMessage.success('已触发文档解析，正在刷新状态')
    await loadDocuments()
  } catch (error) {
    if (axios.isAxiosError(error) && error.code === 'ECONNABORTED') {
      documents.value = documents.value.map((item) =>
        item.id === document.id
          ? {
              ...item,
              parseStatus: 'PROCESSING',
            }
          : item,
      )

      startPolling()
      void loadDocuments(false)
      return
    }
    return
  } finally {
    parsingId.value = null
  }
}

function openPracticeDialog(document: LearningDocument): void {
  currentPracticeDocument.value = document
  practiceDialogVisible.value = true
}

async function handleGeneratePractice(payload: { questionCount: number; title?: string }): Promise<void> {
  if (!currentPracticeDocument.value) {
    return
  }

  practiceSubmitting.value = true

  try {
    const result = await generatePracticeApi({
      documentId: currentPracticeDocument.value.id,
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

function goToDetail(id: number): void {
  void router.push(`/documents/${id}`)
}

function goToChat(id: number): void {
  void router.push(`/documents/${id}/chat`)
}

void loadDocuments()

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<template>
  <PageContainer
    title="文档管理"
    description="上传学习文档、分页检索、触发解析，并从当前文档直接进入问答或练习。"
  >
    <template #actions>
      <el-button type="primary" :icon="UploadFilled" @click="uploadVisible = true">
        上传文档
      </el-button>
    </template>

    <el-card class="document-toolbar app-card" shadow="never">
      <el-form inline>
        <el-form-item label="标题搜索">
          <el-input
            v-model="query.title"
            placeholder="请输入文档标题关键词"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="document-table app-card" shadow="never">
      <el-table v-loading="loading" :data="documents" stripe>
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="fileName" label="文件名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="fileType" label="文件类型" width="110" />
        <el-table-column label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="解析状态" width="130">
          <template #default="{ row }">
            <el-tag :type="getParseStatusConfig(row.parseStatus).type" round>
              {{ getParseStatusConfig(row.parseStatus).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="摘要状态" width="130">
          <template #default="{ row }">
            <el-tag :type="getSummaryStatusConfig(row.summaryStatus).type" round>
              {{ getSummaryStatusConfig(row.summaryStatus).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="360" fixed="right">
          <template #default="{ row }">
            <div class="document-actions">
              <el-button link type="primary" @click="goToDetail(row.id)">详情</el-button>
              <el-button link type="primary" @click="goToDetail(row.id)">查看摘要</el-button>
              <el-button
                link
                type="warning"
                :loading="parsingId === row.id"
                @click="handleParse(row)"
              >
                解析文档
              </el-button>
              <el-button link type="success" @click="goToChat(row.id)">开始问答</el-button>
              <el-button link type="primary" @click="openPracticeDialog(row)">生成练习</el-button>
              <el-button
                link
                type="danger"
                :loading="deletingId === row.id"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
        <template #empty>
          <EmptyState title="暂无文档" description="上传第一份文档后，这里会展示分页列表。">
            <template #action>
              <el-button type="primary" @click="uploadVisible = true">上传文档</el-button>
            </template>
          </EmptyState>
        </template>
      </el-table>

      <div class="document-pagination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :current-page="query.pageNum"
          :page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <DocumentUploadDialog v-model="uploadVisible" @uploaded="handleUploaded" />
    <PracticeGenerateDialog
      v-model="practiceDialogVisible"
      :loading="practiceSubmitting"
      @confirm="handleGeneratePractice"
    />
  </PageContainer>
</template>

<style scoped>
.document-toolbar,
.document-table {
  border-radius: 24px;
}

.document-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 8px;
}

.document-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .document-pagination {
    justify-content: center;
  }
}
</style>
