<template>
  <main class="py-8 md:py-12 bg-gray-50 min-h-screen">
    <div class="container mx-auto px-4 sm:px-6 max-w-6xl">
      <div class="text-center mb-8">
        <h1 class="text-2xl md:text-3xl font-extrabold text-gray-800">Dự Toán Chi Phí Trả Góp</h1>
        <p class="text-gray-500 mt-2 text-sm">Các giá trị dự toán này chỉ mang tính chất tham khảo. Vui lòng liên hệ showroom để có con số chính xác nhất.</p>
      </div>
      
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8 items-start">
        
        <div class="lg:col-span-5 flex flex-col gap-4 sticky top-20 z-10">
          
<div class="flex justify-center items-center h-56 md:h-72 w-full mb-6 mt-4">
  <img 
    class="w-full h-full object-contain transform scale-110 hover:scale-125 transition-transform duration-500 ease-out" 
    :src="carImageUrl" 
    alt="Hình ảnh xe"
  />
</div>

          <div class="bg-white p-5 md:p-6 rounded-xl shadow-sm border border-gray-100">
            <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2 border-b pb-2">
              <i class="fas fa-calculator text-blue-600"></i> Thông tin dự toán
            </h3>
            
            <div class="space-y-3 text-sm text-gray-600">
              <div class="flex justify-between items-center">
                <span>Giá xe:</span>
                <span class="font-semibold text-gray-800 text-base">{{ currencyFormatter.format(totalCarPrice) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span>Trả trước ({{ (selectedDownPaymentPercent * 100).toFixed(0) }}%):</span>
                <span class="font-semibold text-gray-800 text-base">{{ currencyFormatter.format(downPaymentAmount) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span>Số tiền vay:</span>
                <span class="font-semibold text-gray-800 text-base">{{ currencyFormatter.format(loanAmount) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span>Lãi suất tạm tính:</span>
                <span class="font-semibold text-gray-800 text-base">{{ interestRateInput ? interestRateInput.toFixed(1) : '0.0' }}%/năm</span>
              </div>
              <div class="flex justify-between items-center">
                <span>Tổng tiền lãi dự kiến:</span>
                <span class="font-semibold text-red-500 text-base">{{ currencyFormatter.format(totalInterest) }}</span>
              </div>
              
              <div class="border-t border-dashed border-gray-200 pt-3 mt-3">
                <div class="flex justify-between items-center font-bold text-lg text-gray-800">
                  <span>Tổng Vay & Lãi:</span>
                  <span class="text-blue-700">{{ currencyFormatter.format(totalLoanAndInterest) }}</span>
                </div>
              </div>
            </div>

            <div class="mt-5">
              <button id="show-schedule-btn" 
                @click="calculateAndShowModal"
                :disabled="isLoading || totalCarPrice <= 0 || !interestRateInput"
                class="w-full bg-blue-600 text-white py-2.5 rounded-lg font-bold text-sm hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed shadow-md hover:shadow-lg flex justify-center items-center gap-2">
                <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
                <i v-else class="fas fa-table"></i>
                {{ isLoading ? 'Đang tính toán...' : 'XEM LỊCH TRẢ GÓP CHI TIẾT' }}
              </button>
            </div>
          </div>
        </div>

        <div class="lg:col-span-7">
          <div class="bg-white p-6 md:p-8 rounded-xl shadow-sm border border-gray-100 space-y-5">
            
            <h3 class="text-lg font-bold text-gray-800 border-b pb-2 mb-4">Tùy chỉnh thông số</h3>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label for="model" class="block text-sm font-semibold text-gray-700 mb-1.5">Dòng xe</label>
                <select id="model" v-model="selectedModelId" class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3">
                  <option :value="null" disabled>Chọn dòng xe</option>
                  <option v-for="car in allCars" :key="car.id" :value="car.id">{{ car.name }}</option>
                </select>
              </div>

              <div>
                <label for="version" class="block text-sm font-semibold text-gray-700 mb-1.5">Phiên bản</label>
                <select id="version" v-model="selectedVersion" :disabled="allVersions.length === 0" class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3 disabled:bg-gray-100">
                  <option :value="null" disabled>{{ allVersions.length > 0 ? 'Chọn phiên bản' : 'Vui lòng chọn dòng xe' }}</option>
                  <option v-for="version in allVersions" :key="version.id" :value="version">{{ version.name }}</option>
                </select>
              </div>
            </div>

            <div class="bg-gray-50 p-4 rounded-lg border border-gray-100">
              <label class="block text-sm font-semibold text-gray-700 mb-3">Tùy chọn giá xe</label>
              <div role="radiogroup" class="space-y-3 text-sm">
                <div class="flex items-center">
                  <input id="price-type-list" name="price-type" type="radio" value="list" v-model="priceType" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 cursor-pointer">
                  <label for="price-type-list" class="ml-3 block text-gray-700 cursor-pointer">
                    Giá niêm yết: <span id="list-price-display" class="font-bold text-blue-700 ml-1">{{ listPrice > 0 ? currencyFormatter.format(listPrice) : '0 ₫' }}</span>
                  </label>
                </div>
                <div class="flex flex-col sm:flex-row sm:items-center gap-2 sm:gap-3 mt-2">
                  <div class="flex items-center">
                    <input id="price-type-custom" name="price-type" type="radio" value="custom" v-model="priceType" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 cursor-pointer">
                    <label for="price-type-custom" class="ml-3 block text-gray-700 cursor-pointer whitespace-nowrap">Giá sau ưu đãi/đàm phán:</label>
                  </div>
                  <div id="custom-price-container" v-show="priceType === 'custom'" class="flex-1">
                    <input 
                      id="custom-price-input" type="text" inputmode="numeric" 
                      v-model="customPriceInput" @input="formatCustomPriceInput"
                      placeholder="VD: 1.000.000.000" 
                      class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 py-1.5 px-3">
                  </div>
                </div>
              </div>
            </div>

            <div>
              <label for="battery" class="block text-sm font-semibold text-gray-700 mb-1.5">Lựa chọn Pin</label>
              <select id="battery" v-model="batteryChoice" class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3">
                <option value="include">Thuê pin (0đ)</option>
                <option value="exclude">Mua pin đứt (+90.000.000 ₫)</option>
              </select>
            </div>
            
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Thời gian vay (Năm)</label>
              <div class="grid grid-cols-4 sm:grid-cols-8 gap-2">
                <button v-for="year in [1, 2, 3, 4, 5, 6, 7, 8]" :key="year" type="button" @click="selectedLoanDuration = year"
                  :class="[selectedLoanDuration === year ? 'bg-blue-600 text-white border-blue-600 shadow-sm' : 'bg-white text-gray-600 hover:bg-blue-50 border-gray-200']"
                  class="py-1.5 border rounded-lg text-sm font-semibold transition-all">
                  {{ year }}
                </button>
              </div>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">Tỷ lệ trả trước</label>
              <div class="grid grid-cols-4 gap-2">
                <button v-for="p in [0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]" :key="p" type="button" @click="selectedDownPaymentPercent = p"
                  :class="[selectedDownPaymentPercent === p ? 'bg-blue-600 text-white border-blue-600 shadow-sm' : 'bg-white text-gray-600 hover:bg-blue-50 border-gray-200']"
                  class="py-1.5 border rounded-lg text-sm font-semibold transition-all">
                  {{ p * 100 }}%
                </button>
              </div>
            </div>
            
            <hr class="border-gray-100 my-4">

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label for="bank" class="block text-sm font-semibold text-gray-700 mb-1.5">Ngân hàng hỗ trợ</label>
                <select id="bank" v-model="selectedBank" class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3">
                  <option :value="null" disabled>Chọn ngân hàng</option>
                  <option v-for="bank in allBanks" :key="bank.id" :value="bank">{{ bank.name }}</option>
                </select>
              </div>

              <div v-if="selectedBank">
                <label for="policy" class="block text-sm font-semibold text-gray-700 mb-1.5">Gói ưu đãi</label>
                <select id="policy" v-model="selectedPolicy" :disabled="isLoadingPolicies" class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3">
                  <option :value="null" disabled>{{ isLoadingPolicies ? 'Đang tải...' : 'Chọn gói vay' }}</option>
                  <option v-for="policy in bankPolicies" :key="policy.id" :value="policy">
                    {{ policy.name }} ({{ policy.preferentialRatePercent }}%)
                  </option>
                </select>
              </div>
            </div>

            <div>
              <label for="interest-rate-input" class="block text-sm font-semibold text-gray-700 mb-1.5">Lãi suất (%/năm)</label>
              <div class="relative w-full sm:w-1/2">
                <input id="interest-rate-input" type="number" step="0.1" v-model.number="interestRateInput" class="block w-full text-sm rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 py-2 pl-3 pr-10 font-semibold text-blue-700 bg-blue-50/30">
                <span class="absolute right-3 top-2 text-gray-500 font-bold">%</span>
              </div>
              <p class="text-xs text-gray-400 mt-1.5"><i class="fas fa-info-circle"></i> Tự động lấy từ gói vay, có thể chỉnh sửa thủ công.</p>
            </div>

          </div>
        </div>
      </div>
    </div>
  </main> 

  <div id="schedule-modal" class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6" :class="{ 'hidden': !isModalOpen }">
    <div id="schedule-modal-overlay" @click="isModalOpen = false" class="fixed inset-0 bg-gray-900 bg-opacity-70 backdrop-blur-sm transition-opacity"></div>
    
    <div class="relative bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] flex flex-col z-10 animate-fade-in-up">
      <div class="flex justify-between items-center px-6 py-4 border-b border-gray-100 bg-gray-50 rounded-t-xl">
        <h2 class="text-lg md:text-xl font-bold text-gray-800">Bảng Kế Hoạch Trả Nợ (Dự kiến)</h2>
        <div class="flex items-center gap-3">
          <button @click="downloadCSV" id="download-csv-btn" class="bg-emerald-500 text-white px-4 py-2 text-sm rounded-lg font-bold hover:bg-emerald-600 transition-colors flex items-center gap-2 shadow-sm">
            <i class="fas fa-file-csv"></i> Xuất Excel
          </button>
          <button @click="isModalOpen = false" id="close-modal-btn" class="text-gray-400 hover:text-red-500 hover:bg-red-50 w-8 h-8 rounded-full flex items-center justify-center transition-colors">
            <i class="fas fa-times text-xl"></i>
          </button>
        </div>
      </div>
      
      <div class="overflow-y-auto flex-1 p-6 custom-scrollbar">
        <div v-if="isLoading" class="text-center py-12 text-gray-500">
          <i class="fas fa-spinner fa-spin text-4xl text-blue-500 mb-4 block"></i> Đang khởi tạo bảng tính...
        </div>
        
        <div v-else-if="scheduleData.length > 0" class="border rounded-lg overflow-hidden">
          <table class="w-full text-sm text-left repayment-table">
            <thead class="bg-gray-100 text-gray-700 font-bold uppercase text-xs">
              <tr>
                <th class="px-4 py-3 text-center border-r border-gray-200">Kỳ (Tháng)</th>
                <th class="px-4 py-3 border-r border-gray-200 text-right">Dư nợ đầu kỳ</th>
                <th class="px-4 py-3 border-r border-gray-200 text-right">Gốc trả hàng tháng</th>
                <th class="px-4 py-3 border-r border-gray-200 text-right">Lãi trả hàng tháng</th>
                <th class="px-4 py-3 border-r border-gray-200 text-right text-blue-700 bg-blue-50/50">Gốc + Lãi (Thanh toán)</th>
                <th class="px-4 py-3 text-right">Dư nợ cuối kỳ</th>
              </tr>
            </thead>
            <tbody id="schedule-body" class="divide-y divide-gray-200">
              <tr v-for="row in scheduleData" :key="row.month" class="hover:bg-gray-50 transition-colors">
                <td class="px-4 py-2.5 text-center font-medium text-gray-600 border-r border-gray-200">{{ row.month }}</td>
                <td class="px-4 py-2.5 text-right font-mono">{{ currencyFormatter.format(row.remainingBalance + row.principalPaid) }}</td>
                <td class="px-4 py-2.5 text-right font-mono">{{ currencyFormatter.format(row.principalPaid) }}</td>
                <td class="px-4 py-2.5 text-right font-mono text-red-500">{{ currencyFormatter.format(row.interestPaid) }}</td>
                <td class="px-4 py-2.5 text-right font-mono font-bold text-blue-700 bg-blue-50/30 border-r border-gray-200">{{ currencyFormatter.format(row.totalPayment) }}</td>
                <td class="px-4 py-2.5 text-right font-mono text-gray-500">{{ currencyFormatter.format(row.remainingBalance) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'
import VinfastLogo from "@/assets/image/icon_logo/VinFast-logo.svg";


// --- 1. Cấu hình API Client ---
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', 
  headers: {
    'Content-Type': 'application/json'
  }
});

// --- 2. Khai báo Trạng thái ---
const allCars = ref([]) 
const allBanks = ref([]) 
const allVersions = ref([]) 
const scheduleData = ref([]) 

const bankPolicies = ref([]) 
const selectedPolicy = ref(null) 
const isLoadingPolicies = ref(false) 

const selectedModelId = ref(null) 
const selectedVersion = ref(null) 
const priceType = ref('list') 
const customPriceInput = ref('') 
const batteryChoice = ref('exclude')
const selectedLoanDuration = ref(7) 
const selectedDownPaymentPercent = ref(0.3) 
const selectedBank = ref(null) 
const interestRateInput = ref(9.5) 

const isModalOpen = ref(false)
const carImageUrl = ref(VinfastLogo);
const isLoading = ref(false) 
const apiError = ref(null) 

const BATTERY_COST = 90000000 

// --- 3. Các hàm Computed ---
const listPrice = computed(() => {
  return selectedVersion.value ? selectedVersion.value.price : 0
})

const customPrice = computed(() => {
  return parseFormattedNumber(customPriceInput.value)
})

const totalCarPrice = computed(() => {
  let basePrice = 0
  if (priceType.value === 'custom' && customPrice.value > 0) {
    basePrice = customPrice.value
  } else {
    basePrice = listPrice.value
  }
  
  if (batteryChoice.value === 'exclude') {
    return basePrice + BATTERY_COST
  }
  return basePrice
})

const downPaymentAmount = computed(() => {
  return totalCarPrice.value * selectedDownPaymentPercent.value
})

const loanAmount = computed(() => {
  return totalCarPrice.value - downPaymentAmount.value
})

const totalInterest = ref(0);
const totalLoanAndInterest = ref(0);

// --- 4. Logic Tải dữ liệu & Watchers ---
onMounted(async () => {
  isLoading.value = true
  apiError.value = null
  
  try {
    const pResponse = await apiClient.get("/models/active"); 
    allCars.value = pResponse.data.result; 
  } catch (e) { 
    console.error(e);
  }
  
  try {
    const bResponse = await apiClient.get("/banks/active"); 
    allBanks.value = bResponse.data.result;
  } catch (e) { 
    console.error(e);
  }
  
  isLoading.value = false
})

watch(selectedModelId, async (newModelId) => {
  allVersions.value = [] 
  selectedVersion.value = null 
  if (newModelId) {
    isLoading.value = true
    try {
      const vResponse = await apiClient.get(`/variants`, {
        params: {
          modelId: newModelId,
          size: 100 
        }
      });
      const responseData = vResponse.data.result;
      allVersions.value = responseData.content || responseData || []; 
      
      if (allVersions.value.length > 0) {
        selectedVersion.value = allVersions.value[0]; 
      }
    } catch (e) {
      console.error(e);
    }
    isLoading.value = false
  }
})

watch(selectedVersion, (newVersion) => {
  if (newVersion) {
    carImageUrl.value = newVersion.firstImageUrl || 'https://via.placeholder.com/600x400?text=' + newVersion.name;
  } else {
    carImageUrl.value = 'https://via.placeholder.com/600x400?text=Chọn+xe';
  }
  triggerCalculationSummary();
})

watch(selectedBank, async (newBank) => {
    bankPolicies.value = []
    selectedPolicy.value = null
    interestRateInput.value = 7.5; 
    
    if (!selectedLoanDuration.value || selectedLoanDuration.value <= 0) {
        selectedLoanDuration.value = 7; 
    }
    
    if (newBank) {
        isLoadingPolicies.value = true
        try {
            const pResponse = await apiClient.get(`/finance/policies/by-bank/${newBank.id}/active`);
            bankPolicies.value = pResponse.data.result;
            if (bankPolicies.value.length > 0) {
                 selectedPolicy.value = bankPolicies.value[0]; 
            }
        } catch (e) { 
            console.error(e);
        }
        isLoadingPolicies.value = false
    }
});

watch(selectedPolicy, (newPolicy) => {
    if (newPolicy) { 
        if (newPolicy.preferentialRatePercent && newPolicy.preferentialRatePercent > 0) {
             interestRateInput.value = parseFloat(newPolicy.preferentialRatePercent); 
        } else {
             interestRateInput.value = 7.5; 
        }
        if (newPolicy.maxLoanTermYears) {
            selectedLoanDuration.value = Math.min(selectedLoanDuration.value, newPolicy.maxLoanTermYears);
        }
        if (newPolicy.minDownPaymentRate && selectedDownPaymentPercent.value < newPolicy.minDownPaymentRate) {
             selectedDownPaymentPercent.value = newPolicy.minDownPaymentRate;
        }
    } else {
        interestRateInput.value = 7.5; 
    }
})

watch([totalCarPrice, selectedDownPaymentPercent, selectedLoanDuration, interestRateInput], () => {
  triggerCalculationSummary();
})

// --- 5. Logic Tính toán ---
const triggerCalculationSummary = () => {
  if (loanAmount.value <= 0 || !interestRateInput.value || selectedLoanDuration.value <= 0) {
      totalInterest.value = 0;
      totalLoanAndInterest.value = totalCarPrice.value; 
      return;
  }
  
  const p = loanAmount.value;
  const r = (interestRateInput.value / 100) / 12;
  const n = selectedLoanDuration.value * 12;
  
  if (r <= 0) {
    totalInterest.value = 0;
    totalLoanAndInterest.value = p;
    return;
  }

  const monthlyPayment = (p * (r * Math.pow(1 + r, n))) / (Math.pow(1 + r, n) - 1);
  const totalPaid = monthlyPayment * n;
  
  totalInterest.value = Math.round(totalPaid - p);
  totalLoanAndInterest.value = Math.round(totalPaid);
}

const calculateAndShowModal = async () => {
    if (totalCarPrice.value <= 0 || typeof interestRateInput.value !== 'number' || interestRateInput.value <= 0 || selectedLoanDuration.value <= 0) {
        alert("Vui lòng chọn xe và nhập đầy đủ thông tin vay.");
        return;
    }
    
    isLoading.value = true;
    apiError.value = null;
    scheduleData.value = []; 

    const isPolicySelected = selectedPolicy.value !== null;
    const requestPayload = {
        carPrice: totalCarPrice.value,
        downPaymentRate: selectedDownPaymentPercent.value,
        loanTermYears: selectedLoanDuration.value,
        policyId: isPolicySelected ? selectedPolicy.value.id : null, 
        customInterestRate: !isPolicySelected ? interestRateInput.value : null 
    };

    try {
        const response = await apiClient.post('/finance/calculate', requestPayload);
        totalInterest.value = response.data.result.totalInterestPaid;
        totalLoanAndInterest.value = response.data.result.totalAmountPaid;
        scheduleData.value = response.data.result.paymentSchedule; 
        isModalOpen.value = true; 
    } catch (error) {
        apiError.value = 'Lỗi tính toán: ' + (error.response?.data?.message || error.message);
        console.error(error);
    } finally {
        isLoading.value = false;
    }
}

// --- 7. Hàm Helpers ---
function parseFormattedNumber(string) {
  if (!string) return 0
  return parseFloat(string.toString().replace(/[^0-9]/g, '')) || 0
}

function formatCustomPriceInput() {
    let rawValue = parseFormattedNumber(customPriceInput.value);
    customPriceInput.value = new Intl.NumberFormat('vi-VN').format(rawValue);
}

function downloadCSV() {
  if (scheduleData.value.length === 0) return;
  let csvContent = "data:text/csv;charset=utf-8,\uFEFF"; // Thêm BOM để Excel đọc đúng tiếng Việt
  csvContent += "Kỳ hạn,Dư nợ đầu kỳ,Gốc phải trả,Lãi phải trả,Gốc + Lãi,Dư nợ cuối kỳ\n";
  
  scheduleData.value.forEach(row => {
    const openingBalance = row.remainingBalance + row.principalPaid;
    const line = [row.month, openingBalance, row.principalPaid, row.interestPaid, row.totalPayment, row.remainingBalance].join(",");
    csvContent += line + "\n";
  });
  
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "lich_tra_no_du_kien.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

function formatCurrency(value) {
    if (value === null || isNaN(value)) return '0 ₫';
    return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', currency: 'VND', minimumFractionDigits: 0, maximumFractionDigits: 0
    }).format(value);
}
const currencyFormatter = { format: formatCurrency }; 

</script>

<style scoped>
/* Hiệu ứng trượt lên cho Modal */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fadeInUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* Tùy chỉnh thanh cuộn nội bộ Modal */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

#schedule-modal.hidden {
  display: none;
}
</style>