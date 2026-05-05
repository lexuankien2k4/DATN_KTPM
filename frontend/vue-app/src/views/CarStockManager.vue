<template>
  <div class="flex h-screen bg-gray-50 font-sans">
    
    <AdminSidebar :isOpen="isSidebarOpen" @close="isSidebarOpen = false" />

    <main class="flex-1 flex flex-col overflow-hidden relative">
      <header class="h-20 bg-white shadow-sm flex items-center justify-between px-6 z-10 border-b">
        <div class="flex items-center">
          <button @click="isSidebarOpen = true" class="md:hidden text-gray-600 mr-4 hover:text-blue-600 transition-colors">
            <i class="fas fa-bars text-xl"></i>
          </button>
          <h1 class="text-2xl font-bold text-gray-800">Điều phối & Quản lý Kho xe</h1>
        </div>
        <div class="flex items-center gap-4">
          <button v-if="showData" @click="fetchAgingInventory" class="bg-amber-50 text-amber-700 px-4 py-2 rounded-lg text-sm font-bold border border-amber-200 hover:bg-amber-100 transition-all flex items-center gap-2 shadow-sm">
            <i class="fas fa-clock"></i> Xe tồn kho lâu (>60 ngày)
          </button>
          <div class="h-8 w-px bg-gray-200 mx-2"></div>
          <div class="flex items-center gap-3">
            <div class="text-right hidden sm:block">
              <p class="text-sm font-bold text-gray-700">Admin</p>
              <small class="text-gray-500">Quản lý kho</small>
            </div>
            <img src="https://ui-avatars.com/api/?name=Admin&background=0D8ABC&color=fff" alt="Admin" class="h-10 w-10 rounded-full border border-gray-200 shadow-sm">
          </div>
        </div>
      </header>

      <div class="flex-1 overflow-x-hidden overflow-y-auto p-6 h-[calc(100vh-80px)]">
        
        <!-- MÀN HÌNH CHỜ -->
        <div v-if="!showData && !isLoading" class="h-full flex flex-col items-center justify-center animate-fade-in-up">
          <div class="w-32 h-32 bg-indigo-50 rounded-full flex items-center justify-center mb-6 shadow-inner">
            <i class="fas fa-warehouse text-6xl text-indigo-500"></i>
          </div>
          <h2 class="text-2xl font-black text-gray-800 mb-3">Quản lý Bãi & Kho xe</h2>
          <p class="text-gray-500 mb-8 text-center max-w-md">
            Hệ thống chứa dữ liệu của toàn bộ xe nhập xưởng, xe giữ chỗ và xe lỗi. Bấm nút dưới đây để bắt đầu truy xuất danh sách.
          </p>
          <button 
            @click="loadInitialData" 
            class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 px-8 rounded-full shadow-lg transition-transform hover:scale-105 flex items-center gap-2"
          >
            <i class="fas fa-download"></i> Truy xuất Dữ liệu Kho
          </button>
        </div>

        <!-- MÀN HÌNH LOADING -->
        <div v-else-if="isLoading && !showData" class="h-full flex flex-col items-center justify-center">
            <i class="fas fa-circle-notch fa-spin text-5xl text-indigo-600 mb-4"></i>
            <p class="text-gray-500 font-medium animate-pulse">Đang kết nối cơ sở dữ liệu kho...</p>
        </div>

        <!-- MÀN HÌNH DỮ LIỆU KHO CHÍNH -->
        <template v-else-if="showData">
          
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6 animate-fade-in-up">
            <div v-for="stat in inventoryStats" :key="stat.label" class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 transition-transform hover:-translate-y-1">
              <p class="text-xs font-bold text-gray-400 uppercase tracking-wider">{{ stat.label }}</p>
              <p class="text-3xl font-black text-gray-800 mt-2">{{ stat.value }} <small class="text-sm font-medium text-gray-500">xe</small></p>
            </div>
          </div>

          <div class="flex flex-col lg:flex-row justify-between items-center mb-6 gap-4 animate-fade-in-up" style="animation-delay: 0.1s;">
            <div class="flex items-center gap-2 bg-white p-1.5 rounded-xl shadow-sm border border-gray-100 overflow-x-auto w-full lg:w-auto">
              <button 
                v-for="status in statusTabs" 
                :key="status.value"
                @click="changeStatusFilter(status.value)"
                :class="currentStatusFilter === status.value ? 'bg-indigo-600 text-white shadow-md' : 'text-gray-500 hover:bg-gray-50'"
                class="px-4 py-2 rounded-lg text-xs font-bold transition-all whitespace-nowrap"
              >
                {{ status.label }}
              </button>
            </div>
            
            <div class="flex gap-3 w-full lg:w-auto">
              <div class="relative flex-1 lg:w-72">
                <input type="text" v-model="searchTerm" placeholder="Tìm VIN trong trang hiện tại..." class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 outline-none text-sm transition-all">
                <i class="fas fa-search absolute left-3.5 top-3.5 text-gray-400"></i>
              </div>
              <button @click="fetchStocks" class="bg-white hover:bg-gray-50 text-gray-600 border border-gray-200 px-4 py-2.5 rounded-lg shadow-sm transition-all" title="Làm mới trang">
                <i class="fas fa-sync-alt" :class="{ 'fa-spin': isLoading }"></i>
              </button>
              <button @click="openImportModal" class="bg-gray-800 hover:bg-black text-white px-6 py-2.5 rounded-lg shadow-md transition-all flex items-center gap-2 font-bold text-sm transform hover:-translate-y-0.5">
                <i class="fas fa-truck-loading"></i> Nhập lô xe mới
              </button>
            </div>
          </div>

          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden flex flex-col min-h-[500px] animate-fade-in-up" style="animation-delay: 0.2s;">
            <div class="overflow-x-auto flex-1">
              <table class="w-full text-left border-collapse">
                <thead class="bg-gray-50 text-gray-500 uppercase text-[10px] font-black tracking-widest border-b border-gray-100">
                  <tr>
                    <th class="p-5 w-1/4">Số Khung (VIN) / Số Máy</th>
                    <th class="p-5 w-1/4">Phiên bản / Màu sắc</th>
                    <th class="p-5 text-center w-1/6">Ngày nhập / Tuổi kho</th>
                    <!-- Đã bổ sung hiển thị Năm sản xuất -->
                    <th class="p-5 text-center w-1/6">Năm SX / Vị trí</th>
                    <th class="p-5 text-center">Trạng thái</th>
                    <th class="p-5 text-center w-40">Nghiệp vụ</th>
                  </tr>
                </thead>
                <tbody class="text-sm text-gray-700 relative">
                  
                  <div v-if="isLoading" class="absolute inset-0 bg-white/60 backdrop-blur-sm z-10 flex items-center justify-center">
                     <i class="fas fa-spinner fa-spin text-3xl text-indigo-600"></i>
                  </div>

                  <tr v-if="filteredStocks.length === 0"><td colspan="6" class="p-12 text-center text-gray-400 font-medium italic">Không tìm thấy xe nào trong danh mục này.</td></tr>
                  
                  <tr v-else v-for="car in filteredStocks" :key="car.id" class="border-b border-gray-50 hover:bg-blue-50/30 transition-colors group">
                    <td class="p-5">
                      <div class="font-mono font-bold text-gray-800 tracking-tighter text-base">{{ car.vinNumber }}</div>
                      <div class="text-[10px] text-gray-400 font-bold mt-1 uppercase">Máy: <span class="text-gray-600">{{ car.engineNumber || 'N/A' }}</span></div>
                    </td>
                    <td class="p-5">
                      <div class="font-extrabold text-gray-800">{{ car.variantName || 'Đang cập nhật' }}</div>
                      <div class="flex items-center gap-2 mt-1.5">
                          <div class="w-4 h-4 rounded-full border border-gray-200 shadow-inner" :style="{ backgroundColor: car.hexCode || '#ccc' }"></div>
                          <span class="text-xs font-bold text-gray-500">{{ car.colorName || 'N/A' }}</span>
                      </div>
                    </td>
                    <td class="p-5 text-center">
                      <div class="font-bold text-gray-600">{{ formatDate(car.importDate) }}</div>
                      <div v-if="car.status === 'IN_STOCK'" :class="car.daysInStock > 60 ? 'text-rose-500 bg-rose-50 border-rose-200' : 'text-emerald-600 bg-emerald-50 border-emerald-100'" class="inline-block text-[10px] font-black uppercase mt-1.5 px-2 py-0.5 rounded border">
                          Tồn bãi: {{ car.daysInStock || 0 }} ngày
                      </div>
                    </td>
                    <td class="p-5 text-center">
                      <!-- Hiển thị Năm sản xuất và Vị trí Bãi -->
                      <div class="font-bold text-gray-700">{{ car.manufacturingYear || 'N/A' }}</div>
                      <span class="bg-gray-100 px-3 py-1 mt-1 inline-block rounded-lg text-[10px] font-bold text-gray-600 border border-gray-200">
                        <i class="fas fa-map-marker-alt mr-1"></i>{{ car.storageLocation || 'Bãi chính' }}
                      </span>
                    </td>
                    <td class="p-5 text-center">
                      <span :class="getStatusStyle(car.status)" class="py-1.5 px-3 rounded-lg text-[10px] font-black uppercase border tracking-wider shadow-sm whitespace-nowrap">
                        {{ getStatusLabel(car.status) }}
                      </span>
                    </td>
                    <td class="p-5 text-center">
                      <div class="flex items-center justify-center gap-2">
                          <button v-if="car.status === 'RESERVED'" @click="handleAction(car.vinNumber, 'pdi')" class="bg-white border border-gray-200 text-blue-600 hover:bg-blue-600 hover:text-white p-2 rounded-lg transition-all shadow-sm group-hover:shadow" title="Chuyển sang PDI">
                              <i class="fas fa-tools"></i>
                          </button>
                          <button v-if="car.status === 'PDI_PENDING'" @click="handleAction(car.vinNumber, 'deliver')" class="bg-white border border-gray-200 text-emerald-600 hover:bg-emerald-600 hover:text-white p-2 rounded-lg transition-all shadow-sm group-hover:shadow" title="Bàn giao (Xuất kho)">
                              <i class="fas fa-key"></i>
                          </button>
                          <button v-if="['RESERVED', 'PDI_PENDING'].includes(car.status)" @click="handleAction(car.vinNumber, 'release')" class="bg-white border border-gray-200 text-amber-500 hover:bg-amber-500 hover:text-white p-2 rounded-lg transition-all shadow-sm group-hover:shadow" title="Giải phóng (Nhả cọc)">
                              <i class="fas fa-undo"></i>
                          </button>
                          <button v-if="car.status === 'IN_STOCK'" @click="handleAction(car.vinNumber, 'defective')" class="bg-white border border-gray-200 text-rose-500 hover:bg-rose-500 hover:text-white p-2 rounded-lg transition-all shadow-sm group-hover:shadow" title="Báo xe lỗi">
                              <i class="fas fa-exclamation-triangle"></i>
                          </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- THANH PHÂN TRANG -->
            <div class="bg-gray-50 px-6 py-4 border-t border-gray-100 flex items-center justify-between">
              <span class="text-sm font-medium text-gray-500">
                Trang <span class="font-bold text-indigo-600">{{ currentPage }}</span> / {{ totalPages }} 
                <span class="mx-2">|</span> Tổng: {{ totalElements }} xe
              </span>
              <div class="flex items-center gap-2">
                <button 
                  @click="changePage(currentPage - 1)" 
                  :disabled="currentPage <= 1 || isLoading"
                  class="px-3 py-1.5 rounded-lg border border-gray-200 bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed font-medium text-sm transition-colors">
                  <i class="fas fa-chevron-left mr-1"></i> Trước
                </button>
                <button 
                  @click="changePage(currentPage + 1)" 
                  :disabled="currentPage >= totalPages || isLoading"
                  class="px-3 py-1.5 rounded-lg border border-gray-200 bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed font-medium text-sm transition-colors">
                  Sau <i class="fas fa-chevron-right ml-1"></i>
                </button>
              </div>
            </div>
          </div>
        </template>

      </div>
    </main>

    <!-- Modal Nhập Kho -->
    <div v-if="showImportModal" class="fixed inset-0 z-[999] flex items-center justify-center p-4 sm:p-6 overflow-hidden">
        <div class="absolute inset-0 bg-gray-900/70 backdrop-blur-sm transition-opacity" @click="showImportModal = false"></div>
        
        <div class="bg-white rounded-2xl md:rounded-3xl shadow-2xl w-full max-w-[1400px] max-h-[90vh] flex flex-col relative z-10 animate-fade-in-up transform transition-all">
          
          <div class="px-6 py-4 md:px-8 md:py-5 border-b border-gray-100 flex justify-between items-center bg-gray-50/80 sticky top-0 z-20">
            <h2 class="text-lg md:text-xl font-black text-gray-800 flex items-center gap-3">
              <div class="w-8 h-8 md:w-10 md:h-10 bg-indigo-100 text-indigo-600 rounded-lg md:rounded-xl flex items-center justify-center shadow-sm">
                  <i class="fas fa-truck-loading text-sm md:text-base"></i>
              </div> 
              NHẬP KHO LÔ XE MỚI
            </h2>
            <button @click="showImportModal = false" class="text-gray-400 hover:text-rose-500 hover:bg-rose-50 bg-white w-8 h-8 rounded-full shadow-sm flex items-center justify-center transition-all border border-gray-100">
              <i class="fas fa-times"></i>
            </button>
          </div>

          <div class="p-4 md:p-6 overflow-y-auto flex-1 bg-white custom-scrollbar">
            <div class="mb-5 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
              <div class="bg-blue-50/50 border border-blue-100/50 px-4 py-3 rounded-xl flex items-start gap-3 w-full md:w-auto">
                  <i class="fas fa-info-circle text-blue-500 mt-0.5"></i>
                  <p class="text-xs text-blue-800/80 font-medium leading-relaxed max-w-md">
                    Điền thông tin lô xe mới. <span class="font-bold text-red-500">*</span> là bắt buộc. Hệ thống sẽ tự động kiểm tra trùng lặp Số Khung (VIN).
                  </p>
              </div>
              <button @click="addImportRow" class="bg-emerald-500 hover:bg-emerald-600 text-white px-5 py-2.5 rounded-xl shadow-md shadow-emerald-500/20 text-sm font-bold transition-all flex items-center justify-center gap-2 transform hover:-translate-y-0.5 w-full md:w-auto shrink-0">
                <i class="fas fa-plus"></i> Thêm dòng xe 
              </button>
            </div>

            <!-- Đã bổ sung trường Năm sản xuất vào bảng nhập liệu -->
            <div class="border border-gray-200 rounded-xl shadow-sm overflow-hidden bg-white">
              <div class="overflow-x-auto custom-scrollbar">
                <table class="w-full text-left text-sm whitespace-nowrap">
                  <thead class="bg-gray-50 text-gray-500 font-black uppercase text-[10px] tracking-wider border-b border-gray-200">
                    <tr>
                      <th class="p-4 w-12 text-center border-r border-gray-200">STT</th>
                      <th class="p-4 w-48 border-r border-gray-200">Số khung (VIN) <span class="text-red-500">*</span></th>
                      <th class="p-4 w-32 border-r border-gray-200">Số máy</th>
                      <th class="p-4 w-40 border-r border-gray-200">Phiên bản <span class="text-red-500">*</span></th>
                      <th class="p-4 w-32 border-r border-gray-200">Màu sắc <span class="text-red-500">*</span></th>
                      <th class="p-4 w-24 border-r border-gray-200 text-center">Năm SX</th>
                      <th class="p-4 w-32 border-r border-gray-200">Khu vực bãi</th>
                      <th class="p-4 w-16 text-center">Xóa</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-gray-100">
                    <tr v-if="importList.length === 0">
                        <td colspan="8" class="p-10 text-center text-gray-400 font-medium italic">Bấm "Thêm dòng xe" để bắt đầu nhập liệu.</td>
                    </tr>
                    <tr v-for="(item, index) in importList" :key="index" class="hover:bg-blue-50/20 transition-colors group">
                      <td class="p-3 text-center font-black text-gray-400 border-r border-gray-100">{{ index + 1 }}</td>
                      <td class="p-2 border-r border-gray-100">
                        <input v-model="item.vinNumber" type="text" placeholder="Mã VIN..." class="w-full p-2.5 border border-transparent hover:border-gray-200 focus:border-indigo-500 rounded-lg bg-gray-50 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-100 outline-none uppercase font-mono text-xs font-bold transition-all placeholder:text-gray-300" required>
                      </td>
                      <td class="p-2 border-r border-gray-100">
                        <input v-model="item.engineNumber" type="text" placeholder="Số máy..." class="w-full p-2.5 border border-transparent hover:border-gray-200 focus:border-indigo-500 rounded-lg bg-gray-50 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-100 outline-none uppercase font-mono text-xs transition-all placeholder:text-gray-300">
                      </td>
                      <td class="p-2 border-r border-gray-100">
                        <select v-model="item.variantId" class="w-full p-2.5 border border-transparent hover:border-gray-200 focus:border-indigo-500 rounded-lg bg-gray-50 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-100 outline-none font-bold text-gray-700 cursor-pointer transition-all appearance-none" required>
                          <option :value="null" disabled>-- Chọn dòng xe --</option>
                          <option v-for="v in availableVariants" :key="v.id" :value="v.id">{{ v.name }}</option>
                        </select>
                      </td>
                      <td class="p-2 border-r border-gray-100">
                        <select v-model="item.colorId" class="w-full p-2.5 border border-transparent hover:border-gray-200 focus:border-indigo-500 rounded-lg bg-gray-50 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-100 outline-none font-bold text-gray-700 cursor-pointer transition-all appearance-none" required>
                          <option :value="null" disabled>-- Chọn màu --</option>
                          <option v-for="c in availableColors" :key="c.id" :value="c.id">{{ c.colorName }}</option>
                        </select>
                      </td>
                      <!-- Input cho Năm sản xuất -->
                      <td class="p-2 border-r border-gray-100">
                        <input v-model.number="item.manufacturingYear" type="number" min="2020" max="2030" placeholder="YYYY" class="w-full p-2.5 border border-transparent hover:border-gray-200 focus:border-indigo-500 rounded-lg bg-gray-50 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-100 outline-none text-xs font-bold text-center text-gray-600 transition-all placeholder:text-gray-300">
                      </td>
                      <td class="p-2 border-r border-gray-100">
                        <input v-model="item.storageLocation" type="text" placeholder="VD: Khu A" class="w-full p-2.5 border border-transparent hover:border-gray-200 focus:border-indigo-500 rounded-lg bg-gray-50 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-100 outline-none text-xs font-bold text-gray-600 transition-all placeholder:text-gray-300">
                      </td>
                      <td class="p-2 text-center">
                        <button @click="removeImportRow(index)" class="text-gray-300 hover:text-red-500 hover:bg-red-50 w-8 h-8 rounded-lg transition-all opacity-0 group-hover:opacity-100 focus:opacity-100" title="Xóa dòng này">
                          <i class="fas fa-trash-alt"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div class="px-6 py-4 md:px-8 md:py-5 bg-gray-50/80 border-t border-gray-100 flex flex-col sm:flex-row justify-between items-center gap-4 sticky bottom-0 z-20 backdrop-blur-sm">
            <div class="text-sm font-bold text-gray-500 flex items-center">
              Tổng xe sắp lưu kho: <span class="text-indigo-600 text-lg mx-2 bg-indigo-100/50 px-3 py-1 rounded-lg border border-indigo-100">{{ importList.length }}</span>
            </div>
            <div class="flex gap-3 w-full sm:w-auto">
              <button @click="showImportModal = false" class="flex-1 sm:flex-none px-6 py-2.5 rounded-xl border border-gray-200 font-bold text-gray-600 bg-white hover:bg-gray-50 hover:text-gray-800 transition-all shadow-sm">
                Đóng
              </button>
              <button @click="submitImport" :disabled="isSubmitting || importList.length === 0" class="flex-1 sm:flex-none px-8 py-2.5 rounded-xl bg-indigo-600 text-white font-black hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 disabled:bg-gray-300 disabled:text-gray-500 disabled:shadow-none shadow-md shadow-indigo-600/20 transform hover:-translate-y-0.5">
                <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
                <i v-else class="fas fa-save"></i> 
                XÁC NHẬN LƯU KHO 
              </button>
            </div>
          </div>

        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import api from '@/utils/axios'
