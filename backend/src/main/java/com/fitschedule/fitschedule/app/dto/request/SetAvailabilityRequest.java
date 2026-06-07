package com.fitschedule.fitschedule.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SetAvailabilityRequest {

    @NotEmpty(message = "At least one availability slot is required")
    @Valid
    private List<DayAvailability> availabilities;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DayAvailability {

        @NotNull(message = "Day of week is required")
        private String dayOfWeek; // MONDAY, TUESDAY, etc.

        @NotNull(message = "Start time is required")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Start time must be HH:mm")
        private String startTime;

        @NotNull(message = "End time is required")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "End time must be HH:mm")
        private String endTime;
    }
}