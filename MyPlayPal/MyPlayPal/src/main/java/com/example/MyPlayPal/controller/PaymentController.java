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
@RequestMapping("/api/payment")   // ✅ keep this with leading slash
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ 1. Create Razorpay Order (called via JS fetch)
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) {

        System.out.println("⚡ Received payment request: " + data);

        try {
            double amount = Double.parseDouble(data.get("amount").toString());
            String receipt = "booking_" + data.get("bookingId");

            Order order = paymentService.createOrder(amount, receipt);

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment order creation failed"));
        }
    }

    // ✅ 2. Show Payment Page
    @GetMapping("/page")
    public String showPaymentPage(
            @RequestParam double amount,
            @RequestParam Long bookingId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String slots,
            @RequestParam(required = false) String venueName,
            @RequestParam(required = false) String courtName,
            Model model) {

        model.addAttribute("venueName", venueName);
        model.addAttribute("courtName", courtName);
        model.addAttribute("bookingDate", date);
        model.addAttribute("selectedSlots", slots);
        model.addAttribute("totalAmount", amount);
        model.addAttribute("bookingId", bookingId);

        return "payment"; // ✅ loads payment.html
    }


    // ✅ 3. Payment Success Page
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Long bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        return "payment_success";
    }
}
