<template>
  <div class="admin-page-wrapper">
    <AdminSidebar :isOpen="isSidebarOpen" @close="isSidebarOpen = false" />

    <div class="admin-content-area">
      <header class="admin-topbar">
        <div class="d-flex align-items-center gap-3">
          <button @click="isSidebarOpen = true" class="btn btn-light d-md-none border-0 p-2">
            <i class="fas fa-bars text-secondary fs-5"></i>
          </button>
          <div>
            <h1 class="h5 fw-bold text-dark mb-0">Tổng quan</h1>
            <p class="small text-secondary mb-0">{{ greeting }}, Admin!</p>
          </div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <div class="live-time d-none d-md-flex align-items-center gap-2">
            <span class="live-dot"></span>
            <span class="small text-secondary font-monospace">{{ currentTime }}</span>
          </div>
          <div class="text-end d-none d-sm-block">
            <p class="fw-bold text-dark mb-0 small">Admin</p>
            <small class="text-secondary">Quản trị viên</small>
          </div>
          <img src="https://ui-avatars.com/api/?name=Admin&background=0D8ABC&color=fff" alt="Admin" class="rounded-circle border border-2" style="width:42px;height:42px;object-fit:cover"/>
        </div>
      </header>

      <div class="admin-main-scroll p-4">
        <!-- Stat Cards -->
        <div class="row g-4 mb-4">
          <div v-for="(stat, i) in statCards" :key="i" class="col-6 col-xl-3">
            <div class="stat-card">
              <div class="d-flex align-items-center gap-3 p-4">
                <div class="stat-icon" :class="stat.iconClass"><i :class="['fas', stat.icon]"></i></div>
                <div>
                  <p class="stat-label">{{ stat.label }}</p>
                  <h3 class="stat-value">
                    <span v-if="isLoading" class="placeholder col-5 rounded"></span>
                    <span v-else>{{ stat.value }}</span>
                  </h3>
                </div>
              </div>
              <div class="stat-bar" :class="stat.barClass"></div>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <!-- Recent Users Table -->
          <div class="col-12 col-lg-8">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center px-4 py-3 rounded-top-4">
                <h6 class="fw-bold mb-0"><span class="header-dot bg-primary me-2"></span>Khách Hàng Mới Nhất</h6>
                <router-link to="/admin/users" class="btn btn-sm btn-light text-primary fw-semibold">Xem tất cả →</router-link>
              </div>
              <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                  <thead class="table-light">
                    <tr>
                      <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Tài khoản</th>
                      <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Họ tên</th>
                      <th class="px-4 py-3 small text-uppercase text-secondary fw-bold d-none d-md-table-cell">Ngày sinh</th>
                      <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Vai trò</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="isLoading"><td colspan="4" class="text-center py-5"><div class="spinner-border text-primary spinner-border-sm mb-2" role="status"></div><p class="small text-secondary mb-0">Đang tải...</p></td></tr>
                    <tr v-else-if="recentUsers.length === 0"><td colspan="4" class="text-center py-5 text-secondary small">Chưa có dữ liệu</td></tr>
                    <tr v-else v-for="user in recentUsers" :key="user.id">
                      <td class="px-4 py-3">
                        <div class="d-flex align-items-center gap-2">
                          <div class="user-avatar">{{ (user.username ?? '?').charAt(0).toUpperCase() }}</div>
                          <span class="fw-semibold small">{{ user.username }}</span>
                        </div>
                      </td>
                      <td class="px-4 py-3 small text-secondary">{{ [user.firstName, user.lastName].filter(Boolean).join(' ') || '—' }}</td>
                      <td class="px-4 py-3 small text-secondary d-none d-md-table-cell">{{ user.dob || '—' }}</td>
                      <td class="px-4 py-3 text-center"><span class="badge rounded-pill bg-primary-subtle text-primary fw-semibold small">User</span></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- Right column -->
          <div class="col-12 col-lg-4 d-flex flex-column gap-4">
            <!-- Top Cars -->
            <div class="card border-0 shadow-sm rounded-4 flex-grow-1">
              <div class="card-header bg-white border-bottom px-4 py-3 rounded-top-4">
                <h6 class="fw-bold mb-0"><span class="header-dot bg-success me-2"></span>Top Xe Quan Tâm</h6>
              </div>
              <div class="card-body px-4 py-3">
                <div class="d-flex flex-column gap-3">
                  <div v-for="car in topCars" :key="car.name">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                      <span class="small fw-semibold text-dark">{{ car.name }}</span>
                      <span class="small fw-bold" :class="`text-${car.color}`">{{ car.pct }}%</span>
                    </div>
                    <div class="progress" style="height:7px;border-radius:8px;background:#f1f5f9">
                      <div class="progress-bar" :class="`bg-${car.color}`" :style="`width:${car.pct}%`" role="progressbar"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Notice -->
            <div class="card border-0 shadow-sm rounded-4" style="border-left:4px solid #f59e0b !important">
              <div class="card-body px-4 py-3">
                <div class="d-flex align-items-start gap-3">
                  <div class="notice-icon bg-warning-subtle text-warning"><i class="fas fa-bell"></i></div>
                  <div>
                    <h6 class="fw-bold text-dark mb-1 small">Thông báo hệ thống</h6>
                    <p class="small text-secondary mb-0">Hệ thống bảo trì định kỳ vào 00:00 ngày 25/12. Vui lòng lưu dữ liệu trước thời điểm này.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import api from '@/utils/axios'
