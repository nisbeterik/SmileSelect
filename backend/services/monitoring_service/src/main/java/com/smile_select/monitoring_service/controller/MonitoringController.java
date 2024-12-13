package com.smile_select.monitoring_service.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smile_select.monitoring_service.model.SystemMetrics;
import com.smile_select.monitoring_service.service.MonitoringService;

@RestController
@RequestMapping("/api/metrics")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String getMetricsPlain() {
        SystemMetrics metrics = monitoringService.getCurrentMetrics();
        double availableBookedRatioPercentage = metrics.getAppointmentBookedRatio() * 100;
        return String.format(
                """
                        Dentist Logins (Last Minute): %d
                        Dentist Logins (Last 10 Minutes): %d
                        Dentist Logins (Last 30 Minutes): %d
                        Dentist Logins (Last Hour): %d

                        Patient Logins (Last Minute): %d
                        Patient Logins (Last 10 Minutes): %d
                        Patient Logins (Last 30 Minutes): %d
                        Patient Logins (Last Hour): %d

                        Booked Appointments (Last Minute): %d
                        Booked Appointments (Last 10 Minutes): %d
                        Booked Appointments (Last 30 Minutes): %d
                        Booked Appointments (Last Hour): %d

                        Created Appointments (Last Minute): %d
                        Created Appointments (Last 10 Minutes): %d
                        Created Appointments (Last 30 Minutes): %d
                        Created Appointments (Last Hour): %d

                        Total Appointments: %d
                        Total Booked Appointments: %d
                        Appointment Availability: %.2f%%
                        """,
                metrics.getDentistLoginCountLastMinute(),
                metrics.getDentistLoginCountLast10Minutes(),
                metrics.getDentistLoginCountLast30Minutes(),
                metrics.getDentistLoginCountLastHour(),

                metrics.getPatientLoginCountLastMinute(),
                metrics.getPatientLoginCountLast10Minutes(),
                metrics.getPatientLoginCountLast30Minutes(),
                metrics.getPatientLoginCountLastHour(),

                metrics.getAppointmentSlotsBookedLastMinute(),
                metrics.getAppointmentSlotsBookedLast10Minutes(),
                metrics.getAppointmentSlotsBookedLast30Minutes(),
                metrics.getAppointmentSlotsBookedLastHour(),

                metrics.getAppointmentSlotsCreatedLastMinute(),
                metrics.getAppointmentSlotsCreatedLast10Minutes(),
                metrics.getAppointmentSlotsCreatedLast30Minutes(),
                metrics.getAppointmentSlotsCreatedLastHour(),

                metrics.getTotalAppointments(),
                metrics.getTotalBookedAppointments(),
                availableBookedRatioPercentage);
    }
}
