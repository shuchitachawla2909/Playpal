package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // ✅ Fetch all messages ordered by most recent first
    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    // ✅ Fetch all messages from a specific email (useful for user-specific queries)
    List<ContactMessage> findByEmailOrderByCreatedAtDesc(String email);

    // ✅ Search by subject keyword (optional enhancement)
    List<ContactMessage> findBySubjectContainingIgnoreCase(String keyword);
}
