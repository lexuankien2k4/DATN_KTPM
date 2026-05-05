package com.Nhom7.DACN_KTPM.constant;

public enum ContractStatus {
    DRAFT,          // Bước 3: Đã tạo cấu hình nháp
    PAID,           // Bước 5: Đã thanh toán tiền trả trước
    SIGNED,         // Bước 6: Khách đã ký điện tử
    BANK_APPROVING, // Bước 7: Ngân hàng đang thẩm định
    ACTIVE,         // Bước 8: Hợp đồng có hiệu lực (Sẵn sàng giao xe)
    DELIVERED,      // Đã giao xe
    COMPLETED,      // Hoàn tất
    CANCELLED       // Bị hủy
}