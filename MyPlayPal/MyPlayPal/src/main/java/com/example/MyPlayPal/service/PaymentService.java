package com.example.MyPlayPal.service;

import com.razorpay.Order;

public interface PaymentService {
    Order createOrder(double amount, String receipt) throws Exception;

    boolean verifySignature(String orderId, String paymentId, String signature);

    void markBookingAsPaid(Long bookingId, String paymentId);

    void saveRazorpayOrderIdForBooking(Long bookingId, String razorpayOrderId);
}
