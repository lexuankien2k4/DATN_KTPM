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
            <h1 class="h5 fw-bold text-dark mb-0">Quản lý Yêu cầu Tư vấn</h1>
            <p class="small text-secondary mb-0">Xem và xử lý các yêu cầu lái thử từ khách hàng</p>
          </div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <button @click="fetchConsultations" class="btn btn-light btn-sm border" :disabled="loading" title="Làm mới">
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
              <div><div class="small text-secondary">{{ s.label }}</div><div class="fw-bold fs-5">{{ s.value }}</div></div>
            </div>
          </div>
        </div>

        <!-- LOAD TRIGGER -->
        <div v-if="!tableVisible" class="card border-0 shadow-sm rounded-4 d-flex flex-column align-items-center justify-content-center py-5 gap-3" style="min-height:220px">
          <div class="d-flex align-items-center justify-content-center rounded-3 mb-2" style="width:56px;height:56px;background:#fef3c7">
            <i class="fas fa-headset fs-4 text-warning"></i>
          </div>
          <div class="text-center">
            <p class="fw-bold text-dark mb-1">Danh sách yêu cầu tư vấn chưa được tải</p>
            <p class="small text-secondary mb-0">Nhấn nút bên dưới để tải và hiển thị</p>
          </div>
          <button @click="loadTable" class="btn btn-warning fw-bold px-4 d-flex align-items-center gap-2 shadow-sm text-dark">
            <i class="fas fa-download"></i> Tải danh sách yêu cầu
          </button>
        </div>
        <template v-if="tableVisible">
        <!-- Toolbar -->
        <div class="d-flex flex-column flex-sm-row gap-3 mb-4 align-items-sm-center">
          <div class="position-relative flex-grow-1" style="max-width:300px">
            <input v-model="search" type="text" class="form-control ps-5" placeholder="Tìm tên, SĐT...">
            <i class="fas fa-search position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%)"></i>
          </div>
          <select v-model="filterStatus" class="form-select" style="width:auto;min-width:160px">
            <option value="">Tất cả trạng thái</option>
            <option v-for="st in statusOptions" :key="st" :value="st">{{ st }}</option>
          </select>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="card border-0 shadow-sm rounded-4 d-flex align-items-center justify-content-center" style="min-height:300px">
          <div class="spinner-border text-primary mb-2" role="status"></div>
          <p class="text-secondary small">Đang tải dữ liệu...</p>
        </div>

        <!-- Empty -->
        <div v-else-if="filteredList.length === 0" class="card border-0 shadow-sm rounded-4 text-center p-5">
          <i class="fas fa-inbox fa-3x text-secondary mb-3"></i>
          <p class="text-secondary mb-0">Không tìm thấy yêu cầu tư vấn nào.</p>
        </div>

        <!-- Table -->
        <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">ID / Ngày tạo</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Khách hàng</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Xe quan tâm</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Showroom</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Lịch hẹn</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Trạng thái</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in filteredList" :key="item.id">

                  <td class="px-4 py-3 align-top">
                    <div class="fw-bold text-dark small">#{{ item.id }}</div>
                    <div class="text-secondary" style="font-size:0.72rem">{{ formatDate(item.createdAt) }}</div>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <div class="d-flex align-items-center gap-2 mb-1">
                      <div class="consult-avatar">{{ getInitial(item.customerName) }}</div>
                      <!-- FIX: safe access with ?. -->
                      <span class="fw-bold small text-dark">{{ item.customerName ?? '—' }}</span>
                    </div>
                    <a :href="`tel:${item.phoneNumber}`" class="text-primary text-decoration-none small">
                      <i class="fas fa-phone-alt me-1" style="font-size:0.65rem"></i>{{ item.phoneNumber ?? '—' }}
                    </a>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <!-- FIX: safe property chaining with ?? fallback -->
                    <span class="badge rounded-pill bg-secondary-subtle text-secondary fw-semibold small">
                      {{ item.carVariantName ?? item.carVariant?.name ?? 'Chưa rõ' }}
                    </span>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <div class="small text-dark">
                      <i class="fas fa-map-marker-alt text-danger me-1"></i>
                      <!-- FIX: safe chaining -->
                      {{ item.showroomName ?? item.showroom?.name ?? 'Chưa rõ' }}
                    </div>
                  </td>

                  <td class="px-4 py-3 align-top">
                    <div v-if="item.scheduledAt" class="fw-bold small text-primary">
                      {{ formatDate(item.scheduledAt, true) }}
                    </div>
                    <div v-else class="text-secondary fst-italic small">Chưa có lịch</div>
                  </td>

                  <td class="px-4 py-3 align-top text-center">
                    <span :class="['badge rounded-pill fw-semibold', getStatusClass(item.status)]">
                      {{ item.status ?? 'Mới' }}
                    </span>
                  </td>

                  <td class="px-4 py-3 align-top text-center">
                    <div class="d-flex align-items-center justify-content-center gap-1">
                      <button
                        v-if="!item.status || item.status === 'Mới'"
                        @click="receiveConsultation(item)"
                        class="btn btn-primary btn-sm rounded-pill fw-bold px-3"
                        :disabled="actionLoading === item.id"
                      >
                        <span v-if="actionLoading === item.id" class="spinner-border spinner-border-sm me-1" role="status"></span>
                        <i v-else class="fas fa-user-plus me-1"></i>Nhận
                      </button>
                      <button @click="openDetail(item)" class="btn btn-sm btn-light text-secondary" title="Xem chi tiết"><i class="fas fa-eye"></i></button>
                      <button @click="openUpdate(item)" class="btn btn-sm btn-light text-success" title="Cập nhật"><i class="fas fa-edit"></i></button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="card-footer bg-white text-secondary small px-4 py-2 border-top d-flex align-items-center justify-content-between">
            <span>Hiển thị {{ filteredList.length }} / {{ consultations.length }} yêu cầu</span>
            <button @click="hideTable" class="btn btn-sm btn-light text-secondary border" title="Ẩn">
              <i class="fas fa-eye-slash" style="font-size:.75rem"></i> Ẩn
            </button>
          </div>
        </div>
        </template><!-- /tableVisible -->
      </div>
    </div>

    <!-- Modal - Teleport to body to avoid sidebar overlap -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="selectedItem" class="consult-modal-overlay" @click.self="selectedItem = null">
          <div class="consult-modal-dialog animate-pop">
            <div class="consult-modal-header">
              <div>
                <h5 class="fw-bold text-dark mb-0">
                  {{ isUpdateMode ? 'Cập nhật trạng thái' : 'Chi tiết yêu cầu' }}
                  <span class="text-secondary ms-1">#{{ selectedItem.id }}</span>
                </h5>
              </div>
              <button @click="selectedItem = null" class="btn-close"></button>
            </div>

            <div class="consult-modal-body">
              <div class="row g-4">
                <div class="col-12 col-md-6">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-user-circle me-1 text-primary"></i>Khách hàng</div>
                    <div class="detail-row"><label>Họ và tên</label><p class="fw-bold text-dark">{{ selectedItem.customerName ?? '—' }}</p></div>
                    <div class="detail-row"><label>Số điện thoại</label><p class="text-primary fw-semibold">{{ selectedItem.phoneNumber ?? '—' }}</p></div>
                    <div class="detail-row"><label>Email</label><p>{{ selectedItem.email ?? 'N/A' }}</p></div>
                  </div>
                </div>
                <div class="col-12 col-md-6">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-car me-1 text-primary"></i>Xe & Showroom</div>
                    <div class="detail-row">
                      <label>Xe quan tâm</label>
                      <!-- FIX: optional chaining -->
                      <p class="fw-bold text-danger">{{ selectedItem.carVariantName ?? selectedItem.carVariant?.name ?? 'N/A' }}</p>
                    </div>
                    <div class="detail-row">
                      <label>Showroom</label>
                      <p><i class="fas fa-map-marker-alt text-danger me-1"></i>{{ selectedItem.showroomName ?? selectedItem.showroom?.name ?? 'N/A' }}</p>
                    </div>
                    <div class="detail-row" v-if="selectedItem.scheduledAt">
                      <label>Lịch hẹn</label>
                      <p class="fw-bold text-primary">{{ formatDate(selectedItem.scheduledAt, true) }}</p>
                    </div>
                  </div>
                </div>

                <div class="col-12">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-sticky-note me-1 text-warning"></i>Ghi chú từ khách</div>
                    <div class="bg-warning-subtle rounded-3 p-3 small fst-italic text-secondary mt-2">
                      "{{ selectedItem.note ?? 'Không có ghi chú nào.' }}"
                    </div>
                  </div>
                </div>

                <!-- FIX: guard staff with v-if, safe access to staff.firstName/lastName/role -->
                <div v-if="selectedItem.staff" class="col-12">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-user-tie me-1 text-success"></i>Nhân viên phụ trách</div>
                    <div class="d-flex align-items-center gap-3 mt-2">
                      <div class="staff-avatar">{{ getInitials(selectedItem.staff?.firstName, selectedItem.staff?.lastName) }}</div>
                      <div>
                        <p class="fw-bold text-dark mb-1">{{ selectedItem.staff?.firstName ?? '' }} {{ selectedItem.staff?.lastName ?? '' }}</p>
                        <span class="badge rounded-pill bg-primary-subtle text-primary small">{{ selectedItem.staff?.role ?? '' }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Update mode -->
                <div v-if="isUpdateMode" class="col-12">
                  <div class="detail-section">
                    <div class="detail-section-label"><i class="fas fa-edit me-1 text-primary"></i>Chọn trạng thái xử lý</div>
                    <select v-model="statusUpdate" class="form-select mt-2">
                      <option v-for="st in statusOptions" :key="st" :value="st">{{ st }}</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>

            <div class="consult-modal-footer">
              <button @click="selectedItem = null" class="btn btn-outline-secondary fw-semibold px-4">Đóng</button>
              <button v-if="isUpdateMode" @click="updateStatus" :disabled="actionLoading === 'update'" class="btn btn-primary fw-bold px-5 shadow-sm">
                <span v-if="actionLoading === 'update'" class="spinner-border spinner-border-sm me-2" role="status"></span>
                <i v-else class="fas fa-save me-2"></i>Lưu thay đổi
              </button>
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
const tableVisible = ref(false)

const loadTable = async () => {
  tableVisible.value = true
  await fetchConsultations()
}

const hideTable = () => {
  tableVisible.value = false
  consultations.value = []
}

const consultations = ref([])
const loading = ref(true)
const actionLoading = ref(null)
const selectedItem = ref(null)
const isUpdateMode = ref(false)
const statusUpdate = ref('')
const search = ref('')
const filterStatus = ref('')

const statusOptions = ['Mới', 'Đang liên hệ', 'Không nghe máy', 'Đã hẹn lịch', 'Đã đặt cọc', 'Đã hủy']

const statCards = computed(() => [
  { label: 'Tổng yêu cầu', value: consultations.value.length, icon: 'fa-list-alt', iconClass: 'stat-icon--blue' },
  { label: 'Mới', value: consultations.value.filter(c => !c.status || c.status === 'Mới').length, icon: 'fa-bell', iconClass: 'stat-icon--yellow' },
  { label: 'Đang xử lý', value: consultations.value.filter(c => c.status === 'Đang liên hệ').length, icon: 'fa-headset', iconClass: 'stat-icon--purple' },
  { label: 'Đã đặt cọc', value: consultations.value.filter(c => c.status === 'Đã đặt cọc').length, icon: 'fa-check-circle', iconClass: 'stat-icon--green' },
])

const filteredList = computed(() => {
  let list = consultations.value
  if (filterStatus.value) list = list.filter(c => (c.status ?? 'Mới') === filterStatus.value)
  if (search.value.trim()) {
    const term = search.value.toLowerCase()
    list = list.filter(c =>
      (c.customerName ?? '').toLowerCase().includes(term) ||
      (c.phoneNumber ?? '').includes(term)
    )
  }
  return list
})

const fetchConsultations = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('authToken')
    const response = await fetch('http://localhost:8080/api/consultations', {
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    })
    const data = await response.json()
    if (data.code === 1000) consultations.value = data.result ?? []
  } catch (error) {
    console.error('Lỗi kết nối:', error)
  } finally {
    loading.value = false
  }
}

