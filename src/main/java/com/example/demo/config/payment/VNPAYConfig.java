package com.example.demo.config.payment;

import com.example.demo.util.VNPayUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPAYConfig {
    @Getter
    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;
    @Value("${payment.vnPay.returnUrl}")
    private String vnp_ReturnUrl;
    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode ;
    @Getter
    @Value("${payment.vnPay.secretKey}")
    private String secretKey;
    @Value("${payment.vnPay.version}")
    private String vnp_Version;
    @Value("${payment.vnPay.command}")
    private String vnp_Command;
    @Value("${payment.vnPay.orderType}")
    private String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnParamsMap = new HashMap<>();
        vnParamsMap.put("vnp_Version", this.vnp_Version);
        vnParamsMap.put("vnp_Command", this.vnp_Command);
        vnParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnParamsMap.put("vnp_Locale", "vn");
        vnParamsMap.put("vnp_CurrCode", "VND");
        vnParamsMap.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
        vnParamsMap.put("vnp_OrderInfo", "Thanh toán don hang: "+ VNPayUtil.getRandomNumber(8));
        vnParamsMap.put("vnp_OrderType", this.orderType);
        vnParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(calendar.getTime());
        vnParamsMap.put("vnp_CreateDate", vnp_CreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);

        return vnParamsMap;
    }
}
