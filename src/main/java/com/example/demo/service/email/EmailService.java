package com.example.demo.service.email;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendVerificationEmail(String to, String subject, String text) throws MessagingException, UnsupportedEncodingException;
}