const receiveConsultation = async (item) => {
  if (!confirm(`Nhận tư vấn khách hàng "${item.customerName ?? '?'}"?`)) return
  actionLoading.value = item.id
  try {
    const token = localStorage.getItem('authToken')
    const res = await fetch(`http://localhost:8080/api/consultations/${item.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify({ newStatus: 'Đang liên hệ' }),
    })
    if (res.ok) fetchConsultations()
    else alert('Không thể nhận yêu cầu. Vui lòng thử lại.')
  } catch (e) {
    console.error(e)
  } finally {
    actionLoading.value = null
  }
}

const openDetail = (item) => { selectedItem.value = item; isUpdateMode.value = false }
const openUpdate = (item) => { selectedItem.value = item; statusUpdate.value = item.status ?? 'Đang liên hệ'; isUpdateMode.value = true }

const updateStatus = async () => {
  actionLoading.value = 'update'
  try {
    const token = localStorage.getItem('authToken')
    const res = await fetch(`http://localhost:8080/api/consultations/${selectedItem.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify({ newStatus: statusUpdate.value }),
    })
    if (res.ok) { selectedItem.value = null; fetchConsultations() }
    else alert('Cập nhật thất bại.')
  } catch (e) { console.error(e) }
  finally { actionLoading.value = null }
}

