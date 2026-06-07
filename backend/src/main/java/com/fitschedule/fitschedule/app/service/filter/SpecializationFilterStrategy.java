package com.fitschedule.fitschedule.app.service.filter;

import com.fitschedule.fitschedule.app.model.entity.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecializationFilterStrategy implements TrainerFilterStrategy {

    @Override
    public boolean isApplicable(FilterCriteria criteria) {
        return criteria.getSpecialization() != null && !criteria.getSpecialization().isBlank();
    }

    @Override
    public List<Trainer> filter(List<Trainer> trainers, FilterCriteria criteria) {
        String target = criteria.getSpecialization().trim().toLowerCase();
        return trainers.stream()
                .filter(t -> t.getSpecialization() != null &&
                        t.getSpecialization().trim().toLowerCase().equals(target))
                .toList();
    }
}
