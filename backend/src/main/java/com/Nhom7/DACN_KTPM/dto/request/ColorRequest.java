package com.Nhom7.DACN_KTPM.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorRequest {
    @NotBlank(message = "Mã màu không được để trống")
    String colorCode; // VD: W, R, B

    @NotBlank(message = "Tên màu không được để trống")
    String colorName; // VD: Brahminy White

    @NotBlank(message = "Mã Hex không được để trống")
    String hexCode; // VD: #FFFFFF (Dùng cho Vue.js render UI)

    @NotNull(message = "Giá cộng thêm không được để null")
    BigDecimal priceExtra; // 0 nếu là màu tiêu chuẩn, > 0 nếu là màu đặc biệt
}