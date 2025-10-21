package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.repository.ManagerRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import com.example.MyPlayPal.repository.CourtRepository;
import com.example.MyPlayPal.service.VenueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final ManagerRepository managerRepository;
    private final CourtRepository courtRepository;

    public VenueServiceImpl(VenueRepository venueRepository, ManagerRepository managerRepository,
                            CourtRepository courtRepository) {
        this.venueRepository = venueRepository;
        this.managerRepository = managerRepository;
        this.courtRepository = courtRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> listAllVenues() {
        return venueRepository.findAll().stream()
                .map(v -> VenueDto.builder()
                        .id(v.getId())
                        .venuename(v.getVenuename())
                        .city(v.getCity())
                        .rating(v.getRating())
                        .venueImageUrl(v.getVenueImageUrl())  // must be correctly set
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueDto createVenue(CreateVenueRequest req) {
        Manager m = managerRepository.findById(req.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Venue v = Venue.builder()
                .venuename(req.getVenuename())
                .street(req.getStreet())
                .city(req.getCity())
                .state(req.getState())
                .pincode(req.getPincode())
                .manager(m)
                .rating(0.0)
                .build();
        Venue saved = venueRepository.save(v);
        return VenueDto.builder()
                .id(saved.getId())
                .venuename(saved.getVenuename())
                .street(saved.getStreet())
                .city(saved.getCity())
                .state(saved.getState())
                .pincode(saved.getPincode())
                .managerId(m.getId())
                .rating(saved.getRating())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VenueDto getById(Long id) {
        Venue v = venueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        return VenueDto.builder()
                .id(v.getId())
                .venuename(v.getVenuename())
                .street(v.getStreet())
                .city(v.getCity())
                .state(v.getState())
                .pincode(v.getPincode())
                .managerId(v.getManager() == null ? null : v.getManager().getId())
                .rating(v.getRating())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> listByCity(String city) {
        return venueRepository.findByCity(city).stream()
                .map(v -> VenueDto.builder()
                        .id(v.getId())
                        .venuename(v.getVenuename())
                        .street(v.getStreet())
                        .city(v.getCity())
                        .state(v.getState())
                        .pincode(v.getPincode())
                        .managerId(v.getManager() == null ? null : v.getManager().getId())
                        .rating(v.getRating())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourtDto getCourtById(Long courtId) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with ID: " + courtId));

        return mapCourtToDto(court);
    }

    /**
     * Helper method to map Court Entity to Court DTO.
     */
    private CourtDto mapCourtToDto(Court court) {
        return CourtDto.builder()
                .id(court.getId())
                .courtname(court.getSport().getSportname())
                .hourlyRate(court.getHourlyRate())
                .venueId(court.getVenue().getId())
                .sportId(court.getSport().getId())
                .isBookable(court.getIsBookable())
                // NOTE: If CourtDto needs sportImageUrl, you must map it here:
//                 .sportImageUrl(court.getSport().getSportImageUrl())

                .build();
    }
}