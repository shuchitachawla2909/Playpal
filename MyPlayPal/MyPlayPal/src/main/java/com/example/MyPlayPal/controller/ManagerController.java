package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.ManagerDto;
import com.example.MyPlayPal.dto.ManagerSignupRequest; // Standardized DTO
import com.example.MyPlayPal.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @GetMapping
    public ResponseEntity<List<ManagerDto>> listManagers() {
        return ResponseEntity.ok(managerService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagerDto> getManager(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getById(id));
    }

    @PostMapping
    // ✅ FIX: Changed expected request type from CreateManagerRequest to ManagerSignupRequest.
    public ResponseEntity<ManagerDto> createManager(@Valid @RequestBody ManagerSignupRequest request) {
        return ResponseEntity.ok(managerService.createManager(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteManager(@PathVariable Long id) {
        return ResponseEntity.ok("Delete manager endpoint not implemented on service");
    }
}
