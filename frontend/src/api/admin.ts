import request from './index';

export interface User {
  id: number;
  username: string;
  email: string;
  nickname: string;
  role: string;
  status: number;
  createdTime: string;
  lastLoginTime: string;
}

export interface DashboardStats {
  totalUsers: number;
  totalResources: number;
  totalCategories: number;
  totalViews: number;
  recentUsers: User[];
  hotResources: any[];
}

export interface UserListParams {
  current?: number;
  size?: number;
  keyword?: string;
  role?: string;
  status?: number;
}

// 获取用户列表
export function getUserList(params: UserListParams) {
  return request.get('/admin/users', { params });
}

// 更新用户状态
export function updateUserStatus(userId: number, status: number) {
  return request.put(`/admin/users/${userId}/status`, null, { 
    params: { status } 
  });
}

// 更新用户角色
export function updateUserRole(userId: number, role: string) {
  return request.put(`/admin/users/${userId}/role`, null, { 
    params: { role } 
  });
}

// 删除用户
export function deleteUser(userId: number) {
  return request.delete(`/admin/users/${userId}`);
}

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request.get('/admin/dashboard/stats');
}

// 获取系统信息
export function getSystemInfo() {
  return request.get('/admin/system/info');
} 