package com.fitschedule.fitschedule.app.controller;

import com.fitschedule.fitschedule.app.dto.request.SetAvailabilityRequest;
import com.fitschedule.fitschedule.app.dto.response.AvailabilityResponse;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.dto.response.ReviewResponse;
import com.fitschedule.fitschedule.app.dto.response.TimeSlotResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.fitschedule.app.security.CustomUserDetails;
import com.fitschedule.fitschedule.app.service.TrainerSelfService;
import com.fitschedule.fitschedule.app.service.UpdateTrainerProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
@Tag(name = "Trainer Self-Service", description = "Trainer-facing endpoints for schedule, availability, and profile management")
public class TrainerSelfController {

    private final TrainerSelfService trainerSelfService;

    @PostMapping("/availability")
    @Operation(summary = "Set weekly availability and regenerate time slots")
    public ResponseEntity<List<AvailabilityResponse>> setAvailability(
            @Valid @RequestBody SetAvailabilityRequest request,
            Authentication auth) {
        Long trainerId = getUserId(auth);
        return ResponseEntity.ok(trainerSelfService.setAvailability(trainerId, request));
    }

    @GetMapping("/schedule")
    @Operation(summary = "View upcoming bookings (trainer's weekly schedule)")
    public ResponseEntity<List<BookingResponse>> getSchedule(Authentication auth) {
        return ResponseEntity.ok(trainerSelfService.getSchedule(getUserId(auth)));
    }

    @PutMapping("/bookings/{bookingId}/complete")
    @Operation(summary = "Mark a booking as completed")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId,
            Authentication auth) {
        return ResponseEntity.ok(trainerSelfService.completeBooking(getUserId(auth), bookingId));
    }

    @GetMapping("/reviews")
    @Operation(summary = "View all reviews for the authenticated trainer")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication auth) {
        return ResponseEntity.ok(trainerSelfService.getMyReviews(getUserId(auth)));
    }

    @PutMapping("/timeslots/{slotId}/block")
    @Operation(summary = "Block a time slot so clients cannot book it")
    public ResponseEntity<TimeSlotResponse> blockSlot(
            @PathVariable Long slotId,
            Authentication auth) {
        return ResponseEntity.ok(trainerSelfService.blockSlot(getUserId(auth), slotId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainer profile including bio and specialization")
    public ResponseEntity<TrainerResponse> updateProfile(
            @Valid @RequestBody UpdateTrainerProfileRequest request,
            Authentication auth) {
        return ResponseEntity.ok(trainerSelfService.updateProfile(getUserId(auth), request));
    }

    private Long getUserId(Authentication auth) {
        return ((CustomUserDetails) auth.getPrincipal()).getUserId();
    }
}
