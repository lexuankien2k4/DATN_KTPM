<template>
  <div class="flex h-screen bg-[#F0F2F7] font-sans overflow-hidden">

    <AdminSidebar :isOpen="isSidebarOpen" @close="isSidebarOpen = false" />

    <main class="flex-1 flex flex-col overflow-hidden">

      <!-- ====== TOPBAR ====== -->
      <header class="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6 shrink-0 z-20">
        <div class="flex items-center gap-3">
          <button @click="isSidebarOpen = true" class="md:hidden text-gray-500 hover:text-blue-600 transition-colors p-1">
            <i class="fas fa-bars text-lg"></i>
          </button>
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center">
              <i class="fas fa-file-contract text-white text-sm"></i>
            </div>
            <div>
              <h1 class="text-base font-bold text-gray-900 leading-none">Quản lý Hợp đồng</h1>
              <p class="text-[11px] text-gray-400 leading-none mt-0.5">Contract Management System</p>
            </div>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <!-- Live clock -->
          <div class="hidden sm:flex flex-col items-end">
            <span class="text-sm font-bold text-gray-700 tabular-nums">{{ currentTime }}</span>
            <span class="text-[10px] text-gray-400">{{ currentDate }}</span>
          </div>
          <div class="h-8 w-px bg-gray-200"></div>
          <div class="flex items-center gap-2.5">
            <img src="https://ui-avatars.com/api/?name=Admin&background=1d4ed8&color=fff&size=80" alt="Admin" class="h-8 w-8 rounded-full">
            <div class="hidden sm:block">
              <p class="text-xs font-bold text-gray-800">Admin</p>
              <p class="text-[10px] text-gray-400">Quản lý bán hàng</p>
            </div>
          </div>
        </div>
      </header>

      <!-- ====== MAIN CONTENT ====== -->
      <div class="flex-1 overflow-y-auto p-4 sm:p-5 flex flex-col gap-4">

        <!-- KPI SUMMARY CARDS -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <div v-for="kpi in kpiCards" :key="kpi.label"
               class="bg-white rounded-xl border border-gray-100 p-3 sm:p-4 flex items-center gap-3 shadow-sm hover:shadow-md transition-shadow">
            <div :class="kpi.iconBg" class="w-10 h-10 rounded-lg flex items-center justify-center shrink-0">
              <i :class="kpi.icon + ' ' + kpi.iconColor + ' text-lg'"></i>
            </div>
            <div class="min-w-0">
              <p class="text-[10px] sm:text-[11px] font-semibold text-gray-400 uppercase tracking-wider truncate">{{ kpi.label }}</p>
              <p class="text-lg sm:text-xl font-black text-gray-800 leading-tight">{{ kpi.value }}</p>
            </div>
          </div>
        </div>

        <!-- LOAD TRIGGER -->
        <div v-if="!tableVisible"
             class="bg-white rounded-xl border border-gray-100 shadow-sm flex flex-col items-center justify-center py-16 gap-5">
          <div class="w-16 h-16 rounded-2xl bg-blue-50 flex items-center justify-center">
            <i class="fas fa-table text-3xl text-blue-400"></i>
          </div>
          <div class="text-center px-4">
            <p class="font-bold text-gray-700">Danh sách hợp đồng chưa được tải</p>
            <p class="text-xs text-gray-400 mt-1">Nhấn nút bên dưới để tải và hiển thị danh sách</p>
          </div>
          <button @click="loadTable"
                  class="bg-blue-600 hover:bg-blue-700 active:scale-95 text-white font-bold px-6 py-2.5 rounded-xl text-sm transition-all flex items-center gap-2 shadow-md shadow-blue-200">
            <i class="fas fa-download"></i> Tải danh sách hợp đồng
          </button>
        </div>

        <!-- FILTER BAR + SEARCH -->
        <template v-if="tableVisible">
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm px-4 py-3 flex flex-col xl:flex-row items-start xl:items-center gap-3">
          <!-- Status tabs -->
          <div class="flex flex-wrap gap-1.5 flex-1 w-full overflow-x-auto pb-1 custom-x-scrollbar">
            <button
              v-for="tab in statusTabs" :key="tab.value"
              @click="changeStatusFilter(tab.value)"
              :class="statusFilter === tab.value ? tab.activeClass : 'bg-gray-50 text-gray-500 border-gray-200 hover:bg-gray-100'"
              class="px-3 py-1.5 rounded-lg text-xs font-bold border transition-all flex items-center gap-1.5 whitespace-nowrap shrink-0">
              <span :class="statusFilter === tab.value ? 'opacity-100' : 'opacity-60'" class="inline-block w-1.5 h-1.5 rounded-full shrink-0" :style="{ backgroundColor: tab.dot }"></span>
              {{ tab.label }}
              <span v-if="tab.value !== '' && statusFilter === tab.value && totalElements > 0"
                    class="ml-0.5 bg-white/30 text-current text-[10px] font-black px-1.5 py-0.5 rounded-full">
                {{ totalElements }}
              </span>
            </button>
          </div>

          <!-- Search + reload -->
          <div class="flex flex-wrap items-center gap-2 w-full xl:w-auto mt-1 xl:mt-0">
            <div class="relative flex-1 min-w-[200px]">
              <i class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm"></i>
              <input
                type="text"
                v-model="searchInput"
                @keyup.enter="handleSearch"
                placeholder="Tìm KH, mã HĐ..."
                class="w-full pl-9 pr-4 py-2 text-sm border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-gray-50 transition-all">
            </div>
            <button @click="handleSearch"
                    class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-bold transition-colors shrink-0 flex items-center gap-1.5">
              <i class="fas fa-search text-xs"></i> <span class="hidden sm:inline">Tìm</span>
            </button>
            <div class="flex gap-2 shrink-0">
              <button @click="clearFilters" title="Xóa bộ lọc"
                      class="border border-gray-200 text-gray-500 hover:bg-gray-50 px-3 py-2 rounded-lg text-sm transition-colors">
                <i class="fas fa-times"></i>
              </button>
              <button @click="fetchContracts" title="Làm mới danh sách"
                      class="border border-gray-200 text-gray-500 hover:bg-blue-50 hover:text-blue-600 hover:border-blue-200 px-3 py-2 rounded-lg text-sm transition-colors">
                <i class="fas fa-sync-alt" :class="{ 'fa-spin': isLoading }"></i>
              </button>
              <button @click="hideTable" title="Ẩn danh sách"
                      class="border border-gray-200 text-gray-400 hover:bg-red-50 hover:text-red-400 hover:border-red-200 px-3 py-2 rounded-lg text-sm transition-colors hidden sm:block">
                <i class="fas fa-eye-slash"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- CONTRACT TABLE -->
        <div class="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden flex flex-col flex-1 min-h-[400px]">
          <div class="overflow-x-auto custom-x-scrollbar flex-1">
            <table class="w-full text-left min-w-[950px] border-collapse">
              <thead>
                <tr class="border-b border-gray-100 bg-gray-50">
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider sticky left-0 z-10 bg-gray-50 border-r border-gray-100/50">Hợp đồng</th>
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider">Khách hàng</th>
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider">Tài chính</th>
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider text-center">Pin</th>
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider text-center">Lịch trình</th>
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider text-center">Trạng thái</th>
                  <th class="px-4 py-3 text-[11px] font-extrabold text-gray-400 uppercase tracking-wider text-center w-28 sticky right-0 z-10 bg-gray-50 border-l border-gray-100/50">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="isLoading">
                  <td colspan="7" class="py-20 text-center">
                    <div class="flex flex-col items-center gap-3 text-gray-400">
                      <div class="w-8 h-8 border-2 border-blue-200 border-t-blue-600 rounded-full animate-spin"></div>
                      <span class="text-sm font-medium">Đang tải dữ liệu...</span>
                    </div>
                  </td>
                </tr>

                <tr v-else-if="contracts.length === 0">
                  <td colspan="7" class="py-20 text-center">
                    <div class="flex flex-col items-center gap-3 text-gray-400">
                      <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center">
                        <i class="fas fa-folder-open text-3xl text-gray-300"></i>
                      </div>
                      <div>
                        <p class="font-semibold text-gray-500">Không có hợp đồng nào</p>
                        <p class="text-xs text-gray-400 mt-1">Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
                      </div>
                    </div>
                  </td>
                </tr>

                <tr v-else
                    v-for="contract in contracts" :key="contract.id"
                    class="border-b border-gray-50 hover:bg-blue-50/30 transition-colors group">

                  <td class="px-4 py-3.5 sticky left-0 z-10 bg-white group-hover:bg-blue-50/90 border-r border-gray-100/50 transition-colors">
                    <div class="flex items-center gap-2.5">
                      <div class="w-8 h-8 rounded-lg bg-gray-100 group-hover:bg-blue-100 flex items-center justify-center transition-colors shrink-0">
                        <i class="fas fa-file-alt text-gray-400 group-hover:text-blue-500 text-xs transition-colors"></i>
                      </div>
                      <div>
                        <span class="font-mono font-black text-gray-800 text-sm tracking-tight block">{{ contract.contractNumber }}</span>
                        <span class="text-[10px] text-gray-400 font-medium whitespace-nowrap">{{ formatDate(contract.createdAt) }}</span>
                      </div>
                    </div>
                  </td>

                  <td class="px-4 py-3.5">
                    <div class="flex items-center gap-2.5">
                      <div class="w-7 h-7 rounded-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center shrink-0">
                        <span class="text-white text-[10px] font-black">{{ getInitials(contract.customerName) }}</span>
                      </div>
                      <div>
                        <span class="font-semibold text-gray-800 text-sm block truncate max-w-[150px]" :title="contract.customerName">{{ contract.customerName || '—' }}</span>
                        <span class="text-[10px] text-gray-400 font-mono">{{ contract.customerPhone || '' }}</span>
                      </div>
                    </div>
                  </td>

                  <td class="px-4 py-3.5">
                    <div class="font-bold text-gray-800 text-sm">{{ formatPrice(contract.finalPrice) }}</div>
                    <div class="flex items-center gap-1.5 mt-1">
                      <span class="text-[10px] text-gray-400">Cọc:</span>
                      <span class="text-[10px] font-bold text-emerald-600">{{ formatPrice(contract.prepaymentAmount) }}</span>
                    </div>
                    <div v-if="contract.loanTermMonths > 0" class="flex items-center gap-1 mt-0.5 whitespace-nowrap">
                      <i class="fas fa-university text-[9px] text-amber-500"></i>
                      <span class="text-[10px] text-amber-600 font-bold">{{ contract.loanTermMonths }}T · {{ contract.interestRate }}%/năm</span>
                    </div>
                    <div v-else class="mt-0.5">
                      <span class="text-[10px] text-gray-400 font-medium bg-gray-100 px-1.5 py-0.5 rounded">Thanh toán thẳng</span>
                    </div>
                  </td>

                  <td class="px-4 py-3.5 text-center">
                    <span :class="contract.batteryServiceType === 'PURCHASE'
                      ? 'bg-violet-100 text-violet-700 border-violet-200'
                      : 'bg-sky-100 text-sky-600 border-sky-200'"
                      class="inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-[10px] font-black border uppercase">
                      <i :class="contract.batteryServiceType === 'PURCHASE' ? 'fas fa-bolt' : 'fas fa-sync-alt'" class="text-[8px]"></i>
                      {{ contract.batteryServiceType === 'PURCHASE' ? 'Mua' : 'Thuê' }}
                    </span>
                    <div v-if="contract.loanTermMonths > 0" class="text-[10px] text-red-500 font-bold mt-1.5 whitespace-nowrap">
                      {{ formatPriceShort(contract.monthlyInstallment) }}/th
                    </div>
                  </td>

                  <td class="px-4 py-3.5 text-center">
                    <div v-if="contract.signingAppointmentDate || contract.expectedDeliveryDate" class="space-y-1">
                      <div v-if="contract.signingAppointmentDate" class="flex items-center justify-center gap-1 text-[10px] text-gray-500 whitespace-nowrap">
                        <i class="fas fa-pen text-gray-400 w-3"></i>
                        <span class="font-mono font-bold">{{ formatDate(contract.signingAppointmentDate) }}</span>
                      </div>
                      <div v-if="contract.expectedDeliveryDate" class="flex items-center justify-center gap-1 text-[10px] text-blue-500 whitespace-nowrap">
                        <i class="fas fa-car w-3"></i>
                        <span class="font-mono font-bold">{{ formatDate(contract.expectedDeliveryDate) }}</span>
                      </div>
                    </div>
                    <span v-else class="text-[10px] text-gray-300 italic font-medium">Chưa hẹn</span>
                  </td>

                  <td class="px-4 py-3.5 text-center">
                    <div class="flex flex-col items-center gap-1">
                      <span :class="getStatusStyle(contract.status)"
                            class="inline-flex items-center gap-1 py-1 px-2.5 rounded-full text-[10px] font-black border whitespace-nowrap">
                        <span class="w-1.5 h-1.5 rounded-full inline-block" :class="getStatusDot(contract.status)"></span>
                        {{ getStatusLabel(contract.status) }}
                      </span>
                      <span v-if="contract.isSigned" class="text-[9px] text-emerald-500 font-bold flex items-center gap-0.5 whitespace-nowrap mt-0.5">
                        <i class="fas fa-check-circle"></i> Đã ký số
                      </span>
                    </div>
                  </td>

                  <td class="px-4 py-3.5 sticky right-0 z-10 bg-white group-hover:bg-blue-50/90 border-l border-gray-100/50 transition-colors">
                    <div class="flex items-center justify-center gap-1">
                      <button @click="openDetailModal(contract)"
                              class="w-8 h-8 rounded-lg border border-gray-200 text-gray-500 hover:border-blue-300 hover:text-blue-600 hover:bg-blue-50 transition-all flex items-center justify-center bg-white shadow-sm"
                              title="Xem chi tiết & xử lý">
                        <i class="fas fa-edit text-xs"></i>
                      </button>
                      <button @click="openViewModal(contract)"
                              class="w-8 h-8 rounded-lg border border-gray-200 text-gray-500 hover:border-gray-300 hover:text-gray-700 hover:bg-gray-50 transition-all flex items-center justify-center bg-white shadow-sm"
                              title="Xem nhanh">
                        <i class="fas fa-eye text-xs"></i>
                      </button>
                      <button v-if="contract.status === 'DRAFT'"
                              @click="deleteDraft(contract.id)"
                              class="w-8 h-8 rounded-lg border border-gray-200 text-gray-500 hover:border-red-300 hover:text-red-500 hover:bg-red-50 transition-all flex items-center justify-center bg-white shadow-sm"
                              title="Xóa nháp">
                        <i class="fas fa-trash-alt text-xs"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- PAGINATION -->
          <div class="border-t border-gray-100 px-4 sm:px-5 py-3 flex items-center justify-between bg-gray-50/50 shrink-0 flex-wrap gap-3">
            <span class="text-[11px] sm:text-xs text-gray-500">
              Hiển thị <span class="font-bold text-gray-700">{{ contracts.length }}</span>/<!--
              --><span class="font-bold text-gray-700">{{ totalElements }}</span> HĐ
              <span class="text-gray-400 mx-1 hidden sm:inline">·</span>
              <span class="hidden sm:inline">Trang <span class="font-bold text-blue-600">{{ currentPage }}</span>/{{ totalPages }}</span>
            </span>
            <div class="flex items-center gap-1">
              <button @click="changePage(1)" :disabled="currentPage <= 1 || isLoading"
                      class="w-7 h-7 sm:w-8 sm:h-8 rounded border border-gray-200 bg-white text-gray-500 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed text-xs flex items-center justify-center transition-colors">
                <i class="fas fa-angle-double-left"></i>
              </button>
              <button @click="changePage(currentPage - 1)" :disabled="currentPage <= 1 || isLoading"
                      class="w-7 h-7 sm:w-8 sm:h-8 rounded border border-gray-200 bg-white text-gray-500 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed text-xs flex items-center justify-center transition-colors">
                <i class="fas fa-angle-left"></i>
              </button>

              <div class="hidden sm:flex gap-1 mx-1">
                <button v-for="p in visiblePages" :key="p"
                        @click="changePage(p)"
                        :class="p === currentPage
                          ? 'bg-blue-600 border-blue-600 text-white font-bold'
                          : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'"
                        class="w-8 h-8 rounded border text-xs transition-colors">
                  {{ p }}
                </button>
              </div>

              <button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages || isLoading"
                      class="w-7 h-7 sm:w-8 sm:h-8 rounded border border-gray-200 bg-white text-gray-500 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed text-xs flex items-center justify-center transition-colors">
                <i class="fas fa-angle-right"></i>
              </button>
              <button @click="changePage(totalPages)" :disabled="currentPage >= totalPages || isLoading"
                      class="w-7 h-7 sm:w-8 sm:h-8 rounded border border-gray-200 bg-white text-gray-500 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed text-xs flex items-center justify-center transition-colors">
                <i class="fas fa-angle-double-right"></i>
              </button>
            </div>
          </div>
        </div>

        </template><!-- /tableVisible -->

      </div><!-- /main content -->
    </main>

    <!-- ====================================================
         MODAL: CHI TIẾT & XỬ LÝ HỢP ĐỒNG
    ===================================================== -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showDetailModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div class="absolute inset-0 bg-gray-900/60 backdrop-blur-sm" @click="showDetailModal = false"></div>

          <div class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl overflow-hidden flex flex-col relative z-10 max-h-[95vh] sm:max-h-[90vh]">

            <!-- Modal Header -->
            <div class="flex items-center justify-between px-5 sm:px-6 py-4 border-b border-gray-100 bg-gradient-to-r from-blue-600 to-indigo-600 shrink-0">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 bg-white/20 rounded-xl flex items-center justify-center">
                  <i class="fas fa-file-contract text-white"></i>
                </div>
                <div>
                  <h2 class="text-white font-black text-base leading-tight">Xử lý Hợp đồng</h2>
                  <p class="text-blue-200 text-[11px] font-mono">{{ selectedContract?.contractNumber }}</p>
                </div>
              </div>
              <button @click="showDetailModal = false" class="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 text-white flex items-center justify-center transition-colors">
                <i class="fas fa-times text-sm"></i>
              </button>
            </div>

            <!-- Timeline Progress -->
            <div class="px-5 sm:px-6 py-4 sm:py-5 bg-gray-50/80 border-b border-gray-100 shrink-0 overflow-x-auto custom-x-scrollbar">
              <div class="flex items-center justify-between relative min-w-[500px] px-2 py-1">
                <div class="absolute left-6 right-6 top-1/2 -translate-y-1/2 h-0.5 bg-gray-200">
                  <div class="h-full bg-blue-500 transition-all duration-500"
                       :style="{ width: getProgressWidth() }"></div>
                </div>
                <div v-for="(step, i) in timelineSteps" :key="i" class="flex flex-col items-center relative z-10">
                  <div :class="getStepCircleStyle(step.value)"
                       class="w-7 h-7 sm:w-8 sm:h-8 rounded-full flex items-center justify-center text-xs border-2 transition-all duration-300 bg-white shadow-sm">
                    <i v-if="isStepCompleted(step.value)" class="fas fa-check text-white text-[10px]"></i>
                    <span v-else class="font-black text-[10px]">{{ i + 1 }}</span>
                  </div>
                  <span :class="isStepActive(step.value) ? 'text-blue-600 font-black' : isStepCompleted(step.value) ? 'text-emerald-600 font-bold' : 'text-gray-400 font-medium'"
                        class="text-[9px] uppercase mt-2 w-16 text-center leading-tight tracking-wide whitespace-nowrap">
                    {{ step.label }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Modal Body -->
            <div class="overflow-y-auto flex-1 p-4 sm:p-6 space-y-4 custom-y-scrollbar">

              <!-- Thông tin chung & Tài chính -->
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm">
                  <h4 class="text-[10px] font-black text-gray-400 uppercase tracking-widest mb-3 border-b border-gray-100 pb-2">Thông tin khách hàng</h4>
                  <div class="flex items-center gap-3 mb-3">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center shrink-0 shadow-sm">
                      <span class="text-white text-sm font-black">{{ getInitials(selectedContract?.customerName) }}</span>
                    </div>
                    <div class="min-w-0">
                      <p class="font-bold text-gray-800 truncate">{{ selectedContract?.customerName || '—' }}</p>
                      <p class="text-xs text-gray-500 font-mono">{{ selectedContract?.customerPhone || '—' }}</p>
                    </div>
                  </div>
                  <div class="text-xs text-gray-600 space-y-1.5 bg-gray-50 p-2.5 rounded-lg border border-gray-100 mt-2">
                    <div class="flex justify-between items-center">
                      <span class="text-gray-500">Dòng xe:</span>
                      <span class="font-bold text-gray-800">{{ selectedContract?.carVariantName || '—' }}</span>
                    </div>
                    <div class="flex justify-between items-center">
                      <span class="text-gray-500">Số khung (VIN):</span>
                      <span class="font-mono font-bold" :class="selectedContract?.vinNumber ? 'text-indigo-600 bg-indigo-50 px-1 rounded' : 'text-red-400 italic'">
                        {{ selectedContract?.vinNumber || 'Chưa gán' }}
                      </span>
                    </div>
                  </div>
                </div>

                <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm">
                  <h4 class="text-[10px] font-black text-gray-400 uppercase tracking-widest mb-3 border-b border-gray-100 pb-2">Tài chính</h4>
                  <div class="space-y-2 text-xs">
                    <div class="flex justify-between items-center">
                      <span class="text-gray-500">Giá trị HĐ:</span>
                      <span class="font-black text-gray-800 text-[13px]">{{ formatPrice(selectedContract?.finalPrice) }}</span>
                    </div>
                    <div class="flex justify-between items-center">
                      <span class="text-gray-500">Khách trả trước:</span>
                      <span class="font-bold text-emerald-600">{{ formatPrice(selectedContract?.prepaymentAmount) }}</span>
                    </div>
                    <div class="flex justify-between items-center pt-1.5 border-t border-gray-100 border-dashed">
                      <span class="text-gray-500">Gói dịch vụ Pin:</span>
                      <span :class="selectedContract?.batteryServiceType === 'PURCHASE' ? 'text-violet-600 bg-violet-50' : 'text-sky-600 bg-sky-50'" class="font-bold px-1.5 py-0.5 rounded">
                        <i :class="selectedContract?.batteryServiceType === 'PURCHASE' ? 'fas fa-bolt' : 'fas fa-sync-alt'" class="mr-1"></i>
                        {{ selectedContract?.batteryServiceType === 'PURCHASE' ? 'Mua pin' : 'Thuê pin' }}
                      </span>
                    </div>
                    <template v-if="selectedContract?.loanTermMonths > 0">
                      <div class="flex justify-between items-center">
                        <span class="text-gray-500">Kỳ hạn vay:</span>
                        <span class="font-bold text-amber-600">{{ selectedContract?.loanTermMonths }} tháng · Lãi {{ selectedContract?.interestRate }}%</span>
                      </div>
                      <div class="flex justify-between items-center bg-red-50 p-1.5 rounded text-red-600 mt-1">
                        <span class="font-medium">Trả góp mỗi tháng:</span>
                        <span class="font-black">{{ formatPrice(selectedContract?.monthlyInstallment) }}</span>
                      </div>
                    </template>
                  </div>
                </div>
              </div>

              <!-- Lịch trình & Pháp lý (Giao diện mới gộp Form Sửa Lịch) -->
              <div class="bg-blue-50/50 rounded-xl p-4 border border-blue-100">
                <h4 class="text-[10px] font-black text-blue-500 uppercase tracking-widest mb-3 border-b border-blue-200/50 pb-2">Lịch trình & Pháp lý</h4>
                
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-y-4 gap-x-6 text-xs">
                  
                  <!-- Khối nhập Lịch Ký HĐ -->
                  <div class="flex items-start gap-3">
                    <div class="w-7 h-7 rounded bg-white border border-blue-200 flex items-center justify-center text-blue-400 shrink-0"><i class="fas fa-calendar-check"></i></div>
                    <div class="w-full">
                      <p class="text-gray-500 mb-1 font-medium">Ngày hẹn ký HĐ</p>
                      <!-- Cho phép nhập nếu Hợp đồng chưa Hủy/Giao -->
                      <input v-if="selectedContract?.status !== 'CANCELLED' && selectedContract?.status !== 'DELIVERED'"
                             type="datetime-local" v-model="editSigningDate"
                             class="w-full text-xs p-1.5 border border-blue-200 rounded outline-none focus:ring-1 focus:ring-blue-500 bg-white font-mono text-gray-700">
                      <p v-else class="font-bold text-gray-800 font-mono">{{ selectedContract?.signingAppointmentDate ? formatDateTime(selectedContract.signingAppointmentDate) : 'Chưa xếp lịch' }}</p>
                    </div>
                  </div>

                  <!-- Khối nhập Lịch Giao Xe -->
                  <div class="flex items-start gap-3">
                    <div class="w-7 h-7 rounded bg-white border border-blue-200 flex items-center justify-center text-blue-500 shrink-0"><i class="fas fa-truck-loading"></i></div>
                    <div class="w-full">
                      <p class="text-gray-500 mb-1 font-medium">Ngày giao xe dự kiến</p>
                      <input v-if="selectedContract?.status !== 'CANCELLED' && selectedContract?.status !== 'DELIVERED'"
                             type="datetime-local" v-model="editDeliveryDate"
                             class="w-full text-xs p-1.5 border border-blue-200 rounded outline-none focus:ring-1 focus:ring-blue-500 bg-white font-mono text-gray-700">
                      <p v-else class="font-bold text-gray-800 font-mono">{{ selectedContract?.expectedDeliveryDate ? formatDateTime(selectedContract.expectedDeliveryDate) : 'Chưa xếp lịch' }}</p>
                    </div>
                  </div>

                  <!-- Trạng thái & Pháp lý -->
                  <div class="flex items-start gap-3">
                    <div class="w-7 h-7 rounded bg-white border border-blue-200 flex items-center justify-center text-indigo-400 shrink-0"><i class="fas fa-info-circle"></i></div>
                    <div>
                      <p class="text-gray-500 mb-1 font-medium">Trạng thái hệ thống</p>
                      <span :class="getStatusStyle(selectedContract?.status)"
                            class="inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-[10px] font-black border">
                        <span class="w-1.5 h-1.5 rounded-full" :class="getStatusDot(selectedContract?.status)"></span>
                        {{ getStatusLabel(selectedContract?.status) }}
                      </span>
                    </div>
                  </div>

                  <div class="flex items-start gap-3">
                    <div class="w-7 h-7 rounded bg-white border border-blue-200 flex items-center justify-center text-emerald-400 shrink-0"><i class="fas fa-signature"></i></div>
                    <div>
                      <p class="text-gray-500 mb-1 font-medium">Pháp lý (Ký điện tử)</p>
                      <span v-if="selectedContract?.isSigned" class="text-emerald-600 font-bold flex items-center gap-1 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-100 inline-flex">
                        <i class="fas fa-check-circle"></i> Khách đã ký số
                      </span>
                      <span v-else class="text-amber-500 font-medium bg-amber-50 px-2 py-0.5 rounded border border-amber-100 inline-flex">Chờ khách ký</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Admin action panel (Nút Lưu tất cả) -->
              <div class="bg-white rounded-xl p-4 border-2 border-dashed border-gray-300 relative overflow-hidden">
                <div class="absolute top-0 left-0 w-1 h-full bg-blue-500"></div>
                <h4 class="text-[11px] font-black text-gray-700 uppercase tracking-widest mb-3 flex items-center gap-1.5">
                  <i class="fas fa-user-shield text-blue-500"></i> Khu vực Quản trị viên
                </h4>
                <p class="text-xs text-gray-500 mb-3">Vui lòng chọn trạng thái và kiểm tra lại lịch trình. Thao tác lưu sẽ cập nhật toàn bộ thông tin.</p>
                
                <div class="flex gap-2 flex-col sm:flex-row">
                  <select v-model="updateStatusValue"
                          class="flex-1 text-sm font-bold text-gray-700 bg-gray-50 border border-gray-300 rounded-lg p-2.5 outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 focus:bg-white cursor-pointer transition-all">
                    <option value="BANK_APPROVING">🏦 Ngân hàng đang duyệt hồ sơ</option>
                    <option value="ACTIVE">✅ Đã xong thủ tục - Chờ giao xe</option>
                    <option value="DELIVERED">🚗 Xác nhận đã bàn giao xe</option>
                    <option value="CANCELLED">❌ Khách hủy hợp đồng (Hoàn cọc & Trả xe về kho)</option>
                  </select>
                  <!-- Gọi chung 1 hàm handleUpdateStatus -->
                  <button @click="handleUpdateStatus" :disabled="isUpdating"
                          class="bg-blue-600 hover:bg-blue-700 text-white font-bold px-6 py-2.5 rounded-lg transition-colors flex items-center justify-center gap-2 disabled:bg-blue-300 disabled:cursor-not-allowed text-sm shrink-0 shadow-sm">
                    <i v-if="isUpdating" class="fas fa-spinner fa-spin"></i>
                    <i v-else class="fas fa-save"></i>
                    Lưu toàn bộ thay đổi
                  </button>
                </div>
              </div>

            </div><!-- /modal body -->
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- ====================================================
         MODAL: XEM NHANH
    ===================================================== -->
    <!-- ====================================================
         MODAL: XEM NHANH (ĐÃ CẬP NHẬT ĐẦY ĐỦ)
    ===================================================== -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showViewModal" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4">
          <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm" @click="showViewModal = false"></div>
          <div class="bg-white rounded-t-2xl sm:rounded-2xl shadow-2xl w-full sm:max-w-md relative z-10 overflow-hidden flex flex-col max-h-[90vh]">
            
            <!-- Header -->
            <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100 bg-gray-50 shrink-0">
              <h3 class="font-black text-gray-800 text-sm flex items-center gap-2">
                <i class="fas fa-bolt text-amber-500"></i> Thông tin tóm tắt
              </h3>
              <button @click="showViewModal = false" class="text-gray-400 hover:text-red-500 w-6 h-6 rounded flex items-center justify-center hover:bg-red-50 transition-colors">
                <i class="fas fa-times"></i>
              </button>
            </div>
            
            <!-- Body có thanh cuộn -->
            <div class="p-5 overflow-y-auto custom-y-scrollbar space-y-4">
              <!-- Customer Info -->
              <div class="flex items-center gap-3 pb-4 border-b border-gray-100">
                <div class="w-12 h-12 rounded-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center shrink-0 shadow-sm">
                  <span class="text-white text-base font-black">{{ getInitials(viewContract?.customerName) }}</span>
                </div>
                <div class="min-w-0 flex-1">
                  <p class="font-bold text-gray-800 truncate text-base">{{ viewContract?.customerName }}</p>
                  <p class="text-xs text-gray-500 font-mono mt-0.5"><i class="fas fa-phone-alt mr-1"></i>{{ viewContract?.customerPhone || 'Không có SDT' }}</p>
                  <p class="text-[11px] text-blue-500 font-mono font-bold mt-1 bg-blue-50 inline-block px-1.5 py-0.5 rounded">{{ viewContract?.contractNumber }}</p>
                </div>
              </div>
              
              <!-- Property List -->
              <div class="space-y-2">
                <template v-for="(row, index) in viewRows" :key="index">
                  <!-- Nếu là dòng kẻ phân cách -->
                  <div v-if="row.divider" class="h-px bg-gray-100 border-none my-2 w-full"></div>
                  <!-- Nếu là dữ liệu bình thường -->
                  <div v-else class="flex justify-between items-center">
                    <span class="text-gray-500 text-xs">{{ row.label }}</span>
                    <span :class="row.cls || 'font-semibold text-gray-800'" class="text-xs text-right">{{ row.value }}</span>
                  </div>
                </template>
              </div>
            </div>
            
            <!-- Footer -->
            <div class="p-4 border-t border-gray-100 bg-gray-50 shrink-0 text-center">
              <button @click="openDetailModal(viewContract); showViewModal = false" class="text-xs font-bold text-blue-600 hover:text-blue-800 flex items-center justify-center w-full gap-2 transition-colors">
                <i class="fas fa-external-link-alt"></i> Mở trình xử lý chi tiết
              </button>
            </div>
            
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- ====== TOAST ====== -->
    <Teleport to="body">
      <Transition name="toast">
        <div v-if="toast.show"
             :class="toast.type === 'success' ? 'bg-emerald-600 border-emerald-700' : 'bg-red-600 border-red-700'"
             class="fixed bottom-6 right-6 z-[100] text-white px-5 py-3.5 rounded-xl shadow-2xl flex items-center gap-3 text-sm font-bold min-w-[280px] border-b-4">
          <i :class="toast.type === 'success' ? 'fas fa-check-circle text-lg' : 'fas fa-exclamation-circle text-lg'"></i>
          {{ toast.message }}
        </div>
      </Transition>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '@/utils/axios'
import AdminSidebar from '@/components/AdminSidebar.vue'

// ── State chung ──────────────────────────────────────────────────
const isSidebarOpen = ref(false)
const contracts = ref([])
const isLoading = ref(false)
const isUpdating = ref(false)
const tableVisible = ref(false) 

const loadTable = async () => {
  tableVisible.value = true
  await fetchContracts()
}

const hideTable = () => {
  tableVisible.value = false
  contracts.value = []
  totalElements.value = 0
  totalPages.value = 1
  currentPage.value = 1
  searchInput.value = ''
  statusFilter.value = ''
}

// ── Phân trang & Tìm kiếm ────────────────────────────────────────
const currentPage = ref(1)
const totalPages = ref(1)
const totalElements = ref(0)
const pageSize = 10
const searchInput = ref('')
const statusFilter = ref('')

// ── Modal ─────────────────────────────────────────────────────────
const showDetailModal = ref(false)
const showViewModal = ref(false)
const selectedContract = ref(null)
const viewContract = ref(null)
const updateStatusValue = ref('')

// Biến quản lý lịch trình
const editSigningDate = ref('')
const editDeliveryDate = ref('')

// ── Clock ─────────────────────────────────────────────────────────
const currentTime = ref('')
const currentDate = ref('')
let clockInterval = null

const updateClock = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  currentDate.value = now.toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })
}

