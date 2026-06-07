package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.CreateTrainerRequest;
import com.fitschedule.fitschedule.app.dto.response.AdminStatsResponse;
import com.fitschedule.fitschedule.app.dto.response.BookingResponse;
import com.fitschedule.fitschedule.app.dto.response.TrainerResponse;
import com.fitschedule.fitschedule.app.exception.EmailAlreadyExistsException;
import com.fitschedule.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.repository.BookingRepository;
import com.fitschedule.fitschedule.app.repository.ClientRepository;
import com.fitschedule.fitschedule.app.repository.TrainerRepository;
import com.fitschedule.fitschedule.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TrainerRepository trainerRepository;
    private final ClientRepository clientRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TrainerResponse createTrainer(CreateTrainerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Trainer trainer = Trainer.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .bio(request.getBio())
                .specialization(request.getSpecialization())
                .certifications(request.getCertifications())
                .active(true)
                .build();

        return TrainerResponse.fromEntity(trainerRepository.save(trainer));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(BookingStatus status, LocalDate date) {
        return bookingRepository.findAll().stream()
                .filter(b -> status == null || b.getStatus() == status)
                .filter(b -> date == null || b.getTimeSlot().getStartAt().toLocalDate().equals(date))
                .sorted((a, b2) -> b2.getTimeSlot().getStartAt().compareTo(a.getTimeSlot().getStartAt()))
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional
    public TrainerResponse deactivateTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", trainerId));
        trainer.setActive(false);
        return TrainerResponse.fromEntity(trainerRepository.save(trainer));
    }

    @Transactional
    public TrainerResponse activateTrainer(Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", trainerId));
        trainer.setActive(true);
        return TrainerResponse.fromEntity(trainerRepository.save(trainer));
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        long totalThisMonth = bookingRepository.countByCreatedAtBetween(startOfMonth, now);
        long cancelledThisMonth = bookingRepository.countByStatusAndCreatedAtBetween(
                BookingStatus.CANCELLED, startOfMonth, now);
        long activeTrainers = trainerRepository.countByActiveTrue();
        long activeClients = clientRepository.countByActiveTrue();

        double cancellationRate = totalThisMonth > 0
                ? Math.round((double) cancelledThisMonth / totalThisMonth * 10000.0) / 100.0
                : 0.0;

        return AdminStatsResponse.builder()
                .totalBookingsThisMonth(totalThisMonth)
                .activeTrainers(activeTrainers)
                .activeClients(activeClients)
                .cancellationRateThisMonth(cancellationRate)
                .build();
    }
}