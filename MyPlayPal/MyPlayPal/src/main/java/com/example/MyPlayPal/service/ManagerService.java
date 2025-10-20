package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.ManagerDto;
import com.example.MyPlayPal.dto.ManagerSignupRequest;

import java.util.List;

public interface ManagerService {
    ManagerDto createManager(ManagerSignupRequest req);
    ManagerDto getById(Long id);
    List<ManagerDto> listAll();
}
