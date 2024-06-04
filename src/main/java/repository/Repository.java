/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import model.non_entity.SystemDefinition;
import static controller.ViewController.displayErrorMessage;
import model.*;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
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
import static model.Entity.Scope.ALL;
import static model.Entity.Scope.FOR_APPOINTMENT;
import static model.Entity.Scope.FOR_PATIENT;
import static model.Entity.Scope.FOR_PRIMARY_CONDITION;
import static model.Entity.Scope.FOR_QUESTION;
import static model.Entity.Scope.FOR_TREATMENT;
import static model.Entity.Scope.SINGLE;

/**
 *
 * @author colin
 */
public class Repository implements IStoreActions {
    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String repositoryName = null;
    private static Connection postgresConnection = null;
    private static Connection pmsStoreConnection = null;
    
    protected enum EntitySQL {
                            APPOINTMENT,
                            APPOINTMENT_TREATMENT,
                            CLINICAL_NOTE,
                            DOCTOR,
                            MEDICATION,
                            PATIENT,
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
                            TREATMENT,
                            DATABASE}
 
    protected enum PMSSQL   {
                                CANCEL_APPOINTMENT,
                                COUNT_APPOINTMENTS,
                                COUNT_APPOINTMENTS_FOR_DAY,
                                COUNT_APPOINTMENTS_FOR_PATIENT,
                                COUNT_APPOINTMENTS_FROM_DAY,
                                CREATE_APPOINTMENT_TABLE,
                                DELETE_APPOINTMENT, // was commented out
                                DELETE_APPOINTMENTS_FOR_PATIENT,
                                DELETE_APPOINTMENTS,
                                DROP_APPOINTMENT_TABLE,
                                INSERT_APPOINTMENT,
                                READ_APPOINTMENT,
                                READ_APPOINTMENTS,
                                READ_CANCELLED_APPOINTMENTS,
                                READ_APPOINTMENTS_FOR_DAY,
                                READ_APPOINTMENTS_FOR_PATIENT,
                                READ_APPOINTMENTS_FROM_DAY,
                                READ_APPOINTMENT_NEXT_HIGHEST_KEY,
                                READ_DELETED_APPOINTMENTS_FOR_PATIENT,
                                RECOVER_APPOINTMENT,
                                UNCANCEL_APPOINTMENT,
                                UPDATE_APPOINTMENT,
                                
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
                                
                                CREATE_QUESTION_TABLE,
                                COUNT_QUESTION,
                                DELETE_ALL_QUESTION,
                                DELETE_QUESTION,
                                INSERT_QUESTION,
                                READ_QUESTION,
                                READ_QUESTION_FOR_PATIENT,
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
        
