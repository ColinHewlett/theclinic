/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.TemplateReaderException;
import model.*;
import view.View;
import view.views.modal_views.ModalView;
import view.views.non_modal_views.DesktopView;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;
import repository.StoreException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.io.File;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JInternalFrame;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
/*28/03/2024import model.PatientNote;*/
import model.SystemDefinition;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.File;


/**
 *
 * @author colin
 * V02_VCSuppliesDataOnDemandToView
 */
public abstract class ViewController implements ActionListener, PropertyChangeListener{
    public static final LocalTime FIRST_APPOINTMENT_SLOT = LocalTime.of(9,0);
    public static final LocalTime LAST_APPOINTMENT_SLOT = LocalTime.of(17,0);
    
    public DateTimeFormatter dmyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public DateTimeFormatter dmyhhmmFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
    public DateTimeFormatter recallFormat = DateTimeFormatter.ofPattern("MMMM/yyyy");
    public DateTimeFormatter startTime24Hour = DateTimeFormatter.ofPattern("HH:mm");
    public DateTimeFormatter format24Hour = DateTimeFormatter.ofPattern("HH:mm");
    
    private Descriptor controllerDescriptor = null;
    private ScheduleReport scheduleReport = null;
    private ActionListener actionListener = null;
    private DesktopView desktopView;
    private View view = null;
    private ModalView modalView = null;
    
    private PrimaryCondition extractedPrimaryConditiomFromTemplate = null;
    
    public PrimaryCondition getExtractedPrimaryConditionFromTemplate(){
        return extractedPrimaryConditiomFromTemplate;
    }
    public void setExtractedPrimaryConditionFromTemplate(PrimaryCondition value){
        extractedPrimaryConditiomFromTemplate = value;
    }
    
    protected PrimaryCondition extractMedicalHistoryFromTemplate()throws TemplateReaderException{
        TemplateReader.setTemplateFile(
                        new File(SystemDefinition.getPMSSystemDefinition()));
        TemplateReader.setEntityTag("entity");
        TemplateReader.setEntityId("Patient");
        TemplateReader.setSectionId("History");
        PrimaryCondition pc = TemplateReader.extract(new PrimaryCondition());
        return pc;
    }
    
    protected Treatment extractTreatmentFromTemplate()throws TemplateReaderException{
        TemplateReader.setTemplateFile(
                        new File(SystemDefinition.getPMSSystemDefinition()));
        TemplateReader.setEntityTag("entity");
        TemplateReader.setEntityId("Appointment");
        TemplateReader.setSectionId("Treatment");
        Treatment treatment = TemplateReader.extract(new Treatment());
        return treatment;
    }
    
    /**
     * fetches primary condition with the collection of primary conditions defined in the template file
     * @param patient; the patient owner of the primary condition records
     * @return 
     */
    protected PrimaryCondition createPrimaryConditionsFromTemplate(Patient patient)
            throws TemplateReaderException, StoreException{
        PrimaryCondition pc = extractMedicalHistoryFromTemplate();
        /**
         * insert each primary condition record read from the template file
         */
        for(Condition condition : pc.get()){
            PrimaryCondition pCondition = (PrimaryCondition)condition;
            pCondition.setPatient(patient);
            pCondition.insert();
        }
        PrimaryCondition pConditionFromStore = null;
        /**
         * fetch back each primary condition stored
         */
        for(Condition primaryCondition : pc.get()){
            PrimaryCondition pCondition = (PrimaryCondition)primaryCondition;
            pCondition.setPatient(patient);
            pCondition.setScope(Entity.Scope.SINGLE);
            pConditionFromStore = pCondition.read();
            /**
             * insert each secondary condition for each primary condition fetched
             */
            for(Condition secondaryCondition : 
                    pCondition.getSecondaryCondition().get()){
                SecondaryCondition sCondition = (SecondaryCondition)secondaryCondition;
                sCondition.setPrimaryCondition(pConditionFromStore);
                sCondition.insert();
            }
        }
        /**
         * fetch stored primary conditions for this patient
         */
        pc = new PrimaryCondition(patient);
        pc.setScope(Entity.Scope.FOR_PATIENT);
        pc = pc.read();
        return pc;
    }
    
    public enum ViewControllers {
        ScheduleViewController,
        DesktopViewController,
        ImportProgressViewController,
        PatientNotificationViewController,
        PatientViewController,
    }
    
    public enum RequestedAppointmentState{ 
                                            MERGED,
                                            UNMERGED,
                                            REQUESTED_SLOT_STATE_UNDEFINED,
                                            REQUESTED_SLOT_STARTS_AFTER_PREVIOUS_SCHEDULED_SLOT,
                                            REQUESTED_SLOT_END_TIME_UPDATED_TO_LATER_TIME,
                                            APPOINTMENT_ADDED_TO_SCHEDULE,
                                            ERROR_ADDING_APPOINTMENT_TO_SCHEDULE,
                                            COLLISION,
                                            NO_COLLISION,
                                            SLOT_START_OK,
                                            UNDEFINED}
  