// ── Toast ─────────────────────────────────────────────────────────
const toast = ref({ show: false, message: '', type: 'success' })
const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type }
  setTimeout(() => { toast.value.show = false }, 3500)
}

// ── Status Config ─────────────────────────────────────────────────
const statusConfig = {
  DRAFT:          { label: 'Bản Nháp',    style: 'bg-gray-100 text-gray-600 border-gray-200',      dot: 'bg-gray-400' },
  PAID:           { label: 'Đã Cọc',      style: 'bg-blue-100 text-blue-700 border-blue-200',      dot: 'bg-blue-500' },
  SIGNED:         { label: 'Đã Ký',       style: 'bg-indigo-100 text-indigo-700 border-indigo-200', dot: 'bg-indigo-500' },
  BANK_APPROVING: { label: 'Chờ Bank',    style: 'bg-amber-100 text-amber-700 border-amber-200',   dot: 'bg-amber-400' },
  ACTIVE:         { label: 'Chờ Giao',    style: 'bg-emerald-100 text-emerald-700 border-emerald-200', dot: 'bg-emerald-500' },
  DELIVERED:      { label: 'Đã Giao',     style: 'bg-teal-100 text-teal-700 border-teal-200',      dot: 'bg-teal-500' },
  CANCELLED:      { label: 'Đã Hủy',      style: 'bg-rose-100 text-rose-700 border-rose-200',      dot: 'bg-rose-500' },
}

