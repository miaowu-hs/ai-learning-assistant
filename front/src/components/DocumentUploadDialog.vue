<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadInstance, type UploadProps, type UploadRawFile } from 'element-plus'

import { uploadDocumentApi } from '@/api/document'
import type { LearningDocument } from '@/types/document'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  uploaded: [document: LearningDocument]
}>()

const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const submitting = ref(false)
const selectedFile = ref<UploadRawFile | null>(null)

const formModel = reactive({
  title: '',
})

const rules: FormRules<typeof formModel> = {
  title: [
    {
      max: 100,
      message: '标题长度不能超过 100 个字符',
      trigger: 'blur',
    },
  ],
}

const handleFileChange: UploadProps['onChange'] = (file) => {
  selectedFile.value = file.raw ?? null
}

const handleDialogClose = (): void => {
  emit('update:modelValue', false)
}

function resetState(): void {
  formModel.title = ''
  selectedFile.value = null
  formRef.value?.clearValidate()
  uploadRef.value?.clearFiles()
}

async function handleSubmit(): Promise<void> {
  await formRef.value?.validate()

  if (!selectedFile.value) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  if (formModel.title.trim()) {
    formData.append('title', formModel.title.trim())
  }

  submitting.value = true

  try {
    const document = await uploadDocumentApi(formData)
    ElMessage.success('文档上传成功')
    emit('uploaded', document)
    handleDialogClose()
  } finally {
    submitting.value = false
  }
}

watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) {
      resetState()
    }
  },
)
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    title="上传学习文档"
    width="560px"
    destroy-on-close
    @close="handleDialogClose"
  >
    <el-form ref="formRef" :model="formModel" :rules="rules" label-position="top">
      <el-form-item label="文档标题" prop="title">
        <el-input
          v-model="formModel.title"
          maxlength="100"
          placeholder="可选，不填则使用后端默认标题策略"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="文档文件" required>
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          accept=".pdf,.docx,.txt,.md"
          :on-change="handleFileChange"
        >
          <el-icon :size="30">
            <UploadFilled />
          </el-icon>
          <div class="el-upload__text">拖拽文件到此处，或点击选择文件</div>
          <template #tip>
            <div class="upload-tip">支持 pdf、docx、txt、md 格式。</div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          立即上传
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.upload-tip {
  color: var(--color-text-secondary);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
