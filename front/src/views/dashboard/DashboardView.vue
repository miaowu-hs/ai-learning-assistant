<script setup lang="ts">
import { computed, ref } from 'vue'

import { getLearningReportApi } from '@/api/report'
import EmptyState from '@/components/EmptyState.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { LearningReport } from '@/types/report'
import { formatPercent } from '@/utils/format'

const loading = ref(false)
const loadFailed = ref(false)
const reportReturnedEmpty = ref(false)

function createEmptyReport(): LearningReport {
  return {
    documentCount: 0,
    qaCount: 0,
    practiceCount: 0,
    accuracy: 0,
    recentWrongKnowledgePoints: [],
    suggestions: [],
  }
}

function normalizeReport(payload: Partial<LearningReport> | null | undefined): LearningReport {
  if (!payload) {
    reportReturnedEmpty.value = true
    return createEmptyReport()
  }

  return {
    documentCount: payload.documentCount ?? 0,
    qaCount: payload.qaCount ?? 0,
    practiceCount: payload.practiceCount ?? 0,
    accuracy: payload.accuracy ?? 0,
    recentWrongKnowledgePoints: payload.recentWrongKnowledgePoints ?? [],
    suggestions: payload.suggestions ?? [],
  }
}

const report = ref<LearningReport>(createEmptyReport())

const summaryCards = computed(() => {
  const current = report.value

  return [
    {
      title: '文档数量',
      value: current?.documentCount ?? 0,
      icon: 'Reading',
      description: '已纳入学习流程的文档总数',
    },
    {
      title: '问答次数',
      value: current?.qaCount ?? 0,
      icon: 'ChatDotRound',
      description: '累计完成的文档问答次数',
    },
    {
      title: '练习次数',
      value: current?.practiceCount ?? 0,
      icon: 'EditPen',
      description: '已完成或已提交的练习次数',
    },
    {
      title: '正确率',
      value: current ? formatPercent(current.accuracy) : '0%',
      icon: 'TrophyBase',
      description: '基于学习报告的总体正确率',
    },
  ]
})

async function loadReport(): Promise<void> {
  loading.value = true
  loadFailed.value = false
  reportReturnedEmpty.value = false

  try {
    const payload = await getLearningReportApi()
    report.value = normalizeReport(payload)
  } catch {
    loadFailed.value = true
    report.value = createEmptyReport()
  } finally {
    loading.value = false
  }
}

void loadReport()
</script>

