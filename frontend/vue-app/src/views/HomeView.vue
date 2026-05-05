<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const bannerData = ref([])
const evCarsData = ref([])
const gasolineCarsData = ref([])
const serviceCarsData = ref([])
const accessoriesData = ref([])
const getImageUrl = (path) => {
  if (!path) return '/assets/image/icon_logo/VinFast-logo.svg'; // Ảnh mặc định nếu xe chưa có ảnh
  if (path.startsWith('http') || path.startsWith('blob:')) return path;
  return `http://localhost:8080${path.startsWith('/') ? '' : '/'}${path}`;
}
const currentBannerIndex = ref(0)
const evIndex = ref(0)
const gasIndex = ref(0)
const serIndex = ref(0)

let bannerInterval = null
let revealObserver = null

async function fetchData(url) {
  try {
    const response = await fetch(url)
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)
    return await response.json()
  } catch (e) {
    console.error(`Failed to fetch ${url}:`, e)
    return null
  }
}

const currentEvCar = computed(() => evCarsData.value[evIndex.value] ?? {})
const currentGasCar = computed(() => gasolineCarsData.value[gasIndex.value] ?? {})
const currentSerCar = computed(() => serviceCarsData.value[serIndex.value] ?? {})
const featuredAccessories = computed(() => accessoriesData.value.slice(0, 4))

const nextEv = () => { if (evCarsData.value.length) evIndex.value = (evIndex.value + 1) % evCarsData.value.length }
const prevEv = () => { if (evCarsData.value.length) evIndex.value = (evIndex.value - 1 + evCarsData.value.length) % evCarsData.value.length }
const nextGas = () => { if (gasolineCarsData.value.length) gasIndex.value = (gasIndex.value + 1) % gasolineCarsData.value.length }
const prevGas = () => { if (gasolineCarsData.value.length) gasIndex.value = (gasIndex.value - 1 + gasolineCarsData.value.length) % gasolineCarsData.value.length }
const nextSer = () => { if (serviceCarsData.value.length) serIndex.value = (serIndex.value + 1) % serviceCarsData.value.length }
const prevSer = () => { if (serviceCarsData.value.length) serIndex.value = (serIndex.value - 1 + serviceCarsData.value.length) % serviceCarsData.value.length }

function startBannerAutoplay() {
  if (bannerData.value.length <= 1) return
  clearInterval(bannerInterval)
  bannerInterval = setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % bannerData.value.length
  }, 5000)
}

function formatPrice(price) {
  if (!price) return 'Liên hệ'
  return new Intl.NumberFormat('vi-VN').format(price) + ' ₫'
}

onMounted(async () => {
  const imageData = await fetchData('/assets/js/image.json')
  if (imageData?.banner) {
    bannerData.value = imageData.banner
    if (bannerData.value.length > 0) startBannerAutoplay()
  }

  try {
    // Gọi API tập trung để lấy tất cả các xe đang CÓ SẴN trong kho
    // Bạn nên truyền size lớn (VD: 100) để lấy đủ các mẫu xe hiển thị trên Home
    const response = await fetch('http://localhost:8080/api/variants/available?size=100');
    const json = await response.json();

    if (json.code === 1000) {
      const allAvailableVariants = json.result.content; // Lấy mảng dữ liệu từ Page object

      // Phân loại xe dựa trên fuelType hoặc modelName để đưa vào đúng khu vực giao diện
      
      // 1. Lọc xe điện (EV)
      evCarsData.value = allAvailableVariants.filter(car => 
        car.fuelType?.toLowerCase().includes('điện') || car.modelName?.startsWith('VF')
      );

      // 2. Lọc xe xăng (Gasoline)
      gasolineCarsData.value = allAvailableVariants.filter(car => 
        car.fuelType?.toLowerCase().includes('xăng')
      );

  serviceCarsData.value = allAvailableVariants.filter(car => 
        car.modelName === 'Dòng xe dịch vụ' || 
        car.name?.includes('dịch vụ') ||
        car.fuelType?.includes('dịch vụ')
      );
    }
  } catch (e) {
    console.error('Lỗi khi gọi API /api/variants/available:', e);
  }

  const accessoryData = await fetchData('/assets/js/phukien.json')
  if (accessoryData?.pk) accessoriesData.value = accessoryData.pk

  // Scroll reveal
  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.1 })

  document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el))
})

