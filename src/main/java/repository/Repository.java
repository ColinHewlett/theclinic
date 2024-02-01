/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import static controller.ViewController.displayErrorMessage;
import model.Appointment;
import model.Entity;
import model.IStoreClient;
import model.Patient;
import model.Notification;
import model.SurgeryDaysAssignment;
import model.PatientNote;
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
                            PATIENT,
                            PATIENT_NOTIFICATION,
                            PATIENT_NOTE,
                            SURGERY_DAYS_ASSIGNMENT,
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
                                UPDATE_PATIENT_NOTE,
                                
                                COUNT_PATIENTS,
                                CREATE_PATIENT_TABLE,
                                DELETE_PATIENT,
                                DELETE_PATIENTS,
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
                case PATIENT:
                    result = doPMSSQLforPatient(pmsSQL, (Entity)client);
                    break;
                case PATIENT_NOTIFICATION:
                    result = doPMSSQLforNotification(pmsSQL, (Entity)client);
                    break;
                case PATIENT_NOTE:
                    result = doPMSSQLforPatientNote(pmsSQL, (Entity)client);
                    break;
                case SURGERY_DAYS_ASSIGNMENT:
                    result = doPMSSQLforSurgeryDaysAssignment(pmsSQL, (Entity)client);
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
        PatientNoteDelegate patientNoteDelegate = null;
        
        int key = rs.getInt("pid");
        appointment.setStart(rs.getObject("Start", LocalDateTime.class));
        appointment.setDuration(Duration.ofMinutes(rs.getLong("Duration")));
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
        
        int patientNoteKey = rs.getInt("patientNoteKey");
        patientNoteDelegate = new PatientNoteDelegate();
        patientNoteDelegate.setKey(patientNoteKey);
        appointment.setPatientNote(patientNoteDelegate);
        
        delegate = new AppointmentDelegate(appointment);
        delegate.setAppointmentKey(key);
        return delegate;
    }
    
    private String getName(Entity entity){
        if (entity.getIsAppointment()) return "Appointment";
        if (entity.getIsPatient()) return "Patient";
        if (entity.getIsPatientNotification()) return "PatientNotification";
        if (entity.getIsSurgeryDaysAssignment()) return "SurgeryDaysAssignment";
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
        PreparedStatement preparedStatement = null;
        try{
            if (entity.getIsAppointment()){
                entityType = "Appointment";
                AppointmentDelegate delegate = (AppointmentDelegate)entity;
                preparedStatement.setLong(1, delegate.getAppointmentKey());      
            }else if(entity.getIsPatientNotification()){
                entityType = "PatientNotification";
                NotificationDelegate delegate = (NotificationDelegate)entity;
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
                preparedStatement.executeUpdate();
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
            throw new StoreException(message + "StoreException raised in Repository::doDeleteAppointments("
                    + sql + ")",
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
                preparedStatement.setLong(6, 
                        ((PatientNoteDelegate)delegate.getPatientNote()).getKey());
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
                    preparedStatement.setString(4, delegate.getNotes());
                    preparedStatement.setBoolean(5, delegate.getHasPatientBeenContacted());
                    preparedStatement.setLong(6, 
                            ((repository.PatientNoteDelegate)delegate.getPatientNote()).getKey());
                    preparedStatement.setLong(7, delegate.getAppointmentKey());
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
    
    private Entity doReadPatientNoteWithKey(String sql, Entity entity)throws StoreException{
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
    
    private void doDeletePatientNote(String sql, Entity entity)throws StoreException{
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
                        + "patientNoteKey LONG LONG NOT NULL REFERENCES PatientNote(pid), "
                        + "start DateTime, "
                        + "duration LONG, "
                        + "notes char, "
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
                        + "(PatientKey, Start, Duration, Notes,pid, patientNoteKey) "
                        + "VALUES (?,?,?,?,?,?);";
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
                        + "hasPatientBeenContacted = ?, "
                        + "patientNoteKey = ? "
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
            case DELETE_PATIENTS:
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
                        + "patientKey LONG, "
                        + "note Long Text"
                        + "isDeleted YesNo, "
                        + "lastUpdated DateTime";
                doCreatePatientNotificationTable(sql);
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
        PatientNoteDelegate noteDelegate = new PatientNoteDelegate();
        
        pDelegate.setPatientKey(appointeeKey);
        delegate.setPatient(pDelegate);
        noteDelegate.setKey(patientNoteKey);
        delegate.setPatientNote(noteDelegate);
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
    
    public Integer insert(PatientNote patientNote)throws StoreException{
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
    
    @Override
    public void recover(Notification patientNotification, 
            Integer patientNotificationKey)throws StoreException{
        switch(patientNotification.getScope()){
             case DELETED:
                NotificationDelegate delegate = 
                        new NotificationDelegate(patientNotification);
                delegate.setKey(patientNotificationKey);
                runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.RECOVER_NOTIFICATION, delegate);
                break;
             default:
                 String error = "Scope of recovery not defined as DELETED; "
                         + "raised in Repository.recover(Patient)";
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
                runSQL(Repository.EntitySQL.PATIENT,Repository.PMSSQL.DELETE_PATIENTS, null);
        }
    }

    @Override
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
    
    public Point count(PatientNote patientNote)throws StoreException{
        Entity result = null;
        if (patientNote !=null){
            Repository.PMSSQL sqlStatement = null;
            sqlStatement = Repository.PMSSQL.COUNT_PATIENT_NOTES;
            result = (Entity)runSQL(Repository.EntitySQL.PATIENT_NOTE, sqlStatement, patientNote);
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
    public void update(Appointment appointment, Integer key, Integer appointeeKey, Integer patientNoteKey) throws StoreException {
        AppointmentDelegate delegate = new AppointmentDelegate(appointment);
        PatientDelegate pDelegate = new PatientDelegate(delegate.getPatient());
        PatientNoteDelegate noteDelegate = new PatientNoteDelegate();
        noteDelegate.setKey(patientNoteKey);
        delegate.setAppointmentKey(key);
        pDelegate.setPatientKey(appointeeKey);
        delegate.setPatient(pDelegate);
        delegate.setPatientNote(noteDelegate);
        runSQL(Repository.EntitySQL.APPOINTMENT, Repository.PMSSQL.UPDATE_APPOINTMENT, delegate);

    }
    
    @Override
    public List<String[]> importEntityFromCSV(Entity entity) throws StoreException{
        List<String[]> result = null;
        if (entity.getIsAppointment()) {
            result = new CSVReader().getAppointmentDBFRecords(System.getenv("PMS_IMPORT_APPOINTMENT_DATA"));
        }
        if (entity.getIsPatient()) {
            result = new CSVReader().getPatientDBFRecords(System.getenv("PMS_IMPORT_PATIENT_DATA"));
            
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
        
    }
    
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
    public void create(PatientNote pn)throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT_NOTE,Repository.PMSSQL.CREATE_PATIENT_NOTE_TABLE, value);
    }

    
    @Override
    public void create(Notification pn) throws StoreException{
        Entity value = null;
        runSQL(Repository.EntitySQL.PATIENT_NOTIFICATION,Repository.PMSSQL.CREATE_NOTIFICATION_TABLE, value);
    }
    
    public void create()throws StoreException{
        RepositoryType repositoryType = RepositoryType.valueOf(System.getenv("PMS_STORE_TYPE"));
        switch(repositoryType){
            case ACCESS:{
                File file = null;
                try {
                    file = new File(getURL());
                    if (!file.exists()){
                        DatabaseBuilder.create(Database.FileFormat.V2016, file);
                        //01/03/202323
                        new Patient().create();
                        new Notification().create();
                        new SurgeryDaysAssignment().create();
                        new Appointment().create();
                    }else{
                        throw new StoreException("PMS database already exists and must be deleted before being re-created",
                                StoreException.ExceptionType.PMS_DATABASE_EXISTS);
                    }
                } catch (IOException io) {
                    String msg = "IOException -> raised on attempt to create a new Access database in DesktopControllerActionEvent.MIGRATION_DATABASE_CREATION_REQUEST";
                    throw new StoreException(msg + "\nStoreException raised in "
                            + "initialiseTargetStore(file = "
                            + file.toString() + ")", StoreException.ExceptionType.IO_EXCEPTION);
                }
            }
            default:{
                String sql = "CREATE DATABASE abc;";
                try{
                    PreparedStatement preparedStatement = getPMSStoreConnection().prepareStatement(sql);
                    preparedStatement.executeQuery();
                }catch (SQLException ex){
                    String message = ex.getMessage() + "\n";
                    message = message + "StoreException raised in PostgresRepository::createDatabase()";
                    throw new StoreException(message, StoreException.ExceptionType.SQL_EXCEPTION);
                }
            }
        }
    }
    
    public boolean doesPMSDatabaseExist(){
        boolean result = false;
        
        return result;
    }
    private static Repository instance = null;
    private enum RepositoryType{ACCESS, POSTGRES, SQL_SERVER};
    
    public Repository()throws StoreException{
        
        if (instance == null){ //initialkise connection variables
            RepositoryType repositoryType = RepositoryType.valueOf(System.getenv("PMS_STORE_TYPE"));
            switch (repositoryType){
            case ACCESS:{
                this.url = System.getenv("PMS_STORE_ACCESS_URL");
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
                    try{  
                        if (!file.exists()) {
                            DatabaseBuilder.create(Database.FileFormat.V2016, new File(path));
                            //01/03/2023
                            new Patient().create();
                            new Notification().create();
                            new SurgeryDaysAssignment().create();
                            new Appointment().create();
                        }
                        password = "";
                        user = "Admin";
                        instance = this;
                        repositoryName = "ACCESS";
                    }catch (IOException io) {
                        String msg = "IOException -> raised on attempt to create a new Access database in DesktopControllerActionEvent.MIGRATION_DATABASE_CREATION_REQUEST";
                        throw new StoreException(msg + "\nStoreException raised in "
                                + "initialiseTargetStore(file = "
                                + file.toString() + ")", StoreException.ExceptionType.IO_EXCEPTION);
                    }
                }
                break;
            }
            case POSTGRES:
                repositoryName = "POSTGRES";
                this.url = System.getenv("PMS_STORE_POSTGRES_URL");
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
                                        "line1 character varying(255) COLLATE pg_catalog.\"default\" NOT NULL,\n" +
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
                            sql = "CREATE TABLE public.appointment(" +
                                    "pid integer NOT NULL,\n" +
                                    "patientkey integer,\n" +
                                    "duration integer,\n" +
                                    "notes character varying(255) COLLATE pg_catalog.\"default\",\n" +
                                    "start timestamp without time zone,\n" +
                                    "isdeleted boolean DEFAULT false,\n" +
                                    "haspatientbeencontacted boolean DEFAULT false,\n" +
                                    "isCancelled boolean DEFAULT false,\n" +
                                    "CONSTRAINT appointment_pk PRIMARY KEY (pid),\n" +
                                    "CONSTRAINT appointment_fk1 FOREIGN KEY (patientkey)\n" +
                                    "   REFERENCES public.patient (pid) MATCH SIMPLE\n" +
                                    "    ON UPDATE NO ACTION\n" +
                                    "    ON DELETE NO ACTION)\n";
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
                                    "    CONSTRAINT patientnotification_fk1 FOREIGN KEY (pid)\n" +
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
