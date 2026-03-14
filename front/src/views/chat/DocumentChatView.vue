<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getDocumentDetailApi } from '@/api/document'
import { askQuestionApi, createQaSessionApi, getQaHistoryApi } from '@/api/qa'
import ChatMessageList from '@/components/ChatMessageList.vue'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { LearningDocument } from '@/types/document'
import type { ChatMessageItem, QaMessage } from '@/types/qa'
import { extractQaSessionId } from '@/utils/normalize'

const route = useRoute()
const router = useRouter()

const documentId = computed(() => Number(route.params.id))
const sessionId = ref<number | null>(null)
const documentDetail = ref<LearningDocument | null>(null)
const messages = ref<ChatMessageItem[]>([])

const loading = ref(false)
const sessionLoading = ref(false)
const historyLoading = ref(false)
const asking = ref(false)

const questionForm = reactive({
  question: '',
})

function normalizeMessages(historyMessages: QaMessage[]): ChatMessageItem[] {
  return historyMessages.map((item) => ({
    id: item.id,
    role: item.role,
    content: item.content,
    references: item.references ?? [],
    createdAt: item.createdAt,
  }))
}

async function loadDocumentDetail(): Promise<void> {
  documentDetail.value = await getDocumentDetailApi(documentId.value)
}

async function createSession(): Promise<void> {
  sessionLoading.value = true

  try {
    const payload = await createQaSessionApi({
      documentId: documentId.value,
      title: documentDetail.value?.title ? `${documentDetail.value.title} 问答` : undefined,
    })
    const nextSessionId = extractQaSessionId(payload)

    if (!nextSessionId) {
      throw new Error('后端未返回问答会话 ID，无法发起提问')
    }

    sessionId.value = nextSessionId
    messages.value = []

    await router.replace({
      path: route.path,
      query: {
        sessionId: String(nextSessionId),
      },
    })
    ElMessage.success('已创建新问答会话')
  } finally {
    sessionLoading.value = false
  }
}

async function loadHistory(): Promise<void> {
  if (!sessionId.value) {
    return
  }

  historyLoading.value = true

  try {
    const history = await getQaHistoryApi(sessionId.value)
    messages.value = normalizeMessages(history.messages)
  } finally {
    historyLoading.value = false
  }
}

async function bootstrapSession(): Promise<void> {
  const querySessionId =
    typeof route.query.sessionId === 'string' ? Number(route.query.sessionId) : Number.NaN

  if (Number.isFinite(querySessionId) && querySessionId > 0) {
    sessionId.value = querySessionId
    await loadHistory()
    return
  }

  await createSession()
}

async function handleAsk(): Promise<void> {
  const question = questionForm.question.trim()

  if (!question) {
    ElMessage.warning('请输入问题内容')
    return
  }

  if (!sessionId.value) {
    await createSession()
  }

  if (!sessionId.value) {
    return
  }

  const userMessage: ChatMessageItem = {
    id: `user-${Date.now()}`,
    role: 'USER',
    content: question,
    references: [],
    createdAt: new Date().toISOString(),
  }

  messages.value = [...messages.value, userMessage]
  questionForm.question = ''
  asking.value = true

  try {
    const answer = await askQuestionApi(sessionId.value, { question })
    messages.value = [
      ...messages.value,
      {
        id: `assistant-${answer.answeredAt}-${answer.sessionId}`,
        role: 'ASSISTANT',
        content: answer.answer,
        references: answer.references ?? [],
        createdAt: answer.answeredAt,
      },
    ]
  } finally {
    asking.value = false
  }
}

async function initialize(): Promise<void> {
  loading.value = true

  try {
    await loadDocumentDetail()
    await bootstrapSession()
  } finally {
    loading.value = false
  }
}

void initialize()
</script>

<template>
  <PageContainer
    :title="documentDetail?.title ? `${documentDetail.title} · 文档问答` : '文档问答'"
    description="会话直接绑定当前文档，当前后端未提供问答会话列表接口，因此页面只维护当前会话。"
  >
    <template #actions>
      <el-button @click="$router.push(`/documents/${documentId}`)">返回文档</el-button>
      <el-button :loading="historyLoading" @click="loadHistory">刷新历史</el-button>
      <el-button type="primary" :loading="sessionLoading" @click="createSession">新建会话</el-button>
    </template>

    <el-skeleton :loading="loading" animated :rows="10">
      <template v-if="documentDetail">
        <el-card class="chat-meta app-card" shadow="never">
          <div class="chat-meta__content">
            <div>
              <p class="chat-meta__title">当前文档</p>
              <strong>{{ documentDetail.title }}</strong>
            </div>
            <div>
              <p class="chat-meta__title">会话 ID</p>
              <strong>{{ sessionId || '--' }}</strong>
            </div>
            <div>
              <p class="chat-meta__title">提示</p>
              <span>刷新页面后，如路由 query 中存在 `sessionId`，会自动回显当前会话历史。</span>
            </div>
          </div>
        </el-card>

        <ChatMessageList :messages="messages" :loading="asking" />

        <el-card class="chat-editor app-card" shadow="never">
          <template #header>
            <div class="chat-editor__header">
              <div>
                <h3>提问输入区</h3>
                <p>围绕当前文档内容发问，AI 回答下方会展示引用片段。</p>
              </div>
            </div>
          </template>
          <el-input
            v-model="questionForm.question"
            type="textarea"
            :rows="5"
            maxlength="1000"
            show-word-limit
            resize="vertical"
            placeholder="例如：请总结这份文档中 JWT 的工作流程，并指出常见错误点。"
          />
          <div class="chat-editor__actions">
            <el-button type="primary" :loading="asking" @click="handleAsk">发送问题</el-button>
          </div>
        </el-card>
      </template>
      <EmptyState v-else title="未找到文档" description="请确认文档 ID 是否有效。" />
    </el-skeleton>
  </PageContainer>
</template>

<style scoped>
.chat-meta,
.chat-editor {
  border-radius: 24px;
}

.chat-meta__content {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.chat-meta__content > div {
  padding: 16px 18px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}

.chat-meta__title {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.chat-meta strong {
  display: block;
  color: var(--color-secondary);
}

.chat-meta span {
  display: block;
  line-height: 1.75;
  color: var(--color-text-secondary);
}

.chat-editor__header h3 {
  margin: 0;
  color: var(--color-secondary);
}

.chat-editor__header p {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
}

.chat-editor__actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 960px) {
  .chat-meta__content {
    grid-template-columns: 1fr;
  }
}
</style>
