package com.Nhom7.DACN_KTPM.controller;

import com.Nhom7.DACN_KTPM.entity.SystemLog;
import com.Nhom7.DACN_KTPM.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/logs")
@RequiredArgsConstructor
public class SystemLogController {
    private final SystemLogRepository logRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SystemLog> getAllLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }
}