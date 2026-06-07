package com.fitschedule.fitschedule.app.controller;

import com.fitschedule.fitschedule.app.dto.request.CreateTrainerRequest;
import com.fitschedule.fitschedule.app.dto.response.AdminStatsResponse;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only management endpoints")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/trainers")
    @Operation(summary = "Create a new trainer account")
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody CreateTrainerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createTrainer(request));
    }

    @GetMapping("/bookings")
    @Operation(summary = "View all bookings; filter by status and/or date")
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(adminService.getAllBookings(status, date));
    }

    @PutMapping("/trainers/{trainerId}/deactivate")
    @Operation(summary = "Deactivate a trainer (hides from client-facing views)")
    public ResponseEntity<TrainerResponse> deactivateTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(adminService.deactivateTrainer(trainerId));
    }

    @PutMapping("/trainers/{trainerId}/activate")
    @Operation(summary = "Reactivate a previously deactivated trainer")
    public ResponseEntity<TrainerResponse> activateTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(adminService.activateTrainer(trainerId));
    }

    @GetMapping("/stats")
    @Operation(summary = "Dashboard statistics: bookings, active users, cancellation rate")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}