    protected Connection getPMSStoreConnection()throws StoreException{
        String message;
        if (Repository.pmsStoreConnection  == null){
            try{
                Repository.pmsStoreConnection = DriverManager.getConnection(getURL(), getUser(), getPassword());
            }catch (SQLException ex){
                message = ex.getMessage() + "\n";
                message = message + "StoreException raised in "
                    + getRepositoryName() + ".getPMSStoreConnection()";
                throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
        
        return Repository.pmsStoreConnection;
    }
    
    private IStoreClient runSQL(Repository.EntitySQL entitySQL, 
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
                case MEDICATION:
                    result = doPMSSQLforMedication(pmsSQL, (Entity)client);
                    break;
                case PATIENT:
                    result = doPMSSQLforPatient(pmsSQL, (Entity)client);
                    break;
                case PATIENT_NOTIFICATION:
                    result = doPMSSQLforNotification(pmsSQL, (Entity)client);
                    break;
                case PATIENT_NOTE:
                    result = doPMSSQLforPatientNote(pmsSQL, (Entity)client);
                    break;
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
                case TREATMENT:
                    result = doPMSSQLforTreatment(pmsSQL, (Entity)client);
                    break;
                
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
        AppointmentDelegate delegate = null;
        PatientDelegate patientDelegate = null;
        /*PatientNoteDelegate patientNoteDelegate = null;*/
        
        int key = rs.getInt("pid");
        appointment.setStart(rs.getObject("Start", LocalDateTime.class));
        appointment.setDuration(Duration.ofMinutes(rs.getLong("Duration")));
        /*28/03/2024appointment.setNotes(rs.getString("Notes"));*/
        appointment.setNotes(rs.getString("Notes"));
        appointment.setHasPatientBeenContacted(rs.getBoolean("hasPatientBeenContacted"));
        appointment.setIsDeleted(rs.getBoolean("isDeleted"));
        appointment.setIsCancelled(rs.getBoolean("isCancelled"));
        
        //09/06/2023 fix 
        //patientDelegate = new PatientDelegate();
        int patientKey = rs.getInt("PatientKey");
        patientDelegate = new PatientDelegate(patientKey);
        patientDelegate.setPatientKey(patientKey); 
        appointment.setPatient(patientDelegate);
        
        /*28/03/2024
        int patientNoteKey = rs.getInt("patientNoteKey");
        patientNoteDelegate = new PatientNoteDelegate();
        patientNoteDelegate.setKey(patientNoteKey);
        appointment.setPatientNote(patientNoteDelegate);
        */
        
        delegate = new AppointmentDelegate(appointment);
        delegate.setAppointmentKey(key);
        return delegate;
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
            try{
                PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in doCreateAppointmentTable(sql) ",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doRecoverPatientChild(String sql, Entity entity)throws StoreException{
        String entityType = null;
        try{
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            if (entity.getIsAppointment()){
                entityType = "Appointment";
                AppointmentDelegate delegate = (AppointmentDelegate)entity;
                preparedStatement.setLong(1, delegate.getAppointmentKey());      
            }else if(entity.getIsPatientNotification()){
                entityType = "PatientNotification";
                NotificationDelegate delegate = (NotificationDelegate)entity;
                preparedStatement.setLong(1, delegate.getKey());
            }else if(entity.getIsPatientNote()){
                entityType = "PatientNote";
                PatientNoteDelegate delegate = (PatientNoteDelegate)entity;
                preparedStatement.setLong(1, delegate.getKey());
            }
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
        PreparedStatement preparedStatement = null;
        if (entity.getIsPatient()){
            PatientDelegate patient = (PatientDelegate)entity;
            try{
                getPMSStoreConnection().setAutoCommit(false);
                sql = "UPDATE Patient SET isDeleted = false WHERE pid = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
                preparedStatement.executeUpdate();
                sql = "UPDATE Appointment SET isDeleted = false where patientKey = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
                preparedStatement.executeUpdate();
                sql = "UPDATE PatientNotification SET IsDeleted = false WHERE patientToNotify = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
                preparedStatement.executeUpdate();
                getPMSStoreConnection().commit();
            }catch(SQLException ex){
                String message = ex.getMessage() + "\n"
                        + "StoreException raised in Repository::doRecoverPatient()";
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
            PatientDelegate patient = (PatientDelegate)entity;
            try{
                getPMSStoreConnection().setAutoCommit(false);
                sql = "UPDATE Appointment SET isDeleted = true where patientKey = ?;";
                PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
                preparedStatement.executeUpdate();
                sql = "UPDATE PatientNotification SET IsDeleted = true WHERE patientToNotify = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
                /*
                sql = "UPDATE PatientNote SET IsDeleted = true WHERE patientKey = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
                preparedStatement.executeUpdate();
                */
                sql = "UPDATE Patient SET isDeleted = true WHERE pid = ?;";
                preparedStatement = getPMSStoreConnection().prepareStatement(sql); 
                preparedStatement.setLong(1, patient.getPatientKey());
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
        try{
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.executeUpdate();
        }catch (SQLException ex){
            String message = ex.getMessage() + "\n";
            throw new StoreException(message 
                    + "StoreException raised in Repository::doDelete(" + sql + ")",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doDeleteCancelChildEntity(String sql, Entity entity)throws StoreException{
        
        Entity delegate = null;
        try{
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            if (entity.getIsAppointment()){
                delegate = (AppointmentDelegate)entity;
                preparedStatement.setInt(1, ((AppointmentDelegate)delegate).getAppointmentKey());
            }else if (entity.getIsPatientNotification()){
                delegate = (NotificationDelegate)entity;
                preparedStatement.setInt(1, ((NotificationDelegate)delegate).getKey());
            }
            //preparedStatement.setInt(1, ((AppointmentDelegate)delegate).getAppointmentKey());
            preparedStatement.executeUpdate();
        }catch (SQLException ex){
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteCancelChild(String sql, Entity entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
   /*
    private void doDeleteCancelAppointment(String sql, Entity entity)throws StoreException{
        if (entity.getIsAppointment()){
            AppointmentDelegate delegate = (AppointmentDelegate)entity;
            try{
                PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                preparedStatement.setInt(1, ((AppointmentDelegate)delegate).getAppointmentKey());
                preparedStatement.executeUpdate();
            }catch (SQLException ex){
                throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteCancelAppointment(String sql, EntityStoreType entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
            }
        }
    }
    */
    
    private void doInsertAppointment(String sql, Entity entity)throws StoreException{
        if (entity.getIsAppointment()){
            AppointmentDelegate delegate = (AppointmentDelegate)entity;
            try {
                PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                preparedStatement.setInt(1, 
                        ((PatientDelegate)delegate.getPatient()).getPatientKey());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(delegate.getStart()));
                preparedStatement.setLong(3, delegate.getDuration().toMinutes());
                preparedStatement.setString(4, delegate.getNotes());
                preparedStatement.setLong(5, delegate.getAppointmentKey());
                /*Integer patientNoteKey =
                        ((PatientNoteDelegate)delegate.getPatientNote()).getKey();
                if (patientNoteKey == null)
                    preparedStatement.setNull(6, java.sql.Types.INTEGER);
                else preparedStatement.setLong(6, patientNoteKey);*/
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
        
    private Appointment doReadAppointmentWithKey(String sql, Entity entity) throws StoreException{
        Appointment appointment = null;
        if (entity!=null){
            if (entity.getIsAppointment()){
                //appointment = (Appointment)entity;
                AppointmentDelegate delegate = (AppointmentDelegate)entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, delegate.getAppointmentKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    appointment = new Appointment();
                    appointment.setScope(Entity.Scope.SINGLE);
                    appointment = get(appointment, rs);
                    
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadAppointmentWithKey(sql, EntityStoreType)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
        return appointment;            
    }

    private Entity doReadAppointmentHighestKey(String sql)throws StoreException{
        try {
            Point key;
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private Entity doReadCancelledAppointments(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(appointment, rs);
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadCancelledAppointments()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else{
                String msg = 
                        "Unexpected data type specified for entity in Repository::doReadCancelledAppointments()";
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
    
    private Entity doReadAppointments(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private Entity doReadAppointmentsForDay(String sql, Entity entity)throws StoreException{
        Entity result = null;
        Appointment appointment;
        if (entity != null) {
            if (entity.getIsAppointment()){
                appointment = (Appointment)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setInt(1, 
                            ((PatientDelegate)appointment.getPatient()).getPatientKey());
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
                repository.AppointmentDelegate delegate = (repository.AppointmentDelegate)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    if (delegate.getPatient() != null) {
                        preparedStatement.setInt(1, 
                                ((repository.PatientDelegate)delegate.getPatient()).getPatientKey());
                    }
                    preparedStatement.setTimestamp(2, Timestamp.valueOf(delegate.getStart()));
                    preparedStatement.setLong(3, delegate.getDuration().toMinutes());
                    /*28/03/2024preparedStatement.setString(4, delegate.getNotes());*/
                    preparedStatement.setString(4, delegate.getNotes());
                    preparedStatement.setBoolean(5, delegate.getHasPatientBeenContacted());
                    /*28/03/2024preparedStatement.setLong(6, 
                            ((repository.PatientNoteDelegate)delegate.getPatientNote()).getKey());*/
                    preparedStatement.setLong(6, delegate.getAppointmentKey());
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
        patient.setIsDeleted(rs.getBoolean("isDeleted"));
        LocalDate dob = rs.getObject("dob", LocalDate.class);
        
        /*
        -- if (dob.getYear() == 1899) {
            dob = null;
        }
        */
        patient.setDOB(dob);
        patient.getRecall().setDentalFrequency(rs.getInt("recallFrequency"));
        LocalDate recallDate = rs.getObject("recallDate", LocalDate.class);
        /*
        -- if (recallDate.getYear() == 1899) {
            recallDate = null;
        }
        */
        patient.getRecall().setDentalDate(recallDate);
        patient.setIsGuardianAPatient(rs.getBoolean("isGuardianAPatient"));
        if (patient.getIsGuardianAPatient()) {
            int guardianKey = rs.getInt("guardianKey");
            if (guardianKey > 0) {
                PatientDelegate gDelegate = new PatientDelegate(guardianKey);
                patient.setGuardian(gDelegate);
            }
        }
        PatientDelegate delegate = new PatientDelegate(patient);
        delegate.setPatientKey(rs.getInt("pid"));
        return delegate;
    }
    
    private void doInsertSecondaryCondition(String sql, Entity entity)throws StoreException{
        SecondaryCondition sc = null;
        if (entity != null){
            if (entity.getIsSecondaryCondition()){
                sc = (SecondaryCondition)entity;
                try{
                    Integer testKey = sc.getKey();
                    Integer test = sc.getPrimaryCondition().getKey();
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private void doInsertClinicalNote(String sql, Entity entity)throws StoreException{
        ClinicalNote clinicNote = null;
        if (entity != null){
            if (entity.getIsClinicNote()){
                clinicNote = (ClinicalNote)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private void doInsertQuestion(String sql, Entity entity)throws StoreException{
        Question question = null;
        if (entity != null){
            if (entity.getIsQuestion()){
                question = (Question)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private void doInsertPatientPrimaryCondition(String sql, Entity entity)throws StoreException{
        PatientPrimaryCondition patientPrimaryCondition = null;
        if (entity != null){
            if (entity.getIsPatientPrimaryCondition()){
                patientPrimaryCondition = (PatientPrimaryCondition)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private void doInsertDoctor(String sql, Entity entity)throws StoreException{
        Doctor doctor = null;
        if (entity != null){
            if (entity.getIsDoctor()){
                doctor = (Doctor)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        PatientDelegate delegate;
        if (entity != null) {
            if (entity.getIsPatient()) {
                //thePatient = (Patient)entity;
                delegate = (PatientDelegate)entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setString(1, delegate.getName().getTitle());
                    preparedStatement.setString(2, delegate.getName().getForenames());
                    preparedStatement.setString(3, delegate.getName().getSurname());
                    preparedStatement.setString(4, delegate.getAddress().getLine1());
                    preparedStatement.setString(5, delegate.getAddress().getLine2());
                    preparedStatement.setString(6, delegate.getAddress().getTown());
                    preparedStatement.setString(7, delegate.getAddress().getCounty());
                    preparedStatement.setString(8, delegate.getAddress().getPostcode());
                    preparedStatement.setString(9, delegate.getPhone1());
                    preparedStatement.setString(10, delegate.getPhone2());
                    preparedStatement.setString(11, delegate.getGender());
                    if (delegate.getDOB() != null) {
                        preparedStatement.setDate(12, java.sql.Date.valueOf(delegate.getDOB()));
                    } else {
                        //preparedStatement.setDate(12, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(12, java.sql.Types.DATE);
                    }
                    preparedStatement.setBoolean(13, delegate.getIsGuardianAPatient());
                    if (delegate.getRecall().getDentalFrequency()==null)
                        preparedStatement.setInt(14, 0);
                    else
                        preparedStatement.setInt(14, delegate.getRecall().getDentalFrequency()); 
                    if (delegate.getRecall().getDentalDate() != null) {
                        preparedStatement.setDate(15, java.sql.Date.valueOf(delegate.getRecall().getDentalDate()));
                    } else {
                        //preparedStatement.setDate(15, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(15, java.sql.Types.DATE);
                    }
                    preparedStatement.setString(16, delegate.getNotes());
                    //Integer key = delegate.getPatientKey();
                    preparedStatement.setLong(17, delegate.getPatientKey());
                    if (((PatientDelegate)delegate.getGuardian()).getPatientKey() > 0){
                        preparedStatement.setLong(18,((PatientDelegate)delegate.getGuardian()).getPatientKey());
                    }
                    else preparedStatement.setNull(18, java.sql.Types.INTEGER);
                    preparedStatement.setString(19, delegate.getEmail());
                    
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
    
    private Entity doReadDeletedPatients(String sql, Patient patient)throws StoreException{
        Entity result;
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            result = get(patient, rs);
            return result;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::runSQL(PatientManagementSystemSQL..) during a READ_DELETED_PATIENTS statement",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }  
    }
    
    private Entity doReadPatientsAll(String sql, Patient patient) throws StoreException{
        Entity result;
        
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            result = get(patient, rs);
            return result;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::runSQL(PatientManagementSystemSQL..) during a READ_ALL_PATIENTS statement",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }   
    }
    
    private Entity doReadTreatmentAll(String sql, Treatment treatment) throws StoreException{
        Entity result;
        
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            result = get(treatment, rs);
            return result;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::runSQL(TreatmentManagementSystemSQL..) during a READ_ALL_PATIENTS statement",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }   
    }
    
    private Entity doReadQuestionAll(String sql, Question question) throws StoreException{
        Entity result;
        
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            result = get(question, rs);
            return result;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::runSQL(QuestionManagementSystemSQL..) during a READ_ALL_QUESTIONS statement",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }   
    }
    
    private Entity doReadSecondaryConditionAll(String sql, Entity entity) throws StoreException{
        Entity result = null;
        SecondaryCondition sc = null;
        if (entity!=null){
            if (entity.getIsSecondaryCondition()){
                sc =  (SecondaryCondition)entity; 
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(sc, rs);

                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadSecondaryConditionAll()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }else {
                String message = "StoreException raised because entity type incorrectly defined in "
                        + "Repository::doReadSecondaryConditionAll()";
                throw new StoreException(message,StoreException
                        .ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String message = "StoreException raised because entity type undefined in "
                        + "Repository::doReadSecondaryConditionAll()";
                throw new StoreException(message,StoreException
                        .ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
        return result;
    }
    
    private Entity doReadPrimaryConditionAll(String sql, Entity entity) throws StoreException{
        Entity result;
        if (entity!=null){
            PrimaryCondition pc = null;
            if (entity.getIsPrimaryCondition()){
                pc =  (PrimaryCondition)entity;

                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get(pc, rs);

                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPrimaryConditionAll()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }   
            }else{
                String message = "StoreException raised because entity type incorrectly defined in "
                        + "Repository::doReadPrimaryConditionAll()";
                throw new StoreException(message,StoreException
                        .ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }else{
            String message = "StoreException raised because entity type undefined in "
                        + "Repository::doReadPrimaryConditionAll()";
                throw new StoreException(message,StoreException
                        .ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
        return result;
    }
    
    private Entity doReadPatientWithKey(String sql, Entity entity)throws StoreException{
        PatientDelegate delegate;
        Entity result = null;
        if (entity != null){
            if (entity.getIsPatient()){
                delegate  = (PatientDelegate)entity;
                
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, delegate.getPatientKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    result = get((Patient)entity, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientWithKey(sql, EntityStoreType)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> entity not a patient object in Repository::doReadPatientWithKey(sql, EntityStoreType)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity undefined in Repository::doReadPatientWithKey(sql, EntityStoreType)";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
        return result;
    }
    
    private Entity doReadHighestKey(String sql) throws StoreException{
        Entity entity;
        try {
            Point key;
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                PatientDelegate delegate = (PatientDelegate)entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setString(1, delegate.getName().getTitle());
                    preparedStatement.setString(2, delegate.getName().getForenames());
                    preparedStatement.setString(3, delegate.getName().getSurname());
                    preparedStatement.setString(4, delegate.getAddress().getLine1());
                    preparedStatement.setString(5, delegate.getAddress().getLine2());
                    preparedStatement.setString(6, delegate.getAddress().getTown());
                    preparedStatement.setString(7, delegate.getAddress().getCounty());
                    preparedStatement.setString(8, delegate.getAddress().getPostcode());
                    preparedStatement.setString(9, delegate.getPhone1());
                    preparedStatement.setString(10, delegate.getPhone2());
                    preparedStatement.setString(11, delegate.getGender());
                    if (delegate.getDOB() != null) {
                        preparedStatement.setDate(12, java.sql.Date.valueOf(delegate.getDOB()));
                    } else {
                        //preparedStatement.setDate(12, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(12, java.sql.Types.DATE);
                    }
                    preparedStatement.setBoolean(13, delegate.getIsGuardianAPatient());
                    preparedStatement.setInt(14, delegate.getRecall().getDentalFrequency());
                    if (delegate.getRecall().getDentalDate() != null) {
                        preparedStatement.setDate(15, java.sql.Date.valueOf(delegate.getRecall().getDentalDate()));
                    } else {
                        //preparedStatement.setDate(15, java.sql.Date.valueOf(LocalDate.of(1899, 1, 1)));
                        preparedStatement.setNull(15, java.sql.Types.DATE);
                    }
                    preparedStatement.setString(16, delegate.getNotes());
                    if (delegate.getIsGuardianAPatient()) {
                        preparedStatement.setLong(17, ((PatientDelegate)delegate.getGuardian()).getPatientKey());
                    } else {
                        //preparedStatement.setNull(17, 0);
                        preparedStatement.setNull(17, Types.INTEGER);
                    }
                    preparedStatement.setString(18, delegate.getEmail());
                    preparedStatement.setLong(19, delegate.getPatientKey());
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
        NotificationDelegate delegate = new NotificationDelegate(patientNotification);
        delegate.set(null);
        PatientDelegate pDelegate = new PatientDelegate(0);
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
                        delegate.setKey(pid);
                        pDelegate.setPatientKey(patientKey);
                        delegate.setPatient(pDelegate);
                        delegate.setNotificationDate(notificationDate);
                        delegate.setNotificationText(notificationText);
                        delegate.setIsActioned(isActioned);
                        delegate.setIsCancelled(isCancelled);
                        delegate.setIsDeleted(isDeleted);
                        result = delegate;
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
                           delegate = new NotificationDelegate();
                           delegate.setKey(pid);
                           pDelegate = new PatientDelegate(patientKey);
                           //pDelegate.setPatientKey(patientKey);
                           delegate.setPatient(pDelegate);
                           delegate.setNotificationDate(notificationDate);
                           delegate.setNotificationText(notificationText);
                           delegate.setIsActioned(isActioned);
                           collection.add(delegate);
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
    
    /*28/03/2024
    private PatientNote get(PatientNote patientNote, ResultSet rs)throws StoreException{
        PatientNote result = null;
        ArrayList<PatientNote> collection = new ArrayList<>();
        try{
            switch (patientNote.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
                        rs.next();
                        LocalDateTime datestamp = rs.getTimestamp("datestamp").toLocalDateTime();
                        LocalDateTime lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();
                        Integer pid = rs.getInt("pid");
                        Integer patientKey = rs.getInt("patientKey");
                        String notes = rs.getString("notes");
                        Boolean isDeleted = rs.getBoolean("isDeleted");
                        patientNote = new PatientNote(pid);
                        patientNote.setDatestamp(datestamp);
                        patientNote.setPatientKey(patientKey);
                        patientNote.setNote(notes);
                        patientNote.setIsDeleted(isDeleted);
                        patientNote.setLastUpdated(lastUpdated);

                        result = patientNote;
                    }
                    break;
                default:
                    LocalDateTime lastUpdated = null;
                    if (!rs.wasNull()){
                        while (rs.next()){
                            Integer pid = rs.getInt("pid");
                            LocalDateTime datestamp = rs.getTimestamp("datestamp").toLocalDateTime();
                            if (rs.getTimestamp("lastUpdated")!=null)
                                lastUpdated = rs.getTimestamp("lastUpdated").toLocalDateTime();
                            Integer patientKey = rs.getInt("patientKey");
                            String notes = rs.getString("notes");
                            Boolean isDeleted = rs.getBoolean("isDeleted");
                            PatientNote thePatientNote = new PatientNote(pid);
                            thePatientNote.setDatestamp(datestamp);
                            thePatientNote.setPatientKey(patientKey);
                            thePatientNote.setNote(notes);
                            thePatientNote.setIsDeleted(isDeleted);
                            thePatientNote.setLastUpdated(lastUpdated);
                            collection.add(thePatientNote);
                        }
                        patientNote.set(collection);
                    }
                    result = patientNote;
                    break;
            }
            return result;
             
        }catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException -> raised in Repository::get(PatientNote,ResultSet)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    */
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
    
    private ClinicalNote get(ClinicalNote clinicNote, ResultSet rs)throws StoreException{
        ClinicalNote result = null;
        ArrayList<ClinicalNote> collection = new ArrayList<>();
        try{
            switch (clinicNote.getScope()){
                case SINGLE:
                    if (!rs.wasNull()){
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
    
    private Entity doReadPatientNotifications(String sql, Entity entity)throws StoreException{
        Entity result;
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            result = get((Notification)entity, rs);
            return result;
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised in Repository::doReadPatientNotifications(sql))",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private Entity doReadPatientNotesForPatient(String sql, Entity entity)throws StoreException{
        /*28/03/2024
        PatientNote patientNote;
        if (entity != null) {
            if (entity.getIsPatientNote()) {
                patientNote = (PatientNote)entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, patientNote.getPatientKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    return get(patientNote, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientNotesForPatient()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPatientNotesForPatient()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> entity type undefined in Repository::doReadPatientNotesForPatient()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
        */
        return null;
    }
    
    private Entity doReadPatientNotificationsForPatient(String sql, Entity entity)throws StoreException{
        //07/08/2022
        Notification notification = null;
        PatientDelegate delegate;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                notification = (Notification)entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    delegate = (PatientDelegate)notification.getPatient();
                    preparedStatement.setLong(1, delegate.getPatientKey());
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
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private Entity doReadDoctorWithKey(String sql, Entity entity)throws StoreException{
        Doctor doctor = null;
        if (entity != null) {
            if (entity.getIsDoctor()) {
                doctor = (Doctor) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, doctor.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    doctor.setScope(Entity.Scope.SINGLE);
                    return get(new Doctor(), rs);
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
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private Entity doReadClinicalNoteForAppointment(String sql, Entity entity)throws StoreException{
        ClinicalNote clinicNote = null;
        if (entity != null) {
            if (entity.getIsClinicNote()) {
                clinicNote = (ClinicalNote) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    /*
    private Entity doReadClinicNoteForPatient(String sql, Entity entity)throws StoreException{
        ClinicalNote clinicNote = null;
        if (entity != null) {
            if (entity.getIsClinicNote()) {
                clinicNote = (ClinicalNote) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setInt(1, clinicNote.getPatientKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    clinicNote.setScope(Entity.Scope.FOR_PATIENT);
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
    */
    private Entity doReadMedicationWithKey(String sql, Entity entity)throws StoreException{
        Medication medication = null;
        if (entity != null) {
            if (entity.getIsMedication()) {
                medication = (Medication) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, medication.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    medication.setScope(Entity.Scope.SINGLE);
                    return get(new Medication(), rs);
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
    
    private Entity doReadClinicalNoteWithKey(String sql, Entity entity)throws StoreException{
        ClinicalNote clinicNote = null;
        if (entity != null) {
            if (entity.getIsClinicNote()) {
                clinicNote = (ClinicalNote) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, clinicNote.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    clinicNote.setScope(Entity.Scope.SINGLE);
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
    
    private Entity doReadTreatmentWithKey(String sql, Entity entity)throws StoreException{
        Treatment treatment = null;
        if (entity != null) {
            if (entity.getIsTreatment()) {
                treatment = (Treatment) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, treatment.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    treatment.setScope(Entity.Scope.SINGLE);
                    return get(treatment, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadTreatmentWithKey()",
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
    
    private Entity doReadQuestionWithKey(String sql, Entity entity)throws StoreException{
        Question question = null;
        if (entity != null) {
            if (entity.getIsQuestion()) {
                question = (Question) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, question.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    question.setScope(Entity.Scope.SINGLE);
                    return get(question, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadQuestionWithKey()",
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
                
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private Entity doReadPrimaryConditionForPatient(String sql, Entity entity)throws StoreException{
        /*
        PrimaryCondition pc = null;
        if (entity != null) {
            if (entity.getIsPrimaryCondition()) {
                pc = (PrimaryCondition) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, pc.getPatient().getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    pc.setScope(Entity.Scope.FOR_PATIENT);
                    return get(pc, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPrimaryConditionForPatient()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPrimaryConditionForPatient()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPrimaryConditionForPatient()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }*/
        return null;
    }
    
    private Entity doReadPrimaryConditionWithKey(String sql, Entity entity)throws StoreException{
        PrimaryCondition pc = null;
        if (entity != null) {
            if (entity.getIsPrimaryCondition()) {
                pc = (PrimaryCondition) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, pc.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    pc.setScope(Entity.Scope.SINGLE);
                    return get(pc, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPrimaryConditionWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadPrimaryConditionWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadPrimaryConditionWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadSecondaryConditionForPrimaryCondition(String sql, Entity entity)throws StoreException{
        SecondaryCondition sc = null;
        if (entity != null) {
            if (entity.getIsSecondaryCondition()) {
                sc = (SecondaryCondition) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private Entity doReadSecondaryConditionWithKey(String sql, Entity entity)throws StoreException{
        SecondaryCondition sc = null;
        if (entity != null) {
            if (entity.getIsSecondaryCondition()) {
                sc = (SecondaryCondition) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, sc.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    sc.setScope(Entity.Scope.SINGLE);
                    return get(new SecondaryCondition(), rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadSecondaryConditionWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type encountered in Repository::doReadSecondaryConditionWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient note undefined in doReadSecondaryConditionWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private Entity doReadPatientNoteWithKey(String sql, Entity entity)throws StoreException{
        /*
        PatientNoteDelegate delegate = null;
        if (entity != null) {
            if (entity.getIsPatientNote()) {
                delegate = (PatientNoteDelegate) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, delegate.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    delegate.setScope(Entity.Scope.SINGLE);
                    return get(delegate, rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientNoteWithKey()",
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
        */
        return null;
    }
    
    private Entity doReadPatientNotificationWithKey(String sql, Entity entity) throws StoreException{
        NotificationDelegate delegate;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                delegate = (NotificationDelegate) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, delegate.getKey());
                    ResultSet rs = preparedStatement.executeQuery();
                    return get(new Notification(), rs);
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doReadPatientNotificationWithKey()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> patient notifiation defined invalidly in Repository::doReadPatientNotificationWithKey()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notification undefined in Access::doReadPatientNotificationWithKey()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doInsertPatientNote(String sql, Entity entity)throws StoreException{
        PatientNoteDelegate  delegate;
        if (entity != null) {
            if (entity.getIsPatientNote()) {
                delegate = (PatientNoteDelegate)entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(delegate.getDatestamp()));
                    preparedStatement.setLong(2, delegate.getPatientKey());
                    preparedStatement.setString(3, delegate.getNote());
                    preparedStatement.setBoolean(4, false);
                    preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf(delegate.getLastUpdated()));
                    preparedStatement.setLong(6, delegate.getKey());
                    preparedStatement.execute();
                } catch (SQLException ex) {
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doInsertPatientNote()",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }catch(Exception ex){
                    throw new StoreException(ex.getMessage() +"\n"
                            + "Raised in Repository::doInsertPatientNote()",
                            StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                }
            }else {
                String msg = "StoreException -> Unexpected entity type encountered in Repository::doInsertPatientNote()";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        } else {
            String msg = "StoreException -> patient notificaion undefined in doInsertPatientNotification()";
            throw new StoreException(msg, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
        }
    }
    
    private void doInsertPatientNotification(String sql, Entity entity) throws StoreException{
        NotificationDelegate  delegate;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                delegate = (NotificationDelegate) entity;
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, ((PatientDelegate)delegate.getPatient()).getPatientKey());
                    preparedStatement.setDate(2, java.sql.Date.valueOf(delegate.getNotificationDate()));
                    preparedStatement.setString(3, delegate.getNotificationText());
                    preparedStatement.setBoolean(4, delegate.getIsActioned());
                    preparedStatement.setLong(5, delegate.getKey());
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
    
    private void doDeleteClinicalNote(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsClinicNote()){
                ClinicalNote clinicNote = (ClinicalNote)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, clinicNote.getKey());
                    preparedStatement.execute();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteClinicNote(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteClinicNote(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteClinicNote(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteTreatment(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsTreatment()){
                Treatment treatment = (Treatment)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, treatment.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteTreatment(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteTreatment(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteTreatment(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteQuestion(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsQuestion()){
                Question question = (Question)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, question.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteQuestion(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteQuestion(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteQuestion(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteCondition(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsPrimaryCondition() || entity.getIsSecondaryCondition()){
                Condition condition = (Condition)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private void doDeleteMedication(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsMedication()){
                Medication medication = (Medication)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, medication.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteMedicatiom(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteMedicatiom(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteMedicatiom(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeleteDoctor(String sql, Entity entity)throws StoreException{
        if (entity != null){
            if (entity.getIsDoctor()){
                Doctor doctor = (Doctor)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, doctor.getKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeleteMedicatiom(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeleteMedicatiom(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }   
        }else {
                String msg = "StoreException -> undefined entity type in doDeleteMedicatiom(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    private void doDeletePatientNote(String sql, Entity entity)throws StoreException{
        /*28/03/2024
        PatientNoteDelegate delegate = null;
        if (entity != null){
            if (entity.getIsPatientNote()){
                PatientNote patientNote = (PatientNote)entity;
                delegate = new PatientNoteDelegate(patientNote);
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setBoolean(1, true);
                    preparedStatement.setLong(3, patientNote.getPatientKey());
                    preparedStatement.executeUpdate();
                }catch (SQLException ex){
                    throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                            + "StoreException message -> exception raised in Repository::doDeletePatientNote(sql, entity)",
                            StoreException.ExceptionType.SQL_EXCEPTION);
                }
            } else {
                String msg = "StoreException -> unexpected entity type in doDeletePatientNote(sql, entity)";
                throw new StoreException(msg, StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
            }
        }
         */   
    }
    
    private void doDeleteNotification(String sql, Entity entity)throws StoreException{
        NotificationDelegate delegate;
        if (entity != null){
            if (entity.getIsPatientNotification()) {
                delegate = (NotificationDelegate) entity;
                //delegate.setKey(1);
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setBoolean(1, true);
                    preparedStatement.setLong(2, delegate.getKey());
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
    
    private void doUpdatePatientNote(String sql, Entity entity) throws StoreException{
        PatientNoteDelegate delegate;
        if (entity != null) {
            if (entity.getIsPatientNote()){
                    delegate = (PatientNoteDelegate)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    }
    
    private void doUpdateDoctor(String sql, Entity entity) throws StoreException{
        Doctor doctor;
        if (entity != null) {
            if (entity.getIsDoctor()){
                    doctor = (Doctor)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
    
    private void doUpdateClinicalNote(String sql, Entity entity) throws StoreException{
        ClinicalNote clinicNote;
        if (entity != null) {
            if (entity.getIsClinicNote()){
                    clinicNote = (ClinicalNote)entity;
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        PatientDelegate pDelegate;
        NotificationDelegate  delegate;
        if (entity != null) {
            if (entity.getIsPatientNotification()) {
                delegate = (NotificationDelegate) entity;
                pDelegate = (PatientDelegate)delegate.getPatient();
                try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.setLong(1, pDelegate.getPatientKey());
                    preparedStatement.setDate(2, java.sql.Date.valueOf(delegate.getNotificationDate()));
                    preparedStatement.setString(3, delegate.getNotificationText());
                    preparedStatement.setBoolean(4, delegate.getIsActioned());
                    preparedStatement.setLong(5, delegate.getKey());
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
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateClinicNoteTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateTreatmentTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateTreatmentTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    
    
    private void doCreateQuestionTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateQuestionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePatientQuestionTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientQuestionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreateAppointmentTreatmentTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateAppointmentTreatmentTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }

    
    private void doCreatePatientPrimaryConditionTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreatePatientPrimaryConditionTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePatientSecondaryConditionTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new StoreException("SQLException message -> " + ex.getMessage() + "\n"
                    + "StoreException message -> exception raised during doCreateDoctorTable(sql)",
                    StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    
    private void doCreatePatientNotificationTable(String sql) throws StoreException{
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        try {
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                    try {
                        PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
        try {
            PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                try {
                    for (Map.Entry<DayOfWeek, Boolean> entry : surgeryDaysAssignment.get().entrySet()) {
                        String sql = "UPDATE SurgeryDays SET IsSurgery = ? WHERE Day = ?;";
                        PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
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
                        /*+ "patientNoteKey LONG NOT NULL REFERENCES PatientNote(pid), "*/
                        + "start DateTime, "
                        + "duration LONG, "
                        + "notes char(255), "
                        + "isDeleted YesNo, "
                        + "hasPatientBeenContacted YesNo, "
                        + "isCancelled YesNo);";
                        break;
                    case "POSTGRES":
                        break;
                }
                doCreateAppointmentTable(sql);
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
                        /*+ "(PatientKey, Start, Duration, Notes,pid, patientNoteKey) "*/
                        + "(PatientKey, Start, Duration, notes, pid) "
                        + "VALUES (?,?,?,?,?);";
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
                        + "WHERE a.pid = ? "
                        + "AND isDeleted = false;";
                result = doReadAppointmentWithKey(sql, entity);
                break;
            case READ_APPOINTMENTS:
                sql = "SELECT * "
                        + "FROM Appointment "
                        + "WHERE isDeleted = false AND isCancelled = false;";
                result = doReadAppointments(sql,entity);
                break;
            case READ_CANCELLED_APPOINTMENTS:
                sql = "SELECT * "
                        + "FROM Appointment a "
                        + "WHERE isDeleted = false AND isCancelled = true "
                        + "ORDER BY a.Start ASC;";
                result = doReadCancelledAppointments(sql, entity);
                break;
            case READ_DELETED_APPOINTMENTS_FOR_PATIENT:
                sql = "SELECT * "
                        + "FROM Appointment "
                        + "WHERE isDeleted = true "
                        + "AND PatientKey = ?;";
                result = doReadAppointmentsForPatient(sql, entity);
                break;
            case READ_APPOINTMENTS_FOR_DAY:
                switch (repositoryName){
                    case "ACCESS":
                        sql = "select *"
                        + "from appointment as a "
                        + "where DatePart(\"yyyy\",a.start) = ? "
                        + "AND  DatePart(\"m\",a.start) = ? "
                        + "AND  DatePart(\"d\",a.start) = ? "
                        + "AND isDeleted = false "
                        + "AND isCancelled = false "        
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
            case UNCANCEL_APPOINTMENT:
                sql = "UPDATE Appointment "
                        + "SET isCancelled = false "
                        + "WHERE pid = ?;";
                doDeleteCancelChildEntity(sql, entity);
                break;
            case UPDATE_APPOINTMENT:
                sql = "UPDATE Appointment "
                        + "SET PatientKey = ?, "
                        + "Start = ?,"
                        + "Duration = ?,"
                        + "Notes = ?, "
                        /*28/03/2024+ "Notes = ?, "*/
                        + "hasPatientBeenContacted = ? "
                        /*28/03/2024+ "patientNoteKey = ? "*/
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
                        + "notes Char(255), "
                        + "guardianKey Long, "
                        + "isDeleted YesNo);";
                doCreatePatientTable(sql);
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
                    + "isGuardianAPatient,recallFrequency, recallDate, notes,pid, guardianKey, email) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
                doInsertPatient(sql, entity);
                break;
            case READ_PATIENT:
                sql = "SELECT * "
                        + "FROM Patient "
                        + "WHERE pid=? "
                        + "AND isDeleted = false;";
                result = doReadPatientWithKey(sql, entity);
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
                        + "ORDER BY surname, forenames ASC;";
                result = doReadPatientsAll(sql, (Patient)entity);
                break;
            case READ_DELETED_PATIENTS:
                sql = "SELECT * "
                        + "FROM Patient "
                        + "WHERE isDeleted = true "
                        + "ORDER BY surname, forenames ASC;";
                result = doReadDeletedPatients(sql, (Patient)entity);
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
                    + "email = ? "
                    + "WHERE pid = ? ;";
                doUpdatePatient(sql, entity);
                break;
            /*
            case COUNT_SECONDARY_CONDITION:
                sql = "SELECT COUNT(*) as row_count FROM PrimaryCondition;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM PrimaryCondition "
                        + "WHERE isDeleted = true ;";
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            case COUNT_PRIMARY_CONDITION:
                sql = "SELECT COUNT(*) as row_count FROM SecondaryCondition;";
                result.setValue(doCount(sql,entity).getValue());
                sql = "SELECT COUNT(*) as row_count "
                        + "FROM SecondaryCondition "
                        + "WHERE isDeleted = true ;";
                result.setValue(new Point(result.getValue().x,doCount(sql,entity).getValue().x));
                break;
            case READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM SecondaryCondition;";
                result = doReadHighestKey(sql);
                break;
            case READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY:
                sql = "SELECT MAX(pid) as highest_key "
                        + "FROM PrimaryCondition;";
                result = doReadHighestKey(sql);
                break;
            case INSERT_SECONDARY_CONDITION:
                sql
                    = "INSERT INTO PrimaryCondition "
                    + "(pid, patientKey,description, state, notes) "
                    + "VALUES (?,?,?,?,?);";
                doInsertPrimaryCondition(sql, entity);
                break;
            case INSERT_SECONDARY_CONDITION:
                sql
                    = "INSERT INTO PrimaryCondition "
                    + "(pid, primaryConditionKey, description,state) "
                    + "VALUES (?,?,?,?);";
                doInsertSecondaryCondition(sql, entity);
                break;
                */
        }
        return result;
    }
    
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
                doCreateClinicalNoteTable(sql);
                break; 
            case DELETE_ALL_CLINICAL_NOTE:
                sql = "DELETE FROM ClinicalNote;";
                doDelete(sql);
                break;
            case DELETE_CLINICAL_NOTE:
                sql = "DELETE FROM ClinicalNote "
                        + "WHERE pid = ?;";
                doDeleteClinicalNote(sql,entity);
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
                result = doReadClinicalNoteWithKey(sql, entity);
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
                doCreateTreatmentTable(sql);
                break; 
            case DELETE_ALL_TREATMENT:
                sql = "DELETE FROM Treatment;";
                doDelete(sql);
                break;
            case DELETE_TREATMENT:
                sql = "UPDATE Treatment "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteTreatment(sql,entity);
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
                result = doReadTreatmentWithKey(sql, entity);
                break;
            case READ_ALL_TREATMENT:
                sql = "SELECT * "
                        + "FROM Treatment "
                        + "WHERE isDeleted = false "
                        + "ORDER BY description ASC; ";
                result = doReadTreatmentAll(sql, (Treatment)entity);
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
                doCreateQuestionTable(sql);
                break; 
            case DELETE_ALL_QUESTION:
                sql = "DELETE FROM Question;";
                doDelete(sql);
                break;
            case DELETE_QUESTION:
                sql = "UPDATE Question "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteQuestion(sql,entity);
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
                result = doReadQuestionWithKey(sql, entity);
                break;
            case READ_ALL_QUESTION:
                sql = "SELECT * "
                        + "FROM Question "
                        + "WHERE isDeleted = false "
                        + "ORDER BY sortorder ASC; ";
                result = doReadQuestionAll(sql, (Question)entity);
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
                doCreateAppointmentTreatmentTable(sql);
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
                doCreatePatientQuestionTable(sql);
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
                doCreatePatientPrimaryConditionTable(sql);
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
                doCreatePatientSecondaryConditionTable(sql);
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
                doCreateDoctorTable(sql);
                break; 
            case DELETE_ALL_DOCTOR:
                sql = "DELETE FROM Doctor;";
                doDelete(sql);
                break;
            case DELETE_DOCTOR:
                sql = "UPDATE Doctor "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteDoctor(sql,entity);
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
                result = doReadDoctorWithKey(sql, entity);
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
                doCreateMedicationTable(sql);
                break;
            case DELETE_ALL_MEDICATION:
                sql = "DELETE FROM Medication;";
                doDelete(sql);
                break;
            case DELETE_MEDICATION:
                sql = "UPDATE Medication "
                        + "SET isdeleted = true "
                        + "WHERE pid = ?;";
                doDeleteMedication(sql,entity);
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
                result = doReadMedicationWithKey(sql, entity);
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
                doCreatePrimaryConditionTable(sql);
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
                result = doReadPrimaryConditionWithKey(sql, entity);
                break;
                
            case READ_ALL_PRIMARY_CONDITION:
                sql = "SELECT * "
                        + "FROM PrimaryCondition "
                        + "WHERE isDeleted = false "
                        + "ORDER BY description ASC";
                result = doReadPrimaryConditionAll(sql, entity);
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
                doCreateSecondaryConditionTable(sql);
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
                result = doReadSecondaryConditionWithKey(sql, entity);
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
                result = doReadSecondaryConditionAll(sql, entity);
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
    
    
    private Entity doPMSSQLforPatientNote(Repository.PMSSQL q, Entity entity) throws StoreException{
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
                doCreatePatientNoteTable(sql);
                break;
            case DELETE_PATIENT_NOTE:
                sql = "UPDATE PatientNote "
                        + "SET isDeleted = true "
                        + "WHERE pid = ?";

                doDeletePatientNote(sql, entity);
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
                result = doReadPatientNoteWithKey(sql, entity);
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
    }
    
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
                doCreatePatientNotificationTable(sql);
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
                result = doReadPatientNotificationWithKey(sql, entity);
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
                result = doReadPatientNotifications(sql, entity);
                break;
            case READ_NOTIFICATIONS:
                sql = "SELECT * FROM PatientNotification "
                        + "WHERE isDeleted = false "
                        + "AND isCancelled = false "
                        + "ORDER BY notificationDate DESC;";
                result = doReadPatientNotifications(sql, entity);
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
                doCreateSurgeryDaysAssignmentTable(sql);
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
        runSQL(Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT, 
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
        AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        PatientDelegate pDelegate = new PatientDelegate(delegate.getPatient());
        /*28/03/2024PatientNoteDelegate noteDelegate = new PatientNoteDelegate();*/
        
        pDelegate.setPatientKey(appointeeKey);
        delegate.setPatient(pDelegate);
        /*28/03/2024noteDelegate.setKey(patientNoteKey);
        delegate.setPatientNote(noteDelegate);*/
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.APPOINTMENT,
                Repository.PMSSQL.READ_APPOINTMENT_NEXT_HIGHEST_KEY,null);
        entity = (Entity)client;
        if (entity.getValue()!=null) {
            delegate.setAppointmentKey(entity.getValue().x + 1);
            runSQL(Repository.EntitySQL.APPOINTMENT,Repository.PMSSQL.INSERT_APPOINTMENT, delegate);
            return delegate.getAppointmentKey();
        }
        else {
            displayErrorMessage("Unable to calculate a new key value for the new Appointment.\n"
                    + "Error raised in Repository::insert(Appointment) : Integer",
                    "Access store error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    /*28/03/2024public Integer insert(PatientNote patientNote)throws StoreException{
        
        PatientNoteDelegate delegate = null;
        IStoreClient client = null;
        Entity entity = null; 
        client = runSQL(Repository.EntitySQL.PATIENT_NOTE,
                    Repository.PMSSQL.READ_PATIENT_NOTE_NEXT_HIGHEST_KEY,patientNote);
        entity = (Entity)client;
        delegate = new PatientNoteDelegate(patientNote);
        delegate.setKey(entity.getValue().x + 1);
        runSQL(Repository.EntitySQL.PATIENT_NOTE,Repository.PMSSQL.INSERT_PATIENT_NOTE, delegate);
        return delegate.getKey();
        
        
    }*/

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
        NotificationDelegate delegate = null;
        PatientDelegate pDelegate = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,
                    Repository.PMSSQL.READ_NOTIFICATION_NEXT_HIGHEST_KEY,pn);
        entity = (Entity)client;
        delegate = new NotificationDelegate(pn);
        delegate.setKey(entity.getValue().x + 1);
        pDelegate = new PatientDelegate(delegate.getPatient());
        pDelegate.setPatientKey(patientKey);
        delegate.setPatient(pDelegate);
        //30/07/2022 09:26
        runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,
                Repository.PMSSQL.INSERT_NOTIFICATION, delegate);
        return delegate.getKey();
    }
    
    
    
    @Override
    public Integer insert(Doctor doctor)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.DOCTOR,
                    Repository.PMSSQL.READ_DOCTOR_NEXT_HIGHEST_KEY,doctor);
        entity = (Entity)client;
        doctor.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntitySQL.DOCTOR,
                Repository.PMSSQL.INSERT_DOCTOR, doctor);
        
        return doctor.getKey();
    }
    
    @Override
    public Integer insert(Medication medication)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.MEDICATION,
                    Repository.PMSSQL.READ_MEDICATION_NEXT_HIGHEST_KEY,medication);
        entity = (Entity)client;
        medication.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntitySQL.MEDICATION,
                Repository.PMSSQL.INSERT_MEDICATION, medication);
        
        return medication.getKey();
    }
    
    @Override
    public Integer insert(ClinicalNote clinicNote)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        /*
        client = runSQL(Repository.EntitySQL.CLINICAL_NOTE,
                    Repository.PMSSQL.READ_CLINIC_NOTE_NEXT_HIGHEST_KEY,clinicNote);
        entity = (Entity)client;
        clinicNote.setKey(entity.getValue().x + 1);
        */
        runSQL(Repository.EntitySQL.CLINICAL_NOTE,
                Repository.PMSSQL.INSERT_CLINICAL_NOTE, clinicNote);
        
        return clinicNote.getKey();
    }
    
    @Override
    public Integer insert(Treatment treatment)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.TREATMENT,
                    Repository.PMSSQL.READ_TREATMENT_NEXT_HIGHEST_KEY,treatment);
        entity = (Entity)client;
        treatment.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntitySQL.TREATMENT,
                Repository.PMSSQL.INSERT_TREATMENT, treatment);
        
        return treatment.getKey();
    }
    
     @Override
    public Integer insert(Question question)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.QUESTION,
                    Repository.PMSSQL.READ_QUESTION_NEXT_HIGHEST_KEY,question);
        entity = (Entity)client;
        question.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntitySQL.QUESTION,
                Repository.PMSSQL.INSERT_QUESTION, question);
        
        return question.getKey();
    }
    
    @Override
    public Integer insert(PatientCondition patientCondition)throws StoreException{
        
        if (patientCondition.getIsPatientPrimaryCondition()){
            runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
                    Repository.PMSSQL.INSERT_PATIENT_PRIMARY_CONDITION, patientCondition);
        }
        else if(patientCondition.getIsPatientSecondaryCondition()){
            runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION,
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
    /*
    @Override
    public Integer insert(PatientPrimaryCondition patientPrimaryCondition)throws StoreException{
        runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
                Repository.PMSSQL.INSERT_PATIENT_PRIMARY_CONDITION, patientPrimaryCondition);
        
        return null;
    }
    
    @Override
    public Integer insert(PatientSecondaryCondition patientSecondaryCondition)throws StoreException{
        runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION,
                Repository.PMSSQL.INSERT_PATIENT_SECONDARY_CONDITION, patientSecondaryCondition);
        
        return null;
    }
    */
    @Override
    public Integer insert(AppointmentTreatment appointmentTreatment)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        /*
        client = runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,
                    Repository.PMSSQL.READ_APPOINTMENT_TREATMENT_NEXT_HIGHEST_KEY,appointmentTreatment);
        entity = (Entity)client;
        appointmentTreatment.setKey(entity.getValue().x + 1);*/

        runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,
                Repository.PMSSQL.INSERT_APPOINTMENT_TREATMENT, appointmentTreatment);
        
        return null;
    }
    
    @Override
    public Integer insert(PatientQuestion appointmentQuestion)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;

        runSQL(Repository.EntitySQL.PATIENT_QUESTION,
                Repository.PMSSQL.INSERT_PATIENT_QUESTION, appointmentQuestion);
        
        return null;
    }
    
    @Override
    public Integer insert(PrimaryCondition pc)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.PRIMARY_CONDITION,
                    Repository.PMSSQL.READ_PRIMARY_CONDITION_NEXT_HIGHEST_KEY,pc);
        entity = (Entity)client;
        pc.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntitySQL.PRIMARY_CONDITION,
                Repository.PMSSQL.INSERT_PRIMARY_CONDITION, pc);
        
        return pc.getKey();
    }
    
    @Override
    public Integer insert(SecondaryCondition sc)throws StoreException{
        Entity key = null;
        Entity entity;
        IStoreClient client;
        client = runSQL(Repository.EntitySQL.SECONDARY_CONDITION,
                    Repository.PMSSQL.READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY,sc);
        entity = (Entity)client;
        sc.setKey(entity.getValue().x + 1);

        runSQL(Repository.EntitySQL.SECONDARY_CONDITION,
                Repository.PMSSQL.INSERT_SECONDARY_CONDITION, sc);
        
        return sc.getKey();
    }
    
    /*
    @Override
    public Integer insert(SecondaryCondition sc)throws StoreException{
        IStoreClient client = null;
        Entity entity = null;
        Integer key = null;
        
        client = runSQL(Repository.EntitySQL.SECONDARY_CONDITION,
                    Repository.PMSSQL.READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY,sc);
        entity = (Entity)client;
        sc.setKey(entity.getValue().x + 1);
        try{
            getPMSStoreConnection().setAutoCommit(false);
            runSQL(Repository.EntitySQL.SECONDARY_CONDITION,
                    Repository.PMSSQL.INSERT_SECONDARY_CONDITION,sc);
            if (!sc.get().isEmpty()){
                key = entity.getValue().x;
                client = runSQL(Repository.EntitySQL.PATIENT,
                            Repository.PMSSQL
                                    .READ_SECONDARY_CONDITION_NEXT_HIGHEST_KEY,pc);
                entity = (Entity)client;
                Integer nextSecondaryConditionKey = entity.getValue().x + 1;
                for (Patient.MedicalHistory.PrimaryCondition.SecondaryCondition sc : pc.get()){
                    sc.setKey(++nextSecondaryConditionKey);
                    sc.setPrimaryConditionKey(pc.getKey());
                    runSQL(Repository.EntitySQL.PATIENT,
                        Repository.PMSSQL.INSERT_SECONDARY_CONDITION,sc);  
                }
            }
            getPMSStoreConnection().commit();
        }catch (SQLException ex){
            try{
                getPMSStoreConnection().rollback();
            }catch(SQLException exc){
                String message = exc.getMessage() + "\n"
                        + "Raised on attempt to rollback transaction in "
                        + "repository::insert(PrimaryCondition)";
                throw new StoreException(message,StoreException.ExceptionType.SQL_EXCEPTION);
            }
            String message = ex.getMessage() + "\n"
                    + "Raised in Repository::insert(PrimaryCondition";
            throw new StoreException(message,StoreException.ExceptionType.SQL_EXCEPTION);
        }
    }
    */
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
        PatientDelegate delegate;
        PatientDelegate gDelegate;
        delegate = new PatientDelegate(patient);
        if (patientKey==null){
            if (delegate.getIsGuardianAPatient()){
                if (guardianKey==null){
                    throw new StoreException("StoreException raised in AccessRepository.insert(Patient...) because expected a non null guardian key value.",
                    StoreException.ExceptionType.UNEXPECTED_NULL_GUARDIAN_KEY);
                }
                else{
                    gDelegate = new PatientDelegate(delegate.getGuardian());
                    gDelegate.setPatientKey(guardianKey);
                }
            }
            else{
                gDelegate = new PatientDelegate(0);
                //gDelegate.setPatientKey(0);
            }
            client = runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.READ_PATIENT_NEXT_HIGHEST_KEY, new Patient());
            entity = (Entity)client;
            if (entity.getValue()!=null)
                delegate.setPatientKey(entity.getValue().x + 1);
        }else{
            delegate.setPatientKey(patientKey);
            gDelegate = new PatientDelegate(0);
            //gDelegate.setPatientKey(0);
        }
        delegate.setGuardian(gDelegate);           
        runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.INSERT_PATIENT, delegate);
        result =  delegate.getPatientKey();
        return result;
    }
   
    @Override
    public void recover(Appointment appointment, Integer appointmentKey)throws StoreException{
        switch(appointment.getScope()){
            case DELETED:
               AppointmentDelegate delegate = new AppointmentDelegate(appointment);
               delegate.setAppointmentKey(appointmentKey);
               runSQL(Repository.EntitySQL.APPOINTMENT,Repository.PMSSQL.RECOVER_APPOINTMENT, delegate);
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
               PatientDelegate delegate = new PatientDelegate(patient);
               delegate.setPatientKey(patientKey);
               runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.RECOVER_PATIENT, delegate);
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
                runSQL(Repository.EntitySQL.PATIENT_NOTE,Repository.PMSSQL.RECOVER_PATIENT_NOTE, delegate);
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
                NotificationDelegate delegate = 
                        new NotificationDelegate(patientNotification);
                delegate.setKey(patientNotificationKey);
                runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.RECOVER_NOTIFICATION, delegate);
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
                PatientDelegate delegate = new PatientDelegate(patient);
                delegate.setPatientKey(patientKey);
                runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.DELETE_PATIENT, delegate);
                break;
            case ALL: //migration data function only
                runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.DELETE_ALL_PATIENT, null);
        }
    }

    /*28/03/2024
    public void delete(PatientNote patientNote, LocalDateTime datestamp, Integer patientKey)throws StoreException{
        if (patientNote.getScope()!=null){
            switch(patientNote.getScope()){
               case SINGLE:
                   if ((datestamp!=null)&&(patientKey!=null))
                       runSQL(Repository.EntitySQL.PATIENT_NOTE,Repository.PMSSQL.DELETE_PATIENT_NOTE,patientNote);
                   else{
                       String error = "datestamp or patient key undefined. " 
                            + "Raised in Repository.delete(PatientNote, datestamp, patientKey)";
                        throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
                   }
                   break;
               case ALL:
                   runSQL(Repository.EntitySQL.PATIENT_NOTE,Repository.PMSSQL.DELETE_PATIENT_NOTES,null);
                   break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + patientNote.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(PatientNote)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of patient note delete operation undefined (" 
                    + patientNote.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(PatientNote)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }*/
   
    @Override
    public void delete(PrimaryCondition primaryCondition)throws StoreException{
        if (primaryCondition.getScope()!=null){
            switch(primaryCondition.getScope()){
               
               case SINGLE:
                   runSQL(Repository.EntitySQL.PRIMARY_CONDITION,Repository.PMSSQL.DELETE_PRIMARY_CONDITION,primaryCondition);
                   break;
               case ALL:
                   runSQL(Repository.EntitySQL.PRIMARY_CONDITION,Repository.PMSSQL.DELETE_ALL_PRIMARY_CONDITION,null);
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
                    runSQL(Repository.EntitySQL.SECONDARY_CONDITION,Repository.PMSSQL.DELETE_SECONDARY_CONDITION,secondaryCondition);
                    break;
                case ALL:
                    runSQL(Repository.EntitySQL.SECONDARY_CONDITION,Repository.PMSSQL.DELETE_ALL_SECONDARY_CONDITION,null);
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
                    runSQL(Repository.EntitySQL.DOCTOR,Repository.PMSSQL.DELETE_DOCTOR,doctor);
                    break;
               case ALL:
                   runSQL(Repository.EntitySQL.DOCTOR,Repository.PMSSQL.DELETE_ALL_DOCTOR,null);
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
    public void delete(Medication medication)throws StoreException{
        if (medication.getScope()!=null){
            switch(medication.getScope()){
               case SINGLE:
                    runSQL(Repository.EntitySQL.MEDICATION,Repository.PMSSQL.DELETE_MEDICATION,medication);
                    break;
               case ALL:
                    runSQL(Repository.EntitySQL.MEDICATION,Repository.PMSSQL.DELETE_ALL_MEDICATION,null);
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
    public void delete(ClinicalNote clinicNote)throws StoreException{
        if (clinicNote.getScope()!=null){
            switch(clinicNote.getScope()){
               case SINGLE:
                    runSQL(Repository.EntitySQL.CLINICAL_NOTE,Repository.PMSSQL.DELETE_CLINICAL_NOTE,clinicNote);
                    break;
               case ALL:
                    runSQL(Repository.EntitySQL.CLINICAL_NOTE,Repository.PMSSQL.DELETE_ALL_CLINICAL_NOTE,null);
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
                    runSQL(Repository.EntitySQL.TREATMENT,Repository.PMSSQL.DELETE_TREATMENT,treatment);
                    break;
               case ALL:
                    runSQL(Repository.EntitySQL.TREATMENT,Repository.PMSSQL.DELETE_ALL_TREATMENT,null);
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
    public void delete(Question question)throws StoreException{
        if (question.getScope()!=null){
            switch(question.getScope()){
               case SINGLE:
                    runSQL(Repository.EntitySQL.QUESTION,Repository.PMSSQL.DELETE_QUESTION,question);
                    break;
               case ALL:
                    runSQL(Repository.EntitySQL.QUESTION,Repository.PMSSQL.DELETE_ALL_QUESTION,null);
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
                    runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,Repository.PMSSQL.DELETE_APPOINTMENT_TREATMENT,appointmentTreatment);
                    break;
               case ALL:
                    runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,Repository.PMSSQL.DELETE_ALL_APPOINTMENT_TREATMENT,null);
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
                    runSQL(Repository.EntitySQL.PATIENT_QUESTION,Repository.PMSSQL.DELETE_PATIENT_QUESTION,patientQuestion);
                    break;
               case ALL:
                    runSQL(Repository.EntitySQL.PATIENT_QUESTION,Repository.PMSSQL.DELETE_ALL_PATIENT_QUESTION,null);
                    break;
               default:
                    String error = "Unexpected scope encountered (" 
                            + patientQuestion.getScope().toString() + ")\n" 
                            + "Raised in Repository.delete(PatientQuestion)";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.STORE_EXCEPTION);
            }
        }else{
            String error = "Scope of patientQuestion delete operation undefined (" 
                    + patientQuestion.getScope().toString() + ")\n" 
                    + "Raised in Repository.delete(PatientQuestion)";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void delete(PatientCondition patientCondition)throws StoreException{
        boolean isError = false;
        Repository.EntitySQL entitySQL = null;
        Repository.PMSSQL sqlStatement = null;
        if (patientCondition.getIsPatientPrimaryCondition())
            entitySQL = Repository.EntitySQL.PATIENT_PRIMARY_CONDITION;
        else if(patientCondition.getIsPatientSecondaryCondition())
            entitySQL = Repository.EntitySQL.PATIENT_SECONDARY_CONDITION;
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
                    runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,Repository.PMSSQL.DELETE_ALL_PATIENT_PRIMARY_CONDITION,null);
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
        NotificationDelegate delegate = new NotificationDelegate(patientNotification);
        switch (patientNotification.getScope()){
            /*
            case SINGLE:
                if (key != null){
                    delegate.setKey(key);
                    runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.DELETE_PATIENT_NOTIFICATION,delegate);
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
                runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.DELETE_NOTIFICATIONS,null);
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
       runSQL(Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT,
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
        AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        switch(appointment.getScope()){
            
            case SINGLE:
                delegate.setAppointmentKey(key);
                runSQL(Repository.EntitySQL.APPOINTMENT, 
                        Repository.PMSSQL.DELETE_APPOINTMENT, appointment);
                break;
            
            case ALL:
                runSQL(Repository.EntitySQL.APPOINTMENT, 
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
        value = (Entity)runSQL(Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT,Repository.PMSSQL.READ_SURGERY_DAYS_ASSIGNMENT, null);
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
                    entity = (Entity)runSQL(Repository.EntitySQL.PRIMARY_CONDITION, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.PRIMARY_CONDITION,
                            Repository.PMSSQL.READ_ALL_PRIMARY_CONDITION, pc);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.PRIMARY_CONDITION,
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
                    entity = (Entity)runSQL(Repository.EntitySQL.SECONDARY_CONDITION, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.SECONDARY_CONDITION,
                            Repository.PMSSQL.READ_ALL_SECONDARY_CONDITION, sc);
                break;
            case FOR_PRIMARY_CONDITION:
                entity = (Entity)runSQL(Repository.EntitySQL.SECONDARY_CONDITION,
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
                    entity = (Entity)runSQL(Repository.EntitySQL.MEDICATION, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.MEDICATION,
                            Repository.PMSSQL.READ_ALL_MEDICATION, medication);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.MEDICATION,
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
    public ClinicalNote read(ClinicalNote clinicNote)throws StoreException{
        ClinicalNote result = null;
        Entity entity = null;

        switch(clinicNote.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntitySQL.CLINICAL_NOTE, 
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
                    entity = (Entity)runSQL(Repository.EntitySQL.CLINICAL_NOTE, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.CLINICAL_NOTE,
                            Repository.PMSSQL.READ_ALL_CLINICAL_NOTE, clinicNote);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.CLINICAL_NOTE,
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
                    entity = (Entity)runSQL(Repository.EntitySQL.TREATMENT, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.TREATMENT,
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
    public Question read(Question question)throws StoreException{
        Question result = null;
        Entity entity = null;

        switch(question.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntitySQL.QUESTION, 
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
            case ALL:
                entity = (Entity)runSQL(Repository.EntitySQL.QUESTION,
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
                    entity = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,
                            Repository.PMSSQL.READ_ALL_APPOINTMENT_TREATMENT, appointmentTreatment);
                break;
            case FOR_APPOINTMENT:
                entity = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,
                            Repository.PMSSQL.READ_APPOINTMENT_TREATMENT_FOR_APPOINTMENT, appointmentTreatment);
                break;
            case FOR_TREATMENT:
                entity = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,
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
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_QUESTION, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_QUESTION,
                            Repository.PMSSQL.READ_ALL_PATIENT_QUESTION, patientQuestion);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_QUESTION,
                            Repository.PMSSQL.READ_PATIENT_QUESTION_FOR_PATIENT, patientQuestion);
                break;
            case FOR_QUESTION:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_QUESTION,
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
        Repository.EntitySQL entitySQL = null;
        Repository.PMSSQL sqlStatement = null;
        if(patientCondition.getIsPatientPrimaryCondition())
            entitySQL = Repository.EntitySQL.PATIENT_PRIMARY_CONDITION;
        else entitySQL = Repository.EntitySQL.PATIENT_SECONDARY_CONDITION;
        
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
    /*
    @Override
    public PatientPrimaryCondition read(PatientPrimaryCondition patientPrimaryCondition)throws StoreException{
        PatientPrimaryCondition result = null;
        Entity entity = null;

        switch(patientPrimaryCondition.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION, 
                            Repository.PMSSQL.READ_PATIENT_PRIMARY_CONDITION, patientPrimaryCondition);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(PatientPrimaryCondition)" 
                                + patientPrimaryCondition.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case ALL:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
                            Repository.PMSSQL.READ_ALL_PATIENT_PRIMARY_CONDITION, patientPrimaryCondition);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
                            Repository.PMSSQL.READ_PATIENT_PRIMARY_CONDITION_FOR_PATIENT, patientPrimaryCondition);
                break;
            case FOR_PRIMARY_CONDITION:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
                            Repository.PMSSQL.READ_PATIENT_PRIMARY_CONDITION_FOR_PRIMARY_CONDITION, patientPrimaryCondition);
                break;
        }
        if (entity!=null){
            if (entity.getIsPatientPrimaryCondition()){
                result = (PatientPrimaryCondition)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(PatientPrimaryCondition)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PatientPrimaryCondition)" 
                        + patientPrimaryCondition.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    
    @Override
    public PatientSecondaryCondition read(PatientSecondaryCondition patientSecondaryCondition)throws StoreException{
        PatientSecondaryCondition result = null;
        Entity entity = null;

        switch(patientSecondaryCondition.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION, 
                            Repository.PMSSQL.READ_PATIENT_SECONDARY_CONDITION, patientSecondaryCondition);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(PatientSecondaryCondition)" 
                                + patientSecondaryCondition.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }
            case ALL:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION,
                            Repository.PMSSQL.READ_ALL_PATIENT_SECONDARY_CONDITION, patientSecondaryCondition);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION,
                            Repository.PMSSQL.READ_PATIENT_SECONDARY_CONDITION_FOR_PATIENT, patientSecondaryCondition);
                break;
            case FOR_SECONDARY_CONDITION:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION,
                            Repository.PMSSQL.READ_PATIENT_SECONDARY_CONDITION_FOR_SECONDARY_CONDITION, patientSecondaryCondition);
                break;
        }
        if (entity!=null){
            if (entity.getIsPatientSecondaryCondition()){
                result = (PatientSecondaryCondition)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(PatientSecondaryCondition)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PatientSecondaryCondition)" 
                        + patientSecondaryCondition.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }
    */
    @Override
    public Doctor read(Doctor doctor)throws StoreException{
        Doctor result = null;
        Entity entity = null;

        switch(doctor.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntitySQL.DOCTOR, 
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
                entity = (Entity)runSQL(Repository.EntitySQL.DOCTOR,
                            Repository.PMSSQL.READ_ALL_DOCTOR, doctor);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.DOCTOR,
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
    
    /*28/03/2024
    public PatientNote read(PatientNote patientNote, Integer key)throws StoreException{
        PatientNote result = null;
        Entity entity = null;
        PatientNoteDelegate delegate = new PatientNoteDelegate();
        delegate.setKey(key);
        switch(patientNote.getScope()){
            case SINGLE:{
                try{
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTE, 
                            Repository.PMSSQL.READ_PATIENT_NOTE, 
                            delegate);
                }catch(StoreException ex){
                    String message = "";
                    throw new StoreException(
                        message + "StoreException raised -> null value returned from persistent store "
                            + "in method Repository::read(PatientNote[" 
                                + patientNote.getScope().toString() + "])\n",
                        StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
                }
                break;
            }       
            case ALL:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTE,
                            Repository.PMSSQL.READ_ALL_PATIENT_NOTES, 
                            patientNote);
                break;
            case FOR_PATIENT:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTE,
                            Repository.PMSSQL.READ_NOTES_FOR_PATIENT, 
                            patientNote);
                break;
        }
        if (entity!=null){
            if (entity.getIsPatientNote()){
                result = (PatientNote)entity;
                return result;
            }else{
                String message = "";
                throw new StoreException(

                    message + "StoreException raised -> unexpected entity type returned from persistent store "
                        + "in method Repository::read(PatientNote)\n",
                    StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED); 
            }
        }else{
            String message = "";
            throw new StoreException(
                message + "StoreException raised -> null value returned from persistent store "
                    + "in method Repository::read(PatientNote[" 
                        + patientNote.getScope().toString() + "])\n",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
        }
    }*/
    
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
                NotificationDelegate patientNotificationDelegate = new NotificationDelegate(patientNotification);
                patientNotificationDelegate.setKey(key);
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION, 
                            Repository.PMSSQL.READ_NOTIFICATION, 
                            patientNotificationDelegate);
            case UNACTIONED:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_UNACTIONED_NOTIFICATIONS, 
                            patientNotification);
                break;
            case ALL:
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_NOTIFICATIONS, 
                            patientNotification);
                break;
            case FOR_PATIENT:
                PatientDelegate patientDelegate = new PatientDelegate(patientNotification.getPatient());
                patientDelegate.setPatientKey(key);
                patientNotification.setPatient(patientDelegate);
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,
                            Repository.PMSSQL.READ_NOTIFICATIONS_FOR_PATIENT, 
                            patientNotification);
                break;
            case DELETED_FOR_PATIENT:
                patientDelegate = new PatientDelegate(patientNotification.getPatient());
                patientDelegate.setPatientKey(key);
                patientNotification.setPatient(patientDelegate);
                entity = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,
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
        NotificationDelegate delegate = new NotificationDelegate(notification);
        delegate.setKey(notificationKey);
        runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.UNCANCEL_NOTIFICATION, delegate);
    }
    
    public void uncancel(Appointment appointment, Integer appointmentKey)throws StoreException{
        AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        delegate.setAppointmentKey(appointmentKey);
        runSQL(Repository.EntitySQL.APPOINTMENT,Repository.PMSSQL.UNCANCEL_APPOINTMENT, delegate);
    }
    
    public void cancel(Notification notification, Integer notificationKey)throws StoreException{
        NotificationDelegate delegate = new NotificationDelegate(notification);
        delegate.setKey(notificationKey);
        runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.CANCEL_NOTIFICATION, delegate);
    }
    
    public void cancel(Appointment appointment, Integer appointmentKey)throws StoreException{
        AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        delegate.setAppointmentKey(appointmentKey);
        runSQL(Repository.EntitySQL.APPOINTMENT,Repository.PMSSQL.CANCEL_APPOINTMENT, delegate);
    }
    
    /*28/03/2024
    public Point count(PatientNote patientNote)throws StoreException{
        Entity result = null;
        if (patientNote !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_NOTES;
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTE, sqlStatement, patientNote);
        }
        return result.getValue();
    }*/
    
    @Override
    public Point count(Doctor doctor)throws StoreException{
        Entity result = null;
        if (doctor !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_DOCTOR;
            result = (Entity)runSQL(Repository.EntitySQL.DOCTOR, 
                    sqlStatement, doctor);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(ClinicalNote clinicNote)throws StoreException{
        Entity result = null;
        if (clinicNote !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_CLINICAL_NOTE;
            result = (Entity)runSQL(Repository.EntitySQL.CLINICAL_NOTE, 
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
            result = (Entity)runSQL(Repository.EntitySQL.TREATMENT, 
                    sqlStatement, treatment);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(Question treatment)throws StoreException{
        Entity result = null;
        if (treatment !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_QUESTION;
            result = (Entity)runSQL(Repository.EntitySQL.QUESTION, 
                    sqlStatement, treatment);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(PatientQuestion patientQuestion)throws StoreException{
        Entity result = null;
        if (patientQuestion !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_QUESTION;
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT_QUESTION, 
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
            result = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT, 
                    sqlStatement, appointmentTreatment);
        }
        return result.getValue();
    }
    
    @Override
    public Point count(PatientCondition patientCondition)throws StoreException{
        Entity result = null;
        Repository.EntitySQL entitySQL = null;
        Repository.PMSSQL sqlStatement = null;
        if(patientCondition.getIsPatientPrimaryCondition()){
            entitySQL = Repository.EntitySQL.PATIENT_PRIMARY_CONDITION;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_PRIMARY_CONDITION;
        }else if(patientCondition.getIsPatientSecondaryCondition()){
            entitySQL = Repository.EntitySQL.PATIENT_SECONDARY_CONDITION;
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
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION, 
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
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION, 
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
            result = (Entity)runSQL(Repository.EntitySQL.MEDICATION, 
                    sqlStatement, medication);
        }
        return result.getValue();
    }
    
    public Point count(SecondaryCondition sc)throws StoreException{
        Entity result = null;
        if (sc !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_SECONDARY_CONDITION;
            result = (Entity)runSQL(Repository.EntitySQL.SECONDARY_CONDITION, 
                    sqlStatement, sc);
        }
        return result.getValue();
    }
    
    public Point count(PrimaryCondition pc)throws StoreException{
        Entity result = null;
        if (pc !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PRIMARY_CONDITION;
            result = (Entity)runSQL(Repository.EntitySQL.PRIMARY_CONDITION, 
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
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION, sqlStatement, notification);
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
                    PatientDelegate delegate = new PatientDelegate(appointeeKey);
                    //delegate.setPatientKey(appointeeKey);
                    appointment.setPatient(delegate);
                    sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENTS_FOR_PATIENT;
                    break;
                case FROM_DAY:
                    sqlStatement = Repository.PMSSQL.COUNT_APPOINTMENTS_FROM_DAY;
                    break;       
            }
            result = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT, sqlStatement, appointment );
            return result.getValue();
        }
        else throw new StoreException("Appointment undefined in Repository.count(Appointment)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }

    @Override
    public Point count(Patient patient)throws StoreException{
        Entity result;
        if(patient != null){
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT, Repository.PMSSQL.COUNT_PATIENTS, patient );
            return result.getValue();
        }
        else throw new StoreException("Patient undefined in Repository.count(Patient)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }
    
    @Override
    public Point count(SurgeryDaysAssignment surgeryDaysAssignment)throws StoreException{
        Entity result;
        if (surgeryDaysAssignment != null){
            result = (Entity)runSQL(
                    Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT, 
                    Repository.PMSSQL.COUNT_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment );
            return result.getValue();
        }
        else throw new StoreException("SurgeryDaysAssignment undefined in Repository.count(SurgeryDaysAssignment)",
                StoreException.ExceptionType.UNEXPECTED_DATA_TYPE_ENCOUNTERED);
    }
    
    @Override
    public void create(Appointment table) throws StoreException { 
        Entity value = null;
        runSQL(Repository.EntitySQL.APPOINTMENT,Repository.PMSSQL.CREATE_APPOINTMENT_TABLE, value);
    }
    
    @Override
    public void create(Patient table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.CREATE_PATIENT_TABLE, value);
    }
    
    /*28/03/2024
    public void create(PatientNote table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT_NOTE,Repository.PMSSQL.CREATE_PATIENT_NOTE_TABLE, value);
    }*/
    
    @Override
    public void create(Doctor table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.DOCTOR,Repository.PMSSQL.CREATE_DOCTOR_TABLE, value);
    }
    
    @Override
    public void create(PrimaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PRIMARY_CONDITION,Repository.PMSSQL.CREATE_PRIMARY_CONDITION_TABLE, value);
    }
    
    @Override
    public void create(SecondaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.SECONDARY_CONDITION,Repository.PMSSQL.CREATE_SECONDARY_CONDITION_TABLE, value);
    }
    
    @Override
    public void create(ClinicalNote table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.CLINICAL_NOTE,Repository.PMSSQL.CREATE_CLINICAL_NOTE_TABLE, value);
    }
    
    @Override
    public void create(Treatment table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.TREATMENT,Repository.PMSSQL.CREATE_TREATMENT_TABLE, value);
    }
    
    @Override
    public void create(Question table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.QUESTION,Repository.PMSSQL.CREATE_QUESTION_TABLE, value);
    }
    
    @Override
    public void create(AppointmentTreatment table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT,Repository.PMSSQL.CREATE_APPOINTMENT_TREATMENT_TABLE, value);
    }
    
    @Override
    public void create(PatientQuestion table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT_QUESTION,Repository.PMSSQL.CREATE_PATIENT_QUESTION_TABLE, value);
    }
    
    @Override
    public void create(PatientCondition table) throws StoreException{
        Entity value = null;
        
        if (table.getIsPatientPrimaryCondition()){
            runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
                    Repository.PMSSQL.CREATE_PATIENT_PRIMARY_CONDITION_TABLE, value);
        }
        else if(table.getIsPatientSecondaryCondition()){
            runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,
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
        runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION,Repository.PMSSQL.CREATE_PATIENT_PRIMARY_CONDITION_TABLE, value);
    }
    
    @Override
    public void create(PatientSecondaryCondition table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION,Repository.PMSSQL.CREATE_PATIENT_SECONDARY_CONDITION_TABLE, value);
    }
    */
    @Override
    public void create(Medication table) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.MEDICATION,Repository.PMSSQL.CREATE_MEDICATION_TABLE, value);
    }
    
    @Override
    public void create(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
            runSQL(Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT,Repository.PMSSQL.CREATE_SURGERY_DAYS_ASSIGNMENT_TABLE, surgeryDaysAssignment);
    }
    
    @Override
    public Appointment read(Appointment appointment, Integer key)throws StoreException{
        boolean isAppointmentsForDay = false;
        AppointmentDelegate appointmentDelegate = null;
        PatientDelegate patientDelegate = null;
        Entity result = null;
        Repository.PMSSQL sqlStatement = null;
        switch(appointment.getScope()){
            case CANCELLED:
                result = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT, Repository.PMSSQL.READ_CANCELLED_APPOINTMENTS, appointment);
                return (Appointment)result;
            case SINGLE:
                appointmentDelegate = new AppointmentDelegate();
                appointmentDelegate.setAppointmentKey(key);
                result = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT, Repository.PMSSQL.READ_APPOINTMENT, appointmentDelegate);
                return (Appointment)result;
            case ALL:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS;
                break;
            case FOR_DAY:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FOR_DAY;
                isAppointmentsForDay = true;
                break;
            case DELETED_FOR_PATIENT:
                patientDelegate = new PatientDelegate(key);
                appointment.setPatient(patientDelegate);
                sqlStatement = Repository.PMSSQL.READ_DELETED_APPOINTMENTS_FOR_PATIENT;
                break;
            case FOR_PATIENT:
                patientDelegate = new PatientDelegate(key);
                appointment.setPatient(patientDelegate);
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FOR_PATIENT;
                break;
            case FROM_DAY:
                sqlStatement = Repository.PMSSQL.READ_APPOINTMENTS_FROM_DAY;
                break;       
        }
        result = (Entity)runSQL(Repository.EntitySQL.APPOINTMENT, sqlStatement, appointment);
        if (isAppointmentsForDay){//subsequent patient read required to return initialised state for appointee per appointment in collection
            
            Iterator<Appointment> it = ((Appointment)result).get().iterator();
            while (it.hasNext()){
                Appointment a = it.next();
                //Integer theKey = ((PatientDelegate)a.getPatient()).getPatientKey();
                Patient patient = new Patient(((PatientDelegate)a.getPatient()).getPatientKey());
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
            PatientDelegate gDelegate = null;
            PatientDelegate delegate = null;
            Entity entity = null;
            switch (patient.getScope()){
                case SINGLE:
                    delegate = new PatientDelegate(patient);
                    delegate.setPatientKey(key);
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.READ_PATIENT, delegate);
                    if (entity == null) {
                        throw new StoreException(
                                "Could not locate requested patient in "
                                        + "Repository::read(Patient, Integer key)",
                                StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                    }
                    return (Patient)entity;
                case DELETED:
                    delegate = new PatientDelegate(patient);
                    delegate.setPatientKey(key);
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.READ_DELETED_PATIENTS, delegate);
                    if (entity == null) {
                        throw new StoreException(
                                "Could not locate requested deleted patient in "
                                        + "Repository::read(Patient, Integer key)",
                                StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                    }
                    return (Patient)entity;
                default:
                    entity = (Entity)runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.READ_PATIENTS,patient);
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
    
    @Override
    /**
     * update appointment method adopts the delegate mechanism to transfer two key values to store
     * @param appointment
     * @param key Integer value of the appointment key 
     * @param appointeeKey Integer value of the appointment's appointee key
     * @throws StoreException 
     */
    public void update(Appointment appointment, Integer key, Integer appointeeKey /*,28/03/2024Integer patientNoteKey*/) throws StoreException {
        AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        PatientDelegate pDelegate = new PatientDelegate(delegate.getPatient());
        /*28/03/2024PatientNoteDelegate noteDelegate = new PatientNoteDelegate();
        noteDelegate.setKey(patientNoteKey);*/
        delegate.setAppointmentKey(key);
        pDelegate.setPatientKey(appointeeKey);
        delegate.setPatient(pDelegate);
        /*28/03/2024delegate.setPatientNote(noteDelegate);*/
        runSQL(Repository.EntitySQL.APPOINTMENT, Repository.PMSSQL.UPDATE_APPOINTMENT, delegate);

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
        NotificationDelegate delegate = new NotificationDelegate(pn);
        PatientDelegate pDelegate = new PatientDelegate(patientKey);
        delegate.setKey(key);
        delegate.setPatient(pDelegate);
        runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION, Repository.PMSSQL.UPDATE_NOTIFICATION,pn);
        
    }
    
    @Override
    public void update(PatientCondition patientCondition)throws StoreException{
        if (patientCondition.getIsPatientPrimaryCondition())
            runSQL(Repository.EntitySQL.PATIENT_PRIMARY_CONDITION, 
                    Repository.PMSSQL.UPDATE_PATIENT_PRIMARY_CONDITION,patientCondition); 
        else if (patientCondition.getIsPatientSecondaryCondition()){
            runSQL(Repository.EntitySQL.PATIENT_SECONDARY_CONDITION, 
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
        runSQL(Repository.EntitySQL.PRIMARY_CONDITION, Repository.PMSSQL.UPDATE_PRIMARY_CONDITION,pc); 
    }
    
    @Override
    public void update(SecondaryCondition pc)throws StoreException{
        runSQL(Repository.EntitySQL.SECONDARY_CONDITION, Repository.PMSSQL.UPDATE_SECONDARY_CONDITION,pc); 
    }
    
    @Override
    public void update(Doctor doctor)throws StoreException{
        runSQL(Repository.EntitySQL.DOCTOR, Repository.PMSSQL.UPDATE_DOCTOR,doctor); 
    }
    
    @Override
    public void update(Medication medication)throws StoreException{
        runSQL(Repository.EntitySQL.MEDICATION, Repository.PMSSQL.UPDATE_MEDICATION,medication); 
    }
    
    @Override
    public void update(ClinicalNote clinicNote)throws StoreException{
        runSQL(Repository.EntitySQL.CLINICAL_NOTE, Repository.PMSSQL.UPDATE_CLINIC_NOTE,clinicNote); 
    }
    
    @Override
    public void update(Treatment treatment)throws StoreException{
        runSQL(Repository.EntitySQL.TREATMENT, Repository.PMSSQL.UPDATE_TREATMENT,treatment); 
    }
    
    @Override
    public void update(Question question)throws StoreException{
        runSQL(Repository.EntitySQL.QUESTION, Repository.PMSSQL.UPDATE_QUESTION,question); 
    }
    
    @Override
    public void update(AppointmentTreatment treatment)throws StoreException{
        runSQL(Repository.EntitySQL.APPOINTMENT_TREATMENT, Repository.PMSSQL.UPDATE_APPOINTMENT_TREATMENT,treatment); 
    }
    
    @Override
    public void update(PatientQuestion question)throws StoreException{
        runSQL(Repository.EntitySQL.PATIENT_QUESTION, Repository.PMSSQL.UPDATE_PATIENT_QUESTION,question); 
    }
    
    /*28/03/2024
    public void update(PatientNote patientNote, Integer key)throws StoreException{
        PatientNoteDelegate delegate = null;
        delegate = new PatientNoteDelegate(patientNote);
        delegate.setKey(key);
        if ((delegate.getDatestamp()!=null)
                || (delegate.getPatientKey()!=null))
            runSQL(Repository.EntitySQL.PATIENT_NOTE, 
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
        PatientDelegate delegate = new PatientDelegate(patient);
        delegate.setPatientKey(key);
        if (delegate.getIsGuardianAPatient()){
            PatientDelegate gDelegate = new PatientDelegate(delegate.getGuardian());
            gDelegate.setPatientKey(guardianKey);
            delegate.setGuardian(gDelegate);
        }
        runSQL(Repository.EntitySQL.PATIENT, Repository.PMSSQL.UPDATE_PATIENT, delegate);
    }
    
    @Override
    public void update(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
        runSQL(Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT,
                    Repository.PMSSQL.UPDATE_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment);
    }
    
    
    public void populate(SurgeryDaysAssignment surgeryDaysAssignment) throws StoreException {
        runSQL(Repository.EntitySQL.SURGERY_DAYS_ASSIGNMENT, 
                Repository.PMSSQL.INSERT_SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment);
        
    }

    @Override
    public void create(Notification pn) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.CREATE_NOTIFICATION_TABLE, value);
    }
    
    public boolean doesPMSDatabaseExist(){
        boolean result = false;
        
        return result;
    }
    private static Repository instance = null;
    private enum RepositoryType{ACCESS, POSTGRES, SQL_SERVER};
    
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
                            /*28/03/2024create(new PatientNote());*/
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
    }
}
