package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.MyPlayPal.repository.BookingRepository;
import com.example.MyPlayPal.repository.PaymentTransactionRepository;
import com.example.MyPlayPal.model.Booking;
import com.example.MyPlayPal.model.PaymentTransaction;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentTransactionRepository paymentRepo;

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

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes());
            String generatedSignature = new String(Hex.encodeHex(hash));
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            logger.error("Error verifying Razorpay signature: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void markBookingAsPaid(Long bookingId, String paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // ✅ use bookingId directly, no need to call booking.getId()
        PaymentTransaction txn = paymentRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        txn.setStatus(PaymentTransaction.PaymentStatus.SUCCESS);
        txn.setReferenceId(paymentId);
        paymentRepo.save(txn);
    }

    /** ✅ New helper method **/
    @Override
    @Transactional
    public void saveRazorpayOrderIdForBooking(Long bookingId, String razorpayOrderId) {
        PaymentTransaction txn = paymentRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found for bookingId: " + bookingId));
        txn.setReferenceId(razorpayOrderId);
        paymentRepo.save(txn);
    }





}