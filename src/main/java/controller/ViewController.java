/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
import model.Entity;
import model.Appointment;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;


/**
 *
 * @author colin
 * V02_VCSuppliesDataOnDemandToView
 */
public abstract class ViewController implements ActionListener, PropertyChangeListener{
    
    public enum ViewControllers {
        AppointmentRemindersViewController,
        AppointmentScheduleViewController,
        DesktopViewController,
        ImportProgressViewController,
        PatientNotificationViewController,
        PatientViewController,
    }
    
    public enum RequestedAppointmentState{ 
                                            REQUESTED_SLOT_STATE_UNDEFINED,
                                            REQUESTED_SLOT_STARTS_AFTER_PREVIOUS_SCHEDULED_SLOT,
                                            REQUESTED_SLOT_END_TIME_UPDATED_TO_LATER_TIME,
                                            APPOINTMENT_ADDED_TO_SCHEDULE,
                                            ERROR_ADDING_APPOINTMENT_TO_SCHEDULE,
                                            COLLISION,
                                            NO_COLLISION,
                                            SLOT_START_OK,
                                            UNDEFINED}
    
    public static enum AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent{
        APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED
        
    }
    
    public static enum AppointmentRemindersViewControllerActionEvent{
        APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CHANGE_NOTIFICATION,
        APPOINTMENT_SCHEDULE_VIEW_CLOSE_NOTIFICATION,
        VIEW_CLOSE_NOTIFICATION,
        VIEW_CONTROLLER_CLOSE_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION
    }
    
    public static enum AppointmentScheduleViewControllerActionEvent{
        /**
         * PRIMARY VIEW ACTION EVENTS
         */
        //create view request actions
        APPOINTMENT_REMINDERS_VIEW_REQUEST,
        APPOINTMENTS_CANCELLED_VIEW_REQUEST,
        APPOINTMENT_CREATE_VIEW_REQUEST,
        APPOINTMENT_UPDATE_VIEW_REQUEST,
        NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST,
        SURGERY_DAYS_EDITOR_VIEW_REQUEST,
        EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST,
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
        EMPTY_SLOTS_FROM_DAY_REQUEST,
        MODAL_VIEWER_ACTIVATED,
        SURGERY_DAYS_EDIT_REQUEST
    }
    
    public static enum AppointmentScheduleViewControllerPropertyChangeEvent{
        APPOINTMENT_RECEIVED,
        APPOINTMENTS_CANCELLED_RECEIVED,
        APPOINTMENT_FOR_DAY_RECEIVED,
        APPOINTMENTS_FOR_DAY_RECEIVED,
        APPOINTEE_REMINDER_COUNT_RECEIVED,
        APPOINTMENT_SCHEDULE_ERROR_RECEIVED,
        APPOINTMENT_SLOTS_FROM_DAY_RECEIVED,
        INITIALISE_SCHEDULE_DATE,
        NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED,//added 01/02/2023
        NON_SURGERY_DAY_EDIT_RECEIVED,
        REFRESH_DISPLAY_REQUEST_RECEIVED,
        SURGERY_DAYS_ASSIGNMENT_RECEIVED
        }
    
    public static enum DesktopViewControllerActionEvent{
        APPOINTMENT_VIEW_CONTROLLER_REQUEST,
        CLINIC_LOGO_VIEW_MODE_NOTIFICATION,
        COUNT_APPOINTMENT_TABLE_REQUEST,
        COUNT_PATIENT_TABLE_REQUEST,
        COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST,
        COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST,
        DELETE_DATA_FROM_PMS_DATABASE_REQUEST,
        DESKTOP_VIEW_MODE_NOTIFICATION,
        GET_APPOINTMENT_CSV_PATH_REQUEST,
        GET_PATIENT_CSV_PATH_REQUEST,
        GET_PMS_STORE_PATH_REQUEST,
        IMPORT_DATA_FROM_SOURCE,
        IMPORT_EXPORT_APPOINTMENT_DATA,
        IMPORT_EXPORT_APPOINTMENT_DATA_COMPLETED,
        IMPORT_EXPORT_MIGRATED_SURGERY_DAYS_ASSIGNMENT,
        IMPORT_EXPORT_PATIENT_DATA,
        IMPORT_EXPORT_PATIENT_DATA_COMPLETED,
        MODAL_VIEWER_ACTIVATED,
        MODAL_VIEWER_CLOSED,
        PATIENT_NOTIFICATION_VIEW_CONTROLLER_REQUEST,
        PATIENT_SELECTION_VIEW_CONTROLLER_REQUEST,
        PATIENT_VIEW_CONTROLLER_REQUEST,
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
        PATIENT_TABLE_COUNT_RECEIVED,
        PATIENT_NOTIFICATION_TABLE_COUNT_RECEIVED,
        SURGERY_DAYS_ASSIGNMENT_TABLE_COUNT_RECEIVED,
        MIGRATION_ACTION_COMPLETE
    }
    
