/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.model.entity.SecondaryCondition;
import colinhewlettsolutions.client.model.entity.PrimaryCondition;
import colinhewlettsolutions.client.model.entity.Condition;
import colinhewlettsolutions.client.model.non_entity.TreatmentWithState;
import colinhewlettsolutions.client.model.entity.Question;
import colinhewlettsolutions.client.model.entity.PatientQuestion;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.Medication;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.entity.Doctor;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.model.entity.ClinicalNote;
import colinhewlettsolutions.client.model.entity.Entity.Scope;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import colinhewlettsolutions.client.model.repository.StoreException;//01/03/2023
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import static colinhewlettsolutions.client.controller.ViewController.PatientViewControllerActionEvent.PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST;
import static colinhewlettsolutions.client.controller.ViewController.PatientViewControllerActionEvent.PATIENT_RECALL_EDITOR_VIEW_REQUEST;
import static colinhewlettsolutions.client.controller.ViewController.PatientViewControllerActionEvent.VIEW_ACTIVATED_NOTIFICATION;
import colinhewlettsolutions.client.view.views.modal_views.ModalDateDialog;
import java.beans.PropertyChangeSupport;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    public enum Actions{
        CLINICAL_NOTE_VIEW_REQUEST,
        DELETED_PATIENT_REQUEST,
        IMAGE_VIEWER_REQUEST,
        NULL_PATIENT_REQUEST,
        PATIENT_ADDITIONAL_NOTES_VIEW_REQUEST,
        PATIENT_CREATE_REQUEST,
        PATIENT_DELETE_REQUEST,
        PATIENT_DOCTOR_CREATE_REQUEST,
        PATIENT_DOCTOR_DELETE_REQUEST,
        PATIENT_DOCTOR_EDITOR_VIEW_REQUEST,
        PATIENT_DOCTOR_UPDATE_REQUEST,
        PATIENT_DOCUMENT_STORE_VIEW_CONTROLLER_REQUEST,
        PATIENT_GBT_RECALL_EDITOR_VIEW_REQUEST,
        PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST,
        PATIENT_INVOICE_VIEW_CONTROLLER_REQUEST,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST,
        PATIENT_MEDICATION_CREATE_REQUEST,
        PATIENT_MEDICATION_DELETE_REQUEST,
        PATIENT_MEDICATION_EDITOR_VIEW_REQUEST,
        PATIENT_MEDICATION_UPDATE_REQUEST,
        PATIENT_NOTES_EDITOR_VIEW_REQUEST,
        PRINT_PATIENT_MEDICAL_HISTORY_REQUEST,
        PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST,
        PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST,
        PATIENT_RECALL_EDITOR_VIEW_REQUEST,
        PATIENT_RECOVER_REQUEST,
        PATIENT_REQUEST,
        PATIENT_SELECTION_VIEW_REQUEST,
        PATIENT_UPDATE_REQUEST,
        RECOVER_PATIENT_REQUEST,
        SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST,
        SETTINGS_TITLED_BORDER_REQUEST,
        TO_DO_VIEW_CONTROLLER_REQUEST,
        UPLOAD_TO_PATIENT_DOCUMENT_STORE_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSED_NOTIFICATION

    }
    
    private void setCurrentlySelectedPatient(Patient value){
        if (!value.getIsKeyDefined())
        {
            /*getDescriptor().getControllerDescription()
                    .setViewMode(ViewMode.CREATE);*/
            getDescriptor().getControllerDescription()
                    .setProperty(Properties.VIEW_MODE, ViewMode.CREATE);
        }
        else {
            /*getDescriptor().getControllerDescription()
                    .setViewMode(ViewMode.UPDATE);*/
            getDescriptor().getControllerDescription()
                    .setProperty(Properties.VIEW_MODE, ViewMode.UPDATE);
        }
        getDescriptor().getControllerDescription()
                .setProperty(Properties.PATIENT, value);
    }
    
    private Patient getCurrentlySelectedPatient(){
        return (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
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
        Actions actionCommand =
               Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case CLINICAL_NOTE_VIEW_REQUEST:
                doClinicalNoteViewRequest();
                break;
            case DELETED_PATIENT_REQUEST:
                doDeletedPatientRequest();
                break;
            case NULL_PATIENT_REQUEST:
                doNullPatientRequest();
                break; 
            case PATIENT_CREATE_REQUEST:
                doPatientCreateRequest();
                break;
            case PATIENT_DELETE_REQUEST:
                doPatientDeleteRequest();
                break;
            case PATIENT_DOCTOR_EDITOR_VIEW_REQUEST:
                doDoctorEditorViewRequest();
                break;
            case PATIENT_DOCUMENT_STORE_VIEW_CONTROLLER_REQUEST:
                doPatientDocumentStoreViewControllerRequest();
                break;
            case PATIENT_INVOICE_VIEW_CONTROLLER_REQUEST:
                doPatientInvoiceViewControllerRequest();
                break;
            case PRINT_PATIENT_MEDICAL_HISTORY_REQUEST:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent
                                .PRINT_PATIENT_MEDICAL_HISTORY_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST:
                doPatientMedicalHistoryViewControllerRequest();
                break;
            case PATIENT_MEDICATION_EDITOR_VIEW_REQUEST:
                doMedicationEditorViewRequest();
                break;
            case PATIENT_NOTES_EDITOR_VIEW_REQUEST:   
                if (getCurrentlySelectedPatient().getIsKeyDefined()){
                    getDescriptor().getControllerDescription()
                            .setProperty(Properties.PATIENT, getCurrentlySelectedPatient());
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent
                            .TREATMENT_VIEW_CONTROLLER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }else{
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
            case PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST:
                doPatientQuestionnaireViewControllerRequest();
                break;
            case PATIENT_RECALL_EDITOR_VIEW_REQUEST:
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST:
            case PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST:
            case PATIENT_GBT_RECALL_EDITOR_VIEW_REQUEST:
                doPatientEditorViewRequest(actionCommand);
                break;
            case PATIENT_RECOVER_REQUEST:
                //06/12/2023 19:02
                //private void doPatientRequest(ActionEvent e){
                setPatientSelectionMode(PatientSelectionMode.PATIENT_RECOVERY);
                doPatientRecoverySelectionRequest(e);
                setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                break;
            case PATIENT_REQUEST:
                //06/12/2023 19:02
                //doPatientRequest(e);
                setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                doPatientRequest();
                break;
            case PATIENT_SELECTION_VIEW_REQUEST:
                Patient patient = null;
                ArrayList<Patient> patients = null;
                try{
                    patient = new Patient();
                    patient.setScope(Scope.ALL);
                    patient.read();
                    getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
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
            case PATIENT_UPDATE_REQUEST:
                doPatientUpdateRequest();
                break;
            case RECOVER_PATIENT_REQUEST:
                doPatientRecoverRequest();
                break;
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST: //on selection of row in appointment history table
                doScheduleViewControllerRequest();
                break;
            case SETTINGS_TITLED_BORDER_REQUEST:
                break;
            case TO_DO_VIEW_CONTROLLER_REQUEST:
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    PatientViewController.Actions.TO_DO_VIEW_CONTROLLER_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case UPLOAD_TO_PATIENT_DOCUMENT_STORE_REQUEST:
                doUploadToPatientDocumentStoreRequest();
                break;
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
            case VIEW_CLOSED_NOTIFICATION://notification from view uts shutting down
                doPatientViewClosed();
                break;   
        }
    }
    
    private void doSecondaryViewActionRequest(ActionEvent e){
        ActionEvent actionEvent = null;
        
        View the_view = (View)e.getSource();
        switch (the_view.getMyViewType()){
            case CLINICAL_NOTE_VIEW:
                doClinicalNoteViewAction(e);
                break;
            case DOCUMENT_STORE_VIEW:
                doModalDocumentStoreViewAction(e);
                break;
            case MODAL_DATE_DIALOG:
                doModalDateDialogAction(e);
                break;
            case NOTIFICATION_EDITOR_VIEW:
                //do nothing
                break;
            case PATIENT_SELECTION_VIEW:
            case PATIENT_RECOVERY_SELECTION_VIEW:
                doPatientRecoverySelectionViewRequest(e);
                break;
            case PATIENT_GBT_RECALL_EDITOR_VIEW:
            case PATIENT_RECALL_EDITOR_VIEW:
            case PATIENT_PHONE_EMAIL_EDITOR_VIEW:
            case PATIENT_GUARDIAN_EDITOR_VIEW:    
                doPatientEditorViewChange(the_view);
                break;  
            case PATIENT_MEDICATION_EDITOR_VIEW:
                doPatientMedicationEditorViewRequest(e);
                break;
            case PATIENT_DOCTOR_EDITOR_VIEW:
                doPatientDoctorEditorViewRequest(e);
                break;
            case NOTE_TAKER:
                doNoteTakerRequest(e);
                break;
            default:
                JOptionPane.showMessageDialog(getView(), 
                        "Unrecognised view type specified in PatientViewController::doSecondaryViewActionRequest()",
                        "Patient View Controller Error", 
                        JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doPatientRecoverySelectionViewRequest(ActionEvent e){
        Actions actionCommand =
               Actions.valueOf(e.getActionCommand());
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
    }
    
    private void doPatientDocumentStoreViewControllerRequest(){
        ViewController.ViewMode viewMode = (ViewController.ViewMode)getDescriptor().getViewDescription().
                getProperty(Properties.VIEW_MODE);
        getDescriptor().getControllerDescription().setProperty(Properties.VIEW_MODE, viewMode);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                PatientViewController.Actions.PATIENT_DOCUMENT_STORE_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doUploadToPatientDocumentStoreRequest(){
        ArrayList<File> document = (ArrayList<File>)getDescriptor().getViewDescription().
                getProperty(Properties.PATIENT_DOCUMENT);
        /*LocalDateTime date = (LocalDateTime)getDescriptor().getViewDescription().
                getProperty(Properties.DATE_TIME);*/
        Patient patient = (Patient)getDescriptor().getControllerDescription().
                getProperty(Properties.PATIENT);
        String patientNameString = patient.getName().getTitle() + " "
                            + patient.getName().getForenames() + " "
                            + patient.getName().getSurname();
        Path documentStoreFolder = (Path)getDescriptor().getControllerDescription().
                    getProperty(Properties.DOCUMENT_STORE);
        documentStoreFolder = documentStoreFolder.resolve(String.valueOf(patient.getKey()));
        if (Files.notExists(documentStoreFolder)){
            try{
                Files.createDirectory(documentStoreFolder);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        ViewController.ViewMode viewMode = (ViewController.ViewMode)getDescriptor().getViewDescription().
                getProperty(Properties.VIEW_MODE);
        
        switch(viewMode){
            case DOCUMENT ->{
                
                    Path source = document.get(0).toPath();
                    Path target = documentStoreFolder.resolve(source.getFileName().toString());
                    try{
                        Files.copy(source, target);
                    }catch(FileAlreadyExistsException ex){
                        System.err.println("File already exists");
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                break;
            }
            case SCAN ->{
                LocalDateTime date = null;
                if (document.size() == 2){
                    while(date==null){
                        getDescriptor().getControllerDescription().setProperty(Properties.CAPTION, 
                                "Date stamp medical history for " + patientNameString);

                        setModalView((ModalView)new View().make(
                            View.Viewer.MODAL_DATE_DIALOG,
                            this, 
                            this.getDesktopView()).getModalView());

                        date = (LocalDateTime)getDescriptor().getViewDescription().
                                getProperty(Properties.DATE_TIME);
                        if (date == null){
                            JOptionPane.showInternalMessageDialog(this.getView(),"a valid date must be entered for the scanned image", "Data error",JOptionPane.INFORMATION_MESSAGE);
                        }
                        if (!date.equals(LocalDateTime.of(1,1,1,1,1))){ //has date dialog exited via use of 'Cancel' button
                            int count = 1;
                            for(File page : document){
                                String newFilename = date.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
                                String dateStampedFilename = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy (hh:MM:ss)"));
                                newFilename = newFilename + "_" + String.valueOf(count++);
                                String oldFileName = page.toPath().toAbsolutePath().getFileName().toString();
                                int dotIndex = oldFileName.lastIndexOf('.');
                                String extension = (dotIndex == -1) ? "" : oldFileName.substring(dotIndex);
                                Path target = documentStoreFolder.resolve(newFilename + extension);
                                try{
                                    Files.copy(page.toPath().toAbsolutePath(), target);
                                    JOptionPane.showInternalMessageDialog(getView(),"Medical history uploaded to " 
                                            + patientNameString + "'s document store with date stamp '" + dateStampedFilename + "'");
                                }catch(FileAlreadyExistsException ex){
                                    System.err.println("File already exists");
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        }else{
                            JOptionPane.showInternalMessageDialog(this.getView(),"Upload of queued medical history files to patient's document store has been cancelled because date stamp missing","Error on upload of " + patientNameString + "'s medical history",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }else{
                    String message = "both pages of the patient's medical history must be queued for upload before the complete medical history can be uploaded";
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, message);
                    this.firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED.toString(), 
                            this.getView(), 
                            this,
                            null,
                            null
                    );
                }
                break;
            }
        }
    }
    
    private void doDesktopViewControllerActionRequest(ActionEvent e){
        /*ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());*/
        DesktopViewController.Actions actionCommand =
               DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{//prelude to the Desktop VC closing down the Patient VC
                /*
                try{
                    getView().setClosed(true);   
                }catch (PropertyVetoException ex){
                //UnspecifiedError action
                }
                */
                doPatientViewClosed();
                break;
            }
            case INITIALISE_VIEW_CONTROLLER:
                Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
                firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent
                            .PATIENT_TO_SELECT_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    null
                );
                break;
        }
    }  
    
    private void doImageViewerRequest(){
        int count = this.getMyController().getDesktopView().getDeskTop().getAllFrames().length;
        getDescriptor().getControllerDescription().setProperty(Properties.PATIENT_DOCUMENT,
                (ArrayList<File>)getDescriptor().getViewDescription().getProperty(Properties.PATIENT_DOCUMENT));
        setView(new View().make(View.Viewer.IMAGE_VIEWER, this, getDesktopView()));
        getMyController().getDesktopView().cascadeInternalFrames(DesktopView.CascadeOrder.TOP_TO_FRONT);
    }
    
    private void doClinicalNoteViewRequest(){
        getDescriptor().getControllerDescription().setProperty(Properties.APPOINTMENT,
                (Appointment)getDescriptor().getViewDescription().getProperty(Properties.APPOINTMENT));
        setModalView((ModalView)new View().make(View.Viewer.CLINICAL_NOTE_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doModalDateDialogAction(ActionEvent e){
        ModalDateDialog.Actions action = ModalDateDialog.Actions.valueOf(e.getActionCommand());
        switch(action){
            case DATE_DIALOG_OK_REQUEST ->{
                break;
            }
        }
    }
    
    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString(); // e.g. "photo.backup.png"
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            // No dot or dot at end → no extension
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }
    
    private String getTargetFileName(String extension){
        LocalDate date = null;
        LocalTime time = null;
        LocalDateTime dateTime = null;
        String dateTimeFileName = null;
        date = (LocalDate)getDescriptor().getViewDescription().getProperty(Properties.DATE_TIME);
        time = LocalTime.now();
        dateTime = date.atTime(time);
        dateTimeFileName = dateTime.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        return dateTimeFileName + "." + extension;
    }
    
    /*
    private void makeFileNameFromDateAndNow(){
        String extension = null;
        String targetFileName = null;
        Patient patient = null;
        Path documentStoreFolder = null;
        ArrayList<File> sourceDocument = null;


        sourceDocument = (ArrayList<File>)getDescriptor().getViewDescription().
                getProperty(Properties.PATIENT_DOCUMENT);
        patient = (Patient)getDescriptor().getControllerDescription().
                getProperty(Properties.PATIENT);
        documentStoreFolder = ((File)getDescriptor().getControllerDescription().getProperty(Properties.DOCUMENT_STORE)).toPath();
        documentStoreFolder = documentStoreFolder.resolve(String.valueOf(patient.getKey()));
        try{
            //create folder for this patient's documents if it does not already exist
            if (!Files.exists(documentStoreFolder)){
                Files.createDirectories(documentStoreFolder);
            }
            //check if source file to be copied has an extension
            extension = getFileExtension(sourceDocumentFolder);
            if (extension.length() > 0){
                //yes: add extension to copied filename of the source file and copy the file to patient's document store
                targetFileName = getTargetFileName(extension);
                documentStoreFolder = documentStoreFolder.resolve(targetFileName);
                Files.copy(sourceDocumentFolder, documentStoreFolder);
            }
        }catch(IOException ex){
             ex.printStackTrace();  
        }
    }*/
    
    private void doModalDocumentStoreViewAction(ActionEvent e){
        PatientViewController.Actions actionCommand =
               PatientViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case IMAGE_VIEWER_REQUEST ->{
                doImageViewerRequest();
            }
        }
    }
    
    private void doClinicalNoteViewAction(ActionEvent e){
        Appointment appointment = null;
        ClinicalNote clinicalNote = null;
        String message = null;
        String error = null;
        ScheduleViewController.Actions actionCommand =
               ScheduleViewController.Actions.valueOf(e.getActionCommand());
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
                            (Appointment)getDescriptor().getControllerDescription().getProperty(Properties.APPOINTMENT);
                    clinicalNote = (ClinicalNote)getDescriptor().getViewDescription().getProperty(Properties.CLINICAL_NOTE);
                    clinicalNote.insert();
                    doClinicalNoteForAppoinmentRequest(e);
                    if(getDescriptor().getControllerDescription()
                            .getProperty(Properties.CLINICAL_NOTE) == null){
                        error = "Attempt to create a new clinical note failed";
                        getDescriptor().getControllerDescription().setProperty(Properties.ERROR, error);
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
                                .getViewDescription().getProperty(Properties.CLINICAL_NOTE);
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
                                    .getViewDescription().getProperty(Properties.CLINICAL_NOTE);
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
    
    private void doClinicalNoteForAppoinmentRequest(ActionEvent e)throws StoreException{
        Appointment appointment = 
                (Appointment)getDescriptor().getControllerDescription().getProperty(Properties.APPOINTMENT);
        ClinicalNote clinicalNote = new ClinicalNote(appointment);
        clinicalNote.setScope(Entity.Scope.FOR_APPOINTMENT);
        clinicalNote = clinicalNote.read();
        if (clinicalNote.get().isEmpty())
            getDescriptor().getControllerDescription()
                    .setProperty(Properties.CLINICAL_NOTE, null);
        else getDescriptor().getControllerDescription()
                    .setProperty(Properties.CLINICAL_NOTE, clinicalNote.get().get(0));
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

    private void doClinicalNoteViewControllerRequest(){
        getDescriptor().getControllerDescription().setProperty(Properties.APPOINTMENT, 
                getDescriptor().getViewDescription().getProperty(Properties.APPOINTMENT));
        setModalView((ModalView)new View().make(View.Viewer.CLINICAL_NOTE_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.Actions.MODAL_VIEWER_CLOSED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doScheduleViewControllerRequest(){  
        //setEntityDescriptorFromView(view.getViewDescriptor());
        getDescriptor().getControllerDescription().setProperty(Properties.SCHEDULE_DAY,
                getDescriptor().getViewDescription().getProperty(Properties.SCHEDULE_DAY));
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            PatientViewController.Actions.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doPatientMedicalHistoryViewControllerRequest(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            PatientViewController.Actions.PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doPatientInvoiceViewControllerRequest(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            DesktopViewController.Actions.PATIENT_INVOICE_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doPatientQuestionnaireViewControllerRequest(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            DesktopViewController.Actions.PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    /**
     * notification from view it is closing down
     * -- let DesktopVC know so it can close down the Patient VC
     */
    private void doPatientViewClosed(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            DesktopViewController.Actions.
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
                    getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
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
    
    private void doDoctorEditorViewRequest(){
        try{
            Doctor doctor = new Doctor(
                    (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT));
            doctor.setScope(Scope.FOR_PATIENT);
            doctor = doctor.read();
            getDescriptor().getControllerDescription().setProperty(Properties.DOCTOR, doctor);
            setModalView((ModalView)new View().make(View.Viewer.PATIENT_DOCTOR_EDITOR_VIEW,
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
            String message = ex.getMessage() + "\n"
                    + "Handled in PatientViewController::doDoctorEditorViewRequest()";
            displayErrorMessage(message, "Patient view controller", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doMedicationEditorViewRequest(){
        try{
            Medication medication = new Medication(
                    (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT));
            medication.setScope(Scope.FOR_PATIENT);
            medication = medication.read();
            ArrayList<Medication> beforePatientMedicationEdit = medication.get();
            getDescriptor().getControllerDescription().setProperty(Properties.MEDICATION, medication);
            setModalView((ModalView)new View().make(View.Viewer.PATIENT_MEDICATION_EDITOR_VIEW,
                    this, 
                    this.getDesktopView()).getModalView());
            Medication afterPatientMedicationEdit = 
                    (Medication)getDescriptor().getControllerDescription().getProperty(Properties.MEDICATION);
            firePropertyChangeEvent(
                ViewController.PatientViewControllerPropertyChangeEvent.
                    PATIENT_EDITOR_VIEW_CLOSED.toString(),
                getView(),
                this,
                null,
                null
            ); 
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Handled in PatientViewController::doMedicationjEditorViewRequest()";
            displayErrorMessage(message, "Patient view controller", 
                    JOptionPane.WARNING_MESSAGE);
        }    
    }
    
    private void createPatientMedicationQuestionnaireStatus(){
        boolean isQFound = false;
        Question question = null;
        Medication m = (Medication)getDescriptor().getControllerDescription().getProperty(Properties.MEDICATION);
        try{
            Question q = new Question();
            q.setScope(Entity.Scope.ALL);
            q = q.read();
            for (Question _q : q.get()){
                if (_q.getOrder().equals(2)){
                    isQFound = true;
                    question = _q;
                    break;
                }
            }
            if(isQFound){
                Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
                PatientQuestion pq = 
                        new PatientQuestion(patient,question );
                pq.setAnswer(m.getDescription());
                pq.insert();
               
            }else{
                message = "Whoops! could not locate patient medication question in store ...?!\n"
                        + "Message sent from createPatientMedicationQuestionnaireStatus() method";
                displayErrorMessage(message, "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }catch (StoreException ex){
            message = ex.getMessage() +"\n"
                    + "StoreExceptopn handled in "
                    + "createPatientMedicationQuestionnaireStatus() method"; 
            displayErrorMessage(message,"Patient view controller error", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deletePatientMedicationQuestionnaireStatus(){
        boolean isQFound = false;
        Question question = null;
        Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
        try{
            Question q = new Question();
            q.setScope(Entity.Scope.ALL);
            q = q.read();
            for (Question _q : q.get()){
                if (_q.getOrder().equals(2)){
                    isQFound = true;
                    question = _q;
                    break;
                }
            }
            if(isQFound){
                PatientQuestion pq = 
                        new PatientQuestion(patient,question);
                pq.setScope(Entity.Scope.SINGLE);
                pq.delete();
            }else{
                message = "Whoops! could not locate patient medication question in store ...?!\n"
                        + "Message sent from deletePatientMedicationQuestionnaireStatus() method";
                displayErrorMessage(message, "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }catch (StoreException ex){
            message = ex.getMessage() +"\n"
                    + "StoreExceptopn handled in "
                    + "createPatientMedicationQuestionnaireStatus() method"; 
            displayErrorMessage(message,"Patient view controller error", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void syncPatientMedicationWithPatientQuestionnaireResponse(){
        boolean isQFound = false;
        Question question = null;
        Medication m = (Medication)getDescriptor().getControllerDescription().getProperty(Properties.MEDICATION);
        Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
        try{
            Question q = new Question();
            q.setScope(Entity.Scope.ALL);
            q = q.read();
            for (Question _q : q.get()){
                if (_q.getOrder().equals(2)){
                    isQFound = true;
                    question = _q;
                    break;
                }
            }
            if(isQFound){
                String meds = "";
                for (Medication _m : m.get()){
                    meds = meds + _m.getDescription() + "; ";  
                }
                meds = meds.trim();
                meds = meds.substring(0, meds.length()- 1); //skip trailing ';'
                
                PatientQuestion pq = 
                        new PatientQuestion(patient,question);
                pq.setScope(Entity.Scope.SINGLE);
                pq = pq.read();
                pq.setAnswer(meds);
                pq.update();
            }else{
                message = "Whoops! could not locate patient medication question in store ...?!\n"
                        + "Message sent from deletePatientMedicationQuestionnaireStatus() method";
                displayErrorMessage(message, "Patient view controller error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }catch (StoreException ex){
            message = ex.getMessage() +"\n"
                    + "StoreExceptopn handled in "
                    + "syncPatientMedicationWithPatientQuestionnaireResponse() method"; 
            displayErrorMessage(message,"Patient view controller error", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doPatientEditorViewRequest(
            Actions actionCommand){
        switch(actionCommand){
            case PATIENT_GBT_RECALL_EDITOR_VIEW_REQUEST:
                doPatientGBTRecallEditorViewRequest();
                break;
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
    
    private void doPatientGBTRecallEditorViewRequest(){
        setModalView((ModalView)new View().make(
                    View.Viewer.PATIENT_GBT_RECALL_EDITOR_VIEW,
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
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
        if (!patient.getIsKeyDefined()){
            try{
                patient.insert();
                patient.setScope(Scope.SINGLE);
                patient = patient.read();
                
                patient.setScope(Entity.Scope.ALL);
                /*28/03/2024patientNote.insert();*/
                patient.read(); 
                getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENTS_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, patient);
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
            getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
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
    
    private void doPatientDeleteRequest(){
        //setEntityDescriptorFromView(view.getViewDescriptor()); 
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
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
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
        if (patient.getIsKeyDefined()){
            try{
                patient.update();
                patient.setScope(Scope.SINGLE);
                patient.read();
                
                patient.setScope(Entity.Scope.ALL);
                patient.read();
                getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
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
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
        if (patient.getIsKeyDefined()){
            try{
                ArrayList<Appointment> deletedAppointments =
                        patient.getDeletedAppointmentHistory();
                setScheduleReport(new ScheduleReport());
                boolean collisionFromAppointmentRecovery = false;
                for(Appointment a : deletedAppointments){
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
                        }/*28/03/2024else{//assume no collision returning this appointment to schedule
                            PatientNote patientNote = a.getPatientNote();
                            patientNote.recover();
                        }*/
                    }
                } 
                //patient.setScope(Scope.DELETED);
                //patient.recover();
                if (collisionFromAppointmentRecovery) {
                    getDescriptor().getControllerDescription().setProperty(Properties.ERROR, errorLog);
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
                    getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
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
                    getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, patient);
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
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
        if (patient.getIsKeyDefined()){
            try{
                patient.setScope(Scope.DELETED);
                Patient the_patient = patient.read();
                for(Patient p : the_patient.get()){
                    if (p.equals(patient)){
                        requestedDeletedPatient = p;
                        break;
                    }
                }
                if (requestedDeletedPatient!=null){
                    getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, requestedDeletedPatient);
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
   
    /*
    private View patientMedicalHistory2View = null;
    private void setPatientMedicalHistory2View(View view){
        patientMedicalHistory2View = view;
    }
    private View getPatientMedicalHistory2View(){
        return patientMedicalHistory2View;
    }*/
    
    private PrimaryCondition makeFatPrimaryCondition()throws StoreException{
        PrimaryCondition fatPC = null;
        Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
        //fatPC = new PrimaryCondition(patient);
        fatPC.setScope(Scope.FOR_PATIENT);
        fatPC = fatPC.read();
        /**
         * fetch secondary condition collection for each primary condition 
         */
        for (Condition primaryCondition : fatPC.get()){
            PrimaryCondition pCondition = (PrimaryCondition)primaryCondition;
            SecondaryCondition sCondition = new SecondaryCondition(pCondition);
            sCondition.setScope(Scope.FOR_PRIMARY_CONDITION);
            sCondition = sCondition.read();
            sCondition.setPrimaryCondition(pCondition);
            pCondition.setSecondaryCondition(sCondition);
        }
        return fatPC;
    }
    
    private void doNoteTakerRequest(ActionEvent e){
        switch(ViewController.PatientViewControllerActionEvent.valueOf(e.getActionCommand())){
            case PATIENT_MEDICAL_HISTORY_NOTES_TAKEN_REQUEST:
                try{
                    Condition condition = (Condition)getDescriptor().getViewDescription().getProperty(Properties.CONDITION);
                    if(condition.getIsPrimaryCondition()){
                        PrimaryCondition pc = (PrimaryCondition)condition;        
                        //pc.setPatient(getDescriptor().getControllerDescription().getPatient());
                        pc.update();
                    }else if(condition.getIsSecondaryCondition()){
                        SecondaryCondition sc = (SecondaryCondition)condition;
                        sc.update();
                        
                    }
                    getDescriptor().getControllerDescription().setProperty(Properties.CONDITION, makeFatPrimaryCondition());
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                PATIENT_MEDICAL_HISTORY_RECEIVED.toString(),
                            getPatientMedicalHistory1View(),
                            this,
                            null,
                            null
                    );
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "Handled in PatientViewController::doNoteTakerRequest";
                    displayErrorMessage(message,"Patient view controller error", 
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
        }
    }
    
    private View patientMedicalHistory1View = null;
    private void setPatientMedicalHistory1View(View view){
        patientMedicalHistory1View = view;
    }
    private View getPatientMedicalHistory1View(){
        return patientMedicalHistory1View;
    }
    
    private void doPatientDoctorEditorViewRequest(ActionEvent e){
        Doctor doctor = (Doctor)getDescriptor()
                .getViewDescription().getProperty(Properties.DOCTOR);
        Patient patient = (Patient)getDescriptor()
                .getControllerDescription().getProperty(Properties.PATIENT);
        Actions actionCommand = 
                Actions.valueOf(e.getActionCommand());
        try{
            switch(actionCommand){ 
                case PATIENT_DOCTOR_CREATE_REQUEST:
                    doctor.setPatient(patient);
                    int key = doctor.insert();
                    doctor = new Doctor(key);
                    doctor.setPatient(patient);
                    doctor.setScope(Scope.FOR_PATIENT);
                    doctor = doctor.read();
                    getDescriptor().getControllerDescription().setProperty(Properties.DOCTOR, doctor);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                DOCTOR_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    break;
                case PATIENT_DOCTOR_DELETE_REQUEST:
                    doctor.setScope(Scope.SINGLE);
                    doctor.delete();
                    doctor = new Doctor(patient);
                    doctor.setScope(Scope.FOR_PATIENT);
                    doctor = doctor.read();
                    getDescriptor().getControllerDescription().setProperty(Properties.DOCTOR, doctor);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                DOCTOR_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    break;
                case PATIENT_DOCTOR_UPDATE_REQUEST:
                    doctor.update();
                    doctor.setScope(Scope.FOR_PATIENT);
                    doctor = doctor.read();
                    getDescriptor().getControllerDescription().setProperty(Properties.DOCTOR, doctor);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                DOCTOR_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    break;

            }
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Handled in "
                    + "PatientViewController::doPatientDoctorEditorViewRequest()";
            displayErrorMessage(message,"Patient view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private ArrayList<Medication> beforePatientMedicationEdit = null;
    private ArrayList<Medication> getBeforePatientMedicationEdit(){
        return beforePatientMedicationEdit;
    }
    private void setBeforePatientMedicationEdit(ArrayList<Medication> value){
        beforePatientMedicationEdit = value;
    }
    
    private Medication afterPatientMedicationEdit = null;
    private Medication getAfterPatientMedicationEdit(){
        return afterPatientMedicationEdit;
    }
    private void setAfterPatientMedicationEdit(Medication value){
        afterPatientMedicationEdit = value;
    }
    
    private void mopUpAction(){
        /**
         * the following mop up operation necessary
         * -- to keep patient medication state view consistent with patient medication question status in store
         */
        if ((beforePatientMedicationEdit.isEmpty()) && (!afterPatientMedicationEdit.get().isEmpty()))
            createPatientMedicationQuestionnaireStatus();
        else if ((!beforePatientMedicationEdit.isEmpty()) && (afterPatientMedicationEdit.get().isEmpty()))
            deletePatientMedicationQuestionnaireStatus();
        else if(!afterPatientMedicationEdit.get().isEmpty()) syncPatientMedicationWithPatientQuestionnaireResponse(); 
    }
    
    private void doPatientMedicationEditorViewRequest(ActionEvent e){
        Medication medication = (Medication)getDescriptor().getViewDescription().getProperty(Properties.MEDICATION);
        Medication med = (Medication)getDescriptor().getControllerDescription().getProperty(Properties.MEDICATION);
        setBeforePatientMedicationEdit(med.get());
        /*setBeforePatientMedicationEdit(getDescriptor()
                .getControllerDescription().getMedication().get());*/
        try{
            switch(Actions.valueOf(e.getActionCommand())){ 
                case PATIENT_MEDICATION_CREATE_REQUEST:
                    String reply = JOptionPane.showInternalInputDialog(
                            getView(), "",
                            "Enter prescribed medicene", JOptionPane.OK_CANCEL_OPTION);
                    if (reply!=null){
                        if (!reply.trim().isEmpty()){
                            medication = new Medication();
                            medication.setDescription(reply);
                            Patient patient = (Patient)getDescriptor().
                                    getControllerDescription().getProperty(Properties.PATIENT);
                            medication.setPatient(patient);
                            medication.insert();
                            medication = new Medication(patient);
                            medication.setScope(Scope.FOR_PATIENT);
                            medication = medication.read();
                            getDescriptor().getControllerDescription()
                                    .setProperty(Properties.MEDICATION, medication);
                            setAfterPatientMedicationEdit(medication);
                            mopUpAction();
                            firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                CLOSE_VIEW_REQUEST_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                            );
                            doMedicationEditorViewRequest();
                        }
                    }
                    break;
                case PATIENT_MEDICATION_DELETE_REQUEST:
                    medication.setScope(Scope.SINGLE);
                    medication.delete();
                    medication.setScope(Scope.FOR_PATIENT);
                    medication = medication.read();
                    getDescriptor().getControllerDescription().setProperty(Properties.MEDICATION, medication);
                    setAfterPatientMedicationEdit(medication);
                    mopUpAction();
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                CLOSE_VIEW_REQUEST_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    doMedicationEditorViewRequest();
                    break;
                case PATIENT_MEDICATION_UPDATE_REQUEST:
                    reply = JOptionPane.showInternalInputDialog(getView(), 
                                    medication.getDescription(),
                                    "Update selected medicene", JOptionPane.OK_CANCEL_OPTION);
                    if (reply!=null){
                        if (!reply.trim().isEmpty()){
                            medication.setDescription(reply);
                            medication.setPatient((Patient)getDescriptor().
                                    getControllerDescription().getProperty(Properties.PATIENT));
                            medication.update();
                            medication.setScope(Scope.FOR_PATIENT);
                            medication = medication.read();
                            setAfterPatientMedicationEdit(medication);
                            mopUpAction();
                        }
                    }
                    getDescriptor().getControllerDescription().setProperty(Properties.MEDICATION, medication);
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                CLOSE_VIEW_REQUEST_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    doMedicationEditorViewRequest();
                    break;
            }
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Handled in "
                    + "PatientViewController::doPatientMedicationEditorViewRequest()";
            displayErrorMessage(message,
                    "Patient view controller error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    
    
    /*private void doPatientMedicalHistory1EditorViewRequest(ActionEvent e){
        doPatientMedicalHistory1EditorViewRequestNEW(e);
        
        PrimaryCondition pc = null;
        View view = (View)e.getSource();
//27/03/2024 05:43
        setPatientMedicalHistory1View(view);
        switch(ViewController.PatientViewControllerActionEvent.valueOf(e.getActionCommand())){
            case CONDITION_STATE_UPDATE_REQUEST:
                pc = (PrimaryCondition)getDescriptor()
                        .getViewDescription().getCondition();
                try{
                    pc.update();
                    
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException raised in PatientViewController::doPatientMedicalHistory1ViewRequest()";
                    displayErrorMessage(message,"Patient view controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case PATIENT_MEDICAL_HISTORY_NOTE_TAKER_REQUEST:
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            MAKE_VIEW_INVISIBLE.toString(),
                        getPatientMedicalHistory1View(),
                        this,
                        null,
                        null
                );
                Condition condition = getDescriptor().getViewDescription().getCondition();
                getDescriptor().getControllerDescription().setCondition(condition);

                setModalView((ModalView)new View().make(View.Viewer.NOTE_TAKER,
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
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            MAKE_VIEW_VISIBLE.toString(),
                        view,
                        this,
                        null,
                        null
                );
                break;
            case PATIENT_MEDICAL_HISTORY_2_EDITOR_VIEW_REQUEST:

                pc = (PrimaryCondition)getDescriptor()
                        .getViewDescription().getCondition();
                getDescriptor().getControllerDescription().setCondition(pc);
                
                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            MAKE_VIEW_INVISIBLE.toString(),
                        view,
                        this,
                        null,
                        null
                );

                setModalView((ModalView)new View().make(View.Viewer.PATIENT_MEDICAL_HISTORY_2_EDITOR_VIEW,
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

                firePropertyChangeEvent(
                        ViewController.PatientViewControllerPropertyChangeEvent.
                            MAKE_VIEW_VISIBLE.toString(),
                        view,
                        this,
                        null,
                        null
                );

                break;
        }
        
    }*/
    
    /**
     * a change made on a secondary view editor
     * @param secondaryView 
     */
    private void doPatientEditorViewChange(View secondaryView){
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
        try{
            /*if (getDescriptor().getControllerDescription()
                    .getViewMode().equals(ViewController.ViewMode.UPDATE)){*/
            if (getDescriptor().getControllerDescription()
                    .getProperty(Properties.VIEW_MODE).equals(ViewController.ViewMode.UPDATE)){
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
        if (getPatientSelectionMode().equals(PatientSelectionMode.PATIENT_SELECTION)){
            patient = (Patient)getDescriptor().getViewDescription().getProperty(Properties.PATIENT);
        }    
        else
            patient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
        //
        if (patient.getIsKeyDefined()){
            try{
                patient.setScope(Scope.SINGLE);
                Patient p = patient.read();
                setCurrentlySelectedPatient(p);
                getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, p);
                doConvertAppointmentNoteToTreatment(p);
                firePropertyChangeEvent(
                       ViewController.PatientViewControllerPropertyChangeEvent.
                        PATIENT_RECEIVED.toString(),
                        getView(),
                        this,
                        null,
                        getDescriptor()
                );
                /**
                 * check if another patient view is open on the same patient
                 * -- if it is the Desktop VC will send a request to the view to close 
                 * this ensures only one patient view will be displayed on the desktop for a given patient
                 */
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                CLOSE_PATIENT_VIEW_WITH_SAME_PATIENT_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                
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
            getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, patient);
            getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
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
    
    public PatientViewController(DesktopViewController myController,
                                 Descriptor myDescriptor,
                                 DesktopView desktopView)throws StoreException{
        setDesktopView(desktopView);
        setMyController(myController);
        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        patient.read();
        setDescriptor(myDescriptor);

        getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
        View.setViewer(View.Viewer.PATIENT_VIEW);
        setCurrentlySelectedPatient(new Patient());
    }
    
    /**
     * On entry
     * @param e PropertyChangeEvent sent by Desktop controller to prompt view refresh
     */
    @Override
    public void propertyChange(PropertyChangeEvent e){
        Patient scheduleViewPatient = null;
        Descriptor descriptor = (Descriptor)e.getNewValue();
        scheduleViewPatient = (Patient)descriptor.getControllerDescription().getProperty(Properties.PATIENT);
        Patient patientViewPatient = (Patient)getDescriptor().getControllerDescription().getProperty(Properties.PATIENT);
        ViewController.PatientViewControllerPropertyChangeEvent propertyName = 
                ViewController.PatientViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case PATIENT_VIEW_CHANGE_NOTIFICATION:{
                /**
                 * 28/07/2025 update
                 * -- update system wide settings for this view (before any other action), just in case they have been altered
                 */
                
                /**
                 * 26/04/2024 08:13 update
                 */
                /**
                 * Only action property change event if
                 * -- patient is not null
                 * -- patient key is defined
                 * -- patient is not the UNBOOKABLE one
                 */
                if (scheduleViewPatient!=null) {
                    if (scheduleViewPatient.getIsKeyDefined()){
                        if (!scheduleViewPatient.getIsPatientMarkedUnbookable()){
                            try{
                                if (descriptor.getControllerDescription().
                                        getProperty(Properties.VIEW_MODE)==null)
                                    descriptor.getControllerDescription().
                                            setProperty(Properties.VIEW_MODE, ViewController.ViewMode.NO_ACTION);
                                /**
                                 * switch determines 2 cases of view mode
                                 * -- PATIENT_ARCHIVE
                                 * -- -- if selected patient in patient view = selected patient in schedule view
                                 * -- -- -- reads the selected patient from store
                                 * -- -- -- if read patient is archived triggers a null patient request to repopulate the patient selection combobox and clear patient fields in view
                                 * -- -- else trigger a null patient request and fire PATIENT_RECEIVED pce at patient view which defines the currently selected patient on entry
                                 * -- PATIENT_RESTORE
                                 * -- -- -- assume the currently selected patient (if any) in view) is not the patient just restored (fair assumption!)
                                 * -- -- -- trigger a null patient request
                                 * -- -- -- re-initialse Patient VC control descriptor with the selected patient in patient view on entry to PATIENT_VIEW_CHANGE_NOTIFICATION pce
                                 * -- -- -- and fire a PATIENT_RECEIVED pce at the patient view
                                 * -- DEFAULT
                                 * -- -- -- if schedule view patient = patient view patient
                                 * -- -- -- trigger a null patient request (repopulate patient selection combobox)
                                 * -- -- -- re-initialse Patient VC control descriptor with the selected patient in patient view on entry to PATIENT_VIEW_CHANGE_NOTIFICATION pce
                                 * -- -- -- and fire a PATIENT_RECEIVED pce at the patient view
                                 */
                                switch(descriptor.getControllerDescription().getProperty(Properties.VIEW_MODE)){
                                    case ViewController.ViewMode.PATIENT_ARCHIVE ->{
                                        if(scheduleViewPatient.equals(patientViewPatient)){
                                            scheduleViewPatient.setScope(Scope.SINGLE);
                                            scheduleViewPatient = scheduleViewPatient.read();
                                            if (scheduleViewPatient.getIsArchived()) doNullPatientRequest();
                                        }else{
                                            doNullPatientRequest();
                                            firePropertyChangeEvent(
                                                    ViewController.PatientViewControllerPropertyChangeEvent.
                                                            PATIENT_RECEIVED.toString(),
                                                    getView(),
                                                    this,
                                                    null,
                                                    getDescriptor()
                                            );
                                        }
                                        break;
                                    }
                                    case ViewController.ViewMode.PATIENT_RESTORE ->{
                                        doNullPatientRequest();
                                        getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, patientViewPatient);
                                        firePropertyChangeEvent(
                                                ViewController.PatientViewControllerPropertyChangeEvent.
                                                        PATIENT_RECEIVED.toString(),
                                                getView(),
                                                this,
                                                null,
                                                getDescriptor()
                                        );
                                        break;
                                    }
                                    default ->{
                                        if (scheduleViewPatient.equals(patientViewPatient)){
                                            doNullPatientRequest();
                                            getDescriptor().getControllerDescription().setProperty(Properties.PATIENT, patientViewPatient);
                                            firePropertyChangeEvent(
                                                    ViewController.PatientViewControllerPropertyChangeEvent.
                                                            PATIENT_RECEIVED.toString(),
                                                    getView(),
                                                    this,
                                                    null,
                                                    getDescriptor()
                                            );
                                        }
                                        break;   
                                    }
                                }
                            }catch(StoreException ex){
                                displayErrorMessage(ex.getMessage() + "\nRaised in propertyChange() method",
                                    "Patient view controller error", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }/*else {//if no patient selected refresh patient list
                        try{
                        patient = new Patient();
                        patient.setScope(Scope.ALL);
                        patient = patient.read();
                        getDescriptor().getControllerDescription().setProperty(Properties.PATIENTS, patient.get());
                        firePropertyChangeEvent(
                                ViewController.PatientViewControllerPropertyChangeEvent.
                                        PATIENTS_RECEIVED.toString(),
                                getView(),
                                this,
                                null,
                                getDescriptor()
                        ); 
                        }catch(StoreException ex){
                            displayErrorMessage(ex.getMessage() + "\nRaised in propertyChange() method",
                                "Patient view controller error", JOptionPane.WARNING_MESSAGE);
                        }
                    } */
                }
                break; 
            } 
            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerActionRequest(e);
        }
        /*else if(e.getSource() instanceof ModalPatientMedicalHistory1EditorView){
            doSecondaryViewActionRequest(e);*/
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
     
    
    
    private void doOpenNoteTaker(){
        Condition condition = (Condition)getDescriptor().getViewDescription().getProperty(Properties.CONDITION);
        getDescriptor().getControllerDescription().setProperty(Properties.CONDITION, condition);

        setModalView((ModalView)new View().make(View.Viewer.NOTE_TAKER,
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
   
    /*
    private void doRequestClosePatientMedicalHistory1EditorView(View view){
        firePropertyChangeEvent(
            ViewController.PatientViewControllerPropertyChangeEvent.
                CLOSE_VIEW_REQUEST_RECEIVED.toString(),
            view,
            this,
            null,
            null
        );
    }*/
    
    /*
    private void doRequestClosePatientMedicalHistory2EditorView(View view){
        firePropertyChangeEvent(
            ViewController.PatientViewControllerPropertyChangeEvent.
                CLOSE_VIEW_REQUEST_RECEIVED.toString(),
            view,
            this,
            null,
            null
        );
    }*/
    
    /*
    private void doPatientMedicalHistory1EditorViewRequestNEW(ActionEvent e){
        PrimaryCondition pc = null;
        View view = (View)e.getSource();

        // save reference for access later after receipt of a request from the note taker

        setPatientMedicalHistory1View(view);
        switch(ViewController.PatientViewControllerActionEvent.valueOf(e.getActionCommand())){
            case CONDITION_STATE_UPDATE_REQUEST:
                pc = (PrimaryCondition)getDescriptor()
                        .getViewDescription().getProperty(Properties.CONDITION);
                try{
                    pc.update();
                    
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException raised in PatientViewController::doPatientMedicalHistory1ViewRequest()";
                    displayErrorMessage(message,"Patient view controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            
            case PATIENT_MEDICAL_HISTORY_NOTE_TAKER_REQUEST:
                doRequestClosePatientMedicalHistory1EditorView(view);
                
                doOpenNoteTaker();
                
                doOpenPatientMedicalHistory1EditorView();

                break;
            case PATIENT_MEDICAL_HISTORY_2_EDITOR_VIEW_REQUEST:
                
                
                doRequestClosePatientMedicalHistory1EditorView(view);

                pc = (PrimaryCondition)getDescriptor()
                        .getViewDescription().getCondition();
                getDescriptor().getControllerDescription().setCondition(pc);
                setParentPrimaryCondition(pc);
                doOpenPatientMedicalHistory2EditorView();
                
                doOpenPatientMedicalHistory1EditorView();

                break;
                
        } 
    }*/
    
    
    
    /**
     * on entry following 2 references need to need to be kept alive
     * -- reference to PatientMedicalHistory1EditorView needs to be saved
     * -- reference to the the parent PrimaryCondition needs to be saved
     * @param e references PatientMedicalHistory1EditorView via its Source property
     */
    private PrimaryCondition parentPrimaryCondition = null;
    private void setParentPrimaryCondition(PrimaryCondition value){
        parentPrimaryCondition = value;
    }
    private PrimaryCondition getParentPrimaryCondition(){
        return parentPrimaryCondition;
    }
    
    private void doConvertAppointmentNoteToTreatment(Patient p)throws StoreException{
        ArrayList<Appointment> appointments = p.getAppointmentHistory();
        //doFormatAppointmentTreatmentNote(p.getAppointmentHistory());
        
        
        for (Appointment a : appointments){
            TreatmentWithState treatmentWithState = getTreatmentsWithState(a);
            String note = "";
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
                if(!note.trim().isEmpty()){
                    if (note.substring(note.length()-1).equals("/")){
                        note = note.substring(0, note.length() - 2);
                        a.setNotes(note);
                    }
                }
            } 
        }
        
        
        
        getDescriptor().getControllerDescription()
                .setProperty(Properties.APPOINTMENTS, appointments);
    }
}
