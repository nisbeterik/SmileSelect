package com.smile_select.appointment_service.service;

import com.smile_select.appointment_service.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.smile_select.appointment_service.mqtt.MqttGateway;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    private final MqttGateway mqttGateway;
    private final ObjectMapper objectMapper;
    private final CounterService counterService;

    // Partition 0 - Database 1
    private final JdbcTemplate p0Primary;
    private final JdbcTemplate p0Fallback;

    // Partition 1 - Database 2
    private final JdbcTemplate p1Primary;
    private final JdbcTemplate p1Fallback;

    // Partition 2 - Database 3
    private final JdbcTemplate p2Primary;
    private final JdbcTemplate p2Fallback;

    @Autowired
    public AppointmentService(
            CounterService counterService,
            MqttGateway mqttGateway,
            @Qualifier("partition0PrimaryJdbcTemplate") JdbcTemplate p0Primary,
            @Qualifier("partition0FallbackJdbcTemplate") JdbcTemplate p0Fallback,
            @Qualifier("partition1PrimaryJdbcTemplate") JdbcTemplate p1Primary,
            @Qualifier("partition1FallbackJdbcTemplate") JdbcTemplate p1Fallback,
            @Qualifier("partition2PrimaryJdbcTemplate") JdbcTemplate p2Primary,
            @Qualifier("partition2FallbackJdbcTemplate") JdbcTemplate p2Fallback) {

        this.counterService = counterService;
        this.mqttGateway = mqttGateway;

        this.p0Primary = p0Primary;
        this.p0Fallback = p0Fallback;
        this.p1Primary = p1Primary;
        this.p1Fallback = p1Fallback;
        this.p2Primary = p2Primary;
        this.p2Fallback = p2Fallback;

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Enum representing the three possible partition regions
    private enum Region {
        PARTITION_0, PARTITION_1, PARTITION_2
    }

    // Converts a partition index (0, 1 or 2) into a region enum
    private Region getRegionFromPartition(int partition) {
        switch (partition) {
            case 0: return Region.PARTITION_0;
            case 1: return Region.PARTITION_1;
            case 2: return Region.PARTITION_2;
            default: throw new IllegalStateException("Invalid partition index: " + partition);
        }
    }

    //  Checks if the primary database for a given region is healthy and responds
    private boolean isPrimaryHealthy(Region region) {
        try {
            getTemplate(region, true).queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Retreives the appropriate JDBC template for a region
    // And if we want to get primary or fallback database
    private JdbcTemplate getTemplate(Region region, boolean primary) {
        switch (region) {
            case PARTITION_0: return primary ? p0Primary : p0Fallback;
            case PARTITION_1: return primary ? p1Primary : p1Fallback;
            case PARTITION_2: return primary ? p2Primary : p2Fallback;
            default: throw new IllegalStateException("Unknown region: " + region);
        }
    }

    // Maps a database row to an Appointment
    private Appointment mapRowToAppointment(java.sql.ResultSet rs) throws java.sql.SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getLong("id"));
        a.setPatientId((Long) rs.getObject("patient_id"));
        a.setDentistId(rs.getLong("dentist_id"));
        a.setClinicId(rs.getLong("clinic_id"));
        a.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        a.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        return a;
    }

    // Saves a new appointment into the system.
    // 1. Uses counterService to get the next partition (round-robin distribution)
    // 2. Checks if the primary DB for that partition is healthy or not
    // 3. Inserts the appointment into chosen DB
    // 4. Sets the generated ID on the appointment
    // 5. Logs the partition and DB
    public Appointment save(Appointment appointment) {
        // Determine partition using counter service
        int partition = counterService.getNextPartition();
        Region region = getRegionFromPartition(partition);
        boolean primaryHealthy = isPrimaryHealthy(region);
        JdbcTemplate template = getTemplate(region, primaryHealthy);

        String sql = "INSERT INTO appointment (patient_id, dentist_id, clinic_id, start_time, end_time) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        Long id = template.queryForObject(sql, Long.class,
                appointment.getPatientId(),
                appointment.getDentistId(),
                appointment.getClinicId(),
                appointment.getStartTime(),
                appointment.getEndTime());
        appointment.setId(id);

        System.out.println("Assigned appointment ID " + id + " to partition " + partition +
                " (" + region + ", " + (primaryHealthy ? "primary" : "fallback") + ")");
        return appointment;
    }

    // Retrieves an appointment by its id
    // Searches ALL partitions
    // Checks if primary is healthy
    // Queries primary or fallback for given id
    // Returns the first match or Optional.empty()
    public Optional<Appointment> getAppointmentById(Long id) {
        // Search all partitions since we only have the id
        for (Region region : Region.values()) {
            boolean primaryHealthy = isPrimaryHealthy(region);
            JdbcTemplate template = getTemplate(region, primaryHealthy);
            List<Appointment> results = template.query("SELECT * FROM appointment WHERE id = ?", (rs, rowNum) -> mapRowToAppointment(rs), id);
            if (!results.isEmpty()) {
                return Optional.of(results.get(0));
            }
        }
        return Optional.empty();
    }

    // Deletes appointment by id
    // First finds which partition/db by calling "getAppointmentById"
    // If found, delete from that partition
    public void deleteAppointment(Long id) {
        Optional<Appointment> opt = getAppointmentById(id);
        if (opt.isPresent()) {
            Appointment appt = opt.get();
            // Without a known partitioning key, we just delete from the found partition
            for (Region region : Region.values()) {
                boolean primaryHealthy = isPrimaryHealthy(region);
                JdbcTemplate template = getTemplate(region, primaryHealthy);
                int deleted = template.update("DELETE FROM appointment WHERE id = ?", id);
                if (deleted > 0) {
                    System.out.println("Deleted appointment " + id + " from " + region);
                    break;
                }
            }
        }
    }

    // Retrieves ALL appointment from ALL databases
    // Queries each partitions and returns a list of all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> all = new ArrayList<>();
        for (Region region : Region.values()) {
            boolean primaryHealthy = isPrimaryHealthy(region);
            JdbcTemplate template = getTemplate(region, primaryHealthy);
            all.addAll(template.query("SELECT * FROM appointment", (rs, rowNum) -> mapRowToAppointment(rs)));
        }
        return all;
    }

    // HELPER METHOD
    // Runs given SQL query with parameters against all partitions
    // Returns a list
    private List<Appointment> queryAll(String sql, Object... params) {
        List<Appointment> all = new ArrayList<>();
        for (Region region : Region.values()) {
            boolean primaryHealthy = isPrimaryHealthy(region);
            JdbcTemplate template = getTemplate(region, primaryHealthy);
            all.addAll(template.query(sql, (rs, rowNum) -> mapRowToAppointment(rs), params));
        }
        return all;
    }

    // Retrieves all appointments after given date
    public List<Appointment> getAppointmentsAfterDate(LocalDate startDate) {
        return queryAll("SELECT * FROM appointment WHERE DATE(start_time) > ?", startDate);
    }

    // Retrieves all appointments before given date
    public List<Appointment> getAppointmentsBeforeDate(LocalDate endDate) {
        return queryAll("SELECT * FROM appointment WHERE DATE(start_time) < ?", endDate);
    }

    // Retrieves all appointments between two dates
    public List<Appointment> getAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return queryAll("SELECT * FROM appointment WHERE DATE(start_time) BETWEEN ? AND ?", startDate, endDate);
    }

    // Retrieves all appointments by dentist ID
    public List<Appointment> getAppointmentsByDentistId(Long dentistId) {
        return queryAll("SELECT * FROM appointment WHERE dentist_id = ?", dentistId);
    }

    // Retrieves all appointments by patient ID
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        // Without hashing on patient_id, we must query all partitions anyway
        return queryAll("SELECT * FROM appointment WHERE patient_id = ?", patientId);
    }

    // Retrieves all available appointments by dentist ID where patient ID is null
    public List<Appointment> getAvailableAppointmentsByDentistId(Long dentistId) {
        return queryAll("SELECT * FROM appointment WHERE dentist_id = ? AND patient_id IS NULL", dentistId);
    }

    // Method for publishing an MQTT message containting a stringified appointment JSON-object
    public void publishAppointmentMessage(String topic, Appointment appointment) {
        try {
            String message = objectMapper.writeValueAsString(appointment);
            mqttGateway.publishMessage(message, topic);
            System.out.println("Published message to topic: " + topic);
        } catch (Exception e) {
            System.err.println("Failed to publish message to topic " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Publishes an MQTT message for when an appointment is created.
    public void publishAppointmentCreatedEvent(Appointment appointment) {
        try {
            // Use the class-level objectMapper
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointment.getId());
            messageMap.put("patientId", appointment.getPatientId());
            messageMap.put("startTime", appointment.getStartTime()); // LocalDateTime

            String message = objectMapper.writeValueAsString(messageMap);
            System.out.println("Message being published: " + message);
            mqttGateway.publishMessage(message, "/appointments/booked");
            System.out.println("Published appointment created event to topic: /appointments/booked");
        } catch (Exception e) {
            System.err.println("Failed to publish appointment created event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Publishes an MQTT message when an appointment is booked by a dentist.
    public void publishAppointmentBookedByDentist(Appointment appointment) {
        try {
            // Use the class-level objectMapper
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointment.getId());
            messageMap.put("patientId", appointment.getPatientId());
            messageMap.put("startTime", appointment.getStartTime()); // LocalDateTime

            String message = objectMapper.writeValueAsString(messageMap);
            System.out.println("Message being published: " + message);
            mqttGateway.publishMessage(message, "/appointments/booked-by-dentist");
            System.out.println("Published appointment created event to topic: /appointments/booked-by-dentist");
        } catch (Exception e) {
            System.err.println("Failed to publish appointment created event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Publishes an MQTT message when an appointment is booked by a patient.
    public void publishAppointmentBookedByPatient(Appointment appointment) {
        try {
            // Use the class-level objectMapper
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("appointmentId", appointment.getId());
            messageMap.put("patientId", appointment.getPatientId());
            messageMap.put("startTime", appointment.getStartTime()); // LocalDateTime

            String message = objectMapper.writeValueAsString(messageMap);
            System.out.println("Message being published: " + message);
            mqttGateway.publishMessage(message, "/appointments/booked-by-patient");
            System.out.println("Published appointment created event to topic: /appointments/booked-by-patient");
        } catch (Exception e) {
            System.err.println("Failed to publish appointment created event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Checks if a given appointment date/time is already in the past.
    public boolean checkIfDateInvalid(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }

    // Counts total number of appointments across all partitions.
    public long countAppointments() {
        long total = 0;
        for (Region region : Region.values()) {
            boolean primaryHealthy = isPrimaryHealthy(region);
            JdbcTemplate template = getTemplate(region, primaryHealthy);
            Long c = template.queryForObject("SELECT COUNT(*) FROM appointment", Long.class);
            total += (c == null ? 0 : c);
        }
        return total;
    }

    // Counts how many appointments are booked across all partitions.
    public long countBookedAppointments() {
        long total = 0;
        for (Region region : Region.values()) {
            boolean primaryHealthy = isPrimaryHealthy(region);
            JdbcTemplate template = getTemplate(region, primaryHealthy);
            Long c = template.queryForObject("SELECT COUNT(*) FROM appointment WHERE patient_id IS NOT NULL", Long.class);
            total += (c == null ? 0 : c);
        }
        return total;
    }
}
