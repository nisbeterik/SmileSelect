package com.smile_select.appointment_service.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smile_select.appointment_service.model.Appointment;
import com.smile_select.appointment_service.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        if (appointment.getDentistId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dentist ID is required.");
        }
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {

        List<Appointment> appointments;

        // If a full LocalDateTime string is passed, including hh:mm:ss
        if (startTime != null) {
            appointments = appointmentService.getAppointmentsAfterStartTime(startTime);
        }

        // If only a date is passed
        else if (startDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay(); // Converts LocalDate to LocalDateTime (midnight)
            appointments = appointmentService.getAppointmentsAfterStartTime(startDateTime);
        }

        // If no query params are passed
        else {
            appointments = appointmentService.getAllAppointments();
        }

        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No appointments found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable("id") Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no appointment with that ID");
        }
        return ResponseEntity.status(HttpStatus.OK).body(appointment);
    }

}
