package com.smile_select.monitoring_service.model;

public class SystemMetrics {

    private int dentistLoginCountLastMinute;
    private int dentistLoginCountLast10Minutes;
    private int dentistLoginCountLast30Minutes;
    private int dentistLoginCountLastHour;

    private int patientLoginCountLastMinute;
    private int patientLoginCountLast10Minutes;
    private int patientLoginCountLast30Minutes;
    private int patientLoginCountLastHour;

    private int appointmentSlotsBookedLastMinute;
    private int appointmentSlotsBookedLast10Minutes;
    private int appointmentSlotsBookedLast30Minutes;
    private int appointmentSlotsBookedLastHour;

    private int appointmentSlotsCreatedLastMinute;
    private int appointmentSlotsCreatedLast10Minutes;
    private int appointmentSlotsCreatedLast30Minutes;
    private int appointmentSlotsCreatedLastHour;

    private int totalAppointments;
    private int totalBookedAppointments;

    public SystemMetrics(int dentistLoginCountLastMinute, int dentistLoginCountLast10Minutes,
            int dentistLoginCountLast30Minutes, int dentistLoginCountLastHour,
            int patientLoginCountLastMinute, int patientLoginCountLast10Minutes, int patientLoginCountLast30Minutes,
            int patientLoginCountLastHour, int appointmentSlotsBookedLastMinute,
            int appointmentSlotsBookedLast10Minutes, int appointmentSlotsBookedLast30Minutes,
            int appointmentSlotsBookedLastHour, int appointmentSlotsCreatedLastMinute, 
            int appointmentSlotsCreatedLast10Minutes, int appointmentSlotsCreatedLast30Minutes,
            int appointmentSlotsCreatedLastHour,int totalAppointments, int totalBookedAppointments) {

        this.dentistLoginCountLastMinute = dentistLoginCountLastMinute;
        this.dentistLoginCountLast10Minutes = dentistLoginCountLast10Minutes;
        this.dentistLoginCountLast30Minutes = dentistLoginCountLast30Minutes;
        this.dentistLoginCountLastHour = dentistLoginCountLastHour;
        this.patientLoginCountLastMinute = patientLoginCountLastMinute;
        this.patientLoginCountLast10Minutes = patientLoginCountLast10Minutes;
        this.patientLoginCountLast30Minutes = patientLoginCountLast30Minutes;
        this.patientLoginCountLastHour = patientLoginCountLastHour;
        this.appointmentSlotsBookedLastMinute = appointmentSlotsBookedLastMinute;
        this.appointmentSlotsBookedLast10Minutes = appointmentSlotsBookedLast10Minutes;
        this.appointmentSlotsBookedLast30Minutes = appointmentSlotsBookedLast30Minutes;
        this.appointmentSlotsBookedLastHour = appointmentSlotsBookedLastHour;
        this.appointmentSlotsCreatedLastMinute = appointmentSlotsCreatedLastMinute;
        this.appointmentSlotsCreatedLast10Minutes = appointmentSlotsCreatedLast10Minutes;
        this.appointmentSlotsCreatedLast30Minutes = appointmentSlotsCreatedLast30Minutes;
        this.appointmentSlotsCreatedLastHour = appointmentSlotsCreatedLastHour;
        this.totalAppointments = totalAppointments;
        this.totalBookedAppointments = totalBookedAppointments;
    }

    public int getDentistLoginCountLastMinute() {
        return dentistLoginCountLastMinute;
    }

    public void setDentistLoginCountLastMinute(int dentistLoginCountLastMinute) {
        this.dentistLoginCountLastMinute = dentistLoginCountLastMinute;
    }

    public int getDentistLoginCountLast10Minutes() {
        return dentistLoginCountLast10Minutes;
    }

    public void setDentistLoginCountLast10Minutes(int dentistLoginCountLast10Minutes) {
        this.dentistLoginCountLast10Minutes = dentistLoginCountLast10Minutes;
    }

    public int getDentistLoginCountLast30Minutes() {
        return dentistLoginCountLast30Minutes;
    }

    public void setDentistLoginCountLast30Minutes(int dentistLoginCountLast30Minutes) {
        this.dentistLoginCountLast30Minutes = dentistLoginCountLast30Minutes;
    }

    public int getDentistLoginCountLastHour() {
        return dentistLoginCountLastHour;
    }

