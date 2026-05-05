package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.request.CarVariantCreationRequest;
import com.Nhom7.DACN_KTPM.dto.request.CarVariantUpdateRequest;
import com.Nhom7.DACN_KTPM.dto.response.CarVariantBasicResponse;
import com.Nhom7.DACN_KTPM.dto.response.CarVariantDetailResponse;
import com.Nhom7.DACN_KTPM.service.CarVariantService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CarVariantController {

    CarVariantService carVariantService;

    /**
     * Lấy danh sách biến thể xe hỗ trợ phân trang và lọc theo Model.
     * Sử dụng @PageableDefault để mặc định kích thước trang là 10.
     */
    @GetMapping
    ApiRequest<Page<CarVariantBasicResponse>> getAllVariants(
            @RequestParam(required = false) Long modelId,
            // THÊM SORT VÀ DIRECTION VÀO ĐÂY
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (modelId != null) {
            log.info("Yêu cầu lấy danh sách biến thể hoạt động cho Model ID: {}", modelId);
            return ApiRequest.<Page<CarVariantBasicResponse>>builder()
                    .result(carVariantService.getActiveVariantsByModel(modelId, pageable))
                    .build();
        }
        log.info("Yêu cầu lấy TẤT CẢ biến thể (không lọc, có phân trang)");
        return ApiRequest.<Page<CarVariantBasicResponse>>builder()
                .result(carVariantService.getAllVariants(pageable))
                .build();
    }

    @PostMapping
    ApiRequest<CarVariantDetailResponse> createVariant(@RequestBody @Valid CarVariantCreationRequest request) {
        log.info("Khởi tạo biến thể xe mới: {} cho Model ID: {}", request.getName(), request.getModelId());
        return ApiRequest.<CarVariantDetailResponse>builder()
                .result(carVariantService.createVariant(request))
                .message("Khởi tạo biến thể xe thành công")
                .build();
    }

    @GetMapping("/{id}/details")
    ApiRequest<CarVariantDetailResponse> getVariantDetail(@PathVariable Long id) {
        log.info("Lấy chi tiết biến thể xe ID: {}", id);
        return ApiRequest.<CarVariantDetailResponse>builder()
                .result(carVariantService.getVariantDetail(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiRequest<CarVariantDetailResponse> updateVariant(
            @PathVariable Long id,
            @RequestBody @Valid CarVariantUpdateRequest request) {
        log.info("Cập nhật biến thể xe ID: {}", id);
        return ApiRequest.<CarVariantDetailResponse>builder()
                .result(carVariantService.updateVariant(id, request))
                .message("Cập nhật biến thể xe thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiRequest<String> deleteVariant(@PathVariable Long id) {
        log.info("Yêu cầu xóa/vô hiệu hóa biến thể xe ID: {}", id);
        carVariantService.deleteVariant(id);
        return ApiRequest.<String>builder()
                .result("Xử lý xóa/vô hiệu hóa biến thể xe thành công")
                .build();
    }
    @GetMapping("/available")
    ApiRequest<Page<CarVariantBasicResponse>> getAvailableVariants(
            @RequestParam(required = false) Long modelId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Yêu cầu lấy danh sách biến thể CÓ SẴN TRONG KHO");
        return ApiRequest.<Page<CarVariantBasicResponse>>builder()
                .result(carVariantService.getVariantsWithAvailableStock(modelId, pageable))
                .message("Lấy danh sách xe tồn kho thành công")
                .build();
    }
}