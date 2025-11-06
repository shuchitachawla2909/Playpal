package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.EventResponse;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventPaymentService;
import com.example.MyPlayPal.service.EventService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/api/event-payment")
public class EventPaymentController {

    @Autowired
    private EventPaymentService eventPaymentService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventController eventController;
    // Inject EventController to call its methods

    // Helper methods to handle null values safely
    private String getStringOrEmpty(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    private String getStringOrDefault(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private Integer getIntegerOrDefault(Map<String, Object> map, String key, Integer defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Double getDoubleOrDefault(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private List<Long> getListOrEmpty(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            List<?> rawList = (List<?>) value;
            return rawList.stream()
                    .map(obj -> {
                        if (obj instanceof Integer) return ((Integer) obj).longValue();
                        if (obj instanceof Long) return (Long) obj;
                        return Long.parseLong(obj.toString());
                    })
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    /**
     * Show Payment Page for Event Creation (BEFORE event is created)
     */
    @GetMapping("/page")
    public String showEventPaymentPage(
            @RequestParam double amount,
            @RequestParam String eventName,
            @RequestParam String venueName,
            @RequestParam String courtName,
            @RequestParam String bookingDate,
            @RequestParam String selectedSlots,
            @RequestParam(required = false) Integer maxPlayers,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String skillLevelRequired,
            @RequestParam(required = false) Double entryFee,
            @RequestParam(required = false) List<Long> slotIds,
            Model model) {

        System.out.println("📋 Event Payment Page Parameters Received:");
        System.out.println("  - Event: " + eventName);
        System.out.println("  - Amount: " + amount);
        System.out.println("  - Venue: " + venueName);
        System.out.println("  - Court: " + courtName);
        System.out.println("  - Date: " + bookingDate);
        System.out.println("  - Slots: " + selectedSlots);
        System.out.println("  - Max Players: " + maxPlayers);
        System.out.println("  - Description: " + description);
        System.out.println("  - Skill Level: " + skillLevelRequired);
        System.out.println("  - Entry Fee: " + entryFee);
        System.out.println("  - Slot IDs: " + (slotIds != null ? slotIds : "null"));

        // Add all attributes to model
        model.addAttribute("totalAmount", amount);
        model.addAttribute("eventName", eventName);
        model.addAttribute("venueName", venueName);
        model.addAttribute("courtName", courtName);
        model.addAttribute("bookingDate", bookingDate);
        model.addAttribute("selectedSlots", selectedSlots);
        model.addAttribute("maxPlayers", maxPlayers != null ? maxPlayers : 0);
        model.addAttribute("description", description != null ? description : "");
        model.addAttribute("skillLevelRequired", skillLevelRequired != null ? skillLevelRequired : "All Levels");
        model.addAttribute("entryFee", entryFee != null ? entryFee : 0.0);
        model.addAttribute("slotIds", slotIds != null ? slotIds : List.of());
        model.addAttribute("paymentType", "EVENT");

        return "event-payment";
    }
    /**
     * Create Razorpay Order for Event (called via JS fetch)
     * This creates a temporary payment transaction without eventId
     */
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createEventOrder(@RequestBody Map<String, Object> data) {
        System.out.println("⚡ Received event payment request: " + data);

        try {
            // Parse required fields from frontend
            double amount = Double.parseDouble(data.get("amount").toString());
            String receipt = "event_pending_" + System.currentTimeMillis(); // Temporary receipt

            // Create Razorpay order
            Order order = eventPaymentService.createEventOrder(amount, receipt);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("status", "created");
            response.put("receipt", receipt);

            System.out.println("✅ Event order created successfully: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating event order: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Event payment order creation failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verify Event Payment and Create Event After Success
     */
    @PostMapping("/verify-and-create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyAndCreateEvent(@RequestBody Map<String, Object> data) {
        try {
            String paymentId = data.get("paymentId").toString();
            String orderId = data.get("orderId").toString();
            String signature = data.get("signature").toString();

            // Event data that was passed from the payment page
            Map<String, Object> eventData = (Map<String, Object>) data.get("eventData");

            System.out.println("🔐 Verifying payment and creating event...");
            System.out.println("📦 Received eventData: " + eventData);

            // Verify payment signature
            boolean verified = eventPaymentService.verifyEventSignature(orderId, paymentId, signature);

            if (verified) {
                // ✅ Create the request with ALL required fields and proper null handling
                Map<String, Object> createEventRequest = new HashMap<>();

                // Add all fields with null checks
                createEventRequest.put("eventName", getStringOrEmpty(eventData, "eventName"));
                createEventRequest.put("maxPlayers", getIntegerOrDefault(eventData, "maxPlayers", 4));
                createEventRequest.put("description", getStringOrEmpty(eventData, "description"));
                createEventRequest.put("skillLevelRequired", getStringOrDefault(eventData, "skillLevelRequired", "All Levels"));
                createEventRequest.put("entryFee", getDoubleOrDefault(eventData, "entryFee", 0.0));
                createEventRequest.put("slotIds", getListOrEmpty(eventData, "slotIds"));

                // Log the request for debugging
                System.out.println("📦 Creating event with data: " + createEventRequest);

                // Call your existing event creation endpoint
                EventResponse eventResponse = eventController.createEventWithVenue(createEventRequest);

                // Get the created event ID
                Long eventId = eventResponse.getId();

                // Now create the payment transaction for this event
                eventPaymentService.createEventPaymentTransaction(
                        eventId,
                        getDoubleOrDefault(eventData, "amount", 0.0),
                        getCurrentUserId()
                );

                // Mark event as paid
                System.out.println("✅ STEP 6: Marking event as paid...");
                eventPaymentService.markEventAsPaid(eventId, paymentId);

                System.out.println("✅ Event created and payment confirmed: " + eventId);

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "eventId", eventId,
                        "message", "Event created and payment confirmed successfully"
                ));

            } else {
                System.err.println("❌ Payment signature mismatch");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "failed", "message", "Payment signature mismatch"));
            }

        } catch (Exception e) {
            System.err.println("❌ Error in verify-and-create: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Payment Success Page for Events
     */
    @GetMapping("/success")
    public String eventPaymentSuccess(
            @RequestParam Long eventId,
            @RequestParam String paymentId,
            Model model) {

        model.addAttribute("eventId", eventId);
        model.addAttribute("paymentId", paymentId);

        return "event-payment-success";
    }

    /**
     * Helper method to get current user ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getId();
        } catch (Exception e) {
            System.err.println("❌ Error getting current user ID: " + e.getMessage());
            throw new RuntimeException("Unable to get current user ID");
        }
    }
}