package com.fitschedule.fitschedule.app.controller;

import com.fitschedule.fitschedule.app.dto.response.NotificationResponse;
import com.fitschedule.fitschedule.app.security.CustomUserDetails;
import com.fitschedule.fitschedule.app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List all my notifications")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(notificationService.getMyNotifications(principal.getUserId()));
    }

    @GetMapping("/unread")
    @Operation(summary = "List my unread notifications")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(notificationService.getMyUnreadNotifications(principal.getUserId()));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(notificationService.markAsRead(principal.getUserId(), id));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all my notifications as read")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        notificationService.markAllAsRead(principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}