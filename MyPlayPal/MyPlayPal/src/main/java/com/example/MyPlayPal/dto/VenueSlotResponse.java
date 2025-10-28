package com.example.MyPlayPal.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueSlotResponse {
    private String courtName;
    private Long courtId;
    private String date; // formatted as "yyyy-MM-dd"
    private List<SlotInfo> slots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SlotInfo {
        private Long id;
        private String startTime;
        private String endTime;
        private String status;
    }
}
