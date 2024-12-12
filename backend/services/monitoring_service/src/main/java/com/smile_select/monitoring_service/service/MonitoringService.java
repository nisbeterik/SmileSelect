package com.smile_select.monitoring_service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.smile_select.monitoring_service.model.SystemMetrics;

@Service
public class MonitoringService {

    private final ConcurrentMap<String, Instant> dentistLoginEvents = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> patientLoginEvents = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Instant> appointmentSlotEvents = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Instant> appointmentBookedEvents = new ConcurrentHashMap<>();

    public void recordDentistLogin(Long dentistId) {
        // Create a unique key to deal with multiple logins from same dentist
        String uniqueKey = dentistId + "_" + System.currentTimeMillis();
        dentistLoginEvents.put(uniqueKey, Instant.now());
    }

    public void recordPatientLogin(Long patientId) {
        // Create a unique key to deal with multiple logins from same patient
        String uniqueKey = patientId + "_" + System.currentTimeMillis();
        patientLoginEvents.put(uniqueKey, Instant.now());
    }

    public void recordAppointmentSlotCreation(Long appointmentId) {
        appointmentSlotEvents.put(appointmentId, Instant.now());
    }

    public void recordAppointmentSlotBooked(Long appointmentId) {
        appointmentBookedEvents.put(appointmentId, Instant.now());
    }

    private int countLoginEventsInLastPeriod(Map<String, Instant> events, Duration period) {
        Instant cutoff = Instant.now().minus(period);
        return (int) events.values().stream()
                .filter(timestamp -> timestamp.isAfter(cutoff))
                .count();
    }

    private int countAppointmentEventsInLastPeriod(Map<Long, Instant> events, Duration period) {
        Instant cutoff = Instant.now().minus(period);
        return (int) events.values().stream()
                .filter(timestamp -> timestamp.isAfter(cutoff))
                .count();
    }

    private int getTotalAppointments() {
        return 1;
    }

    private int getTotalBookedAppointments() {
        return 1;
    }

    public SystemMetrics getCurrentMetrics() {
        return new SystemMetrics(

                countLoginEventsInLastPeriod(dentistLoginEvents, Duration.ofMinutes(1)),
                countLoginEventsInLastPeriod(dentistLoginEvents, Duration.ofMinutes(10)),
                countLoginEventsInLastPeriod(dentistLoginEvents, Duration.ofMinutes(30)),
                countLoginEventsInLastPeriod(dentistLoginEvents, Duration.ofHours(1)),

                countLoginEventsInLastPeriod(patientLoginEvents, Duration.ofMinutes(1)),
                countLoginEventsInLastPeriod(patientLoginEvents, Duration.ofMinutes(10)),
                countLoginEventsInLastPeriod(patientLoginEvents, Duration.ofMinutes(30)),
                countLoginEventsInLastPeriod(patientLoginEvents, Duration.ofHours(1)),

                countAppointmentEventsInLastPeriod(appointmentBookedEvents, Duration.ofMinutes(1)),
                countAppointmentEventsInLastPeriod(appointmentBookedEvents, Duration.ofMinutes(10)),
                countAppointmentEventsInLastPeriod(appointmentBookedEvents, Duration.ofMinutes(30)),
                countAppointmentEventsInLastPeriod(appointmentBookedEvents, Duration.ofHours(1)),

                countAppointmentEventsInLastPeriod(appointmentSlotEvents, Duration.ofMinutes(1)),
                countAppointmentEventsInLastPeriod(appointmentSlotEvents, Duration.ofMinutes(10)),
                countAppointmentEventsInLastPeriod(appointmentSlotEvents, Duration.ofMinutes(30)),
                countAppointmentEventsInLastPeriod(appointmentSlotEvents, Duration.ofHours(1)),

                getTotalAppointments(),
                getTotalBookedAppointments()

        );
    }

    // Cleanup method every hour
    @Scheduled(fixedRate = 3600000)
    public void cleanupOldEvents() {
        Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
        dentistLoginEvents.entrySet().removeIf(entry -> entry.getValue().isBefore(oneHourAgo));
        patientLoginEvents.entrySet().removeIf(entry -> entry.getValue().isBefore(oneHourAgo));
        appointmentBookedEvents.entrySet().removeIf(entry -> entry.getValue().isBefore(oneHourAgo));
        appointmentSlotEvents.entrySet().removeIf(entry -> entry.getValue().isBefore(oneHourAgo));
    }

}
