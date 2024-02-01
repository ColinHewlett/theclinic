/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

//import controller.DesktopViewController.DesktopViewControllerActionEvent;
import model.Entity;
import model.Patient;
import model.Appointment;
import model.PatientNote;
import model.Entity.Scope;
import view.views.non_modal_views.DesktopView;
import view.View;
import view.views.modal_views.ModalView;
import view.views.modal_views.ModalPatientNotesEditorView;
import repository.StoreException;//01/03/2023
import static controller.ViewController.displayErrorMessage;
import java.beans.PropertyChangeSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author colin
 */
public class PatientViewController extends ViewController {
    private PropertyChangeSupport pcSupportForView = null;
    private PropertyChangeEvent pcEvent = null;
    private String message = null;
    private PatientSelectionMode patientSelectionMode = null;
    private Patient currentlySelectedPatient = null;
    
    
    private void setCurrentlySelectedPatient(Patient value){
        if (!value.getIsKeyDefined())
        {
            getDescriptor().getControllerDescription()
                    .setViewMode(ViewMode.CREATE);
        }
        else {
            getDescriptor().getControllerDescription()
                    .setViewMode(ViewMode.UPDATE);
        }
        getDescriptor().getControllerDescription()
                .setPatient(value);
    }
    
    private Patient getCurrentlySelectedPatient(){
        return getDescriptor().getControllerDescription().getPatient();
    }
    
    private PatientSelectionMode getPatientSelectionMode(){
        return patientSelectionMode;
    }
    
    private void setPatientSelectionMode(PatientSelectionMode value){
        patientSelectionMode = value;
    }

