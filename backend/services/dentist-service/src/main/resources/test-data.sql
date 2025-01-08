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

-- Clean up any existing data
DELETE FROM clinic;

-- Insert sample data into the clinic table
INSERT INTO clinic (name, longitude, latitude, street, zip, city, house_number)
VALUES
    ('Smile Select Clinic', -122.4194, 37.7749, 'Market Street', 94103, 'San Francisco', '101'),
    ('Happy Teeth Clinic', -118.2437, 34.0522, 'Broadway', 90012, 'Los Angeles', '202'),
    ('Bright Smile Clinic', -74.0060, 40.7128, 'Wall Street', 10005, 'New York', '303');

