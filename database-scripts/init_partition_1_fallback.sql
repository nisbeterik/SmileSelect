-- Create the `appointment` table if it does not exist
CREATE TABLE IF NOT EXISTS appointment (
       id SERIAL PRIMARY KEY,
       patient_id BIGINT,
       dentist_id BIGINT NOT NULL,
       clinic_id BIGINT NOT NULL,
       start_time TIMESTAMP NOT NULL,
       end_time TIMESTAMP NOT NULL
);

-- Check if the subscription exists before creating it
DO $$
BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_subscription WHERE subname = 'appointment_sub_1'
        ) THEN
            RAISE NOTICE 'Subscription appointment_sub_1 will be created outside the DO block';
END IF;
END $$;

-- Create the subscription for partition 1 fallback
CREATE SUBSCRIPTION appointment_sub_1
    CONNECTION 'host=partition1-primary-db port=5432 user=postgres password=postgres dbname=appointmentDB_partition_1'
    PUBLICATION appointment_pub_1;
