package com.fitschedule.fitschedule.app.controller;

import com.fitschedule.fitschedule.app.dto.request.CreateBookingRequest;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.security.CustomUserDetails;
import com.fitschedule.fitschedule.app.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Create, view, and cancel bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking for a time slot")
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        BookingResponse response = bookingService.createBooking(principal.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "List my upcoming bookings")
    public ResponseEntity<List<BookingResponse>> getMyUpcomingBookings(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(bookingService.getMyUpcomingBookings(principal.getUserId()));
    }

    @GetMapping("/me/past")
    @Operation(summary = "List my past bookings (cancelled, completed, or finished)")
    public ResponseEntity<List<BookingResponse>> getMyPastBookings(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(bookingService.getMyPastBookings(principal.getUserId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get details of a single booking")
    public ResponseEntity<BookingResponse> getBookingById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookingService.getBookingById(principal.getUserId(), id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<BookingResponse> cancelBooking(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(principal.getUserId(), id));
    }
}