    public static enum ClinicalNoteViewControllerActionEvent{
        CLINICAL_NOTE_FOR_APPOINTMENT_REQUEST,
        CLINICAL_NOTE_DELETE_REQUEST,
        CLINICAL_NOTE_CREATE_REQUEST,
        CLINICAL_NOTE_UPDATE_REQUEST,
        VIEW_CLOSED_NOTIFICATION,
        INITIALISE_VIEW
    }
    
    public static enum ClinicalNoteViewControllerPropertyChangeEvent{
        CLINICAL_NOTE_RECEIVED,
        CLINICAL_NOTE_ERROR_RECEIVED
        
    }
    
    public static enum DesktopViewControllerActionEvent{
        CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST,
        NOTIFICATION_VIEW_CONTROLLER_REQUEST,
        PATIENT_SELECTION_VIEW_CONTROLLER_REQUEST,
        PATIENT_VIEW_CONTROLLER_REQUEST,
        SCHEDULE_VIEW_CONTROLLER_REQUEST,
        TEST_PATIENT_VIEW_CONTROLLER_REQUEST,
        TREAMENT_VIEW_CONTROLLER_REQUEST,

        INITIALISE_VIEW,
        BRING_TO_FRONT_SCHEDULE_VIEW_IF_ACTIVE_REQUEST,
        CLINIC_LOGO_VIEW_MODE_NOTIFICATION,
        COUNT_APPOINTMENT_TABLE_REQUEST,
        COUNT_PATIENT_TABLE_REQUEST,
        COUNT_CLINIC_NOTE_TABLE_REQUEST,
        //COUNT_PATIENT_NOTE_TABLE_REQUEST,
        COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST,
        COUNT_PRIMARY_CONDITION_TABLE_REQUEST,
        COUNT_SECONDARY_CONDITION_TABLE_REQUEST,
        COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST,
        COUNT_TREATMENT_TABLE_REQUEST,
        DELETE_DATA_FROM_PMS_DATABASE_REQUEST,
        DESKTOP_VIEW_MODE_NOTIFICATION,
        GET_APPOINTMENT_CSV_PATH_REQUEST,
        GET_PATIENT_CSV_PATH_REQUEST,
        GET_PMS_STORE_PATH_REQUEST,
        MIGRATE_DATA_FROM_SOURCE_VIEW_REQUEST,
        MIGRATE_APPOINTMENT_DATA,
        MIGRATE_APPOINTMENT_DATA_COMPLETED,
        MIGRATE_SURGERY_DAYS_ASSIGNMENT_DATA,
        MIGRATE_PATIENT_DATA,
        MIGRATE_PATIENT_DATA_COMPLETED,
        MIGRATE_PATIENT_NOTE_DATA,
        MIGRATE_PATIENT_NOTE_DATA_COMPLETED,
        MIGRATE_PRIMARY_CONDITION_DATA,
        MIGRATE_PRIMARY_CONDITION_DATA_COMPLETED,
        MIGRATE_SECONDARY_CONDITION_DATA,
        MIGRATE_SECONDARY_CONDITION_DATA_COMPLETED,
        MIGRATE_TREATMENT_DATA,
        MIGRATE_TREATMENT_DATA_COMPLETED,
        MODAL_VIEWER_ACTIVATED_NOTIFICATION,
        MODAL_VIEWER_CLOSED_NOTIFICATION,
        
        REFRESH_DISPLAY_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CONTROLLER_ACTIVATED_NOTIFICATION,
        VIEW_CONTROLLER_CLOSE_NOTIFICATION,
        VIEW_CONTROLLER_CHANGED_NOTIFICATION,
        VIEW_CLOSE_REQUEST,
        VIEW_CLOSED_NOTIFICATION

    }
    
    public static enum DesktopViewControllerPropertyChangeEvent{
        APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CONTROLLER_REQUEST,
        APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        DESKTOP_VIEW_CHANGED_NOTIFICATION,
        SET_DESKTOP_VIEW_MODE,
        APPOINTMENT_CSV_PATH_RECEIVED,
        PATIENT_CSV_PATH_RECEIVED,
        PMS_STORE_PATH_RECEIVED,
        APPOINTMENT_TABLE_COUNT_RECEIVED,
        CLINIC_NOTE_TABLE_COUNT_RECEIVED,
        PATIENT_TABLE_COUNT_RECEIVED,
        //PATIENT_NOTE_TABLE_COUNT_RECEIVED,
        PATIENT_NOTIFICATION_TABLE_COUNT_RECEIVED,
        PRIMARY_CONDITION_TABLE_COUNT_RECEIVED,
        SECONDARY_CONDITION_TABLE_COUNT_RECEIVED,
        SURGERY_DAYS_ASSIGNMENT_TABLE_COUNT_RECEIVED,
        TREATMENT_TABLE_COUNT_RECEIVED,
        MIGRATION_ACTION_COMPLETE,
        PATIENTS_RECEIVED
    }
    
