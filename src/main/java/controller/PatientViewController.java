/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.controller;

//import clinicpms.controller.DesktopViewController.DesktopViewControllerActionEvent;
import clinicpms.model.Entity;
import clinicpms.model.Patient;
import clinicpms.model.Appointment;
import clinicpms.model.Entity.Scope;
import clinicpms.view.views.DesktopView;
import clinicpms.view.View;
import clinicpms.view.views.interfaces.IView;
import clinicpms.repository.StoreException;//01/03/2023
import static clinicpms.controller.ViewController.displayErrorMessage;
import java.beans.PropertyChangeSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author colin
 */
public class PatientViewController extends ViewController {
    private ActionListener myController = null;
    private PropertyChangeSupport pcSupportForView = null;
    //private PropertyChangeSupport pcSupportForPatientSelector = null;
    private PropertyChangeEvent pcEvent = null;
    private View view = null;
    private View view2 = null;
    DesktopView desktopView = null;
    private Descriptor oldEntityDescriptor = new Descriptor();
    private String message = null;

    private Descriptor getOldEntityDescriptor(){
        return this.oldEntityDescriptor;
    }
    private void setOldEntityDescriptor(Descriptor e){
        this.oldEntityDescriptor = e;
    }

    private void doPrimaryViewActionRequest(ActionEvent e){
        ActionEvent actionEvent = null;
        ViewController.PatientViewControllerActionEvent actionCommand =
               ViewController.PatientViewControllerActionEvent.valueOf(e.getActionCommand());
        switch (actionCommand){
            case VIEW_ACTIVATED_NOTIFICATION:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString());
                this.myController.actionPerformed(actionEvent);
                break;
            case VIEW_CHANGED_NOTIFICATION:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                this.myController.actionPerformed(actionEvent);
                break;
            case APPOINTMENT_VIEW_CONTROLLER_REQUEST: //on selection of row in appointment history table
                doAppointmentViewControllerRequest();
                break;
            case VIEW_CLOSE_NOTIFICATION://notification from view uts shutting down
                doPatientViewClosed();
                break;
            case PATIENT_CREATE_REQUEST:
                doPatientCreateRequest();
                break;
            case PATIENT_DELETE_REQUEST:
                doPatientDeleteRequest();
                break;
            case PATIENT_RECOVER_REQUEST:
                doPatientRecoverySelectionRequest(e);
                break;
            case PATIENT_UPDATE_REQUEST:
                doPatientUpdateRequest();
                break;
            case PATIENT_REQUEST:
                doPatientRequest(e);
                break;
            case NULL_PATIENT_REQUEST:
                doNullPatientRequest();
                break; 
            case PATIENT_SELECTION_VIEW_REQUEST:
                Patient patient = null;
                ArrayList<Patient> patients = null;
                try{
                    patient = new Patient();
                    patient.setScope(Scope.ALL);
                    patient.read();
                    getControllerDescriptor().getControllerDescription().setPatients(patient.get());
                    View.setViewer(View.Viewer.PATIENT_SELECTION_VIEW);
                    this.view2 = View.factory(this, getControllerDescriptor(), this.desktopView);
                }
                catch (StoreException ex){
                    String message = ex.getMessage();
                    displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
                }
                break;
        }
    }
    
    private void doSecondaryViewActionRequest(ActionEvent e){
        ActionEvent actionEvent = null;
        ViewController.PatientViewControllerActionEvent actionCommand =
               ViewController.PatientViewControllerActionEvent.valueOf(e.getActionCommand());
        View the_view = (View)e.getSource();
        switch (the_view.getMyViewType()){
            case PATIENT_SELECTION_VIEW:
            case PATIENT_RECOVERY_SELECTION_VIEW:
                switch (actionCommand){
                    case PATIENT_REQUEST:
                        doPatientRequest(e);
                        break;
                    case PATIENT_RECOVER_REQUEST:
                        doPatientRecoverRequest(e);
                        break;
                    case NULL_PATIENT_REQUEST:
                        doNullPatientRequest();
                        break;
                }
                break;
            case PATIENT_NOTIFICATION_EDITOR_VIEW:
                //do nothing
                break;
            default:
                JOptionPane.showMessageDialog(getView(), 
                        "Unrecognised view type specified in PatientViewController::doSecondaryViewActionRequest()",
                        "Patient View Controller Error", 
                        JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doDesktopViewControllerActionRequest(ActionEvent e){
        ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch (actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{//prelude to the Desktop VC closing down the Patient VC
                try{
                    getView().setClosed(true);   
                }catch (PropertyVetoException ex){
                //UnspecifiedError action
                }
                
                break;
            }
        }
    }  
    
    /**
     * appointment in Patient view's appointment history has been selected to request the appointment schedule for that day 
 -- ViewDescription is forwarded onto the Desktop VC
 -- the forwarded request references the Patient VC's EntityDescriptorFromView which contains details of the selected appointment for this patient; and thus the appointment schedule requested
     */
    private void doAppointmentViewControllerRequest(){  
        setEntityDescriptorFromView(view.getViewDescriptor());
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.APPOINTMENT_VIEW_CONTROLLER_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    /**
     * notification from view it is closing down
     * -- let DesktopVC know so it can close down the Patient VC
     */
    private void doPatientViewClosed(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.DesktopViewControllerActionEvent.
                    VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
        getMyController().actionPerformed(actionEvent); 
    }
    
    private void doPatientCreateRequest(){
        setEntityDescriptorFromView(view.getViewDescriptor());
        Patient patient = getDescriptorFromView().getViewDescription().getPatient();
        if (!patient.getIsKeyDefined()){
            try{
                patient.insert();
                patient.setScope(Scope.SINGLE);
                patient = patient.read();
                patient.setScope(Entity.Scope.ALL);
                patient.read();
                getControllerDescriptor().getControllerDescription().setPatients(patient.get());
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENTS_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()
                );
                getControllerDescriptor().getControllerDescription().setPatient(patient);
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()
                );
                
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() + "\n"
                        + "Exception raised in PatientViewController.doThePatientViewCreateRequest()",
                        "Patient view controller error",JOptionPane.WARNING_MESSAGE);
            }
        }else{
            displayErrorMessage("StoreException -> Key defined in new patient to be created; "
                    + "new patient create operation aborted", "Patient view controller", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doPatientRecoverySelectionRequest(ActionEvent e){
        setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
        Patient patient = null;
        ArrayList<Patient> patients = null;
        try{
            patient = new Patient();
            patient.setScope(Scope.DELETED);
            patient = patient.read();
            getDescriptorFromView().getControllerDescription().setPatients(patient.get());
            View.setViewer(View.Viewer.PATIENT_RECOVERY_SELECTION_VIEW);
            this.view2 = View.factory(this, getControllerDescriptor(), this.desktopView);
        }
        catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doPatientDeleteRequest(){
        setEntityDescriptorFromView(view.getViewDescriptor()); 
        Patient patient = getDescriptorFromView().getViewDescription().getPatient();
        if (patient.getIsKeyDefined()){
            try{
                patient.setScope(Scope.SINGLE);
                patient.delete();
                doNullPatientRequest();
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        null     
                );
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() +"\n"
                        + "Exception raised in PatientViewController::doPatientDeleteRequest()",
                        "Patient view controller error", JOptionPane.WARNING_MESSAGE);           
            }
        }else{
            int test = 10/0;
            displayErrorMessage("Requested patient for deletion has no key defined, delete action aborted",
                    "Patient view controller error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doPatientUpdateRequest(){
        setEntityDescriptorFromView(view.getViewDescriptor()); 
        Patient patient = getDescriptorFromView().getViewDescription().getPatient();
        if (patient.getIsKeyDefined()){
            try{
                patient.update();
                patient.setScope(Scope.SINGLE);
                patient.read();
                patient.setScope(Entity.Scope.ALL);
                patient.read();
                getControllerDescriptor().getControllerDescription().setPatients(patient.get());
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENTS_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()
                );
                getControllerDescriptor().getControllerDescription().setPatient(patient);
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()
                );
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() +"\n"
                        + "Exception raised in PatientViewController::doPatientViewUpdateRequest()",
                        "Patient view controller error", JOptionPane.WARNING_MESSAGE);           
            }
        }else{
            displayErrorMessage("Requested patient for update has no key defined, update action aborted",
                    "Patient view controller error", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    
    
    /**
     * Controller is responsible for checking potential collisions of recovered appointments when a patient is recovered
     * The rules are
     * -- an appointment can only be deleted via the Patient.delete operation
     * -- an appointment can be cancelled only via the Appointment.cancel() operation
     * ---- ie there is no Appointment.delete operation
     * Hence Controller must do the following when a patient recovery is requested
     * -- fetch the appointment history for the patient
     * ---- via the Appointment.read() operation with a scope of DELETED_FOR_PATIENY
     * -- the appointment collection is then iterated through
     * ---- 
     * ---- if a collision arises during the check the controller should cancel the appointment
     * -- then and only then can 
     * @param e 
     */
    private void doPatientRecoverRequest(ActionEvent e){
        String errorLog = "";
        setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor()); 
        Patient patient = getDescriptorFromView().getViewDescription().getPatient();
        
        try{
            ((View)e.getSource()).setClosed(true);
        }
        catch (PropertyVetoException ex){
            String message = ex.getMessage() + "\n";
            message = message + "Error when closing down the NON_SURGERY_DAY_SCHEDULE_EDITOR view in AppointmentViewController::doSurgeryDaysEditorModalViewer()";
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
        if (patient.getIsKeyDefined()){
            try{
                ArrayList<Appointment> deletedAppointments =
                        patient.getDeletedAppointmentHistory();
                setScheduleReport(new ScheduleReport());
                boolean collisionFromAppointmentRecovery = false;
                for(var a : deletedAppointments){
                    if (!a.getIsCancelled()){
                        a.setPatient(patient);
                        Appointment appointment = super.doChangeAppointmentScheduleForDayRequest(
                                ViewMode.NO_ACTION, a);
                        if (appointment == null){//assume a collision has arisen and update appt. cancel status
                            collisionFromAppointmentRecovery = true;
                            a.cancel();
                            LocalDate day = a.getStart().toLocalDate();
                            LocalTime fromTime = a.getStart().toLocalTime();
                            LocalTime toTime = fromTime.plusMinutes(a.getDuration().toMinutes());
                            String date = day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            errorLog = errorLog + getScheduleReport().getError();
                            errorLog = errorLog + "\nHence appointment on " + date +
                                    " for " + a.getPatient().toString()+
                                    " from " + fromTime.format(DateTimeFormatter.ofPattern("HH:mm")) +
                                    " to " + toTime.format(DateTimeFormatter.ofPattern("HH:mm")) +
                                    " has been cancelled";            
                        }
                    }
                }  
                patient.setScope(Scope.DELETED);
                patient.recover();
                if (collisionFromAppointmentRecovery) {
                    getControllerDescriptor().getControllerDescription().setError(errorLog);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                    PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED.toString(),
                            getView(),
                            this,
                            null,
                            getControllerDescriptor()
                    );
                }
                //fetch the recovered patient for the view
                patient.setScope(Scope.SINGLE);
                patient.read();
                getControllerDescriptor().getControllerDescription().setPatient(patient);
                firePropertyChangeEvent(
                       ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()
                );
                //fetch the all the undeleted patients on the system for the view
                patient.setScope(Scope.ALL);
                patient.read();
                getControllerDescriptor().getControllerDescription().setPatients(patient.get());
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                                PATIENTS_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()     
                );
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        null     
                );
                
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() +"\n"
                        + "Exception raised in PatientViewController::doPatientRecoverRequest()",
                        "Patient view controller error", JOptionPane.WARNING_MESSAGE);           
            }
        }
    }

    private void doPatientRequest(ActionEvent e){
        setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
        Patient patient = getDescriptorFromView().getViewDescription().getPatient();
        if (patient.getIsKeyDefined()){
            try{
                patient.setScope(Scope.SINGLE);
                Patient p = patient.read();
                getControllerDescriptor().getControllerDescription().setPatient(p);
                firePropertyChangeEvent(
                       ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_RECEIVED.toString(),
                        view,
                        this,
                        null,
                        getControllerDescriptor()
                );
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() + "\n"
                        + "Exception raised in PatientViewController::doPatientRequest(ActionEvent)",
                        "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }else{
            displayErrorMessage("No key defined for requested patient; fetch operation aborted",
                    "Patient view controller error", JOptionPane.WARNING_MESSAGE);
        }
        int test = 0;
    }
    
    private void doNullPatientRequest(){
        initialiseNewEntityDescriptor();
        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        try{
            patient.read();
            getControllerDescriptor().getControllerDescription().setPatient(patient);
            getControllerDescriptor().getControllerDescription().setPatients(patient.get()); 
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                                NULL_PATIENT_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    getControllerDescriptor()
            );
        }catch(StoreException ex){
            String error = ex.getMessage() +"\n"
                    + "Raised in Patient view controller doNullPatientRequest().";
            displayErrorMessage(error, "Patient view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * update old entity descriptor with previous new entity descriptor 
     * re-initialise the new entity descriptor, but copy over the old selected day
     */
    private void initialiseNewEntityDescriptor(){
        setOldEntityDescriptor(getControllerDescriptor());
        setNewEntityDescriptor(new Descriptor());
        getControllerDescriptor().getViewDescription().setDay(getOldEntityDescriptor().getViewDescription().getDay());
    }
    private ActionListener getMyController(){
        return this.myController;
    }
    private void setMyController(ActionListener myController){
        this.myController = myController;
    }
    
    public PatientViewController(DesktopViewController controller, DesktopView desktopView)throws StoreException{
        this.desktopView = desktopView;
        setMyController(controller);
        pcSupportForView = new PropertyChangeSupport(this);
        setNewEntityDescriptor(new Descriptor());
        this.oldEntityDescriptor = new Descriptor();
        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        patient.read();
        getControllerDescriptor().getControllerDescription().setPatients(patient.get());
        View.setViewer(View.Viewer.PATIENT_VIEW);
        this.view = View.factory(this, getControllerDescriptor(), desktopView);
        super.centreViewOnDesktop(desktopView, view);
        
        this.view.addInternalFrameListeners(); 
        
        view.initialiseView();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.PatientViewControllerPropertyChangeEvent propertyName = 
                ViewController.PatientViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case PATIENT_VIEW_CHANGE_NOTIFICATION:{
                try{
                    Descriptor descriptor = (Descriptor)e.getNewValue();
                    //Patient patient = entityDescriptor.getAppointment().getPatient();
                    Patient patient = descriptor.getControllerDescription().getPatient();
                    if (patient != null){
                        if (getControllerDescriptor().getControllerDescription().getPatient()!=null){
                            if (getControllerDescriptor().getControllerDescription().getPatient().getIsKeyDefined()){
                                if (patient.equals(getControllerDescriptor().getControllerDescription().getPatient())){    
                                        patient.setScope(Scope.SINGLE);
                                        descriptor.getControllerDescription().setPatient(patient.read());
                                        firePropertyChangeEvent(
                                                ViewController.PatientViewControllerPropertyChangeEvent.
                                                        PATIENT_RECEIVED.toString(),
                                                getView(),
                                                this,
                                                null,
                                                descriptor
                                        );
                                }
                            }
                        }
                    }
                }catch(StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nRaised in propertyChange() method",
                            "Patient view controller error", JOptionPane.WARNING_MESSAGE);
                }
                break; 
            }         
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //PropertyChangeListener[] pcls;
        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerActionRequest(e);
        }
        else{
            View the_view = (View)e.getSource();
            switch (the_view.getMyViewType()){
                case PATIENT_VIEW:
                    doPrimaryViewActionRequest(e);
                    break;
                default:
                    doSecondaryViewActionRequest(e);
                    break;
            }
        }
        
    }
    
    public View getView( ){
        return view;
    }
    
}
