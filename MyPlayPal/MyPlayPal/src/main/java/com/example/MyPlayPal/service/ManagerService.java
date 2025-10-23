package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.ManagerDto;
import com.example.MyPlayPal.dto.ManagerSignupRequest;
import com.example.MyPlayPal.model.Manager;
import jakarta.validation.Valid;

import java.util.List;

public interface ManagerService {
    ManagerDto createManager(@Valid ManagerSignupRequest req);
    ManagerDto getById(Long id);
    List<ManagerDto> listAll();
    Manager getLoggedInManager();
}
