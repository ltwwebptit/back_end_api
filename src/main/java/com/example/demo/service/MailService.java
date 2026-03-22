package com.example.demo.service;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendEmailWithToken(String email, String link) throws MessagingException;
    void sendReplyEmail(String toEmail, String subject, String content) throws MessagingException;
}
