import request from './index';

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
  return request.post('/auth/login', data);
}

export function register(data: RegisterParams) {
  return request.post('/auth/register', data);
}

export function getUserProfile() {
  return request.get('/user/profile');
}

export function updateUserProfile(data: Partial<UserProfile>) {
  return request.put('/user/profile', data);
}

export function updatePassword(data: { oldPassword: string; newPassword: string; confirmPassword: string }) {
  return request.put('/user/password', data);
}