    public static enum DataMigrationViewControllerActionEvent{
        DATA_MIGRATION_START_REQUEST,
        DATA_MIGRATION_PROGRESS_VIEW_CLOSE_NOTIFICATION,
        READY_FOR_RECEIPT_OF_APPOINTMENT_MIGRATION_PROGRESS,
        READY_FOR_RECEIPT_OF_PATIENT_NOTE_MIGRATION_PROGRESS,
        READY_FOR_RECEIPT_OF_PATIENT_MIGRATION_PROGRESS,
        READY_FOR_RECEIPT_OF_PRIMARY_CONDITION_MIGRATION_PROGRESS,
        READY_FOR_RECEIPT_OF_SECONDARY_CONDITION_MIGRATION_PROGRESS,
        READY_FOR_RECEIPT_OF_TREATMENT_MIGRATION_PROGRESS
    }
    
    public static enum DataMigrationViewControllerPropertyChangeEvent{
        state,
        progress,
        PATIENT,
        APPOINTMENT,
        DATA_MIGRATION_COMPLETED,
        PREPARE_FOR_RECEIPT_OF_APPOINTMENT_MIGRATION_PROGRESS,
        PREPARE_FOR_RECEIPT_OF_PATIENT_MIGRATION_PROGRESS,
        PREPARE_FOR_RECEIPT_OF_PATIENT_NOTE_MIGRATION_PROGRESS,
        PREPARE_FOR_RECEIPT_OF_PRIMARY_CONDITION_MIGRATION_PROGRESS,
        PREPARE_FOR_RECEIPT_OF_SECONDARY_CONDITION_MIGRATION_PROGRESS,
        PREPARE_FOR_RECEIPT_OF_TREATMENT_MIGRATION_PROGRESS
        }
    
    public static enum NotesViewControllerActionEvent{
        NOTES_VIEW_CLOSE_REQUEST,
        NOTES_FOR_PATIENT_REQUEST,
        NOTES_FOR_PATIENT_CHANGE_REQUEST,
        NOTES_PATIENT_SELECTION_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSED_NOTIFICATION
    }
    
    public static enum NotesViewControllerPropertyChangeEvent{
        NOTES_FOR_PATIENT_RECEIVED,
        PATIENT_SELECTION_REQUESTED
    }
    
    public static enum NotificationViewControllerActionEvent{
        ACTION_NOTIFICATION_REQUEST,
        CANCEL_NOTIFICATION_REQUEST,
        CREATE_NOTIFICATION_REQUEST,
        DELETE_NOTIFICATION_REQUEST,
        MODAL_VIEWER_ACTIVATED,
        MODAL_VIEWER_DEACTIVATED,
        NOTIFICATION_EDITOR_CLOSE_VIEW_REQUEST,
        //PATIENT_NOTIFICATION_EDITOR_DELETE_NOTIFICATION_REQUEST,
        NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST,
        NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST,
        NOTIFICATIONS_REQUEST,
        NOTIFICATIONS_FOR_PATIENT_REQUEST,
        UNACTIONED_NOTIFICATIONS_REQUEST,
        UPDATE_NOTIFICATION_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSED_NOTIFICATION
    }
    
    public static enum NotificationViewControllerPropertyChangeEvent{
        RECEIVED_PATIENT_NOTIFICATION,
        RECEIVED_PATIENT_NOTIFICATIONS,
        RECEIVED_UNACTIONED_NOTIFICATIONS,
    }
    
    public static enum PatientViewControllerActionEvent{
        //primary view requests (commands)
        
        CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST,
        DELETED_PATIENT_REQUEST, 
        
        NULL_PATIENT_REQUEST,
        PATIENT_CREATE_REQUEST,
        PATIENT_DELETE_REQUEST,
        PATIENT_DOCTOR_EDITOR_VIEW_REQUEST,
        PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST,
        PATIENT_MEDICAL_HISTORY_1_EDITOR_VIEW_REQUEST,
        PATIENT_MEDICATION_EDITOR_VIEW_REQUEST,
        PATIENT_NOTES_EDITOR_VIEW_REQUEST,
        PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST,
        PATIENT_RECALL_EDITOR_VIEW_REQUEST,
        PATIENT_RECOVER_REQUEST,
        PATIENT_REQUEST,
        PATIENT_SELECTION_VIEW_REQUEST,
        PATIENT_UPDATE_REQUEST,
        RECOVER_PATIENT_REQUEST,
        SCHEDULE_VIEW_CONTROLLER_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSE_NOTIFICATION, 
        

        CONDITION_STATE_UPDATE_REQUEST,
        PATIENT_MEDICAL_HISTORY_NOTE_TAKER_REQUEST,
        PATIENT_MEDICAL_HISTORY_NOTES_TAKEN_REQUEST,
        PATIENT_MEDICAL_HISTORY_2_EDITOR_VIEW_REQUEST,
        PATIENT_DOCTOR_CREATE_REQUEST,
        PATIENT_DOCTOR_DELETE_REQUEST,
        PATIENT_DOCTOR_UPDATE_REQUEST,
        PATIENT_MEDICATION_CREATE_REQUEST,
        PATIENT_MEDICATION_DELETE_REQUEST,
        PATIENT_MEDICATION_UPDATE_REQUEST,
        MODAL_VIEWER_ACTIVATED,
        PATIENT_EDITOR_VIEW_CHANGE,
        PATIENT_RECALL_EDITOR_VIEW_CHANGE,
        PATIENT_GUARDIAN_REQUEST,
        PATIENT_GUARDIANS_REQUEST,
        PATIENTS_REQUEST,     
        PATIENT_VIEW_CLOSED, 
    }
    
