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

    private final JdbcTemplate partition0FallbackJdbcTemplate;
    private final JdbcTemplate partition1FallbackJdbcTemplate;
    private final JdbcTemplate partition2FallbackJdbcTemplate;

    @Autowired
    public CleanupScheduler(
            @Qualifier("partition0FallbackJdbcTemplate") JdbcTemplate p0FallbackJdbcTemplate,
            @Qualifier("partition1FallbackJdbcTemplate") JdbcTemplate p1FallbackJdbcTemplate,
            @Qualifier("partition2FallbackJdbcTemplate") JdbcTemplate p2FallbackJdbcTemplate
    ) {
        this.partition0FallbackJdbcTemplate = p0FallbackJdbcTemplate;
        this.partition1FallbackJdbcTemplate = p1FallbackJdbcTemplate;
        this.partition2FallbackJdbcTemplate = p2FallbackJdbcTemplate;
    }

    // Runs every hour to clean up old appointments from all fallback DBs
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupOldAppointments() {
        String sql = "DELETE FROM appointment WHERE start_time < (NOW() - INTERVAL '96 hours')";

        int deleted0 = partition0FallbackJdbcTemplate.update(sql);
        if (deleted0 > 0) {
            System.out.println("Cleaned up " + deleted0 + " old appointments from partition_0 fallback DB");
        }

        int deleted1 = partition1FallbackJdbcTemplate.update(sql);
        if (deleted1 > 0) {
            System.out.println("Cleaned up " + deleted1 + " old appointments from partition_1 fallback DB");
        }

        int deleted2 = partition2FallbackJdbcTemplate.update(sql);
        if (deleted2 > 0) {
            System.out.println("Cleaned up " + deleted2 + " old appointments from partition_2 fallback DB");
        }
    }
}
