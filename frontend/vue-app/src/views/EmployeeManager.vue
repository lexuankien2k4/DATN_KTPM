<template>
  <div class="admin-page-wrapper">
    <AdminSidebar :isOpen="isSidebarOpen" @close="isSidebarOpen = false" />

    <div class="admin-content-area">
      <!-- Topbar -->
      <header class="admin-topbar">
        <div class="d-flex align-items-center gap-3">
          <button @click="isSidebarOpen = true" class="btn btn-light d-md-none border-0 p-2">
            <i class="fas fa-bars text-secondary fs-5"></i>
          </button>
          <div>
            <h1 class="h5 fw-bold text-dark mb-0">Quản lý Nhân sự</h1>
            <p class="small text-secondary mb-0">Danh sách nhân viên trong hệ thống</p>
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
            class="rounded-circle border border-2"
            style="width:40px;height:40px;object-fit:cover"
          />
        </div>
      </header>

      <!-- Content -->
      <div class="admin-main-scroll p-4">

        <!-- Stats -->
        <div class="row g-3 mb-4">
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-primary-soft text-primary"><i class="fas fa-users"></i></div>
              <div>
                <div class="small text-secondary">Tổng nhân viên</div>
                <div class="fw-bold fs-5 text-dark">{{ users.length }}</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-danger-soft text-danger"><i class="fas fa-crown"></i></div>
              <div>
                <div class="small text-secondary">Admin</div>
                <div class="fw-bold fs-5 text-dark">{{ countByRole('ADMIN') }}</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-success-soft text-success"><i class="fas fa-user-tie"></i></div>
              <div>
                <div class="small text-secondary">Staff</div>
                <div class="fw-bold fs-5 text-dark">{{ countByRole('STAFF') }}</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card border-0 shadow-sm rounded-3 p-3 d-flex flex-row align-items-center gap-3">
              <div class="stat-icon bg-secondary-soft text-secondary"><i class="fas fa-user"></i></div>
              <div>
                <div class="small text-secondary">User</div>
                <div class="fw-bold fs-5 text-dark">{{ countByRole('USER') }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- LOAD TRIGGER -->
        <div v-if="!tableVisible" class="card border-0 shadow-sm rounded-4 d-flex flex-column align-items-center justify-content-center py-5 gap-3 mb-4" style="min-height:220px">
          <div class="d-flex align-items-center justify-content-center rounded-3 mb-2" style="width:56px;height:56px;background:#dbeafe">
            <i class="fas fa-users fs-4 text-primary"></i>
          </div>
          <div class="text-center">
            <p class="fw-bold text-dark mb-1">Danh sách nhân viên chưa được tải</p>
            <p class="small text-secondary mb-0">Nhấn nút bên dưới để tải và hiển thị</p>
          </div>
          <button @click="loadTable" class="btn btn-primary fw-bold px-4 d-flex align-items-center gap-2 shadow-sm">
            <i class="fas fa-download"></i> Tải danh sách nhân viên
          </button>
        </div>
        <template v-if="tableVisible">
        <!-- Toolbar -->
        <div class="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
          <button @click="openCreateModal" class="btn btn-primary fw-semibold d-flex align-items-center gap-2 shadow-sm">
            <i class="fas fa-user-plus"></i>Thêm Tài Khoản
          </button>
          <div class="position-relative" style="max-width:300px;width:100%">
            <input v-model="searchTerm" type="text" class="form-control ps-5" placeholder="Tìm tên, username...">
            <i class="fas fa-search position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%)"></i>
          </div>
        </div>

        <!-- Table -->
        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Nhân viên</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold d-none d-md-table-cell">Ngày sinh</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Vai trò</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold text-center">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="4" class="text-center py-5">
                    <div class="spinner-border text-primary spinner-border-sm mb-2" role="status"></div>
                    <p class="text-secondary small mb-0">Đang tải dữ liệu...</p>
                  </td>
                </tr>
                <tr v-else-if="filteredUsers.length === 0">
                  <td colspan="4" class="text-center py-5">
                    <i class="fas fa-users-slash fa-2x text-secondary mb-2 d-block"></i>
                    <p class="text-secondary small mb-0">Không tìm thấy nhân viên nào.</p>
                  </td>
                </tr>
                <tr v-else v-for="user in filteredUsers" :key="user.id">
                  <!-- Employee info -->
                  <td class="px-4 py-3">
                    <div class="d-flex align-items-center gap-3">
                      <div class="emp-avatar" :style="{ background: getAvatarGradient(user.username) }">
                        {{ getInitial(user.firstName, user.username) }}
                      </div>
                      <div>
                        <div class="fw-bold text-dark small">
                          {{ [user.firstName, user.lastName].filter(Boolean).join(' ') || '—' }}
                        </div>
                        <div class="text-primary small font-monospace">@{{ user.username ?? '—' }}</div>
                      </div>
                    </div>
                  </td>

                  <!-- DOB -->
                  <td class="px-4 py-3 text-secondary small d-none d-md-table-cell">
                    <!-- FIX: guard dob null -->
                    {{ user.dob ? formatDate(user.dob) : '—' }}
                  </td>

                  <!-- Roles -->
                  <td class="px-4 py-3 text-center">
                    <div class="d-flex justify-content-center gap-1 flex-wrap">
                      <!-- FIX: guard roles array with ?. and ?? [] -->
                      <template v-if="(user.roles ?? []).length > 0">
                        <span
                          v-for="role in user.roles"
                          :key="role.name"
                          :class="['badge rounded-pill fw-bold small', getRoleBadge(role.name)]"
                        >
                          {{ role.name }}
                        </span>
                      </template>
                      <span v-else class="badge rounded-pill bg-secondary-subtle text-secondary fw-bold small">N/A</span>
                    </div>
                  </td>

                  <!-- Actions -->
                  <td class="px-4 py-3 text-center">
                    <div class="d-flex justify-content-center gap-1">
                      <button
                        @click="openEditModal(user)"
                        class="btn btn-sm btn-light text-primary"
                        title="Sửa thông tin"
                      >
                        <i class="fas fa-edit"></i>
                      </button>
                      <button
                        @click="deleteUser(user.id)"
                        class="btn btn-sm btn-light text-danger"
                        title="Xóa nhân viên"
                      >
                        <i class="fas fa-trash-alt"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="card-footer bg-white border-top small text-secondary px-4 py-2 d-flex align-items-center justify-content-between">
            <span>Hiển thị {{ filteredUsers.length }} / {{ users.length }} nhân viên</span>
            <button @click="hideTable" class="btn btn-sm btn-light text-secondary border" title="Ẩn">
              <i class="fas fa-eye-slash" style="font-size:.75rem"></i> Ẩn
            </button>
          </div>
        </div>
        </template><!-- /tableVisible -->

      </div>
    </div>

    <!-- ====== MODAL - Teleport to body ====== -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="showModal" class="emp-modal-overlay" @click.self="closeModal">
          <div class="emp-modal-dialog animate-pop">

            <!-- Header -->
            <div class="emp-modal-header">
              <div class="d-flex align-items-center gap-3">
                <div class="modal-header-icon" :class="isEditing ? 'bg-primary-soft text-primary' : 'bg-success-soft text-success'">
                  <i :class="['fas', isEditing ? 'fa-user-edit' : 'fa-user-plus']"></i>
                </div>
                <div>
                  <h5 class="fw-bold text-dark mb-0">
                    {{ isEditing ? 'Cập nhật Nhân viên' : 'Đăng ký Tài khoản Nhân viên' }}
                  </h5>
                  <small class="text-secondary">{{ isEditing ? `Chỉnh sửa thông tin cho @${form.username}` : 'Tạo tài khoản mới trong hệ thống' }}</small>
                </div>
              </div>
              <button @click="closeModal" class="btn-close"></button>
            </div>

            <!-- Body -->
            <div class="emp-modal-body">
              <form @submit.prevent="submitForm" novalidate>

                <!-- Username -->
                <div class="mb-3">
                  <label class="form-label fw-semibold small">
                    Tên đăng nhập <span class="text-danger">*</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="fas fa-at text-secondary small"></i></span>
                    <input
                      v-model.trim="form.username"
                      type="text"
                      :disabled="isEditing"
                      :class="['form-control bg-light border-start-0', isEditing ? 'text-muted' : '']"
                      placeholder="vd: nguyen.vana"
                      required
                    />
                  </div>
                  <div v-if="isEditing" class="form-text"><i class="fas fa-lock me-1"></i>Tên đăng nhập không thể thay đổi.</div>
                </div>

                <!-- Password -->
                <div class="mb-3">
                  <label class="form-label fw-semibold small">
                    Mật khẩu <span v-if="!isEditing" class="text-danger">*</span>
                  </label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="fas fa-lock text-secondary small"></i></span>
                    <input
                      v-model="form.password"
                      :type="showPassword ? 'text' : 'password'"
                      :required="!isEditing"
                      :placeholder="isEditing ? 'Để trống nếu không đổi mật khẩu' : 'Tối thiểu 6 ký tự'"
                      class="form-control bg-light border-start-0 border-end-0"
                    />
                    <button type="button" class="input-group-text bg-light border-start-0" @click="showPassword = !showPassword">
                      <i :class="['fas text-secondary', showPassword ? 'fa-eye-slash' : 'fa-eye']" style="font-size:0.8rem"></i>
                    </button>
                  </div>
                </div>

                <!-- Name row -->
                <div class="row g-3 mb-3">
                  <div class="col-6">
                    <label class="form-label fw-semibold small">Họ <span class="text-danger">*</span></label>
                    <input v-model.trim="form.firstName" type="text" required class="form-control bg-light" placeholder="Nguyễn">
                  </div>
                  <div class="col-6">
                    <label class="form-label fw-semibold small">Tên <span class="text-danger">*</span></label>
                    <input v-model.trim="form.lastName" type="text" required class="form-control bg-light" placeholder="Văn A">
                  </div>
                </div>

                <!-- DOB -->
                <div class="mb-4">
                  <label class="form-label fw-semibold small">Ngày sinh <span class="text-danger">*</span></label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="fas fa-calendar-alt text-secondary small"></i></span>
                    <input v-model="form.dob" type="date" required class="form-control bg-light border-start-0" :max="maxDobStr">
                  </div>
                </div>

                <!-- Roles -->
                <div class="p-3 bg-light rounded-3 border mb-3">
                  <label class="form-label fw-bold small text-uppercase text-secondary mb-3">
                    <i class="fas fa-shield-alt me-1 text-primary"></i>Phân quyền (Roles)
                  </label>
                  <div v-if="availableRoles.length === 0" class="text-secondary small fst-italic">
                    <span class="spinner-border spinner-border-sm me-2" role="status"></span>Đang tải danh sách quyền...
                  </div>
                  <div class="d-flex flex-wrap gap-2">
                    <label
                      v-for="role in availableRoles"
                      :key="role.name"
                      :class="['role-check-label', form.roles.includes(role.name) ? 'role-check-label--active' : '']"
                    >
                      <input
                        type="checkbox"
                        :value="role.name"
                        v-model="form.roles"
                        class="d-none"
                      />
                      <i class="fas fa-check-circle me-1" style="font-size:0.75rem"></i>
                      {{ role.name }}
                    </label>
                  </div>
                </div>

                <!-- Alert -->
                <Transition name="slide-down">
                  <div v-if="formError" class="alert alert-danger d-flex align-items-center gap-2 py-2 mb-3" role="alert">
                    <i class="fas fa-exclamation-circle flex-shrink-0"></i>
                    <small>{{ formError }}</small>
                  </div>
                </Transition>

              </form>
            </div>

            <!-- Footer -->
            <div class="emp-modal-footer">
              <button type="button" @click="closeModal" class="btn btn-outline-secondary fw-semibold px-4">
                Hủy bỏ
              </button>
              <button type="button" @click="submitForm" :disabled="isLoadingSubmit" class="btn btn-primary fw-bold px-5 shadow-sm">
                <span v-if="isLoadingSubmit" class="spinner-border spinner-border-sm me-2" role="status"></span>
                <i v-else class="fas fa-save me-2"></i>
                {{ isLoadingSubmit ? 'Đang xử lý...' : (isEditing ? 'Lưu thay đổi' : 'Tạo tài khoản') }}
              </button>
            </div>

          </div>
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/utils/axios'
import AdminSidebar from '@/components/AdminSidebar.vue'

const isSidebarOpen = ref(false)
const tableVisible = ref(false)

const loadTable = async () => {
  tableVisible.value = true
  await fetchInitialData()
}

const hideTable = () => {
  tableVisible.value = false
  users.value = []
}

const users = ref([])
const availableRoles = ref([])
const showModal = ref(false)
const isEditing = ref(false)
const isLoading = ref(false)
const isLoadingSubmit = ref(false)
const showPassword = ref(false)
const searchTerm = ref('')
const formError = ref('')

const form = reactive({
  id: null,
  username: '',
  password: '',
  firstName: '',
  lastName: '',
  dob: '',
  roles: ['STAFF'],
})

// Max DOB: 18 years ago
const maxDobStr = computed(() => {
  const d = new Date()
  d.setFullYear(d.getFullYear() - 18)
  return d.toISOString().split('T')[0]
})

const filteredUsers = computed(() => {
  if (!searchTerm.value.trim()) return users.value
  const term = searchTerm.value.toLowerCase()
  return users.value.filter(u =>
    (u.username ?? '').toLowerCase().includes(term) ||
    ((u.firstName ?? '') + ' ' + (u.lastName ?? '')).toLowerCase().includes(term)
  )
})

// FIX: safe role count — guard roles ?? []
const countByRole = (roleName) =>
  users.value.filter(u => (u.roles ?? []).some(r => r.name === roleName)).length

const getRoleBadge = (roleName) => {
  switch (roleName) {
    case 'ADMIN':   return 'bg-danger-subtle text-danger'
    case 'STAFF':   return 'bg-primary-subtle text-primary'
    case 'USER':    return 'bg-success-subtle text-success'
    default:        return 'bg-secondary-subtle text-secondary'
  }
}

const GRADIENTS = [
  'linear-gradient(135deg,#2563eb,#7c3aed)',
  'linear-gradient(135deg,#0891b2,#2563eb)',
  'linear-gradient(135deg,#059669,#0891b2)',
  'linear-gradient(135deg,#d97706,#dc2626)',
  'linear-gradient(135deg,#7c3aed,#db2777)',
]

// FIX: safe getInitial — guard firstName null
const getInitial = (firstName, username) =>
  ((firstName ?? username ?? '?').charAt(0)).toUpperCase()

const getAvatarGradient = (username) =>
  GRADIENTS[(username ?? '').charCodeAt(0) % GRADIENTS.length]

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  try { return new Intl.DateTimeFormat('vi-VN').format(new Date(dateStr)) }
  catch { return dateStr }
}

