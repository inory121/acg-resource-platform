import request from './index';

export function getCategories(params?: { parentId: number }) {
  return request({
    url: '/categories',
    method: 'get',
    params
  })
}

export function getChildrenCategories(parentId: number = 0) {
  return request.get('/categories/children', { params: { parentId } });
}

export function createCategory(data: any) {
  return request.post('/categories', data);
}

export function updateCategory(id: number, data: any) {
  return request.put(`/categories/${id}`, data);
}

export function deleteCategory(id: number) {
  return request.delete(`/categories/${id}`);
}