const statusTabs = [
  { value: '',               label: 'Tất cả',         dot: '#94a3b8', activeClass: 'bg-gray-800 text-white border-gray-800' },
  { value: 'DRAFT',          label: 'Bản nháp',        dot: '#94a3b8', activeClass: 'bg-gray-500 text-white border-gray-500' },
  { value: 'PAID',           label: 'Đã cọc',          dot: '#3b82f6', activeClass: 'bg-blue-600 text-white border-blue-600' },
  { value: 'SIGNED',         label: 'Đã ký',           dot: '#6366f1', activeClass: 'bg-indigo-600 text-white border-indigo-600' },
  { value: 'BANK_APPROVING', label: 'Chờ ngân hàng',   dot: '#f59e0b', activeClass: 'bg-amber-500 text-white border-amber-500' },
  { value: 'ACTIVE',         label: 'Chờ giao',        dot: '#10b981', activeClass: 'bg-emerald-600 text-white border-emerald-600' },
  { value: 'DELIVERED',      label: 'Đã giao',         dot: '#14b8a6', activeClass: 'bg-teal-600 text-white border-teal-600' },
  { value: 'CANCELLED',      label: 'Đã hủy',          dot: '#f43f5e', activeClass: 'bg-rose-500 text-white border-rose-500' },
]

