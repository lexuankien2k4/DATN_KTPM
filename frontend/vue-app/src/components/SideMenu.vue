<script setup>
const props = defineProps({
  isOpen: { type: Boolean, default: false }
})

const emit = defineEmits(['close-menu'])

function onCloseClick() {
  emit('close-menu')
}
</script>

<template>
  <!-- Overlay -->
  <div
    id="menu-overlay"
    class="menu-overlay"
    :class="{ 'is-open': isOpen }"
    @click="onCloseClick"
  ></div>

  <!-- Side Panel -->
  <div
    id="side-menu"
    class="side-menu"
    :class="{ 'is-open': isOpen }"
  >
    <!-- Header -->
    <div class="side-menu-header d-flex justify-content-between align-items-center mb-4">
      <div>
        <h5 class="side-menu-title mb-0">Tiện ích</h5>
        <p class="side-menu-sub mb-0">Dịch vụ &amp; Công cụ</p>
      </div>
      <button class="side-menu-close" @click="onCloseClick" aria-label="Đóng">
        <i class="fas fa-times"></i>
      </button>
    </div>

    <!-- Divider -->
    <div class="side-menu-divider mb-4"></div>

    <!-- Nav -->
    <nav class="d-flex flex-column gap-1">

      <a href="/consultation" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-car-side"></i></span>
        <span class="menu-link-text">
          <span class="menu-link-label">Đăng ký lái thử</span>
          <span class="menu-link-desc">Trải nghiệm xe trực tiếp</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </a>

      <a href="#" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-right-left"></i></span>
        <span class="menu-link-text">
          <span class="menu-link-label">So sánh xe</span>
          <span class="menu-link-desc">Tìm mẫu phù hợp nhất</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </a>

      <router-link to="/chat-consultation" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-file-invoice-dollar"></i></span>
        <span class="menu-link-text">
          <span class="">Mua Xe Với AI</span>
          <span class="menu-link-desc">Tính toán tổng chi phí</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </router-link>

      <router-link to="/installment" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-hand-holding-dollar"></i></span>
        <span class="menu-link-text">
          <span class="menu-link-label">Dự đoán vay trả góp</span>
          <span class="menu-link-desc">Mô phỏng khoản vay</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </router-link>

      <a href="#" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-file-signature"></i></span>
        <span class="menu-link-text">
          <span class="menu-link-label">Thẩm định vay</span>
          <span class="menu-link-desc">Kiểm tra điều kiện vay</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </a>

      <a href="#" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-calendar-check"></i></span>
        <span class="menu-link-text">
          <span class="menu-link-label">Đặt lịch dịch vụ</span>
          <span class="menu-link-desc">Bảo dưỡng, sửa chữa</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </a>

      <router-link to="/contact" class="menu-link" @click="onCloseClick">
        <span class="menu-link-icon"><i class="fas fa-headset"></i></span>
        <span class="menu-link-text">
          <span class="menu-link-label">Liên hệ</span>
          <span class="menu-link-desc">Hotline &amp; hỗ trợ</span>
        </span>
        <i class="fas fa-chevron-right menu-link-arrow"></i>
      </router-link>

    </nav>

    <!-- Footer -->
    <div class="side-menu-footer mt-auto pt-4">
      <div class="side-menu-divider mb-3"></div>
      <p class="side-menu-footer-text">
        <i class="fas fa-phone me-2"></i>Hotline: <strong>1800 2095</strong>
      </p>
    </div>

  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

/* Overlay */
.menu-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0);
  z-index: 1040;
  pointer-events: none;
  transition: background 0.35s ease;
}

.menu-overlay.is-open {
  background: rgba(0, 0, 0, 0.5);
  pointer-events: all;
  backdrop-filter: blur(4px);
}

/* Side panel */
.side-menu {
  position: fixed;
  top: 0;
  right: 0;
  height: 100%;
  width: 360px;
  max-width: 90vw;
  background: #ffffff;
  z-index: 1050;
  transform: translateX(100%);
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  padding: 28px 24px;
  font-family: 'Be Vietnam Pro', sans-serif;
  box-shadow: -8px 0 40px rgba(0,0,0,0.12);
  overflow-y: auto;
}

.side-menu.is-open { transform: translateX(0); }

/* Header */
.side-menu-title {
  font-size: 1.25rem;
  font-weight: 800;
  color: #0f172a;
}

.side-menu-sub {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.side-menu-close {
  width: 36px;
  height: 36px;
  background: #f1f5f9;
  border: none;
  border-radius: 10px;
  color: #64748b;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.side-menu-close:hover {
  background: #e2e8f0;
  color: #0f172a;
}

.side-menu-divider {
  height: 1px;
  background: linear-gradient(90deg, #e2e8f0, transparent);
}

/* Menu links */
.menu-link {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 14px;
  border-radius: 12px;
  text-decoration: none;
  color: #374151;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.menu-link:hover {
  background: linear-gradient(135deg, rgba(37,99,235,0.05), rgba(59,130,246,0.03));
  border-color: rgba(37,99,235,0.12);
  color: #1d4ed8;
}

.menu-link:hover .menu-link-icon {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: white;
  box-shadow: 0 4px 12px rgba(37,99,235,0.3);
}

.menu-link:hover .menu-link-arrow {
  opacity: 1;
  transform: translateX(2px);
}

.menu-link-icon {
  width: 40px;
  height: 40px;
  background: #f1f5f9;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  color: #2563eb;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.menu-link-text {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.menu-link-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: inherit;
}

.menu-link-desc {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 1px;
}

.menu-link-arrow {
  font-size: 0.7rem;
  color: #cbd5e1;
  opacity: 0;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

/* Footer */
.side-menu-footer-text {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}
</style>