package com.fitschedule.fitschedule.app.repository;

import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByClientIdAndStatusAndTimeSlot_StartAtAfterOrderByTimeSlot_StartAtAsc(
            Long clientId, BookingStatus status, LocalDateTime now);

    List<Booking> findByClientIdOrderByTimeSlot_StartAtDesc(Long clientId);

    List<Booking> findByTrainerIdOrderByTimeSlot_StartAtDesc(Long trainerId);

    Optional<Booking> findByTimeSlotId(Long timeSlotId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreatedAtBetween(BookingStatus status, LocalDateTime start, LocalDateTime end);
}
