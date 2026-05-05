package com.Nhom7.DACN_KTPM.repository;

import com.Nhom7.DACN_KTPM.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    // Lấy nhật ký mới nhất hiện lên đầu
    List<SystemLog> findAllByOrderByTimestampDesc();
}
