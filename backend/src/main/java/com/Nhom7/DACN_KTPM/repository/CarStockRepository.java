package com.Nhom7.DACN_KTPM.repository;

import com.Nhom7.DACN_KTPM.entity.CarStock;
import com.Nhom7.DACN_KTPM.constant.StockStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CarStockRepository extends JpaRepository<CarStock, Long> {

    // Tìm xe KHẢ DỤNG cũ nhất (FIFO) theo mẫu và màu sắc
    Optional<CarStock> findFirstByVariantIdAndColorIdAndStatusOrderByImportDateAsc(
            Long variantId, Long colorId, StockStatus status);

    Optional<CarStock> findByVinNumber(String vinNumber);

    List<CarStock> findByStatus(StockStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM CarStock s WHERE s.variant.id = :variantId " +
            "AND s.color.id = :colorId AND s.status = 'IN_STOCK' " +
            "ORDER BY s.importDate ASC")
    Optional<CarStock> findFirstAvailableForReserve(Long variantId, Long colorId);

    boolean existsByVariantId(Long id);

    @Query("SELECT s.vinNumber FROM CarStock s WHERE s.vinNumber IN :vinNumbers")
    List<String> findAllVinIn(@Param("vinNumbers") Collection<String> vinNumbers);

    // Đếm số lượng xe sẵn sàng bán của một phiên bản cụ thể
    long countByVariantIdAndStatus(Long variantId, StockStatus status);

    // Tìm chiếc xe nhập kho sớm nhất (FIFO) đang sẵn sàng bán
    Optional<CarStock> findFirstByVariantIdAndStatusOrderByImportDateAsc(Long variantId, StockStatus status);

    Page<CarStock> findByStatus(StockStatus status, Pageable pageable);
    @Query("SELECT c.status, COUNT(c) FROM CarStock c GROUP BY c.status")
    List<Object[]> countCarsByStatus();

    // Đếm số lượng xe nằm trong bãi quá lâu
    @Query("SELECT COUNT(c) FROM CarStock c WHERE c.status = 'IN_STOCK' AND c.importDate < :thresholdDate")
    long countAgingInventory(@Param("thresholdDate") LocalDate thresholdDate);
}

