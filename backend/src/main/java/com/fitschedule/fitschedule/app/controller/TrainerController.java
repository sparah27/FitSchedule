package com.fitschedule.fitschedule.app.controller;

import com.fitschedule.fitschedule.app.dto.response.TimeSlotResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerSummaryResponse;
import com.fitschedule.fitschedule.app.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainers", description = "Browse and search trainers")
@SecurityRequirement(name = "bearerAuth")
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    @Operation(summary = "List all active trainers, optionally filtered by specialization")
    public ResponseEntity<List<TrainerSummaryResponse>> getAllTrainers(
            @Parameter(description = "Filter by specialization (e.g. Strength, Yoga)")
            @RequestParam(required = false) String specialization
    ) {
        return ResponseEntity.ok(trainerService.getAllTrainers(specialization));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full profile of a single trainer")
    public ResponseEntity<TrainerResponse> getTrainerById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.getTrainerById(id));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Get available time slots for a trainer in a given date range")
    public ResponseEntity<List<TimeSlotResponse>> getTrainerAvailability(
            @PathVariable Long id,
            @Parameter(description = "Start of range (ISO format e.g. 2026-05-04T00:00:00)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "End of range (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(trainerService.getTrainerAvailability(id, from, to));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for trainers with at least one available slot in a date range")
    public ResponseEntity<List<TrainerSummaryResponse>> searchAvailableTrainers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(trainerService.searchAvailableTrainers(from, to));
    }
}