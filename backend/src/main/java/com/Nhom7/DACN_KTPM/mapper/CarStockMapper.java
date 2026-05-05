package com.Nhom7.DACN_KTPM.mapper;

import com.Nhom7.DACN_KTPM.dto.response.CarStockResponse;
import com.Nhom7.DACN_KTPM.entity.CarStock;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface CarStockMapper {

    @Mapping(source = "variant.name", target = "variantName")
    @Mapping(source = "color.colorName", target = "colorName")
    @Mapping(source = "color.hexCode", target = "hexCode")
    CarStockResponse toCarStockResponse(CarStock carStock);

    // Tự động tính số ngày xe đã nằm trong kho để cảnh báo Admin
    @AfterMapping
    default void calculateDaysInStock(CarStock carStock, @MappingTarget CarStockResponse response) {
        if (carStock.getImportDate() != null &&
                (carStock.getStatus() == com.Nhom7.DACN_KTPM.constant.StockStatus.IN_STOCK ||
                        carStock.getStatus() == com.Nhom7.DACN_KTPM.constant.StockStatus.RESERVED)) {

            long days = ChronoUnit.DAYS.between(carStock.getImportDate(), LocalDate.now());
            response.setDaysInStock(days);
        } else {
            response.setDaysInStock(0);
        }
    }
}