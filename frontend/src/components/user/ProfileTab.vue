<template>
  <div class="profile-tab">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="基本信息" name="basic">
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="100px"
          class="profile-form"
        >
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled />
          </el-form-item>
          
          <el-form-item label="邮箱">
            <el-input v-model="userInfo.email" disabled />
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              action="#"
              :http-request="customUpload"
            >
              <el-avatar v-if="profileForm.avatar" :src="profileForm.avatar" :size="100" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="avatar-tip">点击上传头像</div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="updateProfile" :loading="updating">
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="修改密码" name="password">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="100px"
          class="password-form"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="updatePassword" :loading="updatingPassword">
              修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue';
import {useUserStore} from '@/store/user';
import type {FormInstance, FormRules} from 'element-plus';
import {ElMessage} from 'element-plus';
import {Plus} from '@element-plus/icons-vue';

const userStore = useUserStore();
const activeTab = ref('basic');
const updating = ref(false);
const updatingPassword = ref(false);

const profileFormRef = ref<FormInstance>();
const passwordFormRef = ref<FormInstance>();

const userInfo = computed(() => userStore.userInfo);

const profileForm = reactive({
  nickname: '',
  avatar: ''
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const profileRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ]
};

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

onMounted(() => {
  if (userInfo.value) {
    profileForm.nickname = userInfo.value.nickname || '';
    profileForm.avatar = userInfo.value.avatar || '';
  }
});

const updateProfile = async () => {
  if (!profileFormRef.value) return;
  
  try {
    await profileFormRef.value.validate();
    updating.value = true;
    
    await userStore.updateUserProfileAction({
      nickname: profileForm.nickname,
      avatar: profileForm.avatar
    });
    
    ElMessage.success('用户信息更新成功');
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败');
  } finally {
    updating.value = false;
  }
};

const updatePassword = async () => {
  if (!passwordFormRef.value) return;
  
  try {
    await passwordFormRef.value.validate();
    updatingPassword.value = true;
    
    await userStore.updatePasswordAction({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword
    });
    
    ElMessage.success('密码修改成功');
    // 清空表单
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    passwordFormRef.value.resetFields();
  } catch (error: any) {
    ElMessage.error(error.message || '密码修改失败');
  } finally {
    updatingPassword.value = false;
  }
};

const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
};

const handleAvatarSuccess = (response: any) => {
  profileForm.avatar = response.url;
};

const customUpload = async (options: any) => {
  // 这里应该调用文件上传接口
  // 暂时使用模拟上传
  const file = options.file;
  const reader = new FileReader();
  reader.onload = (e) => {
    profileForm.avatar = e.target?.result as string;
    ElMessage.success('头像上传成功');
  };
  reader.readAsDataURL(file);
};
</script>

<style scoped>
.profile-tab {
  padding: 20px 0;
}

.profile-form,
.password-form {
  max-width: 500px;
  margin: 0 auto;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}

.avatar-tip {
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .profile-form,
  .password-form {
    max-width: 100%;
  }
}
</style> 