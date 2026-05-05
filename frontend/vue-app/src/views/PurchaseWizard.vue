<template>
  <div class="min-h-screen bg-gray-50 font-sans text-gray-800 flex flex-col overflow-hidden">
    
    <header class="flex-none bg-white px-8 py-4 z-50 shadow-sm flex items-center justify-between">
      <router-link to="/" class="w-32 block cursor-pointer">
        <img src="@/assets/image/icon_logo/VinFast-logo.svg" alt="VinFast" class="h-8 md:h-10">
      </router-link>
      <div class="flex items-center gap-2 md:gap-6">
        <div v-for="(s, i) in steps" :key="i" class="flex items-center gap-2">
          <span :class="['w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold text-white transition-colors', step >= i+1 ? 'bg-blue-600' : 'bg-gray-300']">{{ i+1 }}</span>
          <span :class="['text-sm font-bold uppercase hidden md:block transition-colors', step >= i+1 ? 'text-blue-600' : 'text-gray-400']">{{ s }}</span>
          <svg v-if="i < 3" class="w-4 h-4 text-gray-300 mx-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
        </div>
      </div>
    </header>

    <div class="flex-1 max-w-7xl w-full mx-auto p-6 grid grid-cols-12 gap-8 h-full overflow-hidden">
      
      <aside class="col-span-12 lg:col-span-4 h-full flex flex-col">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 flex flex-col h-full overflow-hidden sticky top-6">
      <div class="p-4 text-center bg-white rounded-top-4">
          <h4 class="fw-black text-primary italic uppercase mb-4">
              {{ selectedCar?.name || 'Đang Hiển thị...' }}
          </h4>
          
          <img 
              :src="getImageUrl(mainImage)" 
              class="img-fluid drop-shadow py-4" 
              style="max-height: 200px; object-fit: contain;"
              alt="Car Preview"
          >
      </div>
          
          <div class="p-6 flex-1 overflow-y-auto">
            <div class="space-y-4 text-sm">
              <div class="flex justify-between items-center border-b border-dashed pb-2">
                <span class="text-gray-500 font-medium">Giá niêm yết</span>
                <span class="font-bold text-gray-900">{{ formatCurrency(selectedCar?.rawPrice) }}</span>
              </div>
              <div class="flex justify-between items-center border-b border-dashed pb-2">
                <span class="text-gray-500 font-medium">Lựa chọn Pin</span>
                <span :class="['font-bold', form.batteryServiceType === 'PURCHASE' ? 'text-green-600' : 'text-blue-600']">
                  {{ form.batteryServiceType === 'PURCHASE' ? 'Mua pin (+90.000.000 ₫)' : 'Thuê pin' }}
                </span>
              </div>
              <div class="flex justify-between items-center border-b border-dashed pb-2">
                <span class="text-gray-500 font-medium">Tổng giá trị xe</span>
                <span class="font-bold text-lg text-gray-900">{{ formatCurrency(totalCarPrice) }}</span>
              </div>
            </div>

            <div class="mt-6 bg-blue-50 p-4 rounded-xl border border-blue-100">
               <div class="flex justify-between text-sm mb-2">
                 <span class="text-gray-600">Trả trước ({{ form.prepaymentPercent }}%)</span>
                 <span class="font-bold">{{ formatCurrency(prepaymentAmount) }}</span>
               </div>
               <div class="flex justify-between text-sm mb-2">
                 <span class="text-gray-600">Số tiền vay</span>
                 <span class="font-bold">{{ formatCurrency(loanAmount) }}</span>
               </div>
               <div class="flex justify-between text-sm mb-4">
                 <span class="text-gray-600">Kỳ hạn vay</span>
                 <span class="font-bold">{{ form.loanTermYears }} năm ({{ form.loanTermYears * 12 }} tháng)</span>
               </div>
            </div>

            <div class="bg-blue-900 text-white p-5 rounded-xl shadow-inner mt-4">
              <p class="text-xs text-blue-200 uppercase tracking-wider text-center mb-1">Trả góp hằng tháng (Dự kiến)</p>
              <h2 class="text-3xl font-black text-center">{{ formatCurrency(monthlyInstallment) }}</h2>
              <p class="text-[10px] text-blue-300 text-center mt-2 italic">* Số liệu tạm tính dựa trên gói lãi suất ưu đãi hiện tại.</p>
            </div>
          </div>
        </div>
      </aside>

      <main class="col-span-12 lg:col-span-8 bg-white rounded-2xl shadow-sm border border-gray-100 p-8 h-full overflow-y-auto hide-scrollbar relative">
        
        <transition name="fade" mode="out-in">
          <div v-if="step === 1" key="step1" class="space-y-8">
            <h2 class="text-2xl font-bold text-gray-800 border-l-4 border-blue-600 pl-4 uppercase italic">1. Cấu hình Pin & Tài chính</h2>
            
            <div>
              <label class="block text-sm font-bold text-gray-700 uppercase mb-3">Hình thức sở hữu Pin</label>
              <div class="grid grid-cols-2 gap-4">
                <button @click="form.batteryServiceType = 'RENTAL'" :class="['py-4 rounded-xl font-bold border-2 transition-all', form.batteryServiceType === 'RENTAL' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-gray-200 hover:border-blue-300']">
                  THUÊ PIN
                </button>
                <button @click="form.batteryServiceType = 'PURCHASE'" :class="['py-4 rounded-xl font-bold border-2 transition-all', form.batteryServiceType === 'PURCHASE' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-gray-200 hover:border-blue-300']">
                  MUA ĐỨT PIN
                </button>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-bold text-gray-700 uppercase mb-3">Ngân hàng đối tác</label>
                <select v-model="selectedBank" class="w-full bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-3">
                  <option :value="null" disabled>Chọn ngân hàng</option>
                  <option v-for="bank in banks" :key="bank.id" :value="bank">{{ bank.name }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-bold text-gray-700 uppercase mb-3">Gói lãi suất ưu đãi</label>
                <select v-model="selectedPolicy" :disabled="!selectedBank || isLoadingPolicies" class="w-full bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-3">
                  <option :value="null" disabled>{{ isLoadingPolicies ? 'Đang tải...' : 'Chọn gói vay' }}</option>
                  <option v-for="p in bankPolicies" :key="p.id" :value="p">{{ p.name }} ({{ p.preferentialRatePercent }}%/năm)</option>
                </select>
              </div>
            </div>

            <div>
              <label class="block text-sm font-bold text-gray-700 uppercase mb-3">Thời gian vay (Năm)</label>
              <div class="grid grid-cols-4 md:grid-cols-8 gap-2">
                <button v-for="year in [1, 2, 3, 4, 5, 6, 7, 8]" :key="year" @click="form.loanTermYears = year"
                  :class="['py-2 border rounded-lg text-sm font-bold transition-colors', form.loanTermYears === year ? 'bg-blue-600 border-blue-600 text-white shadow-md' : 'bg-white text-gray-700 hover:bg-gray-50']">
                  {{ year }}
                </button>
              </div>
            </div>

            <div>
              <label class="block text-sm font-bold text-gray-700 uppercase mb-3">Tỷ lệ trả trước</label>
              <div class="grid grid-cols-4 md:grid-cols-8 gap-2">
                <button v-for="p in [20, 30, 40, 50, 60, 70, 80]" :key="p" @click="form.prepaymentPercent = p"
                  :disabled="selectedPolicy && p < (selectedPolicy.minDownPaymentRate * 100)"
                  :class="['py-2 border rounded-lg text-sm font-bold transition-colors disabled:opacity-30 disabled:cursor-not-allowed', form.prepaymentPercent === p ? 'bg-blue-600 border-blue-600 text-white shadow-md' : 'bg-white text-gray-700 hover:bg-gray-50']">
                  {{ p }}%
                </button>
              </div>
              <p v-if="selectedPolicy" class="text-xs text-red-500 mt-2">* Gói vay này yêu cầu trả trước tối thiểu {{ selectedPolicy.minDownPaymentRate * 100 }}%</p>
            </div>

            <div class="pt-6 border-t flex justify-end">
              <button @click="nextStep(1)" class="bg-blue-600 text-white font-bold py-3 px-8 rounded-xl hover:bg-blue-700 shadow-lg transform hover:-translate-y-0.5 transition-all">
                BƯỚC TIẾP THEO
              </button>
            </div>
          </div>
        </transition>

        <transition name="fade" mode="out-in">
          <div v-if="step === 2" key="step2" class="space-y-6 pb-10">
            <h2 class="text-2xl font-bold text-gray-800 border-l-4 border-blue-600 pl-4 uppercase italic">2. Thông tin khách hàng định danh</h2>
            
            <form @submit.prevent class="grid grid-cols-2 gap-6">
              <div class="col-span-2 md:col-span-1">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Họ và tên <span class="text-red-500">*</span></label>
                <input v-model="form.fullName" type="text" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500" placeholder="Nguyễn Văn A">
                <span v-if="errors.fullName" class="text-red-500 text-xs mt-1">{{ errors.fullName }}</span>
              </div>
              <div class="col-span-2 md:col-span-1">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Số điện thoại <span class="text-red-500">*</span></label>
                <input v-model="form.phone" type="tel" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500" placeholder="09xxxx">
                 <span v-if="errors.phone" class="text-red-500 text-xs mt-1">{{ errors.phone }}</span>
              </div>
              <div class="col-span-2 md:col-span-1">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Email</label>
                <input v-model="form.email" type="email" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500" placeholder="email@domain.com">
              </div>
              <div class="col-span-2 md:col-span-1">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Số CCCD/CMND <span class="text-red-500">*</span></label>
                <input v-model="form.citizenId" type="text" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500" placeholder="Nhập mã định danh">
                <span v-if="errors.citizenId" class="text-red-500 text-xs mt-1">{{ errors.citizenId }}</span>
              </div>

              <div class="col-span-2 pt-4"><h3 class="font-bold text-gray-800 border-b pb-2">ĐỊA ĐIỂM & LỊCH HẸN KÝ HỢP ĐỒNG</h3></div>

              <div class="col-span-2 md:col-span-1">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Tỉnh/Thành phố <span class="text-red-500">*</span></label>
                <select v-model="form.province" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="">Chọn Tỉnh/Thành</option>
                    <option v-for="prov in uniqueProvinces" :key="prov" :value="prov">{{ prov }}</option>
                </select>
              </div>
              <div class="col-span-2 md:col-span-1">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Showroom <span class="text-red-500">*</span></label>
                <select v-model="form.showroomId" :disabled="!form.province" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500">
                    <option value="">Chọn Showroom</option>
                    <option v-for="show in filteredShowrooms" :key="show.id" :value="show.id">{{ show.name }}</option>
                </select>
              </div>
              <div class="col-span-2">
                <label class="block text-sm font-bold text-gray-700 mb-1 uppercase">Ngày hẹn đến ký hợp đồng gốc <span class="text-red-500">*</span></label>
                <input v-model="form.signingAppointmentDate" type="datetime-local" class="w-full bg-gray-50 border border-gray-300 rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500">
                <p class="text-xs text-gray-500 mt-1">Lịch hẹn dự kiến để tới showroom hoàn tất thủ tục giấy tờ pháp lý.</p>
              </div>
            </form>

            <div class="pt-6 border-t flex gap-4">
              <button @click="step--" class="w-1/3 bg-gray-200 text-gray-700 font-bold py-3 rounded-xl hover:bg-gray-300 transition-all uppercase">QUAY LẠI</button>
              <button @click="nextStep(2)" class="w-2/3 bg-blue-600 text-white font-bold py-3 rounded-xl hover:bg-blue-700 shadow-lg transition-all uppercase">XÁC NHẬN & XEM HỢP ĐỒNG</button>
            </div>
          </div>
        </transition>

        <transition name="fade" mode="out-in">
          <div v-if="step === 3" key="step3" class="h-full flex flex-col pb-6">
             <h2 class="text-2xl font-bold text-gray-800 border-l-4 border-blue-600 pl-4 uppercase italic mb-6">3. Ký kết hợp đồng điện tử</h2>
             
             <div class="flex-1 bg-gray-50 border border-gray-200 rounded-xl p-8 overflow-y-auto mb-6 shadow-inner relative font-serif text-gray-800">
                <div class="text-center mb-8">
                  <h1 class="text-2xl font-bold uppercase mb-2">HỢP ĐỒNG MUA BÁN XE Ô TÔ</h1>
                  <p class="text-sm">Số: {{ generatedDraftNumber || 'DRAFT-XXX' }}/HĐMB/VF</p>
                </div>
                
                <div class="space-y-4 leading-relaxed">
                  <p class="font-bold uppercase">BÊN BÁN (BÊN A): CÔNG TY TNHH KINH DOANH THƯƠNG MẠI VÀ DỊCH VỤ VINFAST</p>
                  <p class="font-bold uppercase">BÊN MUA (BÊN B):</p>
                  <ul class="list-disc pl-8">
                    <li>Họ và tên: <strong>{{ form.fullName }}</strong></li>
                    <li>CCCD/CMND số: <strong>{{ form.citizenId }}</strong></li>
                    <li>Số điện thoại: <strong>{{ form.phone }}</strong></li>
                    <li>Email: <strong>{{ form.email || '(Không cung cấp)' }}</strong></li>
                  </ul>

                  <h3 class="font-bold text-lg mt-6 mb-2">ĐIỀU 1: THÔNG TIN SẢN PHẨM & TÀI CHÍNH</h3>
                  <table class="w-full text-sm border-collapse border border-gray-300 mb-4">
                    <tbody>
                      <tr><td class="border p-2 font-medium bg-gray-100 w-1/3">Loại xe</td><td class="border p-2 font-bold">{{ selectedCar?.name }}</td></tr>
                      <tr><td class="border p-2 font-medium bg-gray-100">Gói Dịch vụ Pin</td><td class="border p-2">{{ form.batteryServiceType === 'PURCHASE' ? 'Mua đứt' : 'Thuê pin' }}</td></tr>
                      <tr><td class="border p-2 font-medium bg-gray-100">Tổng giá trị HĐ</td><td class="border p-2 text-blue-700 font-bold">{{ formatCurrency(totalCarPrice) }}</td></tr>
                      <tr><td class="border p-2 font-medium bg-gray-100">Trả trước ({{form.prepaymentPercent}}%)</td><td class="border p-2">{{ formatCurrency(prepaymentAmount) }}</td></tr>
                      <tr><td class="border p-2 font-medium bg-gray-100">Số tiền vay</td><td class="border p-2">{{ formatCurrency(loanAmount) }} ({{ form.loanTermYears * 12 }} tháng qua {{ selectedBank?.name || 'Ngân hàng đối tác' }})</td></tr>
                    </tbody>
                  </table>

                  <h3 class="font-bold text-lg mt-6 mb-2">ĐIỀU 2: ĐẶT CỌC VÀ GIAO NHẬN</h3>
                  <p>1. Bên B đồng ý thanh toán khoản tiền cọc để giữ xe (đối soát qua hệ thống VNPAY).</p>
                  <p>2. Bên B sẽ có mặt tại Showroom <strong>{{ getFullShowroomInfo() }}</strong> vào thời gian <strong>{{ formatDateTime(form.signingAppointmentDate) }}</strong> để ký bản cứng Hợp đồng Tín dụng và Hợp đồng Mua bán chính thức.</p>
                </div>

                <div class="mt-12 flex justify-between px-10">
                  <div class="text-center">
                    <p class="font-bold mb-8">ĐẠI DIỆN BÊN A</p>
                    <img src="@/assets/image/icon_logo/VinFast-logo.svg" class="h-16 mx-auto opacity-70 mix-blend-multiply">
                  </div>
                  <div class="text-center">
                    <p class="font-bold mb-8">ĐẠI DIỆN BÊN B</p>
                    <div v-if="!form.isSigned" @click="form.isSigned = true" class="px-6 py-4 border-2 border-dashed border-blue-500 rounded-lg text-blue-600 font-bold cursor-pointer hover:bg-blue-50 transition-colors">
                      CHẠM ĐỂ KÝ TÊN ĐIỆN TỬ
                    </div>
                    <div v-else class="text-blue-600" style="font-family: 'Brush Script MT', cursive; font-size: 2.5rem; transform: rotate(-5deg);">
                      {{ form.fullName }}
                    </div>
                  </div>
                </div>
             </div>

             <div class="flex gap-4 flex-none">
              <button @click="step--" class="w-1/3 bg-gray-200 text-gray-700 font-bold py-4 rounded-xl hover:bg-gray-300 transition-all uppercase tracking-wide">CHỈNH SỬA</button>
              <button @click="nextStep(3)" :disabled="!form.isSigned" class="w-2/3 bg-blue-600 text-white font-bold py-4 rounded-xl hover:bg-blue-700 shadow-lg transition-all uppercase tracking-wide disabled:bg-gray-400 disabled:cursor-not-allowed">
                CHUYỂN SANG THANH TOÁN CỌC
              </button>
            </div>
          </div>
        </transition>

        <transition name="fade" mode="out-in">
          <div v-if="step === 4" key="step4" class="flex flex-col items-center justify-center h-full text-center py-10">
            <div class="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mb-6">
              <svg class="w-12 h-12 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            </div>
            <h2 class="text-3xl font-black text-gray-800 mb-2">Hợp đồng điện tử đã được xác nhận!</h2>
            <p class="text-gray-500 mb-8 max-w-md">Để hoàn tất việc giữ xe (khóa số VIN), Quý khách vui lòng tiến hành thanh toán tiền đặt cọc thông qua cổng thanh toán an toàn VNPAY.</p>

            <div class="bg-gray-50 w-full max-w-md p-6 rounded-2xl border border-gray-200 mb-8 text-left">
              <div class="flex justify-between items-center mb-3">
                <span class="text-gray-500 font-medium">Số tiền đặt cọc:</span>
                <span class="text-2xl font-bold text-blue-700">{{ formatCurrency(prepaymentAmount) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-500 font-medium">Phương thức:</span>
                <div class="flex items-center gap-2">
                  <img src="https://vnpayment.vn/images/logo-vnpayment.png" class="h-5">
                </div>
              </div>
            </div>

            <button @click="submitContractAndPay" :disabled="isLoadingPayment" class="w-full max-w-md bg-blue-600 text-white font-bold py-4 rounded-xl hover:bg-blue-700 shadow-xl transition-all uppercase tracking-wide flex justify-center items-center gap-2 disabled:bg-gray-400">
              <span v-if="isLoadingPayment" class="animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full"></span>
              <span>{{ isLoadingPayment ? 'ĐANG CHUYỂN HƯỚNG...' : 'THANH TOÁN QUA VNPAY' }}</span>
            </button>
            <p class="text-xs text-gray-400 mt-4"><i class="bi bi-shield-lock me-1"></i> Giao dịch được mã hóa và bảo mật bởi VNPAY.</p>
          </div>
        </transition>

      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const step = ref(1)
const steps = ['Tài chính', 'Thông tin', 'Ký hợp đồng', 'Thanh toán']

// --- API & State ---
const apiClient = axios.create({ baseURL: 'http://localhost:8080/api' })
const banks = ref([])
const bankPolicies = ref([])
const showroomsData = ref([])
const selectedCar = ref(null)

const isLoadingPolicies = ref(false)
const isLoadingPayment = ref(false)
const generatedDraftNumber = ref('')

const BATTERY_COST = 90000000

// --- Form State (Tương ứng với Entity Contract & Deposit) ---
const selectedBank = ref(null)
const selectedPolicy = ref(null)

const form = reactive({
    // Contract Finance Fields
    batteryServiceType: 'RENTAL',
    loanTermYears: 7, // Lưu ý: Backend cần months
    prepaymentPercent: 30, // UI state, convert to Amount before send
    
    // Deposit & Customer Info Fields
    fullName: '',
    phone: '',
    email: '',
    citizenId: '',
    province: '',
    showroomId: '',
    
    // Contract Appointment & Signature
    signingAppointmentDate: '',
    isSigned: false
})

const errors = reactive({ fullName: '', phone: '', citizenId: '' })

// --- Computed ---
const currentDisplayImage = computed(() => selectedCar.value?.image ? `http://localhost:8080${selectedCar.value.image}` : '')

const totalCarPrice = computed(() => {
    if (!selectedCar.value) return 0
    return selectedCar.value.rawPrice + (form.batteryServiceType === 'PURCHASE' ? BATTERY_COST : 0)
})

const prepaymentAmount = computed(() => {
    return (totalCarPrice.value * form.prepaymentPercent) / 100
})

const loanAmount = computed(() => {
    return totalCarPrice.value - prepaymentAmount.value
})

const monthlyInstallment = computed(() => {
    if (!selectedPolicy.value || loanAmount.value <= 0) return 0
    const p = loanAmount.value
    const r = (selectedPolicy.value.preferentialRatePercent / 100) / 12
    const n = form.loanTermYears * 12
    if(r <= 0) return p / n
    return Math.round((p * (r * Math.pow(1 + r, n))) / (Math.pow(1 + r, n) - 1))
})



const uniqueProvinces = computed(() => [...new Set(showroomsData.value.map(s => s.province))])
const filteredShowrooms = computed(() => showroomsData.value.filter(s => s.province === form.province))
const mainImage = ref('');

// Cập nhật hàm getImageUrl để xử lý đường dẫn tuyệt đối từ server
const getImageUrl = (path) => {
    if (!path) return '/assets/image/icon_logo/VinFast-logo.svg'; // Ảnh mặc định nếu null
    // Xử lý logic nối chuỗi URL server
    return `http://localhost:8080${path.startsWith('/') ? '' : '/'}${path}`;
};
// --- Logic ---
// --- Logic ---
onMounted(async () => {
    const carId = route.query.id;
    try {
        if (carId) {
            const res = await axios.get(`http://localhost:8080/api/variants/${carId}/details`);

            // Variant API trả về ApiRequest (không có code=1000) nên ta chỉ check res.data.result
            if (res.data && res.data.result) {
                const c = res.data.result;
                
                selectedCar.value = { 
                    id: c.id, 
                    name: c.name, 
                    rawPrice: c.price, 
                    // Ưu tiên lấy ảnh đầu tiên trong mảng images, nếu không có thì lấy ảnh thumbnail/image lẻ
                    thumbnail: (c.images && c.images.length > 0) ? c.images[0].imageUrl : (c.image || c.firstImageUrl || ''), 
                    specs: { 
                        power: `${c.maxPower || c.power || 'N/A'} kW`, 
                        range: `${c.rangeNedc || c.range || 'N/A'} km`, 
                        seats: c.seats || 5 
                    }
                };

                // QUAN TRỌNG: Gán đường dẫn ảnh cho mainImage để template (<img>) có thể hiển thị
                mainImage.value = selectedCar.value.thumbnail;
            }
        }
        
        
        const [bankRes, showroomRes] = await Promise.all([
            apiClient.get('/banks/active'),
            apiClient.get('/public/showrooms')
        ])
        
        if (bankRes.data.code === 1000) banks.value = bankRes.data.result
        if (showroomRes.data.code === 1000) showroomsData.value = showroomRes.data.result
        
        // Đặt mặc định ngày hẹn là 3 ngày sau
        const date = new Date()
        date.setDate(date.getDate() + 3)
        date.setHours(14, 0, 0, 0) // 14:00
        form.signingAppointmentDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().slice(0,16)

    } catch (e) {
        console.error("Lỗi khởi tạo:", e)
    }
})

watch(selectedBank, async (newBank) => {
    if (newBank) {
        isLoadingPolicies.value = true
        try {
            const res = await apiClient.get(`/finance/policies/by-bank/${newBank.id}/active`)
            if (res.data.code === 1000) {
                bankPolicies.value = res.data.result
                if(bankPolicies.value.length > 0) {
                    selectedPolicy.value = bankPolicies.value[0]
                }
            }
        } catch(e) { console.error(e) }
        finally { isLoadingPolicies.value = false }
    } else {
        bankPolicies.value = []
        selectedPolicy.value = null
    }
})

watch(selectedPolicy, (newPolicy) => {
    if (newPolicy && newPolicy.minDownPaymentRate) {
        const minPercent = newPolicy.minDownPaymentRate * 100;
        if (form.prepaymentPercent < minPercent) form.prepaymentPercent = minPercent;
    }
})

// --- Helpers ---
const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
const formatDateTime = (dt) => {
    if(!dt) return '';
    const date = new Date(dt);
    return date.toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric' });
}
const getFullShowroomInfo = () => {
    const s = showroomsData.value.find(x => x.id === form.showroomId)
    return s ? `${s.name} - ${s.address}, ${s.district}, ${s.province}` : ''
}

// --- Navigation & Validation ---
const validateStep2 = () => {
    let isValid = true
    errors.fullName = !form.fullName ? 'Vui lòng nhập họ tên' : ''
    errors.phone = !/^\d{10,11}$/.test(form.phone) ? 'SĐT không hợp lệ' : ''
    errors.citizenId = !form.citizenId ? 'Vui lòng nhập CCCD' : ''
    
    if(errors.fullName || errors.phone || errors.citizenId) isValid = false
    if(!form.showroomId || !form.signingAppointmentDate) {
        alert("Vui lòng chọn Showroom và Ngày hẹn hợp lệ!")
        isValid = false
    }
    return isValid
}

const nextStep = (current) => {
    if (current === 1) {
        if(!selectedPolicy.value) return alert("Vui lòng chọn ngân hàng và gói vay!")
        step.value = 2
    }
    else if (current === 2) {
        if(validateStep2()) {
            generatedDraftNumber.value = `VF${Math.floor(Math.random() * 1000000)}`
            step.value = 3
        }
    }
    else if (current === 3) {
        step.value = 4
    }
}

const submitContractAndPay = async () => {
    isLoadingPayment.value = true;
    
    // 1. Payload tinh gọn, KHỚP CHÍNH XÁC với PaymentCreationRequest
    const payload = {
        // LẤY ĐÚNG SỐ TIỀN TRẢ TRƯỚC (Làm tròn để tránh lỗi số thập phân với VNPay)
        amount: Math.round(prepaymentAmount.value), 
        fullName: form.fullName,
        phone: form.phone,
        email: form.email,
        citizenId: form.citizenId, 
        variantId: selectedCar.value.id,
        showroomId: form.showroomId,
        paymentMethod: "VNPAY",
        note: `Thanh toán trả trước ${form.prepaymentPercent}% - Ký HĐ ${generatedDraftNumber.value}`,
        orderInfo: `Thanh toan tra truoc tra gop VF - ${form.phone}`
    };

    try {
        const res = await apiClient.post('/deposits/create-payment', payload);
        
        if (res.data.code === 1000 && res.data.result && res.data.result.paymentUrl) {
            window.location.href = res.data.result.paymentUrl;
        } else {
            alert(res.data.message || "Không thể tạo giao dịch. Vui lòng thử lại.");
            isLoadingPayment.value = false;
        }
    } catch (error) {
        console.error(error);
        alert("Lỗi thanh toán. Vui lòng kiểm tra kết nối.");
        isLoadingPayment.value = false;
    }
}
</script>

<style scoped>
.hide-scrollbar::-webkit-scrollbar { display: none; }
.hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>