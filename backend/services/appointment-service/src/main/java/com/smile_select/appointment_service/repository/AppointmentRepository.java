package com.smile_select.appointment_service.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Repository;
import com.smile_select.appointment_service.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE DATE(a.startTime) > :startDate")
    public List<Appointment> findByStartTimeDateAfter(@Param("startDate") LocalDate startDate);

    @Query("SELECT a FROM Appointment a WHERE DATE(a.startTime) < :endDate")
    public List<Appointment> findByStartTimeDateBefore(@Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Appointment a WHERE DATE(a.startTime) BETWEEN :startDate AND :endDate")
    public List<Appointment> findByStartTimeDateBetween(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Appointment a WHERE a.dentistId = :dentistId AND a.patientId IS NULL AND a.startTime > CURRENT_TIMESTAMP ORDER BY a.startTime ASC")
    public List<Appointment> findAvailableAppointmentsByDentistId(@Param("dentistId") Long dentistId);

    @Query("SELECT a FROM Appointment a WHERE a.dentistId = :dentistId AND DATE(a.startTime) = :date")
    public List<Appointment> findAppointmentsByDentistIdAndDate(
            @Param("dentistId") Long dentistId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.patientId IS NULL AND a.dentistId = :dentistId AND DATE(a.startTime) = :date")
    public List<Appointment> findAvailableAppointmentsByDentistIdAndDate(
            @Param("dentistId") Long dentistId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.clinicId = :clinicId AND a.patientId IS NULL AND a.startTime > CURRENT_TIMESTAMP ORDER BY a.startTime ASC")
    public List<Appointment> findAvailableAppointmentsByClinicId(@Param("clinicId") Long clinicId);

    @Query("SELECT a FROM Appointment a WHERE a.clinicId = :clinicId AND DATE(a.startTime) = :date")
    public List<Appointment> findAppointmentsByClinicIdAndDate(
            @Param("clinicId") Long clinicId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.patientId IS NULL AND a.clinicId = :clinicId AND DATE(a.startTime) = :date")
    public List<Appointment> findAvailableAppointmentsByClinicIdAndDate(
            @Param("clinicId") Long clinicId,
            @Param("date") LocalDate date);

    @Query("SELECT DISTINCT DATE (a.startTime)  FROM Appointment a WHERE a.patientId IS NULL AND  a.startTime > CURRENT_TIMESTAMP AND a.clinicId = :clinicId")
    public List<String> findAvailableDatesForClinic(@Param("clinicId") Long clinicId);

    @Query("SELECT DISTINCT DATE (a.startTime)  FROM Appointment a WHERE a.patientId IS NULL AND  a.startTime > CURRENT_TIMESTAMP AND a.dentistId = :dentistId")
    public List<String> findAvailableDatesForDentist(@Param("dentistId") Long dentistId);

    public List<Appointment> findByDentistId(Long dentistId);
    public List<Appointment> findByClinicId(Long clinicId);
    public List<Appointment> findByPatientId(Long patientId);
    public Long countByPatientIdIsNotNull();

}