import AdminSidebar from '@/components/AdminSidebar.vue'

const isSidebarOpen = ref(false)
const isLoading = ref(false)
const showData = ref(false)
const searchTerm = ref('')
const stocks = ref([])
const currentStatusFilter = ref('ALL')

const currentPage = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const pageSize = 15 

const showImportModal = ref(false)
const isSubmitting = ref(false)
const importList = ref([]) 
const availableVariants = ref([]) 
const availableColors = ref([])   

const statusTabs = [
  { label: 'TẤT CẢ', value: 'ALL' },
  { label: 'TRONG KHO', value: 'IN_STOCK' },
  { label: 'ĐÃ GIỮ CHỖ', value: 'RESERVED' },
  { label: 'CHỜ BÀN GIAO (PDI)', value: 'PDI_PENDING' },
  { label: 'ĐÃ XUẤT KHO', value: 'DELIVERED' },
  { label: 'XE LỖI', value: 'DEFECTIVE' }
]

const statusConfig = {
  IN_STOCK: { label: 'Sẵn sàng', style: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
  RESERVED: { label: 'Đã giữ chỗ', style: 'bg-indigo-50 text-indigo-700 border-indigo-200' },
  PDI_PENDING: { label: 'Đang PDI', style: 'bg-amber-50 text-amber-700 border-amber-200' },
  DELIVERED: { label: 'Đã xuất kho', style: 'bg-gray-100 text-gray-500 border-gray-200' },
  DEFECTIVE: { label: 'Lỗi kĩ thuật', style: 'bg-rose-50 text-rose-700 border-rose-200' }
}

const filteredStocks = computed(() => {
  let result = stocks.value
  if (searchTerm.value) {
    const term = searchTerm.value.toUpperCase()
    result = result.filter(s => s.vinNumber.includes(term) || (s.engineNumber && s.engineNumber.includes(term)))
  }
  return result
})

const inventoryStats = computed(() => [
  { label: 'Số xe trang này (Sẵn sàng)', value: stocks.value.filter(s => s.status === 'IN_STOCK').length },
  { label: 'Đang giữ chỗ', value: stocks.value.filter(s => s.status === 'RESERVED').length },
  { label: 'Chờ giao xe (Đang PDI)', value: stocks.value.filter(s => s.status === 'PDI_PENDING').length },
  { label: 'Tổng đã xuất kho', value: stocks.value.filter(s => s.status === 'DELIVERED').length }
])

const loadInitialData = async () => {
  showData.value = true
  await fetchStocks()
}

const fetchStocks = async () => {
  isLoading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize,
      status: currentStatusFilter.value === 'ALL' ? null : currentStatusFilter.value
    }
    
    const res = await api.get('/api/stocks', { params })
    
    if (res.data.result && res.data.result.content) {
        stocks.value = res.data.result.content
        totalPages.value = res.data.result.totalPages || 1
        totalElements.value = res.data.result.totalElements || 0
    } else {
        stocks.value = res.data.result || []
        totalPages.value = 1
        totalElements.value = stocks.value.length
    }
  } catch (error) {
    console.error("Lỗi lấy danh sách kho:", error)
  } finally { 
    isLoading.value = false 
  }
}

