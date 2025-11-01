package com.example.MyPlayPal.service;

import com.razorpay.Order;

public interface PaymentService {
    Order createOrder(double amount, String receipt) throws Exception;
}
