<template>
  <div class="flex h-screen bg-gray-50 font-sans">
    <AdminSidebar :isOpen="isSidebarOpen" @close="isSidebarOpen = false" />

    <main class="flex-1 overflow-y-auto custom-scrollbar">
      <header class="h-20 bg-white shadow-sm flex items-center px-6 border-b border-gray-100 sticky top-0 z-10">
        <button @click="isSidebarOpen = true" class="md:hidden text-gray-600 mr-4">
          <i class="fas fa-bars text-xl"></i>
        </button>
        <h1 class="text-2xl font-black text-gray-800">Tổng quan Hệ thống (Dashboard)</h1>
      </header>

      <div class="p-6 h-[calc(100vh-80px)]">
        
        <!-- MÀN HÌNH CHỜ: Khi chưa bấm nút -->
        <div v-if="!showStats && !isLoading" class="h-full flex flex-col items-center justify-center animate-fade-in-up">
          <div class="w-32 h-32 bg-blue-50 rounded-full flex items-center justify-center mb-6 shadow-inner">
            <i class="fas fa-chart-line text-6xl text-blue-500"></i>
          </div>
          <h2 class="text-2xl font-black text-gray-800 mb-3">Báo cáo Thống kê Sẵn sàng</h2>
          <p class="text-gray-500 mb-8 text-center max-w-md">
            Hệ thống sẽ tổng hợp doanh thu, tình trạng kho xe và hành vi khách hàng. Quá trình này có thể mất vài giây.
          </p>
          <button 
            @click="loadStatistics" 
            class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-full shadow-lg transition-transform hover:scale-105 flex items-center gap-2"
          >
            <i class="fas fa-sync-alt"></i> Bắt đầu truy xuất dữ liệu
          </button>
        </div>

        <!-- MÀN HÌNH LOADING: Khi đang tải dữ liệu -->
        <div v-else-if="isLoading" class="h-full flex flex-col items-center justify-center">
            <i class="fas fa-circle-notch fa-spin text-5xl text-blue-600 mb-4"></i>
            <p class="text-gray-500 font-medium animate-pulse">Đang tính toán dữ liệu, vui lòng đợi...</p>
        </div>

        <!-- MÀN HÌNH KẾT QUẢ: Khi dữ liệu đã tải xong -->
        <template v-else-if="showStats">
          
          <div class="flex justify-end mb-6 animate-fade-in-up">
             <button @click="loadStatistics" class="text-sm font-bold text-gray-500 hover:text-blue-600 flex items-center gap-2 transition-colors">
               <i class="fas fa-redo"></i> Làm mới dữ liệu
             </button>
          </div>

          <!-- 4 THẺ CHỈ SỐ LỚN -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8 animate-fade-in-up">
            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center justify-between transition-transform hover:-translate-y-1">
              <div>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Doanh thu dự kiến</p>
                <p class="text-2xl font-black text-emerald-600 mt-1">{{ formatCurrency(summaryData?.totalExpectedRevenue) }}</p>
              </div>
              <div class="w-12 h-12 bg-emerald-50 rounded-xl flex items-center justify-center text-emerald-500 shadow-sm">
                <i class="fas fa-money-bill-wave text-xl"></i>
              </div>
            </div>

            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center justify-between transition-transform hover:-translate-y-1">
              <div>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Tổng Hợp đồng</p>
                <p class="text-3xl font-black text-indigo-600 mt-1">{{ summaryData?.totalContracts }}</p>
              </div>
              <div class="w-12 h-12 bg-indigo-50 rounded-xl flex items-center justify-center text-indigo-500 shadow-sm">
                <i class="fas fa-file-signature text-xl"></i>
              </div>
            </div>

            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center justify-between transition-transform hover:-translate-y-1">
              <div>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Xe đang giữ chỗ</p>
                <p class="text-3xl font-black text-blue-600 mt-1">{{ summaryData?.totalReserved }}</p>
              </div>
              <div class="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center text-blue-500 shadow-sm">
                <i class="fas fa-lock text-xl"></i>
              </div>
            </div>

            <div class="bg-white p-6 rounded-2xl shadow-sm border border-red-100 flex items-center justify-between relative overflow-hidden transition-transform hover:-translate-y-1">
              <div class="absolute right-0 top-0 w-1.5 h-full bg-red-500"></div>
              <div>
                <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">Xe tồn kho (>60 ngày)</p>
                <p class="text-3xl font-black text-red-600 mt-1">{{ summaryData?.agingVehicles }}</p>
              </div>
              <div class="w-12 h-12 bg-red-50 rounded-xl flex items-center justify-center text-red-500 shadow-sm">
                <i class="fas fa-exclamation-triangle text-xl"></i>
              </div>
            </div>
          </div>

          <!-- BIỂU ĐỒ DOANH THU & TOP XE -->
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8 animate-fade-in-up" style="animation-delay: 0.1s;">
            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 lg:col-span-2">
              <h3 class="text-base font-black text-gray-800 mb-6 uppercase tracking-wide flex items-center gap-2">
                <i class="fas fa-chart-bar text-blue-500"></i> Doanh thu năm nay
              </h3>
              <div class="h-72">
                 <BarChart v-if="monthlyRevenueChartData" :chartData="monthlyRevenueChartData" :options="barChartOptions" />
              </div>
            </div>

            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
              <h3 class="text-base font-black text-gray-800 mb-6 uppercase tracking-wide flex items-center gap-2">
                <i class="fas fa-trophy text-amber-500"></i> Top 5 Bán Chạy
              </h3>
              <ul class="space-y-4">
                <li v-for="(count, carName, index) in summaryData?.topSellingCars" :key="carName" 
                    class="flex items-center justify-between p-3 rounded-xl bg-gray-50 border border-gray-100 hover:bg-blue-50 transition-colors">
                  <div class="flex items-center gap-3">
                    <span :class="index === 0 ? 'bg-amber-100 text-amber-600' : index === 1 ? 'bg-gray-200 text-gray-600' : index === 2 ? 'bg-orange-100 text-orange-600' : 'bg-blue-50 text-blue-500'" 
                          class="w-8 h-8 rounded-full flex items-center justify-center font-black text-sm">
                      {{ index + 1 }}
                    </span>
                    <span class="font-bold text-gray-700">{{ carName }}</span>
                  </div>
                  <span class="font-black text-indigo-600 bg-indigo-100 px-3 py-1 rounded-lg text-sm">{{ count }} xe</span>
                </li>
                <li v-if="!summaryData?.topSellingCars || Object.keys(summaryData.topSellingCars).length === 0" class="text-center text-gray-400 py-4 text-sm italic">
                  Chưa có dữ liệu giao dịch.
                </li>
              </ul>
            </div>
          </div>

          <!-- BIỂU ĐỒ TRÒN -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 animate-fade-in-up" style="animation-delay: 0.2s;">
            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
              <h3 class="text-sm font-black text-gray-800 mb-4 text-center uppercase tracking-wide">Tình trạng Kho xe</h3>
              <div class="h-56"><PieChart v-if="inventoryChartData" :chartData="inventoryChartData" :options="pieOptions" /></div>
            </div>

            <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
              <h3 class="text-sm font-black text-gray-800 mb-4 text-center uppercase tracking-wide">Dịch vụ Pin</h3>
              <div class="h-56"><DoughnutChart v-if="batteryChartData" :chartData="batteryChartData" :options="pieOptions" /></div>
            </div>
          </div>

        </template>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue' // ĐÃ BỎ onMounted
