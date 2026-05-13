<template>
  <div class="min-vh-100 bg-light" style="padding-top:80px">
    <!-- Loading -->
    <div v-if="loading" class="d-flex justify-content-center align-items-center py-5 mt-5">
      <div class="text-center">
        <div class="spinner-border text-primary mb-3" style="width:3rem;height:3rem" role="status"></div>
        <p class="text-secondary">Đang tải thông tin xe...</p>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="container py-5">
      <div class="alert alert-danger rounded-4 text-center">
        <i class="fas fa-exclamation-triangle fs-2 mb-3 d-block text-danger"></i>
        <h5 class="fw-bold">Không thể tải thông tin xe</h5>
        <p class="mb-3 text-secondary">{{ error }}</p>
        <button @click="fetchDetail" class="btn btn-primary rounded-pill px-4">Thử lại</button>
      </div>
    </div>

    <!-- Content -->
    <div v-else-if="product" class="container py-4 py-md-5">

      <!-- Hero -->
      <div class="row g-5 mb-5 align-items-center">
        <!-- Gallery -->
        <div class="col-12 col-lg-6">
          <div class="product-main-img rounded-4 shadow-sm border border-light mb-3 d-flex align-items-center justify-content-center bg-white p-4">
            <img :src="getImageUrl(mainImage)" :alt="product.name" class="img-fluid" style="max-height:380px;object-fit:contain" />
          </div>
          <div v-if="product.images && product.images.length > 1" class="d-flex gap-2 overflow-auto pb-2">
            <button
              v-for="img in product.images" :key="img.id"
              @click="mainImage = img.imageUrl"
              :class="['thumb-btn bg-white border rounded-3 p-1 flex-shrink-0', mainImage === img.imageUrl ? 'border-primary shadow-sm' : 'border-light']"
              style="width:84px;height:70px"
            >
              <img :src="getImageUrl(img.imageUrl)" class="w-100 h-100 object-fit-contain" />
            </button>
          </div>
        </div>

        <!-- Info -->
        <div class="col-12 col-lg-6">
          <span class="badge bg-primary-subtle text-primary rounded-pill px-3 py-2 fw-bold mb-3 text-uppercase">
            {{ product.modelName }}
          </span>
          <h1 class="display-6 fw-bold text-dark mb-3">{{ product.name }}</h1>
          <p class="fs-3 fw-bold text-primary mb-4">{{ formatPrice(product.price) }}</p>

          <!-- Quick specs -->
          <div class="row g-3 mb-5">
            <div class="col-6">
              <div class="spec-chip">
                <i class="fas fa-users text-primary"></i>
                <div>
                  <div class="spec-chip__label">Số chỗ</div>
                  <div class="spec-chip__val">{{ product.seatCapacity ?? 'N/A' }} chỗ</div>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="spec-chip">
                <i class="fas fa-bolt text-primary"></i>
                <div>
                  <div class="spec-chip__label">Quãng đường</div>
                  <div class="spec-chip__val">{{ product.rangeNedc ?? 'N/A' }} km</div>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="spec-chip">
                <i class="fas fa-gas-pump text-primary"></i>
                <div>
                  <div class="spec-chip__label">Nhiên liệu</div>
                  <div class="spec-chip__val">{{ product.fuelType ?? 'N/A' }}</div>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="spec-chip">
                <i class="fas fa-shield-alt text-success"></i>
                <div>
                  <div class="spec-chip__label">Bảo hành</div>
                  <div class="spec-chip__val">10 năm</div>
                </div>
              </div>
            </div>
          </div>

          <div class="d-flex gap-3">
            <router-link :to="{ path: '/deposit', query: { id: product.id } }" class="btn btn-primary btn-lg flex-grow-1 fw-bold rounded-3 shadow">
              <i class="fas fa-handshake me-2"></i>Mua ngay
            </router-link>
            <button class="btn btn-outline-secondary btn-lg rounded-3 px-3">
              <i class="far fa-heart fs-5"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Technical Specs -->
      <div class="card border-0 shadow-sm rounded-4 p-4 p-md-5">
        <h2 class="h4 fw-bold text-dark mb-5 d-flex align-items-center gap-2">
          <span class="spec-title-icon"><i class="fas fa-list-ul text-primary"></i></span>
          Thông số kỹ thuật chi tiết
        </h2>

        <div class="d-flex flex-column gap-5">
          <div v-for="section in technicalSpecs" :key="section.group">
            <h3 class="fs-6 fw-bold text-primary bg-primary-subtle px-3 py-2 rounded-3 mb-4 d-inline-flex align-items-center gap-2">
              <i class="fas fa-chevron-right small"></i> {{ section.group }}
            </h3>
            <div class="row g-3 px-2">
              <div v-for="(val, label) in section.items" :key="label" class="col-12 col-md-6 col-xl-4">
                <div class="d-flex justify-content-between align-items-center spec-row py-2">
                  <span class="text-secondary small">{{ label }}</span>
                  <span class="fw-semibold text-dark small text-end ms-3">{{ (val && val !== 'null') ? val : 'N/A' }}</span>
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
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/utils/axios'

