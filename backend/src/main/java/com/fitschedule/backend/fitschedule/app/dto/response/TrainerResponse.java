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
public class TrainerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private String specialization;
    private String bio;
    private String certifications;
    private Double averageRating;
    private Integer reviewCount;

    public static TrainerResponse fromEntity(Trainer trainer) {
        return TrainerResponse.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .photoUrl(trainer.getPhotoUrl())
                .specialization(trainer.getSpecialization())
                .bio(trainer.getBio())
                .certifications(trainer.getCertifications())
                .averageRating(null)  // Release 2
                .reviewCount(0)        // Release 2
                .build();
    }
}