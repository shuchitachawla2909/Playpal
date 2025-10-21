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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")

public class BookingController {
    @Autowired
    private BookingService bookingService;

    // Assuming this service exists to map Principal (username) to Long ID
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(Principal principal, @Valid @RequestBody CreateBookingRequest request) {
        // ⭐ Securely retrieve userId from Principal and set it in the request DTO
        // NOTE: You must implement userService.getUserIdByUsername()
        Long currentUserId = userService.getUserIdByUsername(principal.getName());
        request.setUserId(currentUserId);

        // You may want to return 201 Created status
        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        // NOTE: In a secure app, you must check if the authenticated user owns this booking.
        return ResponseEntity.ok(bookingService.getById(id));
    }

    // ⭐ FIX 1: Secure endpoint for listing current user's bookings
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDto>> listByUser(Principal principal) {
        // Deriving ID from Principal, assuming bookingService can now handle Long ID
        Long userId = userService.getUserIdByUsername(principal.getName());
        return ResponseEntity.ok(bookingService.listByUser(userId));
    }

    // Removed old /by-user/{userId} endpoint to enforce security

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        // Implement cancel in service if available
        return ResponseEntity.ok("Cancel booking endpoint not implemented");
    }
}