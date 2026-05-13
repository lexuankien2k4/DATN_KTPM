<template>
  <div class="chat-layout d-flex flex-column">
    <main class="chat-messages flex-grow-1 overflow-y-auto" ref="chatBox">
      <div class="container-fluid px-3 px-md-5 py-4">
        <div
          v-for="(msg, index) in chatStore.messages"
          :key="index"
          :class="['d-flex w-100 mb-4 chat-msg-row', msg.type === 'user' ? 'justify-content-end' : 'justify-content-start']"
        >
          <div v-if="msg.type !== 'user'" class="chat-avatar me-3 flex-shrink-0">
            <i class="fas fa-robot text-white"></i>
          </div>

          <div :class="['d-flex flex-column', msg.type === 'user' ? 'align-items-end' : 'align-items-start']" style="max-width:92%">
            <div :class="['chat-bubble', msg.type === 'user' ? 'chat-bubble--user' : 'chat-bubble--ai']">
              {{ msg.text }}
            </div>

            <div v-if="msg.cars && msg.cars.length > 0" class="row g-3 mt-2 w-100">
              <div v-for="car in msg.cars" :key="car.car_id" class="col-12 col-sm-6 col-xl-4">
                <div class="card car-card h-100 border-0 shadow-sm">
                  <div class="car-card__img-wrap position-relative">
                    <img
                      :src="getImageUrl(car.image_url)"
                      class="car-card__img"
                      :alt="car.name"
                      @error="(e) => e.target.src = '/assets/image/backgroup/vinclub.webp'"
                    />
                    <span class="badge bg-success position-absolute top-0 end-0 m-3 fs-sm fw-bold">
                      {{ car.matchScore }}% MATCH
                    </span>
                  </div>
                  <div class="card-body p-4">
                    <h6 class="card-title fw-bold text-uppercase text-truncate mb-1">{{ car.name }}</h6>
                    <p class="text-primary fw-bold fs-6 mb-3">{{ formatCurrency(car.price) }}</p>
                    <div class="d-grid gap-2 d-sm-flex">
                      <router-link :to="`/product/${car.car_id}`" class="btn btn-outline-primary btn-sm fw-bold rounded-pill flex-fill text-center">Chi Tiết</router-link>
                      <router-link :to="{ path: '/deposit', query: { id: car.car_id } }" class="btn btn-primary btn-sm fw-bold rounded-pill flex-fill text-center">Mua Trả Góp</router-link>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="msg.type === 'user'" class="chat-avatar chat-avatar--user ms-3 flex-shrink-0">
            <i class="fas fa-user text-white"></i>
          </div>
        </div>

        <div v-if="loading" class="d-flex justify-content-start mb-4">
          <div class="chat-avatar me-3 flex-shrink-0"><i class="fas fa-robot text-white"></i></div>
          <div class="chat-bubble chat-bubble--ai chat-bubble--typing">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </main>

    <footer class="chat-footer bg-white border-top px-3 px-md-5 py-3">
      <div class="input-group input-group-lg shadow-sm rounded-4 overflow-hidden border">
        <input
          v-model="userInput"
          type="text"
          class="form-control border-0 px-4 py-3 bg-light"
          placeholder="Hỏi về dòng xe bạn quan tâm..."
          :disabled="loading"
          @keyup.enter="handleSendMessage"
        />
        <button
          @click="handleSendMessage"
          :disabled="!userInput.trim() || loading"
          class="btn btn-primary px-4 border-0"
          style="border-radius:0"
        >
          <i class="fas fa-paper-plane fs-5"></i>
        </button>
      </div>
      <p class="text-center text-muted mt-2 mb-0" style="font-size:0.68rem; letter-spacing:0.2em; text-transform:uppercase">
        Hệ thống tư vấn VinAuto AI
      </p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useChatStore } from '@/stores/chatStore'
import api from '@/utils/axios'

const chatStore = useChatStore()
const userInput = ref('')
const loading = ref(false)
const chatBox = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTo({ top: chatBox.value.scrollHeight, behavior: 'smooth' })
  }
}

watch(() => chatStore.messages.length, scrollToBottom)

const getImageUrl = (path) => {
  if (!path) return '/assets/image/backgroup/vinclub.webp'
  return `http://localhost:8080${path.startsWith('/') ? '' : '/'}${path}`
}

