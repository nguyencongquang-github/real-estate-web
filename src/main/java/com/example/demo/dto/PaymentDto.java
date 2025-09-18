package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public abstract class PaymentDto {
    
    @Getter
    @Setter
    @Builder
    public static class VNPayResponse {
        private String code;
        private String message; 
        private String paymentUrl;

        public VNPayResponse() {}

        public VNPayResponse(String code, String message, String paymentUrl) {
            this.code = code;
            this.message = message;
            this.paymentUrl = paymentUrl;
        }
    }
}