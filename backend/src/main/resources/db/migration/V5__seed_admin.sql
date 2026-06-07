INSERT INTO users (
    user_type, email, password_hash, first_name, last_name,
    phone, photo_url, active, created_at,
    bio, specialization, certifications
) VALUES (
    'ADMIN',
    'admin@fitschedule.com',
    '$2a$10$Z37iu.9gqxR4CR9UeYYJpeo/NsA5AUL.Gpokh2zoqksFCtaN.ws0G',
    'Admin', 'FitSchedule',
    NULL, NULL, true, NOW(),
    NULL, NULL, NULL
);