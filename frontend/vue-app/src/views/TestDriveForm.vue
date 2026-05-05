<template>
  <div class="testdrive-root">
    <!-- Left: Hero image panel -->
    <div class="td-hero d-none d-md-flex">
      <div class="td-hero-overlay"></div>
      <img
        src="https://shop.vinfastauto.com/on/demandware.static/-/Sites-app_vinfast_vn-Library/default/dw906c2893/images/pdp-vfe34/iris-vfe34.png"
        alt="VinFast Car"
        class="td-hero-car"
      />
      <div class="td-hero-text">
        <span class="td-hero-badge">TRẢI NGHIỆM THỰC TẾ</span>
        <h2 class="td-hero-title">Lái thử xe<br>VinFast miễn phí</h2>
        <p class="td-hero-sub">Đặt lịch ngay hôm nay, trải nghiệm cảm giác lái đỉnh cao tại showroom gần bạn nhất.</p>
        <div class="td-hero-stats">
          <div><strong>63+</strong><span>Tỉnh thành</span></div>
          <div><strong>150+</strong><span>Showroom</span></div>
          <div><strong>10K+</strong><span>Lái thử</span></div>
        </div>
      </div>
    </div>

    <!-- Right: Form -->
    <div class="td-form-panel">
      <div class="td-form-wrap">
        <!-- Tab -->
        <div class="d-flex border-bottom mb-4">
          <button
            v-for="tab in tabs" :key="tab.id"
            @click="activeTab = tab.id"
            :class="['tab-btn flex-fill py-3', activeTab === tab.id ? 'tab-btn--active' : '']"
          >
            <i :class="['me-2', tab.icon]"></i>{{ tab.label }}
          </button>
        </div>

        <div class="mb-4">
          <h4 class="fw-bold text-dark mb-1">Đăng Ký Lái Thử</h4>
          <p class="text-secondary small mb-0">Để đăng ký lái thử, Quý khách cần cung cấp giấy phép lái xe cho VinFast.</p>
        </div>

        <form @submit.prevent="submitForm" novalidate>
          <!-- Customer info -->
          <div class="form-section mb-4">
            <div class="form-section-label">Thông tin khách hàng</div>
            <div class="row g-3">
              <div class="col-12">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-user text-secondary small"></i></span>
                  <input
                    v-model.trim="form.customerName"
                    type="text"
                    class="form-control bg-light border-start-0"
                    placeholder="Họ và tên Quý khách *"
                    required
                  />
                </div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-phone text-secondary small"></i></span>
                  <input
                    v-model.trim="form.phoneNumber"
                    type="tel"
                    class="form-control bg-light border-start-0"
                    placeholder="Số điện thoại *"
                    required
                    pattern="[0-9]{10,11}"
                  />
                </div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-envelope text-secondary small"></i></span>
                  <input
                    v-model.trim="form.email"
                    type="email"
                    class="form-control bg-light border-start-0"
                    placeholder="Email *"
                    required
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Car selection -->
          <div class="form-section mb-4">
            <div class="form-section-label">Lựa chọn mẫu xe</div>
            <div class="position-relative">
              <select
                v-model="form.variantId"
                class="form-select bg-light"
                required
              >
                <option :value="null" disabled>Chọn mẫu xe *</option>
                <option v-for="car in carVariants" :key="car.id" :value="car.id">{{ car.name }}</option>
              </select>
            </div>
          </div>

          <!-- Schedule -->
          <div class="form-section mb-4">
            <div class="form-section-label">Lựa chọn thời gian</div>
            <div class="row g-3">
              <div class="col-12 col-sm-6">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-calendar text-secondary small"></i></span>
                  <input
                    v-model="form.scheduleDate"
                    type="date"
                    class="form-control bg-light border-start-0"
                    required
                    :min="todayStr"
                  />
                </div>
              </div>
              <div class="col-12 col-sm-6">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-clock text-secondary small"></i></span>
                  <input
                    v-model="form.scheduleTime"
                    type="time"
                    class="form-control bg-light border-start-0"
                    required
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Location -->
          <div class="form-section mb-4">
            <div class="form-section-label">Lựa chọn địa điểm</div>
            <div class="row g-3">
              <div class="col-12">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-map-pin text-secondary small"></i></span>
                  <select
                    v-model="selectedProvince"
                    class="form-select bg-light border-start-0"
                    required
                  >
                    <option :value="null" disabled>Tỉnh / Thành phố *</option>
                    <option v-for="prov in provinces" :key="prov" :value="prov">{{ prov }}</option>
                  </select>
                </div>
              </div>
              <div class="col-12">
                <div class="input-group">
                  <span class="input-group-text bg-light border-end-0"><i class="fas fa-store text-secondary small"></i></span>
                  <select
                    v-model="selectedShowroom"
                    :disabled="!selectedProvince || isLoadingShowrooms"
                    class="form-select bg-light border-start-0"
                    required
                  >
                    <option :value="null" disabled>
                      {{ isLoadingShowrooms ? 'Đang tải...' : 'Showroom / Chi nhánh *' }}
                    </option>
                    <option v-for="room in showrooms" :key="room.id" :value="room">
                      {{ room.name }} – {{ room.address }}
                    </option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          <!-- Status message -->
          <Transition name="slide-down">
            <div
              v-if="statusMsg.text"
              :class="['alert d-flex align-items-center gap-2 py-2 mb-3', statusMsg.success ? 'alert-success' : 'alert-danger']"
              role="alert"
            >
              <i :class="['fas flex-shrink-0', statusMsg.success ? 'fa-check-circle' : 'fa-times-circle']"></i>
              <small>{{ statusMsg.text }}</small>
            </div>
          </Transition>

          <!-- Submit -->
          <button
            type="submit"
            :disabled="isSubmitting"
            class="btn btn-primary btn-lg w-100 fw-bold shadow-sm text-uppercase ls-wide"
          >
            <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-2" role="status"></span>
            <i v-else class="fas fa-car-side me-2"></i>
            {{ isSubmitting ? 'Đang gửi...' : 'Đăng ký lái thử' }}
          </button>

          <p class="text-center text-secondary mt-3 mb-0" style="font-size:0.72rem">
            <i class="fas fa-shield-alt me-1"></i>Thông tin của bạn được bảo mật hoàn toàn.
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

