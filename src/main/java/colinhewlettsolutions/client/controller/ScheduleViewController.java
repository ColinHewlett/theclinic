/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.controller.Descriptor.ControllerDescription;
import colinhewlettsolutions.client.controller.Descriptor.ViewDescription;
import colinhewlettsolutions.client.model.non_entity.Slot;
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import colinhewlettsolutions.client.model.entity.Entity.Scope;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.ClinicalNote;
import colinhewlettsolutions.client.model.entity.SurgeryDaysAssignment;
import colinhewlettsolutions.client.model.non_entity.Credential;
import colinhewlettsolutions.client.model.repository.StoreException;//01/03/2023
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import colinhewlettsolutions.client.model.entity.AppointmentTreatment;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.entity.Treatment;
import colinhewlettsolutions.client.model.entity.User;
import colinhewlettsolutions.client.model.entity.UserSettings;
import colinhewlettsolutions.client.model.non_entity.TreatmentWithState;
import static colinhewlettsolutions.client.view.View.Viewer.USER_SCHEDULE_DIARY_SETTINGS_VIEW;
import colinhewlettsolutions.client.view.views.non_modal_views.ScheduleDiaryView;
import colinhewlettsolutions.client.view.views.non_modal_views.ScheduleListView;

/**
 *
 * @author colin
 */
public class ScheduleViewController extends ViewController{
    
    public enum Actions{
        APPOINTMENT_CANCEL_REQUEST,
        APPOINTMENTS_CANCELLED_VIEW_REQUEST,
        APPOINTMENT_CREATE_VIEW_REQUEST,
        APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST,
        APPOINTMENTS_FOR_DAY_REQUEST,
        APPOINTMENT_REMINDED_STATUS_UPDATE_REQUEST,
        APPOINTMENT_UPDATE_VIEW_REQUEST,
        BOOKABLE_SLOT_SCANNER_VIEW_REQUEST,
        CLINICAL_NOTE_CREATE_REQUEST,
        CLINICAL_NOTE_DELETE_REQUEST,
        CLINICAL_NOTE_FOR_APPOINTMENT_REQUEST,
        CLINICAL_NOTE_VIEW_REQUEST,
        CLINICAL_NOTE_UPDATE_REQUEST,
        CREATE_APPOINTMENT_REQUEST,
        EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST,
        FIRST_APPOINTMENT_START_TIME_REQUEST,
        LAST_APPOINTMENT_END_TIME_REQUEST,
        NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST,
        PATIENT_VIEW_REQUEST,
        PRINT_SCHEDULE_REQUEST,
        SCHEDULE_EDITOR_DELETE_EMERGENCY_APPOINTMENT_REQUEST,
        SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST,
        SWITCH_VIEW_REQUEST,
        SURGERY_DAYS_EDITOR_VIEW_REQUEST,
        TO_DO_LIST_VIEW_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CANCEL_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST,
        UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST,
        UNBOOKABLE_SLOT_SCANNER_VIEW_REQUEST,
        UPDATE_APPOINTMENT_REQUEST,
        USER_SCHEDULE_DIARY_SETTINGS_EDITOR_VIEW_REQUEST,
        USER_SCHEDULE_DIARY_SETTINGS_REQUEST,
        USER_SCHEDULE_LIST_SETTINGS_EDITOR_VIEW_REQUEST,
        USER_SCHEDULE_LIST_SETTINGS_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSE_NOTIFICATION
                
    }

    private enum RequestedAppointmentState{ 
                                            REQUESTED_SLOT_STATE_UNDEFINED,
                                            REQUESTED_SLOT_STARTS_AFTER_PREVIOUS_SCHEDULED_SLOT,
                                            REQUESTED_SLOT_END_TIME_UPDATED_TO_LATER_TIME,
                                            APPOINTMENT_ADDED_TO_SCHEDULE,
                                            ERROR_ADDING_APPOINTMENT_TO_SCHEDULE,
                                            COLLISION,
                                            NO_COLLISION,
                                            SLOT_START_OK,
                                            UNDEFINED}
    private View secondaryView = null;
    private PropertyChangeSupport pcSupport = null;
    private PropertyChangeEvent pcEvent = null;
    //private LocalDate appointmentScheduleDay = null;
    
    private LocalDate getScheduleDay(){
        return (LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
    }
    
    
    private void setScheduleDay(LocalDate day){
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, day);
    }
    