const fetchInitialData = async () => {
  isLoading.value = true
  try {
    const [usersRes, rolesRes] = await Promise.all([
      api.get('/api/users'),
      api.get('/api/roles'),
    ])
    const allUsers = usersRes.data.result ?? []
    // FIX: guard roles with ?? [] before .some()
    users.value = allUsers.filter(u =>
      (u.roles ?? []).some(r => r.name === 'STAFF' || r.name === 'ADMIN')
    )
    availableRoles.value = rolesRes.data.result ?? []
  } catch (e) {
    console.error('Lỗi tải dữ liệu:', e)
  } finally {
    isLoading.value = false
  }
}

const submitForm = async () => {
  formError.value = ''
  if (!form.username.trim()) return (formError.value = 'Vui lòng nhập tên đăng nhập.')
  if (!isEditing.value && form.password.length < 6) return (formError.value = 'Mật khẩu phải có ít nhất 6 ký tự.')
  if (!form.firstName.trim() || !form.lastName.trim()) return (formError.value = 'Vui lòng nhập họ và tên.')
  if (!form.dob) return (formError.value = 'Vui lòng chọn ngày sinh.')
  if (form.roles.length === 0) return (formError.value = 'Vui lòng chọn ít nhất một vai trò.')

  isLoadingSubmit.value = true
  try {
    const payload = {
      firstName: form.firstName,
      lastName: form.lastName,
      dob: form.dob,
      roles: form.roles,
    }
    if (isEditing.value) {
      if (form.password) payload.password = form.password
      await api.put(`/api/users/${form.id}`, payload)
    } else {
      payload.username = form.username
      payload.password = form.password
      await api.post('/api/users', payload)
    }
    closeModal()
    fetchInitialData()
  } catch (error) {
    formError.value = error.response?.data?.message ?? error.message ?? 'Đã xảy ra lỗi.'
  } finally {
    isLoadingSubmit.value = false
  }
}