const timelineSteps = [
  { value: 'DRAFT',          label: 'Tạo Nháp' },
  { value: 'PAID',           label: 'Thanh Toán' },
  { value: 'SIGNED',         label: 'Ký HĐ' },
  { value: 'BANK_APPROVING', label: 'Ngân Hàng' },
  { value: 'ACTIVE',         label: 'Sẵn Sàng' },
  { value: 'DELIVERED',      label: 'Giao Xe' },
]

const stepHierarchy = ['DRAFT', 'PAID', 'SIGNED', 'BANK_APPROVING', 'ACTIVE', 'DELIVERED']

// ── KPI Cards ────────────────────────────────────────────────────
const kpiCards = computed(() => {
  const total = totalElements.value
  const pending = contracts.value.filter(c => c.status === 'BANK_APPROVING').length
  const active  = contracts.value.filter(c => c.status === 'ACTIVE').length
  const revenue = contracts.value.reduce((s, c) => s + (c.finalPrice || 0), 0)
  return [
    { label: 'Tổng hợp đồng', value: total, icon: 'fas fa-file-contract', iconBg: 'bg-blue-100', iconColor: 'text-blue-600' },
    { label: 'Chờ ngân hàng', value: pending, icon: 'fas fa-university', iconBg: 'bg-amber-100', iconColor: 'text-amber-600' },
    { label: 'Chờ giao xe',   value: active,  icon: 'fas fa-car',        iconBg: 'bg-emerald-100', iconColor: 'text-emerald-600' },
    { label: 'Doanh số trang', value: formatPriceShort(revenue), icon: 'fas fa-chart-line', iconBg: 'bg-violet-100', iconColor: 'text-violet-600' },
  ]
})

