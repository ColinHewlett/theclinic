/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import _system_environment_variables.SystemDefinitions;
import static controller.ViewController.displayErrorMessage;
import model.Entity.Scope;
import model.Appointment;
import model.Patient;
import model.SurgeryDaysAssignment;
import repository.StoreException;//01/03/2023
import view.views.non_modal_views.DesktopView;
import view.View;
import view.views.modal_views.ModalView;
import view.views.modal_views.ModalEmptySlotScanConfigurationView;
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
import java.util.Optional;
import javax.swing.JOptionPane;

/**
 *
 * @author colin
 */
public class ScheduleViewController extends ViewController{

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
    private LocalDate appointmentScheduleDay = null;
    
    private LocalDate getAppointmentScheduleDay(){
        return appointmentScheduleDay;  
    }
    
    private void setAppointmentScheduleDay(LocalDate day){
        appointmentScheduleDay = day;
    }
    
    /**
     * 
     * @param controller
     * @param desktopView
     * @param ed
     * @throws StoreException 
     */
    public ScheduleViewController(ActionListener controller, DesktopView desktopView)throws StoreException{
        setMyController(controller);
        setDesktopView(desktopView);
        pcSupport = new PropertyChangeSupport(this);
        //Descriptor e = ed.orElse(new Descriptor());
        //setDescriptor(e);
        /*
        try{
            SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment();
            //surgeryDaysAssignment.read();
            surgeryDaysAssignment = surgeryDaysAssignment.read();
            getDescriptor().getControllerDescription().setSurgeryDaysAssignment(surgeryDaysAssignment.get());

            pcSupport.removePropertyChangeListener(getView());
        }
        catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        } 
        */
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTMENT_FOR_DAY_RECEIVED:{
                Appointment appointment = ((Descriptor)e.getNewValue()).getControllerDescription().getAppointment();
                if (getAppointmentScheduleDay().isEqual(appointment.getStart().toLocalDate())){
                    appointment.setScope(Scope.FOR_DAY);
                    try{
                        appointment.read();
                        doAppointmentForDayRequest(getAppointmentScheduleDay());
                    }catch(StoreException ex){
                        displayErrorMessage(ex.getMessage() + "\nRaised in propertyChange()",
                                "Appointment schedule view controller error",
                                JOptionPane.WARNING_MESSAGE);
                    } 
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){

        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerAction(e);
        }
        else {
            
            View the_view = (View)e.getSource();
            

            switch(the_view.getMyViewType()){
                case SCHEDULE_VIEW:
                    doPrimaryViewActionRequest(e);
                    break;
                default:
                    doSecondaryViewActionRequest(e);
                    break;
            }
        }
        
    }

    /**
     * handles a cancel appoinment request rais3ed in the primary view, AppointmentScheduleView
     
     * 
     */
    private void doAppointmentCancelRequest(){ 
        //?if (getDescriptorFromView().getViewDescription().getAppointment().getPatient().getIsKeyDefined()){
        if (getDescriptor().getViewDescription().getAppointment().getPatient().getIsKeyDefined()){    
            try{
                //?Appointment appointment = getDescriptorFromView().getViewDescription().getAppointment();
                Appointment appointment = getDescriptor().getViewDescription().getAppointment();
                Patient patient = appointment.getPatient();
                getDescriptor().getControllerDescription().setPatient(patient);
                LocalDate day = appointment.getStart().toLocalDate();
                if (patient.toString().equals(SystemDefinitions.APPOINTMENT_UNBOOKABILITY_MARKER)) {
                    appointment.setScope(Scope.SINGLE);
                    appointment.delete();
                }
                else appointment.cancel();
                if (day.equals(getDescriptor().getControllerDescription().getAppointmentScheduleDay())){
                    appointment = new Appointment();
                    appointment.setStart(day.atStartOfDay());
                    appointment.setScope(Scope.FOR_DAY);
                    appointment.read();
                    getDescriptor().getControllerDescription().setAppointment(appointment);
                    getDescriptor().getControllerDescription().setAppointments(appointment.get());
                    getDescriptor().getControllerDescription().setAppointmentScheduleDay(day);
                    //doAppointeeReminderCount(appointment.get());
                    getUpdatedAppointmentSlotsForDay(appointment);   
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
            getDescriptor().getControllerDescription().
                    setAppointmentCancellations(appointment.get());
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
            getDescriptor().getControllerDescription().setPatients(patient.get());
            /* respond accordingly to the view's mode */
            switch (getDescriptor().getViewDescription().getViewMode()){
                case SLOT_SELECTED:
                    getDescriptor().getControllerDescription().setAppointment(
                            getDescriptor().getViewDescription().getAppointment());
                    break;
                case SLOT_UNSELECTED:
                    Appointment a = new Appointment();
                    a.setStart(LocalDateTime.of(getAppointmentScheduleDay(),LocalTime.of(9,0)));
                    a.setDuration(Duration.ZERO);
                    getDescriptor().getControllerDescription().
                            setAppointment(a);   
                    break;
            }
            getDescriptor().getControllerDescription().setViewMode(ViewMode.CREATE);
            if (getDescriptor().getControllerDescription().
                    getAppointment().getPatient()==null){
                setModalView((ModalView)new View().make(
                    View.Viewer.APPOINTMENT_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
                /**
                 * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
                 * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
                 * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
                 */
                ActionEvent actionEvent = new ActionEvent(
                       this,ActionEvent.ACTION_PERFORMED,
                       ViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
                this.getMyController().actionPerformed(actionEvent);
            }else{
                if (getDescriptor().getControllerDescription().
                    getAppointment().getPatient().toString().equals(SystemDefinitions.APPOINTMENT_UNBOOKABILITY_MARKER)) 
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
    
    private void doUnbookableAppointmentSlotEditorViewRequest(){
        getDescriptor().getControllerDescription().setViewMode(null);
        View.setViewer(View.Viewer.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW);
        switch (getDescriptor().getViewDescription().getViewMode()){
            case SLOT_SELECTED:{
                if(getDescriptor().getViewDescription().
                        getAppointment().getPatient() == null){//check if this slot is occupied by a patient
                    //NO must be unbooked
                    getDescriptor().getControllerDescription().setAppointment(
                            getDescriptor().getViewDescription().getAppointment());
                    getDescriptor().getControllerDescription().
                            setViewMode(ViewController.ViewMode.CREATE);     
                }
                else if (getDescriptor().getViewDescription().
                        getAppointment().getIsUnbookableSlot()){//is this an UNBOOKABLE slot
                    getDescriptor().getControllerDescription().setAppointment(
                        getDescriptor().getViewDescription().getAppointment());
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
                        setAppointment(new Appointment());
                getDescriptor().getControllerDescription().
                        setViewMode(ViewController.ViewMode.CREATE);
                break;
            case UPDATE: // indicates schedule view UPDATE button clicked se;ecvted unbookable slot
                getDescriptor().getControllerDescription().setAppointment(
                    getDescriptor().getViewDescription().getAppointment());
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
        ActionEvent actionEvent = new ActionEvent(
               this,ActionEvent.ACTION_PERFORMED,
               ViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
        this.getMyController().actionPerformed(actionEvent);
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
        if (getDescriptor().getViewDescription().getAppointment().getIsKeyDefined()){
            try{

                //?Appointment appointment = getDescriptorFromView().getViewDescription().getAppointment();
                Appointment appointment = getDescriptor().getViewDescription().getAppointment();
                patient = new Patient();
                patient.setScope(Scope.ALL);
                patient.read();
                //initialiseNewEntityDescriptor();
                getDescriptor().getControllerDescription().setAppointment(appointment);
                getDescriptor().getControllerDescription().setPatients(patient.get());
                getDescriptor().getControllerDescription().setViewMode(ViewMode.UPDATE);
                setModalView((ModalView)new View().make(
                    View.Viewer.APPOINTMENT_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
                /**
                 * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
                 * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
                 * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
                 */
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
                this.getMyController().actionPerformed(actionEvent);
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
        ActionEvent actionEvent = new ActionEvent(
               this,ActionEvent.ACTION_PERFORMED,
               DesktopViewController.DesktopViewControllerActionEvent.VIEW_CLOSED_NOTIFICATION.toString());
        this.actionPerformed(actionEvent); 
    }
    
    /**
     * method fetches appointments from persistent store with the specified date
     * -- the appointee reminder summary for these appointments is initialised
     * -- and a viewable list of appointments prepared which includes any empty slots
     * @param day, LocalDate
     */
    private void doAppointmentForDayRequest(LocalDate day){
        Appointment appointment = new Appointment();
        appointment.setStart(day.atStartOfDay());
        appointment.setScope(Scope.FOR_DAY);
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
            getDescriptor().getControllerDescription().setAppointments(appointment.get());
            getDescriptor().getControllerDescription().setAppointmentScheduleDay(day);
            doAppointeeReminderCount(appointment.get());
            getUpdatedAppointmentSlotsForDay(appointment);
            firePropertyChangeEvent(
                    ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                            APPOINTMENTS_FOR_DAY_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    null
            );
            resetEmptySlotScannerSettings();
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
            getDescriptor().getControllerDescription().setSurgeryDaysAssignment(new SurgeryDaysAssignment().read().get());
            setModalView((ModalView)new View().make(
                    View.Viewer.NON_SURGERY_DAY_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView()); 
            /**
             * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
             * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
             * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
             */
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doSurgeryDayScheduleViewRequest(){
        try{
            getDescriptor().getControllerDescription().setAppointmentScheduleDay(getAppointmentScheduleDay());
            getDescriptor().getControllerDescription().setSurgeryDaysAssignment(new SurgeryDaysAssignment().read().get());
            setModalView((ModalView)new View().make(
                    View.Viewer.SURGERY_DAY_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView()); 
            /**
             * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
             * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
             * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
             */
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
            this.getMyController().actionPerformed(actionEvent);
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
        
        this.setModalView((ModalView)new View().make(
                    View.Viewer.EMPTY_SLOT_SCAN_CONFIGURATION_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());

        /**
         * ENABLE_CONTROLS_REQUEST requests DesktopViewController to enable menu options in its view
         * -- note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
         * -- at which stage its appropriate to re-enable the View menu on the Desktop View Controller's view
         */
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
        this.getMyController().actionPerformed(actionEvent);
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
        //getDescriptor().setViewDescription(getView().getViewDescriptor().getViewDescription());
        ViewController.AppointmentScheduleViewControllerActionEvent actionCommand =
               ViewController.AppointmentScheduleViewControllerActionEvent.valueOf(e.getActionCommand());
        switch (actionCommand){
            case APPOINTMENTS_CANCELLED_VIEW_REQUEST:
                doAppointmentsCancelledViewRequest();
                break;
            case VIEW_CLOSE_NOTIFICATION:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case VIEW_ACTIVATED_NOTIFICATION:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString());
                //this.actionPerformed(actionEvent);
                 this.getMyController().actionPerformed(actionEvent);
                 break;
            case VIEW_CHANGED_NOTIFICATION:
                 actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                 this.getMyController().actionPerformed(actionEvent);
                 break;
            case APPOINTMENT_CANCEL_REQUEST:
                doAppointmentCancelRequest();
                
                mergeScheduleSlotsIfPossible(getAppointmentScheduleDay());
                doAppointmentForDayRequest(getAppointmentScheduleDay());
                
                firePropertyChangeEvent(
                        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENTS_FOR_DAY_RECEIVED.toString(),
                        getView(),//event target/listener
                        this,//event sender
                        null,
                        getDescriptor()//event related data        
                );
                
                if (getDescriptor().getControllerDescription().getEmptySlotFromDay()!=null){
                    firePropertyChangeEvent(
                           ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                   NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                           this.getView(),//event target/listener
                           this,//event sender
                           null,
                           getDescriptor()//event related data        
                    );
                }
                
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                );
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST:
                doUnbookableAppointmentSlotEditorViewRequest();
                break;
            case APPOINTMENT_CREATE_VIEW_REQUEST:
                getDescriptor().getControllerDescription().
                    setViewMode(ViewController.ViewMode.CREATE);
                doAppointmentCreateViewRequest();
                break;
            case APPOINTMENT_UPDATE_VIEW_REQUEST:
                getDescriptor().getControllerDescription().
                    setViewMode(ViewController.ViewMode.UPDATE);
                doAppointmentUpdateViewRequest();
                break;
            case APPOINTMENTS_FOR_DAY_REQUEST:
                //?setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
                //getDescriptor().setViewDescription(((Descriptor)(((View)e.getSource()).getViewDescriptor())).getViewDescription());
                //?setAppointmentScheduleDay(getDescriptorFromView().getViewDescription().getScheduleDay());
                setAppointmentScheduleDay(getDescriptor().getViewDescription().getScheduleDay());
                getDescriptor().getControllerDescription().
                        setAppointmentScheduleDay(getDescriptor().
                                getViewDescription().getScheduleDay());
                doAppointmentForDayRequest(getDescriptor().
                        getControllerDescription().getAppointmentScheduleDay());
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        getDescriptor()
                );
                break;
            /*
            case MODAL_VIEWER_ACTIVATED://notification from view uts shutting down
                doModalViewerActivated();
                break;
            */
            case NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST:
                doNonSurgeryDayScheduleViewRequest();
                break;
            case SURGERY_DAYS_EDITOR_VIEW_REQUEST:
                doSurgeryDayScheduleViewRequest();
                break;
            case EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST: 
                doEmptySlotScannerDialogRequest(e);
                break;     
        }
    }
    
    /**
     * redirects ActionEvents sent from secondary views that have been launched by the primary view
 -- secondary views are identified by the ActionEvent::Source property, defined in the View::ViewGype enum ; thus
 ---- APPOINTMENT_EDITOR_VIEW
 ---- EMPTY_SLOT_SCAN_CONFIGURATION_VIEW
 ---- NON_SURGERY_DAY_EDITOR_VIEW
 ---- SURGERY_DAY_EDITOR_VIEW
     * @param e 
     */
    
    private void doSecondaryViewActionRequest(ActionEvent e){
        setModalView((ModalView)e.getSource());
        //?setEntityDescriptorFromView(this.view2.getViewDescriptor());
        //getDescriptor().setViewDescription(this.view2.getViewDescriptor().getViewDescription());
        switch(this.getModalView().getMyViewType()){
            case APPOINTMENTS_CANCELLED_VIEW:
                doCancelledAppointmentsViewAction(e);
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW:
                doUnbookableAppointmentSlotEditorAction(e);
                //resetEmptySlotScannerSettings();
                break;
            case APPOINTMENT_EDITOR_VIEW:
                doAppointmentCreatorEditorViewAction(e);
                //resetEmptySlotScannerSettings();
                break;
            case EMPTY_SLOT_SCAN_CONFIGURATION_VIEW:
                doEmptySlotScanConfigurationViewAction(e);
                break;
            case NON_SURGERY_DAY_EDITOR_VIEW:
                doNonSurgeryDayScheduleEditorViewAction(e);
                resetEmptySlotScannerSettings();
                break;
            case SURGERY_DAY_EDITOR_VIEW:
                doSurgeryDaysEditorViewAction(e);
                resetEmptySlotScannerSettings();
                break;
        }
    }
    
    private void doNonSurgeryDayScheduleEditorViewAction(ActionEvent e){
        /**
         * Following is execution strategy
         * -- fetch the calling view's entity descriptor
         * -- close down the calling view
         * -- construct a new ActionEvent
         * ---- source = this.view (AppointmentsForDayView
         * ---- property = APPOINTMENTS_FOR_DAY_REQUEST
         * -- recursively call AppointmentViewController::actionPerformed() method
         * The latter call simulates the event raised when the date is updated on the AppointmentsForDayView object
         */
        if (e.getActionCommand().equals(
                ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENTS_FOR_NON_SURGERY_DAY_REQUEST.toString())){
            //?setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
            //getDescriptor().setViewDescription(((Descriptor)(((View)e.getSource()).getViewDescriptor())).getViewDescription());
            try{
                getModalView().setClosed(true);
            }
            catch (PropertyVetoException ex){
                String message = ex.getMessage() + "\n";
                message = message + "Error when closing down the NON_SURGERY_DAY_SCHEDULE_EDITOR view in AppointmentViewController::doSurgeryDaysEditorModalViewer()";
                displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
            }
            
            firePropertyChangeEvent(
                    AppointmentScheduleViewControllerPropertyChangeEvent.
                            NON_SURGERY_DAY_EDIT_RECEIVED.toString(),
                    getSecondaryView(), //listener
                    this, //source
                    null,
                    null      
            );
        }
        else if (e.getActionCommand().equals(
                ViewController.AppointmentScheduleViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString())){
            /**
             * DISABLE_CONTROLS_REQUEST requests DesktopViewController to disable menu options in its view
             */
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString());
            this.actionPerformed(actionEvent);     
        }
    }
    private void doSurgeryDaysEditorViewAction(ActionEvent e){
        ViewController.AppointmentScheduleViewControllerActionEvent actionCommand =
                ViewController.AppointmentScheduleViewControllerActionEvent.valueOf(e.getActionCommand());
            
        switch(actionCommand){
            case SURGERY_DAYS_EDIT_REQUEST:{
                //getDescriptor().setViewDescription(((Descriptor)(((View)e.getSource()).getViewDescriptor())).getViewDescription());
                HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = 
                        getDescriptor().getViewDescription().getSurgeryDaysAssignmentValue();
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
                    getDescriptor().getControllerDescription().setSurgeryDaysAssignment(new SurgeryDaysAssignment().read().get());
                    firePropertyChangeEvent(
                            ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.SURGERY_DAYS_ASSIGNMENT_RECEIVED.toString(),
                            getSecondaryView(),
                            this,
                            null,
                            getDescriptor()
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
                ViewController.AppointmentScheduleViewControllerActionEvent.SURGERY_DAYS_EDIT_REQUEST.toString())){
            //?setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
            //getDescriptor().setViewDescription(((Descriptor)(((View)e.getSource()).getViewDescriptor())).getViewDescription());
            HashMap<DayOfWeek,Boolean> surgeryDaysAssignmentValue = 
                    //?getDescriptorFromView().getViewDescription().getSurgeryDaysAssignmentValue();
                    getDescriptor().getViewDescription().getSurgeryDaysAssignmentValue();
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
                getDescriptor().getControllerDescription().setSurgeryDaysAssignment(new SurgeryDaysAssignment().read().get());
                
                /**
                 * fire event over to APPOINTMENT_SCHEDULE
                 */
                firePropertyChangeEvent(
                        AppointmentScheduleViewControllerPropertyChangeEvent.
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
                ViewController.AppointmentScheduleViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString())){
            //this.view2.initialiseView();
            /**
             * passes message to DesktopView Controller to disable the VIEW control
             */
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_ACTIVATED.toString());
            this.actionPerformed(actionEvent); 
            
        }
    }
    
    private void doAppointmentSlotsFromDayRequest(){
        try{
            getModalView().setClosed(true);
            /**
             * the modal JinternalFrame has closed
             */

        }
        catch (PropertyVetoException ex){

        }
        //initialiseNewEntityDescriptor();
        //LocalDate day = getDescriptorFromView().getViewDescription().getScheduleDay();
        //getNewEntityDescriptor().getControllerDescription().setEmptySlotFromDay(
        //Duration duration = getDescriptorFromView().getViewDescription().getDuration();
        //getNewEntityDescriptor().getControllerDescription().setEmptySlotMinimumDuration(
                
        ArrayList<Appointment> appointments = null;
        Appointment appointment = null;
        try{
            appointment = new Appointment();
            appointment.setScope(Scope.FROM_DAY);
            //appointment.setStart(day.atStartOfDay());
            appointment.setStart(getDescriptor().getControllerDescription().
                    getEmptySlotFromDay().atStartOfDay());
            appointment.setDuration(getDescriptor().getControllerDescription().getEmptySlotMinimumDuration());
            appointment.read();
            if (appointment.get().isEmpty()){
                JOptionPane.showMessageDialog(null, "No scheduled appointments from selected scan date (" + 
                        getDescriptor().getControllerDescription().getEmptySlotFromDay().format(dmyFormat) + ")");
            }
            else{
                ArrayList<Appointment> availableSlotsOfDuration = 
                        getAvailableSlotsFromDayAndDuration(appointment.get());
                getDescriptor().getControllerDescription().setAppointmentSlots(availableSlotsOfDuration);
                getDescriptor().getControllerDescription().setAppointment(appointment);
                this.firePropertyChangeEvent(
                        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        getDescriptor()
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
        ViewController.AppointmentScheduleViewControllerActionEvent actionCommand =
               ViewController.AppointmentScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case EMPTY_SLOTS_FROM_DAY_REQUEST:
                getDescriptor().getControllerDescription().setEmptySlotFromDay(
                        //?getDescriptorFromView().getViewDescription().getScheduleDay());
getDescriptor().getViewDescription().getScheduleDay());
                getDescriptor().getControllerDescription().setEmptySlotMinimumDuration(
                        //?
                        getDescriptor().getViewDescription().getDuration());
                doAppointmentSlotsFromDayRequest();
                break;
        }
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

    private void sendErrorToAppointmentCreatorEditorView(){
        /**
         * fire event over to APPOINTMENT_CREATOR_EDITOR_VIEW
         */
        firePropertyChangeEvent(
                AppointmentScheduleViewControllerPropertyChangeEvent.
                        APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString(),
                getSecondaryView(),
                this,
                null,
                null
        );
    }
    
    private void doCancelledAppointmentsViewAction(ActionEvent e){
        ViewController.AppointmentScheduleViewControllerActionEvent actionCommand =
               ViewController.AppointmentScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case APPOINTMENT_UNCANCEL_REQUEST:{
                //?setEntityDescriptorFromView((Descriptor)(
                //getDescriptor().setViewDescription((Descriptor.ViewDescription)(
                        //((View)e.getSource()).getViewDescriptor().getViewDescription()));
                //?
                Appointment appointment = getDescriptor().getViewDescription().getAppointment();
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
                        getDescriptor().getControllerDescription().setPatient(patient);
                        LocalDate day = appointment.getStart().toLocalDate();

                        appointment.uncancel();
                        appointment.setScope(Scope.CANCELLED);
                        appointment.read();
                        getDescriptor().getControllerDescription().
                                setAppointmentCancellations(appointment.get());
                        if (getDescriptor().getControllerDescription().getEmptySlotFromDay()!=null){
                            getDescriptor().getControllerDescription().setEmptySlotFromDay(null);
                            getDescriptor().getControllerDescription().setEmptySlotMinimumDuration(null);
                            this.firePropertyChangeEvent(
                                    ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                            NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                                    getView(),
                                    this,
                                    null,
                                    getDescriptor()
                            );
                        }
                        this.firePropertyChangeEvent(
                                ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                        APPOINTMENTS_CANCELLED_RECEIVED.toString(),
                                (View)e.getSource(),
                                this,
                                null,
                                getDescriptor()
                            );
                        if(appointment.getStart().toLocalDate().equals(
                                getDescriptor().getControllerDescription().
                                        getAppointmentScheduleDay())){
                            appointment = new Appointment();
                            appointment.setStart(getAppointmentScheduleDay().atStartOfDay());
                            appointment.setScope(Scope.FOR_DAY);
                            appointment.read();

                            getDescriptor().getControllerDescription().setAppointment(appointment);
                            getDescriptor().getControllerDescription().setAppointments(appointment.get());
                            doAppointeeReminderCount(appointment.get());
                            getUpdatedAppointmentSlotsForDay(appointment);
                
                            mergeScheduleSlotsIfPossible(getAppointmentScheduleDay());
                            doAppointmentForDayRequest(getAppointmentScheduleDay());
                            
                            firePropertyChangeEvent(
                                   ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                           APPOINTMENTS_FOR_DAY_RECEIVED.toString(),
                                   this.getView(),//event target/listener
                                   this,//event sender
                                   null,
                                   getDescriptor()//event related data        
                           );   
                        }
                        firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
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
                getDescriptor().getControllerDescription().getAppointment();   
        changedSlotRequest.setPatient(new Patient(1));
        LocalDate day = changedSlotRequest.getStart().toLocalDate();
        ViewController.AppointmentScheduleViewControllerActionEvent actionCommand =
               ViewController.AppointmentScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        setScheduleReport(new ScheduleReport());
        switch (actionCommand){
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST:
                result = doAppointmentCreateRequest(e, changedSlotRequest);
                break;
            case UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST:
                result = doAppointmentUpdateRequest(e, changedSlotRequest);
                break;
        }
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
            getDescriptor().getControllerDescription().setPatient(result.getPatient());

            firePropertyChangeEvent(
                    ViewController.DesktopViewControllerPropertyChangeEvent.
                            APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                    (DesktopViewController)getMyController(),
                    this,
                    null,
                    getDescriptor()
            );
        }
        else {
            sendErrorToAppointmentCreatorEditorView();
            //02/12/2022
            /**
             * update forces a refresh of the appt schedule for the day to its original state prior to the erroneous create/update attempt
             * -- necessary because repaint which occurs on schedule can repaint the incorrect new appt
             * -- ditto for the update case below
             */
            doAppointmentForDayRequest(day);
        }
    }
    
    /**
     * handles both CREATE and UPDATE actions in the secondary view, ModalAppointmentCreateEditorView; thus
     * -- APPOINTMENT_SCHEDULE_CREATE_REQUEST & APPOINTMENT_EDITOR_UPDATE_REQUEST
 ---- on receipt of ActionEvent the secondary view is closed
 ---- the requested change is validated for correctness
 ------ if correct the current appointment schedule is updated
 ------ and a APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION property change event fired to this ciew controller's controller (DesktopViewController) 
 ------ if the change is not correct an error message is fired off to the secondary view
 ------ and the appointment schedule refreshed
 ------ this shouldn't be necessary but changes made to objects in the secondary view copy though to the primary view independently of the program code (?)
     * @param e, ActionEvent 
     */
    private void doAppointmentCreatorEditorViewAction(ActionEvent e){
        Appointment result;
        Appointment changedSlotRequest = 
                //?getDescriptorFromView().getControllerDescription().getAppointment();
                getDescriptor().getControllerDescription().getAppointment();
                
        LocalDate day = changedSlotRequest.getStart().toLocalDate();
        ViewController.AppointmentScheduleViewControllerActionEvent actionCommand =
               ViewController.AppointmentScheduleViewControllerActionEvent.valueOf(e.getActionCommand());        
        switch (actionCommand){
            case APPOINTMENT_EDITOR_CREATE_REQUEST:
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
                    getDescriptor().getControllerDescription().setPatient(result.getPatient());
                    
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else {
                    sendErrorToAppointmentCreatorEditorView();
                    //02/12/2022
                    /**
                     * update forces a refresh of the appt schedule for the day to its original state prior to the erroneous create/update attempt
                     * -- necessary because repaint which occurs on schedule can repaint the incorrect new appt
                     * -- ditto for the update case below
                     */
                    doAppointmentForDayRequest(day);
                }
                break;
            case APPOINTMENT_EDITOR_UPDATE_REQUEST:
                day = 
                        //?getDescriptorFromView().getViewDescription().getAppointment().
                        getDescriptor().getViewDescription().getAppointment().
                                getStart().toLocalDate();
                result = doAppointmentUpdateRequest(e, changedSlotRequest);
                if (result!=null) {
                    try{
                        getModalView().setClosed(true);
                    }
                    catch (PropertyVetoException ex){
                    }
                    mergeScheduleSlotsIfPossible(day);
                    doAppointmentForDayRequest(day);
                    getDescriptor().getControllerDescription().setPatient(result.getPatient());
                    
                    
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else {
                    //2/12/2022
                    sendErrorToAppointmentCreatorEditorView();
                    doAppointmentForDayRequest(day); 
                }
                break;
            
        } 
    }

    private void doDesktopViewControllerAction(ActionEvent e){
        ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case REFRESH_DISPLAY_REQUEST:
                doAppointmentForDayRequest(getDescriptor().getControllerDescription().getAppointmentScheduleDay());
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                try{
                    /**
                     * sent by Desktop VC prior to closing this controller
                     */
                    getView().setClosed(true);
                }
                catch (PropertyVetoException ex){
                    //UnspecifiedError action
                }
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
    
    /*
    private ScheduleReport doAppointmentCollisionCheckOnScheduleChangeRequest(
            Appointment requestedSlot,
            ArrayList<Appointment> appointments, ViewMode mode){
        setScheduleReport(new ScheduleReport());
        //scheduleReport.setState(RequestedAppointmentState.UNDEFINED);
        Iterator<Appointment> appointmentsForDay = appointments.iterator();
        while (appointmentsForDay.hasNext()){
            Appointment nextScheduledSlot = appointmentsForDay.next();
            switch(mode){
                case CREATE:
                    switch(getScheduleReport().getState()){
                        
                    //2/12/2022
                    case SLOT_START_OK:

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
*/
    private ArrayList<Appointment> getAvailableSlotsFromDayAndDuration(
            ArrayList<Appointment> appointments){
    //private ArrayList<Appointment> getAvailableSlotsFromDayAndDuration(
            //ArrayList<Appointment> appointments, Duration duration, LocalDate searchStartDay){
        Duration duration = getDescriptor().getControllerDescription().getEmptySlotMinimumDuration();
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

        currentDate = getDescriptor().getControllerDescription().getEmptySlotFromDay();;
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
                else {
                    Appointment emptySlot = createEmptyAppointmentSlot(nextEmptySlotStartTime,
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
        if (getIsBookedStatus(lastAppointment)){
            //15/07/2023 enables an appointment to run over LAST_APPOINTMENT_SLOT time
            /*
            Duration durationToDayEnd = 
                    Duration.between(nextEmptySlotStartTime.toLocalTime(), ViewController.LAST_APPOINTMENT_SLOT).abs();
            if (!durationToDayEnd.isZero()) {
                Appointment emptySlot = createEmptyAppointmentSlot(nextEmptySlotStartTime);
                apptsForDayIncludingEmptySlots.add(emptySlot);
            }
            */
 
            Duration durationToDayEnd = 
                    Duration.between(nextEmptySlotStartTime.toLocalTime(), ViewController.LAST_APPOINTMENT_SLOT);
            if (!durationToDayEnd.isNegative()){
                if (!durationToDayEnd.isZero()) {
                    Appointment emptySlot = createEmptyAppointmentSlot(nextEmptySlotStartTime);
                    apptsForDayIncludingEmptySlots.add(emptySlot);
                }
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
        getDescriptor().getControllerDescription().setAppointmentSlotsForDay(appointmentSlotsForDay); 
    }
    
    private Boolean getIsBookedStatus(Appointment appointment){
        if (appointment.getPatient()==null) return false;
        if(!appointment.getPatient().getIsKeyDefined())return false;
        return true;
    } 
    
    /*
    public class ScheduleReport{
        private String error = null;
        private RequestedAppointmentState state = null;
        
        private String getError(){
            return error;
        }
        
        private void setError(String value){
            error = value;
        }
        
        private RequestedAppointmentState getState(){
            return state;
        }
        
        private void setState(RequestedAppointmentState value){
            state = value;
        }
    }
    */
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
            getDescriptor().getControllerDescription().
                    setAppointeeRemindersCountForDay(appointeeRemindedCount);
        }
        else getDescriptor().getControllerDescription().
                setAppointeeRemindersCountForDay(new Point(0,0));
    }
 
    private void resetEmptySlotScannerSettings(){
        firePropertyChangeEvent(
                ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED.toString(),
                getView(),
                this,
                null,
                getDescriptor()
        );
        getDescriptor().getControllerDescription().setEmptySlotFromDay(null);
        getDescriptor().getControllerDescription().setEmptySlotMinimumDuration(null);
    }
    
    private void setSecondaryView(View value){
        secondaryView = value;
    }
    
    private View getSecondaryView(){
        return secondaryView;
    }
}
