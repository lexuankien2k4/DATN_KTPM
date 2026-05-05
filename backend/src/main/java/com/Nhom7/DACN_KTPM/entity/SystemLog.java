package com.Nhom7.DACN_KTPM.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "system_logs")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Người thực hiện
    private String action;   // Loại hành động (Thêm, Sửa, Xóa)

    @Column(columnDefinition = "TEXT")
    private String detail;   // Chi tiết nội dung thay đổi

    private String status;   // Success hoặc Error
    private String ipAddress; // Địa chỉ IP người dùng

    @CreationTimestamp
    private LocalDateTime timestamp; // Thời gian hệ thống tự ghi nhận
}
