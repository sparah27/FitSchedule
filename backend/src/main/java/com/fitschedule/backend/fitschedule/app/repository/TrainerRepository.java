package com.fitschedule.backend.fitschedule.app.repository;

import com.fitschedule.backend.fitschedule.app.model.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    List<Trainer> findAllByActiveTrue();

    List<Trainer> findAllByActiveTrueAndSpecializationIgnoreCase(String specialization);
}