-- V1__create_playpal_schema.sql

-- users
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(200) NOT NULL,
  password VARCHAR(255) NOT NULL,
  contact VARCHAR(30),
  city VARCHAR(100),
  state VARCHAR(100),
  profile_picture_url VARCHAR(500),
  age INT,
  gender VARCHAR(10),
  registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT uq_users_contact UNIQUE (contact),
  CONSTRAINT uq_users_username UNIQUE (username)
) ENGINE=InnoDB;

-- managers
CREATE TABLE managers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  managername VARCHAR(150),
  contact VARCHAR(30) UNIQUE,
  email VARCHAR(200) UNIQUE,
  password VARCHAR(255)
) ENGINE=InnoDB;

-- sports
CREATE TABLE sports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sportname VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- venues
CREATE TABLE venues (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  venuename VARCHAR(200) NOT NULL,
  street VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  pincode VARCHAR(20),
  manager_id BIGINT,
  rating DECIMAL(2,1) DEFAULT 0,
  CONSTRAINT fk_venues_manager FOREIGN KEY (manager_id) REFERENCES managers(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- courts
CREATE TABLE courts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  courtname VARCHAR(200) NOT NULL,
  venue_id BIGINT,
  sport_id BIGINT,
  hourly_rate DECIMAL(10,2) DEFAULT 0,
  is_bookable BOOLEAN DEFAULT TRUE,
  CONSTRAINT fk_courts_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
  CONSTRAINT fk_courts_sport FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- court_slots (slot-based availability)
CREATE TABLE court_slots (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  court_id BIGINT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE | RESERVED | BOOKED | MAINTENANCE
  CONSTRAINT fk_slots_court FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE CASCADE,
  CONSTRAINT ux_slot_unique UNIQUE (court_id, start_time, end_time)
) ENGINE=InnoDB;

-- bookings
CREATE TABLE bookings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  slot_id BIGINT NOT NULL,
  booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) DEFAULT 'PENDING', -- PENDING | CONFIRMED | CANCELLED
  total_amount DECIMAL(10,2),
  CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_bookings_slot FOREIGN KEY (slot_id) REFERENCES court_slots(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- events
CREATE TABLE events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_name VARCHAR(255) NOT NULL,
  organizer_user_id BIGINT,
  sport_id BIGINT,
  venue_id BIGINT,
  start_time DATETIME,
  end_time DATETIME,
  max_players INT,
  description TEXT,
  skill_level_required VARCHAR(50),
  entry_fee DECIMAL(10,2) DEFAULT 0,
  CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_user_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_events_sport FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE SET NULL,
  CONSTRAINT fk_events_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- event_participants
CREATE TABLE event_participants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) DEFAULT 'JOINED', -- JOINED | PENDING | CANCELLED
  CONSTRAINT fk_ep_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT fk_ep_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT ux_event_user UNIQUE (event_id, user_id)
) ENGINE=InnoDB;

-- notifications
CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  message TEXT,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- payment_transactions
CREATE TABLE payment_transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  booking_id BIGINT,
  amount DECIMAL(10,2),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(20) DEFAULT 'INITIATED', -- INITIATED | SUCCESS | FAILED | REFUNDED
  CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- reviews_ratings
CREATE TABLE reviews_ratings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  venue_id BIGINT,
  user_id BIGINT,
  rating INT,
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_venue FOREIGN KEY (venue_id) REFERENCES venues(id) ON DELETE CASCADE,
  CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- user_sport (associative)
CREATE TABLE user_sport (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  sport_id BIGINT,
  skill_level VARCHAR(50),
  CONSTRAINT fk_us_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_us_sport FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE CASCADE,
  CONSTRAINT ux_user_sport UNIQUE (user_id, sport_id)
) ENGINE=InnoDB;
