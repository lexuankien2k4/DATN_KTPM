package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.response.CarImageResponse;
import com.Nhom7.DACN_KTPM.entity.CarImage;
import com.Nhom7.DACN_KTPM.entity.CarVariant;
import com.Nhom7.DACN_KTPM.mapper.CarImageMapper;
import com.Nhom7.DACN_KTPM.repository.CarImageRepository;
import com.Nhom7.DACN_KTPM.repository.CarVariantRepository;
import com.Nhom7.DACN_KTPM.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class CarImageController {

    private final FileStorageService fileStorageService;
    private final CarImageRepository carImageRepository;
    private final CarVariantRepository carVariantRepository;
    private final CarImageMapper carImageMapper;

    /**
     * ✅ TỐI ƯU: Upload nhiều ảnh cùng lúc cho một Variant
     */
    @PostMapping("/upload-multiple/{variantId}")
    public ApiRequest<List<CarImageResponse>> uploadMultipleImages(
            @PathVariable Long variantId,
            @RequestParam("files") List<MultipartFile> files // Nhận danh sách file
    ) {
        log.info("Request to upload {} images for variant ID: {}", files.size(), variantId);

        // 1. Kiểm tra variant tồn tại
        CarVariant variant = carVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản xe ID: " + variantId));

        List<CarImageResponse> responses = new ArrayList<>();

        // 2. Lặp qua danh sách file để xử lý
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Lưu file vật lý vào thư mục 'uploads'
                String imageUrl = fileStorageService.storeFile(file);

                // Lưu thông tin ảnh vào Database
                CarImage carImage = new CarImage();
                carImage.setImageUrl(imageUrl);
                carImage.setCarVariant(variant);
                CarImage savedImage = carImageRepository.save(carImage);

                // Chuyển đổi sang Response DTO
                responses.add(carImageMapper.toCarImageResponse(savedImage));
            }
        }

        return ApiRequest.<List<CarImageResponse>>builder()
                .result(responses)
                .message("Đã tải lên " + responses.size() + " ảnh thành công")
                .build();
    }

    /**
     * Giữ nguyên upload đơn lẻ nếu cần dùng cho các trường hợp khác
     */
    @PostMapping("/upload/{variantId}")
    public ApiRequest<CarImageResponse> uploadImage(
            @PathVariable Long variantId,
            @RequestParam("file") MultipartFile file
    ) {
        CarVariant variant = carVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản xe ID: " + variantId));

        String imageUrl = fileStorageService.storeFile(file);

        CarImage carImage = new CarImage();
        carImage.setImageUrl(imageUrl);
        carImage.setCarVariant(variant);

        CarImage savedImage = carImageRepository.save(carImage);

        return ApiRequest.<CarImageResponse>builder()
                .result(carImageMapper.toCarImageResponse(savedImage))
                .message("Upload ảnh thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiRequest<String> deleteImage(@PathVariable Long id) {
        // Chuyển đổi ID an toàn hơn
        if (!carImageRepository.existsById(Math.toIntExact(id))) {
            throw new RuntimeException("Ảnh không tồn tại");
        }
        carImageRepository.deleteById(Math.toIntExact(id));

        return ApiRequest.<String>builder()
                .result("Đã xóa ảnh thành công")
                .build();
    }
}