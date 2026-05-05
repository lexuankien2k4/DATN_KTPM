package com.Nhom7.DACN_KTPM.mapper;

import com.Nhom7.DACN_KTPM.dto.request.CreateConsultationRequest;
import com.Nhom7.DACN_KTPM.dto.response.ConsultationResponse;
import com.Nhom7.DACN_KTPM.entity.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsultationMapper {


    @Mapping(target = "carVariant", ignore = true)
    @Mapping(target = "showroom", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Consultation toConsultationRequest(CreateConsultationRequest request);




    @Mapping(source = "carVariant.name", target = "carVariantName")


    @Mapping(source = "carVariant.carModel.name", target = "carModelName")

    @Mapping(source = "showroom.name", target = "showroomName")


    @Mapping(source = "showroom.address", target = "showroomAddress")
    // Map object staff từ Entity sang Response
    @Mapping(source = "staff", target = "staff")
    ConsultationResponse toConsultationResponse(Consultation consultationRequest);
}