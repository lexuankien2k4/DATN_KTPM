package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.ContractCreationRequest;
import com.Nhom7.DACN_KTPM.dto.response.ApiResponse;
import com.Nhom7.DACN_KTPM.dto.response.ContractResponse;
import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import com.Nhom7.DACN_KTPM.service.ContractService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractController {

    ContractService contractService;

    /**
     * BƯỚC 3: TẠO HỢP ĐỒNG NHÁP (DRAFT)
     * Được gọi ngay sau khi khách hàng xác nhận cấu hình xe, pin và trả góp.
     */
    @PostMapping
    public ApiResponse<ContractResponse> createDraft(@RequestBody @Valid ContractCreationRequest request) {
        return ApiResponse.<ContractResponse>builder()
                .code(1000)
                .result(contractService.createContract(request))
                .build();
    }

    /**
     * BƯỚC 5.5: HẸN LỊCH KÝ HỢP ĐỒNG & GIAO XE
     * Khách hàng chọn ngày sau khi thanh toán tiền trả trước thành công.
     */
    @PutMapping("/{id}/schedule")
    public ApiResponse<ContractResponse> scheduleAppointment(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime signingDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deliveryDate) {

        return ApiResponse.<ContractResponse>builder()
                .code(1000)
                .result(contractService.scheduleAppointment(id, signingDate, deliveryDate))
                .build();
    }

    /**
     * BƯỚC 6: KÝ HỢP ĐỒNG ĐIỆN TỬ
     * Khách hàng xác nhận nội dung và thực hiện ký số/ký xác nhận trên giao diện.
     */
    @PutMapping("/{id}/sign")
    public ApiResponse<ContractResponse> signContract(@PathVariable Long id) {
        return ApiResponse.<ContractResponse>builder()
                .code(1000)
                .result(contractService.signContract(id))
                .build();
    }

    /**
     * BƯỚC 7 & 8: QUẢN LÝ TRẠNG THÁI (DÀNH CHO ADMIN)
     * Dùng để cập nhật trạng thái BANK_APPROVING, ACTIVE, DELIVERED hoặc CANCELLED.
     */
    @PatchMapping("/{id}/status")
    public ApiResponse<ContractResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ContractStatus status) {

        return ApiResponse.<ContractResponse>builder()
                .code(1000)
                .result(contractService.updateStatus(id, status))
                .build();
    }

    /**
     * LẤY CHI TIẾT HỢP ĐỒNG
     * Phục vụ trang hiển thị thông tin hợp đồng cho khách hàng hoặc Admin.
     */
    @GetMapping("/{id}")
    public ApiResponse<ContractResponse> getContractDetail(@PathVariable Long id) {
        // Bạn có thể bổ sung hàm getById vào Service nếu chưa có
        return ApiResponse.<ContractResponse>builder()
                .code(1000)
                .result(contractService.getContractById(id))
                .build();
    }

    /**
     * DANH SÁCH HỢP ĐỒNG (DÀNH CHO ADMIN) - ĐÃ CẬP NHẬT PHÂN TRANG
     * Hiển thị trên Dashboard để quản lý toàn bộ các giao dịch mua xe.
     */
    @GetMapping
    public ApiResponse<Page<ContractResponse>> getContracts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ContractStatus status) {

        return ApiResponse.<Page<ContractResponse>>builder()
                .code(1000)
                .result(contractService.getContractsWithPagination(page, size, keyword, status))
                .build();
    }

    /**
     * XÓA HỢP ĐỒNG NHÁP
     * Chỉ cho phép xóa khi hợp đồng vẫn ở trạng thái DRAFT và khách chưa trả tiền.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDraft(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã xóa bản thảo hợp đồng thành công")
                .build();
    }
    /**
     * CẬP NHẬT TOÀN BỘ (TRẠNG THÁI & LỊCH TRÌNH) DÀNH CHO ADMIN
     * Gộp chung API để giao diện Frontend chỉ cần gọi 1 lần duy nhất.
     */
    @PatchMapping("/{id}")
    public ApiResponse<ContractResponse> updateContractAdmin(
            @PathVariable Long id,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime signingDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deliveryDate) {

        return ApiResponse.<ContractResponse>builder()
                .code(1000)
                .result(contractService.updateContractAdmin(id, status, signingDate, deliveryDate))
                .message("Cập nhật thông tin hợp đồng thành công")
                .build();
    }
}