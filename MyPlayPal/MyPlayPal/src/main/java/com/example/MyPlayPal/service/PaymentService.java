package com.example.MyPlayPal.service;


import com.example.MyPlayPal.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> listByUser(Long userId);
    PaymentDto getById(Long id);
}
