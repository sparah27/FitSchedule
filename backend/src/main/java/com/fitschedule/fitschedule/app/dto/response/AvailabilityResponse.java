package com.fitschedule.fitschedule.app.dto.response;

import com.fitschedule.fitschedule.app.model.entity.Availability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {

    private Long id;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public static AvailabilityResponse fromEntity(Availability a) {
        return AvailabilityResponse.builder()
                .id(a.getId())
                .dayOfWeek(a.getDayOfWeek().name())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .build();
    }
}