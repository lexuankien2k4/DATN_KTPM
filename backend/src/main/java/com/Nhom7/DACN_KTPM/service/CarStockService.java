package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.dto.request.CarImportRequest;
import com.Nhom7.DACN_KTPM.dto.response.CarStockResponse;
import com.Nhom7.DACN_KTPM.entity.CarStock;
import com.Nhom7.DACN_KTPM.entity.CarVariant;
import com.Nhom7.DACN_KTPM.entity.Color;
import com.Nhom7.DACN_KTPM.constant.StockStatus;
import com.Nhom7.DACN_KTPM.exception.AppException;
import com.Nhom7.DACN_KTPM.exception.ErrorCode;
import com.Nhom7.DACN_KTPM.mapper.CarStockMapper;
import com.Nhom7.DACN_KTPM.repository.CarStockRepository;
import com.Nhom7.DACN_KTPM.repository.CarVariantRepository;
import com.Nhom7.DACN_KTPM.repository.ColorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true) // TỐI ƯU: Mặc định là Read-Only để tăng hiệu năng các hàm GET
public class CarStockService {

    CarStockRepository carStockRepository;
    CarVariantRepository variantRepository;
    ColorRepository colorRepository;
    CarStockMapper carStockMapper;

    // ==========================================
    // 1. NGHIỆP VỤ NHẬP KHO (INBOUND)
    // ==========================================

