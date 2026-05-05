-- Seed: 5 sample trainers for development & demo
-- Password for all: Test1234! (BCrypt hash below)
-- Hash generated with bcrypt strength 10

INSERT INTO users (
    user_type, email, password_hash, first_name, last_name,
    phone, photo_url, active, created_at,
    bio, specialization, certifications
) VALUES
      (
          'TRAINER',
          'marko.strength@fitschedule.com',
          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
          'Marko', 'Kovač',
          '+387 61 200 001',
          'https://i.pravatar.cc/300?img=12',
          true, NOW(),
          'Certified strength and conditioning coach with 8 years of experience working with both beginners and competitive athletes.',
          'Strength',
          'NSCA-CSCS, CrossFit Level 2'
      ),
      (
          'TRAINER',
          'amina.yoga@fitschedule.com',
          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
          'Amina', 'Hodžić',
          '+387 61 200 002',
          'https://i.pravatar.cc/300?img=47',
          true, NOW(),
          'Yoga instructor specializing in vinyasa and restorative practices. Helps clients build flexibility, strength, and mindfulness.',
          'Yoga',
          'RYT-500, Yin Yoga Certified'
      ),
      (
          'TRAINER',
          'damir.cardio@fitschedule.com',
          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
          'Damir', 'Begić',
          '+387 61 200 003',
          'https://i.pravatar.cc/300?img=33',
          true, NOW(),
          'Endurance and cardio coach. Former marathon runner. Designs programs for fat loss, endurance building, and general fitness.',
          'Cardio',
          'ACE-CPT, RRCA Running Coach'
      ),
      (
          'TRAINER',
          'lejla.pilates@fitschedule.com',
          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
          'Lejla', 'Mujić',
          '+387 61 200 004',
          'https://i.pravatar.cc/300?img=49',
          true, NOW(),
          'Pilates instructor with a focus on rehabilitation and posture correction. Works with clients recovering from injuries.',
          'Pilates',
          'PMA Certified, Stott Pilates Level 3'
      ),
      (
          'TRAINER',
          'tarik.crossfit@fitschedule.com',
          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
          'Tarik', 'Selimović',
          '+387 61 200 005',
          'https://i.pravatar.cc/300?img=15',
          true, NOW(),
          'CrossFit and functional fitness coach. High-intensity workouts for serious athletes. No shortcuts, no excuses.',
          'Strength',
          'CrossFit Level 3, USAW Sports Performance'
      );