const tabs = [
  { id: 'car', label: 'Xe ô tô', icon: 'fas fa-car' },
  { id: 'ebike', label: 'Xe máy điện', icon: 'fas fa-motorcycle' },
]
const activeTab = ref('car')

const provinces = ref([])
const showrooms = ref([])
const carVariants = ref([])
const selectedProvince = ref(null)
const selectedShowroom = ref(null)
const isSubmitting = ref(false)
const isLoadingShowrooms = ref(false)
const statusMsg = reactive({ text: '', success: false })

const form = reactive({
  customerName: '',
  phoneNumber: '',
  email: '',
  variantId: null,
  scheduleDate: '',
  scheduleTime: '',
})

const todayStr = computed(() => new Date().toISOString().split('T')[0])

onMounted(async () => {
  try {
    const [provRes, carRes] = await Promise.all([
      axios.get(`${API_URL}/showrooms/provinces`),
      axios.get(`${API_URL}/cars/variants`),
    ])
    provinces.value = provRes.data.result ?? []
    carVariants.value = carRes.data.result ?? []
  } catch (e) {
    console.error('Lỗi tải dữ liệu ban đầu:', e)
  }
})

watch(selectedProvince, async (newVal) => {
  showrooms.value = []
  selectedShowroom.value = null
  if (!newVal) return
  isLoadingShowrooms.value = true
  try {
    const res = await axios.get(`${API_URL}/showrooms`, { params: { province: newVal } })
    showrooms.value = res.data.result ?? []
  } catch (e) {
    console.error('Lỗi tải showroom:', e)
  } finally {
    isLoadingShowrooms.value = false
  }
})

