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
            <h1 class="h5 fw-bold text-dark mb-0">Khách hàng Đã Đặt Cọc</h1>
            <p class="small text-secondary mb-0">Danh sách khách hàng đã chốt đơn thành công</p>
          </div>
        </div>
        <div class="d-flex align-items-center gap-3">
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

        <!-- Search & Filter toolbar -->
        <div class="d-flex flex-column flex-sm-row gap-3 mb-4">
          <div class="position-relative flex-grow-1" style="max-width:320px">
            <input v-model="search" type="text" class="form-control ps-5" placeholder="Tìm theo tên, SĐT, email...">
            <i class="fas fa-search position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%)"></i>
          </div>
          <span class="badge bg-success-subtle text-success rounded-pill d-flex align-items-center px-3 fw-semibold">
            <i class="fas fa-circle me-2" style="font-size:0.5rem"></i>{{ filteredList.length }} khách hàng
          </span>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="card border-0 shadow-sm rounded-4 d-flex align-items-center justify-content-center" style="min-height:300px">
          <div class="spinner-border text-primary mb-3" role="status"></div>
          <p class="text-secondary">Đang tải dữ liệu...</p>
        </div>

        <!-- Empty state -->
        <div v-else-if="filteredList.length === 0" class="card border-0 shadow-sm rounded-4 text-center p-5">
          <i class="fas fa-users-slash fa-3x text-secondary mb-3"></i>
          <h5 class="fw-bold text-dark">Chưa có dữ liệu</h5>
          <p class="text-secondary small mb-0">Không tìm thấy khách hàng phù hợp với bộ lọc hiện tại.</p>
        </div>

        <!-- Table -->
        <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Khách hàng</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Xe đã chọn</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Showroom</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Nhân viên</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Trạng thái</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="customer in filteredList" :key="customer.id">
                  <!-- Customer info -->
                  <td class="px-4 py-3">
                    <div class="d-flex align-items-center gap-3">
                      <div class="customer-avatar">{{ getInitials(customer.customerName) }}</div>
                      <div>
                        <div class="fw-bold text-dark small">{{ customer.customerName || '—' }}</div>
                        <a :href="`tel:${customer.phoneNumber}`" class="text-primary text-decoration-none small">
                          <i class="fas fa-phone-alt me-1" style="font-size:0.65rem"></i>{{ customer.phoneNumber || '—' }}
                        </a>
                        <div class="text-secondary" style="font-size:0.72rem">{{ customer.email || '' }}</div>
                      </div>
                    </div>
                  </td>

                  <!-- Car variant -->
                  <td class="px-4 py-3">
                    <span class="badge rounded-pill bg-primary-subtle text-primary fw-semibold">
                      {{ customer.carVariantName || 'N/A' }}
                    </span>
                  </td>

                  <!-- Showroom -->
                  <td class="px-4 py-3">
                    <div class="small fw-semibold text-dark">
                      <i class="fas fa-store text-danger me-1"></i>
                      {{ customer.showroomName || 'Chưa xác định' }}
                    </div>
                    <div class="text-secondary" style="font-size:0.72rem">{{ customer.province || '' }}</div>
                  </td>

                  <!-- Staff -->
                  <td class="px-4 py-3">
                    <div v-if="customer.staff" class="d-flex align-items-center gap-2">
                      <div class="staff-avatar">{{ getInitials(customer.staff.firstName, customer.staff.lastName) }}</div>
                      <div>
                        <div class="fw-semibold text-dark small">{{ customer.staff.firstName }} {{ customer.staff.lastName }}</div>
                        <span :class="getRoleBadge(customer.staff.role)" class="badge rounded-pill" style="font-size:0.62rem">
                          {{ customer.staff.role }}
                        </span>
                      </div>
                    </div>
                    <div v-else class="small text-secondary fst-italic">
                      <i class="fas fa-user-slash me-1"></i>Chưa phân công
                    </div>
                  </td>

                  <!-- Status -->
                  <td class="px-4 py-3 text-center">
                    <span class="badge rounded-pill bg-success-subtle text-success fw-semibold">
                      <i class="fas fa-check me-1" style="font-size:0.65rem"></i>
                      {{ customer.status || 'Đã đặt cọc' }}
                    </span>
                  </td>

                  <!-- Action -->
                  <td class="px-4 py-3 text-center">
                    <button @click="viewDetails(customer)" class="btn btn-sm btn-primary fw-semibold rounded-pill px-3">
                      <i class="fas fa-file-contract me-1"></i>Hợp đồng
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="card-footer bg-white small text-secondary px-4 py-2 border-top">
            Hiển thị {{ filteredList.length }} / {{ depositedCustomers.length }} khách hàng đặt cọc
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chi tiết - dùng Teleport để tránh bị sidebar che -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="selectedCustomer" class="contract-modal-overlay" @click.self="selectedCustomer = null">
          <div class="contract-modal-dialog animate-pop">

            <!-- Header -->
            <div class="contract-modal-header">
              <div>
                <h5 class="fw-bold text-dark mb-0">Thông tin Hợp đồng</h5>
                <small class="text-secondary">Mã tham chiếu: #{{ selectedCustomer.id }}</small>
              </div>
              <button @click="selectedCustomer = null" class="btn-close"></button>
            </div>

            <!-- Body -->
            <div class="contract-modal-body">
              <div class="row g-4">
                <!-- Buyer -->
                <div class="col-12 col-md-6">
                  <div class="contract-section">
                    <div class="contract-section-label text-primary">
                      <i class="fas fa-user-circle me-1"></i> Bên mua xe (Khách hàng)
                    </div>
                    <div class="contract-field">
                      <label>Họ và tên</label>
                      <p class="fw-bold text-dark fs-6">{{ selectedCustomer.customerName }}</p>
                    </div>
                    <div class="contract-field">
                      <label>Số điện thoại</label>
                      <p>{{ selectedCustomer.phoneNumber }}</p>
                    </div>
                    <div class="contract-field">
                      <label>Email</label>
                      <p>{{ selectedCustomer.email || 'Chưa cung cấp' }}</p>
                    </div>
                  </div>
                </div>

                <!-- Car & Dealer -->
                <div class="col-12 col-md-6">
                  <div class="contract-section">
                    <div class="contract-section-label text-primary">
                      <i class="fas fa-car me-1"></i> Thông tin Xe & Đại lý
                    </div>
                    <div class="contract-field">
                      <label>Dòng xe</label>
                      <p class="fw-bold text-danger fs-6">{{ selectedCustomer.carVariantName || 'Chưa chọn' }}</p>
                    </div>
                    <div class="contract-field">
                      <label>Showroom</label>
                      <p><i class="fas fa-map-marker-alt text-danger me-1"></i>{{ selectedCustomer.showroomName || 'Chưa xác định' }}</p>
                    </div>
                  </div>
                </div>

                <!-- Staff -->
                <div class="col-12">
                  <div class="contract-section">
                    <div class="contract-section-label text-primary">
                      <i class="fas fa-user-tie me-1"></i> Nhân viên thực hiện hợp đồng
                    </div>
                    <div v-if="selectedCustomer.staff" class="d-flex align-items-center gap-3 mt-2">
                      <div class="staff-avatar-lg">
                        {{ getInitials(selectedCustomer.staff.firstName, selectedCustomer.staff.lastName) }}
                      </div>
                      <div>
                        <p class="fw-bold text-dark fs-6 mb-1">{{ selectedCustomer.staff.firstName }} {{ selectedCustomer.staff.lastName }}</p>
                        <span :class="getRoleBadge(selectedCustomer.staff.role)" class="badge rounded-pill">
                          {{ selectedCustomer.staff.role }}
                        </span>
                      </div>
                    </div>
                    <p v-else class="text-secondary fst-italic small">Chưa có nhân viên phụ trách</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Footer -->
            <div class="contract-modal-footer">
              <button @click="selectedCustomer = null" class="btn btn-outline-secondary fw-semibold px-4">
                Đóng
              </button>
              <button class="btn btn-primary fw-bold px-4 shadow-sm">
                <i class="fas fa-print me-2"></i>Xuất PDF
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
const allConsultations = ref([])
const loading = ref(true)
const selectedCustomer = ref(null)
const search = ref('')

