<template>
  <Transition name="fade-backdrop">
    <div
      v-if="isOpen"
      class="sidebar-backdrop d-md-none"
      @click="$emit('close')"
    ></div>
  </Transition>

  <aside
    class="admin-sidebar"
    :class="{ 'sidebar-open': isOpen, 'sidebar-collapsed': isCollapsed && !isMobile }"
  >
    <div class="sidebar-inner d-flex flex-column h-100">

      <div class="sidebar-header d-flex align-items-center px-3 py-3 border-bottom border-white-10">
        <img
          src="https://cdn-001.haui.edu.vn//img/logo-haui-size.png"
          alt="Logo"
          class="sidebar-logo flex-shrink-0"
        />
        <Transition name="fade-text">
          <div v-if="!isCollapsed || isMobile" class="flex-grow-1 ms-2 overflow-hidden">
            <div class="sidebar-brand text-truncate">DASHBOARD</div>
            <div class="sidebar-sub text-truncate">Admin Panel</div>
          </div>
        </Transition>

        <button
          @click="toggleCollapse"
          class="btn-sidebar-toggle d-none d-md-flex ms-auto"
          :title="isCollapsed ? 'Mở rộng' : 'Thu gọn'"
        >
          <i :class="['fas', isCollapsed ? 'fa-chevron-right' : 'fa-chevron-left']"></i>
        </button>

        <button
          @click="$emit('close')"
          class="btn-sidebar-toggle d-md-none ms-auto"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>

      <nav class="sidebar-nav flex-grow-1 overflow-auto py-3">
        <div v-if="!isCollapsed || isMobile" class="nav-section-label px-3 mb-2">CHỨC NĂNG</div>

        <router-link
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          active-class="nav-item-active"
          class="nav-item-link"
          :title="isCollapsed && !isMobile ? item.label : ''"
          @click="isMobile && $emit('close')"
        >
          <span class="nav-item-icon">
            <i :class="['fas', item.icon]"></i>
          </span>
          <Transition name="fade-text">
            <span v-if="!isCollapsed || isMobile" class="nav-label text-truncate">{{ item.label }}</span>
          </Transition>
        </router-link>

        <div v-if="!isCollapsed || isMobile" class="nav-section-label px-3 mb-2 mt-4">GIAO DỊCH</div>

        <router-link
          v-for="item in transactionItems"
          :key="item.to"
          :to="item.to"
          active-class="nav-item-active"
          class="nav-item-link"
          :title="isCollapsed && !isMobile ? item.label : ''"
          @click="isMobile && $emit('close')"
        >
          <span class="nav-item-icon">
            <i :class="['fas', item.icon]"></i>
          </span>
          <Transition name="fade-text">
            <span v-if="!isCollapsed || isMobile" class="nav-label text-truncate">{{ item.label }}</span>
          </Transition>
        </router-link>
      </nav>

      <div class="sidebar-footer px-2 py-3 border-top border-white-10">
        <button @click="handleLogout" class="btn-logout-sidebar w-100" :title="isCollapsed && !isMobile ? 'Đăng xuất' : ''">
          <i class="fas fa-sign-out-alt"></i>
          <Transition name="fade-text">
            <span v-if="!isCollapsed || isMobile" class="ms-2">Đăng xuất</span>
          </Transition>
        </button>
      </div>

    </div>
  </aside>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

defineProps({
  isOpen: { type: Boolean, default: false }
})
defineEmits(['close'])

const router = useRouter()
const isCollapsed = ref(false)
const windowWidth = ref(window.innerWidth)

const isMobile = computed(() => windowWidth.value < 768)

const navItems = [
  { to: '/admin/dashboard', icon: 'fa-th-large', label: 'Dashboard' },
  { to: '/admin/cars', icon: 'fa-car', label: 'Quản lý Sản phẩm' },
  { to: '/admin/employee', icon: 'fa-users-cog', label: 'Quản lý tài khoản' },
  { to: '/admin/dashboard2', icon: 'fa-chart-line', label: 'Thống kê' },
  { to: '/admin/consultations', icon: 'fa-headset', label: 'Yêu cầu Tư vấn' },
]
const transactionItems = [
  { to: '/admin/stocks', icon: 'fa-file-invoice-dollar', label: 'Quản lý kho xe' },
  { to: '/admin/contracts', icon: 'fa-file-contract', label: 'Hợp đồng' },
  { to: '/admin/nhatky', icon: 'fa-history', label: 'Nhật ký hệ thống' },
]

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const onResize = () => { windowWidth.value = window.innerWidth }
onMounted(() => window.addEventListener('resize', onResize))
onUnmounted(() => window.removeEventListener('resize', onResize))

