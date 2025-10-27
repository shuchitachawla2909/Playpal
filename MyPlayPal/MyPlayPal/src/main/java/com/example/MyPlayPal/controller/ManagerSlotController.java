package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.SlotTemplateRequest;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.repository.CourtRepository;
import com.example.MyPlayPal.service.SlotService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager/slots")
public class ManagerSlotController {

    private final SlotService slotService;
    private final CourtRepository courtRepo;

    public ManagerSlotController(SlotService slotService, CourtRepository courtRepo) {
        this.slotService = slotService;
        this.courtRepo = courtRepo;
    }

    // ✅ Show slot creation form
    @GetMapping("/create-template/{courtId}")
    public String showCreateSlotForm(@PathVariable Long courtId, Model model) {
        Court court = courtRepo.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Court not found"));

        // Optional: Security check to ensure manager owns this court
        // if (!court.getManager().getId().equals(currentManagerId)) throw new AccessDeniedException("Unauthorized");

        SlotTemplateRequest request = new SlotTemplateRequest();
        request.setCourtId(courtId);

        model.addAttribute("request", request);
        model.addAttribute("court", court);
        return "create-slot-template";
    }

    // ✅ Handle slot creation
    @PostMapping("/create-template")
    public String createSlotTemplate(@ModelAttribute("request") SlotTemplateRequest request, Model model) {
        try {
            slotService.generateSlotsForCourt(request);
            model.addAttribute("success", "✅ Slots successfully generated for this court!");
        } catch (Exception e) {
            model.addAttribute("error", "⚠️ Error while generating slots: " + e.getMessage());
        }

        Court court = courtRepo.findById(request.getCourtId()).orElse(null);
        model.addAttribute("court", court);
        model.addAttribute("request", new SlotTemplateRequest());
        return "create-slot-template";
    }
}
