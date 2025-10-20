-- V2__insert_initial_data.sql
-- MODIFIED to use corrected schema names (snake_case in SQL)
-- and include the payment_transactions reference_id column.

-- ---------------------------
-- Insert sample users
-- ---------------------------
INSERT INTO users (username, email, password, contact, city, state, profile_picture_url, age, gender)
VALUES
('john_doe', 'john@example.com', 'password123', '9876543210', 'Varanasi', 'UP', 'https://example.com/john.jpg', 25, 'Male'),
('jane_smith', 'jane@example.com', 'password123', '9876543211', 'Varanasi', 'UP', 'https://example.com/jane.jpg', 23, 'Female');

-- ---------------------------
-- Insert sample managers
-- ---------------------------
-- Column name is 'name' (correct).
INSERT INTO managers (name, contact, email, password)
VALUES
('Rajesh Kumar', '9876500010', 'rajesh@example.com', 'managerPass'),
('Anita Singh', '9876500011', 'anita@example.com', 'managerPass');

-- ---------------------------
-- Insert sample sports
-- ---------------------------
-- Column name is 'sport_name' (correct).
INSERT INTO sports (sport_name, sport_image_url)
VALUES
('Tennis', 'https://images.pexels.com/photos/5739121/pexels-photo-5739121.jpeg'),
('Badminton', 'https://images.pexels.com/photos/8496267/pexels-photo-8496267.jpeg'),
('Football', 'https://images.pexels.com/photos/16543164/pexels-photo-16543164.jpeg');

-- ---------------------------
-- Insert sample venues
-- ---------------------------
-- Columns are 'venue_name' and 'pin_code' (correct).
INSERT INTO venues (venue_name, street, city, state, pin_code, manager_id, rating, venue_image_url)
VALUES
('Central Sports Club', 'MG Road', 'Varanasi', 'UP', '221001', 1, 4.5, 'https://images.pexels.com/photos/25724405/pexels-photo-25724405.jpeg'),
('Eastside Arena', 'Kashi Road', 'Varanasi', 'UP', '221002', 2, 4.0, 'https://images.pexels.com/photos/2277981/pexels-photo-2277981.jpeg');

-- ---------------------------
-- Insert sample courts
-- ---------------------------
-- Column name is 'court_name' (correct).
INSERT INTO courts (court_name, venue_id, sport_id, hourly_rate, is_bookable)
VALUES
('Tennis Court 1', 1, 1, 200, TRUE),
('Badminton Court A', 1, 2, 150, TRUE),
('Football Ground', 2, 3, 500, TRUE);

-- ---------------------------
-- Insert sample court_slots
-- ---------------------------
INSERT INTO court_slots (court_id, start_time, end_time, status)
VALUES
(1, '2025-10-07 08:00:00', '2025-10-07 09:00:00', 'AVAILABLE'),
(1, '2025-10-07 09:00:00', '2025-10-07 10:00:00', 'AVAILABLE'),
(2, '2025-10-07 10:00:00', '2025-10-07 11:00:00', 'AVAILABLE');

-- ---------------------------
-- Insert sample bookings
-- ---------------------------
INSERT INTO bookings (user_id, slot_id, status, total_amount)
VALUES
(1, 1, 'CONFIRMED', 200),
(2, 3, 'PENDING', 150);

-- ---------------------------
-- Insert sample events
-- ---------------------------
INSERT INTO events (event_name, organizer_user_id, sport_id, venue_id, start_time, end_time, max_players, current_players, description, skill_level_required, entry_fee)
VALUES
('Weekend Tennis Tournament', 1, 1, 1, '2025-10-10 09:00:00', '2025-10-10 17:00:00', 16, 4, 'Friendly tennis tournament', 'Intermediate', 100),
('Badminton Fun Day', 2, 2, 1, '2025-10-12 10:00:00', '2025-10-12 15:00:00', 20, 5, 'Casual badminton event', 'Beginner', 50);

-- ---------------------------
-- Insert sample event participants
-- ---------------------------
INSERT INTO event_participants (event_id, user_id, status)
VALUES
(1, 1, 'JOINED'),
(1, 2, 'JOINED'),
(2, 2, 'JOINED');

-- ---------------------------
-- Insert sample notifications
-- ---------------------------
INSERT INTO notifications (user_id, message)
VALUES
(1, 'Your booking for Tennis Court 1 is confirmed!'),
(2, 'Reminder: Badminton Fun Day is coming up!');

-- ---------------------------
-- Insert sample payment_transactions
-- ---------------------------
-- ⭐ CRITICAL FIX: Include the new 'reference_id' column
INSERT INTO payment_transactions (user_id, booking_id, amount, status, reference_id)
VALUES
(1, 1, 200, 'SUCCESS', 'PAY-REF-XYZ123'),
(2, 2, 150, 'INITIATED', 'PAY-REF-ABC456');

-- ---------------------------
-- Insert sample reviews_ratings
-- ---------------------------
INSERT INTO reviews_ratings (venue_id, user_id, rating, comment)
VALUES
(1, 1, 5, 'Great venue and well-maintained courts.'),
(2, 2, 4, 'Good experience, but parking was limited.');

-- ---------------------------
-- Insert sample user_sport
-- ---------------------------
INSERT INTO user_sport (user_id, sport_id, skill_level)
VALUES
(1, 1, 'Intermediate'),
(1, 2, 'Beginner'),
(2, 2, 'Beginner'),
(2, 3, 'Intermediate');