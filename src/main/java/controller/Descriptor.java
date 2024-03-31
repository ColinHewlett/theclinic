/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.*;
import java.awt.Point;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.HashMap;

/**
 *
 * @author colin
 */
public class Descriptor {
    private Descriptor.ViewDescription viewDescription= null;
    private Descriptor.ControllerDescription controllerDescription = null;

    public static enum AppointmentField {ID,
                                KEY,
                                APPOINTMENT_PATIENT,
                                START,
                                DURATION,
                                NOTES}
    
    public static enum PatientField {
                              KEY,
                              TITLE,
                              FORENAMES,
                              SURNAME,
                              LINE1,
                              LINE2,
                              TOWN,
                              COUNTY,
                              POSTCODE,
                              PHONE1,
                              PHONE2,
                              GENDER,
                              DOB,
                              IS_GUARDIAN_A_PATIENT,
                              GUARDIAN,
                              NOTES,
                              DENTAL_RECALL_DATE,
                              HYGIENE_RECALL_DATE,
                              DENTAL_RECALL_FREQUENCY,
                              HYGIENE_RECALL_FREQUENCY,
                              DENTAL_APPOINTMENT_HISTORY,
                              HYGIENE_APPOINTMENT_HISTORY}
    
    protected Descriptor() {  
        viewDescription = new Descriptor.ViewDescription();
        controllerDescription = new Descriptor.ControllerDescription();
    }

    public Descriptor.ViewDescription getViewDescription(){
        return viewDescription;
    }
    
    public void setViewDescription(Descriptor.ViewDescription value){
        viewDescription = value;
    }
    
    public Descriptor.ControllerDescription getControllerDescription(){
        return controllerDescription;
    }
    
    public class ControllerDescription{
        ViewController.ViewMode viewMode = null;
        private Doctor doctor = null;
        private Medication medication = null;
        private Condition condition = null;
        /*28/03/2024private PatientNote patientNote = null;*/
        /*28/03/2024private ArrayList<PatientNote> patientNotes = null;*/
        private LocalDate scheduleDay = null;
        private LocalDateTime appointmentEarlyStart = null;
        private LocalDateTime appointmentLateStart = null;
        private Boolean isAppointmentUnbookable = false;
        private Point appointeeRemindersCountForDay = null;
        private Appointment appointment = null;
        private Duration emptySlotMinimumDuration = null;
        private LocalDate emptySlotFromDay = null;
        private LocalDate appointmentScheduleDay = null;
        private ArrayList<Appointment> appointmentSlots = null;
        private ArrayList<Appointment> appointmentSlotsForTheDay = null;
        private ArrayList<Appointment> appointmentCancellations = null;
        private ArrayList<Appointment> appointments = null;
        private HashMap<DayOfWeek,Boolean> surgeryDaysAssignment = null;
        String error = null;
        String appointmentCSVPath = null;
        String pmsStorePath = null;
        String patientCSVPath = null;
        Patient patient = null;
        Notification patientNotification = null;
        ArrayList<Notification> patientNotifications = null;
        ArrayList<Patient> patients = null;
        private Point tableRowCount = null;
        
        public LocalDate getScheduleDay(){
            if (scheduleDay == null) scheduleDay = LocalDate.now();
            return scheduleDay;
        }
        public void setScheduleDay(LocalDate value){
            this.scheduleDay = value;
        }
        
        public Doctor getDoctor(){
            return doctor; 
        }
        public void setDoctor(Doctor value){
            doctor = value;
        }
        
        public Medication getMedication(){
            return medication; 
        }
        public void setMedication(Medication value){
            medication = value;
        }
        
        public LocalDateTime getAppointmentEarlyStart(){
            return appointmentEarlyStart; 
        }
        public void setAppointmentEarlyStart(LocalDateTime value){
            appointmentEarlyStart = value;
        }
        
        public LocalDateTime getAppointmentLateStart(){
            return appointmentLateStart; 
        }
        public void setAppointmentLateStart(LocalDateTime value){
            appointmentLateStart = value;
        }
        
        public Boolean getIsAppointmentUnbookable(){
            return isAppointmentUnbookable; 
        }
        public void setIsAppointmentUnbookable(Boolean value){
            isAppointmentUnbookable = value;
        }
        
        public ViewController.ViewMode getViewMode(){
            return viewMode; 
        }
        public void setViewMode(ViewController.ViewMode value){
            viewMode = value;
        }
        /*28/03/2024
        public PatientNote getPatientNote(){
            return patientNote;
        }
        
        public void setPatientNotes(PatientNote value){
            patientNote = value;
        }*/
        
        /*28/03/2024
        public ArrayList<PatientNote> getPatientNotes(){
            return patientNotes;
        }
        
        public void setPatientNotes(ArrayList<PatientNote> value){
            patientNotes = value;
        }*/
        
        public ArrayList<Appointment> getAppointmentCancellations(){
            return appointmentCancellations;
        }
        
        public void setAppointmentCancellations(ArrayList<Appointment> value){
            appointmentCancellations = value;
        }
        
        public Point getTableRowCount(){
            return tableRowCount;
        }

        public void setTableRowCount(Point value){
            tableRowCount = value;
        }
        
        public HashMap<DayOfWeek,Boolean> getSurgeryDaysAssignment(){
            return surgeryDaysAssignment;
        }

        public void setSurgeryDaysAssignment(HashMap<DayOfWeek,Boolean> value){
            surgeryDaysAssignment = value;
        }
        
        public LocalDate getEmptySlotFromDay(){
            return  emptySlotFromDay;
        }
        
        public void setEmptySlotFromDay(LocalDate value){
            emptySlotFromDay = value;
        }
        
