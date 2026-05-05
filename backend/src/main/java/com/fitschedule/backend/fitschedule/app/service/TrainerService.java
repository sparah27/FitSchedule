package com.fitschedule.backend.fitschedule.app.service;

import com.fitschedule.backend.fitschedule.app.dto.response.TimeSlotResponse;
import com.fitschedule.backend.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.backend.fitschedule.app.dto.response.TrainerSummaryResponse;
import com.fitschedule.backend.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.backend.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.backend.fitschedule.app.model.entity.Trainer;
import com.fitschedule.backend.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.backend.fitschedule.app.repository.TimeSlotRepository;
import com.fitschedule.backend.fitschedule.app.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final TimeSlotRepository timeSlotRepository;

    public List<TrainerSummaryResponse> getAllTrainers(String specialization) {
        List<Trainer> trainers = (specialization == null || specialization.isBlank())
                ? trainerRepository.findAllByActiveTrue()
                : trainerRepository.findAllByActiveTrueAndSpecializationIgnoreCase(specialization.trim());

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
        // verify trainer exists & active
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
        List<TimeSlot> availableSlots = timeSlotRepository
                .findByStatusAndStartAtBetweenOrderByStartAt(
                        TimeSlotStatus.AVAILABLE, from, to);

        Set<Long> trainerIds = availableSlots.stream()
                .map(slot -> slot.getTrainer().getId())
                .collect(Collectors.toSet());

        if (trainerIds.isEmpty()) {
            return List.of();
        }

        List<Trainer> trainers = trainerRepository.findAllById(trainerIds).stream()
                .filter(t -> Boolean.TRUE.equals(t.getActive()))
                .toList();

        return trainers.stream()
                .map(TrainerSummaryResponse::fromEntity)
                .toList();
    }
}