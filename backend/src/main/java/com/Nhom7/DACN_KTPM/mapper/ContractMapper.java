package com.Nhom7.DACN_KTPM.mapper;

import com.Nhom7.DACN_KTPM.dto.response.ContractResponse;
import com.Nhom7.DACN_KTPM.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(source = "deposit.customerName", target = "customerName")
    @Mapping(source = "deposit.phone", target = "customerPhone")
    @Mapping(source = "deposit.variant.name", target = "carVariantName")
    ContractResponse toContractResponse(Contract contract);
}