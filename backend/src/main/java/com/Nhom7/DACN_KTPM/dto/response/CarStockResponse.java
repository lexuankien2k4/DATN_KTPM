package com.Nhom7.DACN_KTPM.dto.response;

import com.Nhom7.DACN_KTPM.constant.StockStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarStockResponse {
    Long id;
    String vinNumber;
    String engineNumber;

    String variantName; // Tên dòng xe (Lấy từ CarVariant)
    String colorName;   // Tên màu (Lấy từ Color)
    String hexCode;     // Mã màu để hiển thị UI

    StockStatus status;
    LocalDate importDate;
    Integer manufacturingYear;
    String storageLocation;

    // Tính năng "Ghi điểm": Hiển thị số ngày xe đã nằm trong kho
    long daysInStock;
}