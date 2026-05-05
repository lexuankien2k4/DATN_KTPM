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
            <h1 class="h5 fw-bold text-dark mb-0">Nhật ký hệ thống</h1>
            <p class="small text-secondary mb-0">Giám sát mọi hoạt động thay đổi dữ liệu</p>
          </div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <button @click="fetchLogs" class="btn btn-light btn-sm border" :disabled="isLoading" title="Làm mới">
            <i class="fas fa-sync-alt" :class="{ 'fa-spin': isLoading }"></i>
          </button>
          <div class="text-end d-none d-sm-block">
            <p class="fw-bold text-dark mb-0 small">Admin</p>
            <small class="text-secondary">Quản trị viên</small>
          </div>
          <img
            src="https://ui-avatars.com/api/?name=Admin&background=0D8ABC&color=fff"
            alt="Admin"
            class="rounded-circle border"
            style="width:40px;height:40px;object-fit:cover"
          />
        </div>
      </header>

      <div class="admin-main-scroll p-4">

        <!-- Stats row -->
        <div class="row g-3 mb-4">
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-primary-subtle text-primary"><i class="fas fa-list-alt"></i></div>
              <div>
                <div class="small text-secondary">Tổng nhật ký</div>
                <div class="fw-bold fs-5 text-dark">{{ logs.length }}</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-success-subtle text-success"><i class="fas fa-check-circle"></i></div>
              <div>
                <div class="small text-secondary">Thành công</div>
                <div class="fw-bold fs-5 text-success">{{ successCount }}</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-danger-subtle text-danger"><i class="fas fa-times-circle"></i></div>
              <div>
                <div class="small text-secondary">Thất bại</div>
                <div class="fw-bold fs-5 text-danger">{{ failCount }}</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-warning-subtle text-warning"><i class="fas fa-users"></i></div>
              <div>
                <div class="small text-secondary">Tài khoản</div>
                <div class="fw-bold fs-5 text-dark">{{ uniqueUsers }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Table card -->
        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
          <!-- Toolbar -->
          <div class="card-header bg-white border-bottom d-flex flex-column flex-sm-row justify-content-between align-items-start align-items-sm-center gap-3 py-3 px-4">
            <h6 class="fw-bold mb-0">Lịch sử hoạt động</h6>
            <div class="d-flex gap-2 w-100 w-sm-auto">
              <div class="position-relative flex-grow-1" style="max-width:260px">
                <input v-model="search" type="text" class="form-control form-control-sm ps-5" placeholder="Tìm kiếm...">
                <i class="fas fa-search position-absolute text-secondary" style="left:12px;top:50%;transform:translateY(-50%);font-size:0.75rem"></i>
              </div>
              <select v-model="filterStatus" class="form-select form-select-sm" style="width:auto">
                <option value="">Tất cả</option>
                <option value="SUCCESS">Thành công</option>
                <option value="FAIL">Thất bại</option>
              </select>
              <button @click="exportCSV" class="btn btn-outline-success btn-sm fw-semibold" title="Xuất CSV">
                <i class="fas fa-file-csv me-1"></i><span class="d-none d-md-inline">Xuất CSV</span>
              </button>
            </div>
          </div>

          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Thời gian</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Tài khoản</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Hành động</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">IP Address</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Chi tiết</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="6" class="text-center py-5">
                    <div class="spinner-border text-primary spinner-border-sm mb-2" role="status"></div>
                    <p class="small text-secondary mb-0">Đang tải nhật ký...</p>
                  </td>
                </tr>

                <tr v-else-if="filteredLogs.length === 0">
                  <td colspan="6" class="text-center py-5">
                    <i class="fas fa-inbox fa-2x text-secondary mb-2 d-block"></i>
                    <p class="text-secondary small mb-0">Chưa có hoạt động nào được ghi lại.</p>
                  </td>
                </tr>

                <tr v-else v-for="log in filteredLogs" :key="log.id">
                  <td class="px-4 py-3">
                    <span class="font-monospace small text-secondary">{{ formatDate(log.timestamp) }}</span>
                  </td>
                  <td class="px-4 py-3">
                    <div class="d-flex align-items-center gap-2">
                      <div class="log-avatar">{{ (log.username ?? '?').charAt(0).toUpperCase() }}</div>
                      <span class="fw-semibold text-dark small">{{ log.username || '—' }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3">
                    <span :class="['badge rounded-pill fw-semibold', getActionBadge(log.action)]">
                      {{ log.action }}
                    </span>
                  </td>
                  <td class="px-4 py-3">
                    <span class="font-monospace small text-secondary">{{ log.ipAddress || 'N/A' }}</span>
                  </td>
                  <td class="px-4 py-3" style="max-width:240px">
                    <span class="small text-secondary text-truncate d-block" :title="log.detail">
                      {{ log.detail || '—' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <span :class="['badge rounded-pill fw-semibold', isSuccess(log.status) ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger']">
                      <i :class="['fas me-1', isSuccess(log.status) ? 'fa-check' : 'fa-times']"></i>
                      {{ log.status || 'UNKNOWN' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div v-if="!isLoading && filteredLogs.length > 0" class="card-footer bg-white text-secondary small px-4 py-2 border-top">
            Hiển thị {{ filteredLogs.length }} / {{ logs.length }} bản ghi
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import AdminSidebar from '@/components/AdminSidebar.vue'

const isSidebarOpen = ref(false)
const logs = ref([])
const isLoading = ref(false)
const search = ref('')
const filterStatus = ref('')

const successCount = computed(() => logs.value.filter(l => isSuccess(l.status)).length)
const failCount = computed(() => logs.value.filter(l => !isSuccess(l.status)).length)
const uniqueUsers = computed(() => new Set(logs.value.map(l => l.username).filter(Boolean)).size)

const filteredLogs = computed(() => {
  let list = logs.value
  if (filterStatus.value === 'SUCCESS') list = list.filter(l => isSuccess(l.status))
  else if (filterStatus.value === 'FAIL') list = list.filter(l => !isSuccess(l.status))
  if (search.value.trim()) {
    const term = search.value.toLowerCase()
    list = list.filter(l =>
      (l.username ?? '').toLowerCase().includes(term) ||
      (l.action ?? '').toLowerCase().includes(term) ||
      (l.ipAddress ?? '').toLowerCase().includes(term) ||
      (l.detail ?? '').toLowerCase().includes(term)
    )
  }
  return list
})

const isSuccess = (status) => status && status.startsWith('SUCCESS')

const formatDate = (dateString) => {
  if (!dateString) return '—'
  try {
    return new Intl.DateTimeFormat('vi-VN', { dateStyle: 'short', timeStyle: 'medium' }).format(new Date(dateString))
  } catch { return dateString }
}

const getActionBadge = (action) => {
  const a = (action ?? '').toUpperCase()
  if (a.includes('CREATE') || a.includes('ADD')) return 'bg-success-subtle text-success'
  if (a.includes('DELETE') || a.includes('REMOVE')) return 'bg-danger-subtle text-danger'
  if (a.includes('UPDATE') || a.includes('EDIT')) return 'bg-warning-subtle text-warning'
  if (a.includes('LOGIN') || a.includes('AUTH')) return 'bg-primary-subtle text-primary'
  return 'bg-secondary-subtle text-secondary'
}

const fetchLogs = async () => {
  const token = localStorage.getItem('authToken')
  if (!token || token === 'null' || token === 'undefined') {
    console.warn('Chưa có Token.')
    return
  }
  isLoading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/v1/admin/logs', {
      headers: { Authorization: `Bearer ${token}` },
    })
    // FIX: handle both array and paginated response
    logs.value = Array.isArray(response.data) ? response.data : (response.data?.result ?? response.data?.content ?? [])
  } catch (error) {
    console.error('Lỗi tải nhật ký:', error)
  } finally {
    isLoading.value = false
  }
}

const exportCSV = () => {
  if (!filteredLogs.value.length) return
  const headers = ['Thời gian', 'Tài khoản', 'Hành động', 'IP Address', 'Chi tiết', 'Trạng thái']
  const rows = filteredLogs.value.map(l => [
    formatDate(l.timestamp), l.username ?? '', l.action ?? '', l.ipAddress ?? '', l.detail ?? '', l.status ?? ''
  ])
  let csv = 'data:text/csv;charset=utf-8,' + [headers, ...rows].map(r => r.map(c => `"${String(c).replace(/"/g, '""')}"`).join(',')).join('\n')
  const link = document.createElement('a')
  link.href = encodeURI(csv)
  link.download = `nhatky_${new Date().toISOString().slice(0,10)}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

onMounted(fetchLogs)
</script>

<style scoped>
.admin-page-wrapper { display: flex; height: 100vh; background: #f1f5f9; overflow: hidden; font-family: 'Segoe UI', system-ui, sans-serif; }
.admin-content-area { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-width: 0; }
.admin-topbar { height: 68px; background: #fff; box-shadow: 0 1px 6px rgba(0,0,0,0.07); display: flex; align-items: center; justify-content: space-between; padding: 0 1.5rem; flex-shrink: 0; z-index: 10; }
.admin-main-scroll { flex: 1; overflow-y: auto; overflow-x: hidden; }

.stat-icon {
  width: 44px; height: 44px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.log-avatar {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: #fff;
  font-size: 0.7rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.table th { font-size: 0.72rem; }
.table td { font-size: 0.88rem; }
</style>