    /**
     * Nhập kho hàng loạt (Batch Import) từ file Excel của Nhà máy.
     */
    @Transactional
    public List<CarStockResponse> importCarsBatch(List<CarImportRequest> requests) {
        // 1. Lấy tất cả VIN từ request để kiểm tra trùng lặp nội bộ trong danh sách nhập
        Set<String> vinInRequest = requests.stream()
                .map(CarImportRequest::getVinNumber)
                .collect(Collectors.toSet());
        if (vinInRequest.size() < requests.size()) {
            throw new AppException(ErrorCode.DUPLICATE_VIN_IN_LIST);
        }

        // 2. Kiểm tra trùng lặp với Database 1 lần duy nhất
        List<String> existingVins = carStockRepository.findAllVinIn(vinInRequest);
        if (!existingVins.isEmpty()) {
            throw new RuntimeException("Các số khung sau đã tồn tại: " + String.join(", ", existingVins));
        }

        // 3. Xử lý mapping và lưu hàng loạt
        List<CarStock> newStocks = requests.stream().map(req -> {
            CarVariant variant = variantRepository.findById(req.getVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.CAR_VARIANT_NOT_FOUND));
            Color color = colorRepository.findById(req.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));

            return CarStock.builder()
                    .vinNumber(req.getVinNumber())
                    .variant(variant)
                    .color(color)
                    .status(StockStatus.IN_STOCK)
                    .importDate(LocalDate.now())
                    .build();
        }).toList();

        return carStockRepository.saveAll(newStocks).stream()
                .map(carStockMapper::toCarStockResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // 2. NGHIỆP VỤ XUẤT KHO & ĐIỀU PHỐI (OUTBOUND)
    // ==========================================

    /**
     * GIỮ XE TỰ ĐỘNG (FIFO) - Gọi khi khách thanh toán VNPay thành công.
     * @return Mã VIN của chiếc xe được giữ để gán vào Hợp đồng.
     */
    @Transactional
    public String autoReserveCar(Long variantId, Long colorId) {
        // Sử dụng hàm đã có Lock để đảm bảo tính tuần tự khi nhiều người mua cùng lúc
        CarStock car = carStockRepository
                .findFirstAvailableForReserve(variantId, colorId)
                .orElseThrow(() -> new AppException(ErrorCode.OUT_OF_STOCK)); // Dùng ErrorCode chuẩn

        car.setStatus(StockStatus.RESERVED);
        // carStockRepository.save(car); // Không cần thiết nếu đã có @Transactional
        return car.getVinNumber();
    }

    /**
     * CHUẨN BỊ GIAO XE (PDI) - Kỹ thuật viên kiểm tra xe trước khi giao.
     */
    @Transactional
    public void startPdiProcess(String vinNumber) {
        CarStock car = getCarByVin(vinNumber);
        if (car.getStatus() != StockStatus.RESERVED) {
            throw new RuntimeException("Chỉ xe đã được Giữ chỗ (RESERVED) mới có thể làm PDI.");
        }
        car.setStatus(StockStatus.PDI_PENDING);
        carStockRepository.save(car);
    }

    /**
     * BÀN GIAO XE (DELIVERED) - Khách đã ký nhận và lái xe ra khỏi Showroom.
     */
    @Transactional
    public void markAsDelivered(String vinNumber) {
        CarStock car = getCarByVin(vinNumber);
        if (car.getStatus() != StockStatus.PDI_PENDING) {
            throw new RuntimeException("Xe chưa qua bước Kiểm tra trước giao (PDI). Không thể bàn giao.");
        }
        car.setStatus(StockStatus.DELIVERED);
        carStockRepository.save(car);
    }

    // ==========================================
    // 3. NGHIỆP VỤ XỬ LÝ SỰ CỐ & HỦY BỎ
    // ==========================================

    /**
     * KHÁCH HỦY HỢP ĐỒNG - Giải phóng xe về lại bãi để bán cho người khác.
     */
    @Transactional
    public void releaseReservedCar(String vinNumber) {
        CarStock car = getCarByVin(vinNumber);
        // Chỉ nhả những xe đang bị khóa (RESERVED hoặc PDI_PENDING)
        if (car.getStatus() == StockStatus.RESERVED || car.getStatus() == StockStatus.PDI_PENDING) {
            car.setStatus(StockStatus.IN_STOCK);
            carStockRepository.save(car);
        }
    }

    /**
     * XE LỖI KỸ THUẬT - Khóa xe lại chờ bảo hành, không bán.
     */
    @Transactional
    public void markAsDefective(String vinNumber) {
        CarStock car = getCarByVin(vinNumber);
        car.setStatus(StockStatus.DEFECTIVE);
        carStockRepository.save(car);
    }

    // ==========================================
    // 4. NGHIỆP VỤ THỐNG KÊ (DASHBOARD ADMIN)
    // ==========================================

    /**
     * Lấy danh sách xe trong kho theo Trạng thái.
     */
    public List<CarStockResponse> getInventoryByStatus(StockStatus status) {
        List<CarStock> cars = (status == null)
                ? carStockRepository.findAll()
                : carStockRepository.findByStatus(status);

        return cars.stream()
                .map(carStockMapper::toCarStockResponse)
                .collect(Collectors.toList());
    }

    /**
     * (TÍNH NĂNG ĐIỂM CỘNG) BÁO CÁO TUỔI KHO (INVENTORY AGING):
     * Lấy danh sách những chiếc xe IN_STOCK đã nằm ở bãi quá số ngày quy định (VD: > 60 ngày).
     */
    public List<CarStockResponse> getAgingInventory(int thresholdDays) {
        LocalDate cutoffDate = LocalDate.now().minus(thresholdDays, ChronoUnit.DAYS);

        // Lọc xe IN_STOCK nhập kho trước cutoffDate
        return carStockRepository.findAll().stream()
                .filter(car -> car.getStatus() == StockStatus.IN_STOCK)
                .filter(car -> car.getImportDate().isBefore(cutoffDate))
                .map(carStockMapper::toCarStockResponse)
                .collect(Collectors.toList());
    }


    // ==========================================
    // HELPER METHODS (Dùng chung nội bộ)
    // ==========================================
    private CarStock getCarByVin(String vinNumber) {
        return carStockRepository.findByVinNumber(vinNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin xe với Số khung: " + vinNumber));
    }

    /**
     * Lấy danh sách xe trong kho theo Trạng thái (Hỗ trợ Phân trang).
     */
    public Page<CarStockResponse> getInventoryWithPagination(int page, int size, StockStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("importDate").descending());

        Page<CarStock> carPage = (status == null)
                ? carStockRepository.findAll(pageable)
                : carStockRepository.findByStatus(status, pageable);

        return carPage.map(carStockMapper::toCarStockResponse);
    }
}