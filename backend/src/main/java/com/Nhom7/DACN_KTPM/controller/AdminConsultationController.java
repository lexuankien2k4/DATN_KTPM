package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.request.UpdateConsultationStatusRequest;
import com.Nhom7.DACN_KTPM.dto.response.ConsultationResponse;
import com.Nhom7.DACN_KTPM.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/consultations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminConsultationController {

    ConsultationService consultationService;

    @GetMapping
    public ApiRequest<List<ConsultationResponse>> getAllConsultations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String province) {

        return ApiRequest.<List<ConsultationResponse>>builder()
                .result(consultationService.getAllConsultations(status, province))
                .build();
    }

    @PutMapping("/{id}/status")
    public ApiRequest<ConsultationResponse> updateStatus(
            // 👇 SỬA: Integer -> Long
            @PathVariable("id") Long requestId,
            @Valid @RequestBody UpdateConsultationStatusRequest request) {

        return ApiRequest.<ConsultationResponse>builder()
                .result(consultationService.updateConsultationStatus(requestId, request))
                .build();
    }
}