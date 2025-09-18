package com.example.demo.service.payment;


import com.example.demo.config.payment.VNPAYConfig;
import com.example.demo.constant.PredefinedRole;
import com.example.demo.dto.PaymentDto;
import com.example.demo.entity.Payment;
import com.example.demo.entity.User;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.user.UserServiceImpl;
import com.example.demo.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {
    private final VNPAYConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    public PaymentDto.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDto.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Transactional
    public void handleVnPayCallback(Map<String, String> callbackData, String username) {
        // Extract data from callbackData
        String transactionNo = callbackData.get("vnp_TransactionNo");
        String orderInfo = callbackData.get("vnp_OrderInfo");
        String amount = callbackData.get("vnp_Amount");
        String responseCode = callbackData.get("vnp_ResponseCode");
        String transactionStatus = callbackData.get("vnp_TransactionStatus");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (responseCode.equals("00")) {
            userService.updateUserRole(user, PredefinedRole.REALTOR_ROLE);
        }

        // Create a Payment entity and save it to the database
        Payment payment = Payment.builder()
                .transactionNo(transactionNo)
                .orderInfo(orderInfo)
                .amount(new BigDecimal(amount))
                .responseCode(responseCode)
                .transactionStatus(transactionStatus)
                .user(user)
                .build();

        paymentRepository.save(payment);
    }
}