const handleLogout = () => {
  if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
    localStorage.removeItem('authToken')
    router.push('/login')
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

/* ==============================
   SIDEBAR BASE (Chuyển sang nền sáng)
============================== */
.admin-sidebar {
  width: 260px;
  height: 100vh;
  background: #ffffff; /* Nền trắng */
  flex-shrink: 0;
  position: sticky;
  top: 0;
  left: 0;
  z-index: 200;           /* Thấp hơn modal (z-index: 1050+) */
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1),
              transform 0.28s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Be Vietnam Pro', sans-serif;
  box-shadow: 2px 0 16px rgba(0, 0, 0, 0.05); /* Bóng đổ nhạt đi */
  border-right: 1px solid #e2e8f0; /* Thêm đường viền mỏng bên phải */
  overflow: hidden;
}

/* Desktop: thu gọn */
.sidebar-collapsed {
  width: 72px;
}

/* Mobile: ẩn bằng fixed + translateX */
@media (max-width: 767px) {
  .admin-sidebar {
    position: fixed;
    top: 0; left: 0;
    height: 100%;
    z-index: 200;
    transform: translateX(-100%);
    width: 260px !important;  /* luôn full trên mobile */
  }
  .admin-sidebar.sidebar-open {
    transform: translateX(0);
  }
}

/* ==============================
   BACKDROP (mobile)
============================== */
.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3); /* Làm mờ đi một chút so với nền tối */
  z-index: 199;             /* Ngay dưới sidebar */
  backdrop-filter: blur(2px);
}

/* ==============================
   HEADER
============================== */
.sidebar-header {
  min-height: 64px;
}

.sidebar-logo {
  width: 36px;
  height: 36px;
  min-width: 36px;
  object-fit: contain;
  border-radius: 8px;
  background: white;
  padding: 2px;
  border: 1px solid #e2e8f0; /* Thêm viền cho logo nếu nền đã trắng */
}

.sidebar-brand {
  font-size: 0.875rem;
  font-weight: 800;
  color: #0f172a; /* Chữ đậm màu tối */
  letter-spacing: 0.4px;
  line-height: 1.2;
}

.sidebar-sub {
  font-size: 0.68rem;
  color: #64748b; /* Chữ xám nhạt */
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

/* ==============================
   TOGGLE / CLOSE BUTTONS
============================== */
.btn-sidebar-toggle {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9; /* Nền nút xám sáng */
  border: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 0.75rem;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  flex-shrink: 0;
}
.btn-sidebar-toggle:hover {
  background: #e2e8f0;
  color: #1e293b;
}

/* ==============================
   NAV SECTION LABEL
============================== */
.nav-section-label {
  font-size: 0.62rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: #94a3b8; /* Chữ tiêu đề xám nhạt hơn */
  text-transform: uppercase;
  margin-bottom: 4px;
}

/* ==============================
   NAV ITEMS
============================== */
.nav-item-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  margin: 2px 8px;
  border-radius: 10px;
  color: #475569; /* Chữ menu xám đậm */
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
  overflow: hidden;
}

.nav-item-link:hover {
  background: #f8fafc; /* Nền hover xám siêu nhạt */
  color: #2563eb; /* Đổi màu xanh khi hover */
}

.nav-item-active,
.nav-item-link.router-link-active {
  background: #eff6ff !important; /* Nền active xanh nhạt */
  color: #2563eb !important;
  border: 1px solid #dbeafe; /* Viền active xanh nhạt */
  font-weight: 600;
}

.nav-item-icon {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  background: #f1f5f9; /* Nền icon xám sáng */
  color: #64748b;
  font-size: 0.82rem;
  flex-shrink: 0;
  transition: all 0.2s;
}

.nav-item-link:hover .nav-item-icon {
  background: #e2e8f0;
}

.nav-item-active .nav-item-icon,
.nav-item-link.router-link-active .nav-item-icon {
  background: #2563eb !important; /* Nền icon active xanh đậm */
  color: #ffffff !important; /* Icon màu trắng */
}

.nav-label {
  flex: 1;
  min-width: 0;
}

/* ==============================
   LOGOUT
============================== */
.btn-logout-sidebar {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff1f2; /* Nền nút logout đỏ nhạt */
  border: 1px solid #ffe4e6;
  color: #e11d48; /* Chữ đỏ */
  padding: 9px 12px;
  border-radius: 10px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: 'Be Vietnam Pro', sans-serif;
  white-space: nowrap;
  overflow: hidden;
}
.btn-logout-sidebar:hover {
  background: #ffe4e6;
  border-color: #fecdd3;
  color: #be123c;
}

/* ==============================
   MISCELLANEOUS
============================== */
.border-white-10 { 
  border-color: #e2e8f0 !important; /* Đổi viền trắng trong suốt thành xám nhạt */
}

/* Transitions (Không thay đổi) */
.fade-text-enter-active,
.fade-text-leave-active { transition: opacity 0.15s ease; }
.fade-text-enter-from,
.fade-text-leave-to { opacity: 0; }

.fade-backdrop-enter-active,
.fade-backdrop-leave-active { transition: opacity 0.25s ease; }
.fade-backdrop-enter-from,
.fade-backdrop-leave-to { opacity: 0; }
</style>