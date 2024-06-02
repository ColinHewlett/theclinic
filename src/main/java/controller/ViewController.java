/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.*;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JInternalFrame;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
/*28/03/2024import model.PatientNote;*/
import model.non_entity.SystemDefinition;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;


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
            //pCondition.setPatient(patient);
            pCondition.insert();
        }
        PrimaryCondition pConditionFromStore = null;
        /**
         * fetch back each primary condition stored
         */
        for(Condition primaryCondition : pc.get()){
            PrimaryCondition pCondition = (PrimaryCondition)primaryCondition;
            //pCondition.setPatient(patient);
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
        /*pc = new PrimaryCondition(patient);
        pc.setScope(Entity.Scope.FOR_PATIENT);
        pc = pc.read();*/
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
        MEDICAL_CONDITION_VIEW_CONTROLLER_REQUEST,
        NOTIFICATION_VIEW_CONTROLLER_REQUEST,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST,
        PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST,
        PRINT_PATIENT_MEDICAL_HISTORY_REQUEST,//basically requires PMH view controller to achieve this
        PRINT_NEW_PATIENT_DETAILS_REQUEST, //also requires MC view controller to accomplish this
        PRINT_SCHEDULE_REQUEST,// Desktop VC accesses methods in ViewControllet to get job done, so no requirement for a scheduile VC
        PATIENT_SELECTION_VIEW_CONTROLLER_REQUEST,
        PATIENT_VIEW_CONTROLLER_REQUEST,
        
        SCHEDULE_VIEW_CONTROLLER_REQUEST,
        TEST_PATIENT_VIEW_CONTROLLER_REQUEST,
        TREATMENT_VIEW_CONTROLLER_REQUEST,

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
        IMPORT_LIST_FILES,
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
        MEDICAL_CONDITION_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        TREATMENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_MEDICAL_CONDITION_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
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
    
    public static enum MedicalConditionViewControllerActionEvent{
        PRIMARY_CONDITION_CREATE_REQUEST,
        PRIMARY_CONDITION_DELETE_REQUEST,
        PRIMARY_CONDITION_READ_REQUEST,
        PRIMARY_CONDITION_RENAME_REQUEST,
        PRINT_NEW_PATIENT_DETAILS_REQUEST,
        SECONDARY_CONDITION_CREATE_REQUEST,
        SECONDARY_CONDITION_DELETE_REQUEST,
        SECONDARY_CONDITION_READ_REQUEST,
        SECONDARY_CONDITION_RENAME_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public static enum MedicalConditionViewControllerPropertyChangeEvent{
        MEDICAL_CONDITION_VIEW_CONTROLLER_ERROR_RECEIVED,
        PRIMARY_CONDITION_RECEIVED,
        CONDITION_ERROR_RECEIVED
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
    
    public static enum PatientMedicalHistoryViewControllerActionEvent{
        PATIENT_CONDITION_READ_REQUEST,
        PATIENT_CONDITION_CREATE_REQUEST,
        PATIENT_CONDITION_DELETE_REQUEST,
        PATIENT_CONDITION_COMMENT_UPDATE_REQUEST,
        PATIENT_CONDITION_COMMENT_DELETE_REQUEST,
        PRINT_PATIENT_MEDICAL_HISTORY_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public static enum PatientMedicalHistoryViewControllerPropertyChangeEvent{
        CONDITION_WITH_STATE_RECEIVED,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_ERROR_RECEIVED,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_CHANGE_NOTIFICATION
    }
    
    public enum PatientQuestionnaireViewControllerActionEvent{
        PATIENT_QUESTION_READ_REQUEST,
        PATIENT_QUESTION_CREATE_REQUEST,
        PATIENT_QUESTION_DELETE_REQUEST,
        PATIENT_QUESTION_ANSWER_UPDATE_REQUEST,
        PATIENT_QUESTION_ANSWER_DELETE_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public static enum PatientQuestionnaireViewControllerPropertyChangeEvent{
        QUESTION_WITH_STATE_RECEIVED,
        PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_ERROR_RECEIVED,
        PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_CHANGE_NOTIFICATION
    }
    
    public static enum PatientViewControllerActionEvent{
        //primary view requests (commands)
        
        CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST,
        DELETED_PATIENT_REQUEST, 
        
        NULL_PATIENT_REQUEST,
        PATIENT_CREATE_REQUEST,
        PATIENT_DELETE_REQUEST,
        PATIENT_ADDITIONAL_NOTES_VIEW_REQUEST,
        
        PATIENT_DOCTOR_EDITOR_VIEW_REQUEST,
        PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST,
        PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST,
        PATIENT_MEDICAL_HISTORY_1_EDITOR_VIEW_REQUEST,
        PATIENT_MEDICATION_EDITOR_VIEW_REQUEST,
        PATIENT_NOTES_EDITOR_VIEW_REQUEST,
        PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST,
        PATIENT_RECALL_EDITOR_VIEW_REQUEST,
        PATIENT_RECOVER_REQUEST,
        PATIENT_REQUEST,
        PATIENT_SELECTION_VIEW_REQUEST,
        PATIENT_UPDATE_REQUEST,
        PRINT_PATIENT_MEDICAL_HISTORY_REQUEST,
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
        PRINT_SCHEDULE_REQUEST,
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
                    second.cancel(); /*26/04/2024 13:41 UNBOOKABLE APPOINTMENT SLOT EDITOR VIEW log*/
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
    
    /*
    protected ConditionWithState getConditionWithState(
            Condition signature, Patient patient)throws StoreException{
        ConditionWithState result = null;
        if (signature.getIsPrimaryCondition()){
            PrimaryCondition pc = (PrimaryCondition) signature;
            result = getConditionWithState(pc, patient); 
        }else if(signature .getIsSecondaryCondition()){
            SecondaryCondition sc = (SecondaryCondition) signature;
            result = getConditionWithState(sc, patient); 
        }
        return result; //left to sender to handle case result = null;
    }
    */
    /*
    private ConditionWithState getConditionWithState(
            PrimaryCondition signature, Patient patient)throws StoreException{
        PrimaryConditionWithState thePrimaryConditionWithState = 
                new PrimaryConditionWithState();
        PrimaryCondition primaryCondition = new PrimaryCondition();
        primaryCondition.setScope(Entity.Scope.ALL);
        primaryCondition = primaryCondition.read();
        
        PatientPrimaryCondition patientPrimaryCondition =
                new PatientPrimaryCondition(patient);
        patientPrimaryCondition.setScope(Entity.Scope.FOR_PATIENT);
        patientPrimaryCondition = (PatientPrimaryCondition)patientPrimaryCondition.read();
        
        for (Condition c : primaryCondition.get()){
            PrimaryCondition pc = (PrimaryCondition)c;
            PrimaryConditionWithState primaryConditionWithState = 
                    new PrimaryConditionWithState(pc);
            for(PatientCondition patientCondition : patientPrimaryCondition.get()){
                if(pc.getKey().equals(patientCondition.getKey())){
                    primaryConditionWithState.setState(true);
                    primaryConditionWithState.setComment(patientCondition.getComment());
                }
            }
            thePrimaryConditionWithState.get().add(primaryConditionWithState);
        }
        return thePrimaryConditionWithState;
    }
    */
    protected ConditionWithState getConditionWithState(
            PatientCondition patientCondition)throws StoreException{
        
        PrimaryCondition pc = null;
        SecondaryCondition sc = null;
        SecondaryCondition _sc = null;
        Condition condition = null;
        PatientCondition pac = null;
        PrimaryConditionWithState pCWS = null;
        SecondaryConditionWithState sCWS = null;
        
        Patient patient = patientCondition.getPatient();
        ConditionWithState CWS = new ConditionWithState();

        /**
         * case patientCondition is a PatientPrimaryCondition
         */
        if(patientCondition.getIsPatientPrimaryCondition()){ 
            /**
             * fetch all primary conditions
             */
            pc = new PrimaryCondition();
            pc.setScope(Entity.Scope.ALL);
            pc = pc.read();
            condition = pc;
            
            /**
             * initialise each primary condition with their collection of secondary conditions (if any)
             */
            PrimaryCondition _pc = null;
            for(Condition c : pc.get()){
                _pc = (PrimaryCondition)c;
                sc = new SecondaryCondition(_pc);
                sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
                sc = sc.read();
                _pc.setSecondaryCondition(sc);
            }
            
            /**
             * fetch all PatientPrimaryConditions for this patient
             */
            pac = new PatientPrimaryCondition(patient,pc);
            pac.setScope(Entity.Scope.FOR_PATIENT);
            pac = pac.read();
            
            /**
             * for each PrimaryCondition fetched
             */
            for(Condition c : condition.get()){ 
                /**
                 * create a new PrimaryConditionWitState object for this primary condition
                 */
                pCWS = new PrimaryConditionWithState((PrimaryCondition)c);
                
                /**
                 * for each PatientCondition for this patient
                 */
                for(PatientCondition _pac : pac.get()){
                    /**
                     * check this primary condition is owned by this patient
                     */
                    if(c.getKey().equals(_pac.getCondition().getKey())){
                        /**
                         * initialise the new PrimaryConditionWitState object
                         * -- set its state property true
                         * -- initialise its comment property
                         */
                        pCWS.setState(true);
                        pCWS.setComment(_pac.getComment());
                    }

                }
                CWS.get().add(pCWS);
            }
        }
        else {
            /**
             * fetch all secondary conditions for the specified primary condition
             * -- but fetch all details of the secondary condition's parent prim,ary condition first
             */
            sc = (SecondaryCondition)patientCondition.getCondition();
            pc = sc.getPrimaryCondition();
            pc.setScope(Entity.Scope.SINGLE);
            pc = pc.read();

            sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
            sc = sc.read();
            sc.setPrimaryCondition(pc);
            condition = sc;
            
            /**
             * for each secondary condition child of the parent primary cxondition
             * -- copy in the fully initialsed parent primary condition
             * -- create from the secondary condition a secondary condition with state object
             */
            for (Condition c : condition.get()){
                _sc = (SecondaryCondition)c;
                _sc.setPrimaryCondition(pc);
                sCWS = new SecondaryConditionWithState(_sc);
                
                /**
                 * create a new PatientSecondaryCondition object from specified patient and secondary condition
                 * -- use this to fetch the patient condition from the respository if it exists
                 */
                pac = new PatientSecondaryCondition(patient, (SecondaryCondition)c);
                pac.setScope((Entity.Scope.SINGLE));
                pac = pac.read();
                
                /**
                 * if patient condition exists
                 * -- initialise condition with state 'state' property
                 * -- initialise the condition with state's 'comment property from the fetched patent condition
                 */
                if (pac!=null){
                    sCWS.setComment(pac.getComment());
                    sCWS.setState(true);
                }
                /**
                 * add fetched the fully formed condition with state
                 */
                CWS.get().add(sCWS);
            }
        }
        return CWS;
    }
    
    
    
    protected void synchPrimaryConditionStateWithItsSecondaries(PatientCondition pac)throws StoreException{
        Boolean shouldParentPPCExist = null;
        PrimaryCondition pc = null;
        SecondaryCondition sc = null;
        Patient patient = pac.getPatient();
        
        if (pac.getIsPatientPrimaryCondition())
            sc = new SecondaryCondition((PrimaryCondition)pac.getCondition());
        else sc = (SecondaryCondition)pac.getCondition();
        pc = sc.getPrimaryCondition();

        sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
        sc = sc.read();
        
        /**
         * check if any PatientSecondaryConditions in store
         */
        for (Condition c : sc.get()){
            pac = new PatientSecondaryCondition(patient, (SecondaryCondition)c);
            pac.setScope((Entity.Scope.SINGLE));
            pac = pac.read();
            if (pac!=null){
                shouldParentPPCExist = true;
                break;
            }else shouldParentPPCExist = false;
        }
        
        if (shouldParentPPCExist!=null){//this primary condition has child secondaries
            /**
             * yes -- ensure parent PatientPrimaryCondition exists; if not insert a new one
             * no  -- ensure a parent PatientPrimaryCondition does not exist; if one does delete it
             */
            pac = new PatientPrimaryCondition(patient, pc);
            pac.setScope((Entity.Scope.SINGLE));
            pac = pac.read();
            if (shouldParentPPCExist){//yes
                if (pac==null) {
                    pac = new PatientPrimaryCondition(patient, pc);
                    pac.insert();
                }
            }else{
                if (pac!=null) {
                    pac.setScope((Entity.Scope.SINGLE));
                    pac.delete();
                }
            }
        }//do noithinfg if this primary condition has no child secondaries
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
    
    public void sendNoOpMessage(View view){
        String message = "Currently this option is awaiting implementation";
        JOptionPane.showInternalMessageDialog(
                view, message,"View controller",JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected void doFormatAppointmentTreatmentNote(ArrayList<Appointment> appointments)throws StoreException{
        for (Appointment a : appointments){
            if ((a.getPatient()!=null) ||
                    (!a.getPatient().toString().equals(
                            SystemDefinition.APPOINTMENT_UNBOOKABILITY_MARKER))){
                TreatmentWithState treatmentWithState = getTreatmentsWithState(a);
                String note = new String();
                for(TreatmentWithState tws : treatmentWithState.get()){
                    if (tws.getState()) {
                        note = note + " " + tws.getTreatment().getDescription();
                        if(tws.getComment()!=null){
                            if(!tws.getComment().trim().isEmpty())
                                note = note + " (" + tws.getComment() + ")" + " /";
                            else note = note + " /";
                        }
                        else note = note + " /";
                        a.setNotes(note);
                    }  
                }
                note = a.getNotes();

                if (note!=null){
                    if (note.substring(note.length()-1).equals("/")){
                        note = note.substring(0, note.length() - 2);
                        a.setNotes(note);
                    }
                }
            }
        }
    }
    
    private boolean isForPatient = false;
    private boolean getIsForPatient(){
        return isForPatient;
    }
    private void setIsForPatient(boolean value){
        isForPatient = value;
    }
    
    protected void doPrintAppointmentScheduleForDay(LocalDate day){
        doAppointmentForDayRequestForPrintScheduleRequest(day);
        XWPFDocument document = null;
        XWPFTable table = null;
        CTTblWidth tableWidth = null;
        
        InputStream fis = getClass().getResourceAsStream("/Appointment schedule.docx");
        try{
            document = new XWPFDocument(fis);
            setDocument(document);
            List<XWPFTable> tables = getDocument().getTables();
            for(int index = 0; index < tables.size(); index++){
                table = document.getTableArray(index);
                tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
                tableWidth.setW(BigInteger.valueOf(10300)); // 8000 in Twips (1/20 of a point)
            }
            populateAppointmentScheduleHeaderTable(document.getTableArray(0), day);
            populateAppointmentScheduleTable(document.getTableArray(1));
            
            FileOutputStream out = new FileOutputStream("AppointmentScheduleForDay.docx");
            document.write(out);
            System.out.println(new File(".").getAbsolutePath());
            System.out.println("Word document with complex table created successfully!");
            out.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
            
    }
      
    protected void doPrintPatientMedicalHistoryQuestionnaireRequest(boolean isNotForNewPatient)throws StoreException{
        setPatient(getDescriptor().getControllerDescription().getPatient());
        setIsForPatient(isNotForNewPatient);
        List<XWPFTableRow> rowList = null;
        XWPFDocument document = null;
        XWPFParagraph paragraph = null;
        XWPFTableRow row = null;
        XWPFRun run = null;
        XWPFTable table;
        CTTblWidth tableWidth = null;
        CTR ctr = null;
        CTTc ctTc = null;
        XWPFTableCell cell = null;
        HashMap<PatientDetailsTableName,Integer>tables = null;
        int tableColumnCount = 6;
        int tableRowCount = 0;
        boolean debug = false;
        InputStream fis = getClass().getResourceAsStream("/PatientMedicalHistory4.docx");
        try{
            document = new XWPFDocument(fis);
            setDocument(document);
            tables = this.getTablesFromDocument();
            for(int index = 0; index < tables.size(); index++){
                table = document.getTableArray(index);
                tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
                tableWidth.setW(BigInteger.valueOf(10300)); // 8000 in Twips (1/20 of a point)
            } 
            
            populate(PatientDetailsTableName.PATIENT_CONTACT_DETAILS);
            populate(PatientDetailsTableName.PATIENT_QUESTIONNAIRE);
            populate(PatientDetailsTableName.PATIENT_MEDICAL_HISTORY);
            
            
            FileOutputStream out = new FileOutputStream("PatientMedicalHistory.docx");
            document.write(out);
            System.out.println(new File(".").getAbsolutePath());
            System.out.println("Word document with complex table created successfully!");
            out.close();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (WordTableBuilderException ex){
            ex.printStackTrace();
        }
    }
    
    private enum PatientDetailsTableName{
        PATIENT_CONTACT_DETAILS,
        TABLE_HEADER_1,
        PATIENT_QUESTIONNAIRE,
        TABLE_HEADER_2,
        PATIENT_HAVE_YOU_QUESTIONS,
        TABLE_HEADER_3,
        PATIENT_YOU_AND_THE_CLINIC_QUESTIONS,
        PATIENT_MEDICAL_HISTORY}
        
    private HashMap<PatientDetailsTableName, Integer> patientDetailsTableMap = null;
    private HashMap<PatientDetailsTableName, Integer> getPatientDetailsTableNameMap(){
        return patientDetailsTableMap;
    }
    private void setPatientDetailsTableNameMap(HashMap<PatientDetailsTableName, Integer> value){
        patientDetailsTableMap = value;
    }
    

    private XWPFTable getPatientDetailsTableFromName(PatientDetailsTableName pdtn){
        int tableIndex = getPatientDetailsTableNameMap().get(pdtn);
        return getDocument().getTableArray(tableIndex); 
    }
    
    private HashMap<PatientDetailsTableName,Integer> getTablesFromDocument()throws WordTableBuilderException{
        HashMap<PatientDetailsTableName,Integer> map = null;
        List<XWPFTable> tables = getDocument().getTables();
        if (tables.size()==5){
            map = new HashMap<>();
            map.put(PatientDetailsTableName.PATIENT_CONTACT_DETAILS, 0);
            map.put(PatientDetailsTableName.TABLE_HEADER_1, 1);
            map.put(PatientDetailsTableName.PATIENT_QUESTIONNAIRE, 2);
            map.put(PatientDetailsTableName.TABLE_HEADER_2, 3);
            map.put(PatientDetailsTableName.PATIENT_MEDICAL_HISTORY, 4);
            setPatientDetailsTableNameMap(map);
        }else{
            String message = "Number of tables located in document = " + tables.size() + "; number expected = 5\n"
                    + "WordTableBuilderException raised in ViewController.getTablesFromDocument(XWPFDocument document)";
            throw new WordTableBuilderException(message,
                    WordTableBuilderException.ExceptionType.INCORRECT_NUMBER_OF_TABLES_FOUND);  
        }
        return map;
    }
    
    private ConditionWithState getConditionWithStateFromCondition(Condition condition)throws StoreException{
        PrimaryConditionWithState pCWS = null;
        SecondaryConditionWithState sCWS = null;
        PatientCondition pac = null;
        PrimaryCondition pc = null;
        SecondaryCondition sc = null;
        ConditionWithState result = null;
        if (condition.getIsPrimaryCondition()){
            pc = (PrimaryCondition) condition;
            pac = new PatientPrimaryCondition(
                    getDescriptor().getControllerDescription().getPatient(),pc );
            pac.setScope(Entity.Scope.SINGLE);
            pac = pac.read();
            if (pac!=null){
                pCWS = new PrimaryConditionWithState(pc);
                pCWS.setState(true);
                pCWS.setComment(pac.getComment());
                result = pCWS;
            } 
        }else if(condition.getIsSecondaryCondition()){
            sc = (SecondaryCondition) condition;
            pac = new PatientSecondaryCondition(
                    getDescriptor().getControllerDescription().getPatient(),sc );
            pac.setScope(Entity.Scope.SINGLE);
            pac = pac.read();
            if (pac!=null){
                sCWS = new SecondaryConditionWithState(sc);
                sCWS.setState(true);
                sCWS.setComment(pac.getComment());
                result = sCWS;
            }
        }
        return result;
    }
    
    private void setTextInCell(XWPFTableCell cell,String text,SystemDefinition.ScheduleTable entity){
        if(entity!=null){ //switches between 'header' table and 'schedule' table
            switch(entity){
                case PATIENT:
                    if (text.contains("UNBOOKABLE")) runText(cell, text, SystemDefinition.FONT.DEFAULT_RED, ParagraphAlignment.CENTER);
                    else if(text.contains("AVAILABLE SLOT")) runText(cell, text, SystemDefinition.FONT.DEFAULT_BLUE, ParagraphAlignment.CENTER);
                    else runText(cell, text, SystemDefinition.FONT.DEFAULT, ParagraphAlignment.LEFT);
                    break;
                case FROM:
                    runText(cell, text, SystemDefinition.FONT.DEFAULT, ParagraphAlignment.CENTER);
                    break;
                case TO:
                    runText(cell, text, SystemDefinition.FONT.DEFAULT, ParagraphAlignment.CENTER);
                    break;
                case TREATMENT:
                    runText(cell, text, SystemDefinition.FONT.DYNAMIC, ParagraphAlignment.LEFT);
                    break;
                case CONFIRMED:
                    runText(cell, text, SystemDefinition.FONT.TICK, ParagraphAlignment.CENTER);
                    break;
            }
        }else runText(cell, text, SystemDefinition.FONT.SCHEDULE_HEADER, ParagraphAlignment.CENTER);
    }
    
    private void setTextInCell(boolean isWithState, XWPFTable table, int row, XWPFTableCell cell, Condition condition)throws StoreException{
        ConditionWithState CWS = null;
        if (condition.getIsPrimaryCondition()){
                runText(cell, condition.getDescription(), SystemDefinition.FONT.DEFAULT_BOLD,ParagraphAlignment.LEFT);
        }else runText(cell, condition.getDescription(), SystemDefinition.FONT.DEFAULT,ParagraphAlignment.LEFT);

        if (isWithState){
            /**
             * if a primary condition
             * -- fetch patient primary condition from system if it exists
             * else
             * -- fetch patient secondary condition from system if it exists
             * if a patient condition does exist print the state and attached comment (if any)
             */
            if (condition.getIsPrimaryCondition()){
                PrimaryCondition pc = (PrimaryCondition)condition;
                if(pc.getSecondaryCondition().get().isEmpty()){ //no aecondaries
                    CWS = getConditionWithStateFromCondition(pc);
                }
            }else if(condition.getIsSecondaryCondition()){
                SecondaryCondition pc = (SecondaryCondition)condition;
                CWS = getConditionWithStateFromCondition(pc);
            }
            if (CWS!=null){
                cell = table.getRow(row).getCell(2);
                runText(cell, "P", SystemDefinition.FONT.TICK,ParagraphAlignment.CENTER );// a tick symbol in Wingdings2
                if (CWS.getComment()!=null){
                    cell = table.getRow(row).getCell(3);
                    runText(cell, CWS.getComment(), SystemDefinition.FONT.DYNAMIC,ParagraphAlignment.LEFT );
                }
            }
        }
    }
    
    private void runText(XWPFTableCell cell, String text, SystemDefinition.FONT font,ParagraphAlignment alignment ){
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        int cellWidthTwips = 4813;
        cell.getCTTc().addNewTcPr().addNewNoWrap();
        switch (font){
            case SCHEDULE_HEADER:
                if (text!=null){
                    if (!text.trim().isEmpty()){
                        run.setText(text);
                        run.setFontFamily(font.fontName());
                        run.setFontSize(font.fontSize());

                    }
                }
                break;
            case DEFAULT_RED:
            case DEFAULT_BLUE:
            case DEFAULT_BOLD:
            case DEFAULT:
                if (text!=null){
                    if (!text.trim().isEmpty()){
                        run.setText(text);
                        run.setFontFamily(font.fontName());
                        run.setFontSize(font.fontSize());
                        run.setBold(font.IsFontBold());
                        run.setColor(font.fontColor());
                    }
                }
                break;
            case DYNAMIC:
                // Set no word wrap
                if (text!=null){
                    if (!text.trim().isEmpty()){
                        run.setText(text);
                        run.setFontFamily(font.fontName());
                        //int cellWidthTwips = getTableCellWidthInTwips(cell);
                        int maxFontSize = calculateMaxFontSize(text, cellWidthTwips);
                        run.setFontSize(maxFontSize);
                    }
                }
                break;
            case TICK:
                if (text!=null){
                    if (!text.trim().isEmpty()){
                        run.setText(text);
                        run.setFontFamily(font.fontName());
                        run.setFontSize(font.fontSize());
                    }
                }

                break;
        }

        paragraph.setAlignment(alignment);
        verticallyAlignTextInCell(cell);
        
    }
    
    /*
    private void setTextInCell(XWPFTableCell cell, String text){
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        

        // Set the font
        run.setFontFamily("Arial Narrow");
        run.setFontSize(10);
        verticallyAlignTextInCell(cell);
    }
    */
    
    private void verticallyAlignTextInCell(XWPFTableCell cell){
        CTTc ctTc = cell.getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();
        ctTcPr.addNewVAlign().setVal(STVerticalJc.CENTER);
    }
    
    private void shadeCellOnEvenRows(XWPFTable table, int row, int column){
        XWPFTableCell cell = null;
        
        if(row % 2 == 0) {
            cell = table.getRow(row).getCell(column);
            cell.setColor("F2F2F2");
        }                
    }
    
    private static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    private static void mergeCellsHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        XWPFTableCell cell = table.getRow(row).getCell(fromCol);
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        for (int colIndex = fromCol + 1; colIndex <= toCol; colIndex++) {
            cell = table.getRow(row).getCell(colIndex);
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }
    
    private XWPFTable setHeaderRowHeight(XWPFTable table, int row, int pips){
        XWPFTableCell cell = null;
        cell = table.getRow(row).getCell(0);
        // Get the table cell properties
        CTTc ctTc = cell.getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();

        // Set the height of the cell (in twentieths of a point, 1 point = 20 twentieths)
        CTTblWidth cellHeight = ctTcPr.addNewTcW();
        cellHeight.setType(STTblWidth.DXA);
        cellHeight.setW(BigInteger.valueOf(pips)); // Example height (50 points)
        return table;
    }
    
    private void fetchMedicalConditionsFromStore(){
        PrimaryCondition primaryCondition = new PrimaryCondition();
        primaryCondition.setScope(Entity.Scope.ALL);
        try{
            primaryCondition = primaryCondition.read();
            if (primaryCondition.get().isEmpty()){
                primaryCondition = extractMedicalHistoryFromTemplate();
                for(Condition condition : primaryCondition.get()){
                    PrimaryCondition pCondition = (PrimaryCondition)condition;
                    //pCondition.setPatient(patient);
                    Integer pConditionKey = pCondition.insert();
                    if (!pCondition.getSecondaryCondition().get().isEmpty()){
                        for (Condition c : pCondition.getSecondaryCondition().get()){
                            SecondaryCondition sCondition = (SecondaryCondition)c;
                            sCondition.setPrimaryCondition(new PrimaryCondition(pConditionKey));
                            sCondition.insert();
                        }
                    }
                }   
                setExtractedPrimaryConditionFromTemplate(primaryCondition);
            }
            getFatPrimaryConditions();
        }catch(StoreException ex){
            String message = ex.getMessage() ;
            displayErrorMessage(message, 
                    "Medical condition view controller error", 
                    JOptionPane.WARNING_MESSAGE);
 
        }catch (TemplateReaderException ex){

        }   
    }
    
    private void getFatPrimaryConditions()throws StoreException{
        PrimaryCondition primaryCondition = new PrimaryCondition();
        primaryCondition.setScope(Entity.Scope.ALL);
        primaryCondition = primaryCondition.read();
        SecondaryCondition sc = null;
        PrimaryCondition pc = null;
        for(Condition c : primaryCondition.get()){
            pc = (PrimaryCondition)c;
            sc = new SecondaryCondition(pc);
            sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
            sc = sc.read();
            pc.setSecondaryCondition(sc);
        }
        getDescriptor().getControllerDescription()
                .setPrimaryCondition(primaryCondition);
    }
    
    private XWPFDocument document = null;
    private XWPFDocument getDocument(){
        return document;
    }
    private void setDocument(XWPFDocument value){
        document = value;
    }
    
    private XWPFTable table = null;
    private XWPFTable getTable(){
        return table;
    }
    private void setTable(XWPFTable value){
        table = value;
    }
    
    private void populate(PatientDetailsTableName pdtn)throws StoreException{
        XWPFTable _table = getPatientDetailsTableFromName(pdtn);
        switch (pdtn){
            case PATIENT_CONTACT_DETAILS:
                populatePatientContactDetails(_table);
                break;
            case PATIENT_QUESTIONNAIRE:
                populatePatientQuestionnaireTable(_table);
                break;
            case PATIENT_MEDICAL_HISTORY:
                populatePatientMedicalHistoryTable(_table);
                break;
        }
    }

    private Patient patient = null;
    private void setPatient(Patient value){
        patient = value;
    }
    private Patient getPatient(){
        return patient;
    }
    
    private static void appendTextToCell(XWPFTableCell cell, String textToAppend) {
        // Retrieve the existing paragraphs
        XWPFParagraph paragraph = cell.getParagraphs().get(0);

        // Append new text to the existing paragraph
        XWPFRun run = paragraph.createRun();
        run.setText(textToAppend);
    }
    
    private void populatePatientMedication(XWPFTable table)throws StoreException{
        int rowCount1 = 0;
        int rowCount2 = 0;
        String s = null;
        XWPFTableCell cell = null;
        Medication medication = getPatient().getMedicalHistory().getMedication();
        cell = table.getRow(SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.row()).getCell(1);
        runText(cell, SystemDefinition.TICK, SystemDefinition.FONT.TICK,ParagraphAlignment.CENTER);
        for (Medication _medication : medication.get()){
            if (rowCount1 < SystemDefinition.TABLE_2.MEDICATION_LAST_ROW.row() - SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.row() + 1){
                cell = table.getRow(SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.row() + rowCount1).getCell(SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.column());
                runText(cell, _medication.getDescription(), SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
                rowCount1++;
            }else if (rowCount1 < (SystemDefinition.TABLE_2.MEDICATION_LAST_ROW.row() - SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.row() + 1) * 2) {
                cell = table.getRow(SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.row() + rowCount2++).getCell(SystemDefinition.TABLE_2.MEDICATION_FIRST_ROW.column()); 
                s = "; " + _medication.getDescription();
                runText(cell, s, SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
                rowCount1++;
            }else break; //quit if more than 10 medications
        } 
    }

    private void populatePatientQuestionnaireTable(XWPFTable table)throws StoreException{
        boolean isMedicationQuestion = false;
        
        XWPFTableCell cell = null;
        XWPFTableRow row = null;
        PatientQuestion pq = null;
        
        
        if (getIsForPatient()){
            Medication medication = getPatient().getMedicalHistory().getMedication();
            QuestionWithState qws = new QuestionWithState();
            ArrayList<QuestionWithState> collection = new ArrayList<>();
            Patient patient = getDescriptor().getControllerDescription().getPatient();
            Question question = new Question();
            question.setScope(Entity.Scope.ALL);
            question = question.read();
            for(Question q : question.get()){
                qws = new QuestionWithState(q);
                pq = new PatientQuestion(patient, q);
                pq.setScope(Entity.Scope.SINGLE);
                pq = pq.read();
                if (pq!=null){
                    qws.setAnswer(pq.getAnswer());
                    qws.setState(true);
                }
                collection.add(qws);      
            }
            qws.set(collection);
            int index = -1;
            for (QuestionWithState q : qws.get()){
                index++;
                if (q.getState()){
                    switch(index){
                        case 0:
                            row = table.getRow(SystemDefinition.TABLE_2._1.row());
                            break;
                        case 1:
                            populatePatientMedication(table);
                        case 3://case 1 & 2 there to handle patient meds if any
                            row = table.getRow(SystemDefinition.TABLE_2._4.row());
                            break;
                        case 4:
                            row = table.getRow(SystemDefinition.TABLE_2._5.row());
                            break;
                        case 5:
                            row = table.getRow(SystemDefinition.TABLE_2._6.row());
                            break;
                        case 6:
                            row = table.getRow(SystemDefinition.TABLE_2._7.row());
                            break;            
                        case 7:
                            row = table.getRow(SystemDefinition.TABLE_2._8.row());
                            break;
                        case 8:
                            row = table.getRow(SystemDefinition.TABLE_2._9.row());
                            break;
                        case 9:
                            row = table.getRow(SystemDefinition.TABLE_2._10.row());
                            break;
                        case 10:
                            row = table.getRow(SystemDefinition.TABLE_2._11.row());
                            break;
                        case 11:
                            row = table.getRow(SystemDefinition.TABLE_2._12.row());
                            break;
                        case 12:
                            row = table.getRow(SystemDefinition.TABLE_2._13.row());
                            break;   
                    }
                    if (index!=1 && index!=2){
                        if (q.getState()){
                            cell = row.getCell(1);
                            runText(cell, SystemDefinition.TICK,SystemDefinition.FONT.TICK,ParagraphAlignment.CENTER);
                            cell = row.getCell(2);
                            runText(cell, q.getAnswer(),SystemDefinition.FONT.DYNAMIC,ParagraphAlignment.LEFT);
                        } 
                    }
                }
            }
        }
    } 

    private void populatePatientContactDetails(XWPFTable table)throws StoreException{
        String s = null;
        XWPFTableCell cell = null;
        if (getIsForPatient()){
            Doctor doctor = getPatient().getMedicalHistory().getDoctor();
            if (doctor!=null){
                cell = table.getRow(SystemDefinition.TABLE_1.GP.row()).getCell(SystemDefinition.TABLE_1.GP.column());
                String drDetails = "";
                s = doctor.getTitle();
                if (s!=null)
                    drDetails = doctor.getTitle() + "; ";
                s = doctor.getLine1();
                if (s!=null)
                    drDetails = drDetails + doctor.getLine1() + "; ";
                s = doctor.getLine2();
                if (s!=null)
                    drDetails = drDetails + doctor.getLine2() + "; ";
                s = doctor.getTown();
                if (s!=null)
                    drDetails = drDetails + doctor.getTown() + "; ";
                s = doctor.getCounty();
                if (s!=null)
                    drDetails = drDetails + doctor.getCounty() + "; ";
                s = doctor.getPostcode();
                if (s!=null)
                    drDetails = drDetails + doctor.getPostcode() + ";";
                if (!drDetails.trim().isEmpty())
                    runText(cell, drDetails,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
                s = doctor.getPhone();
                if (s!=null){
                    cell = table.getRow(SystemDefinition.TABLE_1.GP_PHONE.row()).getCell(SystemDefinition.TABLE_1.GP_PHONE.column());
                    runText(cell, doctor.getPhone(),SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
                }
            }
            cell = table.getRow(SystemDefinition.TABLE_1.TITLE.row()).getCell(SystemDefinition.TABLE_1.TITLE.column());
            runText(cell, getPatient().getName().getTitle(),SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            
            cell = table.getRow(SystemDefinition.TABLE_1.ADDRESS.row()).getCell(SystemDefinition.TABLE_1.ADDRESS.column());
            String address = "";
            s = getPatient().getAddress().getLine1();
            if (s!=null) address = address + s + "; ";
            s = getPatient().getAddress().getLine2();
            if (s!=null) address = address + s + "; ";
            s = getPatient().getAddress().getTown();
            if (s!=null) address = address + s + "; ";
            s = getPatient().getAddress().getCounty();
            if (s!=null) address = address + s;
            runText(cell, address,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.DOB.row()).getCell(SystemDefinition.TABLE_1.DOB.column());
            LocalDate d = getPatient().getDOB();
            if (d!=null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String formattedDate = getPatient().getDOB().format(formatter);
                runText(cell, formattedDate,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            }
            cell = table.getRow(SystemDefinition.TABLE_1.EMAIL.row()).getCell(SystemDefinition.TABLE_1.EMAIL.column());
            s = getPatient().getEmail();
            if (s!=null) 
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.FORENAMES.row()).getCell(SystemDefinition.TABLE_1.FORENAMES.column());
            s = getPatient().getName().getForenames();
            if (s!=null) 
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.GENDER.row()).getCell(SystemDefinition.TABLE_1.GENDER.column());
            s = getPatient().getGender();
            if (s!=null)
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.PHONE_1.row()).getCell(SystemDefinition.TABLE_1.PHONE_1.column());
            s = getPatient().getPhone1();
            if (s!=null)
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.PHONE_2.row()).getCell(SystemDefinition.TABLE_1.PHONE_2.column());
            s = getPatient().getPhone2();
            if (s!=null)
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.POSTCODE.row()).getCell(SystemDefinition.TABLE_1.POSTCODE.column());
            s = getPatient().getAddress().getPostcode();
            if (s!=null)
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            cell = table.getRow(SystemDefinition.TABLE_1.SURNAME.row()).getCell(SystemDefinition.TABLE_1.SURNAME.column());
            s = getPatient().getName().getSurname();
            if (s!=null)
                runText(cell, s,SystemDefinition.FONT.DEFAULT,ParagraphAlignment.CENTER);
            
        }
    }
    
    private void populateAppointmentScheduleHeaderTable(XWPFTable table, LocalDate day){
        String _day = day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String header = "Appointment schedule (" + _day + ")";
        XWPFTableCell cell = table.getRow(0).getCell(0);
        setTextInCell(cell, header, null);
    }
    
    private void populateAppointmentScheduleTable(XWPFTable table){
        XWPFTableCell cell = null;
        int row = 0;
        String patient = "";
        String from = "";
        String to = "";
        String treatment = "";
        String confirmed = "";
        
        Iterator<Appointment> it = getDescriptor().getControllerDescription().getAppointmentSlotsForDay().iterator();
        while (it.hasNext()){
            row++;
            Appointment appointment = (Appointment)it.next();

            if (appointment.getPatient()==null) patient = "<AVAILABLE SLOT>";
            else if (appointment.getPatient().toString()
                    .equals(SystemDefinition.APPOINTMENT_UNBOOKABILITY_MARKER))
                patient = "<UNBOOKABLE_SLOT>";
            else patient = appointment.getPatient().toString();


            from = appointment.getStart().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            Long duration = appointment.getDuration().toMinutes();
            to = appointment.getStart().toLocalTime().plusMinutes(duration)
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

            treatment = appointment.getNotes();
            
            if (row > 1) table.createRow();//this because we already start with one blank row pluis the column headers
            setTableRowHeightInTwips(table.getRow(row), SystemDefinition.SCHEDULE_TABLE_CELL_HEIGHT);
            cell = table.getRow(row).getCell(SystemDefinition.ScheduleTable.PATIENT.column());
            setTextInCell(cell,patient,SystemDefinition.ScheduleTable.PATIENT);
            cell = table.getRow(row).getCell(SystemDefinition.ScheduleTable.FROM.column());
            setTextInCell(cell,from,SystemDefinition.ScheduleTable.FROM);
            cell = table.getRow(row).getCell(SystemDefinition.ScheduleTable.TO.column());
            setTextInCell(cell,to,SystemDefinition.ScheduleTable.TO);
            cell = table.getRow(row).getCell(SystemDefinition.ScheduleTable.TREATMENT.column());
            setTextInCell(cell,treatment,SystemDefinition.ScheduleTable.TREATMENT);
            cell = table.getRow(row).getCell(SystemDefinition.ScheduleTable.CONFIRMED.column());
            if (appointment.getHasPatientBeenContacted())confirmed = SystemDefinition.TICK;
            else confirmed = null;
            setTextInCell(cell,confirmed,SystemDefinition.ScheduleTable.CONFIRMED);
        }
    }
    
    private void populatePatientMedicalHistoryTable(XWPFTable table)throws StoreException{
        setIsForPatient(isForPatient);
        XWPFTableCell cell = null;
        fetchMedicalConditionsFromStore();
        int pcRowCount = 0;
        PrimaryCondition pc = getDescriptor()
                .getControllerDescription().getPrimaryCondition();
        boolean isLastEntryPrimaryCondition = false;
        boolean isLastEntrySecondaryCondition = false;
        int rowCount = 0;
        pc = getDescriptor()
                .getControllerDescription().getPrimaryCondition();
        for (Condition c : pc.get()){
            PrimaryCondition _pc = (PrimaryCondition)c;
            if (rowCount > 0) {
                rowCount++;
                table.createRow();
               setTableRowHeightInTwips(table.getRow(rowCount), SystemDefinition.EXACT_TABLE_CELL_HEIGHT_IN_TWIPS);
                pcRowCount = rowCount;
            }
            cell = table.getRow(rowCount).getCell(0);
            setTextInCell(getIsForPatient(), table, rowCount, cell, c);
            isLastEntryPrimaryCondition = true;
            if (!_pc.getSecondaryCondition().get().isEmpty()){
                for(Condition _c : _pc.getSecondaryCondition().get()){
                    if (isLastEntryPrimaryCondition) {
                        cell = table.getRow(rowCount).getCell(1);
                        isLastEntryPrimaryCondition = false;
                    }else {
                        table.createRow();
                        setTableRowHeightInTwips(table.getRow(++rowCount), 380);
                        cell = table.getRow(rowCount).getCell(1);
                    }
                    setTextInCell(getIsForPatient(), table, rowCount, cell, _c);
                    shadeCellOnEvenRows(table, rowCount, 1);
                    shadeCellOnEvenRows(table, rowCount, 2);
                    shadeCellOnEvenRows(table, rowCount, 3);
                    isLastEntrySecondaryCondition = true;
                }
                if (isLastEntrySecondaryCondition){
                    /*
                    if (!getIsForPatient()){
                        table.createRow();
                        cell = table.getRow(++rowCount).getCell(1);
                        runText(cell, "Other ...", SystemDefinition.FONT.DEFAULT, ParagraphAlignment.LEFT);
                    }*/
                    shadeCellOnEvenRows(table, rowCount, 1);
                    shadeCellOnEvenRows(table, rowCount, 2);
                    shadeCellOnEvenRows(table, rowCount, 3);
                    isLastEntrySecondaryCondition = false;
                    mergeCellsVertically(table, 0, pcRowCount, rowCount);
                    verticallyAlignTextInCell(table.getRow(pcRowCount).getCell(0));
                }
            }else{
                mergeCellsHorizontally(table, pcRowCount, 0, 1);
                shadeCellOnEvenRows(table, pcRowCount, 0);
                shadeCellOnEvenRows(table, pcRowCount, 2);
                shadeCellOnEvenRows(table, pcRowCount, 3);
            }
        }
        table.createRow();
        cell = table.getRow(++rowCount).getCell(0);
        table.getRow(rowCount).setHeight(500);
        runText(cell,"Other ...", SystemDefinition.FONT.DEFAULT, ParagraphAlignment.LEFT);
        shadeCellOnEvenRows(table, rowCount, 0);
        mergeCellsHorizontally(table,rowCount,0 , 3);
    }
    
    private int getTableCellWidthInTwips(XWPFTableCell cell) {
        BigInteger bigInteger = null;
        CTTblWidth cellWidth = cell.getCTTc().getTcPr().getTcW();
        if (cellWidth != null && cellWidth.getW() != null) {
            bigInteger = (BigInteger)cellWidth.getW();
            return bigInteger.intValue();
        }
        return -1; // Return -1 if width is not set
    }
    
    /*
    private void setCellTextFit(XWPFTableCell cell, String text) {
        // Clear existing paragraphs in the cell
        cell.removeParagraph(0);

        // Create a new paragraph
        XWPFParagraph paragraph = cell.addParagraph();

        // Create a new run and set the text
        XWPFRun run = paragraph.createRun();
        run.setText(text);

        // Calculate the maximum font size that will fit in the cell
        //int cellWidthTwips = 3000; // Fixed cell width in twips
        int cellWidthTwips = getTableCellWidthInTwips(cell);
        int maxFontSize = calculateMaxFontSize(text, cellWidthTwips);

        // Set the font size
        run.setFontSize(maxFontSize);

        // Set no word wrap
        cell.getCTTc().addNewTcPr().addNewNoWrap();
    }*/
    
    private int calculateMaxFontSize(String text, int cellWidthTwips) {
        int fontSize = 10; // Start with a default font size
        while (true) {
            int textWidthTwips = estimateTextWidth(text, fontSize);
            if (textWidthTwips <= cellWidthTwips || fontSize <= 9) {
                break;
            }
            fontSize--;
        }
        return fontSize;
    }
    
    private static int estimateTextWidth(String text, int fontSize) {
        // Approximate character width in twips for a given font size
        int charWidthTwips = fontSize * 7;
        return text.length() * charWidthTwips;
    }
    private static void setTableRowHeightInTwips(XWPFTableRow row, int heightTwips) {
        CTTrPr trPr = row.getCtRow().addNewTrPr();
        CTHeight ctHeight = trPr.addNewTrHeight();
        ctHeight.setVal(BigInteger.valueOf(heightTwips));
        ctHeight.setHRule(STHeightRule.EXACT);
    }
    
    protected void doAppointmentForDayRequestForPrintScheduleRequest(LocalDate day){
        Appointment appointment = new Appointment();
        appointment.setStart(day.atStartOfDay());
        appointment.setScope(Entity.Scope.FOR_DAY);
        try{
            appointment.read();
            if (!appointment.get().isEmpty()){
                if (appointment.get().get(0).getStart().
                        isBefore(day.atTime(ViewController.FIRST_APPOINTMENT_SLOT))){
                    getDescriptor().getControllerDescription().
                            setAppointmentEarlyStart(appointment.get().get(0).getStart());
                }else getDescriptor().getControllerDescription().
                            setAppointmentEarlyStart(null);
            }
            if (!appointment.get().isEmpty()){
                if (appointment.get().get(appointment.get().size()-1).getStart().
                        isAfter(day.atTime(ViewController.LAST_APPOINTMENT_SLOT))){
                    getDescriptor().getControllerDescription().
                            setAppointmentLateStart(appointment.get().
                                    get(appointment.get().size()-1).getStart());
                }else getDescriptor().getControllerDescription().
                            setAppointmentLateStart(null);
            }
            /**
             * generate appointment note from treatments selected
             */
            doFormatAppointmentTreatmentNote(appointment.get());

            getDescriptor().getControllerDescription().setAppointments(appointment.get());
            getDescriptor().getControllerDescription().setScheduleDay(day);
            //doAppointeeReminderCount(appointment.get());
            //getUpdatedAppointmentSlotsForDay(appointment);// = getUpdatedAppointmentSlotsForDay(appointment) contents
            ArrayList<Appointment> appointmentSlotsForDay =
                getAppointmentsForSelectedDayIncludingEmptySlotsForPrintScheduleRequest(appointment.get(),appointment.getStart().toLocalDate());
            getDescriptor().getControllerDescription().setAppointmentSlotsForDay(appointmentSlotsForDay); 
        }
        catch (StoreException ex){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String message = ex.getMessage() 
                    + "\nRaised in doAppointmentForDayRequest(" + day.format(formatter) + ")";
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private ArrayList<Appointment> getAppointmentsForSelectedDayIncludingEmptySlotsForPrintScheduleRequest(
            ArrayList<Appointment> appointments, LocalDate day) {
        LocalDateTime nextEmptySlotStartTime = null;
        
        /* code to handle an early appointment */
        if (!appointments.isEmpty()){
            if (appointments.get(0).getStart().toLocalTime().
                    isBefore(ViewController.FIRST_APPOINTMENT_SLOT)){
                nextEmptySlotStartTime = appointments.get(0).getStart();
            }else {
                nextEmptySlotStartTime = LocalDateTime.of(day, 
                                        ViewController.FIRST_APPOINTMENT_SLOT);
            }
        }else{
           nextEmptySlotStartTime = LocalDateTime.of(day, 
                                        ViewController.FIRST_APPOINTMENT_SLOT); 
        }


        ArrayList<Appointment> apptsForDayIncludingEmptySlots = new ArrayList<>();      
        Iterator<Appointment> it = appointments.iterator();
        /**
         * check for no appointments on this day if no appointment create a
         * single empty slot for whole day
         */
        if (appointments.isEmpty()) {
            apptsForDayIncludingEmptySlots.add(createEmptyAppointmentSlotForPrintScheduleRequest(
                                                nextEmptySlotStartTime));
        } 
        /**
         * At least one appointment scheduled, calculate empty slot intervals
         * interleaved appropriately (time ordered) with scheduled
         * appointment(s)
         */
        else { 
            while (it.hasNext()) {
                Appointment appointment = it.next();
                Duration durationToNextSlot = Duration.between(
                        nextEmptySlotStartTime,appointment.getStart() );
                /**
                 * check if no time exists between next scheduled appointment
                 * If so update nextEmptySlotStartTime to immediately follow
                 * the current scheduled appointment
                 */
                if (durationToNextSlot.isZero()) {
                    nextEmptySlotStartTime = 
                            appointment.getStart().plusMinutes(appointment.getDuration().toMinutes());
                    apptsForDayIncludingEmptySlots.add(appointment);
                } 
                /**
                 * If time exists between nextEmptySlotTime and the current 
                 * appointment,
                 * -- create an empty appointment slot to fill the gap
                 * -- re-initialise nextEmptySlotTime to immediately follow the
                 *    the current appointment
                 */
                else {
                    Appointment emptySlot = createEmptyAppointmentSlotForPrintScheduleRequest(nextEmptySlotStartTime,
                            Duration.between(nextEmptySlotStartTime, appointment.getStart()).abs());
                    apptsForDayIncludingEmptySlots.add(emptySlot);
                    apptsForDayIncludingEmptySlots.add(appointment);
                    nextEmptySlotStartTime =
                            appointment.getStart().plusMinutes(appointment.getDuration().toMinutes());
                }
            }
        }
        Appointment lastAppointment = 
                apptsForDayIncludingEmptySlots.get(apptsForDayIncludingEmptySlots.size()-1);
        //06/08/2022 08:49
        if (getIsBookedStatusForPrintScheduleRequest(lastAppointment)){
            //15/07/2023 enables an appointment to run over LAST_APPOINTMENT_SLOT time
 
            Duration durationToDayEnd = 
                    Duration.between(nextEmptySlotStartTime.toLocalTime(), ViewController.LAST_APPOINTMENT_SLOT);
            if (!durationToDayEnd.isNegative()){
                if (!durationToDayEnd.isZero()) {
                    Appointment emptySlot = createEmptyAppointmentSlotForPrintScheduleRequest(nextEmptySlotStartTime);
                    apptsForDayIncludingEmptySlots.add(emptySlot);
                }
            }
            
        }
        return apptsForDayIncludingEmptySlots;
    }
    
    private Appointment createEmptyAppointmentSlotForPrintScheduleRequest(LocalDateTime start){
        Appointment appointment = new Appointment();
        appointment.setPatient(null);
        appointment.setStart(start);
        appointment.setDuration(Duration.between(start.toLocalTime(), 
                                                ViewController.LAST_APPOINTMENT_SLOT));
         //06/08/2022 08:49                                       
        //appointment.setStatus(Appointment.Status.UNBOOKED);
        return appointment;
    }
    
    private Appointment createEmptyAppointmentSlotForPrintScheduleRequest(LocalDateTime start, Duration duration){
        Appointment appointment = new Appointment();
        appointment.setPatient(null);
        appointment.setStart(start);
        appointment.setDuration(duration);
        //appointment.setStatus(Appointment.Status.UNBOOKED);
        //appointment.setEnd(appointment.getStart().plusMinutes(duration.toMinutes()));
        return appointment;
    }
    
    private Boolean getIsBookedStatusForPrintScheduleRequest(Appointment appointment){
        if (appointment.getPatient()==null) return false;
        if(!appointment.getPatient().getIsKeyDefined())return false;
        return true;
    }
}