onUnmounted(() => {
  clearInterval(bannerInterval)
  revealObserver?.disconnect()
})
</script>

<template>
  <main>
    <!-- Hero Banner -->
    <section id="hero-slider" class="hero-slider position-relative overflow-hidden">
      <img
        v-for="(banner, index) in bannerData"
        :key="banner.image || index"
        :src="banner.image"
        :alt="`VinFast Banner ${index + 1}`"
        class="slider-image"
        :class="{ active: index === currentBannerIndex }"
      />
      <!-- Dots -->
      <div v-if="bannerData.length > 1" class="slider-dots">
        <button
          v-for="(_, i) in bannerData" :key="i"
          class="slider-dot"
          :class="{ active: i === currentBannerIndex }"
          @click="currentBannerIndex = i"
        ></button>
      </div>
    </section>

    <!-- EV Cars -->
    <section id="ev-showcase" class="py-5 py-md-7 bg-white overflow-hidden">
      <div class="container">
        <div class="text-center mb-5 reveal">
          <span class="section-badge text-uppercase">Xe Điện</span>
          <h2 class="display-5 fw-bold text-dark mt-2 mb-3">Ô TÔ ĐIỆN THÔNG MINH</h2>
          <p class="text-secondary fs-5 mx-auto" style="max-width:560px">Thiết kế tinh xảo, công nghệ vượt trội, khẳng định vị thế dẫn đầu.</p>
        </div>

        <template v-if="evCarsData.length > 0">
          <div class="row align-items-center g-5 reveal">
            <div class="col-12 col-lg-6">
              <div class="car-image-wrap">
<img :src="getImageUrl(currentEvCar.firstImageUrl)" :alt="currentEvCar.name" class="img-fluid rounded-4 shadow-lg car-img">              </div>
            </div>
            <div class="col-12 col-lg-6">
              <h3 class="display-6 fw-bold text-dark mb-2">{{ currentEvCar.name }}</h3>
              <p class="fs-3 fw-bold text-primary mb-4">{{ formatPrice(currentEvCar.price) }}</p>
              <div class="row g-3 mb-4">
                <div class="col-6"><div class="spec-chip"><i class="fas fa-road text-primary me-2"></i>{{ currentEvCar.rangeNedc || 'N/A' }}km (NEDC)</div></div>
                <div class="col-6"><div class="spec-chip"><i class="fas fa-car-alt text-primary me-2"></i>{{ currentEvCar.type || 'SUV' }}</div></div>
                <div class="col-6"><div class="spec-chip"><i class="fas fa-users text-primary me-2"></i>{{ currentEvCar.seats || '5' }} Chỗ</div></div>
                <div class="col-6">
                  <div class="spec-chip fw-bold" :class="currentEvCar.availableStock > 0 ? 'text-success border-success bg-success-soft' : 'text-danger border-danger bg-danger-soft'">
                    <i class="fas" :class="currentEvCar.availableStock > 0 ? 'fa-check-circle me-2' : 'fa-times-circle me-2'"></i>
                    {{ currentEvCar.battery }}kWh
                  </div>
                </div>
              </div>
              <div class="d-flex flex-column flex-sm-row gap-3">
                <router-link v-if="currentEvCar.availableStock > 0" :to="{ path: '/deposit', query: { id: currentEvCar.id, type: 'ev' } }" class="btn btn-primary btn-lg rounded-pill px-4 fw-bold shadow">Đặt Cọc</router-link>
                <button v-else disabled class="btn btn-secondary btn-lg rounded-pill px-4 fw-bold shadow" style="cursor: not-allowed; opacity: 0.7;">Tạm Hết Hàng</button>
                <router-link :to="`/product/${currentEvCar.id}`" class="btn btn-outline-secondary btn-lg rounded-pill px-4 fw-bold">Xem Chi Tiết</router-link>
              </div>
            </div>
          </div>
          <div v-if="evCarsData.length > 1" class="d-flex justify-content-center gap-3 mt-5">
            <button @click="prevEv" class="btn-nav-circle"><i class="fas fa-arrow-left"></i></button>
            <span class="d-flex align-items-center text-secondary small">{{ evIndex + 1 }} / {{ evCarsData.length }}</span>
            <button @click="nextEv" class="btn-nav-circle"><i class="fas fa-arrow-right"></i></button>
          </div>
        </template>
        <div v-else class="text-center py-5 text-secondary">
          <div class="spinner-border text-primary mb-3" role="status"></div>
          <p>Đang tải dữ liệu xe điện...</p>
        </div>
      </div>
    </section>

    <!-- Gasoline Cars -->
    <section id="gasoline-showcase" class="py-5 py-md-7 bg-light overflow-hidden">
      <div class="container">
        <div class="text-center mb-5 reveal">
          <span class="section-badge text-uppercase">Xe Xăng</span>
          <h2 class="display-5 fw-bold text-dark mt-2 mb-3">HIỆU SUẤT VƯỢT TRỘI</h2>
          <p class="text-secondary fs-5 mx-auto" style="max-width:560px">Sức mạnh bền bỉ, tiết kiệm nhiên liệu, lý tưởng cho mọi hành trình.</p>
        </div>

        <template v-if="gasolineCarsData.length > 0">
          <div class="row align-items-center g-5 flex-lg-row-reverse reveal">
            <div class="col-12 col-lg-6">
              <div class="car-image-wrap">
