-- init_counter.sql
CREATE TABLE IF NOT EXISTS partition_counter (
     id SERIAL PRIMARY KEY,
     counter INT NOT NULL,
     partition_0_count INT NOT NULL DEFAULT 0,
     partition_1_count INT NOT NULL DEFAULT 0,
     partition_2_count INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE IF NOT EXISTS global_appointment_seq
  START WITH 1
  INCREMENT BY 1;

INSERT INTO partition_counter (counter, partition_0_count, partition_1_count, partition_2_count)
SELECT 0, 0, 0, 0
    WHERE NOT EXISTS (SELECT 1 FROM partition_counter);
