package com.example.MyPlayPal.service;

import com.razorpay.Order;

public interface EventPaymentService {

    // Create Razorpay order for event creation
    Order createEventOrder(double amount, String receipt) throws Exception;

    // Verify payment signature for events
    boolean verifyEventSignature(String orderId, String paymentId, String signature);

    // Mark event as paid and confirmed
    void markEventAsPaid(Long eventId, String paymentId);

    // Save Razorpay order ID for event payment
    void saveRazorpayOrderIdForEvent(Long eventId, String razorpayOrderId);

    // Create payment transaction for event
    void createEventPaymentTransaction(Long eventId, double amount, Long userId);
}