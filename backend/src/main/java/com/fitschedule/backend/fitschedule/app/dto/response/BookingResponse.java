package com.fitschedule.backend.fitschedule.app.dto.response;

import com.fitschedule.backend.fitschedule.app.model.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long clientId;
    private String clientFullName;
    private Long trainerId;
    private String trainerFullName;
    private String trainerPhotoUrl;
    private String trainerSpecialization;
    private Long timeSlotId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .clientId(booking.getClient().getId())
                .clientFullName(booking.getClient().getFirstName() + " " + booking.getClient().getLastName())
                .trainerId(booking.getTrainer().getId())
                .trainerFullName(booking.getTrainer().getFirstName() + " " + booking.getTrainer().getLastName())
                .trainerPhotoUrl(booking.getTrainer().getPhotoUrl())
                .trainerSpecialization(booking.getTrainer().getSpecialization())
                .timeSlotId(booking.getTimeSlot().getId())
                .startAt(booking.getTimeSlot().getStartAt())
                .endAt(booking.getTimeSlot().getEndAt())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }
}