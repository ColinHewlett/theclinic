/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.ViewController.DesktopViewControllerActionEvent.VIEW_CONTROLLER_CHANGED_NOTIFICATION;
import static controller.ViewController.DesktopViewControllerActionEvent.VIEW_CONTROLLER_CLOSE_NOTIFICATION;
import static controller.ViewController.DesktopViewControllerPropertyChangeEvent.PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION;
import static controller.ViewController.DesktopViewControllerPropertyChangeEvent.SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION;
import static controller.ViewController.displayErrorMessage;
import static controller.ViewController.ViewControllers;
import static controller.ViewController.displayErrorMessage;
import model.*;
import controller.exceptions.TemplateReaderException;
import repository.Repository;
import repository.StoreException;
import model.SystemDefinition;
import org.apache.commons.io.FilenameUtils;
import repository.StoreException;//01/03/2023
import view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import view.View;

/**
 *
 * @author colin
 */
public class DesktopViewController extends ViewController{
    private boolean isFirstActionEventReceivedFromAppointmentScheduleViewController = true;
    private DesktopViewMode desktopViewMode;
    private boolean isDesktopPendingClosure = false;
    private DesktopView desktopView = null;
    //private ArrayList<AppointmentRemindersViewController> appointmentRemindersViewControllers = null;
    private ArrayList<ScheduleViewController>scheduleViewControllers = null;
    private ArrayList<NotificationViewController> notificationViewControllers = null;
    private ArrayList<PatientViewController> patientViewControllers = null;
    private ArrayList<TreatmentViewController> treatmentViewControllers = null;
    private ArrayList<MedicalConditionViewController> medicalConditionViewControllers = null;
    private ArrayList<ClinicalNoteViewController> clinicalNoteViewControllers = null;
    private ArrayList<DataMigrationProgressViewController> importProgressViewControllers = null;
    private static Boolean isDataMigrationOptionEnabled = null;
    private PropertyChangeSupport pcSupport = null;
    private Descriptor entityDescriptor = null;
    private int count = 0;
    private int recordCount = 0;
    
    
    private DesktopViewController(){
               setDescriptor(new Descriptor());
        /**
         * Constructor for DesktopView takes two arguments
         * -- object reference to view controller (this)
         * -- Boolean signifying whether view enables data migration functions
         */
        DesktopView desktopView = new DesktopView(this, isDataMigrationOptionEnabled, getDescriptor() );
        desktopView.setLocationRelativeTo(null);
        setDesktopView(desktopView);
        
        
        //view.setContentPane(view);
        pcSupport = new PropertyChangeSupport(this);
        //appointmentScheduleViewControllersMap = new HashMap<>();
        scheduleViewControllers = new ArrayList<>();
        patientViewControllers = new ArrayList<>();
        importProgressViewControllers = new ArrayList<>();
        notificationViewControllers = new ArrayList<>();
        treatmentViewControllers = new ArrayList<>();
        clinicalNoteViewControllers = new ArrayList<>();
        medicalConditionViewControllers = new ArrayList<>();
        boolean isPMSStoreDefined;
        try{
            new Repository();

            if (isDataMigrationOptionEnabled) {
                notifyMigrationActionCompleted();
                getDesktopView().initialiseView();
            }
            else{
                if ((SystemDefinition.getPMSOperationMode()).equals("undefined")){
                    displayErrorMessage("A PMS store has not been defined; ClinicPMS will abort\n"
                            + "Re-enter application in data migration mode by specifying DATA_MIGRATION_ENABLED on the command line",
                            "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                } 
            }
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage() + "\n"
                    + "Raised in Desktop view controller constructor",
                    "Desktop view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doActionEventForClinicalNoteViewController(ActionEvent e){
        String message = null;
        ClinicalNoteViewController cvc = (ClinicalNoteViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                boolean hasFoundViewConroller = false;
                Iterator<ClinicalNoteViewController> viewControllerIterator = 
                        this.clinicalNoteViewControllers.iterator();
                while(viewControllerIterator.hasNext()){
                    cvc = viewControllerIterator.next();
                    if (cvc.equals(e.getSource())){
                        hasFoundViewConroller = true;
                        break;
                    }
                }
                if (hasFoundViewConroller){
                    if (!this.clinicalNoteViewControllers.remove(cvc)){
                        message = "Problem arose on attempt to remove a "
                                + "ClinicalNotte view conroller";
                        displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
                    }/*else System.out.println("CVC removed("
                            + clinicalNoteViewControllers.size() + ")");*/
                }else{
                    message = "Could not locate the ClinicalNote view controller "
                            + "which requested tto be removed";
                        displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
                }
            }   
        }
    }
    
    private void doActionEventForTreatmentViewController(ActionEvent e){
        String message = null;
        TreatmentViewController tvc = (TreatmentViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.treatmentViewControllers.size()){
                    case 0:
                        message = "No Treatment view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (tvc.equals(this.treatmentViewControllers.get(0))){
                            this.treatmentViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find Treatment view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one Treatment view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForTreatmentViewController"
                            + "(case VIEW_CONTROLLER_CLOSE_NOTIFICATION)\n"
                            + message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }   
        }
    }
    
    private void doActionEventForMedicalConditionViewController(ActionEvent e){
        String message = null;
        MedicalConditionViewController mcvc = (MedicalConditionViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.medicalConditionViewControllers.size()){
                    case 0:
                        message = "No Medical condition view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (mcvc.equals(this.medicalConditionViewControllers.get(0))){
                            this.medicalConditionViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find Medical condition view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one Medical condition view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForMwedicalConditionViewController"
                            + "(case VIEW_CONTROLLER_CLOSE_NOTIFICATION)\n"
                            + message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }   
        }
    }