const depositedCustomers = computed(() =>
  allConsultations.value.filter(item =>
    ['Đã đặt cọc', 'PAID', 'APPROVED'].includes(item.status)
  )
)

const filteredList = computed(() => {
  if (!search.value.trim()) return depositedCustomers.value
  const term = search.value.toLowerCase()
  return depositedCustomers.value.filter(c =>
    (c.customerName ?? '').toLowerCase().includes(term) ||
    (c.phoneNumber ?? '').includes(term) ||
    (c.email ?? '').toLowerCase().includes(term) ||
    (c.carVariantName ?? '').toLowerCase().includes(term)
  )
})

const fetchCustomers = async () => {
  try {
    const token = localStorage.getItem('authToken')
    const response = await fetch('http://localhost:8080/api/customers-with-staff', {
      headers: { Authorization: `Bearer ${token}` },
    })
    const data = await response.json()
    if (data.code === 1000) {
      allConsultations.value = data.result ?? []
    }
  } catch (error) {
    console.error('Lỗi tải danh sách khách hàng:', error)
  } finally {
    loading.value = false
  }
}

// FIX: support single name string OR (firstName, lastName) args
const getInitials = (firstOrFull, last) => {
  if (last !== undefined) {
    // Called with (firstName, lastName)
    return ((firstOrFull?.charAt(0) ?? '') + (last?.charAt(0) ?? '')).toUpperCase() || '?'
  }
  // Called with full name
  const parts = (firstOrFull ?? '').trim().split(' ')
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase() || '?'
  return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase()
}

