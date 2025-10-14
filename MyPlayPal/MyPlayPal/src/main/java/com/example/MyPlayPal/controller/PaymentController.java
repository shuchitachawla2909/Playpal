package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.PaymentDto;
import com.example.MyPlayPal.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<PaymentDto>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.listByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }
}
