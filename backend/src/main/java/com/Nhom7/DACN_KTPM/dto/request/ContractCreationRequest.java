package com.Nhom7.DACN_KTPM.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractCreationRequest {
    Long depositId;
    String contractNumber;
    String batteryServiceType;
    BigDecimal prepaymentAmount;
    Integer loanTermMonths;
    BigDecimal interestRate;

    // Thêm ngày dự kiến nhận xe từ lúc khách hàng cấu hình
    LocalDateTime expectedDeliveryDate;
}