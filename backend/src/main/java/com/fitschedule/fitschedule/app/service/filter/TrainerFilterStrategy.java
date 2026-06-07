package com.fitschedule.fitschedule.app.service.filter;

import com.fitschedule.fitschedule.app.model.entity.Trainer;

import java.util.List;

public interface TrainerFilterStrategy {

    boolean isApplicable(FilterCriteria criteria);

    List<Trainer> filter(List<Trainer> trainers, FilterCriteria criteria);
}