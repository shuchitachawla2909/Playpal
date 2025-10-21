package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.CourtSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtSlotRepository extends JpaRepository<CourtSlot, Long> {

    // Pessimistic lock for safe booking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from CourtSlot s where s.id = :id")
    Optional<CourtSlot> findByIdForUpdate(@Param("id") Long id);

    Optional<CourtSlot> findByCourtIdAndStartTimeAndEndTime(Long courtId, LocalDateTime startTime, LocalDateTime endTime);
    List<CourtSlot> findByCourtIdAndStartTimeBetween(Long courtId, LocalDateTime from, LocalDateTime to);

    List<CourtSlot> findByCourtIdAndStatus(Long courtId, CourtSlot.SlotStatus status);
}
