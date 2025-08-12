<template>
  <div class="login-page">
    <!-- 头部 -->
    <div class="login-header">
      <button @click="goBack" class="back-btn">← 返回</button>
      <h2>登录</h2>
    </div>

    <!-- 登录表单容器 -->
    <div class="login-container">
      <!-- Logo 区域 -->
      <div class="logo-section">
        <div class="logo">🏮</div>
        <h1 class="app-title">红包雨活动</h1>
        <p class="app-subtitle">请登录后参与活动</p>
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input id="username" v-model="loginForm.username" type="text" placeholder="请输入用户名" required
            class="form-input" />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input id="password" v-model="loginForm.password" type="password" placeholder="请输入密码" required
            class="form-input" />
        </div>

        <!-- 登录按钮 -->
        <div class="login-actions">
          <button type="submit" :disabled="loading" class="login-btn">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
      </form>

      <!-- 其他操作 -->
      <div class="other-actions">
        <button type="button" @click="showRegisterDialog = true" class="register-btn">
          还没有账号？立即注册
        </button>
      </div>
    </div>

    <!-- 注册对话框 -->
    <div v-if="showRegisterDialog" class="dialog-overlay" @click="closeRegisterDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>用户注册</h3>
          <button @click="closeRegisterDialog" class="close-btn">×</button>
        </div>

        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-group">
            <label for="reg-username">用户名</label>
            <input id="reg-username" v-model="registerForm.username" type="text" placeholder="请输入用户名" required
              class="form-input" />
          </div>

          <div class="form-group">
            <label for="reg-password">密码</label>
            <input id="reg-password" v-model="registerForm.password" type="password" placeholder="请输入密码" required
              class="form-input" />
          </div>

          <div class="form-group">
            <label for="reg-confirm">确认密码</label>
            <input id="reg-confirm" v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码"
              required class="form-input" />
          </div>

          <div class="form-group">
            <label for="reg-nickname">昵称</label>
            <input id="reg-nickname" v-model="registerForm.nickname" type="text" placeholder="请输入昵称（可选）"
              class="form-input" />
          </div>

          <div class="dialog-actions">
            <button type="button" @click="closeRegisterDialog" class="cancel-btn">取消</button>
            <button type="submit" :disabled="loading" class="confirm-btn">
              {{ loading ? '注册中...' : '注册' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

// 状态
const showRegisterDialog = ref(false)
const loading = ref(false)

// 模拟用户数据存储
const users = ref<Array<{ username: string, password: string, nickname: string }>>([])

// 处理登录
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    alert('请填写完整信息')
    return
  }

  loading.value = true

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 简单验证（实际项目中应该调用后端API）
    const user = users.value.find(u => u.username === loginForm.username && u.password === loginForm.password)

    if (user) {
      // 存储登录状态
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('currentUser', JSON.stringify(user))

      alert('登录成功！')

      // 登录成功后跳转
      const redirect = route.query.redirect as string
      if (redirect) {
        router.push(redirect)
      } else {
        router.push('/')
      }
    } else {
      alert('用户名或密码错误')
    }
  } catch (error: unknown) {
    console.error('登录错误:', error)
    alert('登录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password) {
    alert('请填写完整信息')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    alert('两次输入的密码不一致')
    return
  }

  if (users.value.find(u => u.username === registerForm.username)) {
    alert('用户名已存在')
    return
  }

  loading.value = true

  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 添加新用户
    users.value.push({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname || registerForm.username
    })

    alert('注册成功！')

    // 关闭注册对话框
    showRegisterDialog.value = false

    // 自动填入登录表单
    loginForm.username = registerForm.username

    // 清空注册表单
    Object.assign(registerForm, {
      username: '',
      password: '',
      confirmPassword: '',
      nickname: ''
    })

  } catch (error: unknown) {
    console.error('注册错误:', error)
    alert('注册失败，请重试')
  } finally {
    loading.value = false
  }
}

// 关闭注册对话框
const closeRegisterDialog = () => {
  showRegisterDialog.value = false
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}

// 页面初始化
onMounted(() => {
  // 如果已经登录，直接跳转
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  if (isLoggedIn === 'true') {
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  }

  // 初始化一些测试用户
  users.value = [
    { username: 'admin', password: '123456', nickname: '管理员' },
    { username: 'test', password: '123456', nickname: '测试用户' }
  ]
})
</script>

<style scoped>
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #DC143C, #FF6B6B);
}

.login-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: rgba(0, 0, 0, 0.1);
  color: white;
}

.back-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.login-header h2 {
  margin: 0 0 0 20px;
  font-size: 18px;
  font-weight: bold;
}

.login-container {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 60px;
  margin-bottom: 16px;
  display: block;
}

.app-title {
  color: white;
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 8px 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.app-subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: white;
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 16px;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.form-input:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.2);
}

.login-actions {
  margin-top: 30px;
}

.login-btn {
  width: 100%;
  padding: 14px;
  background: rgba(255, 255, 255, 0.9);
  color: #DC143C;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover:not(:disabled) {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.other-actions {
  text-align: center;
  padding: 20px 0;
}

.register-btn {
  color: rgba(255, 255, 255, 0.9);
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.register-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.6);
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog-content {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.register-form {
  padding: 0 20px 20px;
}

.register-form .form-input {
  background: #f5f5f5;
  border: 1px solid #ddd;
  color: #333;
}

.register-form .form-input::placeholder {
  color: #999;
}

.register-form .form-input:focus {
  border-color: #DC143C;
  background: white;
}

.register-form label {
  color: #333;
}

.dialog-actions {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.cancel-btn,
.confirm-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.confirm-btn {
  background: #DC143C;
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  background: #B91C3C;
}

.confirm-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .login-container {
    padding: 16px;
  }

  .logo {
    font-size: 50px;
  }

  .app-title {
    font-size: 20px;
  }

  .dialog-content {
    width: 95%;
  }
}
</style>