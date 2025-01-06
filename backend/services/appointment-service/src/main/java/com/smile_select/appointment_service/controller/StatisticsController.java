package com.smile_select.appointment_service.controller;

import com.smile_select.appointment_service.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/appointments/stats")
public class StatisticsController {

    private final CounterService counterService;

    @Autowired
    public StatisticsController(CounterService counterService) {
        this.counterService = counterService;
    }

    @GetMapping("/partitions")
    public ResponseEntity<Map<String, Integer>> getPartitionStats() {
        Map<String, Integer> stats = counterService.getPartitionStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/counter")
    public ResponseEntity<Integer> getCurrentCounter() {
        int counter = counterService.getCurrentCounter();
        return ResponseEntity.ok(counter);
    }
}
