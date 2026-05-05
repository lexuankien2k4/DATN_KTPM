package com.Nhom7.DACN_KTPM.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorResponse {
    Long id;
    String colorCode;
    String colorName;
    String hexCode;
    BigDecimal priceExtra;
}