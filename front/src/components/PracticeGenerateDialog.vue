<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
  loading: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: [payload: { questionCount: number; title?: string }]
}>()

const formRef = ref<FormInstance>()

const formModel = reactive({
  questionCount: 6,
  title: '',
})

const rules: FormRules<typeof formModel> = {
  questionCount: [
    { required: true, message: '请输入题目数量', trigger: 'change' },
    {
      validator: (_, value: number, callback) => {
        if (!Number.isInteger(value) || value <= 0) {
          callback(new Error('题目数量必须为正整数'))
          return
        }

        callback()
      },
      trigger: 'change',
    },
  ],
  title: [
    { max: 100, message: '练习标题不能超过 100 个字符', trigger: 'blur' },
  ],
}

function handleClose(): void {
  emit('update:modelValue', false)
}

async function handleConfirm(): Promise<void> {
  await formRef.value?.validate()
  emit('confirm', {
    questionCount: formModel.questionCount,
    title: formModel.title.trim() || undefined,
  })
}

watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) {
      formModel.questionCount = 6
      formModel.title = ''
      formRef.value?.clearValidate()
    }
  },
)
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    title="生成练习"
    width="480px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formModel" :rules="rules" label-position="top">
      <el-form-item label="题目数量" prop="questionCount">
        <el-input-number v-model="formModel.questionCount" :min="1" :max="20" />
      </el-form-item>
      <el-form-item label="练习标题" prop="title">
        <el-input
          v-model="formModel.title"
          maxlength="100"
          placeholder="可选，用于区分不同练习"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleConfirm">
          生成练习
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
