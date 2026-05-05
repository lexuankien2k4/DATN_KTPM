package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.dto.response.ProductDisplayResponse;
import com.Nhom7.DACN_KTPM.entity.CarModel;
import com.Nhom7.DACN_KTPM.entity.CarVariant;
import com.Nhom7.DACN_KTPM.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicProductService {

    private final CarModelRepository carModelRepository;

    public List<ProductDisplayResponse> getProductsByCategory(String categoryType) {
        Integer categoryId;

        // Xác định ID danh mục dựa trên loại xe
        switch (categoryType.toLowerCase()) {
            case "ev": categoryId = 1; break;
            case "gasoline": categoryId = 2; break;
            case "service": categoryId = 3; break;
            default: categoryId = 1;
        }

        List<CarModel> models = carModelRepository.findByCategoryId(categoryId);
        List<ProductDisplayResponse> responses = new ArrayList<>();

        for (CarModel model : models) {
            // Lấy biến thể đầu tiên làm đại diện hiển thị trên trang chủ
            CarVariant representativeVariant = model.getVariants().stream().findFirst().orElse(null);

            if (representativeVariant != null) {
                // Xử lý URL ảnh để đảm bảo hiển thị đúng
                String rawPath = representativeVariant.getImages().stream()
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse("default.png");

                String finalPath = rawPath.startsWith("images/") || rawPath.startsWith("/images/")
                        ? rawPath
                        : "/images/" + rawPath;

                String imageUrl = "http://localhost:8080" + (finalPath.startsWith("/") ? "" : "/") + finalPath;

                responses.add(ProductDisplayResponse.builder()
                        .id(representativeVariant.getId())
                        .name(model.getName())
                        .price(representativeVariant.getPrice())
                        .image(imageUrl)
                        .range((representativeVariant.getRangeNedc() != null ? representativeVariant.getRangeNedc() : 0) + " km")
                        .type(model.getBodyType())
                        .categoryName(model.getCategory().getName())

                        .seats(representativeVariant.getSeatCapacity() != null
                                ? String.valueOf(representativeVariant.getSeatCapacity())
                                : "N/A")
                        .build());
            }
        }
        return responses;
    }
}