const formatDate = (dateString, includeTime = false) => {
  if (!dateString) return '—'
  try {
    return new Intl.DateTimeFormat('vi-VN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      ...(includeTime ? { hour: '2-digit', minute: '2-digit' } : {})
    }).format(new Date(dateString))
  } catch { return dateString }
}

// FIX: safe initial — guard against null/undefined customerName
const getInitial = (name) => (name ?? '?').charAt(0).toUpperCase()
const getInitials = (first, last) => ((first ?? '').charAt(0) + (last ?? '').charAt(0)).toUpperCase() || '?'

const getStatusClass = (status) => {
  switch (status) {
    case 'Mới': return 'bg-primary-subtle text-primary'
    case 'Đang liên hệ': return 'bg-indigo-subtle text-indigo'
    case 'Đã hẹn lịch': return 'bg-warning-subtle text-warning'
    case 'Không nghe máy': return 'bg-orange-subtle text-orange'
    case 'Đã hủy': return 'bg-danger-subtle text-danger'
    case 'Đã đặt cọc': return 'bg-success-subtle text-success'
    default: return 'bg-secondary-subtle text-secondary'
  }
}

onMounted(() => {})
</script>

<style scoped>
.admin-page-wrapper { display:flex; height:100vh; background:#f1f5f9; overflow:hidden; font-family:'Segoe UI',system-ui,sans-serif; }
.admin-content-area { flex:1; display:flex; flex-direction:column; overflow:hidden; min-width:0; }
.admin-topbar { height:68px; background:#fff; box-shadow:0 1px 6px rgba(0,0,0,0.07); display:flex; align-items:center; justify-content:space-between; padding:0 1.5rem; flex-shrink:0; z-index:10; }
.admin-main-scroll { flex:1; overflow-y:auto; overflow-x:hidden; }

.stat-icon { width:44px;height:44px;border-radius:12px;display:flex;align-items:center;justify-content:center;font-size:1.1rem;flex-shrink:0; }
.stat-icon--blue { background:#dbeafe;color:#1d4ed8; }
.stat-icon--green { background:#dcfce7;color:#15803d; }
.stat-icon--yellow { background:#fef3c7;color:#b45309; }
.stat-icon--purple { background:#ede9fe;color:#6d28d9; }

.consult-avatar { width:28px;height:28px;border-radius:50%;background:linear-gradient(135deg,#2563eb,#7c3aed);color:#fff;font-size:0.65rem;font-weight:700;display:flex;align-items:center;justify-content:center;flex-shrink:0; }
.staff-avatar { width:42px;height:42px;border-radius:50%;background:linear-gradient(135deg,#1d4ed8,#3b82f6);color:#fff;font-size:0.85rem;font-weight:700;display:flex;align-items:center;justify-content:center;flex-shrink:0; }

/* Modal */
.consult-modal-overlay { position:fixed;inset:0;background:rgba(15,23,42,0.6);backdrop-filter:blur(4px);z-index:1055;display:flex;align-items:center;justify-content:center;padding:1rem; }
.consult-modal-dialog { background:#fff;border-radius:1rem;width:100%;max-width:700px;max-height:90vh;display:flex;flex-direction:column;box-shadow:0 24px 64px rgba(0,0,0,0.25);overflow:hidden; }
.consult-modal-header { display:flex;justify-content:space-between;align-items:center;padding:1.25rem 1.5rem;border-bottom:1px solid #e5e7eb;flex-shrink:0; }
.consult-modal-body { flex:1;overflow-y:auto;padding:1.5rem;scrollbar-width:thin; }
.consult-modal-footer { display:flex;justify-content:flex-end;gap:.75rem;padding:1rem 1.5rem;border-top:1px solid #e5e7eb;background:#f8fafc;flex-shrink:0; }

.detail-section { background:#f8fafc;border:1px solid #e5e7eb;border-radius:.75rem;padding:1.25rem;height:100%; }
.detail-section-label { font-size:.7rem;font-weight:700;text-transform:uppercase;letter-spacing:.08em;margin-bottom:.75rem;padding-bottom:.5rem;border-bottom:1px solid #e5e7eb; }
.detail-row label { display:block;font-size:.65rem;font-weight:700;text-transform:uppercase;color:#94a3b8;letter-spacing:.05em;margin-bottom:2px; }
.detail-row p { color:#374151;font-size:.9rem;margin-bottom:.5rem; }

.modal-fade-enter-active, .modal-fade-leave-active { transition:opacity .25s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity:0; }
@keyframes popIn { from{opacity:0;transform:scale(.94) translateY(10px)}to{opacity:1;transform:scale(1) translateY(0)} }
.animate-pop { animation:popIn .3s cubic-bezier(.16,1,.3,1); }

.table th { font-size:.72rem; }
.table td { font-size:.88rem; }
.bg-primary-subtle{background:#dbeafe!important} .bg-success-subtle{background:#dcfce7!important} .bg-warning-subtle{background:#fef3c7!important} .bg-danger-subtle{background:#fee2e2!important} .bg-secondary-subtle{background:#f1f5f9!important} .bg-indigo-subtle{background:#ede9fe!important} .bg-orange-subtle{background:#ffedd5!important}
.text-indigo{color:#4f46e5!important} .text-orange{color:#ea580c!important}
</style>