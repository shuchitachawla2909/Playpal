package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
// FIX 1: Import the @Query annotation
import org.springframework.data.jpa.repository.Query; // <--- ADD THIS LINE
import org.springframework.stereotype.Repository;

import java.util.List;
// FIX 2: Import the Optional class
import java.util.Optional; // <--- ADD THIS LINE

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByVenueIdOrderByCreatedAtDesc(Long venueId);

    boolean existsByVenueIdAndUserId(Long venueId, Long userId);

    // This method uses the two symbols you needed to import: @Query and Optional
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.venue.id = :venueId")
    Optional<Double> findAverageRatingByVenueId(Long venueId);

    long countByVenueId(Long venueId);
}