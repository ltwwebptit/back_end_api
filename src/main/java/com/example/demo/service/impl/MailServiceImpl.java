package com.example.demo.service.impl;

import com.example.demo.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSenderImpl mailSender;

    @Override
    public void sendEmailWithToken(String email, String link) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String subject = "Register account";
        String text = "Click the link to register your account " + link;
        mimeMessage.setSubject(subject);
        helper.setTo(email);
        helper.setText(text, true);
        try{
            mailSender.send(mimeMessage);
        }
        catch (Exception e){
            throw new MailSendException("Fail to send mail");
        }
    }

    @Override
    public void sendReplyEmail(String toEmail, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true);
        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailSendException("Fail to send mail");
        }
    }
}
