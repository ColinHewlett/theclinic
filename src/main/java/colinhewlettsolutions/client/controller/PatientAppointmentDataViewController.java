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
        PATIENT_ARCHIVE_REQUEST,
        PATIENT_RECALL_ACTIVITY_STATUS_CHANGE,
        VIEW_CLOSE_NOTIFICATION,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        //secondary view action requests
        PROCESS_PENDING_REQUEST,
        PROCESS_START_REQUEST,
        PROCESS_STOP_REQUEST,
        MODAL_VIEW_CLOSE_NOTIFICATION 
    }
    
    public enum Properties{
        PATIENT_APPOINTMENT_DATA_RECEIVED,
        PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PROCESS_CHANGE_NOTIFICATION,
        PROCESS_ENDED_NOTIFICATION,
        PROCESS_UPDATE_NOTIFICATION
    }
    
    public enum ViewMode{
        PROCESS_ENDED,
        PROCESS_PENDING,
        PROCESS_STARTED,
        PROCESS_STOPPED
    }
    
    public PatientAppointmentDataViewController(
            DesktopViewController controller,
            DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerActionEvent actionCommand = ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
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
        Actions actionCommand =
               Actions.valueOf(e.getActionCommand());
        View the_view = (View)e.getSource();
        switch (the_view.getMyViewType()){
            case MODAL_PROGRESS_VIEW ->{
                switch(actionCommand){
                    case MODAL_VIEW_CLOSE_NOTIFICATION ->{
                        break;
                    }
                    case PROCESS_PENDING_REQUEST ->{
                        
                        firePropertyChangeEvent(
                                Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
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
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE,ViewMode.PROCESS_STARTED);
                        firePropertyChangeEvent(
                                Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
                                getModalView(),
                                this,
                                null,
                                null
                        );
                        startProcessTask(pad);
                        break;
                    }
                    case PROCESS_STOP_REQUEST ->{
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE,ViewMode.PROCESS_STOPPED);
                        firePropertyChangeEvent(
                                Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
                                getModalView(),
                                this,
                                null,
                                null
                        );

                        /*
                        try{
                            pad = (PatientAppointmentData)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                            pad = pad.read();
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                            firePropertyChangeEvent(
                                    ViewController.PatientAppointmentDataViewControllerPropertyChangeEvent.
                                            PATIENT_APPOINTMENT_DATA_RECEIVED.toString(),
                                    getView(),
                                    this,
                                    null,
                                    null
                            );
                        }catch(StoreException ex){
                            String message = ex.getMessage() + "\n"
                                    + "Handled in PatientAppointmentDataViewController::doSecondaryViewActionRequest(PROCESS_STOP_REQUEST)";
                            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                        }
                        break;*/
                    }
                    /**
                     * Following case is actioned on 2 property change events received in ModalProgressView
                     * -- [1] on receipt of PROCESS_CHANGE_NOTIFICATION with a view mode defined as PROCESS_STOPPED
                     * -- [2] on receipt of PROCESS_CHANGE_NOTIFICATION with a view mode defines as PROCESS_ENDED
                     * In either case the PatientAppointmentData table is refreshed 
                     */
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
                    default ->{
                    JOptionPane.showMessageDialog(getView(), 
                            "Unrecognised view type specified in PatientAppointmentDataViewController::doSecondaryViewActionRequest()",
                            "View Controller Error", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
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
                        ViewMode viewMode = (ViewMode)getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.VIEW_MODE);
                        if (viewMode.equals(ViewMode.PROCESS_STOPPED)) break;
                        Patient patient = _pad.getPatient();
                        patient.setIsArchived(true);
                        patient.update();
                        setItemCount(getItemCount()+1);
                        getDescriptor().getControllerDescription().
                                setProperty(SystemDefinition.Properties.ITEM_COUNTER, new Point(getItemCount(), getItemTotal()));
                        firePropertyChangeEvent(
                                Properties.PROCESS_UPDATE_NOTIFICATION.toString(),
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
                                setProperty(SystemDefinition.Properties.VIEW_MODE, ViewMode.PROCESS_ENDED);
                firePropertyChangeEvent(
                        Properties.PROCESS_CHANGE_NOTIFICATION.toString(),
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
                        setProperty(SystemDefinition.Properties.VIEW_MODE, ViewMode.PROCESS_PENDING);
                setModalView((ModalView)new View().make(View.Viewer.MODAL_PROGRESS_VIEW,
                        this, this.getDesktopView()).getModalView());
                /**
                try{
                    for(PatientAppointmentData _pad : pad.get()){
                        patient = _pad.getPatient();
                        patient.setIsArchived(true);
                        patient.update();
                    }
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                try{
                    pad.setFromYear((
                            (PatientAppointmentData)getDescriptor()
                                    .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getFromYear());
                    pad.setToYear((
                            (PatientAppointmentData)getDescriptor()
                                    .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getToYear());
                    pad.setScope((
                            (PatientAppointmentData)getDescriptor()
                                    .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getScope());
                    getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                    fetchAndSendViewPatientAppointmentData();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE,ViewMode.PATIENT_ARCHIVE);
                firePropertyChangeEvent(
                    ViewController.DesktopViewControllerPropertyChangeEvent.
                            PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                    getMyController(),
                    this,
                    null,
                    getDescriptor()
                );
                System.out.println("3 " + String.valueOf((Boolean)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_REQUIRED)));
                System.out.println("3 desktopView " + String.valueOf((Boolean)getDesktopView().getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_REQUIRED)));
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

            case VIEW_CLOSE_NOTIFICATION ->{
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            }
            case VIEW_ACTIVATED_NOTIFICATION ->{
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString());
                 this.getMyController().actionPerformed(actionEvent);
                 break;
            }
            case VIEW_CHANGED_NOTIFICATION ->{
                 actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
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
            case PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION ->{
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
                ViewController.PatientAppointmentDataViewControllerPropertyChangeEvent.
                        PATIENT_APPOINTMENT_DATA_RECEIVED.toString(),
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