    public static enum PatientViewControllerPropertyChangeEvent{
        NULL_PATIENT_RECEIVED,
        PATIENT_MEDICAL_HISTORY_RECEIVED,
        PATIENT_GUARDIANS_RECEIVED,
        PATIENT_RECEIVED,
        PATIENTS_RECEIVED,
        PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED,
        PATIENT_VIEW_CHANGE_NOTIFICATION,
        PATIENT_EDITOR_VIEW_CLOSED,
        PATIENT_NOTES_RECEIVE,
        MAKE_VIEW_VISIBLE,
        MAKE_VIEW_INVISIBLE,
        PATIENT_NOTES_RECEIVED,
        CLOSE_VIEW_REQUEST_RECEIVED,
        DOCTOR_RECEIVED
        
    }

    public static enum PatientAppointmentContactListViewControllerActionEvent {
        PATIENT_APPOINTMENT_CONTACT_VIEW_CLOSED,
        PATIENT_APPOINTMENT_CONTACT_VIEW_REQUEST
    }
    
    public static enum ScheduleViewControllerActionEvent{
        /**
         * PRIMARY VIEW ACTION EVENTS
         */
        //create view request actions
        //APPOINTMENT_REMINDERS_VIEW_REQUEST,
        APPOINTMENT_REMINDED_STATUS_UPDATE_REQUEST,
        APPOINTMENTS_CANCELLED_VIEW_REQUEST,
        APPOINTMENT_CREATE_VIEW_REQUEST,
        APPOINTMENT_UPDATE_VIEW_REQUEST,
        CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST,
        NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST,
        SURGERY_DAYS_EDITOR_VIEW_REQUEST,
        EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST,
        //view action requests
        APPOINTMENT_CANCEL_REQUEST,
        APPOINTMENT_FOR_DAY_REQUEST,
        APPOINTMENTS_FOR_DAY_REQUEST,
        VIEW_CLOSE_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_ACTIVATED_NOTIFICATION,
        /**
         * SECONDARY VIEW ACTION EVENTS
         */
        APPOINTMENT_UNCANCEL_REQUEST,
        APPOINTMENT_EDITOR_CREATE_REQUEST,
        APPOINTMENTS_FOR_NON_SURGERY_DAY_REQUEST,
        APPOINTMENT_EDITOR_UPDATE_REQUEST,
        APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST,
        APPOINTMENT_TREATMENT_UPDATE_REQUEST, 
        APPOINTMENT_TREATMENT_COMMENT_UPDATE_REQUEST,
        APPOINTMENT_TREATMENT_CREATE_REQUEST,
        APPOINTMENT_TREATMENT_NAME_UPDATE_REQUEST,
        APPOINTMENT_TREATMENT_DELETE_REQUEST,
        APPOINTMENT_TREATMENT_STATE_SET_REQUEST,
        APPOINTMENT_TREATMENT_STATE_RESET_REQUEST,
        EMPTY_SLOTS_FROM_DAY_REQUEST,
        MODAL_VIEWER_ACTIVATED,
        SCHEDULE_VIEW_CONTROLLER_REQUEST,
        SURGERY_DAYS_EDIT_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST,
        TREATMENT_CREATE_REQUEST
    }
    
    public static enum ScheduleViewControllerPropertyChangeEvent{
        APPOINTMENT_RECEIVED,
        APPOINTMENTS_CANCELLED_RECEIVED,
        APPOINTMENT_FOR_DAY_RECEIVED,
        APPOINTMENTS_FOR_DAY_RECEIVED,
        APPOINTEE_REMINDER_COUNT_RECEIVED,
        APPOINTMENT_SCHEDULE_ERROR_RECEIVED,
        APPOINTMENT_SLOTS_FROM_DAY_RECEIVED,
        APPOINTMENT_TREATMENT_WITH_STATE_RECEIVED,
        INITIALISE_SCHEDULE_DATE,
        NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED,//added 01/02/2023
        NON_SURGERY_DAY_EDIT_RECEIVED,
        REFRESH_DISPLAY_REQUEST_RECEIVED,
        SURGERY_DAYS_ASSIGNMENT_RECEIVED,
        CLOSE_VIEW_REQUEST_RECEIVED
        }
    
    public static enum TreatmentViewControllerActionEvent{
        TREATMENT_CREATE_REQUEST,
        TREATMENT_DELETE_REQUEST,
        TREATMENTS_READ_REQUEST,
        TREATMENT_RENAME_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public static enum TreatmentViewControllerPropertyChangeEvent{
        TREATMENT_RECEIVED,
        TREATMENT_ERROR_RECEIVED
    }

    public enum ViewMode {
        CREATE,
        Create,
        UPDATE,
        Update,
        SLOT_SELECTED,
        SLOT_UNSELECTED,
        SCHEDULE_REFERENCED_FROM_PATIENT_VIEW,
        SCHEDULE_REFERENCED_DESKTOP_VIEW,
        NO_ACTION
    } 
    
    
    
