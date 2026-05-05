<script setup>
// (Không cần script gì đặc biệt cho footer)
</script>

<template>
  <footer id="contact" class="app-footer pt-5 pb-3">
    <div class="container">
      <div class="footer-top-line mb-5"></div>

      <div class="row g-5 mb-5">
        <div class="col-12 col-md-4">
          <div class="d-flex align-items-center gap-2 mb-3">
            <div class="footer-logo-icon">
              <i class="fas fa-car"></i>
            </div>
            <span class="footer-brand">Vin<span class="footer-brand-accent">Auto</span></span>
          </div>
          <p class="footer-tagline">Cùng bạn kiến tạo một tương lai xanh hơn, thông minh hơn và an toàn hơn.</p>
          <div class="d-flex gap-2 mt-4">
            <a href="#" class="social-btn"><i class="fab fa-facebook-f"></i></a>
            <a href="#" class="social-btn"><i class="fab fa-youtube"></i></a>
            <a href="#" class="social-btn"><i class="fab fa-instagram"></i></a>
          </div>
        </div>

        <div class="col-6 col-md-2">
          <h6 class="footer-heading">Khám Phá</h6>
          <ul class="list-unstyled footer-links">
            <li><a href="#">Về chúng tôi</a></li>
            <li><router-link to="/#cars-showcase">Sản phẩm ô tô</router-link></li>
            <li><router-link to="/#bikes-showcase">Sản phẩm xe máy</router-link></li>
          </ul>
        </div>

        <div class="col-6 col-md-2">
          <h6 class="footer-heading">Hỗ Trợ</h6>
          <ul class="list-unstyled footer-links">
            <li><a href="#">Tìm Showroom</a></li>
            <li><a href="#">Chính sách bảo hành</a></li>
            <li><a href="#">Liên hệ</a></li>
          </ul>
        </div>

        <div class="col-12 col-md-4">
          <h6 class="footer-heading">Đăng Ký Nhận Tin</h6>
          <p class="footer-tagline mb-3">Nhận thông tin mới nhất và ưu đãi.</p>
          <form class="newsletter-form" @submit.prevent>
            <div class="input-group">
              <input
                type="email"
                placeholder="Email của bạn..."
                class="form-control newsletter-input"
              />
              <button type="submit" class="newsletter-btn">
                <i class="fas fa-paper-plane"></i>
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="footer-bottom d-flex flex-column flex-md-row justify-content-between align-items-center gap-2 pt-4">
        <p class="mb-0 footer-copy">&copy; {{ new Date().getFullYear() }} VinAuto Corporation. All rights reserved.</p>
        <div class="d-flex gap-3">
          <a href="#" class="footer-policy-link">Chính sách bảo mật</a>
          <a href="#" class="footer-policy-link">Điều khoản sử dụng</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

.app-footer {
  background: #ffffff; /* Đổi thành nền trắng */
  color: #475569;      /* Màu chữ xám đậm */
  font-family: 'Be Vietnam Pro', sans-serif;
  border-top: 1px solid #e2e8f0; /* Thêm viền nhẹ phía trên */
}

.footer-top-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
}

.footer-brand {
  font-size: 1.35rem;
  font-weight: 800;
  color: #0f172a; /* Màu thương hiệu tối */
  letter-spacing: -0.5px;
}

.footer-brand-accent { color: #2563eb; }

.footer-tagline {
  font-size: 0.875rem;
  color: #64748b;
  line-height: 1.6;
}

.social-btn {
  width: 36px;
  height: 36px;
  border-radius: 9px;
  background: #f1f5f9; /* Nền nút mạng xã hội sáng */
  border: 1px solid #e2e8f0;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  text-decoration: none;
  transition: all 0.25s ease;
}

.social-btn:hover {
  background: #e0e7ff;
  border-color: #c7d2fe;
  color: #2563eb;
  transform: translateY(-2px);
}

.footer-heading {
  color: #1e293b; /* Tiêu đề mục màu tối */
  font-weight: 700;
  font-size: 0.875rem;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: 1rem;
}

.footer-links a {
  color: #64748b;
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.2s;
}

.footer-links a:hover { color: #2563eb; }

.newsletter-input {
  background: #f8fafc !important;
  border: 1px solid #cbd5e1 !important;
  color: #0f172a !important;
  border-radius: 10px 0 0 10px !important;
}

.newsletter-input::placeholder { color: #94a3b8; }

.newsletter-btn {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  border: none;
  color: white;
  padding: 10px 20px;
  border-radius: 0 10px 10px 0;
}

.footer-bottom {
  border-top: 1px solid #f1f5f9;
}

.footer-copy, .footer-policy-link {
  font-size: 0.8rem;
  color: #94a3b8;
}

.footer-policy-link:hover { color: #2563eb; }
</style>