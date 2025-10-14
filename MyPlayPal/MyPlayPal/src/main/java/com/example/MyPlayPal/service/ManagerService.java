package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateManagerRequest;
import com.example.MyPlayPal.dto.ManagerDto;

import java.util.List;

public interface ManagerService {
    ManagerDto createManager(CreateManagerRequest req);
    ManagerDto getById(Long id);
    List<ManagerDto> listAll();
}