<img :src="getImageUrl(currentGasCar.firstImageUrl)" :alt="currentGasCar.name" class="img-fluid rounded-4 shadow-lg car-img">              </div>
            </div>
            <div class="col-12 col-lg-6">
              <h3 class="display-6 fw-bold text-dark mb-2">{{ currentGasCar.name }}</h3>
              <p class="fs-3 fw-bold text-danger mb-4">{{ formatPrice(currentGasCar.price) }}</p>
              <div class="row g-3 mb-4">
                <div class="col-6"><div class="spec-chip"><i class="fas fa-gas-pump text-danger me-2"></i>{{ currentGasCar.fuelType || 'Xăng' }}</div></div>
                <div class="col-6"><div class="spec-chip"><i class="fas fa-car-alt text-danger me-2"></i>{{ currentGasCar.type || 'Sedan' }}</div></div>
                <div class="col-6"><div class="spec-chip"><i class="fas fa-users text-danger me-2"></i>{{ currentGasCar.seats || '5' }} Chỗ</div></div>
                <div class="col-6">
                  <div class="spec-chip fw-bold" :class="currentGasCar.availableStock > 0 ? 'text-success border-success bg-success-soft' : 'text-danger border-danger bg-danger-soft'">
                    <i class="fas" :class="currentGasCar.availableStock > 0 ? 'fa-check-circle me-2' : 'fa-times-circle me-2'"></i>
                    {{ currentGasCar.drivetrain }}
                  </div>
                </div>
              </div>
              <div class="d-flex flex-column flex-sm-row gap-3">
                <router-link v-if="currentGasCar.availableStock > 0" :to="{ path: '/deposit', query: { id: currentGasCar.id, type: 'gasoline' } }" class="btn btn-danger btn-lg rounded-pill px-4 fw-bold shadow">Đặt Cọc</router-link>
                <button v-else disabled class="btn btn-secondary btn-lg rounded-pill px-4 fw-bold shadow" style="cursor: not-allowed; opacity: 0.7;">Tạm Hết Hàng</button>
                <router-link :to="`/product/${currentGasCar.id}`" class="btn btn-outline-secondary btn-lg rounded-pill px-4 fw-bold">Xem Chi Tiết</router-link>
              </div>
            </div>
          </div>
          <div v-if="gasolineCarsData.length > 1" class="d-flex justify-content-center gap-3 mt-5">
            <button @click="prevGas" class="btn-nav-circle"><i class="fas fa-arrow-left"></i></button>
            <span class="d-flex align-items-center text-secondary small">{{ gasIndex + 1 }} / {{ gasolineCarsData.length }}</span>
            <button @click="nextGas" class="btn-nav-circle"><i class="fas fa-arrow-right"></i></button>
          </div>
        </template>
        <div v-else class="text-center py-5 text-secondary">
          <div class="spinner-border text-danger mb-3" role="status"></div>
          <p>Đang tải dữ liệu xe xăng...</p>
        </div>
      </div>
    </section>

    <!-- Service Cars -->
    <section id="service-showcase" class="py-5 py-md-7 bg-white overflow-hidden">
      <div class="container">
        <div class="text-center mb-5 reveal">
          <span class="section-badge section-badge--blue text-uppercase">Vận Tải</span>
          <h2 class="display-5 fw-bold text-primary mt-2 mb-3">GIẢI PHÁP VẬN TẢI XANH</h2>
          <p class="text-secondary fs-5 mx-auto" style="max-width:560px">Dành cho doanh nghiệp kinh doanh dịch vụ vận tải tiên phong.</p>
        </div>

        <template v-if="serviceCarsData.length > 0">
          <div class="row align-items-center g-5 reveal">
            <div class="col-12 col-lg-6">
              <div class="car-image-wrap">
              <img :src="getImageUrl(currentSerCar.firstImageUrl)" :alt="currentSerCar.name" class="img-fluid rounded-4 shadow-lg car-img">              </div>
            </div>
