<template>
  <div class="login-root">
    <!-- Left Panel: Brand -->
    <div class="login-brand d-none d-lg-flex">
      <div class="brand-overlay"></div>
      <div class="brand-content">
        <h1 class="brand-title">VinAuto Admin</h1>
        <p class="brand-sub">Hệ thống quản trị thông minh, toàn diện và bảo mật cao.</p>
        <div class="brand-features">
          <div class="brand-feature-item"><i class="fas fa-shield-alt"></i><span>Bảo mật đa lớp</span></div>
          <div class="brand-feature-item"><i class="fas fa-chart-line"></i><span>Phân tích thời gian thực</span></div>
          <div class="brand-feature-item"><i class="fas fa-robot"></i><span>Tư vấn AI thông minh</span></div>
        </div>
      </div>
    </div>

    <!-- Right Panel: Form -->
    <div class="login-form-panel">
      <div class="login-form-wrap">
        <!-- Mobile logo -->
        <div class="text-center mb-4 d-lg-none">
          <img src="@/assets/image/icon_logo/VinFast-logo.svg" alt="VinFast" style="height:40px" onerror="this.style.display='none'">
        </div>

        <div class="mb-5">
          <h2 class="login-title">Chào mừng trở lại</h2>
          <p class="login-sub">Đăng nhập để tiếp tục quản trị hệ thống</p>
        </div>

        <form @submit.prevent="handleLogin" novalidate>
          <!-- Username -->
          <div class="mb-4">
            <label class="form-label fw-semibold small">Tên đăng nhập</label>
            <div class="input-group input-group-lg">
              <span class="input-group-text border-end-0 bg-light"><i class="fas fa-user text-secondary"></i></span>
              <input
                v-model.trim="username"
                type="text"
                class="form-control border-start-0 bg-light ps-0"
                placeholder="Nhập tên đăng nhập"
                required
                autocomplete="username"
              />
            </div>
          </div>

          <!-- Password -->
          <div class="mb-4">
            <label class="form-label fw-semibold small">Mật khẩu</label>
            <div class="input-group input-group-lg">
              <span class="input-group-text border-end-0 bg-light"><i class="fas fa-lock text-secondary"></i></span>
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                class="form-control border-start-0 border-end-0 bg-light ps-0"
                placeholder="Nhập mật khẩu"
                required
                autocomplete="current-password"
              />
              <button type="button" class="input-group-text bg-light border-start-0" @click="showPassword = !showPassword">
                <i :class="['fas', showPassword ? 'fa-eye-slash' : 'fa-eye', 'text-secondary']"></i>
              </button>
            </div>
          </div>

          <!-- Error alert -->
          <Transition name="slide-down">
            <div v-if="errorMessage" class="alert alert-danger d-flex align-items-center gap-2 py-2 mb-4" role="alert">
              <i class="fas fa-exclamation-circle flex-shrink-0"></i>
              <small>{{ errorMessage }}</small>
            </div>
          </Transition>

          <!-- Submit -->
          <button
            type="submit"
            :disabled="isLoading || !username || !password"
            class="btn btn-primary btn-lg w-100 fw-bold shadow-sm"
          >
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
            <i v-else class="fas fa-sign-in-alt me-2"></i>
            {{ isLoading ? 'Đang xử lý...' : 'Đăng nhập' }}
          </button>
        </form>

        <div class="mt-4 text-center text-secondary small">
          Chưa có tài khoản?
          <router-link to="/register" class="text-primary fw-semibold ms-1 text-decoration-none">Đăng ký ngay →</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!username.value || !password.value) return
  isLoading.value = true
  errorMessage.value = ''
  try {
    const response = await api.post('/api/auth/token', {
      username: username.value,
      password: password.value,
    })
    const { token, authenticated } = response.data.result
    if (authenticated && token) {
      localStorage.setItem('authToken', token)
      router.push('/admin/dashboard')
    } else {
      errorMessage.value = 'Xác thực thất bại. Vui lòng thử lại.'
    }
  } catch (error) {
    console.error(error)
    errorMessage.value = error.response?.data?.message || 'Tên đăng nhập hoặc mật khẩu không đúng.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

.login-root {
  display: flex;
  min-height: 100vh;
  font-family: 'Be Vietnam Pro', sans-serif;
  background: #f8fafc;
}

/* ===== LEFT BRAND PANEL ===== */
.login-brand {
  flex: 1;
  position: relative;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 50%, #1d4ed8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-brand::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.brand-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 30% 50%, rgba(59,130,246,0.2) 0%, transparent 60%);
}

.brand-content {
  position: relative;
  z-index: 1;
  color: white;
  text-align: center;
  padding: 2rem;
  max-width: 420px;
}

.brand-logo {
  height: 56px;
  filter: brightness(0) invert(1);
}

.brand-title {
  font-size: 2.2rem;
  font-weight: 800;
  letter-spacing: -0.5px;
  margin-bottom: 0.75rem;
}

.brand-sub {
  color: rgba(255,255,255,0.65);
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 2.5rem;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  text-align: left;
}

.brand-feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(255,255,255,0.8);
  font-size: 0.9rem;
  font-weight: 500;
}

.brand-feature-item i {
  width: 36px;
  height: 36px;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 0.85rem;
  color: #60a5fa;
}

/* ===== RIGHT FORM PANEL ===== */
.login-form-panel {
  width: 100%;
  max-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

@media (max-width: 991px) {
  .login-form-panel { max-width: 100%; }
}

.login-form-wrap {
  width: 100%;
  max-width: 420px;
}

.login-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.3px;
}

.login-sub {
  color: #64748b;
  font-size: 0.95rem;
  margin-top: 0.25rem;
}

/* Input group styling */
.input-group-lg .input-group-text {
  border-color: #e2e8f0;
  transition: border-color 0.2s;
}
.input-group-lg .form-control {
  border-color: #e2e8f0;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.input-group-lg:focus-within .input-group-text,
.input-group-lg:focus-within .form-control {
  border-color: #2563eb;
}
.input-group-lg:focus-within .form-control {
  box-shadow: none;
}

/* Transition */
.slide-down-enter-active, .slide-down-leave-active {
  transition: all 0.3s ease;
}
.slide-down-enter-from, .slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>