const visiblePages = computed(() => {
  const range = []
  const start = Math.max(1, currentPage.value - 2)
  const end   = Math.min(totalPages.value, currentPage.value + 2)
  for (let i = start; i <= end; i++) range.push(i)
  return range
})

// ── Quick view rows (Cập nhật đầy đủ thuộc tính) ──────────────────
const viewRows = computed(() => {
  const c = viewContract.value
  if (!c) return []
  
  return [
    { label: 'Dòng xe',          value: c.carVariantName || '—', cls: 'font-bold text-gray-800' },
    { label: 'Số khung (VIN)',   value: c.vinNumber || 'Chưa gán', cls: c.vinNumber ? 'font-mono font-bold text-indigo-600 bg-indigo-50 px-1 rounded' : 'italic text-red-400' },
    { label: 'Trạng thái',       value: getStatusLabel(c.status), cls: 'font-black text-blue-600' },
    { label: 'Pháp lý',          value: c.isSigned ? 'Khách đã ký số' : 'Chưa ký', cls: c.isSigned ? 'font-bold text-emerald-600' : 'font-medium text-amber-500' },
    { divider: true }, // Dòng kẻ phân cách
    { label: 'Giá trị HĐ',       value: formatPrice(c.finalPrice), cls: 'font-black text-gray-800 text-[13px]' },
    { label: 'Gói dịch vụ Pin',  value: c.batteryServiceType === 'PURCHASE' ? 'Mua pin' : 'Thuê pin', cls: 'font-bold text-gray-700' },
    { label: 'Chi phí Pin',      value: formatPrice(c.batteryCost), cls: 'text-gray-600' },
    { label: 'Khách trả trước',  value: formatPrice(c.prepaymentAmount), cls: 'font-bold text-emerald-600' },
    ...(c.loanTermMonths > 0 ? [
      { label: 'Kỳ hạn vay',     value: `${c.loanTermMonths} tháng`, cls: 'font-bold text-amber-600' },
      { label: 'Lãi suất',       value: `${c.interestRate}% / năm`, cls: 'font-bold text-amber-600' },
      { label: 'Góp/tháng',      value: formatPrice(c.monthlyInstallment), cls: 'font-black text-red-500' },
    ] : [
      { label: 'Thanh toán',     value: 'Thanh toán thẳng (Không vay)', cls: 'font-medium text-gray-500 italic' }
    ]),
    { divider: true }, // Dòng kẻ phân cách
    { label: 'Hẹn ký HĐ',        value: c.signingAppointmentDate ? formatDate(c.signingAppointmentDate) : 'Chưa hẹn', cls: 'font-mono text-gray-700' },
    { label: 'Hẹn giao xe',      value: c.expectedDeliveryDate ? formatDate(c.expectedDeliveryDate) : 'Chưa hẹn', cls: 'font-mono text-gray-700' },
    { label: 'Ngày tạo HĐ',      value: formatDateTime(c.createdAt), cls: 'text-gray-400 text-[11px]' },
  ]
})

