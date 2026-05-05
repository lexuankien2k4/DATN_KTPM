package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import com.Nhom7.DACN_KTPM.constant.StockStatus;
import com.Nhom7.DACN_KTPM.dto.response.DashboardSummaryResponse;
import com.Nhom7.DACN_KTPM.repository.CarStockRepository;
import com.Nhom7.DACN_KTPM.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final CarStockRepository carStockRepository;
    private final ContractRepository contractRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        // 1. Xử lý dữ liệu Kho
        List<Object[]> stockCounts = carStockRepository.countCarsByStatus();
        Map<String, Long> inventoryChart = new HashMap<>();
        long inStock = 0, reserved = 0, pdi = 0;

        for (Object[] row : stockCounts) {
            StockStatus status = (StockStatus) row[0];
            Long count = (Long) row[1];
            inventoryChart.put(status.name(), count);

            if (status == StockStatus.IN_STOCK) inStock = count;
            if (status == StockStatus.RESERVED) reserved = count;
            if (status == StockStatus.PDI_PENDING) pdi = count;
        }

        // Lấy xe tồn lâu (> 60 ngày)
        LocalDate thresholdDate = LocalDate.now().minus(60, ChronoUnit.DAYS);
        long agingCars = carStockRepository.countAgingInventory(thresholdDate);

        // 2. Xử lý dữ liệu Kinh doanh
        List<Object[]> contractCounts = contractRepository.countContractsByStatus();
        Map<String, Long> contractChart = new HashMap<>();
        long totalContracts = 0, delivered = 0;

        for (Object[] row : contractCounts) {
            ContractStatus status = (ContractStatus) row[0];
            Long count = (Long) row[1];
            contractChart.put(status.name(), count);
            totalContracts += count;
            if (status == ContractStatus.DELIVERED) delivered = count;
        }

        BigDecimal revenue = contractRepository.calculateTotalExpectedRevenue();
        if (revenue == null) revenue = BigDecimal.ZERO;
// 1. Top 5 xe bán chạy
        List<Object[]> topCarsData = contractRepository.findTopSellingModels(PageRequest.of(0, 5));
        Map<String, Long> topSellingCars = new HashMap<>();
        for (Object[] row : topCarsData) {
            topSellingCars.put((String) row[0], (Long) row[1]);
        }

        // 2. Mua pin vs Thuê pin (Đã bỏ Trả góp)
        List<Object[]> batteryData = contractRepository.countByBatteryServiceType();
        Map<String, Long> batteryChart = new HashMap<>();
        for (Object[] row : batteryData) {
            String type = "PURCHASE".equals(row[0]) ? "Mua Pin" : "Thuê Pin";
            batteryChart.put(type, (Long) row[1]);
        }

        // 3. Doanh thu theo tháng
        List<Object[]> monthlyData = contractRepository.getMonthlyRevenue();
        Map<Integer, BigDecimal> monthlyRevenue = new HashMap<>();
        for (Object[] row : monthlyData) {
            monthlyRevenue.put((Integer) row[0], (BigDecimal) row[1]);
        }

        return DashboardSummaryResponse.builder()
                .totalInStock(inStock)
                .totalReserved(reserved)
                .totalPdiPending(pdi)
                .agingVehicles(agingCars)
                .inventoryStatusChart(inventoryChart)
                .totalContracts(totalContracts)
                .totalDelivered(delivered)
                .totalExpectedRevenue(revenue)
                .contractStatusChart(contractChart)
                .topSellingCars(topSellingCars)
                .batteryServiceChart(batteryChart)
                .monthlyRevenue(monthlyRevenue)
                .build();
    }
}