package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import com.Nhom7.DACN_KTPM.dto.request.ContractCreationRequest;
import com.Nhom7.DACN_KTPM.dto.response.ContractResponse;
import com.Nhom7.DACN_KTPM.entity.Contract;

import com.Nhom7.DACN_KTPM.entity.Deposit;
import com.Nhom7.DACN_KTPM.mapper.ContractMapper;
import com.Nhom7.DACN_KTPM.repository.ContractRepository;
import com.Nhom7.DACN_KTPM.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final DepositRepository depositRepository;
    private final ContractMapper contractMapper;
    private final CarStockService carStockService;
    private static final BigDecimal BATTERY_PURCHASE_PRICE = new BigDecimal("90000000");


    /**
     * Lấy chi tiết hợp đồng theo ID.
     * Phục vụ cho các trang xem chi tiết hợp đồng của Khách hàng hoặc Admin.
     */
    public ContractResponse getContractById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + id));

        return contractMapper.toContractResponse(contract);
    }
    /**
     * XÓA HỢP ĐỒNG: Chỉ cho phép xóa khi còn ở trạng thái DRAFT.
     * Nếu đã thanh toán hoặc ký kết, không được phép xóa để đảm bảo tính toàn vẹn dữ liệu.
     */
    @Transactional
    public void deleteContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hợp đồng không tồn tại để xóa."));

        // Chỉ cho phép xóa nếu là bản thảo (DRAFT)
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new RuntimeException("Không thể xóa hợp đồng đã thanh toán hoặc đã ký. Chỉ có thể hủy (CANCEL).");
        }

        contractRepository.delete(contract);
    }
    /**
     * BƯỚC 3: Tạo DRAFT Contract ngay khi khách xác nhận cấu hình (Chưa thanh toán)
     */
    @Transactional
    public ContractResponse createContract(ContractCreationRequest request) {
        Deposit deposit = depositRepository.findById(request.getDepositId())
                .orElseThrow(() -> new RuntimeException("Đơn đặt cọc không tồn tại"));

        if (contractRepository.existsByDepositId(request.getDepositId())) {
            throw new RuntimeException("Đơn cọc này đã có hợp đồng.");
        }

        BigDecimal carPrice = deposit.getVariant().getPrice();
        BigDecimal batteryCost = BigDecimal.ZERO;
        BigDecimal finalPrice = carPrice;

        if ("PURCHASE".equals(request.getBatteryServiceType())) {
            batteryCost = BATTERY_PURCHASE_PRICE;
            finalPrice = carPrice.add(batteryCost);
        }

        BigDecimal loanAmount = finalPrice.subtract(request.getPrepaymentAmount());
        BigDecimal monthlyInstallment = calculatePMT(loanAmount, request.getLoanTermMonths(), request.getInterestRate());

        Contract contract = Contract.builder()
                .contractNumber(request.getContractNumber())
                .deposit(deposit)
                .finalPrice(finalPrice)
                .batteryServiceType(request.getBatteryServiceType())
                .batteryCost(batteryCost)
                .prepaymentAmount(request.getPrepaymentAmount())
                .loanTermMonths(request.getLoanTermMonths())
                .interestRate(request.getInterestRate())
                .monthlyInstallment(monthlyInstallment)
                .status(ContractStatus.DRAFT) // Luôn là DRAFT ở bước này
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .isSigned(false)
                .createdBy("SYSTEM") // Hoặc lấy từ SecurityContext nếu có
                .build();

        return contractMapper.toContractResponse(contractRepository.save(contract));
    }

    /**
     * BƯỚC 5: VNPay Callback - Cập nhật đã thanh toán và gán Số khung xe (Giữ kho)
     */
    @Transactional
    public void markAsPaid(String contractNumber, String vnpTransactionNo, String vinNumber) {
        Contract contract = contractRepository.findByContractNumber(contractNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng."));

        contract.setStatus(ContractStatus.PAID);
        contract.setVnpTransactionNo(vnpTransactionNo);
        contract.setVinNumber(vinNumber); // Gán VIN vật lý để xuất kho sau này
        contractRepository.save(contract);
    }

    /**
     * BƯỚC 5.5: Hẹn ngày ký hợp đồng và giao xe
     */
    @Transactional
    public ContractResponse scheduleAppointment(Long contractId, LocalDateTime signingDate, LocalDateTime deliveryDate) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng."));

        if (contract.getStatus() != ContractStatus.PAID && contract.getStatus() != ContractStatus.DRAFT) {
            throw new RuntimeException("Trạng thái hiện tại không cho phép hẹn lịch.");
        }

        contract.setSigningAppointmentDate(signingDate);
        contract.setExpectedDeliveryDate(deliveryDate);
        return contractMapper.toContractResponse(contractRepository.save(contract));
    }

    /**
     * BƯỚC 6: Khách hàng ký điện tử
     */
    @Transactional
    public ContractResponse signContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Hợp đồng không tồn tại."));

        contract.setIsSigned(true);
        contract.setStatus(ContractStatus.SIGNED);
        return contractMapper.toContractResponse(contractRepository.save(contract));
    }

    /**
     * Update trạng thái tự do (Admin dùng)
     */
    @Transactional
    public ContractResponse updateStatus(Long contractId, ContractStatus newStatus) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng."));

        // BƯỚC 2: RÀNG BUỘC NGHIỆP VỤ - Hủy hợp đồng phải nhả kho xe
        if (newStatus == ContractStatus.CANCELLED) {
            if (contract.getVinNumber() != null) {
                // Giải phóng xe về lại bãi (IN_STOCK)
                carStockService.releaseReservedCar(contract.getVinNumber());
                // Xóa mã VIN khỏi hợp đồng bị hủy
                contract.setVinNumber(null);
            }
            // Có thể reset thêm lịch hẹn nếu muốn
            contract.setExpectedDeliveryDate(null);
            contract.setSigningAppointmentDate(null);
        }

        contract.setStatus(newStatus);
        return contractMapper.toContractResponse(contractRepository.save(contract));
    }

    public List<ContractResponse> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toContractResponse)
                .collect(Collectors.toList());
    }

    private BigDecimal calculatePMT(BigDecimal loan, int months, BigDecimal rateYear) {
        if (loan.compareTo(BigDecimal.ZERO) <= 0 || months <= 0) return BigDecimal.ZERO;
        double r = rateYear.doubleValue() / 12 / 100;
        double pmt = (loan.doubleValue() * r * Math.pow(1 + r, months)) / (Math.pow(1 + r, months) - 1);
        return BigDecimal.valueOf(pmt).setScale(0, RoundingMode.HALF_UP);
    }
    public Page<ContractResponse> getContractsWithPagination(int page, int size, String keyword, ContractStatus status) {
        // Pageable trong Spring Data bắt đầu từ 0
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Page<Contract> contractPage;
        // Logic truy vấn sẽ dựa vào các tham số tìm kiếm được truyền vào
        if (keyword != null && status != null) {
            contractPage = contractRepository.findByCustomerNameContainingAndStatus(keyword, status, pageable);
        } else if (keyword != null) {
            contractPage = contractRepository.findByCustomerNameContaining(keyword, pageable);
        } else if (status != null) {
            contractPage = contractRepository.findByStatus(status, pageable);
        } else {
            contractPage = contractRepository.findAll(pageable); // Lấy tất cả nhưng có phân trang
        }

        return contractPage.map(contractMapper::toContractResponse);
    }
    /**
     * CẬP NHẬT GỘP (ADMIN): Đổi trạng thái, xếp lịch trình và đồng bộ với Kho Xe
     */
    @Transactional
    public ContractResponse updateContractAdmin(Long contractId, ContractStatus newStatus, LocalDateTime signingDate, LocalDateTime deliveryDate) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng."));

        // 1. XỬ LÝ TRẠNG THÁI VÀ ĐỒNG BỘ VÒNG ĐỜI KHO XE
        if (newStatus != null && contract.getStatus() != newStatus) {
            String vin = contract.getVinNumber();

            // A. Khách hủy hợp đồng -> Giải phóng xe, xóa VIN, xóa lịch hẹn
            if (newStatus == ContractStatus.CANCELLED) {
                if (vin != null) {
                    carStockService.releaseReservedCar(vin);
                    contract.setVinNumber(null);
                }
                contract.setExpectedDeliveryDate(null);
                contract.setSigningAppointmentDate(null);
            }
            // B. Hợp đồng sẵn sàng giao -> Báo kho dọn rửa xe (PDI)
            else if (newStatus == ContractStatus.ACTIVE) {
                if (vin != null) {
                    try {
                        carStockService.startPdiProcess(vin);
                    } catch (Exception e) {
                        // Bỏ qua lỗi nếu xe đã ở trạng thái PDI từ trước
                    }
                }
            }
            // C. Bàn giao xe cho khách -> Xuất kho chiếc xe tương ứng
            else if (newStatus == ContractStatus.DELIVERED) {
                if (vin != null) {
                    carStockService.markAsDelivered(vin);
                }
            }

            contract.setStatus(newStatus);
        }

        // 2. XỬ LÝ LỊCH TRÌNH (Chỉ cho phép sửa nếu hợp đồng chưa bị HỦY)
        if (contract.getStatus() != ContractStatus.CANCELLED) {
            // Ghi đè trực tiếp giá trị từ Frontend (Kể cả null nếu Admin muốn xóa lịch hẹn)
            contract.setSigningAppointmentDate(signingDate);
            contract.setExpectedDeliveryDate(deliveryDate);
        }

        return contractMapper.toContractResponse(contractRepository.save(contract));
    }
}