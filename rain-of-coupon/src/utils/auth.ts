// 认证相关工具函数

const TOKEN_KEY = 'token'

// 获取token
export const getToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
}

// 设置token
export const setToken = (token: string, remember: boolean = false): void => {
  if (remember) {
    localStorage.setItem(TOKEN_KEY, token)
  } else {
    sessionStorage.setItem(TOKEN_KEY, token)
  }
}

// 移除token
export const removeToken = (): void => {
  localStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
}

// 检查是否已登录
export const isLoggedIn = (): boolean => {
  return !!getToken()
}