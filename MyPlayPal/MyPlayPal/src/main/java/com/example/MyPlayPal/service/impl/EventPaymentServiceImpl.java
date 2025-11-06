package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.PaymentTransaction;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.PaymentTransactionRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventPaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class EventPaymentServiceImpl implements EventPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(EventPaymentServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public Order createEventOrder(double amount, String receipt) throws Exception {
        logger.info("Creating Razorpay order for event with Key ID: {}", razorpayKeyId);

        // Validate credentials
        if (razorpayKeyId == null || razorpayKeyId.isEmpty()) {
            logger.error("Razorpay Key ID is not configured!");
            throw new Exception("Razorpay Key ID is not configured");
        }

        if (razorpayKeySecret == null || razorpayKeySecret.isEmpty()) {
            logger.error("Razorpay Key Secret is not configured!");
            throw new Exception("Razorpay Key Secret is not configured");
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            // Convert amount to paise
            int amountInPaise = (int) (amount * 100);

            logger.info("Creating event order for amount: {} INR ({} paise), receipt: {}", amount, amountInPaise, receipt);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);

            Order order = client.orders.create(orderRequest);
            return order;

        } catch (Exception e) {
            logger.error("Failed to create Razorpay order for event: {}", e.getMessage());
            throw new Exception("Failed to create event payment order: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyEventSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes());
            String generatedSignature = new String(Hex.encodeHex(hash));
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            logger.error("Error verifying Razorpay signature for event: {}", e.getMessage());
            return false;
        }
    }


    @Override
    @Transactional
    public void saveRazorpayOrderIdForEvent(Long eventId, String razorpayOrderId) {
        PaymentTransaction transaction = paymentTransactionRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found for eventId: " + eventId));

        transaction.setReferenceId(razorpayOrderId);
        paymentTransactionRepository.save(transaction);

        logger.info("Saved Razorpay order ID {} for event {}", razorpayOrderId, eventId);
    }

    // Add to EventPaymentServiceImpl class

    /**
     * Create payment transaction after event is created
     */
    @Override
    @Transactional
    public void createEventPaymentTransaction(Long eventId, double amount, Long userId) {
        // Find event and user
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Create new payment transaction for event
        PaymentTransaction transaction = PaymentTransaction.builder()
                .event(event)
                .user(user)
                .amount(BigDecimal.valueOf(amount))
                .status(PaymentTransaction.PaymentStatus.SUCCESS) // Already paid
                .build();

        paymentTransactionRepository.save(transaction);
        logger.info("Created payment transaction for event {} with amount {}", eventId, amount);
    }

    /**
     * Mark event as paid (update status if needed)
     */
    @Override
    @Transactional
    public void markEventAsPaid(Long eventId, String paymentId) {
        try {
            System.out.println("🔄 Marking event as paid - Event ID: " + eventId + ", Payment ID: " + paymentId);

            // 1. Find and update the event status to CONFIRMED
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

            System.out.println("📋 Found event: " + event.getEventName() + ", Current status: " + event.getStatus());

            // Update event status to CONFIRMED
            event.setStatus(Event.EventStatus.CONFIRMED);
            Event savedEvent = eventRepository.save(event);

            System.out.println("✅ Event status updated to: " + savedEvent.getStatus());

            // 2. Update payment transaction
            PaymentTransaction transaction = paymentTransactionRepository.findByEventId(eventId)
                    .orElseThrow(() -> new RuntimeException("Payment transaction not found for eventId: " + eventId));

            transaction.setStatus(PaymentTransaction.PaymentStatus.SUCCESS);
            transaction.setReferenceId(paymentId);
            paymentTransactionRepository.save(transaction);

            System.out.println("✅ Payment transaction updated for event: " + eventId);

            logger.info("Event {} marked as paid with payment ID: {}", eventId, paymentId);

        } catch (Exception e) {
            System.err.println("❌ Error in markEventAsPaid: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to mark event as paid: " + e.getMessage(), e);
        }
    }
}