const submitForm = async () => {
  statusMsg.text = ''

  // FIX: validate phone client-side
  if (!/^\d{10,11}$/.test(form.phoneNumber)) {
    statusMsg.success = false
    statusMsg.text = 'Số điện thoại không hợp lệ (10-11 chữ số).'
    return
  }
  if (!selectedShowroom.value) {
    statusMsg.success = false
    statusMsg.text = 'Vui lòng chọn showroom.'
    return
  }

  isSubmitting.value = true
  try {
    const scheduledAt = `${form.scheduleDate}T${form.scheduleTime}:00`
    const payload = {
      customerName: form.customerName,
      phoneNumber: form.phoneNumber,
      email: form.email,
      province: selectedProvince.value,
      showroomId: selectedShowroom.value?.id,
      variantId: form.variantId,
      scheduledAt,
      note: 'Khách đăng ký lái thử từ trang chủ',
    }
    await axios.post(`${API_URL}/consultations`, payload)
    statusMsg.success = true
    statusMsg.text = '🎉 Đăng ký thành công! Chúng tôi sẽ liên hệ sớm nhất.'
    Object.assign(form, { customerName: '', phoneNumber: '', email: '', variantId: null, scheduleDate: '', scheduleTime: '' })
    selectedProvince.value = null
    selectedShowroom.value = null
  } catch (error) {
    statusMsg.success = false
    statusMsg.text = error.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

.testdrive-root {
  display: flex;
  min-height: 100vh;
  font-family: 'Be Vietnam Pro', sans-serif;
  background: #f8fafc;
}

/* ===== HERO PANEL ===== */
.td-hero {
  flex: 1.1;
  position: relative;
  background: linear-gradient(145deg, #0f172a 0%, #1e3a5f 60%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 3rem;
  overflow: hidden;
}

.td-hero-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 60% 40%, rgba(37,99,235,0.25) 0%, transparent 65%);
}

.td-hero-car {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -55%);
  width: 85%;
  max-width: 560px;
  object-fit: contain;
  filter: drop-shadow(0 20px 40px rgba(0,0,0,0.5));
  transition: transform 0.6s ease;
}
.td-hero-car:hover { transform: translate(-50%, -58%) scale(1.03); }

.td-hero-text {
  position: relative;
  z-index: 1;
  color: #fff;
}

.td-hero-badge {
  display: inline-block;
  background: rgba(59,130,246,0.2);
  border: 1px solid rgba(59,130,246,0.4);
  color: #60a5fa;
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.15em;
  padding: 4px 12px;
  border-radius: 20px;
  margin-bottom: 1rem;
}

.td-hero-title {
  font-size: 2.4rem;
  font-weight: 800;
  line-height: 1.15;
  margin-bottom: 1rem;
}

.td-hero-sub {
  color: rgba(255,255,255,0.6);
  font-size: 0.9rem;
  line-height: 1.6;
  max-width: 360px;
  margin-bottom: 2rem;
}

.td-hero-stats {
  display: flex;
  gap: 2rem;
}
.td-hero-stats > div {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.td-hero-stats strong {
  font-size: 1.5rem;
  font-weight: 800;
  color: #60a5fa;
}
.td-hero-stats span {
  font-size: 0.72rem;
  color: rgba(255,255,255,0.5);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* ===== FORM PANEL ===== */
.td-form-panel {
  width: 100%;
  max-width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  overflow-y: auto;
}

@media (max-width: 767px) {
  .td-form-panel { max-width: 100%; }
}

.td-form-wrap {
  width: 100%;
  max-width: 460px;
}

/* ===== TABS ===== */
.tab-btn {
  border: none;
  background: transparent;
  font-weight: 600;
  color: #64748b;
  font-size: 0.9rem;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
  cursor: pointer;
  font-family: inherit;
}
.tab-btn:hover { color: #1d4ed8; }
.tab-btn--active { color: #1d4ed8 !important; border-bottom-color: #1d4ed8; }

/* ===== FORM SECTION ===== */
.form-section-label {
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #94a3b8;
  margin-bottom: 0.75rem;
}

.input-group-text { border-color: #e2e8f0; }
.form-control, .form-select { border-color: #e2e8f0; }
.input-group:focus-within .input-group-text,
.input-group:focus-within .form-control,
.input-group:focus-within .form-select {
  border-color: #2563eb;
}
.input-group:focus-within .form-control,
.input-group:focus-within .form-select { box-shadow: none; }

.ls-wide { letter-spacing: 0.08em; }

.slide-down-enter-active, .slide-down-leave-active { transition: all 0.3s ease; }
.slide-down-enter-from, .slide-down-leave-to { opacity: 0; transform: translateY(-8px); }
</style>