    private void doActionEventForNotificationViewController(ActionEvent e){
        String message = null;
        NotificationViewController pnvc = (NotificationViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.notificationViewControllers.size()){
                    case 0:
                        message = "No PatientNotification view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (pnvc.equals(this.notificationViewControllers.get(0))){
                            this.notificationViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find PatientNotification view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one PatientNotification view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForPatientNotificationViewController(case VIEW_CONTROLLER_CLOSE_NOTIFICATION)\n"
                            + "message",
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }   
        }
    }
    
    /**
     * ActionEvent responder; action events sent by an ActionViewController include
     * -- SCHEDULE_VIEW_CONTROLLER_REQUEST
     * ---- 
     * -- APPOINTMENT_HISTORY_CHANGE_NOTIFICATION
     * -- DISABLE_DESKTOP_CONTROLS_REQUEST
     * -- ENABLE_DESKTOP_CONTROLS_REQUEST
     * -- VIEW_CLOSED_NOTIFICATION
     * @param e:ActionEvent received; indicates which ActionCommand from above list was sent
     */
    private void doActionEventForScheduleViewController(ActionEvent e){
        ScheduleViewController avc = (ScheduleViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST:
                doRequestForClinicalNoteViewController(e);
                break;
            case SCHEDULE_VIEW_CONTROLLER_REQUEST:  
                doRequestForScheduleViewController(avc);
                break;
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                Iterator<ScheduleViewController> viewControllerIterator = 
                        this.scheduleViewControllers.iterator();
                while(viewControllerIterator.hasNext()){
                    avc = viewControllerIterator.next();
                    if (avc.equals(e.getSource())){
                        break;
                    }
                }
                if (!this.scheduleViewControllers.remove(avc)){
                    String message = "Could not find AppointmentViewController in "
                                            + "DesktopViewController collection.";
                    displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    if (this.isDesktopPendingClosure){
                        this.requestViewControllersToCloseViews();
                    }
                    if (this.scheduleViewControllers.isEmpty() && 
                            this.patientViewControllers.isEmpty()){ 
                    }
                }
                break;
            }
        }           
    }
    
    private void doActionEventForPatientViewController(ActionEvent e){
        PatientViewController pvc = null;
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:
                Iterator<PatientViewController> viewControllerIterator = 
                        this.patientViewControllers.iterator();
                while(viewControllerIterator.hasNext()){
                    pvc = viewControllerIterator.next();
                    if (pvc.equals(e.getSource())){
                        break;
                    }
                }
                if (!this.patientViewControllers.remove(pvc)){
                    String message = "Could not find PatientViewController in "
                                            + "DesktopViewController collection.";
                    displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                }
                else{
                    if (this.isDesktopPendingClosure){
                        this.requestViewControllersToCloseViews();
                    }
                    if (this.scheduleViewControllers.isEmpty() && 
                            this.patientViewControllers.isEmpty()){ 
                    }
                }
                break;
            case CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST:
                doRequestForClinicalNoteViewController(e);
                break;
            /*
            case TREAMENT_VIEW_CONTROLLER_REQUEST:
                doRequestForTreatmentViewController(e);
                break;
            */
            case SCHEDULE_VIEW_CONTROLLER_REQUEST:
                /**
                 * VC receives a request for a new AppointmentVC from a PatientVC
                 * -- the PatientVC view's EntityDescriptorFromView object defines an appointment for the selected patient
                 * -- the appointment date is used in the construction of a new AppointmentVC and associated appointment schedule view which includes the selected patient's appointment
                 */
                PatientViewController patientViewController = (PatientViewController)e.getSource();
                patientViewController.getDescriptor()
                        .getControllerDescription()
                        .setViewMode(ViewController.ViewMode.SCHEDULE_REFERENCED_FROM_PATIENT_VIEW);
                createNewAppointmentScheduleViewController(patientViewController.getDescriptor());
                break;
        }
    }
 
    private void doActionEventForImportProgressViewController(ActionEvent e){
        DesktopViewControllerActionEvent actionCommand =
                DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        
        switch (actionCommand){
            case VIEW_CLOSED_NOTIFICATION:
                importProgressViewControllers.clear();
                notifyMigrationActionCompleted();
                break;
                
            case MIGRATE_PRIMARY_CONDITION_DATA:
                try{
                    PrimaryCondition pc = 
                            extractMedicalHistoryFromTemplate();
                    setExtractedPrimaryConditionFromTemplate(pc);
                    if (PMSStore.isSelected()) 
                           startBackgroundThread(pc, this);
                }catch (TemplateReaderException ex){
                    displayErrorMessage(ex.getMessage() + "\nTemplateReaderException handled"
                            + " in case MIGRATE_PRIMARY_CONDITION_DATA inside "
                            + "doExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nStoreException handled"
                            + " in case MIGRATE_PATIENT_MEDICAL_HISTORY inside "
                            + "doExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
                
            case MIGRATE_SECONDARY_CONDITION_DATA:
                try{
                    if (PMSStore.isSelected()) 
                        startBackgroundThread(new SecondaryCondition(
                                getExtractedPrimaryConditionFromTemplate()), this);
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nStoreException handled"
                            + " in case MIGRATE_PATIENT_MEDICAL_HISTORY inside "
                            + "doExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
                
            case MIGRATE_TREATMENT_DATA:
                try{
                    Treatment treatment = 
                            extractTreatmentFromTemplate();
                    //setExtractedTreatmentFromTemplate(treatment);
                    if (PMSStore.isSelected()) 
                           startBackgroundThread(treatment, this);
                }catch (TemplateReaderException ex){
                    displayErrorMessage(ex.getMessage() + "\nTemplateReaderException handled"
                            + " in case MIGRATE_TREATMENT_DATA inside "
                            + "doExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nStoreException handled"
                            + " in case MIGRATE_PATIENT_MEDICAL_HISTORY inside "
                            + "doExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
                
            case MIGRATE_PATIENT_DATA:
                try{
                    if (PMSStore.isSelected()) 
                        startBackgroundThread(new Patient(), this);
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nException handled"
                            + " in case EXPORT_MIGRATED_PATIENTS inside "
                            + "doExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
            case MIGRATE_APPOINTMENT_DATA:
                try{
                    if (PMSStore.isSelected()) 
                        startBackgroundThread(new Appointment(), this);
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nException handled"
                            + " in case EXPORT_MIGRATED_APPOINTMENTS inside "
                            + "doImportExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);    
                }
                break;
            /*28/03/2024
            case MIGRATE_PATIENT_NOTE_DATA:
                try{
                    if (PMSStore.isSelected()) 
                        startBackgroundThread(new PatientNote(), this);
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage() + "\nException handled"
                            + " in case EXPORT_MIGRATED_APPOINTMENTS inside "
                            + "doImportExportProgressViewControllerAction()",
                            "Desktop View Controller error",
                            JOptionPane.WARNING_MESSAGE);    
                }
                break;  */ 
            case MIGRATE_SURGERY_DAYS_ASSIGNMENT_DATA:
                SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment();       
                try{
                    surgeryDaysAssignment.insert();
                    getDesktopView().initialiseView();
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage(), 
                            "Desktop view controleer", JOptionPane.WARNING_MESSAGE);
                }
                break;
        }
    }
   
    /**
     * 
     * @param e source of event is X_DesktopView object
     */
    private void doActionEventForDesktopView(ActionEvent e){ 
        ViewController.DesktopViewControllerActionEvent actionCommand = null;
        Point theCount = null;
        PrimaryCondition primaryCondition = null;
        SecondaryCondition secondaryCondition = null;
        Treatment treatment = null;
        try{
            /**
             * 11/01/2023 19:08 update
             * -- modified PMSStore.getPath() method creates a new Access database file if one doesn't already exists
             */
            //PMSStore.getPath();
            actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
            switch (actionCommand){
                case IMPORT_LIST_FILES:
                    /**
                     * read the list files to populate the following tables
                     * -- PrimaryCondition table
                     * -- SecondaryCondition table
                     * -- Treatment table
                     */
                    primaryCondition = extractMedicalHistoryFromTemplate();
                    for(Condition condition : primaryCondition.get()){
                        PrimaryCondition pCondition = (PrimaryCondition)condition;
                        //pCondition.setPatient(patient);
                        Integer pConditionKey = pCondition.insert();
                        if (!pCondition.getSecondaryCondition().get().isEmpty()){
                            for (Condition c : secondaryCondition.get()){
                                SecondaryCondition sCondition = (SecondaryCondition)c;
                                sCondition.setPrimaryCondition(new PrimaryCondition(pConditionKey));
                                sCondition.insert();
                            }
                        }
                    }
                    
                    treatment = extractTreatmentFromTemplate();
                    for(Treatment t : treatment.get()){
                        t.insert();
                    }
                    break;
                case VIEW_ACTIVATED_NOTIFICATION:
                    break;
                    
                case VIEW_CHANGED_NOTIFICATION:
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                            getDesktopView(),
                            this,
                            null,
                            null
                    );
                case CLINIC_LOGO_VIEW_MODE_NOTIFICATION:
                    setDesktopViewMode(DesktopViewMode.CLINIC_LOGO);
                    break;
                case DESKTOP_VIEW_MODE_NOTIFICATION:
                    setDesktopViewMode(DesktopViewMode.DESKTOP);
                    break;
                
                case GET_APPOINTMENT_CSV_PATH_REQUEST:
                    doRequestForGetPath(actionCommand, e.getSource());
                    break;
                
                case GET_PATIENT_CSV_PATH_REQUEST:
                    doRequestForGetPath(actionCommand, e.getSource());
                    break;
                case GET_PMS_STORE_PATH_REQUEST:
                    doRequestForGetPath(actionCommand, e.getSource());
                    break;
                
                case COUNT_APPOINTMENT_TABLE_REQUEST:
                    theCount = doRequestCountForAppointmentTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()   
                    );
                    break;
                case COUNT_PATIENT_TABLE_REQUEST:
                    theCount = doRequestCountForPatientTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                /*
                case COUNT_PATIENT_NOTE_TABLE_REQUEST:
                    theCount = COUNT_CLINIC_NOTE_TABLE_REQUEST();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_NOTE_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                    */
                case COUNT_CLINIC_NOTE_TABLE_REQUEST:
                    theCount = doRequestCountForClinicNoteTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    CLINIC_NOTE_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                case COUNT_TREATMENT_TABLE_REQUEST:
                    theCount = doRequestCountForTreatmentTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    TREATMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                case COUNT_PRIMARY_CONDITION_TABLE_REQUEST:
                    theCount = doRequestCountForPrimaryConditionTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                case COUNT_SECONDARY_CONDITION_TABLE_REQUEST:
                    theCount = doRequestCountForSecondaryConditionTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SECONDARY_CONDITION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                case COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST:
                    theCount = doRequestCountForPatientNotificationTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_NOTIFICATION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                case COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST:
                    theCount = doRequestCountForSurgeryDaysAssignmentTable();
                    getDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SURGERY_DAYS_ASSIGNMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                case MEDICAL_CONDITION_VIEW_CONTROLLER_REQUEST:
                    doRequestForMedicalConditionViewController(e);
                    break;
                case TREATMENT_VIEW_CONTROLLER_REQUEST:
                    //getDescriptor().getControllerDescription().setPatient(null);
                    doRequestForTreatmentViewController(e);
                    break;
                    
                case NOTIFICATION_VIEW_CONTROLLER_REQUEST:
                    doRequestForNotificationViewController();
                    break;
                case VIEW_CLOSE_REQUEST:{
                    doRequestForViewClose();
                    break;
                }
                case SCHEDULE_VIEW_CONTROLLER_REQUEST:{                  
                    doRequestForScheduleViewController((DesktopView)e.getSource());
                    break;
                }
                case TEST_PATIENT_VIEW_CONTROLLER_REQUEST:{
                    doRequestForTestPatientViewController();
                    break;
                }
                case PATIENT_VIEW_CONTROLLER_REQUEST:{
                    doRequestForPatientViewController();
                    break;
                }
                case VIEW_CLOSED_NOTIFICATION:{/* user has attempted to close Desktop view*/
                    doRequestForViewNotification();
                    break;
                }  
                case DELETE_DATA_FROM_PMS_DATABASE_REQUEST:{
                    doRequestForDeleteDataFromPMSDatabase();
                    getDesktopView().initialiseView();
                    break;
                }
                case MIGRATE_DATA_FROM_SOURCE_VIEW_REQUEST:{                  
                    PMSStore.getPath();   
                    doRequestForImportProgressViewController();
                    break;
                }
            }
        }catch (StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Exception raised in DesktopViewController.doActionEventForDesktopView("
                    + actionCommand + ")";
            displayErrorMessage(message, " Desktop ViewController error",JOptionPane.WARNING_MESSAGE);
        }catch (TemplateReaderException ex){
            String message = ex.getMessage() + "\n"
                    + "Exception raised in DesktopViewController.doActionEventForDesktopView("
                    + actionCommand + ")";
            displayErrorMessage(message, " Desktop ViewController error",JOptionPane.WARNING_MESSAGE);
        }          
    }
    
    private void requestViewControllersToCloseViews(){
        if (!this.patientViewControllers.isEmpty()){
            Iterator<PatientViewController> pvcIterator = patientViewControllers.iterator();
            while(pvcIterator.hasNext()){
                PatientViewController pvc = pvcIterator.next();
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                pvc.actionPerformed(actionEvent);    
            }
        }
        
        if (!this.scheduleViewControllers.isEmpty()){
            Iterator<ScheduleViewController> avcIterator = scheduleViewControllers.iterator();
            while(avcIterator.hasNext()){
                ScheduleViewController avc = avcIterator.next();
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                avc.actionPerformed(actionEvent);    
            }
        }
        if ((scheduleViewControllers.isEmpty()) && (patientViewControllers.isEmpty())){
            if (this.isDesktopPendingClosure){
                getDesktopView().dispose();
                System.exit(0);
            }
        } 
    }

    private void createNewAppointmentScheduleViewController(Descriptor ed){       
        try{
            ScheduleViewController avc =
                    new ScheduleViewController(this, getDesktopView());
            this.scheduleViewControllers.add(avc);
            try{
                SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment();
                surgeryDaysAssignment = surgeryDaysAssignment.read();
                if (ed == null) ed = new Descriptor();
                ed.getControllerDescription().setSurgeryDaysAssignment(
                            surgeryDaysAssignment.get());  
            }
            catch (StoreException ex){
                displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
            } 
            avc.setDescriptor(ed);
            
            avc.setView(new View().make(
                View.Viewer.SCHEDULE_VIEW,
                avc, 
                getDesktopView()));


            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                doSetupDesktopViewMode();
            }
            
        }
        catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doRequestForViewClose(){
        String[] options = {"Yes", "No"};
        String message;
        if (!scheduleViewControllers.isEmpty()||!patientViewControllers.isEmpty()){
            message = "At least one patient or appointment view is active. Close application anyway?";
        }
        else {message = "Close The Clinic practice management system?";}
        int close = JOptionPane.showOptionDialog(getDesktopView(),
                        message,null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
        if (close == JOptionPane.YES_OPTION){
            this.isDesktopPendingClosure = true;
            if (!this.scheduleViewControllers.isEmpty()||!this.patientViewControllers.isEmpty()){
                requestViewControllersToCloseViews();
            }
            else {
                getDesktopView().dispose();
                System.exit(0);
            }    
        }
    }
    
    /**
     * method does following
     * -- constructs a new VC (ExportProgressViewControler)
     */
    private void doRequestForImportProgressViewController(){
        if (importProgressViewControllers.isEmpty()){
            importProgressViewControllers.add(
                                    new DataMigrationProgressViewController(this, getDesktopView(), getDescriptor()));
            DataMigrationProgressViewController ipvc = importProgressViewControllers.get(importProgressViewControllers.size()-1);
            ipvc.setView(new View().make(
                View.Viewer.MIGRATION_MANAGER_VIEW,
                ipvc, 
                getDesktopView()));
            
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            };
        }else{
            String message = "An export is currently in progress; hence "
                    + "the request for a new export process to start is ignored.";
            displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }   
    }
    
    private void doRequestForScheduleViewController(DesktopView desktopView){
        ScheduleViewController activeViewController = null;
        for(ScheduleViewController svc : scheduleViewControllers){
            /**
             * the Schedule VC has been called from the Desktop view
             * -- hence  the need to check if a schedule for today already exists
             */
            if (LocalDate.now().isEqual(svc.getDescriptor().getControllerDescription().getScheduleDay())){
                activeViewController = svc;
                break;
            }
        }
        if (activeViewController!=null)
            activeViewController.getView().toFront();
        else {
            Descriptor descriptor = new Descriptor();
            descriptor.getControllerDescription().setViewMode(ViewController.ViewMode.SCHEDULE_REFERENCED_DESKTOP_VIEW);
            descriptor.getControllerDescription().setScheduleDay(LocalDate.now());
            createNewAppointmentScheduleViewController(descriptor);
        }
    }
    
    private void doRequestForScheduleViewController(ScheduleViewController vc){
        ScheduleViewController activeViewController = null;
        for(ScheduleViewController svc : scheduleViewControllers){
                if(vc.getDescriptor().getViewDescription().getScheduleDay().
                        isEqual(svc.getDescriptor().getControllerDescription().getScheduleDay())){
                    activeViewController = svc;
                    break;
                }
        }
        if (activeViewController!=null)
            activeViewController.getView().toFront();
        else {
            Descriptor descriptor = new Descriptor();
            descriptor.getControllerDescription().setScheduleDay(
                    vc.getDescriptor().getViewDescription().getScheduleDay());
            createNewAppointmentScheduleViewController(descriptor);
        }
    }
    
    /**
     * Initialise new VC's descriptor with the Appointment property defined ActionEvent source's descriptor
     * @param e ActionEvent
     */
    private void doRequestForClinicalNoteViewController(ActionEvent e){
        ClinicalNoteViewController cvc = null;
        Descriptor descriptor = new Descriptor();
        ViewController vc = (ViewController)e.getSource();
        Appointment appointment = vc.getDescriptor()
                .getControllerDescription().getAppointment();
        clinicalNoteViewControllers.add(
                new ClinicalNoteViewController(
                        this,
                        getDesktopView()));
        cvc = clinicalNoteViewControllers
                .get(clinicalNoteViewControllers.size()-1);
        cvc.getDescriptor().getControllerDescription()
                .setAppointment(appointment);
        setView(new View().make(View.Viewer.CLINICAL_NOTE_VIEW,
                    cvc, 
                    getDesktopView()));
        clinicalNoteViewControllers
                .get(clinicalNoteViewControllers.size()-1).getView().toFront();

        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.INITIALISE_VIEW.toString());
         cvc.actionPerformed(actionEvent);

        if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                doSetupDesktopViewMode();
        }
    }
    
    private void doRequestForTreatmentViewController(ActionEvent e){
        TreatmentViewController tvc = null;
        if (treatmentViewControllers.isEmpty()){
            treatmentViewControllers.add(
                    new TreatmentViewController(
                            this,
                            getDesktopView()));
            tvc = treatmentViewControllers.get(treatmentViewControllers.size()-1);
            setView(new View().make(View.Viewer.TREATMENT_VIEW,
                        tvc, 
                        getDesktopView()));

            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.DesktopViewControllerActionEvent.INITIALISE_VIEW.toString());
             tvc.actionPerformed(actionEvent);

            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
        }else {
            treatmentViewControllers.get(0).getView().toFront();
        }//do nothing because only one patient notification VC allowed
    }
    
    private void doRequestForMedicalConditionViewController(ActionEvent e){
        MedicalConditionViewController mcvc = null;
        if (medicalConditionViewControllers.isEmpty()){
            medicalConditionViewControllers.add(
                    new MedicalConditionViewController(
                            this,
                            getDesktopView()));
            mcvc = medicalConditionViewControllers.get(medicalConditionViewControllers.size()-1);
            setView(new View().make(View.Viewer.MEDICAL_CONDITION_VIEW,
                        mcvc, 
                        getDesktopView()));

            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.DesktopViewControllerActionEvent.INITIALISE_VIEW.toString());
             mcvc.actionPerformed(actionEvent);

            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
        }else {
            medicalConditionViewControllers.get(0).getView().toFront();
        }//do nothing because only one medical condition VC allowed
    }

    private void doRequestForNotificationViewController(){
        if (notificationViewControllers.isEmpty()){
            try{
                notificationViewControllers.add(
                                            new NotificationViewController(this,getDesktopView()));
                NotificationViewController nvc = 
                        notificationViewControllers.get(notificationViewControllers.size()-1);
                nvc.setView(new View().make(View.Viewer.NOTIFICATION_VIEW/*SCHEDULE_VIEW*/,
                        nvc, 
                        getDesktopView()));
                
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                        doSetupDesktopViewMode();
                }
                


            }catch (StoreException ex){
                String message = ex.getMessage();
                JOptionPane.showMessageDialog(this.getDesktopView(), 
                        message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
            }
        }else {
            notificationViewControllers.get(0).getView().toFront();
        }//do nothing because only one patient notification VC allowed
    }
    
    private void doRequestForTestPatientViewController(){
        try{
            
            patientViewControllers.add(
                                    new PatientViewController(this, getDesktopView()));
            
            PatientViewController pvc = patientViewControllers.get(patientViewControllers.size()-1);
            pvc.setView(new View().make(
                View.Viewer.TEST_PATIENT_VIEW,
                pvc, 
                getDesktopView()));
            
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            } 
        
        }
        catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    private void doRequestForPatientViewController(){
        try{
            patientViewControllers.add(
                                    new PatientViewController(this, getDesktopView()));
            PatientViewController pvc = patientViewControllers.get(patientViewControllers.size()-1);
            pvc.setView(new View().make(
                View.Viewer.PATIENT_VIEW,
                pvc, 
                getDesktopView()));
            
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            } 
        }
        catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doRequestForViewNotification(){
            System.exit(0);
    }

    /**
     * Using the SwingWorker class facilitates communication between concurrent tasks
     * -- the SwingWorker-based task runs on a thread in the background 
     * -- which is designed to fire a property change event to a specified listener when a bound variable changes
     * -- i.e. whenever the setProgress(0..100) changes the value of the bound variable
     * -- the done() method is called when the doInBackground() method completes; and is executed on the Event Despatch 
     * -- which is used to send an action event to the ExportProgressViewController to indicate the completion of the task
     * @param entity:IEntityStoreType, which can be interrogated to determine if a collection of appointment or patient objects  have been specified
     * @param desktopViewController references the DesktopViewController object which is referenced in the Action Event sent in the done90 method  
     */
    private void startBackgroundThread(Entity entity,DesktopViewController desktopViewController){
        SwingWorker sw1 = new SwingWorker(){
        int appointmentRecordCount = 0;    
            @Override
            protected String doInBackground()  
            {
                List<String[]>dbfRecords = null;
                String result = null;
                int count = 0;
                try{
                    if (entity.getIsPatient()){
                        
                        /**
                         * 13/02/2024 08:50 code update
                         * -- creates a patient with a pid = 1
                         * -- required for the UNBOOKABLE 'appointment' schedule mechanism
                         * -- implemented by first 2 lines
                         */
                        Patient patient = new Patient(1);
                        patient.insert();
                        
                        Patient patientTable = (Patient)entity;
                            dbfRecords = patientTable.importEntityFromCSV();  
                            count = dbfRecords.size();
                            Iterator dbfRecordsIt = dbfRecords.iterator();
                            int recordCount = 1;
                            
                            while(dbfRecordsIt.hasNext()){
                                patient = patientTable.convertDBFToPatient((String[])dbfRecordsIt.next());
                                patient.reformat();
                                try{
                                    patient.insert();
                                }catch (StoreException ex){
                                displayErrorMessage(ex.getMessage(), "Desktop View Controller error", 
                                JOptionPane.WARNING_MESSAGE);
                                }   
                                recordCount++;
                                if (recordCount <= count){
                                    Integer percentage = recordCount*100/count;
                                    setProgress(percentage);
                                }
                                else {
                                    break;
                                }
                            }
                        dbfRecords.clear();  
                    
                    }else if (entity.getIsTreatment()){
                        Treatment treatment = (Treatment)entity;
                        count = 1;
                        recordCount = 0;
                        for(Treatment t : treatment.get()){
                            t.insert();
                        }
                        recordCount++;
                        if (recordCount <= count){
                            Integer percentage = recordCount*100/count;
                            setProgress(percentage);
                        }
                        
                    /*}else if (entity.getIsPrimaryCondition()){

                        PrimaryCondition pc = (PrimaryCondition)entity;
                        Patient patient = new Patient(1);
                        count = 1;
                        recordCount = 0;
                        for(Condition condition : pc.get()){
                            PrimaryCondition pCondition = (PrimaryCondition)condition;
                            pCondition.setPatient(patient);
                            pCondition.insert();
                        }
                        recordCount++;
                        if (recordCount <= count){
                            Integer percentage = recordCount*100/count;
                            setProgress(percentage);
                        }*/
                    }else if (entity.getIsAppointment()){
                        Appointment appointmentTable = (Appointment)entity;
                        //appointmentTable.create();
                        dbfRecords = appointmentTable.importEntityFromCSV();
                        setCount(dbfRecords.size());
                        Iterator dbfRecordsIt = dbfRecords.iterator();
                        setRecordCount(0);
                        while(dbfRecordsIt.hasNext()){
                            insertAppointments(
                                    appointmentTable.convertDBFRecordToAppointments(
                                            (String[])dbfRecordsIt.next()),
                                             appointmentTable);
                            setRecordCount(getRecordCount()+1);
                            if (getRecordCount() <= getCount()){
                                Integer percentage = getRecordCount()*100/getCount();
                                setProgress(percentage);
                            }
                        }//end of dbfRecords iteration
                        dbfRecords.clear();
                    }
                    /*28/03/2024
                    else if(entity.getIsPatientNote()){
                        recordCount = 0;
                        Appointment appointment = new Appointment();
                        appointment.setScope(Entity.Scope.ALL);
                        appointment.read();
                        setCount(appointment.count().x);
                        Iterator<Appointment> it = ((Appointment)appointment).get().iterator();
                        while (it.hasNext()){
                            Appointment a = it.next();
                            new PatientNote().createPatientNoteFromAppointment(a);
                            recordCount++;
                            if (recordCount <= getCount()){
                                Integer percentage = recordCount*100/getCount();
                                setProgress(percentage);
                            }
                        }
                    }else if (entity.getIsSecondaryCondition()){

                        SecondaryCondition sc = (SecondaryCondition)entity;
                        PrimaryCondition pc = sc.getPrimaryCondition();
                        PrimaryCondition pConditionFromStore = null;
                        Patient patient = new Patient(1);
                        count = 1;
                        recordCount = 0;
                        for(Condition primaryCondition : pc.get()){
                            PrimaryCondition pCondition = (PrimaryCondition)primaryCondition;
                            pCondition.setPatient(patient);
                            pCondition.setScope(Entity.Scope.SINGLE);
                            pConditionFromStore = pCondition.read();

                            for(Condition secondaryCondition : 
                                    pCondition.getSecondaryCondition().get()){
                                SecondaryCondition sCondition = (SecondaryCondition)secondaryCondition;
                                sCondition.setPrimaryCondition(pConditionFromStore);
                                sCondition.insert();
                            }
                        }
                        recordCount++;
                        if (recordCount <= count){
                            Integer percentage = recordCount*100/count;
                            setProgress(percentage);
                        }
                    }*/
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage(), "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
                return result;
            }
            
            private void insertAppointments(ArrayList<Appointment> appointments,
                    Appointment appointmentTable) throws StoreException {
                for(Appointment appointment : appointments){
                    appointment.insert();
                    appointmentRecordCount = appointmentRecordCount + 1;
                    /*
                    if (getRecordCount() <= getCount()){
                        Integer percentage = getRecordCount()*100/getCount();
                        setProgress(percentage);
                    }
                    else {
                        break;
                    }
                    */
                }
            }
            
            private int getRecordCount(){
                return recordCount;
            }
            
            private void setRecordCount(int value){
                recordCount = value;
            }
            
            private int getCount(){
                return count;
            }
            
            private void setCount(int value){
                count = value;
            }
            
            /**
             * Invoked when the doInBackground() method completes
             * -- used to send an action event to the ExportProgressViewController signalling task completion
             * -- uses also the specified IEntityStoreTYpe to determine the value of the event sent
             * -- i.e. either EXPORT_MIGRATED_PATIENTS_COMPLETED event or EXPORT_MIGRATED_APPOINTMENTS_COMPLETED event
             */
            @Override
            protected void done(){
                DesktopViewControllerActionEvent event = null;
                if (entity.getIsPatient()){
                    event = DesktopViewControllerActionEvent.MIGRATE_PATIENT_DATA_COMPLETED;
                    //getDesktopView().initialiseView();
                    
                }
                if (entity.getIsAppointment()){
                    event = DesktopViewControllerActionEvent.MIGRATE_APPOINTMENT_DATA_COMPLETED;
                    //getDesktopView().initialiseView();
                }
                if (entity.getIsPatientNote()){
                    event = DesktopViewControllerActionEvent.MIGRATE_PATIENT_NOTE_DATA_COMPLETED;
                    //getDesktopView().initialiseView();
                }
                
                if (entity.getIsPrimaryCondition()){
                    event = DesktopViewControllerActionEvent.MIGRATE_PRIMARY_CONDITION_DATA_COMPLETED;
                }
                
                if (entity.getIsSecondaryCondition()){
                    event = DesktopViewControllerActionEvent.MIGRATE_SECONDARY_CONDITION_DATA_COMPLETED;
                }
                
                if (entity.getIsTreatment()){
                    event = DesktopViewControllerActionEvent.MIGRATE_TREATMENT_DATA_COMPLETED;
                }
                
                DataMigrationProgressViewController evc = importProgressViewControllers.get(0);
                if (event!=null){
                    ActionEvent actionEvent = new ActionEvent(
                            desktopViewController,ActionEvent.ACTION_PERFORMED,
                            event.toString());
                    evc.actionPerformed(actionEvent);
                }else{
                    String message = "Unexpected null encountered for event in SwingWorker::done() method";
                    displayErrorMessage(message, "Desktop View Controller error", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        };
        
        DataMigrationProgressViewController evc = importProgressViewControllers.get(0);
        sw1.addPropertyChangeListener(evc.getView());
        sw1.execute();

    }
   
    private Point doRequestCountForAppointmentTable(){
        Point result = null;
        //07/08/2022
        Appointment appointment = new Appointment();
        appointment.setScope(Entity.Scope.ALL);
        try{
            result = appointment.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doRequestCountForAppointmentTable()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForPrimaryConditionTable(){
        Point result = null;
        
        PrimaryCondition pc = new PrimaryCondition();
        pc.setScope(Entity.Scope.ALL);
        try{
            result = pc.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doPatientNoteTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForSecondaryConditionTable(){
        Point result = null;
        
        SecondaryCondition sc = new SecondaryCondition();
        sc.setScope(Entity.Scope.ALL);
        try{
            result = sc.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doPatientNoteTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    /*
    private Point doRequestCountForPatientNoteTable(){
        Point result = null;
        //07/08/2022
        PatientNote patientNote = new PatientNote();
        patientNote.setScope(Entity.Scope.ALL);
        try{
            result = patientNote.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doPatientNoteTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    */
    private Point doRequestCountForClinicNoteTable(){
        Point result = null;
        //07/08/2022
        ClinicalNote clinicNote = new ClinicalNote();
        clinicNote.setScope(Entity.Scope.ALL);
        try{
            result = clinicNote.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doClinicNoteTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForTreatmentTable(){
        Point result = null;
        //07/08/2022
        Treatment treatment = new Treatment();
        treatment.setScope(Entity.Scope.ALL);
        try{
            result = treatment.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doTreatmentTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForPatientTable(){
        Point result = null;
        //07/08/2022
        Patient patient = new Patient();
        patient.setScope(Entity.Scope.ALL);
        try{
            result = patient.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doPatientTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForPatientNotificationTable(){
        Point result = null;
        Notification patientNotification = new Notification();
        try{
            patientNotification.setScope(Entity.Scope.ALL);
            result = patientNotification.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doPatientNotificationTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForSurgeryDaysAssignmentTable(){
        Point result = null;
        SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment();
        try{
            result = surgeryDaysAssignment.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doAppointmentTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private void doPropertyChangeEvent(DesktopViewControllerPropertyChangeEvent event,
                                        PropertyChangeListener view){
        pcSupport.removePropertyChangeListener(view);
        pcSupport.addPropertyChangeListener(view);
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(
                this,event.toString(),
                null,getDescriptor());
        pcSupport.firePropertyChange(pcEvent);
        //pcSupport.removePropertyChangeListener(view);
    }
    
    /**
     * 10/01/2023 07:29 update
     * -- Store static methods directly accessed to fetch PMS store and CSV import paths
     * -- these now being defined in environment variables
     * @param actionCommand
     * @param source 
     */
    
    private void doRequestForGetPath(ViewController.DesktopViewControllerActionEvent actionCommand,
                                    Object source){
        String path = null;
        DesktopViewControllerPropertyChangeEvent propertyChangeEvent = null;
        switch(actionCommand){
            case GET_APPOINTMENT_CSV_PATH_REQUEST:
                path = SystemDefinition.getPMSImportedAppointmentData();
                getDescriptor().getControllerDescription().setPathForAppointmentCSVData(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.APPOINTMENT_CSV_PATH_RECEIVED;
                break;
            case GET_PATIENT_CSV_PATH_REQUEST:
                path = SystemDefinition.getPMSImportedPatientData();
                getDescriptor().getControllerDescription().setPathForPatientCSVData(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.PATIENT_CSV_PATH_RECEIVED;
                break;
            case GET_PMS_STORE_PATH_REQUEST:
                if (SystemDefinition.getPMSStoreType().equals("ACCESS"))
                    path = SystemDefinition.getPMSStoreAccessURL();
                else path = SystemDefinition.getPMSStorePostgresSQLURL();
                getDescriptor().getControllerDescription().setPathForPMSStore(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.PMS_STORE_PATH_RECEIVED;
                break;
        }    
        doPropertyChangeEvent(propertyChangeEvent, (PropertyChangeListener)source); 

    }
    

    private void notifyMigrationActionCompleted(){
        pcSupport.addPropertyChangeListener(getDesktopView());
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
            DesktopViewController.DesktopViewControllerPropertyChangeEvent.PMS_STORE_PATH_RECEIVED.toString(),
            null,getDescriptor());
        pcSupport.firePropertyChange(pcEvent);
        pcSupport.removePropertyChangeListener(getDesktopView());
    }
    
    private void setDesktopViewMode(DesktopViewMode mode){
        this.desktopViewMode = mode;
    }
    
    private DesktopViewMode getDesktopViewMode(){
        return desktopViewMode;
    }
    
    private void doSetupDesktopViewMode(){
        pcSupport.addPropertyChangeListener(getDesktopView());
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
            DesktopViewController.
                    DesktopViewControllerPropertyChangeEvent.SET_DESKTOP_VIEW_MODE.toString(),
        null,new Descriptor());
        pcSupport.firePropertyChange(pcEvent);
        pcSupport.removePropertyChangeListener(getDesktopView());
    }
    
    /**
     * removes all data from the tables in the current PMS database
     */
    private void doRequestForDeleteDataFromPMSDatabase(){
        try{
            AppointmentTreatment at = new AppointmentTreatment();
            at.setScope(Entity.Scope.ALL); 
            at.delete();
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            ClinicalNote cn = new ClinicalNote();
            cn.setScope(Entity.Scope.ALL); 
            cn.delete();
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Treatment treatment = new Treatment();
            treatment.setScope(Entity.Scope.ALL); 
            treatment.delete();
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Appointment appointment = new Appointment();
            appointment.setScope(Entity.Scope.ALL);
            appointment.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment();
            surgeryDaysAssignment.setScope(Entity.Scope.ALL);
            surgeryDaysAssignment.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Notification patientNotification = new Notification();
            patientNotification.setScope(Entity.Scope.ALL);
            patientNotification.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            SecondaryCondition secondaryCondition = new SecondaryCondition();
            secondaryCondition.setScope(Entity.Scope.ALL);
            secondaryCondition.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            PrimaryCondition primaryCondition = new PrimaryCondition();
            primaryCondition.setScope(Entity.Scope.ALL);
            primaryCondition.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }

        try{
            Doctor doctor = new Doctor();
            doctor.setScope(Entity.Scope.ALL);
            doctor.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Medication medication = new Medication();
            medication.setScope(Entity.Scope.ALL);
            medication.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        /*28/03/2024
        try{
            PatientNote patientNote = new PatientNote();
            patientNote.setScope(Entity.Scope.ALL);
            patientNote.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }*/
        
        try{
            Treatment treatment = new Treatment();
            treatment.setScope(Entity.Scope.ALL);
            treatment.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Patient patient = new Patient();
            patient.setScope(Entity.Scope.ALL);
            patient.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getActionCommand().equals(ViewController.DesktopViewControllerActionEvent.
                VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString())){
            firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
            );
        }
        else{
            String s;
            s = e.getSource().getClass().getSimpleName();
            switch(s){
                case "DesktopView":
                    doActionEventForDesktopView(e);
                     break;
                case "ScheduleViewController":
                    doActionEventForScheduleViewController(e);
                    break;
                case "NotificationViewController":
                    doActionEventForNotificationViewController(e);
                    break;
                case "ClinicalNoteViewController":
                    doActionEventForClinicalNoteViewController(e);
                    break;
                case "MedicalConditionViewController":
                    doActionEventForMedicalConditionViewController(e);
                    break;
                case "TreatmentViewController":
                    doActionEventForTreatmentViewController(e);
                    break;
                case "PatientViewController":
                    doActionEventForPatientViewController(e);
                    break;
                case "DataMigrationProgressViewController":
                    doActionEventForImportProgressViewController(e);
                    break;
            }  
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {   
        boolean isExceptionRaised = false;
        isDataMigrationOptionEnabled = false;
        try{
            //System.out.println(args.length);
            String xmlFileName = System.getenv("PMS_SYSTEM_DEFINITION");
            TemplateReader.setTemplateFile(new File(xmlFileName));
            TemplateReader.setEntityTag("entity");
            TemplateReader.setEntityId("SystemDefinition");
            TemplateReader.setSectionId(null);
            SystemDefinition.setSystemDefinitions(
                    TemplateReader.extract(new HashMap<String,String>()));
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            
            if (SystemDefinition.getPMSOperationMode().equals("DATA_MIGRATION_ENABLED"))
                isDataMigrationOptionEnabled = true;
            
            String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
            switch (lookAndFeel){
                case "Metal":
                    //javax.swing.UIManager.setLookAndFeel("Metal"); do nothing
                    break;
                case "Windows":
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
            }
        }catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            isExceptionRaised = true;
        }catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            isExceptionRaised = true;
        }catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            isExceptionRaised = true;
        }catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            isExceptionRaised = true;
        }catch (TemplateReaderException ex){
            displayErrorMessage(ex.getMessage() + "\n"
                    + "Raised in DesktopViewController::main()",
                    "Desktop view controller error",
                    JOptionPane.WARNING_MESSAGE);
            isExceptionRaised = true;
        }
        if (!isExceptionRaised){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new DesktopViewController();
                }
            });  
        }else System.exit(0);
    }
    
    private void importDataFromListFiles(){
        
    }
    
    
    private void doPatientViewControllerChangeNotification(PropertyChangeEvent e){
        for(ScheduleViewController asvc: this.scheduleViewControllers){
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            REFRESH_DISPLAY_REQUEST.toString());
            asvc.actionPerformed(actionEvent); 
        }
        
        PatientViewController requestingPVC = 
                    (PatientViewController)e.getSource();
        for(PatientViewController pvc: this.patientViewControllers){
            if (!requestingPVC.equals(pvc)){
                firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                    pvc,
                    this,
                    null,
                    getDescriptor()       
                );  
            }
        }
        
        for(NotificationViewController pnvc : this.notificationViewControllers){
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            REFRESH_DISPLAY_REQUEST.toString());
            pnvc.actionPerformed(actionEvent); 
        }
    }
    
    private void doScheduleViewControllerChangeNotification(PropertyChangeEvent e){
        setDescriptor((Descriptor)e.getNewValue());
        for(PatientViewController pvc: this.patientViewControllers){
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                    pvc,
                    this,
                    null,
                    getDescriptor()       
            ); 
        }
        
        ScheduleViewController requestingSVC = 
                    (ScheduleViewController)e.getSource();
        for(ScheduleViewController svc: this.scheduleViewControllers){
            if (!requestingSVC.equals(svc)){
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                REFRESH_DISPLAY_REQUEST.toString());
                svc.actionPerformed(actionEvent); 
            }
        }
        
        
        
    } 
    
    private void doTreatmentViewControllerChangeNotification(PropertyChangeEvent e){
        setDescriptor((Descriptor)e.getNewValue());
        for(PatientViewController pvc: this.patientViewControllers){
            firePropertyChangeEvent(
                    ViewController.PatientViewControllerPropertyChangeEvent.
                            PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                    pvc,
                    this,
                    null,
                    getDescriptor()       
            ); 
        }
        for(ScheduleViewController svc: this.scheduleViewControllers){
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            REFRESH_DISPLAY_REQUEST.toString());
            svc.actionPerformed(actionEvent); 
        }
    } 
    /*
    private void doPropertyChangeEventScheduleViewController(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION:{
                setDescriptor((Descriptor)e.getNewValue());
                for(PatientViewController pvc: this.patientViewControllers){
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                    PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                            pvc,
                            this,
                            null,
                            getDescriptor()       
                    ); 
                }
                break;
            }
        }
    } 
    */
    /*
     private void doPropertyChangeEventScheduleViewController(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION:{
                setDescriptor((Descriptor)e.getNewValue());
                for(PatientViewController pvc: this.patientViewControllers){
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                    PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                            pvc,
                            this,
                            null,
                            getDescriptor()       
                    ); 
                }
                break;
            }
        }
    } 
    */
    public void propertyChange(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName =
                ViewController.DesktopViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            case PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doPatientViewControllerChangeNotification(e);
                break;
            case SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doScheduleViewControllerChangeNotification(e);
                break;
            case TREATMENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doTreatmentViewControllerChangeNotification(e);
                break;
                
        }
    }
    /*
    public void propertyChange(PropertyChangeEvent e){
        String viewController = e.getSource().getClass().getSimpleName();
        switch(ViewControllers.valueOf(viewController)){
            case ScheduleViewController:
                doScheduleViewControllerChangeNotification(e);
                break;
            case DesktopViewController:
                break;
            case PatientNotificationViewController:
                break;
            case PatientViewController:
                doPatientViewControllerChangeNotification(e);
                break;
  
        }
    }*/
    
    static class PMSStore {  

        static String getPath()throws StoreException{ 
            String path = null;
            String pmsStore = SystemDefinition.getPMSStoreType();
            /**
             * 11/01/2023 10:05 uddate
             * -- access system environment variable for path to PMS store 
             */
            switch (pmsStore){
                case "ACCESS":{
                    String url = SystemDefinition.getPMSStoreAccessURL();
                    path = url.substring(url.indexOf("//")+2);
                    
                    try{
                        File file = new File(path);
                        if (!SystemDefinition.getPMSDebug().equals("ENABLED")){
                            if (!file.exists()){
                                Entity.createPMSDatabase();
                                Patient patientTable = new Patient();
                                patientTable.create();
                                /*28/03/2024PatientNote patientNoteTable = new PatientNote();*/
                                patientTable.create();
                                Notification patientNotificationTable = new Notification();
                                patientNotificationTable.create();
                                Doctor doctor = new Doctor();
                                doctor.create();
                                Medication medication = new Medication();
                                PrimaryCondition pc = new PrimaryCondition();
                                pc.create();
                                SecondaryCondition sc = new SecondaryCondition();
                                sc.create();
                                SurgeryDaysAssignment surgeryDaysAssignmentTable = new SurgeryDaysAssignment();
                                surgeryDaysAssignmentTable.create();
                                Appointment appointmentTable = new Appointment();
                                appointmentTable.create();
                            }
                        }

                    }catch (StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "Exception message raised in "
                                + "DesktopViewController.PMSStore.getPath(ACCESS)";
                        displayErrorMessage(message,
                                "Desktop View Controller error", 
                                JOptionPane.WARNING_MESSAGE);
                    }
                    
                    break;
                }
                case "POSTGRES":
                    String url = SystemDefinition.getPMSStorePostgresSQLURL();
                    path = url;
                    break;
            }
            
            return path;
        }
        
        static boolean isSelected()throws StoreException{
            /**
             * 11/01/2023 10:05 update
             * -- 
             */
            boolean result = false;
            //String test = FilenameUtils.getName(getPath());
            if (!FilenameUtils.getName(getPath()).isEmpty())result = true;
            return result;
        }  
    }
    
    enum DesktopViewMode {CLINIC_LOGO, DESKTOP};
   
}