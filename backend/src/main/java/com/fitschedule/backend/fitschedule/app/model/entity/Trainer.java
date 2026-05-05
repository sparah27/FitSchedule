package com.fitschedule.backend.fitschedule.app.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TRAINER")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Trainer extends User {

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 100)
    private String specialization;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Availability> availabilities = new ArrayList<>();

    @OneToMany(mappedBy = "trainer")
    @Builder.Default
    private List<TimeSlot> timeSlots = new ArrayList<>();
}