const changeStatusFilter = (status) => {
  currentStatusFilter.value = status
  currentPage.value = 1 
  fetchStocks()
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchStocks()
  }
}

const fetchAgingInventory = async () => {
    isLoading.value = true
    try {
        const res = await api.get('/api/stocks/aging', { params: { thresholdDays: 60 } })
        
        if (res.data.result && res.data.result.content) {
             stocks.value = res.data.result.content;
        } else {
             stocks.value = res.data.result || [];
        }
        
        totalPages.value = 1
        currentPage.value = 1
        totalElements.value = stocks.value.length
        
        alert("Đã tải danh sách xe nằm trong kho quá 60 ngày.")
    } catch (error) { 
        alert("Lỗi tải báo cáo") 
    } finally { 
        isLoading.value = false 
    }
}

const handleAction = async (vin, action) => {
    const confirmMsg = {
        pdi: `Xác nhận bắt đầu quy trình kiểm tra PDI cho xe [${vin}]?`,
        deliver: `Xác nhận xe [${vin}] đã được bàn giao cho khách và xuất kho?`,
        release: `Khách hàng hủy hợp đồng? Xác nhận giải phóng xe [${vin}] về lại kho?`,
        defective: `Xác nhận khóa xe [${vin}] do phát hiện lỗi kỹ thuật?`
    }
    if (!confirm(confirmMsg[action])) return

    try {
        await api.patch(`/api/stocks/${vin}/${action}`)
        alert("Cập nhật trạng thái thành công!")
        fetchStocks() 
    } catch (error) {
        alert("Lỗi cập nhật: " + (error.response?.data?.message || error.message))
    }
}