    public void setDentistLoginCountLastHour(int dentistLoginCountLastHour) {
        this.dentistLoginCountLastHour = dentistLoginCountLastHour;
    }

    public int getPatientLoginCountLastMinute() {
        return patientLoginCountLastMinute;
    }

    public void setPatientLoginCountLastMinute(int patientLoginCountLastMinute) {
        this.patientLoginCountLastMinute = patientLoginCountLastMinute;
    }

    public int getPatientLoginCountLast10Minutes() {
        return patientLoginCountLast10Minutes;
    }

    public void setPatientLoginCountLast10Minutes(int patientLoginCountLast10Minutes) {
        this.patientLoginCountLast10Minutes = patientLoginCountLast10Minutes;
    }

    public int getPatientLoginCountLast30Minutes() {
        return patientLoginCountLast30Minutes;
    }

    public void setPatientLoginCountLast30Minutes(int patientLoginCountLast30Minutes) {
        this.patientLoginCountLast30Minutes = patientLoginCountLast30Minutes;
    }

    public int getPatientLoginCountLastHour() {
        return patientLoginCountLastHour;
    }

    public void setPatientLoginCountLastHour(int patientLoginCountLastHour) {
        this.patientLoginCountLastHour = patientLoginCountLastHour;
    }

    public int getAppointmentSlotsBookedLastMinute() {
        return appointmentSlotsBookedLastMinute;
    }

    public void setAppointmentSlotsBookedLastMinute(int appointmentSlotsBookedLastMinute) {
        this.appointmentSlotsBookedLastMinute = appointmentSlotsBookedLastMinute;
    }

    public int getAppointmentSlotsBookedLast10Minutes() {
        return appointmentSlotsBookedLast10Minutes;
    }

    public void setAppointmentSlotsBookedLast10Minutes(int appointmentSlotsBookedLast10Minutes) {
        this.appointmentSlotsBookedLast10Minutes = appointmentSlotsBookedLast10Minutes;
    }

    public int getAppointmentSlotsBookedLast30Minutes() {
        return appointmentSlotsBookedLast30Minutes;
    }

    public void setAppointmentSlotsBookedLast30Minutes(int appointmentSlotsBookedLast30Minutes) {
        this.appointmentSlotsBookedLast30Minutes = appointmentSlotsBookedLast30Minutes;
    }

    public int getAppointmentSlotsBookedLastHour() {
        return appointmentSlotsBookedLastHour;
    }

    public void setAppointmentSlotsBookedLastHour(int appointmentSlotsBookedLastHour) {
        this.appointmentSlotsBookedLastHour = appointmentSlotsBookedLastHour;
    }

    public int getAppointmentSlotsCreatedLastMinute() {
        return appointmentSlotsCreatedLastMinute;
    }

    public void setAppointmentSlotsCreatedLastMinute(int appointmentSlotsCreatedLastMinute) {
        this.appointmentSlotsCreatedLastMinute = appointmentSlotsCreatedLastMinute;
    }

    public int getAppointmentSlotsCreatedLast10Minutes() {
        return appointmentSlotsCreatedLast10Minutes;
    }

    public void setAppointmentSlotsCreatedLast10Minutes(int appointmentSlotsCreatedLast10Minutes) {
        this.appointmentSlotsCreatedLast10Minutes = appointmentSlotsCreatedLast10Minutes;
    }

    public int getAppointmentSlotsCreatedLast30Minutes() {
        return appointmentSlotsCreatedLast30Minutes;
    }

    public void setAppointmentSlotsCreatedLast30Minutes(int appointmentSlotsCreatedLast30Minutes) {
        this.appointmentSlotsCreatedLast30Minutes = appointmentSlotsCreatedLast30Minutes;
    }

    public int getAppointmentSlotsCreatedLastHour() {
        return appointmentSlotsCreatedLastHour;
    }

    public void setAppointmentSlotsCreatedLastHour(int appointmentSlotsCreatedLastHour) {
        this.appointmentSlotsCreatedLastHour = appointmentSlotsCreatedLastHour;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getTotalBookedAppointments() {
        return totalBookedAppointments;
    }

    public void setTotalBookedAppointments(int totalBookedAppointments) {
        this.totalBookedAppointments = totalBookedAppointments;
    }

    public double getAppointmentBookedRatio() {
        if (totalAppointments == 0) {
            return 0;
        }
        double availableAppointments = totalAppointments - totalBookedAppointments;
        return availableAppointments / totalAppointments;

    }
}