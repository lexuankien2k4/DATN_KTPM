package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.AiRecommendationRequest;
import com.Nhom7.DACN_KTPM.dto.response.ApiResponse;
import com.Nhom7.DACN_KTPM.dto.response.AiRecommendationResponse;
import com.Nhom7.DACN_KTPM.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiRecommendationService aiRecommendationService;


    @PostMapping("/chat-consult")
    public ApiResponse<List<AiRecommendationResponse>> chatConsult(@RequestBody AiRecommendationRequest request) {

        return ApiResponse.<List<AiRecommendationResponse>>builder()
                .result(aiRecommendationService.processNaturalLanguageRequest(request.getMessage()))
                .message("AI đã phân tích và gợi ý xe cho bạn")
                .build();
    }
}