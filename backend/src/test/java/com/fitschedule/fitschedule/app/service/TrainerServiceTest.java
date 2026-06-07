package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.response.TrainerSummaryResponse;
import com.fitschedule.fitschedule.app.model.entity.Trainer;
import com.fitschedule.fitschedule.app.repository.TimeSlotRepository;
import com.fitschedule.fitschedule.app.repository.TrainerRepository;
import com.fitschedule.fitschedule.app.service.filter.DateRangeFilterStrategy;
import com.fitschedule.fitschedule.app.service.filter.SpecializationFilterStrategy;
import com.fitschedule.fitschedule.app.service.filter.TrainerFilterStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TimeSlotRepository timeSlotRepository;

    private TrainerService trainerService;

    private Trainer yogaTrainer;
    private Trainer strengthTrainer;

    @BeforeEach
    void setUp() {
        List<TrainerFilterStrategy> strategies = List.of(
                new SpecializationFilterStrategy(),
                new DateRangeFilterStrategy(timeSlotRepository)
        );
        trainerService = new TrainerService(trainerRepository, timeSlotRepository, strategies);

        yogaTrainer = Trainer.builder()
                .firstName("Amina").lastName("Hodžić")
                .specialization("Yoga").active(true).build();

        strengthTrainer = Trainer.builder()
                .firstName("Marko").lastName("Kovač")
                .specialization("Strength").active(true).build();
    }

    @Test
    void getAllTrainers_filteredBySpecialization_returnsOnlyMatchingTrainers() {
        when(trainerRepository.findAllByActiveTrue()).thenReturn(List.of(yogaTrainer, strengthTrainer));

        List<TrainerSummaryResponse> result = trainerService.getAllTrainers("Yoga");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialization()).isEqualTo("Yoga");
        assertThat(result.get(0).getFirstName()).isEqualTo("Amina");
    }

    @Test
    void getAllTrainers_noFilter_returnsAllActiveTrainers() {
        when(trainerRepository.findAllByActiveTrue()).thenReturn(List.of(yogaTrainer, strengthTrainer));

        List<TrainerSummaryResponse> result = trainerService.getAllTrainers(null);

        assertThat(result).hasSize(2);
    }
}
