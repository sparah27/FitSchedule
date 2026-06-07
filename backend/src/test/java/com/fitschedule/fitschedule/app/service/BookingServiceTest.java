package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.CreateBookingRequest;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.exception.ForbiddenActionException;
import com.fitschedule.fitschedule.app.exception.SlotNotAvailableException;
import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.entity.Client;
import com.fitschedule.fitschedule.app.model.entity.TimeSlot;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.model.enums.TimeSlotStatus;
import com.fitschedule.fitschedule.app.repository.BookingRepository;
import com.fitschedule.fitschedule.app.repository.TimeSlotRepository;
import com.fitschedule.fitschedule.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private TimeSlotRepository timeSlotRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private BookingService bookingService;

    private Client client;
    private Trainer trainer;
    private TimeSlot availableSlot;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, timeSlotRepository, userRepository, eventPublisher);

        trainer = Trainer.builder()
                .firstName("Marko").lastName("Kovač")
                .active(true).build();
        setId(trainer, 10L);

        client = Client.builder()
                .firstName("Ana").lastName("Anić")
                .active(true).build();
        setId(client, 1L);

        availableSlot = TimeSlot.builder()
                .trainer(trainer)
                .startAt(LocalDateTime.now().plusDays(1))
                .endAt(LocalDateTime.now().plusDays(1).plusHours(1))
                .status(TimeSlotStatus.AVAILABLE)
                .build();
        setId(availableSlot, 100L);
    }

    @Test
    void createBooking_success_whenSlotAvailableAndClientExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(timeSlotRepository.findById(100L)).thenReturn(Optional.of(availableSlot));

        Booking saved = Booking.builder()
                .client(client).trainer(trainer).timeSlot(availableSlot)
                .status(BookingStatus.CONFIRMED).build();
        setId(saved, 200L);
        when(bookingRepository.save(any())).thenReturn(saved);

        CreateBookingRequest req = new CreateBookingRequest(100L);
        BookingResponse response = bookingService.createBooking(1L, req);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        verify(timeSlotRepository).save(argThat(s -> s.getStatus() == TimeSlotStatus.BOOKED));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void createBooking_throwsSlotNotAvailable_whenSlotAlreadyBooked() {
        availableSlot.setStatus(TimeSlotStatus.BOOKED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(timeSlotRepository.findById(100L)).thenReturn(Optional.of(availableSlot));

        assertThatThrownBy(() -> bookingService.createBooking(1L, new CreateBookingRequest(100L)))
                .isInstanceOf(SlotNotAvailableException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void cancelBooking_success_freesSlotAndPublishesEvent() {
        Booking booking = Booking.builder()
                .client(client).trainer(trainer).timeSlot(availableSlot)
                .status(BookingStatus.CONFIRMED).build();
        setId(booking, 200L);
        availableSlot.setStatus(TimeSlotStatus.BOOKED);

        when(bookingRepository.findById(200L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bookingService.cancelBooking(1L, 200L);

        ArgumentCaptor<TimeSlot> slotCaptor = ArgumentCaptor.forClass(TimeSlot.class);
        verify(timeSlotRepository).save(slotCaptor.capture());
        assertThat(slotCaptor.getValue().getStatus()).isEqualTo(TimeSlotStatus.AVAILABLE);
        verify(eventPublisher).publishEvent(any());
    }

    // Utility: set id via reflection since Lombok @Builder doesn't expose id setter for auto-generated ids
    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getSuperclass() != Object.class
                    ? findField(entity.getClass()) : entity.getClass().getDeclaredField("id");
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