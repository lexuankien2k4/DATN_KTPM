package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.constant.StockStatus;
import com.Nhom7.DACN_KTPM.dto.request.CarVariantCreationRequest;
import com.Nhom7.DACN_KTPM.dto.request.CarVariantUpdateRequest;
import com.Nhom7.DACN_KTPM.dto.response.CarStockResponse;
import com.Nhom7.DACN_KTPM.dto.response.CarVariantBasicResponse;
import com.Nhom7.DACN_KTPM.dto.response.CarVariantDetailResponse;
import com.Nhom7.DACN_KTPM.entity.CarImage;
import com.Nhom7.DACN_KTPM.entity.CarModel;
import com.Nhom7.DACN_KTPM.entity.CarVariant;
import com.Nhom7.DACN_KTPM.exception.AppException;
import com.Nhom7.DACN_KTPM.exception.ErrorCode;
import com.Nhom7.DACN_KTPM.mapper.CarVariantMapper;
import com.Nhom7.DACN_KTPM.repository.CarImageRepository;
import com.Nhom7.DACN_KTPM.repository.CarModelRepository;
import com.Nhom7.DACN_KTPM.repository.CarStockRepository;
import com.Nhom7.DACN_KTPM.repository.CarVariantRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CarVariantService {
    private final CarStockRepository carStockRepository;

    CarVariantRepository carVariantRepository;
    CarModelRepository carModelRepository;
    CarVariantMapper carVariantMapper;
    CarImageRepository carImageRepository;


    @Transactional
    public CarVariantDetailResponse createVariant(CarVariantCreationRequest request) {
        // Ép kiểu modelId sang Long để khớp với Repository
        Long modelId = Long.valueOf(request.getModelId());

        log.info("Creating new car variant: {} for model ID: {}", request.getName(), modelId);

        // Kiểm tra tên phiên bản đã tồn tại trong model này chưa
        if (carVariantRepository.existsByNameAndCarModelId(request.getName(), modelId)) {
            throw new AppException(ErrorCode.CAR_VARIANT_EXISTED);
        }

        // Tìm CarModel tương ứng
        CarModel carModel = carModelRepository.findById(modelId)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_MODEL_NOT_FOUND));

        CarVariant carVariant = carVariantMapper.toCarVariant(request);
        carVariant.setCarModel(carModel);

        try {
            // 1. Lưu xe trước để có ID
            carVariant = carVariantRepository.save(carVariant);

            // Lưu danh sách ảnh (Text Links)
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                for (String url : request.getImageUrls()) {
                    if (url != null && !url.trim().isEmpty()) {
                        CarImage carImage = new CarImage();
                        carImage.setCarVariant(carVariant);
                        carImage.setImageUrl(url.trim());
                        carImageRepository.save(carImage);
                    }
                }
            }

        } catch (DataIntegrityViolationException e) {
            log.error("Error creating car variant - likely constraint violation: {}", request.getName(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return carVariantMapper.toCarVariantDetailResponse(carVariant);
    }


    public Page<CarVariantBasicResponse> getActiveVariantsByModel(Long modelId, Pageable pageable) {
        log.info("Fetching active variants for model ID: {} with pagination", modelId);
        return carVariantRepository.findByCarModelIdAndIsActiveTrueOrderByNameAsc(modelId, pageable)
                .map(carVariantMapper::toCarVariantBasicResponse);
    }


    public CarVariantDetailResponse getVariantDetail(Long id) {
        log.info("Fetching car variant detail for ID: {}", id);
        CarVariant carVariant = carVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_VARIANT_NOT_FOUND));
        return carVariantMapper.toCarVariantDetailResponse(carVariant);
    }

    @Transactional
    public CarVariantDetailResponse updateVariant(Long id, CarVariantUpdateRequest request) {
        log.info("Updating car variant with ID: {}", id);
        CarVariant carVariant = carVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_VARIANT_NOT_FOUND));

        // Kiểm tra tên mới có bị trùng với variant khác trong cùng model không
        if (!carVariant.getName().equalsIgnoreCase(request.getName()) &&
                carVariantRepository.existsByNameAndCarModelIdAndIdNot(request.getName(), carVariant.getCarModel().getId(), id)) {
            throw new AppException(ErrorCode.CAR_VARIANT_EXISTED);
        }

        carVariantMapper.updateCarVariant(carVariant, request);

        try {
            carVariant = carVariantRepository.save(carVariant);
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating car variant ID {} - name likely exists: {}", id, request.getName(), e);
            throw new AppException(ErrorCode.CAR_VARIANT_EXISTED);
        }

        return carVariantMapper.toCarVariantDetailResponse(carVariant);
    }

    @Transactional
    public void deleteVariant(Long id) {
        log.info("Performing soft delete for car variant ID: {}", id);
        CarVariant carVariant = carVariantRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAR_VARIANT_NOT_FOUND));

        // Kiểm tra nếu đã có xe trong kho thì không được xóa vật lý
        if (carStockRepository.existsByVariantId(id)) {
            carVariant.setIsActive(false);
            carVariantRepository.save(carVariant);
            log.info("Variant ID {} has stock, performed soft delete (isActive=false)", id);
        } else {
            carVariantRepository.deleteById(id);
            log.info("Deleted car variant ID: {}", id);
        }
    }

    @Transactional
    public Page<CarVariantBasicResponse> getAllVariants(Pageable pageable) {
        log.info("Fetching ALL car variants with pagination");
        return carVariantRepository.findAll(pageable)
                .map(carVariantMapper::toCarVariantBasicResponse);
    }
    /**
     * HÀM MỚI: Lấy danh sách biến thể xe KÈM SỐ LƯỢNG TỒN KHO.
     * Dành riêng cho giao diện Khách hàng.
     */
    @Transactional
    public Page<CarVariantBasicResponse> getVariantsWithAvailableStock(Long modelId, Pageable pageable) {
        log.info("Lấy danh sách xe kèm số lượng tồn kho. Model ID: {}", modelId);

        Page<CarVariant> variants;
        if (modelId != null) {
            variants = carVariantRepository.findByCarModelIdAndIsActiveTrueOrderByNameAsc(modelId, pageable);
        } else {
            variants = carVariantRepository.findAll(pageable);
        }

        return variants.map(variant -> {
            CarVariantBasicResponse response = carVariantMapper.toCarVariantBasicResponse(variant);
            long stockCount = carStockRepository.countByVariantIdAndStatus(variant.getId(), StockStatus.IN_STOCK);            response.setAvailableStock(stockCount);
            return response;
        });
    }
}