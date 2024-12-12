package com.smile_select.monitoring_service.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smile_select.monitoring_service.model.SystemMetrics;
import com.smile_select.monitoring_service.service.MonitoringService;

@Component
public class MetricsCliRunner implements CommandLineRunner {
    private final MonitoringService monitoringService;

    @Autowired
    public MetricsCliRunner(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "get-metrics".equals(args[0])) {
            SystemMetrics metrics = monitoringService.getCurrentMetrics();
            printMetrics(metrics);
        }
    }

    private void printMetrics(SystemMetrics metrics) {
        System.out.println("System Metrics:");
        // Print metrics here:
    }
}
