package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.*;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.*;
import com.example.MyPlayPal.repository.*;
import com.example.MyPlayPal.service.VenueService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final ManagerRepository managerRepository;
    private final CourtRepository courtRepository;
    private final SportRepository sportRepository;
    private final ReviewRepository reviewRepository; // ✅ Added if not already present

    public VenueServiceImpl(VenueRepository venueRepository,
                            ManagerRepository managerRepository,
                            CourtRepository courtRepository,
                            SportRepository sportRepository,
                            ReviewRepository reviewRepository) {
        this.venueRepository = venueRepository;
        this.managerRepository = managerRepository;
        this.courtRepository = courtRepository;
        this.sportRepository = sportRepository;
        this.reviewRepository = reviewRepository;
    }

    // ---------------- Existing methods ----------------

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> listAllVenues() {
        return venueRepository.findAll().stream()
                .map(v -> VenueDto.builder()
                        .id(v.getId())
                        .venuename(v.getVenuename())
                        .city(v.getCity())
                        .rating(v.getRating())
                        .venueImageUrl(v.getVenueImageUrl())
                        .reviewCount((int) reviewRepository.countByVenueId(v.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueDto createVenue(CreateVenueRequest request) {
        Manager manager = getLoggedInManager();

        Venue v = Venue.builder()
                .venuename(request.getVenuename())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .venueImageUrl(request.getVenueImageUrl())
                .manager(manager)
                .rating(0.0)
                .build();

        Venue saved = venueRepository.save(v);

        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueDto getById(Long id) {
        Venue v = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        return convertToDto(v);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> listByCity(String city) {
        return venueRepository.findByCity(city).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourtDto getCourtById(Long courtId) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with ID: " + courtId));
        return mapCourtToDto(court);
    }

    // ---------------- Manager-specific methods ----------------

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> listVenuesForCurrentManager() {
        Manager manager = getLoggedInManager();
        return venueRepository.findByManager(manager).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addCourtToVenue(Long venueId, CreateCourtRequest request) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        Court court = new Court();
        court.setCourtname(request.getCourtname());
        court.setHourlyRate(request.getHourlyRate());
        court.setIsBookable(request.getIsBookable());
        court.setVenue(venue);

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        court.setSport(sport);

        courtRepository.save(court);
    }

    // ---------------- Update and Delete ----------------

    @Override
    public VenueDto updateVenue(Long venueId, VenueDto updatedVenue) {
        Venue existing = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        existing.setVenuename(updatedVenue.getVenuename());
        existing.setStreet(updatedVenue.getStreet());
        existing.setCity(updatedVenue.getCity());
        existing.setState(updatedVenue.getState());
        existing.setPincode(updatedVenue.getPincode());
        existing.setVenueImageUrl(updatedVenue.getVenueImageUrl());

        Venue saved = venueRepository.save(existing);
        return convertToDto(saved);
    }

    @Override
    public void deleteVenue(Long venueId) {
        if (!venueRepository.existsById(venueId)) {
            throw new ResourceNotFoundException("Venue not found");
        }
        venueRepository.deleteById(venueId);
    }

    // ---------------- Helper mapping methods ----------------

    private CourtDto mapCourtToDto(Court court) {
        return CourtDto.builder()
                .id(court.getId())
                .courtname(court.getCourtname())
                .hourlyRate(court.getHourlyRate())
                .venueId(court.getVenue().getId())
                .sportId(court.getSport() != null ? court.getSport().getId() : null)
                .isBookable(court.getIsBookable())
                .sportImageUrl(court.getSport() != null ? court.getSport().getSportImageUrl() : null)
                .build();
    }

    private List<CourtDto> mapCourtsToDto(List<Court> courts) {
        return courts.stream().map(this::mapCourtToDto).toList();
    }

    private List<ReviewDto> mapReviewsToDto(List<Review> reviews) {
        return reviews.stream()
                .map(r -> ReviewDto.builder()
                        .id(r.getId())
                        .comment(r.getComment())
                        .rating(r.getRating())
                        .userId(r.getUser() != null ? r.getUser().getId() : null)
                        .userName(r.getUser() != null ? r.getUser().getUsername() : "Anonymous")
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }


    private Manager getLoggedInManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return managerRepository.findByManagername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in manager not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportDto> getAllSports() {
        return sportRepository.findAll().stream()
                .map(s -> SportDto.builder()
                        .id(s.getId())
                        .sportname(s.getSportname())
                        .sportImageUrl(s.getSportImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    // ✅ Central conversion function — fixes both Court and Review mapping issues
    private VenueDto convertToDto(Venue v) {
        return VenueDto.builder()
                .id(v.getId())
                .venuename(v.getVenuename())
                .street(v.getStreet())
                .city(v.getCity())
                .state(v.getState())
                .pincode(v.getPincode())
                .venueImageUrl(v.getVenueImageUrl())
                .rating(v.getRating())
                .managerId(v.getManager() != null ? v.getManager().getId() : null)
                .courts(v.getCourts() != null ? mapCourtsToDto(v.getCourts()) : List.of())
                .reviews(v.getReviews() != null ? mapReviewsToDto(v.getReviews()) : List.of())
                .reviewCount((int) reviewRepository.countByVenueId(v.getId()))
                .build();
    }

    @Override
    @Transactional
    public void updateCourt(Long courtId, CourtDto updatedCourt) {
        Court existing = courtRepository.findById(courtId)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found"));

        existing.setCourtname(updatedCourt.getCourtname());
        existing.setHourlyRate(updatedCourt.getHourlyRate());
        existing.setIsBookable(updatedCourt.getIsBookable());

        // Update sport
        Sport sport = sportRepository.findById(updatedCourt.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        existing.setSport(sport);

        courtRepository.save(existing);
    }

}