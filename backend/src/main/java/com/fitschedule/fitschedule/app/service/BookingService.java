package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.CreateBookingRequest;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitschedule.fitschedule.app.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public BookingResponse createBooking(Long userId, CreateBookingRequest request) {
        // 1. Verify user is a client
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!(user instanceof Client client)) {
            throw new ForbiddenActionException("Only clients can book sessions");
        }

        // 2. Load slot
        TimeSlot slot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));

        // 3. Slot must be AVAILABLE
        if (slot.getStatus() != TimeSlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("This time slot is not available");
        }

        // 4. Slot must be in the future
        if (slot.getStartAt().isBefore(LocalDateTime.now())) {
            throw new SlotNotAvailableException("Cannot book a slot in the past");
        }

        // 5. Trainer must be active
        if (!Boolean.TRUE.equals(slot.getTrainer().getActive())) {
            throw new SlotNotAvailableException("This trainer is no longer available");
        }

        // 6. Mark slot BOOKED
        slot.setStatus(TimeSlotStatus.BOOKED);
        timeSlotRepository.save(slot);

        // 7. Create booking
        Booking booking = Booking.builder()
                .client(client)
                .trainer(slot.getTrainer())
                .timeSlot(slot)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Notify client (booking confirmation)
        notificationService.createNotification(
                client,
                NotificationType.BOOKING_CONFIRMED,
                "Your booking with " + slot.getTrainer().getFirstName() + " "
                        + slot.getTrainer().getLastName() + " is confirmed for "
                        + slot.getStartAt().toLocalDate() + " at "
                        + slot.getStartAt().toLocalTime() + ".",
                saved.getId()
        );

// Notify trainer (new booking)
        notificationService.createNotification(
                slot.getTrainer(),
                NotificationType.BOOKING_CONFIRMED,
                "New booking from " + client.getFirstName() + " " + client.getLastName()
                        + " for " + slot.getStartAt().toLocalDate() + " at "
                        + slot.getStartAt().toLocalTime() + ".",
                saved.getId()
        );

        return BookingResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyUpcomingBookings(Long userId) {
        List<Booking> bookings = bookingRepository
                .findByClientIdAndStatusAndTimeSlot_StartAtAfterOrderByTimeSlot_StartAtAsc(
                        userId, BookingStatus.CONFIRMED, LocalDateTime.now());

        return bookings.stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyPastBookings(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> all = bookingRepository.findByClientIdOrderByTimeSlot_StartAtDesc(userId);

        // Past = anything NOT (CONFIRMED with future slot)
        return all.stream()
                .filter(b -> !(b.getStatus() == BookingStatus.CONFIRMED
                        && b.getTimeSlot().getStartAt().isAfter(now)))
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Only the booking's client OR its trainer can view it
        boolean isOwnerClient = booking.getClient().getId().equals(userId);
        boolean isOwnerTrainer = booking.getTrainer().getId().equals(userId);
        if (!isOwnerClient && !isOwnerTrainer) {
            throw new ForbiddenActionException("You don't have access to this booking");
        }

        return BookingResponse.fromEntity(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Only the booking's client can cancel
        if (!booking.getClient().getId().equals(userId)) {
            throw new ForbiddenActionException("You can only cancel your own bookings");
        }

        // Can't cancel if already cancelled or completed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new SlotNotAvailableException("This booking cannot be cancelled");
        }

        // Can't cancel sessions in the past
        if (booking.getTimeSlot().getStartAt().isBefore(LocalDateTime.now())) {
            throw new SlotNotAvailableException("Cannot cancel a session that has already started or passed");
        }

        // Update booking
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        // Free up the slot
        TimeSlot slot = booking.getTimeSlot();
        slot.setStatus(TimeSlotStatus.AVAILABLE);
        timeSlotRepository.save(slot);

        Booking saved = bookingRepository.save(booking);

        // Notify trainer (client cancelled)
        notificationService.createNotification(
                booking.getTrainer(),
                NotificationType.BOOKING_CANCELLED_BY_CLIENT,
                booking.getClient().getFirstName() + " " + booking.getClient().getLastName()
                        + " cancelled the session scheduled for "
                        + slot.getStartAt().toLocalDate() + " at "
                        + slot.getStartAt().toLocalTime() + ".",
                booking.getId()
        );

// Notify client (own cancellation)
        notificationService.createNotification(
                booking.getClient(),
                NotificationType.BOOKING_CANCELLED_BY_CLIENT,
                "Your booking on " + slot.getStartAt().toLocalDate() + " at "
                        + slot.getStartAt().toLocalTime() + " was cancelled.",
                booking.getId()
        );
        return BookingResponse.fromEntity(saved);
    }
}