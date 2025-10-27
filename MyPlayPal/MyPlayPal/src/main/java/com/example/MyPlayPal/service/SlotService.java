package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.SlotTemplateRequest;

public interface SlotService {
    void generateSlotsForCourt(SlotTemplateRequest request);
}
