import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login, getUserProfile, register, updateUserProfile, updatePassword } from '@/api/user';
import type { RegisterParams } from '@/api/user';

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('token'));
  const userInfo = ref<any>(null);

  async function loginAction(data: { username: string; password: string }) {
    const res = await login(data);
    token.value = res.data.data.token;
    localStorage.setItem('token', token.value!);
    userInfo.value = res.data.data.user;
    
    // 保存用户角色和用户信息到localStorage
    localStorage.setItem('userRole', res.data.data.user.role);
    localStorage.setItem('userInfo', JSON.stringify(res.data.data.user));
  }

  async function registerAction(data: RegisterParams) {
    const res = await register(data);
    return res.data;
  }

  async function fetchUserProfile() {
    const res = await getUserProfile();
    userInfo.value = res.data.data;
    
    // 更新localStorage中的用户信息
    localStorage.setItem('userRole', res.data.data.role);
    localStorage.setItem('userInfo', JSON.stringify(res.data.data));
  }

  async function updateUserProfileAction(data: { nickname?: string; avatar?: string }) {
    const res = await updateUserProfile(data);
    // 更新成功后重新获取用户信息
    await fetchUserProfile();
    return res.data;
  }

  async function updatePasswordAction(data: { oldPassword: string; newPassword: string; confirmPassword: string }) {
    const res = await updatePassword(data);
    return res.data;
  }

  function logout() {
    token.value = null;
    userInfo.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userInfo');
  }

  return {
    token,
    userInfo,
    loginAction,
    registerAction,
    fetchUserProfile,
    updateUserProfileAction,
    updatePasswordAction,
    logout,
  };
});