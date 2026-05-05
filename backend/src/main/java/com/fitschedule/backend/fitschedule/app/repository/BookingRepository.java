package com.fitschedule.backend.fitschedule.app.repository;

import com.fitschedule.backend.fitschedule.app.model.entity.Booking;
import com.fitschedule.backend.fitschedule.app.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Upcoming bookings: CONFIRMED status AND slot in the future
    List<Booking> findByClientIdAndStatusAndTimeSlot_StartAtAfterOrderByTimeSlot_StartAtAsc(
            Long clientId, BookingStatus status, LocalDateTime now);

    // Past bookings: anything that's not upcoming (cancelled, completed, or confirmed but slot in past)
    List<Booking> findByClientIdOrderByTimeSlot_StartAtDesc(Long clientId);

    // For trainer schedule view (Release 2, but we expose it now since it's trivial)
    List<Booking> findByTrainerIdOrderByTimeSlot_StartAtDesc(Long trainerId);

    Optional<Booking> findByTimeSlotId(Long timeSlotId);
}