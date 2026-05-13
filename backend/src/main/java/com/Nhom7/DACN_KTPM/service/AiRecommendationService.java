package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.dto.response.AiRecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final RestTemplate restTemplate; // Phải được cấu hình Bean trong AppConfig
    private final String PYTHON_AI_URL = "http://localhost:5000/api/ai/recommend";

    public List<AiRecommendationResponse> processNaturalLanguageRequest(String userInput) {
        try {
            // 1. Chuẩn bị dữ liệu gửi sang Python
            Map<String, String> requestBody = Map.of("message", userInput);

            // 2. Gọi sang Server Python (Flask)
            Map<String, Object> response = restTemplate.postForObject(PYTHON_AI_URL, requestBody, Map.class);

            // 3. Bóc tách dữ liệu từ Map trả về
            if (response != null && "success".equals(response.get("status"))) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

                // Chuyển đổi List Map thành List DTO để trả về cho Controller
                return data.stream().map(item -> AiRecommendationResponse.builder()
                        .carId(Long.valueOf(item.get("car_id").toString()))
                        .name((String) item.get("name"))
                        .imageUrl((String) item.get("image_url"))
                        // SỬA Ở ĐÂY: Khớp key "category_name" từ Python truyền sang
                        .category((String) item.get("category_name"))
                        .price(Double.valueOf(item.get("price").toString()))
                        .matchScore(Double.valueOf(item.get("match_score").toString()))
                        .build()
                ).toList();
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối AI Python: " + e.getMessage());
            // SỬA Ở ĐÂY: Ném lỗi để VueJS tự bắt và in ra "Hệ thống đang bận..."
            throw new RuntimeException("Dạ, hệ thống đang bận, vui lòng thử lại sau!");
        }

        // Trả về mảng rỗng [] nếu status là "off_topic" hoặc Python tìm không thấy xe
        // VueJS sẽ nhận mảng rỗng này và in ra câu "em thấy mình có nhắc đến xe nhưng chưa rõ nhu cầu..."
        return Collections.emptyList();
    }
}