package com.fitschedule.backend.fitschedule.app.repository;

import com.fitschedule.backend.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.backend.fitschedule.app.model.enums.TimeSlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByTrainerIdAndStartAtBetweenOrderByStartAt(
            Long trainerId, LocalDateTime from, LocalDateTime to);

    List<TimeSlot> findByTrainerIdAndStatusAndStartAtBetweenOrderByStartAt(
            Long trainerId, TimeSlotStatus status, LocalDateTime from, LocalDateTime to);

    List<TimeSlot> findByStatusAndStartAtBetweenOrderByStartAt(
            TimeSlotStatus status, LocalDateTime from, LocalDateTime to);
}