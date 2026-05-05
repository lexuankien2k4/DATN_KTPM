package com.Nhom7.DACN_KTPM.repository;

import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import com.Nhom7.DACN_KTPM.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByDepositId(Long depositId);
    boolean existsByContractNumber(String contractNumber);
    Optional<Contract> findByContractNumber(String contractNumber);

    // Dùng @Query để JOIN sang bảng Deposit và tìm kiếm theo customerName
    @Query("SELECT c FROM Contract c WHERE c.deposit.customerName LIKE %:keyword% AND c.status = :status")
    Page<Contract> findByCustomerNameContainingAndStatus(
            @Param("keyword") String keyword,
            @Param("status") ContractStatus status,
            Pageable pageable);

    // Dùng @Query để JOIN sang bảng Deposit và tìm kiếm theo customerName
    @Query("SELECT c FROM Contract c WHERE c.deposit.customerName LIKE %:keyword%")
    Page<Contract> findByCustomerNameContaining(
            @Param("keyword") String keyword,
            Pageable pageable);

    // Hàm này giữ nguyên vì tìm trực tiếp trên trường status của bảng Contract
    Page<Contract> findByStatus(ContractStatus status, Pageable pageable);
    // Đếm tổng số hợp đồng theo từng trạng thái
    @Query("SELECT c.status, COUNT(c) FROM Contract c GROUP BY c.status")
    List<Object[]> countContractsByStatus();

    // Tính tổng doanh thu từ các hợp đồng đã chốt
    @Query("SELECT SUM(c.finalPrice) FROM Contract c WHERE c.status IN ('PAID', 'SIGNED', 'ACTIVE', 'DELIVERED')")
    BigDecimal calculateTotalExpectedRevenue();
    // 1. TOP 5 XE BÁN CHẠY (Dựa trên các hợp đồng đã chốt)
    @Query("SELECT c.deposit.variant.carModel.name, COUNT(c) FROM Contract c " +
            "WHERE c.status IN ('PAID', 'SIGNED', 'ACTIVE', 'DELIVERED') " +
            "GROUP BY c.deposit.variant.carModel.name ORDER BY COUNT(c) DESC")
    List<Object[]> findTopSellingModels(Pageable pageable);


    // 3. TỶ LỆ MUA PIN VS THUÊ PIN
    @Query("SELECT c.batteryServiceType, COUNT(c) FROM Contract c GROUP BY c.batteryServiceType")
    List<Object[]> countByBatteryServiceType();

    // 4. DOANH THU THEO THÁNG (Trong năm hiện tại)
    @Query("SELECT MONTH(c.createdAt), SUM(c.finalPrice) FROM Contract c " +
            "WHERE YEAR(c.createdAt) = YEAR(CURRENT_DATE) " +
            "AND c.status IN ('PAID', 'SIGNED', 'ACTIVE', 'DELIVERED') " +
            "GROUP BY MONTH(c.createdAt) ORDER BY MONTH(c.createdAt)")
    List<Object[]> getMonthlyRevenue();
}