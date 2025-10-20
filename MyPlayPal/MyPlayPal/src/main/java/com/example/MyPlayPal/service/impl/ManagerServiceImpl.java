package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.ManagerDto;
import com.example.MyPlayPal.dto.ManagerSignupRequest;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.repository.ManagerRepository;
import com.example.MyPlayPal.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository repo;
    private final PasswordEncoder passwordEncoder;

    public ManagerServiceImpl(ManagerRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ManagerDto createManager(@Valid ManagerSignupRequest req) {
        Manager m = Manager.builder()
                .managername(req.getManagername().trim())
                .contact(req.getContact().trim())
                .email(req.getEmail().trim())
                .password(passwordEncoder.encode(req.getPassword().trim()))
                .build();
        Manager saved = repo.save(m);
        return ManagerDto.builder().id(saved.getId()).managername(saved.getManagername())
                .contact(saved.getContact()).email(saved.getEmail()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ManagerDto getById(Long id) {
        Manager m = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        return ManagerDto.builder().id(m.getId()).managername(m.getManagername())
                .contact(m.getContact()).email(m.getEmail()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerDto> listAll() {
        return repo.findAll().stream()
                .map(m -> ManagerDto.builder().id(m.getId()).managername(m.getManagername())
                        .contact(m.getContact()).email(m.getEmail()).build())
                .collect(Collectors.toList());
    }
}

