package com.smile_select.appointment_service.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT a FROM Appointment a WHERE a.dentistId = :dentistId AND a.patientId IS NULL")
    public List<Appointment> findAvailableAppointmentsByDentistId(@Param("dentistId") Long dentistId);

    public List<Appointment> findByDentistId(Long dentistId);
    public List<Appointment> findByPatientId(Long patientId);

}
