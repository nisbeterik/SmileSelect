-- Create the clinic table
CREATE TABLE IF NOT EXISTS clinic (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      name VARCHAR(255) NOT NULL,
                                      longitude DOUBLE NOT NULL,
                                      latitude DOUBLE NOT NULL,
                                      street VARCHAR(255) NOT NULL,
                                      zip INT NOT NULL,
                                      city VARCHAR(255) NOT NULL,
                                      house_number VARCHAR(50) NOT NULL
);

-- Create the dentist table
CREATE TABLE IF NOT EXISTS dentist (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       first_name VARCHAR(50) NOT NULL,
                                       last_name VARCHAR(50) NOT NULL,
                                       email VARCHAR(100) NOT NULL UNIQUE,
                                       password VARCHAR(255) NOT NULL,
                                       clinic_id INT,
                                       FOREIGN KEY (clinic_id) REFERENCES clinic(id)
);

-- Clean up any existing data
DELETE FROM dentist;
DELETE FROM clinic;

-- Insert sample data into the clinic table
INSERT INTO clinic (name, longitude, latitude, street, zip, city, house_number)
VALUES
    ('Smile Select Clinic', -122.4194, 37.7749, 'Market Street', 94103, 'San Francisco', '101'),
    ('Happy Teeth Clinic', -118.2437, 34.0522, 'Broadway', 90012, 'Los Angeles', '202'),
    ('Bright Smile Clinic', -74.0060, 40.7128, 'Wall Street', 10005, 'New York', '303');

-- Insert sample data into the dentist table with dynamic clinic IDs
INSERT INTO dentist (first_name, last_name, email, password, clinic_id)
VALUES
    ('Tooth', 'Ache', 'tooth.ache@example.com', 'securepassword123', (SELECT id FROM clinic WHERE name = 'Smile Select Clinic')),
    ('Brush', 'Yawrteeth', 'brush.yawrteeth@example.com', 'mypassword456', (SELECT id FROM clinic WHERE name = 'Happy Teeth Clinic')),
    ('Emily', 'Johnson', 'emily.johnson@example.com', 'passw0rd789', (SELECT id FROM clinic WHERE name = 'Bright Smile Clinic')),
    ('Michael', 'Brown', 'michael.brown@example.com', 'passwordABC', (SELECT id FROM clinic WHERE name = 'Smile Select Clinic')),
    ('Laura', 'Wilson', 'laura.wilson@example.com', 'passwordXYZ', (SELECT id FROM clinic WHERE name = 'Happy Teeth Clinic'));
