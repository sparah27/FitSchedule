package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.response.NotificationResponse;
import com.fitschedule.fitschedule.app.exception.ForbiddenActionException;
import com.fitschedule.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.fitschedule.app.model.entity.Notification;
import com.fitschedule.fitschedule.app.model.entity.User;
import com.fitschedule.fitschedule.app.model.enums.NotificationType;
import com.fitschedule.fitschedule.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Internal method called from BookingService etc.
     * Not exposed via REST.
     */
    @Transactional
    public void createNotification(User user, NotificationType type, String message, Long relatedId) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .relatedId(relatedId)
                .readAt(null)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!n.getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("You can't modify someone else's notification");
        }

        if (n.getReadAt() == null) {
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
        return NotificationResponse.fromEntity(n);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> n.setReadAt(now));
        notificationRepository.saveAll(unread);
    }
}