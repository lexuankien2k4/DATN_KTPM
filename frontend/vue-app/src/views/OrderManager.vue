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
            <h1 class="h5 fw-bold text-dark mb-0">Quản lý Đặt cọc</h1>
            <p class="small text-secondary mb-0">Danh sách các đơn cọc xe từ khách hàng</p>
          </div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <button @click="fetchDeposits" class="btn btn-light btn-sm border" :disabled="loading" title="Làm mới">
            <i class="fas fa-sync-alt" :class="{ 'fa-spin': loading }"></i>
          </button>
          <div class="text-end d-none d-sm-block">
            <p class="fw-bold text-dark mb-0 small">Admin</p>
            <small class="text-secondary">Quản trị viên</small>
          </div>
          <img src="https://ui-avatars.com/api/?name=Admin&background=0D8ABC&color=fff" alt="Admin" class="rounded-circle border" style="width:40px;height:40px;object-fit:cover">
        </div>
      </header>

      <div class="admin-main-scroll p-4">

        <!-- Stats -->
        <div class="row g-3 mb-4">
          <div class="col-6 col-md-3" v-for="s in statCards" :key="s.label">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon" :class="s.iconClass"><i :class="['fas', s.icon]"></i></div>
              <div><div class="small text-secondary">{{ s.label }}</div><div class="fw-bold fs-5" :class="s.valClass">{{ s.value }}</div></div>
            </div>
          </div>
        </div>

        <!-- Filter toolbar -->
        <div class="d-flex flex-column flex-sm-row gap-3 mb-4">
          <div class="position-relative flex-grow-1" style="max-width:300px">
            <input v-model="search" type="text" class="form-control ps-5" placeholder="Tìm tên, SĐT, xe...">
            <i class="fas fa-search position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%)"></i>
          </div>
          <select v-model="filterStatus" class="form-select" style="width:auto;min-width:160px">
            <option value="">Tất cả trạng thái</option>
            <option v-for="st in ['PENDING','PAID','APPROVED','REJECTED']" :key="st" :value="st">{{ st }}</option>
          </select>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="card border-0 shadow-sm rounded-4 d-flex align-items-center justify-content-center" style="min-height:300px">
          <div class="spinner-border text-primary mb-2" role="status"></div>
          <p class="text-secondary small">Đang tải dữ liệu...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="filteredList.length === 0" class="card border-0 shadow-sm rounded-4 text-center p-5">
          <i class="fas fa-file-invoice-dollar fa-3x text-secondary mb-3"></i>
          <p class="text-secondary mb-0">Chưa có đơn đặt cọc nào.</p>
        </div>

        <!-- Table -->
        <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Mã đơn</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Khách hàng</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Thông tin xe</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Số tiền cọc</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Trạng thái</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in filteredList" :key="item.id">

                  <td class="px-4 py-3 align-top">
                    <div class="fw-bold text-dark small">#{{ item.id }}</div>
                    <div class="text-secondary" style="font-size:.72rem">{{ formatDate(item.createdAt) }}</div>
                    <!-- FIX: safe check before rendering paymentMethod -->
                    <span v-if="item.paymentMethod" class="badge bg-light text-secondary border small mt-1">{{ item.paymentMethod }}</span>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <div class="d-flex align-items-center gap-2 mb-1">
                      <div class="order-avatar">{{ getInitial(item.customerName ?? item.userId) }}</div>
                      <!-- FIX: coalesce customerName with userId -->
                      <span class="fw-bold small text-dark">{{ item.customerName ?? `User #${item.userId ?? '?'}` }}</span>
                    </div>
                    <div class="text-primary small">{{ item.phoneNumber ?? '' }}</div>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <!-- FIX: safe display of carVariantName -->
                    <div class="fw-semibold small text-dark mb-1">
                      {{ item.carVariantName ?? (item.variantId != null ? `ID: ${item.variantId}` : 'N/A') }}
                    </div>
                    <!-- FIX: guard color display -->
                    <div v-if="item.selectedColor" class="d-flex align-items-center gap-2 mb-1">
                      <span class="color-dot border" :style="{ background: getColorHex(item.selectedColor) }"></span>
                      <span class="text-secondary small">{{ item.selectedColor }}</span>
                    </div>
                    <div v-if="item.showroomName" class="text-secondary small">
                      <i class="fas fa-map-marker-alt me-1 text-danger"></i>{{ item.showroomName }}
                    </div>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <!-- FIX: guard amount -->
                    <span class="fw-bold text-primary font-monospace small">
                      {{ item.amount != null ? formatCurrency(item.amount) : '—' }}
                    </span>
                  </td>

                  <td class="px-4 py-3 align-top text-center">
                    <!-- FIX: guard status -->
                    <span :class="['badge rounded-pill fw-semibold', statusBadge(item.status)]">
                      {{ item.status ?? 'N/A' }}
                    </span>
                  </td>

                  <td class="px-4 py-3 align-top text-center">
                    <div class="d-flex justify-content-center gap-1">
                      <button
                        v-if="item.status === 'PAID'"
                        @click="updateStatus(item.id, 'APPROVED')"
                        :disabled="actionLoading === item.id"
                        class="btn btn-sm btn-success fw-bold rounded-pill px-3"
                      >
                        <i class="fas fa-check me-1"></i>Duyệt
                      </button>
                      <button
                        v-if="['PENDING','PAID'].includes(item.status)"
                        @click="updateStatus(item.id, 'REJECTED')"
                        :disabled="actionLoading === item.id"
                        class="btn btn-sm btn-danger fw-bold rounded-pill px-3"
                      >
                        <i class="fas fa-times me-1"></i>Hủy
                      </button>
                      <button
                        v-if="['APPROVED','REJECTED'].includes(item.status)"
                        @click="viewDetails(item)"
                        class="btn btn-sm btn-outline-primary fw-bold rounded-pill px-3"
                      >
                        <i class="fas fa-eye me-1"></i>Chi tiết
                      </button>
                    </div>
                  </td>

                </tr>
              </tbody>
            </table>
          </div>
          <div class="card-footer bg-white text-secondary small px-4 py-2 border-top">
            Hiển thị {{ filteredList.length }} / {{ deposits.length }} đơn đặt cọc
          </div>
        </div>
      </div>
    </div>

    <!-- Detail Modal - Teleport to avoid sidebar z-index issue -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="selectedDeposit" class="order-modal-overlay" @click.self="selectedDeposit = null">
          <div class="order-modal-dialog animate-pop">

            <!-- Header -->
            <div class="order-modal-header">
              <div>
                <h5 class="fw-bold text-dark mb-0">Chi tiết Đơn đặt cọc</h5>
                <small class="text-secondary">Mã đơn: #{{ selectedDeposit.id }}</small>
              </div>
              <button @click="selectedDeposit = null" class="btn-close"></button>
            </div>

            <!-- Body -->
            <div class="order-modal-body">
              <!-- Status banner -->
              <div class="text-center mb-4">
                <span :class="['badge rounded-pill fw-bold px-4 py-2 fs-6', statusBadge(selectedDeposit.status)]">
                  {{ selectedDeposit.status ?? 'N/A' }}
                </span>
              </div>

              <div class="row g-4">
                <!-- Customer -->
                <div class="col-12 col-md-6">
                  <div class="detail-section">
                    <div class="detail-section-label text-primary"><i class="fas fa-user-circle me-1"></i>Khách hàng</div>
                    <div class="detail-row"><label>Họ tên</label><p class="fw-bold">{{ selectedDeposit.customerName ?? `User #${selectedDeposit.userId ?? '?'}` }}</p></div>
                    <div class="detail-row"><label>Số điện thoại</label><p>{{ selectedDeposit.phoneNumber ?? '—' }}</p></div>
                    <div class="detail-row"><label>Email</label><p>{{ selectedDeposit.email ?? '—' }}</p></div>
                  </div>
                </div>

                <!-- Product -->
                <div class="col-12 col-md-6">
                  <div class="detail-section">
                    <div class="detail-section-label text-primary"><i class="fas fa-car me-1"></i>Sản phẩm</div>
                    <div class="detail-row">
                      <label>Dòng xe</label>
                      <p class="fw-bold text-danger">{{ selectedDeposit.carVariantName ?? (selectedDeposit.variantId != null ? `ID: ${selectedDeposit.variantId}` : 'N/A') }}</p>
                    </div>
                    <div class="detail-row" v-if="selectedDeposit.selectedColor">
                      <label>Màu sắc</label>
                      <div class="d-flex align-items-center gap-2">
                        <span class="color-dot border" :style="{ background: getColorHex(selectedDeposit.selectedColor) }"></span>
                        <span>{{ selectedDeposit.selectedColor }}</span>
                      </div>
                    </div>
                    <div class="detail-row"><label>Showroom</label><p>{{ selectedDeposit.showroomName ?? '—' }}</p></div>
                    <div class="detail-row"><label>Số tiền cọc</label><p class="fw-bold text-primary">{{ selectedDeposit.amount != null ? formatCurrency(selectedDeposit.amount) : '—' }}</p></div>
                  </div>
                </div>

                <!-- Note -->
                <div class="col-12" v-if="selectedDeposit.note">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-sticky-note me-1 text-warning"></i>Ghi chú</div>
                    <p class="fst-italic text-secondary small mt-1 mb-0">"{{ selectedDeposit.note }}"</p>
                  </div>
                </div>

                <!-- Staff - FIX: guard before accessing nested -->
                <div v-if="selectedDeposit.staff" class="col-12">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-user-tie me-1 text-success"></i>Nhân viên xử lý</div>
                    <div class="d-flex align-items-center gap-3 mt-2">
                      <div class="staff-avatar">{{ getInitials(selectedDeposit.staff?.firstName, selectedDeposit.staff?.lastName) }}</div>
                      <div>
                        <p class="fw-bold text-dark mb-1">{{ selectedDeposit.staff?.firstName ?? '' }} {{ selectedDeposit.staff?.lastName ?? '' }}</p>
                        <span class="badge rounded-pill bg-secondary-subtle text-secondary small">{{ selectedDeposit.staff?.role ?? '' }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="order-modal-footer">
              <button @click="selectedDeposit = null" class="btn btn-outline-secondary fw-semibold px-4">Đóng</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AdminSidebar from '@/components/AdminSidebar.vue'

const isSidebarOpen = ref(false)
const deposits = ref([])
const loading = ref(true)
const actionLoading = ref(null)
const selectedDeposit = ref(null)
const search = ref('')
const filterStatus = ref('')

const statCards = computed(() => [
  { label: 'Tổng đơn', value: deposits.value.length, icon: 'fa-file-invoice', iconClass: 'stat-icon--blue', valClass: '' },
  { label: 'Chờ thanh toán', value: deposits.value.filter(d => d.status === 'PENDING').length, icon: 'fa-clock', iconClass: 'stat-icon--yellow', valClass: 'text-warning' },
  { label: 'Đã thanh toán', value: deposits.value.filter(d => d.status === 'PAID').length, icon: 'fa-credit-card', iconClass: 'stat-icon--blue', valClass: 'text-primary' },
  { label: 'Đã duyệt', value: deposits.value.filter(d => d.status === 'APPROVED').length, icon: 'fa-check-circle', iconClass: 'stat-icon--green', valClass: 'text-success' },
])

const filteredList = computed(() => {
  let list = deposits.value
  if (filterStatus.value) list = list.filter(d => d.status === filterStatus.value)
  if (search.value.trim()) {
    const term = search.value.toLowerCase()
    list = list.filter(d =>
      (d.customerName ?? '').toLowerCase().includes(term) ||
      (d.phoneNumber ?? '').includes(term) ||
      (d.carVariantName ?? '').toLowerCase().includes(term)
    )
  }
  return list
})

const formatCurrency = (value) => {
  if (value == null) return '—'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const formatDate = (dateString) => {
  if (!dateString) return '—'
  try { return new Intl.DateTimeFormat('vi-VN', { dateStyle: 'short' }).format(new Date(dateString)) }
  catch { return dateString }
}

const statusBadge = (status) => {
  switch (status) {
    case 'PAID': return 'bg-primary-subtle text-primary'
    case 'APPROVED': return 'bg-success-subtle text-success'
    case 'REJECTED': return 'bg-danger-subtle text-danger'
    case 'PENDING': return 'bg-warning-subtle text-warning'
    default: return 'bg-secondary-subtle text-secondary'
  }
}

// FIX: null-safe color hex lookup
const getColorHex = (colorName) => {
  if (!colorName) return '#d1d5db'
  const lower = String(colorName).toLowerCase()
  if (lower.includes('white') || lower.includes('trắng')) return '#f9fafb'
  if (lower.includes('red') || lower.includes('đỏ')) return '#dc2626'
  if (lower.includes('grey') || lower.includes('gray') || lower.includes('xám')) return '#6b7280'
  if (lower.includes('blue') || lower.includes('xanh dương')) return '#2563eb'
  if (lower.includes('green') || lower.includes('xanh lá')) return '#16a34a'
  if (lower.includes('yellow') || lower.includes('vàng')) return '#fbbf24'
  if (lower.includes('black') || lower.includes('đen')) return '#1f2937'
  if (lower.includes('orange') || lower.includes('cam')) return '#f97316'
  return '#d1d5db'
}

const getInitial = (name) => (String(name ?? '?')).charAt(0).toUpperCase()
const getInitials = (first, last) => ((first ?? '').charAt(0) + (last ?? '').charAt(0)).toUpperCase() || '?'

const fetchDeposits = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('authToken')
    const res = await fetch('http://localhost:8080/api/deposits', {
      headers: { Authorization: `Bearer ${token}` }
    })
    const data = await res.json()
    if (data.code === 1000) {
      deposits.value = (data.result ?? []).sort((a, b) => (b.id ?? 0) - (a.id ?? 0))
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const updateStatus = async (id, status) => {
  const label = status === 'APPROVED' ? 'DUYỆT' : 'HỦY'
  if (!confirm(`Chắc chắn muốn ${label} đơn hàng #${id}?`)) return
  actionLoading.value = id
  try {
    const token = localStorage.getItem('authToken')
    const res = await fetch(`http://localhost:8080/api/deposits/${id}/status`, {
      method: 'PUT',
      headers: { 'Content-Type': 'text/plain', Authorization: `Bearer ${token}` },
      body: status,
    })
    const data = await res.json()
    if (data.code === 1000) fetchDeposits()
    else alert('Cập nhật thất bại: ' + (data.message ?? ''))
  } catch (e) { console.error(e); alert('Lỗi kết nối server') }
  finally { actionLoading.value = null }
}

const viewDetails = (item) => { selectedDeposit.value = item }

onMounted(fetchDeposits)
</script>

<style scoped>
.admin-page-wrapper { display:flex;height:100vh;background:#f1f5f9;overflow:hidden;font-family:'Segoe UI',system-ui,sans-serif; }
.admin-content-area { flex:1;display:flex;flex-direction:column;overflow:hidden;min-width:0; }
.admin-topbar { height:68px;background:#fff;box-shadow:0 1px 6px rgba(0,0,0,.07);display:flex;align-items:center;justify-content:space-between;padding:0 1.5rem;flex-shrink:0;z-index:10; }
.admin-main-scroll { flex:1;overflow-y:auto;overflow-x:hidden; }

.stat-icon { width:44px;height:44px;border-radius:12px;display:flex;align-items:center;justify-content:center;font-size:1.1rem;flex-shrink:0; }
.stat-icon--blue{background:#dbeafe;color:#1d4ed8}.stat-icon--green{background:#dcfce7;color:#15803d}.stat-icon--yellow{background:#fef3c7;color:#b45309}.stat-icon--red{background:#fee2e2;color:#b91c1c}

.order-avatar { width:28px;height:28px;border-radius:50%;background:linear-gradient(135deg,#2563eb,#7c3aed);color:#fff;font-size:.65rem;font-weight:700;display:flex;align-items:center;justify-content:center;flex-shrink:0; }
.staff-avatar { width:42px;height:42px;border-radius:50%;background:linear-gradient(135deg,#4f46e5,#7c3aed);color:#fff;font-size:.85rem;font-weight:700;display:flex;align-items:center;justify-content:center;flex-shrink:0; }
.color-dot { width:14px;height:14px;border-radius:50%;display:inline-block;flex-shrink:0; }

.order-modal-overlay { position:fixed;inset:0;background:rgba(15,23,42,.6);backdrop-filter:blur(4px);z-index:1055;display:flex;align-items:center;justify-content:center;padding:1rem; }
.order-modal-dialog { background:#fff;border-radius:1rem;width:100%;max-width:700px;max-height:90vh;display:flex;flex-direction:column;box-shadow:0 24px 64px rgba(0,0,0,.25);overflow:hidden; }
.order-modal-header { display:flex;justify-content:space-between;align-items:center;padding:1.25rem 1.5rem;border-bottom:1px solid #e5e7eb;flex-shrink:0; }
.order-modal-body { flex:1;overflow-y:auto;padding:1.5rem;scrollbar-width:thin; }
.order-modal-footer { display:flex;justify-content:flex-end;gap:.75rem;padding:1rem 1.5rem;border-top:1px solid #e5e7eb;background:#f8fafc;flex-shrink:0; }

.detail-section { background:#f8fafc;border:1px solid #e5e7eb;border-radius:.75rem;padding:1.25rem;height:100%; }
.detail-section-label { font-size:.7rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em;margin-bottom:.75rem;padding-bottom:.5rem;border-bottom:1px solid #e5e7eb; }
.detail-row label { display:block;font-size:.65rem;font-weight:700;text-transform:uppercase;color:#94a3b8;margin-bottom:2px; }
.detail-row p { color:#374151;font-size:.9rem;margin-bottom:.5rem; }

.modal-fade-enter-active,.modal-fade-leave-active{transition:opacity .25s ease}.modal-fade-enter-from,.modal-fade-leave-to{opacity:0}
@keyframes popIn{from{opacity:0;transform:scale(.94) translateY(10px)}to{opacity:1;transform:scale(1) translateY(0)}}.animate-pop{animation:popIn .3s cubic-bezier(.16,1,.3,1)}

.bg-primary-subtle{background:#dbeafe!important}.bg-success-subtle{background:#dcfce7!important}.bg-warning-subtle{background:#fef3c7!important}.bg-danger-subtle{background:#fee2e2!important}.bg-secondary-subtle{background:#f1f5f9!important}
.table th{font-size:.72rem}.table td{font-size:.88rem}
</style>