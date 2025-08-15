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
          <button type="submit" :disabled="authStore.loading" class="login-btn">
            {{ authStore.loading ? '登录中...' : '登录' }}
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

    <!-- 气泡提示 -->
    <div v-if="toast.show" class="toast" :class="toast.type">
      <div class="toast-content">
        <span class="toast-icon">{{ toast.type === 'success' ? '✓' : '⚠' }}</span>
        <span class="toast-message">{{ toast.message }}</span>
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
            <button type="submit" :disabled="authStore.loading" class="confirm-btn">
              {{ authStore.loading ? '注册中...' : '注册' }}
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
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

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

// 气泡提示状态
const toast = reactive({
  show: false,
  type: 'success' as 'success' | 'error',
  message: ''
})

// 显示气泡提示
const showToast = (type: 'success' | 'error', message: string) => {
  toast.show = true
  toast.type = type
  toast.message = message

  // 3秒后自动隐藏
  setTimeout(() => {
    toast.show = false
  }, 3000)
}

// 处理登录
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    showToast('error', '请填写完整信息')
    return
  }

  try {
    await authStore.login(loginForm.username, loginForm.password)

    showToast('success', '登录成功！')

    // 延迟跳转，让用户看到成功提示
    setTimeout(() => {
      const redirect = route.query.redirect as string
      if (redirect) {
        router.push(redirect)
      } else {
        router.push('/')
      }
    }, 1500)
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '登录失败'
    showToast('error', errorMessage)
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password) {
    showToast('error', '请填写完整信息')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    showToast('error', '两次输入的密码不一致')
    return
  }

  try {
    await authStore.register({
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname || registerForm.username
    })

    showToast('success', '注册成功！')

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
    const errorMessage = error instanceof Error ? error.message : '注册失败'
    showToast('error', errorMessage)
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
  // 检查是否已经登录
  authStore.checkAuthStatus()

  if (authStore.isLoggedIn) {
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  }
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
  justify-content: center;
  padding: 15px 20px;
  background: rgba(0, 0, 0, 0.1);
  color: white;
  position: relative;
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
  position: absolute;
  left: 20px;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.login-header h2 {
  margin: 0;
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

/* 气泡提示样式 */
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2000;
  padding: 12px 20px;
  border-radius: 25px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  animation: slideDown 0.3s ease-out;
  max-width: 80%;
  text-align: center;
}

.toast.success {
  background: linear-gradient(135deg, #4CAF50, #45a049);
}

.toast.error {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.toast-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.toast-icon {
  font-size: 16px;
  font-weight: bold;
}

.toast-message {
  font-size: 14px;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }

  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
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

  .toast {
    max-width: 90%;
    font-size: 13px;
  }
}
</style>