import axios from './index';

export interface ResourceParams {
  name: string;
  description?: string;
  url: string;
  icon?: string;
  categoryId: number;
  tags?: string;
}

export function getResourceList(params: { current?: number; size?: number; categoryId?: number; keyword?: string }) {
  return axios.get('/api/resources', { params });
}

export function getResourceById(id: number) {
  return axios.get(`/api/resources/${id}`);
}

export function createResource(data: ResourceParams) {
  return axios.post('/api/resources', data);
}

export function updateResource(id: number, data: Partial<ResourceParams>) {
  return axios.put(`/api/resources/${id}`, data);
}

export function deleteResource(id: number) {
  return axios.delete(`/api/resources/${id}`);
}

export function getHotResources(limit = 10) {
  return axios.get('/api/resources/hot', { params: { limit } });
}

export function getLatestResources(limit = 10) {
  return axios.get('/api/resources/latest', { params: { limit } });
}

export function likeResource(id: number) {
  return axios.post(`/api/resources/${id}/like`);
}