    private enum PatientSelectionMode{ PATIENT_SELECTION, PATIENT_RECOVERY};

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
                this.getMyController().actionPerformed(actionEvent);
                break;
            case VIEW_CHANGED_NOTIFICATION:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case SCHEDULE_VIEW_CONTROLLER_REQUEST: //on selection of row in appointment history table
                doScheduleViewControllerRequest();
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
            case PATIENT_RECALL_EDITOR_VIEW_REQUEST:
            case PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST:
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST:
                doPatientEditorViewRequest(actionCommand);
                break;
            case PATIENT_NOTES_EDITOR_VIEW_REQUEST:
                if (getCurrentlySelectedPatient().getIsKeyDefined())
                    doPatientNotesEditorViewRequest();
                else{
                    JOptionPane.showMessageDialog(getView(), 
                        "A patient has not been selected; notes editor request aborted",
                        "Patient View Controller Error", 
                        JOptionPane.WARNING_MESSAGE);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                PATIENT_EDITOR_VIEW_CLOSED.toString(),
                            getView(),
                            this,
                            null,
                            null
                    );
                }
                break;
            case RECOVER_PATIENT_REQUEST:
                doPatientRecoverRequest();
                break;
            case PATIENT_RECOVER_REQUEST:
                //06/12/2023 19:02
                //private void doPatientRequest(ActionEvent e){
                setPatientSelectionMode(PatientSelectionMode.PATIENT_RECOVERY);
                doPatientRecoverySelectionRequest(e);
                setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                break;
            case PATIENT_UPDATE_REQUEST:
                doPatientUpdateRequest();
                break;
            case PATIENT_REQUEST:
                //06/12/2023 19:02
                //doPatientRequest(e);
                setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                doPatientRequest();
                break;
            case DELETED_PATIENT_REQUEST:
                doDeletedPatientRequest();
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
                    getDescriptor().getControllerDescription().setPatients(patient.get());
                    View.setViewer(View.Viewer.PATIENT_SELECTION_VIEW);
                    //this.view2 = View.factory(this, getDescriptor(), this.desktopView);
                    setView((ModalView)new View().make(
                            View.Viewer.PATIENT_SELECTION_VIEW,
                            this,
                            this.getDesktopView()).getModalView());
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
                        //06/12/2023 19:02
                        //doPatientRequest(e);
                        doPatientRequest();
                        break;
                    /*
                    case PATIENT_RECOVER_REQUEST:
                        doPatientRecoverRequest(e);
                        break;
                    */
                    case NULL_PATIENT_REQUEST:
                        doNullPatientRequest();
                        break;
                }
                break;
            case NOTIFICATION_EDITOR_VIEW:
                //do nothing
                break;
            case PATIENT_RECALL_EDITOR_VIEW:
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW:
            case PATIENT_GUARDIAN_EDITOR_VIEW:    
                doPatientEditorViewChange(the_view);
                break;  
            case PATIENT_NOTES_EDITOR_VIEW:
                doPatientNotesEditorViewChange(e);
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
    private void doScheduleViewControllerRequest(){  
        //setEntityDescriptorFromView(view.getViewDescriptor());
        getDescriptor().getControllerDescription().setScheduleDay(
                getDescriptor().getViewDescription().getScheduleDay());
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.SCHEDULE_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
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
        this.getMyController().actionPerformed(actionEvent); 
    }
    
    private void doPatientPhoneEmailEditorViewRequest(){
        setModalView((ModalView)new View().make(
                    View.Viewer.PATIENT_PHONE_EMAIL_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_EDITOR_VIEW_CLOSED.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
    }
    
    private void doPatientGuardianEditorViewRequest(){
        LocalDate dob = getCurrentlySelectedPatient().getDOB();
        if (dob!=null){
            if (Period.between(dob, LocalDate.now()).getYears() > 17){
                JOptionPane.showMessageDialog(
                        getView(), 
                        "The selected patient is at least 18; hence search for guardian details aborted",
                        "Patient View Controller Error",
                        JOptionPane.WARNING_MESSAGE); 
                firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                PATIENT_EDITOR_VIEW_CLOSED.toString(),
                            getView(),
                            this,
                            null,
                            null
                    );
            }else {
                /**
                 * 31/01/24
                 * presumed ControllerDescription::patient is already initialised
                 */
                try{
                    Patient patient = new Patient();
                    patient.setScope(Scope.ALL);
                    patient.read();
                    getDescriptor()
                            .getControllerDescription()
                            .setPatients(patient.get());
                    setModalView((ModalView)new View().make(
                                View.Viewer.PATIENT_GUARDIAN_EDITOR_VIEW,
                                this, 
                                this.getDesktopView()).getModalView());
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                PATIENT_EDITOR_VIEW_CLOSED.toString(),
                            getView(),
                            this,
                            null,
                            null
                    );
                }catch(StoreException ex){

                }
            }
        }else{
            JOptionPane.showMessageDialog(
                    getView(), 
                    "the patient's date of birth needs to be defined",
                    null,
                    JOptionPane.WARNING_MESSAGE);
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_EDITOR_VIEW_CLOSED.toString(),
                    getView(),
                    this,
                    null,
                    null
            );
        }          
    }
    
    private void doPatientEditorViewRequest(
            ViewController.PatientViewControllerActionEvent actionCommand){
        switch(actionCommand){
            case PATIENT_RECALL_EDITOR_VIEW_REQUEST:
                doPatientRecallEditorViewRequest();
                break;
            case PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST:
                doPatientGuardianEditorViewRequest();
                break;
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST:
                doPatientPhoneEmailEditorViewRequest();
                break;
        }
    }
    
    private void doPatientNotesEditorViewRequest(){
        PatientNote patientNote = new PatientNote(getCurrentlySelectedPatient());
        patientNote.setScope(Scope.FOR_PATIENT);
        try{
            patientNote.read();
            getDescriptor()
                    .getControllerDescription()
                    .setPatientNotes(patientNote.get());
            getDescriptor()
                    .getControllerDescription()
                    .setPatient(patientNote.getPatient());

            setModalView((ModalView)new View().make(
                        View.Viewer.PATIENT_NOTES_EDITOR_VIEW,
                        this, 
                        this.getDesktopView()).getModalView());
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_EDITOR_VIEW_CLOSED.toString(),
                    getView(),
                    this,
                    null,
                    null
            );
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Patient view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    private void doPatientRecallEditorViewRequest(){
        setModalView((ModalView)new View().make(
                    View.Viewer.PATIENT_RECALL_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        
        firePropertyChangeEvent(
                ViewController.PatientViewControllerPropertyChangeEvent.
                    PATIENT_EDITOR_VIEW_CLOSED.toString(),
                getView(),
                this,
                null,
                null
        );
    }
    
    private void doPatientCreateRequest(){
        //setEntityDescriptorFromView(view.getViewDescriptor());
        Patient patient = getDescriptor().getViewDescription().getPatient();
        if (!patient.getIsKeyDefined()){
            try{
                patient.insert();
                patient.setScope(Scope.SINGLE);
                patient = patient.read();
                patient.setScope(Entity.Scope.ALL);
                patient.read();
                getDescriptor().getControllerDescription().setPatients(patient.get());
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENTS_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                getDescriptor().getControllerDescription().setPatient(patient);
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        null
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
    
    /**
     * primary view has requested a list of deleted patients on the system
     * -- method initialises the Descriptor with the deleted patients on the system
     * -- primary view is sent a PATIENTS_RECEIVED property change event
     * @param e 
     */
    private void doPatientRecoverySelectionRequest(ActionEvent e){
        Patient patient = null;
        ArrayList<Patient> patients = null;
        try{
            patient = new Patient();
            patient.setScope(Scope.DELETED);
            patient = patient.read();
            getDescriptor().getControllerDescription().setPatients(patient.get());
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENTS_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    null
            );
        }
        catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"Patient View Controller error",JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    /*
    private void doPatientRecoverySelectionRequest(ActionEvent e){
        //setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
        Patient patient = null;
        ArrayList<Patient> patients = null;
        try{
            patient = new Patient();
            patient.setScope(Scope.DELETED);
            patient = patient.read();
            getDescriptor().getControllerDescription().setPatients(patient.get());
            View.setViewer(View.Viewer.PATIENT_RECOVERY_SELECTION_VIEW);
            setView((ModalView)new View().make(
                    View.Viewer.PATIENT_RECOVERY_SELECTION_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        }
        catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"AppointmentViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    */
    
    private void doPatientDeleteRequest(){
        //setEntityDescriptorFromView(view.getViewDescriptor()); 
        Patient patient = getDescriptor().getViewDescription().getPatient();
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
        //setEntityDescriptorFromView(view.getViewDescriptor()); 
        Patient patient = getDescriptor().getViewDescription().getPatient();
        if (patient.getIsKeyDefined()){
            try{
                patient.update();
                patient.setScope(Scope.SINGLE);
                patient.read();
                
                patient.setScope(Entity.Scope.ALL);
                patient.read();
                getDescriptor().getControllerDescription().setPatients(patient.get());
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENTS_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        getDescriptor()
                );
                //getDescriptor().getControllerDescription().setPatient(patient);
                setCurrentlySelectedPatient(patient);
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        getDescriptor()
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
    private void doPatientRecoverRequest(){
        String errorLog = "";
        Patient patient = getDescriptor().getViewDescription().getPatient();
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
                //patient.setScope(Scope.DELETED);
                //patient.recover();
                if (collisionFromAppointmentRecovery) {
                    getDescriptor().getControllerDescription().setError(errorLog);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                    PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED.toString(),
                            getView(),
                            this,
                            null,
                            getDescriptor()
                    );
                    doNullPatientRequest();
                }
                else{
                    //recover deleted patient
                    patient.setScope(Scope.DELETED);
                    patient.recover();
                    //fetch the all the undeleted patients on the system for the view
                    patient.setScope(Scope.ALL);
                    patient.read();
                    getDescriptor().getControllerDescription().setPatients(patient.get());
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                    PATIENTS_RECEIVED.toString(),
                            getView(),
                            this,
                            null,
                            getDescriptor()     
                    );
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            null     
                    );
                    //fetch the recovered patient for the view
                    patient.setScope(Scope.SINGLE);
                    patient.read();
                    getDescriptor().getControllerDescription().setPatient(patient);
                    firePropertyChangeEvent(
                       ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        getDescriptor()
                    );
                }    
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() +"\n"
                        + "Exception raised in PatientViewController::doPatientRecoverRequest()",
                        "Patient view controller error", JOptionPane.WARNING_MESSAGE);           
            }
        }
    }

    private void doDeletedPatientRequest(){
        Patient requestedDeletedPatient = null;
        Patient patient = getDescriptor().getViewDescription().getPatient();
        if (patient.getIsKeyDefined()){
            try{
                patient.setScope(Scope.DELETED);
                Patient the_patient = patient.read();
                for(var p : the_patient.get()){
                    if (p.equals(patient)){
                        requestedDeletedPatient = p;
                        break;
                    }
                }
                if (requestedDeletedPatient!=null){
                    getDescriptor().getControllerDescription().setPatient(requestedDeletedPatient);
                    firePropertyChangeEvent(
                           ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_RECEIVED.toString(),
                            getView(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
                else{
                    displayErrorMessage(
                        "Could not find selected deleted patient in repository)",
                        "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
                }
                
            }catch (StoreException ex){
                displayErrorMessage(ex.getMessage() + "\n"
                        + "Exception raised in PatientViewController::doPatientRequest(ActionEvent)",
                        "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void doPatientNotesEditorViewChange(ActionEvent e){
        PatientNote patientNote = getDescriptor().getViewDescription().getPatientNote();
        try{
            switch(getDescriptor().getViewDescription().getViewMode()){
                case CREATE:
                    patientNote.insert();
                    break;
                case UPDATE:
                    patientNote.update();
                    break;
            }
            patientNote.setScope(Scope.FOR_PATIENT);
            PatientNote thePatientNote = new PatientNote(getCurrentlySelectedPatient());
            patientNote.read();
            getDescriptor().getControllerDescription().setPatientNotes(patientNote.get());
            firePropertyChangeEvent(
                       ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_NOTES_RECEIVED.toString(),
                        (ModalPatientNotesEditorView)e.getSource(),
                        this,
                        null,
                        getDescriptor()
                );
            
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage() + "\n"
                    + "Raised in PatientViewController::doPatientNotesEditorViewChange()",
                    "Patient view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * a change made on a secondary view editor
     * @param secondaryView 
     */
    private void doPatientEditorViewChange(View secondaryView){
        Patient patient = getDescriptor().getViewDescription().getPatient();
        try{
            if (getDescriptor().getControllerDescription()
                    .getViewMode().equals(ViewController.ViewMode.UPDATE)){
            patient.update();
            patient.setScope(Scope.SINGLE);
            patient.read();
            setCurrentlySelectedPatient(patient);
            }
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage() + "\n"
                        + "Exception raised in PatientViewController::doPatientEditorViewChange(View secondaryView)",
                        "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
        }
    }
    
    //06/12/2023 19:02
    //private void doPatientRequest(ActionEvent e){
    private void doPatientRequest(){
        //setEntityDescriptorFromView(((View)e.getSource()).getViewDescriptor());
        Patient patient = null;
        if (getPatientSelectionMode().equals(PatientSelectionMode.PATIENT_SELECTION))
            patient = getDescriptor().getViewDescription().getPatient();
            
        else
            patient = getDescriptor().getControllerDescription().getPatient();
        //
        if (patient.getIsKeyDefined()){
            try{
                patient.setScope(Scope.SINGLE);
                Patient p = patient.read();
                setCurrentlySelectedPatient(p);
                getDescriptor().getControllerDescription().setPatient(p);
                firePropertyChangeEvent(
                       ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        getDescriptor()
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
        //initialiseNewEntityDescriptor();
        setCurrentlySelectedPatient(new Patient());
        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        try{
            patient.read();
            getDescriptor().getControllerDescription().setPatient(patient);
            getDescriptor().getControllerDescription().setPatients(patient.get()); 
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                                NULL_PATIENT_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    null
            );
        }catch(StoreException ex){
            String error = ex.getMessage() +"\n"
                    + "Raised in Patient view controller doNullPatientRequest().";
            displayErrorMessage(error, "Patient view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public PatientViewController(ActionListener controller, 
            DesktopView desktopView)throws StoreException{
        setDesktopView(desktopView);
        setMyController(controller);

        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        patient.read();
        getDescriptor().getControllerDescription().setPatients(patient.get());
        View.setViewer(View.Viewer.PATIENT_VIEW);
        setCurrentlySelectedPatient(new Patient());
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
                        if (getDescriptor().getControllerDescription().getPatient()!=null){
                            if (getDescriptor().getControllerDescription().getPatient().getIsKeyDefined()){
                                if (patient.equals(getDescriptor().getControllerDescription().getPatient())){    
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
}
