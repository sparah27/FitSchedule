package com.fitschedule.fitschedule.app.service;

import com.fitschedule.fitschedule.app.dto.request.CreateReviewRequest;
import com.fitschedule.fitschedule.app.dto.response.ReviewResponse;
import com.fitschedule.fitschedule.app.exception.ForbiddenActionException;
import com.fitschedule.fitschedule.app.exception.ResourceNotFoundException;
import com.fitschedule.fitschedule.app.exception.ReviewAlreadyExistsException;
import com.fitschedule.fitschedule.app.model.entity.Booking;
import com.fitschedule.fitschedule.app.model.entity.Client;
import com.fitschedule.fitschedule.app.model.entity.Review;
import com.fitschedule.fitschedule.app.model.enums.BookingStatus;
import com.fitschedule.fitschedule.app.repository.BookingRepository;
import com.fitschedule.fitschedule.app.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public ReviewResponse submitReview(Long clientUserId, Long bookingId, CreateReviewRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        if (!(booking.getClient() instanceof Client client) || !client.getId().equals(clientUserId)) {
            throw new ForbiddenActionException("You can only review your own bookings");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ForbiddenActionException("You can only review a completed session");
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new ReviewAlreadyExistsException(bookingId);
        }

        Review review = Review.builder()
                .client(client)
                .trainer(booking.getTrainer())
                .booking(booking)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForTrainer(Long trainerId) {
        return reviewRepository.findByTrainerIdOrderByCreatedAtDesc(trainerId)
                .stream()
                .map(ReviewResponse::fromEntity)
                .toList();
    }
}