import api from '@/utils/axios'
import AdminSidebar from '@/components/AdminSidebar.vue'
import { PieChart, DoughnutChart, BarChart } from 'vue-chart-3'
import { Chart, registerables } from "chart.js"

Chart.register(...registerables)

const isSidebarOpen = ref(false)
const isLoading = ref(false) // Mặc định ban đầu là false
const showStats = ref(false) // Biến mới: Điều khiển việc hiển thị kết quả
const summaryData = ref(null)

const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)

// --- HÀM TẢI DỮ LIỆU KHI BẤM NÚT ---
const loadStatistics = async () => {
  isLoading.value = true;
  showStats.value = false;
  
  try {
    const res = await api.get('/api/dashboard/summary')
    summaryData.value = res.data.result
    showStats.value = true; // Chỉ hiện khi tải thành công
  } catch (error) {
    console.error("Lỗi lấy dữ liệu Dashboard", error)
    alert("Có lỗi xảy ra khi truy xuất dữ liệu thống kê!");
  } finally {
    isLoading.value = false;
  }
}

// ... CÁC PHẦN COMPUTED CHART DATA GIỮ NGUYÊN ...
const monthlyRevenueChartData = computed(() => {
  if (!summaryData.value?.monthlyRevenue) return null
  const months = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12']
  const data = Array.from({ length: 12 }, (_, i) => summaryData.value.monthlyRevenue[i + 1] || 0)

  return {
    labels: months,
    datasets: [{
      label: 'Doanh thu (VNĐ)',
      data: data,
      backgroundColor: '#3b82f6', 
      borderRadius: 6,
    }]
  }
})

const inventoryChartData = computed(() => {
  if (!summaryData.value?.inventoryStatusChart) return null
  return {
    labels: Object.keys(summaryData.value.inventoryStatusChart),
    datasets: [{
      data: Object.values(summaryData.value.inventoryStatusChart),
      backgroundColor: ['#10b981', '#3b82f6', '#f59e0b', '#6b7280', '#ef4444'],
      borderWidth: 0
    }]
  }
})

const batteryChartData = computed(() => {
  if (!summaryData.value?.batteryServiceChart) return null
  return {
    labels: Object.keys(summaryData.value.batteryServiceChart),
    datasets: [{
      data: Object.values(summaryData.value.batteryServiceChart),
      backgroundColor: ['#14b8a6', '#6366f1'], 
      borderWidth: 0
    }]
  }
})

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'bottom', labels: { font: { family: 'Inter, sans-serif', size: 12 } } } }
}

const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { 
    legend: { display: false },
    tooltip: { 
      callbacks: {
        label: function(context) { return formatCurrency(context.raw); }
      }
    }
  },
  scales: {
    y: { beginAtZero: true, grid: { color: '#f3f4f6' }, ticks: { callback: (val) => (val / 1000000000) + ' Tỷ' } },
    x: { grid: { display: false } }
  }
}
</script>

<style scoped>
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fadeInUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
</style>