<div class="col-12 col-lg-6">
  <h3 class="display-6 fw-bold text-primary-dark mb-2">{{ currentSerCar.name }}</h3>
  
  <p class="fs-3 fw-bold text-primary mb-4">{{ formatPrice(currentSerCar.price) }}</p>
  
  <div class="row g-3 mb-4">
    <div class="col-6">
      <div class="spec-chip fw-bold text-success border-success bg-success-soft">
        <i class="fas fa-check-circle me-2"></i>
        {{ currentSerCar.battery }} KWH
      </div>
    </div>
    <div class="col-6">
      <div class="spec-chip">
        <i class="fas fa-taxi text-warning me-2"></i>
        {{ currentSerCar.modelName }}
      </div>
    </div>
  </div>
  
  <router-link to="/consultation" class="btn btn-primary btn-lg rounded-pill px-5 fw-bold shadow">
    Liên Hệ Tư Vấn
  </router-link>
</div>
          </div>
          <div v-if="serviceCarsData.length > 1" class="d-flex justify-content-center gap-3 mt-5">
            <button @click="prevSer" class="btn-nav-circle"><i class="fas fa-arrow-left"></i></button>
            <span class="d-flex align-items-center text-secondary small">{{ serIndex + 1 }} / {{ serviceCarsData.length }}</span>
            <button @click="nextSer" class="btn-nav-circle"><i class="fas fa-arrow-right"></i></button>
          </div>
        </template>
        <div v-else class="text-center py-5 text-secondary">
          <div class="spinner-border text-primary mb-3" role="status"></div>
          <p>Đang tải dữ liệu xe dịch vụ...</p>
        </div>
      </div>
    </section>

    <!-- Accessories 
    <section id="accessories" class="py-5 py-md-7 bg-light">
      <div class="container">
        <div class="text-center mb-5 reveal">
          <span class="section-badge text-uppercase">Phụ Kiện</span>
          <h2 class="display-5 fw-bold text-dark mt-2 mb-3">Phụ Kiện Chính Hãng</h2>
          <p class="text-secondary fs-5 mx-auto" style="max-width:560px">Nâng tầm phong cách và cá nhân hóa trải nghiệm lái xe của bạn.</p>
        </div>
        <div class="row g-4">
          <div
            v-for="(item, index) in featuredAccessories"
            :key="item.id ?? index"
            class="col-6 col-md-3 reveal"
          >
            <div class="card acc-card h-100 border-0 shadow-sm">
              <div class="acc-img-wrap">
                <img :src="item.image" :alt="item.name" class="card-img-top acc-img">
              </div>
              <div class="card-body">
                <h6 class="card-title fw-bold text-truncate mb-1">{{ item.name }}</h6>
                <p class="text-primary fw-semibold mb-0">{{ item.gia }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="text-center mt-5 reveal">
          <router-link to="#" class="btn btn-outline-dark btn-lg rounded-pill px-5 fw-bold">Xem Thêm Phụ Kiện</router-link>
        </div>
      </div>
    </section>
-->
    <!-- Battery & Charging -->
    <section id="battery-charging" class="py-5 py-md-7 bg-white">
      <div class="container">
        <div class="row g-5 align-items-center reveal">
          <div class="col-12 col-lg-6">
            <span class="section-badge text-uppercase">Hạ Tầng</span>
            <h2 class="display-5 fw-bold text-dark mt-2 mb-4">Pin & Trạm Sạc</h2>
            <p class="text-secondary fs-5 mb-4">Hệ sinh thái xe điện toàn diện của VinFast đảm bảo mọi hành trình luôn tràn đầy năng lượng.</p>
            <ul class="list-unstyled feature-list">
              <li class="d-flex align-items-start gap-3 mb-4">
                <span class="feature-icon bg-primary-soft text-primary"><i class="fas fa-charging-station"></i></span>
                <div>
                  <h5 class="fw-bold mb-1">Trạm sạc toàn quốc</h5>
                  <p class="text-secondary mb-0">Phủ khắp 63 tỉnh thành trên toàn quốc.</p>
                </div>
              </li>
              <li class="d-flex align-items-start gap-3">
                <span class="feature-icon bg-success-soft text-success"><i class="fas fa-battery-full"></i></span>
                <div>
                  <h5 class="fw-bold mb-1">Công nghệ Pin LFP</h5>
                  <p class="text-secondary mb-0">An toàn tuyệt đối, tuổi thọ cao và ổn định.</p>
                </div>
              </li>
            </ul>
          </div>
          <div class="col-12 col-lg-6">
            <img src="/assets/image/pk/pin-tramsac-1_1660273470.png" alt="Trạm sạc VinFast" class="img-fluid rounded-4 shadow-lg w-100 object-fit-cover">
          </div>
        </div>
      </div>
    </section>

    <!-- After Sales -->
    <section id="after-sales" class="py-5 py-md-7 bg-light">
      <div class="container">
        <div class="text-center mb-5 reveal">
          <span class="section-badge text-uppercase">Hậu Mãi</span>
          <h2 class="display-5 fw-bold text-dark mt-2 mb-3">Dịch Vụ Hậu Mãi Xuất Sắc</h2>
          <p class="text-secondary fs-5 mx-auto" style="max-width:560px">Cam kết đồng hành cùng khách hàng trong suốt quá trình sử dụng xe.</p>
        </div>
        <div class="row g-4">
          <div class="col-12 col-md-4 reveal">
            <div class="card service-card h-100 border-0 shadow-sm text-center p-4">
              <div class="service-icon text-primary mb-4"><i class="fas fa-shield-alt fa-3x"></i></div>
              <h4 class="fw-bold mb-3">Bảo Hành 10 Năm</h4>
              <p class="text-secondary mb-0">Chính sách bảo hành dẫn đầu thị trường, mang lại sự an tâm tuyệt đối.</p>
            </div>
          </div>
          <div class="col-12 col-md-4 reveal" style="transition-delay:100ms">
            <div class="card service-card h-100 border-0 shadow-sm text-center p-4">
              <div class="service-icon text-warning mb-4"><i class="fas fa-tools fa-3x"></i></div>
              <h4 class="fw-bold mb-3">Cứu Hộ 24/7</h4>
              <p class="text-secondary mb-0">Dịch vụ cứu hộ miễn phí trong suốt thời gian bảo hành, mọi lúc mọi nơi.</p>
            </div>
          </div>
          <div class="col-12 col-md-4 reveal" style="transition-delay:200ms">
            <div class="card service-card h-100 border-0 shadow-sm text-center p-4">
              <div class="service-icon text-success mb-4"><i class="fas fa-charging-station fa-3x"></i></div>
              <h4 class="fw-bold mb-3">Sạc Lưu Động</h4>
              <p class="text-secondary mb-0">Dịch vụ sạc pin lưu động giúp bạn yên tâm trên mọi cung đường.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Why Us -->
    <section id="why-us" class="py-5 py-md-7 bg-white">
      <div class="container">
        <div class="row g-5 align-items-center reveal">
          <div class="col-12 col-lg-6 order-lg-2">
            <span class="section-badge text-uppercase">Tinh Thần Việt</span>
            <h2 class="display-5 fw-bold text-dark mt-2 mb-4">Mãnh Liệt Tinh Thần Việt Nam</h2>
            <p class="text-secondary fs-5 mb-4">VinFast không chỉ là một chiếc xe, mà là niềm tự hào của người Việt, vươn tầm thế giới.</p>
            <div class="d-flex flex-column gap-4">
              <div class="d-flex align-items-start gap-3">
                <span class="feature-icon bg-primary-soft text-primary"><i class="fas fa-globe"></i></span>
                <div>
                  <h5 class="fw-bold mb-1">Đẳng Cấp Quốc Tế</h5>
                  <p class="text-secondary mb-0">Được công nhận tại Mỹ, Châu Âu và các thị trường khó tính nhất.</p>
                </div>
              </div>
              <div class="d-flex align-items-start gap-3">
                <span class="feature-icon bg-success-soft text-success"><i class="fas fa-leaf"></i></span>
                <div>
                  <h5 class="fw-bold mb-1">Cam Kết Xanh</h5>
                  <p class="text-secondary mb-0">Tiên phong trong cuộc cách mạng giao thông xanh vì một tương lai bền vững.</p>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12 col-lg-6 order-lg-1">
            <img src="/assets/image/backgroup/vinclub.webp" alt="VinFast Community" class="img-fluid rounded-4 shadow-lg w-100 object-fit-cover">
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.bg-danger-soft { background: #fee2e2 !important; }
.border-danger { border-color: #ef4444 !important; }
.border-success { border-color: #22c55e !important; }
/* ===== HERO SLIDER ===== */
#hero-slider {
  position: relative;
  height: 100vh;
  min-height: 480px;
  background: #000;
}
.slider-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 1.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.slider-image.active { opacity: 1; }

.slider-dots {
  position: absolute;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 10;
}
.slider-dot {
  width: 10px; height: 10px;
  border-radius: 50%;
  border: none;
  background: rgba(255,255,255,0.45);
  cursor: pointer;
  transition: all 0.3s;
  padding: 0;
}
.slider-dot.active {
  background: #fff;
  transform: scale(1.35);
}

/* ===== SECTION SPACING ===== */
.py-md-7 { padding-top: 5rem !important; padding-bottom: 5rem !important; }

/* ===== SECTION BADGE ===== */
.section-badge {
  display: inline-block;
  background: #e8f0fe;
  color: #1a56db;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.15em;
  padding: 5px 14px;
  border-radius: 20px;
}
.section-badge--blue { background: #dbeafe; color: #1d4ed8; }

/* ===== CAR IMAGE ===== */
.car-image-wrap { position: relative; overflow: hidden; border-radius: 1rem; }
.car-img {
  width: 100%;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}
.car-image-wrap:hover .car-img { transform: scale(1.04); }

/* ===== SPEC CHIP ===== */
.spec-chip {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 50px;
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #374151;
  display: flex;
  align-items: center;
  transition: border-color 0.2s, background 0.2s;
}
.spec-chip:hover { background: #e8f0fe; border-color: #93c5fd; }

/* ===== NAV CIRCLE ===== */
.btn-nav-circle {
  width: 52px; height: 52px;
  border-radius: 50%;
  border: 2px solid #e5e7eb;
  background: #fff;
  color: #374151;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.25s;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.btn-nav-circle:hover { background: #2563eb; border-color: #2563eb; color: #fff; transform: scale(1.08); }

/* ===== ACCESSORIES ===== */
.acc-card { border-radius: 1rem !important; transition: transform 0.3s, box-shadow 0.3s; overflow: hidden; }
.acc-card:hover { transform: translateY(-6px); box-shadow: 0 12px 32px rgba(0,0,0,0.13) !important; }
.acc-img-wrap { overflow: hidden; aspect-ratio: 1; background: #f8f9fa; }
.acc-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s; }
.acc-card:hover .acc-img { transform: scale(1.07); }

/* ===== SERVICE CARDS ===== */
.service-card { border-radius: 1rem !important; transition: transform 0.3s, box-shadow 0.3s; }
.service-card:hover { transform: translateY(-6px); box-shadow: 0 12px 32px rgba(0,0,0,0.12) !important; }
.service-icon { font-size: 1.1rem; }

/* ===== FEATURE ICON ===== */
.feature-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}
.bg-primary-soft { background: #e8f0fe; }
.bg-success-soft { background: #dcfce7; }

/* ===== REVEAL ANIMATION ===== */
.reveal {
  opacity: 0;
  transform: translateY(36px);
  transition: opacity 0.75s ease-out, transform 0.75s ease-out;
}
.reveal.visible {
  opacity: 1;
  transform: translateY(0);
}
</style>