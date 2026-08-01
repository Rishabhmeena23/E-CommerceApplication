import { queryString, request } from './client'

export const authApi = {
  register: (payload) => request('/auth/register', { method: 'POST', body: payload }),
  login: (payload) => request('/auth/login', { method: 'POST', body: payload }),
}

export const userApi = {
  list: () => request('/users'),
  get: (id) => request(`/users/${id}`),
  ban: (id) => request(`/users/${id}/ban`, { method: 'PATCH' }),
  unban: (id) => request(`/users/${id}/unban`, { method: 'PATCH' }),
  changeRole: (id, role) => request(`/users/${id}/role`, { method: 'PATCH', body: { role } }),
}

export const productApi = {
  list: () => request('/products'),
  get: (id) => request(`/products/${id}`),
  search: (filters) => request(`/products/search${queryString(filters)}`),
  create: (payload) => request('/products', { method: 'POST', body: payload }),
  update: (id, payload) => request(`/products/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => request(`/products/${id}`, { method: 'DELETE' }),
  images: {
    list: (productId) => request(`/products/${productId}/images`),
    add: (productId, images) => request(`/products/${productId}/images`, { method: 'POST', body: images }),
    update: (productId, imageId, image) =>
      request(`/products/${productId}/images/${imageId}`, { method: 'PUT', body: image }),
    remove: (productId, imageId) =>
      request(`/products/${productId}/images/${imageId}`, { method: 'DELETE' }),
  },
  inventory: {
    get: (productId) => request(`/products/${productId}/inventory`),
    create: (productId, inventory) =>
      request(`/products/${productId}/inventory`, { method: 'POST', body: inventory }),
    update: (productId, inventory) =>
      request(`/products/${productId}/inventory`, { method: 'PUT', body: inventory }),
    increase: (productId, quantity) =>
      request(`/products/${productId}/inventory/increase`, { method: 'PATCH', body: { quantity } }),
    decrease: (productId, quantity) =>
      request(`/products/${productId}/inventory/decrease`, { method: 'PATCH', body: { quantity } }),
  },
}

export const categoryApi = {
  list: () => request('/categories'),
  get: (id) => request(`/categories/${id}`),
  create: (payload) => request('/categories', { method: 'POST', body: payload }),
  update: (id, payload) => request(`/categories/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => request(`/categories/${id}`, { method: 'DELETE' }),
}

export const subCategoryApi = {
  list: (categoryId) => request(`/subcategories${queryString({ categoryId })}`),
  get: (id) => request(`/subcategories/${id}`),
  create: (payload) => request('/subcategories', { method: 'POST', body: payload }),
  update: (id, payload) => request(`/subcategories/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => request(`/subcategories/${id}`, { method: 'DELETE' }),
}

export const customerApi = {
  create: (payload) => request('/customers', { method: 'POST', body: payload }),
  me: () => request('/customers/me'),
  updateMe: (payload) => request('/customers/me', { method: 'PUT', body: payload }),
  deleteMe: () => request('/customers/me', { method: 'DELETE' }),
  list: () => request('/customers'),
  get: (id) => request(`/customers/${id}`),
  update: (id, payload) => request(`/customers/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => request(`/customers/${id}`, { method: 'DELETE' }),
}

export const sellerApi = {
  create: (payload) => request('/sellers', { method: 'POST', body: payload }),
  get: (id) => request(`/sellers/${id}`),
  getByUser: (userId) => request(`/sellers/user/${userId}`),
  me: () => request('/sellers/me'),
  list: () => request('/sellers'),
  update: (id, payload) => request(`/sellers/${id}`, { method: 'PUT', body: payload }),
  updateStatus: (id, approvalStatus) =>
    request(`/sellers/${id}/status`, { method: 'PATCH', body: { approvalStatus } }),
  deactivate: (id) => request(`/sellers/${id}`, { method: 'DELETE' }),
}

export const cartApi = {
  create: () => request('/cart', { method: 'POST' }),
  get: () => request('/cart'),
  add: (productId, quantity = 1) => request('/cart/items', { method: 'POST', body: { productId, quantity } }),
  update: (productId, quantity) =>
    request(`/cart/items/${productId}`, { method: 'PUT', body: { quantity } }),
  remove: (productId) => request(`/cart/items/${productId}`, { method: 'DELETE' }),
  clear: () => request('/cart', { method: 'DELETE' }),
}

export const wishlistApi = {
  create: () => request('/wishlist', { method: 'POST' }),
  get: () => request('/wishlist'),
  add: (productId) => request('/wishlist/items', { method: 'POST', body: { productId } }),
  remove: (productId) => request(`/wishlist/items/${productId}`, { method: 'DELETE' }),
  clear: () => request('/wishlist', { method: 'DELETE' }),
}

export const orderApi = {
  create: (payload) => request('/orders', { method: 'POST', body: payload }),
  mine: () => request('/orders/me'),
  list: () => request('/orders'),
  get: (id) => request(`/orders/${id}`),
  cancel: (id) => request(`/orders/${id}/cancel`, { method: 'PATCH' }),
  updateStatus: (id, status) => request(`/orders/${id}/status`, { method: 'PATCH', body: { status } }),
}

export const paymentApi = {
  pay: (payload) => request('/payments', { method: 'POST', body: payload }),
  mine: () => request('/payments/me'),
  list: () => request('/payments'),
  get: (id) => request(`/payments/${id}`),
}

export const adminApi = {
  health: () => request('/admin/health'),
  dashboard: () => request('/admin/dashboard'),
  users: () => request('/admin/users'),
  user: (id) => request(`/admin/users/${id}`),
  banUser: (id, reason) => request(`/admin/users/${id}/ban`, { method: 'PATCH', body: { reason } }),
  unbanUser: (id) => request(`/admin/users/${id}/unban`, { method: 'PATCH' }),
  assignRole: (id, role) => request(`/admin/users/${id}/role`, { method: 'PATCH', body: { role } }),
  products: () => request('/admin/products'),
  orders: () => request('/admin/orders'),
}