        public Duration getEmptySlotMinimumDuration(){
            return  emptySlotMinimumDuration;
        }
        
        public void setEmptySlotMinimumDuration(Duration value){
            emptySlotMinimumDuration = value;
        }
        
        public ArrayList<Notification> getPatientNotifications(){
            return patientNotifications;
        }

        public void setPatientNotifications(ArrayList<Notification> patientNotifications){
            this.patientNotifications = patientNotifications;
        }
        
        public Notification getPatientNotification(){
            return patientNotification;
        }

        public void setPatientNotification(Notification value){
            this.patientNotification = value;
        }
        
        public Condition getCondition() {
            return condition;
        }
        protected void setCondition(Condition value){
            condition = value;
        }
        
        public Patient getPatient() {
            return patient;
        }
        protected void setPatient(Patient value){
            patient = value;
        }
        public ArrayList<Patient> getPatients(){
            return patients;
        }
        public void setPatients (ArrayList<Patient> value){
            patients = value;
        }
        
        public String getPathForPatientCSVData(){
            return patientCSVPath;
        }

        public void setPathForPatientCSVData(String value){
            patientCSVPath = value;
        }
        
        public String getPathForPMSStore(){
            return pmsStorePath;
        }

        public void setPathForPMSStore(String value){
            pmsStorePath = value;
        }
        
        public String getPathForApppointmentCSVData(){
            return appointmentCSVPath;
        }

        public void setPathForAppointmentCSVData(String value){
            appointmentCSVPath = value;
        }
        
        public String getError(){
            return error;
        }

        protected void setError(String message){
            error = message;
        }
        
        public ArrayList<Appointment> getAppointmentSlotsForDay(){
            return appointmentSlotsForTheDay;
        }

        public void setAppointmentSlotsForDay(ArrayList<Appointment> appointments){
            appointmentSlotsForTheDay = appointments;
        }

        public ArrayList<Appointment> getAppointmentSlots(){
            return appointmentSlots;
        }


        public void setAppointmentSlots(ArrayList<Appointment> appointments){
            appointmentSlots = appointments;
        }
        
        public ArrayList<Appointment> getAppointments(){
            return appointments;
        }

        public void setAppointments(ArrayList<Appointment> value){
            appointments = value;
        }
                
        public Point getAppointeeRemindersCountForDay(){
            return appointeeRemindersCountForDay;
        }
        
        public void setAppointeeRemindersCountForDay(Point count){
            appointeeRemindersCountForDay = count;
        }
        
        public Appointment getAppointment() {
            return appointment;
        }

        protected void setAppointment(Appointment value) {
            appointment = value;
        }

    }
    
    public class ViewDescription {
        ViewController.ViewMode viewMode = null;
        /*28/03/2024private PatientNote patientNote = null;*/
        private Appointment appointment = null;
        private Patient thePatient = null;
        private Patient theGuardian = null;
        private LocalDate day = null;
        private Duration duration = null;
        private String databaseLocation = null;
        private ArrayList<Notification> patientNotifications = null;
        private Notification patientNotification = null;
        private SurgeryDaysAssignment surgeryDaysAssignment = null;
        private Doctor doctor = null;
        private Medication medication = null;
        private Condition condition = null;
        
        private HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = null;


        protected ViewDescription() {
            appointment = new Appointment();
            thePatient = new Patient();
            theGuardian = new Patient();
            day = LocalDate.now();
            duration = Duration.ZERO; 
            HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = new HashMap<>();
            
        }
        
        public Doctor getDoctor(){
            return doctor; 
        }
        public void setDoctor(Doctor value){
            doctor = value;
        }
        
        public Medication getMedication(){
            return medication; 
        }
        public void setMedication(Medication value){
            medication = value;
        }
        
        public Condition getCondition() {
            return condition;
        }
        public void setCondition(Condition value){
            condition = value;
        }
        
        public ViewController.ViewMode getViewMode(){
            return viewMode; 
        }
        public void setViewMode(ViewController.ViewMode value){
            viewMode = value;
        }
        
        public Patient getTheGuardian(){
            return theGuardian;
        }
        
        public void setTheGuardian(Patient patient){
            theGuardian = patient;
        }
        
        public Appointment getAppointment(){
            return appointment;
        }
        
        public void setAppointment(Appointment value){
            appointment = value;
        }
        
        public Patient getPatient(){
            return thePatient;
        }
        
        public void setPatient(Patient patient){
            thePatient = patient;
        }
        /*28/03/2024
        public PatientNote getPatientNote(){
            return patientNote;
        }
        
        public void setPatientNote(PatientNote value){
            patientNote = value;
        }*/
        
        public Notification getNotification(){
            return patientNotification;
        }
        
        public void setPatientNotification(Notification value){
            patientNotification = value;
        }
        
        public ArrayList<Notification> getPatientNotifications(){
            return patientNotifications;
        }
        
        public void setPatientNotifications(ArrayList<Notification> value){
            patientNotifications = value;
        }

        public void setSurgeryDaysAssignmentValue(HashMap<DayOfWeek,Boolean> value){
            surgeryDaysAssignmentValue = value;
        }
        
        public HashMap<DayOfWeek,Boolean> getSurgeryDaysAssignmentValue(){
            return surgeryDaysAssignmentValue;
        }
        
        public void setDatabaseLocation(String value){
            databaseLocation = value;
        }
        
        public String getDatabaseLocation(){
            return databaseLocation;
        }

        public LocalDate getScheduleDay(){
            return day;
        }
        
        public void setScheduleDay(LocalDate value){
            this.day = value;
        }
        
        public Duration getDuration(){
            return duration;
        }
        
        public void setDuration(Duration value){
            duration = value;
        }
    }
    
}