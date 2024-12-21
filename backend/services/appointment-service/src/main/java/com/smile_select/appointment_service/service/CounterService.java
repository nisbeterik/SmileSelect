package com.smile_select.appointment_service.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CounterService {

    private final JdbcTemplate jdbcTemplate;
    private static final int PARTITIONS = 3;

    public CounterService(JdbcTemplate counterJdbcTemplate) {
        this.jdbcTemplate = counterJdbcTemplate;
    }

    @Transactional
    public int getNextPartition() {
        // Atomically fetch and increment the counter
        int counter = jdbcTemplate.queryForObject("UPDATE partition_counter SET counter = counter + 1 RETURNING counter", Integer.class);
        int partition = Math.abs(counter % PARTITIONS);

        // Update partition count
        String updatePartitionCount = "UPDATE partition_counter SET partition_" + partition + "_count = partition_" + partition + "_count + 1";
        jdbcTemplate.update(updatePartitionCount);

        return partition;
    }

    public Map<String, Integer> getPartitionStatistics() {
        return jdbcTemplate.queryForObject("SELECT partition_0_count, partition_1_count, partition_2_count FROM partition_counter", (rs, rowNum) -> {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("partition_0_count", rs.getInt("partition_0_count"));
            stats.put("partition_1_count", rs.getInt("partition_1_count"));
            stats.put("partition_2_count", rs.getInt("partition_2_count"));
            return stats;
        });
    }

    public int getCurrentCounter() {
        return jdbcTemplate.queryForObject("SELECT counter FROM partition_counter LIMIT 1", Integer.class);
    }

    @Transactional
    public long getNextGlobalAppointmentId() {

        String sql = "SELECT nextval('global_appointment_seq')";
        Long nextId = jdbcTemplate.queryForObject(sql, Long.class);
        return nextId;
    }

}
