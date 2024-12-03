package com.smile_select.appointment_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

        // TODO - If statement to check if booked for patient or just an empty slot
        Appointment createdAppointment = appointmentService.save(appointment);
        appointmentService.publishAppointmentMessage("/appointments/new", createdAppointment);

        // Publish created appointment to "/appointments/booked" topic
       // appointmentService.publishAppointmentCreatedEvent(createdAppointment);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<Appointment> appointments;

        // If both startDate and endDate are provided, query appointments between dates
        if (startDate != null && endDate != null) {
            appointments = appointmentService.getAppointmentsBetweenDates(startDate, endDate);
        }
        // If only startDate is provided, query appointments after the given date
        else if (startDate != null) {
            appointments = appointmentService.getAppointmentsAfterDate(startDate);
        }
        // If only endDate is provided, query appointments before the given date
        else if (endDate != null) {
            appointments = appointmentService.getAppointmentsBeforeDate(endDate);
        }
        // If no query params are passed, fetch all appointments
        else {
            appointments = appointmentService.getAllAppointments();
        }

        // If no appointments are found
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

    @GetMapping(value = "/dentist/{dentistId}")
    public ResponseEntity<?> getAppointmentsByDentistId(
        @PathVariable("dentistId") Long dentistId,
        @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable) {
            List<Appointment> appointments;

            if (onlyAvailable) {
            
                appointments = appointmentService.getAvailableAppointmentsByDentistId(dentistId);
            } else {
                
                appointments = appointmentService.getAppointmentsByDentistId(dentistId);
            }

            if (appointments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("There are no appointments associated with that dentist ID");
            }
            return ResponseEntity.status(HttpStatus.OK).body(appointments);
            
            }

    @GetMapping(value = "/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatientId(@PathVariable("patientId") Long patientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There are no appointments associated with that patient ID");
        }
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @PatchMapping
    public ResponseEntity<?> updateAppointmentDetails(@RequestBody Appointment incompleteAppointment) {
        Optional<Appointment> optionalAppointment = appointmentService
                .getAppointmentById(incompleteAppointment.getId());

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();

            if (incompleteAppointment.getDentistId() != null) {
                appointment.setDentistId(incompleteAppointment.getDentistId());
            }

            if (incompleteAppointment.getPatientId() != null) {
                appointment.setPatientId(incompleteAppointment.getPatientId());
            } else if (incompleteAppointment.getPatientId() == null) {
                appointment.setPatientId(null);
            }


            if (incompleteAppointment.getStartTime() != null) {
                appointment.setStartTime(incompleteAppointment.getStartTime());
            }

            if (incompleteAppointment.getEndTime() != null) {
                appointment.setEndTime(incompleteAppointment.getEndTime());
            }

            appointmentService.save(appointment);

            return ResponseEntity.ok(appointment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }
    }

    @PatchMapping("/add-patient")
    public ResponseEntity<?> addPatientToAppointment(@RequestBody Appointment appointmentWithPatient) {
        Optional<Appointment> optionalAppointment = appointmentService
                .getAppointmentById(appointmentWithPatient.getId());

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();

            if (appointmentWithPatient.getPatientId() != null) {
                appointment.setPatientId(appointmentWithPatient.getPatientId());
                appointmentService.save(appointment);

                // Publish event for email notification when patient is added
                appointmentService.publishAppointmentCreatedEvent(appointment);

                return ResponseEntity.ok(appointment);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient ID is required to add a patient");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable("id") Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isPresent()) {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
        }
    }
}
