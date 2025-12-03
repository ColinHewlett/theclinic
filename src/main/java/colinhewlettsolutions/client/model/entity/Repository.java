/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.entity;

import colinhewlettsolutions.client.model.entity.interfaces.IStoreClient;
import colinhewlettsolutions.client.controller.SystemDefinition;
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import static colinhewlettsolutions.client.model.entity.Entity.Scope.ALL;
import static colinhewlettsolutions.client.model.entity.Entity.Scope.SINGLE;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import colinhewlettsolutions.client.model.repository.CSVReader;
import colinhewlettsolutions.client.model.repository.IStoreActions;
import colinhewlettsolutions.client.model.repository.LoginException;
import colinhewlettsolutions.client.model.repository.StoreException;

/**
 *
 * @author colin
 */
public class Repository implements IStoreActions {
    
    private static Connection postgresConnection = null;
    private static Connection pmsStoreConnection = null;
    
    protected enum EntityType {
                            ARCHIVED_PATIENT,
                            APPOINTMENT,
                            APPOINTMENT_TREATMENT,
                            CLINICAL_NOTE,
                            DOCTOR,
                            INVOICE,
                            MEDICATION,
                            PATIENT,
                            PATIENT_APPOINTMENT_DATA,
                            PATIENT_NOTIFICATION,
                            PATIENT_NOTE,
                            PATIENT_PRIMARY_CONDITION,
                            PATIENT_QUESTION,
                            PATIENT_SECONDARY_CONDITION,
                            PRIMARY_CONDITION,
                            QUESTION,
                            SECONDARY_CONDITION,
                            SYSTEM_DEFINITION,
                            SURGERY_DAYS_ASSIGNMENT,
                            TO_DO,
                            TRANSMISSION,
                            TREATMENT,
                            TREATMENT_COST,
                            USER,
                            USER_SETTINGS,
                            DATABASE}
 
    protected enum PMSSQL   {
                                CANCEL_APPOINTMENT,
                                COUNT_APPOINTMENTS,
                                COUNT_APPOINTMENTS_FOR_DAY,
                                COUNT_APPOINTMENTS_FOR_PATIENT,
                                COUNT_APPOINTMENTS_FROM_DAY,
                                CREATE_APPOINTMENT_TABLE,
                                DELETE_EMERGENCY_APPOINTMENT,
                                DELETE_APPOINTMENT, // was commented out
                                DELETE_APPOINTMENTS_FOR_PATIENT,
                                DELETE_APPOINTMENTS,
                                DROP_APPOINTMENT_TABLE,
                                INSERT_APPOINTMENT,
                                READ_APPOINTMENT,
                                READ_APPOINTMENTS,
                                READ_CANCELLED_APPOINTMENTS,
                                READ_APPOINTMENTS_FOR_DAY,
                                READ_APPOINTMENTS_FOR_DAY_AND_EMERGENCY_APPOINTMENT,
                                READ_APPOINTMENTS_FOR_DAY_AND_NON_EMERGENCY_APPOINTMENT,
                                READ_APPOINTMENTS_FOR_PATIENT,
                                READ_APPOINTMENTS_FOR_INVOICE,
                                READ_APPOINTMENTS_FROM_DAY,
                                READ_APPOINTMENT_NEXT_HIGHEST_KEY,
                                READ_DELETED_APPOINTMENTS_FOR_PATIENT,
                                RECOVER_APPOINTMENT,
                                UNCANCEL_APPOINTMENT,
                                UPDATE_APPOINTMENT,
                                
                                //COUNT_PATIENT_APPOINTMENT_DATA,
                                //CREATE_PATIENT_APPOINTMENT_DATA_TABLE,
                                //DELETE_ALL_PATIENT_APPOINTMENT_DATA,
                                //INSERT_PATIENT_APPOINTMENT_DATA,
                                READ_ARCHIVED_PATIENT_APPOINTMENT_DATA,
                                READ_PATIENT_APPOINTMENT_DATA_BY_LAST_APPOINTMENT_DATE,
                                READ_PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT,
                                READ_PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT,
                                //UPDATE_PATIENT_APPOINTMENT_DATA,  
                                
                                COUNT_PENDING_TRANSMISSIONS,
                                COUNT_TRANSMISSION,
                                CREATE_TRANSMISSION_TABLE,
                                DELETE_ALL_TRANSMISSION,
                                INSERT_TRANSMISSION,
                                READ_ALL_TRANSMISSIONS,
                                READ_PENDING_TRANSMISSIONS,
                                READ_PENDING_TRANSMISSIONS_FOR_PATIENT,
                                UPDATE_TRANSMISSION, 
                                
                                COUNT_ARCHIVED_PATIENT,
                                CREATE_ARCHIVED_PATIENT_TABLE,
                                DELETE_ALL_ARCHIVED_PATIENT,
                                INSERT_ARCHIVED_PATIENT,
                                READ_ARCHIVED_PATIENT_BY_LAST_APPOINTMENT_DATE,
                                READ_ARCHIVED_PATIENT_BY_PATIENT,
                                UPDATE_ARCHIVED_PATIENT,
                                
                                CANCEL_NOTIFICATION,
                                COUNT_DELETED_NOTIFICATIONS,
                                COUNT_PATIENT_NOTIFICATIONS,
                                COUNT_UNACTIONED_NOTIFICATIONS,
                                CREATE_NOTIFICATION_TABLE,
                                //DELETE_PATIENT_NOTIFICATION,
                                DELETE_NOTIFICATIONS,
                                INSERT_NOTIFICATION,
                                READ_NOTIFICATION,
                                READ_NOTIFICATIONS,
                                READ_UNACTIONED_NOTIFICATIONS,
                                READ_NOTIFICATION_NEXT_HIGHEST_KEY,
                                READ_NOTIFICATIONS_FOR_PATIENT,
                                READ_DELETED_NOTIFICATIONS_FOR_PATIENT,
                                RECOVER_NOTIFICATION,
                                UNCANCEL_NOTIFICATION,
                                UPDATE_NOTIFICATION,
                                
                                COUNT_PATIENT_NOTES,
                                CREATE_PATIENT_NOTE_TABLE,
                                DELETE_PATIENT_NOTE,
                                DELETE_PATIENT_NOTES,
                                INSERT_PATIENT_NOTE,
                                READ_PATIENT_NOTE,
                                READ_ALL_PATIENT_NOTES,
                                READ_NOTES_FOR_PATIENT,
                                READ_PATIENT_NOTE_NEXT_HIGHEST_KEY,
                                RECOVER_PATIENT_NOTE,
                                UPDATE_PATIENT_NOTE,
                                
                                CREATE_CLINICAL_NOTE_TABLE,
                                COUNT_CLINICAL_NOTE,
                                DELETE_ALL_CLINICAL_NOTE,
                                DELETE_CLINICAL_NOTE,
                                INSERT_CLINICAL_NOTE,
                                READ_CLINICAL_NOTE,
                                READ_CLINICAL_NOTE_FOR_APPOINTMENT,
                                READ_ALL_CLINICAL_NOTE,
                                READ_CLINIC_NOTE_FOR_PATIENT,
                                READ_CLINIC_NOTE_NEXT_HIGHEST_KEY,
                                UPDATE_CLINIC_NOTE,
                                
                                CREATE_TREATMENT_TABLE,
                                COUNT_TREATMENT,
                                DELETE_ALL_TREATMENT,
                                DELETE_TREATMENT,
                                INSERT_TREATMENT,
                                READ_TREATMENT,
                                READ_TREATMENT_FOR_APPOINTMENT,
                                READ_ALL_TREATMENT,
                                READ_TREATMENT_FOR_PATIENT,
                                READ_TREATMENT_NEXT_HIGHEST_KEY,
                                UPDATE_TREATMENT,
                                
                                CREATE_USER_TABLE,
                                COUNT_USER,
                                DELETE_ALL_USER,
                                DELETE_USER,
                                INSERT_USER,
                                READ_USER,
                                READ_USER_WITH_NAME,
                                READ_ALL_USER,
                                READ_USER_NEXT_HIGHEST_KEY,
                                UPDATE_USER,
                                
                                CREATE_USER_SETTINGS_TABLE,
                                COUNT_USER_SETTINGS,
                                DELETE_ALL_USER_SETTINGS,
                                DELETE_USER_SETTINGS,
                                INSERT_USER_SETTINGS,
                                READ_USER_SETTINGS,
                                READ_USER_SETTINGS_WITH_NAME,
                                READ_ALL_USER_SETTINGS,
                                READ_USER_SETTINGS_NEXT_HIGHEST_KEY,
                                UPDATE_USER_SETTINGS,
                                
                                CREATE_APPOINTMENT_TREATMENT_TABLE,
                                COUNT_APPOINTMENT_TREATMENT,
                                DELETE_ALL_APPOINTMENT_TREATMENT,
                                DELETE_APPOINTMENT_TREATMENT,
                                INSERT_APPOINTMENT_TREATMENT,
                                READ_APPOINTMENT_TREATMENT,
                                READ_APPOINTMENT_TREATMENT_FOR_APPOINTMENT,
                                READ_APPOINTMENT_TREATMENT_FOR_TREATMENT,
                                READ_ALL_APPOINTMENT_TREATMENT,
                                //READ_APPOINTMENT_TREATMENT_FOR_PATIENT,
                                //READ_APPOINTMENT_TREATMENT_NEXT_HIGHEST_KEY,
                                UPDATE_APPOINTMENT_TREATMENT,
                                
                                
                                
                                CREATE_INVOICE_TABLE,
                                COUNT_INVOICE,
                                DELETE_ALL_INVOICE,
                                DELETE_INVOICE,
                                INSERT_INVOICE,
                                READ_INVOICE,
                                READ_INVOICE_FOR_PATIENT,
                                READ_ALL_INVOICE,
                                READ_INVOICE_NEXT_HIGHEST_KEY,
                                UPDATE_INVOICE,
                                
                                CREATE_TO_DO_TABLE,
                                CANCEL_TO_DO,
                                COUNT_TO_DO,
                                DELETE_ALL_TO_DO,
                                DELETE_TO_DO,
                                INSERT_TO_DO,
                                READ_TO_DO,
                                READ_TO_DO_FOR_USER,
                                READ_ALL_TO_DO,
                                READ_UNACTIONED_TO_DO,
                                READ_TO_DO_NEXT_HIGHEST_KEY,
                                UPDATE_TO_DO,
                                
                                CREATE_TREATMENT_COST_TABLE,
                                COUNT_TREATMENT_COST,
                                DELETE_ALL_TREATMENT_COST,
                                DELETE_TREATMENT_COST,
                                INSERT_TREATMENT_COST,
                                READ_TREATMENT_COST,
                                READ_TREATMENT_COST_FOR_INVOICE,
                                READ_ALL_TREATMENT_COST,
                                READ_TREATMENT_COST_NEXT_HIGHEST_KEY,
                                UPDATE_TREATMENT_COST,
                                
                                
                                CREATE_QUESTION_TABLE,
                                COUNT_QUESTION,
                                DELETE_ALL_QUESTION,
                                DELETE_QUESTION,
                                INSERT_QUESTION,
                                READ_QUESTION,
                                READ_QUESTION_FOR_PATIENT,
                                READ_QUESTION_FOR_CATEGORY,
                                READ_ALL_QUESTION,
                                READ_QUESTION_NEXT_HIGHEST_KEY,
                                UPDATE_QUESTION,
                                
                                CREATE_PATIENT_QUESTION_TABLE,
                                COUNT_PATIENT_QUESTION,
                                DELETE_ALL_PATIENT_QUESTION,
                                DELETE_PATIENT_QUESTION,
                                INSERT_PATIENT_QUESTION,
                                READ_PATIENT_QUESTION,
                                READ_PATIENT_QUESTION_FOR_PATIENT,
                                READ_PATIENT_QUESTION_FOR_QUESTION,
                                READ_ALL_PATIENT_QUESTION,
                                //READ_PATIENT_QUESTION_FOR_PATIENT,
                                //READ_PATIENT_QUESTION_NEXT_HIGHEST_KEY,
                                UPDATE_PATIENT_QUESTION,
                                
                                CREATE_PATIENT_PRIMARY_CONDITION_TABLE,
                                COUNT_PATIENT_PRIMARY_CONDITION,
                                DELETE_ALL_PATIENT_PRIMARY_CONDITION,
                                DELETE_PATIENT_PRIMARY_CONDITION,
                                INSERT_PATIENT_PRIMARY_CONDITION,
                                READ_PATIENT_PRIMARY_CONDITION,
                                READ_PATIENT_PRIMARY_CONDITION_FOR_PATIENT,
                                READ_PATIENT_PRIMARY_CONDITION_FOR_PRIMARY_CONDITION,
                                READ_ALL_PATIENT_PRIMARY_CONDITION,
                                UPDATE_PATIENT_PRIMARY_CONDITION,
                                
                                CREATE_PATIENT_SECONDARY_CONDITION_TABLE,
                                COUNT_PATIENT_SECONDARY_CONDITION,
                                DELETE_ALL_PATIENT_SECONDARY_CONDITION,
                                DELETE_PATIENT_SECONDARY_CONDITION,
                                INSERT_PATIENT_SECONDARY_CONDITION,
                                READ_PATIENT_SECONDARY_CONDITION,
                                READ_PATIENT_SECONDARY_CONDITION_FOR_PATIENT,
                                READ_PATIENT_SECONDARY_CONDITION_FOR_SECONDARY_CONDITION,
                                READ_ALL_PATIENT_SECONDARY_CONDITION,
                                UPDATE_PATIENT_SECONDARY_CONDITION,
                                
                                CREATE_DOCTOR_TABLE,
                                COUNT_DOCTOR,
                                DELETE_ALL_DOCTOR,
                                DELETE_DOCTOR,
                                INSERT_DOCTOR,
                                READ_DOCTOR,
                                READ_ALL_DOCTOR,
                                READ_DOCTOR_FOR_PATIENT,
                                READ_DOCTOR_NEXT_HIGHEST_KEY,
                                UPDATE_DOCTOR,
                                
                                CREATE_MEDICATION_TABLE,
                                COUNT_MEDICATION,
                                DELETE_ALL_MEDICATION,
                                DELETE_MEDICATION,
                                INSERT_MEDICATION,
                                READ_MEDICATION,
                                READ_ALL_MEDICATION,
                                READ_MEDICATION_FOR_PATIENT,
                                READ_MEDICATION_NEXT_HIGHEST_KEY,
                                UPDATE_MEDICATION,
                                
                                CREATE_SECONDARY_CONDITION_TABLE,
                                COUNT_SECONDARY_CONDITION,
                                DELETE_ALL_SECONDARY_CONDITION,
                                DELETE_SECONDARY_CONDITION,
                                INSERT_SECONDARY_CONDITION,
                                READ_SECONDARY_CONDITION,
                                READ_ALL_SECONDARY_CONDITION,
                                READ_SECONDARY_CONDITION_FOR_PRIMARY_CONDITION,
                                READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY,
                                UPDATE_SECONDARY_CONDITION,
                                
                                CREATE_PRIMARY_CONDITION_TABLE,
                                COUNT_PRIMARY_CONDITION,
                                DELETE_ALL_PRIMARY_CONDITION,
                                DELETE_PRIMARY_CONDITION,
                                INSERT_PRIMARY_CONDITION,
                                READ_PRIMARY_CONDITION,
                                READ_PRIMARY_CONDITION_FOR_PATIENT,
                                READ_ALL_PRIMARY_CONDITION,
                                READ_PRIMARY_CONDITION_NEXT_HIGHEST_KEY,
                                UPDATE_PRIMARY_CONDITION,
                                
                                COUNT_PATIENTS,
                                CREATE_PATIENT_TABLE,
                                DELETE_PATIENT,
                                DELETE_ALL_PATIENT,
                                INSERT_PATIENT,
                                READ_PATIENT,
                                READ_PATIENTS,
                                READ_ARCHIVED_PATIENTS,
                                READ_DELETED_PATIENTS,
                                RECOVER_PATIENT,
                                READ_PATIENT_NEXT_HIGHEST_KEY,
                                UPDATE_PATIENT,
                                
                                COUNT_SURGERY_DAYS_ASSIGNMENT,
                                CREATE_SURGERY_DAYS_ASSIGNMENT_TABLE,
                                DELETE_SURGERY_DAYS_ASSIGNMENT,
                                DROP_SURGERY_DAYS_ASSIGNMENT_TABLE,
                                INSERT_SURGERY_DAYS_ASSIGNMENT,
                                READ_SURGERY_DAYS_ASSIGNMENT,
                                UPDATE_SURGERY_DAYS_ASSIGNMENT,
                                
                                COUNT_SYSTEM_DEFINITION,
                                READ_SYSTEM_DEFINITION
                                }
    
    protected void setRepositoryName(String value){
        repositoryName = value;
    }
    
    private String getRepositoryName(){
        return repositoryName;
    }
    
    protected void setURL(String value){
        url = value;
    }
    
    private String getURL(){
        return url;
    }
    
    protected void setUser(String value){
        user = value;
    }
    
    private String getUser(){
        return user;
    }
    
    protected void setPassword(String value){
        password = value;
    }
    
    private String getPassword(){
        return password;
    }
    
