package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public Order createOrder(double amount, String receipt) throws Exception {

        // Log credentials (be careful in production - don't log secrets!)
        logger.info("Creating Razorpay order with Key ID: {}", razorpayKeyId);
        logger.info("Key Secret is configured: {}", (razorpayKeySecret != null && !razorpayKeySecret.isEmpty()));

        // Validate credentials
        if (razorpayKeyId == null || razorpayKeyId.isEmpty()) {
            logger.error("Razorpay Key ID is not configured!");
            throw new Exception("Razorpay Key ID is not configured in application.properties");
        }

        if (razorpayKeySecret == null || razorpayKeySecret.isEmpty()) {
            logger.error("Razorpay Key Secret is not configured!");
            throw new Exception("Razorpay Key Secret is not configured in application.properties");
        }

        try {
            // Initialize Razorpay client
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            // Convert amount to paise (INR → multiply by 100)
            int amountInPaise = (int) (amount * 100);

            logger.info("Creating order for amount: {} INR ({} paise), receipt: {}", amount, amountInPaise, receipt);

            // Build order request payload
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1); // Auto-capture payments

            // Create order on Razorpay
            Order order = client.orders.create(orderRequest);

            return order;

        } catch (Exception e) {
            logger.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new Exception("Failed to create payment order: " + e.getMessage(), e);
        }
    }
}