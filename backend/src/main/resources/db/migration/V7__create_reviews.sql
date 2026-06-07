CREATE TABLE reviews (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id   BIGINT      NOT NULL,
    trainer_id  BIGINT      NOT NULL,
    booking_id  BIGINT      NOT NULL UNIQUE,
    rating      TINYINT     NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     VARCHAR(1000),
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_client  FOREIGN KEY (client_id)  REFERENCES users(id),
    CONSTRAINT fk_review_trainer FOREIGN KEY (trainer_id) REFERENCES users(id),
    CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),

    INDEX idx_review_trainer (trainer_id),
    INDEX idx_review_client  (client_id)
) ENGINE=InnoDB;