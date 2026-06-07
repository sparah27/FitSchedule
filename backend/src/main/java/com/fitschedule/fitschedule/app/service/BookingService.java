package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.CreateBookingRequest;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.event.BookingCancelledEvent;
import com.fitschedule.fitschedule.app.event.BookingCreatedEvent;
import com.fitschedule.fitschedule.app.exception.ForbiddenActionException;
import com.fitschedule.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.fitschedule.app.exception.SlotNotAvailableException;
import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.entity.Client;
import com.fitschedule.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.fitschedule.app.model.entity.User;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.fitschedule.app.repository.BookingRepository;
import com.fitschedule.fitschedule.app.repository.TimeSlotRepository;
import com.fitschedule.fitschedule.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public BookingResponse createBooking(Long userId, CreateBookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!(user instanceof Client client)) {
            throw new ForbiddenActionException("Only clients can book sessions");
        }

        TimeSlot slot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));

        if (slot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("This time slot is not available");
        }

        if (slot.getStartAt().isBefore(LocalDateTime.now())) {
            throw new SlotNotAvailableException("Cannot book a slot in the past");
        }

        if (!Boolean.TRUE.equals(slot.getTrainer().getActive())) {
            throw new SlotNotAvailableException("This trainer is no longer available");
        }

        slot.setStatus(TimeSlotStatus.BOOKED);
        timeSlotRepository.save(slot);

        Booking booking = Booking.builder()
                .client(client)
                .trainer(slot.getTrainer())
                .timeSlot(slot)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Decoupled notification via Observer pattern
        eventPublisher.publishEvent(new BookingCreatedEvent(this, saved));

        return BookingResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyUpcomingBookings(Long userId) {
        return bookingRepository
                .findByClientIdAndStatusAndTimeSlot_StartAtAfterOrderByTimeSlot_StartAtAsc(
                        userId, BookingStatus.CONFIRMED, LocalDateTime.now())
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyPastBookings(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findByClientIdOrderByTimeSlot_StartAtDesc(userId).stream()
                .filter(b -> !(b.getStatus() == BookingStatus.CONFIRMED
                        && b.getTimeSlot().getStartAt().isAfter(now)))
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isClient = booking.getClient().getId().equals(userId);
        boolean isTrainer = booking.getTrainer().getId().equals(userId);
        if (!isClient && !isTrainer) {
            throw new ForbiddenActionException("You don't have access to this booking");
        }

        return BookingResponse.fromEntity(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getClient().getId().equals(userId)) {
            throw new ForbiddenActionException("You can only cancel your own bookings");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new SlotNotAvailableException("This booking cannot be cancelled");
        }

        if (booking.getTimeSlot().getStartAt().isBefore(LocalDateTime.now())) {
            throw new SlotNotAvailableException("Cannot cancel a session that has already started or passed");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        TimeSlot slot = booking.getTimeSlot();
        slot.setStatus(TimeSlotStatus.AVAILABLE);
        timeSlotRepository.save(slot);

        Booking saved = bookingRepository.save(booking);

        // Decoupled notification via Observer pattern
        eventPublisher.publishEvent(new BookingCancelledEvent(this, saved));

        return BookingResponse.fromEntity(saved);
    }
}