<template>
  <PageContainer
    title="学习概览"
    description="从报告接口统一查看当前文档学习进度、问答频次、练习表现与近期建议。"
  >
    <el-skeleton :loading="loading" animated :rows="8">
      <template v-if="!loadFailed">
        <el-alert
          v-if="reportReturnedEmpty"
          title="报告接口已调用成功，但后端返回了空数据，当前页面以默认值展示。"
          type="info"
          :closable="false"
          show-icon
        />
        <div class="dashboard-grid dashboard-grid--stats">
          <el-card
            v-for="card in summaryCards"
            :key="card.title"
            class="dashboard-card dashboard-card--stat app-card"
            shadow="never"
          >
            <div class="dashboard-card__icon">
              <el-icon :size="22">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="dashboard-card__content">
              <span>{{ card.title }}</span>
              <strong>{{ card.value }}</strong>
              <p>{{ card.description }}</p>
            </div>
          </el-card>
        </div>

        <div class="dashboard-grid dashboard-grid--main">
          <el-card class="dashboard-card app-card" shadow="never">
            <template #header>
              <div class="dashboard-card__header">
                <div>
                  <h3>正确率趋势</h3>
                  <p>学习报告当前返回整体准确率，适合快速掌握阶段状态。</p>
                </div>
                <span class="dashboard-card__value">{{ formatPercent(report.accuracy) }}</span>
              </div>
            </template>
            <div class="dashboard-progress">
              <el-progress
                :percentage="Number((report.accuracy * 100).toFixed(0))"
                :stroke-width="18"
                status="success"
              />
            </div>
          </el-card>

          <el-card class="dashboard-card app-card" shadow="never">
            <template #header>
              <div class="dashboard-card__header">
                <div>
                  <h3>AI 学习建议</h3>
                </div>
              </div>
            </template>
            <div v-if="report.suggestions.length" class="dashboard-suggestions">
              <div
                v-for="(item, index) in report.suggestions"
                :key="`${item}-${index}`"
                class="dashboard-suggestions__item"
              >
                <span>{{ index + 1 }}</span>
                <p>{{ item }}</p>
              </div>
            </div>
            <EmptyState v-else title="暂无学习建议" description="当前报告未返回建议内容。" />
          </el-card>
        </div>

        <el-card class="dashboard-card app-card" shadow="never">
          <template #header>
            <div class="dashboard-card__header">
              <div>
                <h3>最近错题知识点</h3>
                <p>聚合展示近期高频出错的知识点。</p>
              </div>
            </div>
          </template>

          <el-table
            v-if="report.recentWrongKnowledgePoints.length"
            :data="report.recentWrongKnowledgePoints"
            stripe
          >
            <el-table-column prop="knowledgePoint" label="知识点" min-width="220" />
            <el-table-column prop="count" label="错题次数" width="120" />
            <el-table-column label="风险等级" width="160">
              <template #default="{ row }">
                <el-tag :type="row.count >= 3 ? 'danger' : row.count === 2 ? 'warning' : 'info'" round>
                  {{ row.count >= 3 ? '重点复习' : row.count === 2 ? '建议复习' : '轻微波动' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <EmptyState v-else title="暂无错题知识点" description="继续练习后，这里会展示近期错误聚焦点。" />
        </el-card>
      </template>
      <EmptyState
        v-else
        title="学习报告加载失败"
        description="报告接口请求未成功返回，当前无法展示学习概览。"
      />
    </el-skeleton>
  </PageContainer>
</template>

<style scoped>
.dashboard-grid {
  display: grid;
  gap: 20px;
}

.dashboard-grid--stats {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.dashboard-grid--main {
  grid-template-columns: 1.1fr 0.9fr;
}

.dashboard-card {
  border-radius: 26px;
}

.dashboard-card--stat {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.dashboard-card__icon {
  width: 52px;
  height: 52px;
  display: grid;
  place-items: center;
  border-radius: 18px;
  color: var(--color-primary);
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.16), rgba(37, 99, 235, 0.06));
}

.dashboard-card__content span,
.dashboard-card__content p,
.dashboard-card__header p {
  color: var(--color-text-secondary);
}

.dashboard-card__content span {
  display: block;
  font-size: 14px;
}

.dashboard-card__content strong {
  display: block;
  margin-top: 10px;
  font-size: 34px;
  color: var(--color-secondary);
}

.dashboard-card__content p {
  margin: 10px 0 0;
  line-height: 1.7;
}

.dashboard-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.dashboard-card__header h3 {
  margin: 0;
  color: var(--color-secondary);
}

.dashboard-card__header p {
  margin: 8px 0 0;
}

.dashboard-card__value {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-primary);
}

.dashboard-progress {
  padding: 14px 4px;
}

.dashboard-suggestions {
  display: grid;
  gap: 14px;
}

.dashboard-suggestions__item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 18px;
  border-radius: 18px;
  background: var(--color-surface-muted);
}

.dashboard-suggestions__item span {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  color: var(--color-primary);
  background: rgba(37, 99, 235, 0.12);
}

.dashboard-suggestions__item p {
  margin: 0;
  line-height: 1.75;
  color: var(--color-text-secondary);
}

@media (max-width: 1200px) {
  .dashboard-grid--stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 960px) {
  .dashboard-grid--stats,
  .dashboard-grid--main {
    grid-template-columns: 1fr;
  }
}
</style>
