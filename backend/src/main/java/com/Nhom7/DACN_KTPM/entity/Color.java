package com.Nhom7.DACN_KTPM.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String colorCode; // VD: "VFB" cho VinFast Blue

    @Column(nullable = false)
    String colorName; // VD: "VinFast Blue"

    String hexCode; // VD: "#0033A0" để Frontend hiển thị

    BigDecimal priceExtra; // Giá tiền cộng thêm nếu chọn màu này
}