package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtSlotRepository extends JpaRepository<CourtSlot, Long> {

    // --- For slot generation ---
    @Query("""
           SELECT cs FROM CourtSlot cs
           WHERE cs.court.id = :courtId
             AND cs.startTime < :end
             AND cs.endTime > :start
           """)
    List<CourtSlot> findOverlappingSlots(@Param("courtId") Long courtId,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("""
           SELECT cs FROM CourtSlot cs
             JOIN cs.court c
             JOIN c.venue v
           WHERE v.id = :venueId
             AND cs.startTime >= :from
             AND cs.endTime <= :to
           """)
    List<CourtSlot> findByVenueAndDateRange(@Param("venueId") Long venueId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    // --- Atomic AVAILABLE→BOOKED update ---
    @Modifying
    @Query("UPDATE CourtSlot cs SET cs.status = :newStatus " +
            "WHERE cs.id = :slotId AND cs.status = :expectedStatus")
    int updateStatusIfCurrent(@Param("slotId") Long slotId,
                              @Param("expectedStatus") CourtSlot.SlotStatus expectedStatus,
                              @Param("newStatus") CourtSlot.SlotStatus newStatus);

    // --- Existing methods you already had ---
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM CourtSlot s WHERE s.id = :id")
    Optional<CourtSlot> findByIdForUpdate(@Param("id") Long id);

    Optional<CourtSlot> findByCourtIdAndStartTimeAndEndTime(Long courtId,
                                                            LocalDateTime startTime,
                                                            LocalDateTime endTime);

    List<CourtSlot> findByCourtIdAndStartTimeBetween(Long courtId,
                                                     LocalDateTime from,
                                                     LocalDateTime to);

    List<CourtSlot> findByCourtIdAndStatus(Long courtId, CourtSlot.SlotStatus status);
    Optional<CourtSlot> findByCourtIdAndStartTime(Long courtId, LocalDateTime startTime);

    // CourtSlotRepository.java
    @Query("SELECT s FROM CourtSlot s WHERE s.court.id = :courtId " +
            "AND DATE(s.startTime) = :date AND s.status = 'AVAILABLE'")
    List<CourtSlot> findAvailableSlotsByCourtAndDate(@Param("courtId") Long courtId,
                                                     @Param("date") LocalDate date);
    boolean existsByCourtIdAndStartTimeAndEndTime(Long courtId, LocalDateTime startTime, LocalDateTime endTime);
    List<CourtSlot> findByCourtIdOrderByStartTimeAsc(Long courtId);


}
