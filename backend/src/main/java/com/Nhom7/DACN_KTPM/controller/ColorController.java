package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.ColorRequest;
import com.Nhom7.DACN_KTPM.dto.response.ApiResponse;
import com.Nhom7.DACN_KTPM.dto.response.ColorResponse;
import com.Nhom7.DACN_KTPM.service.ColorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColorController {

    ColorService colorService;

    @GetMapping
    public ApiResponse<List<ColorResponse>> getAllColors() {
        return ApiResponse.<List<ColorResponse>>builder()
                .code(1000)
                .result(colorService.getAllColors())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ColorResponse> getColorById(@PathVariable Long id) {
        return ApiResponse.<ColorResponse>builder()
                .code(1000)
                .result(colorService.getColorById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<ColorResponse> createColor(@RequestBody @Valid ColorRequest request) {
        return ApiResponse.<ColorResponse>builder()
                .code(1000)
                .result(colorService.createColor(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ColorResponse> updateColor(@PathVariable Long id, @RequestBody @Valid ColorRequest request) {
        return ApiResponse.<ColorResponse>builder()
                .code(1000)
                .result(colorService.updateColor(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa màu sắc thành công")
                .build();
    }
}