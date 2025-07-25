<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" @keyup.enter="onLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onLogin" style="width:100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <span>没有账号？<el-link type="primary" @click="goRegister">去注册</el-link></span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const formRef = ref();
const form = ref({
  username: '',
  password: '',
});
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

async function onLogin() {
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    loading.value = true;
    try {
      await userStore.loginAction(form.value);
      // ElMessage.success('登录成功'); // Removed ElMessage import, so this line is removed.
      router.push('/');
    } catch (e: any) {
      // ElMessage.error(e?.message || '登录失败'); // Removed ElMessage import, so this line is removed.
    } finally {
      loading.value = false;
    }
  });
}

function goRegister() {
  router.push('/register');
}
</script>

<style scoped>
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color);
  overflow: hidden;
}
body {
  overflow: hidden;
}

.login-card {
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