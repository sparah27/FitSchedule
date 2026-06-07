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
public class ClientNotificationListener {

    private final NotificationService notificationService;

    @EventListener
    public void onBookingCreated(BookingCreatedEvent event) {
        Booking booking = event.getBooking();
        notificationService.createNotification(
                booking.getClient(),
                NotificationType.BOOKING_CONFIRMED,
                "Your booking with " + booking.getTrainer().getFirstName() + " "
                        + booking.getTrainer().getLastName() + " is confirmed for "
                        + booking.getTimeSlot().getStartAt().toLocalDate() + " at "
                        + booking.getTimeSlot().getStartAt().toLocalTime() + ".",
                booking.getId()
        );
    }

    @EventListener
    public void onBookingCancelled(BookingCancelledEvent event) {
        Booking booking = event.getBooking();
        notificationService.createNotification(
                booking.getClient(),
                NotificationType.BOOKING_CANCELLED_BY_CLIENT,
                "Your booking on " + booking.getTimeSlot().getStartAt().toLocalDate() + " at "
                        + booking.getTimeSlot().getStartAt().toLocalTime() + " was cancelled.",
                booking.getId()
        );
    }
}