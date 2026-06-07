package com.fitschedule.fitschedule.app.controller;

import com.fitschedule.fitschedule.app.dto.request.CreateReviewRequest;
import com.fitschedule.fitschedule.app.dto.response.ReviewResponse;
import com.fitschedule.fitschedule.app.security.CustomUserDetails;
import com.fitschedule.fitschedule.app.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Submit and retrieve trainer reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/bookings/{bookingId}/review")
    @Operation(summary = "Submit a review for a completed session (client only)")
    public ResponseEntity<ReviewResponse> submitReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication auth) {
        Long clientId = ((CustomUserDetails) auth.getPrincipal()).getUserId();
        ReviewResponse response = reviewService.submitReview(clientId, bookingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/trainers/{trainerId}/reviews")
    @Operation(summary = "Get all reviews for a trainer (public)")
    public ResponseEntity<List<ReviewResponse>> getTrainerReviews(@PathVariable Long trainerId) {
        return ResponseEntity.ok(reviewService.getReviewsForTrainer(trainerId));
    }
}