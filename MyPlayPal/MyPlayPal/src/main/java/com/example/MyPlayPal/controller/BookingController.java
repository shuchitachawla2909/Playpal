package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.BookingDto;
import com.example.MyPlayPal.dto.CreateBookingRequest;
import com.example.MyPlayPal.service.BookingService;
import com.example.MyPlayPal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.MyPlayPal.model.User;
import java.security.Principal;
import com.example.MyPlayPal.repository.UserRepository;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(Principal principal,
                                                    @Valid @RequestBody CreateBookingRequest request) {
        // Securely retrieve userId from Principal and set it in the request DTO
        Long currentUserId = userService.getUserIdByUsername(principal.getName());
        request.setUserId(currentUserId);

        BookingDto createdBooking = bookingService.createBooking(request);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id, Principal principal) {
        Long currentUserId = userService.getUserIdByUsername(principal.getName());
        BookingDto booking = bookingService.getById(id);

        // Security check: ensure the current user owns this booking
        if (!booking.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(booking);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDto>> getMyBookings(Principal principal) {
        // Step 1: Get the username from the logged-in user
        String username = principal.getName();

        // Step 2: Fetch the User entity to get the ID
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 3: Use the user ID to fetch bookings
        List<BookingDto> bookings = bookingService.getBookingsByUserId(user.getId());

        // Step 4: Return the list as the response
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable Long id, Principal principal) {
        // 1️⃣ Get the logged-in user ID
        Long currentUserId = userService.getUserIdByUsername(principal.getName());

        // 2️⃣ Call service method
        BookingDto cancelledBooking = bookingService.cancelBooking(id, currentUserId);

        // 3️⃣ Return success response
        return ResponseEntity.ok(cancelledBooking);
    }

}

