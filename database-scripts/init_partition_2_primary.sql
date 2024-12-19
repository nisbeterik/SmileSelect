-- Create table if not exists
CREATE TABLE IF NOT EXISTS appointment (
       id SERIAL PRIMARY KEY,
       patient_id BIGINT,
       dentist_id BIGINT NOT NULL,
       clinic_id BIGINT NOT NULL,
       start_time TIMESTAMP NOT NULL,
       end_time TIMESTAMP NOT NULL
);

-- Create materialized view
CREATE MATERIALIZED VIEW IF NOT EXISTS appointment_next_96_hours AS
SELECT * FROM appointment
WHERE start_time BETWEEN NOW() AND NOW() + INTERVAL '96 hours';

-- Setup logical replication publication for partition 2
CREATE PUBLICATION appointment_pub_2 FOR TABLE appointment;
