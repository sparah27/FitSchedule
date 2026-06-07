package com.fitschedule.fitschedule.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {

    private long totalBookingsThisMonth;
    private long activeTrainers;
    private long activeClients;
    private double cancellationRateThisMonth;
}