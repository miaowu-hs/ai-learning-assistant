<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { useAuthStore } from '@/stores/auth'

interface RegisterFormModel {
  username: string
  password: string
  confirmPassword: string
  nickname: string
  email: string
  phone: string
}

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)

const formModel = reactive<RegisterFormModel>({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  phone: '',
})

const rules: FormRules<RegisterFormModel> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度需在 3 到 50 个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 50, message: '密码长度需在 6 到 50 个字符之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_, value: string, callback) => {
        if (value !== formModel.password) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过 50 个字符', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] },
  ],
  phone: [
    { max: 20, message: '手机号长度不能超过 20 个字符', trigger: 'blur' },
  ],
}

async function handleSubmit(): Promise<void> {
  await formRef.value?.validate()
  submitting.value = true

  try {
    await authStore.register({
      username: formModel.username.trim(),
      password: formModel.password,
      nickname: formModel.nickname.trim() || undefined,
      email: formModel.email.trim() || undefined,
      phone: formModel.phone.trim() || undefined,
    })
    ElMessage.success('注册成功，请登录')
    await router.push('/login')
  } catch {
    // 错误提示已由请求层统一处理，这里阻止未捕获异常继续冒泡到 Vue 事件系统。
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-page__panel app-card">
      <section class="register-page__intro">
        <span class="register-page__eyebrow">Start Learning</span>
        <h1>创建你的 AI 学习助手账号</h1>
        <p>完成注册后即可上传学习资料、提问、出题、查看错题与学习报告。</p>
        <ul class="register-page__list">
          <li>统一对接 Spring Boot 后端接口</li>
          <li>登录后自动读取当前用户和鉴权状态</li>
          <li>支持文档驱动的摘要、问答和练习流程</li>
        </ul>
      </section>

      <section class="register-form">
        <div class="register-form__header">
          <h2>注册</h2>
          <p>以下字段均按后端注册接口契约提交。</p>
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
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="formModel.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
              clearable
              @keyup.enter="handleSubmit"
            />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="formModel.nickname" placeholder="可选" clearable />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formModel.email" placeholder="可选" clearable />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="formModel.phone" placeholder="可选" clearable />
          </el-form-item>
          <el-button type="primary" class="register-form__submit" :loading="submitting" @click="handleSubmit">
            注册账号
          </el-button>
        </el-form>

        <p class="register-form__footer">
          已有账号？
          <router-link to="/login">返回登录</router-link>
        </p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 32px;
}

.register-page__panel {
  width: min(1080px, 100%);
  display: grid;
  grid-template-columns: minmax(320px, 1fr) minmax(360px, 460px);
  overflow: hidden;
  border-radius: 32px;
}

.register-page__intro {
  padding: 48px;
  color: #eff6ff;
  background:
    radial-gradient(circle at bottom right, rgba(96, 165, 250, 0.32), transparent 30%),
    linear-gradient(155deg, #0f172a 0%, #10203b 45%, #1e3a8a 100%);
}

.register-page__eyebrow {
  display: inline-block;
  margin-bottom: 18px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: rgba(191, 219, 254, 0.84);
}

.register-page__intro h1 {
  margin: 0;
  font-size: 38px;
  line-height: 1.18;
}

.register-page__intro p {
  margin: 16px 0 0;
  max-width: 440px;
  line-height: 1.8;
  color: rgba(219, 234, 254, 0.82);
}

.register-page__list {
  margin: 28px 0 0;
  padding-left: 18px;
  color: rgba(219, 234, 254, 0.82);
  line-height: 1.9;
}

.register-form {
  padding: 40px 36px;
  background: rgba(255, 255, 255, 0.96);
}

.register-form__header h2 {
  margin: 0;
  font-size: 28px;
  color: var(--color-secondary);
}

.register-form__header p {
  margin: 10px 0 0;
  color: var(--color-text-secondary);
}

.register-form__submit {
  width: 100%;
  height: 44px;
  margin-top: 12px;
}

.register-form__footer {
  margin: 20px 0 0;
  color: var(--color-text-secondary);
}

.register-form__footer a {
  color: var(--color-primary);
  font-weight: 600;
}

@media (max-width: 960px) {
  .register-page {
    padding: 16px;
  }

  .register-page__panel {
    grid-template-columns: 1fr;
  }

  .register-page__intro,
  .register-form {
    padding: 28px 24px;
  }

  .register-page__intro h1 {
    font-size: 30px;
  }
}
</style>
