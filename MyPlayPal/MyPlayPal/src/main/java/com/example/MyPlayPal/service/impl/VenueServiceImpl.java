package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.model.Court;
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

    public VenueServiceImpl(
            VenueRepository venueRepository,
            ManagerRepository managerRepository,
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
                        .venueName(v.getVenueName())
                        .city(v.getCity())
                        .rating(v.getRating())
                        .venueImageUrl(v.getVenueImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueDto createVenue(CreateVenueRequest req) {
        Manager m = managerRepository.findById(req.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Venue v = Venue.builder()
                .venueName(req.getVenueName())
                .city(req.getCity())
                .state(req.getState())
                .pinCode(req.getPinCode())
                .manager(m)
                .rating(0.0)
                .build();
        Venue saved = venueRepository.save(v);
        return VenueDto.builder()
                .id(saved.getId())
                .venueName(saved.getVenueName())
                .street(saved.getStreet())
                .city(saved.getCity())
                .state(saved.getState())
                .pinCode(saved.getPinCode()) // ✅ CORRECTED
                .managerId(m.getId())
                .rating(saved.getRating())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VenueDto getById(Long id) {
        Venue v = venueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        Manager m = v.getManager();
        List<Court> courts = v.getCourts(); // Assuming a getCourts() method exists in Venue entity

        return VenueDto.builder()
                .id(v.getId())
                .venueName(v.getVenueName()) // ✅ CORRECTED
                .street(v.getStreet())
                .city(v.getCity())
                .state(v.getState())
                .pinCode(v.getPinCode()) // ✅ CORRECTED
                .managerId(m == null ? null : m.getId())
                .rating(v.getRating())

                // ⭐ NEW FIELDS REQUIRED BY FRONTEND:
                .managerName(m == null ? "N/A" : m.getName())
                .managerContact(m == null ? "N/A" : m.getContact()) // Assuming getName()/getContact() exist on Manager entity

                .venueImageUrl(v.getVenueImageUrl())
                // ⭐ COURT LIST MAPPING: Populating the list of courts for venue-detail.html
                .courts(courts.stream().map(this::mapCourtToDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> listByCity(String city) {
        return venueRepository.findByCity(city).stream()
                .map(v -> VenueDto.builder()
                        .id(v.getId())
                        .venueName(v.getVenueName()) // ✅ CORRECTED
                        .street(v.getStreet())
                        .city(v.getCity())
                        .state(v.getState())
                        .pinCode(v.getPinCode()) // ✅ CORRECTED
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
                .courtName(court.getSport().getSportName()) // ✅ CORRECTED: uses sportName/courtName
                .hourlyRate(court.getHourlyRate())
                .venueId(court.getVenue().getId())
                .sportId(court.getSport().getId())
                .isBookable(court.getIsBookable())
                // NOTE: If CourtDto needs sportImageUrl, you must map it here:
                // .sportImageUrl(court.getSport().getSportImageUrl())

                // ⭐ FIX: Map the image URL from the associated Sport entity
                .sportImageUrl(court.getSport().getSportImageUrl())

                .build();
    }
}