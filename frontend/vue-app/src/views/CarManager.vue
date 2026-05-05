<template>
  <!-- Root: dùng flex row, height 100vh -->
  <div class="admin-page-wrapper">

    <AdminSidebar :isOpen="isSidebarOpen" @close="isSidebarOpen = false" />

    <div class="admin-content-area">

      <!-- Header -->
      <header class="admin-topbar">
        <div class="d-flex align-items-center gap-3">
          <button @click="isSidebarOpen = true" class="btn btn-light d-md-none border-0 p-2">
            <i class="fas fa-bars text-secondary fs-5"></i>
          </button>
          <h1 class="h5 fw-bold text-dark mb-0">Quản lý Sản phẩm</h1>
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

      <!-- Main content -->
      <div class="admin-main-scroll p-4">

        <!-- Toolbar -->
        <div class="d-flex flex-column flex-sm-row justify-content-between align-items-center mb-4 gap-3">
          <button
            @click="openCreateModal()"
            class="btn btn-primary fw-semibold d-flex align-items-center gap-2 shadow-sm"
          >
            <i class="fas fa-plus"></i> Thêm Xe Mới
          </button>
          <div class="position-relative w-100" style="max-width:340px">
            <input
              type="text"
              v-model="searchTerm"
              placeholder="Tìm kiếm trong trang hiện tại..."
              class="form-control ps-5"
            />
            <i class="fas fa-search position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%)"></i>
          </div>
        </div>

        <!-- Empty state -->
        <div
          v-if="!showList"
          class="card border-0 shadow-sm rounded-4 d-flex flex-column align-items-center justify-content-center text-center p-5"
          style="min-height:420px"
        >
          <div class="bg-indigo-soft p-4 rounded-circle mb-4" style="background:#eef2ff">
            <i class="fas fa-car-side text-indigo fa-4x" style="color:#818cf8"></i>
          </div>
          <h4 class="fw-bold text-dark mb-2">Quản lý kho xe chuyên nghiệp</h4>
          <p class="text-secondary mb-4" style="max-width:400px">Hệ thống đã sẵn sàng. Nhấn nút bên dưới để bắt đầu truy xuất dữ liệu từ máy chủ.</p>
          <button
            @click="loadCarList(0)"
            class="btn btn-indigo btn-lg fw-bold d-flex align-items-center gap-2"
            style="background:#4f46e5;color:#fff"
          >
            <i class="fas fa-cloud-download-alt"></i> Tải danh sách ô tô
          </button>
        </div>

        <!-- Table -->
        <div v-else class="card border-0 shadow-sm rounded-4 overflow-hidden">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th class="px-4 py-3 text-center small text-uppercase text-secondary fw-bold" style="width:60px">ID</th>
                  <th class="px-4 py-3 text-center small text-uppercase text-secondary fw-bold" style="width:110px">Hình ảnh</th>
                  <th class="px-4 py-3 small text-uppercase text-secondary fw-bold">Tên Phiên Bản</th>
                  <th class="px-4 py-3 text-center small text-uppercase text-secondary fw-bold">Đặc tính</th>
                  <th class="px-4 py-3 text-end small text-uppercase text-secondary fw-bold">Giá Niêm Yết</th>
                  <th class="px-4 py-3 text-center small text-uppercase text-secondary fw-bold">Trạng thái</th>
                  <th class="px-4 py-3 text-center small text-uppercase text-secondary fw-bold" style="width:140px">Hành động</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="7" class="text-center py-5">
                    <div class="spinner-border text-primary mb-2" role="status"></div>
                    <p class="text-secondary mb-0">Đang tải dữ liệu...</p>
                  </td>
                </tr>
                <tr v-else-if="filteredVariants.length === 0">
                  <td colspan="7" class="text-center py-5 text-secondary fw-medium">Không tìm thấy dữ liệu.</td>
                </tr>
                <tr v-else v-for="variant in filteredVariants" :key="variant.id">
                  <td class="px-4 py-3 text-center text-secondary fw-bold">#{{ variant.id }}</td>
                  <td class="px-4 py-3 text-center">
                    <div class="mx-auto border rounded-3 overflow-hidden bg-white" style="width:80px;height:56px;padding:4px">
                      <img :src="getImageUrl(variant.firstImageUrl)" class="w-100 h-100" style="object-fit:contain" @error="handleImageError">
                    </div>
                  </td>
                  <td class="px-4 py-3">
                    <div class="fw-bold text-dark mb-1">{{ variant.name }}</div>
                    <span class="badge rounded-pill text-bg-primary-subtle text-primary small fw-semibold">{{ variant.modelName || 'N/A' }}</span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="d-flex flex-column align-items-center gap-1">
                      <span v-if="variant.seatCapacity" class="badge rounded-pill bg-light text-secondary border small"><i class="fas fa-users me-1"></i>{{ variant.seatCapacity }} chỗ</span>
                      <span v-if="variant.fuelType" class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle small"><i class="fas fa-gas-pump me-1"></i>{{ variant.fuelType }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3 text-end fw-bold font-monospace text-primary">{{ formatPrice(variant.price) }}</td>
                  <td class="px-4 py-3 text-center">
                    <span :class="['badge rounded-pill fw-bold', variant.isActive ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger']">
                      {{ variant.isActive ? 'Đang bán' : 'Tạm ẩn' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="d-flex align-items-center justify-content-center gap-1">
                      <button @click="openDetailModal(variant.id)" class="btn btn-sm btn-light text-secondary" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button @click="openEditModal(variant.id)" class="btn btn-sm btn-light text-primary" title="Sửa">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button @click="deleteVariant(variant.id)" class="btn btn-sm btn-light text-danger" title="Xóa">
                        <i class="fas fa-trash-alt"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- Pagination -->
          <div class="px-4 py-3 border-top d-flex justify-content-between align-items-center small text-secondary bg-white">
            <span>Hiển thị trang <strong class="text-dark">{{ currentPage }}</strong> (tối đa {{ itemsPerPage }} xe/trang)</span>
            <div class="d-flex align-items-center gap-1">
              <button @click="changePage(currentPage - 1)" :disabled="currentPage <= 1 || isLoading" class="btn btn-sm btn-outline-secondary">
                <i class="fas fa-chevron-left me-1 small"></i>Trước
              </button>
              <span class="px-3 fw-bold text-dark">{{ currentPage }} / {{ totalPages || 1 }}</span>
              <button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages || isLoading" class="btn btn-sm btn-outline-secondary">
                Sau<i class="fas fa-chevron-right ms-1 small"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ============================
         MODAL: Thêm / Sửa xe
    ============================= -->
    <Teleport to="body">
      <div v-if="showModal" class="car-modal-overlay" @click.self="closeModal">
        <div class="car-modal-dialog animate-slide-up">

          <!-- Modal Header -->
          <div class="px-4 py-3 border-bottom d-flex justify-content-between align-items-center bg-white flex-shrink-0 rounded-top-4">
            <h5 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
              <i class="fas fa-car text-primary"></i>
              {{ isEditing ? 'Cập Nhật Thông Tin Phiên Bản' : 'Khai Báo Phiên Bản Xe Mới' }}
            </h5>
            <button @click="closeModal" class="btn-close"></button>
          </div>

          <!-- Modal Body -->
          <div class="p-4 overflow-auto flex-grow-1 modal-body-scroll">
            <form @submit.prevent="submitForm">

              <!-- Section 1: Thông tin cơ bản -->
              <div class="card border-0 bg-white shadow-sm rounded-3 p-4 mb-4">
                <h6 class="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
                  <span class="section-num">1</span> Thông tin cơ bản & Mức giá
                </h6>
                <div class="row g-3">
                  <div class="col-12 col-md-6 col-lg-4">
                    <label class="form-label fw-semibold small">Tên Phiên Bản <span class="text-danger">*</span></label>
                    <input v-model="form.name" type="text" required class="form-control" placeholder="VD: VF 8 Eco">
                  </div>
                  <div class="col-12 col-md-6 col-lg-4">
                    <label class="form-label fw-semibold small">Thuộc Dòng Xe <span class="text-danger">*</span></label>
                    <select v-model="form.modelId" required class="form-select">
                      <option :value="null" disabled>-- Lựa chọn dòng xe --</option>
                      <option v-for="model in carModels" :key="model.id" :value="model.id">{{ model.name }}</option>
                    </select>
                  </div>
                  <div class="col-12 col-md-6 col-lg-4">
                    <label class="form-label fw-semibold small">Trạng Thái Kinh Doanh</label>
                    <select v-model="form.isActive" class="form-select">
                      <option :value="true">Đang kinh doanh</option>
                      <option :value="false">Ngừng kinh doanh</option>
                    </select>
                  </div>
                  <div class="col-12 col-md-6 col-lg-4">
                    <label class="form-label fw-semibold small">Giá Niêm Yết <span class="text-danger">*</span></label>
                    <div class="input-group">
                      <input v-model.number="form.price" type="number" required min="0" class="form-control font-monospace fw-bold text-primary" placeholder="0">
                      <span class="input-group-text text-secondary fw-bold">VNĐ</span>
                    </div>
                  </div>
                  <div class="col-12 col-md-6 col-lg-4">
                    <label class="form-label fw-semibold small">Giá Khuyến Mãi</label>
                    <div class="input-group">
                      <input v-model.number="form.discountedPrice" type="number" min="0" class="form-control font-monospace fw-bold text-success" placeholder="0">
                      <span class="input-group-text text-secondary fw-bold">VNĐ</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Section 2: Thông số kỹ thuật -->
              <div class="card border-0 bg-white shadow-sm rounded-3 p-4 mb-4">
                <h6 class="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
                  <span class="section-num">2</span> Cấu hình & Thông số kỹ thuật
                </h6>

                <!-- Sub: Kích thước -->
                <div class="spec-group-header bg-light mb-3">
                  <span class="spec-group-bar"></span>Đặc tính chung & Kích thước
                </div>
                <div class="row g-3 mb-3">
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Số chỗ ngồi</label><input v-model.number="form.seatCapacity" type="number" class="form-control form-control-sm" placeholder="5"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Nhiên liệu</label><input v-model="form.fuelType" type="text" class="form-control form-control-sm" placeholder="Điện"></div>
                  <div class="col-12 col-md-6">
                    <label class="form-label small fw-medium">Dài × Rộng × Cao (mm)</label>
                    <div class="input-group input-group-sm">
                      <input v-model.number="form.length" type="number" class="form-control text-center" placeholder="Dài">
                      <span class="input-group-text">×</span>
                      <input v-model.number="form.width" type="number" class="form-control text-center" placeholder="Rộng">
                      <span class="input-group-text">×</span>
                      <input v-model.number="form.height" type="number" class="form-control text-center" placeholder="Cao">
                    </div>
                  </div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Chiều dài cơ sở (mm)</label><input v-model.number="form.wheelbase" type="number" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Khoảng sáng gầm (mm)</label><input v-model.number="form.groundClearance" type="number" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Kích thước Mâm (inch)</label><input v-model.number="form.wheelSize" type="number" class="form-control form-control-sm"></div>
                </div>

                <!-- Sub: Vận hành -->
                <div class="spec-group-header spec-group-header--indigo mb-3 mt-2">
                  <span class="spec-group-bar spec-group-bar--indigo"></span>Hệ thống Vận hành & Pin
                </div>
                <div class="row g-3 mb-3">
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Công suất tối đa (hp)</label><input v-model.number="form.maxPower" type="number" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Mô-men xoắn (Nm)</label><input v-model.number="form.maxTorque" type="number" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Dung lượng Pin (kWh)</label><input v-model.number="form.battery" type="number" step="0.1" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Quãng đường chuẩn (km)</label><input v-model.number="form.rangeNedc" type="number" class="form-control form-control-sm fw-bold text-success"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Hệ dẫn động</label><input v-model="form.drivetrain" type="text" class="form-control form-control-sm" placeholder="AWD, FWD..."></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Chế độ lái</label><input v-model="form.driveModes" type="text" class="form-control form-control-sm" placeholder="Eco, Sport..."></div>
                  <div class="col-12 col-md-6"><label class="form-label small fw-medium">Thời gian sạc</label><input v-model="form.chargeTimeDetail" type="text" class="form-control form-control-sm" placeholder="VD: 10% → 70% trong 24 phút"></div>
                </div>

                <!-- Sub: Tiện nghi -->
                <div class="spec-group-header mb-3 mt-2">
                  <span class="spec-group-bar"></span>Nội thất & Tiện nghi
                </div>
                <div class="row g-3">
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Màn hình giải trí (inch)</label><input v-model.number="form.entertainmentScreen" type="number" step="0.1" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Hệ thống loa</label><input v-model="form.speakerSystem" type="text" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Hệ thống điều hòa</label><input v-model="form.airConditioningSystem" type="text" class="form-control form-control-sm"></div>
                  <div class="col-6 col-md-3"><label class="form-label small fw-medium">Tùy chỉnh ghế lái</label><input v-model="form.driverSeatAdjustment" type="text" class="form-control form-control-sm" placeholder="Chỉnh điện 8 hướng..."></div>
                </div>
              </div>

              <!-- Section 3: Hình ảnh -->
              <div class="card border-0 bg-white shadow-sm rounded-3 p-4 mb-4">
                <h6 class="fw-bold text-dark mb-4 d-flex align-items-center gap-2">
                  <span class="section-num">3</span> Thư viện hình ảnh
                </h6>

                <!-- Existing images -->
                <div v-if="isEditing && currentImages.length > 0" class="mb-4 bg-light rounded-3 p-3">
                  <p class="small fw-bold text-secondary mb-3">Hình ảnh đã tải lên:</p>
                  <div class="d-flex flex-wrap gap-3">
                    <div v-for="img in currentImages" :key="img.id" class="position-relative">
                      <img :src="getImageUrl(img.imageUrl)" class="rounded-3 border" style="height:90px;width:130px;object-fit:cover" @error="handleImageError">
                      <button type="button" @click="deleteImage(img.id)"
                        class="btn btn-danger btn-sm position-absolute d-flex align-items-center justify-content-center p-0 rounded-circle"
                        style="width:24px;height:24px;top:-8px;right:-8px;font-size:0.7rem"
                      >
                        <i class="fas fa-times"></i>
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Upload zone -->
                <div
                  class="upload-zone rounded-3 p-4 text-center"
                  :class="{ 'upload-zone--has-files': selectedFiles.length > 0 }"
                  @click="$refs.fileInput.click()"
                >
                  <input ref="fileInput" type="file" multiple accept="image/*" class="d-none" @change="handleFileSelect">

                  <div v-if="selectedFiles.length === 0" class="py-3">
                    <div class="upload-icon mx-auto mb-3">
                      <i class="fas fa-cloud-upload-alt fa-2x text-primary"></i>
                    </div>
                    <p class="fw-bold text-dark mb-1">Kéo thả hoặc nhấn để tải ảnh lên</p>
                    <p class="small text-secondary mb-0">Hỗ trợ JPG, PNG, WEBP. Có thể chọn nhiều file.</p>
                  </div>

                  <div v-else class="row g-2">
                    <div v-for="(preview, index) in imagePreviews" :key="index" class="col-4 col-md-2">
                      <div class="position-relative">
                        <img :src="preview" class="w-100 rounded-2 border" style="height:70px;object-fit:cover">
                        <span class="badge bg-primary position-absolute" style="top:4px;right:4px;font-size:0.6rem">Mới</span>
                      </div>
                    </div>
                    <div class="col-4 col-md-2 d-flex align-items-center justify-content-center border border-dashed rounded-2 text-primary small fw-bold" style="height:70px;cursor:pointer">
                      <div class="text-center"><i class="fas fa-plus d-block mb-1"></i>Thêm ảnh</div>
                    </div>
                  </div>
                </div>
              </div>

            </form>
          </div>

          <!-- Modal Footer -->
          <div class="px-4 py-3 border-top bg-light d-flex justify-content-end gap-3 flex-shrink-0 rounded-bottom-4">
            <button @click="closeModal" class="btn btn-outline-secondary fw-semibold px-4">Hủy bỏ</button>
            <button @click="submitForm" :disabled="isLoadingSubmit" class="btn btn-primary fw-bold px-5">
              <span v-if="isLoadingSubmit" class="spinner-border spinner-border-sm me-2" role="status"></span>
              <i v-else class="fas fa-save me-2"></i>
              {{ isLoadingSubmit ? 'Đang xử lý...' : 'Lưu & Upload' }}
            </button>
          </div>

        </div>
      </div>
    </Teleport>

    <!-- ============================
         MODAL: Chi tiết xe
    ============================= -->
    <Teleport to="body">
      <div v-if="showDetailModal" class="car-modal-overlay" @click.self="showDetailModal = false">
        <div class="car-modal-dialog car-modal-dialog--md animate-slide-up">
          <div v-if="isLoadingDetail" class="d-flex justify-content-center align-items-center" style="height:300px">
            <div class="spinner-border text-primary" role="status"></div>
          </div>
          <template v-else>
            <div class="position-relative" style="height:220px;overflow:hidden">
              <img :src="getDetailHeroImage()" class="w-100 h-100" style="object-fit:contain;background:#f1f5f9" @error="handleImageError">
              <div class="position-absolute bottom-0 start-0 end-0 p-4" style="background:linear-gradient(to top,rgba(0,0,0,0.75),transparent)">
                <span class="badge bg-primary mb-1">{{ selectedVariant?.modelName }}</span>
                <h4 class="text-white fw-bold mb-0">{{ selectedVariant?.name }}</h4>
                <p class="text-white font-monospace fw-bold mb-0">{{ formatPrice(selectedVariant?.price) }}</p>
              </div>
              <button @click="showDetailModal = false" class="btn-close btn-close-white position-absolute top-0 end-0 m-3"></button>
            </div>
            <div class="p-4 overflow-auto modal-body-scroll">
              <h6 class="fw-bold mb-4 d-flex align-items-center gap-2">
                <i class="fas fa-info-circle text-primary"></i> Thông số kỹ thuật chi tiết
              </h6>
              <div class="row g-3 text-sm">
                <div class="col-6 col-md-4" v-for="(spec, i) in detailSpecs" :key="i">
                  <div class="detail-spec-row">
                    <span class="text-secondary small d-block">{{ spec.label }}</span>
                    <strong :class="spec.cls">{{ spec.value }}</strong>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/utils/axios'
import { useRouter } from 'vue-router'
import VinfastLogo from "@/assets/image/icon_logo/VinFast-logo.svg"
import AdminSidebar from '@/components/AdminSidebar.vue'

const router = useRouter()

const showList = ref(false)
const isSidebarOpen = ref(false)
const variants = ref([])
const carModels = ref([])
const showModal = ref(false)
const showDetailModal = ref(false)
const isEditing = ref(false)
const isLoading = ref(false)
const isLoadingDetail = ref(false)
const isLoadingSubmit = ref(false)
const searchTerm = ref('')
const selectedVariant = ref(null)
const currentPage = ref(1)
const totalPages = ref(1)
const itemsPerPage = ref(10)
const selectedFiles = ref([])
const imagePreviews = ref([])
const currentImages = ref([])

const form = reactive({
  id: null, modelId: null, name: '', price: 0, discountedPrice: 0, isActive: true,
  seatCapacity: null, fuelType: '',
  length: null, width: null, height: null, wheelbase: null, groundClearance: null, wheelSize: null,
  maxPower: null, maxTorque: null, battery: null, rangeNedc: null,
  drivetrain: '', driveModes: '', entertainmentScreen: null, speakerSystem: '',
  airConditioningSystem: '', driverSeatAdjustment: '', suspensionSystem: '', brakeSystem: '',
  frontLightType: '', trunkAccess: '', chargeTimeDetail: ''
})

const filteredVariants = computed(() => {
  if (!searchTerm.value) return variants.value
  const term = searchTerm.value.toLowerCase()
  return variants.value.filter(v =>
    v.name.toLowerCase().includes(term) || (v.modelName && v.modelName.toLowerCase().includes(term))
  )
})

const detailSpecs = computed(() => {
  const v = selectedVariant.value
  if (!v) return []
  return [
    { label: 'Số chỗ ngồi', value: `${v.seatCapacity ?? '—'} chỗ`, cls: 'text-primary' },
    { label: 'Loại nhiên liệu', value: v.fuelType || '—', cls: '' },
    { label: 'Dài × Rộng × Cao', value: `${v.length} × ${v.width} × ${v.height} mm`, cls: '' },
    { label: 'Chiều dài cơ sở', value: `${v.wheelbase ?? '—'} mm`, cls: '' },
    { label: 'Khoảng sáng gầm', value: `${v.groundClearance ?? '—'} mm`, cls: '' },
    { label: 'Công suất tối đa', value: `${v.maxPower ?? '—'} hp`, cls: '' },
    { label: 'Mô-men xoắn', value: `${v.maxTorque ?? '—'} Nm`, cls: '' },
    { label: 'Dung lượng Pin', value: `${v.battery ?? '—'} kWh`, cls: '' },
    { label: 'Quãng đường (NEDC)', value: `${v.rangeNedc ?? '—'} km`, cls: 'text-success' },
    { label: 'Hệ dẫn động', value: v.drivetrain || '—', cls: '' },
    { label: 'Thời gian sạc', value: v.chargeTimeDetail || '—', cls: '' },
    { label: 'Màn hình giải trí', value: `${v.entertainmentScreen ?? '—'} inch`, cls: '' },
    { label: 'Hệ thống loa', value: v.speakerSystem || '—', cls: '' },
    { label: 'Điều hòa', value: v.airConditioningSystem || '—', cls: '' },
    { label: 'Ghế lái', value: v.driverSeatAdjustment || '—', cls: '' },
    { label: 'Cốp xe', value: v.trunkAccess || '—', cls: '' },
  ]
})

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0)

const getImageUrl = (path) => {
  if (!path) return VinfastLogo
  if (path.startsWith('http') || path.startsWith('blob:')) return path
  return `http://localhost:8080${path.startsWith('/') ? '' : '/'}${path}`
}
const getDetailHeroImage = () => {
  if (selectedVariant.value?.images?.length) return getImageUrl(selectedVariant.value.images[0].imageUrl)
  return VinfastLogo
}
const handleImageError = (e) => { e.target.src = VinfastLogo }

const fetchModelsOnly = async () => {
  try {
    const res = await api.get('/api/models')
    carModels.value = res.data.result || []
  } catch (e) { console.error('Lỗi lấy models:', e) }
}

const loadCarList = async (pageIndex = 0) => {
  isLoading.value = true
  showList.value = true
  try {
    const res = await api.get('/api/variants', { params: { page: pageIndex, size: itemsPerPage.value } })
    
    // pageData chính là cục "result"
    const pageData = res.data.result

    if (pageData) {
      // 1. Lấy danh sách xe
      variants.value = pageData.content || []
      
      // 2. Trỏ đúng vào object "page" để lấy thông số phân trang
      if (pageData.page) {
        totalPages.value = pageData.page.totalPages || 1
        currentPage.value = pageData.page.number + 1
      } else {
        // Dự phòng nếu API trả về cấu trúc cũ
        totalPages.value = pageData.totalPages || 1
        const actualPage = pageData.number ?? pageIndex
        currentPage.value = actualPage + 1
      }
    } else {
      variants.value = []
      totalPages.value = 1
      currentPage.value = 1
    }
    
  } catch (e) {
    console.error('Lỗi khi gọi API:', e)
    alert('Lỗi khi tải dữ liệu từ máy chủ.')
  } finally {
    isLoading.value = false
  }
}
const changePage = (p) => { if (p >= 1 && p <= totalPages.value) loadCarList(p - 1) }

const fetchVariantDetail = async (id) => {
  try {
    const res = await api.get(`/api/variants/${id}/details`)
    return res.data.result
  } catch { return null }
}

const openCreateModal = () => {
  isEditing.value = false
  Object.keys(form).forEach(k => form[k] = (k === 'isActive' ? true : (k === 'price' ? 0 : null)))
  form.name = ''; form.fuelType = ''; form.drivetrain = ''; form.driveModes = '';
  form.speakerSystem = ''; form.airConditioningSystem = ''; form.driverSeatAdjustment = '';
  form.suspensionSystem = ''; form.brakeSystem = ''; form.frontLightType = '';
  form.trunkAccess = ''; form.chargeTimeDetail = '';
  selectedFiles.value = []; imagePreviews.value = []; currentImages.value = []
  showModal.value = true
}

const openEditModal = async (id) => {
  isEditing.value = true
  showModal.value = true
  const detail = await fetchVariantDetail(id)
  if (detail) {
    Object.assign(form, detail)
    form.modelId = detail.carModel?.id || null
    if (!form.modelId && detail.modelName) {
      const m = carModels.value.find(x => x.name === detail.modelName)
      if (m) form.modelId = m.id
    }
    currentImages.value = detail.images || []
    selectedFiles.value = []; imagePreviews.value = []
  } else closeModal()
}

const openDetailModal = async (id) => {
  showDetailModal.value = true
  isLoadingDetail.value = true
  const detail = await fetchVariantDetail(id)
  if (detail) selectedVariant.value = detail
  isLoadingDetail.value = false
}

const closeModal = () => {
  showModal.value = false
  imagePreviews.value.forEach(url => URL.revokeObjectURL(url))
  imagePreviews.value = []
  selectedFiles.value = []
}

const handleFileSelect = (e) => {
  const files = Array.from(e.target.files)
  if (!files.length) return
  selectedFiles.value = [...selectedFiles.value, ...files]
  imagePreviews.value = [...imagePreviews.value, ...files.map(f => URL.createObjectURL(f))]
}

const submitForm = async () => {
  if (!form.name || !form.price || !form.modelId) return alert('Vui lòng điền đủ tên, giá và dòng xe!')
  isLoadingSubmit.value = true
  try {
    let variantId = form.id
    const payload = { ...form }
    delete payload.images
    if (isEditing.value) {
      await api.put(`/api/variants/${variantId}`, payload)
    } else {
      const res = await api.post('/api/variants', payload)
      variantId = res.data.result.id
    }
    if (selectedFiles.value.length > 0 && variantId) {
      await Promise.all(selectedFiles.value.map(file => {
        const fd = new FormData()
        fd.append('file', file)
        return api.post(`/api/images/upload/${variantId}`, fd, { headers: { 'Content-Type': 'multipart/form-data' } })
      }))
    }
    if (showList.value) await loadCarList(currentPage.value - 1)
    closeModal()
    alert(isEditing.value ? 'Cập nhật thành công!' : 'Thêm mới thành công!')
  } catch (error) {
    alert('Lỗi: ' + (error.response?.data?.message ?? error.message))
  } finally {
    isLoadingSubmit.value = false
  }
}

const deleteImage = async (id) => {
  if (!confirm('Xóa ảnh này?')) return
  try {
    await api.delete(`/api/images/${id}`)
    currentImages.value = currentImages.value.filter(i => i.id !== id)
  } catch { alert('Lỗi xóa ảnh') }
}

const deleteVariant = async (id) => {
  if (!confirm('Chắc chắn xóa phiên bản xe này?')) return
  try {
    await api.delete(`/api/variants/${id}`)
    
    // Tính toán lại trang cần load sau khi xóa
    let targetPage = currentPage.value - 1;
    // Nếu trang hiện tại chỉ còn 1 item và không phải trang 1, lùi về 1 trang
    if (variants.value.length === 1 && targetPage > 0) {
      targetPage -= 1;
    }
    
    await loadCarList(targetPage)
  } catch { 
    alert('Lỗi xóa xe') 
  }
}

onMounted(() => fetchModelsOnly())
</script>

<style scoped>
/* ==============================
   PAGE LAYOUT
============================== */
.admin-page-wrapper {
  display: flex;
  height: 100vh;
  background: #f1f5f9;
  overflow: hidden;
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.admin-content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0; /* Prevent flex overflow */
}

.admin-topbar {
  height: 68px;
  background: #fff;
  box-shadow: 0 1px 6px rgba(0,0,0,0.07);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  flex-shrink: 0;
  z-index: 10;
}

.admin-main-scroll {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

/* ==============================
   MODAL OVERLAY + DIALOG
============================== */
.car-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.65);
  backdrop-filter: blur(4px);
  z-index: 1055;       /* Trên Bootstrap navbar (1030) và sidebar (200) */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.car-modal-dialog {
  background: #f8fafc;
  border-radius: 1rem;
  width: 100%;
  max-width: 900px;
  max-height: 92vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 64px rgba(0,0,0,0.3);
  overflow: hidden;
}

.car-modal-dialog--md {
  max-width: 780px;
}

.modal-body-scroll {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}
.modal-body-scroll::-webkit-scrollbar { width: 5px; }
.modal-body-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }

