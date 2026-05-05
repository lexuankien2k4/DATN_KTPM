package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.request.CarCategoryCreationRequest;
import com.Nhom7.DACN_KTPM.dto.request.CarCategoryUpdateRequest;
import com.Nhom7.DACN_KTPM.dto.response.CarCategoryResponse;
import com.Nhom7.DACN_KTPM.service.CarCategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CarCategoryController {

    CarCategoryService carCategoryService;


    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    ApiRequest<CarCategoryResponse> createCategory(@RequestBody @Valid CarCategoryCreationRequest request) {
        log.info("Request to create car category: {}", request.getName());
        return ApiRequest.<CarCategoryResponse>builder()
                .result(carCategoryService.createCategory(request))
                .message("Car category created successfully")
                .build();
    }

    @GetMapping
    ApiRequest<List<CarCategoryResponse>> getAllCategories() {
        log.info("Request to get all car categories");
        return ApiRequest.<List<CarCategoryResponse>>builder()
                .result(carCategoryService.getAllCategories())
                .build();
    }

    @GetMapping("/{id}")
    ApiRequest<CarCategoryResponse> getCategoryById(@PathVariable Integer id) {
        log.info("Request to get car category by ID: {}", id);
        return ApiRequest.<CarCategoryResponse>builder()
                .result(carCategoryService.getCategory(id))
                .build();
    }

    @PutMapping("/{id}")
        // @PreAuthorize("hasRole('ADMIN')")
    ApiRequest<CarCategoryResponse> updateCategory(@PathVariable Integer id, @RequestBody @Valid CarCategoryUpdateRequest request) {
        log.info("Request to update car category ID: {}", id);
        return ApiRequest.<CarCategoryResponse>builder()
                .result(carCategoryService.updateCategory(id, request))
                .message("Car category updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
        // @PreAuthorize("hasRole('ADMIN')")
    ApiRequest<String> deleteCategory(@PathVariable Integer id) {
        log.info("Request to delete car category ID: {}", id);
        carCategoryService.deleteCategory(id);
        return ApiRequest.<String>builder()
                .result("Danh mục xe đã được xóa thành công")
                .build();
    }
}
