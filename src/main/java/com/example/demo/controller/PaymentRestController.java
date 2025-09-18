package com.example.demo.controller;

import com.example.demo.dto.PaymentDto;
import com.example.demo.dto.ResponseObject;
import com.example.demo.service.jwt.JwtServiceImpl;
import com.example.demo.service.payment.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentRestController {
    private final PaymentServiceImpl paymentService;
    private final JwtServiceImpl jwtService;
    public static String username;

    @GetMapping("/vn-pay")
    @PreAuthorize("hasAnyAuthority('REALTOR', 'USER')")
    public ResponseObject<PaymentDto.VNPayResponse> pay(HttpServletRequest request, @RequestHeader("Authorization") String token) {
        this.username = jwtService.extractUsername(token.substring(7));
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }
}
