package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.constant.StockStatus;
import com.Nhom7.DACN_KTPM.dto.request.CarImportRequest;
import com.Nhom7.DACN_KTPM.dto.response.ApiResponse;
import com.Nhom7.DACN_KTPM.dto.response.CarStockResponse;
import com.Nhom7.DACN_KTPM.service.CarStockService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarStockController {

    CarStockService carStockService;

    /**
     * 1. NHẬP KHO HÀNG LOẠT (BATCH IMPORT)
     * Dùng để import danh sách xe từ file Excel của nhà máy.
     */
    @PostMapping("/batch-import")
    public ApiResponse<List<CarStockResponse>> importCarsBatch(@RequestBody @Valid List<CarImportRequest> requests) {
        return ApiResponse.<List<CarStockResponse>>builder()
                .code(1000)
                .result(carStockService.importCarsBatch(requests))
                .message("Nhập kho hàng loạt thành công")
                .build();
    }

    /**
     * 2. TỰ ĐỘNG GIỮ XE (FIFO)
     * Được gọi (có thể từ Backend/Frontend) khi khách thanh toán VNPay thành công để gán VIN.
     */
    @PostMapping("/auto-reserve")
    public ApiResponse<String> autoReserveCar(
            @RequestParam Long variantId,
            @RequestParam Long colorId) {
        return ApiResponse.<String>builder()
                .code(1000)
                .result(carStockService.autoReserveCar(variantId, colorId))
                .message("Đã giữ chỗ xe thành công")
                .build();
    }

    /**
     * 3. KIỂM TRA TRƯỚC GIAO XE (PDI)
     * Kỹ thuật viên xác nhận xe đang được kiểm tra trước khi giao cho khách.
     */
    @PatchMapping("/{vinNumber}/pdi")
    public ApiResponse<Void> startPdiProcess(@PathVariable String vinNumber) {
        carStockService.startPdiProcess(vinNumber);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã chuyển trạng thái xe sang Đang chờ PDI")
                .build();
    }

    /**
     * 4. BÀN GIAO XE (DELIVERED)
     * Chuyển trạng thái xe thành Đã giao (Xuất kho thành công).
     */
    @PatchMapping("/{vinNumber}/deliver")
    public ApiResponse<Void> markAsDelivered(@PathVariable String vinNumber) {
        carStockService.markAsDelivered(vinNumber);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Bàn giao xe thành công")
                .build();
    }

    /**
     * 5. GIẢI PHÓNG XE (KHI HỦY HỢP ĐỒNG)
     * Đưa xe từ trạng thái RESERVED hoặc PDI_PENDING về lại IN_STOCK.
     */
    @PatchMapping("/{vinNumber}/release")
    public ApiResponse<Void> releaseReservedCar(@PathVariable String vinNumber) {
        carStockService.releaseReservedCar(vinNumber);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã giải phóng xe về lại kho (IN_STOCK)")
                .build();
    }

    /**
     * 6. ĐÁNH DẤU LỖI KỸ THUẬT
     * Khóa xe lại không cho phép bán nếu phát hiện lỗi.
     */
    @PatchMapping("/{vinNumber}/defective")
    public ApiResponse<Void> markAsDefective(@PathVariable String vinNumber) {
        carStockService.markAsDefective(vinNumber);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã đánh dấu xe bị lỗi kỹ thuật (DEFECTIVE)")
                .build();
    }

    /**
     * 7. LẤY DANH SÁCH TỒN KHO - ĐÃ CẬP NHẬT PHÂN TRANG
     * Có thể lọc theo trạng thái (Ví dụ: ?status=IN_STOCK). Dùng cho trang Quản lý kho.
     */
    @GetMapping
    public ApiResponse<Page<CarStockResponse>> getInventory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) StockStatus status) {
        return ApiResponse.<Page<CarStockResponse>>builder()
                .code(1000)
                .result(carStockService.getInventoryWithPagination(page, size, status))
                .build();
    }

    /**
     * 8. BÁO CÁO TUỔI KHO (INVENTORY AGING)
     * Xem danh sách xe tồn kho quá lâu (Mặc định: 60 ngày nếu không truyền tham số).
     */
    @GetMapping("/aging")
    public ApiResponse<List<CarStockResponse>> getAgingInventory(
            @RequestParam(defaultValue = "60") int thresholdDays) {
        return ApiResponse.<List<CarStockResponse>>builder()
                .code(1000)
                .result(carStockService.getAgingInventory(thresholdDays))
                .message("Báo cáo danh sách xe tồn kho trên " + thresholdDays + " ngày")
                .build();
    }
}