package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueSlotResponse {
    private String courtName;
    private Long courtId;
    private String date;
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
