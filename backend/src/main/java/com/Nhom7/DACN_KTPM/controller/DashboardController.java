package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.response.ApiResponse; // Giả định bạn có class ApiResponse chuẩn
import com.Nhom7.DACN_KTPM.dto.response.DashboardSummaryResponse;
import com.Nhom7.DACN_KTPM.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary() {
        return ApiResponse.<DashboardSummaryResponse>builder()
                .result(dashboardService.getDashboardSummary())
                .build();
    }
}