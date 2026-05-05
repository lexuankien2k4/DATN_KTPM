package com.Nhom7.DACN_KTPM.constant;

public enum StockStatus {
    IN_TRANSIT,     // Xe đang trên đường từ nhà máy về Showroom
    IN_STOCK,
    RESERVED,   // Đã được giữ chỗ (khi khách cọc)
    SOLD,// Xe đã nhập kho, sẵn sàng để bán// Đã được giữ chỗ (Sau khi khách thanh toán cọc/trả trước)
    PDI_PENDING,    // Đang kiểm tra kỹ thuật trước khi giao (Pre-Delivery Inspection)
    DELIVERED,      // Đã bàn giao cho khách (Xuất kho thành công)
    DEFECTIVE,      // Xe lỗi, chờ xử lý bảo hành
    CANCELLED_RETURN // Trả về kho do khách hủy hợp đồng
}