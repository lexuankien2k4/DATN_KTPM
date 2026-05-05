package com.Nhom7.DACN_KTPM.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDisplayResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String image; // URL ảnh đại diện
    private String range; // Quãng đường
    private String seats; // Số chỗ
    private String type;  // Loại xe (SUV, Sedan...)
    private String categoryName;
}