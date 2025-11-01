package com.example.MyPlayPal.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.MyPlayPal.service.PaymentService;  // ✅ add this line



@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);





    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public Order createOrder(double amount, String receipt) throws Exception {

        logger.info("🔑 Razorpay Key ID: {}", razorpayKeyId);
        logger.info("🔐 Razorpay Key Secret: {}", razorpayKeySecret != null ? "Loaded" : "NULL");
        logger.info("Creating order for amount {} and receipt {}", amount, receipt);
        // Initialize Razorpay client
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        // Amount must be in paise (INR → multiply by 100)
        int amountInPaise = (int) (amount * 100);

        // Build order request payload
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1); // Auto-capture payments

        // Create order on Razorpay
        return client.orders.create(orderRequest);
    }



}
