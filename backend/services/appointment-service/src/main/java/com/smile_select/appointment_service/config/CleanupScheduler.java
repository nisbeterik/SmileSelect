package com.smile_select.appointment_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableScheduling
public class CleanupScheduler {

    private final JdbcTemplate fallbackJdbcTemplate;

    @Autowired
    public CleanupScheduler(@Qualifier("fallbackJdbcTemplate") JdbcTemplate fallbackJdbcTemplate) {
        this.fallbackJdbcTemplate = fallbackJdbcTemplate;
    }

    // Runs every hour to clean up old appointments
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupOldAppointments() {
        // Delete appointments older than 96 hours from now
        String sql = "DELETE FROM appointment WHERE start_time < (NOW() - INTERVAL '96 hours')";
        int deleted = fallbackJdbcTemplate.update(sql);
        if (deleted > 0) {
            System.out.println("Cleaned up " + deleted + " old appointments from redundant DB");
        }
    }
}