const deleteUser = async (id) => {
  if (!confirm('Chắc chắn muốn xóa nhân viên này? Hành động này không thể hoàn tác.')) return
  try {
    await api.delete(`/api/users/${id}`)
    fetchInitialData()
  } catch (error) {
    alert('Lỗi khi xóa: ' + (error.response?.data?.message ?? error.message))
  }
}

const openCreateModal = () => {
  isEditing.value = false
  showPassword.value = false
  formError.value = ''
  Object.assign(form, { id: null, username: '', password: '', firstName: '', lastName: '', dob: '', roles: ['STAFF'] })
  showModal.value = true
}

const openEditModal = (user) => {
  isEditing.value = true
  showPassword.value = false
  formError.value = ''
  Object.assign(form, {
    id: user.id,
    username: user.username ?? '',
    password: '',
    firstName: user.firstName ?? '',
    lastName: user.lastName ?? '',
    dob: user.dob ?? '',
    // FIX: guard roles before .map()
    roles: (user.roles ?? []).map(r => r.name),
  })
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formError.value = ''
}

onMounted(() => {})
</script>

<style scoped>
/* ===== LAYOUT ===== */
.admin-page-wrapper { display:flex; height:100vh; background:#f1f5f9; overflow:hidden; font-family:'Segoe UI',system-ui,sans-serif; }
.admin-content-area { flex:1; display:flex; flex-direction:column; overflow:hidden; min-width:0; }
.admin-topbar { height:68px; background:#fff; box-shadow:0 1px 6px rgba(0,0,0,.07); display:flex; align-items:center; justify-content:space-between; padding:0 1.5rem; flex-shrink:0; z-index:10; }
.admin-main-scroll { flex:1; overflow-y:auto; overflow-x:hidden; }

/* ===== STAT ICONS ===== */
.stat-icon { width:44px;height:44px;border-radius:12px;display:flex;align-items:center;justify-content:center;font-size:1rem;flex-shrink:0; }
.bg-primary-soft { background:#dbeafe!important; }
.bg-danger-soft  { background:#fee2e2!important; }
.bg-success-soft { background:#dcfce7!important; }
.bg-secondary-soft { background:#f1f5f9!important; }

/* ===== EMPLOYEE AVATAR ===== */
.emp-avatar {
  width: 40px; height: 40px;
  border-radius: 50%;
  color: #fff;
  font-size: 1rem;
  font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

/* ===== TABLE ===== */
.table th { font-size:.72rem; }
.table td { font-size:.88rem; }
.bg-primary-subtle   { background:#dbeafe!important; }
.bg-success-subtle   { background:#dcfce7!important; }
.bg-danger-subtle    { background:#fee2e2!important; }
.bg-secondary-subtle { background:#f1f5f9!important; }

/* ===== MODAL ===== */
.emp-modal-overlay {
  position: fixed; inset: 0;
  background: rgba(15,23,42,.65);
  backdrop-filter: blur(4px);
  z-index: 1055;
  display: flex; align-items: center; justify-content: center;
  padding: 1rem;
}
.emp-modal-dialog {
  background: #fff;
  border-radius: 1rem;
  width: 100%;
  max-width: 540px;
  max-height: 92vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 64px rgba(0,0,0,.28);
  overflow: hidden;
}
.emp-modal-header { display:flex;justify-content:space-between;align-items:center;padding:1.25rem 1.5rem;border-bottom:1px solid #e5e7eb;flex-shrink:0; }
.emp-modal-body { flex:1;overflow-y:auto;padding:1.5rem;scrollbar-width:thin;scrollbar-color:#cbd5e1 transparent; }
.emp-modal-footer { display:flex;justify-content:flex-end;gap:.75rem;padding:1rem 1.5rem;border-top:1px solid #e5e7eb;background:#f8fafc;flex-shrink:0; }

.modal-header-icon { width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;font-size:1.1rem;flex-shrink:0; }

/* ===== INPUT STYLING ===== */
.input-group-text { border-color:#e2e8f0; }
.form-control, .form-select { border-color:#e2e8f0; }
.input-group:focus-within .input-group-text,
.input-group:focus-within .form-control { border-color:#2563eb; }
.input-group:focus-within .form-control { box-shadow:none; }

/* ===== ROLE CHECKBOX PILLS ===== */
.role-check-label {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  border: 2px solid #e5e7eb;
  border-radius: 50px;
  font-size: .82rem;
  font-weight: 600;
  color: #6b7280;
  cursor: pointer;
  user-select: none;
  transition: all .2s;
  background: #fff;
}
.role-check-label:hover { border-color:#93c5fd; color:#1d4ed8; background:#eff6ff; }
.role-check-label--active { background:#dbeafe; border-color:#2563eb; color:#1d4ed8; }

/* ===== TRANSITIONS ===== */
.modal-fade-enter-active,.modal-fade-leave-active{transition:opacity .25s ease}
.modal-fade-enter-from,.modal-fade-leave-to{opacity:0}
@keyframes popIn{from{opacity:0;transform:scale(.94) translateY(12px)}to{opacity:1;transform:scale(1) translateY(0)}}
.animate-pop{animation:popIn .3s cubic-bezier(.16,1,.3,1)}
.slide-down-enter-active,.slide-down-leave-active{transition:all .25s ease}
.slide-down-enter-from,.slide-down-leave-to{opacity:0;transform:translateY(-6px)}
</style>