// ── Helpers ───────────────────────────────────────────────────────
const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0)

const formatPriceShort = (v) => {
  if (!v) return '0đ'
  if (v >= 1_000_000_000) return (v / 1_000_000_000).toFixed(1) + ' tỷ'
  if (v >= 1_000_000)     return (v / 1_000_000).toFixed(0) + ' triệu'
  return formatPrice(v)
}

const formatDate = (s) => {
  if (!s) return '—'
  return new Intl.DateTimeFormat('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }).format(new Date(s))
}

const formatDateTime = (s) => {
  if (!s) return '—'
  return new Intl.DateTimeFormat('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(s))
}

const getInitials = (name) => {
  if (!name) return '?'
  const parts = name.trim().split(' ')
  return parts.length >= 2
    ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
    : name.slice(0, 2).toUpperCase()
}

const getStatusLabel = (s) => statusConfig[s]?.label || s
const getStatusStyle = (s) => statusConfig[s]?.style || 'bg-gray-100 text-gray-700 border-gray-200'
const getStatusDot   = (s) => statusConfig[s]?.dot   || 'bg-gray-400'

const getCurrentStepIndex = () => stepHierarchy.indexOf(selectedContract.value?.status)

const isStepCompleted = (v) => {
  const i = stepHierarchy.indexOf(v)
  return i < getCurrentStepIndex() && selectedContract.value?.status !== 'CANCELLED'
}
const isStepActive = (v) => v === selectedContract.value?.status

const getStepCircleStyle = (v) => {
  if (selectedContract.value?.status === 'CANCELLED') return 'border-gray-300 text-gray-400'
  if (isStepCompleted(v)) return 'border-emerald-500 bg-emerald-500 text-white shadow-sm'
  if (isStepActive(v))    return 'border-blue-600 bg-blue-600 text-white ring-4 ring-blue-100 shadow-sm'
  return 'border-gray-300 text-gray-400 bg-white'
}

const getProgressWidth = () => {
  const idx = getCurrentStepIndex()
  if (idx <= 0 || selectedContract.value?.status === 'CANCELLED') return '0%'
  return `${(idx / (timelineSteps.length - 1)) * 100}%`
}

// ── API ───────────────────────────────────────────────────────────
const fetchContracts = async () => {
  isLoading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize,
      keyword: searchInput.value.trim() || null,
      status: statusFilter.value || null,
    }
    const res = await api.get('/api/contracts', { params })
    if (res.data.result?.content) {
      contracts.value    = res.data.result.content
      totalPages.value   = res.data.result.totalPages || 1
      totalElements.value = res.data.result.totalElements || 0
    } else {
      contracts.value    = res.data.result || []
      totalPages.value   = 1
      totalElements.value = contracts.value.length
    }
  } catch (err) {
    console.error('Lỗi lấy danh sách hợp đồng:', err)
    showToast('Không thể tải dữ liệu hợp đồng', 'error')
  } finally {
    isLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchContracts()
}

const changeStatusFilter = (status) => {
  statusFilter.value = status
  currentPage.value  = 1
  fetchContracts()
}

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value && !isLoading.value) {
    currentPage.value = page
    fetchContracts()
  }
}

