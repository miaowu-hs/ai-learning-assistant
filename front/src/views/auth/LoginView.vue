<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { useAuthStore } from '@/stores/auth'

interface LoginFormModel {
  username: string
  password: string
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const formModel = reactive<LoginFormModel>({
  username: '',
  password: '',
})

const rules: FormRules<LoginFormModel> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度需在 3 到 50 个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度需在 6 到 50 个字符之间', trigger: 'blur' },
  ],
}

const redirectPath = computed(() =>
  typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard',
)

async function handleSubmit(): Promise<void> {
  await formRef.value?.validate()
  submitting.value = true

  try {
    await authStore.login({
      username: formModel.username.trim(),
      password: formModel.password,
    })
    ElMessage.success('登录成功')
    await router.replace(redirectPath.value)
  } catch {
    // 错误提示已由请求层统一处理，这里阻止未捕获异常继续冒泡到 Vue 事件系统。
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-page__panel app-card">
      <section class="auth-page__hero">
        <span class="auth-page__eyebrow">AI Learning Assistant</span>
        <h1>让文档学习进入问答与练习闭环</h1>
        <p>
          上传资料、解析摘要、基于文档提问、生成练习，并持续跟踪错题与学习建议。
        </p>
        <div class="auth-page__highlights">
          <div>
            <strong>RAG 问答</strong>
            <span>围绕当前文档回答问题并返回引用片段。</span>
          </div>
          <div>
            <strong>智能练习</strong>
            <span>从文档直接生成习题并查看判题结果。</span>
          </div>
          <div>
            <strong>错题复练</strong>
            <span>按文档维度重练错题，形成持续迭代。</span>
          </div>
        </div>
      </section>

      <section class="auth-form">
        <div class="auth-form__header">
          <h2>登录</h2>
          <p>使用你的账号进入学习空间。</p>
        </div>

        <el-form ref="formRef" :model="formModel" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formModel.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="formModel.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
              @keyup.enter="handleSubmit"
            />
          </el-form-item>
          <el-button type="primary" class="auth-form__submit" :loading="submitting" @click="handleSubmit">
            登录并进入系统
          </el-button>
        </el-form>

        <p class="auth-form__footer">
          还没有账号？
          <router-link to="/register">去注册</router-link>
        </p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 32px;
}

.auth-page__panel {
  width: min(1120px, 100%);
  display: grid;
  grid-template-columns: 1.2fr minmax(360px, 420px);
  overflow: hidden;
  border-radius: 32px;
}

.auth-page__hero {
  position: relative;
  padding: 56px;
  color: #eff6ff;
  background:
    radial-gradient(circle at top left, rgba(96, 165, 250, 0.45), transparent 28%),
    linear-gradient(160deg, #10203b 0%, #17335f 55%, #1d4ed8 100%);
}

.auth-page__eyebrow {
  display: inline-block;
  margin-bottom: 18px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(191, 219, 254, 0.82);
}

.auth-page__hero h1 {
  margin: 0;
  font-size: 42px;
  line-height: 1.15;
}

.auth-page__hero p {
  margin: 18px 0 0;
  max-width: 520px;
  line-height: 1.8;
  color: rgba(219, 234, 254, 0.82);
}

.auth-page__highlights {
  display: grid;
  gap: 14px;
  margin-top: 36px;
}

.auth-page__highlights div {
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(15, 23, 42, 0.22);
  border: 1px solid rgba(191, 219, 254, 0.16);
}

.auth-page__highlights strong,
.auth-page__highlights span {
  display: block;
}

.auth-page__highlights strong {
  color: #f8fbff;
}

.auth-page__highlights span {
  margin-top: 6px;
  font-size: 14px;
  line-height: 1.7;
  color: rgba(219, 234, 254, 0.78);
}

.auth-form {
  padding: 56px 40px;
  background: rgba(255, 255, 255, 0.96);
}

.auth-form__header h2 {
  margin: 0;
  font-size: 30px;
  color: var(--color-secondary);
}

.auth-form__header p {
  margin: 10px 0 0;
  color: var(--color-text-secondary);
}

.auth-form__submit {
  width: 100%;
  margin-top: 12px;
  height: 44px;
}

.auth-form__footer {
  margin: 24px 0 0;
  color: var(--color-text-secondary);
}

.auth-form__footer a {
  color: var(--color-primary);
  font-weight: 600;
}

@media (max-width: 960px) {
  .auth-page {
    padding: 16px;
  }

  .auth-page__panel {
    grid-template-columns: 1fr;
  }

  .auth-page__hero,
  .auth-form {
    padding: 28px 24px;
  }

  .auth-page__hero h1 {
    font-size: 32px;
  }
}
</style>
