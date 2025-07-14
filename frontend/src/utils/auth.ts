/**
 * 权限验证工具
 */

/**
 * 检查当前用户是否为管理员
 */
export function isAdmin(): boolean {
  const userRole = localStorage.getItem('userRole')
  return userRole === 'ADMIN'
}

/**
 * 检查当前用户是否已登录
 */
export function isLoggedIn(): boolean {
  const token = localStorage.getItem('token')
  return !!token
}

/**
 * 获取当前用户角色
 */
export function getUserRole(): string | null {
  return localStorage.getItem('userRole')
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 清除用户登录信息
 */
export function clearUserInfo() {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userInfo')
} 