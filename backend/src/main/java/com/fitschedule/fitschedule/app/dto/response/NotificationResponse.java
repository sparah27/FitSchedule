package com.fitschedule.fitschedule.app.dto.response;

import com.fitschedule.fitschedule.app.model.entity.Notification;
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
public class NotificationResponse {

    private Long id;
    private String type;
    private String message;
    private Boolean isRead;
    private LocalDateTime readAt;
    private Long relatedId;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType().name())
                .message(n.getMessage())
                .isRead(n.getReadAt() != null)
                .readAt(n.getReadAt())
                .relatedId(n.getRelatedId())
                .createdAt(n.getCreatedAt())
                .build();
    }
}