const clearFilters = () => {
  searchInput.value  = ''
  statusFilter.value = ''
  currentPage.value  = 1
  fetchContracts()
}

// ── Modal handlers ────────────────────────────────────────────────
const openDetailModal = (contract) => {
  selectedContract.value  = contract
  updateStatusValue.value = ['DRAFT', 'PAID', 'SIGNED'].includes(contract.status) ? 'BANK_APPROVING' : contract.status
  
  // Gắn sẵn dữ liệu ngày giờ vào input khi mở modal
  editSigningDate.value = contract.signingAppointmentDate ? contract.signingAppointmentDate.substring(0, 16) : ''
  editDeliveryDate.value = contract.expectedDeliveryDate ? contract.expectedDeliveryDate.substring(0, 16) : ''
  
  showDetailModal.value   = true
}

const openViewModal = (contract) => {
  viewContract.value  = contract
  showViewModal.value = true
}

// GỘP CHUNG LOGIC LƯU LỊCH VÀ TRẠNG THÁI VÀO 1 NÚT
const handleUpdateStatus = async () => {
  if (!confirm(`Xác nhận lưu toàn bộ thay đổi cho hợp đồng này?`)) return
  isUpdating.value = true
  
  try {
    // Gọi API duy nhất, truyền cả status và 2 mốc thời gian
    await api.patch(`/api/contracts/${selectedContract.value.id}`, null, {
      params: {
        status: updateStatusValue.value,
        // Dấu ? ... : null giúp tự động chuyển thành null nếu Admin xóa trắng ô nhập ngày
        signingDate: editSigningDate.value ? editSigningDate.value + ':00' : null,
        deliveryDate: editDeliveryDate.value ? editDeliveryDate.value + ':00' : null
      }
    })

    showToast('Đã lưu toàn bộ thay đổi thành công!')
    showDetailModal.value = false
    await fetchContracts()
  } catch (err) {
    showToast('Lỗi khi lưu: ' + (err.response?.data?.message || err.message), 'error')
  } finally {
    isUpdating.value = false
  }
}

