<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getDocumentDetailApi } from '@/api/document'
import { deleteWrongQuestionApi, getWrongbookApi, regenerateWrongPracticeApi } from '@/api/wrongbook'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { WrongQuestion } from '@/types/wrongbook'
import { formatDateTime, getQuestionTypeLabel } from '@/utils/format'
import { extractPracticeSetId } from '@/utils/normalize'

const router = useRouter()

const loading = ref(false)
const deletingId = ref<number | null>(null)
const regeneratingDocumentId = ref<number | null>(null)
const wrongQuestions = ref<WrongQuestion[]>([])
const documentTitleMap = reactive<Record<number, string>>({})

const groupedWrongQuestions = computed(() => {
  const groups = new Map<number, WrongQuestion[]>()

  wrongQuestions.value.forEach((item) => {
    const collection = groups.get(item.documentId) ?? []
    collection.push(item)
    groups.set(item.documentId, collection)
  })

  return Array.from(groups.entries())
    .map(([documentId, items]) => ({
      documentId,
      title: documentTitleMap[documentId] || `文档 #${documentId}`,
      items: [...items].sort((a, b) => b.updatedAt.localeCompare(a.updatedAt)),
    }))
    .sort((a, b) => b.items.length - a.items.length)
})

async function loadDocumentTitles(documentIds: number[]): Promise<void> {
  const uniqueIds = Array.from(new Set(documentIds)).filter((id) => !documentTitleMap[id])

  await Promise.allSettled(
    uniqueIds.map(async (id) => {
      try {
        const document = await getDocumentDetailApi(id)
        documentTitleMap[id] = document.title
      } catch {
        documentTitleMap[id] = `文档 #${id}`
      }
    }),
  )
}

async function loadWrongbook(): Promise<void> {
  loading.value = true

  try {
    const data = await getWrongbookApi()
    wrongQuestions.value = data
    await loadDocumentTitles(data.map((item) => item.documentId))
  } finally {
    loading.value = false
  }
}

async function handleDelete(item: WrongQuestion): Promise<void> {
  await ElMessageBox.confirm('确认删除这条错题记录吗？', '删除确认', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
  })

  deletingId.value = item.id

  try {
    await deleteWrongQuestionApi(item.id)
    ElMessage.success('错题已删除')
    await loadWrongbook()
  } finally {
    deletingId.value = null
  }
}

async function handleRegenerate(documentId: number, items: WrongQuestion[]): Promise<void> {
  await ElMessageBox.confirm('将按当前文档下的错题重新生成练习，是否继续？', '重新生成练习', {
    type: 'warning',
    confirmButtonText: '继续生成',
    cancelButtonText: '取消',
  })

  regeneratingDocumentId.value = documentId

  try {
    const result = await regenerateWrongPracticeApi({
      documentId,
      // 契约仅给出 wrongQuestionIds 字段名，未进一步说明映射 questionId 还是错题记录 id。
      // 这里按错题本记录 id 提交；若后端期望原题 questionId，请将 item.id 改为 item.questionId。
      wrongQuestionIds: items.map((item) => item.id),
    })
    const practiceSetId = extractPracticeSetId(result)

    if (!practiceSetId) {
      throw new Error('后端未返回练习集 ID，无法进入作答页')
    }

    ElMessage.success('已生成错题重练')
    await router.push(`/practices/${practiceSetId}`)
  } finally {
    regeneratingDocumentId.value = null
  }
}

void loadWrongbook()
</script>

<template>
  <PageContainer
    title="错题本"
    description="查看错题记录、按文档维度重新生成练习，并清理无效错题。"
  >
    <el-skeleton :loading="loading" animated :rows="10">
      <template v-if="groupedWrongQuestions.length">
        <div class="wrongbook-list">
          <el-card
            v-for="group in groupedWrongQuestions"
            :key="group.documentId"
            class="wrongbook-card app-card"
            shadow="never"
          >
            <template #header>
              <div class="wrongbook-card__header">
                <div>
                  <h3>{{ group.title }}</h3>
                  <p>文档 ID：{{ group.documentId }}，共 {{ group.items.length }} 道错题</p>
                </div>
                <el-button
                  type="primary"
                  :loading="regeneratingDocumentId === group.documentId"
                  @click="handleRegenerate(group.documentId, group.items)"
                >
                  重新生成练习
                </el-button>
              </div>
            </template>

            <el-table :data="group.items" stripe>
              <el-table-column type="expand">
                <template #default="{ row }">
                  <div class="wrongbook-expand">
                    <div class="wrongbook-expand__block">
                      <span>正确答案</span>
                      <p>{{ row.correctAnswer || '--' }}</p>
                    </div>
                    <div class="wrongbook-expand__block">
                      <span>题目解析</span>
                      <p>{{ row.explanation || '暂无解析' }}</p>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="题型" width="120">
                <template #default="{ row }">
                  {{ getQuestionTypeLabel(row.questionType) }}
                </template>
              </el-table-column>
              <el-table-column prop="stem" label="题干" min-width="280" show-overflow-tooltip />
              <el-table-column prop="knowledgePoint" label="知识点" min-width="160" />
              <el-table-column prop="wrongCount" label="错误次数" width="100" />
              <el-table-column prop="lastUserAnswer" label="上次答案" min-width="120" />
              <el-table-column label="更新时间" width="180">
                <template #default="{ row }">
                  {{ formatDateTime(row.updatedAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="110" fixed="right">
                <template #default="{ row }">
                  <el-button
                    link
                    type="danger"
                    :loading="deletingId === row.id"
                    @click="handleDelete(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </template>
      <EmptyState v-else title="暂无错题" description="完成练习后，错题会自动沉淀在这里。" />
    </el-skeleton>
  </PageContainer>
</template>

<style scoped>
.wrongbook-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.wrongbook-card {
  border-radius: 24px;
}

.wrongbook-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.wrongbook-card__header h3 {
  margin: 0;
  color: var(--color-secondary);
}

.wrongbook-card__header p {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
}

.wrongbook-expand {
  display: grid;
  gap: 12px;
  padding: 8px 12px;
}

.wrongbook-expand__block {
  padding: 14px 16px;
  border-radius: 16px;
  background: var(--color-surface-muted);
}

.wrongbook-expand__block span {
  display: block;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.wrongbook-expand__block p {
  margin: 8px 0 0;
  line-height: 1.75;
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .wrongbook-card__header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
