package com.Nhom7.DACN_KTPM.controller;


import com.Nhom7.DACN_KTPM.dto.request.ApiRequest;
import com.Nhom7.DACN_KTPM.dto.request.RoleRequest;
import com.Nhom7.DACN_KTPM.dto.response.RoleResponse;
import com.Nhom7.DACN_KTPM.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiRequest<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiRequest.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiRequest<List<RoleResponse>> getAll() {
        return ApiRequest.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiRequest<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return ApiRequest.<Void>builder().build();
    }
}
