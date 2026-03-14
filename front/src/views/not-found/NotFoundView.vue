<template>
  <div class="not-found">
    <el-result
      icon="warning"
      title="页面不存在"
      sub-title="请检查访问路径，或返回学习概览页继续操作。"
    >
      <template #extra>
        <router-link to="/dashboard">
          <el-button type="primary">返回学习概览</el-button>
        </router-link>
      </template>
    </el-result>
  </div>
</template>

<style scoped>
.not-found {
  min-height: calc(100vh - 48px);
  display: grid;
  place-items: center;
}
</style>
