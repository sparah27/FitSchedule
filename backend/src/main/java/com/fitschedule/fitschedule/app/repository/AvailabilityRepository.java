package com.fitschedule.fitschedule.app.repository;

import com.fitschedule.fitschedule.app.model.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByTrainerId(Long trainerId);
}