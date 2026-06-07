package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.SetAvailabilityRequest;
import com.fitschedule.fitschedule.app.dto.request.UpdateProfileRequest;
import com.fitschedule.fitschedule.app.dto.response.AvailabilityResponse;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.dto.response.ReviewResponse;
import com.fitschedule.fitschedule.app.dto.response.TimeSlotResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.fitschedule.app.exception.ForbiddenActionException;
import com.fitschedule.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.fitschedule.app.model.entity.Availability;
import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.fitschedule.app.repository.AvailabilityRepository;
import com.fitschedule.fitschedule.app.repository.BookingRepository;
import com.fitschedule.fitschedule.app.repository.ReviewRepository;
import com.fitschedule.fitschedule.app.repository.TimeSlotRepository;
import com.fitschedule.fitschedule.app.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerSelfService {

    private final TrainerRepository trainerRepository;
    private final AvailabilityRepository availabilityRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public List<AvailabilityResponse> setAvailability(Long trainerId, SetAvailabilityRequest request) {
        Trainer trainer = loadTrainer(trainerId);

        // Delete all existing AVAILABLE and BLOCKED future slots (keep BOOKED)
        List<TimeSlot> futureSlots = timeSlotRepository
                .findByTrainerIdAndStartAtBetweenOrderByStartAt(
                        trainerId, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
        futureSlots.stream()
                .filter(s -> s.getStatus() != TimeSlotStatus.BOOKED)
                .forEach(timeSlotRepository::delete);

        // Replace availability records
        availabilityRepository.deleteAll(availabilityRepository.findByTrainerId(trainerId));

        List<Availability> newAvailabilities = new ArrayList<>();
        for (SetAvailabilityRequest.DayAvailability day : request.getAvailabilities()) {
            DayOfWeek dow = DayOfWeek.valueOf(day.getDayOfWeek().toUpperCase());
            LocalTime start = LocalTime.parse(day.getStartTime());
            LocalTime end = LocalTime.parse(day.getEndTime());

            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start time must be before end time for " + day.getDayOfWeek());
            }

            Availability avail = Availability.builder()
                    .trainer(trainer)
                    .dayOfWeek(dow)
                    .startTime(start)
                    .endTime(end)
                    .build();
            newAvailabilities.add(availabilityRepository.save(avail));

            generateTimeSlots(trainer, dow, start, end);
        }

        return newAvailabilities.stream()
                .map(AvailabilityResponse::fromEntity)
                .toList();
    }

    private void generateTimeSlots(Trainer trainer, DayOfWeek dow, LocalTime startTime, LocalTime endTime) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusWeeks(4);

        LocalDate current = today;
        while (current.getDayOfWeek() != dow) {
            current = current.plusDays(1);
        }

        while (!current.isAfter(endDate)) {
            LocalTime slotStart = startTime;
            while (slotStart.plusHours(1).compareTo(endTime) <= 0) {
                LocalDateTime startAt = LocalDateTime.of(current, slotStart);
                if (startAt.isAfter(LocalDateTime.now())) {
                    TimeSlot slot = TimeSlot.builder()
                            .trainer(trainer)
                            .startAt(startAt)
                            .endAt(startAt.plusHours(1))
                            .status(TimeSlotStatus.AVAILABLE)
                            .build();
                    timeSlotRepository.save(slot);
                }
                slotStart = slotStart.plusHours(1);
            }
            current = current.plusWeeks(1);
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getSchedule(Long trainerId) {
        return bookingRepository.findByTrainerIdOrderByTimeSlot_StartAtDesc(trainerId)
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED
                        && b.getTimeSlot().getStartAt().isAfter(LocalDateTime.now()))
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional
    public BookingResponse completeBooking(Long trainerId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!booking.getTrainer().getId().equals(trainerId)) {
            throw new ForbiddenActionException("You can only complete your own bookings");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ForbiddenActionException("Only confirmed bookings can be marked as completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        return BookingResponse.fromEntity(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(Long trainerId) {
        return reviewRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId)
                .stream()
                .map(ReviewResponse::fromEntity)
                .toList();
    }

    @Transactional
    public TimeSlotResponse blockSlot(Long trainerId, Long slotId) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot", slotId));

        if (!slot.getTrainer().getId().equals(trainerId)) {
            throw new ForbiddenActionException("You can only block your own time slots");
        }

        if (slot.getStatus() == TimeSlotStatus.BOOKED) {
            throw new ForbiddenActionException("Cannot block a slot that is already booked");
        }

        slot.setStatus(TimeSlotStatus.BLOCKED);
        return TimeSlotResponse.fromEntity(timeSlotRepository.save(slot));
    }

    @Transactional
    public TrainerResponse updateProfile(Long trainerId, UpdateTrainerProfileRequest request) {
        Trainer trainer = loadTrainer(trainerId);

        if (request.getBio() != null) trainer.setBio(request.getBio());
        if (request.getSpecialization() != null) trainer.setSpecialization(request.getSpecialization());
        if (request.getCertifications() != null) trainer.setCertifications(request.getCertifications());
        if (request.getFirstName() != null && !request.getFirstName().isBlank())
            trainer.setFirstName(request.getFirstName());
        if (request.getLastName() != null && !request.getLastName().isBlank())
            trainer.setLastName(request.getLastName());
        if (request.getPhone() != null) trainer.setPhone(request.getPhone());
        if (request.getPhotoUrl() != null) trainer.setPhotoUrl(request.getPhotoUrl());

        return TrainerResponse.fromEntity(trainerRepository.save(trainer));
    }

    private Trainer loadTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", trainerId));
    }
}