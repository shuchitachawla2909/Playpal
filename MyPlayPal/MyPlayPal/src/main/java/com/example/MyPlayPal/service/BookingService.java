package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.BookingDto;
import com.example.MyPlayPal.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingRequest req);

    BookingDto getById(Long id);

    List<BookingDto> listByUser(Long userId);

    List<BookingDto> getBookingsByUserId(Long userId);

    BookingDto cancelBooking(Long bookingId, Long userId);

}
