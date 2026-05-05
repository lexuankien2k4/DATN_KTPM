<template>
  <div class="consultation-root">

    <!-- Hero Banner -->
    <div class="consult-hero">
      <div class="consult-hero__overlay"></div>
      <div class="consult-hero__content">
        <span class="section-badge mb-3">Trải nghiệm VinFast</span>
        <h1 class="consult-hero__title">Đăng Ký Tư Vấn & Lái Thử</h1>
        <p class="consult-hero__sub">Khám phá công nghệ hiện đại, trải nghiệm cảm giác lái đẳng cấp — hoàn toàn miễn phí.</p>
      </div>
    </div>

    <div class="container py-5" style="max-width:1320px">
      <div class="row g-5">

        <!-- LEFT: Car Selection -->
        <div class="col-12 col-lg-8">

          <!-- Category tabs -->
          <div class="d-flex gap-2 mb-4 cat-tabs">
            <button
              v-for="cat in categories" :key="cat.id"
              @click="selectedCategory = cat.id"
              :class="['btn fw-bold rounded-pill px-4 py-2', selectedCategory === cat.id ? 'btn-primary shadow' : 'btn-outline-secondary']"
            >
              <i :class="['me-2', cat.icon]"></i>{{ cat.name }}
            </button>
          </div>

          <!-- Loading skeleton -->
          <div v-if="loading" class="row g-4">
            <div v-for="n in 6" :key="n" class="col-12 col-sm-6 col-xl-4">
              <div class="card border-0 shadow-sm rounded-4 skeleton-card">
                <div class="skeleton-img"></div>
                <div class="card-body"><div class="skeleton-line w-75 mb-2"></div><div class="skeleton-line w-50"></div></div>
              </div>
            </div>
          </div>

          <!-- Empty -->
          <div v-else-if="filteredCars.length === 0" class="card border-0 shadow-sm rounded-4 text-center py-5">
            <i class="fas fa-car-side fa-3x text-secondary mb-3 d-block"></i>
            <p class="text-secondary mb-0">Hiện không có xe thuộc danh mục này.</p>
          </div>

          <!-- Car cards -->
          <div v-else class="row g-4">
            <div
              v-for="car in filteredCars" :key="car.id"
              class="col-12 col-sm-6 col-xl-4"
            >
              <div
                @click="form.variant_id = car.id"
                :class="['card car-card border-2 rounded-4 overflow-hidden cursor-pointer h-100',
                  form.variant_id === car.id ? 'border-primary shadow car-card--selected' : 'border-light shadow-sm']"
              >
                <!-- Selected badge -->
                <div v-if="form.variant_id === car.id" class="selected-badge">
                  <i class="fas fa-check"></i>
                </div>

                <!-- Image -->
                <div class="car-card__img-wrap">
                  <img
                    :src="car.image || '/assets/image/icon_logo/VinFast-logo.svg'"
                    :alt="car.name"
                    class="car-card__img"
                    @error="e => e.target.src = '/assets/image/icon_logo/VinFast-logo.svg'"
                  >
                </div>

                <!-- Info -->
                <div class="card-body p-4">
                  <h5 class="fw-bold text-dark mb-1 car-card__name">{{ car.name }}</h5>
                  <!-- FIX: guard price before calling toLocaleString -->
                  <p class="fw-bold text-primary mb-3">
                    {{ car.price != null ? Number(car.price).toLocaleString('vi-VN') + ' VNĐ' : 'Liên hệ' }}
                  </p>
                  <div class="d-flex gap-3 small text-secondary border-top pt-2">
                    <span><i class="fas fa-users me-1"></i>{{ car.seats ?? 5 }} chỗ</span>
                    <span><i class="fas fa-bolt me-1"></i>{{ car.range ?? 'N/A' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- RIGHT: Form -->
        <div class="col-12 col-lg-4">
          <div class="card border-0 shadow-lg rounded-4 p-4 sticky-top" style="top:90px">
            <h4 class="fw-bold text-dark mb-1 d-flex align-items-center gap-2">
              <i class="fas fa-paper-plane text-primary"></i> Thông Tin Đăng Ký
            </h4>
            <p class="text-secondary small mb-4">Vui lòng chọn xe bên trái và điền thông tin.</p>

            <!-- Selected car preview -->
            <Transition name="fade">
              <div v-if="selectedCarObj" class="selected-preview rounded-3 p-3 mb-4 d-flex align-items-center gap-3">
                <img :src="selectedCarObj.image || '/assets/image/icon_logo/VinFast-logo.svg'" style="height:48px;object-fit:contain" alt="">
                <div>
                  <div class="fw-bold small text-dark">{{ selectedCarObj.name }}</div>
                  <div class="text-primary small fw-semibold">
                    {{ selectedCarObj.price != null ? Number(selectedCarObj.price).toLocaleString('vi-VN') + ' VNĐ' : 'Liên hệ' }}
                  </div>
                </div>
              </div>
            </Transition>

            <form @submit.prevent="submitForm" novalidate>
              <!-- Personal info -->
              <div class="mb-3">
                <label class="form-label fw-semibold small">Họ và tên <span class="text-danger">*</span></label>
                <input v-model.trim="form.customer_name" type="text" required class="form-control bg-light" placeholder="Nguyễn Văn A">
              </div>
              <div class="mb-3">
                <label class="form-label fw-semibold small">Số điện thoại <span class="text-danger">*</span></label>
                <input v-model.trim="form.phone_number" type="tel" required class="form-control bg-light" :class="{ 'is-invalid': phoneError }" placeholder="09xx xxx xxx" @blur="validatePhone">
                <div class="invalid-feedback">{{ phoneError }}</div>
              </div>
              <div class="mb-3">
                <label class="form-label fw-semibold small">Email</label>
                <input v-model.trim="form.email" type="email" class="form-control bg-light" placeholder="email@example.com">
              </div>

              <!-- Location -->
              <div class="mb-3">
                <label class="form-label fw-semibold small">Tỉnh / Thành phố <span class="text-danger">*</span></label>
                <select v-model="form.province" required class="form-select bg-light">
                  <option value="" disabled>Chọn tỉnh thành</option>
                  <option v-for="prov in provinces" :key="prov" :value="prov">{{ prov }}</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label fw-semibold small">Showroom <span class="text-danger">*</span></label>
                <select v-model="form.showroom_id" required :disabled="!form.province || filteredShowrooms.length === 0" class="form-select bg-light">
                  <option :value="null" disabled>{{ !form.province ? 'Chọn tỉnh trước' : 'Chọn showroom' }}</option>
                  <option v-for="room in filteredShowrooms" :key="room.id" :value="room.id">
                    {{ room.name }} – {{ room.address }}
                  </option>
                </select>
              </div>

              <!-- Schedule -->
              <div class="mb-3">
                <label class="form-label fw-semibold small">Lịch hẹn dự kiến</label>
                <input v-model="scheduledDateTime" type="datetime-local" class="form-control bg-light" :min="minDateTime">
              </div>

              <!-- Note -->
              <div class="mb-4">
                <label class="form-label fw-semibold small">Ghi chú</label>
                <textarea v-model="form.note" rows="3" class="form-control bg-light resize-none" placeholder="Tôi muốn lái thử vào cuối tuần..."></textarea>
              </div>

              <!-- Alert -->
              <Transition name="fade">
                <div v-if="statusMsg.text" :class="['alert py-2 d-flex align-items-center gap-2 mb-3', statusMsg.success ? 'alert-success' : 'alert-danger']" role="alert">
                  <i :class="['fas flex-shrink-0', statusMsg.success ? 'fa-check-circle' : 'fa-exclamation-circle']"></i>
                  <small>{{ statusMsg.text }}</small>
                </div>
              </Transition>

              <button
                type="submit"
                :disabled="isSubmitting || !form.variant_id"
                class="btn btn-primary btn-lg w-100 fw-bold shadow"
              >
                <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status"></span>
                <i v-else class="fas fa-paper-plane me-2"></i>
                {{ isSubmitting ? 'Đang gửi...' : 'Gửi Yêu Cầu Ngay' }}
              </button>
              <p v-if="!form.variant_id" class="text-danger text-center small mt-2 fst-italic">
                <i class="fas fa-info-circle me-1"></i>Vui lòng chọn xe ở danh sách bên cạnh!
              </p>
            </form>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const categories = [
  { id: 'ev',       name: 'Ô tô Điện',   icon: 'fas fa-bolt' },
  { id: 'gasoline', name: 'Ô tô Xăng',   icon: 'fas fa-gas-pump' },
  { id: 'service',  name: 'Xe Dịch vụ',  icon: 'fas fa-shuttle-van' },
]

const selectedCategory = ref('ev')
const loading = ref(true)
const isSubmitting = ref(false)
const allCars = ref([])
const showroomsData = ref([])
const provinces = ref([])
const phoneError = ref('')
const statusMsg = reactive({ text: '', success: false })
const scheduledDateTime = ref('')

const form = reactive({
  customer_name: '',
  phone_number: '',
  email: '',
  province: '',
  variant_id: null,
  showroom_id: null,
  scheduled_at: '',
  note: '',
})

// FIX: min datetime = now
const minDateTime = computed(() => new Date().toISOString().slice(0, 16))

const filteredCars = computed(() => allCars.value.filter(c => c.category === selectedCategory.value))

const filteredShowrooms = computed(() => {
  if (!form.province) return []
  return showroomsData.value.filter(s => s.province === form.province)
})

// FIX: safe lookup of selected car object
const selectedCarObj = computed(() => allCars.value.find(c => c.id === form.variant_id) ?? null)

const validatePhone = () => {
  phoneError.value = /^\d{10,11}$/.test(form.phone_number) ? '' : 'SĐT không hợp lệ (10-11 chữ số)'
}

const fetchData = async (url) => {
  try {
    const res = await fetch(url)
    const json = await res.json()
    return json.code === 1000 ? json.result : []
  } catch (e) {
    console.error('Fetch error:', url, e)
    return []
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [evRes, gasRes, serRes, roomRes] = await Promise.all([
      fetchData('http://localhost:8080/api/public/products/ev'),
      fetchData('http://localhost:8080/api/public/products/gasoline'),
      fetchData('http://localhost:8080/api/public/products/service'),
      fetchData('http://localhost:8080/api/public/showrooms'),
    ])

    // FIX: normalize with safe property access — handle both camelCase & snake_case
    const normalize = (list, cat) => (list ?? []).map(c => ({
      id: c.id,
      name: c.name ?? 'N/A',
      // FIX: price is a number — don't coerce to string here
      price: c.price ?? null,
      image: c.image ?? c.firstImageUrl ?? null,
      category: cat,
      // FIX: seats may come as seatCapacity
      seats: c.seats ?? c.seatCapacity ?? 5,
      // FIX: range may come as rangeNedc, range_nedc, or range
      range: (c.rangeNedc ?? c.range_nedc ?? c.range)
        ? `${c.rangeNedc ?? c.range_nedc ?? c.range} km`
        : 'N/A',
    }))

    allCars.value = [
      ...normalize(evRes, 'ev'),
      ...normalize(gasRes, 'gasoline'),
      ...normalize(serRes, 'service'),
    ]

    showroomsData.value = roomRes ?? []
    provinces.value = [...new Set((roomRes ?? []).map(s => s.province).filter(Boolean))]
  } catch (err) {
    console.error('Lỗi tải dữ liệu ban đầu:', err)
  } finally {
    loading.value = false
  }
})

watch(() => form.province, () => {
  form.showroom_id = null
})

const submitForm = async () => {
  if (!form.variant_id) { statusMsg.text = 'Vui lòng chọn xe!'; statusMsg.success = false; return }
  validatePhone()
  if (phoneError.value) return

  if (scheduledDateTime.value) {
    form.scheduled_at = new Date(scheduledDateTime.value).toISOString()
  }

  isSubmitting.value = true
  statusMsg.text = ''

  try {
    const response = await fetch('http://localhost:8080/api/public/consultations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    })
    const result = await response.json()
    if (result.code === 1000) {
      statusMsg.success = true
      statusMsg.text = '🎉 Đăng ký thành công! Chúng tôi sẽ liên hệ sớm nhất.'
      Object.assign(form, { customer_name: '', phone_number: '', email: '', province: '', variant_id: null, showroom_id: null, scheduled_at: '', note: '' })
      scheduledDateTime.value = ''
      setTimeout(() => router.push('/'), 2000)
    } else {
      statusMsg.success = false
      statusMsg.text = result.message || 'Gửi yêu cầu thất bại. Vui lòng thử lại.'
    }
  } catch (error) {
    console.error(error)
    statusMsg.success = false
    statusMsg.text = 'Lỗi kết nối server.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');
.consultation-root { font-family: 'Be Vietnam Pro', sans-serif; background: #f8fafc; min-height: 100vh; }

/* Hero */
.consult-hero {
  position: relative;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 60%, #1d4ed8 100%);
  padding: 5rem 1.5rem;
  text-align: center;
  overflow: hidden;
}
.consult-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}
.consult-hero__overlay { position: absolute; inset: 0; background: radial-gradient(ellipse at 50% 50%, rgba(37,99,235,0.2) 0%, transparent 70%); }
.consult-hero__content { position: relative; z-index: 1; }
.consult-hero__title { font-size: clamp(1.6rem, 4vw, 2.8rem); font-weight: 800; color: #fff; text-transform: uppercase; letter-spacing: -0.5px; margin-bottom: 0.75rem; }
.consult-hero__sub { color: rgba(255,255,255,0.65); font-size: 1rem; max-width: 500px; margin: 0 auto; }

.section-badge { display: inline-block; background: rgba(255,255,255,0.12); border: 1px solid rgba(255,255,255,0.2); color: #93c5fd; font-size: 0.7rem; font-weight: 700; letter-spacing: 0.15em; padding: 4px 14px; border-radius: 20px; text-transform: uppercase; }

/* Car cards */
.car-card { cursor: pointer; transition: transform 0.3s, box-shadow 0.3s, border-color 0.2s; }
.car-card:hover { transform: translateY(-5px); box-shadow: 0 12px 32px rgba(0,0,0,0.12) !important; }
.car-card--selected { transform: translateY(-4px); }
.car-card__img-wrap { height: 160px; display: flex; align-items: center; justify-content: center; padding: 1rem; background: #f8fafc; overflow: hidden; }
.car-card__img { max-width: 100%; max-height: 100%; object-fit: contain; transition: transform 0.4s; }
.car-card:hover .car-card__img { transform: scale(1.06); }
.car-card__name { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.selected-badge { position: absolute; top: 12px; right: 12px; width: 30px; height: 30px; background: #2563eb; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.75rem; box-shadow: 0 2px 8px rgba(37,99,235,0.4); z-index: 10; }

/* Selected preview */
.selected-preview { background: #eff6ff; border: 1px solid #bfdbfe; }

/* Skeleton */
.skeleton-card { height: 260px; overflow: hidden; }
.skeleton-img { height: 160px; background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%); background-size: 200% 100%; animation: shimmer 1.5s infinite; }
.skeleton-line { height: 14px; background: #e5e7eb; border-radius: 4px; animation: shimmer 1.5s infinite; }
.w-75 { width: 75% !important; } .w-50 { width: 50% !important; }
@keyframes shimmer { 0%{background-position:200% 0} 100%{background-position:-200% 0} }

/* Sticky form */
.sticky-top { top: 90px !important; }
.form-control, .form-select { border-color: #e2e8f0; transition: border-color 0.2s; }
.form-control:focus, .form-select:focus { border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }
.resize-none { resize: none !important; }
.cursor-pointer { cursor: pointer; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>