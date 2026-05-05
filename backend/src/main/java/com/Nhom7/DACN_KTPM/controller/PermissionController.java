package com.Nhom7.DACN_KTPM.controller;


import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.request.PermissionRequest;
import com.Nhom7.DACN_KTPM.dto.response.PermissionResponse;
import com.Nhom7.DACN_KTPM.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiRequest<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiRequest.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiRequest<List<PermissionResponse>> getAll() {
        return ApiRequest.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiRequest<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return ApiRequest.<Void>builder().build();
    }
}
