package com.Nhom7.DACN_KTPM.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarImportRequest {
    // Không cần gửi ID vì đây là xe mới
    Long variantId; // ID của dòng xe (VD: VF 8)
    Long colorId;   // ID của màu sơn thực tế

    String vinNumber; // Số khung
    String engineNumber; // Số máy
    Integer manufacturingYear; // Năm sản xuất (VD: 2024, 2025)
    String storageLocation; // Vị trí đỗ trong bãi (Khu A, Lô B...)
}