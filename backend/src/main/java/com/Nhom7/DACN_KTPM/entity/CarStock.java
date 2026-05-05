package com.Nhom7.DACN_KTPM.entity;

import com.Nhom7.DACN_KTPM.constant.StockStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Entity
@Table(name = "car_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "vin_number", unique = true, nullable = false)
    String vinNumber; // Số khung - Định danh duy nhất

    @Column(name = "engine_number", unique = true)
    String engineNumber; // Số máy

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    CarVariant variant; // Liên kết tới biến thể (VF8, VF9...)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    Color color; // Liên kết tới bảng Màu sắc chuyên nghiệp

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockStatus status;

    @Column(name = "import_date")
    LocalDate importDate; // Ngày nhập kho để thực hiện FIFO

    @Column(name = "manufacturing_year")
    Integer manufacturingYear; // Năm sản xuất (VIN 2024, 2025...)

    @Column(name = "storage_location")
    String storageLocation; // Vị trí trong bãi (Khu A, Lô B...)
}