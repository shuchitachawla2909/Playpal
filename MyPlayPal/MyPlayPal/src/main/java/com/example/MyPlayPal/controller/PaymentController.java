package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.service.PaymentService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ Create Razorpay Order (called via JS fetch)
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) {
        System.out.println("⚡ Received payment request: " + data);

        try {
            double amount = Double.parseDouble(data.get("amount").toString());
            String receipt = "booking_temp_" + System.currentTimeMillis();

            Order order = paymentService.createOrder(amount, receipt);

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("status", "created");

            System.out.println("✅ Order created successfully: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Payment order creation failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
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
    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) String paymentId,
            Model model) {

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("paymentId", paymentId);

        return "payment_success";
    }
}