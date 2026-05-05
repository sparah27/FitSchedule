package com.fitschedule.backend.fitschedule.app.dto.response;

import com.fitschedule.backend.fitschedule.app.model.entity.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {

    private Long id;
    private Long trainerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public static TimeSlotResponse fromEntity(TimeSlot slot) {
        return TimeSlotResponse.builder()
                .id(slot.getId())
                .trainerId(slot.getTrainer().getId())
                .startTime(slot.getStartAt())
                .endTime(slot.getEndAt())
                .status(slot.getStatus().name())
                .build();
    }
}