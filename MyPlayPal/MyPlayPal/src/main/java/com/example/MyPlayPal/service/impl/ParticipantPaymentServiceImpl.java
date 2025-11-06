package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.PaymentTransaction;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.PaymentTransactionRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.ParticipantPaymentService;
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

@Service
public class ParticipantPaymentServiceImpl implements ParticipantPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantPaymentServiceImpl.class);

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public Order createParticipantOrder(double amount, String receipt) throws Exception {
        logger.info("Creating Razorpay order for participant");

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            // Convert amount to paise
            int amountInPaise = (int) (amount * 100);

            logger.info("Creating participant order for amount: {} INR ({} paise), receipt: {}", amount, amountInPaise, receipt);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);

            Order order = client.orders.create(orderRequest);
            return order;

        } catch (Exception e) {
            logger.error("Failed to create Razorpay order for participant: {}", e.getMessage());
            throw new Exception("Failed to create participant payment order: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyParticipantSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes());
            String generatedSignature = new String(Hex.encodeHex(hash));
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            logger.error("Error verifying Razorpay signature for participant: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void markParticipantAsPaid(Long participantId, String paymentId) {
        try {
            System.out.println("🔄 Marking participant as paid - Participant ID: " + participantId + ", Payment ID: " + paymentId);

            // Find and update participant status to JOINED
            EventParticipant participant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("Participant not found with id: " + participantId));

            System.out.println("📋 Found participant for event: " + participant.getEvent().getEventName() + ", Current status: " + participant.getStatus());

            // Update participant status to JOINED
            participant.setStatus(EventParticipant.ParticipantStatus.JOINED);
            EventParticipant savedParticipant = participantRepository.save(participant);

            System.out.println("✅ Participant status updated to: " + savedParticipant.getStatus());

            // Update payment transaction
            PaymentTransaction transaction = paymentTransactionRepository.findByParticipantId(participantId)
                    .orElseThrow(() -> new RuntimeException("Payment transaction not found for participantId: " + participantId));

            transaction.setStatus(PaymentTransaction.PaymentStatus.SUCCESS);
            transaction.setReferenceId(paymentId);
            paymentTransactionRepository.save(transaction);

            System.out.println("✅ Payment transaction updated for participant: " + participantId);

            logger.info("Participant {} marked as paid with payment ID: {}", participantId, paymentId);

        } catch (Exception e) {
            System.err.println("❌ Error in markParticipantAsPaid: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to mark participant as paid: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void saveRazorpayOrderIdForParticipant(Long participantId, String razorpayOrderId) {
        PaymentTransaction transaction = paymentTransactionRepository.findByParticipantId(participantId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found for participantId: " + participantId));

        transaction.setReferenceId(razorpayOrderId);
        paymentTransactionRepository.save(transaction);

        logger.info("Saved Razorpay order ID {} for participant {}", razorpayOrderId, participantId);
    }

    @Override
    @Transactional
    public void createParticipantPaymentTransaction(Long participantId, double amount, Long userId) {
        // Find participant and user
        EventParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found with id: " + participantId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Create new payment transaction for participant
        PaymentTransaction transaction = PaymentTransaction.builder()
                .participant(participant)
                .user(user)
                .amount(BigDecimal.valueOf(amount))
                .status(PaymentTransaction.PaymentStatus.INITIATED)
                .build();

        paymentTransactionRepository.save(transaction);
        logger.info("Created payment transaction for participant {} with amount {}", participantId, amount);
    }
}