const route = useRoute()
const product = ref(null)
const loading = ref(true)
const error = ref(null)
const mainImage = ref('')

const fetchDetail = async () => {
  loading.value = true
  error.value = null
  try {
    const res = await api.get(`/api/variants/${route.params.id}/details`)
    product.value = res.data.result  // FIX: removed stale `product.result = ...` assignment
    if (product.value?.images?.length > 0) {
      mainImage.value = product.value.images[0].imageUrl
    }
  } catch (e) {
    console.error('Lỗi tải chi tiết:', e)
    error.value = 'Không thể tải thông tin xe. Vui lòng thử lại sau.'
  } finally {
    loading.value = false
  }
}

const getImageUrl = (path) => {
  if (!path) return '/assets/image/default.png'
  return `http://localhost:8080${path.startsWith('/') ? '' : '/'}${path}`
}

const formatPrice = (p) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(p || 0)

const technicalSpecs = computed(() => {
  if (!product.value) return []
  const p = product.value
  return [
    {
      group: 'Kích thước & Khối lượng',
      items: {
        'Dài x Rộng x Cao': `${p.length ?? 'N/A'} x ${p.width ?? 'N/A'} x ${p.height ?? 'N/A'} mm`,
        'Chiều dài cơ sở': p.wheelbase ? `${p.wheelbase} mm` : 'N/A',
        'Khoảng sáng gầm': p.groundClearance ? `${p.groundClearance} mm` : 'N/A',
        'Cỡ mâm': p.wheelSize ? `${p.wheelSize} inch` : 'N/A',
      }
    },
    {
      group: 'Động cơ & Vận hành',
      items: {
        'Loại nhiên liệu': p.fuelType,
        'Công suất tối đa': p.maxPower ? `${p.maxPower} hp` : 'N/A',
        'Mô-men xoắn cực đại': p.maxTorque ? `${p.maxTorque} Nm` : 'N/A',
        'Hệ dẫn động': p.drivetrain,
        'Chế độ lái': p.driveModes,
        'Hệ thống treo': p.suspensionSystem,
        'Hệ thống phanh': p.brakeSystem,
      }
    },
    {
      group: 'Pin & Sạc (Xe điện)',
      items: {
        'Dung lượng pin': p.battery ? `${p.battery} kWh` : 'N/A',
        'Quãng đường (NEDC)': p.rangeNedc ? `${p.rangeNedc} km` : 'N/A',
        'Thông tin sạc': p.chargeTimeDetail,
      }
    },
    {
      group: 'Tiện nghi',
      items: {
        'Số chỗ ngồi': p.seatCapacity ? `${p.seatCapacity} chỗ` : 'N/A',
        'Màn hình': p.entertainmentScreen ? `${p.entertainmentScreen} inch` : 'N/A',
        'Hệ thống âm thanh': p.speakerSystem,
        'Điều hòa': p.airConditioningSystem,
        'Cốp xe': p.trunkAccess,
      }
    }
  ]
})

onMounted(fetchDetail)
</script>

<style scoped>
/* Spec chips */
.spec-chip {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: border-color 0.2s, background 0.2s;
}
.spec-chip:hover { background: #eff6ff; border-color: #93c5fd; }
.spec-chip__label { font-size: 0.72rem; color: #9ca3af; text-transform: uppercase; letter-spacing: 0.05em; }
.spec-chip__val { font-size: 0.9rem; font-weight: 700; color: #1f2937; }
.spec-chip i { font-size: 1.1rem; width: 24px; text-align: center; flex-shrink: 0; }

/* Gallery */
.product-main-img { min-height: 300px; aspect-ratio: 16/9; }
.thumb-btn { cursor: pointer; transition: border-color 0.2s, box-shadow 0.2s; }
.thumb-btn:hover { border-color: #93c5fd !important; }

/* Spec table */
.spec-row { border-bottom: 1px solid #f3f4f6; }
.spec-row:last-child { border-bottom: none; }

.spec-title-icon {
  width: 36px; height: 36px;
  background: #eff6ff;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
}

/* Badge */
.bg-primary-subtle { background: #dbeafe !important; }
.text-primary { color: #1d4ed8 !important; }
</style>