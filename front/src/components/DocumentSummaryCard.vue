<script setup lang="ts">
import { computed } from 'vue'

import type {
  DocumentParseStatus,
  DocumentSummary,
  DocumentSummaryStatus,
} from '@/types/document'
import { getParseStatusConfig, getSummaryStatusConfig } from '@/utils/format'

import EmptyState from './EmptyState.vue'

const props = defineProps<{
  summary: DocumentSummary | null
  loading: boolean
  parseStatus: DocumentParseStatus
  summaryStatus: DocumentSummaryStatus
}>()

const parseTag = computed(() => getParseStatusConfig(props.parseStatus))
const summaryTag = computed(() => getSummaryStatusConfig(props.summaryStatus))
</script>

<template>
  <el-card class="summary-card app-card" shadow="never">
    <template #header>
      <div class="summary-card__header">
        <div>
          <h3>文档摘要</h3>
          <p>结构化查看摘要、提纲与关键知识点。</p>
        </div>
        <div class="summary-card__tags">
          <el-tag :type="parseTag.type" round>{{ parseTag.label }}</el-tag>
          <el-tag :type="summaryTag.type" round>{{ summaryTag.label }}</el-tag>
        </div>
      </div>
    </template>

    <el-skeleton :loading="loading" animated :rows="8">
      <template v-if="summary">
        <div class="summary-card__section">
          <h4>摘要</h4>
          <p>{{ summary.shortSummary || '暂无摘要内容' }}</p>
        </div>
        <div class="summary-card__section">
          <h4>知识提纲</h4>
          <ul>
            <li v-for="(item, index) in summary.outline" :key="`${item}-${index}`">{{ item }}</li>
          </ul>
        </div>
        <div class="summary-card__section">
          <h4>关键点</h4>
          <div class="summary-card__points">
            <el-tag
              v-for="(item, index) in summary.keyPoints"
              :key="`${item}-${index}`"
              round
              effect="plain"
            >
              {{ item }}
            </el-tag>
          </div>
        </div>
      </template>
      <EmptyState
        v-else-if="parseStatus !== 'COMPLETED'"
        title="文档尚未解析完成"
        description="请先触发解析，摘要会在后端处理完成后显示。"
      />
      <EmptyState
        v-else
        title="摘要暂不可用"
        description="当前摘要未生成完成，或后端尚未返回摘要内容。"
      />
    </el-skeleton>
  </el-card>
</template>

<style scoped>
.summary-card {
  border-radius: 24px;
}

.summary-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.summary-card__header h3,
.summary-card__section h4 {
  margin: 0;
  color: var(--color-secondary);
}

.summary-card__header p {
  margin: 6px 0 0;
  color: var(--color-text-secondary);
}

.summary-card__tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.summary-card__section + .summary-card__section {
  margin-top: 24px;
}

.summary-card__section p,
.summary-card__section ul {
  margin: 12px 0 0;
  color: var(--color-text-secondary);
}

.summary-card__section ul {
  padding-left: 18px;
}

.summary-card__points {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .summary-card__header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
