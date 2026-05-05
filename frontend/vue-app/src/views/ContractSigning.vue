<template>
  <div class="max-w-4xl mx-auto py-12 px-6">
    <div class="bg-white rounded-3xl shadow-lg border overflow-hidden">
      <div class="bg-gray-100 p-6 border-b flex justify-between items-center">
        <h2 class="font-black text-blue-900 uppercase">Hợp đồng mua bán điện tử</h2>
        <span class="bg-blue-100 text-blue-600 px-4 py-1 rounded-full text-xs font-bold">Mã số: {{ contract?.contractNumber }}</span>
      </div>
      
      <div class="p-10 h-96 overflow-y-auto text-sm leading-relaxed text-gray-600">
        <p class="font-bold text-center mb-6 text-lg">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
        <p>Bên mua: <strong>{{ contract?.customerName }}</strong></p>
        <p>Số điện thoại: <strong>{{ contract?.customerPhone }}</strong></p>
        <p>Mẫu xe: <strong>{{ contract?.carVariantName }}</strong></p>
        <p>Số khung (VIN): <strong>{{ contract?.vinNumber }}</strong></p>
        <p class="mt-4">Điều khoản tài chính: Vay qua ngân hàng với lãi suất {{ contract?.interestRate }}%.</p>
        </div>

      <div class="p-8 bg-slate-50 border-t flex flex-col items-center">
        <p class="text-xs text-gray-400 mb-4 italic">Bằng việc nhấn "Ký tên", bạn xác nhận đã đọc và đồng ý với các điều khoản trên.</p>
        <button @click="signContract" class="bg-blue-600 text-white px-20 py-4 rounded-2xl font-bold shadow-lg hover:bg-blue-700">
          XÁC NHẬN KÝ TÊN
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const signContract = async () => {
    // Gọi API PUT /api/contracts/{id}/sign
    const res = await fetch(`/api/contracts/${contractId}/sign`, { method: 'PUT' });
    if((await res.json()).code === 1000) {
        alert("Ký hợp đồng thành công! Vui lòng chờ ngân hàng thẩm định.");
    }
}
</script>