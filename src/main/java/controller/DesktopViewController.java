/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.controller;

import static clinicpms.controller.ViewController.displayErrorMessage;
import static clinicpms.controller.ViewController.ViewControllers;
import clinicpms.model.Entity;
import clinicpms.model.Entity.Scope;
import clinicpms._system_environment_variables.SystemDefinitions;
import org.apache.commons.io.FilenameUtils;
import clinicpms.model.*;
import clinicpms.repository.StoreException;//01/03/2023
import clinicpms.view.views.DesktopView;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author colin
 */
public class DesktopViewController extends ViewController{
    private boolean isFirstActionEventReceivedFromAppointmentScheduleViewController = true;
    private DesktopViewMode desktopViewMode;
    private boolean isDesktopPendingClosure = false;
    private DesktopView view = null;
    private ArrayList<AppointmentRemindersViewController> appointmentRemindersViewControllers = null;
    private ArrayList<PatientNotificationViewController> patientNotificationViewControllers = null;
    private HashMap<AppointmentScheduleViewController, AppointmentRemindersViewController> appointmentScheduleViewControllersMap = null;
    private ArrayList<PatientViewController> patientViewControllers = null;
    private ArrayList<ImportProgressViewController> importProgressViewControllers = null;
    private static Boolean isDataMigrationOptionEnabled = null;
    private PropertyChangeSupport pcSupport = null;
    private Descriptor entityDescriptor = null;
    private int count = 0;
    private int recordCount = 0;
    
    /*
    private void setControllerDescriptor(Descriptor value){
        this.entityDescriptor =  value;
    }
    */
    
    private Boolean getDataMigrationOption(){
        return isDataMigrationOptionEnabled;
    }
    private void setDataMigrationOption(Boolean value){
        isDataMigrationOptionEnabled = value;
    }       
    
