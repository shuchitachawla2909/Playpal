package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // You can keep admin email in application.properties instead of hardcoding it
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
}