const openImportModal = async () => {
    showImportModal.value = true;
    try {
        if (availableVariants.value.length === 0) {
            const resV = await api.get('/api/variants'); 
            availableVariants.value = resV.data.result.content || resV.data.result || []; 
        }
        if (availableColors.value.length === 0) {
            const resC = await api.get('/api/colors'); 
            availableColors.value = resC.data.result || [];
        }
    } catch (error) {
        console.error("Lỗi tải danh mục:", error);
    }
    importList.value = [{
        vinNumber: '',
        engineNumber: '',
        variantId: null,
        colorId: null,
        manufacturingYear: new Date().getFullYear(), // Mặc định là năm hiện tại
        storageLocation: 'Bãi chính'
    }];
}

const addImportRow = () => {
    const lastRow = importList.value.length > 0 ? importList.value[importList.value.length - 1] : null;
    importList.value.push({
        vinNumber: '',
        engineNumber: '',
        variantId: lastRow ? lastRow.variantId : null, 
        colorId: lastRow ? lastRow.colorId : null,
        manufacturingYear: lastRow ? lastRow.manufacturingYear : new Date().getFullYear(),
        storageLocation: lastRow ? lastRow.storageLocation : 'Bãi chính'
    });
}

const removeImportRow = (index) => {
    importList.value.splice(index, 1);
}

