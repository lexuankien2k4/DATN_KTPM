package com.Nhom7.DACN_KTPM.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CarVariantBasicResponse {
    private Long id;
    private String name; // Tên phiên bản
    private Long price;
    private Long discountedPrice;
    private Boolean isActive;
    private String modelName; // Tên dòng xe
    private String firstImageUrl;
    private Integer seatCapacity;
    private String fuelType;
    private Long availableStock;
    private String rangeNedc;
    private String carType;
    private String drivetrain;
    private BigDecimal battery;

}
