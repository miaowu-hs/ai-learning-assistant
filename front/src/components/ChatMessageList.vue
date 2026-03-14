<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'

import type { ChatMessageItem } from '@/types/qa'
import { formatDateTime } from '@/utils/format'

import EmptyState from './EmptyState.vue'

const props = defineProps<{
  messages: ChatMessageItem[]
  loading: boolean
}>()

const containerRef = ref<HTMLElement>()

async function scrollToBottom(): Promise<void> {
  await nextTick()

  if (containerRef.value) {
    containerRef.value.scrollTop = containerRef.value.scrollHeight
  }
}

watch(
  () => props.messages.length,
  () => {
    void scrollToBottom()
  },
)
</script>

<template>
  <div ref="containerRef" class="chat-message-list app-card">
    <template v-if="messages.length">
      <div
        v-for="message in messages"
        :key="message.id"
        class="chat-message"
        :class="message.role === 'USER' ? 'chat-message--user' : 'chat-message--assistant'"
      >
        <div class="chat-message__bubble">
          <div class="chat-message__meta">
            <span>{{ message.role === 'USER' ? '你' : 'AI 助手' }}</span>
            <time v-if="message.createdAt">{{ formatDateTime(message.createdAt) }}</time>
          </div>
          <p>{{ message.content }}</p>
          <div
            v-if="message.role === 'ASSISTANT' && message.references.length"
            class="chat-message__references"
          >
            <h4>引用片段</h4>
            <div
              v-for="reference in message.references"
              :key="reference.chunkId"
              class="chat-message__reference"
            >
              <div class="chat-message__reference-meta">
                <span>片段 #{{ reference.chunkIndex }}</span>
                <span>相关度 {{ reference.score }}</span>
              </div>
              <p>{{ reference.content }}</p>
            </div>
          </div>
        </div>
      </div>
      <div v-if="loading" class="chat-message chat-message--assistant">
        <div class="chat-message__bubble chat-message__bubble--loading">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <span>AI 正在生成回答...</span>
        </div>
      </div>
    </template>
    <EmptyState
      v-else
      title="开始首轮提问"
      description="先创建问答会话，再基于当前文档提问。"
    />
  </div>
</template>

<style scoped>
.chat-message-list {
  height: 620px;
  overflow-y: auto;
  border-radius: 28px;
  padding: 24px;
}

.chat-message {
  display: flex;
  margin-bottom: 18px;
}

.chat-message--user {
  justify-content: flex-end;
}

.chat-message__bubble {
  max-width: min(760px, 100%);
  padding: 18px 18px 16px;
  border-radius: 22px;
  background: #ffffff;
  border: 1px solid var(--color-border);
}

.chat-message--user .chat-message__bubble {
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.92), rgba(29, 78, 216, 0.96));
  border-color: transparent;
  color: #eff6ff;
}

.chat-message__bubble p {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.75;
}

.chat-message__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.chat-message--user .chat-message__meta {
  color: rgba(239, 246, 255, 0.8);
}

.chat-message__references {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed var(--color-border);
}

.chat-message__references h4 {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--color-secondary);
}

.chat-message__reference + .chat-message__reference {
  margin-top: 10px;
}

.chat-message__reference {
  padding: 12px;
  border-radius: 16px;
  background: var(--color-surface-muted);
}

.chat-message__reference-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.chat-message__bubble--loading {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .chat-message-list {
    height: auto;
    min-height: 420px;
    padding: 16px;
  }

  .chat-message__bubble {
    max-width: 100%;
  }
}
</style>
