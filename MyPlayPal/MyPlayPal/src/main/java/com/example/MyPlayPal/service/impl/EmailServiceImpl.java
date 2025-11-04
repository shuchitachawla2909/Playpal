package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Ideally move this to application.properties later
    private static final String ADMIN_EMAIL = "youradminemail@gmail.com";

    @Override
    public void sendAdminNotification(String name, String email, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(ADMIN_EMAIL);
            mail.setSubject("📩 New Contact Message: " + subject);
            mail.setText(
                    "You have received a new message from the contact form.\n\n" +
                            "👤 Name: " + name + "\n" +
                            "📧 Email: " + email + "\n" +
                            "📝 Subject: " + subject + "\n" +
                            "💬 Message:\n" + message + "\n\n" +
                            "-- MyPlayPal Contact System"
            );

            mailSender.send(mail);
            System.out.println("✅ Admin notified successfully via email.");
        } catch (Exception e) {
            System.err.println("❌ Failed to send admin notification: " + e.getMessage());
        }
    }

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
        helper.setText(htmlContent, true); // true enables HTML content

        mailSender.send(message);
    }
}