    public void firePropertyChangeEvent(
                                        String pcEventName,
                                        PropertyChangeListener pcListener,
                                        Object pcSource,
                                        Object oldPropertyValue,
                                        Object newPropertyValue
                                        ){
        PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);
        /*
        for(PropertyChangeListener pcl : pcSupport.getPropertyChangeListeners()){
            pcSupport.removePropertyChangeListener(pcl);
        }
        */
        pcSupport.addPropertyChangeListener(pcListener);
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(
                pcSource,
                pcEventName,
                oldPropertyValue,
                newPropertyValue);
        pcSupport.firePropertyChange(pcEvent);
        pcSupport.removePropertyChangeListener(pcListener);
    }
    
    /**
     * utility helper method which sets the column header colour and each of the widths of the specified table's columns as per the specified WIDTH PERCENTAGES 
     * @param table; JTable whose properties are updated
     * @param tablePreferredWidth; int which defines the total width of the JTable
     * @param percentages; double[] which defines the percentage of the total width of each of the table's columns 
     */
    public static void setJTableColumnProperties(JTable table, int tablePreferredWidth,
        double... percentages) {
        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth((int)
                    (tablePreferredWidth * (percentages[i] / total)));
            column.setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        }
    }
    
    public void centreViewOnDesktop(Frame desktopView, JInternalFrame view){
        Insets insets = desktopView.getInsets();
        Dimension deskTopViewDimension = desktopView.getSize();
        Dimension myViewDimension = view.getSize();
        Point point = new Point(
                (int)((deskTopViewDimension.getWidth() - (2*insets.left)) - (myViewDimension.getWidth()))/2,
                //(int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2);
                (int)((deskTopViewDimension.getHeight() - (2*insets.top)) - myViewDimension.getHeight())/2);
        
        view.setLocation(point);
        
    }
    
    public static void displayErrorMessage(String message, String title, int messageType){
        JOptionPane.showMessageDialog(null,new ErrorMessagePanel(message),title,messageType);
    }
    
    public final Descriptor getDescriptor(){
        if (controllerDescriptor==null) setDescriptor(new Descriptor());
        return controllerDescriptor;
    }
    
    protected final ActionListener getMyController(){
        return actionListener;
    }
    
    protected final void setMyController(ActionListener value){
        actionListener = value;
    }
    
    public final void setDescriptor(Descriptor value){
        controllerDescriptor = value;
    }
    
    public void setNewEntityDescriptor(Descriptor value){
        controllerDescriptor = value;
    }

    protected Appointment doChangeAppointmentScheduleForDayRequest(ViewMode mode, Appointment rSlot) throws StoreException{
        Appointment result = null;
        /*28/03/2024PatientNote patientNote = null;*/
        LocalDate day = rSlot.getStart().toLocalDate();
        Appointment appointment = new Appointment(); 
        appointment.setStart(day.atStartOfDay());
        appointment.setScope(Entity.Scope.FOR_DAY);
        appointment.read();
        setScheduleReport(doAppointmentCollisionCheckOnScheduleChangeRequest(
                    rSlot, appointment.get(), mode));
        if (getScheduleReport().getState().equals(RequestedAppointmentState.COLLISION)){
            getDescriptor().getControllerDescription().setError(scheduleReport.getError());
            result = null;
        }
        else{
            getDescriptor().getControllerDescription().setError(null);
            switch (mode){//one or more appointments already exist so check the CREATE or UPGRADE make sense
                case CREATE:
                    /**
                     * 11/02/2024  code change
                     *
                     */
                    //if (!rSlot.getIsUnbookableSlot()){
                        /*28/03/2024patientNote = new PatientNote(rSlot.getPatient());
                        patientNote.setDatestamp(rSlot.getStart());
                        patientNote.setNote(rSlot.getPatientNote().getNote());
                        patientNote.setLastUpdated(LocalDateTime.now());
                        Integer patientNoteKey = patientNote.insert();
                        rSlot.setPatientNote(new PatientNote(patientNoteKey));*/
                        //rSlot.setPatientNoteKey(patientNoteKey);
                    //}
                    rSlot.insert(); 
                    rSlot.setScope(Entity.Scope.SINGLE);
                    result = rSlot.read();
                    
                    break;
                case UPDATE:
                    /*05/04/2024 19:31 code update */
                    Appointment temp = new Appointment(rSlot.getKey());
                    temp.setScope(Entity.Scope.SINGLE);
                    temp = temp.read();
                    rSlot.setNotes(temp.getNotes());
                    /*end of code update*/
                    rSlot.update();
                    rSlot.setScope(Entity.Scope.SINGLE);
                    result = rSlot.read();
                    /**
                     * 11/02/2024 code logic update
                     
                    //if (!rSlot.getIsUnbookableSlot()){
                      /  patientNote = result.getPatientNote();
                        patientNote.setDatestamp(result.getStart());
                        patientNote.setPatient(result.getPatient());
                        patientNote.setNote(result.getPatientNote().getNote());
                        patientNote.setLastUpdated(LocalDateTime.now());
                        patientNote.setIsDeleted(result.getIsDeleted());
                        patientNote.setScope(Entity.Scope.SINGLE);
                        patientNote.update();
                    //}
                    */
                    break;
                case NO_ACTION:
                    result = rSlot;
                    break;
            }
            
        }
        return result;
    }
    
    private ScheduleReport doAppointmentCollisionCheckOnScheduleChangeRequest(
            Appointment requestedSlot,
            ArrayList<Appointment> appointments, ViewMode mode){
        String error = null;
        getScheduleReport().setState(RequestedAppointmentState.UNDEFINED);
        Iterator<Appointment> appointmentsForDay = appointments.iterator();
        while (appointmentsForDay.hasNext()){
            Appointment nextScheduledSlot = appointmentsForDay.next();
            switch(mode){
                case CREATE:
                case NO_ACTION:
                    switch(scheduleReport.getState()){
                        case SLOT_START_OK:
                            /**
                             * In CREATE new appointment mode checks if requested slot overlaps the next scheduled slot 
                             */
                            if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime())){
                                scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                            }
                            else{
                                scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                if (requestedSlot.getIsUnbookableSlot()){
                                    error = "The new unbookable slot overwrites ";
                                    if (nextScheduledSlot.getIsUnbookableSlot()){
                                        error = error + "another unbookable appointment slot which starts at ";
                                        error = error + nextScheduledSlot.getUnbookableSlotStartTime();
                                    }
                                    else {
                                        error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                    }
                                    
                                }else{
                                    error =  "The new appointment for " 
                                            + requestedSlot.getAppointeeName() 
                                            + " overwrites ";
                                    if (nextScheduledSlot.getIsUnbookableSlot()){
                                        error = error + "an unbookable appointment slot which starts at ";
                                        error = error + nextScheduledSlot.getUnbookableSlotStartTime(); 
                                    }
                                    else {
                                        error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                    }  
                                }
                            }
                            scheduleReport.setError(error);
                            break;
                        case UNDEFINED:
                            if (requestedSlot.getSlotStartTime().isBefore(nextScheduledSlot.getSlotStartTime())){
                                scheduleReport.setState(RequestedAppointmentState.SLOT_START_OK);
                                if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime()))
                                    scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                                else if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime())){
                                    scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                                }
                                else{
                                    scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                    if (requestedSlot.getIsUnbookableSlot()){
                                        error = "The new unbookable slot overwrites ";
                                        if (nextScheduledSlot.getIsUnbookableSlot()){
                                            error = error + "another unbookable appointment slot which starts at ";
                                            error = error + nextScheduledSlot.getUnbookableSlotStartTime();
                                        }
                                        else {
                                            error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                        }

                                    }else{
                                        error =  "The new appointment for " 
                                                + requestedSlot.getAppointeeName() 
                                                + " overwrites ";
                                        if (nextScheduledSlot.getIsUnbookableSlot()){
                                            error = error + "an unBookable appointment slot which starts at ";
                                            error = error + nextScheduledSlot.getUnbookableSlotStartTime(); 
                                        }
                                        else {
                                            error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                        }  
                                    } 
                                    scheduleReport.setError(error);
                                }
                                
                            }
                            else if (requestedSlot.getSlotStartTime().isEqual(nextScheduledSlot.getSlotEndTime())){
                                scheduleReport.setState(RequestedAppointmentState.SLOT_START_OK);
                            }
                            else if (!requestedSlot.getSlotStartTime().isAfter(nextScheduledSlot.getSlotEndTime())){
                                scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                if (requestedSlot.getIsUnbookableSlot()){
                                    error = "The new unbookable slot overwrites ";
                                    if (nextScheduledSlot.getIsUnbookableSlot()){
                                        error = error + "another unbookable appointment slot which starts at ";
                                        error = error + nextScheduledSlot.getUnbookableSlotStartTime();
                                    }
                                    else {
                                        error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                    }
                                    
                                }else{
                                    error =  "The new appointment for " 
                                            + requestedSlot.getAppointeeName() 
                                            + " overwrites ";
                                    if (nextScheduledSlot.getIsUnbookableSlot()){
                                        error = error + "an unbookable appointment slot which starts at ";
                                        error = error + nextScheduledSlot.getUnbookableSlotStartTime(); 
                                    }
                                    else {
                                        error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                    }  
                                }
                                scheduleReport.setError(error);
                            }
                    }
                    break;
                case UPDATE:
                    switch(scheduleReport.getState()){
                        case SLOT_START_OK:
                            /**
                             * In requested slot UPDATE mode checks if requested slot does not end after the start of the next scheduled slot
                             * -- if yes requested state = NO COLLISION
                             * -- else requested state = COLLISION
                             */
                            if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime()))
                                scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                            else {
                                scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                if (requestedSlot.getIsUnbookableSlot()){
                                    error = "The updated unbookable slot overwrites ";
                                    if (nextScheduledSlot.getIsUnbookableSlot()){
                                        error = error + "another unbookable appointment slot which starts at ";
                                        error = error + nextScheduledSlot.getUnbookableSlotStartTime();
                                    }
                                    else {
                                        error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                    }
                                    
                                }else{
                                    error =  "The new appointment for " 
                                            + requestedSlot.getAppointeeName() 
                                            + " overwrites ";
                                    if (nextScheduledSlot.getIsUnbookableSlot()){
                                        error = error + "an unBookable appointment slot which starts at ";
                                        error = error + nextScheduledSlot.getUnbookableSlotStartTime(); 
                                    }
                                    else {
                                        error = error + "existing appointment for " + nextScheduledSlot.getAppointeeNamePlusSlotStartTime();
                                    }  
                                }
                                scheduleReport.setError(error);
                            }
                            break;
                        case UNDEFINED:
                            /**
                             * In UPDATE appointment mode checks if an available start time exists before the next scheduled slot starts
                             * -- if yes checks if requested slot does not end after the next scheduled slot
                             * ---- if yes requested slot state made equal to NO_COLLISION
                             * ---- if no checks if requested slot appointee is same as next scheduled slot appointee
                             * ------ if yes checks requested slot does not end after the next scheduled slot end time
                             * --------if yes requested slot state made equal to NO_COLLISION (treated as an update of next scheduled slot)
                             * --------if no requested slot state made equal to SLOT_START_OK requested appointment overlaps an appointment slot for another patient 
                             * ------ if no requested state = COLLISION because 
                             */
                            if (requestedSlot.getSlotStartTime().isBefore(nextScheduledSlot.getSlotStartTime())){
                                if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime()))
                                    scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                                else if (requestedSlot.getPatient().equals(nextScheduledSlot.getPatient())){
                                    if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotEndTime()))
                                        scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                                    else scheduleReport.setState(RequestedAppointmentState.SLOT_START_OK);
                                }
                                else{//collides with an appointment for a different patient
                                    scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                    if (requestedSlot.getIsUnbookableSlot()){
                                        scheduleReport.setError(        
                                            "The updated unbookable appointment slot"
                                                + " overwrites existing appointment for " 
                                            + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
                                    }else if (nextScheduledSlot.getIsUnbookableSlot()){
                                        scheduleReport.setError(
                                        "The updated appointment for " + requestedSlot.getAppointeeName()
                                            + " overwrites an unbookable appointment slot which starts at " 
                                            + nextScheduledSlot.getUnbookableSlotStartTime());
                                    }else{
                                        scheduleReport.setError(
                                        "The updated appointment for " + requestedSlot.getAppointeeName()
                                            + " overwrites an existing appointment for " 
                                            + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
                                    }
                                }
                            }
                            else{
                                if (requestedSlot.getSlotStartTime().isBefore(nextScheduledSlot.getSlotEndTime())){
                                    if (!requestedSlot.getPatient().equals(nextScheduledSlot.getPatient())){
                                        scheduleReport.setState(RequestedAppointmentState.COLLISION); 
                                     
                                        if (requestedSlot.getIsUnbookableSlot()){
                                            scheduleReport.setError(        
                                                "The updated unbookable appointment slot"
                                                    + " overwrites existing appointment for " 
                                                + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
                     
                                        }else if (nextScheduledSlot.getIsUnbookableSlot()){
                                            scheduleReport.setError(
                                            "The updated appointment for " + requestedSlot.getAppointeeName()
                                                + " overwrites the unbookable slot which starts at " 
                                                + nextScheduledSlot.getUnbookableSlotStartTime());
                                        }
                                        else scheduleReport.setError(
                                                "The updated appointment for " + requestedSlot.getAppointeeName()
                                                + " overwrites the appointment for "
                                                        + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
                                    }
                                    else scheduleReport.setState(RequestedAppointmentState.SLOT_START_OK);
                                }
                                else{//remain in UNDEFINED state

                                }
                            }
                    }
                    break;
            }
        }
        return scheduleReport;
    }
    
    public class ScheduleReport{
        private String error = null;
        private RequestedAppointmentState state = null;
        
        protected String getError(){
            return error;
        }
        
        private void setError(String value){
            error = value;
        }
        
        protected RequestedAppointmentState getState(){
            return state;
        }
        
        protected void setState(RequestedAppointmentState value){
            state = value;
        } 
    }
    public ScheduleReport getScheduleReport(){
        return scheduleReport;
    }
        
    public void setScheduleReport(ScheduleReport value){
        scheduleReport = value;
    }
    
    public void mergeScheduleSlotsIfPossible(LocalDate day){
        Appointment appointment = new Appointment();
        appointment.setStart(day.atStartOfDay());
        appointment.setScope(Entity.Scope.FOR_DAY);
        setScheduleReport(new ScheduleReport());
        getScheduleReport().setState(RequestedAppointmentState.MERGED);
        while(getScheduleReport().getState().equals(RequestedAppointmentState.MERGED)){
            try{
                appointment.read();
                ArrayList<Appointment> appointments = appointment.get();
                doMergeCheck(appointments);
            }catch(StoreException ex){

            }
        }
    }
    
    private void doMergeCheck(ArrayList<Appointment> appointments){
        getScheduleReport().setState(RequestedAppointmentState.UNMERGED);
        try{
            for (int i = 0; i < appointments.size()-1; i++){
                if (appointments.size() == 1) break;
                else {
                    if (attemptMerge(appointments.get(i), appointments.get(i+1))){
                        getScheduleReport().setState(RequestedAppointmentState.MERGED);
                        break;
                    }
                }
            }
        }catch (Exception ex){
            displayErrorMessage("Raised in ViewController.doMergeCheck()\n" + ex.getMessage(),
                    "View controller error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean attemptMerge(Appointment first, Appointment second){
        boolean result = false;
        if (first.getSlotEndTime().isEqual(second.getSlotStartTime())){
            if (first.getPatient().equals(second.getPatient())){
                //merge first and second appointments
                first.setDuration(first.getDuration().plus(second.getDuration()));
                try{
                    /*05/04/2024 19:31 code update */
                    Appointment temp = new Appointment(first.getKey());
                    temp.setScope(Entity.Scope.SINGLE);
                    temp = temp.read();
                    first.setNotes(temp.getNotes());
                    /*end of code update*/
                    first.update();
                    second.setScope(Entity.Scope.SINGLE);
                    second.delete();
                }catch(StoreException ex){
                    
                }
                result = true;
            }
        }
        return result;
    }

    protected final void setDesktopView (DesktopView value){
        desktopView = value;
    }
    
    public final DesktopView getDesktopView(){
        return desktopView;
    }
    
    public final void setView (View value){
        view = value;
    }
    
    public final View getView(){
        return view;
    }
    
    public final void setModalView (ModalView value){
        modalView = value;
    }
    
    public final ModalView getModalView(){
        return modalView;
    }
    
    class Email{
        
        Email(){
            
        }
        
        public void send(Patient patient, String subject){
            String document = null;
            try {
                // Set the SMTP server properties
                Properties props = new Properties();
                props.put("mail.smtp.host", getSMTPServer());
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.port", "587");

                // Create a session with the SMTP server and authenticate
                Session session = Session.getInstance(props, new Authenticator() {
                    String SMTPSender = getSMTPSender();
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTPSender, getSMTPUserPassword());
                    }
                });

                // Create the email message
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(getSMTPSender()));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(getSMTPReceiver()));
                message.setSubject(subject);
                document = extractFromSource();
                document = getSalutation() + document;
                message.setText(document);

                // Send the email
                Transport.send(message);

                System.out.println("Email sent successfully.");
            } catch (MessagingException e) {
                e.printStackTrace();
            }catch(StoreException ex){
                String message = ex.getMessage() + "\n"
                        + "Raised in ViewController::Email.send()";
            }
            
        }
        
        private String extractFromSource(){
            String document = null;
            try {
                // Path to your .docx file
                String filePath = getSMTPBody();

                // Open the .docx file
                FileInputStream fis = new FileInputStream(filePath);
                XWPFDocument docx = new XWPFDocument(fis);

                // Create a Word extractor
                XWPFWordExtractor extractor = new XWPFWordExtractor(docx);

                // Extract text from the document
                document = extractor.getText();
                //System.out.println("Contents of the .docx file:");
                //System.out.println(text);

                // Close the extractor and file input stream
                extractor.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(StoreException ex){
                String message = ex.getMessage() + "\n"
                        + "Raised in ViewController::Email.extractFromSource()";
            }
            
            return document;
        }
        
        private final String getSMTPSender()throws StoreException{
            return SystemDefinition.getPMSSMTPUser();
        }
        
        private final String getSMTPServer()throws StoreException{
            return SystemDefinition.getPMSSMTPServer();
        }
        
        private final String getSMTPBody()throws StoreException{
            return SystemDefinition.getPMSSMTPBody();
        }
        
        private final String smtpUserPassword = "pmsq20000907B@@@";
        private String getSMTPUserPassword(){
            return smtpUserPassword;
        }
        
        private Patient patientToReceiveEmail = null;
        private void setPatientToReceiveEmail(Patient patient){
            patientToReceiveEmail = patient; 
        }
        private Patient getPatientToReceiveEmail(){
            return patientToReceiveEmail;
        }
        
        private String getSMTPReceiver(){
            return getPatientToReceiveEmail().getEmail();
        }
        
        private String getSalutation(){
            String name = getPatientToReceiveEmail().getName().getForenames();
            String[] names = name.split(" ");
            return "Dear " + names[0] + "\n";
        }       
    }
    
    protected TreatmentWithState getTreatmentsWithState(
            Appointment appointment)throws StoreException{
        TreatmentWithState theTreatmentWithState = new TreatmentWithState();
        Treatment treatment = new Treatment();
        treatment.setScope(Entity.Scope.ALL);
        treatment = treatment.read();
        AppointmentTreatment appointmentTreatment = new AppointmentTreatment(appointment);
        appointmentTreatment.setScope(Entity.Scope.FOR_APPOINTMENT);
        appointmentTreatment = appointmentTreatment.read();
        for(Treatment t : treatment.get()){       
            TreatmentWithState treatmentWithState = new TreatmentWithState(t);
            for(AppointmentTreatment at : appointmentTreatment.get()){
                if (t.getKey().equals(at.getTreatment().getKey())) {  
                    treatmentWithState.setState(true); 
                    treatmentWithState.setComment(at.getComment());//08/04/2024 07:41
                }
            }
            theTreatmentWithState.get().add(treatmentWithState);
        }
        return theTreatmentWithState;
    }
    
}

