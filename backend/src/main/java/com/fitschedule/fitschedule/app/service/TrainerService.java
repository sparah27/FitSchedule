package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.response.TimeSlotResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerSummaryResponse;
import com.fitschedule.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.fitschedule.app.repository.TimeSlotRepository;
import com.fitschedule.fitschedule.app.repository.TrainerRepository;
import com.fitschedule.fitschedule.app.service.filter.FilterCriteria;
import com.fitschedule.fitschedule.app.service.filter.TrainerFilterStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final TimeSlotRepository timeSlotRepository;
    // Spring injects all TrainerFilterStrategy @Component implementations
    private final List<TrainerFilterStrategy> filterStrategies;

    public List<TrainerSummaryResponse> getAllTrainers(String specialization) {
        List<Trainer> trainers = trainerRepository.findAllByActiveTrue();

        FilterCriteria criteria = FilterCriteria.builder()
                .specialization(specialization)
                .build();

        for (TrainerFilterStrategy strategy : filterStrategies) {
            if (strategy.isApplicable(criteria)) {
                trainers = strategy.filter(trainers, criteria);
            }
        }

        return trainers.stream()
                .map(TrainerSummaryResponse::fromEntity)
                .toList();
    }

    public TrainerResponse getTrainerById(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .filter(t -> Boolean.TRUE.equals(t.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id " + id));
        return TrainerResponse.fromEntity(trainer);
    }

    public List<TimeSlotResponse> getTrainerAvailability(Long trainerId, LocalDateTime from, LocalDateTime to) {
        trainerRepository.findById(trainerId)
                .filter(t -> Boolean.TRUE.equals(t.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id " + trainerId));

        List<TimeSlot> slots = timeSlotRepository
                .findByTrainerIdAndStatusAndStartAtBetweenOrderByStartAt(
                        trainerId, TimeSlotStatus.AVAILABLE, from, to);

        return slots.stream()
                .map(TimeSlotResponse::fromEntity)
                .toList();
    }

    public List<TrainerSummaryResponse> searchAvailableTrainers(LocalDateTime from, LocalDateTime to) {
        List<Trainer> trainers = trainerRepository.findAllByActiveTrue();

        FilterCriteria criteria = FilterCriteria.builder()
                .from(from)
                .to(to)
                .build();

        for (TrainerFilterStrategy strategy : filterStrategies) {
            if (strategy.isApplicable(criteria)) {
                trainers = strategy.filter(trainers, criteria);
            }
        }

        return trainers.stream()
                .map(TrainerSummaryResponse::fromEntity)
                .toList();
    }
}