import { useRouter } from 'vue-router'
import AdminSidebar from '@/components/AdminSidebar.vue'

const router = useRouter()
const isSidebarOpen = ref(false)
const isLoading = ref(false)
const recentUsers = ref([])
const currentTime = ref('')

const stats = reactive({ totalCars: 0, totalUsers: 0 })

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Chào buổi sáng'
  if (h < 18) return 'Chào buổi chiều'
  return 'Chào buổi tối'
})

const statCards = computed(() => [
  { label: 'Tổng Phiên Bản Xe', value: stats.totalCars, icon: 'fa-car', iconClass: 'bg-primary-subtle text-primary', barClass: 'bg-primary' },
  { label: 'Khách Hàng', value: stats.totalUsers, icon: 'fa-users', iconClass: 'bg-success-subtle text-success', barClass: 'bg-success' },
  { label: 'Yêu Cầu Tư Vấn', value: 18, icon: 'fa-comments', iconClass: 'bg-warning-subtle text-warning', barClass: 'bg-warning' },
  { label: 'Doanh Thu Tháng', value: '2.5 Tỷ', icon: 'fa-chart-line', iconClass: 'bg-danger-subtle text-danger', barClass: 'bg-danger' },
])

const topCars = [
  { name: 'VinFast VF 8', pct: 85, color: 'primary' },
  { name: 'VinFast VF 9', pct: 60, color: 'success' },
  { name: 'VinFast VF 5', pct: 45, color: 'warning' },
  { name: 'VinFast VF e34', pct: 30, color: 'danger' },
]

const updateTime = () => {
  currentTime.value = new Intl.DateTimeFormat('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(new Date())
}
let timer = null

const fetchDashboardData = async () => {
  isLoading.value = true
  try {
    const [carRes, userRes] = await Promise.all([api.get('/api/variants'), api.get('/api/users')])
    const cars = carRes.data.result ?? []
    const users = userRes.data.result ?? []
    stats.totalCars = Array.isArray(cars) ? cars.length : (cars.totalElements ?? 0)
    stats.totalUsers = Array.isArray(users) ? users.length : (users.totalElements ?? 0)
    recentUsers.value = (Array.isArray(users) ? users : (users.content ?? [])).slice(0, 5)
  } catch (error) {
    console.error('Lỗi tải dashboard:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => { fetchDashboardData(); updateTime(); timer = setInterval(updateTime, 1000) })
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.admin-page-wrapper { display: flex; height: 100vh; background: #f1f5f9; overflow: hidden; font-family: 'Segoe UI', system-ui, sans-serif; }
.admin-content-area { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-width: 0; }
.admin-topbar { height: 68px; background: #fff; box-shadow: 0 1px 6px rgba(0,0,0,0.07); display: flex; align-items: center; justify-content: space-between; padding: 0 1.5rem; flex-shrink: 0; z-index: 10; }
.admin-main-scroll { flex: 1; overflow-y: auto; overflow-x: hidden; }

.live-time { background: #f1f5f9; border-radius: 20px; padding: 5px 12px; }
.live-dot { width: 7px; height: 7px; border-radius: 50%; background: #22c55e; box-shadow: 0 0 0 2px rgba(34,197,94,0.25); animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{box-shadow:0 0 0 2px rgba(34,197,94,0.25)}50%{box-shadow:0 0 0 5px rgba(34,197,94,0.1)} }

.stat-card { background:#fff; border-radius:1rem; box-shadow:0 2px 12px rgba(0,0,0,0.06); overflow:hidden; position:relative; transition:transform 0.25s,box-shadow 0.25s; }
.stat-card:hover { transform:translateY(-4px); box-shadow:0 8px 24px rgba(0,0,0,0.1); }
.stat-bar { position:absolute; bottom:0; left:0; right:0; height:3px; }
.stat-icon { width:52px; height:52px; border-radius:14px; display:flex; align-items:center; justify-content:center; font-size:1.1rem; flex-shrink:0; }
.stat-label { font-size:0.78rem; color:#64748b; font-weight:500; margin-bottom:2px; }
.stat-value { font-size:1.5rem; font-weight:800; color:#0f172a; margin:0; }

.header-dot { width:8px; height:8px; border-radius:50%; display:inline-block; }
.user-avatar { width:30px; height:30px; border-radius:50%; background:linear-gradient(135deg,#1d4ed8,#3b82f6); color:#fff; font-size:0.7rem; font-weight:700; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.notice-icon { width:38px; height:38px; border-radius:10px; display:flex; align-items:center; justify-content:center; font-size:0.9rem; flex-shrink:0; }
.progress-bar { transition:width 1s ease-out; }
.bg-primary-subtle { background:#dbeafe !important; }
.bg-success-subtle { background:#dcfce7 !important; }
.bg-warning-subtle { background:#fef3c7 !important; }
.bg-danger-subtle { background:#fee2e2 !important; }
.table th { font-size:0.72rem; }
.table td { font-size:0.88rem; }
</style>