package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateCourtRequest;
import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.repository.CourtRepository;
import com.example.MyPlayPal.repository.SportRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import com.example.MyPlayPal.service.CourtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final VenueRepository venueRepository;
    private final SportRepository sportRepository;

    public CourtServiceImpl(CourtRepository courtRepository, VenueRepository venueRepository, SportRepository sportRepository) {
        this.courtRepository = courtRepository;
        this.venueRepository = venueRepository;
        this.sportRepository = sportRepository;
    }

    @Override
    @Transactional
    public CourtDto createCourt(CreateCourtRequest req) {
        Venue venue = venueRepository.findById(req.getVenueId()).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        Sport sport = sportRepository.findById(req.getSportId()).orElseThrow(() -> new ResourceNotFoundException("Sport not found"));

        Court c = Court.builder()
                .courtName(req.getCourtName())
                .venue(venue)
                .sport(sport)
                .hourlyRate(req.getHourlyRate())
                .isBookable(req.getIsBookable())
                .build();

        Court saved = courtRepository.save(c);
        return CourtDto.builder()
                .id(saved.getId())
                .courtName(saved.getCourtName())
                .venueId(venue.getId())
                .sportId(sport.getId())
                .hourlyRate(saved.getHourlyRate())
                .isBookable(saved.getIsBookable())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CourtDto getById(Long id) {
        Court c = courtRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Court not found"));
        return CourtDto.builder()
                .id(c.getId())
                .courtName(c.getCourtName())
                .venueId(c.getVenue() == null ? null : c.getVenue().getId())
                .sportId(c.getSport() == null ? null : c.getSport().getId())
                .hourlyRate(c.getHourlyRate())
                .isBookable(c.getIsBookable())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtDto> listByVenue(Long venueId) {
        return courtRepository.findByVenueId(venueId).stream()
                .map(c -> CourtDto.builder()
                        .id(c.getId())
                        .courtName(c.getCourtName())
                        .venueId(c.getVenue() == null ? null : c.getVenue().getId())
                        .sportId(c.getSport() == null ? null : c.getSport().getId())
                        .hourlyRate(c.getHourlyRate())
                        .isBookable(c.getIsBookable())
                        .build())
                .collect(Collectors.toList());
    }
}
