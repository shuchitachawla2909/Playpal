package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateSportRequest;
import com.example.MyPlayPal.dto.SportDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.repository.SportRepository;
import com.example.MyPlayPal.service.SportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;

    public SportServiceImpl(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    @Override
    @Transactional
    public SportDto createSport(CreateSportRequest req) {
        Sport s = Sport.builder().sportname(req.getSportname()).build();
        Sport saved = sportRepository.save(s);
        return SportDto.builder().id(saved.getId()).sportname(saved.getSportname()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public SportDto getById(Long id) {
        Sport s = sportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        return SportDto.builder().id(s.getId()).sportname(s.getSportname()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportDto> listAll() {
        return sportRepository.findAll().stream()
                .map(s -> SportDto.builder()
                        .id(s.getId())
                        .sportname(s.getSportname())
                        .sportImageUrl(s.getSportImageUrl()) // new
                        .build())
                .collect(Collectors.toList());
    }

}

