package com.example.MyPlayPal.service;

import com.razorpay.Order;

public interface ParticipantPaymentService {

    // Create Razorpay order for participant entry fee
    Order createParticipantOrder(double amount, String receipt) throws Exception;

    // Verify payment signature for participants
    boolean verifyParticipantSignature(String orderId, String paymentId, String signature);

    // Mark participant as paid and confirmed
    void markParticipantAsPaid(Long participantId, String paymentId);

    // Save Razorpay order ID for participant payment
    void saveRazorpayOrderIdForParticipant(Long participantId, String razorpayOrderId);

    // Create payment transaction for participant
    void createParticipantPaymentTransaction(Long participantId, double amount, Long userId);
}