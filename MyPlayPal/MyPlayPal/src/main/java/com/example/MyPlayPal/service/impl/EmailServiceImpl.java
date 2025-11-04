package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendBookingConfirmation(String toEmail, String subject, String htmlContent)
            throws MessagingException {

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendBookingCancellation(String toEmail, String subject, String htmlContent)
            throws MessagingException {

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    /**
     * Utility method to send any HTML email using JavaMailSender.
     */
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true enables HTML support

        mailSender.send(message);
    }
}
