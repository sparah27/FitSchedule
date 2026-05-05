package com.fitschedule.backend.fitschedule.app.dto.response;

import com.fitschedule.backend.fitschedule.app.model.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerSummaryResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private String specialization;
    private Double averageRating;

    public static TrainerSummaryResponse fromEntity(Trainer trainer) {
        return TrainerSummaryResponse.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .photoUrl(trainer.getPhotoUrl())
                .specialization(trainer.getSpecialization())
                .averageRating(null) // placeholder — reviews are Release 2
                .build();
    }
}