    /**
     * 
     * @param controller
     * @param desktopView
     * @param ed
     * @throws StoreException 
     */
    public ScheduleViewController(DesktopViewController controller, DesktopView desktopView)throws StoreException{
        setMyController(controller);
        setDesktopView(desktopView);
        pcSupport = new PropertyChangeSupport(this);
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.ScheduleViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTMENT_FOR_DAY_RECEIVED:/*{
                Appointment appointment = ((Descriptor)e.getNewValue()).getControllerDescription().getAppointment();
                if (getScheduleDay().isEqual(appointment.getStart().toLocalDate())){
                    appointment.setScope(Scope.FOR_DAY);
                    try{
                        appointment.read();
                        doAppointmentForDayRequest(getScheduleDay());
                    }catch(StoreException ex){
                        displayErrorMessage(ex.getMessage() + "\nRaised in propertyChange()",
                                "Appointment schedule view controller error",
                                JOptionPane.WARNING_MESSAGE);
                    } 
                }
            }*/
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        DesktopViewController dvc = (DesktopViewController)getMyController();
        
        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerAction(e);
        }
        else {
            
            View the_view = (View)e.getSource();
            

            switch(the_view.getMyViewType()){
                case SCHEDULE_LIST_VIEW:
                case SCHEDULE_DIARY_VIEW:     
                    doPrimaryViewActionRequest(e);
                    break;
                default:
                    doSecondaryViewActionRequest(e);
                    break;
            }
        }
        
    }
    
    private void doUserScheduleDiaryColorSettingsViewRequest(){
        setModalView((ModalView)new View().make(
                    View.Viewer.USER_SCHEDULE_DIARY_SETTINGS_VIEW,
                    this, 
                    this.getDesktopView()).getModalView()); 
        /**
         * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
         * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
         * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
         */
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
        
        
       /* ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
                ViewController.DesktopViewControllerActionEvent.
                        USER_SETTINGS_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);*/
    }
    
    private void doUserScheduleListColorSettingsViewRequest(){
        setModalView((ModalView)new View().make(
                    View.Viewer.USER_SCHEDULE_LIST_SETTINGS_VIEW,
                    this, 
                    this.getDesktopView()).getModalView()); 
        /**
         * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
         * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
         * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
         */
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
        
        
        /*ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
                ViewController.DesktopViewControllerActionEvent.
                        USER_SETTINGS_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);*/
    }

    /**
     * handles a cancel appoinment request rais3ed in the primary view, AppointmentScheduleView
     
     * 
     */
    private void doAppointmentCancelRequest(){ 
        Appointment theCancelledAppointment = null;
        Appointment appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        //?if (getDescriptorFromView().getViewDescription().getAppointment().getPatient().getIsKeyDefined()){
        if (appointment.getPatient().getIsKeyDefined()){    
            try{
                //?Appointment appointment = getDescriptorFromView().getViewDescription().getAppointment();
                appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                Patient patient = appointment.getPatient();
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                LocalDate day = appointment.getStart().toLocalDate();
                if (patient.toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())) {
                    appointment.setScope(Scope.SINGLE);
                    appointment.delete();
                }
                else appointment.cancel();
                theCancelledAppointment = appointment;
                if (day.equals(getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                    appointment = new Appointment();
                    appointment.setStart(day.atStartOfDay());
                    appointment.setScope(Scope.FOR_DAY);
                    appointment.read();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENTS, appointment.get());
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, day);
                    //doAppointeeReminderCount(appointment.get());
                    getUpdatedAppointmentSlotsForDay(appointment);  
                    getDescriptor().getControllerDescription()
                            .setProperty(SystemDefinition.Properties.APPOINTMENT, theCancelledAppointment);
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                    firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                    );
                }
             
            }
            catch (StoreException ex){
               String message = ex.getMessage();
               displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
            }
       }
    }
    
    private void doAppointmentsCancelledViewRequest(){
        Appointment appointment = new Appointment();
        appointment.setScope(Scope.CANCELLED);
        try{
            appointment.read();
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_CACELLATIONS, appointment.get());
            setModalView((ModalView)new View().make(
                    View.Viewer.APPOINTMENTS_CANCELLED_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
            
        }catch (StoreException ex){
            String msg = ex.getMessage() +"\n"
                    + "Raised in Repository.doAppointmentsCancelledViewRequest()";
            displayErrorMessage(msg,"Appointment view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    private void doUnbookableSlotScannerViewRequest(){
        doUnbookableSlotsFromDayRequest();
        setModalView((ModalView)new View().make(View.Viewer.UNBOOKABLE_SLOT_SCANNER_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
    }
    
    private void doBookableSlotScannerViewRequest(ActionEvent e){
        setModalView((ModalView)new View().make(View.Viewer.BOOKABLE_SLOT_SCANNER_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
    }
    
    private void doClinicalNoteViewRequest(){
        getDescriptor().getControllerDescription().
                setProperty(SystemDefinition.Properties.APPOINTMENT,
                        (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT));
        setModalView((ModalView)new View().make(View.Viewer.CLINICAL_NOTE_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
    }
    
    private void doAppointmentCreateViewRequest(){
        /**
         * on receipt of APPOINTMENT_CREATE_VIEW_REQUEST
         * -- initialises NewEntityDescriptor with the collection of all patients on the system
         * -- launches the APPOINTMENT_CREATOR_EDITOR_VIEW for the selected appointment for update
         */
        Patient patient = null;
        ArrayList<Patient> patients = null;
        try{
            /* get a list of all patients on the system */
            patient = new Patient();
            patient.setScope(Scope.ALL);
            patient.read();
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENTS, patient.get());
            /* respond accordingly to the view's mode */
            switch (getDescriptor().getViewDescription().getViewMode()){
                case SLOT_SELECTED:
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, 
                            (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT));
                    break;
                case SLOT_UNSELECTED:
                    Appointment a = new Appointment();
                    a.setStart(LocalDateTime.of(getScheduleDay(),LocalTime.of(9,0)));
                    a.setDuration(Duration.ZERO);
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, a);
                    break;
            }
            getDescriptor().getControllerDescription().setViewMode(ViewMode.CREATE);
            if (((Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).getPatient()==null){
                setModalView((ModalView)new View().make(View.Viewer.SCHEDULE_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
                /**
                 * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
                 * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
                 * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
                 */
                doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
            }else{
                if (((Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).getPatient().toString()
                        .equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())) 
                    displayErrorMessage("Cannot create an appointment in an unbookable slot", 
                        "Schedule view controller error",JOptionPane.WARNING_MESSAGE);
                else displayErrorMessage("Cannot create an appointment in the selected slot "
                        + "because it is already assigned", 
                        "Schedule view controller error",JOptionPane.WARNING_MESSAGE);
            }
            
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doClinicalNoteViewControllerRequest(){
        /*
        getDescriptor().getControllerDescription().setAppointment(
                getDescriptor().getViewDescription().getAppointment());
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);*/
    }
    
    private void MODAL_VIEWER_CLOSED_NOTIFICATION(){
        getDescriptor().getControllerDescription().setViewMode(null);
        View.setViewer(View.Viewer.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW);
        switch (getDescriptor().getViewDescription().getViewMode()){
            case SLOT_SELECTED:{
                if(((Appointment)getDescriptor().getViewDescription()
                        .getProperty(SystemDefinition.Properties.APPOINTMENT)).getPatient() == null){//check if this slot is occupied by a patient
                    //NO must be unbooked
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT,
                            (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT));
                    getDescriptor().getControllerDescription().
                            setViewMode(ViewController.ViewMode.CREATE);     
                }
                else if (((Appointment)getDescriptor().getViewDescription().
                        getProperty(SystemDefinition.Properties.APPOINTMENT)).getIsUnbookableSlot()){//is this an UNBOOKABLE slot
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT,
                        (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT));
                    getDescriptor().getControllerDescription().
                            setViewMode(ViewController.ViewMode.UPDATE);
                }
                else {//YES slot already occupied by a patient
                    displayErrorMessage("The selected slot is already occupied "
                            + "and therefor cannot be marked as unbookable",
                        "Appointment schedule view controller error", JOptionPane.WARNING_MESSAGE);
                    getDescriptor().getControllerDescription().
                            setViewMode(null);
                }
                break; 
            }
            case SLOT_UNSELECTED:
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.APPOINTMENT, new Appointment());
                getDescriptor().getControllerDescription().
                        setViewMode(ViewController.ViewMode.CREATE);
                break;
            case UPDATE: // indicates schedule view UPDATE button clicked se;ecvted unbookable slot
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT,
                    (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT));
                getDescriptor().getControllerDescription().
                        setViewMode(ViewController.ViewMode.UPDATE);
                break;
        }

        //this.view2 = View.factory(this, getDescriptor(), this.desktopView);
        setModalView((ModalView)new View().make(
                    View.Viewer.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        /**
         * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
         * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
         * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
         */
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
    }
    
    private void doDeleteEmergencyAppointmentRequest(){
        Appointment appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        if (appointment.getIsEmergency()){
            appointment.setScope(Scope.EMERGENCY);
            try{
                appointment.delete();
                doAppointmentForDayRequest(appointment.getStart().toLocalDate());
                //getDescriptor().getControllerDescription().setPatient(result.getPatient());
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                );
            }catch (StoreException ex){
                String message = ex.getMessage() +"\n"
                        + "StoreException handled in doDeleteEmergencyAppointment()";
                displayErrorMessage(message,
                        "Schedule view controller error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void doMakeEmergencyAppointmentViewRequest(){
        /* get a list of all patients on the system */
        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        try{
            patient.read();
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENTS, patient.get());
            Appointment appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
            getDescriptor().getControllerDescription().setViewMode(ViewMode.EMERGENCY);
            setModalView((ModalView)new View().make(View.Viewer.SCHEDULE_EDITOR_VIEW,
                this, 
                this.getDesktopView()).getModalView());
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doAppointmentUpdateViewRequest(){
        /**
         * on receipt of APPOINTMENT_UPDATE_VIEW_REQUEST
         * -- on entry assumes EntityDescriptorFromView has already been initialised from the view's entity descriptor
         * -- launches the APPOINTMENT_CREATOR_EDITOR_VIEW for the selected appointment for update
         */
        //07/08/2022 08:58
        //Patient.Collection patients = null;
        ArrayList<Patient> patients = null;
        Patient patient = null;
        //?if (getDescriptorFromView().getViewDescription().getAppointment().getIsKeyDefined()){
        if (((Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).getIsKeyDefined()){
            try{

                //?Appointment appointment = getDescriptorFromView().getViewDescription().getAppointment();
                Appointment appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                patient = new Patient();
                patient.setScope(Scope.ALL);
                patient.read();
                //initialiseNewEntityDescriptor();
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENTS, patient.get());
                getDescriptor().getControllerDescription().setViewMode(ViewMode.UPDATE);
                setModalView((ModalView)new View().make(View.Viewer.SCHEDULE_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
                /**
                 * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
                 * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
                 * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
                 */
                /*
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
                this.getMyController().actionPerformed(actionEvent);*/
            }
            catch (StoreException ex){
                String message = ex.getMessage();
                displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
            } 
        }
    }
    
    private void doAppointmentsViewClosedRequest(){
        /**
         * APPOINTMENTS_VIEW_CLOSED
         */
        doActionEventFor(DesktopViewController.Actions.VIEW_CLOSED_NOTIFICATION); 
    }
    
    /**
     * method fetches appointments from persistent store with the specified date
     * -- the appointee reminder summary for these appointments is initialised
     * -- and a viewable list of appointments prepared which includes any empty slots
     * @param day, LocalDate
     */
    private void doAppointmentForDayRequest(LocalDate day){
        Appointment lastAppointment = null;
        LocalDateTime earlyStart = (LocalDateTime)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME);
        LocalDateTime lateEnd = (LocalDateTime)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME);
        if (earlyStart!=null){
            if (!day.equals(earlyStart.toLocalDate()))
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME, null);
        }
        if (lateEnd!=null){
            if (!day.equals(lateEnd.toLocalDate()))
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME,null);
        }
        
        Appointment appointment = new Appointment();
        appointment.setStart(day.atStartOfDay());
        
        appointment.setScope(Entity.Scope.FOR_DAY_AND_NON_EMERGENCY_APPOINTMENT);
        //appointment.setScope(Scope.FOR_DAY);
        try{
            appointment.read();
            if (!appointment.get().isEmpty()){
                if (appointment.get().get(0).getStart().
                        isBefore(day.atTime(ViewController.FIRST_APPOINTMENT_SLOT))){
                    /*getDescriptor().getControllerDescription().
                            setAppointmentEarlyStart(appointment.get().get(0).getStart());*/
                    if (earlyStart!=null){
                        if (!earlyStart.isBefore(appointment.get().get(0).getStart()))
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME, appointment.get().get(0).getStart());
                            /*getDescriptor().getControllerDescription().
                                    setEarlyBookingStartTime(appointment.get().get(0).getStart());*/
                    }else{
                        getDescriptor().getControllerDescription().
                                    setProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME, appointment.get().get(0).getStart());
                    }
                }else getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME,null);
            }
            if (getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME)!=null){
            //if (getDescriptor().getControllerDescription().getLateBookingEndTime()!=null){
                if (!appointment.get().isEmpty()){ 
                    lastAppointment = appointment.get().get(appointment.get().size()-1);
                    if(!lastAppointment.getEnd().isBefore((LocalDateTime)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME))){
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME, lastAppointment.getEnd());
                    }
                }
            }
            
            /**
             * generate appointment note from treatments selected
             */
            doFormatAppointmentTreatmentNote(appointment.get());
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENTS, appointment.get());
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, day);
            doAppointeeReminderCount(appointment.get());
            getUpdatedAppointmentSlotsForDay(appointment);
            /**
             * 25/06/2024 07:42 update
             * -- separately determines if any emergency appointments on this day
             * -- yes; recreate the schedule list with the emergency appointment(s) at the top
             */

            appointment.setScope(Scope.FOR_DAY_AND_EMERGENCY_APPOINTMENT);
            appointment.setStart(day.atTime(ViewController.FIRST_APPOINTMENT_SLOT));
            appointment = appointment.read();
            if (!appointment.get().isEmpty()){
                ArrayList<Appointment> scheduleListForTheDay = new ArrayList<>();
                for (Appointment a : appointment.get()){
                    a.setNotes(SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT.mark());
                    scheduleListForTheDay.add(a);
                }
                ArrayList<Appointment> test = (ArrayList<Appointment>)getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT);
                for (Appointment a: test){ 
                    scheduleListForTheDay.add(a);
                }
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT, scheduleListForTheDay);
            }
            
            doSendViewNewSchedule();
        }
        catch (StoreException ex){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String message = ex.getMessage() 
                    + "\nRaised in doAppointmentForDayRequest(" + day.format(formatter) + ")";
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /*
    private void doModalViewerActivated(){
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString());
        this.myController.actionPerformed(actionEvent);
    }
    */
    private void doNonSurgeryDayScheduleViewRequest(){
        try{
            getDescriptor().getControllerDescription().
                    setProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT, new SurgeryDaysAssignment().read().get());
            setModalView((ModalView)new View().make(
                    View.Viewer.NON_SURGERY_DAY_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView()); 
            /**
             * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
             * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
             * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
             */
            doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION); 
        }
        catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doSurgeryDayScheduleViewRequest(){
        try{
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, getScheduleDay());
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, new SurgeryDaysAssignment().read().get());
            setModalView((ModalView)new View().make(
                    View.Viewer.SURGERY_DAY_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView()); 
            /**
             * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
             * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
             * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
             */
            doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION); 
        }
        catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doEmptySlotScannerDialogRequest(ActionEvent e){
        /**
         * EMPTY_SLOT_SCANNER_DIALOG_REQUEST constructs an EmptySlotScanEditorModalViewer
         */
        
        this.setModalView((ModalView)new View().make(View.Viewer.APPOINTMENT_EMPTY_SLOT_SCAN_CONFIGURATION_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());

        /**
         * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
         * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
         * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
         */
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
    }

    /**
     * handles the following action events
     * -- APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REQUEST
     * ---- raised when the primary view requests contact details of the scheduled appointees
     * ---- fires an APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION to DesktopVC
     * -- APPOINTMENT_SCHEDULE_CANCEL_REQUEST
     * ---- raised when primary key requests the deletion of an appointment from the schedule
     * ---- handles request and hen fires an APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION property change event to the DesktopVC
     * -- APPOINTMENT_CREATE_VIEW_REQUEST
     * ---- raised when primary view requests to create a new appointment
     * ---- handles request by launching a secondary view
     * -- APPOINTMENT_UPDATE_VIEW_REQUEST
     * ---- raised when primary view requests to update a selected appointment
     * ---- handles request by launching a secondary view
     * -- APPOINTMENT_SCHEDULE_VIEW_CLOSE_REQUEST
     * -- raised when the primary view attempts to close the primary view
     * ---- handles request and ires event to notify the DesktopVC whats happening
     * -- APPOINTMENTS_FOR_DAY_REQUEST
     * ---- raised when primary view requests a refresh of the scheduled appointments (for example schedule date has been changed)
     * ---- handles request and fires an APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION property change event to the DesktopVC
     * -- NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST
     * ---- raised when primary view requests to select a 'non surgery day, for the appointment schedule
     * -- SURGERY_DAYS_EDITOR_VIEW_REQUEST
     * ---- raised when primary view requests to change/check the current surgery days assignment
     * -- EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST
     * ---- raised when primary view requests to scan ahead for available appointment slots
     * @param e, ActionEvent received 
     */
    private void doPrimaryViewActionRequest(ActionEvent e){ 
        User user = null;
        UserSettings userSettings = null;
        ActionEvent actionEvent = null;
        Appointment appointment = null;
        Appointment result = null;
        Appointment changedSlotRequest = 
                (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        ScheduleViewController.Actions actionCommand = ScheduleViewController.Actions.valueOf(e.getActionCommand());
        
        switch (actionCommand){
            case APPOINTMENT_CANCEL_REQUEST:
                appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                if(!appointment.getIsEmergency()){
                    doAppointmentCancelRequest();
                
                    mergeScheduleSlotsIfPossible(getScheduleDay());
                    
                }else{
                    appointment.setScope(Scope.EMERGENCY);
                    
                    try{
                        appointment.delete();
                    }catch (StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "StoreException handled in "
                                + "ScheduleViewController::doPrimaryViewActionPerformed"
                                + "(" + actionCommand.toString() + ")";
                    }
                }
                doAppointmentForDayRequest(getScheduleDay());
                doSendViewNewSchedule();
                
                if (getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY)!=null){
                    firePropertyChangeEvent(
                           ViewController.ScheduleViewControllerPropertyChangeEvent.
                                   NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                           this.getView(),//event target/listener
                           this,//event sender
                           null,
                           getDescriptor()//event related data        
                    );
                }
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                );
                break;
            case APPOINTMENTS_CANCELLED_VIEW_REQUEST:
                doAppointmentsCancelledViewRequest();
                break;
            case APPOINTMENT_CREATE_VIEW_REQUEST:
                getDescriptor().getControllerDescription().
                    setViewMode(ViewController.ViewMode.CREATE);
                doAppointmentCreateViewRequest();
                break;
            case APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST:
                doOpenTreatmentView();
                break;
            case APPOINTMENTS_FOR_DAY_REQUEST:
                setScheduleDay((LocalDate)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));

                doAppointmentForDayRequest((LocalDate)getDescriptor().
                        getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                );
                /**
                 * check if another schedule view is open on the same day
                 * -- if it is the Desktop VC will send a request to the view to close 
                 * this ensures only one schedule view will be displayed on the desktop for a given day
                 */
                doActionEventFor(DesktopViewController.Actions.CLOSE_SCHEDULE_VIEW_WITH_SAME_DATE_REQUEST);
                break;
            case APPOINTMENT_REMINDED_STATUS_UPDATE_REQUEST:
                appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                try{
                    appointment.update();
                    /*05/04/2024 19:31 next line required to refresh schedule view*/
                    doAppointmentForDayRequest(appointment.getStart().toLocalDate());
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage(), 
                            "Schedule view controller",JOptionPane.WARNING_MESSAGE);
                }
                break;
            case APPOINTMENT_UPDATE_VIEW_REQUEST:
                
                getDescriptor().getControllerDescription().
                    setViewMode(ViewController.ViewMode.UPDATE);
                doAppointmentUpdateViewRequest();
                break;
            case BOOKABLE_SLOT_SCANNER_VIEW_REQUEST:
                doBookableSlotScannerViewRequest(e);
                break;
            case CLINICAL_NOTE_VIEW_REQUEST:
                doClinicalNoteViewRequest();
                break;
            case CREATE_APPOINTMENT_REQUEST:
                LocalDate day = changedSlotRequest.getStart().toLocalDate();
                setScheduleReport(new ScheduleReport());
                result = doAppointmentCreateRequest(e, (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT));
                if (result!=null){
                    mergeScheduleSlotsIfPossible(day);
                    doAppointmentForDayRequest(day);
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, result.getPatient());
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else {
                    sendErrorToScheduleEditorView();
                    doAppointmentForDayRequest(day);
                }
                break;
            case EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST: 
                doEmptySlotScannerDialogRequest(e);
                break;
            case FIRST_APPOINTMENT_START_TIME_REQUEST:
                LocalDateTime requestedEarlyStart = (LocalDateTime)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME);
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME, requestedEarlyStart);
                /**
                 * extra code to implement early start time
                 */
                doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                break;
            case LAST_APPOINTMENT_END_TIME_REQUEST:
                LocalDateTime requestedLateEnd = (LocalDateTime)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME);
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME, requestedLateEnd);
                doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                break;
            case NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST:
                doNonSurgeryDayScheduleViewRequest();
                break;
            case PATIENT_VIEW_REQUEST:
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT,
                        (Patient)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT));
                doActionEventFor(DesktopViewController.Actions.PATIENT_VIEW_CONTROLLER_REQUEST);
                break;
            case PRINT_SCHEDULE_REQUEST:
                doPrintAppointmentScheduleForDay(
                        (LocalDate)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                break;
            case TO_DO_LIST_VIEW_REQUEST:
                doActionEventFor(DesktopViewController.Actions.TO_DO_VIEW_CONTROLLER_REQUEST);
                break;
            case SCHEDULE_EDITOR_DELETE_EMERGENCY_APPOINTMENT_REQUEST:
                doDeleteEmergencyAppointmentRequest();
                break;
            case SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST:
                doMakeEmergencyAppointmentViewRequest();
                break;
            case SURGERY_DAYS_EDITOR_VIEW_REQUEST:
                doSurgeryDayScheduleViewRequest();
                break;
            case SWITCH_VIEW_REQUEST:
                doSwitchView();
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST:
                doUnbookableAppointmentSlotEditorViewRequest();
                break;
            case UNBOOKABLE_SLOT_SCANNER_VIEW_REQUEST:
                doUnbookableSlotScannerViewRequest();
                break;
            case UPDATE_APPOINTMENT_REQUEST:
                //day = changedSlotRequest.getStart().toLocalDate();
                setScheduleReport(new ScheduleReport());
                result = doAppointmentUpdateRequest(e,changedSlotRequest);
                if(result!=null){
                    //mergeScheduleSlotsIfPossible(day);
                    //doAppointmentForDayRequest(day);
                    mergeScheduleSlotsIfPossible((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, result.getPatient());
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else {
                    sendErrorToScheduleEditorView();
                    //doAppointmentForDayRequest(day);
                    doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                }
                break;
            case USER_SCHEDULE_DIARY_SETTINGS_REQUEST:
                doUserSettingsRequest(Entity.Scope.USER_SCHEDULE_DIARY_SETTINGS);
                break;
            case USER_SCHEDULE_DIARY_SETTINGS_EDITOR_VIEW_REQUEST:
                /**
                 * this case never arises because
                 * -- user selects a schedule diary view within the schedule list view
                 * -- and this request is handled by the schedule view controller's doSwitchView() method
                 */
                //doUserScheduleDiaryColorSettingsViewRequest();
                break;
            case USER_SCHEDULE_LIST_SETTINGS_REQUEST:
                doUserSettingsRequest(Entity.Scope.USER_SCHEDULE_LIST_SETTINGS);
                break;
            case USER_SCHEDULE_LIST_SETTINGS_EDITOR_VIEW_REQUEST:
                doUserScheduleListColorSettingsViewRequest();
                break;
            case VIEW_ACTIVATED_NOTIFICATION:
                doActionEventFor(DesktopViewController.Actions.VIEW_CONTROLLER_ACTIVATED_NOTIFICATION);
                 break;
            case VIEW_CHANGED_NOTIFICATION:
                doActionEventFor(DesktopViewController.Actions.VIEW_CONTROLLER_CHANGED_NOTIFICATION);
                 break;
            case VIEW_CLOSE_NOTIFICATION:
                doActionEventFor(DesktopViewController.Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION);
                break;           
            
                /*
            case CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST:
                doClinicalNoteViewControllerRequest();
                break;*/
            /*
            case MODAL_VIEWER_ACTIVATED://notification from view uts shutting down
                doModalViewerActivated();
                break;
            */        
        }
    }
    
    private void doUnbookableAppointmentSlotEditorViewRequest(){
        Appointment appointment = null;
        getDescriptor().getControllerDescription().setViewMode(null);
        View.setViewer(View.Viewer.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW);
        switch (getDescriptor().getViewDescription().getViewMode()){
            case SLOT_SELECTED:{
                /*if(getDescriptor().getViewDescription().
                        getAppointment().getPatient() == null){//check if this slot is occupied by a patient*/
                appointment = (Appointment)getDescriptor().
                        getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                if(appointment.getPatient() == null){//check if this slot is occupied by a patient
                    //NO must be unbooked
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.VIEW_MODE,ViewController.ViewMode.CREATE);
                    /*
                    getDescriptor().getControllerDescription().setAppointment(
                            getDescriptor().getViewDescription().getAppointment());
                    getDescriptor().getControllerDescription().
                            setViewMode(ViewController.ViewMode.CREATE);   */  
                }
                else if (appointment.getIsUnbookableSlot()){//is this an UNBOOKABLE slot
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.VIEW_MODE,ViewController.ViewMode.UPDATE);
                    
                            /*
                        getAppointment().getIsUnbookableSlot()){//is this an UNBOOKABLE slot
                    getDescriptor().getControllerDescription().setAppointment(
                        getDescriptor().getViewDescription().getAppointment());
                    getDescriptor().getControllerDescription().
                            setViewMode(ViewController.ViewMode.UPDATE);*/
                }
                else {//YES slot already occupied by a patient
                    displayErrorMessage("The selected slot is already occupied "
                            + "and therefor cannot be marked as unbookable",
                        "Appointment schedule view controller error", JOptionPane.WARNING_MESSAGE);
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE, null);
                    /*getDescriptor().getControllerDescription().
                            setViewMode(null);*/
                }
                break; 
            }
            case SLOT_UNSELECTED:
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.APPOINTMENT, new Appointment());
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.VIEW_MODE, ViewController.ViewMode.CREATE);
                /*
                getDescriptor().getControllerDescription().
                        setAppointment(new Appointment());
                getDescriptor().getControllerDescription().
                        setViewMode(ViewController.ViewMode.CREATE);*/
                break;
            case UPDATE: // indicates schedule view UPDATE button clicked se;ecvted unbookable slot
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.APPOINTMENT,appointment);
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.VIEW_MODE, ViewController.ViewMode.UPDATE);
                /*getDescriptor().getControllerDescription().setAppointment(
                    getDescriptor().getViewDescription().getAppointment());
                getDescriptor().getControllerDescription().
                        setViewMode(ViewController.ViewMode.UPDATE);*/
                break;
        }

        //this.view2 = View.factory(this, getDescriptor(), this.desktopView);
        setModalView((ModalView)new View().make(
                    View.Viewer.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        /**
         * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
         * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
         * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
         */
        ActionEvent actionEvent = new ActionEvent(
               this,ActionEvent.ACTION_PERFORMED,
               ViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doActionEventFor(DesktopViewController.Actions action){
        ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                action.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doUserSettingsRequest(Entity.Scope scope){
        User user = null;
        UserSettings userSettings = null;
        Credential credential = (Credential)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL);
        if (credential != null){
            user = new User(credential.getUsername());
            if (!user.getUsername().equals("guest")){
                try{
                    userSettings = new UserSettings(user);
                    userSettings.setScope(scope);
                    userSettings = userSettings.read();
                    ControllerDescription cd = getDescriptor().getControllerDescription();
                    for(Map.Entry<SystemDefinition.Properties, Object> entry : userSettings.getSettings().entrySet()){
                        getDescriptor().getControllerDescription().setProperty(entry.getKey(), entry.getValue());
                    }

                }catch(StoreException ex){
                    String message = "StoreException handled ScheduleViewController::dsoUserSettingsRequest( " 
                            + scope.toString() + " ) method";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
            }else /** guest user has default settings
                   *  -- on entry these are in DesktopViewController's descriptor
                   *  -- method nulls values of schedule diary settings
                   *  -- and initialises these from the DesktopViewController's Descriptor object
                   */{
                userSettings = new UserSettings();
                userSettings.setScope(scope);
                userSettings.setNullSettings();
                for(Map.Entry<SystemDefinition.Properties, Object> entry : userSettings.getSettings().entrySet()){
                        Object object = this.getMyController().getDescriptor().getControllerDescription().getProperty(entry.getKey());
                        getDescriptor().getControllerDescription().setProperty(entry.getKey(), object);
                }
            }
        }else /** user undefined so initialise with default values (as if 'guest' user*/  {
            userSettings = new UserSettings();
            userSettings.setScope(scope);
            userSettings.setNullSettings();
            for(Map.Entry<SystemDefinition.Properties, Object> entry : userSettings.getSettings().entrySet()){
                    Object object = this.getMyController().getDescriptor().getControllerDescription().getProperty(entry.getKey());
                    getDescriptor().getControllerDescription().setProperty(entry.getKey(), object);
            }
        }
    }

    private void doSecondaryViewActionRequest(ActionEvent e){
        setModalView((ModalView)e.getSource());
        setSecondaryView(getModalView());
        switch(this.getModalView().getMyViewType()){
            case APPOINTMENTS_CANCELLED_VIEW ->{
                doCancelledAppointmentsViewAction(e);
                break;
            }
            case APPOINTMENT_TREATMENT_VIEW ->{
                doAppointmentTreatmentViewAction(e);
                break;
            }
            case BOOKABLE_SLOT_SCANNER_VIEW ->{
                doBookableSlotScannerViewAction(e);
                break;
            }
            case CLINICAL_NOTE_VIEW ->{
                doClinicalNoteViewAction(e);
                break;
            }
            case LATE_BOOKING_END_EDITOR_VIEW ->{
                doLateBookingEndAction(e);
                break;
            }
            case NON_SURGERY_DAY_EDITOR_VIEW ->{
                doNonSurgeryDayScheduleEditorViewAction(e);
                //resetEmptySlotScannerSettings();
                break;
            }
            case SCHEDULE_EDITOR_VIEW ->{
                doScheduleEditorViewAction(e);
                //resetEmptySlotScannerSettings();
                break;
            }
            case SURGERY_DAY_EDITOR_VIEW ->{
                doSurgeryDaysEditorViewAction(e);
                //resetEmptySlotScannerSettings();
                break;
            }
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW ->{
                doUnbookableAppointmentSlotEditorAction(e);
                //resetEmptySlotScannerSettings();
                break;
            }
            case UNBOOKABLE_SLOT_SCANNER_VIEW ->{
                doUnBookableSlotScannerViewAction(e);
                break; 
            }
            case USER_SCHEDULE_DIARY_SETTINGS_VIEW,
                    USER_SCHEDULE_LIST_SETTINGS_VIEW ->{
                doScheduleSettingsRequest(e);
                break;
            }
        }
    }
    
    private void doScheduleSettingsRequest(ActionEvent e){
        boolean settingsHaveBeenUpdated = false;
        ControllerDescription controllerDescription = getDescriptor().getControllerDescription();
        ControllerDescription desktopViewControllerDescription = getMyController().getDescriptor().getControllerDescription();
        ViewDescription viewDescription = getDescriptor().getViewDescription();
        String username = ((Credential)controllerDescription.
                getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL)).getUsername();
        User user = new User(username);
        UserSettings userSettings = new UserSettings(user);
       
        ViewController.ScheduleViewControllerActionEvent actionCommand = 
                ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());
        try{
            switch(actionCommand){
                /**
                 * case USER_SCHEDULE_DIARY_FACTORY_SETTINGS_REQUEST
                 * -- method initialises the ControllerDescription with appropriate settings defined in the DesktopViewController's descriptor 
                 */
                case USER_SCHEDULE_DIARY_FACTORY_SETTINGS_REQUEST ->{
                    for(SystemDefinition.UserScheduleDiarySettings settings : SystemDefinition.UserScheduleDiarySettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        viewDescription.setProperty(property,desktopViewControllerDescription.getProperty(property));
                    }
                    userSettings.setScope(Entity.Scope.USER_SCHEDULE_DIARY_SETTINGS);
                    doUpdateSettingsRequest(userSettings, getDescriptor());
                    settingsHaveBeenUpdated = true;
                    break;
                }
                /**
                 * case USER_SCHEDULE_DIARY_SETTINGS_UPDATE_REQUEST
                 * -- method initialises the ControllerDescription with appropriate settings defined in the descriptor's ViewDescription
                 * -- method updates these to permanent storage
                 */
                case USER_SCHEDULE_DIARY_SETTINGS_UPDATE_REQUEST ->{
                    /*for(SystemDefinition.UserScheduleDiarySettings settings : SystemDefinition.UserScheduleDiarySettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        controllerDescription.setProperty(property,viewDescription.getProperty(property));
                    }*/
                    userSettings.setScope(Entity.Scope.USER_SCHEDULE_DIARY_SETTINGS);
                    doUpdateSettingsRequest(userSettings, getDescriptor());
                    settingsHaveBeenUpdated = true;
                    break;
                }
                /**
                 * case USER_SCHEDULE_LIST_FACTORY_SETTINGS_REQUEST
                 * -- method initialises the ControllerDescription with appropriate settings defined in the DesktopViewController's descriptor
                 */
                case USER_SCHEDULE_LIST_FACTORY_SETTINGS_REQUEST ->{
                    for(SystemDefinition.UserScheduleListSettings settings : SystemDefinition.UserScheduleListSettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        viewDescription.setProperty(property,desktopViewControllerDescription.getProperty(property));
                    }
                    userSettings.setScope(Entity.Scope.USER_SCHEDULE_LIST_SETTINGS);
                    doUpdateSettingsRequest(userSettings, getDescriptor());
                    settingsHaveBeenUpdated = true;
                    break;
                }
                /**
                 * case USER_SCHEDULE_DIARY_SETTINGS_UPDATE_REQUEST
                 * -- method initialises the ControllerDescription with appropriate settings defined in the descriptor's ViewDescription
                 * -- updates these to permanent storage
                 */
                case USER_SCHEDULE_LIST_SETTINGS_UPDATE_REQUEST ->{
                    /*for(SystemDefinition.UserScheduleListSettings settings : SystemDefinition.UserScheduleListSettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        controllerDescription.setProperty(property,viewDescription.getProperty(property));
                    }*/
                    userSettings.setScope(Entity.Scope.USER_SCHEDULE_LIST_SETTINGS);
                    doUpdateSettingsRequest(userSettings, getDescriptor());
                    settingsHaveBeenUpdated = true;
                    break;
                }
                case VIEW_CLOSE_NOTIFICATION ->{
                    doActionEventFor(DesktopViewController.Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION);
                    break;
                }
                case VIEW_CHANGED_NOTIFICATION ->{

                    break;
                }
            }
            if (settingsHaveBeenUpdated){
                firePropertyChangeEvent(
                            ViewController.ScheduleViewControllerPropertyChangeEvent.
                                    USER_SCHEDULE_SETTINGS_RECEIVED.toString(),
                            this.getView(),
                            this,
                            null,
                            null
                );
            }
        }catch (StoreException ex){
            String message = ex.getMessage() + "\n";
            message = message + "StoreException handled in ScheduleViewController::doScheduleSettingsRequest( "
                    + "case = " + e.getActionCommand() + ") method";
            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doUpdateSettingsRequest(UserSettings userSettings, Descriptor descriptor)throws StoreException{
        HashMap<SystemDefinition.Properties,Object> settings = userSettings.getSettingsFrom(descriptor.getViewDescription());
        userSettings.setSettings(settings);
        userSettings.update();
        userSettings = userSettings.read();
        for(Map.Entry<SystemDefinition.Properties,Object> entry : userSettings.getSettings().entrySet()){
            descriptor.getControllerDescription().
                    setProperty((SystemDefinition.Properties)entry.getKey(), entry.getValue());
        }
    }
    
    private void doNonSurgeryDayScheduleEditorViewAction(ActionEvent e){
        if (e.getActionCommand().equals(
                ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_NON_SURGERY_DAY_REQUEST.toString())){
            try{
                getModalView().setClosed(true);
            }
            catch (PropertyVetoException ex){
                String message = ex.getMessage() + "\n";
                message = message + "Error when closing down the NON_SURGERY_DAY_SCHEDULE_EDITOR view in AppointmentViewController::doSurgeryDaysEditorModalViewer()";
                displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
            }
            
            if(!((LocalDate)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY)).
                    isEqual ((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
        else if (e.getActionCommand().equals(
                ViewController.ScheduleViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString())){
            /**
             * DISABLE_CONTROLS_REQUEST requests DesktopViewController to disable menu options in its view
             */
            doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_ACTIVATED_NOTIFICATION);    
        }
    }
    
    private void doSwitchView(){
        ScheduleViewController svc = this;
        View _view = getView();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean isError = false;
                if (_view instanceof ScheduleListView){
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.CONTROLLER_VIEW_MODE, ControllerViewMode.DIARY);
                    setView(new View().make(View.Viewer.SCHEDULE_DIARY_VIEW,svc, getDesktopView()));
                }else if (_view instanceof ScheduleDiaryView){
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.CONTROLLER_VIEW_MODE, ControllerViewMode.LIST);
                    setView(new View().make(View.Viewer.SCHEDULE_LIST_VIEW,svc, getDesktopView()));
                }else{
                    isError = true;
                    String message = "Unexpected view type encountered ("
                            + _view.getClass().getSimpleName() + ")";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                if (!isError){

                    firePropertyChangeEvent(
                        ViewController.ScheduleViewControllerPropertyChangeEvent.CLOSE_VIEW_REQUEST_RECEIVED.toString(), 
                        _view, 
                        svc,
                        null,
                        null
                    );

                    doAppointmentForDayRequest((LocalDate)getDescriptor().
                            getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            svc,
                            null,
                            getDescriptor()
                    );
                }
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.CASCADE_DESKTOP_VIEWS.toString(),
                        getDesktopView(),
                        svc,
                        null,
                        getDescriptor()
                );
            }
        });
        
        
    }
    
    private void doSurgeryDaysEditorViewAction(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand =
                ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());
            
        switch(actionCommand){
            case SURGERY_DAYS_EDIT_REQUEST:{
                //getDescriptor().setViewDescription(((Descriptor)(((View)e.getSource()).getViewDescriptor())).getViewDescription());
                setSecondaryView((ModalView)e.getSource());
                HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = 
                        (HashMap<DayOfWeek,Boolean>)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT);
                try{
                    getModalView().setClosed(true);
                }
                catch (PropertyVetoException ex){
                    String message = ex.getMessage() + "\n";
                    message = message + "Error when closing down the SURGERY_DAYS_EDITOR view in AppointmentViewController::doSurgeryDaysEditorModalViewer()";
                    displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
                }
                try{
                    SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment(surgeryDaysAssignmentValue);
                    surgeryDaysAssignment.update();
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT, new SurgeryDaysAssignment().read().get());
                    firePropertyChangeEvent(
                            ViewController.ScheduleViewControllerPropertyChangeEvent.SURGERY_DAYS_ASSIGNMENT_RECEIVED.toString(),
                            getView(),
                            this,
                            null,
                            null
                    );
                }
                catch(StoreException ex){
                    String message = ex.getMessage();
                    displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case MODAL_VIEWER_ACTIVATED:
                //this.view2.initialiseView();
                break;
        }

                
        if (e.getActionCommand().equals(
                ViewController.ScheduleViewControllerActionEvent.SURGERY_DAYS_EDIT_REQUEST.toString())){
            HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = 
                    (HashMap<DayOfWeek,Boolean>)getDescriptor().getViewDescription().
                            getProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT);
            try{
                getModalView().setClosed(true);
            }
            catch (PropertyVetoException ex){
                String message = ex.getMessage() + "\n";
                message = message + "Error when closing down the SURGERY_DAYS_EDITOR view in AppointmentViewController::doSurgeryDaysEditorModalViewer()";
                displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
            }
            //HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = getDescriptorFromView().getViewDescription().getSurgeryDaysAssignmentValue();
            try{
                SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment(surgeryDaysAssignmentValue);
                surgeryDaysAssignment.update();
                //?getDescriptorFromView().getControllerDescription().setSurgeryDaysAssignment(new SurgeryDaysAssignment().read().get());
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT, new SurgeryDaysAssignment().read().get());
                
                /**
                 * fire event over to APPOINTMENT_SCHEDULE
                 */
                firePropertyChangeEvent(
                        ScheduleViewControllerPropertyChangeEvent.
                                SURGERY_DAYS_ASSIGNMENT_RECEIVED.toString(),
                        getSecondaryView(),
                        this,
                        null,
                        null
                );
            }
            catch(StoreException ex){
                String message = ex.getMessage();
                displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (e.getActionCommand().equals(
                ViewController.ScheduleViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString())){
            //this.view2.initialiseView();
            /**
             * passes message to DesktopView Controller to disable the VIEW control
             */
            doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_ACTIVATED_NOTIFICATION);            
        }
    }
    
    private void doUnbookableSlotsFromDayRequest(){
        ArrayList<Appointment> unbookableSlots = new ArrayList<>();
        Appointment appointment = new Appointment(
                SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY);
        try{
            appointment.setScope(Scope.FROM_DAY);
            appointment.setStart(LocalDate.now().atStartOfDay());
            appointment.read();
            for (Appointment a : appointment.get()){
                if(a.getIsUnbookableSlot()) unbookableSlots.add(a);
            }
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS, unbookableSlots);
        }catch (StoreException ex){
            
        }
    }
    
    private void doAppointmentSlotsFromDayRequest(){
        
        ArrayList<Appointment> appointments = null;
        Appointment appointment = null;
        try{
            appointment = new Appointment();
            appointment.setScope(Scope.FROM_DAY);
            appointment.setStart(((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY)).atStartOfDay());
            appointment.setDuration((Duration)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION));
            appointment.read();
            if (appointment.get().isEmpty()){
                JOptionPane.showMessageDialog(null, "No scheduled appointments from selected scan date (" + 
                        ((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY)).format(dmyFormat) + ")");
            }
            else{
                ArrayList<Appointment> availableSlotsOfDuration = 
                        getAvailableSlotsFromDayAndDuration(appointment.get());
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS, availableSlotsOfDuration);
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
                this.firePropertyChangeEvent(
                        ViewController.ScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                        getSecondaryView(),
                        this,
                        null,
                        null
                );

            }
        }
        catch (StoreException ex){
            displayErrorMessage("StoreException raised in controller doAppointmentSlotsFromDay()",
                    "Appointment view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doEmptySlotScanConfigurationViewAction(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case EMPTY_SLOTS_FROM_DAY_REQUEST:
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY, 
                          getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION, 
                                getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.DURATION));
                doAppointmentSlotsFromDayRequest();
                break;
        }
    }
    
    private void doClinicalNoteForAppoinmentRequest(ActionEvent e)throws StoreException{
        Appointment appointment = 
                (Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        ClinicalNote clinicalNote = new ClinicalNote(appointment);
        clinicalNote.setScope(Entity.Scope.FOR_APPOINTMENT);
        clinicalNote = clinicalNote.read();
        if (clinicalNote.get().isEmpty())
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.CLINICAL_NOTE, null);
        else getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.CLINICAL_NOTE, clinicalNote.get().get(0));
        firePropertyChangeEvent(
            ViewController.ClinicalNoteViewControllerPropertyChangeEvent
                    .CLINICAL_NOTE_RECEIVED.toString(),
            //getView(),
            (ModalView)e.getSource(),
            this,
            null,
            null
        );
    }
    
    private Appointment doAppointmentCreateRequest(ActionEvent e, Appointment changedRequestedSlot){
        //ViewController.ViewMode viewMode = ((View)e.getSource()).getViewMode();
        Appointment result = null;
        try{
            setScheduleReport(new ScheduleReport());
            result = doChangeAppointmentScheduleForDayRequest(ViewController.ViewMode.CREATE, changedRequestedSlot); 
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error on attempt to create a new appointment",JOptionPane.WARNING_MESSAGE);
        }catch (NullPointerException ex){
            String message = ex.getMessage();
            message = message + "\n";
        }
        return result;
    }
    
    private Appointment doAppointmentUpdateRequest(ActionEvent e, Appointment changedSlotRequest){
        Appointment result = null;
        
        try{
            setScheduleReport(new ScheduleReport());
            result = doChangeAppointmentScheduleForDayRequest(ViewMode.UPDATE, changedSlotRequest);
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error on attempt to update an appointment",JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }

    private void sendErrorToScheduleEditorView(){
        /**
         * fire event over to SCHEDULE_EDITOR_VIEW
         */
        
        firePropertyChangeEvent(
                ScheduleViewControllerPropertyChangeEvent.
                        APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString(),
                getSecondaryView(),
                this,
                null,
                null
        );
    }
    
    private void doCancelledAppointmentsViewAction(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST:
                try{
                getModalView().setClosed(true); 
                }
                catch (PropertyVetoException ex){
                    String message = ex.getMessage() + "\n";
                    message = message + "Error when closing down SCHEDULE_VIEW in ScheduleViewController::doCancelledAppointmentsViewAction()";
                    displayErrorMessage(message,"ScheduleViewController error",JOptionPane.WARNING_MESSAGE);
                }
                /**
                 * a new schedule view controller is only requested if schedule date not the same 
                 */
                if (!((LocalDate)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY)).
                        isEqual((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                    ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }
                break;
            case APPOINTMENT_UNCANCEL_REQUEST:{
                Appointment theUncancelledAppointment = null;
                Appointment appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                setScheduleReport(new ScheduleReport());
                try{
                        getModalView().setClosed(true);
                    appointment = super.doChangeAppointmentScheduleForDayRequest(
                                ViewMode.NO_ACTION, appointment);
                    if (appointment == null){//assume a collision has arisen and update appt. cancel status
                            displayErrorMessage("Uncancelling this appointment causes the following scheduling error.\n"
                                    + getScheduleReport().getError() + "\n"
                                            + "Uncancelling operation aborted.",
                                    "Appointment view controller error",
                                    JOptionPane.WARNING_MESSAGE);
                    }else{
                        Patient patient = appointment.getPatient();
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                        LocalDate day = appointment.getStart().toLocalDate();
                        theUncancelledAppointment = appointment;
                        appointment.uncancel();
                        appointment.setScope(Scope.CANCELLED);
                        appointment.read();
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_CACELLATIONS, appointment.get());
                        if (getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY)!=null){
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY, null);
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION, null);
                            this.firePropertyChangeEvent(
                                    ViewController.ScheduleViewControllerPropertyChangeEvent.
                                            NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                                    getView(),
                                    this,
                                    null,
                                    getDescriptor()
                            );
                        }
                        this.firePropertyChangeEvent(
                                ViewController.ScheduleViewControllerPropertyChangeEvent.
                                        APPOINTMENTS_CANCELLED_RECEIVED.toString(),
                                (View)e.getSource(),
                                this,
                                null,
                                getDescriptor()
                            );
                        if(appointment.getStart().toLocalDate().equals(
                                getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                            appointment = new Appointment();
                            appointment.setStart(getScheduleDay().atStartOfDay());
                            appointment.setScope(Scope.FOR_DAY);
                            appointment.read();

                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENTS, appointment.get());
                            doAppointeeReminderCount(appointment.get());
                            getUpdatedAppointmentSlotsForDay(appointment);
                
                            mergeScheduleSlotsIfPossible(getScheduleDay());
                            doAppointmentForDayRequest(getScheduleDay());
                            
                            doSendViewNewSchedule();
                               
                        }
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, theUncancelledAppointment);
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                        firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                        );
                    }
                }catch(StoreException ex){
                        String error = ex.getMessage() + "\n"
                                + "StoreException raised in Repository.doCancelledAppointmentsViewAction()";
                        displayErrorMessage(error, "Appointment schedule view controller", JOptionPane.WARNING_MESSAGE);
                }catch (PropertyVetoException ex){
                }
                
                break;
            }
        }
    }
    
    private void doUnbookableAppointmentSlotEditorAction(ActionEvent e){
        Appointment result = null;
        Appointment changedSlotRequest = 
                (Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        changedSlotRequest.setPatient(new Patient(1));
        LocalDate day = changedSlotRequest.getStart().toLocalDate();
        ScheduleViewController.Actions actionCommand =
               ScheduleViewController.Actions.valueOf(e.getActionCommand());        
        setScheduleReport(new ScheduleReport());
        switch (actionCommand){
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CANCEL_REQUEST ->{
                doAppointmentCancelRequest();
                result = new Appointment();
                break;
            }
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST ->{
                result = doAppointmentCreateRequest(e, changedSlotRequest);
                break;
            }
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST ->{
                result = doAppointmentUpdateRequest(e, changedSlotRequest);
                break;
            }
        }
        if (result!=null){
            try{
                getModalView().setClosed(true);
            }
            catch (PropertyVetoException ex){
            }
            mergeScheduleSlotsIfPossible(day);
            doAppointmentForDayRequest(day);
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT,result.getPatient());
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
            firePropertyChangeEvent(
                    ViewController.DesktopViewControllerPropertyChangeEvent.
                            SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                    (DesktopViewController)getMyController(),
                    this,
                    null,
                    getDescriptor()
            );
        }
        else {
            sendErrorToScheduleEditorView();
            //02/12/2022
            /**
             * update forces a refresh of the appt schedule for the day to its original state prior to the erroneous create/update attempt
             * -- necessary because repaint which occurs on schedule can repaint the incorrect new appt
             * -- ditto for the update case below
             */
            doAppointmentForDayRequest(day);
        }
    }
    
    private void doAppointmentTreatmentViewAction(ActionEvent e){
        Treatment treatment = null;
        boolean isError = false;
        AppointmentTreatment appointmentTreatment = null;
        Appointment appointment = (Appointment)getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        TreatmentWithState tws = (TreatmentWithState)getDescriptor()
                .getViewDescription().getProperty(SystemDefinition.Properties.TREATMENT_WITH_STATE);
        appointmentTreatment = 
                        new AppointmentTreatment(appointment, tws.getTreatment());
        appointmentTreatment.setScope(Scope.SINGLE);
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());
        switch (actionCommand){
            case TREATMENT_CREATE_REQUEST:
                treatment = tws.getTreatment();
                try{
                    treatment.insert();
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
            case APPOINTMENT_TREATMENT_CREATE_REQUEST:
                try{
                    /*new AppointmentTreatment(
                            appointment,tws.getTreatment()).insert();*/
                    appointmentTreatment.insert();
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
            case APPOINTMENT_TREATMENT_COMMENT_UPDATE_REQUEST:
                try{
                    /*AppointmentTreatment at = new AppointmentTreatment(
                            appointment, tws.getTreatment());*/
                    
                    appointmentTreatment.setComment(tws.getComment());
                    appointmentTreatment.update();
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
            case APPOINTMENT_TREATMENT_DELETE_REQUEST:
                /**
                 * Because treatment is not actually being deleted (its 'isDeleted' column is set true)
                 * Referential integrity between Treatment and AppointmentTreatment tables must be  handled by the code
                 * So on delete treatment request
                 * -- check an entry an A table does not exist for the treatment to be deleted
                 * ---- if it does send error message to view
                 * ---- else continue with the deletion process
                 */
                String error = null;
                treatment = tws.getTreatment();
                AppointmentTreatment at = new AppointmentTreatment(
                        new Appointment(), treatment);
                at.setScope(Entity.Scope.FOR_TREATMENT);
                try{
                    at = at.read();
                    if (!at.get().isEmpty()) {
                        error = "<html><center>'" + tws.getTreatment().getDescription() +"' "
                                + "treatment is currently selected "
                                + "by one or more appointments</center>"
                                + "<center>Request to delete aborted</center></html>";
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                        isError = true;
                    }
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\nHandled in "
                            + "TreatmentViewController::actionPerformed("
                            + actionCommand + ") after an AppointmentTreatment table read";
                    displayErrorMessage(message, 
                            "Treatment view controller error", 
                            JOptionPane.WARNING_MESSAGE);
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, null);
                    isError = true;
                }
                if (isError){
                    firePropertyChangeEvent(
                        ViewController.ScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString(),
                        (View)e.getSource(),
                        this,
                        null,
                        getDescriptor()
                    );
                }else{
                    try{
                        treatment.setScope(Entity.Scope.SINGLE);
                        treatment.delete();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "StoreException handled in "
                                + "ScheduleViewController::doAppointmentTreatmentViewAction("
                                + actionCommand.toString() + ")";
                        displayErrorMessage(message, 
                                "Schedule view controller error",
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                }
                break;
            case APPOINTMENT_TREATMENT_NAME_UPDATE_REQUEST:
                treatment = tws.getTreatment();
                try{
                    treatment.update();
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
                
            case APPOINTMENT_TREATMENT_STATE_SET_REQUEST:
                try{
                    appointmentTreatment.insert();
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
            case APPOINTMENT_TREATMENT_STATE_RESET_REQUEST:
                try{
                    appointmentTreatment.delete();
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
        }
        if (!isError){
            try{
                TreatmentWithState treatmentWithState =
                        getTreatmentsWithState(appointment);
                getDescriptor().getControllerDescription()
                        .setProperty(SystemDefinition.Properties.TREATMENT_WITH_STATE, treatmentWithState);
                doAppointmentForDayRequest(appointment.getStart().toLocalDate());
                firePropertyChangeEvent(
                        ViewController.ScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENT_TREATMENT_WITH_STATE_RECEIVED.toString(),
                        (View)e.getSource(),
                        this,
                        null,
                        null
                );
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                );
            }catch(StoreException ex){
                String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "ScheduleViewController::doAppointmentTreatmentViewAction()";
                    displayErrorMessage(message, 
                            "Schedule view controller error",
                            JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void doUnBookableSlotScannerViewAction(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case EMPTY_SLOTS_FROM_DAY_REQUEST:
                if (getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY)!=null){
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY, 
                            getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION, 
                            getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.DURATION));
                    doAppointmentSlotsFromDayRequest();
                }else this.resetEmptySlotScannerSettings();
                break;
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                break;
            case SCHEDULE_DIARY_VIEW_CONTROLLER_REQUEST:
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.SCHEDULE_DIARY_VIEW_CONTROLLER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                break;
        }
    }
    
    private void doBookableSlotScannerViewAction(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case EMPTY_SLOTS_FROM_DAY_REQUEST:
                if (getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY)!=null){
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY, 
                            getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION, 
                            getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.DURATION));
                    doAppointmentSlotsFromDayRequest();
                }else this.resetEmptySlotScannerSettings();
                break;
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                break;
            case SCHEDULE_DIARY_VIEW_CONTROLLER_REQUEST:
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.SCHEDULE_DIARY_VIEW_CONTROLLER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                break;
        }
    }
    
    private void doClinicalNoteViewAction(ActionEvent e){
        Appointment appointment = null;
        ClinicalNote clinicalNote = null;
        String message = null;
        String error = null;
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());
        switch (actionCommand){
            case CLINICAL_NOTE_FOR_APPOINTMENT_REQUEST:
                try{
                    doClinicalNoteForAppoinmentRequest(e);
                }catch(StoreException ex){
                    message = ex.getMessage() + "\n"
                            + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                            + actionCommand.toString() +")";
                    displayErrorMessage(message,"View controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
            case CLINICAL_NOTE_CREATE_REQUEST:
                try{
                    appointment = 
                            (Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                    clinicalNote = (ClinicalNote)getDescriptor()
                            .getViewDescription().getProperty(SystemDefinition.Properties.CLINICAL_NOTE);
                    clinicalNote.insert();
                    doClinicalNoteForAppoinmentRequest(e);
                    if(getDescriptor().getControllerDescription()
                            .getProperty(SystemDefinition.Properties.CLINICAL_NOTE) == null){
                        error = "Attempt to create a new clinical note failed";
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                        firePropertyChangeEvent(
                            ViewController.ClinicalNoteViewControllerPropertyChangeEvent
                                    .CLINICAL_NOTE_ERROR_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                    }
                }catch(StoreException ex){
                    message = ex.getMessage() + "\n"
                            + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                            + actionCommand.toString() +")";
                    displayErrorMessage(message,"View controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
            case CLINICAL_NOTE_DELETE_REQUEST:
                try{
                    clinicalNote = (ClinicalNote)getDescriptor()
                                .getViewDescription().getProperty(SystemDefinition.Properties.CLINICAL_NOTE);
                    clinicalNote.setScope(Entity.Scope.SINGLE);
                    clinicalNote.delete();
                    doClinicalNoteForAppoinmentRequest(e);
                }catch(StoreException ex){
                    message = ex.getMessage() + "\n"
                            + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                            + actionCommand.toString() +")";
                    displayErrorMessage(message,"View controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
            case CLINICAL_NOTE_UPDATE_REQUEST:
                try{
                        clinicalNote = (ClinicalNote)getDescriptor()
                                    .getViewDescription().getProperty(SystemDefinition.Properties.CLINICAL_NOTE);
                        clinicalNote.update();
                        doClinicalNoteForAppoinmentRequest(e);
                    }catch(StoreException ex){
                        message = ex.getMessage() + "\n"
                                + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                                + actionCommand.toString() +")";
                        displayErrorMessage(message,"View controller error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                break;
        }
    }
    
    private void doLateBookingEndAction(ActionEvent e){
        
    }

    /**
     * 26/06/2024 05:32 update 
     * @param e 
     */
    private void doScheduleEditorViewAction(ActionEvent e){
        Appointment result;
        Appointment changedSlotRequest = 
                (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        LocalDate day = changedSlotRequest.getStart().toLocalDate();
        ViewController.ScheduleViewControllerActionEvent actionCommand =
               ViewController.ScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case APPOINTMENTS_FOR_DAY_REQUEST:
                doAppointmentForDayRequest((LocalDate)getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));       
                break;
            
            case SCHEDULE_EDITOR_CREATE_APPOINTMENT_REQUEST:
                setScheduleReport(new ScheduleReport());
                result = doAppointmentCreateRequest(e, changedSlotRequest);
                if (result!=null){
                    try{
                        getModalView().setClosed(true);
                    }
                    catch (PropertyVetoException ex){
                    }
                    mergeScheduleSlotsIfPossible(day);
                    doAppointmentForDayRequest(day);
                    //getControllerDescriptor().getControllerDescription().setAppointment(result);
                    //getControllerDescriptor().getControllerDescription().setAppointments(result.get());
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, result.getPatient());
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                    /**
                     * code update 09/12/2025
                     * -- ensures correct view mode setting when PVC property change event processed
                     */
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.VIEW_MODE,ViewController.ViewMode.CREATE);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else {
                    sendErrorToScheduleEditorView();
                    //02/12/2022
                    /**
                     * update forces a refresh of the appt schedule for the day to its original state prior to the erroneous create/update attempt
                     * -- necessary because repaint which occurs on schedule can repaint the incorrect new appt
                     * -- ditto for the update case below
                     */
                    doAppointmentForDayRequest(day);
                }
                break;
            case SCHEDULE_EDITOR_UPDATE_APPOINTMENT_REQUEST:
                day = 
                        //?getDescriptorFromView().getViewDescription().getAppointment().
                        ((Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).
                                getStart().toLocalDate();
                result = doAppointmentUpdateRequest(e, changedSlotRequest);
                if (result!=null) {
                    try{
                        getModalView().setClosed(true);
                    }
                    catch (PropertyVetoException ex){
                    }
                    //mergeScheduleSlotsIfPossible(day);
                    //doAppointmentForDayRequest(day);
                    mergeScheduleSlotsIfPossible((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, result.getPatient()); 
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                    /**
                     * code update 09/12/2025
                     * -- ensures correct view mode setting when PVC property change event processed
                     */
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.VIEW_MODE,ViewController.ViewMode.UPDATE);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else {
                    //2/12/2022
                    sendErrorToScheduleEditorView();
                    //doAppointmentForDayRequest(day); 
                    doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                }
                break;
            case SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST:
                //check this requewsted for a patient not already a patient today
                boolean isEmergencyPatientAlreadyBookedForAppointmentToday = false;
                Patient patient = changedSlotRequest.getPatient();
                ArrayList<Appointment> test = (ArrayList<Appointment>)getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENTS);
                for (Appointment appointment : test){
                /*for (Appointment appointment : 
                        getDescriptor().getControllerDescription().getAppointments()){*/
                    if(appointment.getPatient().equals(patient)){
                        isEmergencyPatientAlreadyBookedForAppointmentToday = true;
                        break;
                    }
                }
                if(!isEmergencyPatientAlreadyBookedForAppointmentToday){
                    
                    day = ((Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).
                                getStart().toLocalDate();
                    result = doAppointmentCreateRequest(e, changedSlotRequest);
                    if (result!=null) {//note: unlikely outcome since emergency appointment and should clash
                        try{
                            getModalView().setClosed(true);
                        }
                        catch (PropertyVetoException ex){
                        }
                        //mergeScheduleSlotsIfPossible(day); not necessary because this is only entry for patient
                        doAppointmentForDayRequest(day);
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, result.getPatient());

                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                        firePropertyChangeEvent(
                                ViewController.DesktopViewControllerPropertyChangeEvent.
                                        SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                                (DesktopViewController)getMyController(),
                                this,
                                null,
                                getDescriptor()
                        );
                    }else{
                        changedSlotRequest.setIsEmergency(true);
                        try{
                            changedSlotRequest.insert();
                            doAppointmentForDayRequest(day);
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR,getDescriptor());
                            firePropertyChangeEvent(
                                ViewController.DesktopViewControllerPropertyChangeEvent.
                                        SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                                (DesktopViewController)getMyController(),
                                this,
                                null,
                                getDescriptor()
                            );
                            getModalView().setClosed(true);
                        }catch(StoreException ex){
                            String message = ex.getMessage() + "\n"
                                    + "StoreException handled in doScheduleEditorViewAction("
                                    + actionCommand.toString() + ")";
                            displayErrorMessage(message,
                                    "Schedule view controller error",JOptionPane.WARNING_MESSAGE);
                        }catch (PropertyVetoException ex){
                        
                        }

                    }
                }else {//emergency patient already on the schedule
                    String error = "Emergency patient selection invalid because this patient already booked on schedule";
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                    sendErrorToScheduleEditorView();
                    doAppointmentForDayRequest(day);
                }
                break;
                case APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST:
                /**
                 * -- check ModalAppointmentEditorView viewmode == CREATE
                 * -- send message to ModalAppointmentEditorView to close view
                 * ---- assumes current ControllerDescription will re-launch view ok
                 * -- launch ModalTreatmetView
                 * -- when that closes relaunch ModalAppointmentEditorView
                 */
                
                View view = (View)e.getSource();
                doRequestCloseModalAppointmentEditorView(view);
                doOpenTreatmentView();
                doReopenModelAppointmentEditorView();
                break;
        } 
    }

    private void doReopenModelAppointmentEditorView(){
        setModalView((ModalView)new View().make(View.Viewer.SCHEDULE_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        doActionEventFor(DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION);
    }
    
    private void doOpenTreatmentView(){
        Appointment appointment = (Appointment)getDescriptor()
                .getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
        try{
            TreatmentWithState treatmentWithState =
                    getTreatmentsWithState(appointment);
            getDescriptor().getControllerDescription()
                    .setProperty(SystemDefinition.Properties.TREATMENT_WITH_STATE, treatmentWithState);
            setModalView((ModalView)new View().make(
                        View.Viewer.APPOINTMENT_TREATMENT_VIEW,
                        this, 
                        this.getDesktopView()).getModalView());
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "StoreException handled in ScheduleViewController:: getTreatmentWithState()";
            displayErrorMessage(message, "Schedule view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doRequestCloseModalAppointmentEditorView(View view){
        firePropertyChangeEvent(
            ViewController.PatientViewControllerPropertyChangeEvent.
                CLOSE_VIEW_REQUEST_RECEIVED.toString(),
            view,
            this,
            null,
            null
        );
    }

    private void doDesktopViewControllerAction(ActionEvent e){
        LocalDate scheduleDay = (LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
        /*ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());*/
        DesktopViewController.Actions actionCommand =
               DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case INITIALISE_VIEW_CONTROLLER:
                /*getDescriptor().getControllerDescription().setEarlyBookingStartTime(
                        LocalDateTime.of(scheduleDay,ViewController.FIRST_APPOINTMENT_SLOT));*/
                break;
            case REFRESH_DISPLAY_REQUEST:
                doAppointmentForDayRequest((LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                doActionEventFor(DesktopViewController.Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION);
            }
        }
    }
    
    private String getNameOfSlotOwnerPlusSlotStart(Appointment slot){
        String result = getNameOfSlotOwner(slot);
        LocalTime start = slot.getStart().toLocalTime();
        result = result + " which starts at " + start.format(DateTimeFormatter.ofPattern("HH:mm"));
        return result;
    }
  
    private String getNameOfSlotOwner(Appointment slot){
        String title;
        String forenames;
        String surname;
        
        title = slot.getPatient().getName().getTitle();
        forenames = slot.getPatient().getName().getForenames();
        surname = slot.getPatient().getName().getSurname();
        if (title.length()==0) title = "?";
        if (forenames.length() == 0) forenames = "<...>";
        if (surname.length() == 0) surname = "<...>";
       
        return title + " " + forenames + " " + surname;
    }

    private ArrayList<Appointment> getAvailableSlotsFromDayAndDuration(
            ArrayList<Appointment> appointments){
    //private ArrayList<Appointment> getAvailableSlotsFromDayAndDuration(
            //ArrayList<Appointment> appointments, Duration duration, LocalDate searchStartDay){
        Duration duration = (Duration)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION);
        //LocalDate searchStartDate = getDescriptor().getControllerDescription().getEmptySlotFromDay();
        ArrayList<Appointment> result = new ArrayList<>();
        ArrayList<Appointment> appointmentsForSingleDay = new ArrayList<>();
        ArrayList<ArrayList<Appointment>> appointmentsGroupedByDay = new ArrayList<>();
        LocalDate currentDate = null;
        Iterator<Appointment> it = appointments.iterator();
        while(it.hasNext()){
            Appointment appointment = it.next();
            if (currentDate==null) 
                currentDate = appointment.getStart().toLocalDate();
                //currentDate = getDescriptor().getControllerDescription().getEmptySlotFromDay();
            if (appointment.getStart().toLocalDate().equals(currentDate)) appointmentsForSingleDay.add(appointment);
            else {
                appointmentsGroupedByDay.add(appointmentsForSingleDay);
                currentDate = appointment.getStart().toLocalDate();
                appointmentsForSingleDay = new ArrayList<>();
                appointmentsForSingleDay.add(appointment);
            }
        }
        appointmentsGroupedByDay.add(appointmentsForSingleDay);
        Iterator<ArrayList<Appointment>> it1 = appointmentsGroupedByDay.iterator();

        currentDate = (LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY);
        while(it1.hasNext()){
            appointmentsForSingleDay = it1.next();
            LocalDate appointmentsForSingleDayDate = appointmentsForSingleDay.get(0).getStart().toLocalDate();
            while(currentDate.isBefore(appointmentsForSingleDayDate)){
                if(currentDate.getDayOfWeek().equals(DayOfWeek.TUESDAY) 
                            || currentDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)
                            || currentDate.getDayOfWeek().equals(DayOfWeek.FRIDAY))
                        result.add(this.createEmptyAppointmentSlot(
                              currentDate.atTime(ViewController.FIRST_APPOINTMENT_SLOT))); 
                currentDate = currentDate.plusDays(1); 
            }
            ArrayList<Appointment> slotsForDay = 
                    getAppointmentsForSelectedDayIncludingEmptySlots(
                            appointmentsForSingleDay, appointmentsForSingleDayDate); 
            Iterator<Appointment> it2 = slotsForDay.iterator();
            while(it2.hasNext()){
                Appointment slot = it2.next();
                if (!getIsBookedStatus(slot)){
                    long slotDuration = slot.getDuration().toMinutes();
                    if (slotDuration >= duration.toMinutes()){
                        result.add(slot);
                    }
                }
            } 
            currentDate = currentDate.plusDays(1);
        } 
        /**
         * if scan duration == all day (8 hours)
         * 
         * else
         * -- check and process days which have no appointments on, as follows
         *   -- consecutive appointment-less days are merged into a single slot 
         *   -- the single slot duration represents in hours the number of consecutive days
         */
        
        boolean multiDayIntervalHasStarted = false;
        Appointment multiDayIntervalWithNoAppointments = null;
        ArrayList<Appointment> finalisedResult = new ArrayList<>();
        it = result.iterator();
        int count = 0;
        
        if (duration.toHours()==8){//empty slot scan duration is all day
            while (it.hasNext()){
                count = count + 1;
                if (count == 23){
                    count = 19;                    
                }
                Appointment appointment = it.next();
                if (finalisedResult.isEmpty()&&multiDayIntervalWithNoAppointments==null){//start of procedure on entry
                    multiDayIntervalWithNoAppointments = new Appointment();
                    multiDayIntervalWithNoAppointments.setStart(appointment.getStart());
                    multiDayIntervalWithNoAppointments.setDuration(Duration.ofHours(0));
                }
                else{
                    if (areTheseSlotsOnConsecutivePracticeDays(multiDayIntervalWithNoAppointments,appointment)){
                        if (multiDayIntervalWithNoAppointments!=null){
                            Duration d = multiDayIntervalWithNoAppointments.getDuration();
                            multiDayIntervalWithNoAppointments.setDuration(d.plusHours(8));
                        }
                    }
                    else{
                        if (multiDayIntervalWithNoAppointments!=null){
                            Duration d = multiDayIntervalWithNoAppointments.getDuration();
                            multiDayIntervalWithNoAppointments.setDuration(d.plusHours(8));
                            finalisedResult.add(multiDayIntervalWithNoAppointments);
                            multiDayIntervalWithNoAppointments = new Appointment();
                            multiDayIntervalWithNoAppointments.setStart(appointment.getStart());
                            multiDayIntervalWithNoAppointments.setDuration(Duration.ofHours(0));
                        }
                    }
                }
            }
            if (multiDayIntervalWithNoAppointments!=null){
                Duration d = multiDayIntervalWithNoAppointments.getDuration();
                multiDayIntervalWithNoAppointments.setDuration(d.plusHours(8));
                finalisedResult.add(multiDayIntervalWithNoAppointments);
            }
        }
        else{// this is not a scan of all day slots
            while(it.hasNext()){
                Appointment appointment = it.next();
                /*
                if (appointment.getStart().toLocalDate().isEqual(LocalDate.of(2021,7, 16))){
                    LocalDate test = appointment.getStart().toLocalDate();
                }
                */
                if (appointment.getDuration().toHours() == 8){
                    //WHAT HAPPENS WHEN APPOINTMENT CHANGES MULTIDAYINTERVALHASSTARTED SLOT
                    if (!multiDayIntervalHasStarted) {
                        multiDayIntervalHasStarted = true;
                        multiDayIntervalWithNoAppointments = new Appointment();
                        multiDayIntervalWithNoAppointments.setStart(appointment.getStart());
                        multiDayIntervalWithNoAppointments.setDuration(Duration.ofHours(0));
                    }
                    else if (areTheseSlotsOnConsecutivePracticeDays(
                            multiDayIntervalWithNoAppointments,appointment)){
                            if (multiDayIntervalWithNoAppointments!=null){
                                duration = multiDayIntervalWithNoAppointments.getDuration();
                                multiDayIntervalWithNoAppointments.setDuration(duration.plusHours(8));
                            }
                    }
                    else{
                        if (multiDayIntervalWithNoAppointments!=null){
                            Duration d = multiDayIntervalWithNoAppointments.getDuration();
                            multiDayIntervalWithNoAppointments.setDuration(d.plusHours(8));
                            finalisedResult.add(multiDayIntervalWithNoAppointments);
                            multiDayIntervalWithNoAppointments = new Appointment();
                            multiDayIntervalWithNoAppointments.setStart(appointment.getStart());
                            multiDayIntervalWithNoAppointments.setDuration(Duration.ofHours(0));
                            //06/08/2022 08:49
                            //multiDayIntervalWithNoAppointments.setStatus(Appointment.Status.UNBOOKED);
                        }
                    }
                }
                else if (multiDayIntervalHasStarted){
                    if (multiDayIntervalWithNoAppointments!=null){
                        Duration d = multiDayIntervalWithNoAppointments.getDuration();
                        multiDayIntervalWithNoAppointments.setDuration(d.plusHours(8));
                        finalisedResult.add(multiDayIntervalWithNoAppointments);
                        multiDayIntervalHasStarted = false;
                        finalisedResult.add(appointment);
                    }
                }
                else finalisedResult.add(appointment);  
            } 
            if (multiDayIntervalHasStarted){
                if (multiDayIntervalWithNoAppointments!=null){
                    Duration d = multiDayIntervalWithNoAppointments.getDuration();
                    multiDayIntervalWithNoAppointments.setDuration(d.plusHours(8));
                    finalisedResult.add(multiDayIntervalWithNoAppointments);
                }
            }
        }
        
        return finalisedResult;
        
    }
    
    private LocalDate getPracticeDayOnWhichSlotEnds(Appointment slot){
        
        long intervalHours = slot.getDuration().toHours();
        long intervalDays = intervalHours/8;
        LocalDate currentDate = slot.getStart().toLocalDate();
        for (int index = 0; index < intervalDays ; index ++){
            do{
                currentDate = currentDate.plusDays(1);
            }
            while(!isValidDay(currentDate));
        }
        return currentDate;
    }
    private boolean isValidDay(LocalDate day){
        return(day.getDayOfWeek().equals(DayOfWeek.TUESDAY) 
                            || day.getDayOfWeek().equals(DayOfWeek.THURSDAY)
                            || day.getDayOfWeek().equals(DayOfWeek.FRIDAY));
    }
    private boolean areTheseSlotsOnConsecutivePracticeDays(Appointment slot1, Appointment slot2){
        boolean result = false;
        LocalDate d1 = getPracticeDayOnWhichSlotEnds(slot1);
        LocalDate d2 = slot2.getStart().toLocalDate();
        LocalDate nextPracticeDay = d1;
        do{
            nextPracticeDay = nextPracticeDay.plusDays(1);
            
        }while (nextPracticeDay.getDayOfWeek()==DayOfWeek.SATURDAY||
                nextPracticeDay.getDayOfWeek()==DayOfWeek.SUNDAY||
                nextPracticeDay.getDayOfWeek()==DayOfWeek.MONDAY||
                nextPracticeDay.getDayOfWeek()==DayOfWeek.WEDNESDAY);
        if (nextPracticeDay.isEqual(d2)){
            result = true;
        }
        return result;
    }
    
    private ArrayList<Appointment> getAppointmentsForSelectedDayIncludingEmptySlots(
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
        
        /**
         * extra code to implement early start time
         */
        LocalDateTime earlyStart = (LocalDateTime)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME);
        if (earlyStart!=null){
            nextEmptySlotStartTime = earlyStart;
        }
        
        ArrayList<Appointment> apptsForDayIncludingEmptySlots = new ArrayList<>();      
        Iterator<Appointment> it = appointments.iterator();
        
        /**
         * check for no appointments on this day if no appointment create a
         * single empty slot for whole day
         */
        if (appointments.isEmpty()) {
            apptsForDayIncludingEmptySlots.add(createEmptyAppointmentSlot(
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
                else if (durationToNextSlot.isPositive()){
                    Appointment emptySlot = createEmptyAppointmentSlot(nextEmptySlotStartTime,
                            Duration.between(nextEmptySlotStartTime, appointment.getStart()).abs());
                    apptsForDayIncludingEmptySlots.add(emptySlot);
                    apptsForDayIncludingEmptySlots.add(appointment);
                    nextEmptySlotStartTime =
                            appointment.getStart().plusMinutes(appointment.getDuration().toMinutes());
                    
                }
            }
        }

        Appointment emptySlot = null;
        Duration durationToDayEnd = null;
        Appointment lastAppointment = 
                apptsForDayIncludingEmptySlots.get(apptsForDayIncludingEmptySlots.size()-1);

        LocalDateTime lateEnd = (LocalDateTime)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME);
        if (getIsBookedStatus(lastAppointment)){
            //15/07/2023 enables an appointment to run over LAST_APPOINTMENT_SLOT time
            if (lateEnd==null){
                durationToDayEnd = 
                        Duration.between(nextEmptySlotStartTime.toLocalTime(), ViewController.LAST_APPOINTMENT_SLOT);
                if (!(durationToDayEnd.isNegative() || durationToDayEnd.isZero())){
                    emptySlot = createEmptyAppointmentSlot(nextEmptySlotStartTime);
                    apptsForDayIncludingEmptySlots.add(emptySlot);
                }
            }else{
                Duration duration = Duration.between(lastAppointment.getEnd(),lateEnd);
                if (duration.isPositive()){
                    emptySlot = createEmptyAppointmentSlot(lastAppointment.getEnd(),duration);
                            apptsForDayIncludingEmptySlots.add(emptySlot);
                }
            }     
        }else{//available slot
           if (lateEnd==null){
               
           } 
            
        }
        return apptsForDayIncludingEmptySlots;
    }
    private Appointment createEmptyAppointmentSlot(LocalDateTime start){
        Appointment appointment = new Appointment();
        appointment.setPatient(null);
        appointment.setStart(start);
        appointment.setDuration(Duration.between(start.toLocalTime(), 
                                                ViewController.LAST_APPOINTMENT_SLOT));
         //06/08/2022 08:49                                       
        //appointment.setStatus(Appointment.Status.UNBOOKED);
        return appointment;
    }

    private Appointment createEmptyAppointmentSlot(LocalDateTime start, Duration duration){
        Appointment appointment = new Appointment();
        appointment.setPatient(null);
        appointment.setStart(start);
        appointment.setDuration(duration);
        //appointment.setStatus(Appointment.Status.UNBOOKED);
        //appointment.setEnd(appointment.getStart().plusMinutes(duration.toMinutes()));
        return appointment;
    }

    private void getUpdatedAppointmentSlotsForDay(Appointment appointment)throws StoreException{
        ArrayList<Appointment> appointmentSlotsForDay =
                getAppointmentsForSelectedDayIncludingEmptySlots(appointment.get(),appointment.getStart().toLocalDate());
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT,appointmentSlotsForDay); 

    }
    
    private Boolean getIsBookedStatus(Appointment appointment){
        if (appointment.getPatient()==null) return false;
        if(!appointment.getPatient().getIsKeyDefined())return false;
        return true;
    } 
   
    /**
     * method generates a Point object from the collection of Appointment objects passed to it
     * -- the Point objects's x property = number of patients who have had an appt reminder
     * -- the Point objects's y property = number of appointments in the collection 
     * -- if the collection received is empty both x and Y in the Point are set to 0
     * -- the newEntity||Descriptor::appointeeReminderCountForTheday is initialised with the Point
     * @param appointmentsForDay, ArrayList<Appointment>
     */
    private void doAppointeeReminderCount(ArrayList<Appointment> appointmentsForDay){
        Appointment appointment;
        int appointeesReminded = 0;
        if (appointmentsForDay.size()>0){
            Iterator it = appointmentsForDay.iterator();
            while(it.hasNext()){
                appointment = (Appointment)it.next();
                if (appointment.getHasPatientBeenContacted()){
                    appointeesReminded++;
                }
            }
            Point appointeeRemindedCount = new Point(appointeesReminded, appointmentsForDay.size());
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTEE_REMINDERS_COUNT_FOR_DAY, appointeeRemindedCount);
        }
        else getDescriptor().getControllerDescription().
                setProperty(SystemDefinition.Properties.APPOINTEE_REMINDERS_COUNT_FOR_DAY, new Point(0,0));
    }
 
    private void resetEmptySlotScannerSettings(){
        firePropertyChangeEvent(
                ViewController.ScheduleViewControllerPropertyChangeEvent.NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                getSecondaryView(),
                this,
                null,
                getDescriptor()
        );
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY,null);
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION, null);
    }
    
    private void setSecondaryView(View value){
        secondaryView = value;
    }
    
    private View getSecondaryView(){
        return secondaryView;
    }
    
    private void doSendViewNewSchedule(){
        try{
            ArrayList<Slot> slots = 
                    convertScheduleListToDiaryFormat((ArrayList<Appointment>)getDescriptor().
                            getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT));
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_DIARY_FORMAT, slots);
            Patient patient = new Patient();
            patient.setScope(Scope.ALL);
            patient.read();
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENTS, patient.get());
            firePropertyChangeEvent(
                    ViewController.ScheduleViewControllerPropertyChangeEvent.
                            APPOINTMENTS_FOR_DAY_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    null
            );
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "StoreException raised in doSendViewNewSchedule()";
            displayErrorMessage(message,"Schedule view controller", JOptionPane.WARNING_MESSAGE);
        }
    }
}
