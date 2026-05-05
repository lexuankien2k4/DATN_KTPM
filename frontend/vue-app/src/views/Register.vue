<template>
  <div class="login-root">
    <!-- Left Brand -->
    <div class="login-brand d-none d-lg-flex">
      <div class="brand-overlay"></div>
      <div class="brand-content">
        <h1 class="brand-title ">VinAuto</h1>
        <p class="brand-sub">Tạo tài khoản để trải nghiệm hệ thống quản trị VinAuto hiện đại.</p>
        <ul class="brand-checklist">
          <li><i class="fas fa-check-circle text-emerald"></i> Quản lý xe & phiên bản</li>
          <li><i class="fas fa-check-circle text-emerald"></i> Theo dõi đơn đặt cọc</li>
          <li><i class="fas fa-check-circle text-emerald"></i> Tư vấn AI thông minh</li>
          <li><i class="fas fa-check-circle text-emerald"></i> Báo cáo & nhật ký hệ thống</li>
        </ul>
      </div>
    </div>

    <!-- Right Form -->
    <div class="login-form-panel">
      <div class="login-form-wrap">
        <div class="text-center mb-4 d-lg-none">
          <img src="@/assets/image/icon_logo/VinFast-logo.svg" alt="VinFast" style="height:36px" onerror="this.style.display='none'">
        </div>

        <div class="mb-4">
          <h2 class="login-title">Đăng ký tài khoản</h2>
          <p class="login-sub">Điền thông tin để bắt đầu sử dụng hệ thống</p>
        </div>

        <form @submit.prevent="handleRegister" novalidate>
          <!-- Username -->
          <div class="mb-3">
            <label class="form-label fw-semibold small">Tên đăng nhập <span class="text-danger">*</span></label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0"><i class="fas fa-user text-secondary small"></i></span>
              <input
                v-model.trim="form.username"
                type="text"
                class="form-control bg-light border-start-0"
                :class="{ 'is-invalid': fieldErrors.username }"
                placeholder="Tối thiểu 4 ký tự"
                required
                autocomplete="username"
              />
              <div class="invalid-feedback">{{ fieldErrors.username }}</div>
            </div>
          </div>

          <!-- Password -->
          <div class="mb-3">
            <label class="form-label fw-semibold small">Mật khẩu <span class="text-danger">*</span></label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0"><i class="fas fa-lock text-secondary small"></i></span>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-control bg-light border-start-0 border-end-0"
                :class="{ 'is-invalid': fieldErrors.password }"
                placeholder="Tối thiểu 6 ký tự"
                required
              />
              <button type="button" class="input-group-text bg-light border-start-0" @click="showPassword = !showPassword">
                <i :class="['fas', showPassword ? 'fa-eye-slash' : 'fa-eye', 'text-secondary']" style="font-size:0.8rem"></i>
              </button>
              <div class="invalid-feedback">{{ fieldErrors.password }}</div>
            </div>
          </div>

          <!-- First & Last name -->
          <div class="row g-3 mb-3">
            <div class="col-6">
              <label class="form-label fw-semibold small">Họ</label>
              <input v-model.trim="form.firstName" type="text" class="form-control bg-light" placeholder="Nguyễn">
            </div>
            <div class="col-6">
              <label class="form-label fw-semibold small">Tên</label>
              <input v-model.trim="form.lastName" type="text" class="form-control bg-light" placeholder="Văn A">
            </div>
          </div>

          <!-- DOB -->
          <div class="mb-4">
            <label class="form-label fw-semibold small">Ngày sinh <span class="text-danger">*</span></label>
            <div class="input-group">
              <span class="input-group-text bg-light border-end-0"><i class="fas fa-calendar-alt text-secondary small"></i></span>
              <input
                v-model="form.dob"
                type="date"
                class="form-control bg-light border-start-0"
                :class="{ 'is-invalid': fieldErrors.dob }"
                required
                :max="maxDobStr"
              />
              <div class="invalid-feedback">{{ fieldErrors.dob }}</div>
            </div>
            <div class="form-text">Bạn phải trên 10 tuổi để đăng ký.</div>
          </div>

          <!-- Alert -->
          <Transition name="slide-down">
            <div
              v-if="message"
              :class="['alert d-flex align-items-center gap-2 py-2 mb-3', isSuccess ? 'alert-success' : 'alert-danger']"
              role="alert"
            >
              <i :class="['fas flex-shrink-0', isSuccess ? 'fa-check-circle' : 'fa-exclamation-circle']"></i>
              <small>{{ message }}</small>
            </div>
          </Transition>

          <!-- Submit -->
          <button type="submit" :disabled="isLoading" class="btn btn-success btn-lg w-100 fw-bold shadow-sm">
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
            <i v-else class="fas fa-user-plus me-2"></i>
            {{ isLoading ? 'Đang xử lý...' : 'Đăng ký ngay' }}
          </button>
        </form>

        <div class="mt-4 text-center text-secondary small">
          Đã có tài khoản?
          <router-link to="/login" class="text-primary fw-semibold ms-1 text-decoration-none">Đăng nhập →</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const isLoading = ref(false)
