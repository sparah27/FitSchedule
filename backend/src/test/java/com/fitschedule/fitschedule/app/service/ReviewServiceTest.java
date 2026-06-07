package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.CreateReviewRequest;
import com.fitschedule.fitschedule.app.exception.ForbiddenActionException;
import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.entity.Client;
import com.fitschedule.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.fitschedule.app.repository.BookingRepository;
import com.fitschedule.fitschedule.app.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private BookingRepository bookingRepository;

    private ReviewService reviewService;

    private Client client;
    private Trainer trainer;
    private TimeSlot slot;
    private Booking confirmedBooking;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, bookingRepository);

        trainer = Trainer.builder().firstName("Marko").lastName("Kovač").active(true).build();
        client = Client.builder().firstName("Ana").lastName("Anić").active(true).build();
        setId(client, 1L);

        slot = TimeSlot.builder()
                .trainer(trainer)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().minusDays(1).plusHours(1))
                .status(TimeSlotStatus.BOOKED)
                .build();

        confirmedBooking = Booking.builder()
                .client(client).trainer(trainer).timeSlot(slot)
                .status(BookingStatus.CONFIRMED).build();
        setId(confirmedBooking, 50L);
    }

    @Test
    void submitReview_throwsForbidden_whenBookingIsNotCompleted() {
        when(bookingRepository.findById(50L)).thenReturn(Optional.of(confirmedBooking));

        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(5);
        req.setComment("Great session");

        assertThatThrownBy(() -> reviewService.submitReview(1L, 50L, req))
                .isInstanceOf(ForbiddenActionException.class)
                .hasMessageContaining("completed");
    }

    private void setId(Object entity, Long id) {
        try {
            var field = findField(entity.getClass());
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Could not set id on " + entity.getClass().getSimpleName(), e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) return findField(clazz.getSuperclass());
            throw e;
        }
    }
}
