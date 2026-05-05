package com.Nhom7.DACN_KTPM.mapper;

import com.Nhom7.DACN_KTPM.dto.request.ColorRequest;
import com.Nhom7.DACN_KTPM.dto.response.ColorResponse;
import com.Nhom7.DACN_KTPM.entity.Color;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    ColorResponse toColorResponse(Color color);

    Color toColor(ColorRequest request);

    // Hàm hỗ trợ cập nhật Entity có sẵn từ Request
    void updateColorFromRequest(ColorRequest request, @MappingTarget Color color);
}
