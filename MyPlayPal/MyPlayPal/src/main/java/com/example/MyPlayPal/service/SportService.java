package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateSportRequest;
import com.example.MyPlayPal.dto.SportDto;

import java.util.List;

public interface SportService {
    SportDto createSport(CreateSportRequest req);
    SportDto getById(Long id);
    List<SportDto> listAll();
}
