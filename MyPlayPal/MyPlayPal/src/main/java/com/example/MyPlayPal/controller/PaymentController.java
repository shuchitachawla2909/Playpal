package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.BookingDto;
import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.model.Booking;
import com.example.MyPlayPal.model.PaymentTransaction;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.PaymentTransactionRepository;
import com.example.MyPlayPal.service.*;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final SportService sportService;
    private final UserSportService userSportService;
    private final BookingService bookingService;
    private final CourtService courtService;
    private final CourtSlotService courtSlotService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public PaymentController(PaymentService paymentService, UserService userService, SportService sportService,
                             UserSportService userSportService, BookingService bookingService, CourtService courtService,
                             CourtSlotService courtSlotService) {

        this.paymentService = paymentService;
        this.userService = userService;
        this.sportService = sportService;
        this.userSportService = userSportService;
        this.bookingService = bookingService;
        this.courtService = courtService;
        this.courtSlotService = courtSlotService;
    }

    // ✅ Create Razorpay Order (called via JS fetch)
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) {
        System.out.println("⚡ Received payment request: " + data);

        try {
            // 🔹 Parse required fields from frontend
            double amount = Double.parseDouble(data.get("amount").toString());
            Long bookingId = Long.parseLong(data.get("bookingId").toString());  // ✅ required
            String receipt = "booking_" + bookingId;

            // 🔹 Create Razorpay order via PaymentService
            Order order = paymentService.createOrder(amount, receipt);

            // 🔹 Save the Razorpay Order ID in PaymentTransaction table for this booking
            paymentService.saveRazorpayOrderIdForBooking(bookingId, order.get("id"));

            // 🔹 Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("status", "created");

            System.out.println("✅ Order created successfully for booking " + bookingId + ": " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Payment order creation failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, Object> data) {
        try {
            String paymentId = data.get("paymentId").toString();
            String orderId = data.get("orderId").toString();
            String signature = data.get("signature").toString();
            Long bookingId = Long.parseLong(data.get("bookingId").toString());

            boolean verified = paymentService.verifySignature(orderId, paymentId, signature);

            if (verified) {
                paymentService.markBookingAsPaid(bookingId, paymentId);
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "failed", "message", "Signature mismatch"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }


    // ✅ Show Payment Page
    @GetMapping("/page")
    public String showPaymentPage(
            @RequestParam double amount,
            @RequestParam(required = false, defaultValue = "0") Long bookingId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String slots,
            @RequestParam(required = false) String venueName,
            @RequestParam(required = false) String courtName,
            @RequestParam(required = false) Long courtId,
            Model model) {

        model.addAttribute("venueName", venueName);
        model.addAttribute("courtName", courtName);
        model.addAttribute("bookingDate", date);
        model.addAttribute("selectedSlots", slots);
        model.addAttribute("totalAmount", amount);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("courtId", courtId);

        return "payment";
    }

    // ✅ Payment Success Page
    // ✅ Payment Success Page — confirm booking here
//    @GetMapping("/success")
//    public String paymentSuccess(
//            @RequestParam Long bookingId,
//            @RequestParam String paymentId,
//            Model model) {
//
//        BookingDto booking = bookingService.getById(bookingId);
//        CourtSlotDto courtSlot = courtSlotService.getCourtSlotById(booking.getSlotId());
//
//        // Confirm the booking (mark as PAID/CONFIRMED)
//        paymentService.markBookingAsPaid(bookingId, paymentId);
//
//        model.addAttribute("bookingId", bookingId);
//        model.addAttribute("paymentId", paymentId);
//
//        return "payment_success";
//    }

    @GetMapping("/success")
    @Transactional
    public String paymentSuccess(@RequestParam String paymentId,
                                 Model model,
                                 Principal principal) {

        // 1️⃣ Fetch PaymentTransaction using Razorpay payment ID
        PaymentTransaction txn = paymentTransactionRepository
                .findByReferenceId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment transaction not found for paymentId: " + paymentId));

        Booking bookingEntity = txn.getBooking();  // fetch Booking object
        if (bookingEntity == null || bookingEntity.getId() == null) {
            throw new RuntimeException("Booking missing in payment transaction for paymentId: " + paymentId);
        }
        Long bookingId = bookingEntity.getId();
        if (bookingId == null) {
            throw new RuntimeException("Booking ID is missing in payment transaction for paymentId: " + paymentId);
        }

        // 2️⃣ Confirm the booking (mark as PAID/CONFIRMED)
        paymentService.markBookingAsPaid(bookingId, paymentId);

        // 3️⃣ Fetch booking details
        BookingDto booking = bookingService.getById(bookingId);
        if (booking == null || booking.getSlotId() == null) {
            throw new RuntimeException("Invalid booking or missing slotId for bookingId: " + bookingId);
        }

        // 4️⃣ Fetch court slot
        CourtSlotDto courtSlot = courtSlotService.getCourtSlotById(booking.getSlotId());
        if (courtSlot == null || courtSlot.getCourtId() == null) {
            throw new RuntimeException("Invalid court slot or missing courtId for slotId: " + booking.getSlotId());
        }

        // 5️⃣ Fetch court
        CourtDto court = courtService.getById(courtSlot.getCourtId());
        if (court == null || court.getSportId() == null) {
            throw new RuntimeException("Invalid court or missing sportId for courtId: " + courtSlot.getCourtId());
        }

        Long sportId = court.getSportId();

        // 6️⃣ Get current logged-in user
        Long userId = userService.getUserIdByUsername(principal.getName());

        // 7️⃣ Add entry in user_sport table (if not exists)
        userSportService.addUserSport(userId, sportId);

        // 8️⃣ Add attributes for frontend
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("paymentId", paymentId);
        model.addAttribute("sportId", sportId);

        return "payment_success";
    }



}