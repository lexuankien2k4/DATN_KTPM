package com.Nhom7.DACN_KTPM.service;

import com.Nhom7.DACN_KTPM.configuration.VnPayProperties;
import com.Nhom7.DACN_KTPM.configuration.VnPayUtil;
import com.Nhom7.DACN_KTPM.constant.ContractStatus;
import com.Nhom7.DACN_KTPM.constant.StockStatus;
import com.Nhom7.DACN_KTPM.dto.request.PaymentCreationRequest;
import com.Nhom7.DACN_KTPM.dto.response.DepositResponse;
import com.Nhom7.DACN_KTPM.dto.response.PaymentResponse;
import com.Nhom7.DACN_KTPM.dto.response.VnPayResponse;
import com.Nhom7.DACN_KTPM.entity.*;
import com.Nhom7.DACN_KTPM.exception.AppException;
import com.Nhom7.DACN_KTPM.exception.ErrorCode;
import com.Nhom7.DACN_KTPM.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final VnPayProperties vnpProperties;
    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final CarVariantRepository carVariantRepository;
    private final ShowroomRepository showroomRepository;
    private final ContractRepository contractRepository;
    private final CarStockRepository carStockRepository;

    // Inject thêm để set password mặc định cho user mới (nếu cần)
    private final PasswordEncoder passwordEncoder;

    @Transactional // Đảm bảo User và Deposit cùng thành công hoặc cùng thất bại
    public PaymentResponse createPayment(PaymentCreationRequest request, HttpServletRequest httpServletRequest) {
        if (request.getAmount() <= 0) throw new AppException(ErrorCode.INVALID_AMOUNT);

        // 1. TẠO USER MỚI (Dựa trên thông tin form)
        // Tách họ và tên (Ví dụ: "Nguyễn Văn A" -> Ho: "Nguyễn Văn", Ten: "A")
        String fullName = request.getFullName();
        String firstName = "";
        String lastName = "";
        if (fullName != null && !fullName.isBlank()) {
            int lastSpaceIdx = fullName.lastIndexOf(" ");
            if (lastSpaceIdx != -1) {
                firstName = fullName.substring(0, lastSpaceIdx);
                lastName = fullName.substring(lastSpaceIdx + 1);
            } else {
                lastName = fullName;
            }
        }

        User newUser = new User();

        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPhone(request.getPhone());

        // Tạo username unique (Vì bảng User yêu cầu username unique)
        // Cách đơn giản: dùng SĐT + timestamp để tránh trùng lặp
        newUser.setUsername(request.getPhone() + "_" + System.currentTimeMillis());

        // Set mật khẩu mặc định (VD: 123456)
        newUser.setPassword(passwordEncoder.encode("123456"));
        newUser.setCustomerStatus("NEW"); // Khách hàng mới

        // Lưu User vào DB
        User savedUser = userRepository.save(newUser);


        // 2. TÌM CÁC THÔNG TIN XE VÀ SHOWROOM
        CarVariant variant = carVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        long availableStock = carStockRepository.countByVariantIdAndStatus(request.getVariantId(), StockStatus.IN_STOCK);
        if (availableStock <= 0) {
            throw new RuntimeException("Xin lỗi, mẫu xe này hiện tại đã hết hàng trong kho!");
        }
        Showroom showroom = showroomRepository.findById(request.getShowroomId())
                .orElseThrow(() -> new AppException(ErrorCode.SHOWROOM_NOT_FOUND));


        // 3. TẠO DEPOSIT (Lưu thông tin User mới và thông tin Form)
        Deposit deposit = Deposit.builder()
                .amount(BigDecimal.valueOf(request.getAmount()))
                .user(savedUser)

                .customerName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .cccd(request.getCitizenId())

                .variant(variant)
                .showroom(showroom)
                .selectedColor(request.getSelectedColor())
                .paymentMethod("VNPAY")
                .status("PENDING")
                .note(request.getNote())
                .build();

        Deposit savedDeposit = depositRepository.save(deposit);

// 4. TẠO URL VNPAY
        String vnp_TxnRef = String.valueOf(savedDeposit.getId());
        long amount = (long) (request.getAmount() * 100L);

        // Xử lý IP Address: Ép về IPv4 nếu chạy localhost
        String vnp_IpAddr = VnPayUtil.getIpAddress(httpServletRequest);
        if (vnp_IpAddr == null || vnp_IpAddr.equals("0:0:0:0:0:0:0:1") || vnp_IpAddr.contains(":")) {
            vnp_IpAddr = "127.0.0.1";
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpProperties.getTmnCode().trim()); // Loại bỏ khoảng trắng thừa
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // TUYỆT ĐỐI không dùng Tiếng Việt có dấu ở đây để tránh lỗi Hash
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);

        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpProperties.getReturnUrl().trim());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // ĐỔI MÚI GIỜ CHUẨN VIỆT NAM
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // TẠO HASH
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    // StandardCharsets.US_ASCII là chuẩn của VNPay v2.1.0
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                    String encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());

                    hashData.append(fieldName).append('=').append(encodedValue);
                    query.append(encodedName).append('=').append(encodedValue);

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                } catch (Exception e) {
                    throw new AppException(ErrorCode.PAYMENT_CREATION_FAILED);
                }
            }
        }
        String queryUrl = query.toString();
        String hashSecret = vnpProperties.getHashSecret().trim();
        String vnp_SecureHash = VnPayUtil.hmacSHA512(hashSecret, hashData.toString());

        // ======= PHẦN QUAN TRỌNG: IN RA CONSOLE ĐỂ KIỂM TRA =======
        System.out.println("=== VNPAY DEBUG INFO ===");
        System.out.println("1. Chuỗi HashData: " + hashData.toString());
        System.out.println("2. SecretKey (kiểm tra xem có khoảng trắng không): [" + hashSecret + "]");
        System.out.println("3. Hash tạo ra: " + vnp_SecureHash);

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpProperties.getPayUrl() + "?" + queryUrl;

        return PaymentResponse.builder().paymentUrl(paymentUrl).build();
    }

    //  CẬP NHẬT HÀM LẤY DANH SÁCH ĐỂ HIỂN THỊ ĐÚNG THÔNG TIN MỚI LƯU
    public List<DepositResponse> getAllDeposits() {
        return depositRepository.findAll().stream().map(deposit -> {

            // Ưu tiên lấy thông tin từ bảng Deposit (nếu có), nếu null thì mới lấy từ bảng User
            String finalName = deposit.getCustomerName();
            if (finalName == null && deposit.getUser() != null) {
                finalName = deposit.getUser().getLastName() + " " + deposit.getUser().getFirstName();
            }

            String finalPhone = deposit.getPhone();
            if (finalPhone == null && deposit.getUser() != null) {
                finalPhone = deposit.getUser().getPhone();
            }

            String carName = deposit.getVariant() != null ? deposit.getVariant().getName() : "Unknown Car";
            String showroomName = deposit.getShowroom() != null ? deposit.getShowroom().getName() : "";

            return DepositResponse.builder()
                    .id(deposit.getId())
                    .amount(deposit.getAmount())
                    .status(deposit.getStatus())
                    .paymentMethod(deposit.getPaymentMethod())

                    .userId(deposit.getUser() != null ? deposit.getUser().getId() : null)

                    //  Trả về các trường thông tin chi tiết
                    .customerName(finalName)
                    .customerPhone(finalPhone)
                    .email(deposit.getEmail())       // Map email
                    .citizenId(deposit.getCccd())    // Cần thêm field này vào DepositResponse

                    .variantId(deposit.getVariant() != null ? deposit.getVariant().getId() : null)
                    .carName(carName)
                    .showroomId(deposit.getShowroom() != null ? deposit.getShowroom().getId() : null)
                    .showroomName(showroomName)
                    .selectedColor(deposit.getSelectedColor())
                    .note(deposit.getNote())
                    .createdAt(deposit.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    // ... Các hàm updateDepositStatus và handleVnPayCallback giữ nguyên
    public void updateDepositStatus(Long id, String status) {
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));
        deposit.setStatus(status);
        depositRepository.save(deposit);
    }

    @Transactional
    public VnPayResponse handleVnPayCallback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");

        if ("00".equals(status)) {
            Long depositId = Long.parseLong(vnp_TxnRef);
            Deposit deposit = depositRepository.findById(depositId)
                    .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

            if ("PENDING".equals(deposit.getStatus())) {
                // 1. CẬP NHẬT ĐƠN ĐẶT CỌC
                deposit.setStatus("PAID");
                deposit.setPaymentMethod("VNPAY");
                depositRepository.save(deposit);

                // 2. TẠO HỢP ĐỒNG (CONTRACT) NẾU CHƯA TỒN TẠI
                if (!contractRepository.existsByDepositId(depositId)) {
                    String contractNo = "HD-" + System.currentTimeMillis() + "-" + depositId;

                    Contract newContract = Contract.builder()
                            .contractNumber(contractNo)
                            .deposit(deposit)
                            .finalPrice(deposit.getVariant() != null ? deposit.getVariant().getPrice() : BigDecimal.ZERO)
                            .prepaymentAmount(deposit.getAmount())
                            .status(ContractStatus.PAID)
                            .isSigned(false)
                            .createdBy("SYSTEM_VNPAY")
                            .vnpTransactionNo(request.getParameter("vnp_TransactionNo"))
                            .build();

                    // LƯU HỢP ĐỒNG TRƯỚC
                    contractRepository.save(newContract);

                    // 3. TỰ ĐỘNG KHÓA XE TRONG KHO (GÁN VIN) - FIFO
                    if (deposit.getVariant() != null) {

                        CarStock availableCar = carStockRepository.findFirstByVariantIdAndStatusOrderByImportDateAsc(
                                deposit.getVariant().getId(),
                                StockStatus.IN_STOCK
                        ).orElse(null);

                        if (availableCar != null) {
                            // SỬA TẠI ĐÂY: Dùng StockStatus.RESERVED thay vì "RESERVED"
                            availableCar.setStatus(StockStatus.RESERVED);
                            carStockRepository.save(availableCar);

                            // Cập nhật số khung (VIN) vào Hợp đồng
                            newContract.setVinNumber(availableCar.getVinNumber());
                            contractRepository.save(newContract);
                        }
                    }
                }
            }
            return VnPayResponse.builder().code("00").message("Success").build();
        } else {
            return VnPayResponse.builder().code("99").message("Failed").build();
        }
    }
}