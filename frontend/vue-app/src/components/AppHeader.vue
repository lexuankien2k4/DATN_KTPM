<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const isScrolled = ref(false)
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

const isLoggedIn = ref(false)
const currentUser = ref(null)

const checkLoginStatus = () => {
  const token = localStorage.getItem('authToken')
  const userStr = localStorage.getItem('user')
  if (token) {
    isLoggedIn.value = true
    if (userStr) currentUser.value = JSON.parse(userStr)
  } else {
    isLoggedIn.value = false
    currentUser.value = null
  }
}

const handleLogout = () => {
  localStorage.removeItem('authToken')
  localStorage.removeItem('user')
  isLoggedIn.value = false
  currentUser.value = null
  router.push('/login')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  checkLoginStatus()
  window.addEventListener('storage', checkLoginStatus)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('storage', checkLoginStatus)
})

const emit = defineEmits(['toggle-menu'])
function onToggleClick() {
  emit('toggle-menu')
}
</script>

<template>
  <header
    id="main-header"
    class="header-fixed transition-header"
    :class="{ 'header-scrolled': isScrolled }"
  >
    <div class="container-fluid px-4">
      <div class="d-flex justify-content-between align-items-center py-3">

        <!-- Logo -->
        <router-link to="/" class="brand-logo text-decoration-none d-flex align-items-center gap-2">
          <div class="logo-icon">
            <i class="fas fa-car"></i>
          </div>
          <span class="brand-text">Vin<span class="brand-accent">Auto</span></span>
        </router-link>

        <!-- Nav Desktop -->
<!-- Nav Desktop (Chuẩn Vue.js) -->
<!-- Nav Desktop -->
<nav class="d-none d-xl-flex align-items-center gap-1">
  <a href="/#ev-showcase" class="nav-pill">Xe điện</a>
  
  <!-- SỬA Ở ĐÂY: Đổi gas-showcase thành gasoline-showcase -->
  <a href="/#gasoline-showcase" class="nav-pill">Xe xăng</a>
  
  <a href="/#service-showcase" class="nav-pill">Xe dịch vụ</a>
  <a href="/#accessories" class="nav-pill">Phụ kiện</a>
  <a href="/#battery-charging" class="nav-pill">Pin & Sạc</a>
  <a href="/#after-sales" class="nav-pill">Hậu mãi</a>
  <a href="/#why-us" class="nav-pill">Về VinAuto</a>
</nav>

        <!-- Actions -->
        <div class="d-flex align-items-center gap-3">
          <div class="d-none d-md-flex align-items-center gap-2">

            <template v-if="isLoggedIn">
              <div class="d-flex align-items-center gap-2">
                <div class="user-badge">
                  <i class="fas fa-user-circle me-1"></i>
                  <span class="fw-semibold">{{ currentUser?.lastName || 'Khách' }}</span>
                </div>
                <button @click="handleLogout" class="btn-logout">
                  <i class="fas fa-sign-out-alt"></i>
                </button>
              </div>
              <router-link
                v-if="currentUser?.roles?.some(r => r.name === 'ADMIN')"
                to="/admin/dashboard"
                class="btn-admin"
                title="Trang quản trị"
              >
                <i class="fas fa-user-cog"></i>
              </router-link>
            </template>

            <template v-else>
              <RouterLink to="/login" class="btn-auth-outline">Đăng nhập</RouterLink>
              <RouterLink to="/register" class="btn-auth-fill">Đăng ký</RouterLink>
            </template>
          </div>

          <button @click="onToggleClick" class="btn-hamburger" aria-label="Toggle Menu">
            <span></span>
            <span></span>
            <span></span>
          </button>
        </div>

      </div>
    </div>
  </header>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

* { font-family: 'Be Vietnam Pro', sans-serif; }

.header-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1050;
}

.transition-header {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
}

.header-scrolled {
  background: rgba(255, 255, 255, 0.92) !important;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow: 0 1px 0 rgba(0,0,0,0.06), 0 8px 32px rgba(0,0,0,0.08);
}

/* Logo */
.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(59,130,246,0.35);
}

.brand-text {
  font-size: 1.4rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.brand-accent { color: #2563eb; }

/* Nav pills */
.nav-pill {
  padding: 6px 14px;
  border-radius: 8px;
  color: #374151;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-pill:hover,
.nav-pill.router-link-active {
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
}

/* Auth buttons */
.btn-auth-outline {
  padding: 7px 16px;
  border-radius: 8px;
  border: 1.5px solid #d1d5db;
  color: #374151;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-auth-outline:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: rgba(37,99,235,0.04);
}

.btn-auth-fill {
  padding: 7px 18px;
  border-radius: 8px;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: white;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 600;
  transition: all 0.2s ease;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(37,99,235,0.3);
}

.btn-auth-fill:hover {
  background: linear-gradient(135deg, #1d4ed8, #1e40af);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(37,99,235,0.4);
}

/* User badge */
.user-badge {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background: rgba(37, 99, 235, 0.08);
  border-radius: 20px;
  color: #2563eb;
  font-size: 0.875rem;
}

.btn-logout {
  background: none;
  border: 1.5px solid #fee2e2;
  color: #dc2626;
  width: 34px;
  height: 34px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-logout:hover {
  background: #fee2e2;
}

.btn-admin {
  color: #374151;
  text-decoration: none;
  font-size: 1.15rem;
  transition: color 0.2s;
}
.btn-admin:hover { color: #2563eb; }

/* Hamburger */
.btn-hamburger {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 6px;
  border-radius: 8px;
  transition: background 0.2s;
}

.btn-hamburger:hover { background: rgba(0,0,0,0.05); }

.btn-hamburger span {
  display: block;
  width: 22px;
  height: 2px;
  background: #1f2937;
  border-radius: 2px;
  transition: all 0.3s ease;
}
</style>