package com.Nhom7.DACN_KTPM.entity;

import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "contract_number", unique = true, nullable = false)
    String contractNumber;

    // Lưu ý: nullable = true vì Hợp đồng được tạo NHÁP trước khi khách thanh toán cọc
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id", nullable = true)
    Deposit deposit;

    @Column(name = "final_price")
    BigDecimal finalPrice;

    // Chuyển từ String sang Enum để quản lý luồng 8 bước
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ContractStatus status;

    @Column(name = "created_by")
    String createdBy;

    // == THÔNG TIN DỊCH VỤ PIN ==
    @Column(name = "battery_service_type")
    String batteryServiceType;

    @Column(name = "battery_cost")
    BigDecimal batteryCost;

    // == THÔNG TIN TÀI CHÍNH TRẢ GÓP ==
    @Column(name = "prepayment_amount")
    BigDecimal prepaymentAmount;

    @Column(name = "loan_term_months")
    Integer loanTermMonths;

    @Column(name = "monthly_installment")
    BigDecimal monthlyInstallment;

    @Column(name = "interest_rate")
    BigDecimal interestRate;

    // == NGHIỆP VỤ KÝ KẾT & LỊCH HẸN (MỚI) ==
    @Column(name = "is_signed" ,nullable = false)
    Boolean isSigned = false; // Đánh dấu đã ký điện tử

    @Column(name = "vnp_transaction_no")
    String vnpTransactionNo; // Lưu mã giao dịch VNPay để đối soát

    @Column(name = "signing_appointment_date")
    LocalDateTime signingAppointmentDate; // Lịch hẹn đến showroom ký hợp đồng

    @Column(name = "expected_delivery_date")
    LocalDateTime expectedDeliveryDate; // Lịch hẹn giao xe

    // == QUẢN LÝ KHO (MỚI) ==
    @Column(name = "vin_number")
    String vinNumber; // Số khung của chiếc xe vật lý được "Giữ chỗ" (RESERVED) cho hợp đồng này

    // == THÔNG TIN THỜI GIAN ==
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}