package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.ParticipantPaymentService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;



@Controller
@RequestMapping("/api/participant-payment")
public class ParticipantPaymentController {

    @Autowired
    private ParticipantPaymentService participantPaymentService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    /**
     * Show Payment Page for Event Participation
     */
    @GetMapping("/page")
    @Transactional(readOnly = true)
    public String showParticipantPaymentPage(
            @RequestParam Long eventId,
            Model model) {

        try {
            System.out.println("🔄 Loading payment page for event ID: " + eventId);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Try to find event with proper error handling
            Optional<Event> eventOpt = eventRepository.findById(eventId);

            if (eventOpt.isEmpty()) {
                model.addAttribute("error", "Event not found with ID: " + eventId);
                return "error";
            }

            Event event = eventOpt.get();
            System.out.println("✅ Found event: " + event.getEventName());

            // Force load venue within transactional context
            if (event.getVenue() != null) {
                try {
                    // This forces Hibernate to load the venue
                    String venueName = event.getVenue().getVenuename();
                    System.out.println("✅ Venue loaded: " + venueName);
                } catch (Exception e) {
                    System.out.println("⚠️ Could not load venue, but continuing...");
                }
            }

            // Check if user is already a participant
            Optional<EventParticipant> existingParticipant = participantRepository.findByEventAndUser(event, currentUser);
            if (existingParticipant.isPresent()) {
                model.addAttribute("error", "You are already registered for this event!");
                // Still pass the event to show details
                model.addAttribute("event", event);
                model.addAttribute("currentUser", currentUser);
                return "participant-payment";
            }

            // Check if event has entry fee
            if (event.getEntryFee() == null || event.getEntryFee().doubleValue() <= 0) {
                model.addAttribute("error", "This event is free to join!");
                model.addAttribute("event", event);
                model.addAttribute("currentUser", currentUser);
                return "participant-payment";
            }

            System.out.println("✅ Adding event to model: " + event.getEventName());
            model.addAttribute("event", event);
            model.addAttribute("entryFee", event.getEntryFee());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("paymentType", "PARTICIPANT");

            return "participant-payment";

        } catch (Exception e) {
            System.err.println("❌ Error loading payment page: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading payment page: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Create Razorpay Order for Participant (called via JS fetch)
     */
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createParticipantOrder(@RequestBody Map<String, Object> data) {
        System.out.println("⚡ Received participant payment request: " + data);

        try {
            // Parse required fields from frontend
            double amount = Double.parseDouble(data.get("amount").toString());
            Long eventId = Long.parseLong(data.get("eventId").toString());
            Long userId = Long.parseLong(data.get("userId").toString());
            String receipt = "participant_" + eventId + "_" + userId + "_" + System.currentTimeMillis();

            // Get event and user details
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create participant with PENDING status
            EventParticipant participant = EventParticipant.builder()
                    .event(event)
                    .user(user)
                    .status(EventParticipant.ParticipantStatus.PENDING)
                    .build();

            EventParticipant savedParticipant = participantRepository.save(participant);
            Long participantId = savedParticipant.getId();

            // Create payment transaction
            participantPaymentService.createParticipantPaymentTransaction(participantId, amount, userId);

            // Create Razorpay order
            Order order = participantPaymentService.createParticipantOrder(amount, receipt);

            // Save the Razorpay Order ID
            participantPaymentService.saveRazorpayOrderIdForParticipant(participantId, order.get("id"));

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("status", "created");
            response.put("participantId", participantId);

            System.out.println("✅ Participant order created successfully for event: " + event.getEventName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating participant order: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Participant payment order creation failed");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verify Participant Payment
     */
    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyParticipantPayment(@RequestBody Map<String, Object> data) {
        try {
            String paymentId = data.get("paymentId").toString();
            String orderId = data.get("orderId").toString();
            String signature = data.get("signature").toString();
            Long participantId = Long.parseLong(data.get("participantId").toString());

            System.out.println("🔐 Verifying participant payment for participant: " + participantId);

            boolean verified = participantPaymentService.verifyParticipantSignature(orderId, paymentId, signature);

            if (verified) {
                // Mark participant as paid
                participantPaymentService.markParticipantAsPaid(participantId, paymentId);

                // Get participant details for response
                EventParticipant participant = participantRepository.findById(participantId)
                        .orElseThrow(() -> new RuntimeException("Participant not found"));

                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "participantId", participantId,
                        "eventId", participant.getEvent().getId(),
                        "message", "Payment successful! You are now registered for the event."
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "failed", "message", "Payment signature mismatch"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Payment Success Page for Participants
     */
    /**
     * Payment Success Page for Participants
     */
    /**
     * Payment Success Page for Participants
     */
    @GetMapping("/success")
    @Transactional(readOnly = true)
    public String participantPaymentSuccess(
            @RequestParam Long participantId,
            @RequestParam String paymentId,
            Model model) {

        try {
            System.out.println("🔄 Loading success page for participant: " + participantId);

            // Load participant with event
            EventParticipant participant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));

            System.out.println("✅ Found participant for event ID: " + participant.getEvent().getId());

            // Load event with all necessary relationships
            Event event = eventRepository.findById(participant.getEvent().getId())
                    .orElseThrow(() -> new RuntimeException("Event not found with ID: " + participant.getEvent().getId()));

            System.out.println("✅ Event loaded: " + event.getEventName());

            // DEBUG: Check what data is available
            System.out.println("🔍 DEBUG - Event details:");
            System.out.println("   - Event Name: " + event.getEventName());
            System.out.println("   - Entry Fee: " + event.getEntryFee());
            System.out.println("   - Booking Date: " + event.getBookingDate());
            System.out.println("   - Venue: " + (event.getVenue() != null ? event.getVenue() : "NULL"));

            if (event.getVenue() != null) {
                System.out.println("   - Venue ID: " + event.getVenue().getId());
                System.out.println("   - Venue Name: " + event.getVenue().getVenuename());
                System.out.println("   - Venue Street: " + event.getVenue().getStreet());
                System.out.println("   - Venue City: " + event.getVenue().getCity());
            } else {
                System.out.println("   ⚠️ Venue is NULL for this event");
            }

            // Force load venue details
            if (event.getVenue() != null) {
                try {
                    // Access venue properties to force Hibernate to load them
                    String venueName = event.getVenue().getVenuename();
                    String street = event.getVenue().getStreet();
                    String city = event.getVenue().getCity();
                    String state = event.getVenue().getState();

                    System.out.println("✅ Venue details loaded:");
                    System.out.println("   - Name: " + venueName);
                    System.out.println("   - Location: " + street + ", " + city + ", " + state);
                } catch (Exception e) {
                    System.err.println("❌ Error loading venue details: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            model.addAttribute("participant", participant);
            model.addAttribute("event", event);
            model.addAttribute("paymentId", paymentId);

            System.out.println("🎉 Success page ready to render");

            return "participant-payment-success";

        } catch (Exception e) {
            System.err.println("❌ Error loading success page: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading success page: " + e.getMessage());
            return "error";
        }
    }
}