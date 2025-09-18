package com.example.demo.controller;

import com.example.demo.service.jwt.JwtServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.PaymentDto;
import com.example.demo.dto.ResponseObject;
import com.example.demo.service.payment.PaymentServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentServiceImpl paymentService;
    private final JwtServiceImpl jwtService;

    @GetMapping("/vn-pay-callback")
    public String payCallbackHandler(@RequestParam Map<String, String> callbackData, Model model) {
        // Xử lý callback và lưu thông tin thanh toán
        paymentService.handleVnPayCallback(callbackData, PaymentRestController.username);

        // Lấy thông tin thanh toán từ callbackData
        String responseCode = callbackData.get("vnp_ResponseCode");
        String transactionNo = callbackData.get("vnp_TransactionNo");
        String amount = callbackData.get("vnp_Amount");

        // Thêm thông tin vào model để hiển thị trên giao diện
        model.addAttribute("transactionNo", transactionNo);
        model.addAttribute("amount", amount);
        model.addAttribute("responseCode", responseCode);
        model.addAttribute("isSuccess", "00".equals(responseCode)); // Kiểm tra xem thanh toán có thành công không

        // Trả về tên view để hiển thị
        return "invoice"; // Tên của trang HTML sẽ hiển thị thông tin hóa đơn
    }
}