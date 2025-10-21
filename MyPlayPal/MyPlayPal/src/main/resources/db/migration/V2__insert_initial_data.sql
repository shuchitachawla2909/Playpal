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
INSERT INTO managers (managername, contact, email, password)
VALUES
('Rajesh Kumar', '9876500010', 'rajesh@example.com', 'managerPass'),
('Anita Singh', '9876500011', 'anita@example.com', 'managerPass');

-- ---------------------------
-- Insert sample sports
-- ---------------------------

INSERT INTO sports (sportname, sport_image_url)
VALUES
('Tennis', 'https://images.pexels.com/photos/5739121/pexels-photo-5739121.jpeg'),
('Badminton', 'https://images.pexels.com/photos/8496267/pexels-photo-8496267.jpeg'),
('Football', 'https://images.pexels.com/photos/16543164/pexels-photo-16543164.jpeg');

-- ---------------------------
-- Insert sample venues
-- ---------------------------
INSERT INTO venues (venuename, street, city, state, pincode, manager_id, rating, venue_image_url)
VALUES
('Central Sports Club', 'MG Road', 'Varanasi', 'UP', '221001', 1, 4.5, 'https://images.pexels.com/photos/25724405/pexels-photo-25724405.jpeg'),
('Eastside Arena', 'Kashi Road', 'Varanasi', 'UP', '221002', 1, 4.0, 'https://images.pexels.com/photos/2277981/pexels-photo-2277981.jpeg');

-- ---------------------------
-- Insert sample courts
-- ---------------------------
INSERT INTO courts (courtname, venue_id, sport_id, hourly_rate, is_bookable)
VALUES
('Tennis Court 1', 1, 1, 200, TRUE),
('Badminton Court A', 1, 2, 150, TRUE),
('Football Ground', 2, 3, 500, TRUE);
