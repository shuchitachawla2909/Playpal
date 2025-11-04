package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.ContactMessageDto;
import com.example.MyPlayPal.dto.CreateContactMessageRequest;
import com.example.MyPlayPal.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contact")
public class ContactMessageController {

    @Autowired
    private ContactMessageService contactMessageService;

    // ✅ Serve the Contact Us page (for Thymeleaf)
    @GetMapping
    public String contactPage() {
        return "contact"; // corresponds to contact.html in templates
    }

    // ✅ Handle form submission (AJAX or normal form POST)
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody CreateContactMessageRequest request) {
        ContactMessageDto savedMessage = contactMessageService.createMessage(request);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Thank you for contacting us!",
                "data", savedMessage
        ));
    }


    // ✅ (Optional) Fetch all messages — for admin dashboard
    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<List<ContactMessageDto>> getAllMessages() {
        List<ContactMessageDto> messages = contactMessageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    // ✅ (Optional) Fetch messages by email
    @GetMapping("/messages/by-email")
    @ResponseBody
    public ResponseEntity<List<ContactMessageDto>> getMessagesByEmail(@RequestParam String email) {
        List<ContactMessageDto> messages = contactMessageService.getMessagesByEmail(email);
        return ResponseEntity.ok(messages);
    }
}
