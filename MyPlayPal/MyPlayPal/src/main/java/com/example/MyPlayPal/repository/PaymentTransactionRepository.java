package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByUserId(Long userId);

    Optional<PaymentTransaction> findByBookingId(Long bookingId);

    // ✅ Optional helper for updating after payment success
    Optional<PaymentTransaction> findByReferenceId(String referenceId);


    Optional<PaymentTransaction> findByEventId(Long eventId);
}
