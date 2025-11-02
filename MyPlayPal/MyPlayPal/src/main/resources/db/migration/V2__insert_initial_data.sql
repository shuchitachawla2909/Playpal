-- ===========================================================
-- USERS
-- ===========================================================
INSERT INTO users (username, email, password, contact, city, state, profile_picture_url, age, gender)
VALUES
('john_doe', 'john@example.com', 'password123', '9876543210', 'Varanasi', 'UP', 'https://example.com/john.jpg', 25, 'Male'),
('jane_smith', 'jane@example.com', 'password123', '9876543211', 'Varanasi', 'UP', 'https://example.com/jane.jpg', 23, 'Female'),
('rohit_sharma', 'rohit@example.com', 'password123', '9876543212', 'Lucknow', 'UP', 'https://example.com/rohit.jpg', 28, 'Male');

-- ===========================================================
-- MANAGERS
-- ===========================================================
INSERT INTO managers (managername, contact, email, password)
VALUES
('Rajesh Kumar', '9876500010', 'rajesh@example.com', 'managerPass'),
('Anita Singh', '9876500011', 'anita@example.com', 'managerPass');

-- ===========================================================
-- SPORTS
-- ===========================================================
INSERT INTO sports (sportname, sport_image_url)
VALUES
('Tennis', 'https://images.pexels.com/photos/5739121/pexels-photo-5739121.jpeg'),
('Badminton', 'https://images.pexels.com/photos/8496267/pexels-photo-8496267.jpeg'),
('Football', 'https://images.pexels.com/photos/16543164/pexels-photo-16543164.jpeg');

-- ===========================================================
-- VENUES
-- ===========================================================
INSERT INTO venues (venuename, street, city, state, pincode, manager_id, rating, venue_image_url)
VALUES
('Central Sports Club', 'MG Road', 'Varanasi', 'UP', '221001', 1, 4.5, 'https://images.pexels.com/photos/25724405/pexels-photo-25724405.jpeg'),
('Eastside Arena', 'Kashi Road', 'Varanasi', 'UP', '221002', 2, 4.0, 'https://images.pexels.com/photos/2277981/pexels-photo-2277981.jpeg');

-- ===========================================================
-- COURTS
-- ===========================================================
INSERT INTO courts (courtname, venue_id, sport_id, hourly_rate, is_bookable)
VALUES
('Tennis Court 1', 1, 1, 200, TRUE),
('Badminton Court A', 1, 2, 150, TRUE),
('Football Ground', 2, 3, 500, TRUE);

-- ===========================================================
-- COURT SLOTS
-- ===========================================================
INSERT INTO court_slots (court_id, start_time, end_time, status)
VALUES
(1, '2025-10-30 08:00:00', '2025-10-30 09:00:00', 'AVAILABLE'),
(1, '2025-10-30 09:00:00', '2025-10-30 10:00:00', 'AVAILABLE'),
(2, '2025-10-30 08:00:00', '2025-10-30 09:00:00', 'AVAILABLE'),
(2, '2025-10-30 09:00:00', '2025-10-30 10:00:00', 'BOOKED'),
(3, '2025-10-30 17:00:00', '2025-10-30 18:00:00', 'AVAILABLE');

-- ===========================================================
-- BOOKINGS
-- ===========================================================
INSERT INTO bookings (user_id, slot_id, booking_date, status, total_amount)
VALUES
(1, 2, NOW(), 'CONFIRMED', 200.00),
(2, 4, NOW(), 'PENDING', 150.00);

-- ===========================================================
-- EVENTS
-- ===========================================================
INSERT INTO events (event_name, organizer_user_id, sport_id, venue_id, booking_date, max_players, current_players, description, skill_level_required, entry_fee, status, total_amount)
VALUES
('Morning Football Match', 3, 3, 2, NOW(), 22, 10, 'Friendly football match at Eastside Arena', 'Intermediate', 100.00, 'CONFIRMED', 2200.00),
('Evening Badminton Doubles', 2, 2, 1, NOW(), 8, 4, 'Doubles match for club members', 'Beginner', 50.00, 'PENDING', 400.00);

-- ===========================================================
-- EVENT SLOTS
-- ===========================================================
INSERT INTO event_slots (event_id, slot_id)
VALUES
(1, 5),
(2, 3);

-- ===========================================================
-- EVENT PARTICIPANTS
-- ===========================================================
INSERT INTO event_participants (event_id, user_id, join_date, status)
VALUES
(1, 1, NOW(), 'JOINED'),
(1, 2, NOW(), 'JOINED'),
(2, 3, NOW(), 'PENDING');

-- ===========================================================
-- PAYMENT TRANSACTIONS
-- ===========================================================
INSERT INTO payment_transactions (user_id, booking_id, amount, status, reference_id)
VALUES
(1, 1, 200.00, 'SUCCESS', 'TXN001'),
(2, 2, 150.00, 'INITIATED', 'TXN002');

INSERT INTO payment_transactions (user_id, event_id, amount, status, reference_id)
VALUES
(3, 1, 2200.00, 'SUCCESS', 'TXN003');

-- ===========================================================
-- REVIEWS & RATINGS
-- ===========================================================
INSERT INTO reviews_ratings (venue_id, user_id, rating, comment)
VALUES
(1, 1, 5, 'Excellent courts and maintenance!'),
(2, 2, 4, 'Good experience overall.');

-- ===========================================================
-- NOTIFICATIONS
-- ===========================================================
INSERT INTO notifications (user_id, message, is_read)
VALUES
(1, 'Your booking for Tennis Court 1 is confirmed.', FALSE),
(2, 'Your payment for Badminton Court A is pending.', FALSE);

-- ===========================================================
-- USER_SPORT
-- ===========================================================
INSERT INTO user_sport (user_id, sport_id, skill_level)
VALUES
(1, 1, 'Intermediate'),
(1, 2, 'Beginner'),
(2, 2, 'Intermediate'),
(3, 3, 'Advanced');
