package com.example.MyPlayPal.service;

import jakarta.mail.MessagingException;
public interface EmailService {
    void sendAdminNotification(String name, String email, String subject, String message);
    /**
     * Sends a booking confirmation email to the user.
     *
     * @param toEmail Recipient email address
     * @param subject Subject of the email
     * @param htmlContent HTML content for the email body
     * @throws MessagingException if sending fails
     */
    void sendBookingConfirmation(String toEmail, String subject, String htmlContent) throws MessagingException;

    /**
     * Sends a booking cancellation email to the user.
     *
     * @param toEmail Recipient email address
     * @param subject Subject of the email
     * @param htmlContent HTML content for the email body
     * @throws MessagingException if sending fails
     */
    void sendBookingCancellation(String toEmail, String subject, String htmlContent) throws MessagingException;
}