    public static enum ImportProgressViewControllerActionEvent{
        IMPORT_EXPORT_START_REQUEST,
        IMPORT_EXPORT_PROGRESS_CLOSE_NOTIFICATION,
        READY_FOR_RECEIPT_OF_APPOINTMENT_PROGRESS,
        READY_FOR_RECEIPT_OF_PATIENT_PROGRESS
    }
    
    public static enum ImportProgressViewControllerPropertyChangeEvent{
        state,
        progress,
        PATIENT,
        APPOINTMENT,
        OPERATION_COMPLETED,
        PREPARE_FOR_RECEIPT_OF_APPOINTMENT_PROGRESS,
        PREPARE_FOR_RECEIPT_OF_PATIENT_PROGRESS
        }
    
    public static enum PatientNotificationViewControllerActionEvent{
        ACTION_PATIENT_NOTIFICATION_REQUEST,
        CREATE_PATIENT_NOTIFICATION_REQUEST,
        DELETE_PATIENT_NOTIFICATION_REQUEST,
        MODAL_VIEWER_ACTIVATED,
        MODAL_VIEWER_DEACTIVATED,
        PATIENT_NOTIFICATION_EDITOR_CLOSE_VIEW_REQUEST,
        //PATIENT_NOTIFICATION_EDITOR_DELETE_NOTIFICATION_REQUEST,
        PATIENT_NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST,
        PATIENT_NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST,
        PATIENT_NOTIFICATIONS_REQUEST,
        UNACTIONED_PATIENT_NOTIFICATIONS_REQUEST,
        UPDATE_PATIENT_NOTIFICATION_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSED_NOTIFICATION
    }
    
    public static enum PatientNotificationViewControllerPropertyChangeEvent{
        RECEIVED_PATIENT_NOTIFICATION,
        RECEIVED_PATIENT_NOTIFICATIONS,
        RECEIVED_UNACTIONED_NOTIFICATIONS,
    }
    
    public static enum PatientViewControllerActionEvent{
        APPOINTMENT_VIEW_CONTROLLER_REQUEST,
        MODAL_VIEWER_ACTIVATED,
        NULL_PATIENT_REQUEST,
        PATIENT_GUARDIAN_REQUEST,
        PATIENT_GUARDIANS_REQUEST,
        PATIENT_REQUEST,
        PATIENTS_REQUEST,
        PATIENT_SELECTION_VIEW_REQUEST,
        PATIENT_CREATE_REQUEST,
        PATIENT_VIEW_CLOSED,
        PATIENT_UPDATE_REQUEST,
        PATIENT_DELETE_REQUEST,
        PATIENT_RECOVER_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSE_NOTIFICATION      
    }
    
    public static enum PatientViewControllerPropertyChangeEvent{
        NULL_PATIENT_RECEIVED,
        PATIENT_GUARDIANS_RECEIVED,
        PATIENT_RECEIVED,
        PATIENTS_RECEIVED,
        PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED,
        PATIENT_VIEW_CHANGE_NOTIFICATION
        //PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION
    }

    public static enum PatientAppointmentContactListViewControllerActionEvent {
        PATIENT_APPOINTMENT_CONTACT_VIEW_CLOSED,
        PATIENT_APPOINTMENT_CONTACT_VIEW_REQUEST
    }

    public enum ViewMode {
        CREATE,
        Create,
        UPDATE,
        Update,
        NO_ACTION
    } 
    
    public static final LocalTime FIRST_APPOINTMENT_SLOT = LocalTime.of(9,0);
    public static final LocalTime LAST_APPOINTMENT_SLOT = LocalTime.of(17,0);
    
    public DateTimeFormatter dmyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public DateTimeFormatter dmyhhmmFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
    public DateTimeFormatter recallFormat = DateTimeFormatter.ofPattern("MMMM/yyyy");
    public DateTimeFormatter startTime24Hour = DateTimeFormatter.ofPattern("HH:mm");
    public DateTimeFormatter format24Hour = DateTimeFormatter.ofPattern("HH:mm");
    
    private Descriptor entityDescriptorFromView = null;
    private Descriptor controllerDescriptor = null;
    private ScheduleReport scheduleReport = null;
    
    public void firePropertyChangeEvent(
                                        String pcEventName,
                                        PropertyChangeListener pcListener,
                                        Object pcSource,
                                        Object oldPropertyValue,
                                        Object newPropertyValue
                                        ){
        PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);
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
    
