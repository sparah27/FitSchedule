package com.fitschedule.fitschedule.app.event.listener;

import com.fitschedule.fitschedule.app.event.BookingCancelledEvent;
import com.fitschedule.fitschedule.app.event.BookingCreatedEvent;
import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.enums.NotificationType;
import com.fitschedule.fitschedule.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerNotificationListener {

    private final NotificationService notificationService;

    @EventListener
    public void onBookingCreated(BookingCreatedEvent event) {
        Booking booking = event.getBooking();
        notificationService.createNotification(
                booking.getTrainer(),
                NotificationType.BOOKING_CONFIRMED,
                "New booking from " + booking.getClient().getFirstName() + " "
                        + booking.getClient().getLastName() + " for "
                        + booking.getTimeSlot().getStartAt().toLocalDate() + " at "
                        + booking.getTimeSlot().getStartAt().toLocalTime() + ".",
                booking.getId()
        );
    }

    @EventListener
    public void onBookingCancelled(BookingCancelledEvent event) {
        Booking booking = event.getBooking();
        notificationService.createNotification(
                booking.getTrainer(),
                NotificationType.BOOKING_CANCELLED_BY_CLIENT,
                booking.getClient().getFirstName() + " " + booking.getClient().getLastName()
                        + " cancelled the session scheduled for "
                        + booking.getTimeSlot().getStartAt().toLocalDate() + " at "
                        + booking.getTimeSlot().getStartAt().toLocalTime() + ".",
                booking.getId()
        );
    }
}