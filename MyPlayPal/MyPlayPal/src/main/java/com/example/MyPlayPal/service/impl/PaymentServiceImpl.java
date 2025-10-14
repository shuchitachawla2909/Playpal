package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.PaymentDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.PaymentTransaction;
import com.example.MyPlayPal.repository.PaymentTransactionRepository;
import com.example.MyPlayPal.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository repo;

    public PaymentServiceImpl(PaymentTransactionRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> listByUser(Long userId) {
        return repo.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getById(Long id) {
        PaymentTransaction p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return toDto(p);
    }

    private PaymentDto toDto(PaymentTransaction p) {
        return PaymentDto.builder()
                .id(p.getId())
                .userId(p.getUser() == null ? null : p.getUser().getId())
                .bookingId(p.getBooking() == null ? null : p.getBooking().getId())
                .amount(p.getAmount())
                .timestamp(p.getTimestamp())
                .status(p.getStatus().name())
                .build();
    }
}

