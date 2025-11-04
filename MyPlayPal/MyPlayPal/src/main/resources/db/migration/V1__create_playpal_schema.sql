-- ================================================
-- USERS TABLE
-- ================================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    contact VARCHAR(15) UNIQUE,
    city VARCHAR(100),
    state VARCHAR(100),
    profile_picture_url VARCHAR(255),
    age INT,
    gender VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================================
-- MANAGERS TABLE
-- ================================================
CREATE TABLE managers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    managername VARCHAR(100) NOT NULL,
    contact VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- ================================================
-- SPORTS TABLE
-- ================================================
CREATE TABLE sports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sportname VARCHAR(100) NOT NULL,
    sport_image_url VARCHAR(255)
);

-- ================================================
-- VENUES TABLE
-- ================================================
CREATE TABLE venues (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    venuename VARCHAR(150) NOT NULL,
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    rating DOUBLE,
    manager_id BIGINT,
    venue_image_url VARCHAR(255),
    CONSTRAINT fk_venue_manager FOREIGN KEY (manager_id)
        REFERENCES managers(id)
        ON DELETE SET NULL
);

-- ================================================
-- COURTS TABLE
-- ================================================
CREATE TABLE courts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    courtname VARCHAR(100) NOT NULL,
    venue_id BIGINT,
    sport_id BIGINT,
    hourly_rate DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    is_bookable BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_court_venue FOREIGN KEY (venue_id)
        REFERENCES venues(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_court_sport FOREIGN KEY (sport_id)
        REFERENCES sports(id)
        ON DELETE CASCADE
);

-- ================================================
-- COURT SLOTS TABLE
-- ================================================
CREATE TABLE court_slots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    court_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    CONSTRAINT fk_slot_court FOREIGN KEY (court_id)
        REFERENCES courts(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_court_slot UNIQUE (court_id, start_time, end_time)
);

-- ================================================
-- BOOKINGS TABLE
-- ================================================
CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(10,2),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id)
        REFERENCES court_slots(id)
        ON DELETE CASCADE
);

-- ================================================
-- EVENTS TABLE (UPDATED)
-- ================================================
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(150) NOT NULL,
    organizer_user_id BIGINT,
    sport_id BIGINT,
    venue_id BIGINT,  -- ✅ NEW COLUMN for linking event to venue
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    max_players INT,
    current_players INT DEFAULT 0,
    description TEXT,
    skill_level_required VARCHAR(100),
    entry_fee DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(10,2),

    CONSTRAINT fk_event_organizer FOREIGN KEY (organizer_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_event_sport FOREIGN KEY (sport_id)
        REFERENCES sports(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id)  -- ✅ NEW FOREIGN KEY
        REFERENCES venues(id)
        ON DELETE CASCADE
);

-- ================================================
-- EVENT SLOTS (Many-to-Many between EVENTS and COURT_SLOTS)
-- ================================================
CREATE TABLE event_slots (
    event_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, slot_id),
    CONSTRAINT fk_event_slot_event FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_event_slot_slot FOREIGN KEY (slot_id)
        REFERENCES court_slots(id)
        ON DELETE CASCADE
);

-- ================================================
-- EVENT PARTICIPANTS TABLE
-- ================================================
CREATE TABLE event_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'JOINED',
    CONSTRAINT uq_event_user UNIQUE (event_id, user_id),
    CONSTRAINT fk_participant_event FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ================================================
-- USER_SPORT TABLE (Many-to-Many)
-- ================================================
CREATE TABLE user_sport (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    sport_id BIGINT NOT NULL,
    skill_level VARCHAR(50),
    CONSTRAINT uq_user_sport UNIQUE (user_id, sport_id),
    CONSTRAINT fk_user_sport_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_sport_sport FOREIGN KEY (sport_id)
        REFERENCES sports(id)
        ON DELETE CASCADE
);

-- ================================================
-- PAYMENT TRANSACTIONS TABLE
-- ================================================
CREATE TABLE payment_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    booking_id BIGINT UNIQUE,
    event_id BIGINT UNIQUE,
    participant_id BIGINT UNIQUE,
    amount DECIMAL(10,2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'INITIATED',
    reference_id VARCHAR(100),
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id)
        REFERENCES bookings(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_payment_event FOREIGN KEY (event_id)
        REFERENCES events(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_payment_participant FOREIGN KEY (participant_id)
        REFERENCES event_participants(id)
        ON DELETE CASCADE
);

-- ================================================
-- REVIEWS & RATINGS TABLE
-- ================================================
CREATE TABLE reviews_ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    venue_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_venue FOREIGN KEY (venue_id)
        REFERENCES venues(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ================================================
-- NOTIFICATIONS TABLE
-- ================================================
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    message VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ================================================
-- CONTACT MESSAGES TABLE
-- ================================================
CREATE TABLE contact_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    subject VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);