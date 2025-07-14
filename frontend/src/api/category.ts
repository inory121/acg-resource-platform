import axios from './index';

export function getCategories() {
  return axios.get('/api/categories');
}

export function getChildrenCategories(parentId: number = 0) {
  return axios.get('/api/categories/children', { params: { parentId } });
}

export function createCategory(data: any) {
  return axios.post('/api/categories', data);
}

export function updateCategory(id: number, data: any) {
  return axios.put(`/api/categories/${id}`, data);
}

export function deleteCategory(id: number) {
  return axios.delete(`/api/categories/${id}`);
}