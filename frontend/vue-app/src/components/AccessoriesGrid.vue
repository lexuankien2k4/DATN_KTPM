<template>
  <section class="accessories-section py-5">
    <div class="container">

      <!-- Section Header -->
      <div class="section-header text-center mb-5">
        <span class="section-label">Phụ kiện chính hãng</span>
        <h2 class="section-title">Accessories</h2>
        <p class="section-desc">Nâng cấp xe của bạn với phụ kiện chất lượng cao từ VinAuto</p>
      </div>

      <!-- Grid -->
      <div class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-5 g-3">
        <div class="col" v-for="item in items" :key="item.id">
          <div class="acc-card h-100">
            <!-- Placeholder image area -->
            <div class="acc-img-wrap">
              <div class="acc-img-placeholder">
                <i class="fas fa-box-open"></i>
              </div>
              <span class="acc-badge">Chính hãng</span>
            </div>

            <div class="acc-body p-3">
              <h6 class="acc-name">{{ item.name }}</h6>
              <div class="d-flex align-items-center justify-content-between mt-2">
                <span class="acc-price">{{ formatPrice(item.price) }}</span>
                <button class="acc-add-btn" title="Thêm vào giỏ">
                  <i class="fas fa-plus"></i>
                </button>
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
  name: 'AccessoriesGrid',
  props: {
    items: { type: Array, default: () => [] }
  },
  methods: {
    formatPrice(v) {
      return typeof v === 'number' ? `$${v.toFixed(2)}` : v
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

.accessories-section {
  font-family: 'Be Vietnam Pro', sans-serif;
  background: #f8fafc;
}

/* Header */
.section-label {
  display: inline-block;
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #2563eb;
  background: rgba(37,99,235,0.08);
  padding: 4px 14px;
  border-radius: 20px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 2rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
  margin-bottom: 8px;
}

.section-desc {
  font-size: 0.9rem;
  color: #64748b;
  max-width: 480px;
  margin: 0 auto;
}

/* Card */
.acc-card {
  background: white;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: all 0.25s ease;
  cursor: pointer;
}

.acc-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.1);
  border-color: rgba(37,99,235,0.2);
}

/* Image */
.acc-img-wrap {
  position: relative;
  height: 120px;
  background: linear-gradient(135deg, #f0f4ff, #e8f0fe);
  display: flex;
  align-items: center;
  justify-content: center;
}

.acc-img-placeholder {
  font-size: 2rem;
  color: #bfdbfe;
}

.acc-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  font-size: 0.65rem;
  font-weight: 700;
  background: rgba(37,99,235,0.12);
  color: #2563eb;
  padding: 2px 8px;
  border-radius: 6px;
  letter-spacing: 0.03em;
}

/* Body */
.acc-name {
  font-size: 0.825rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.acc-price {
  font-size: 0.9rem;
  font-weight: 700;
  color: #2563eb;
}

.acc-add-btn {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  border: none;
  color: white;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.acc-add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 10px rgba(37,99,235,0.35);
}
</style>