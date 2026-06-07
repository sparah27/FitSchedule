package com.fitschedule.fitschedule.app.dto.response;

import com.fitschedule.fitschedule.app.model.entity.Review;
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
public class ReviewResponse {

    private Long id;
    private Long clientId;
    private String clientFullName;
    private Long trainerId;
    private Long bookingId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .clientId(r.getClient().getId())
                .clientFullName(r.getClient().getFirstName() + " " + r.getClient().getLastName())
                .trainerId(r.getTrainer().getId())
                .bookingId(r.getBooking().getId())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}