    private DesktopViewController(){
               setControllerDescriptor(new Descriptor());
        /**
         * Constructor for DesktopView takes two arguments
         * -- object reference to view controller (this)
         * -- Boolean signifying whether view enables data migration functions
         */
        view = new DesktopView(this, isDataMigrationOptionEnabled, getControllerDescriptor() );
        //view.setSize(1020, 650);
        //view.setVisible(true);
        setView(view);
        
        
        //view.setContentPane(view);
        pcSupport = new PropertyChangeSupport(this);
        appointmentScheduleViewControllersMap = new HashMap<>();
        patientViewControllers = new ArrayList<>();
        importProgressViewControllers = new ArrayList<>();
        patientNotificationViewControllers = new ArrayList<>();
        appointmentRemindersViewControllers = new ArrayList<>();
        boolean isPMSStoreDefined;
        if (isDataMigrationOptionEnabled) {
            notifyMigrationActionCompleted();
            getView().initialiseView();
        }
        else{
            if ((SystemDefinitions.getPMSOperationMode()).equals("undefined")){
                displayErrorMessage("A PMS store has not been defined; ClinicPMS will abort\n"
                        + "Re-enter application in data migration mode by specifying DATA_MIGRATION_ENABLED on the command line",
                        "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            } 
        }
    }

    private void doActionEventForAppointmentRemindersViewController(ActionEvent e){
        AppointmentRemindersViewController arc = (AppointmentRemindersViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
                    
            /**
             * remove the VC which sent the action command from the mapped collection appointment schedule view controllers
             */
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:
                for (Map.Entry<AppointmentScheduleViewController, AppointmentRemindersViewController> entry :
                        appointmentScheduleViewControllersMap.entrySet()){
                    if (entry.getValue()!=null){
                        if (entry.getValue().equals(e.getSource())){
                            appointmentScheduleViewControllersMap.put(entry.getKey(), null);
                        }
                    }
                }
                break;
        }
    }
    
    private void doActionEventForPatientNotificationViewController(ActionEvent e){
        String message = null;
        PatientNotificationViewController pnvc = (PatientNotificationViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        this.view,
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.patientNotificationViewControllers.size()){
                    case 0:
                        message = "No PatientNotification view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (pnvc.equals(this.patientNotificationViewControllers.get(0))){
                            this.patientNotificationViewControllers.remove(0);
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
     * -- APPOINTMENT_HISTORY_CHANGE_NOTIFICATION
     * -- DISABLE_DESKTOP_CONTROLS_REQUEST
     * -- ENABLE_DESKTOP_CONTROLS_REQUEST
     * -- VIEW_CLOSED_NOTIFICATION
     * @param e:ActionEvent received; indicates which ActionCommand from above list was sent
     */
    private void doActionEventForAppointmentScheduleViewController(ActionEvent e){
        AppointmentScheduleViewController avc = (AppointmentScheduleViewController)e.getSource();
        ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:
                for (Map.Entry<AppointmentScheduleViewController, AppointmentRemindersViewController> entry :
                        appointmentScheduleViewControllersMap.entrySet()){
                    if (entry.getKey().equals(e.getSource())){
                        if (entry.getValue()!=null){
                            /**
                             * send VIEW_CONTROLLER_CLOSE_NOTIFICATION to appointee reminder list VC 
                             * -- then remove mapped entry from the collection of ASVCs
                             */
                            ActionEvent actionEvent = new ActionEvent(
                                    this,ActionEvent.ACTION_PERFORMED,
                                    ViewController.AppointmentRemindersViewControllerActionEvent.
                                            APPOINTMENT_SCHEDULE_VIEW_CLOSE_NOTIFICATION.toString());
                            entry.getValue().actionPerformed(actionEvent);             
                        }
                        appointmentScheduleViewControllersMap.remove(entry.getKey());
                        //break;
                    }
                    break;                  
                }
                
                /**
                 * after successfully removing the specified controller and view
                 * -- check to see if Desktop view is waiting to be closed; and continue closure of other controllers and views if so
                 * -- if at this stage there are no appointment or patient view controllers active, re-enable the DesktopView DATA menu and its window close control
                 */
                if (this.isDesktopPendingClosure){
                    this.requestViewControllersToCloseViews();
                }

                if (this.appointmentScheduleViewControllersMap.isEmpty() && 
                        this.patientViewControllers.isEmpty()){ 

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
                        getView(),
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
                    if (this.appointmentScheduleViewControllersMap.isEmpty() && 
                            this.patientViewControllers.isEmpty()){ 
                    }
                }
                break;
            case APPOINTMENT_VIEW_CONTROLLER_REQUEST:
                /**
                 * VC receives a request for a new AppointmentVC from a PatientVC
                 * -- the PatientVC view's EntityDescriptorFromView object defines an appointment for the selected patient
                 * -- the appointment date is used in the construction of a new AppointmentVC and associated appointment schedule view which includes the selected patient's appointment
                 */
                PatientViewController patientViewController = (PatientViewController)e.getSource();
                Optional<Descriptor> ed = Optional.of(patientViewController.getDescriptorFromView());
                createNewAppointmentScheduleViewController(ed);
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
                
            case IMPORT_EXPORT_PATIENT_DATA:
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
            case IMPORT_EXPORT_APPOINTMENT_DATA:
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
            case IMPORT_EXPORT_MIGRATED_SURGERY_DAYS_ASSIGNMENT:
                SurgeryDaysAssignment surgeryDaysAssignment = new SurgeryDaysAssignment();       
                try{
                    surgeryDaysAssignment.insert();
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
        Point theCount = null;
        
        try{
            /**
             * 11/01/2023 19:08 update
             * -- modified PMSStore.getPath() method creates a new Access database file if one doesn't already exists
             */
            PMSStore.getPath();
            ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
            switch (actionCommand){
                case VIEW_ACTIVATED_NOTIFICATION:
                    break;
                    
                case VIEW_CHANGED_NOTIFICATION:
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                            getView(),
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
                    getControllerDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getControllerDescriptor()   
                    );
                    break;
                case COUNT_PATIENT_TABLE_REQUEST:
                    theCount = doRequestCountForPatientTable();
                    getControllerDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getControllerDescriptor()
                    );
                    break;
                case COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST:
                    theCount = doRequestCountForPatientNotificationTable();
                    getControllerDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_NOTIFICATION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getControllerDescriptor()
                    );
                    break;
                case COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST:
                    theCount = doRequestCountForSurgeryDaysAssignmentTable();
                    getControllerDescriptor().getControllerDescription().setTableRowCount(theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SURGERY_DAYS_ASSIGNMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getControllerDescriptor()
                    );
                    break;
                case PATIENT_NOTIFICATION_VIEW_CONTROLLER_REQUEST:
                    doRequestForPatientNotificationViewController();
                    break;
                case VIEW_CLOSE_REQUEST:{
                    doRequestForViewClose();
                    break;
                }
                case APPOINTMENT_VIEW_CONTROLLER_REQUEST:{
                    doRequestForAppointmentScheduleViewController();
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
                    break;
                }
                case IMPORT_DATA_FROM_SOURCE:{                  
                        doRequestForImportProgressViewController();
                    break;
                }

            }
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(), " Desktop ViewController error",JOptionPane.WARNING_MESSAGE);
        }          
    }

    private DesktopView getView(){
        return this.view;
    }       
    private void setView(DesktopView view){
        this.view = view;
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
        
        if (!this.appointmentScheduleViewControllersMap.isEmpty()){
            for(Map.Entry<AppointmentScheduleViewController, 
                    AppointmentRemindersViewController> entry : 
                    this.appointmentScheduleViewControllersMap.entrySet()){
                if (entry.getValue()!=null){
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    entry.getValue().actionPerformed(actionEvent);
                }
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                entry.getKey().actionPerformed(actionEvent);
                
            }
        }
        if ((appointmentScheduleViewControllersMap.isEmpty()) && (patientViewControllers.isEmpty())){
            if (this.isDesktopPendingClosure){
                getView().dispose();
                System.exit(0);
            }
        } 
    }

    private void createNewAppointmentScheduleViewController(Optional<Descriptor> ed){
        try{
            AppointmentScheduleViewController avc =
                    new AppointmentScheduleViewController(this, getView(),ed);
            appointmentScheduleViewControllersMap.put(avc, null);
                /*
                appointmentScheduleViewControllers.add(
                                            new AppointmentScheduleViewController(this, getView(),ed));
                AppointmentScheduleViewController avc = 
                        appointmentScheduleViewControllers.get(appointmentScheduleViewControllers.size()-1);
                */
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                doSetupDesktopViewMode();
            }
            this.getView().getDeskTop().add(avc.getView());
            avc.getView().setVisible(true);
            avc.getView().setTitle("Appointments");
            avc.getView().setClosable(false);
            avc.getView().setMaximizable(false);
            avc.getView().setIconifiable(true);
            avc.getView().setResizable(false);
            avc.getView().setSelected(true);
            avc.getView().setSize(800,560);
            }
        catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }
        catch (PropertyVetoException ex){
            displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
            /*
            JOptionPane.showMessageDialog(getView(),
                                      new ErrorMessagePanel(ex.getMessage()));
            */
        }
    }

    private void doRequestForViewClose(){
        String[] options = {"Yes", "No"};
        String message;
        if (!appointmentScheduleViewControllersMap.isEmpty()||!patientViewControllers.isEmpty()){
            message = "At least one patient or appointment view is active. Close application anyway?";
        }
        else {message = "Close The Clinic practice management system?";}
        int close = JOptionPane.showOptionDialog(getView(),
                        message,null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
        if (close == JOptionPane.YES_OPTION){
            this.isDesktopPendingClosure = true;
            if (!this.appointmentScheduleViewControllersMap.isEmpty()||!this.patientViewControllers.isEmpty()){
                requestViewControllersToCloseViews();
            }
            else {
                getView().dispose();
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
                                    new ImportProgressViewController(this, getView(), getControllerDescriptor()));
            ImportProgressViewController evc = importProgressViewControllers.get(importProgressViewControllers.size()-1);
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
            this.getView().getDeskTop().add(evc.getView());
        }else{
            String message = "An export is currently in progress; hence "
                    + "the request for a new export process to start is ignored.";
            displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }   
    }
    
    private void doRequestForAppointmentScheduleViewController(){
        createNewAppointmentScheduleViewController(Optional.ofNullable(null));
    }
    
    private void doRequestForPatientNotificationViewController(){
        if (patientNotificationViewControllers.isEmpty()){
            try{
                patientNotificationViewControllers.add(
                                            new PatientNotificationViewController(this,getView()));
                PatientNotificationViewController pnvc = 
                        patientNotificationViewControllers.get(patientNotificationViewControllers.size()-1);
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                        doSetupDesktopViewMode();
                }
                //this.getView().getDeskTop().add(pnvc.getView());
                pnvc.getView().initialiseView();


            }catch (StoreException ex){
                String message = ex.getMessage();
                JOptionPane.showMessageDialog(this.getView(), 
                        message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
            }
        }else {
        }//do nothing because only one patient notification VC allowed
    }
                
    private void doRequestForPatientViewController(){
        try{
            patientViewControllers.add(
                                    new PatientViewController(this, getView()));
            PatientViewController pvc = patientViewControllers.get(patientViewControllers.size()-1);
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
            this.getView().getDeskTop().add(pvc.getView());
            pvc.getView().setVisible(true);
            pvc.getView().setClosable(false);
            pvc.getView().setMaximizable(false);
            pvc.getView().setIconifiable(true);
            pvc.getView().setResizable(false);
            pvc.getView().setSelected(true);
            pvc.getView().setSize(700
                    ,560);
        }
        catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
        }
        catch (PropertyVetoException ex){
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
            
            @Override
            protected String doInBackground()  
            {
                List<String[]>dbfRecords = null;
                String result = null;
                int count = 0;
                try{
                    if (entity.getIsPatient()){
                        Patient patientTable = (Patient)entity;
                            dbfRecords = patientTable.importEntityFromCSV();  
                            count = dbfRecords.size();
                            Iterator dbfRecordsIt = dbfRecords.iterator();
                            int recordCount = 0;

                            while(dbfRecordsIt.hasNext()){
                                Patient patient = patientTable.convertDBFToPatient((String[])dbfRecordsIt.next());
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
                    }

                    else if (entity.getIsAppointment()){
                        Appointment appointmentTable = (Appointment)entity;
                        //appointmentTable.create();
                        dbfRecords = appointmentTable.importEntityFromCSV();
                        setCount(dbfRecords.size());
                        Iterator dbfRecordsIt = dbfRecords.iterator();
                        setRecordCount(0);
                        while(dbfRecordsIt.hasNext()){
                            setRecordCount(getRecordCount()+1);
                            insertAppointments(
                                    appointmentTable.convertDBFRecordToAppointments(
                                            (String[])dbfRecordsIt.next()),
                                             appointmentTable);
                        }//end of dbfRecords iteration
                        dbfRecords.clear();
                    }
                     
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
                    if (getRecordCount() <= getCount()){
                        Integer percentage = getRecordCount()*100/getCount();
                        setProgress(percentage);
                    }
                    else {
                        break;
                    }
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
                if (entity.getIsPatient())event = DesktopViewControllerActionEvent.IMPORT_EXPORT_PATIENT_DATA_COMPLETED;
                if (entity.getIsAppointment())event = DesktopViewControllerActionEvent.IMPORT_EXPORT_APPOINTMENT_DATA_COMPLETED;
                
                ImportProgressViewController evc = importProgressViewControllers.get(0);
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
        
        ImportProgressViewController evc = importProgressViewControllers.get(0);
        sw1.addPropertyChangeListener(evc.getView());
        sw1.execute();

    }
   
    private Point doRequestCountForAppointmentTable(){
        Point result = null;
        //07/08/2022
        Appointment appointment = new Appointment();
        appointment.setScope(Scope.ALL);
        try{
            result = appointment.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doRequestCountForAppointmentTable()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForPatientTable(){
        Point result = null;
        //07/08/2022
        Patient patient = new Patient();
        patient.setScope(Scope.ALL);
        try{
            result = patient.count();
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage() + "\n Exception handled in doAppointmentTableCountRequest()",
                    "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Point doRequestCountForPatientNotificationTable(){
        Point result = null;
        Notification patientNotification = new Notification();
        try{
            patientNotification.setScope(Scope.ALL);
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
                null,getControllerDescriptor());
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
                path = SystemDefinitions.getPMSImportedAppointmentData();
                getControllerDescriptor().getControllerDescription().setPathForAppointmentCSVData(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.APPOINTMENT_CSV_PATH_RECEIVED;
                break;
            case GET_PATIENT_CSV_PATH_REQUEST:
                path = SystemDefinitions.getPMSImportedPatientData();
                getControllerDescriptor().getControllerDescription().setPathForPatientCSVData(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.PATIENT_CSV_PATH_RECEIVED;
                break;
            case GET_PMS_STORE_PATH_REQUEST:
                if (SystemDefinitions.PMSStoreType().equals("ACCESS"))
                    path = SystemDefinitions.getPMSStoreAccessURL();
                else path = SystemDefinitions.getPMSStorePostgresURL();
                getControllerDescriptor().getControllerDescription().setPathForPMSStore(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.PMS_STORE_PATH_RECEIVED;
                break;
        }    
        doPropertyChangeEvent(propertyChangeEvent, (PropertyChangeListener)source);         
    }
    

    private void notifyMigrationActionCompleted(){
        pcSupport.addPropertyChangeListener(view);
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
            DesktopViewController.DesktopViewControllerPropertyChangeEvent.MIGRATION_ACTION_COMPLETE.toString(),
            null,getControllerDescriptor());
        pcSupport.firePropertyChange(pcEvent);
        pcSupport.removePropertyChangeListener(view);
    }
    
    private void setDesktopViewMode(DesktopViewMode mode){
        this.desktopViewMode = mode;
    }
    
    private DesktopViewMode getDesktopViewMode(){
        return desktopViewMode;
    }
    
    private void doSetupDesktopViewMode(){
        pcSupport.addPropertyChangeListener(view);
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
            DesktopViewController.
                    DesktopViewControllerPropertyChangeEvent.SET_DESKTOP_VIEW_MODE.toString(),
        null,new Descriptor());
        pcSupport.firePropertyChange(pcEvent);
        pcSupport.removePropertyChangeListener(view);
    }
    
    /**
     * removes all data from the tables in the current PMS database
     */
    private void doRequestForDeleteDataFromPMSDatabase(){
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
            Patient patient = new Patient();
            patient.setScope(Entity.Scope.ALL);
            patient.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
    
        notifyMigrationActionCompleted();
    }
       /*
    public Descriptor getViewDescriptor(){
        return this.entityDescriptor;
    }
*/
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getActionCommand().equals(ViewController.DesktopViewControllerActionEvent.
                VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString())){
            firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getView(),
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
                case "AppointmentRemindersViewController":
                    doActionEventForAppointmentRemindersViewController(e);
                    break;
                case "AppointmentScheduleViewController":
                    doActionEventForAppointmentScheduleViewController(e);
                    break;
                case "PatientNotificationViewController":
                    doActionEventForPatientNotificationViewController(e);
                    break;
                case "PatientViewController":
                    doActionEventForPatientViewController(e);
                    break;
                case "ImportProgressViewController":
                    doActionEventForImportProgressViewController(e);
                    break;
            }  
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {   
        isDataMigrationOptionEnabled = false;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        
            if (SystemDefinitions.getPMSOperationMode().equals("DATA_MIGRATION_ENABLED"))
                isDataMigrationOptionEnabled = true;
            
       
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DesktopView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DesktopViewController();
            }
        });   
    }
    
    /*
    private void doAppointeeContactDetailsForSchedulePropertyChangeEvent(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION: 
                //could be more thane asvc so send event to each
                for(Map.Entry<AppointmentScheduleViewController,
                        AppointeeContactDetailsForScheduleViewController> entry : 
                        this.appointmentScheduleViewControllersMap.entrySet()){
                    firePropertyChangeEvent(
                        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENT_FOR_DAY_RECEIVED.toString(),
                        entry.getKey(),
                        this,
                        null,
                        e.getNewValue()
                    );
                }

                break;   
        }
    }
    */

    
    private void doPropertyChangeEventForAppointmentRemindersViewController(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                setControllerDescriptor((Descriptor)e.getNewValue());
                for(Map.Entry<AppointmentScheduleViewController,
                        AppointmentRemindersViewController> entry : 
                        this.appointmentScheduleViewControllersMap.entrySet()){
                    firePropertyChangeEvent(
                        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                APPOINTMENT_FOR_DAY_RECEIVED.toString(),
                        entry.getKey(),
                        this,
                        null,
                        e.getNewValue()
                    );
                }
                
                
                /*
                for (AppointmentScheduleViewController asvc : this.appointmentScheduleViewControllers){
                    firePropertyChangeEvent(
                            ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.
                                    APPOINTMENT_FOR_DAY_RECEIVED.toString(),
                            asvc,
                            this,
                            null,
                            getViewDescriptor()
                    );
                }
                break;
                */
        }
    }
    
    private void doPropertyChangeEventPatientViewController(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                for(AppointmentScheduleViewController asvc: this.appointmentScheduleViewControllersMap.keySet()){
                    ActionEvent actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            ViewController.DesktopViewControllerActionEvent.
                                    REFRESH_DISPLAY_REQUEST.toString());
                    asvc.actionPerformed(actionEvent); 
                }
                for(PatientNotificationViewController pnvc : this.patientNotificationViewControllers){
                    ActionEvent actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            ViewController.DesktopViewControllerActionEvent.
                                    REFRESH_DISPLAY_REQUEST.toString());
                    pnvc.actionPerformed(actionEvent); 
                }
                break;
        }
    }
    
    /**
     * handles the following property change events received from an AppointmentScheduleViewController
 -- APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION
 ---- fires an APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED property change event to each AppointeeContactDetailsForscheduleVC (assumes there could be more than one)
 ---- on entry event newValue is an Descriptor whose Appointment, Appointments and Day properties represent the current state in the AppointmentScheduleView sender  
 
 
 --
     * @param e 
     */
    private void doPropertyChangeEventAppointmentScheduleViewController(PropertyChangeEvent e){
        ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTMENT_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                setControllerDescriptor((Descriptor)e.getNewValue());
                for(AppointmentRemindersViewController acdfsvc: 
                        this.appointmentRemindersViewControllers){
                    firePropertyChangeEvent(
                            ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent.
                                    APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED.toString(),
                            acdfsvc,
                            this,
                            null,
                            getControllerDescriptor()
                    );
                }
                for(PatientViewController pvc: this.patientViewControllers){
                    firePropertyChangeEvent(
                            ViewController.PatientViewControllerPropertyChangeEvent.
                                    PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                            pvc,
                            this,
                            null,
                            getControllerDescriptor()       
                    ); 
                }
                break;
            case APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CONTROLLER_REQUEST:{
                /**
                 * 13/12/2023 checks if requesting ASVC already has an active 'appointee reminder list'
                 */
                AppointmentScheduleViewController asvc = (AppointmentScheduleViewController)e.getSource();
                AppointmentRemindersViewController appointeeAideMemoireVC =
                        (AppointmentRemindersViewController)
                        this.appointmentScheduleViewControllersMap.get(asvc);
                if (appointeeAideMemoireVC == null){
                    getControllerDescriptor().setViewDescription(((Descriptor)e.getNewValue()).getViewDescription());
                
                    try{
                        AppointmentRemindersViewController acdfsvc = 
                                new AppointmentRemindersViewController(this, getView(), getControllerDescriptor());
                        appointmentRemindersViewControllers.add(acdfsvc);
                        appointmentScheduleViewControllersMap.put(asvc, acdfsvc);
                        firePropertyChangeEvent(
                            ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent.
                                    APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED.toString(),
                            acdfsvc,//event target
                            this,//event source
                            null,
                            //?entityDescriptor//event related data 
                            getControllerDescriptor()
                        );
                    }catch (StoreException ex){
                        displayErrorMessage(ex.getMessage() + 
                                "\nRaised in createNewAppointeeContactDetailsForScheduleViewController()",
                                "DesktopViewController error", JOptionPane.WARNING_MESSAGE);
                        
                    }
                }
                else{
                    appointeeAideMemoireVC.getView().toFront();
                }
                break;
            }
        }
    }
    
    private void sendRefreshPropertyChangeEvent(Descriptor entityDescriptor){
        firePropertyChangeEvent(ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent.
                    APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED.toString(),
            appointmentRemindersViewControllers.get(0).getView(),//event target
            this,//event source
            null,
            entityDescriptor//event related data        
        );
    }
        
    
    public void propertyChange(PropertyChangeEvent e){
        String viewController = e.getSource().getClass().getSimpleName();
        switch(ViewControllers.valueOf(viewController)){
            case AppointmentRemindersViewController:
                doPropertyChangeEventForAppointmentRemindersViewController(e);
                break;
            case AppointmentScheduleViewController:
                doPropertyChangeEventAppointmentScheduleViewController(e);
                break;
            case DesktopViewController:
                break;
            case PatientNotificationViewController:
                break;
            case PatientViewController:
                doPropertyChangeEventPatientViewController(e);
                break;
  
        }
    }
    
    static class PMSStore {  

        static String getPath()throws StoreException{ 
            String path = null;
            String pmsStore = SystemDefinitions.PMSStoreType();
            /**
             * 11/01/2023 10:05 uddate
             * -- access system environment variable for path to PMS store 
             */
            switch (pmsStore){
                case "ACCESS":{
                    String url = SystemDefinitions.getPMSStoreAccessURL();
                    path = url.substring(url.indexOf("//")+2);

                    try{
                        File file = new File(path);
                        if (!SystemDefinitions.getPMSDebug().equals("ENABLED")){
                            if (!file.exists()){
                                Entity.createPMSDatabase();
                                Patient patientTable = new Patient();
                                patientTable.create();
                                Notification patientNotificationTable = new Notification();
                                patientNotificationTable.create();
                                SurgeryDaysAssignment surgeryDaysAssignmentTable = new SurgeryDaysAssignment();
                                surgeryDaysAssignmentTable.create();
                                Appointment appointmentTable = new Appointment();
                                appointmentTable.create();
                            }
                        }

                    }catch (StoreException ex){
                        displayErrorMessage("Exception message raised in doDesktopViewAction(), case IMPORT_DATA_FROM_SOURCE\n"
                                + ex.getMessage(),"Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                }
                case "POSTGRES":
                    String url = SystemDefinitions.getPMSStorePostgresURL();
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