    protected void centreViewOnDesktop(Frame desktopView, JInternalFrame view){
        Insets insets = desktopView.getInsets();
        Dimension deskTopViewDimension = desktopView.getSize();
        Dimension myViewDimension = view.getSize();
        Point point = new Point(
                (int)((deskTopViewDimension.getWidth()) - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2);
        
        view.setLocation(point);
        System.out.println("Location = " + point);
        System.out.println("Desktop size = " + desktopView.getSize());
        System.out.println("Internal frame size = " + view.getSize());
        System.out.println("2 x point x = " + desktopView.getWidth()+ "-" + view.getWidth());
        /*
        view.setLocation(new Point(
                (int)(deskTopViewDimension.getWidth() - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2));
        */
    }
    
    public static void displayErrorMessage(String message, String title, int messageType){
        JOptionPane.showMessageDialog(null,new ErrorMessagePanel(message),title,messageType);
    }
    
    public Descriptor getControllerDescriptor(){
        return controllerDescriptor;
    }
    
    public void setControllerDescriptor(Descriptor value){
        controllerDescriptor = value;
    }
    
    public void setEntityDescriptorFromView(Descriptor value){
        entityDescriptorFromView = value;
    }
    
    public void setNewEntityDescriptor(Descriptor value){
        controllerDescriptor = value;
    }
    
    public Descriptor getDescriptorFromView(){
        return entityDescriptorFromView;
    }

    protected Appointment doChangeAppointmentScheduleForDayRequest(ViewMode mode, Appointment rSlot) throws StoreException{
        Appointment result = null;
        
        LocalDate day = rSlot.getStart().toLocalDate();
        Appointment appointment = new Appointment(); 
        appointment.setStart(day.atStartOfDay());
        appointment.setScope(Entity.Scope.FOR_DAY);
        appointment.read();
        setScheduleReport(doAppointmentCollisionCheckOnScheduleChangeRequest(
                    rSlot, appointment.get(), mode));
        if (getScheduleReport().getState().equals(RequestedAppointmentState.COLLISION)){
            getControllerDescriptor().getControllerDescription().setError(scheduleReport.getError());
            result = null;
        }
        else{
            getControllerDescriptor().getControllerDescription().setError(null);
            switch (mode){//one or more appointments already exist so check the CREATE or UPGRADE make sense
                case CREATE:
                    rSlot.insert(); 
                    rSlot.setScope(Entity.Scope.SINGLE);
                    result = rSlot.read();
                    break;
                case UPDATE:
                    rSlot.update();
                    rSlot.setScope(Entity.Scope.SINGLE);
                    result = rSlot.read();
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
        getScheduleReport().setState(RequestedAppointmentState.UNDEFINED);
        Iterator<Appointment> appointmentsForDay = appointments.iterator();
        while (appointmentsForDay.hasNext()){
            Appointment nextScheduledSlot = appointmentsForDay.next();
            switch(mode){
                case CREATE:
                case NO_ACTION:
                    switch(scheduleReport.getState()){
                        
                    //2/12/2022
                    case SLOT_START_OK:
                            /**
                             * In CREATE new appointment mode checks if requested slot overlaps the next scheduled slot 
                             */
                            if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime())){
                                scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                            }
                            else{
                                scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                scheduleReport.setError(
                                    "The new appointment for " + requestedSlot.getAppointeeName()
                                        + " overwrites existing appointment for " 
                                        + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
                            }
                            break;
                        case UNDEFINED:
                            if (requestedSlot.getSlotStartTime().isBefore(nextScheduledSlot.getSlotStartTime())){
                                scheduleReport.setState(RequestedAppointmentState.SLOT_START_OK);
                                if (!requestedSlot.getSlotEndTime().isAfter(nextScheduledSlot.getSlotStartTime()))
                                    scheduleReport.setState(RequestedAppointmentState.NO_COLLISION);
                                else scheduleReport.setState(RequestedAppointmentState.COLLISION);
                            }
                            else if (requestedSlot.getSlotStartTime().isEqual(nextScheduledSlot.getSlotEndTime())){
                                scheduleReport.setState(RequestedAppointmentState.SLOT_START_OK);
                            }
                            else if (!requestedSlot.getSlotStartTime().isAfter(nextScheduledSlot.getSlotEndTime())){
                                scheduleReport.setState(RequestedAppointmentState.COLLISION);
                                scheduleReport.setError(
                                    "The new appointment for " + requestedSlot.getAppointeeName()
                                        + " overwrites existing appointment for " 
                                        + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
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
                                scheduleReport.setError(
                                    "The new appointment for " + requestedSlot.getAppointeeName()
                                        + " overwrites existing appointment for " 
                                        + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
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
                                    scheduleReport.setError(
                                            "The updated appointment for " + requestedSlot.getAppointeeName()
                                                + " overwrites existing appointment for " 
                                                + nextScheduledSlot.getAppointeeNamePlusSlotStartTime());
                                }
                            }
                            else{
                                if (requestedSlot.getSlotStartTime().isBefore(nextScheduledSlot.getSlotEndTime())){
                                    if (!requestedSlot.getPatient().equals(nextScheduledSlot.getPatient())){
                                        scheduleReport.setState(RequestedAppointmentState.COLLISION); 
                                        scheduleReport.setError(
                                                "The updated appointment for " + requestedSlot.getAppointeeName()
                                                    + " overwrites existing appointment for " 
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
}
