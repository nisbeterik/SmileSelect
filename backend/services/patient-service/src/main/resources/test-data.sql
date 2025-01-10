
-- Create the patient table
CREATE TABLE IF NOT EXISTS patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    role VARCHAR(50) DEFAULT 'PATIENT'
);

DELETE FROM patient;

INSERT INTO patient (first_name, last_name, email, password, date_of_birth)
VALUES
    ('Bad', 'Teeth', 'bad.teeth@example.com', 'Password123?', '1999-01-01'),
    ('Bad', 'Breath', 'bad.breath@example.com', 'Password123?', '1999-01-01'),
    ('No', 'Teeth', 'no.teeth@example.com', 'Password123?', '1999-01-01');
