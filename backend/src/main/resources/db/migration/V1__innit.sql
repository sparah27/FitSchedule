-- ============================================
-- V1: FitSchedule initial schema
-- ============================================

-- USERS (single table inheritance: clients + trainers)
CREATE TABLE users (
                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_type       VARCHAR(20)   NOT NULL,
                       email           VARCHAR(255)  NOT NULL UNIQUE,
                       password_hash   VARCHAR(255)  NOT NULL,
                       first_name      VARCHAR(100)  NOT NULL,
                       last_name       VARCHAR(100)  NOT NULL,
                       phone           VARCHAR(30),
                       photo_url       VARCHAR(500),
                       active          BOOLEAN       NOT NULL DEFAULT TRUE,
                       created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Trainer-only fields (NULL for clients)
                       bio             TEXT,
                       specialization  VARCHAR(100),
                       certifications  TEXT,

                       INDEX idx_users_email (email),
                       INDEX idx_users_type (user_type),
                       INDEX idx_users_specialization (specialization)
) ENGINE=InnoDB;

-- AVAILABILITIES (trainer's weekly recurring availability)
CREATE TABLE availabilities (
                                id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                trainer_id      BIGINT        NOT NULL,
                                day_of_week     VARCHAR(10)   NOT NULL,  -- MONDAY, TUESDAY, ...
                                start_time      TIME          NOT NULL,
                                end_time        TIME          NOT NULL,
                                created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_availability_trainer
                                    FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE CASCADE,
                                CONSTRAINT chk_availability_time CHECK (start_time < end_time),

                                INDEX idx_availability_trainer (trainer_id),
                                INDEX idx_availability_day (day_of_week)
) ENGINE=InnoDB;

-- TIME SLOTS (concrete bookable time windows)
CREATE TABLE time_slots (
                            id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                            trainer_id      BIGINT        NOT NULL,
                            start_at        DATETIME      NOT NULL,
                            end_at          DATETIME      NOT NULL,
                            status          VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE, BOOKED, BLOCKED

                            CONSTRAINT fk_slot_trainer
                                FOREIGN KEY (trainer_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT chk_slot_time CHECK (start_at < end_at),
                            CONSTRAINT uq_slot_trainer_start UNIQUE (trainer_id, start_at),

                            INDEX idx_slot_trainer (trainer_id),
                            INDEX idx_slot_start (start_at),
                            INDEX idx_slot_status (status)
) ENGINE=InnoDB;

-- BOOKINGS
CREATE TABLE bookings (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          client_id       BIGINT        NOT NULL,
                          trainer_id      BIGINT        NOT NULL,
                          time_slot_id    BIGINT        NOT NULL UNIQUE,  -- one booking per slot
                          status          VARCHAR(20)   NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CANCELLED, COMPLETED
                          created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          cancelled_at    DATETIME,

                          CONSTRAINT fk_booking_client
                              FOREIGN KEY (client_id) REFERENCES users(id),
                          CONSTRAINT fk_booking_trainer
                              FOREIGN KEY (trainer_id) REFERENCES users(id),
                          CONSTRAINT fk_booking_slot
                              FOREIGN KEY (time_slot_id) REFERENCES time_slots(id),

                          INDEX idx_booking_client (client_id),
                          INDEX idx_booking_trainer (trainer_id),
                          INDEX idx_booking_status (status)
) ENGINE=InnoDB;

-- NOTIFICATIONS
CREATE TABLE notifications (
                               id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id         BIGINT        NOT NULL,
                               type            VARCHAR(30)   NOT NULL,  -- BOOKING_CONFIRMED, BOOKING_CANCELLED, ...
                               message         VARCHAR(500)  NOT NULL,
                               related_id      BIGINT,                  -- e.g. booking_id (loose reference)
                               read_at         DATETIME,
                               created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_notification_user
                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

                               INDEX idx_notif_user (user_id),
                               INDEX idx_notif_unread (user_id, read_at)
) ENGINE=InnoDB;