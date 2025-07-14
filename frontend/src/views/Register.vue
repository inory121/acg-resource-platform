<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" @keyup.enter="onRegister">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" autocomplete="email" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onRegister" style="width:100%">注册</el-button>
        </el-form-item>
        <el-form-item>
          <span>已有账号？<el-link type="primary" @click="goLogin">去登录</el-link></span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const formRef = ref();
const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  nickname: '',
});
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== form.value.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
  nickname: [{ required: false }],
};

async function onRegister() {
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    loading.value = true;
    try {
      await userStore.registerAction(form.value);
      ElMessage.success('注册成功，请登录');
      router.push('/login');
    } catch (e: any) {
      ElMessage.error(e?.response?.data?.message || '注册失败');
    } finally {
      loading.value = false;
    }
  });
}

function goLogin() {
  router.push('/login');
}
</script>

<style scoped>
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: var(--el-bg-color);
}
.register-card {
  width: 380px;
  padding: 32px 24px 16px 24px;
}
.title {
  text-align: center;
  margin-bottom: 24px;
}
:deep(.el-form-item__label) {
  font-size: 15px;
  white-space: nowrap;
}
</style>