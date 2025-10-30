package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.MyPlayPal.model.User;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ✅ Fetch all bookings of a specific user, ordered by booking date (most recent first)
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

    // ✅ Fetch all bookings for a specific slot
    List<Booking> findBySlotId(Long slotId);
    List<Booking> findByUser(User user);

}


