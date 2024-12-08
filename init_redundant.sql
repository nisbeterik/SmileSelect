-- Create the `appointment` table if it does not exist
CREATE TABLE IF NOT EXISTS appointment (
   id SERIAL PRIMARY KEY,
   patient_id BIGINT,
   dentist_id BIGINT NOT NULL,
   start_time TIMESTAMP NOT NULL,
   end_time TIMESTAMP NOT NULL
);

-- Check if the subscription exists before creating it
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_subscription WHERE subname = 'appointment_sub'
        ) THEN
            -- Flag a notice so the user knows subscription creation is deferred
            RAISE NOTICE 'Subscription will be created outside the DO block';
        END IF;
    END $$;

-- Create the subscription outside the DO block
CREATE SUBSCRIPTION appointment_sub
    CONNECTION 'host=primary_db port=5432 user=postgres password=postgres dbname=appointmentDB'
    PUBLICATION appointment_pub;
