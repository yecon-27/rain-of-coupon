import { ref } from 'vue'
import { defineStore } from 'pinia'

export interface User {
  username: string
  nickname: string
  // 不存储密码！
}

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const isLoggedIn = ref(false)
  const currentUser = ref<User | null>(null)
  const token = ref<string | null>(null) // JWT token或session ID
  const loading = ref(false)

  // 登录
  const login = async (username: string, password: string) => {
    loading.value = true
    try {
      // 模拟API调用 - 实际项目中应该调用后端API
      const response = await mockLoginAPI(username, password)
      
      if (response.success && response.user && response.token) {
        // 只存储安全信息
        isLoggedIn.value = true
        currentUser.value = {
          username: response.user.username,
          nickname: response.user.nickname
        }
        token.value = response.token // JWT token
        
        // 只在sessionStorage中存储token（页面关闭后自动清除）
        sessionStorage.setItem('auth_token', response.token)
        sessionStorage.setItem('user_info', JSON.stringify(currentUser.value))
        
        return { success: true }
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (error) {
      console.error('登录错误:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    isLoggedIn.value = false
    currentUser.value = null
    token.value = null
    
    // 清除存储的信息
    sessionStorage.removeItem('auth_token')
    sessionStorage.removeItem('user_info')
    
    console.log('用户已安全登出')
  }

  // 检查登录状态（从sessionStorage恢复）
  const checkAuthStatus = () => {
    const storedToken = sessionStorage.getItem('auth_token')
    const storedUser = sessionStorage.getItem('user_info')
    
    if (storedToken && storedUser) {
      try {
        token.value = storedToken
        currentUser.value = JSON.parse(storedUser)
        isLoggedIn.value = true
        return true
      } catch (error) {
        console.error('恢复登录状态失败:', error)
        logout() // 清除无效数据
      }
    }
    return false
  }

  // 注册
  const register = async (userData: {username: string, password: string, nickname: string}) => {
    loading.value = true
    try {
      // 模拟API调用
      const response = await mockRegisterAPI(userData)
      
      if (response.success) {
        return { success: true }
      } else {
        throw new Error(response.message || '注册失败')
      }
    } catch (error) {
      console.error('注册错误:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    // 状态
    isLoggedIn,
    currentUser,
    token,
    loading,
    // 方法
    login,
    logout,
    register,
    checkAuthStatus
  }
})

// 定义API响应类型
interface LoginResponse {
  success: boolean
  user?: {
    username: string
    nickname: string
  }
  token?: string
  message?: string
}

interface RegisterResponse {
  success: boolean
  message?: string
}

// 模拟登录API
async function mockLoginAPI(username: string, password: string): Promise<LoginResponse> {
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  // 模拟用户数据库
  const users = [
    { username: 'admin', password: 'Admin@2024', nickname: '管理员' },
    { username: 'test', password: 'Test@2024', nickname: '测试用户' }
  ]
  
  const user = users.find(u => u.username === username && u.password === password)
  
  if (user) {
    return {
      success: true,
      user: {
        username: user.username,
        nickname: user.nickname
        // 不返回密码！
      },
      token: `jwt_token_${Date.now()}_${Math.random()}` // 模拟JWT token
    }
  } else {
    return {
      success: false,
      message: '用户名或密码错误'
    }
  }
}

// 模拟注册API
async function mockRegisterAPI(userData: {username: string, password: string, nickname: string}): Promise<RegisterResponse> {
  await new Promise(resolve => setTimeout(resolve, 1000))
  
  // 模拟检查用户名是否存在
  const existingUsers = ['admin', 'test'] // 模拟已存在的用户名
  
  if (existingUsers.includes(userData.username)) {
    return {
      success: false,
      message: '用户名已存在'
    }
  }
  
  return {
    success: true,
    message: '注册成功'
  }
}