const submitImport = async () => {
    if (importList.value.length === 0) return;
    const hasError = importList.value.some(item => !item.vinNumber.trim() || !item.variantId || !item.colorId);
    if (hasError) {
        alert("Vui lòng điền đủ Mã VIN, Phiên bản và Màu sắc cho TẤT CẢ các xe trong bảng!");
        return;
    }
    const vins = importList.value.map(item => item.vinNumber.trim().toUpperCase());
    const uniqueVins = new Set(vins);
    if (uniqueVins.size < vins.length) {
        alert("Có mã số khung (VIN) bị nhập trùng lặp trong bảng. Vui lòng kiểm tra lại!");
        return;
    }
    isSubmitting.value = true;
    try {
        const payload = importList.value.map(item => ({
            ...item,
            vinNumber: item.vinNumber.trim().toUpperCase(),
            engineNumber: item.engineNumber ? item.engineNumber.trim().toUpperCase() : null
        }));
        // API Batch Import (Khớp với Controller)
        await api.post('/api/stocks/batch-import', payload);
        alert(`Đã nhập kho thành công lô hàng gồm ${payload.length} xe!`);
        showImportModal.value = false;
        fetchStocks(); 
    } catch (error) {
        alert("Lỗi nhập kho: " + (error.response?.data?.message || "Dữ liệu không hợp lệ hoặc VIN đã tồn tại trong hệ thống."));
    } finally { 
        isSubmitting.value = false; 
    }
}

const getStatusLabel = (status) => statusConfig[status]?.label || status
const getStatusStyle = (status) => statusConfig[status]?.style || 'bg-gray-100 text-gray-500'
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(date)
}
</script>

<style scoped>
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fadeInUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
.custom-scrollbar::-webkit-scrollbar { width: 8px; height: 8px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>