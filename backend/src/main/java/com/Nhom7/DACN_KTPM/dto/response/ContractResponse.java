package com.Nhom7.DACN_KTPM.dto.response;
import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {
    Long id;
    String contractNumber;

    String customerName;
    String customerPhone;
    String carVariantName;

    BigDecimal finalPrice;
    String batteryServiceType;
    BigDecimal batteryCost;
    BigDecimal prepaymentAmount;
    Integer loanTermMonths;
    BigDecimal monthlyInstallment;
    BigDecimal interestRate;

    // Các trường phản ánh luồng 8 bước và quản lý kho
    ContractStatus status;
    boolean isSigned;
    String vinNumber;
    LocalDateTime signingAppointmentDate;
    LocalDateTime expectedDeliveryDate;
    LocalDateTime createdAt;
}