package com.fitschedule.fitschedule.app.service.filter;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FilterCriteria {

    private final String specialization;
    private final LocalDateTime from;
    private final LocalDateTime to;
}