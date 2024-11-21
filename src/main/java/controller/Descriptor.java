/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.entity.*;
import model.non_entity.*;
import model.non_entity.SystemDefinition.LoginViewMode;
import view.views.dialogs.LoginDialog;
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
        ViewController.ScheduleViewMode scheduleViewMode = null;
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
        private ArrayList<Appointment> appointmentSlotsForTheDayInListFormat = null;
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
        //ArrayList<Patient> patients = null;
        private Point tableRowCount = null;
        
        public ViewController.ScheduleViewMode getScheduleViewMode(){
            return scheduleViewMode;
        }
        public void setScheduleViewMode(ViewController.ScheduleViewMode value){
            scheduleViewMode = value;
        }
        
        public LocalDate getScheduleDay(){
            if (scheduleDay == null) scheduleDay = LocalDate.now();
            return scheduleDay;
        }
        public void setScheduleDay(LocalDate value){
            this.scheduleDay = value;
        }
        
        private PatientCondition patientCondition = null;
        public PatientCondition getPatientCondition(){
            return patientCondition; 
        }
        public void setPatientCondition(PatientCondition value){
            patientCondition = value;
        }
        
        private PatientAppointmentData patientAppointmentData = null;
        public PatientAppointmentData getPatientAppointmentData(){
            return patientAppointmentData; 
        }
        public void setPatientAppointmentData(PatientAppointmentData value){
            patientAppointmentData = value;
        }
        
        private Invoice invoice = null;
        public Invoice getInvoice(){
            return invoice; 
        }
        public void setInvoice(Invoice value){
            invoice = value;
        }
        
        private ToDo toDo = null;
        public ToDo getToDo(){
            return toDo; 
        }
        public void setToDo(ToDo value){
            toDo = value;
        }
        
        private Credential newUserCredential = null;
        public Credential getNewUserCredential(){
            return newUserCredential; 
        }
        public void setNewUserCredential(Credential value){
            newUserCredential = value;
        }
        
        private Credential loginCredential = null;
        public Credential getLoginCredential(){
            return loginCredential; 
        }
        public void setLoginCredential(Credential value){
            loginCredential = value;
        }
        
        public Doctor getDoctor(){
            return doctor; 
        }
        public void setDoctor(Doctor value){
            doctor = value;
        }
        
        private TreatmentWithState treatmentWithState = null;
        public TreatmentWithState getTreatmentWithState(){
            return treatmentWithState; 
        }
        public void setTreatmentWithState(TreatmentWithState value){
            treatmentWithState = value;
        }
        
        private QuestionWithState questionWithState = null;
        public QuestionWithState getQuestionWithState(){
            return questionWithState; 
        }
        public void setQuestionWithState(QuestionWithState value){
            questionWithState = value;
        }
        
        private ConditionWithState conditionWithState = null;
        public ConditionWithState getConditionWithState(){
            return conditionWithState; 
        }
        public void setConditionWithState(ConditionWithState value){
            conditionWithState = value;
        }
        
        private ClinicalNote clinicalNote = null;
        public ClinicalNote getClinicalNote(){
            return clinicalNote; 
        }
        public void setClinicalNote(ClinicalNote value){
            clinicalNote = value;
        }
        
        private PrimaryCondition primaryCondition = null;
        public PrimaryCondition getPrimaryCondition(){
            return primaryCondition; 
        }
        protected void setPrimaryCondition(PrimaryCondition value){
            primaryCondition = value;
        }
        
        private Treatment treatment = null;
        public Treatment getTreatment(){
            return treatment; 
        }
        public void setTreatment(Treatment value){
            treatment = value;
        }
        
        private LoginViewMode loginViewMode = null;
        public LoginViewMode getLoginViewMode(){
            return loginViewMode; 
        }
        public void setLoginViewMode(LoginViewMode value){
            loginViewMode = value;
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
        
        ArrayList<Patient> patients = null;
        public ArrayList<Patient> getPatients(){
            return patients;
        }
        public void setPatients (ArrayList<Patient> value){
            patients = value;
        }
        
        ArrayList<ToDo> toDos = null;
        public ArrayList<ToDo> getToDos(){
            return toDos;
        }
        public void setToDos (ArrayList<ToDo> value){
            toDos = value;
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
        
        public ArrayList<Appointment> getAppointmentSlotsForDayInListFormat(){
            return appointmentSlotsForTheDayInListFormat;
        }
        public void setAppointmentSlotsForDayInListFormat(ArrayList<Appointment> appointments){
            appointmentSlotsForTheDayInListFormat = appointments;
        }
        
        private ArrayList<Slot> appointmentSlotsForTheDayInDiaryFormat = null;
        public ArrayList<Slot> getAppointmentSlotsForDayInDiaryFormat(){
            return appointmentSlotsForTheDayInDiaryFormat;
        }
        public void setAppointmentSlotsForDayInDiaryFormat(ArrayList<Slot> slots){
            appointmentSlotsForTheDayInDiaryFormat = slots;
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
        
        private ViewController.ScheduleViewMode scheduleViewMode = null;
        public ViewController.ScheduleViewMode getScheduleViewMode(){
            return scheduleViewMode;
        }
        public void setScheduleViewMode(ViewController.ScheduleViewMode value){
            scheduleViewMode = value;
        }
        
        private ConditionWithState conditionWithState = null;
        public ConditionWithState getConditionWithState(){
            return conditionWithState; 
        }
        public void setConditionWithState(ConditionWithState value){
            conditionWithState = value;
        }
        
        private QuestionWithState questionWithState = null;
        public QuestionWithState getQuestionWithState(){
            return questionWithState; 
        }
        public void setQuestionWithState(QuestionWithState value){
            questionWithState = value;
        }
        
        public Doctor getDoctor(){
            return doctor; 
        }
        public void setDoctor(Doctor value){
            doctor = value;
        }
        
        private PatientAppointmentData patientAppointmentData = null;
        public PatientAppointmentData getPatientAppointmentData(){
            return patientAppointmentData; 
        }
        public void setPatientAppointmentData(PatientAppointmentData value){
            patientAppointmentData = value;
        }

        private ToDo toDo = null;
        public ToDo getToDo(){
            return toDo; 
        }
        public void setToDo(ToDo value){
            toDo = value;
        }
        
        private Credential newUserCredential = null;
        public Credential getNewUserCredential(){
            return newUserCredential; 
        }
        public void setNewUserCredential(Credential value){
            newUserCredential = value;
        }
        
        private Credential loginCredential = null;
        public Credential getLoginCredential(){
            return loginCredential; 
        }
        public void setLoginCredential(Credential value){
            loginCredential = value;
        }
        
        private LoginViewMode loginViewMode = null;
        public LoginViewMode getLoginViewMode(){
            return loginViewMode; 
        }
        public void setLoginViewMode(LoginViewMode value){
            loginViewMode = value;
        }
        
        private PatientCondition patientCondition = null;
        public PatientCondition getPatientCondition(){
            return patientCondition; 
        }
        public void setPatientCondition(PatientCondition value){
            patientCondition = value;
        }
        
        private PrimaryCondition primaryCondition = null;
        public PrimaryCondition getPrimaryCondition(){
            return primaryCondition; 
        }
        public void setPrimaryCondition(PrimaryCondition value){
            primaryCondition = value;
        }

        private Invoice invoice = null;
        public Invoice getInvoice(){
            return invoice; 
        }
        public void setInvoice(Invoice value){
            invoice = value;
        }
        
        private LoginDialog loginDialog = null;
        public LoginDialog getLoginDialog(){
            return loginDialog; 
        }
        public void setLoginDialog(LoginDialog value){
            loginDialog = value;
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
        
        private TreatmentWithState treatmentWithState = null;
        public TreatmentWithState getTreatmentWithState(){
            return treatmentWithState; 
        }
        public void setTreatmentWithState(TreatmentWithState value){
            treatmentWithState = value;
        }
        
        private Treatment treatment = null;
        public Treatment getTreatment(){
            return treatment; 
        }
        public void setTreatment(Treatment value){
            treatment = value;
        }
        
        private ClinicalNote clinicalNote = null;
        public ClinicalNote getClinicalNote(){
            return clinicalNote; 
        }
        public void setClinicalNote(ClinicalNote value){
            clinicalNote = value;
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
        
        private String error = "";
        public String getError(){
            return error;
        }
        public void setError(String message){
            error = message;
        }
    }
    
}