    protected Connection getPostgresConnection() throws StoreException{
        String message;
        url = "jdbc:postgresql://localhost:5432/postgres";
        if (Repository.postgresConnection == null){
            try{
                Repository.postgresConnection = DriverManager.getConnection(getURL(), getUser(), getPassword());
            }catch (SQLException ex){
                message = ex.getMessage() + "\n";
                message = message + "StoreException raised in "
                    + getRepositoryName() + ".getPostgresConnection()";
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        return Repository.postgresConnection;
    }
    
    enum SupportedDatabaseTypes{
        ACCESS,
        MYSQL,
        POSTGRESQL,
        SQLSERVER
    }
    private static String databaseType = null;
    public static void setDatabaseType(String value){
        databaseType = value;
    }
    public static String getDatabaseType(){
        return databaseType;
    }
    
    private static String databaseURL = null;
    public static void setDatabaseURL(String value){
        databaseURL = value;
    }
    public static String getDatabaseURL(){
        return databaseURL;
    }

    private Connection connection = null;
    /*
    public Connection getConnection()throws StoreException{
        String message;
        if (connection == null){
            try{
                SupportedDatabaseTypes databaseType = SupportedDatabaseTypes.valueOf(getDatabaseType());
                switch(databaseType){
                    case ACCESS -> {

                    }
                    case MYSQL -> {
                        connection = DriverManager.getConnection(getDatabaseURL(), "root", "ch19450907A@");
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy HH:mm:ss");
                        String formattedDateTime = now.format(formatter);
                        String sql = "INSERT INTO events (connection_datestamp) VALUES (?)";

                        try (PreparedStatement stmt = connection.prepareStatement(sql)){

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    case POSTGRESQL -> {
                        
                    }
                    case SQLSERVER ->{
                        
                    }
                }
            }catch (SQLException ex){
                message = ex.getMessage() + "\n";
                message = message + "StoreException raised in getConnection() for " + databaseType + " database" ;
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        
        return connection;
    }*/
    
    public Connection getConnection() throws StoreException{
        try{
            if (connection == null){
                connection = DriverManager.getConnection(getURL(), getUser(), getPassword());
            }
        }catch(SQLException ex){
            String message = ex.getMessage();
            message = message + "\nStoreException raised in Repository.getConnection(+ " + getURL() + ", " + getUser() + ", " + getPassword() + ") method";
            throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
        }
        return connection;
    }
        
    protected Connection getPMSStoreConnection()throws StoreException{
        String message;
        if (Repository.pmsStoreConnection  == null){
            try{
                Repository.pmsStoreConnection = DriverManager.getConnection(getURL(), getUser(), getPassword());
                pmsStoreConnection.setAutoCommit(true);
            }catch (SQLException ex){
                message = ex.getMessage() + "\n";
                message = message + "StoreException raised in "
                    + getRepositoryName() + ".getPMSStoreConnection()";
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        
        return Repository.pmsStoreConnection;
    }
    
    protected Connection getPMSStoreConnection(Boolean autoCommitIsEnabled )throws StoreException{
        String message;
        if (Repository.pmsStoreConnection  == null){
            try{
                Repository.pmsStoreConnection = DriverManager.getConnection(getURL(), getUser(), getPassword());
                pmsStoreConnection.setAutoCommit(autoCommitIsEnabled);
            }catch (SQLException ex){
                message = ex.getMessage() + "\n";
                message = message + "StoreException raised in "
                    + getRepositoryName() + ".getPMSStoreConnection()";
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        
        return Repository.pmsStoreConnection;
    }
    
    private IStoreClient runSQL(Repository.EntityType entitySQL, 
            Repository.PMSSQL pmsSQL, IStoreClient client)throws StoreException{
        IStoreClient result = null;
        try{
            getPMSStoreConnection().setAutoCommit(true);
            switch (entitySQL){
                case APPOINTMENT:
                    result = doPMSSQLforAppointment(pmsSQL, (Entity)client);
                    break;
                case APPOINTMENT_TREATMENT:
                    result = doPMSSQLforAppointmentTreatment(pmsSQL, (Entity)client);
                    break;
                case CLINICAL_NOTE:
                    result = doPMSSQLforClinicalNote(pmsSQL, (Entity)client);
                    break;    
                case DOCTOR:
                    result = doPMSSQLforDoctor(pmsSQL, (Entity)client);
                    break;
                case INVOICE:
                    result = doPMSSQLforInvoice(pmsSQL, (Entity)client);
                    break;
                case MEDICATION:
                    result = doPMSSQLforMedication(pmsSQL, (Entity)client);
                    break;
                case PATIENT:
                    result = doPMSSQLforPatient(pmsSQL, (Entity)client);
                    break;
                case PATIENT_APPOINTMENT_DATA:
                    result = doPMSSQLforPatientAppointmentData(pmsSQL, (Entity)client);
                    break;
                case PATIENT_NOTIFICATION:
                    result = doPMSSQLforNotification(pmsSQL, (Entity)client);
                    break;
                /*case PATIENT_NOTE:
                    result = doPMSSQLforPatientNote(pmsSQL, (Entity)client);
                    break;*/
                case PATIENT_PRIMARY_CONDITION:
                    result = doPMSSQLforPatientPrimaryCondition(pmsSQL, (Entity)client);
                    break;
                case PATIENT_QUESTION:
                    result = doPMSSQLforPatientQuestion(pmsSQL, (Entity)client);
                    break;
                case PATIENT_SECONDARY_CONDITION:
                    result = doPMSSQLforPatientSecondaryCondition(pmsSQL, (Entity)client);
                    break;
                case PRIMARY_CONDITION:
                    result = doPMSSQLforPrimaryCondition(pmsSQL, (Entity)client);
                    break;
                case QUESTION:
                    result = doPMSSQLforQuestion(pmsSQL, (Entity)client);
                    break;
                case SECONDARY_CONDITION:
                    result = doPMSSQLforSecondaryCondition(pmsSQL, (Entity)client);
                    break;
                case SURGERY_DAYS_ASSIGNMENT:
                    result = doPMSSQLforSurgeryDaysAssignment(pmsSQL, (Entity)client);
                    break;
                case TO_DO:
                    result = doPMSSQLforToDo(pmsSQL, (Entity)client);
                    break;
                case TREATMENT:
                    result = doPMSSQLforTreatment(pmsSQL, (Entity)client);
                    break;
                case TREATMENT_COST:
                    result = doPMSSQLforTreatmentCost(pmsSQL, (Entity)client);
                    break;
                case USER:
                    result = doPMSSQLforUser(pmsSQL, (Entity)client);
                    break;
                case USER_SETTINGS:
                    result = doPMSSQLforUserSettings(pmsSQL, (Entity)client);
                    
            }
            return result;
        }catch (SQLException ex){
            String msg = ex.getMessage() +"\n"
                    + "StoreException raised in Repository::runSQL()";
            throw new StoreException(msg, StoreException.ExceptionType.SQL_EXCEPTION);
                    
        }
    } 
    
    
    
    /**
     * Method
     * @param appointment
     * @param rs
     * @return
     * @throws StoreException 
     */
    private Appointment get(Appointment appointment, ResultSet rs) throws StoreException {
        Appointment result = null;
        ArrayList<Appointment> appointments = new ArrayList<>();
        try {
            switch(appointment.getScope()){
                case SINGLE:{
                    if (!rs.wasNull()) {
                        if (rs.next()){
                            result = getAppointmentDetailsFromRs(rs);
                            result.setScope(Entity.Scope.SINGLE);
                        }
                    }
                    break;
                }
                default:
                    if (!rs.wasNull()) {
                        while (rs.next()) {
                            Appointment nextAppointment = getAppointmentDetailsFromRs(rs);
                            appointments.add(nextAppointment);
                        }
                        appointment.set(appointments);
                        result = appointment;
                    }
                    break;
            }  
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Access::get(Appointment,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
        return result;
    }
    
    /**
     * Method constructs an AppointmentDelegate from the fields fetched from persistent store
     * @param rs
     * @return Appointment
     * @throws SQLException 
     */
    private Appointment getAppointmentDetailsFromRs(ResultSet rs)throws SQLException{
        Appointment appointment = new Appointment();
        //AppointmentDelegate delegate = null;
        Patient patient = null;
        /*PatientNoteDelegate patientNoteDelegate = null;*/
        
        int key = rs.getInt("pid");
        appointment.setKey(key);
        appointment.setStart(rs.getObject("Start", LocalDateTime.class));
        appointment.setDuration(Duration.ofMinutes(rs.getLong("Duration")));
        /*28/03/2024appointment.setNotes(rs.getString("Notes"));*/
        appointment.setNotes(rs.getString("Notes"));
        appointment.setHasPatientBeenContacted(rs.getBoolean("hasPatientBeenContacted"));
        appointment.setIsDeleted(rs.getBoolean("isDeleted"));
        appointment.setIsCancelled(rs.getBoolean("isCancelled"));
        appointment.setIsEmergency(rs.getBoolean("isEmergency"));
        int invoiceKey = rs.getInt("invoiceKey");
        if (invoiceKey==0)appointment.setInvoice(new Invoice(invoiceKey));

        int patientKey = rs.getInt("PatientKey");
        patient = new Patient(patientKey);
        patient.setKey(patientKey); 
        appointment.setPatient(patient);

        //delegate = new AppointmentDelegate(appointment);
        //delegate.setAppointmentKey(key);
        return appointment;
    }
    
    private String getName(Entity entity){
        if (entity.getIsAppointment()) return "Appointment";
        if (entity.getIsPatient()) return "Patient";
        if (entity.getIsClinicNote()) return "ClinicNote";
        /*28/03/2024if (entity.getIsPatientNote()) return "PatientNote";*/
        if (entity.getIsPatientNotification()) return "PatientNotification";
        if (entity.getIsSurgeryDaysAssignment()) return "SurgeryDaysAssignment";
        if (entity.getIsPrimaryCondition()) return "PrimaryCondition";
        if (entity.getIsSecondaryCondition()) return "SecondaryCondition";
        if (entity.getIsMedication()) return "Medication";
        if (entity.getIsDoctor()) return "Doctor";
        if (entity.getIsTreatment()) return "Treatment";
        if (entity.getIsAppointmentTreatment()) return "AppointmentTreatment";
        return null;
    }
    
    private Entity doCount(String sql, Entity entity)throws StoreException{
        Point value;
        if (getName(entity) != null){
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next())value = new Point(rs.getInt("row_count"),0);
                else value = new Point();
                entity = new Entity();
                entity.setValue(value);
                return entity;
            }catch (SQLException ex) {
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                        + "StoreException message -> exception raised in Repository::doCount("
                        + getName(entity) +")",
                        StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        else throw new StoreException("Entity concrete subclass undefined in Repository.doCount(sql, entity)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        
    }
    
    private void doCreateAppointmentTable(String sql)throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();

        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in doCreateAppointmentTable(sql) ",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateTable(String sql)throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();

        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in doCreateTable(sql) ",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doRecoverPatientChild(String sql, Entity entity)throws StoreException{
        String entityType = null;
        try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            if (entity.getIsAppointment()){
                entityType = "Appointment";
                Appointment appointment = (Appointment)entity;
                preparedStatement.setLong(1, appointment.getKey());      
            }/*else if(entity.getIsPatientNotification()){
                entityType = "PatientNotification";
                NotificationDelegate delegate = (NotificationDelegate)entity;
                preparedStatement.setLong(1, delegate.getKey());
            }*//*else if(entity.getIsPatientNote()){
                entityType = "PatientNote";
                //PatientNoteDelegate delegate = (PatientNoteDelegate)entity;
                preparedStatement.setLong(1, delegate.getKey());
            }*/
            else{
                throw new StoreException("Expected parent child entity is neither "
                        + "Appointment nor PatientNotification", 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
            preparedStatement.executeUpdate();
        }catch(SQLException ex){
            String message = ex.getMessage() + "\nStoreException raised in "
                    + "Repository.doRecoverPatientChild("
                    + entityType + ")";
            throw new StoreException(message, 
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doRecoverPatient(Entity entity)throws StoreException{
        String sql;
        if (entity.getIsPatient()){
            Patient patient = (Patient)entity;
            try(Connection conn = getPMSStoreConnection();){
                conn.setAutoCommit(false);
                String[] queries = {"UPDATE Patient SET isDeleted = false WHERE pid = ?;",
                                    "UPDATE Appointment SET isDeleted = false where patientKey = ?;",
                                    "UPDATE PatientNotification SET IsDeleted = false WHERE patientToNotify = ?;"};
                try{
                    for (String query : queries){
                        try(PreparedStatement preparedStatement = conn.prepareStatement(query);){
                            preparedStatement.setLong(1, patient.getKey());
                            preparedStatement.executeUpdate();
                        } 
                    }
                    conn.commit();
                }catch(SQLException ex){
                    if (conn!=null){
                        try{
                            conn.rollback();
                        }catch(SQLException exe){
                            String message = exe.getMessage() + "\n";
                            throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
                        }
                    }
                    String message = ex.getMessage() + "\n"
                            + "Unable to rollback transaction because database connection is null. StoreException raised in Repository::doRecoverPatient()";
                    throw new StoreException(message, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                }
            }catch(SQLException ex){
                String message = ex.getMessage() + "\n"
                        + "StoreException raised in Repository::doRecoverPatient() on sttempt to establish a database connection";
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }   
        }else{
            String message = "StoreException raised in "
                    + "Repository::doRecoverPatient(); entity data type not a Patient";
            throw new StoreException(message, 
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    /**
     * Multiple persistent store operation contained in a single transaction
     * -- delete the included collection  of appointments
     * -- delete the included collection of patient notifications
     * -- prior to deleting the referenced patient
     * @param entity 
     */
    private void doDeletePatient(Entity entity)throws StoreException{ 
        String sql;
        if (entity.getIsPatient()){
            Patient patient = (Patient)entity;
            try(Connection conn = getPMSStoreConnection();){
                conn.setAutoCommit(false);
                sql = "UPDATE Appointment SET isDeleted = true where patientKey = ?;";
                //PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setLong(1, patient.getKey());
                preparedStatement.executeUpdate();
                sql = "UPDATE PatientNotification SET IsDeleted = true WHERE patientToNotify = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getKey());
                sql = "UPDATE Patient SET isDeleted = true WHERE pid = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getKey());
                preparedStatement.executeUpdate();
                getPMSStoreConnection().commit();
            }
            catch (SQLException ex){
                String message = ex.getMessage() + "\n"
                        + "StoreException raised in Repository::doDeletePatient()";
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        else{
            String message = "StoreException raised in "
                    + "Repository::doDeletePatient(); entity data type invalid";
            throw new StoreException(message, 
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDelete(String sql)throws StoreException{
        try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.executeUpdate();
        }catch (SQLException ex){
            String message = ex.getMessage() + "\n";
            throw new StoreException(message 
                    + "StoreException raised in Repository::doDelete(" + sql + ")",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doDeleteEmergencyAppointment(String sql, Entity entity) throws StoreException{
        if (entity.getIsAppointment()){
            //AppointmentDelegate delegate = (AppointmentDelegate)entity;
            Appointment appointment = (Appointment)entity;
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                //preparedStatement.setInt(1, ((AppointmentDelegate)delegate).getAppointmentKey());
                preparedStatement.setInt(1, appointment.getKey());
                preparedStatement.execute();
            }catch(SQLException ex){
                String message = ex.getMessage() + "\n";
                throw new StoreException(message 
                        + "StoreException raised in Repository::doDeleteEmergencyAppointment()",
                        StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }else{
            String message = "Entity not an Appointment as expected\n"
                    + "StoreException raised in Repository::doDeleteEmergencyAppointment()";
            throw new StoreException(message, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
                
                    
    }
    
    private void doDeleteCancelChildEntity(String sql, Entity entity)throws StoreException{
        try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.setInt(1,entity.getKey());
            /*
            if (entity.getIsAppointment()){
                Appointment appointment = (Appointment)entity;
                //delegate = (AppointmentDelegate)entity;
                //preparedStatement.setInt(1, ((AppointmentDelegate)delegate).getAppointmentKey());
                preparedStatement.setInt(1, (appointment.getKey()));
            }else if (entity.getIsPatientNotification()){
                delegate = (NotificationDelegate)entity;
                preparedStatement.setInt(1, ((NotificationDelegate)delegate).getKey());
            }else
            //preparedStatement.setInt(1, ((AppointmentDelegate)delegate).getAppointmentKey());*/
            preparedStatement.executeUpdate();
        }catch (SQLException ex){
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteCancelChild(String sql, Entity entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
   
    private void doInsertAppointment(String sql, Entity entity)throws StoreException{
        if (entity.getIsAppointment()){
            Appointment appointment = (Appointment)entity;
            try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                preparedStatement.setBoolean(1,appointment.getIsEmergency());
                //if (delegate.getInvoice()!=null) preparedStatement.setInt(2, delegate.getInvoice().getKey());
                //else preparedStatement.setNull(2, java.sql.Types.INTEGER);
                if (appointment.getInvoice()==null) preparedStatement.setInt(2, 1066);
                else preparedStatement.setInt(2, appointment.getInvoice().getKey());
                preparedStatement.setInt(3, 
                        ((Patient)appointment.getPatient()).getKey());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(appointment.getStart()));
                preparedStatement.setLong(5, appointment.getDuration().toMinutes());
                preparedStatement.setString(6, appointment.getNotes());
                preparedStatement.setLong(7, appointment.getKey());
                preparedStatement.executeUpdate();
                   
            } catch (SQLException ex) {
                switch (repositoryName){
                    case "ACCESS":
                        if (!(ex.getMessage().contains("foreign key no parent"))
                            && !(ex.getMessage().contains("Missing columns in relationship"))) {
                            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                    + "StoreException message -> exception raised in Repository::runSQL(PracticeManagementSystemSQL.INSERT_APPOINTMENT)",
                                    StoreException.ExceptionType.SQL_EXCEPTION);
                        }
                        break;
                    case "POSTGRES":
                        if (!(ex.getMessage().contains("appointment_fk1"))){
                            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                    + "StoreException message -> exception raised in Repository::runSQL(PracticeManagementSystemSQL.INSERT_APPOINTMENT)",
                                    StoreException.ExceptionType.SQL_EXCEPTION);
                        }
                        
                        /*
                        else{
                            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                    + "StoreException message -> exception raised in Repository::runSQL(PracticeManagementSystemSQL.INSERT_APPOINTMENT)",
                                    StoreException.ExceptionType.SQL_EXCEPTION);
                        }   
                        */
                }

            }
        }
    }
   
    private Entity doReadAppointmentHighestKey(String sql)throws StoreException{
        Point key;
        try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                key = new Point((int) rs.getLong("highest_key"), 0);
            } else {
                key = new Point();
            }
            Entity entity = new Entity();
            entity.setValue(key);
            return entity; 
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::runSQL(AppointmentSQL..) during execution of an READ_HIGHEST_KEY statement",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private Entity doReadAppointmentsForDay(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    LocalDate day = appointment.getStart().toLocalDate();
                    preparedStatement.setInt(1, day.getYear());
                    preparedStatement.setInt(2, day.getMonthValue());
                    preparedStatement.setInt(3, day.getDayOfMonth());
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(appointment, rs);
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadAppointmentsForDay()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = 
                        "Unexpected data type specified for entity in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String msg = 
                        "Entity data type undefined in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }   
        return result;
    }
    
    private Entity doReadAppointmentsFromDay(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    LocalDate day = appointment.getStart().toLocalDate();
                    preparedStatement.setDate(1, java.sql.Date.valueOf(day));
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(appointment, rs);
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadAppointmentsForDay()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = 
                        "Unexpected data type specified for entity in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String msg = 
                        "Entity data type undefined in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }   
        return result;
    }
    
    private Entity doReadAppointmentsForPatient(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setInt(1, 
                            ((Patient)appointment.getPatient()).getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(appointment, rs);
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadAppointmentsForDay()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = 
                        "Unexpected data type specified for entity in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String msg = 
                        "Entity data type undefined in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }   
        return result;
    }
    
    private Entity doReadAppointmentsForInvoice(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setInt(1,appointment.getInvoice().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(appointment, rs);
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadAppointmentsForDay()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = 
                        "Unexpected data type specified for entity in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String msg = 
                        "Entity data type undefined in Repository::doReadAppointmentsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }   
        return result;
    }
    
    private void doUpdateAppointment(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsAppointment()){
                Appointment appointment = (Appointment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    if (appointment.getPatient() != null) {
                        preparedStatement.setInt(1, 
                                ((Patient)appointment.getPatient()).getKey());
                    }
                    if (appointment.getInvoice()==null)
                        preparedStatement.setNull(2, java.sql.Types.INTEGER);
                    else preparedStatement.setInt(2, appointment.getInvoice().getKey());
                    preparedStatement.setTimestamp(3, Timestamp.valueOf(appointment.getStart()));
                    preparedStatement.setLong(4, appointment.getDuration().toMinutes());
                    preparedStatement.setString(5, appointment.getNotes());
                    preparedStatement.setBoolean(6, appointment.getIsEmergency());
                    preparedStatement.setBoolean(7, appointment.getHasPatientBeenContacted());
                    preparedStatement.setLong(8, appointment.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doUpdateAppointment(sql, entity",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> entity wrongly defined in Repository::doUpdateAppointment(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String msg = "StoreException -> entity undefined in Repository::doUpdateAppointment(sql, entity)";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
            
    }

    /**
     * method unloads the patient field values fetched from persistent store
     * -- if scope is SINGLE the process constructs a new Patient object from the fetched persistent store values. Since persistent store does not include a scope value, the SINGLE value is added in case required further downstream   
     * -- else the process returns the collection of records read from persistent store. These are added to the patient object specified in the method parameters.
     * @param patient
     * @param rs; ResultSet returned from persistent store
     * @return Patient object, which is different to the object specified in method parameters if SINGLE scope is defined
     * @throws StoreException 
     */
    private Patient get(Patient patient, ResultSet rs) throws StoreException{
        Patient result = null;
        ArrayList<Patient> patients = new ArrayList<>();
        try{
            switch (patient.getScope()){
                case SINGLE:
                    if (!rs.wasNull()) {
                        if (rs.next()) {
                            result = getPatientDetailsFromResultSet(rs);
                            result.setScope(Entity.Scope.SINGLE);
                        }
                    } else {
                        result = null;
                    }
                    break;
                default:
                    if (!rs.wasNull()) {
                        while (rs.next()) {
                            Patient nextPatient = getPatientDetailsFromResultSet(rs);
                            patients.add(nextPatient);
                        }
                        patient.set(patients);
                    }
                    result = patient;
                    break;
            }
            
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Access::get(Patient,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
        return result;
    }
    
    /**
     * method unloads the patient field values fetched from persistent store
     * -- a delegate object is created to enable the transfer of the patient's key value
     * -- another delegate is created if a guardian exists for this patient  
     * @param rs
     * @return Patient object which is freshly constructed 
     * @throws SQLException 
     */
    private Patient getPatientDetailsFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        int key = rs.getInt("pid");
        patient.getName().setTitle(rs.getString("title"));
        patient.getName().setForenames(rs.getString("forenames"));
        patient.getName().setSurname(rs.getString("surname"));
        patient.getAddress().setLine1(rs.getString("line1"));
        patient.getAddress().setLine2(rs.getString("line2"));
        patient.getAddress().setTown(rs.getString("town"));
        patient.getAddress().setCounty(rs.getString("county"));
        patient.getAddress().setPostcode(rs.getString("postcode"));
        patient.setPhone1(rs.getString("phone1"));
        patient.setPhone2(rs.getString("phone2"));
        patient.setEmail(rs.getString("email"));
        patient.setGender(rs.getString("gender"));
        patient.setNotes(rs.getString("notes"));
        patient.setIsArchived(rs.getBoolean("isArchived"));
        LocalDate dob = rs.getObject("dob", LocalDate.class);
        patient.setDOB(dob);
        patient.getRecall().setDentalFrequency(rs.getInt("recallFrequency"));
        LocalDate recallDate = rs.getObject("recallDate", LocalDate.class);
        patient.getRecall().setDentalDate(recallDate);
        patient.getRecall().setGBTFrequency(rs.getInt("recallFrequencyGBT"));
        recallDate = rs.getObject("recallDateGBT", LocalDate.class);
        patient.getRecall().setGBTDate(recallDate);
        
        patient.setIsGuardianAPatient(rs.getBoolean("isGuardianAPatient"));
        if (patient.getIsGuardianAPatient()) {
            int guardianKey = rs.getInt("guardianKey");
            if (guardianKey > 0) {
                Patient guardian = new Patient(guardianKey);
                patient.setGuardian(guardian);
            }
        }
        
        patient.setIsRequestToSendPatientGBTRecallPending(rs.getBoolean("isRequestToSendPatientGBTRecallPending"));
        patient.setIsRequestToSendPatientNonGBTRecallPending(rs.getBoolean("isRequestToSendPatientNonGBTRecallPending"));
        patient.setLastGBTRecallSentDate(rs.getObject("lastGBTRecallSentDate", LocalDate.class));
        patient.setLastNonGBTRecallSentDate(rs.getObject("lastNonGBTRecallSentDate", LocalDate.class));
        patient.setKey(rs.getInt("pid"));
        return patient;
    }
    
    private void doInsertSecondaryCondition(String sql, Entity entity)throws StoreException{
        SecondaryCondition sc = null;
        if (entity != null){
            if (entity.getIsSecondaryCondition()){
                sc = (SecondaryCondition)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    Integer testKey = sc.getKey();
                    Integer test = sc.getPrimaryCondition().getKey();                 
                    preparedStatement.setLong(1,sc.getKey());
                    preparedStatement.setLong(2,sc.getPrimaryCondition().getKey());
                    preparedStatement.setString(3,sc.getDescription());
                    /*preparedStatement.setBoolean(4,sc.getState());*/
                    preparedStatement.setBoolean(4,sc.getIsDeleted());
                    /*preparedStatement.setString(6,sc.getNotes());*/
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertSecondaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition object\n"
                        + "StoreException raised in repository::doInsertSecondaryCondition()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String message = "Entity undefined\n"
                    + "StoreException raised in repository::doInsertSecondaryCondition()";
            throw new StoreException(message, 
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doInsertPatientAppointmentData(String sql, Entity entity)throws StoreException{
        PatientAppointmentData pad = null;
        if(entity!=null){
            if (entity.getIsPatientAppointmentData()){
                pad = (PatientAppointmentData)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setInt(1,pad.getFromYear());
                    preparedStatement.setInt(2,pad.getToYear());
                    preparedStatement.execute();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in "
                            + "Repository::doInsertPatientAppointmentData("
                            + "from=" + pad.getFromYear() + ", to=" + pad.getToYear() + ")",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a PatientAppointmentData object\n"
                        + "StoreException raised in repository::doInsertPatientAppointmentData()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
                String message = "Entity not defined (null)\n"
                        + "StoreException raised in repository::doInsertPatientAppointmentData()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doInsertClinicalNote(String sql, Entity entity)throws StoreException{
        ClinicalNote clinicNote = null;
        if (entity != null){
            if (entity.getIsClinicNote()){
                clinicNote = (ClinicalNote)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,clinicNote.getKey());;
                    preparedStatement.setString(2,clinicNote.getNotes());
                    preparedStatement.setBoolean(3,clinicNote.getIsDeleted());
                    preparedStatement.execute();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertClinicNote()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertClinicNote()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertTreatment(String sql, Entity entity)throws StoreException{
        Treatment treatment = null;
        if (entity != null){
            if (entity.getIsTreatment()){
                treatment = (Treatment)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,treatment.getKey());
                    preparedStatement.setString(2,treatment.getDescription());
                    preparedStatement.setBoolean(3,treatment.getIsDeleted());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertTreatment()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertTreatment()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doUpdateUserSettings(String sql, Entity entity)throws StoreException{
        HashMap<SystemDefinition.Properties, Object> settings = null;
        UserSettings userSettings = null;
        if (entity!=null){
            if (entity.getIsUserSettings()){
                userSettings = (UserSettings)entity; 
                settings = userSettings.getSettings();
                Connection pmsConnection = getPMSStoreConnection(false);
                try (PreparedStatement preparedStatement = pmsConnection.prepareStatement(sql);){
                    switch(userSettings.getScope()){
                        case USER_SCHEDULE_DIARY_SETTINGS,
                             USER_SCHEDULE_LIST_SETTINGS ->{
                             for(SystemDefinition.Properties key : settings.keySet()){
                                preparedStatement.setInt(1,((Color)settings.get(key)).getRed());
                                preparedStatement.setInt(2,((Color)settings.get(key)).getGreen());
                                preparedStatement.setInt(3,((Color)settings.get(key)).getBlue());
                                preparedStatement.setNull(4,Types.VARCHAR);
                                preparedStatement.setNull(5,Types.TINYINT);
                                preparedStatement.setString(6, userSettings.getUser().getUsername());
                                preparedStatement.setString(7,key.toString());
                                preparedStatement.executeUpdate();
                            }
                            break;
                        } 
                        case USER_SYSTEM_WIDE_SETTINGS ->{
                            for(SystemDefinition.Properties key : settings.keySet()){
                                switch(key){
                                    case TITLED_BORDER_COLOR ->{
                                        preparedStatement.setInt(1,((Color)settings.get(key)).getRed());
                                        preparedStatement.setInt(2,((Color)settings.get(key)).getGreen());
                                        preparedStatement.setInt(3,((Color)settings.get(key)).getBlue());
                                        preparedStatement.setNull(4,Types.VARCHAR);
                                        preparedStatement.setNull(5,Types.TINYINT);
                                        preparedStatement.setString(6, userSettings.getUser().getUsername());
                                        preparedStatement.setString(7,key.toString());
                                        preparedStatement.executeUpdate();
                                        break;
                                    }
                                    case TITLED_BORDER_FONT ->{
                                        preparedStatement.setNull(1,Types.TINYINT);
                                        preparedStatement.setNull(2,Types.TINYINT);
                                        preparedStatement.setNull(3,Types.TINYINT);
                                        preparedStatement.setString(4,((Font)settings.get(key)).getFamily());
                                        preparedStatement.setInt(5,((Font)settings.get(key)).getSize());
                                        preparedStatement.setString(6, userSettings.getUser().getUsername());
                                        preparedStatement.setString(7,key.toString());
                                        preparedStatement.executeUpdate();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }catch(SQLException ex){
                    try{
                        pmsStoreConnection.rollback();
                    }catch (SQLException e){
                        throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doUpdateUserSettings() after attempt to rollback the table update",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                    }
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doUpdateUserSettings(); rollback performed",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
    }
    /**
     * On entry single username variable defined in SQL statement selects all settings which belong to the user
     * The subsequent get(UserSettings,Recordset) method selects from these the subset of settings defined by the UserSettings settings property
     * @param sql
     * @param entity
     * @return
     * @throws StoreException 
     */
    private Entity doReadUserSettings(String sql, Entity entity)throws StoreException{
        Entity result = null;
        UserSettings userSettings = null;
        if (entity!=null){
            if (entity.getIsUserSettings()){
                userSettings = (UserSettings)entity;   
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, userSettings.getUser().getUsername());   
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(userSettings,rs);
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doReadUserSettings()",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
        return result;
    }
    
    private void doInsertUserSettings(String sql, Entity entity)throws StoreException{
        HashMap<SystemDefinition.Properties,Object> settings = null;
        UserSettings userSettings = null;
        if (entity!=null){
            if (entity.getIsUserSettings()){
                userSettings = (UserSettings)entity; 
                settings = userSettings.getSettings();
                Connection pmsConnection = getPMSStoreConnection(false);
                try (PreparedStatement preparedStatement = pmsConnection.prepareStatement(sql);){
                    switch(userSettings.getScope()){
                        case USER_SCHEDULE_DIARY_SETTINGS ->{
                            for(SystemDefinition.Properties key : settings.keySet()){
                                preparedStatement.setString(1, userSettings.getUser().getUsername());
                                preparedStatement.setString(2,key.toString());
                                preparedStatement.setInt(3,((Color)settings.get(key)).getRed());
                                preparedStatement.setInt(4,((Color)settings.get(key)).getGreen());
                                preparedStatement.setInt(5,((Color)settings.get(key)).getBlue());
                                preparedStatement.setNull(6,Types.VARCHAR);
                                preparedStatement.setNull(7,Types.TINYINT);
                                preparedStatement.executeUpdate();
                            }
                            break;
                        }
                        case USER_SCHEDULE_LIST_SETTINGS ->{
                            for(SystemDefinition.Properties key : settings.keySet()){
                                preparedStatement.setString(1, userSettings.getUser().getUsername());
                                preparedStatement.setString(2,key.toString());
                                preparedStatement.setInt(3,((Color)settings.get(key)).getRed());
                                preparedStatement.setInt(4,((Color)settings.get(key)).getGreen());
                                preparedStatement.setInt(5,((Color)settings.get(key)).getBlue());
                                preparedStatement.setNull(6,Types.VARCHAR);
                                preparedStatement.setNull(7,Types.TINYINT);
                                preparedStatement.executeUpdate();
                            }
                            break;
                        }
                        case USER_SYSTEM_WIDE_SETTINGS ->{
                            for(SystemDefinition.Properties key : settings.keySet()){
                                preparedStatement.setString(1, userSettings.getUser().getUsername());
                                preparedStatement.setString(2,key.toString());
                                switch(key){
                                    case TITLED_BORDER_COLOR ->{
                                        preparedStatement.setInt(3, ((Color)settings.get(key)).getRed());
                                        preparedStatement.setInt(4, ((Color)settings.get(key)).getGreen());
                                        preparedStatement.setInt(5, ((Color)settings.get(key)).getBlue());
                                        preparedStatement.setNull(6,Types.VARCHAR);
                                        preparedStatement.setNull(7,Types.TINYINT);
                                        preparedStatement.executeUpdate();
                                        break;
                                    }
                                    case TITLED_BORDER_FONT ->{
                                        preparedStatement.setNull(3, Types.TINYINT);
                                        preparedStatement.setNull(4, Types.TINYINT);
                                        preparedStatement.setNull(5, Types.TINYINT);
                                        preparedStatement.setString(6,((Font)settings.get(key)).getName());
                                        preparedStatement.setInt(7,((Font)settings.get(key)).getSize());
                                        preparedStatement.executeUpdate();
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                    

                }catch(SQLException ex){
                    try{
                        pmsStoreConnection.rollback();
                    }catch (SQLException e){
                        throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doInsertUserSettings() after attempt to rollback the table insertion",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                    }
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doInsertUserSettings(); rollback performed",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                } 
            }
        }
    }
    
    private void doInsertUser(String sql, Entity entity)throws StoreException{
        User _user = null;
        if (entity != null){
            if (entity.getIsUser()){
                _user = (User)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    //preparedStatement.setLong(1,_user.getKey());
                    preparedStatement.setString(1,_user.getUsername());
                    preparedStatement.setString(2,SystemDefinition.userP);
                    preparedStatement.setString(3,SystemDefinition.userS);
                    preparedStatement.setBoolean(4,_user.getIsDeleted());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertUser()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertUser()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertQuestion(String sql, Entity entity)throws StoreException{
        Question question = null;
        if (entity != null){
            if (entity.getIsQuestion()){
                question = (Question)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,question.getKey());
                    preparedStatement.setString(2,question.getCategory().toString());
                    preparedStatement.setString(3,question.getDescription());
                    preparedStatement.setBoolean(4,question.getIsDeleted());
                    preparedStatement.setInt(5,question.getOrder());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertQuestion()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertQuestion()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertTransmission(String sql,Entity entity) throws StoreException{
        Transmission transmission = null;
        if (entity != null){
            if (entity.getIsTransmission()){
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,transmission.getPatient().getKey());
                    preparedStatement.setString(2, transmission.getMode().name());
                    preparedStatement.setString(3, transmission.getStatus().name());
                    if (transmission.getDate() != null) {
                        preparedStatement.setDate(4, java.sql.Date.valueOf(transmission.getDate()));
                    } else {
                        preparedStatement.setNull(4, java.sql.Types.DATE);
                    }
                    preparedStatement.setString(5, transmission.getContent());
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertTransmission()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
    }
    
    private void doUpdateTransmission(String sql, Entity entity)throws StoreException{
        Transmission transmission = null;
        if (entity != null){
            if (entity.getIsTransmission()){
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,transmission.getPatient().getKey());
                    preparedStatement.setString(2, transmission.getMode().name());
                    preparedStatement.setString(3, transmission.getStatus().name());
                    if (transmission.getDate() != null) {
                        preparedStatement.setDate(4, java.sql.Date.valueOf(transmission.getDate()));
                    } else {
                        preparedStatement.setNull(4, java.sql.Types.DATE);
                    }
                    preparedStatement.setString(5, transmission.getContent());
                    preparedStatement.setLong(6, transmission.getKey());
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateTransmission()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
    }
    
    private Transmission doReadTransmissionPending(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Transmission transmission;
        if (entity != null) {
            if (entity.getIsTransmission()){
                transmission = (Transmission)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    switch (transmission.getScope()){
                        case PENDING ->{
                            break;
                        }
                        case PENDING_FOR_PATIENT ->{
                            preparedStatement.setLong(1,transmission.getKey());
                            break;
                        }
                    }
                    ResultSet rs = preparedStatement.executeQuery();
                    result = getDataRead(entity, rs);
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadTransmissionPending()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = 
                        "Unexpected data type specified for entity in Repository::doReadTransmissionPendingr()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String msg = 
                        "Entity data type undefined in Repository::doReadTransmissionsForDay()";
                throw new StoreException(
                        msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }   
        return (Transmission)result;
    }
    
    private void doInsertPatientPrimaryCondition(String sql, Entity entity)throws StoreException{
        PatientPrimaryCondition patientPrimaryCondition = null;
        if (entity != null){
            if (entity.getIsPatientPrimaryCondition()){
                patientPrimaryCondition = (PatientPrimaryCondition)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,patientPrimaryCondition.getPatient().getKey());
                    preparedStatement.setLong(2,patientPrimaryCondition.getCondition().getKey());
                    preparedStatement.execute();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPatientPrimaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertPatientPrimaryCondition()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertPatientSecondaryCondition(String sql, Entity entity)throws StoreException{
        PatientSecondaryCondition patientSecondaryCondition = null;
        if (entity != null){
            if (entity.getIsPatientSecondaryCondition()){
                patientSecondaryCondition = (PatientSecondaryCondition)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,patientSecondaryCondition.getPatient().getKey());
                    preparedStatement.setLong(2,patientSecondaryCondition.getCondition().getKey());
                    preparedStatement.setString(3,patientSecondaryCondition.getComment());
                    preparedStatement.execute();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPatientSecondaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertPatientSecondaryCondition()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertAppointmentTreatment(String sql, Entity entity)throws StoreException{
        AppointmentTreatment appointmentTreatment = null;
        if (entity != null){
            if (entity.getIsAppointmentTreatment()){
                appointmentTreatment = (AppointmentTreatment)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,appointmentTreatment.getAppointment().getKey());
                    preparedStatement.setLong(2,appointmentTreatment.getTreatment().getKey());
                    preparedStatement.setString(3,appointmentTreatment.getComment());
                    preparedStatement.execute();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertAppointmentTreatment()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertAppointmentTreatment()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertPatientQuestion(String sql, Entity entity)throws StoreException{
        PatientQuestion appointmentQuestion = null;
        if (entity != null){
            if (entity.getIsPatientQuestion()){
                appointmentQuestion = (PatientQuestion)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,appointmentQuestion.getPatient().getKey());
                    preparedStatement.setLong(2,appointmentQuestion.getQuestion().getKey());
                    preparedStatement.setString(3,appointmentQuestion.getAnswer());
                    preparedStatement.execute();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPatientQuestion()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertPatientQuestion()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertMedication(String sql, Entity entity)throws StoreException{
        Medication medication = null;
        if (entity != null){
            if (entity.getIsMedication()){
                medication = (Medication)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,medication.getKey());
                    preparedStatement.setLong(2,medication.getPatient().getKey());
                    preparedStatement.setString(3,medication.getDescription());
                    preparedStatement.setString(4,medication.getNotes());
                    preparedStatement.setBoolean(5,medication.getIsDeleted());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertMedication()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertMedication()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertInvoice(String sql, Entity entity)throws StoreException{
        Invoice invoice = null;
        if (entity != null){
            if (entity.getIsInvoice()){
                invoice = (Invoice)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setDouble(1,invoice.getAmount());
                    preparedStatement.setString(2,invoice.getDescription());
                    preparedStatement.setBoolean(3,invoice.getIsDeleted());
                    preparedStatement.setLong(4,invoice.getPatient().getKey());
                    preparedStatement.setLong(5,invoice.getKey());
                    
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertInvoice()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertInvoice()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertTreatmentCost(String sql, Entity entity)throws StoreException{
        TreatmentCost treatmentCost = null;
        if (entity != null){
            if (entity.getIsTreatmentCost()){
                treatmentCost = (TreatmentCost)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setDouble(1,treatmentCost.getAmount());
                    preparedStatement.setString(2,treatmentCost.getDescription());
                    preparedStatement.setLong(3,treatmentCost.getInvoice().getKey());
                    preparedStatement.setBoolean(4,treatmentCost.getIsDeleted());
                    preparedStatement.setLong(5,treatmentCost.getKey());
                    
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertTreatmentCost()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertTreatmentCost()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertDoctor(String sql, Entity entity)throws StoreException{
        Doctor doctor = null;
        if (entity != null){
            if (entity.getIsDoctor()){
                doctor = (Doctor)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,doctor.getKey());
                    preparedStatement.setLong(2,doctor.getPatient().getKey());
                    preparedStatement.setString(3,doctor.getTitle());
                    preparedStatement.setString(4,doctor.getLine1());
                    preparedStatement.setString(5,doctor.getLine2());
                    preparedStatement.setString(6,doctor.getTown());
                    preparedStatement.setString(7,doctor.getCounty());
                    preparedStatement.setString(8,doctor.getPostcode());
                    preparedStatement.setString(9,doctor.getPhone());
                    preparedStatement.setString(10,doctor.getEmail());
                    preparedStatement.setBoolean(11,doctor.getIsDeleted());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertDoctor()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertDoctor()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertPrimaryCondition(String sql, Entity entity)throws StoreException{
        PrimaryCondition pc = null;
        if (entity != null){
            if (entity.getIsPrimaryCondition()){
                pc = (PrimaryCondition)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1,pc.getKey());
                    //preparedStatement.setLong(2,pc.getPatient().getKey());
                    preparedStatement.setString(2,pc.getDescription());
                    /*preparedStatement.setBoolean(3,pc.getState());
                    preparedStatement.setString(4,pc.getNotes());*/
                    preparedStatement.setBoolean(3,pc.getIsDeleted());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPrimaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String message = "Entity not defined as a Condition\n"
                        + "StoreException raised in repository::doInsertPrimaryCondition()";
                throw new StoreException(message, 
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
    }
    
    private void doInsertPatient(String sql, Entity entity)throws StoreException{
        Patient patient = null;
        if (entity != null) {
            if (entity.getIsPatient()) {
                //thePatient = (Patient)entity;
                patient = (Patient)entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, patient.getName().getTitle());
                    preparedStatement.setString(2, patient.getName().getForenames());
                    preparedStatement.setString(3, patient.getName().getSurname());
                    preparedStatement.setString(4, patient.getAddress().getLine1());
                    preparedStatement.setString(5, patient.getAddress().getLine2());
                    preparedStatement.setString(6, patient.getAddress().getTown());
                    preparedStatement.setString(7, patient.getAddress().getCounty());
                    preparedStatement.setString(8, patient.getAddress().getPostcode());
                    preparedStatement.setString(9, patient.getPhone1());
                    preparedStatement.setString(10, patient.getPhone2());
                    preparedStatement.setString(11, patient.getGender());
                    if (patient.getDOB() != null) {
                        preparedStatement.setDate(12, java.sql.Date.valueOf(patient.getDOB()));
                    } else {
                        //preparedStatement.setDate(12, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(12, java.sql.Types.DATE);
                    }
                    preparedStatement.setBoolean(13, patient.getIsGuardianAPatient());
                    if (patient.getRecall().getDentalFrequency()==null)
                        preparedStatement.setInt(14, 0);
                    else
                        preparedStatement.setInt(14, patient.getRecall().getDentalFrequency()); 
                    if (patient.getRecall().getDentalDate() != null) {
                        preparedStatement.setDate(15, java.sql.Date.valueOf(patient.getRecall().getDentalDate()));
                    } else {
                        //preparedStatement.setDate(15, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(15, java.sql.Types.DATE);
                    }
                    preparedStatement.setString(16, patient.getNotes());
                    preparedStatement.setLong(17, patient.getKey());
                    
                    if(patient.getIsGuardianAPatient()){
                        if (((Patient)patient.getGuardian()).getKey() > 0){
                            preparedStatement.setLong(18,((Patient)patient.getGuardian()).getKey());
                        }
                    }
                    else preparedStatement.setNull(18, java.sql.Types.INTEGER);
                    preparedStatement.setString(19, patient.getEmail());

                    if (patient.getRecall().getGBTFrequency()==null)
                        preparedStatement.setInt(20, 0);
                    else
                        preparedStatement.setInt(20, patient.getRecall().getGBTFrequency()); 
                    if (patient.getRecall().getGBTDate() != null) {
                        preparedStatement.setDate(21, java.sql.Date.valueOf(patient.getRecall().getGBTDate()));
                    } else {
                        //preparedStatement.setDate(15, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(21, java.sql.Types.DATE);
                    }
                    preparedStatement.setBoolean(22, patient.getIsArchived());
                    preparedStatement.setBoolean(23, patient.getIsRequestToSendPatientGBTRecallPending());
                    preparedStatement.setBoolean(24, patient.getIsRequestToSendPatientNonGBTRecallPending());
                    if (patient.getLastGBTRecallSentDate() != null) {
                        preparedStatement.setDate(25, java.sql.Date.valueOf(patient.getLastGBTRecallSentDate()));
                    } else {
                        preparedStatement.setNull(25, java.sql.Types.DATE);
                    }
                    if (patient.getLastNonGBTRecallSentDate() != null) {
                        preparedStatement.setDate(26, java.sql.Date.valueOf(patient.getLastNonGBTRecallSentDate()));
                    } else {
                        preparedStatement.setNull(26, java.sql.Types.DATE);
                    }
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPatient()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> entity invalidly defined, expected patient object, in Repository::doInsertPatient()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity undefined in Repository::doInsertPatient()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }

    private Entity doReadPatientAppointmentDataWithoutAppointment(String sql, Entity entity)throws StoreException{
        PatientAppointmentData pad = null;
        ResultSet rs = null;
        Entity result = null;
        if (entity!=null){
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql)){
                if(entity.getIsPatientAppointmentData()){
                    pad = (PatientAppointmentData)entity;
                    rs = preparedStatement.executeQuery();
                    result = getDataRead(entity, rs);
                }else {
                    rs = preparedStatement.executeQuery();
                    result = getDataRead(entity, rs);
                }
                 
            } catch (SQLException ex) {
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                        + "StoreException message -> exception raised in Repository::doReadAll( '" + getEntityType(entity) + "' )",
                        StoreException.ExceptionType.SQL_EXCEPTION);
            }   
        }else{
            String message = "StoreException raised because entity type undefined in "
                        + "Repository::doReadAll( '" + getEntityType(entity) + "' )";
                throw new StoreException(message,StoreException
                        .ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            
        }
        return result;
    }
    
    private Entity doReadAll(String sql, Entity entity)throws StoreException{
        PatientAppointmentData pad = null;
        ResultSet rs = null;
        Entity result = null;
        if (entity!=null){
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql)){
                if(entity.getIsPatientAppointmentData()){
                    pad = (PatientAppointmentData)entity;
                    if(!pad.getScope().equals(Entity.Scope.ARCHIVED)){
                        preparedStatement.setInt(1,pad.getFromYear());
                        preparedStatement.setInt(2,pad.getToYear());
                    }
                    rs = preparedStatement.executeQuery();
                    result = getDataRead(entity, rs);
                }else {
                    rs = preparedStatement.executeQuery();
                    result = getDataRead(entity, rs);
                }
                 
            } catch (SQLException ex) {
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                        + "StoreException message -> exception raised in Repository::doReadAll( '" + getEntityType(entity) + "' )",
                        StoreException.ExceptionType.SQL_EXCEPTION);
            }   
        }else{
            String message = "StoreException raised because entity type undefined in "
                        + "Repository::doReadAll( '" + getEntityType(entity) + "' )";
                throw new StoreException(message,StoreException
                        .ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            
        }
        return result;
    } 
    
    private Entity doReadHighestKey(String sql) throws StoreException{
        Entity entity;
        Point key;
        try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                key = new Point((int)rs.getLong("highest_key"),0);
            } else {
                key = new Point();
            }
            entity = new Entity();
            entity.setValue(key);
            return entity;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::doReadHighestKey()",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    
    
     private void doCreatePatientTable(String sql)throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doUpdatePatient(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsPatient()){
                Patient patient = (Patient)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, patient.getName().getTitle());
                    preparedStatement.setString(2, patient.getName().getForenames());
                    preparedStatement.setString(3, patient.getName().getSurname());
                    preparedStatement.setString(4, patient.getAddress().getLine1());
                    preparedStatement.setString(5, patient.getAddress().getLine2());
                    preparedStatement.setString(6, patient.getAddress().getTown());
                    preparedStatement.setString(7, patient.getAddress().getCounty());
                    preparedStatement.setString(8, patient.getAddress().getPostcode());
                    preparedStatement.setString(9, patient.getPhone1());
                    preparedStatement.setString(10, patient.getPhone2());
                    preparedStatement.setString(11, patient.getGender());
                    if (patient.getDOB() != null) {
                        preparedStatement.setDate(12, java.sql.Date.valueOf(patient.getDOB()));
                    } else {
                        //preparedStatement.setDate(12, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(12, java.sql.Types.DATE);
                    }
                    preparedStatement.setBoolean(13, patient.getIsGuardianAPatient());
                    
                    if (patient.getRecall().getDentalFrequency()==null)
                        preparedStatement.setInt(14, 0);
                    else
                        preparedStatement.setInt(14, patient.getRecall().getDentalFrequency());
                    if (patient.getRecall().getDentalDate() != null) {
                        preparedStatement.setDate(15, java.sql.Date.valueOf(patient.getRecall().getDentalDate()));
                    } else {
                        //preparedStatement.setDate(15, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(15, java.sql.Types.DATE);
                    }
                    preparedStatement.setString(16, patient.getNotes());
                    if (patient.getIsGuardianAPatient()) {
                        preparedStatement.setLong(17, ((Patient)patient.getGuardian()).getKey());
                    } else {
                        //preparedStatement.setNull(17, 0);
                        preparedStatement.setNull(17, Types.INTEGER);
                    }
                    preparedStatement.setString(18, patient.getEmail());
                    
                    if (patient.getRecall().getGBTFrequency()==null)
                        preparedStatement.setInt(19, 0);
                    else
                        preparedStatement.setInt(19, patient.getRecall().getGBTFrequency());

                    if (patient.getRecall().getGBTDate() != null) {
                        preparedStatement.setDate(20, java.sql.Date.valueOf(patient.getRecall().getGBTDate()));
                    } else {
                        //preparedStatement.setDate(15, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(20, java.sql.Types.DATE);
                    }
                    
                    preparedStatement.setBoolean(21, patient.getIsArchived());
                    
                    preparedStatement.setBoolean(22, patient.getIsRequestToSendPatientGBTRecallPending());
                    preparedStatement.setBoolean(23, patient.getIsRequestToSendPatientNonGBTRecallPending());
                    if (patient.getLastGBTRecallSentDate()!=null)
                        preparedStatement.setDate(24, java.sql.Date.valueOf(patient.getLastGBTRecallSentDate()));
                    else
                        preparedStatement.setNull(24, java.sql.Types.DATE);
                    if (patient.getLastNonGBTRecallSentDate()!=null)
                        preparedStatement.setDate(25, java.sql.Date.valueOf(patient.getLastNonGBTRecallSentDate()));
                    else
                        preparedStatement.setNull(25, java.sql.Types.DATE);
                    
                    preparedStatement.setLong(26, patient.getKey());
                    
                    
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePatient(sql, EntityStoreType)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> entity undefined in Repository::doUpdatePatient(sql, EntityStoreType)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity undefined in Repository::doUpdatePatient(sql, EntityStoreType)";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
   
    
    /**
     * method fetches a persistent store image which is constrained by the scope of the Notification parameter
 -- if the scope is SINGLE the first record only of the result set is returned; and the collection object in the Notification parameter is nullified
 -- else all records in the result set are returned and defined as the PatientNotificagtion's collection object, even if there are zero records in the collection object 
     * @param patientNotification; Notification object which on entry must have an initialised scope value
     * @param rs
     * @return Notification; this is a new instance of the Notification parameter;
     * @throws StoreException 
     */
    private Notification get(Notification patientNotification, ResultSet rs)throws StoreException{
        Notification result = null;
        ArrayList<Notification> collection = new ArrayList<>();
        //NotificationDelegate delegate = new NotificationDelegate(patientNotification);
        //delegate.set(null);
        Patient patient = new Patient(0);
        try{
            switch (patientNotification.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        int pid = rs.getInt("pid");
                        int patientKey = rs.getInt("patientToNotify");
                        LocalDate notificationDate = rs.getObject("notificationDate", LocalDate.class);
                        String notificationText = rs.getString("notificationText");
                        Boolean isActioned = rs.getBoolean("isActioned");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        Boolean isCancelled = rs.getBoolean("isCancelled");
                        patientNotification.setKey(pid);
                        patient.setKey(patientKey);
                        patientNotification.setPatient(patient);
                        patientNotification.setNotificationDate(notificationDate);
                        patientNotification.setNotificationText(notificationText);
                        patientNotification.setIsActioned(isActioned);
                        patientNotification.setIsCancelled(isCancelled);
                        patientNotification.setIsDeleted(isDeleted);
                        result = patientNotification;
                    }
                    break;
                default:
                    if (!rs.wasNull()){
                        while (rs.next()){
                           int pid = rs.getInt("pid");
                           int patientKey = rs.getInt("patientToNotify");
                           LocalDate notificationDate = rs.getObject("notificationDate", LocalDate.class);
                           String notificationText = rs.getString("notificationText");
                           Boolean isActioned = rs.getBoolean("isActioned");
                           //patientNotification = new NotificationDelegate();
                           patientNotification.setKey(pid);
                           patient = new Patient(patientKey);
                           //pDelegate.setPatientKey(patientKey);
                           patientNotification.setPatient(patient);
                           patientNotification.setNotificationDate(notificationDate);
                           patientNotification.setNotificationText(notificationText);
                           patientNotification.setIsActioned(isActioned);
                           collection.add(patientNotification);
                        }
                        patientNotification.set(collection);
                    }
                    result = patientNotification;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientNotification,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private PrimaryCondition get(PrimaryCondition primaryCondition, ResultSet rs)throws StoreException{
        PrimaryCondition result = null;
        ArrayList<Condition> collection = new ArrayList<>();
        try{
            switch (primaryCondition.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        //Integer patientKey = rs.getInt("patientKey");
                        String description = rs.getString("description");
                        //String notes = rs.getString("state"); 
                        //Boolean state = rs.getBoolean("isDeleted");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        primaryCondition = new PrimaryCondition(pid);
                        //primaryCondition.setPatient(new Patient(patientKey));
                        primaryCondition.setDescription(description);
                        //primaryCondition.setNotes(notes);
                        //primaryCondition.setState(state);
                        primaryCondition.setIsDeleted(isDeleted);
                        result = primaryCondition;
                    }
                    break;
                default:
                    LocalDateTime lastUpdated = null;
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            //Integer patientKey = rs.getInt("patientKey");
                            String description = rs.getString("description");
                            //String notes = rs.getString("notes"); 
                            //Boolean state = rs.getBoolean("state");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            primaryCondition = new PrimaryCondition(pid);
                            //primaryCondition.setPatient(new Patient(patientKey));
                            primaryCondition.setDescription(description);
                            //primaryCondition.setNotes(notes);
                            //primaryCondition.setState(state);
                            primaryCondition.setIsDeleted(isDeleted);
                            collection.add(primaryCondition);
                        }
                        primaryCondition.set(collection);
                    }
                    result = primaryCondition;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PrimaryCondition,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private SecondaryCondition get(SecondaryCondition secondaryCondition, ResultSet rs)throws StoreException{
        SecondaryCondition result = null;
        ArrayList<Condition> collection = new ArrayList<>();
        try{
            switch (secondaryCondition.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        Integer primaryConditionKey = rs.getInt("primaryConditionKey");
                        String description = rs.getString("description");
                        //String notes = rs.getString("notes");
                        //Boolean state = rs.getBoolean("state");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        secondaryCondition = new SecondaryCondition(pid);
                        secondaryCondition.setPrimaryCondition(new PrimaryCondition(primaryConditionKey));
                        secondaryCondition.setDescription(description);
                        //secondaryCondition.setNotes(notes);
                        //secondaryCondition.setState(state);
                        secondaryCondition.setIsDeleted(isDeleted);
                        result = secondaryCondition;
                    }
                    break;
                default:
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            Integer primaryConditionKey = rs.getInt("primaryConditionKey");
                            String description = rs.getString("description");
                            //String notes = rs.getString("notes");
                            //Boolean state = rs.getBoolean("state");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            secondaryCondition = new SecondaryCondition(pid);
                            secondaryCondition.setPrimaryCondition(new PrimaryCondition(primaryConditionKey));
                            secondaryCondition.setDescription(description);
                            //secondaryCondition.setNotes(notes);
                            //secondaryCondition.setState(state);
                            secondaryCondition.setIsDeleted(isDeleted);
                            collection.add(secondaryCondition);
                        }
                        secondaryCondition.set(collection);
                    }
                    result = secondaryCondition;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(SecondaryCondition,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    /**
     * 
     * @param userSettings; the settings property of which defines the sub set (category) of settings that have been requested
     * @param rs; ResultSet includes all the possible settings for the specified user
     * @return
     * @throws StoreException 
     */
    private UserSettings get(UserSettings userSettings, ResultSet rs)throws StoreException{
        SystemDefinition.Properties key = null;
        key = SystemDefinition.Properties.valueOf(userSettings.getScope().toString());
        switch (key){
            case USER_SCHEDULE_DIARY_SETTINGS ->{
                /**
                 * each setting in this group is a Color object
                 */
                 try{
                    if (!rs.wasNull()){
                       while(rs.next()){
                           Integer red = rs.getInt("red");
                           Integer green = rs.getInt("green");
                           Integer blue = rs.getInt("blue");
                           String setting = rs.getString("setting");
                           /**
                            * the setting values in the ResultSet are iterate through 
                            * -- this to find the matching setting in the UserSetting's settings property
                            * -- this entry in the UserSetting's settings is then initialised with the Color value fetched from the ResultSet
                            * 
                            */
                           for (Map.Entry<SystemDefinition.Properties,Object> entry: userSettings.getSettings().entrySet()){
                               if (setting.equals(entry.getKey().toString())){
                                   entry.setValue(new Color(red, green, blue));
                                   break;
                               }
                           }
                       } 
                    }
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(UserSettings userSettings, ResultSet rs) when handling settings category " + key,
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }           
            }
            case USER_SCHEDULE_LIST_SETTINGS ->{
                /**
                 * each setting in this group is a Color object
                 */
                try{
                    if (!rs.wasNull()){
                       while(rs.next()){
                           Integer red = rs.getInt("red");
                           Integer green = rs.getInt("green");
                           Integer blue = rs.getInt("blue");
                           String setting = rs.getString("setting");
                           /**
                            * the setting values in the ResultSet are iterate through 
                            * -- this to find the matching setting in the UserSetting's settings property
                            * -- this entry in the UserSetting's settings is then initialised with the Color value fetched from the ResultSet
                            * 
                            */
                           for (Map.Entry<SystemDefinition.Properties,Object> entry: userSettings.getSettings().entrySet()){
                               if (setting.equals(entry.getKey().toString())){
                                   entry.setValue(new Color(red, green, blue));
                                   break;
                               }
                           }
                       } 
                    }
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(UserSettings userSettings, ResultSet rs) when handling settings category " + key,
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
                            
            }
            case USER_SYSTEM_WIDE_SETTINGS ->{
                /**
                 * 2 settings in rthis group
                 * -- TITLED_BORDER_COLOR; which is a Color object
                 * -- TITLED_BORDER_FONT; which is a Font object
                 */
                try{
                    if (!rs.wasNull()){
                       while(rs.next()){
                           Integer red = rs.getInt("red");
                           Integer green = rs.getInt("green");
                           Integer blue = rs.getInt("blue");
                           String fontName = rs.getString("font");
                           Integer size = rs.getInt("size");
                           String setting = rs.getString("setting");
                           /**
                            * the setting values in the ResultSet are iterate through 
                            * -- on location of the settings property, its value in the UserSettings' settings property as follows
                            * -- this entry in the UserSetting's settings is then initialised with the Color value fetched from the ResultSet
                            * ---- if settings property is TITLED_BORDER_COLOR a Color object initialised from the red, gree, and blue values returned in the ResultSet
                            * ---- if setting property is TITLED_BORDER_FONT a Font object is initialised from the font (String) and size (Integer) values returned in the ResultSet
                            */
                           for (Map.Entry<SystemDefinition.Properties,Object> entry: userSettings.getSettings().entrySet()){
                               if (setting.equals(entry.getKey().toString())){
                                    if (SystemDefinition.Properties.valueOf(setting).equals(SystemDefinition.Properties.TITLED_BORDER_COLOR)){
                                        entry.setValue(new Color(red, green, blue));
                                        break;
                                    }else if(SystemDefinition.Properties.valueOf(setting).equals(SystemDefinition.Properties.TITLED_BORDER_FONT)){
                                        entry.setValue(new Font(fontName,Font.BOLD, size));
                                        break;
                                    }
                               }
                           }
                       } 
                    }
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(UserSettings userSettings, ResultSet rs) when handling settings category " + key,
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
        return userSettings;
    }
    
    private Doctor get(Doctor doctor, ResultSet rs)throws StoreException{
        Doctor result = null;
        ArrayList<Doctor> collection = new ArrayList<>();
        try{
            switch (doctor.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        Integer patientKey = rs.getInt("patientKey");
                        String title = rs.getString("title");
                        String line1 = rs.getString("line1");
                        String line2 = rs.getString("line2");
                        String town = rs.getString("town");
                        String county = rs.getString("county");
                        String postcode = rs.getString("postcode");
                        String phone = rs.getString("phone");
                        String email = rs.getString("email");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        doctor = new Doctor(pid);
                        doctor.setPatient(new Patient(patientKey));
                        doctor.setTitle(title);
                        doctor.setLine1(line1);
                        doctor.setLine2(line2);
                        doctor.setTown(town);
                        doctor.setCounty(county);
                        doctor.setPostcode(postcode);
                        doctor.setPhone(phone);
                        doctor.setEmail(email);
                        doctor.setIsDeleted(isDeleted);
                    }
                    break;
                default:
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            Integer patientKey = rs.getInt("patientKey");
                            String title = rs.getString("title");
                            String line1 = rs.getString("line1");
                            String line2 = rs.getString("line2");
                            String town = rs.getString("town");
                            String county = rs.getString("county");
                            String postcode = rs.getString("postcode");
                            String phone = rs.getString("phone");
                            String email = rs.getString("email");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            doctor = new Doctor(pid);
                            doctor.setTitle(title);
                            doctor.setPatient(new Patient(patientKey));
                            doctor.setLine1(line1);
                            doctor.setLine2(line2);
                            doctor.setTown(town);
                            doctor.setCounty(county);
                            doctor.setPostcode(postcode);
                            doctor.setPhone(phone);
                            doctor.setEmail(email);
                            doctor.setIsDeleted(isDeleted);
                            collection.add(doctor);
                        }
                        doctor.set(collection);
                    }
                    result = doctor;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(Doctor,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private Transmission get(Transmission transmission, ResultSet rs) throws StoreException {
        Transmission result = null;
        ArrayList<Transmission> collection = new ArrayList<>();
        try{
            switch (transmission.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        Integer patientKey = rs.getInt("patientKey");
                        Transmission.Mode mode = Transmission.Mode.valueOf(rs.getString("mode"));
                        Transmission.Status status = Transmission.Status.valueOf(rs.getString("status"));
                        LocalDate date = rs.getObject("dateTransmitted", LocalDate.class);
                        String content = rs.getString("content");
                        result = new Transmission(pid);
                        result.setPatient(new Patient(patientKey));
                        result.setMode(mode);
                        result.setStatus(status);
                        result.setDate(date);
                        result.setContent(content);
                    }
                    break;
                default://specifically Scope.ALL (all transmissions)
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            Integer patientKey = rs.getInt("patientKey");
                            Transmission.Mode mode = Transmission.Mode.valueOf(rs.getString("mode"));
                            Transmission.Status status = Transmission.Status.valueOf(rs.getString("status"));
                            LocalDate date = rs.getObject("dateTransmitted", LocalDate.class);
                            String content = rs.getString("content");
                            result = new Transmission(pid);
                            result.setPatient(new Patient(patientKey));
                            result.setMode(mode);
                            result.setStatus(status);
                            result.setDate(date);
                            result.setContent(content);
                            collection.add(result);
                        }
                        result.set(collection);
                    }
                    result = transmission;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(Treatment,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private PatientAppointmentData get(PatientAppointmentData pad, ResultSet rs) throws StoreException{
        int test = 0;
        PatientAppointmentData result = null;
        ArrayList<PatientAppointmentData> collection = new ArrayList<>();
        try{
            if (!rs.wasNull()){
                while (rs.next()){
                    Integer patientKey = rs.getInt("patientKey");
                    if (patientKey.equals(17443)){
                        test++;
                    }
                    LocalDate patient_GBTRecallSent = rs.getObject("lastGBTRecallSentDate", LocalDate.class);
                    LocalDate patient_nonGBTRecallSent = rs.getObject("lastNonGBTRecallSentDate", LocalDate.class);
                    Boolean patient_isRequestToSendPatientNonGBTRecallPending = rs.getBoolean("isRequestToSendPatientNonGBTRecallPending");
                    Boolean patient_isRequestToSendPatientGBTRecallPending = rs.getBoolean("isRequestToSendPatientGBTRecallPending");

                    String patient_forenames = rs.getString("patient_forenames");
                    String patient_surname = rs.getString("patient_surname");
                    String patient_title = rs.getString("patient_title");
                    String patient_phone1 = rs.getString("patient_phone1");
                    String patient_phone2 = rs.getString("patient_phone2");
                    String patient_email = rs.getString("patient_email");
                    LocalDate recall_date = rs.getObject("recall_date", LocalDate.class);
                    Integer recall_frequency = rs.getInt("recall_frequency");
                    LocalDate recall_date_GBT = rs.getObject("recall_date_GBT", LocalDate.class);
                    Integer recall_frequency_GBT = rs.getInt("recall_frequency_GBT");
                    Integer appointmentKey = rs.getInt("appointmentKey");
                    LocalDateTime last_appointment_date = rs.getObject("last_appointment_date", LocalDateTime.class);
                    String treatment = rs.getString("treatment");
                    //Boolean isCancelled = rs.getBoolean("isCancelled");
                    pad = new PatientAppointmentData();
                    Patient patient = new Patient(patientKey);
                    
                    patient.setLastGBTRecallSentDate(patient_GBTRecallSent);
                    patient.setLastNonGBTRecallSentDate(patient_nonGBTRecallSent);
                    patient.setIsRequestToSendPatientNonGBTRecallPending(patient_isRequestToSendPatientNonGBTRecallPending);
                    patient.setIsRequestToSendPatientGBTRecallPending(patient_isRequestToSendPatientGBTRecallPending);
                    
                    patient.getName().setForenames(patient_forenames);
                    patient.getName().setSurname(patient_surname);
                    patient.getName().setTitle(patient_title);
                    patient.setPhone1(patient_phone1);
                    patient.setPhone2(patient_phone2);
                    patient.setEmail(patient_email);
                    patient.getRecall().setDentalDate(recall_date);
                    patient.getRecall().setDentalFrequency(recall_frequency);
                    patient.getRecall().setGBTDate(recall_date_GBT);
                    patient.getRecall().setGBTFrequency(recall_frequency_GBT);
                    Appointment appointment = new Appointment(appointmentKey);
                    appointment.setStart(last_appointment_date);
                    appointment.setNotes(treatment);
                    pad.setAppointment(appointment);
                    //pad.setIsCancelled(isCancelled);
                    pad.setPatient(patient);
                    collection.add(pad);
                }
                pad.set(collection);
                result = pad;
            }
            return result;
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientAppointmentData,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private Medication get(Medication medication, ResultSet rs)throws StoreException{
        Medication result = null;
        ArrayList<Medication> collection = new ArrayList<>();
        try{
            switch (medication.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        Integer patientKey = rs.getInt("patientKey");
                        String description = rs.getString("description");
                        String notes = rs.getString("notes");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        medication = new Medication(pid);
                        medication.setPatient(new Patient(patientKey));
                        medication.setDescription(description);
                        medication.setNotes(notes);
                        medication.setIsDeleted(isDeleted);
                    }
                    break;
                default:
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            Integer patientKey = rs.getInt("patientKey");
                            String description = rs.getString("description");
                            String notes = rs.getString("notes");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            medication = new Medication(pid);
                            medication.setPatient(new Patient(patientKey));
                            medication.setDescription(description);
                            medication.setNotes(notes);
                            medication.setIsDeleted(isDeleted);
                            collection.add(medication);
                        }
                        medication.set(collection);
                    }
                    result = medication;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(Medication,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private Invoice get(Invoice invoice, ResultSet rs)throws StoreException{
        Invoice result = null;
        ArrayList<Invoice> collection = new ArrayList<>();
        try{
            switch (invoice.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        Integer patientKey = rs.getInt("patientKey");
                        String description = rs.getString("description");
                        Double amount = rs.getDouble("amount");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        invoice = new Invoice(pid);
                        invoice.setPatient(new Patient(patientKey));
                        invoice.setDescription(description);
                        invoice.setAmount(amount);
                        invoice.setIsDeleted(isDeleted);
                    }
                    break;
                default:
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            Integer patientKey = rs.getInt("patientKey");
                            String description = rs.getString("description");
                            Double amount = rs.getDouble("amount");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            invoice = new Invoice(pid);
                            invoice.setPatient(new Patient(patientKey));
                            invoice.setDescription(description);
                            invoice.setAmount(amount);
                            invoice.setIsDeleted(isDeleted);
                            collection.add(invoice);
                        }
                        invoice.set(collection);
                    }
                    result = invoice;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(Invoice,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private ToDo get(ToDo theToDo, ResultSet rs)throws StoreException{
        ToDo result = null;
        ToDo toDo = null;
        ArrayList<ToDo> collection = new ArrayList<>();
        try{
            switch (theToDo.getScope()){
                case SINGLE:
                    if (rs.next()){ //the scope expects a single row reurn (if any)
                        if(!rs.wasNull()){
                            int pid = rs.getInt("pid");
                            String username = rs.getString("userKey");
                            LocalDate toDoDate = rs.getObject("toDoDate", LocalDate.class);
                            String toDoDescription = rs.getString("toDoDescription");
                            Boolean isActioned = rs.getBoolean("isActioned");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            Boolean isCancelled = rs.getBoolean("isCancelled");
                            toDo = new ToDo();
                            toDo.setKey(pid);
                            User user = new User(username);
                            toDo.setUser(user);
                            toDo.setDate(toDoDate);
                            toDo.setDescription(toDoDescription);
                            toDo.setIsActioned(isActioned);
                            toDo.setIsCancelled(isCancelled);
                            toDo.setIsDeleted(isDeleted);
                            result = toDo;
                        }
                    }
                    result = toDo;
                    break;
                default: //the scope expects one or more rows returned (if any)
                    while(rs.next()){
                        toDo = new ToDo();
                        int pid = rs.getInt("pid");
                        String username = rs.getString("userKey");
                        LocalDate toDoDate = rs.getObject("toDoDate", LocalDate.class);
                        String toDoDescription = rs.getString("toDoDescription");
                        Boolean isActioned = rs.getBoolean("isActioned");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        Boolean isCancelled = rs.getBoolean("isCancelled");
                        toDo.setKey(pid);
                        User user = new User(username);
                        toDo.setUser(user);
                        toDo.setDate(toDoDate);
                        toDo.setDescription(toDoDescription);
                        toDo.setIsActioned(isActioned);
                        toDo.setIsCancelled(isCancelled);
                        toDo.setIsDeleted(isDeleted);
                        collection.add(toDo);
                    }
                    if (toDo!=null)toDo.set(collection);
                    result = toDo;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientToDo,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private TreatmentCost get(TreatmentCost treatmentCost, ResultSet rs)throws StoreException{
        TreatmentCost result = null;
        ArrayList<TreatmentCost> collection = new ArrayList<>();
        try{
            switch (treatmentCost.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        Integer invoiceKey = rs.getInt("invoiceKey");
                        String description = rs.getString("description");
                        Double amount = rs.getDouble("amount");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        treatmentCost = new TreatmentCost(pid);
                        treatmentCost.setInvoice(new Invoice(invoiceKey));
                        treatmentCost.setDescription(description);
                        treatmentCost.setAmount(amount);
                        treatmentCost.setIsDeleted(isDeleted);
                    }
                    break;
                default:
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                        Integer invoiceKey = rs.getInt("invoiceKey");
                        String description = rs.getString("description");
                        Double amount = rs.getDouble("amount");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        treatmentCost = new TreatmentCost(pid);
                        treatmentCost.setInvoice(new Invoice(invoiceKey));
                        treatmentCost.setDescription(description);
                        treatmentCost.setAmount(amount);
                        treatmentCost.setIsDeleted(isDeleted);
                            collection.add(treatmentCost);
                        }
                        treatmentCost.set(collection);
                    }
                    result = treatmentCost;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(TreatmentCost,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private ClinicalNote get(ClinicalNote clinicNote, ResultSet rs)throws StoreException{
        ClinicalNote result = null;
        ArrayList<ClinicalNote> collection = new ArrayList<>();
        try{
            switch (clinicNote.getScope()){
                case SINGLE:
                    //if (!rs.wasNull()){
                    if (rs.next()){
                        rs.beforeFirst();
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        String notes = rs.getString("notes");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        clinicNote = new ClinicalNote(pid);
                        clinicNote.setNotes(notes);
                        clinicNote.setIsDeleted(isDeleted);
                    }
                    break;
                default://specifically Scope.FOR_PATIENT (clinic note for a patient)
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            String notes = rs.getString("notes");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            clinicNote = new ClinicalNote(pid);
                            clinicNote.setNotes(notes);
                            clinicNote.setIsDeleted(isDeleted);
                            collection.add(clinicNote);
                        }
                        clinicNote.set(collection);
                    }
                    result = clinicNote;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(ClinicNote,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private User get(User theUser, ResultSet rs)throws StoreException{
        User result = null;
        ArrayList<User> collection = new ArrayList<>();
        try{
            switch(theUser.getScope()){
                case WITH_NAME:
                    if(rs.next()){
                        rs.isBeforeFirst();
                        User _user = new User(rs.getString("username"));
                        _user.setIsDeleted(rs.getBoolean("isDeleted")); 
                        result = _user;
                    }
                    break;
                case WITH_CREDENTIAL:{
                    if(rs.next()){
                        User _user = new User(rs.getString("username"));
                        SystemDefinition.userP = rs.getString("password");
                        SystemDefinition.userS = rs.getString("salt");
                        _user.setIsDeleted(rs.getBoolean("isDeleted"));
                        /**
                         * following required else unhashed password lost
                         */
                        _user.setPassword(theUser.getPassword());
                        result = _user;
                    }
                    break;
                }
            }
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(User,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
        return result;
    }
    
    private Treatment get(Treatment treatment, ResultSet rs)throws StoreException{
        Treatment result = null;
        ArrayList<Treatment> collection = new ArrayList<>();
        try{
            switch (treatment.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        String description = rs.getString("description");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        treatment = new Treatment(pid);
                        treatment.setDescription(description);
                        treatment.setIsDeleted(isDeleted);
                    }
                    break;
                default://specifically Scope.ALL (all treatments)
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            String description = rs.getString("description");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            treatment = new Treatment(pid);
                            treatment.setDescription(description);
                            treatment.setIsDeleted(isDeleted);
                            collection.add(treatment);
                        }
                        treatment.set(collection);
                    }
                    result = treatment;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(Treatment,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private Question get(Question question, ResultSet rs)throws StoreException{
        Question result = null;
        ArrayList<Question> collection = new ArrayList<>();
        try{
            switch (question.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer pid = rs.getInt("pid");
                        String category = rs.getString("category");
                        String description = rs.getString("description");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        Integer order = rs.getInt("sortorder");
                        question = new Question(pid);
                        question.setCategory(category);
                        question.setDescription(description);
                        question.setIsDeleted(isDeleted);
                        question.setOrder(order);
                    }
                    break;
                default://specifically Scope.ALL (all questions)
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            String category = rs.getString("category");
                            String description = rs.getString("description");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            Integer order = rs.getInt("sortorder");
                            question = new Question(pid);
                            question.setCategory(category);
                            question.setDescription(description);
                            question.setIsDeleted(isDeleted);
                            question.setOrder(order);
                            collection.add(question);
                        }
                        question.set(collection);
                    }
                    result = question;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(Question,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private AppointmentTreatment get(AppointmentTreatment appointmentTreatment, ResultSet rs)throws StoreException{
        AppointmentTreatment result = null;
        ArrayList<AppointmentTreatment> collection = new ArrayList<>();
        try{
            switch (appointmentTreatment.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        Integer appointmentKey = rs.getInt("appointmentKey");
                        Integer treatmentKey = rs.getInt("treatmentKey");
                        String comment = rs.getString("comment");
                        Appointment appointment = new Appointment(appointmentKey);
                        Treatment treatment = new Treatment(treatmentKey);
                        result = new AppointmentTreatment(appointment,treatment);
                        result.setComment(comment);

                    }
                    break;
                default://specifically Scope.FOR_APPOINMENT or FOR_TREATMENT 
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer appointmentKey = rs.getInt("appointmentKey");
                            Integer treatmentKey = rs.getInt("treatmentKey");
                            String comment = rs.getString("comment");
                            AppointmentTreatment theAppointmentTreatment = 
                                    new AppointmentTreatment(
                                            new Appointment(appointmentKey),
                                            new Treatment(treatmentKey));
                            theAppointmentTreatment.setComment(comment);
                            collection.add(theAppointmentTreatment);
                        }
                        appointmentTreatment.set(collection);
                    }
                    result = appointmentTreatment;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(AppointmentTreatment,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private PatientQuestion get(PatientQuestion patientQuestion, ResultSet rs)throws StoreException{
        PatientQuestion result = null;
        ArrayList<PatientQuestion> collection = new ArrayList<>();
        try{
            switch (patientQuestion.getScope()){
                case SINGLE:
                    if (rs.next()){
                        Integer patientKey = rs.getInt("patientKey");
                        Integer questionKey = rs.getInt("questionKey");
                        String answer = rs.getString("answer");
                        Patient patient = new Patient(patientKey);
                        Question question = new Question(questionKey);
                        result = new PatientQuestion(patient,question);
                        result.setAnswer(answer);

                    }
                    break;
                default://specifically Scope.FOR_APPOINMENT or FOR_TREATMENT 
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer patientKey = rs.getInt("patientKey");
                            Integer questionKey = rs.getInt("questionKey");
                            String answer = rs.getString("answer");
                            PatientQuestion thePatientQuestion = 
                                    new PatientQuestion(
                                            new Patient(patientKey),
                                            new Question(questionKey));
                            thePatientQuestion.setAnswer(answer);
                            collection.add(thePatientQuestion);
                        }
                        patientQuestion.set(collection);
                    }
                    result = patientQuestion;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientQuestion,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private PatientPrimaryCondition get(PatientPrimaryCondition patientPrimaryCondition, ResultSet rs)throws StoreException{
        PatientPrimaryCondition result = null;
        ArrayList<PatientCondition> collection = new ArrayList<>();
        try{
            switch (patientPrimaryCondition.getScope()){
                case SINGLE:
                    if (rs.next()){
                        Integer patientKey = rs.getInt("patientKey");
                        Integer primaryConditionKey = rs.getInt("primaryConditionKey");
                        String comment = rs.getString("comment");
                        Patient patient = new Patient(patientKey);
                        PrimaryCondition primaryCondition = new PrimaryCondition(primaryConditionKey);
                        result = new PatientPrimaryCondition(patient,primaryCondition);
                        result.setComment(comment);
                    }
                    break;
                default://specifically Scope.FOR_PATIENT or FOR_PRIMARY_CONDITION
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer patientKey = rs.getInt("patientKey");
                            Integer primaryConditionKey = rs.getInt("primaryConditionKey");
                            String comment = rs.getString("comment");
                            PatientPrimaryCondition thePatientPrimaryCondition = 
                                    new PatientPrimaryCondition(
                                            new Patient(patientKey),
                                            new PrimaryCondition(primaryConditionKey));
                            thePatientPrimaryCondition.setComment(comment);
                            collection.add(thePatientPrimaryCondition);
                        }
                        patientPrimaryCondition.set(collection);
                        result = patientPrimaryCondition;
                    }
                    break;
            }

        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientPrimaryCondition,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
        return result;
    }
    
    private PatientSecondaryCondition get(PatientSecondaryCondition patientSecondaryCondition, ResultSet rs)throws StoreException{
        PatientSecondaryCondition result = null;
        Integer patientKey = null;
        Integer secondaryConditionKey = null;
        String comment = null;
        ArrayList<PatientCondition> collection = new ArrayList<>();
        try{
            switch (patientSecondaryCondition.getScope()){
                case SINGLE:{
                    if(rs.next()){
                        patientKey = rs.getInt("patientKey");
                        secondaryConditionKey = rs.getInt("secondaryConditionKey");
                        comment = rs.getString("comment");
                        Patient patient = new Patient(patientKey);
                        SecondaryCondition secondaryCondition = new SecondaryCondition(secondaryConditionKey);
                        result = new PatientSecondaryCondition(patient,secondaryCondition);
                        result.setComment(comment);
                    }
                    break;
                }
                default:{//specifically Scope.FOR_PATIENT or FOR_SECONDARY_CONDITION
                    while (rs.next()){
                        patientKey = rs.getInt("patientKey");
                        secondaryConditionKey = rs.getInt("secondaryConditionKey");
                        comment = rs.getString("comment");
                        PatientSecondaryCondition thePatientSecondaryCondition = 
                                new PatientSecondaryCondition(
                                        new Patient(patientKey),
                                        new SecondaryCondition(secondaryConditionKey));
                        thePatientSecondaryCondition.setComment(comment);
                        collection.add(thePatientSecondaryCondition);
                    }
                    patientSecondaryCondition.set(collection);
                    result = patientSecondaryCondition;
                    break;
                }
            } 
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientSecondaryCondition,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
        return result;
    }
    
    private Entity doReadPatientNotificationsForPatient(String sql, Entity entity)throws StoreException{
        //07/08/2022
        Notification notification = null;
        Patient patient;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                notification = (Notification)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    patient = (Patient)notification.getPatient();
                    preparedStatement.setLong(1, patient.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    return get(notification, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientNotificationsForPatient()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity definition, expecting a patient object, in Repository::doReadPatientNotificationForPatient()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notification undefined in Access::doReadPatientNotificationForPatient()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadDoctorForPatient(String sql, Entity entity)throws StoreException{
        Doctor doctor = null;
        if (entity != null) {
            if (entity.getIsDoctor()) {
                doctor = (Doctor) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, doctor.getPatient().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    doctor.setScope(Entity.Scope.FOR_PATIENT);
                    return get(doctor, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadDoctorWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNoteWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientNoteWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadMedicationForPatient(String sql, Entity entity)throws StoreException{
        Medication medication = null;
        if (entity != null) {
            if (entity.getIsMedication()) {
                medication = (Medication) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, medication.getPatient().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    medication.setScope(Entity.Scope.FOR_PATIENT);
                    return get(medication, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadMedicationWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNoteWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientNoteWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadInvoiceForPatient(String sql, Entity entity)throws StoreException{
        Invoice invoice = null;
        if (entity != null) {
            if (entity.getIsInvoice()) {
                invoice = (Invoice) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, invoice.getPatient().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    invoice.setScope(Entity.Scope.FOR_PATIENT);
                    return get(invoice, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadInvoiceWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNoteWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientNoteWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadTreatmentCostForInvoice(String sql, Entity entity)throws StoreException{
        TreatmentCost treatmentCost = null;
        if (entity != null) {
            if (entity.getIsTreatmentCost()) {
                treatmentCost = (TreatmentCost) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, treatmentCost.getInvoice().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    treatmentCost.setScope(Entity.Scope.FOR_INVOICE);
                    return get(treatmentCost, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadTreatmentCostWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNoteWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientNoteWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadClinicalNoteForAppointment(String sql, Entity entity)throws StoreException{
        ClinicalNote clinicNote = null;
        if (entity != null) {
            if (entity.getIsClinicNote()) {
                clinicNote = (ClinicalNote) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, clinicNote.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    clinicNote.setScope(Entity.Scope.FOR_APPOINTMENT);
                    return get(clinicNote, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadClinicNoteWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNoteWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientNoteWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }

    private Entity doReadUser(String sql, Entity entity)throws StoreException{
        if (entity != null){
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, ((User)entity).getUsername());
                    ResultSet rs = preparedStatement.executeQuery();
                    return getDataRead(entity, rs);
            }catch(SQLException ex){
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadUserWithName()" ,
                            StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }else {
            String msg = "StoreException -> patient note undefined in doReadSingle( '" + getEntityType(entity) + "' )";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadSingle(String sql, Entity entity)throws StoreException{
        if (entity != null){
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, entity.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    entity.setScope(Entity.Scope.SINGLE);
                    return getDataRead(entity, rs);
            }catch(SQLException ex){
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadSingle( '" + getEntityType(entity) + "' )",
                            StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }else {
            String msg = "StoreException -> patient note undefined in doReadSingle( '" + getEntityType(entity) + "' )";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadQuestionForCategory(String sql, Entity entity)throws StoreException{
        Question question = null;
        if (entity != null) {
            if (entity.getIsQuestion()) {
                question = (Question) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, question.getCategory().toString());
                    ResultSet rs = preparedStatement.executeQuery();
                    question.setScope(Entity.Scope.FOR_CATEGORY);
                    return get(question, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadQuestionForCategory()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNoteWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientNoteWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadPatientQuestionWithKey(String sql, Entity entity)throws StoreException{
        PatientQuestion patientQuestion = null;
        if (entity != null) {
            if (entity.getIsPatientQuestion()) {
                patientQuestion = (PatientQuestion) entity;
                
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    switch(patientQuestion.getScope()){
                        case SINGLE:
                            preparedStatement.setLong(1, patientQuestion.getPatient().getKey());
                            preparedStatement.setLong(2, patientQuestion.getQuestion().getKey());
                        case FOR_PATIENT:
                            preparedStatement.setLong(1, patientQuestion.getPatient().getKey());
                            break;
                        case FOR_QUESTION:
                            preparedStatement.setLong(1, patientQuestion.getQuestion().getKey());
                            break;
                    }
                    ResultSet rs = preparedStatement.executeQuery();
                    //patientQuestion.setScope(Entity.Scope.SINGLE);
                    return get(patientQuestion, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientQuestionWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientQuestionWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientQuestionWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadAppointmentTreatmentWithKey(String sql, Entity entity)throws StoreException{
        AppointmentTreatment appointmentTreatment = null;
        if (entity != null) {
            if (entity.getIsAppointmentTreatment()) {
                appointmentTreatment = (AppointmentTreatment) entity;
                
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    switch(appointmentTreatment.getScope()){
                        case SINGLE:
                            preparedStatement.setLong(1, appointmentTreatment.getAppointment().getKey());
                            preparedStatement.setLong(2, appointmentTreatment.getTreatment().getKey());
                        case FOR_APPOINTMENT:
                            preparedStatement.setLong(1, appointmentTreatment.getAppointment().getKey());
                            break;
                        case FOR_TREATMENT:
                            preparedStatement.setLong(1, appointmentTreatment.getTreatment().getKey());
                            break;
                    }
                    ResultSet rs = preparedStatement.executeQuery();
                    //ppointmentTreatment.setScope(Entity.Scope.SINGLE);
                    return get(appointmentTreatment, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadAppointmentTreatmentWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadAppointmentTreatmentWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> ppointment note undefined in doReadAppointmentTreatmentWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadPatientPrimaryConditionWithKey(String sql, Entity entity)throws StoreException{
        PatientPrimaryCondition patientPrimaryCondition = null;
        if (entity != null) {
            if (entity.getIsPatientPrimaryCondition()) {
                patientPrimaryCondition = (PatientPrimaryCondition) entity;
                
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    switch(patientPrimaryCondition.getScope()){
                        case SINGLE:
                            preparedStatement.setLong(1, patientPrimaryCondition.getPatient().getKey());
                            preparedStatement.setLong(2, patientPrimaryCondition.getCondition().getKey());
                        case FOR_PATIENT:
                            preparedStatement.setLong(1, patientPrimaryCondition.getPatient().getKey());
                            break;
                        case FOR_PRIMARY_CONDITION:
                            preparedStatement.setLong(1, patientPrimaryCondition.getCondition().getKey());
                            break;
                    }
                    ResultSet rs = preparedStatement.executeQuery();
                    //patientPrimaryCondition.setScope(Entity.Scope.SINGLE);
                    return get(patientPrimaryCondition, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientPrimaryConditionWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientPrimaryConditionWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientPrimaryConditionWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadPatientSecondaryConditionWithKey(String sql, Entity entity)throws StoreException{
        PatientSecondaryCondition patientSecondaryCondition = null;
        if (entity != null) {
            if (entity.getIsPatientSecondaryCondition()) {
                patientSecondaryCondition = (PatientSecondaryCondition) entity;
                
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    switch(patientSecondaryCondition.getScope()){ 
                        case SINGLE:
                            preparedStatement.setLong(1, patientSecondaryCondition.getPatient().getKey());
                            preparedStatement.setLong(2, patientSecondaryCondition.getCondition().getKey());
                        case FOR_PATIENT:
                            preparedStatement.setLong(1, patientSecondaryCondition.getPatient().getKey());
                            break;
                        case FOR_SECONDARY_CONDITION:
                            preparedStatement.setLong(1, patientSecondaryCondition.getCondition().getKey());
                            break;
                    }
                    ResultSet rs = preparedStatement.executeQuery();
                    //patientSecondaryCondition.setScope(Entity.Scope.SINGLE);
                    return get(patientSecondaryCondition, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientSecondaryConditionWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientSecondaryConditionWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPatientSecondaryConditionWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadSecondaryConditionForPrimaryCondition(String sql, Entity entity)throws StoreException{
        SecondaryCondition sc = null;
        if (entity != null) {
            if (entity.getIsSecondaryCondition()) {
                sc = (SecondaryCondition) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, sc.getPrimaryCondition().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
                    return get(sc, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadSecondaryConditionForPatient()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadSecondaryConditionForPatient()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadSecondaryConditionForPatient()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doInsertPatientNotification(String sql, Entity entity) throws StoreException{
        //  delegate;
        Notification notification = null;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                notification = (Notification) entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, ((Patient)notification.getPatient()).getKey());
                    preparedStatement.setDate(2, java.sql.Date.valueOf(notification.getNotificationDate()));
                    preparedStatement.setString(3, notification.getNotificationText());
                    preparedStatement.setBoolean(4, notification.getIsActioned());
                    preparedStatement.setLong(5, notification.getKey());
                    preparedStatement.execute();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPatientNotification()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> patient notification defined invalidly in doInsertPatientNotification()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notificaion undefined in doInsertPatientNotification()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity getDataRead(Entity entity, ResultSet rs)throws StoreException{
        Entity result = null;
        switch(getEntityType(entity)){
            case APPOINTMENT ->{
                result = get((Appointment)entity,rs);
                break;
            }
            case APPOINTMENT_TREATMENT ->{
                result = get((AppointmentTreatment)entity,rs);
                break;
            }
            case CLINICAL_NOTE ->{
                result = get((ClinicalNote)entity,rs);
                break;
            }
            case DOCTOR ->{
                result = get((Doctor)entity,rs);
                break;
            }
            case INVOICE ->{
                result = get((Invoice)entity,rs);
                break;
            }
            case MEDICATION ->{
                result = get((Medication)entity,rs);
                break;
            }
            case PATIENT ->{
                result = get((Patient)entity,rs);
                break;
            }
            case PATIENT_APPOINTMENT_DATA ->{
                result = get((PatientAppointmentData)entity,rs);
                break; 
            }
            case PATIENT_NOTE ->{
                //get((PatientNote)entity,rs);
               break; 
            }
            case PATIENT_NOTIFICATION ->{
                result = get((Notification)entity,rs);
                break;
            }
            case PATIENT_PRIMARY_CONDITION ->{
                result = get((PatientPrimaryCondition)entity,rs);
                break;
            }
            case PATIENT_QUESTION ->{
                result = get((PatientQuestion)entity,rs);
                break;
            }
            case PATIENT_SECONDARY_CONDITION ->{
                result = get((PatientSecondaryCondition)entity,rs);
                break;
            }
            case PRIMARY_CONDITION ->{
                result = get((PrimaryCondition)entity,rs);
                break;
            }
            case QUESTION ->{
                result = get((Question)entity,rs);
                break;
            }
            case SECONDARY_CONDITION ->{
                result = get((SecondaryCondition)entity,rs);
                break; 
            }
            case SURGERY_DAYS_ASSIGNMENT ->{
                result = get((SurgeryDaysAssignment)entity,rs);
                break;
            }
            case TO_DO ->{
                result = get((ToDo)entity,rs);
                break;
            }
            case TRANSMISSION ->{
                result = get((Transmission)entity,rs);
                break;
            }
            case TREATMENT ->{
                result = get((Treatment)entity,rs);
                break;
            }
            case TREATMENT_COST ->{
                result = get((TreatmentCost)entity,rs);
                break;
            }
            case USER ->{
                result = get((User)entity,rs);
                break;
            }
        }
        return result;
    }
    
    private EntityType getEntityType(Entity entity){
        EntityType result = null;
        if (entity.getIsAppointment()) result = EntityType.APPOINTMENT;
        else if(entity.getIsAppointmentTreatment()) result = EntityType.APPOINTMENT_TREATMENT;
        else if(entity.getIsClinicNote()) result = EntityType.CLINICAL_NOTE;
        else if(entity.getIsDoctor()) result = EntityType.DOCTOR;
        else if(entity.getIsInvoice()) result = EntityType.INVOICE;
        else if(entity.getIsMedication()) result = EntityType.MEDICATION;
        else if(entity.getIsPatient()) result = EntityType.PATIENT;
        else if(entity.getIsPatientAppointmentData()) result = EntityType.PATIENT_APPOINTMENT_DATA;
        else if(entity.getIsPatientNote()) result = EntityType.PATIENT_NOTE;
        else if(entity.getIsPatientNotification()) result = EntityType.PATIENT_NOTIFICATION;
        else if(entity.getIsPatientPrimaryCondition()) result = EntityType.PATIENT_PRIMARY_CONDITION;
        else if(entity.getIsPatientQuestion()) result = EntityType.PATIENT_QUESTION;
        else if(entity.getIsPatientSecondaryCondition()) result = EntityType.PATIENT_SECONDARY_CONDITION;
        else if(entity.getIsPrimaryCondition()) result = EntityType.PRIMARY_CONDITION;
        else if(entity.getIsQuestion()) result = EntityType.QUESTION;
        else if(entity.getIsSecondaryCondition()) result = EntityType.SECONDARY_CONDITION;
        else if(entity.getIsSurgeryDaysAssignment()) result = EntityType.SURGERY_DAYS_ASSIGNMENT;
        else if(entity.getIsToDo()) result = EntityType.TO_DO;
        else if(entity.getIsTreatment()) result = EntityType.TREATMENT;
        else if(entity.getIsTreatmentCost()) result = EntityType.TREATMENT_COST;
        else if(entity.getIsUser()) result = EntityType.USER;
        return result;
                
    }
    
    private void doDeleteSingle(String sql, Entity entity)throws StoreException{
        if (entity != null){
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, entity.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised when entity type being deleted is " + getEntityType(entity).toString(),
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteSingle(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteCondition(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsPrimaryCondition() || entity.getIsSecondaryCondition()){
                Condition condition = (Condition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, condition.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteCondition(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteCondition(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteCondition(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeletePatientPrimaryCondition(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsPatientPrimaryCondition()){
                PatientPrimaryCondition patientPrimaryCondition = (PatientPrimaryCondition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, patientPrimaryCondition.getPatient().getKey());
                    preparedStatement.setLong(2, patientPrimaryCondition.getCondition().getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeletePatientPrimaryCondition(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeletePatientPrimaryCondition(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeletePatientPrimaryCondition(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeletePatientSecondaryCondition(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsPatientSecondaryCondition()){
                PatientSecondaryCondition patientSecondaryCondition = (PatientSecondaryCondition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, patientSecondaryCondition.getPatient().getKey());
                    preparedStatement.setLong(2, patientSecondaryCondition.getCondition().getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeletePatientSecondaryCondition(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeletePatientSecondaryCondition(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeletePatientSecondaryCondition(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteAppointmentTreatment(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsAppointmentTreatment()){
                AppointmentTreatment appointmentTreatment = (AppointmentTreatment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, appointmentTreatment.getAppointment().getKey());
                    preparedStatement.setLong(2, appointmentTreatment.getTreatment().getKey());
                    preparedStatement.execute();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteAppointmentTreatment(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteAppointmentTreatment(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteAppointmentTreatment(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeletePatientQuestion(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsPatientQuestion()){
                PatientQuestion patientQuestion = (PatientQuestion)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, patientQuestion.getPatient().getKey());
                    preparedStatement.setLong(2, patientQuestion.getQuestion().getKey());
                    preparedStatement.execute();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeletePatientQuestion(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeletePatientQuestion(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeletePatientQuestion(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteNotification(String sql, Entity entity)throws StoreException{
        //NotificationDelegate delegate;
        Notification notification = null;
        if (entity != null){
            if (entity.getIsPatientNotification()) {
                notification = (Notification) entity;
                //delegate.setKey(1);
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setBoolean(1, true);
                    preparedStatement.setLong(2, notification.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeletePatientNotification(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeletePatientNotification(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity undefined in doUpdatePatientNotification(sql, entity)";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    /*private void doUpdatePatientNote(String sql, Entity entity) throws StoreException{
        PatientNoteDelegate delegate;
        if (entity != null) {
            if (entity.getIsPatientNote()){
                    delegate = (PatientNoteDelegate)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(delegate.getDatestamp()));
                    preparedStatement.setLong(2, delegate.getPatientKey());
                    preparedStatement.setString(3, delegate.getNote());
                    preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(delegate.getLastUpdated()));
                    preparedStatement.setLong(5, delegate.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePatientNote()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdatePatientNote()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdatePatientNote()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }*/
    
    private void doUpdateDoctor(String sql, Entity entity) throws StoreException{
        Doctor doctor;
        if (entity != null) {
            if (entity.getIsDoctor()){
                    doctor = (Doctor)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, doctor.getPatient().getKey());
                    preparedStatement.setString(2, doctor.getTitle());
                    preparedStatement.setString(3, doctor.getLine1());
                    preparedStatement.setString(4, doctor.getLine2());
                    preparedStatement.setString(5, doctor.getTown());
                    preparedStatement.setString(6, doctor.getCounty());
                    preparedStatement.setString(7, doctor.getPostcode());
                    preparedStatement.setString(8, doctor.getPhone());
                    preparedStatement.setString(9, doctor.getEmail());
                    preparedStatement.setBoolean(10, doctor.getIsDeleted());
                    preparedStatement.setLong(11, doctor.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateDoctor()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateDoctor()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateDoctor()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }

    private void doUpdatePrimaryCondition(String sql, Entity entity) throws StoreException{
        PrimaryCondition pc;
        if (entity != null) {
            if (entity.getIsPrimaryCondition()){
                    pc = (PrimaryCondition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                   // preparedStatement.setLong(1, pc.getPatient().getKey());
                    preparedStatement.setString(1, pc.getDescription());
                   /* preparedStatement.setBoolean(3, pc.getState());
                    preparedStatement.setString(4, pc.getNotes());*/
                    preparedStatement.setBoolean(2, pc.getIsDeleted());
                    preparedStatement.setLong(3, pc.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePrimaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdatePrimaryCondition()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdatePrimaryCondition()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateSecondaryCondition(String sql, Entity entity) throws StoreException{
        SecondaryCondition sc;
        if (entity != null) {
            if (entity.getIsSecondaryCondition()){
                    sc = (SecondaryCondition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, sc.getPrimaryCondition().getKey());
                    preparedStatement.setString(2, sc.getDescription());
                   /* preparedStatement.setBoolean(3, sc.getState());
                    preparedStatement.setString(4, sc.getNotes());*/
                    preparedStatement.setBoolean(3, sc.getIsDeleted());
                    preparedStatement.setLong(4, sc.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateSecondaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateSecondaryCondition()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateSecondaryCondition()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateMedication(String sql, Entity entity) throws StoreException{
        Medication medication;
        if (entity != null) {
            if (entity.getIsMedication()){
                    medication = (Medication)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, medication.getPatient().getKey());
                    preparedStatement.setString(2, medication.getDescription());
                    preparedStatement.setString(3, medication.getNotes());
                    preparedStatement.setBoolean(4, medication.getIsDeleted());
                    preparedStatement.setLong(5, medication.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateMedication()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateMedication()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateMedication()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateInvoice(String sql, Entity entity) throws StoreException{
        Invoice invoice;
        if (entity != null) {
            if (entity.getIsInvoice()){
                    invoice = (Invoice)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setDouble(1, invoice.getAmount());
                    preparedStatement.setString(2, invoice.getDescription());
                    preparedStatement.setBoolean(3, invoice.getIsDeleted());
                    preparedStatement.setLong(4, invoice.getPatient().getKey());
                    preparedStatement.setLong(5, invoice.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateInvoice()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateInvoice()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateInvoice()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateUser(String sql,Entity entity)throws StoreException{
        User _user = null;
        if (entity != null) {
            if (entity.getIsUser()){
                _user = (User)entity;
            }
            try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                
                preparedStatement.setString(1, SystemDefinition.userP);
                preparedStatement.setString(2, SystemDefinition.userS);
                preparedStatement.setString(3, _user.getUsername());
                preparedStatement.executeUpdate();   
            }catch(SQLException ex){
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                        + "StoreException message -> exception raised in Repository::doUpdateTreatmentCost()",
                        StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }else{
            String msg = "StoreException -> entity undefined in doUpdateUser()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateTreatmentCost(String sql, Entity entity) throws StoreException{
        TreatmentCost treatmentCost;
        if (entity != null) {
            if (entity.getIsTreatmentCost()){
                    treatmentCost = (TreatmentCost)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setDouble(1, treatmentCost.getAmount());
                    preparedStatement.setString(2, treatmentCost.getDescription());
                    preparedStatement.setLong(3, treatmentCost.getInvoice().getKey());
                    preparedStatement.setBoolean(4, treatmentCost.getIsDeleted());
                    preparedStatement.setLong(5, treatmentCost.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateTreatmentCost()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateTreatmentCost()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateTreatmentCost()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    
    private void doUpdateClinicalNote(String sql, Entity entity) throws StoreException{
        ClinicalNote clinicNote;
        if (entity != null) {
            if (entity.getIsClinicNote()){
                    clinicNote = (ClinicalNote)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, clinicNote.getNotes());
                    preparedStatement.setLong(2, clinicNote.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateClinicNote()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateClinicNote()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateClinicNote()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateAppointmentTreatment(String sql, Entity entity) throws StoreException{
        AppointmentTreatment appointmentTreatment;
        if (entity != null) {
            if (entity.getIsAppointmentTreatment()){
                    appointmentTreatment = (AppointmentTreatment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, appointmentTreatment.getComment());
                    //preparedStatement.setBoolean(2, appointmentTreatment.getIsDeleted());
                    preparedStatement.setLong(2, appointmentTreatment.getAppointment().getKey());
                    preparedStatement.setLong(3, appointmentTreatment.getTreatment().getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateAppointmentTreatment()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateAppointmentTreatment()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateAppointmentTreatment()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdatePatientQuestion(String sql, Entity entity) throws StoreException{
        PatientQuestion patientQuestion;
        if (entity != null) {
            if (entity.getIsPatientQuestion()){
                    patientQuestion = (PatientQuestion)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, patientQuestion.getAnswer());
                    //preparedStatement.setBoolean(2, patientQuestion.getIsDeleted());
                    preparedStatement.setLong(2, patientQuestion.getPatient().getKey());
                    preparedStatement.setLong(3, patientQuestion.getQuestion().getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePatientQuestion()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdatePatientQuestion()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdatePatientQuestion()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdatePatientSecondaryCondition(String sql, Entity entity) throws StoreException{
        PatientSecondaryCondition patientSecondaryCondition;
        if (entity != null) {
            if (entity.getIsPatientSecondaryCondition()){
                    patientSecondaryCondition = (PatientSecondaryCondition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, patientSecondaryCondition.getComment());
                    preparedStatement.setLong(2, patientSecondaryCondition.getPatient().getKey());
                    preparedStatement.setLong(3, patientSecondaryCondition.getCondition().getKey());
                    preparedStatement.executeUpdate();
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePatientSecondaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdatePatientSecondaryCondition()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdatePatientSecondaryCondition()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    } 
    
    private void doUpdatePatientPrimaryCondition(String sql, Entity entity) throws StoreException{
        PatientPrimaryCondition patientPrimaryCondition;
        if (entity != null) {
            if (entity.getIsPatientPrimaryCondition()){
                    patientPrimaryCondition = (PatientPrimaryCondition)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, patientPrimaryCondition.getComment());
                    preparedStatement.setLong(2, patientPrimaryCondition.getPatient().getKey());
                    preparedStatement.setLong(3, patientPrimaryCondition.getCondition().getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePatientPrimaryCondition()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdatePatientPrimaryCondition()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdatePatientPrimaryCondition()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateTreatment(String sql, Entity entity) throws StoreException{
        Treatment treatment;
        if (entity != null) {
            if (entity.getIsTreatment()){
                    treatment = (Treatment)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, treatment.getDescription());
                    preparedStatement.setBoolean(2, treatment.getIsDeleted());
                    preparedStatement.setLong(3, treatment.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateTreatment()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateTreatment()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateTreatment()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateQuestion(String sql, Entity entity) throws StoreException{
        Question question;
        if (entity != null) {
            if (entity.getIsQuestion()){
                    question = (Question)entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setString(1, question.getCategory().toString());
                    preparedStatement.setString(2, question.getDescription());
                    preparedStatement.setBoolean(3, question.getIsDeleted());
                    preparedStatement.setLong(4, question.getOrder());
                    preparedStatement.setInt(5, question.getKey());
                    preparedStatement.executeUpdate();   
                }catch(SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateQuestion()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = "StoreException -> patient note defined invalidly in doUpdateQuestion()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doUpdateQuestion()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdatePatientNotification(String sql, Entity entity) throws StoreException{
        Patient patient;
        Notification notification;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                notification = (Notification) entity;
                patient = (Patient)notification.getPatient();
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setLong(1, patient.getKey());
                    preparedStatement.setDate(2, java.sql.Date.valueOf(notification.getNotificationDate()));
                    preparedStatement.setString(3, notification.getNotificationText());
                    preparedStatement.setBoolean(4, notification.getIsActioned());
                    preparedStatement.setLong(5, notification.getKey());
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdatePatientNotification()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> patient notification defined invalidly in doUpdatePatientNotification()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notificaion undefined in doUpdatePatientNotification()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doCreatePatientNoteTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientNoteTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateSecondaryConditionTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateSecondaryConditionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePrimaryConditionTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePrimaryConditionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateClinicalNoteTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateClinicNoteTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateTreatmentTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateTreatmentTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    
    
    private void doCreateQuestionTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateQuestionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePatientQuestionTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientQuestionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateAppointmentTreatmentTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateAppointmentTreatmentTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }

    
    private void doCreatePatientPrimaryConditionTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientPrimaryConditionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePatientSecondaryConditionTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientSecondaryConditionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateMedicationTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateMedicationTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateDoctorTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateDoctorTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePatientNotificationTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientNotificationTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private SurgeryDaysAssignment get(SurgeryDaysAssignment surgeryDaysAssignment, ResultSet rs) throws StoreException {
        String day;
        try {
            if (!rs.wasNull()) {
                while (rs.next()) {
                    day = rs.getString("Day");
                    switch (day) {
                        case "Monday":
                            surgeryDaysAssignment.get().put(DayOfWeek.MONDAY, rs.getBoolean("isSurgery"));
                            break;
                        case "Tuesday":
                            surgeryDaysAssignment.get().put(DayOfWeek.TUESDAY, rs.getBoolean("isSurgery"));
                            break;
                        case "Wednesday":
                            surgeryDaysAssignment.get().put(DayOfWeek.WEDNESDAY, rs.getBoolean("isSurgery"));
                            break;
                        case "Thursday":
                            surgeryDaysAssignment.get().put(DayOfWeek.THURSDAY, rs.getBoolean("isSurgery"));
                            break;
                        case "Friday":
                            surgeryDaysAssignment.get().put(DayOfWeek.FRIDAY, rs.getBoolean("isSurgery"));
                            break;
                        case "Saturday":
                            surgeryDaysAssignment.get().put(DayOfWeek.SATURDAY, rs.getBoolean("isSurgery"));
                            break;
                        case "Sunday":
                            surgeryDaysAssignment.get().put(DayOfWeek.SUNDAY, rs.getBoolean("isSurgery"));
                            break;
                    }
                }

            }
            return surgeryDaysAssignment;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Access::get(SurgeryDaysAssignment,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateSurgeryDaysAssignmentTable(String sql)throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {

            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during Repository::doCreateSurgeryDaysAssignmentTable()",
                    StoreException.ExceptionType.SQL_EXCEPTION);

        }
    }
    
    private void doDropSurgeryDaysAssignmentTable(String sql) throws StoreException{
        try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.execute();
                } catch (SQLException ex) {

                }
    }
    
    private void doInsertSurgeryDaysAssignment(Entity entity ) throws StoreException{
        SurgeryDaysAssignment surgeryDaysAssignment;
        if (entity != null) {
            if (entity.getIsSurgeryDaysAssignment()) {
                surgeryDaysAssignment = (SurgeryDaysAssignment)entity;
                for (Map.Entry<DayOfWeek, Boolean> entry : surgeryDaysAssignment.get().entrySet()) {
                    String sql = "INSERT INTO SurgeryDays (Day, IsSurgery) VALUES(?, ?);";
                    try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                        preparedStatement.setBoolean(2, entry.getValue());
                        switch (entry.getKey()) {
                            case MONDAY:
                                preparedStatement.setString(1, "Monday");
                                break;
                            case TUESDAY:
                                preparedStatement.setString(1, "Tuesday");
                                break;
                            case WEDNESDAY:
                                preparedStatement.setString(1, "Wednesday");
                                break;
                            case THURSDAY:
                                preparedStatement.setString(1, "Thursday");
                                break;
                            case FRIDAY:
                                preparedStatement.setString(1, "Friday");
                                break;
                            case SATURDAY:
                                preparedStatement.setString(1, "Saturday");
                                break;
                            case SUNDAY:
                                preparedStatement.setString(1, "Sunday");
                                break;
                        }
                        preparedStatement.execute();
                    } catch (SQLException ex) {
                        throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                                + "StoreException message -> exception raised in Repository::doInsertSurgeryDaysAssignment()",
                                StoreException.ExceptionType.SQL_EXCEPTION);
                    }
                }
            } else {
                String msg = "StoreException -> entity wrongly defined in Repository::doInsertSurgeryDaysAssignment()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity undefined in Repository::doInsertSurgeryDaysAssignment()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    
    private SurgeryDaysAssignment doReadSurgeryDaysAssignment(String sql)throws StoreException{
        SurgeryDaysAssignment surgeryDaysAssignment = null;
        try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
            ResultSet rs = preparedStatement.executeQuery();
            if (rs != null) {
                surgeryDaysAssignment = (SurgeryDaysAssignment) get(new SurgeryDaysAssignment(), rs);
            }
            return surgeryDaysAssignment;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::runSQL(PMS.READ_SURGERY_DAYS)",
                    StoreException.ExceptionType.SURGERY_DAYS_TABLE_MISSING_IN_PMS_DATABASE);
        }
    }
    
    private void doUpdateSurgeryDaysAssignment(Entity entity)throws StoreException{
        SurgeryDaysAssignment surgeryDaysAssignment;
        if (entity != null) {
            if (entity.getIsSurgeryDaysAssignment()) {
                surgeryDaysAssignment = (SurgeryDaysAssignment)entity;
                String sql = "UPDATE SurgeryDays SET IsSurgery = ? WHERE Day = ?;";
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    for (Map.Entry<DayOfWeek, Boolean> entry : surgeryDaysAssignment.get().entrySet()) {
                        //String sql = "UPDATE SurgeryDays SET IsSurgery = ? WHERE Day = ?;";
                        preparedStatement.setBoolean(1, entry.getValue());
                        switch (entry.getKey()) {
                            case MONDAY:
                                preparedStatement.setString(2, "Monday");
                                break;
                            case TUESDAY:
                                preparedStatement.setString(2, "Tuesday");
                                break;
                            case WEDNESDAY:
                                preparedStatement.setString(2, "Wednesday");
                                break;
                            case THURSDAY:
                                preparedStatement.setString(2, "Thursday");
                                break;
                            case FRIDAY:
                                preparedStatement.setString(2, "Friday");
                                break;
                            case SATURDAY:
                                preparedStatement.setString(2, "Saturday");
                                break;
                            case SUNDAY:
                                preparedStatement.setString(2, "Sunday");
                                break;
                        }
                        preparedStatement.execute();
                    }

                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateSurgeryDaysAssignment()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else {
                String msg = "StoreException -> entity wrongly defined in Repository::doUpdateSurgeryDaysAssignment()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity undefined in Repository::doUpdateSurgeryDaysAssignment()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doPMSSQLforTransmission(Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = new Entity(); 
        String sql = null;
        switch (q){
            case COUNT_TRANSMISSION ->{
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Transmission;";
                result.setValue(doCount(sql,entity).getValue());
                
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM TRansmission "
                        + "WHERE isdeleted = true";
                
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            }
            case CREATE_TRANSMISSION_TABLE ->{
                switch(repositoryName){
                    case "ACCESS" ->{
                        sql = "CREATE TABLE Transmission ("
                        + "pid AUTOINCREMENT PRIMARY KEY, "
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid), "
                        + "mode char(5), "
                        + "status char(7), "
                        + "transmissionDate DateTime, "
                        + "content LONGTEXT);";
                        break;
                    }
                    case "POSTGRES" ->{
                        break;
                    }
                }
                doCreateTable(sql);
                break;
            }
            case INSERT_TRANSMISSION ->{
                sql = "INSERT INTO Transmission "
                        + "(patientKey, mode, status, transmissionDate, content) "
                        + "VALUES(?,?,?,?,?);";
                doInsertTransmission(sql, entity);
                break;
            }
            case READ_ALL_TRANSMISSIONS ->{
                sql = "SELECT * "
                        + "FROM Transmission;";
                result = doReadAll(sql, entity);
                break; 
            }
            case READ_PENDING_TRANSMISSIONS ->{
                sql = "SELECT * "
                        + "FROM Transmission as t "
                        + "WHERE t.status = 'PENDING' "
                        + "ORDER BY transmissionDate ASC;";
                result = doReadTransmissionPending(sql,entity);
                break;
            }
            case READ_PENDING_TRANSMISSIONS_FOR_PATIENT ->{
                sql = "SELECT * "
                        + "FROM Transmission as t "
                        + "WHERE t.status = 'PENDING' "
                        + "AND patientKey = ? "
                        + "ORDER BY transmissionDate ASC;";
                result = doReadTransmissionPending(sql,entity);
                break;
            }
            case UPDATE_TRANSMISSION ->{
                sql = "UPDATE Transmission "
                        + "SET patientKey = ?, "
                        + "mode = ?, "
                        + "status = ?, "
                        + "transmissionDate = ?, "
                        + "content = ? "
                        + "WHERE pid = ? ;";
                doUpdateTransmission(sql, entity);
                break;
            }
        }
        return result;
    }

    private Entity doPMSSQLforAppointment(Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = new Entity(); 
        String sql = null;
        switch (q){
            case CANCEL_APPOINTMENT:
                sql = "UPDATE Appointment "
                        + "SET isCancelled = true "
                        + "WHERE pid = ?;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            case COUNT_APPOINTMENTS:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Appointment;";
                result.setValue(doCount(sql,entity).getValue());
                
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Appointment "
                        + "WHERE isdeleted = true";
                
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            case COUNT_APPOINTMENTS_FOR_DAY:
                switch (repositoryName){
                    case "ACCESS":
                        sql = "SELECT COUNT(*) as row_count "
                        + "FROM APPOINTMENT "
                        + "WHERE DatePart(\"yyyy\",a.start) = ? "
                        + "AND  DatePart(\"m\",a.start) = ? "
                        + "AND  DatePart(\"d\",a.start) = ? "
                        + "AND isDeleted = false;"; 
                        break;
                    case "POSTGRES":
                        sql = "SELECT COUNT(*) as row_count "
                        + "FROM APPOINTMENT "
                        + "WHERE EXTRACT(year from a.start) = ? "
                        + "AND  DatePart(month from a.start) = ? "
                        + "AND  DatePart(day from a.start) = ? "
                        + "AND isDeleted = false;"; 
                        break;
                }                     
                result = doCount(sql,entity);
                break;
            case COUNT_APPOINTMENTS_FOR_PATIENT:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM APPOINTMENT "
                        + "WHERE PatientKey = ? "
                        + "AND isDeleted = false ;";               
                result = doCount(sql,entity);
                break;
            case COUNT_APPOINTMENTS_FROM_DAY:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM APPOINTMENT "
                        + "WHERE start > ? "
                        + "AND isDeleted = false";
                result = doCount(sql,entity);
                break;
            case CREATE_APPOINTMENT_TABLE:
                switch(repositoryName){
                    case "ACCESS":
                        sql = "CREATE TABLE Appointment ("
                        + "pid LONG PRIMARY KEY, "
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid), "
                        + "invoiceKey LONG NOT NULL REFERENCES Invoice(pid), "
                        /*+ "patientNoteKey LONG NOT NULL REFERENCES PatientNote(pid), "*/
                        + "start DateTime, "
                        + "duration LONG, "
                        + "notes char(255), "
                        + "isDeleted YesNo, "
                        + "hasPatientBeenContacted YesNo, "
                        + "isEmergency YesNo, "
                        + "isCancelled YesNo);";
                        break;
                    case "POSTGRES":
                        break;
                }
                doCreateTable(sql);
                break;
            case DELETE_EMERGENCY_APPOINTMENT:
                sql = "DELETE FROM Appointment "
                        + "WHERE pid = ?;";
                doDeleteEmergencyAppointment(sql, entity);
                break;
            case DELETE_APPOINTMENT: //was commented out
                sql = "UPDATE Appointment "
                        + "SET isDeleted = true "
                        + "WHERE pid = ? ;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            
            case DELETE_APPOINTMENTS:
                sql = "Delete from Appointment;";
                doDelete(sql);
                break;
            case INSERT_APPOINTMENT:
                sql = "INSERT INTO Appointment "
                        /*+ "(PatientKey, Start, Duration, Notes,pid, patientNoteKey, invoiceKey) "*/
                        + "(isEmergency, InvoiceKey, PatientKey, Start, Duration, notes, pid) "
                        + "VALUES (?,?,?,?,?,?,?);";
                doInsertAppointment(sql, entity);
                break;
            case RECOVER_APPOINTMENT:
                sql = "UPDATE PatientNotificaton "
                        + "SET isDeleted = false "
                        + "WHERE pid = ?;";
                doRecoverPatientChild(sql, entity);
                break;
            case READ_APPOINTMENT_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Appointment;";
                result = doReadAppointmentHighestKey(sql);
                break;
            case READ_APPOINTMENT:
                //sql = "SELECT a.pid, a.Start, a.PatientKey, a.Duration, a.Notes, a.hasPatientBeenContacted "
                sql = "SELECT * "        
                        + "FROM Appointment AS a "
                        + "WHERE a.pid = ?; ";
                        /*+ "AND isDeleted = false;";*/
                result = doReadSingle(sql, entity);
                break;
            case READ_APPOINTMENTS:
                sql = "SELECT * "
                        + "FROM Appointment "
                        + "WHERE isDeleted = false AND isCancelled = false;";
                result = doReadAll(sql,entity);
                break;
            case READ_CANCELLED_APPOINTMENTS:
                sql = "SELECT * "
                        + "FROM Appointment a "
                        + "WHERE isDeleted = false AND isCancelled = true "
                        + "ORDER BY a.Start ASC;";
                result = doReadAll(sql, entity);
                break;
            case READ_DELETED_APPOINTMENTS_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM Appointment "
                        + "WHERE isDeleted = true "
                        + "AND PatientKey = ?;";
                result = doReadAppointmentsForPatient(sql, entity);
                break;
            case READ_APPOINTMENTS_FOR_DAY:
            case READ_APPOINTMENTS_FOR_DAY_AND_NON_EMERGENCY_APPOINTMENT:
                switch (repositoryName){
                    case "ACCESS":
                        sql = "select *"
                        + "from appointment as a "
                        + "where DatePart(\"yyyy\",a.start) = ? "
                        + "AND  DatePart(\"m\",a.start) = ? "
                        + "AND  DatePart(\"d\",a.start) = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false " 
                        + "AND isEmergency = false "
                        + "ORDER BY a.start ASC;";
                        break;
                    case "POSTGRES":
                        sql = "select *"
                        + "from appointment as a "
                        + "where EXTRACT(year from a.start) = ? "
                        + "AND  EXTRACT(month from a.start) = ? "
                        + "AND  EXTRACT(day from a.start) = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "AND isEmergency = false "
                        + "ORDER BY a.start ASC;";
                        break;
                }
                result = doReadAppointmentsForDay(sql, entity);
                break;
            case READ_APPOINTMENTS_FOR_DAY_AND_EMERGENCY_APPOINTMENT:
                switch (repositoryName){
                    case "ACCESS":
                        sql = "select *"
                        + "from appointment as a "
                        + "where DatePart(\"yyyy\",a.start) = ? "
                        + "AND  DatePart(\"m\",a.start) = ? "
                        + "AND  DatePart(\"d\",a.start) = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false " 
                        + "AND isEmergency = true "
                        + "ORDER BY a.start ASC;";
                        break;
                    case "POSTGRES":
                        sql = "select *"
                        + "from appointment as a "
                        + "where EXTRACT(year from a.start) = ? "
                        + "AND  EXTRACT(month from a.start) = ? "
                        + "AND  EXTRACT(day from a.start) = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "AND isEmergency = true "
                        + "ORDER BY a.start ASC;";
                        break;
                }
                result = doReadAppointmentsForDay(sql, entity);
                break;
            case READ_APPOINTMENTS_FROM_DAY:
                //sql = "SELECT a.pid, a.Start, a.PatientKey, a.Duration, a.Notes, a.hasPatientBeenContacted "
                sql = "SELECT * "        
                        + "FROM Appointment AS a "
                        + "WHERE a.Start >= ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "AND isEmergency = false "
                        + "ORDER BY a.Start ASC;";
                result = doReadAppointmentsFromDay(sql, entity);
                break;
            case READ_APPOINTMENTS_FOR_PATIENT:
                //sql = "SELECT a.pid, a.Start, a.PatientKey, a.Duration, a.Notes, a.hasPatientBeenContacted "
                sql = "SELECT * "
                        + "FROM Appointment AS a "
                        + "WHERE a.PatientKey = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY a.Start DESC";
                result = doReadAppointmentsForPatient(sql, entity);
                break;
            case READ_APPOINTMENTS_FOR_INVOICE:
                //sql = "SELECT a.pid, a.Start, a.PatientKey, a.Duration, a.Notes, a.hasPatientBeenContacted "
                sql = "SELECT * "
                        + "FROM Appointment AS a "
                        + "WHERE a.invoiceKey = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY a.Start DESC";
                result = doReadAppointmentsForInvoice(sql, entity);
                break;
            case UNCANCEL_APPOINTMENT:
                sql = "UPDATE Appointment "
                        + "SET isCancelled = false "
                        + "WHERE pid = ?;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            case UPDATE_APPOINTMENT:
                sql = "UPDATE Appointment "
                        + "SET PatientKey = ?, "
                        + "InvoiceKey = ?, "
                        + "Start = ?, "
                        + "Duration = ?, "
                        + "Notes = ?, "
                        + "isEmergency = ?, "
                        + "hasPatientBeenContacted = ? "
                        + "WHERE pid = ? ;";
                doUpdateAppointment(sql, entity);
                break;
        }
        return result;
    }
    
    private Entity doPMSSQLforPatient(Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_PATIENTS:
                sql = "SELECT COUNT(*) as row_count FROM Patient;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Patient "
                        + "WHERE isDeleted = true ;";
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            case CREATE_PATIENT_TABLE:
                sql = "CREATE TABLE Patient ("
                        + "pid Long PRIMARY KEY,"
                        + "title Char(10),"
                        + "forenames Char(25), "
                        + "surname Char(25), "
                        + "line1 Char(30), "
                        + "line2 Char(30), "
                        + "town Char(25), "
                        + "county Char(25), "
                        + "postcode Char(15), "
                        + "phone1 Char(30), "
                        + "phone2 Char(30), "
                        + "email Char(30), " 
                        + "gender Char(10), "
                        + "dob DateTime,"
                        + "isGuardianAPatient YesNo,"
                        + "recallFrequency Byte, "
                        + "recallDate DateTime, "
                        + "recallFrequencyGBT Byte, "
                        + "recallDateGBT DateTime, "
                        + "notes Char(255), "
                        + "guardianKey Long, "
                        + "isDeleted YesNo, "
                        + "isArchived YesNo), "
                        + "isRequestToSendPatientGBTRecallPending YesNo, "
                        + "isRequestToSendPatientNonGBTRecallPending YesNo, "
                        + "lastGBTRecallSendDate DateTime, "
                        + "lastNonGBTRecallSendDate;";
                doCreateTable(sql);
                break;
            case DELETE_PATIENT:
                doDeletePatient(entity);
                break;
            case DELETE_ALL_PATIENT:
                sql = "DELETE FROM Patient;";
                doDelete(sql);
                break;
            case INSERT_PATIENT:
                sql
                    = "INSERT INTO Patient "
                    + "(title, forenames, surname, line1, line2,"
                    + "town, county, postcode,phone1, phone2, gender, dob,"
                    + "isGuardianAPatient,recallFrequency, recallDate, notes,pid, guardianKey, email, recallFrequencyGBT, recallDateGBT, "
                        + "isArchived, isRequestToSendPatientGBTRecallPending, isRequestToSendPatientNonGBTRecallPending, lastGBTRecallSendDate, "
                        + "lastNonGBTRecallSendDate) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
                doInsertPatient(sql, entity);
                break;
            case READ_PATIENT:
                sql = "SELECT * "
                        + "FROM Patient "
                        + "WHERE pid=? "
                        + "AND isDeleted = false;";
                result = doReadSingle(sql, entity);
                break;
            case READ_PATIENT_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Patient;";
                result = doReadHighestKey(sql);
                break;
            case READ_PATIENTS:
                sql = "SELECT * "
                        + "FROM Patient "
                        + "WHERE isDeleted = false "
                        + "AND isArchived = false "
                        + "ORDER BY surname, forenames ASC;";
                result = doReadAll(sql, (Patient)entity);
                break;
            case READ_ARCHIVED_PATIENTS:
                sql = "SELECT * "
                        + "FROM Patient "
                        + "WHERE isArchived = true "
                        + "ORDER BY surname, forenames ASC;";
                result = doReadAll(sql, (Patient)entity);
                break; 
            case READ_DELETED_PATIENTS:
                sql = "SELECT * "
                        + "FROM Patient "
                        + "WHERE isDeleted = true "
                        + "ORDER BY surname, forenames ASC;";
                result = doReadAll(sql, (Patient)entity);
                break;    
            case RECOVER_PATIENT:
                doRecoverPatient(entity);
                break;
            case UPDATE_PATIENT:
                sql = "UPDATE PATIENT "
                    + "SET title = ?, "
                    + "forenames = ?,"
                    + "surname = ?,"
                    + "line1 = ?,"
                    + "line2 = ?,"
                    + "town = ?,"
                    + "county = ?,"
                    + "postcode = ?,"
                    + "phone1 = ?,"
                    + "phone2 = ?,"
                    + "gender = ?,"
                    + "dob = ?,"
                    + "isGuardianAPatient = ?,"
                    + "recallFrequency = ?,"
                    + "recallDate = ?,"
                    + "notes = ?,"
                    + "guardianKey = ?, "
                    + "email = ?, "
                    + "recallFrequencyGBT = ?, "
                    + "recallDateGBT = ?, "
                    + "isArchived = ?, "  
                    + "isRequestToSendPatientNonGBTRecallPending = ?, "
                    + "isRequestToSendPatientGBTRecallPending = ?, "
                    + "lastGBTRecallSentDate = ?, "
                    + "lastNonGBTRecallSentDate = ? "
                    + "WHERE pid = ? ;";
                doUpdatePatient(sql, entity);
                break;
        
        }
        return result;
    }
    
    private Entity doPMSSQLforPatientAppointmentData (Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case READ_ARCHIVED_PATIENT_APPOINTMENT_DATA:
                sql = "Select "
                        + "p.pid as patientKey, "
                        + "p.forenames as patient_forenames, "
                        + "p.surname as patient_surname, "
                        + "p.title as patient_title, "
                        + "p.phone1 as patient_phone1, "
                        + "p.phone2 as patient_phone2, "
                        + "p.email as patient_email, "
                        + "p.recallDate as recall_date, "
                        + "p.recallFrequency as recall_frequency, "
                        + "p.recallDateGBT as recall_date_GBT, "
                        + "p.recallFrequencyGBT as recall_frequency_GBT, "
                        + "p.isRequestToSendPatientGBTRecallPending, "
                        + "p.isRequestToSendPatientNonGBTRecallPending, "
                        + "p.lastGBTRecallSentDate, "
                        + "p.lastNonGBTRecallSentDate, "
                        + "a.pid as appointmentKey, "
                        + "a.start as last_appointment_date, "
                        + "a.notes as treatment "
                        + "FROM Patient p "
                        + "LEFT JOIN appointment as a ON ("
                        + "a.start = DMax (\"start\", \"appointment\", \"patientKey=\" & p.pid)) "
                        + "AND (a.patientKey = p.pid) "
                        + "WHERE p.isArchived = true "
                        + "ORDER BY p.surname, p.forenames ASC;";
                result = doReadAll(sql, (PatientAppointmentData )entity);
                break;
            case READ_PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT:
                sql = "Select "
                        + "p.pid as patientKey, "
                        + "p.forenames as patient_forenames, "
                        + "p.surname as patient_surname, "
                        + "p.title as patient_title, "
                        + "p.phone1 as patient_phone1, "
                        + "p.phone2 as patient_phone2, "
                        + "p.email as patient_email, "
                        + "p.recallDate as recall_date, "
                        + "p.recallFrequency as recall_frequency, "
                        + "p.recallDateGBT as recall_date_GBT, "
                        + "p.recallFrequencyGBT as recall_frequency_GBT, "
                        + "p.isRequestToSendPatientGBTRecallPending, "
                        + "p.isRequestToSendPatientNonGBTRecallPending, "
                        + "p.lastGBTRecallSentDate, "
                        + "p.lastNonGBTRecallSentDate, "
                        + "a.pid as appointmentKey, "
                        + "a.start as last_appointment_date, "
                        + "a.notes as treatment "
                        + "FROM Patient p "
                        + "LEFT JOIN Appointment a ON p.pid = a.patientKey "
                        + "WHERE p.pid > 1 "
                        + "AND p.pid <> 13814 "
                        + "AND p.isArchived = false "
                        + "AND a.patientKey IS NULL "
                        + "ORDER BY patient_surname,patient_forenames ASC;";
                result = doReadPatientAppointmentDataWithoutAppointment(sql, (PatientAppointmentData )entity);   
                break;
            case READ_PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT:
                sql = "Select "
                        + "p.pid as patientKey, "
                        + "p.forenames as patient_forenames, "
                        + "p.surname as patient_surname, "
                        + "p.title as patient_title, "
                        + "p.phone1 as patient_phone1, "
                        + "p.phone2 as patient_phone2, "
                        + "p.email as patient_email, "
                        + "p.recallDate as recall_date, "
                        + "p.recallFrequency as recall_frequency, "
                        + "p.recallDateGBT as recall_date_GBT, "
                        + "p.recallFrequencyGBT as recall_frequency_GBT, "
                        + "p.isRequestToSendPatientNonGBTRecallPending, "
                        + "p.isRequestToSendPatientGBTRecallPending, "
                        + "p.lastGBTRecallSentDate, "
                        + "p.lastNonGBTRecallSentDate, "
                        + "a.pid as appointmentKey, "
                        + "a.start as last_appointment_date, "
                        + "a.notes as treatment "
                        + "FROM Patient p, Appointment a "
                        + "WHERE p.pid = a.patientKey "
                        + "AND p.pid > 1 "
                        + "AND p.pid <> 13814 "
                        + "AND p.isArchived = false "
                        + "AND DatePart(\"yyyy\",a.start) BETWEEN ? AND ? "
                        /*+ "AND a.isCancelled = false "*/
                        + "AND a.start = "
                        + "(Select MAX(a2.start) "
                        + " FROM Appointment a2 "
                        + " WHERE a.patientKey = a2.patientKey) "
                        + "ORDER BY patient_surname,patient_forenames ASC;";
                result = doReadAll(sql, (PatientAppointmentData )entity);
                break;
            case READ_PATIENT_APPOINTMENT_DATA_BY_LAST_APPOINTMENT_DATE:
                sql = "Select "
                        + "p.pid as patientKey, "
                        + "p.forenames as patient_forenames, "
                        + "p.surname as patient_surname, "
                        + "p.title as patient_title, "
                        + "p.phone1 as patient_phone1, "
                        + "p.phone2 as patient_phone2, "
                        + "p.email as patient_email, "
                        + "p.recallDate as recall_date, "
                        + "p.recallFrequency as recall_frequency, "
                        + "p.recallDateGBT as recall_date_GBT, "
                        + "p.recallFrequencyGBT as recall_frequency_GBT, "
                        + "p.requestToSendPatientRecallReceived, "
                        + "p.GBTRecallSentDate, "
                        + "p.nonGBTRecallSentDate, "
                        + "a.pid as appointmentKey, "
                        + "a.start as last_appointment_date, "
                        + "a.notes as treatment "
                        + "FROM Patient p, Appointment a "
                        + "WHERE p.pid = a.patientKey "
                        + "AND p.pid > 1 "
                        + "AND p.pid <> 13814 "
                        + "AND p.isArchived = false "
                        + "AND DatePart(\"yyyy\",a.start) BETWEEN ? AND ? "
                        + "AND a.start = "
                        + "(Select MAX(a2.start) "
                        + " FROM Appointment a2 "
                        + " WHERE a.patientKey = a2.patientKey "
                        + " AND a2.isCancelled = false) "
                        + "ORDER BY last_appointment_date ASC;";
                result = doReadAll(sql, (PatientAppointmentData )entity);
                break;
        }
        return result;
    }
    
    /*private Entity doPMSSQLforArchivedPatient (Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_ARCHIVED_PATIENT:
                sql = "SELECT COUNT(*) as row_count FROM ArchivedPatient ;";
                result.setValue(doCount(sql,entity).getValue());

                break;
            case CREATE_ARCHIVED_PATIENT_TABLE:
                sql = "CREATE TABLE ArchivedPatient  ("
                        + "patientKey Long,"
                        + "patient_forenames CHAR(50), "
                        + "patient_surname CHAR(50), "
                        + "patient_title CHAR(15), "
                        + "appointmentKey Long, "
                        + "last_appointment_date DateTime;";
                doCreateTable(sql);
                break;
            case DELETE_ALL_ARCHIVED_PATIENT :
                sql = "DELETE FROM ArchivedPatient ;";
                doDelete(sql);
                break;
            case INSERT_ARCHIVED_PATIENT :
                sql
                    = "INSERT INTO ArchivedPatient  "
                    + "(patientKey, "
                        + "patient_forenames, "
                        + "patient_surname, "
                        + "patient_title, "
                        + "appointmentKey, "
                        + "last_appointment_date) "
                    + "Select p.pid, "
                        + "p.forenames, "
                        + "p.surname, "
                        + "p.title, "
                        + "a.pid, "
                        + "a.start "
                        + "FROM Patient p, Appointment a "
                        + "WHERE p.pid = a.patientKey "
                        + "AND p.pid > 1 "
                        + "AND p.pid <> 13814 "
                        + "AND p.isArchived = true "
                        + "AND DatePart(\"yyyy\",a.start) BETWEEN ? AND ? "
                        + "AND a.start = "
                        + "(Select MAX(a2.start) "
                        + " FROM Appointment a2 "
                        + " WHERE a.patientKey = a2.patientKey);";
                doInsertArchivedPatient (sql, entity);
                break;
            case READ_ARCHIVED_PATIENT_BY_PATIENT:
                sql = "SELECT * "
                        + "FROM ArchivedPatient p "
                        + "ORDER BY patient_surname,patient_forenames ASC;";
                result = doReadAll(sql, (ArchivedPatient )entity);
                break;  
            case READ_ARCHIVED_PATIENT_BY_LAST_APPOINTMENT_DATE:
                sql = "SELECT * "
                        + "FROM ArchivedPatient p "
                        + "ORDER BY last_appointment_date ASC;";
                result = doReadAll(sql, (ArchivedPatient )entity);
                break;
        }
        return result;
    }*/
    
    private Entity doPMSSQLforClinicalNote(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_CLINICAL_NOTE:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM ClinicalNote ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM ClinicalNote "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_CLINICAL_NOTE_TABLE:
                sql = "CREATE TABLE ClinicalNote ("
                        + "pid LONG CONSTRAINT PK_ClinicalNote PRIMARY KEY "
                        + "REFERENCES Appointment(pid), "
                        + "notes LONGTEXT, "
                        + "isDeleted YesNo);";
                doCreateTable(sql);
                break; 
            case DELETE_ALL_CLINICAL_NOTE:
                sql = "DELETE FROM ClinicalNote;";
                doDelete(sql);
                break;
            case DELETE_CLINICAL_NOTE:
                sql = "DELETE FROM ClinicalNote "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_CLINICAL_NOTE:
                sql = "INSERT INTO ClinicalNote "
                        + "(pid, notes, isDeleted) "
                        + "VALUES(?,?,?);";
                doInsertClinicalNote(sql, entity);
                break; 
            case READ_CLINICAL_NOTE:
                sql = "SELECT * "
                        + "FROM ClinicalNote "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_ALL_CLINICAL_NOTE:
                break;
            case READ_CLINICAL_NOTE_FOR_APPOINTMENT:
                sql = "SELECT * "
                        + "FROM ClinicalNote "
                        + "WHERE isDeleted = false "
                        + "AND pid = ?; ";
                result = doReadClinicalNoteForAppointment(sql, entity);
                break;
            case UPDATE_CLINIC_NOTE:
                sql = "UPDATE ClinicalNote "
                        + "SET notes = ? "
                        + "WHERE pid = ?;";
                doUpdateClinicalNote(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforToDo(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case CANCEL_TO_DO:
                sql = "UPDATE ToDo "
                        + "SET isCancelled = true "
                        + "WHERE pid = ?;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            case COUNT_TO_DO:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM ToDo ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM ToDo "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_TO_DO_TABLE:
                sql = "CREATE TABLE ToDo ("
                        + "pid LONG PRIMARY KEY, "
                        + "toDoDescription Char(150), "
                        + "toDoDate Double, "
                        + "isActioned YESNO, "
                        + "isCancelled YESNO, "
                        + "isDeleted YESNO; ";
                doCreateTable(sql);
                break;
            case DELETE_ALL_TO_DO:
                sql = "DELETE FROM ToDo;";
                doDelete(sql);
                break;
            case DELETE_TO_DO:
                sql = "UPDATE ToDo "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_TO_DO:
                sql = "INSERT INTO ToDo "
                        + "(toDoDate,toDoDescription,isActioned, isCancelled, isDeleted,pid) "
                        + "VALUES(?,?,?,?,?,?);";
                doInsertToDo(sql, entity);
                break; 
            case READ_TO_DO:
                sql = "SELECT * "
                        + "FROM ToDo "
                        + "WHERE pid = ?; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_TO_DO_FOR_USER:
                sql = "SELECT * "
                        + "FROM ToDo "
                        + "WHERE userKey = ? "
                        + "ORDER BY pid ASC;";
                //result = doReadToDoForPatient(sql, entity);
                break;
            case READ_ALL_TO_DO:
                sql = "SELECT * "
                        + "FROM ToDo "
                        + "WHERE isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY pid DESC;";
                result = doReadAll(sql,(ToDo)entity);
                break;
            case READ_UNACTIONED_TO_DO:
                sql = "SELECT * "
                        + "FROM ToDo "
                        + "WHERE isActioned = false "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY pid DESC;";
                result = doReadAll(sql,(ToDo)entity);
                break;
            case READ_TO_DO_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM ToDo;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_TO_DO:
                sql = "UPDATE ToDo "
                        + "SET toDoDate = ?, "
                        + "toDoDescription = ?, "
                        + "isActioned = ?, "
                        + "isCancelled = ?, "
                        + "isDeleted = ? "
                        + "WHERE pid = ?";
                doUpdateToDo(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforTreatment(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_TREATMENT:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Treatment ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Treatment "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_TREATMENT_TABLE:
                sql = "CREATE TABLE Treatment ("
                        + "pid LONG PRIMARY KEY, "
                        + "description CHAR(255), "
                        + "isDeleted YesNo);";
                doCreateTable(sql);
                break; 
            case DELETE_ALL_TREATMENT:
                sql = "DELETE FROM Treatment;";
                doDelete(sql);
                break;
            case DELETE_TREATMENT:
                sql = "UPDATE Treatment "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_TREATMENT:
                sql = "INSERT INTO Treatment "
                        + "(pid, description, isDeleted) "
                        + "VALUES(?,?,?);";
                doInsertTreatment(sql, entity);
                break; 
            case READ_TREATMENT:
                sql = "SELECT * "
                        + "FROM Treatment "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_ALL_TREATMENT:
                sql = "SELECT * "
                        + "FROM Treatment "
                        + "WHERE isDeleted = false "
                        + "ORDER BY description ASC; ";
                result = doReadAll(sql, (Treatment)entity);
                break;
            case READ_TREATMENT_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Treatment;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_TREATMENT:
                sql = "UPDATE Treatment "
                        + "SET description = ?, "
                        + "isDeleted = ? "
                        + "WHERE pid = ?;";
                doUpdateTreatment(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforUserSettings(Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case INSERT_USER_SETTINGS ->{
                sql = "INSERT INTO UserSettings "
                        + "(username, setting, red, green, blue, font, size) "
                        + "VALUES(?,?,?,?, ?, ?, ?);";
                doInsertUserSettings(sql, entity);
                break;   
            }
            case READ_USER_SETTINGS ->{
                sql = "SELECT username, setting, red, green, blue, font, size "
                        + "FROM UserSettings "
                        + "WHERE username = ? ;";
                result = doReadUserSettings(sql, entity);      
                break;
            }
            case UPDATE_USER_SETTINGS ->{
                sql =  "UPDATE UserSettings "
                        + "SET red = ?, "
                        + "green = ?, "
                        + "blue = ?, "
                        + "font = ?, "
                        + "size = ? "
                        + "WHERE username = ? AND setting = ? ;";
                doUpdateUserSettings(sql, entity);
            }
        }
        return result;
    }
    
    private Entity doPMSSQLforUser(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_USER:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM User ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM User "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_USER_TABLE:
                sql = "CREATE TABLE User ("
                        + "pid LONG PRIMARY KEY, "
                        + "username CHAR(30), "
                        + "password LONG TEXT"
                        + "salt LONG TEXT"
                        + "isDeleted YesNo);";
                doCreateTable(sql);
                break; 
            case DELETE_ALL_USER:
                sql = "DELETE FROM User;";
                doDelete(sql);
                break;
            case DELETE_USER:
                sql = "UPDATE User "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_USER:
                sql = "INSERT INTO User "
                        + "(username, password, salt, isDeleted) "
                        + "VALUES(?,?,?,?);";
                doInsertUser(sql, entity);
                break; 
            /*
            case READ_USER:
                sql = "SELECT * "
                        + "FROM User "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
                */
            case READ_USER:
                sql = "SELECT * "
                        + "FROM User "
                        + "WHERE username = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadUser(sql, entity);
                break;
            case READ_ALL_USER:
                sql = "SELECT * "
                        + "FROM User "
                        + "WHERE isDeleted = false "
                        + "ORDER BY usernaqme ASC; ";
                result = doReadAll(sql, (User)entity);
                break;
            case READ_USER_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM User;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_USER:
                sql = "UPDATE User "
                        + "SET password = ?, "
                        + "salt = ? "
                        + "WHERE username = ?;";
                doUpdateUser(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforQuestion(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_QUESTION:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Question ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Question "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_QUESTION_TABLE:
                sql = "CREATE TABLE Question ("
                        + "pid LONG PRIMARY KEY, "
                        + "category CHAR(20), "
                        + "description CHAR(255), "
                        + "sortorder LONG, "
                        + "isDeleted YesNo;";
                doCreateTable(sql);
                break; 
            case DELETE_ALL_QUESTION:
                sql = "DELETE FROM Question;";
                doDelete(sql);
                break;
            case DELETE_QUESTION:
                sql = "UPDATE Question "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_QUESTION:
                sql = "INSERT INTO Question "
                        + "(pid, category, description, isDeleted, sortorder) "
                        + "VALUES(?,?,?,?,?);";
                doInsertQuestion(sql, entity);
                break; 
            case READ_QUESTION:
                sql = "SELECT * "
                        + "FROM Question "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_QUESTION_FOR_CATEGORY:
                sql = "SELECT * "
                        + "FROM Question "
                        + "WHERE category = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadQuestionForCategory(sql, entity);
                break; 
            case READ_ALL_QUESTION:
                sql = "SELECT * "
                        + "FROM Question "
                        + "WHERE isDeleted = false "
                        + "ORDER BY sortorder ASC; ";
                result = doReadAll(sql, (Question)entity);
                break;
            case READ_QUESTION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Question;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_QUESTION:
                sql = "UPDATE Question "
                        + "SET category ?, "
                        + "description = ?, "
                        + "isDeleted = ?, "
                        + "sortorder = ?, "
                        + "WHERE pid = ?;";
                doUpdateQuestion(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforAppointmentTreatment(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_APPOINTMENT_TREATMENT:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM AppointmentTreatment ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM AppointmentTreatment "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_APPOINTMENT_TREATMENT_TABLE:
                sql = "CREATE TABLE AppointmentTreatment ("
                        + "appointmentKey LONG NOT NULL CONSTRAINT FK_AppointmentKey "
                        + "REFERENCES Appointment(pid), "
                        + "treatmentKey LONG NOT NULL CONSTRAINT FK_TreatmentKey "
                        + "REFERENCES Treatment(pid), "
                        + "comment CHAR(255), "
                        + "CONSTRAINT PK_AppointmentTreatment PRIMARY KEY(appointmentKey,treatmentKey));";
                doCreateTreatmentTable(sql);
                break; 
            case DELETE_ALL_APPOINTMENT_TREATMENT:
                sql = "DELETE FROM AppointmentTreatment;";
                doDelete(sql);
                break;
            case DELETE_APPOINTMENT_TREATMENT:
                sql = "DELETE FROM AppointmentTreatment "
                        + "WHERE appointmentKey = ? "
                        + "AND treatmentKey = ?;";
                doDeleteAppointmentTreatment(sql,entity);
                break;
            case INSERT_APPOINTMENT_TREATMENT:
                sql = "INSERT INTO AppointmentTreatment "
                        + "(appointmentKey, treatmentKey, comment) "
                        + "VALUES(?,?,?);";
                doInsertAppointmentTreatment(sql, entity);
                break; 
            case READ_APPOINTMENT_TREATMENT:
                sql = "SELECT * "
                        + "FROM AppointmentTreatment "
                        + "WHERE appointmentKey = ? "
                        + "AND treatmentKey = ?; ";
                result = doReadAppointmentTreatmentWithKey(sql, entity);
                break;
            case READ_APPOINTMENT_TREATMENT_FOR_APPOINTMENT:
                sql = "SELECT * "
                        + "FROM AppointmentTreatment "
                        + "WHERE appointmentKey = ?; ";                       
                result = doReadAppointmentTreatmentWithKey(sql, entity);
                break;
            case READ_APPOINTMENT_TREATMENT_FOR_TREATMENT:
                sql = "SELECT * "
                        + "FROM AppointmentTreatment "
                        + "WHERE treatmentKey = ?; ";                       
                result = doReadAppointmentTreatmentWithKey(sql, entity);
                break;
            /*
            case READ_APPOINTMENT_TREATMENT_FOR_TREATMENT:
                sql = "SELECT * "
                        + "FROM AppointmentTreatment "
                        + "WHERE treatmentKey = ?; ";                       
                result = doReadAppointmentTreatmentWithKey(sql, entity);
                break;#
                */
            /*
            case READ_ALL_APPOINTMENT_TREATMENT:
                sql = "SELECT * "
                        + "FROM AppointmentTreatment; ";
                result = doReadAppointmentTreatmentAll(sql, (AppointmentTreatment)entity);
                break;*/
            /*
            case READ_APPOINTMENT_TREATMENT_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM AppointmentTreatment;";
                result = doReadHighestKey(sql);
                break;*/
            
            case UPDATE_APPOINTMENT_TREATMENT:
                sql = "UPDATE AppointmentTreatment "
                        + "SET comment = ? "
                        //+ "isDeleted = ? "
                        + "WHERE appointmentKey = ? "
                        + "AND treatmentKey = ?;";
                doUpdateAppointmentTreatment(sql, entity);
                
        }
        return result;
    }
    
    private Entity doPMSSQLforPatientQuestion(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_PATIENT_QUESTION:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientQuestion ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientQuestion "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_PATIENT_QUESTION_TABLE:
                sql = "CREATE TABLE PatientQuestion ("
                        + "patientKey LONG NOT NULL CONSTRAINT FK_PatientKey "
                        + "REFERENCES Patient(pid), "
                        + "questionKey LONG NOT NULL CONSTRAINT FK_QuestionKey "
                        + "REFERENCES Question(pid), "
                        + "answer CHAR(255), "
                        + "CONSTRAINT PK_PatientQuestion PRIMARY KEY(patientKey,questionKey));";
                doCreateTreatmentTable(sql);
                break; 
            case DELETE_ALL_PATIENT_QUESTION:
                sql = "DELETE FROM PatientQuestion;";
                doDelete(sql);
                break;
            case DELETE_PATIENT_QUESTION:
                sql = "DELETE FROM PatientQuestion "
                        + "WHERE patientKey = ? "
                        + "AND questionKey = ?;";
                doDeletePatientQuestion(sql,entity);
                break;
            case INSERT_PATIENT_QUESTION:
                sql = "INSERT INTO PatientQuestion "
                        + "(patientKey, questionKey, answer) "
                        + "VALUES(?,?,?);";
                doInsertPatientQuestion(sql, entity);
                break; 
            case READ_PATIENT_QUESTION:
                sql = "SELECT * "
                        + "FROM PatientQuestion "
                        + "WHERE patientKey = ? "
                        + "AND questionKey = ?; ";
                result = doReadPatientQuestionWithKey(sql, entity);
                break;
            case READ_PATIENT_QUESTION_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM PatientQuestion "
                        + "WHERE patientKey = ?; ";                       
                result = doReadPatientQuestionWithKey(sql, entity);
                break;
            case READ_PATIENT_QUESTION_FOR_QUESTION:
                sql = "SELECT * "
                        + "FROM PatientQuestion "
                        + "WHERE questionKey = ?; ";                       
                result = doReadPatientQuestionWithKey(sql, entity);
                break;
            /*
            case READ_PATIENT_QUESTION_FOR_QUESTION:
                sql = "SELECT * "
                        + "FROM PatientQuestion "
                        + "WHERE questionKey = ?; ";                       
                result = doReadPatientQuestionWithKey(sql, entity);
                break;#
                */
            /*
            case READ_ALL_PATIENT_QUESTION:
                sql = "SELECT * "
                        + "FROM PatientQuestion; ";
                result = doReadPatientQuestionAll(sql, (PatientQuestion)entity);
                break;*/
            /*
            case READ_PATIENT_QUESTION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM PatientQuestion;";
                result = doReadHighestKey(sql);
                break;*/
            
            case UPDATE_PATIENT_QUESTION:
                sql = "UPDATE PatientQuestion "
                        + "SET answer = ? "
                        //+ "isDeleted = ? "
                        + "WHERE patientKey = ? "
                        + "AND questionKey = ?;";
                doUpdatePatientQuestion(sql, entity);
                
        }
        return result;
    }
    
    
    private Entity doPMSSQLforPatientPrimaryCondition(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_PATIENT_PRIMARY_CONDITION:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientPrimaryCondition ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientPrimaryCondition "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_PATIENT_PRIMARY_CONDITION_TABLE:
                sql = "CREATE TABLE PatientPrimaryCondition ("
                        + "patientKey LONG NOT NULL CONSTRAINT FK_PatientKey "
                        + "REFERENCES Patient(pid), "
                        + "primaryConditionKey LONG NOT NULL CONSTRAINT FK_PrimaryConditionKey "
                        + "REFERENCES PrimaryCondition(pid), "
                        + "comment CHAR(255), "
                        + "CONSTRAINT PK_PatientPrimaryCondition PRIMARY KEY(patientKey,primaryConditionKey));";
                doCreateTreatmentTable(sql);
                break; 
            case DELETE_ALL_PATIENT_PRIMARY_CONDITION:
                sql = "DELETE FROM PatientPrimaryCondition;";
                doDelete(sql);
                break;
            
            case DELETE_PATIENT_PRIMARY_CONDITION:
                sql = "DELETE FROM PatientPrimaryCondition "
                        + "WHERE patientKey = ? "
                        + "AND primaryConditionKey = ?;";
                doDeletePatientPrimaryCondition(sql,entity);
                break;
                
            case INSERT_PATIENT_PRIMARY_CONDITION:
                sql = "INSERT INTO PatientPrimaryCondition "
                        + "(patientKey, primaryConditionKey) "
                        + "VALUES(?,?);";
                doInsertPatientPrimaryCondition(sql, entity);
                break; 
            case READ_PATIENT_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PatientPrimaryCondition "
                        + "WHERE patientKey = ? "
                        + "AND primaryConditionKey = ?; ";
                result = doReadPatientPrimaryConditionWithKey(sql, entity);
                break;
            case READ_PATIENT_PRIMARY_CONDITION_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM PatientPrimaryCondition "
                        + "WHERE patientKey = ?; ";                       
                result = doReadPatientPrimaryConditionWithKey(sql, entity);
                break;
            case READ_PATIENT_PRIMARY_CONDITION_FOR_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PatientPrimaryCondition "
                        + "WHERE primaryConditionKey = ?; ";                       
                result = doReadPatientPrimaryConditionWithKey(sql, entity);
                break;
            /*
            case READ_ALL_PATIENT_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PatientPrimaryCondition; ";
                result = doReadPatientPrimaryConditionAll(sql, (PatientPrimaryCondition)entity);
                break;*/
            /*
            case READ_PATIENT_PRIMARY_CONDITION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM PatientPrimaryCondition;";
                result = doReadHighestKey(sql);
                break;*/
            
            case UPDATE_PATIENT_PRIMARY_CONDITION:
                sql = "UPDATE PatientPrimaryCondition "
                        + "SET comment = ? "
                        + "WHERE patientKey = ? "
                        + "AND primaryConditionKey = ?;";
                doUpdatePatientPrimaryCondition(sql, entity);
                
        }
        return result;
    }
    
    private Entity doPMSSQLforPatientSecondaryCondition(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_PATIENT_SECONDARY_CONDITION:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientSecondaryCondition ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientSecondaryCondition "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_PATIENT_SECONDARY_CONDITION_TABLE:
                sql = "CREATE TABLE PatientSecondaryCondition ("
                        + "patientKey LONG NOT NULL CONSTRAINT FK_PatientKey "
                        + "REFERENCES Patient(pid), "
                        + "secondaryConditionKey LONG NOT NULL CONSTRAINT FK_SecondaryConditionKey "
                        + "REFERENCES SecondaryCondition(pid), "
                        + "comment CHAR(255), "
                        + "CONSTRAINT PK_PatientSecondaryCondition SECONDARY KEY(patientKey,secondaryConditionKey));";
                doCreateTable(sql);
                break; 
            case DELETE_ALL_PATIENT_SECONDARY_CONDITION:
                sql = "DELETE FROM PatientSecondaryCondition;";
                doDelete(sql);
                break;
            case DELETE_PATIENT_SECONDARY_CONDITION:
                sql = "DELETE FROM PatientSecondaryCondition "
                        + "WHERE patientKey = ? "
                        + "AND secondaryConditionKey = ?;";
                doDeletePatientSecondaryCondition(sql,entity);
                break;
            case INSERT_PATIENT_SECONDARY_CONDITION:
                sql = "INSERT INTO PatientSecondaryCondition "
                        + "(patientKey, secondaryConditionKey,comment) "
                        + "VALUES(?,?,?);";
                doInsertPatientSecondaryCondition(sql, entity);
                break; 
            case READ_PATIENT_SECONDARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PatientSecondaryCondition "
                        + "WHERE patientKey = ? "
                        + "AND secondaryConditionKey = ?; ";
                result = doReadPatientSecondaryConditionWithKey(sql, entity);
                break;
            case READ_PATIENT_SECONDARY_CONDITION_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM PatientSecondaryCondition "
                        + "WHERE patientKey = ?; ";                       
                result = doReadPatientSecondaryConditionWithKey(sql, entity);
                break;
            case READ_PATIENT_SECONDARY_CONDITION_FOR_SECONDARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PatientSecondaryCondition "
                        + "WHERE secondaryConditionKey = ?; ";                       
                result = doReadPatientSecondaryConditionWithKey(sql, entity);
                break;
            
            case UPDATE_PATIENT_SECONDARY_CONDITION:
                sql = "UPDATE PatientSecondaryCondition "
                        + "SET comment = ? "
                        + "WHERE patientKey = ? "
                        + "AND secondaryConditionKey = ?;";
                doUpdatePatientSecondaryCondition(sql, entity);
                
        }
        return result;
    }
    
    private Entity doPMSSQLforDoctor(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_DOCTOR:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Doctor ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Doctor "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_DOCTOR_TABLE:
                sql = "CREATE TABLE Doctor ("
                        + "pid LONG PRIMARY KEY, "
                        + "title Char(30), "
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid), "
                        + "line1 Char(30), " 
                        + "line2 Char(30), " 
                        + "town Char(25), " 
                        + "county Char(25), " 
                        + "postcode Char(15), " 
                        + "phone1 Char(30), " 
                        + "email Char(30), " 
                        + "isDeleted YesNo, "
                        + "lastUpdated DateTime);";
                doCreateTreatmentTable(sql);
                break; 
            case DELETE_ALL_DOCTOR:
                sql = "DELETE FROM Doctor;";
                doDelete(sql);
                break;
            case DELETE_DOCTOR:
                sql = "UPDATE Doctor "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_DOCTOR:
                sql = "INSERT INTO Doctor "
                        + "(pid, patientKey,title, line1, "
                        + "line2, town, county, postcode, "
                        + "phone, email, isDeleted) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?);";
                doInsertDoctor(sql, entity);
                break; 
            case READ_DOCTOR:
                sql = "SELECT * "
                        + "FROM Doctor "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_ALL_DOCTOR:
                break;
            case READ_DOCTOR_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Doctor;";
                result = doReadHighestKey(sql);
                break;
            case READ_DOCTOR_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM Doctor "
                        + "WHERE patientKey = ? "
                        + "AND isDeleted = false; ";
                result = doReadDoctorForPatient(sql, entity);
                break;
            case UPDATE_DOCTOR:
                sql = "UPDATE Doctor "
                        + "SET patientKey = ?, "
                        + "title = ?, "
                        + "line1 = ?, "
                        + "line2 = ?, "
                        + "town = ?, "
                        + "county = ?, "
                        + "postcode = ?, "
                        + "phone = ?, "
                        + "email = ?, "
                        + "isDeleted = ? "
                        + "WHERE pid = ?;";
                doUpdateDoctor(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforMedication(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_MEDICATION:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Medication ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Medication "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_MEDICATION_TABLE:
                sql = "CREATE TABLE Medication ("
                        + "pid LONG PRIMARY KEY, "
                        + "description Char(50), "
                        + "notes Char(255), "
                        + "isDeleted YesNo, "
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid));";
                doCreateTreatmentTable(sql);
                break;
            case DELETE_ALL_MEDICATION:
                sql = "DELETE FROM Medication;";
                doDelete(sql);
                break;
            case DELETE_MEDICATION:
                sql = "UPDATE Medication "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_MEDICATION:
                sql = "INSERT INTO Medication "
                        + "(pid,patientKey,description,notes,isDeleted) "
                        + "VALUES(?,?,?,?,?);";
                doInsertMedication(sql, entity);
                break; 
            case READ_MEDICATION:
                sql = "SELECT * "
                        + "FROM Medication "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_MEDICATION_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM Medication "
                        + "WHERE patientKey = ? "
                        + "AND isDeleted = false "
                        + "ORDER BY description ASC;";
                result = doReadMedicationForPatient(sql, entity);
                break;
            case READ_ALL_MEDICATION:
                break;
            case READ_MEDICATION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Medication;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_MEDICATION:
                sql = "UPDATE Medication "
                        + "SET patientKey = ?, "
                        + "description = ?, "
                        + "notes = ?, "
                        + "isDeleted = ? "
                        + "WHERE pid = ?";
                doUpdateMedication(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforInvoice(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_INVOICE:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Invoice ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM Invoice "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_INVOICE_TABLE:
                sql = "CREATE TABLE Invoice ("
                        + "pid LONG PRIMARY KEY, "
                        + "description Char(150), "
                        + "amount Double, "
                        + "isDeleted YESNO"
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid));";
                doCreateTable(sql);
                break;
            case DELETE_ALL_INVOICE:
                sql = "DELETE FROM Invoice;";
                doDelete(sql);
                break;
            case DELETE_INVOICE:
                sql = "UPDATE Invoice "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_INVOICE:
                sql = "INSERT INTO Invoice "
                        + "(amount,description,isdeleted,patientKey,pid) "
                        + "VALUES(?,?,?,?,?);";
                doInsertInvoice(sql, entity);
                break; 
            case READ_INVOICE:
                sql = "SELECT * "
                        + "FROM Invoice "
                        + "WHERE pid = ?; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_INVOICE_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM Invoice "
                        + "WHERE patientKey = ? "
                        + "ORDER BY pid ASC;";
                result = doReadInvoiceForPatient(sql, entity);
                break;
            case READ_ALL_INVOICE:
                break;
            case READ_INVOICE_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM Invoice;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_INVOICE:
                sql = "UPDATE Invoice "
                        + "SET amount = ?, "
                        + "description = ?, "
                        + "isDeleted = ? "
                        + "patientKey = ?, "
                        + "WHERE pid = ?";
                doUpdateInvoice(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforTreatmentCost(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_TREATMENT_COST:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM TreatmentCost ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM TreatmentCost "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_TREATMENT_COST_TABLE:
                sql = "CREATE TABLE TreatmentCost ("
                        + "pid LONG PRIMARY KEY, "
                        + "description Char(150), "
                        + "amount Double, "
                        + "isDeleted YESNO"
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid));";
                doCreateTable(sql);
                break;
            case DELETE_ALL_TREATMENT_COST:
                sql = "DELETE FROM TreatmentCost;";
                doDelete(sql);
                break;
            case DELETE_TREATMENT_COST:
                sql = "UPDATE TreatmentCost "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteSingle(sql,entity);
                break;
            case INSERT_TREATMENT_COST:
                sql = "INSERT INTO TreatmentCost "
                        + "(amount,description,invoiceKey,isDeleted,pid) "
                        + "VALUES(?,?,?,?,?);";
                doInsertTreatmentCost(sql, entity);
                break; 
            case READ_TREATMENT_COST:
                sql = "SELECT * "
                        + "FROM TreatmentCost "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_TREATMENT_COST_FOR_INVOICE:
                sql = "SELECT * "
                        + "FROM TreatmentCost "
                        + "WHERE ivoiceKeyKey = ? "
                        + "AND isDeleted = false "
                        + "ORDER BY pid ASC;";
                result = doReadTreatmentCostForInvoice(sql, entity);
                break;
            case READ_ALL_TREATMENT_COST:
                break;
            case READ_TREATMENT_COST_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM TreatmentCost;";
                result = doReadHighestKey(sql);
                break;
            case UPDATE_TREATMENT_COST:
                sql = "UPDATE TreatmentCost "
                        + "SET amount = ?, "
                        + "description = ?, "
                        + "invoiceKey = ?, "
                        + "isDeleted = ? "
                        + "WHERE pid = ?";
                doUpdateTreatmentCost(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforPrimaryCondition(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_PRIMARY_CONDITION:
                sql = "SELECT COUNT(*) as row_count FROM PrimaryCondition;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PrimaryCondition "
                        + "WHERE isDeleted = true ;";
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            case CREATE_PRIMARY_CONDITION_TABLE:
                sql = "CREATE TABLE PrimaryCondition ("
                        + "pid LONG PRIMARY KEY, "
                        + "description Char(100), "
                       /* + "notes Char(255), "
                        + "state YesNo, "*/
                        + "isDeleted YesNo); ";
                        //+ "patientKey LONG NOT NULL REFERENCES Patient(pid));";
                doCreateTreatmentTable(sql);
                break;
            case DELETE_ALL_PRIMARY_CONDITION:
                sql = "DELETE FROM PrimaryCondition;";
                doDelete(sql);
                break;
            case DELETE_PRIMARY_CONDITION:
                sql = "DELETE FROM PrimaryCondition "
                        + "WHERE pid = ?;";
                doDeleteCondition(sql,entity);
                break;
            case READ_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PrimaryCondition "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
                
            case READ_ALL_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PrimaryCondition "
                        + "WHERE isDeleted = false "
                        + "ORDER BY description ASC";
                result = doReadAll(sql, entity);
                break;
                
            case READ_PRIMARY_CONDITION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM PrimaryCondition;";
                result = doReadHighestKey(sql);
                break;
            case INSERT_PRIMARY_CONDITION:
                sql
                    = "INSERT INTO PrimaryCondition "
                    + "(pid, description, /*state, notes,*/isDeleted) "
                    + "VALUES (?,?,?);";
                doInsertPrimaryCondition(sql, entity);
                break;
            case UPDATE_PRIMARY_CONDITION:
                sql = "UPDATE PrimaryCondition "
                        + "SET description = ?, "
                        /*+ "state = ?, "
                        + "notes = ?, "*/
                        + "isDeleted = ? "
                        + "WHERE pid = ?";
                doUpdatePrimaryCondition(sql, entity);
        }
        return result;
    }
    
    private Entity doPMSSQLforSecondaryCondition(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_SECONDARY_CONDITION:
                sql = "SELECT COUNT(*) as row_count FROM SecondaryCondition;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM SecondaryCondition "
                        + "WHERE isDeleted = true ;";
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            case CREATE_SECONDARY_CONDITION_TABLE:
                sql = "CREATE TABLE SecondaryCondition ("
                        + "pid LONG PRIMARY KEY, "
                        + "description Char(100), "
                       /* + "state YesNo, "
                        + "notes Char(255), "*/
                        + "isDeleted YesNo, "
                        + "primaryConditionKey LONG NOT NULL REFERENCES PrimaryCondition(pid));";
                doCreateTreatmentTable(sql);
            case DELETE_ALL_SECONDARY_CONDITION:
                sql = "DELETE FROM SecondaryCondition;";
                doDelete(sql);
                break;
            case DELETE_SECONDARY_CONDITION:
                sql = "DELETE FROM SecondaryCondition "
                        + "WHERE pid = ?";
                doDeleteCondition(sql,entity);
                break;
            case READ_SECONDARY_CONDITION:
                sql = "SELECT * "
                        + "FROM SecondaryCondition "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_SECONDARY_CONDITION_FOR_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM SecondaryCondition "
                        + "WHERE primaryConditionKey = ? "
                        + "AND isDeleted = false "
                        + "ORDER BY description ASC";
                result = doReadSecondaryConditionForPrimaryCondition(sql, entity);
                break;
            case READ_ALL_SECONDARY_CONDITION:
                sql = "SELECT * "
                        + "FROM SecondaryCondition "
                        + "WHERE isDeleted = false; ";
                result = doReadAll(sql, entity);
                break;
            case READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM SecondaryCondition;";
                result = doReadHighestKey(sql);
                break;
            case INSERT_SECONDARY_CONDITION:
                sql
                    = "INSERT INTO SecondaryCondition "
                    + "(pid, primaryConditionKey, description,isDeleted) "
                    + "VALUES (?,?,?,?);";
                doInsertSecondaryCondition(sql, entity);
                break;
            case UPDATE_SECONDARY_CONDITION:
                sql = "UPDATE SecondaryCondition "
                        + "SET primaryConditionKey = ?, "
                        + "description = ?, "
                       /* + "state = ?, "
                        + "notes = ?, "*/
                        + "isDeleted = ? "
                        + "WHERE pid = ?";
                doUpdateSecondaryCondition(sql, entity);
        }
        return result;
    }
    
    
    /*private Entity doPMSSQLforPatientNote(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case COUNT_PATIENT_NOTES:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientNote ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientNote "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case CREATE_PATIENT_NOTE_TABLE:
                sql = "CREATE TABLE PatientNote ("
                        + "pid LONG PRIMARY KEY, "
                        + "datestamp DateTime, "
                        + "patientKey LONG NOT NULL REFERENCES Patient(pid), "
                        + "note Long Text, "
                        + "isDeleted YesNo, "
                        + "lastUpdated DateTime;";
                doCreateTreatmentTable(sql);
                break;
            case DELETE_PATIENT_NOTE:
                sql = "UPDATE PatientNote "
                        + "SET isDeleted = true "
                        + "WHERE pid = ?";

                doDeleteSingle(sql, entity);
                break; 
            case DELETE_PATIENT_NOTES:
                sql = "DELETE FROM PatientNote;";
                doDelete(sql);
                break;
            case INSERT_PATIENT_NOTE:
                sql = "INSERT INTO PatientNote "
                        + "(datestamp, patientKey, notes, isDeleted,lastUpdated,pid) "
                        + "VALUES(?,?,?,?,?,?);";
                doInsertPatientNote(sql, entity);
                break; 
            case READ_PATIENT_NOTE:
                sql = "SELECT * "
                        + "FROM PatientNote "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false; ";                       
                result = doReadSingle(sql, entity);
                break;
            case READ_ALL_PATIENT_NOTES:
                break;
            case READ_PATIENT_NOTE_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM PatientNote;";
                result = doReadHighestKey(sql);
                break;
            case READ_NOTES_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM PatientNote "
                        + "WHERE patientKey = ? "
                        + "AND isDeleted = false "
                        + "ORDER BY datestamp DESC;";
                result = doReadPatientNotesForPatient(sql, entity);
                break;
            case RECOVER_PATIENT_NOTE:
                sql = "UPDATE PatientNote "
                        + "SET isDeleted = false "
                        + "WHERE pid = ?;";
                doRecoverPatientChild(sql, entity);
                break;
            case UPDATE_PATIENT_NOTE:
                sql = "UPDATE PatientNote "
                        + "SET datestamp = ?, "
                        + "patientKey = ?, "
                        + "notes = ?, "
                        + "lastUpdated = ? "
                        + "WHERE pid = ?";
                doUpdatePatientNote(sql, entity);
            

        }
        return result;
    }*/
    
    private Entity doPMSSQLforNotification(Repository.PMSSQL q, Entity entity) throws StoreException{
        Entity result = new Entity();
        String sql;
        switch (q){
            case CANCEL_NOTIFICATION:
                sql = "UPDATE PatientNotification "
                        + "SET isCancelled = true "
                        + "WHERE pid = ?;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            case COUNT_DELETED_NOTIFICATIONS:
                sql = "SELECT COUNT(*) as row_count FROM PatientNotification "
                        + "WHERE isDeleted = true;";
                result = doCount(sql,entity);
                break;
            case COUNT_PATIENT_NOTIFICATIONS:
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientNotification ;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PatientNotification "
                        + "WHERE isDeleted = true;";
                result.setValue(new Point(result.getValue().x,
                        doCount(sql,entity).getValue().x));
                break;
            case COUNT_UNACTIONED_NOTIFICATIONS:
                sql = "SELECT COUNT(*) as record_count "
                        + "FROM PatientNotifications "
                        + "WHERE isActioned = false "
                        + "AND isDeleted = false;";
                result = doCount(sql,entity);
                break;
            case CREATE_NOTIFICATION_TABLE:
                sql = "CREATE TABLE PatientNotification ("
                        + "pid LONG PRIMARY KEY, "
                        + "patientToNotify LONG NOT NULL REFERENCES Patient(pid), "
                        + "notificationDate DateTime, "
                        + "notificationText char,"
                        + "isActioned YesNo,"
                        + "isDeleted YesNo,"
                        + "isCancelled YesNo);";
                doCreateTreatmentTable(sql);
                break;
            /*
            case DELETE_PATIENT_NOTIFICATION:
                sql = "UPDATE Notification "
                        + "SET isDeleted = ? "
                        + "WHERE pid = ?;"; 
                doDeleteNotification(sql, entity);
                break; 
            */
            case DELETE_NOTIFICATIONS:
                sql = "DELETE FROM PatientNotification;";
                doDelete(sql);
                break;
            case INSERT_NOTIFICATION:
                sql = "INSERT INTO PatientNotification "
                        + "(patientToNotify, notificationDate, notificationText, isActioned, pid) "
                        + "VALUES(?,?,?,?,?);";
                doInsertPatientNotification(sql, entity);
                break;
            case READ_NOTIFICATION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM PatientNotification;";
                result = doReadHighestKey(sql);
                break;
            case READ_NOTIFICATION:
                sql = "SELECT * "
                        + "FROM PatientNotification "
                        + "WHERE pid = ? "
                        + "AND isDeleted = false;";
                result = doReadSingle(sql, entity);
                break;
            case READ_NOTIFICATIONS_FOR_PATIENT:
                //sql = "SELECT patientToNotify, notificationDate, notificationText, isActioned, isDeleted, pid "
                sql = "SELECT * "
                        + "FROM PatientNotification "
                        + "WHERE patientToNotify = ?"
                        + "AND isCancelled = false "
                        + "AND isDeleted = false;";
                result = doReadPatientNotificationsForPatient(sql, entity);
                break;
            case READ_DELETED_NOTIFICATIONS_FOR_PATIENT:
                //sql = "SELECT patientToNotify, notificationDate, notificationText, isActioned, isDeleted, pid "
                sql = "SELECT * "
                        + "FROM PatientNotification "
                        + "WHERE patientToNotify = ?"
                        + "AND isDeleted = true;";
                result = doReadPatientNotificationsForPatient(sql, entity);
                break;
            case READ_UNACTIONED_NOTIFICATIONS:
                sql = "SELECT * FROM PatientNotification "
                        + "WHERE IsActioned = false "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY notificationDate DESC;";
                result = doReadAll(sql, entity);
                break;
            case READ_NOTIFICATIONS:
                sql = "SELECT * FROM PatientNotification "
                        + "WHERE isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY notificationDate DESC;";
                result = doReadAll(sql, entity);
                break;
            case RECOVER_NOTIFICATION:
                sql = "UPDATE PatientNotificaton "
                        + "SET isDeleted = false "
                        + "WHERE pid = ?;";
                doRecoverPatientChild(sql, entity);
                break;
            /*    
            case UNCANCEL_APPOINTMENT:
                sql = "UPDATE Appointment "
                        + "SET isCancelled = false "
                        + "WHERE pid = ?;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            */
            case UPDATE_NOTIFICATION:
                sql = "UPDATE PatientNotification "
                        + "SET patientToNotify = ?, "
                        + "notificationDate = ?, "
                        + "notificationText = ?, "
                        + "isActioned = ? "
                        + "WHERE pid = ?;";
                doUpdatePatientNotification(sql, entity);                
        }
        return result;
    }
    
    private Entity doPMSSQLforSurgeryDaysAssignment(Repository.PMSSQL q, Entity entity)throws StoreException{
        Entity result = null;
        String sql ;
        switch (q){
            case COUNT_SURGERY_DAYS_ASSIGNMENT:
                sql = "SELECT COUNT(*) as row_count FROM SurgeryDays;";
                result = doCount(sql,entity);
                break;
            case CREATE_SURGERY_DAYS_ASSIGNMENT_TABLE:
                sql = "CREATE TABLE SurgeryDays ("
                        + "Day Char(10),"
                        + "IsSurgery YesNo);";
                doCreateTreatmentTable(sql);
                break;
            case DELETE_SURGERY_DAYS_ASSIGNMENT:
                sql = "DELETE FROM SurgeryDays;";
                doDelete(sql);
                break;
            case DROP_SURGERY_DAYS_ASSIGNMENT_TABLE:
                sql = "DROP TABLE SurgeryDays;";
                doDropSurgeryDaysAssignmentTable(sql);
                break;
            case READ_SURGERY_DAYS_ASSIGNMENT:
                sql = "SELECT Day, IsSurgery FROM SurgeryDays;";
                result = doReadSurgeryDaysAssignment(sql);
                break;
            case INSERT_SURGERY_DAYS_ASSIGNMENT:
                doInsertSurgeryDaysAssignment(entity);
                break;
            case UPDATE_SURGERY_DAYS_ASSIGNMENT:
                doUpdateSurgeryDaysAssignment(entity);
                      
        }
        return result;
    }
    
    @Override
    public Integer insert(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
        runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT, 
            Repository.PMSSQL.INSERT_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment);
        return null;  
    }

    /**
     * method requires explicit declaration of the appointee's key value
     * @param appointment
     * @param appointeeKey
     * @return
     * @throws StoreException 
     */
    @Override
    public Integer insert(Appointment appointment,Integer appointeeKey, Integer patientNoteKey) throws StoreException {
        Integer result = null;
        //Appointment delegate = new AppointmentDelegate(appointment);
        //Patient patient = delegate.getPatient();
        /*28/03/2024PatientNoteDelegate noteDelegate = new PatientNoteDelegate();*/
        
        //patient.setKey(appointeeKey);
        //delegate.setPatient(patient);
        /*28/03/2024noteDelegate.setKey(patientNoteKey);
        delegate.setPatientNote(noteDelegate);*/
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.APPOINTMENT,
                Repository.PMSSQL.READ_APPOINTMENT_NEXT_HIGHEST_KEY,null);
        entity = (Entity)client;
        if (entity.getValue()!=null) {
            appointment.setKey(entity.getValue().x + 1);
            runSQL(Repository.EntityType.APPOINTMENT,Repository.PMSSQL.INSERT_APPOINTMENT, appointment);
            return appointment.getKey();
        }
        else {
            displayErrorMessage("Unable to calculate a new key value for the new Appointment.\n"
                    + "Error raised in Repository::insert(Appointment) : Integer",
                    "Access store error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }

    @Override
    /**
     * method attempts to insert a new patient notification record on the database
     * -- it assumes the key of the PatientNotification object is undefined
     * -- it fetches the next highest key value from the database and initialises the PatientNotification object with this
     * -- after creating a new patient notification record the method attempts to read back the record using the key value it defined
     * -- on success the method returns; else throws an exception
     * @param pn; PatientNotification which points to the calling PatientNotification object instance
     * @exception StoreException is thrown 
     * -- [1] if the received PatientNotification object already has a key value
     * -- [2] if patient notification record cannot be read back successfully
     * -- [3] passes on a StoreException error thrown by the database
     */
    public Integer insert(Notification pn, Integer patientKey)throws StoreException{
        Entity key = null;
        Notification notification = null;
        Patient patient = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.PATIENT_NOTIFICATION,
                    Repository.PMSSQL.READ_NOTIFICATION_NEXT_HIGHEST_KEY,pn);
        entity = (Entity)client;
        //notification = new NotificationDelegate(pn);
        pn.setKey(entity.getValue().x + 1);
        //patient = notification.getPatient();
        //patient.setKey(patientKey);
        notification.setPatient(patient);
        //30/07/2022 09:26
        runSQL(Repository.EntityType.PATIENT_NOTIFICATION,
                Repository.PMSSQL.INSERT_NOTIFICATION, pn);
        return notification.getKey();
    }

    @Override
    public Integer insert(Doctor doctor)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.DOCTOR,
                    Repository.PMSSQL.READ_DOCTOR_NEXT_HIGHEST_KEY,doctor);
        entity = (Entity)client;
        doctor.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.DOCTOR,
                Repository.PMSSQL.INSERT_DOCTOR, doctor);
        
        return doctor.getKey();
    }
    
    @Override
    public void insert(UserSettings _userSettings)throws StoreException{
        Entity entity;
        IStoreClient client;
        runSQL(Repository.EntityType.USER_SETTINGS,
                Repository.PMSSQL.INSERT_USER_SETTINGS, _userSettings);
    }
    
    @Override
    public Integer insert(User _user)throws StoreException{
        boolean test = false;
        int count = 0;
        Entity entity;
        IStoreClient client;
        SystemDefinition.userS = colinhewlettsolutions.client.controller.PasswordUtils.generateSalt();
        colinhewlettsolutions.client.controller.PasswordUtils.setSalt(SystemDefinition.userS);
        try{
            SystemDefinition.userP = colinhewlettsolutions.client.controller.PasswordUtils.hashPassword(_user.getPassword(), SystemDefinition.userS);
            colinhewlettsolutions.client.controller.PasswordUtils.setHashedPassword(SystemDefinition.userP);
            
            test = colinhewlettsolutions.client.controller.PasswordUtils.isSaltCorrect(SystemDefinition.userS);
            test = colinhewlettsolutions.client.controller.PasswordUtils.isHashedPasswordCorrect(SystemDefinition.userP);
            test = colinhewlettsolutions.client.controller.PasswordUtils.isPasswordCorrect(_user.getPassword(),SystemDefinition.userP, SystemDefinition.userS);
            count = SystemDefinition.userS.length();
            count = SystemDefinition.userP.length();
            //SystemDefinition.userP = controller.PasswordUtilsx.hashPassword(_user.getPassword(), SystemDefinition.userS);
            /*client = runSQL(Repository.EntityType.USER,
                        Repository.PMSSQL.READ_USER_NEXT_HIGHEST_KEY,_user);
            entity = (Entity)client;
            if (entity == null) _user.setKey(1);
            else _user.setKey(entity.getValue().x + 1);*/

            runSQL(Repository.EntityType.USER,
                Repository.PMSSQL.INSERT_USER, _user);
            
            _user.setScope(Entity.Scope.WITH_CREDENTIAL);
            entity = (Entity)runSQL(Repository.EntityType.USER, 
                            Repository.PMSSQL.READ_USER, _user);
            
            count = colinhewlettsolutions.client.controller.PasswordUtils.getSalt().length();
            count = colinhewlettsolutions.client.controller.PasswordUtils.getHashedPassword().length();
            count = SystemDefinition.userS.length();
            count = SystemDefinition.userP.length();
            test = colinhewlettsolutions.client.controller.PasswordUtils.isSaltCorrect(SystemDefinition.userS);
            test = colinhewlettsolutions.client.controller.PasswordUtils.isHashedPasswordCorrect(SystemDefinition.userP);
            test = colinhewlettsolutions.client.controller.PasswordUtils.isPasswordCorrect(_user.getPassword(), SystemDefinition.userP, SystemDefinition.userS);
            
            if (entity!=null){
                if(!colinhewlettsolutions.client.controller.PasswordUtils.isPasswordCorrect(_user.getPassword(),SystemDefinition.userP, SystemDefinition.userS)){
                    //if(!MessageDigest.isEqual(hashPassword, SystemDefinition.userP)){
                    LoginException loginException = new LoginException("security breach");
                    loginException.setLoginErrorType(LoginException.LoginExceptionType.MATCHING_PASSWORD_NOT_FOUND);
                    throw loginException;
                }
            }
            
            
        }catch(NoSuchAlgorithmException ex){
            String message = ex.getMessage() + "\n"
                    + "NoSuchAlgorithmException raised in Repository::insert(User)";
            displayErrorMessage(message,"Repository error", JOptionPane.WARNING_MESSAGE);
        }catch(InvalidKeySpecException ex){
            String message = ex.getMessage() + "\n"
                    + "InvalidKeySpecException raised in Repository::insert(User)";
            displayErrorMessage(message,"Repository error", JOptionPane.WARNING_MESSAGE);
        }
        return _user.getKey();
    }
    
    @Override
    public Integer insert(Medication medication)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.MEDICATION,
                    Repository.PMSSQL.READ_MEDICATION_NEXT_HIGHEST_KEY,medication);
        entity = (Entity)client;
        medication.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.MEDICATION,
                Repository.PMSSQL.INSERT_MEDICATION, medication);
        
        return medication.getKey();
    }
    
    @Override
    public Integer insert(Invoice invoice)throws StoreException{
        Entity key = null;
        Entity entity;
        /*
        IStoreClient client;
        client = runSQL(Repository.EntityType.INVOICE,
                    Repository.PMSSQL.READ_INVOICE_NEXT_HIGHEST_KEY,invoice);
        entity = (Entity)client;
        invoice.setKey(entity.getValue().x + 1);
        */
        runSQL(Repository.EntityType.INVOICE,
                Repository.PMSSQL.INSERT_INVOICE, invoice);
        
        return invoice.getKey();
    }
    
    @Override
    public Integer insert(TreatmentCost treatmentCost)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.TREATMENT_COST,
                    Repository.PMSSQL.READ_TREATMENT_COST_NEXT_HIGHEST_KEY,treatmentCost);
        entity = (Entity)client;
        treatmentCost.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.TREATMENT_COST,
                Repository.PMSSQL.INSERT_TREATMENT_COST, treatmentCost);
        
        return treatmentCost.getKey();
    }
    
    @Override
    public Integer insert(ClinicalNote clinicNote)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        /*
        client = runSQL(Repository.EntityType.CLINICAL_NOTE,
                    Repository.PMSSQL.READ_CLINIC_NOTE_NEXT_HIGHEST_KEY,clinicNote);
        entity = (Entity)client;
        clinicNote.setKey(entity.getValue().x + 1);
        */
        runSQL(Repository.EntityType.CLINICAL_NOTE,
                Repository.PMSSQL.INSERT_CLINICAL_NOTE, clinicNote);
        
        return clinicNote.getKey();
    }
    
    @Override
    public Integer insert(Treatment treatment)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.TREATMENT,
                    Repository.PMSSQL.READ_TREATMENT_NEXT_HIGHEST_KEY,treatment);
        entity = (Entity)client;
        treatment.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.TREATMENT,
                Repository.PMSSQL.INSERT_TREATMENT, treatment);
        
        return treatment.getKey();
    }
    
    @Override
    public Integer insert(Transmission transmission)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        /*client = runSQL(Repository.EntityType.TREATMENT,
                    Repository.PMSSQL.READ_TREATMENT_NEXT_HIGHEST_KEY,transmission);
        entity = (Entity)client;
        transmission.setKey(entity.getValue().x + 1);*/

        runSQL(Repository.EntityType.TRANSMISSION,
                Repository.PMSSQL.INSERT_TRANSMISSION, transmission);
        
        return transmission.getKey();
    }
    
     @Override
    public Integer insert(Question question)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.QUESTION,
                    Repository.PMSSQL.READ_QUESTION_NEXT_HIGHEST_KEY,question);
        entity = (Entity)client;
        question.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.QUESTION,
                Repository.PMSSQL.INSERT_QUESTION, question);
        
        return question.getKey();
    }
    
    @Override
    public Integer insert(PatientCondition patientCondition)throws StoreException{
        
        if (patientCondition.getIsPatientPrimaryCondition()){
            runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION,
                    Repository.PMSSQL.INSERT_PATIENT_PRIMARY_CONDITION, patientCondition);
        }
        else if(patientCondition.getIsPatientSecondaryCondition()){
            runSQL(Repository.EntityType.PATIENT_SECONDARY_CONDITION,
                    Repository.PMSSQL.INSERT_PATIENT_SECONDARY_CONDITION, patientCondition);
        }
        else {
            String message = "Unexpected data type encountered in "
                    + "Repository.create(PatientCondition)";
            throw new StoreException(message,
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
        
        return null;
    }

    @Override
    public Integer insert(AppointmentTreatment appointmentTreatment)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        /*
        client = runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,
                    Repository.PMSSQL.READ_APPOINTMENT_TREATMENT_NEXT_HIGHEST_KEY,appointmentTreatment);
        entity = (Entity)client;
        appointmentTreatment.setKey(entity.getValue().x + 1);*/

        runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,
                Repository.PMSSQL.INSERT_APPOINTMENT_TREATMENT, appointmentTreatment);
        
        return null;
    }
    
    @Override
    public Integer insert(PatientQuestion appointmentQuestion)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;

        runSQL(Repository.EntityType.PATIENT_QUESTION,
                Repository.PMSSQL.INSERT_PATIENT_QUESTION, appointmentQuestion);
        
        return null;
    }
    
    @Override
    public Integer insert(PrimaryCondition pc)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.PRIMARY_CONDITION,
                    Repository.PMSSQL.READ_PRIMARY_CONDITION_NEXT_HIGHEST_KEY,pc);
        entity = (Entity)client;
        pc.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.PRIMARY_CONDITION,
                Repository.PMSSQL.INSERT_PRIMARY_CONDITION, pc);
        
        return pc.getKey();
    }
    
    @Override
    public Integer insert(SecondaryCondition sc)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.SECONDARY_CONDITION,
                    Repository.PMSSQL.READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY,sc);
        entity = (Entity)client;
        sc.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.SECONDARY_CONDITION,
                Repository.PMSSQL.INSERT_SECONDARY_CONDITION, sc);
        
        return sc.getKey();
    }
   
    /*@Override
    public void insert(ArchivedPatient ap) throws StoreException{
        runSQL(Repository.EntityType.ARCHIVED_PATIENT,
                Repository.PMSSQL.INSERT_ARCHIVED_PATIENT, ap);
    }*/
    
    /*@Override
    public void insert(PatientAppointmentData pad) throws StoreException{
        runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA,
                Repository.PMSSQL.INSERT_PATIENT_APPOINTMENT_DATA, pad);
    }*/
    
    /**
     * method supports insertion of patient records with pre-defined key value (data migration app mode), and without pre-defined key values (PMS mode of app)
     * -- the Patient.getIsKeyDefined() method determines if the app is in data migration mode or not
     * -- in PMS app mode the method calculates the next highest key value to use for the insertion
     * @param patient Patient
     * @param patientKey Integer key value of patient; null if new patient, not null if case of import of migrated data
     * @param guardianKey Integer key value patient guardian; note if not null patientKey should be null
     * @throws StoreException 
     * @return Integer specifying the key value of the new value created
     */
    @Override
    public Integer insert(Patient patient, Integer patientKey, Integer guardianKey) throws StoreException{ 
        Entity entity;
        IStoreClient client;
        Integer result = null;
        Patient guardian = null;
        if (patientKey==null){
            if (patient.getIsGuardianAPatient()){
                if (guardianKey==null){
                    throw new StoreException("StoreException raised in AccessRepository.insert(Patient...) because expected a non null guardian key value.",
                    StoreException.ExceptionType.UNEXPECTED_NULL_GUARDIAN_KEY);
                }
                else{
                    guardian = patient.getGuardian();
                    guardian.setKey(guardianKey);
                }
            }
            else{
                //gDelegate = new Patient(0);
                //gDelegate.setPatientKey(0);
            }
            client = runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.READ_PATIENT_NEXT_HIGHEST_KEY, new Patient());
            entity = (Entity)client;
            if (entity.getValue()!=null)
                patient.setKey(entity.getValue().x + 1);
        }else{
            patient.setKey(patientKey);
            //gDelegate = new Patient(0);
            //gDelegate.setPatientKey(0);
        }
        if (guardian!=null) patient.setGuardian(guardian);           
        runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.INSERT_PATIENT, patient);
        result =  patient.getKey();
        return result;
    }
   
    @Override
    public void recover(Appointment appointment, Integer appointmentKey)throws StoreException{
        switch(appointment.getScope()){
            case DELETED:
               //AppointmentDelegate delegate = new AppointmentDelegate(appointment);
               //delegate.setAppointmentKey(appointmentKey);
               runSQL(Repository.EntityType.APPOINTMENT,Repository.PMSSQL.RECOVER_APPOINTMENT, appointment);
               break;
            default:
                String error = "Scope of recovery not defined as DELETED; "
                        + "raised in Repository.recover(Appointment)";
                throw new StoreException(error, 
                        StoreException.ExceptionType.STORE_EXCEPTION);
        }
    } 
    
    @Override
    public void recover(Patient patient, Integer patientKey)throws StoreException{
       switch(patient.getScope()){
            case DELETED:
               runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.RECOVER_PATIENT, patient);
               break;
            default:
                String error = "Scope of recovery not defined as DELETED; "
                        + "raised in Repository.recover(Patient)";
                throw new StoreException(error, 
                        StoreException.ExceptionType.STORE_EXCEPTION);
       }
   }
    
    /*28/03/2024
    public void recover(PatientNote patientNote, Integer patientNoteKey)throws StoreException{
        switch(patientNote.getScope()){
             case DELETED:
                PatientNoteDelegate delegate = 
                        new PatientNoteDelegate();
                delegate.setKey(patientNoteKey);
                runSQL(Repository.EntityType.PATIENT_NOTE,Repository.PMSSQL.RECOVER_PATIENT_NOTE, delegate);
                break;
             default:
                 String error = "Scope of recovery not defined as DELETED; "
                         + "raised in Repository.recover(PatientNote)";
                 throw new StoreException(error, 
                         StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }*/
    
    @Override
    public void recover(Notification patientNotification, 
            Integer patientNotificationKey)throws StoreException{
        switch(patientNotification.getScope()){
             case DELETED:
                runSQL(Repository.EntityType.PATIENT_NOTIFICATION,Repository.PMSSQL.RECOVER_NOTIFICATION, patientNotification);
                break;
             default:
                 String error = "Scope of recovery not defined as DELETED; "
                         + "raised in Repository.recover(PatientNotification)";
                 throw new StoreException(error, 
                         StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }

    @Override
    public void delete(Patient patient, Integer patientKey)throws StoreException{
        switch(patient.getScope()){
            case SINGLE:
                runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.DELETE_PATIENT, patient);
                break;
            case ALL: //migration data function only
                runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.DELETE_ALL_PATIENT, null);
        }
    }
    
    /*@Override
    public void delete(ArchivedPatient ap)throws StoreException{
                runSQL(Repository.EntityType.ARCHIVED_PATIENT,Repository.PMSSQL.DELETE_ALL_ARCHIVED_PATIENT, null);
    }*/
    
    /*@Override
    public void delete(PatientAppointmentData pad)throws StoreException{
                runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA,Repository.PMSSQL.DELETE_ALL_PATIENT_APPOINTMENT_DATA, null);
    }*/
   
    @Override
    public void delete(PrimaryCondition primaryCondition)throws StoreException{
        if (primaryCondition.getScope()!=null){
            switch(primaryCondition.getScope()){
               
               case SINGLE:
                   runSQL(Repository.EntityType.PRIMARY_CONDITION,Repository.PMSSQL.DELETE_PRIMARY_CONDITION,primaryCondition);
                   break;
               case ALL:
                   runSQL(Repository.EntityType.PRIMARY_CONDITION,Repository.PMSSQL.DELETE_ALL_PRIMARY_CONDITION,null);
                   break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + primaryCondition.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(PrimaryCondition)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of primary condition delete operation undefined (" 
                    + primaryCondition.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(PrimaryCondition)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(SecondaryCondition secondaryCondition)throws StoreException{
        if (secondaryCondition.getScope()!=null){
            switch(secondaryCondition.getScope()){
                case SINGLE:
                    runSQL(Repository.EntityType.SECONDARY_CONDITION,Repository.PMSSQL.DELETE_SECONDARY_CONDITION,secondaryCondition);
                    break;
                case ALL:
                    runSQL(Repository.EntityType.SECONDARY_CONDITION,Repository.PMSSQL.DELETE_ALL_SECONDARY_CONDITION,null);
                    break;
                default:
                    String error = "Unexpected scope encountered (" 
                            + secondaryCondition.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(SecondaryCondition)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of secondary condition delete operation undefined (" 
                    + secondaryCondition.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(SecondaryCondition)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(Doctor doctor)throws StoreException{
        if (doctor.getScope()!=null){
            switch(doctor.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.DELETE_DOCTOR,doctor);
                    break;
               case ALL:
                   runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.DELETE_ALL_DOCTOR,null);
                   break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + doctor.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(Doctor)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of doctor delete operation undefined (" 
                    + doctor.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(Doctor)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(User _user)throws StoreException{
        if (_user.getScope()!=null){
            switch(_user.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.DELETE_USER,_user);
                    break;
               case ALL:
                   runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.DELETE_ALL_USER,null);
                   break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + _user.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(User)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of _user delete operation undefined (" 
                    + _user.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(User)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(UserSettings _userSettings)throws StoreException{
        if (_userSettings.getScope()!=null){
            switch(_userSettings.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.DELETE_USER_SETTINGS,_userSettings);
                    break;
               case ALL:
                   runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.DELETE_ALL_USER_SETTINGS,null);
                   break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + _userSettings.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(UserSettings)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of _userSettings delete operation undefined (" 
                    + _userSettings.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(UserSettings)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(Medication medication)throws StoreException{
        if (medication.getScope()!=null){
            switch(medication.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.MEDICATION,Repository.PMSSQL.DELETE_MEDICATION,medication);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.MEDICATION,Repository.PMSSQL.DELETE_ALL_MEDICATION,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + medication.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(Medication)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of medication delete operation undefined (" 
                    + medication.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(Medication)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(TreatmentCost treatmentCost)throws StoreException{
        if (treatmentCost.getScope()!=null){
            switch(treatmentCost.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.TREATMENT_COST,Repository.PMSSQL.DELETE_TREATMENT_COST,treatmentCost);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.TREATMENT_COST,Repository.PMSSQL.DELETE_ALL_TREATMENT_COST,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + treatmentCost.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(TreatmentCost)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of treatmentCost delete operation undefined (" 
                    + treatmentCost.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(TreatmentCost)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(Invoice invoice)throws StoreException{
        if (invoice.getScope()!=null){
            switch(invoice.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.INVOICE,Repository.PMSSQL.DELETE_INVOICE,invoice);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.INVOICE,Repository.PMSSQL.DELETE_ALL_INVOICE,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + invoice.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(Invoice)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of invoice delete operation undefined (" 
                    + invoice.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(Invoice)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(ClinicalNote clinicNote)throws StoreException{
        if (clinicNote.getScope()!=null){
            switch(clinicNote.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.CLINICAL_NOTE,Repository.PMSSQL.DELETE_CLINICAL_NOTE,clinicNote);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.CLINICAL_NOTE,Repository.PMSSQL.DELETE_ALL_CLINICAL_NOTE,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + clinicNote.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(ClinicNote)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of clinicNote delete operation undefined (" 
                    + clinicNote.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(ClinicNote)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(Treatment treatment)throws StoreException{
        if (treatment.getScope()!=null){
            switch(treatment.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.TREATMENT,Repository.PMSSQL.DELETE_TREATMENT,treatment);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.TREATMENT,Repository.PMSSQL.DELETE_ALL_TREATMENT,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + treatment.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(Treatment)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of treatment delete operation undefined (" 
                    + treatment.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(Treatment)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override 
    public void delete(Transmission transmission) throws StoreException{
        
    }
    
   
    @Override
    public void delete(Question question)throws StoreException{
        if (question.getScope()!=null){
            switch(question.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.QUESTION,Repository.PMSSQL.DELETE_QUESTION,question);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.QUESTION,Repository.PMSSQL.DELETE_ALL_QUESTION,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + question.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(Question)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of question delete operation undefined (" 
                    + question.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(Question)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(AppointmentTreatment appointmentTreatment)throws StoreException{
        if (appointmentTreatment.getScope()!=null){
            switch(appointmentTreatment.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,Repository.PMSSQL.DELETE_APPOINTMENT_TREATMENT,appointmentTreatment);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,Repository.PMSSQL.DELETE_ALL_APPOINTMENT_TREATMENT,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + appointmentTreatment.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(AppointmentTreatment)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of appointmentTreatment delete operation undefined (" 
                    + appointmentTreatment.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(AppointmentTreatment)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
     @Override
    public void delete(PatientQuestion patientQuestion)throws StoreException{
        if (patientQuestion.getScope()!=null){
            switch(patientQuestion.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.PATIENT_QUESTION,Repository.PMSSQL.DELETE_PATIENT_QUESTION,patientQuestion);
                    break;
               case ALL:
                    runSQL(Repository.EntityType.PATIENT_QUESTION,Repository.PMSSQL.DELETE_ALL_PATIENT_QUESTION,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + patientQuestion.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(PatientQuestion)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of patientQuestion delete operation undefined \n" 
                    + "Raised in Repository.delete(PatientQuestion)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(PatientCondition patientCondition)throws StoreException{
        boolean isError = false;
        Repository.EntityType entitySQL = null;
        Repository.PMSSQL sqlStatement = null;
        if (patientCondition.getIsPatientPrimaryCondition())
            entitySQL = Repository.EntityType.PATIENT_PRIMARY_CONDITION;
        else if(patientCondition.getIsPatientSecondaryCondition())
            entitySQL = Repository.EntityType.PATIENT_SECONDARY_CONDITION;
        else{
            isError = true;
            String message = "PatientCondition not defined properly\n"
                    + "StoreException raised in Repository.delete(PatientCondition)";
            throw new StoreException(message, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
        if(!isError){
            if (patientCondition.getScope()!=null){
                switch(patientCondition.getScope()){
                case SINGLE:
                    if (patientCondition.getIsPatientPrimaryCondition())
                        sqlStatement = Repository.PMSSQL.DELETE_PATIENT_PRIMARY_CONDITION;
                    else sqlStatement = Repository.PMSSQL.DELETE_PATIENT_SECONDARY_CONDITION;
                    break;
               case ALL:
                    if (patientCondition.getIsPatientPrimaryCondition())
                        sqlStatement = Repository.PMSSQL.DELETE_ALL_PATIENT_PRIMARY_CONDITION;
                    else sqlStatement = Repository.PMSSQL.DELETE_ALL_PATIENT_SECONDARY_CONDITION;
                    runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION,Repository.PMSSQL.DELETE_ALL_PATIENT_PRIMARY_CONDITION,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + patientCondition.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(PatientCondition)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
                }
                runSQL(entitySQL, sqlStatement, patientCondition);
            }else{
                String error = "Scope of patientCondition delete operation undefined (" 
                        + patientCondition.getScope().toString() + ")\n" 
                        + "Raised in Repository.delete(PatientCondition)";
                throw new StoreException(error, 
                        StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }

    }
    
   /**
    * Method 'deletes' the specified Notification record from the system via an update to the record's isDeleted field
    * @param patientNotification
    * @param key Integer specifying the PatiwentNotification record to be updated to a deleted status
    * @throws StoreException 
    */
   @Override
   public void delete(Notification patientNotification, Integer key)throws StoreException{
        //NotificationDelegate delegate = new NotificationDelegate(patientNotification);
        switch (patientNotification.getScope()){
            /*
            case SINGLE:
                if (key != null){
                    delegate.setKey(key);
                    runSQL(Repository.EntityType.PATIENT_NOTIFICATION,Repository.PMSSQL.DELETE_PATIENT_NOTIFICATION,delegate);
                }
                else{
                    String msg = "StoreException raised in method Repository::delete(Notification, Integer key)\n"
                            + "Cause -> null key value specified";
                    throw new StoreException(
                            msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                }
                break;
            */
            case ALL:
                runSQL(Repository.EntityType.PATIENT_NOTIFICATION,Repository.PMSSQL.DELETE_NOTIFICATIONS,null);
                break;
                /*
            case FOR_PATIENT:
                break;
                */
            default:
                String error = "Unexpected scope encountered (" 
                        + patientNotification.getScope().toString() + ")\n" 
                        + "Raised in Repository.delete(PatientNotification)";
                throw new StoreException(error, 
                        StoreException.ExceptionType.STORE_EXCEPTION);
        }
         
    }

    public void delete(SurgeryDaysAssignment surgeryDaysAssignment)throws StoreException{
       runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT,
               Repository.PMSSQL.DELETE_SURGERY_DAYS_ASSIGNMENT,null);
    }
   
    /**
     * 
     * @param appointment
     * @param Integer key
     * @throws StoreException 
     */
    @Override
    public void delete(Appointment appointment, Integer key) throws StoreException {
        // delegate = new AppointmentDelegate(appointment);
        switch(appointment.getScope()){
            
            case SINGLE:
                //delegate.setAppointmentKey(key);
                runSQL(Repository.EntityType.APPOINTMENT, 
                        Repository.PMSSQL.DELETE_APPOINTMENT, appointment);
                break;
            case EMERGENCY:
                //delegate.setAppointmentKey(key);
                runSQL(Repository.EntityType.APPOINTMENT, 
                        Repository.PMSSQL.DELETE_EMERGENCY_APPOINTMENT, appointment);
                break;
            case ALL:
                runSQL(Repository.EntityType.APPOINTMENT, 
                        Repository.PMSSQL.DELETE_APPOINTMENTS, null);
                break;
            default:
                String error = "Unexpected scope encountered (" 
                        + appointment.getScope().toString() + ")\n" 
                        + "Raised in Repository.delete(Appointment)";
                throw new StoreException(error, 
                        StoreException.ExceptionType.STORE_EXCEPTION);
        }
         
    }
    
    @Override
    public SurgeryDaysAssignment read(SurgeryDaysAssignment s) throws StoreException {
        SurgeryDaysAssignment surgeryDaysAssignment = null;
        Entity value = null;
        value = (Entity)runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT,Repository.PMSSQL.READ_SURGERY_DAYS_ASSIGNMENT, null);
        if (value != null) {
            if (value.getIsSurgeryDaysAssignment()) {
                surgeryDaysAssignment = (SurgeryDaysAssignment) value;
            }
        }
        return surgeryDaysAssignment;
            
    }
    
    @Override
    public PrimaryCondition read(PrimaryCondition pc)throws StoreException{
        PrimaryCondition result = null;
        Entity entity = null;

        switch(pc.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.PRIMARY_CONDITION, 
                            Repository.PMSSQL.READ_PRIMARY_CONDITION, pc);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(PrimryCondition)" 
                                + pc.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.PRIMARY_CONDITION,
                            Repository.PMSSQL.READ_ALL_PRIMARY_CONDITION, pc);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.PRIMARY_CONDITION,
                            Repository.PMSSQL.READ_PRIMARY_CONDITION_FOR_PATIENT,pc);
                break;
        }
        if (entity!=null){
            if (entity.getIsPrimaryCondition()){
                result = (PrimaryCondition)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(PrimaryCondition)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PrimaryCondition[" 
                        + pc.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public SecondaryCondition read(SecondaryCondition sc)throws StoreException{
        SecondaryCondition result = null;
        Entity entity = null;

        switch(sc.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.SECONDARY_CONDITION, 
                            Repository.PMSSQL.READ_SECONDARY_CONDITION, sc);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(PrimaryCondition)" 
                                + sc.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.SECONDARY_CONDITION,
                            Repository.PMSSQL.READ_ALL_SECONDARY_CONDITION, sc);
                break;
            case FOR_PRIMARY_CONDITION:
                entity = (Entity)runSQL(Repository.EntityType.SECONDARY_CONDITION,
                            Repository.PMSSQL.READ_SECONDARY_CONDITION_FOR_PRIMARY_CONDITION,sc);
                break;
        }
        if (entity!=null){
            if (entity.getIsSecondaryCondition()){
                result = (SecondaryCondition)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(SecondaryCondition)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(SecondaryCondition)" 
                        + sc.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public Medication read(Medication medication)throws StoreException{
        Medication result = null;
        Entity entity = null;

        switch(medication.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.MEDICATION, 
                            Repository.PMSSQL.READ_MEDICATION, medication);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Medication)" 
                                + medication.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.MEDICATION,
                            Repository.PMSSQL.READ_ALL_MEDICATION, medication);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.MEDICATION,
                            Repository.PMSSQL.READ_MEDICATION_FOR_PATIENT,medication);
                break;
        }
        if (entity!=null){
            if (entity.getIsMedication()){
                result = (Medication)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(Medication)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(Medication)" 
                        + medication.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public TreatmentCost read(TreatmentCost treatmentCost)throws StoreException{
        TreatmentCost result = null;
        Entity entity = null;

        switch(treatmentCost.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.TREATMENT_COST, 
                            Repository.PMSSQL.READ_TREATMENT_COST, treatmentCost);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(TreatmentCost)" 
                                + treatmentCost.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.TREATMENT_COST,
                            Repository.PMSSQL.READ_ALL_TREATMENT_COST, treatmentCost);
                break;
            case FOR_INVOICE:
                entity = (Entity)runSQL(Repository.EntityType.TREATMENT_COST,
                            Repository.PMSSQL.READ_TREATMENT_COST_FOR_INVOICE,treatmentCost);
                break;
        }
        if (entity!=null){
            if (entity.getIsTreatmentCost()){
                result = (TreatmentCost)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(TreatmentCost)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(TreatmentCost)" 
                        + treatmentCost.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public Invoice read(Invoice invoice)throws StoreException{
        Invoice result = null;
        Entity entity = null;

        switch(invoice.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.INVOICE, 
                            Repository.PMSSQL.READ_INVOICE, invoice);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Invoice)" 
                                + invoice.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.INVOICE,
                            Repository.PMSSQL.READ_ALL_INVOICE, invoice);
                break;
            case FOR_INVOICE:
                entity = (Entity)runSQL(Repository.EntityType.INVOICE,
                            Repository.PMSSQL.READ_INVOICE_FOR_PATIENT,invoice);
                break;
        }
        if (entity!=null){
            if (entity.getIsInvoice()){
                result = (Invoice)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(Invoice)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(Invoice)" 
                        + invoice.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public ClinicalNote read(ClinicalNote clinicNote)throws StoreException{
        ClinicalNote result = null;
        Entity entity = null;

        switch(clinicNote.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.CLINICAL_NOTE, 
                            Repository.PMSSQL.READ_CLINICAL_NOTE, clinicNote);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(ClinicNote)" 
                                + clinicNote.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case FOR_APPOINTMENT:
                try{
                    entity = (Entity)runSQL(Repository.EntityType.CLINICAL_NOTE, 
                            Repository.PMSSQL.READ_CLINICAL_NOTE_FOR_APPOINTMENT, clinicNote);
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreExcepion raised in Repository::read(ClinicNote, Scope = "
                            + clinicNote.getScope().toString() + ")";
                    throw new StoreException(
                        message,StoreException.ExceptionType.SQL_EXCEPTION);
                }
                break;
                
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.CLINICAL_NOTE,
                            Repository.PMSSQL.READ_ALL_CLINICAL_NOTE, clinicNote);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.CLINICAL_NOTE,
                            Repository.PMSSQL.READ_CLINIC_NOTE_FOR_PATIENT,clinicNote);
                break;
        }
        if (entity!=null){
            if (entity.getIsClinicNote()){
                result = (ClinicalNote)entity;
            }
        }
        return result;
    }
    
    @Override
    public Treatment read(Treatment treatment)throws StoreException{
        Treatment result = null;
        Entity entity = null;

        switch(treatment.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.TREATMENT, 
                            Repository.PMSSQL.READ_TREATMENT, treatment);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Treatment)" 
                                + treatment.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.TREATMENT,
                            Repository.PMSSQL.READ_ALL_TREATMENT, treatment);
                break;
        }
        if (entity!=null){
            if (entity.getIsTreatment()){
                result = (Treatment)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(Treatment)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(Treatment)" 
                        + treatment.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public Transmission read(Transmission transmission)throws StoreException{
        Transmission result = null;
        Entity entity = null;

        switch(transmission.getScope()){
            case PENDING_FOR_PATIENT:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.TRANSMISSION, 
                            Repository.PMSSQL.READ_PENDING_TRANSMISSIONS_FOR_PATIENT, transmission);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Transmission)" 
                                + transmission.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case PENDING:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.TRANSMISSION, 
                            Repository.PMSSQL.READ_PENDING_TRANSMISSIONS, transmission);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Transmission)" 
                                + transmission.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.TRANSMISSION,
                            Repository.PMSSQL.READ_ALL_TRANSMISSIONS, transmission);
                break;
        }
        if (entity!=null){
            if (entity.getIsTransmission()){
                result = (Transmission)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(Transmission)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(Transmission)" 
                        + transmission.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public Question read(Question question)throws StoreException{
        Question result = null;
        Entity entity = null;

        switch(question.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.QUESTION, 
                            Repository.PMSSQL.READ_QUESTION, question);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Question)" 
                                + question.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case FOR_CATEGORY:
                entity = (Entity)runSQL(Repository.EntityType.QUESTION,
                            Repository.PMSSQL.READ_QUESTION_FOR_CATEGORY, question);
                break;
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.QUESTION,
                            Repository.PMSSQL.READ_ALL_QUESTION, question);
                break;
        }
        if (entity!=null){
            if (entity.getIsQuestion()){
                result = (Question)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(Question)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(Question)" 
                        + question.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public AppointmentTreatment read(AppointmentTreatment appointmentTreatment)throws StoreException{
        AppointmentTreatment result = null;
        Entity entity = null;

        switch(appointmentTreatment.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.APPOINTMENT_TREATMENT, 
                            Repository.PMSSQL.READ_APPOINTMENT_TREATMENT, appointmentTreatment);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(AppointmentTreatment)" 
                                + appointmentTreatment.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,
                            Repository.PMSSQL.READ_ALL_APPOINTMENT_TREATMENT, appointmentTreatment);
                break;
            case FOR_APPOINTMENT:
                entity = (Entity)runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,
                            Repository.PMSSQL.READ_APPOINTMENT_TREATMENT_FOR_APPOINTMENT, appointmentTreatment);
                break;
            case FOR_TREATMENT:
                entity = (Entity)runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,
                            Repository.PMSSQL.READ_APPOINTMENT_TREATMENT_FOR_TREATMENT, appointmentTreatment);
                break;
        }
        if (entity!=null){
            if (entity.getIsAppointmentTreatment()){
                result = (AppointmentTreatment)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(AppointmentTreatment)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(AppointmentTreatment)" 
                        + appointmentTreatment.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public PatientQuestion read(PatientQuestion patientQuestion)throws StoreException{
        PatientQuestion result = null;
        Entity entity = null;

        switch(patientQuestion.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.PATIENT_QUESTION, 
                            Repository.PMSSQL.READ_PATIENT_QUESTION, patientQuestion);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(PatientQuestion)" 
                                + patientQuestion.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_QUESTION,
                            Repository.PMSSQL.READ_ALL_PATIENT_QUESTION, patientQuestion);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_QUESTION,
                            Repository.PMSSQL.READ_PATIENT_QUESTION_FOR_PATIENT, patientQuestion);
                break;
            case FOR_QUESTION:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_QUESTION,
                            Repository.PMSSQL.READ_PATIENT_QUESTION_FOR_QUESTION, patientQuestion);
                break;
        }
        if (entity!=null){
            if (entity.getIsPatientQuestion()){
                result = (PatientQuestion)entity;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(PatientQuestion)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }/*else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PatientQuestion)" 
                        + patientQuestion.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }*/
        return result;
    }
    
    @Override
    public PatientCondition read(PatientCondition patientCondition)throws StoreException{
        PatientCondition result = null;
        Entity entity = null;
        Repository.EntityType entitySQL = null;
        Repository.PMSSQL sqlStatement = null;
        if(patientCondition.getIsPatientPrimaryCondition())
            entitySQL = Repository.EntityType.PATIENT_PRIMARY_CONDITION;
        else entitySQL = Repository.EntityType.PATIENT_SECONDARY_CONDITION;
        
        switch(patientCondition.getScope()){
            case SINGLE:
                if(patientCondition.getIsPatientPrimaryCondition())
                    sqlStatement = Repository.PMSSQL.READ_PATIENT_PRIMARY_CONDITION;
                else sqlStatement = Repository.PMSSQL.READ_PATIENT_SECONDARY_CONDITION;
                break;
            case ALL:
                if(patientCondition.getIsPatientPrimaryCondition())
                    sqlStatement = Repository.PMSSQL.READ_ALL_PATIENT_PRIMARY_CONDITION;
                else sqlStatement = Repository.PMSSQL.READ_ALL_PATIENT_SECONDARY_CONDITION;
                break;
            case FOR_PATIENT:
                if(patientCondition.getIsPatientPrimaryCondition())
                    sqlStatement = Repository.PMSSQL.READ_PATIENT_PRIMARY_CONDITION_FOR_PATIENT;
                else sqlStatement = Repository.PMSSQL.READ_PATIENT_SECONDARY_CONDITION_FOR_PATIENT;
                break;
            case FOR_PRIMARY_CONDITION:
                sqlStatement = Repository.PMSSQL.READ_PATIENT_PRIMARY_CONDITION_FOR_PRIMARY_CONDITION;  
                break;
            case FOR_SECONDARY_CONDITION:
                sqlStatement = Repository.PMSSQL.READ_PATIENT_SECONDARY_CONDITION_FOR_SECONDARY_CONDITION; 
                break;
        }

        entity = (Entity)runSQL(entitySQL, sqlStatement, patientCondition);
        
        if (entity!=null){
            result = (PatientCondition)entity;
        }
        /*
        else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PatientCondition, [case = " 
                        + sqlStatement.toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
        */
        return result;
    }

    @Override
    public Doctor read(Doctor doctor)throws StoreException{
        Doctor result = null;
        Entity entity = null;

        switch(doctor.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.DOCTOR, 
                            Repository.PMSSQL.READ_DOCTOR, doctor);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(Doctor)" 
                                + doctor.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.DOCTOR,
                            Repository.PMSSQL.READ_ALL_DOCTOR, doctor);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.DOCTOR,
                            Repository.PMSSQL.READ_DOCTOR_FOR_PATIENT,doctor);
                break;
        }
        if (entity!=null){
            if (entity.getIsDoctor()){
                result = (Doctor)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(Doctor)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(Doctor)" 
                        + doctor.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public UserSettings read(UserSettings userSettings)throws StoreException{
        Entity entity;
        entity = (Entity)runSQL(Repository.EntityType.USER_SETTINGS,
                            Repository.PMSSQL.READ_USER_SETTINGS,userSettings);
        return (UserSettings)entity;
    }
    
    @Override
    public User read(User _user)throws StoreException{
        User result = null;
        Entity entity = null;
        byte[] salt = null;
        byte[] hashedPassword = null;
        int count = 0;

        switch(_user.getScope()){
            case WITH_NAME:
                    entity = (Entity)runSQL(Repository.EntityType.USER, 
                            Repository.PMSSQL.READ_USER, _user);
                break;
            case WITH_CREDENTIAL:{
                //SystemDefinition.userS = controller.PasswordUtilsx.generateSaltBytes();
                //controller.PasswordUtilsx.setSalt(salt);
                try{
                    //SystemDefinition.userP = controller.PasswordUtilsx.hashPassword("jeff", SystemDefinition.userS);
                    //boolean test = controller.PasswordUtilsx.isPasswordCorrect("jeff",SystemDefinition.userP);
                    //if (test) count = 1;
                
                
                    entity = (Entity)runSQL(Repository.EntityType.USER, 
                            Repository.PMSSQL.READ_USER, _user);
                    if (entity!=null){
                        //byte[] hashPassword = controller.PasswordUtilsx.hashPassword(_user.getPassword(), SystemDefinition.userS);
                        if(!colinhewlettsolutions.client.controller.PasswordUtils.isPasswordCorrect(_user.getPassword(),SystemDefinition.userP, SystemDefinition.userS)){
                            //if(!MessageDigest.isEqual(hashPassword, SystemDefinition.userP)){
                            LoginException loginException = new LoginException("security breach");
                            loginException.setLoginErrorType(LoginException.LoginExceptionType.MATCHING_PASSWORD_NOT_FOUND);
                            throw loginException;
                        }
                    }
                }catch(NoSuchAlgorithmException ex){
                    String message = ex.getMessage() + "\n"
                            + "Raised in Repository::read(User) using case 'WITH_NAME'";
                    displayErrorMessage(message,"Repository error",JOptionPane.WARNING_MESSAGE);
                }catch(InvalidKeySpecException ex){
                    String message = ex.getMessage() + "\n"
                            + "Raised in Repository::read(User) using case 'WITH_NAME'";
                    displayErrorMessage(message,"Repository error",JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
        }
        if(entity!=null){
            if (entity.getIsUser()){
                result = (User)entity;
            }
        }else result = null;
        return result;
    }
    
    /**
     * method fetches a collection of patient notifications from store
 -- to enable transfer of the owning patient's key value, a delegate class replaces the patient's in the Notification associated with the collection
 -- the specified collection object defines the scope of the required collection
 -- for each notification's patient only the key value is returned; its the responsibility of the caller to issue another read request per notification to fetch the patient's other details, if this is necessary 
     * @param patientNotificationCollection
     * @param key, if the requested collection is for a specific patient
 -- this is the key value of the owning patient in the associated Notification from which a delagate class will be constructed
 -- if not a patient-based collection the key value is null
     * @return
     * @throws StoreException 
     */
    @Override
    public Notification read(Notification patientNotification, Integer key)throws StoreException{
        Notification result = null;
        Entity entity = null;
        IStoreClient client;
        switch(patientNotification.getScope()){
            case SINGLE:
                //NotificationDelegate patientNotificationDelegate = new NotificationDelegate(patientNotification);
                //patientNotificationDelegate.setKey(key);
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_NOTIFICATION, 
                            Repository.PMSSQL.READ_NOTIFICATION, 
                            patientNotification);
            case UNACTIONED:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_UNACTIONED_NOTIFICATIONS, 
                            patientNotification);
                break;
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_NOTIFICATIONS, 
                            patientNotification);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_NOTIFICATIONS_FOR_PATIENT, 
                            patientNotification);
                break;
            case DELETED_FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_DELETED_NOTIFICATIONS_FOR_PATIENT, 
                            patientNotification);
                break;
        }
        if (entity!=null){
                if (entity.getIsPatientNotification()){
                    result = (Notification)entity;

                    return result;
                }else{
                    String message = "";
                    throw new StoreException(
                            
                        message + "StoreException raised -> unexpected data type returned from persistent store "
                            + "in method Repository::read(PatientNotification.Collection)\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
                }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PatientNotification.Collection)\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }

    public void uncancel(Notification notification, Integer notificationKey)throws StoreException{
        //NotificationDelegate delegate = new NotificationDelegate(notification);
        //delegate.setKey(notificationKey);
        runSQL(Repository.EntityType.PATIENT_NOTIFICATION,Repository.PMSSQL.UNCANCEL_NOTIFICATION, notification);
    }
    
    public void uncancel(Appointment appointment, Integer appointmentKey)throws StoreException{
        //AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        //delegate.setAppointmentKey(appointmentKey);
        runSQL(Repository.EntityType.APPOINTMENT,Repository.PMSSQL.UNCANCEL_APPOINTMENT, appointment);
    }
    
    public void cancel(Notification notification, Integer notificationKey)throws StoreException{
        //NotificationDelegate delegate = new NotificationDelegate(notification);
        //delegate.setKey(notificationKey);
        runSQL(Repository.EntityType.PATIENT_NOTIFICATION,Repository.PMSSQL.CANCEL_NOTIFICATION, notification);
    }
    
    public void cancel(Appointment appointment, Integer appointmentKey)throws StoreException{
        //AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        //delegate.setAppointmentKey(appointmentKey);
        runSQL(Repository.EntityType.APPOINTMENT,Repository.PMSSQL.CANCEL_APPOINTMENT, appointment);
    }

    /*28/03/2024
    public Point count(PatientNote patientNote)throws StoreException{
        Entity result = null;
        if (patientNote !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_NOTES;
            result = (Entity)runSQL(Repository.EntityType.PATIENT_NOTE, sqlStatement, patientNote);
        }
        return result.getValue();
    }*/
    
    @Override
    public Point count(Doctor doctor)throws StoreException{
        Entity result = null;
        if (doctor !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_DOCTOR;
            result = (Entity)runSQL(Repository.EntityType.DOCTOR, 
                    sqlStatement, doctor);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(User _user)throws StoreException{
        Entity result = null;
        if (_user !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_USER;
            result = (Entity)runSQL(Repository.EntityType.USER, 
                    sqlStatement, _user);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(UserSettings _userSettings)throws StoreException{
        Entity result = null;
        if (_userSettings !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_USER_SETTINGS;
            result = (Entity)runSQL(Repository.EntityType.USER_SETTINGS, 
                    sqlStatement, _userSettings);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(ClinicalNote clinicNote)throws StoreException{
        Entity result = null;
        if (clinicNote !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_CLINICAL_NOTE;
            result = (Entity)runSQL(Repository.EntityType.CLINICAL_NOTE, 
                    sqlStatement, clinicNote);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(Treatment treatment)throws StoreException{
        Entity result = null;
        if (treatment !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_TREATMENT;
            result = (Entity)runSQL(Repository.EntityType.TREATMENT, 
                    sqlStatement, treatment);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(Transmission transmission)throws StoreException{
        Entity result = null;
        if (transmission !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_TRANSMISSION;
            result = (Entity)runSQL(Repository.EntityType.TRANSMISSION, 
                    sqlStatement, transmission);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(Question question)throws StoreException{
        Entity result = null;
        if (question !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_QUESTION;
            result = (Entity)runSQL(Repository.EntityType.QUESTION, 
                    sqlStatement, question);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(Invoice invoice)throws StoreException{
        Entity result = null;
        if (invoice !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_INVOICE;
            result = (Entity)runSQL(Repository.EntityType.INVOICE, 
                    sqlStatement, invoice);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(TreatmentCost treatmentCost)throws StoreException{
        Entity result = null;
        if (treatmentCost !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_TREATMENT_COST;
            result = (Entity)runSQL(Repository.EntityType.TREATMENT_COST, 
                    sqlStatement, treatmentCost);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(PatientQuestion patientQuestion)throws StoreException{
        Entity result = null;
        if (patientQuestion !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_QUESTION;
            result = (Entity)runSQL(Repository.EntityType.PATIENT_QUESTION, 
                    sqlStatement, patientQuestion);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(AppointmentTreatment appointmentTreatment)throws StoreException{
        Entity result = null;
        if (appointmentTreatment !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENT_TREATMENT;
            result = (Entity)runSQL(Repository.EntityType.APPOINTMENT_TREATMENT, 
                    sqlStatement, appointmentTreatment);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(PatientCondition patientCondition)throws StoreException{
        Entity result = null;
        Repository.EntityType entitySQL = null;
        Repository.PMSSQL sqlStatement = null;
        if(patientCondition.getIsPatientPrimaryCondition()){
            entitySQL = Repository.EntityType.PATIENT_PRIMARY_CONDITION;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_PRIMARY_CONDITION;
        }else if(patientCondition.getIsPatientSecondaryCondition()){
            entitySQL = Repository.EntityType.PATIENT_SECONDARY_CONDITION;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_SECONDARY_CONDITION;
        }else {
            String message = "Unexpected data type encountered in "
                    + "Repository.create(PatientCondition)";
            throw new StoreException(message,
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
        result = (Entity)runSQL(entitySQL, sqlStatement,patientCondition);
        return result.getValue();
    }
    /*
    @Override
    public Point count(PatientPrimaryCondition patientPrimaryCondition)throws StoreException{
        Entity result = null;
        if (patientPrimaryCondition !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_PRIMARY_CONDITION;
            result = (Entity)runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION, 
                    sqlStatement, patientPrimaryCondition);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(PatientSecondaryCondition patientSecondaryCondition)throws StoreException{
        Entity result = null;
        if (patientSecondaryCondition !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_SECONDARY_CONDITION;
            result = (Entity)runSQL(Repository.EntityType.PATIENT_SECONDARY_CONDITION, 
                    sqlStatement, patientSecondaryCondition);
        }
        return result.getValue();
    }
    */
    @Override
    public Point count(Medication medication)throws StoreException{
        Entity result = null;
        if (medication !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_MEDICATION;
            result = (Entity)runSQL(Repository.EntityType.MEDICATION, 
                    sqlStatement, medication);
        }
        return result.getValue();
    }
    
    public Point count(SecondaryCondition sc)throws StoreException{
        Entity result = null;
        if (sc !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_SECONDARY_CONDITION;
            result = (Entity)runSQL(Repository.EntityType.SECONDARY_CONDITION, 
                    sqlStatement, sc);
        }
        return result.getValue();
    }
    
    public Point count(PrimaryCondition pc)throws StoreException{
        Entity result = null;
        if (pc !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PRIMARY_CONDITION;
            result = (Entity)runSQL(Repository.EntityType.PRIMARY_CONDITION, 
                    sqlStatement, pc);
        }
        return result.getValue();
    }

    public Point count(Notification notification)throws StoreException{
        Entity result = null;
        if (notification !=null){
            Repository.PMSSQL sqlStatement = null;
            switch (notification.getScope()){
                case ALL:
                    sqlStatement = Repository.PMSSQL.COUNT_PATIENT_NOTIFICATIONS;
                    break;
                case UNACTIONED:
                    sqlStatement = Repository.PMSSQL.COUNT_UNACTIONED_NOTIFICATIONS;
                    break;
            }
            result = (Entity)runSQL(Repository.EntityType.PATIENT_NOTIFICATION, sqlStatement, notification);
            return result.getValue();
        }
        else throw new StoreException("PatientNotification undefined in Repository.count(PatientNotification)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }

    /**
     * On entry the method expects the caller's scope for the count to be defined which specifies what is being counted
     * -- all appointments
     * -- appointments on a given day
     * -- appointments from a given day
     * -- appointments for a given patient
     * @param appointment, identifies the persistent store entity to be counted 
     * @param appointeeKey, identifies the appointee if count for the number of appointments for a given patient
     * @return Integer, the total counted
     * @throws StoreException 
     */
    @Override
    public Point count(Appointment appointment, Integer appointeeKey) throws StoreException{
        Entity result;
        if (appointment != null){
            Repository.PMSSQL sqlStatement = null;
            switch(appointment.getScope()){
                case ALL:
                    sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENTS;
                    break;
                case FOR_DAY:
                    sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENTS_FOR_DAY;
                    break;
                case FOR_PATIENT:
                    sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENTS_FOR_PATIENT;
                    break;
                case FROM_DAY:
                    sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENTS_FROM_DAY;
                    break;       
            }
            result = (Entity)runSQL(Repository.EntityType.APPOINTMENT, sqlStatement, appointment );
            return result.getValue();
        }
        else throw new StoreException("Appointment undefined in Repository.count(Appointment)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }

    @Override
    public Point count(Patient patient)throws StoreException{
        Entity result;
        if(patient != null){
            result = (Entity)runSQL(Repository.EntityType.PATIENT, Repository.PMSSQL.COUNT_PATIENTS, patient );
            return result.getValue();
        }
        else throw new StoreException("Patient undefined in Repository.count(Patient)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }
    
    /*@Override
    public Point count(PatientAppointmentData pad)throws StoreException{
        Entity result;
        if(pad != null){
            result = (Entity)runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA, Repository.PMSSQL.COUNT_PATIENT_APPOINTMENT_DATA, pad );
            return result.getValue();
        }
        else throw new StoreException("PatientAppointmentData undefined in Repository.count(PatientAppointmentData)",
                StoreException.ExceptionType.NULL_KEY_EXCEPTION);
    }*/
    
    /*@Override
    public Point count(ArchivedPatient ap)throws StoreException{
        Entity result;
        if(ap != null){
            result = (Entity)runSQL(Repository.EntityType.ARCHIVED_PATIENT, Repository.PMSSQL.COUNT_ARCHIVED_PATIENT, ap );
            return result.getValue();
        }
        else throw new StoreException("ArchivedPatient undefined in Repository.count(PatientAppointmentData)",
                StoreException.ExceptionType.NULL_KEY_EXCEPTION);
    }*/
    
    @Override
    public Point count(SurgeryDaysAssignment surgeryDaysAssignment)throws StoreException{
        Entity result;
        if (surgeryDaysAssignment != null){
            result = (Entity)runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT, 
                    Repository.PMSSQL.COUNT_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment );
            return result.getValue();
        }
        else throw new StoreException("SurgeryDaysAssignment undefined in Repository.count(SurgeryDaysAssignment)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }
    
    @Override
    public void create(Invoice table) throws StoreException { 
        Entity value = table;
        runSQL(Repository.EntityType.INVOICE,Repository.PMSSQL.CREATE_INVOICE_TABLE, value);
    }
    
    @Override
    public void create(TreatmentCost table) throws StoreException { 
        Entity value = table;
        runSQL(Repository.EntityType.TREATMENT_COST,Repository.PMSSQL.CREATE_TREATMENT_COST_TABLE, value);
    }
    
    @Override
    public void create(Appointment table) throws StoreException { 
        Entity value = null;
        runSQL(Repository.EntityType.APPOINTMENT,Repository.PMSSQL.CREATE_APPOINTMENT_TABLE, value);
    }
    
    @Override
    public void create(Patient table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.CREATE_PATIENT_TABLE, value);
    }
    
    /*@Override
    public void create(PatientAppointmentData table) throws StoreException{
        Entity value = null;
       runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA,Repository.PMSSQL.CREATE_PATIENT_APPOINTMENT_DATA_TABLE, value);
    }*/
    
    /*@Override
    public void create(ArchivedPatient table) throws StoreException{
        Entity value = null;
       runSQL(Repository.EntityType.ARCHIVED_PATIENT,Repository.PMSSQL.CREATE_ARCHIVED_PATIENT_TABLE, value);
    }*/
    
    @Override
    public void create(Doctor table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.DOCTOR,Repository.PMSSQL.CREATE_DOCTOR_TABLE, value);
    }
    
    @Override
    public void create(User table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.USER,Repository.PMSSQL.CREATE_USER_TABLE, value);
    }
    
    @Override
    public void create(UserSettings table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.USER_SETTINGS,Repository.PMSSQL.CREATE_USER_SETTINGS_TABLE, value);
    }
    
    @Override
    public void create(PrimaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.PRIMARY_CONDITION,Repository.PMSSQL.CREATE_PRIMARY_CONDITION_TABLE, value);
    }
    
    @Override
    public void create(SecondaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.SECONDARY_CONDITION,Repository.PMSSQL.CREATE_SECONDARY_CONDITION_TABLE, value);
    }
    
    @Override
    public void create(ClinicalNote table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.CLINICAL_NOTE,Repository.PMSSQL.CREATE_CLINICAL_NOTE_TABLE, value);
    }
    
    @Override
    public void create(Treatment table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.TREATMENT,Repository.PMSSQL.CREATE_TREATMENT_TABLE, value);
    }
    
    @Override
    public void create(Transmission table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.TREATMENT,Repository.PMSSQL.CREATE_TREATMENT_TABLE, value);
    }
    
    @Override
    public void create(Question table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.QUESTION,Repository.PMSSQL.CREATE_QUESTION_TABLE, value);
    }
    
    @Override
    public void create(AppointmentTreatment table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.APPOINTMENT_TREATMENT,Repository.PMSSQL.CREATE_APPOINTMENT_TREATMENT_TABLE, value);
    }
    
    @Override
    public void create(PatientQuestion table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.PATIENT_QUESTION,Repository.PMSSQL.CREATE_PATIENT_QUESTION_TABLE, value);
    }
    
    @Override
    public void create(PatientCondition table) throws StoreException{
        Entity value = null;
        
        if (table.getIsPatientPrimaryCondition()){
            runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION,
                    Repository.PMSSQL.CREATE_PATIENT_PRIMARY_CONDITION_TABLE, value);
        }
        else if(table.getIsPatientSecondaryCondition()){
            runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION,
                    Repository.PMSSQL.CREATE_PATIENT_PRIMARY_CONDITION_TABLE, value);
        }
        else {
            String message = "Unexpected data type encountered in "
                    + "Repository.create(PatientCondition)";
            throw new StoreException(message,
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
            
        
    }
    /*
    @Override
    public void create(PatientPrimaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION,Repository.PMSSQL.CREATE_PATIENT_PRIMARY_CONDITION_TABLE, value);
    }
    
    @Override
    public void create(PatientSecondaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.PATIENT_SECONDARY_CONDITION,Repository.PMSSQL.CREATE_PATIENT_SECONDARY_CONDITION_TABLE, value);
    }
    */
    @Override
    public void create(Medication table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.MEDICATION,Repository.PMSSQL.CREATE_MEDICATION_TABLE, value);
    }
    
    @Override
    public void create(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
            runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT,Repository.PMSSQL.CREATE_SURGERY_DAYS_ASSIGNMENT_TABLE, surgeryDaysAssignment);
    }
    
    @Override
    public Appointment read(Appointment appointment, Integer key)throws StoreException{
        boolean isAppointmentsForDay = false;
        //AppointmentDelegate appointmentDelegate = null;
        Patient patient = null;
        Entity result = null;
        Repository.PMSSQL sqlStatement = null;
        switch(appointment.getScope()){
            case CANCELLED:
                result = (Entity)runSQL(Repository.EntityType.APPOINTMENT, Repository.PMSSQL.READ_CANCELLED_APPOINTMENTS, appointment);
                return (Appointment)result;
            case SINGLE:
                //appointmentDelegate = new AppointmentDelegate();
                //appointmentDelegate.setAppointmentKey(key);
                result = (Entity)runSQL(Repository.EntityType.APPOINTMENT, Repository.PMSSQL.READ_APPOINTMENT, appointment);
                return (Appointment)result;
            case ALL:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS;
                break;
            case FOR_DAY:
            case FOR_DAY_AND_NON_EMERGENCY_APPOINTMENT:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FOR_DAY;
                isAppointmentsForDay = true;
                break;
            case FOR_DAY_AND_EMERGENCY_APPOINTMENT:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FOR_DAY_AND_EMERGENCY_APPOINTMENT;
                isAppointmentsForDay = true;
                break;
            case DELETED_FOR_PATIENT:
                patient = new Patient(key);
                appointment.setPatient(patient);
                sqlStatement = Repository.PMSSQL.READ_DELETED_APPOINTMENTS_FOR_PATIENT;
                break;
            case FOR_PATIENT:
                patient = new Patient(key);
                appointment.setPatient(patient);
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FOR_PATIENT;
                break;
            case FROM_DAY:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FROM_DAY;
                break; 
            case FOR_INVOICE:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FOR_INVOICE;
                break;
        }
        result = (Entity)runSQL(Repository.EntityType.APPOINTMENT, sqlStatement, appointment);
        if (isAppointmentsForDay){//subsequent patient read required to return initialised state for appointee per appointment in collection
            
            Iterator<Appointment> it = ((Appointment)result).get().iterator();
            while (it.hasNext()){
                Appointment a = it.next();
                patient = new Patient(((Patient)a.getPatient()).getKey());
                patient.setScope(Entity.Scope.SINGLE);
                //01/03/2023
                //a.setPatient(patient.read());
            }
        }
        return (Appointment)result;
    }

    @Override
    public Patient read(Patient patient, Integer key) throws StoreException { 
        if (patient != null) {
            Entity entity = null;
            switch (patient.getScope()){
                case SINGLE:
                    entity = (Entity)runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.READ_PATIENT, patient);
                    if (entity == null) {
                        throw new StoreException(
                                "Could not locate requested patient in "
                                        + "Repository::read(Patient, Integer key)",
                                StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                    }
                    return (Patient)entity;
                case DELETED:
                    entity = (Entity)runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.READ_DELETED_PATIENTS, patient);
                    if (entity == null) {
                        throw new StoreException(
                                "Could not locate requested deleted patient in "
                                        + "Repository::read(Patient, Integer key)",
                                StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                    }
                    return (Patient)entity;
                case ARCHIVED:
                    entity = (Entity)runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.READ_ARCHIVED_PATIENTS, patient);
                    if (entity == null) {
                        throw new StoreException(
                                "Could not locate requested deleted patient in "
                                        + "Repository::read(Patient, Integer key)",
                                StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                    }
                    return (Patient)entity;
                default:
                    entity = (Entity)runSQL(Repository.EntityType.PATIENT,Repository.PMSSQL.READ_PATIENTS,patient);
                    if (entity!=null){
                        if (entity.getIsPatient()){
                            patient = (Patient)entity;
                            return patient;
                        }else{
                            throw new StoreException(
                                "StoreException raised -> unexpected data type returned from persistent store "
                                    + "in method Repository::read(Patient, Integer key)\n",
                                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
                        }
                    }else{
                        throw new StoreException(
                            "StoreException raised -> null value returned from persistent store "
                                + "in method Repository::read(Patient.Collection)\n",
                            StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                    }  
            }   
        }
        else{
            throw new StoreException("StoreException raised because Patient object uninitialised on entry to Repository::read(Patient, ...)",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
    }
    
    /*@Override
    public ArchivedPatient read(ArchivedPatient ap) throws StoreException{
        Entity entity = null;
        switch (ap.getScope()){
            case BY_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.ARCHIVED_PATIENT,Repository.PMSSQL.READ_ARCHIVED_PATIENT_BY_PATIENT,ap);
                break;
            case BY_LAST_APPOINTMENT_DATE:
                entity = (Entity)runSQL(Repository.EntityType.ARCHIVED_PATIENT,Repository.PMSSQL.READ_ARCHIVED_PATIENT_BY_LAST_APPOINTMENT_DATE,ap);
                break;
            default:
                String message = "Unexpected read scope encountered (" + ap.getScope().toString() +")\n";
                message = message = "StoreEXception raised in " + this.getClass().getSimpleName() + "::read(PatientAppointmentData)";
                throw new StoreException(message, StoreException.ExceptionType.UNEXPECTED_READ_SCOPE_ENCOUNTERED);
        }
        if (entity!=null){
            if (entity.getIsArchivedPatient()){
                ap = (ArchivedPatient)entity;
                return ap;
            }else{
                throw new StoreException(
                    "StoreException raised -> unexpected data type returned from persistent store "
                        + "in method " + this.getClass().getSimpleName() + "::read(PatientAppointmentData)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            throw new StoreException(
                "StoreException raised -> null value returned from persistent store "
                    + "in method " + this.getClass().getSimpleName() + "::read(PatientAppointmentData)\n",
                StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }  
    }*/
    
    @Override
    public PatientAppointmentData read(PatientAppointmentData pad) throws StoreException{
        Entity entity = null;
        switch (pad.getScope()){
            case ARCHIVED:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA,Repository.PMSSQL.READ_ARCHIVED_PATIENT_APPOINTMENT_DATA,pad);
                break;
            case PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA,Repository.PMSSQL.READ_PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT,pad);
                break;
            case PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT:
                entity = (Entity)runSQL(Repository.EntityType.PATIENT_APPOINTMENT_DATA,Repository.PMSSQL.READ_PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT,pad);
                break;
            default:
                
                break;
        }
        if (entity!=null){
            if (entity.getIsPatientAppointmentData()){
                pad = (PatientAppointmentData)entity;
                return pad;
            }else{
                throw new StoreException(
                    "StoreException raised -> unexpected data type returned from persistent store "
                        + "in method " + this.getClass().getSimpleName() + "::read(PatientAppointmentData)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            throw new StoreException(
                "StoreException raised -> null value returned from persistent store "
                    + "in method " + this.getClass().getSimpleName() + "::read(PatientAppointmentData)\n",
                StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }  
    }
    
    @Override
    /**
     * update appointment method adopts the delegate mechanism to transfer two key values to store
     * @param appointment
     * @param key Integer value of the appointment key 
     * @param appointeeKey Integer value of the appointment's appointee key
     * @throws StoreException 
     */
    public void update(Appointment appointment, Integer key, Integer appointeeKey /*,28/03/2024Integer patientNoteKey*/) throws StoreException {
        //AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        //Patient pDelegate = new Patient(delegate.getPatient());
        /*28/03/2024PatientNoteDelegate noteDelegate = new PatientNoteDelegate();
        noteDelegate.setKey(patientNoteKey);*/
        //delegate.setAppointmentKey(key);
        //pDelegate.setPatientKey(appointeeKey);
        //delegate.setPatient(pDelegate);
        /*28/03/2024delegate.setPatientNote(noteDelegate);*/
        runSQL(Repository.EntityType.APPOINTMENT, Repository.PMSSQL.UPDATE_APPOINTMENT, appointment);

    }
    
    @Override
    public List<String[]> importEntityFromCSV(Entity entity) throws StoreException{
        List<String[]> result = null;
        if (entity.getIsAppointment()) {
            result = new CSVReader().getAppointmentDBFRecords(SystemDefinition.getPMSImportedAppointmentData());
            
        }
        if (entity.getIsPatient()) {
            result = new CSVReader().getPatientDBFRecords(SystemDefinition.getPMSImportedPatientData());
            
            
        }
        
        return result;
    }
    
    /**
     * method sends the specified pn to persistent store
     * @param pn, Notification to be updated
     * @param key, Integer this patoent notification's pid
     * @param patientKey, Integer the associated patients's pid
     * @throws StoreException if exception arises in transaction control
     */
    @Override
    public void update(Notification pn, Integer key, Integer patientKey)throws StoreException{
        //NotificationDelegate delegate = new NotificationDelegate(pn);
        //Patient pDelegate = new Patient(patientKey);
        //delegate.setKey(key);
        //delegate.setPatient(pDelegate);
        runSQL(Repository.EntityType.PATIENT_NOTIFICATION, Repository.PMSSQL.UPDATE_NOTIFICATION,pn);
        
    }

    @Override
    public void update(PatientCondition patientCondition)throws StoreException{
        if (patientCondition.getIsPatientPrimaryCondition())
            runSQL(Repository.EntityType.PATIENT_PRIMARY_CONDITION, 
                    Repository.PMSSQL.UPDATE_PATIENT_PRIMARY_CONDITION,patientCondition); 
        else if (patientCondition.getIsPatientSecondaryCondition()){
            runSQL(Repository.EntityType.PATIENT_SECONDARY_CONDITION, 
                    Repository.PMSSQL.UPDATE_PATIENT_SECONDARY_CONDITION,patientCondition); 
        }else{
            String message = "Patient condition type received is unexpected\n"
                    + "StoreException raised in Repository.update(PatientCondition)";
            throw new StoreException(message, StoreException.ExceptionType
                    .UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public void update(PrimaryCondition pc)throws StoreException{
        runSQL(Repository.EntityType.PRIMARY_CONDITION, Repository.PMSSQL.UPDATE_PRIMARY_CONDITION,pc); 
    }
    
    @Override
    public void update(SecondaryCondition pc)throws StoreException{
        runSQL(Repository.EntityType.SECONDARY_CONDITION, Repository.PMSSQL.UPDATE_SECONDARY_CONDITION,pc); 
    }
    
    @Override
    public void update(Doctor doctor)throws StoreException{
        runSQL(Repository.EntityType.DOCTOR, Repository.PMSSQL.UPDATE_DOCTOR,doctor); 
    }
    
    @Override
    public void update(UserSettings userSettings)throws StoreException{
        runSQL(Repository.EntityType.USER_SETTINGS,Repository.PMSSQL.UPDATE_USER_SETTINGS, userSettings);
    }
    
    @Override
    public void update(User _user)throws StoreException{
        SystemDefinition.userS = colinhewlettsolutions.client.controller.PasswordUtils.generateSalt();
        try{
            SystemDefinition.userP = colinhewlettsolutions.client.controller.PasswordUtils.hashPassword(_user.getPassword(), SystemDefinition.userS);
            runSQL(Repository.EntityType.USER,Repository.PMSSQL.UPDATE_USER, _user);
        }catch(NoSuchAlgorithmException ex){
            String message = ex.getMessage() + "\n"
                    + "NoSuchAlgorithmException raised in Repository::insert(User)";
            displayErrorMessage(message,"Repository error", JOptionPane.WARNING_MESSAGE);
        }catch(InvalidKeySpecException ex){
            String message = ex.getMessage() + "\n"
                    + "InvalidKeySpecException raised in Repository::insert(User)";
            displayErrorMessage(message,"Repository error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    @Override
    public void update(Medication medication)throws StoreException{
        runSQL(Repository.EntityType.MEDICATION, Repository.PMSSQL.UPDATE_MEDICATION,medication); 
    }
    
    @Override
    public void update(Invoice invoice)throws StoreException{
        runSQL(Repository.EntityType.INVOICE, Repository.PMSSQL.UPDATE_INVOICE,invoice); 
    }
    
    @Override
    public void update(TreatmentCost treatmentCost)throws StoreException{
        runSQL(Repository.EntityType.TREATMENT_COST, Repository.PMSSQL.UPDATE_TREATMENT_COST,treatmentCost); 
    }
    
    @Override
    public void update(ClinicalNote clinicNote)throws StoreException{
        runSQL(Repository.EntityType.CLINICAL_NOTE, Repository.PMSSQL.UPDATE_CLINIC_NOTE,clinicNote); 
    }
    
    @Override
    public void update(Treatment treatment)throws StoreException{
        runSQL(Repository.EntityType.TREATMENT, Repository.PMSSQL.UPDATE_TREATMENT,treatment); 
    }
    
    @Override
    public void update(Transmission transmission)throws StoreException{
        runSQL(Repository.EntityType.TREATMENT, Repository.PMSSQL.UPDATE_TREATMENT,transmission); 
    }
    
    @Override
    public void update(Question question)throws StoreException{
        runSQL(Repository.EntityType.QUESTION, Repository.PMSSQL.UPDATE_QUESTION,question); 
    }
    
    @Override
    public void update(AppointmentTreatment treatment)throws StoreException{
        runSQL(Repository.EntityType.APPOINTMENT_TREATMENT, Repository.PMSSQL.UPDATE_APPOINTMENT_TREATMENT,treatment); 
    }
    
    @Override
    public void update(PatientQuestion question)throws StoreException{
        runSQL(Repository.EntityType.PATIENT_QUESTION, Repository.PMSSQL.UPDATE_PATIENT_QUESTION,question); 
    }
    
    /*28/03/2024
    public void update(PatientNote patientNote, Integer key)throws StoreException{
        PatientNoteDelegate delegate = null;
        delegate = new PatientNoteDelegate(patientNote);
        delegate.setKey(key);
        if ((delegate.getDatestamp()!=null)
                || (delegate.getPatientKey()!=null))
            runSQL(Repository.EntityType.PATIENT_NOTE, 
                    Repository.PMSSQL.UPDATE_PATIENT_NOTE,delegate);
        else{
            throw new StoreException(
                    "Patient note datastamp or patient key is undefined in "
                            + "Repository::update(PatientNote",
                    StoreException.ExceptionType.NULL_KEY_EXCEPTION);   
        }
        
    }*/
    
    @Override
    public void update(Patient patient, Integer key, Integer guardianKey) throws StoreException {
        Patient delegate = patient;
        //delegate.setPatientKey(key);
        if (delegate.getIsGuardianAPatient()){
            Patient gDelegate = delegate.getGuardian();
            //gDelegate.setPatientKey(guardianKey);
            delegate.setGuardian(gDelegate);
        }
        runSQL(Repository.EntityType.PATIENT, Repository.PMSSQL.UPDATE_PATIENT, delegate);
    }
    
    @Override
    public void update(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
        runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT,
                    Repository.PMSSQL.UPDATE_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment);
    }
    
    
    public void populate(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
        runSQL(Repository.EntityType.SURGERY_DAYS_ASSIGNMENT, 
                Repository.PMSSQL.INSERT_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment);
        
    }

    @Override
    public void create(Notification pn) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.PATIENT_NOTIFICATION,Repository.PMSSQL.CREATE_NOTIFICATION_TABLE, value);
    }

    public boolean doesPMSDatabaseExist(){
        boolean result = false;
        
        return result;
    }
    
    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String repositoryName = null;
    private static Repository instance = null;
    private enum RepositoryType{ACCESS, MYSQL, POSTGRES, SQL_SERVER};
    public Repository(String databaseType, String databaseURL)throws StoreException{
        RepositoryType repositoryType = RepositoryType.valueOf(databaseType);
        switch(repositoryType){
            case ACCESS ->{
                try{
                    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                    if (!databaseURL.contains("jdbc:ucanaccess://")){
                        throw new StoreException("PMS access database incorrectly specified "
                                + "(missing jdbc driver prefix",
                        StoreException.ExceptionType.PMS_DATABASE_INCORRECTLY_DEFINED);
                    }
                    String path = databaseURL.substring(databaseURL.indexOf("//")+2);
                    File file = new File(path);
                    url = databaseURL;
                    password = "";
                    user = "Admin";
                    instance = this;
                    repositoryName = "ACCESS";
                    if (!file.exists()){
                        displayErrorMessage("Repositorey constructor could not locate the current ACCESS database","Repository error",JOptionPane.WARNING_MESSAGE);
                    }
                }catch(ClassNotFoundException ex){
                    displayErrorMessage(ex.getMessage(),"Repository constructor error",JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case MYSQL ->{
                break;
            }
            case POSTGRES ->{
                break;
            }
            case SQL_SERVER ->{
                break;
            }
        }
    }
    
    /*
    public Repository()throws StoreException{
        
        if (instance == null){ //initialkise connection variables
            RepositoryType repositoryType = RepositoryType.valueOf(SystemDefinition.getPMSStoreType());
            //RepositoryType repositoryType = RepositoryType.valueOf(System.getenv("PMS_STORE_TYPE"));
            switch (repositoryType){
            case ACCESS:{
                this.url = SystemDefinition.getPMSStoreAccessURL();
                
                if (this.url.equals("undefined")){
                    throw new StoreException("PMS access database undefined",
                    StoreException.ExceptionType.PMS_DATABASE_UNDEFINED);
                }else if (!this.url.contains("jdbc:ucanaccess://")){
                    throw new StoreException("PMS access database incorrectly specified "
                            + "(missing jdbc driver prefix",
                    StoreException.ExceptionType.PMS_DATABASE_INCORRECTLY_DEFINED);
                }
                else{
                    String path = (this.url).substring(this.url.indexOf("//")+2);
                    File file = new File(path);
                    password = "";
                    user = "Admin";
                    instance = this;
                    repositoryName = "ACCESS";
                    try{  
                        if (!file.exists()) {
                            
                            DatabaseBuilder.create(Database.FileFormat.V2016, new File(path));
                            //01/03/2023
                            create(new Patient());
                            //28/03/2024create(new PatientNote());
                            create(new PrimaryCondition());
                            create(new SecondaryCondition());
                            create(new Doctor());
                            create(new Medication());
                            create(new Notification());
                            create(new SurgeryDaysAssignment());
                            create(new Appointment());
                            create(new ClinicalNote());
                            create(new Treatment());
                            create(new AppointmentTreatment());
                        }
                        
                    }catch (IOException io) {
                        String msg = "IOException -> raised on attempt to create a new Access database in DesktopControllerActionEvent.MIGRATION_DATABASE_CREATION_REQUEST";
                        throw new StoreException(msg + "\nStoreException raised in "
                                + "initialiseTargetStore(file = "
                                + file.toString() + ")", StoreException.ExceptionType.IO_EXCEPTION);
                    }catch(StoreException ex){
                        displayErrorMessage(ex.getMessage() + "\n "
                                + "Raised in Repository.create(ACCESS)",
                                "Repository error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
                break;
            }
            case POSTGRES:
                repositoryName = "POSTGRES";
                this.url = SystemDefinition.getPMSStorePostgresSQLURL();
                
                if (this.url.equals("undefined")){
                    throw new StoreException("PMS postgres database undefined",
                    StoreException.ExceptionType.PMS_DATABASE_UNDEFINED);
                }else if (!this.url.contains("jdbc:postgresql://")){
                    throw new StoreException("PMS postgres database incorrectly specified "
                            + "(missing jdbc driver prefix",
                    StoreException.ExceptionType.PMS_DATABASE_INCORRECTLY_DEFINED);
                }
                
                String databaseName = (this.url).substring(this.url.indexOf("5432/")+5);
                password = "ch19450907A@";
                user = "postgres";
                String sql = "SELECT * FROM pg_database WHERE datname = ?";
                
                try{
                    PreparedStatement preparedStatement = getPostgresConnection().prepareStatement(sql);
                    preparedStatement.setString(1, databaseName);
                    ResultSet rs = preparedStatement.executeQuery();
                    if (!rs.next()){//defined pms database does not exist
                        try{
                            sql = "CREATE DATABASE " + databaseName +
                                    "    WITH \n" +
                                    "    OWNER = postgres\n" +
                                    "    ENCODING = 'UTF8'\n" +
                                    "    LC_COLLATE = 'English_United Kingdom.1252'\n" +
                                    "    LC_CTYPE = 'English_United Kingdom.1252'\n" +
                                    "    TABLESPACE = pg_default\n" +
                                    "    CONNECTION LIMIT = -1;";
                            preparedStatement = getPostgresConnection().prepareStatement(sql);
                            preparedStatement.execute();
                            url = "jdbc:postgresql://localhost:5432/" + databaseName;
                            sql = "CREATE TABLE public.patient (" +
                                        "title character varying(255) COLLATE pg_catalog.\"default\",\n" +                            
                                        "forenames character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "surname character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "line1 character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "line2 character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "town character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "county character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "postcode character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "phone1 character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "phone2 character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "email character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "gender character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "dob date,\n" +
                                        "isguardianapatient boolean DEFAULT false,\n" +
                                        "recallfrequency smallint,\n" +
                                        "recalldate date,\n" +
                                        "recallfrequencyGBT smallint,\n" +
                                        "recalldateGBT date,\n" +
                                        "notes character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                        "guardiankey integer,\n" +
                                        "pid integer NOT NULL,\n" +
                                        "isdeleted boolean DEFAULT false,\n" +
                                        "CONSTRAINT patient_pk PRIMARY KEY (pid),\n" +
                                        "CONSTRAINT patient_fk1 FOREIGN KEY (guardiankey)\n" +
                                        "    REFERENCES public.patient (pid) MATCH SIMPLE\n" +
                                        "    ON UPDATE NO ACTION\n" +
                                        "    ON DELETE NO ACTION \n);";
                            preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                            preparedStatement.execute();   
                            sql = "CREATE TABLE public.patientnote (" +
                                    "pid integer NOT NULL,\n" +
                                    "datestamp timestamp without time zone,\n" +
                                    "notes text,\n" +
                                    "isdeleted boolean DEFAULT false,\n" +
                                    "lastupdated timestamp without time zone,\n" +
                                    "patientkey integer NOT NULL,\n" +
                                    "CONSTRAINT patientnote_pk PRIMARY KEY (pid),\n" +
                                    "CONSTRAINT patient_fk2 FOREIGN KEY (patientkey)\n" +
                                    "    REFERENCES public.patient (pid) MATCH SIMPLE\n" +
                                    "     ON UPDATE NO ACTION\n" +
                                    "     ON DELETE NO ACTION \n);"; 
                            preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                            preparedStatement.execute();        
                            
                            sql = "CREATE TABLE public.appointment(" +
                                    "pid integer NOT NULL,\n" +
                                    "patientkey integer NOT NULL,\n" +
                                    "patientnotekey integer,\n" +
                                    "duration integer,\n" +
                                    "notes character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                    "start timestamp without time zone,\n" +
                                    "isdeleted boolean DEFAULT false,\n" +
                                    "haspatientbeencontacted boolean DEFAULT false,\n" +
                                    "isCancelled boolean DEFAULT false,\n" +
                                    "CONSTRAINT appointment_pk PRIMARY KEY (pid),\n" +
                                    "CONSTRAINT appointment_fk1 FOREIGN KEY (patientkey) \n" +
                                    "   REFERENCES public.patient (pid) MATCH SIMPLE\n" +
                                    "    ON UPDATE NO ACTION\n" +
                                    "    ON DELETE NO ACTION,\n" +
                                    "CONSTRAINT appointment_fk2 FOREIGN KEY (patientnotekey)\n" +
                                    "   REFERENCES public.patientnote (pid) MATCH SIMPLE\n" +
                                    "    ON UPDATE NO ACTION\n" +
                                    "    ON DELETE NO ACTION\n);";
                            preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                            preparedStatement.execute();
                            sql = "CREATE TABLE public.patientnotification(\n" +
                                    "    pid integer NOT NULL,\n" +
                                    "    isdeleted boolean DEFAULT false,\n" +
                                    "    isactioned boolean DEFAULT false,\n" +
                                    "    notificationdate date,\n" +
                                    "    notificationtext character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                    "    patienttonotify integer,\n" +
                                    "    isCancelled boolean DEFAULT false,\n" +
                                    "    CONSTRAINT patientnotification_pk PRIMARY KEY (pid),\n" +
                                    "    CONSTRAINT patientnotification_fk1 FOREIGN KEY (patienttonotify)\n" +
                                    "    REFERENCES public.patient (pid) MATCH SIMPLE\n" +
                                    "    ON UPDATE NO ACTION\n" +
                                    "    ON DELETE NO ACTION)";
                            preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                            preparedStatement.execute();        
                            sql = "CREATE TABLE public.surgerydays(\n" +
                                    "  day character varying(255) COLLATE pg_catalog.\"default\" NOT NULL,\n"+
                                    "  issurgery boolean DEFAULT false,\n" +
                                    "  CONSTRAINT surgery_days_assignment_pk PRIMARY KEY (day))";
                            preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                            preparedStatement.execute();
                            instance = this;
                            repositoryName = "POSTGRES";
                        }catch (SQLException ex){
                            String message = ex.getMessage() +"\n";
                            throw new StoreException(message + "StoreException raised on attempt to create a new database",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                        }

                    }
                    else{
                        url = "jdbc:postgresql://localhost:5432/" + databaseName;
                        repositoryName = "POSTGRES";
                    }
                }catch (SQLException ex){
                    String message = ex.getMessage() +"\n";
                    throw new StoreException(message + "StoreException raised on query for named postgres database (" + databaseName 
                            + ")", StoreException.ExceptionType.SQL_EXCEPTION);
                }
                break;
            case SQL_SERVER:
                break;
            }
             
        }else{
            //connection variables already initialised
            int test = 0;
            test++;
            
        }
    }*/
    
    private void doInsertToDo(String sql, Entity entity) throws StoreException{
        ToDo  toDo;
        if (entity != null) {
            if (entity.getIsToDo()) {
                toDo = (ToDo) entity;
                try (PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setDate(1, java.sql.Date.valueOf(toDo.getDate()));
                    preparedStatement.setString(2, (toDo.getDescription()));
                    preparedStatement.setBoolean(3, toDo.getIsActioned());
                    preparedStatement.setBoolean(4, toDo.getIsCancelled());
                    preparedStatement.setBoolean(5, toDo.getIsDeleted());
                    preparedStatement.setLong(6, toDo.getKey());
                    preparedStatement.execute();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertToDo()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> patient toDo defined invalidly in doInsertToDo()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notificaion undefined in doInsertToDo()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doUpdateToDo(String sql, Entity entity) throws StoreException{
        Patient patient;
        ToDo  toDo;
        if (entity != null) {
            if (entity.getIsToDo()) {
                toDo = (ToDo) entity;
                try(PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);){
                    preparedStatement.setDate(1, java.sql.Date.valueOf(toDo.getDate()));
                    preparedStatement.setString(2, toDo.getDescription());
                    preparedStatement.setBoolean(3, toDo.getIsActioned());
                    preparedStatement.setBoolean(4, toDo.getIsCancelled());
                    preparedStatement.setBoolean(5, toDo.getIsDeleted());
                    preparedStatement.setLong(6, toDo.getKey());
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doUpdateToDo()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> patient toDo defined invalidly in doUpdateToDo()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notificaion undefined in doUpdateToDo()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    public void cancel(ToDo toDo)throws StoreException{
        runSQL(Repository.EntityType.TO_DO,Repository.PMSSQL.CANCEL_TO_DO, toDo);
    }
    
    @Override
    public void create(ToDo table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntityType.TO_DO,Repository.PMSSQL.CREATE_TO_DO_TABLE, value);
    }
    
    @Override
    public Point count(ToDo toDo)throws StoreException{
        Entity result = null;
        if (toDo !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_TO_DO;
            result = (Entity)runSQL(Repository.EntityType.TO_DO, 
                    sqlStatement, toDo);
        }
        return result.getValue();
    }
    
    @Override
    public void delete(ToDo toDo)throws StoreException{
        if (toDo.getScope()!=null){
            switch(toDo.getScope()){
               case SINGLE:
                    runSQL(Repository.EntityType.TO_DO,Repository.PMSSQL.DELETE_TO_DO,toDo);
                    break;
               case ALL:
                   runSQL(Repository.EntityType.TO_DO,Repository.PMSSQL.DELETE_ALL_TO_DO,null);
                   break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + toDo.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(ToDo)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of toDo delete operation undefined (" 
                    + toDo.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(ToDo)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public Integer insert(ToDo toDo)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntityType.TO_DO,
                    Repository.PMSSQL.READ_TO_DO_NEXT_HIGHEST_KEY,toDo);
        entity = (Entity)client;
        toDo.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntityType.TO_DO,
                Repository.PMSSQL.INSERT_TO_DO, toDo);
        
        return toDo.getKey();
    }
    
    @Override
    public ToDo read(ToDo toDo)throws StoreException{
        ToDo result = null;
        Entity entity = null;

        switch(toDo.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntityType.TO_DO, 
                            Repository.PMSSQL.READ_TO_DO, toDo);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(ToDo)" 
                                + toDo.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }  
            case UNACTIONED:
                entity = (Entity)runSQL(Repository.EntityType.TO_DO,
                            Repository.PMSSQL.READ_UNACTIONED_TO_DO, toDo);
                break;
            case ALL:
                entity = (Entity)runSQL(Repository.EntityType.TO_DO,
                            Repository.PMSSQL.READ_ALL_TO_DO, toDo);
                break;
                /*
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntityType.TO_DO,
                            Repository.PMSSQL.READ_TO_DO_FOR_PATIENT,toDo);
                break;*/
        }
        if (entity!=null){
            if (entity.getIsToDo()){
                result = (ToDo)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(ToDo)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{ return null;
            /*
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(ToDo)" 
                        + toDo.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);*/
        }
    }
    
    @Override
    public void update(ToDo toDo)throws StoreException{
        runSQL(Repository.EntityType.TO_DO, Repository.PMSSQL.UPDATE_TO_DO,toDo); 
    }
}

