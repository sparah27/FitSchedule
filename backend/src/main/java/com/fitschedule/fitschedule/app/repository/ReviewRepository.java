package com.fitschedule.fitschedule.app.repository;

import com.fitschedule.fitschedule.app.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTrainerIdOrderByCreatedAtDesc(Long trainerId);

    boolean existsByBookingId(Long bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.trainer.id = :trainerId")
    Double findAverageRatingByTrainerId(@Param("trainerId") Long trainerId);

    long countByTrainerId(Long trainerId);
}