const deleteDraft = async (id) => {
  if (!confirm('Chỉ có thể xóa hợp đồng ở trạng thái Nháp. Xác nhận xóa?')) return
  try {
    await api.delete(`/api/contracts/${id}`)
    showToast('Đã xóa bản nháp thành công!')
    await fetchContracts()
  } catch (err) {
    showToast('Lỗi xóa hợp đồng: ' + (err.response?.data?.message || err.message), 'error')
  }
}

onMounted(() => {
  updateClock()
  clockInterval = setInterval(updateClock, 1000)
})

onUnmounted(() => {
  clearInterval(clockInterval)
})
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active { transition: opacity 0.2s ease; }
.modal-enter-active .bg-white,
.modal-leave-active .bg-white { transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.2s ease; }
.modal-enter-from,
.modal-leave-to { opacity: 0; }
.modal-enter-from .bg-white { transform: scale(0.95) translateY(10px); opacity: 0; }

.toast-enter-active,
.toast-leave-active { transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-enter-from,
.toast-leave-to { opacity: 0; transform: translateY(20px) scale(0.95); }

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fadeInUp 0.3s ease-out forwards;
}

/* --- CSS Scrollbar Tối Ưu Mobile --- */
.custom-x-scrollbar {
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}
.custom-x-scrollbar::-webkit-scrollbar { height: 6px; }
.custom-x-scrollbar::-webkit-scrollbar-track { background: #f1f5f9; border-radius: 8px; }
.custom-x-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 8px; }
.custom-x-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

.custom-y-scrollbar {
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 transparent;
}
.custom-y-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-y-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-y-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 99px; }
.custom-y-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
</style>