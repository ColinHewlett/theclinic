/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import colinhewlettsolutions.client.view.views.modal_views.ModalProgressView;
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.PatientAppointmentData;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.view.View;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.time.LocalDate;


/**
 *
 * @author colin
 */
public class PatientAppointmentDataViewController extends ViewController{
     
    public enum Actions{
        //primary view action rtequests
        PATIENT_APPOINTMENT_DATA_REQUEST,
        PATIENT_APPOINTMENT_DATA_SORT_REQUEST,
        PATIENT_ARCHIVE_REQUEST,
        PATIENT_RECALL_ACTIVITY_STATUS_CHANGE,
        PATIENT_RECALL_VIEW_REQUEST,
        SET_TIME_FRAME_REQUEST,
        VIEW_CLOSE_NOTIFICATION,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION
    }
    
    public enum Properties{
        ARCHIVED_PATIENT_RECEIVED,
        PATIENT_APPOINTMENT_DATA_RECEIVED,
        PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        VIEW_CHANGE_NOTIFICATION
    }
    
    public enum ViewMode{
        PROCESS_ENDED,
        PROCESS_PENDING,
        PROCESS_STARTED,
        PROCESS_STOPPED
    }
    
    public PatientAppointmentDataViewController(
            DesktopViewController controller,
            Descriptor descriptor,
            DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView); 
        setDescriptor(descriptor);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() instanceof DesktopViewController){
            DesktopViewController.Actions actionCommand = DesktopViewController.Actions.valueOf(e.getActionCommand());
            switch (actionCommand){
                case INITIALISE_VIEW_CONTROLLER:
                    try{
                        doInitialiseView();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "Handled in PatientAppointmentDataViewController::actionPerformed(INITIALISE_VIEW)";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }
        }
        else{
            View the_view = (View)e.getSource();
            switch (the_view.getMyViewType()){
                case PATIENT_APPOINTMENT_DATA_VIEW:
                    doPrimaryViewActionRequest(e);
                    break;
                default:
                    doSecondaryViewActionRequest(e);
                    break;
            }
        }
    } 

    private void doSecondaryViewActionRequest(ActionEvent e){
        PatientAppointmentData pad = (PatientAppointmentData)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
        ActionEvent actionEvent = null;
        ModalProgressView.Actions actionCommand =
               ModalProgressView.Actions.valueOf(e.getActionCommand());
        View the_view = (View)e.getSource();
        switch (the_view.getMyViewType()){
            case MODAL_PROGRESS_VIEW ->{
                switch(actionCommand){
                    case PROCESS_PENDING_REQUEST ->{
                        
                        firePropertyChangeEvent(
                                ModalProgressView.Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
                                getModalView(),
                                this,
                                null,
                                null
                        );
                        break;
                    }
                    case PROCESS_START_REQUEST ->{
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ITEM_COUNTER, new Point(0, pad .get().size()));
                        setItemCount(((Point)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.ITEM_COUNTER)).x);
                        setItemTotal(((Point)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.ITEM_COUNTER)).y);
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE,ModalProgressView.ViewMode.PROCESS_STARTED);
                        firePropertyChangeEvent(
                                ModalProgressView.Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
                                getModalView(),
                                this,
                                null,
                                null
                        );
                        startProcessTask(pad);
                        break;
                    }
                    case PROCESS_STOP_REQUEST ->{
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE,ModalProgressView.ViewMode.PROCESS_STOPPED);
                        firePropertyChangeEvent(
                                ModalProgressView.Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
                                getModalView(),
                                this,
                                null,
                                null
                        );
                    }
                    /**
                     * Following case is actioned on 2 property change events received in ModalProgressView
                     * -- [1] on receipt of PROCESS_CHANGE_NOTIFICATION with a view mode defined as PROCESS_STOPPED
                     * -- [2] on receipt of PROCESS_CHANGE_NOTIFICATION with a view mode defines as PROCESS_ENDED
                     * In either case the PatientAppointmentData table is refreshed 
                     */
                    case REFRESH_DATA_REQUEST ->{
                        try{
                            fetchAndSendViewPatientAppointmentData();
                        }catch(StoreException ex){
                            String message = ex.getMessage() +"\n";
                            message = message + "Exception handled in "
                                    + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                        }          
                        break;
                    }
                    default ->{
                    JOptionPane.showMessageDialog(getView(), 
                            "Unrecognised view type specified in PatientAppointmentDataViewController::doSecondaryViewActionRequest()",
                            "View Controller Error", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
                break;
            }
            
        }
    }
            
    private void startProcessTask(Object object){
        Patient patient = null;
        PatientAppointmentData pad = (PatientAppointmentData)object;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {  
                try{
                    for(PatientAppointmentData _pad : pad.get()){
                        ModalProgressView.ViewMode viewMode = (ModalProgressView.ViewMode)getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.VIEW_MODE);
                        if (viewMode.equals(ModalProgressView.ViewMode.PROCESS_STOPPED)) break;
                        Patient patient = _pad.getPatient();
                        patient.setIsArchived(true);
                        patient.update();
                        setItemCount(getItemCount()+1);
                        getDescriptor().getControllerDescription().
                                setProperty(SystemDefinition.Properties.ITEM_COUNTER, new Point(getItemCount(), getItemTotal()));
                        firePropertyChangeEvent(
                                ModalProgressView.Properties.PROCESS_UPDATE_NOTIFICATION.toString(),
                                getModalView(),
                                this,
                                null,
                                null
                        );
                    }
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::startProcessTask(" + object.getClass().getSimpleName() + ")";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                return null;
            }  
            @Override
            protected void done(){
                getDescriptor().getControllerDescription().
                                setProperty(SystemDefinition.Properties.VIEW_MODE, ModalProgressView.ViewMode.PROCESS_ENDED);
                firePropertyChangeEvent(
                        ModalProgressView.Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
                        getModalView(),
                        this,
                        null,
                        null
                );
            }
        }; 
        worker.execute();
    }
        
    private void doPrimaryViewActionRequest(ActionEvent e){
        Integer patientsToArchiveTotal = null;
        Integer patientsArchivedSoFar = null;
        ActionEvent actionEvent = null;
        Patient patient = null;
        PatientAppointmentData pad = null;
        Actions actionCommand =
                Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case PATIENT_APPOINTMENT_DATA_REQUEST ->{
                try{
                    fetchAndSendViewPatientAppointmentData();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }          
                break;
            }
            case PATIENT_ARCHIVE_REQUEST ->{
                pad = (PatientAppointmentData)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                patientsToArchiveTotal = pad.get().size();
                patientsArchivedSoFar = 0;
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.
                        Properties.ITEM_COUNTER, new Point(patientsArchivedSoFar, patientsToArchiveTotal));
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.VIEW_MODE, ModalProgressView.ViewMode.PROCESS_PENDING);
                
                try{
                    for(PatientAppointmentData _pad : pad.get()){
                        
                        /**
                         * disable Properties.VIEW_MODE update
                         
                        ModalProgressView.ViewMode viewMode = (ModalProgressView.ViewMode)getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.VIEW_MODE);*/
                        
                        patient = _pad.getPatient();
                        patient.setIsArchived(true);
                        patient.update();
                    }
                    
                    /**
                     * refresh PAD table in view
                     */
                    fetchAndSendViewPatientAppointmentData();
                    
                    /**
                     * let the controller know a view change has occurred
                     */
                    actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            DesktopViewController.Actions.
                                    VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                     this.getMyController().actionPerformed(actionEvent);
                    
                    
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::actionPerfprmed( " + actionCommand + " )";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                
                /**
                 * disable ModalProgressView
                 
                setModalView((ModalView)new View().make(View.Viewer.MODAL_PROGRESS_VIEW,
                        this, this.getDesktopView()).getModalView());
                */
                
                
                break;
            }
            case PATIENT_RECALL_ACTIVITY_STATUS_CHANGE ->{
                //patient = (Patient)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT);
                pad = (PatientAppointmentData)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                try{
                    pad.getPatient().update();
                    fetchAndSendViewPatientAppointmentData();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case PATIENT_RECALL_VIEW_REQUEST ->{
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.
                            PATIENT_RECALL_VIEW_CONTROLLER_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            }
            case VIEW_CLOSE_NOTIFICATION ->{
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            }
            case VIEW_ACTIVATED_NOTIFICATION ->{
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString());
                 this.getMyController().actionPerformed(actionEvent);
                 break;
            }
            case VIEW_CHANGED_NOTIFICATION ->{
                 actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                 this.getMyController().actionPerformed(actionEvent);
                 break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        PatientAppointmentDataViewController.Properties property = 
                PatientAppointmentDataViewController.Properties.valueOf(e.getPropertyName());
        switch (property){
            case VIEW_CHANGE_NOTIFICATION ->{
                try{
                    fetchAndSendViewPatientAppointmentData();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::properetyChange(" + property + ")";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
        }
    }
    
    private Integer itemCount = null;
    private Integer getItemCount(){
        return itemCount;
    }
    private void setItemCount(Integer value){
        itemCount = value;
    }
    
    private Integer itemTotal = null;
    private Integer getItemTotal(){
        return itemTotal;
    }
    private void setItemTotal(Integer value){
        itemTotal = value;
    }
    
    private void fetchAndSendViewPatientAppointmentData()throws StoreException{
        /**
         * [1] on entry ViewDescription::PatientAppointmentData (pad) is initialsed; thus
         * -- pad.FromYear
         * -- pad.ToYear
         * -- pad.Scope
         * [2] these values are copied over to the new PatientAppointmentData (_pad) object after its been read from store
         * [3] the new PatientAppointmentData (_pad) is then saved to the ControllerDescription 
         * -- thus the view knows the values (from/to year and sort order(scope)) used to produce the PatientAutomationData collection 
         */
        PatientAppointmentData pad = null;
        PatientAppointmentData _pad = null;
        pad = (PatientAppointmentData)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
        _pad = pad.read();
        if (_pad==null) _pad = new PatientAppointmentData();
        _pad.setFromYear(pad.getFromYear());
        _pad.setToYear(pad.getToYear());
        _pad.setScope(pad.getScope());
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, _pad);
        firePropertyChangeEvent(
                Properties.PATIENT_APPOINTMENT_DATA_RECEIVED.toString(),
                getView(),
                this,
                null,
                null
        );
    }
    
    private void doInitialiseView() throws StoreException{
        /**
         * -- initialise a PatientAppointmentData object and send to view (currently disabled)
         */
        PatientAppointmentData pad = new PatientAppointmentData();
        pad.setFromYear(1992);
        pad.setToYear(LocalDate.now().getYear() + 1);
        pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT);
        getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
    }
}
