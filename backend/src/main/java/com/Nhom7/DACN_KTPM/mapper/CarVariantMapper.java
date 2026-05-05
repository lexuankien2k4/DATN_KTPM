package com.Nhom7.DACN_KTPM.mapper;

import com.Nhom7.DACN_KTPM.dto.request.CarVariantCreationRequest;
import com.Nhom7.DACN_KTPM.dto.request.CarVariantUpdateRequest;
import com.Nhom7.DACN_KTPM.dto.response.CarVariantBasicResponse;
import com.Nhom7.DACN_KTPM.dto.response.CarVariantDetailResponse;
import com.Nhom7.DACN_KTPM.entity.CarImage;
import com.Nhom7.DACN_KTPM.entity.CarVariant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CarImageMapper.class})
public interface CarVariantMapper {

    @Mapping(source = "carModel.name", target = "modelName")
    @Mapping(source = "carModel.category.name", target = "fuelType")
    @Mapping(source = "rangeNedc", target = "rangeNedc") // Quãng đường di chuyển
    @Mapping(source = "seatCapacity", target = "seatCapacity") // Số chỗ ngồi
    @Mapping(source = "battery", target = "battery")
    @Mapping(target = "firstImageUrl", expression = "java(getFirstImageUrl(carVariant.getImages()))")
    CarVariantBasicResponse toCarVariantBasicResponse(CarVariant carVariant);

    List<CarVariantBasicResponse> toCarVariantBasicResponseList(List<CarVariant> carVariants);

    @Mapping(source = "carModel.name", target = "modelName")
    @Mapping(source = "carModel.category.name", target = "fuelType")
    CarVariantDetailResponse toCarVariantDetailResponse(CarVariant carVariant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carModel", ignore = true)
    @Mapping(target = "images", ignore = true)


    CarVariant toCarVariant(CarVariantCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carModel", ignore = true)
    @Mapping(target = "images", ignore = true)

    void updateCarVariant(@MappingTarget CarVariant carVariant, CarVariantUpdateRequest request);

    default String getFirstImageUrl(List<CarImage> images) {
        if (images != null && !images.isEmpty()) {
            return images.stream()
                    .findFirst()
                    .map(CarImage::getImageUrl)
                    .orElse(null);
        }
        return null;
    }
}