const handleSendMessage = async () => {
  const text = userInput.value.trim()
  if (!text || loading.value) return
  
  // 1. Thêm tin nhắn của user vào store
  chatStore.addMessage({ type: 'user', text })
  userInput.value = ''
  loading.value = true
  
  try {
    // 2. Gọi API
    const response = await api.post('/api/ai/chat-consult', { message: text })
    
    // 3. Xử lý trường hợp "Không liên quan đến xe" (off_topic)
    if (response.data.status === 'off_topic') {
       chatStore.addMessage({
          type: 'ai',
          text: response.data.message, 
          cars: []
       })
       return; 
    }

    // 4. Xử lý luồng bình thường (Tìm kiếm xe)
    const cars = response.data.result ?? []
    let aiText = response.data.message || 'Dạ, đây là các mẫu xe phù hợp nhất với yêu cầu của bạn:'
    
    // SỬA Ở ĐÂY: Update lại câu text bao quát nhất
    if (cars.length === 0) {
      aiText = 'Dạ, em chưa tìm thấy mẫu xe nào khớp hoặc nhu cầu của mình chưa đủ thông tin. Bạn có thể chia sẻ thêm về khoảng giá, số chỗ ngồi hoặc kiểu dáng xe (ví dụ: gầm cao, điện) được không ạ?'
    }

    // 5. Thêm tin nhắn của AI vào giao diện
    chatStore.addMessage({
      type: 'ai',
      text: aiText,
      cars: cars
    })
    
  } catch (error) {
    chatStore.addMessage({ 
      type: 'ai', 
      text: 'Dạ, hệ thống tư vấn đang bận hoặc gián đoạn. Vui lòng thử lại sau giây lát nhé!' 
    })
  } finally {
    loading.value = false
  }
}

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)

onMounted(scrollToBottom)
</script>

<style scoped>
.chat-layout {
  /* TÍNH TOÁN LẠI CHIỀU CAO: Trừ đi 80px (chiều cao của Navbar/Header) */
  height: calc(100vh - 80px);
  /* ĐẨY KHUNG CHAT XUỐNG DƯỚI HEADER */
  margin-top: 80px;
  background: #f8fafc;
  font-family: 'Segoe UI', system-ui, sans-serif;
  overflow: hidden;
}

.chat-messages {
  /* Đã xóa padding-top: 80px ở đây vì layout tổng đã được đẩy xuống */
  scrollbar-width: thin;
  scrollbar-color: #dde1e7 transparent;
}
.chat-messages::-webkit-scrollbar { width: 5px; }
.chat-messages::-webkit-scrollbar-thumb { background: #dde1e7; border-radius: 10px; }

/* Avatars */
.chat-avatar {
  width: 38px; height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  font-size: 0.9rem;
  box-shadow: 0 2px 8px rgba(37,99,235,0.3);
  margin-top: 2px;
}
.chat-avatar--user {
  background: linear-gradient(135deg, #374151, #6b7280);
  box-shadow: 0 2px 8px rgba(55,65,81,0.25);
}

/* Bubbles */
.chat-bubble {
  padding: 14px 20px;
  border-radius: 1.2rem;
  font-size: 0.97rem;
  line-height: 1.65;
  white-space: pre-line;
  max-width: 640px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  animation: fadeInMsg 0.3s ease-out;
}
.chat-bubble--user {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: #fff;
  border-top-right-radius: 4px;
}
.chat-bubble--ai {
  background: #fff;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-top-left-radius: 4px;
}

/* Typing dots */
.chat-bubble--typing {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 16px 20px;
  min-width: 70px;
}
.chat-bubble--typing span {
  display: inline-block;
  width: 8px; height: 8px;
  background: #9ca3af;
  border-radius: 50%;
  animation: typingBounce 1.2s infinite;
}
.chat-bubble--typing span:nth-child(2) { animation-delay: 0.2s; }
.chat-bubble--typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.5; }
  30% { transform: translateY(-8px); opacity: 1; }
}

/* Car cards */
.car-card {
  border-radius: 1rem !important;
  transition: transform 0.3s, box-shadow 0.3s;
  overflow: hidden;
}
.car-card:hover { transform: translateY(-5px); box-shadow: 0 12px 30px rgba(0,0,0,0.12) !important; }
.car-card__img-wrap {
  background: #f8f9fa;
  height: 190px;
  overflow: hidden;
  display: flex; align-items: center; justify-content: center;
}
.car-card__img {
  width: 100%; height: 100%;
  object-fit: contain;
  padding: 1rem;
  transition: transform 0.4s;
}
.car-card:hover .car-card__img { transform: scale(1.06); }

/* Footer */
.chat-footer { padding-bottom: max(12px, env(safe-area-inset-bottom)); }
.chat-footer .input-group { border-color: #e5e7eb !important; }
.chat-footer .form-control:focus { box-shadow: none; background: #fff !important; }

@keyframes fadeInMsg {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

.fs-sm { font-size: 0.72rem; }
</style>