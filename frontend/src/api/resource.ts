import axios from './index';

export interface ResourceParams {
  name: string;
  description?: string;
  url: string;
  icon?: string;
  categoryId: number;
  tags?: string;
}

export function getResourceList(params?: any) {
  return axios.get('/resources', { params });
}

export function getResourceById(id: number) {
  return axios.get(`/resources/${id}`);
}

export function createResource(data: any) {
  return axios.post('/resources', data);
}

export function updateResource(id: number, data: any) {
  return axios.put(`/resources/${id}`, data);
}

export function deleteResource(id: number) {
  return axios.delete(`/resources/${id}`);
}

// 手动触发所有资源的状态检测
export function checkAllResourcesStatus() {
  return axios.post('/admin/resources/check-status');
}

export function getHotResources(limit = 10) {
  return axios.get('/resources/hot', { params: { limit } });
}

export function getLatestResources(limit = 10) {
  return axios.get('/resources/latest', { params: { limit } });
}

export function likeResource(id: number) {
  return axios.post(`/api/resources/${id}/like`);
}