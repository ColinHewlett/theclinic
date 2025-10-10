/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

/**
 *
 * @author colin
 */
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.view.views.modal_views.ModalProgressView;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.entity.PatientAppointmentData;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import javax.swing.SwingWorker;


public class ArchivedPatientsViewController extends ViewController {

    public enum Actions{
        //primary view action rtequests
        ARCHIVED_PATIENTS_REQUEST,
        PATIENT_RESTORE_REQUEST,
        VIEW_CLOSE_NOTIFICATION,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        MODAL_VIEW_CLOSE_NOTIFICATION 
    }
    
    public enum Properties{
        ARCHIVED_PATIENTS_RECEIVED,
        VIEW_CHANGE_NOTIFICATION,
        //PROCESS_CHANGE_NOTIFICATION,
        //PROCESS_ENDED_NOTIFICATION,
        //PROCESS_UPDATE_NOTIFICATION
    }
    
    public enum ViewMode{
        //PATIENT_RESTORE,
        //PROCESS_ENDED,
        //PROCESS_PENDING,
        //PROCESS_STARTED,
        //PROCESS_STOPPED
    }
    
    public ArchivedPatientsViewController(
            DesktopViewController controller,
            Descriptor descriptor,
            DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView); 
        setDescriptor(descriptor);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Patient patient = null;
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
        }else{//ARCHIVED_PATIENTS_VIEW
            View the_view = (View)e.getSource();
            switch (the_view.getMyViewType()){
                case ARCHIVED_PATIENTS_VIEW:
                    doPrimaryViewActionRequest(e);
                    break;
                default:
                    doSecondaryViewActionRequest(e);
                    break;
            }
        }
            
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ArchivedPatientsViewControllerPropertyChangeEvent event = 
                ArchivedPatientsViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(event){
            case VIEW_CHANGE_NOTIFICATION ->{
                try{
                    fetchAndSendArchivedPatients();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::propertyChange(" + event + ")";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
        }
    }

    private void doPrimaryViewActionRequest(ActionEvent e){
        ActionEvent actionEvent = null;
        PatientAppointmentData pad = null;
        Actions actionCommand = Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            
            case ARCHIVED_PATIENTS_REQUEST ->{
                try{
                    fetchAndSendArchivedPatients();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }          
                break;
            }

            case PATIENT_RESTORE_REQUEST ->{
                pad = (PatientAppointmentData)getDescriptor().getViewDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                setItemTotal(pad.get().size());
                setItemCount(0);
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.
                        Properties.ITEM_COUNTER, new Point(getItemCount(), getItemTotal()));
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA,pad);
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.VIEW_MODE, ModalProgressView.ViewMode.PROCESS_PENDING);
                
                /**
                 * disable call to model process view
                 
                setModalView((ModalView)new View().make(View.Viewer.MODAL_PROGRESS_VIEW,
                        this, this.getDesktopView()).getModalView());*/
                
                try{
                    for(PatientAppointmentData _pad : pad.get()){
                        Patient patient = _pad.getPatient();
                        patient.setIsArchived(false);
                        patient.update();
                    }
                    fetchAndSendArchivedPatients();
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::doPrimaryViewActionRequest( " + actionCommand.toString() + " )";
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

    private void doSecondaryViewActionRequest(ActionEvent e){
        Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
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
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ITEM_COUNTER, new Point(0, patient .get().size()));
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
                        startProcessTask(patient);
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
                     * In either case the ArchivedPatientView table is refreshed 
                     */
                    case REFRESH_DATA_REQUEST ->{
                        try{
                            fetchAndSendArchivedPatients();
                        }catch(StoreException ex){
                            String message = ex.getMessage() +"\n";
                            message = message + "Exception handled in "
                                    + this.getClass().getSimpleName() + "::doSecondaryViewActionRequest)" + actionCommand + "(";
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
        Patient patient = (Patient)object;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {  
                try{
                    for(Patient _patient : patient.get()){
                        
                        ModalProgressView.ViewMode viewMode = (ModalProgressView.ViewMode)getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.VIEW_MODE);
                        if (viewMode.equals(ModalProgressView.ViewMode.PROCESS_STOPPED)) break;
                        _patient.setIsArchived(false);
                        _patient.update();
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
    
    private void fetchAndSendArchivedPatients()throws StoreException{
        PatientAppointmentData pad = new PatientAppointmentData();
        pad.setScope(Entity.Scope.ARCHIVED);
        pad = pad.read();
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
        firePropertyChangeEvent(
                Properties.ARCHIVED_PATIENTS_RECEIVED.toString(),
                getView(),
                this,
                null,
                null
        );
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
    
    private void doInitialiseView() throws StoreException{
        /**
         * -- initialise a PatientAppointmentData object and send to view (currently disabled)
         */

    }
}
