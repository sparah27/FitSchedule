package com.fitschedule.fitschedule.app.service.filter;

import com.fitschedule.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.fitschedule.app.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DateRangeFilterStrategy implements TrainerFilterStrategy {

    private final TimeSlotRepository timeSlotRepository;

    @Override
    public boolean isApplicable(FilterCriteria criteria) {
        return criteria.getFrom() != null && criteria.getTo() != null;
    }

    @Override
    public List<Trainer> filter(List<Trainer> trainers, FilterCriteria criteria) {
        List<TimeSlot> slots = timeSlotRepository.findByStatusAndStartAtBetweenOrderByStartAt(
                TimeSlotStatus.AVAILABLE, criteria.getFrom(), criteria.getTo());

        Set<Long> trainerIdsWithSlots = slots.stream()
                .map(s -> s.getTrainer().getId())
                .collect(Collectors.toSet());

        return trainers.stream()
                .filter(t -> trainerIdsWithSlots.contains(t.getId()))
                .toList();
    }
}