const message = ref('')
const isSuccess = ref(false)
const showPassword = ref(false)
const fieldErrors = reactive({ username: '', password: '', dob: '' })

const form = reactive({
  username: '',
  password: '',
  firstName: '',
  lastName: '',
  dob: '',
})

// Max dob: 10 years ago
const maxDobStr = computed(() => {
  const d = new Date()
  d.setFullYear(d.getFullYear() - 10)
  return d.toISOString().split('T')[0]
})

const validateClient = () => {
  let valid = true
  fieldErrors.username = ''
  fieldErrors.password = ''
  fieldErrors.dob = ''

  if (form.username.length < 4) {
    fieldErrors.username = 'Tên đăng nhập phải có ít nhất 4 ký tự.'
    valid = false
  }
  if (form.password.length < 6) {
    fieldErrors.password = 'Mật khẩu phải có ít nhất 6 ký tự.'
    valid = false
  }
  if (!form.dob) {
    fieldErrors.dob = 'Vui lòng chọn ngày sinh.'
    valid = false
  } else if (form.dob > maxDobStr.value) {
    fieldErrors.dob = 'Bạn chưa đủ tuổi (yêu cầu trên 10 tuổi).'
    valid = false
  }
  return valid
}

const handleRegister = async () => {
  message.value = ''
  if (!validateClient()) return

  isLoading.value = true
  isSuccess.value = false

  try {
    await api.post('/api/users', form)
    isSuccess.value = true
    message.value = 'Đăng ký thành công! Đang chuyển đến trang đăng nhập...'
    Object.assign(form, { username: '', password: '', firstName: '', lastName: '', dob: '' })
    setTimeout(() => router.push('/login'), 1500)
  } catch (error) {
    isSuccess.value = false
    console.error('Lỗi đăng ký:', error)
    if (error.response?.data) {
      const { code, message: errMsg } = error.response.data
      // FIX: original code had a bug — compared message.value instead of code for INVALID_DOB
      if (code === 1002) message.value = 'Tài khoản đã tồn tại.'
      else if (code === 1003) message.value = 'Tên đăng nhập không hợp lệ (tối thiểu 4 ký tự).'
      else if (code === 1004) message.value = 'Mật khẩu quá yếu (tối thiểu 6 ký tự).'
      else if (errMsg === 'INVALID_DOB') message.value = 'Bạn chưa đủ tuổi (yêu cầu trên 10 tuổi).'
      else message.value = errMsg || 'Đăng ký thất bại. Vui lòng thử lại.'
    } else {
      message.value = 'Không thể kết nối đến máy chủ.'
    }
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

.login-brand {
  flex: 1;
  position: relative;
  background: linear-gradient(135deg, #064e3b 0%, #065f46 50%, #047857 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.login-brand::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}
.brand-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 30% 50%, rgba(16,185,129,0.2) 0%, transparent 60%);
}
.brand-content {
  position: relative;
  z-index: 1;
  color: white;
  padding: 2rem;
  max-width: 400px;
}
.brand-logo { height: 50px; filter: brightness(0) invert(1); }
.brand-title { font-size: 2rem; font-weight: 800; margin-bottom: 0.75rem; }
.brand-sub { color: rgba(255,255,255,0.65); font-size: 0.95rem; line-height: 1.6; margin-bottom: 2rem; }
.brand-checklist { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 0.85rem; }
.brand-checklist li { display: flex; align-items: center; gap: 10px; color: rgba(255,255,255,0.85); font-size: 0.9rem; }
.brand-checklist i { color: #6ee7b7; font-size: 1rem; }

.login-form-panel {
  width: 100%;
  max-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
@media (max-width: 991px) { .login-form-panel { max-width: 100%; } }
.login-form-wrap { width: 100%; max-width: 420px; }
.login-title { font-size: 1.7rem; font-weight: 800; color: #0f172a; }
.login-sub { color: #64748b; font-size: 0.9rem; margin-top: 0.2rem; }

.input-group-text { border-color: #e2e8f0; }
.form-control { border-color: #e2e8f0; }
.input-group:focus-within .input-group-text,
.input-group:focus-within .form-control { border-color: #10b981; }
.input-group:focus-within .form-control { box-shadow: none; }

.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; }
.slide-down-enter-from, .slide-down-leave-to { opacity: 0; transform: translateY(-8px); }
</style>