<template>
  <section class="product-slider-section py-5">
    <div class="container-fluid px-4 px-md-5">

      <!-- Header -->
      <div class="d-flex align-items-end justify-content-between mb-4">
        <div>
          <span class="slider-label">Danh mục nổi bật</span>
          <h2 class="slider-title">{{ title }}</h2>
        </div>
        <div class="d-flex gap-2">
          <button class="slider-arrow" @click="scrollLeft" aria-label="Trước">
            <i class="fas fa-chevron-left"></i>
          </button>
          <button class="slider-arrow" @click="scrollRight" aria-label="Sau">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>

      <!-- Slider track -->
      <div class="slider-track" ref="track">
        <div
          class="product-slide"
          v-for="p in items"
          :key="p.id"
        >
          <div class="product-card h-100">
            <!-- Image placeholder -->
            <div class="product-img-wrap">
              <div class="product-img-placeholder">
                <i class="fas fa-car"></i>
              </div>
              <div class="product-overlay">
                <button class="btn-quick-view">
                  <i class="fas fa-eye me-1"></i> Xem nhanh
                </button>
              </div>
            </div>

            <div class="product-body p-3">
              <span class="product-tag">Xe nổi bật</span>
              <h6 class="product-name mt-1">{{ p.title }}</h6>

              <div class="d-flex align-items-center gap-1 mb-2">
                <i class="fas fa-star text-warning" style="font-size:0.7rem" v-for="n in 5" :key="n"></i>
                <span class="rating-count">(24)</span>
              </div>

              <div class="d-flex align-items-center justify-content-between">
                <span class="product-price">{{ formatPrice(p.price) }}</span>
                <button class="btn-detail">Chi tiết <i class="fas fa-arrow-right ms-1"></i></button>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </section>
</template>

<script>
export default {
  name: 'ProductSlider',
  props: {
    items: { type: Array, default: () => [] },
    title: { type: String, default: 'Products' }
  },
  methods: {
    formatPrice(v) {
      return typeof v === 'number' ? `$${v.toFixed(2)}` : v
    },
    scrollLeft() {
      this.$refs.track.scrollBy({ left: -320, behavior: 'smooth' })
    },
    scrollRight() {
      this.$refs.track.scrollBy({ left: 320, behavior: 'smooth' })
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

.product-slider-section {
  font-family: 'Be Vietnam Pro', sans-serif;
  background: white;
}

/* Header */
.slider-label {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #2563eb;
  display: block;
  margin-bottom: 4px;
}

.slider-title {
  font-size: 1.75rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
  margin: 0;
}

/* Arrow buttons */
.slider-arrow {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: 1.5px solid #e2e8f0;
  background: white;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.slider-arrow:hover {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border-color: #2563eb;
  color: white;
  box-shadow: 0 4px 12px rgba(37,99,235,0.3);
}

/* Track */
.slider-track {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  padding-bottom: 12px;
  /* hide scrollbar but keep functionality */
  scrollbar-width: none;
}

.slider-track::-webkit-scrollbar { display: none; }

/* Slide item */
.product-slide {
  min-width: 260px;
  scroll-snap-align: start;
  flex-shrink: 0;
}

/* Card */
.product-card {
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(0,0,0,0.1);
  border-color: rgba(37,99,235,0.2);
}

/* Image */
.product-img-wrap {
  position: relative;
  height: 160px;
  background: linear-gradient(135deg, #f0f4ff, #dbeafe);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-img-placeholder {
  font-size: 3rem;
  color: #bfdbfe;
  transition: transform 0.3s ease;
}

.product-card:hover .product-img-placeholder {
  transform: scale(1.1);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15,23,42,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay { opacity: 1; }

.btn-quick-view {
  background: white;
  border: none;
  color: #1d4ed8;
  font-size: 0.8rem;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: 'Be Vietnam Pro', sans-serif;
}

.btn-quick-view:hover {
  background: #2563eb;
  color: white;
}

/* Body */
.product-tag {
  font-size: 0.65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #2563eb;
  background: rgba(37,99,235,0.08);
  padding: 2px 8px;
  border-radius: 4px;
}

.product-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rating-count {
  font-size: 0.7rem;
  color: #94a3b8;
  margin-left: 2px;
}

.product-price {
  font-size: 1rem;
  font-weight: 800;
  color: #dc2626;
}

.btn-detail {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 0.775rem;
  font-weight: 600;
  cursor: pointer;
  padding: 0;
  transition: all 0.2s;
  font-family: 'Be Vietnam Pro', sans-serif;
}

.btn-detail:hover {
  color: #1d4ed8;
  transform: translateX(2px);
}
</style>