const getRoleBadge = (role) => {
  switch (role) {
    case 'ADMIN': return 'bg-danger-subtle text-danger'
    case 'MANAGER': return 'bg-purple-subtle text-purple'
    case 'STAFF': return 'bg-primary-subtle text-primary'
    default: return 'bg-secondary-subtle text-secondary'
  }
}

const viewDetails = (customer) => { selectedCustomer.value = customer }

onMounted(fetchCustomers)
</script>

<style scoped>
.admin-page-wrapper { display: flex; height: 100vh; background: #f1f5f9; overflow: hidden; font-family: 'Segoe UI', system-ui, sans-serif; }
.admin-content-area { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-width: 0; }
.admin-topbar { height: 68px; background: #fff; box-shadow: 0 1px 6px rgba(0,0,0,0.07); display: flex; align-items: center; justify-content: space-between; padding: 0 1.5rem; flex-shrink: 0; z-index: 10; }
.admin-main-scroll { flex: 1; overflow-y: auto; overflow-x: hidden; }

.customer-avatar {
  width: 38px; height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.staff-avatar {
  width: 30px; height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: #fff;
  font-size: 0.65rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.staff-avatar-lg {
  width: 52px; height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
  color: #fff;
  font-size: 1rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(29,78,216,0.3);
}

/* ===== MODAL ===== */
.contract-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  z-index: 1055;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.contract-modal-dialog {
  background: #fff;
  border-radius: 1rem;
  width: 100%;
  max-width: 680px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 64px rgba(0,0,0,0.25);
  overflow: hidden;
}

.contract-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
  flex-shrink: 0;
}

.contract-modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}

.contract-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  background: #f8fafc;
  flex-shrink: 0;
}

.contract-section {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 0.75rem;
  padding: 1.25rem;
  height: 100%;
}

.contract-section-label {
  font-size: 0.72rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.contract-field label {
  display: block;
  font-size: 0.68rem;
  font-weight: 700;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 0.05em;
  margin-bottom: 2px;
}
.contract-field p {
  color: #374151;
  margin-bottom: 0.75rem;
  font-size: 0.9rem;
}

/* Transitions */
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.25s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }

@keyframes popIn {
  from { opacity: 0; transform: scale(0.94) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.animate-pop { animation: popIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); }

.table th { font-size: 0.72rem; }
.table td { font-size: 0.88rem; }
</style>