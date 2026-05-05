package com.Nhom7.DACN_KTPM.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardSummaryResponse {
    long totalInStock;
    long totalReserved;
    long totalPdiPending;
    long agingVehicles;
    Map<String, Long> inventoryStatusChart;

    long totalContracts;
    long totalDelivered;
    BigDecimal totalExpectedRevenue;
    Map<String, Long> contractStatusChart;

    Map<String, Long> topSellingCars;
    Map<String, Long> batteryServiceChart;
    Map<Integer, BigDecimal> monthlyRevenue;
}