/* ==============================
   SECTION NUMBERS
============================== */
.section-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #2563eb;
  color: #fff;
  border-radius: 50%;
  font-size: 0.75rem;
  font-weight: 800;
  flex-shrink: 0;
}

/* ==============================
   SPEC GROUP HEADERS
============================== */
.spec-group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.72rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #475569;
  padding: 6px 10px;
  border-radius: 6px;
  background: #f1f5f9;
}
.spec-group-header--indigo {
  background: #eef2ff;
  color: #4338ca;
}
.spec-group-bar {
  width: 3px;
  height: 14px;
  background: #94a3b8;
  border-radius: 2px;
  flex-shrink: 0;
}
.spec-group-bar--indigo { background: #818cf8; }

/* ==============================
   UPLOAD ZONE
============================== */
.upload-zone {
  border: 2px dashed #93c5fd;
  background: #f0f9ff;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}
.upload-zone:hover {
  border-color: #2563eb;
  background: #eff6ff;
}
.upload-zone--has-files {
  border-style: solid;
  border-color: #93c5fd;
  background: #fff;
}
.upload-icon {
  width: 56px; height: 56px;
  background: #dbeafe;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}

/* ==============================
   DETAIL SPEC ROW
============================== */
.detail-spec-row {
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}

/* ==============================
   BADGES
============================== */
.text-bg-primary-subtle { background: #dbeafe !important; }

/* ==============================
   ANIMATION
============================== */
@keyframes slideUp {
  from { opacity: 0; transform: translateY(24px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.animate-slide-up { animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
</style>