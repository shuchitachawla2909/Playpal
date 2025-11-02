package com.example.MyPlayPal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/event-payment")
public class EventPaymentController {

    /**
     * Show Payment Page for Event Creation
     * This page displays event details and venue booking cost before payment
     */
    @GetMapping("/page")
    public String showEventPaymentPage(
            @RequestParam double amount,
            @RequestParam String eventName,
            @RequestParam String venueName,
            @RequestParam String courtName,
            @RequestParam String bookingDate,
            @RequestParam String selectedSlots,
            @RequestParam(required = false) String eventData, // JSON string of event data
            Model model) {

        // Add all attributes to model for Thymeleaf template
        model.addAttribute("totalAmount", amount);
        model.addAttribute("eventName", eventName);
        model.addAttribute("venueName", venueName);
        model.addAttribute("courtName", courtName);
        model.addAttribute("bookingDate", bookingDate);
        model.addAttribute("selectedSlots", selectedSlots);
        model.addAttribute("eventData", eventData);
        model.addAttribute("paymentType", "EVENT"); // Distinguish from regular booking

        return "event-payment"; // Returns event-payment.html
    }

    /**
     * Payment Success Page for Events
     * Redirects here after successful payment
     */
    @GetMapping("/success")
    public String eventPaymentSuccess(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) Long eventId,
            Model model) {

        model.addAttribute("paymentId", paymentId);
        model.addAttribute("eventId", eventId);

        return "event-payment-success"; // Returns event-payment-success.html
    }
}