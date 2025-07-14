import axios from './index';

export interface LoginParams {
  username: string;
  password: string;
}

export interface RegisterParams {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  nickname?: string;
}

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  nickname: string;
  avatar: string;
  role: string;
  status: number;
  lastLoginTime: string;
  createdTime: string;
}

export function login(data: LoginParams) {
  return axios.post('/api/auth/login', data);
}

export function register(data: RegisterParams) {
  return axios.post('/api/auth/register', data);
}

export function getUserProfile() {
  return axios.get('/api/user/profile');
}

export function updateUserProfile(data: Partial<UserProfile>) {
  return axios.put('/api/user/profile', data);
}

export function updatePassword(data: { oldPassword: string; newPassword: string; confirmPassword: string }) {
  return axios.put('/api/user/password', data);
}