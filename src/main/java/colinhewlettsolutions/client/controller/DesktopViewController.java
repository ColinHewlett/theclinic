/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.controller;

import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import colinhewlettsolutions.client.model.non_entity.Credential;
import colinhewlettsolutions.client.model.non_entity.JarFileFinder;
import colinhewlettsolutions.client.controller.exceptions.TemplateReaderException;
import colinhewlettsolutions.client.model.entity.*;
import org.apache.commons.io.FilenameUtils;
import colinhewlettsolutions.client.model.repository.StoreException;//01/03/2023
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import colinhewlettsolutions.client.view.View;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.swing.JInternalFrame;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author colin
 */
public class DesktopViewController extends ViewController{
    private int test = 0;
    private boolean isFirstActionEventReceivedFromAppointmentScheduleViewController = true;
    private DesktopViewMode desktopViewMode;
    private boolean isDesktopPendingClosure = false;
    private DesktopView desktopView = null;
    
    private ArrayList<ArchivedPatientsViewController> archivedPatientsViewControllers = new ArrayList<>();
    private ArrayList<ClinicalNoteViewController> clinicalNoteViewControllers = new ArrayList<>();
    private ArrayList<LoginViewController> loginViewControllers = new ArrayList<>();
    //private ArrayList<DataMigrationProgressViewController> importProgressViewControllers = new ArrayList<>();
    private ArrayList<ImageViewerViewController> imageViewerViewControllers = new ArrayList<>();
    private ArrayList<MedicalConditionViewController> medicalConditionViewControllers = new ArrayList<>();
    //private ArrayList<NotificationViewController> notificationViewControllers = new ArrayList<>();
    private ArrayList<PatientAppointmentDataViewController> patientAppointmentDataViewControllers = new ArrayList<>();
    private ArrayList<PatientDocumentStoreViewController> patientDocumentStoreViewControllers = new ArrayList<>();
    private ArrayList<PatientInvoiceViewController> patientInvoiceViewControllers = new ArrayList<>();
    private ArrayList<PatientMedicalHistoryViewController> patientMedicalHistoryViewControllers = new ArrayList<>();
    private ArrayList<PatientQuestionnaireViewController> patientQuestionnaireViewControllers = new ArrayList<>();
    private ArrayList<PatientRecallViewController> patientRecallViewControllers = new ArrayList<>();
    private ArrayList<PatientViewController> patientViewControllers = new ArrayList<>();
    private ArrayList<ScheduleViewController>scheduleViewControllers = new ArrayList<>();
    private ArrayList<ToDoViewController> toDoViewControllers = new ArrayList<>();
    private ArrayList<TreatmentViewController> treatmentViewControllers = new ArrayList<>();
    private ArrayList<UserSystemWideSettingsViewController> userSystemWideSettingsViewControllers = new ArrayList<>();

    private static Boolean isDataMigrationOptionEnabled = null;
    
    private PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);
    private int count = 0;
    private int recordCount = 0;
    private TemplateReader templateReader = null;
    private colinhewlettsolutions.client.controller.SystemDefinition systemDefinition;
    
    private void removeAllViewControllers(){
        ArrayList viewControllers = new ArrayList<>();
        viewControllers.add(clinicalNoteViewControllers);
        viewControllers.add(loginViewControllers);
        viewControllers.add(medicalConditionViewControllers);
        //viewControllers.add(notificationViewControllers);
        viewControllers.add(patientAppointmentDataViewControllers);
        viewControllers.add(patientDocumentStoreViewControllers);
        viewControllers.add(patientInvoiceViewControllers);
        viewControllers.add(patientMedicalHistoryViewControllers);
        viewControllers.add(patientQuestionnaireViewControllers);
        viewControllers.add(patientRecallViewControllers);
        viewControllers.add(patientViewControllers);
        viewControllers.add(scheduleViewControllers);
        viewControllers.add(toDoViewControllers);
        viewControllers.add(treatmentViewControllers);
        viewControllers.add(userSystemWideSettingsViewControllers);
        
        for (var collection : viewControllers){
            ArrayList c = (ArrayList)collection;
            while(!c.isEmpty()){
                c.remove(0);
            }
        }

    }
    
    public ViewController getPVC(){
        return this.patientViewControllers.get(0);
    }
    public ViewController getSVC(){
        return this.scheduleViewControllers.get(0);
    }
    
    public int getPVCSize(){
        PatientViewController pvc = this.patientViewControllers.get(0);
        ArrayList<Appointment> appointments = 
                (ArrayList<Appointment>)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENTS);
        return appointments.size();
    }
    
    public int getSVCSize(){
        ScheduleViewController svc = this.scheduleViewControllers.get(0);
        ArrayList<Appointment> appointments = 
                (ArrayList<Appointment>)svc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENTS);
        if (appointments==null){
            return 0;
        }else return appointments.size();
    }
    
    public enum Actions{
        ARCHIVED_PATIENTS_VIEW_CONTROLLER_REQUEST,
        CHANGE_USER_PASSWORD_REQUEST,
        CLINIC_LOGO_VIEW_MODE_NOTIFICATION,
        CLOSE_PATIENT_VIEW_WITH_SAME_PATIENT_REQUEST,
        CLOSE_SCHEDULE_VIEW_WITH_SAME_DATE_REQUEST,
        COMMENT_MIGRATION_REQUEST,
        DESKTOP_VIEW_MODE_NOTIFICATION,
        IMAGE_VIEWER_VIEW_CONTROLLER_REQUEST,
        INITIALISE_VIEW_CONTROLLER,
        LOGIN_VIEW_CONTROLLER_REQUEST,
        LOGOUT_REQUEST,
        MODAL_VIEWER_ACTIVATED_NOTIFICATION,
        MODAL_VIEWER_CLOSED_NOTIFICATION,
        MEDICAL_CONDITION_VIEW_CONTROLLER_REQUEST,
        PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_REQUEST,
        PATIENT_DOCUMENT_STORE_VIEW_CONTROLLER_REQUEST,
        PATIENT_INVOICE_VIEW_CONTROLLER_REQUEST,
        PRINT_PATIENT_MEDICAL_HISTORY_REQUEST,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST,
        PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST,
        PATIENT_RECALL_VIEW_CONTROLLER_REQUEST,
        PATIENT_VIEW_CONTROLLER_REQUEST,
        PRINT_NEW_PATIENT_DETAILS_REQUEST,
        PRINT_SCHEDULE_REQUEST,
        REFRESH_DISPLAY_REQUEST,
        REFRESH_TO_DO_DISPLAY_REQUEST,
        SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST,
        TO_DO_VIEW_CONTROLLER_REQUEST,
        TREATMENT_VIEW_CONTROLLER_REQUEST,
        USER_LOGIN_NOTIFICATION,
        USER_SYSTEM_WIDE_FACTORY_SETTINGS_REQUEST,
        USER_SETTINGS_INITIALISATION_REQUEST,
        USER_SYSTEM_WIDE_SETTINGS_REQUEST,
        USER_SYSTEM_WIDE_SETTINGS_VIEW_CONTROLLER_REQUEST,
        
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSED_NOTIFICATION,
        VIEW_CLOSE_REQUEST,
        VIEW_CONTROLLER_ACTIVATED_NOTIFICATION,
        VIEW_CONTROLLER_CHANGED_NOTIFICATION,
        VIEW_CONTROLLER_CLOSE_NOTIFICATION
    }
    
    public enum Properties{
        ARCHIVED_PATIENTS_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        CASCADE_DESKTOP_VIEWS,
        DESKTOP_VIEW_CHANGED_NOTIFICATION,
        MEDICAL_CONDITION_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        PROGRESS_MONITOR_SETUP_NOTIFICATION_RECEIVED,
        SET_DESKTOP_VIEW_MODE,
        SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        TO_DO_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        TREATMENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION,
        USER_SYSTEM_WIDE_SETTINGS_RECEIVED,
        VIEW_CHANGED_NOTIFICATION
    }
    
    public enum SystemExitCode{
        INVALID_DOCUMENT_STORE_FOLDER,
        INVALID_PRINT_FOLDER,
        INVALID_SYSTEM_DEFINITION_XML_PATH,
        INVALID_SYSTEM_DEFINITION_XSD_PATH,
        NORMAL_EXIT,
        MISSING_COMMAND_LINE_ARGUMENT,
        MAIN_METHOD_EXCEPTION
    }
    
    public static void systemExitFor(SystemExitCode value, Exception ex){
        int status = 0;
        String message = "";
        switch (value){
            case INVALID_DOCUMENT_STORE_FOLDER ->{
                message = "System exited with status '3': 'document_store' folder does not exist";
                status=3;
                break;
            }
            case INVALID_PRINT_FOLDER ->{
                message = "System exited with status '4': 'print_folder' does not exist";
                status=4;
                break;
            }
            case MISSING_COMMAND_LINE_ARGUMENT ->{
                message = "System exited with status '1': missing command line argument";
                status = 1;
                break;
            }
            case MAIN_METHOD_EXCEPTION ->{
                if (ex!=null){
                    message = "System exited with status '2': " + ex.getMessage();
                    status=2;
                }
                break;
            }
            case NORMAL_EXIT ->{
                break;
            }
            case INVALID_SYSTEM_DEFINITION_XML_PATH ->{
                message = "System exited with status (5): SystemDefinition.xml cannot be located";
                status = 5;
                break;
            }
            case INVALID_SYSTEM_DEFINITION_XSD_PATH ->{
                message = "System exited with status (6): SystemDefinition.xsd cannot be located";
                status = 6;
                break;
            }
        }
        System.exit(status);
    }
    
    /**
     * On entry if the jar file executed is from within Netbeans a command line argument exists
     * @param args 
     */
    public DesktopViewController(String[] args){
        String projectId = "";
        String xmlFilename = "";
        String xsdFilename = "";
        System.out.println(JarFileFinder.getPath());
        System.out.println("Compiler version = " + System.getProperty("java.version"));
        try{
            /**
             * if currently executing jar file is stand alone and a command line does not exist
             * -- derive system folder location from the jar file
             * -- else derive system folder location from the command line argument
             */
            String test = JarFileFinder.getName();
            if(!JarFileFinder.getName().equals("")){
                if (args.length==0){
                    xmlFilename = JarFileFinder.getPath().substring(0,JarFileFinder.getPath().lastIndexOf('/') + 1) + "/SystemDefinition.xml";
                    xsdFilename = JarFileFinder.getPath().substring(0,JarFileFinder.getPath().lastIndexOf('/') + 1) + "/SystemDefinition.xsd";
                    projectId = JarFileFinder.getPath().substring(0,JarFileFinder.getPath().lastIndexOf('/'));
                    projectId = projectId.substring(projectId.lastIndexOf('/')+1);
                }else{
                    projectId = args[0];
                    xmlFilename = projectId + "/SystemDefinition.xml";
                    xsdFilename = projectId + "/SystemDefinition.xsd";
                }
            }else {
                /**
                 * main method has already checked a command line argument must exist in the case where this is a Netbeans launch of the app
                 */
                projectId = args[0];
                String the_projectId = projectId.substring(projectId.lastIndexOf('/') + 1);
                switch(the_projectId){
                    case "Fractals" ->{
                        xmlFilename = projectId + "/SystemDefinition.xml";
                        xsdFilename = projectId + "/SystemDefinition.xsd";
                        break;
                    }
                    case "PMS" ->{
                        xmlFilename = projectId + "/SystemDefinition.xml";
                        xsdFilename = projectId + "/SystemDefinition.xsd";
                        break;
                    }
                    
                }
                projectId = the_projectId;
            }
            
            /**
             * check if derived xml and xsd filenames exist
             * if either does not exit system with an error status
             */
            Path xmlPath = Path.of(xmlFilename);
            if (!Files.exists(xmlPath)){
                systemExitFor(SystemExitCode.INVALID_SYSTEM_DEFINITION_XML_PATH, null);
            }
            Path xsdPath = Path.of(xsdFilename);
            if (!Files.exists(xsdPath)){
                systemExitFor(SystemExitCode.INVALID_SYSTEM_DEFINITION_XSD_PATH, null);
            }
            templateReader = new TemplateReader();
            if(!templateReader.validateXMLSchema(new File(xsdFilename), new File(xmlFilename))){
                throw new TemplateReaderException(
                        "Invalid XML document format encountered",TemplateReaderException.ExceptionType.SAX_EXCEPTION);     
            }

            this.templateReader.setTemplateFile(new File(xmlFilename));
            this.templateReader.setSectionId("SystemDefinition");
            setDescriptor(new Descriptor());
            this.templateReader.setProjectId("UNIVERSAL");
            HashMap<String, Object> map1 = this.templateReader.extract(new HashMap<>());
            this.templateReader.setProjectId(projectId);
            HashMap<String, Object> map2 = this.templateReader.extract(new HashMap<>());
            map1.putAll(map2);
            for (Map.Entry<String,Object> entry : map1.entrySet()){
                SystemDefinition.Properties key = SystemDefinition.Properties.valueOf(entry.getKey());
                getDescriptor().getControllerDescription().setProperty(key, entry.getValue());
            }
            
            Boolean isDebugSMTPSet = (Boolean)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DEBUG_SMTP);
            if (isDebugSMTPSet){
                System.out.println("DEBUG_SMTP enabled");
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SMTP_SERVER_OUT, "smtp.gmail.com");
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.SMTP_USER, "colin.hewlett.solutions@gmail.com");
            }
            
            //doTemporaryFix();
            System.out.println("Database url = " + (String)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DATABASE_URL));
            setUserFactorySettings();
            Repository repository = 
                    new Repository((String)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DATABASE_TYPE),
                            (String)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DATABASE_URL));
            Entity.setRepository(repository);
            repository.getConnection();
            
            String lookAndFeel = (String)getDescriptor().getControllerDescription().getProperty(colinhewlettsolutions.client.controller.SystemDefinition.Properties.LOOK_AND_FEEL);
            switch (lookAndFeel){
                case "Metal" ->
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                case "Windows" ->
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
            }
        }catch (ClassNotFoundException | 
                UnsupportedLookAndFeelException |
                InstantiationException |
                IllegalAccessException |
                //IOException |
                StoreException |
                TemplateReaderException 
                ex) {
                displayErrorMessage(ex.getMessage(),"Desktop View Controller error",JOptionPane.WARNING_MESSAGE);
                colinhewlettsolutions.client.controller.SystemDefinition.setSystemExitCode(2);
                SystemDefinition.setSystemExitCode(2);
                systemExitFor(SystemExitCode.MAIN_METHOD_EXCEPTION,ex);
        }
        desktopView = new DesktopView(this);
        desktopView.setLocationRelativeTo(null);
        setDesktopView(desktopView);
        desktopView.initialiseView();
        desktopView.setVisible(true);
    }

    private void doTemporaryFix(){
        Path path = null;
        Object object  = null;       
        /**
         * temporary fix to ensure integrity of DOCUMENT_STORE property
         * -- ensures its a Path object
         * -- if path does not exist system exited with appropriate status
         */
        object = getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.DOCUMENT_STORE);
        if (object instanceof String){
            path = Paths.get(object.toString());
            getDescriptor().getControllerDescription().
                setProperty(SystemDefinition.Properties.DOCUMENT_STORE,path);
        }
        path = (Path)getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.DOCUMENT_STORE);
        
        if (!Files.exists(path)){
            systemExitFor(SystemExitCode.INVALID_DOCUMENT_STORE_FOLDER, null);
        }
        
        /**
         * temporary fix to ensure integrity of PRINT_FOLDER property
         * -- ensures its a Path object
         * -- if path does not exist system exited with appropriate status
         */
        object = getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PRINT_FOLDER);
        if (object instanceof String){
            path = Paths.get(object.toString());
            getDescriptor().getControllerDescription().
                setProperty(SystemDefinition.Properties.PRINT_FOLDER,path);
        }
        path = (Path)getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PRINT_FOLDER);
        
        if (!Files.exists(path)){
            systemExitFor(SystemExitCode.INVALID_PRINT_FOLDER, null);
        }
    }
    public void updateProjectIdEntity(File xmlFile, String newValue) throws IOException {
        String xml = Files.readString(xmlFile.toPath());
        Pattern pattern = Pattern.compile("(<!ENTITY\\s+PROJECT_ID\\s+\")([^\"]*)(\">)");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            String updated = matcher.replaceFirst("$1" + newValue + "$3");
            Files.writeString(xmlFile.toPath(), updated);
        } else {
            throw new IOException("PROJECT_ID entity not found in DOCTYPE.");
        }
    }

    
    private void setUserSettings(User user, HashMap<SystemDefinition.Properties,HashMap<Properties,Object>> settings){
        
    }
    
    private HashMap<SystemDefinition.Properties, HashMap<SystemDefinition.Properties,Object>> userFactorySettings = null;
    private void setUserFactorySettings(HashMap<SystemDefinition.Properties,HashMap<SystemDefinition.Properties,Object>> value){
        userFactorySettings = value;
    }
    private HashMap<SystemDefinition.Properties,HashMap<SystemDefinition.Properties,Object>> getUserFactorySettings(){
        return userFactorySettings;
    }
    
    

    private void setUserFactorySettings(){
        HashMap<SystemDefinition.Properties,Object> userScheduleDiaryColorFactorySettingsMap = new HashMap<>();
        HashMap<SystemDefinition.Properties,Object> userScheduleListColorFactorySettingsMap = new HashMap<>();
        HashMap<SystemDefinition.Properties,Object> userSystemWideFactorySettingsMap = new HashMap<>();;
        HashMap<SystemDefinition.Properties,HashMap<SystemDefinition.Properties,Object>> userFactorySettings = new HashMap<>();
        //initialise schedule diary color settings
        userScheduleDiaryColorFactorySettingsMap.put(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_BACKGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_FOREGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND));
        userScheduleDiaryColorFactorySettingsMap.put(
                SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND));
        //initialise user schedule list color settings
        userScheduleListColorFactorySettingsMap.put(
                SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND));
        userScheduleListColorFactorySettingsMap.put(
                SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND));
        userScheduleListColorFactorySettingsMap.put(
                SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND));
        userScheduleListColorFactorySettingsMap.put(
                SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND));
        userScheduleListColorFactorySettingsMap.put(
                SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND));
        userScheduleListColorFactorySettingsMap.put(
                SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND));
        //initialise user system wide settings
        userSystemWideFactorySettingsMap.put(
                SystemDefinition.Properties.TITLED_BORDER_COLOR,
                (Color)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR));
        userSystemWideFactorySettingsMap.put(
                SystemDefinition.Properties.TITLED_BORDER_FONT,
                (Font)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT));
        //initialise factory setting collection
        userFactorySettings.put(SystemDefinition.Properties.USER_SCHEDULE_DIARY_SETTINGS, userScheduleDiaryColorFactorySettingsMap);
        userFactorySettings.put(SystemDefinition.Properties.USER_SCHEDULE_LIST_SETTINGS, userScheduleDiaryColorFactorySettingsMap);
        userFactorySettings.put(SystemDefinition.Properties.USER_SYSTEM_WIDE_SETTINGS, userScheduleDiaryColorFactorySettingsMap);
        setUserFactorySettings(userFactorySettings);
    }
    
    private boolean checkCredential(Credential credential){
        return credential.getIsPasswordValid() && credential.getIsUsernameValid();
    }
    
    private void doActionForImageViewerViewController(ActionEvent e){
        boolean hasFoundViewController = false;
        ImageViewerViewController ivvc = (ImageViewerViewController)e.getSource();
        DesktopViewController.Actions actionCommand = 
                DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION ->{
                hasFoundViewController = false;
                for(ImageViewerViewController vc : imageViewerViewControllers){
                    if (vc.equals(ivvc)){
                        hasFoundViewController = true;
                        break;
                    }
                }
                if (hasFoundViewController){
                    if (!imageViewerViewControllers.remove(ivvc)){
                        String message = "Problem arose on attempt to remove an "
                                + "ImageViewer view conroller";
                        displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    String message = "Could not locate the ImageViewer view controller when trying to remove it";
                    displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            default ->{
                String message = "Unexpected ImageViewer view controller action command encountered: " + actionCommand;
                displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void doActionEventForPatientDocumentStoreViewController(ActionEvent e){
        boolean hasFoundViewConroller = false;
        PatientDocumentStoreViewController pdsvc = (PatientDocumentStoreViewController)e.getSource();
        DesktopViewController.Actions actionCommand = 
                DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case IMAGE_VIEWER_VIEW_CONTROLLER_REQUEST ->{
                doRequestForImageViewerViewController(e);
                break;
            }
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION ->{
                hasFoundViewConroller = false;
                for(PatientDocumentStoreViewController vc : patientDocumentStoreViewControllers){
                    if (vc.equals(pdsvc)){
                        hasFoundViewConroller = true;
                        break;
                    }
                }
                if (hasFoundViewConroller){
                    if (!patientDocumentStoreViewControllers.remove(pdsvc)){
                        String message = "Problem arose on attempt to remove a "
                                + "PatientDocumentStore view conroller";
                        displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }else{
                    String message = "Could not locate the PatientDocumentStore view controller when trying to remove it";
                    displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            default ->{
                String message = "Unexpected PatientDocumentStore view controller action command encountered: " + actionCommand;
                displayErrorMessage(
                                message,"DesktopViewController error",
                                JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void doActionEventForClinicalNoteViewController(ActionEvent e){
        String message = null;
        ClinicalNoteViewController cvc = (ClinicalNoteViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
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
                    }
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
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
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
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
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
    
    private void doActionEventForToDoViewController(ActionEvent e){
        String message = null;
        ToDoViewController tdvc = (ToDoViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.toDoViewControllers.size()){
                    case 0:
                        message = "No ToDo view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (tdvc.equals(this.toDoViewControllers.get(0))){
                            this.toDoViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find ToDo view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one ToDo view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    message = message + "\n"
                            + "Raised in doActionEventForToDoViewController(case VIEW_CONTROLLER_CLOSE_NOTIFICATION)";
                    displayErrorMessage(message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }   
        }
    }

    /*private void doActionEventForNotificationViewController(ActionEvent e){
        String message = null;
        NotificationViewController pnvc = (NotificationViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
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
    }*/
    
    /**
     * ActionEvent responder; action events sent by an ActionViewController include
 -- SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST
 ---- 
 -- APPOINTMENT_HISTORY_CHANGE_NOTIFICATION
 -- DISABLE_DESKTOP_CONTROLS_REQUEST
 -- ENABLE_DESKTOP_CONTROLS_REQUEST
 -- VIEW_CLOSED_NOTIFICATION
     * @param e:ActionEvent received; indicates which ActionCommand from above list was sent
     */
    private void doActionEventForScheduleViewController(ActionEvent e){
        ClinicalNoteViewController _cnvc = null;
        ScheduleViewController avc = (ScheduleViewController)e.getSource();
        DesktopViewController.Actions actionCommand =
                    DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case CLOSE_SCHEDULE_VIEW_WITH_SAME_DATE_REQUEST:
                for(ScheduleViewController _svc : this.scheduleViewControllers){
                    if (!avc.equals(_svc)){
                        if(avc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY).equals(
                                _svc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                            this.firePropertyChangeEvent(
                                    ScheduleViewController.Properties.CLOSE_VIEW_REQUEST_RECEIVED.toString(),
                                    _svc.getView(),
                                    _svc,
                                    null,
                                    null
                            );
                        }
                    }
                }
                break;
            case PATIENT_VIEW_CONTROLLER_REQUEST:
                //javax.swing.SwingUtilities.invokeLater(() -> { 
                    doRequestForPatientViewControllerForDefinedPatient(e);
                //});
                
                break;
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST:  
                //getDescriptor().getControllerDescription().setScheduleViewMode(ScheduleViewMode.LIST);
                doRequestForScheduleViewController(avc);
                break;
            //case SCHEDULE_DIARY_VIEW_CONTROLLER_REQUEST:
                //getDescriptor().getControllerDescription().setScheduleViewMode(ScheduleViewMode.DIARY);
                //doRequestForScheduleViewController(avc);
            case TO_DO_VIEW_CONTROLLER_REQUEST:
                doRequestForToDoViewController();
                break;
            case USER_SYSTEM_WIDE_SETTINGS_VIEW_CONTROLLER_REQUEST:
                doRequestForUserSystemWideSettingsViewController(e);
                break;
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                if (!this.scheduleViewControllers.remove(avc)){
                    String message = "Could not find AppointmentViewController in "
                                            + "DesktopViewController collection.";
                    displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                }else{
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
    
    private void doActionEventForArchivedPatientsViewController(ActionEvent e){
        String message = null;
        ArchivedPatientsViewController vc = (ArchivedPatientsViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION ->{
                /*
                firePropertyChangeEvent(
                        ArchivedPatientsViewController.Properties.VIEW_CHANGE_NOTIFICATION.toString(),
                        vc.getView(),
                        this,
                        null,
                        null
                );*/
                
                if (!this.patientAppointmentDataViewControllers.isEmpty()){
                    PatientAppointmentDataViewController padvc = patientAppointmentDataViewControllers.get(0);
                    firePropertyChangeEvent(
                            PatientAppointmentDataViewController.Properties.VIEW_CHANGE_NOTIFICATION.toString(),
                            padvc,
                            this,
                            null,
                            null
                    );
                }
                
                break;
            }
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION ->{
                switch (this.archivedPatientsViewControllers.size()){
                    case 0:
                        message = "No archived patients view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (vc.equals(this.archivedPatientsViewControllers.get(0))){
                            this.archivedPatientsViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find archived patients view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one archived patients view view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForArchivedPatientsViewController"
                            + "(case = " + actionCommand.toString() + ")\n"
                            + message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } 
        }
    }
    
    private void doActionEventForPatientAppointmentDataViewController(ActionEvent e){
        String message = null;
        PatientAppointmentDataViewController vc = (PatientAppointmentDataViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case PATIENT_RECALL_VIEW_CONTROLLER_REQUEST ->{
                doRequestForPatientRecallViewController(e);
                break;
            }
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION ->{
                
                /**
                 * remove existing code
                 
                 firePropertyChangeEvent(
                        ArchivedPatientsViewController.Properties.VIEW_CHANGE_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );*/
                
                /**
                 * check for an active ArchivedPatientsViewController
                 * -- yes; send property change event 
                 */
                if (!this.archivedPatientsViewControllers.isEmpty()){
                   ArchivedPatientsViewController avc =  archivedPatientsViewControllers.get(0);
                   firePropertyChangeEvent(
                            ArchivedPatientsViewController.Properties.VIEW_CHANGE_NOTIFICATION.toString(),
                            avc,
                            this,
                            null,
                            null
                    );
                }
                
                break;
            }
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION ->{
                switch (this.patientAppointmentDataViewControllers.size()){
                    case 0:
                        message = "No patient appointment data view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (vc.equals(this.patientAppointmentDataViewControllers.get(0))){
                            this.patientAppointmentDataViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find patient appointment data view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one patient appointment data view view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForPatientAppointmentDataViewController"
                            + "(case = " + actionCommand.toString() + ")\n"
                            + message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } 
        }
    }
    
    private void doActionEventForPatientQuestionnaireViewController(ActionEvent e){
        String message = null;
        PatientQuestionnaireViewController vc = (PatientQuestionnaireViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.patientQuestionnaireViewControllers.size()){
                    case 0:
                        message = "No patient questionnaire view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (vc.equals(this.patientQuestionnaireViewControllers.get(0))){
                            this.patientQuestionnaireViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find patient questionnaire view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one patient questionnaire view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForPatientQuestionnaireViewController"
                            + "(case = " + actionCommand.toString() + ")\n"
                            + message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } 
        }
    }
    
    private void doActionEventForPatientMedicalHistoryViewController(ActionEvent e){
        String message = null;
        PatientMedicalHistoryViewController vc = (PatientMedicalHistoryViewController)e.getSource();
        Actions actionCommand =
                    Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:{
                switch (this.patientMedicalHistoryViewControllers.size()){
                    case 0:
                        message = "No patient medical history view controllers found in "
                                                    + "DesktopViewController collection.";
                        break;
                    case 1:
                        if (vc.equals(this.patientMedicalHistoryViewControllers.get(0))){
                            this.patientMedicalHistoryViewControllers.remove(0);
                        }
                        else{
                            message = "Could not find patient medical history view controller in "
                                                    + "DesktopViewController collection.";
                        }
                        break;
                    default:
                        message = "More than one patient medical history view controller found in "
                                                    + "DesktopViewController collection.";
                        break;
                }
                if (message!=null){
                    displayErrorMessage("Raised in doActionEventForPatientMedicalHistoryViewController"
                            + "(case = " + actionCommand.toString() + ")\n"
                            + message,
                            "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } ;
        }
    }
    
    private void doActionEventForLoginViewController(ActionEvent e){
        LoginViewController lvc = (LoginViewController)e.getSource();
        DesktopViewController.Actions actionCommand =
                    DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case USER_SETTINGS_INITIALISATION_REQUEST ->{
                break;
            }
            case USER_LOGIN_NOTIFICATION -> {
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL, 
                        lvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL));
                this.getDesktopView().enableMenus();
                break;
            }
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION -> {
                Credential credential = (Credential)lvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL);
                if (credential!=null){
                    if (!credential.getIsPasswordValid() || !credential.getIsUsernameValid()) {
                        System.exit(0);
                    }else{
                        if (!this.loginViewControllers.remove(lvc)){
                            String message = "Could not find LoginViewController in "
                                                    + "DesktopViewController collection.";
                            displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                        }
                        else {
                            if(!loginViewControllers.isEmpty()){
                                String message = "Unexpected occurrence: a Login View Controller is still current after the closure of a similar View Controller";
                                displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }else {
                    System.exit(0);
                }  
            }
        }
    }
    
    private void doActionEventForUserSystemWideSettingsViewController(ActionEvent e){
        UserSystemWideSettingsViewController vc = null;
        vc = (UserSystemWideSettingsViewController)e.getSource();
        DesktopViewController.Actions actionCommand =
                    DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION ->{
                if (!this.userSystemWideSettingsViewControllers.remove(vc)){
                    String message = "Could not find UserSettingsViewController in "
                                            + "DesktopViewController collection.";
                    displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS_VIEW_CONTROLLER_REQUEST ->{
                String username = ((Credential)getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL)).getUsername();
                User user = new User(username);
                UserSettings userSettings = new UserSettings(user);
                userSettings.setScope(Entity.Scope.USER_SYSTEM_WIDE_SETTINGS);
                try{
                    userSettings = userSettings.read();
                    for(Map.Entry<SystemDefinition.Properties,Object> entry : userSettings.getSettings().entrySet()){
                        vc.getDescriptor().getControllerDescription().
                                        setProperty(entry.getKey(), entry.getValue());
                    }
                    if (getDesktopView().getDeskTop().getAllFrames().length > 0){
                        for (JInternalFrame frame : getDesktopView().getDeskTop().getAllFrames()){
                            if (frame instanceof View view ) {
                                ViewController _vc = view.getMyController();
                                for(Map.Entry<SystemDefinition.Properties,Object> entry : userSettings.getSettings().entrySet()){
                                    _vc.getDescriptor().getControllerDescription().
                                                    setProperty(entry.getKey(), entry.getValue());
                                }
                                firePropertyChangeEvent(
                                            Properties.
                                                    USER_SYSTEM_WIDE_SETTINGS_RECEIVED.toString(),
                                            view,
                                            this,
                                            null,
                                            null
                                );
                            }
                        }
                    }
                    break;
                    
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n";
                    message = message + "Handle in DesktopView|Controller::actionPerformed( case = " + actionCommand + " )";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }

        }
    }
    
    private void doActionEventForUserSettingViewController(ActionEvent e){
        UserSystemWideSettingsViewController vc = null;
        DesktopViewController.Actions actionCommand = DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION ->{
                vc = (UserSystemWideSettingsViewController)e.getSource();
                if (!this.userSystemWideSettingsViewControllers.remove(vc)){
                    String message = "Could not find UserSettingsViewController in "
                                            + "DesktopViewController collection.";
                    displayErrorMessage(message,"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
                }
                break;
            }

        }
    }
    
    private void doActionEventForPatientViewController(ActionEvent e){
        PatientViewController pvc = null;
        Iterator<ClinicalNoteViewController> clinicalNoteViewControllerIterator = null;
        
        /*ViewController.DesktopViewControllerActionEvent actionCommand =
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());*/
        
        DesktopViewController.Actions actionCommand = DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CONTROLLER_CHANGED_NOTIFICATION:
                firePropertyChangeEvent(
                        Properties.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                );
                break;
            case VIEW_CONTROLLER_CLOSE_NOTIFICATION:
                /**
                 * Check if the patient belonging to this PVC is the appointee in an appointment belonging to active ClinicalNOte view
                 * if yes send PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED message to PVC's view with appropriate message
                 * if no 
                 * -- send message to PVS's view.setClosed(true)
                 * -- close the view controller here as normal
                 */
                pvc = (PatientViewController)e.getSource();
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
            case CLOSE_PATIENT_VIEW_WITH_SAME_PATIENT_REQUEST:
                pvc = (PatientViewController)e.getSource();
                for(PatientViewController _pvc : this.patientViewControllers){
                    if (!pvc.equals(_pvc)){
                        if(pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY).equals(
                                _pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                            this.firePropertyChangeEvent(
                                     PatientViewController.Properties.CLOSE_VIEW_REQUEST_RECEIVED.toString(),
                                    _pvc.getView(),
                                    _pvc,
                                    null,
                                    null
                            );
                        }
                    }
                }
                break;
            case PATIENT_DOCUMENT_STORE_VIEW_CONTROLLER_REQUEST:
                doRequestForPatientDocumentStoreViewController(e);
                break;
            case PATIENT_INVOICE_VIEW_CONTROLLER_REQUEST:
                doRequestForPatientInvoiceViewController(e);
                break;
            case PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST:
                doRequestForPatientMedicalHistoryViewController(e);
                break;
            case PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST:
                doRequestForPatientQuestionnaireViewController(e);
                break;
            case PRINT_PATIENT_MEDICAL_HISTORY_REQUEST:
                doRequestToPrintPatientMedicalHistory(e);
                break;
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST:
                /**
                 * VC receives a request for a new AppointmentVC from a PatientVC
                 * -- a default new descriptor is constructed
                 * -- the pvc's descriptor's schedule day is included in the new descriptor
                 * -- as is the SCHEDULE_REFERENCED_FROM_PATIENT_VIEW view mode which is used in the schedule view's initialisation
                 */
                PatientViewController patientViewController = (PatientViewController)e.getSource();
                
                /*patientViewController.getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.CONTROLLER_VIEW_MODE, ControllerViewMode.LIST);*/
                
                Descriptor descriptor = getNewTemplatedDescriptor();
                descriptor.getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, 
                        patientViewController.getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                descriptor.getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE, 
                        ViewController.ViewMode.SCHEDULE_REFERENCED_FROM_PATIENT_VIEW);
                
                createNewAppointmentScheduleViewController(descriptor);
                break;
            case TO_DO_VIEW_CONTROLLER_REQUEST:
                doRequestForToDoViewController();
                break;
        }
    }
    
    /**
     * 
     * @param e source of event is X_DesktopView object
     */
    private void doActionEventForDesktopView(ActionEvent e){ 
        DesktopViewController.Actions actionCommand = null;

        actionCommand = DesktopViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case ARCHIVED_PATIENTS_VIEW_CONTROLLER_REQUEST ->{
                doRequestForArchivedPatientsViewController();
                break;
            }
            case CHANGE_USER_PASSWORD_REQUEST ->{
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.LOGIN_VIEW_MODE, ViewController.LoginViewMode.OLD_PASSWORD_CHECK);
                doRequestForLoginViewController(e);
                break;
            }
            case CLINIC_LOGO_VIEW_MODE_NOTIFICATION ->{
                setDesktopViewMode(DesktopViewMode.CLINIC_LOGO);
                break;
            }
            case DESKTOP_VIEW_MODE_NOTIFICATION ->{
                setDesktopViewMode(DesktopViewMode.DESKTOP);
                break;
            }
            /*
            case LOGIN_VIEW_CONTROLLER_REQUEST ->{
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.LOGIN_VIEW_MODE, ViewController.LoginViewMode.LOGIN_CHECK);
                doRequestForLoginViewController(e);
                break;
            }*/
            case LOGOUT_REQUEST ->{
                getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL, new Credential());
                break;
            }
            case MEDICAL_CONDITION_VIEW_CONTROLLER_REQUEST ->{
                doRequestForMedicalConditionViewController(e);
                break;
            }
            /*
            case NOTIFICATION_VIEW_CONTROLLER_REQUEST ->{
                doRequestForNotificationViewController();
                break;
            }*/
            case PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_REQUEST ->{
                doRequestForPatientAppointmentDataViewController();
                break;
            }
            case PATIENT_VIEW_CONTROLLER_REQUEST ->{
                doRequestForPatientViewController();
                break;
            }
            case PRINT_SCHEDULE_REQUEST ->{
                doPrintAppointmentScheduleForDay(
                        (LocalDate)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
                break;
            }
            case PRINT_NEW_PATIENT_DETAILS_REQUEST ->{
                doPrintNewPatientDetailsRequest(e);
                break;
            }
            case SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST ->{ 
                doRequestForScheduleViewController2((DesktopView)e.getSource());
                break;
            }
            case TO_DO_VIEW_CONTROLLER_REQUEST ->{
                doRequestForToDoViewController();
                break;
            }
            case TREATMENT_VIEW_CONTROLLER_REQUEST ->{
                doRequestForTreatmentViewController(e);
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS_VIEW_CONTROLLER_REQUEST ->{
                doRequestForUserSystemWideSettingsViewController(e);
                break;   
            }
            case VIEW_ACTIVATED_NOTIFICATION ->{
                break;
            }
            case VIEW_CHANGED_NOTIFICATION ->{
                firePropertyChangeEvent(
                        Properties.
                                DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                        getDesktopView(),
                        this,
                        null,
                        null
                ); 
                break;
            }
            case VIEW_CLOSE_REQUEST ->{
                doRequestForViewClose();
                break;
            }
            /*
            case VIEW_CLOSED_NOTIFICATION ->{// user has attempted to close Desktop view
                doRequestForViewNotification();
                break;
            } */ 
            default ->{
                //doCheckForMigrationActionCommand(e);
                break;
            }
        }
                 
    }
    
    /*
    private void doCheckForMigrationActionCommand(ActionEvent e){
        Point theCount = null;   
        DesktopViewController.Actions actionCommand = DesktopViewController.Actions.valueOf(e.getActionCommand());
        try{
            switch(actionCommand){
                case DELETE_DATA_FROM_PMS_DATABASE_REQUEST ->{
                        doRequestForDeleteDataFromPMSDatabase();
                        getDesktopView().initialiseView();
                        break;
                    }
                case MIGRATE_DATA_FROM_SOURCE_VIEW_REQUEST ->{                  
                    PMSStore.getPath();   
                    doRequestForImportProgressViewController();
                    break;
                }
                case GET_APPOINTMENT_CSV_PATH_REQUEST ->{
                    doRequestForGetPath(actionCommand, e.getSource());
                    break;
                }
                case GET_PATIENT_CSV_PATH_REQUEST ->{
                    doRequestForGetPath(actionCommand, e.getSource());
                    break;
                }
                case GET_PMS_STORE_PATH_REQUEST ->{
                    doRequestForGetPath(actionCommand, e.getSource());
                    break;
                }
                case COUNT_APPOINTMENT_TABLE_REQUEST ->{
                    theCount = doRequestCountForAppointmentTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()   
                    );
                    break;
                }
                case COUNT_PATIENT_TABLE_REQUEST ->{
                    theCount = doRequestCountForPatientTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                }
                case COUNT_CLINIC_NOTE_TABLE_REQUEST ->{
                    theCount = doRequestCountForClinicNoteTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    CLINIC_NOTE_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                }
                case COUNT_TREATMENT_TABLE_REQUEST ->{
                    theCount = doRequestCountForTreatmentTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    TREATMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                }
                case COUNT_PRIMARY_CONDITION_TABLE_REQUEST ->{
                    theCount = doRequestCountForPrimaryConditionTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                }
                case COUNT_SECONDARY_CONDITION_TABLE_REQUEST ->{
                    theCount = doRequestCountForSecondaryConditionTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SECONDARY_CONDITION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                }
                case COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST ->{
                    theCount = doRequestCountForPatientNotificationTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_NOTIFICATION_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                    break;
                }
                case COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST ->{
                    theCount = doRequestCountForSurgeryDaysAssignmentTable();
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.TABLE_ROW_COUNT, theCount);
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    SURGERY_DAYS_ASSIGNMENT_TABLE_COUNT_RECEIVED.toString(),
                            (PropertyChangeListener)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                    );
                }
            }
        }catch (StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Exception raised in DesktopViewController.doActionEventForDesktopView("
                    + actionCommand + ")";
            displayErrorMessage(message, " Desktop ViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }*/
    
    private void requestViewControllersToCloseViews(){
        if (!this.patientViewControllers.isEmpty()){
            Iterator<PatientViewController> pvcIterator = patientViewControllers.iterator();
            while(pvcIterator.hasNext()){
                PatientViewController pvc = pvcIterator.next();
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                pvc.actionPerformed(actionEvent);    
            }
        }
        
        if (!this.scheduleViewControllers.isEmpty()){
            Iterator<ScheduleViewController> avcIterator = scheduleViewControllers.iterator();
            while(avcIterator.hasNext()){
                ScheduleViewController avc = avcIterator.next();
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        Actions.VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
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
                ed.getControllerDescription().
                        setProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT, surgeryDaysAssignment.get());
                  
            }
            catch (StoreException ex){
                displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
            } 
            avc.setDescriptor(ed);
            //assume only LIST format available
            avc.setView(new View().make(View.Viewer.SCHEDULE_LIST_VIEW,avc,getDesktopView()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.INITIALISE_VIEW_CONTROLLER.toString());
             avc.actionPerformed(actionEvent);
         
            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                this.firePropertyChangeEvent(
                    Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                    getDesktopView(),
                    this,
                    null,
                    null
                );
            }

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
    /*
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
    }*/
    
    /**
     * 
     * @param desktopView 
     */
    private void doRequestForScheduleViewController2(DesktopView desktopView){
        /**
         * Only one schedule view controller is allowed with a view with the same date as the SVC about to be created
         * -- if an SVC already exists with such a view; the desktop VC sends it a CLOSE_VIEW_REQUEST_RECEIVED property change event
         */
        ScheduleViewController targetSVC = null;
        for(ScheduleViewController svc : scheduleViewControllers){
            /**
             * the Schedule VC has been called from the Desktop view
             * -- hence  the need to check if a schedule for today already exists
             */
            if (LocalDate.now().isEqual(
                    (LocalDate)svc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY))){
                targetSVC = svc;
                break;
            }
        }
        if (targetSVC!=null){
            this.firePropertyChangeEvent(
                    ScheduleViewController.Properties.CLOSE_VIEW_REQUEST_RECEIVED.toString(), 
                    targetSVC.getView(),
                    targetSVC,
                    null,
                    null
            );
        }
        Descriptor descriptor = getNewTemplatedDescriptor();
        descriptor.getControllerDescription().
                setProperty(SystemDefinition.Properties.VIEW_MODE,
                        ViewController.ViewMode.SCHEDULE_REQUESTED_FROM_DESKTOP_VIEW);
        
        /*descriptor.getControllerDescription().
                setProperty(SystemDefinition.Properties.CONTROLLER_VIEW_MODE, ControllerViewMode.LIST);*/
        descriptor.getControllerDescription().
                setProperty(SystemDefinition.Properties.SCHEDULE_DAY, LocalDate.now());
        createNewAppointmentScheduleViewController(descriptor);
  
    } 
    
    private Descriptor getNewTemplatedDescriptor(){
        Descriptor descriptor = new Descriptor();
        for (Map.Entry<SystemDefinition.Properties,Object> entry : getDescriptor().getControllerDescription().getProperties().entrySet()){
            descriptor.getControllerDescription().setProperty(entry.getKey(),entry.getValue());   
        }
        return descriptor;
    }
    
    
    private void doRequestForScheduleViewController(ScheduleViewController vc){
        ScheduleViewController activeViewController = null;
        for(ScheduleViewController svc : scheduleViewControllers){
            LocalDate vDescriptorDate = 
                    (LocalDate)vc.getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
            LocalDate cDescriptorDate = 
                    (LocalDate)svc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY);

            if(vDescriptorDate.isEqual(cDescriptorDate)){
                activeViewController = svc;
                break;
            }
        }
        if (activeViewController!=null)
            activeViewController.getView().toFront();
        else {
            Descriptor descriptor = getNewTemplatedDescriptor();
            
            descriptor.getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE,
                    vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.VIEW_MODE));
            descriptor.getControllerDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, 
                    vc.getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));

            createNewAppointmentScheduleViewController(descriptor);
        }
        
        if (scheduleViewControllers.size()>1){
                this.firePropertyChangeEvent(
                    Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                    getDesktopView(),
                    this,
                    null,
                    null
                );
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
        Appointment appointment = 
                (Appointment)vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);

        clinicalNoteViewControllers.add(
                new ClinicalNoteViewController(
                        this,
                        getDesktopView()));
        cvc = clinicalNoteViewControllers
                .get(clinicalNoteViewControllers.size()-1);
        cvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
        setView(new View().make(View.Viewer.CLINICAL_NOTE_VIEW,
                    cvc, 
                    getDesktopView()));
        clinicalNoteViewControllers
                .get(clinicalNoteViewControllers.size()-1).getView().toFront();

        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.Actions.INITIALISE_VIEW_CONTROLLER.toString());
         cvc.actionPerformed(actionEvent);

        if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                doSetupDesktopViewMode();
        }
        if (getDesktopView().getDeskTop().getAllFrames().length>1){
            this.firePropertyChangeEvent(
                    Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                    getDesktopView(),
                    this, 
                    null,
                    null
            );
        }
    }

    private void doRequestForLoginViewController(ActionEvent e){
        if (loginViewControllers.isEmpty()){
            LoginViewController lvc = new LoginViewController(this, getDesktopView());
            Descriptor descriptor = getNewTemplatedDescriptor();
            lvc.setDescriptor(descriptor);
            this.loginViewControllers.add(lvc);
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.INITIALISE_VIEW_CONTROLLER.toString());
            lvc.actionPerformed(actionEvent);
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
        }else{
            String message = "Only one Login View Congtroller can be active";
            displayErrorMessage(
                    message,"DesktopViewController error",
                    JOptionPane.WARNING_MESSAGE);
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
                    DesktopViewController.Actions.INITIALISE_VIEW_CONTROLLER.toString());
             tvc.actionPerformed(actionEvent);

            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
        }else {
            treatmentViewControllers.get(0).getView().toFront();
        }//do nothing because only one patient notification VC allowed
        if (getDesktopView().getDeskTop().getAllFrames().length>1){
            this.firePropertyChangeEvent(
                    Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                    getDesktopView(),
                    this, 
                    null,
                    null
            );
        }
    }
    
    private void doRequestToPrintPatientMedicalHistory(ActionEvent e){
        boolean isPMVCForSamePatientActive = false;
        PatientMedicalHistoryViewController pmhvc = null;
        PatientMedicalHistoryViewController activeVCForSamePatient = null;
        PatientViewController pvc = (PatientViewController)e.getSource();
        Patient patient = (Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        if (patient!=null){
            if (patient.getIsKeyDefined()){
                //if (!patient.getKey().equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY)){
                //if (!patient.equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY)){
                  if (!patient.equals(new Patient(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY))){
                    for (PatientMedicalHistoryViewController vc : patientMedicalHistoryViewControllers){
                        if (vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                            isPMVCForSamePatientActive = true;
                            activeVCForSamePatient = vc;
                            break;
                        }
                    }
                    if (!isPMVCForSamePatientActive){
                        patientMedicalHistoryViewControllers.add(
                            new PatientMedicalHistoryViewController(
                            this,
                            getDesktopView()));
                        pmhvc = patientMedicalHistoryViewControllers.get(
                            patientMedicalHistoryViewControllers.size()-1);
                        pmhvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                        activeVCForSamePatient = pmhvc;
                    }
                    ActionEvent actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            PatientMedicalHistoryViewController.Actions.PRINT_PATIENT_MEDICAL_HISTORY_REQUEST.toString());
                    activeVCForSamePatient.actionPerformed(actionEvent);
                }
            }
        }
    }
  
    private void doRequestForPatientQuestionnaireViewController(ActionEvent e){
        boolean isPQVCForSamePatientActive = false;
        PatientQuestionnaireViewController activeVCForSamePatient = null;
        PatientQuestionnaireViewController pqhvc = null;
        PatientViewController pvc = (PatientViewController)e.getSource();
        Patient patient = (Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        if (patient!=null){
            if (patient.getIsKeyDefined()){
                //if (!patient.getKey().equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY)){
                //if (!patient.equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY)){
                if (!patient.equals(new Patient(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY))){
                    for (PatientQuestionnaireViewController vc : patientQuestionnaireViewControllers){
                        if (vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                            isPQVCForSamePatientActive = true;
                            activeVCForSamePatient = vc;
                            break;
                        }
                    }
                    if (!isPQVCForSamePatientActive){
                        patientQuestionnaireViewControllers.add(
                            new PatientQuestionnaireViewController(
                            this,
                            getDesktopView()));
                        pqhvc = patientQuestionnaireViewControllers.get(
                            patientQuestionnaireViewControllers.size()-1);
                        pqhvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                        setView(new View().make(View.Viewer.PATIENT_QUESTIONNAIRE_VIEW,
                                pqhvc, 
                                getDesktopView()));
                        /*
                        ActionEvent actionEvent = new ActionEvent(
                                this,ActionEvent.ACTION_PERFORMED,
                                DesktopViewController.DesktopViewControllerActionEvent.INITIALISE_VIEW.toString());
                         mcvc.actionPerformed(actionEvent);*/

                        if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                                doSetupDesktopViewMode();
                        }
                    }else {
                        activeVCForSamePatient.getView().toFront();
                    }//do nothing because only one medical condition VC allowed
                    if (getDesktopView().getDeskTop().getAllFrames().length>1){
                        this.firePropertyChangeEvent(
                                Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                                getDesktopView(),
                                this, 
                                null,
                                null
                        );
                    }
                }
            }
        }
    }
    
    
    
    private void doRequestForPatientInvoiceViewController(ActionEvent e){
        boolean isPIVCForSamePatientActive = false;
        PatientInvoiceViewController activeVCForSamePatient = null;
        PatientInvoiceViewController pivc = null;
        PatientViewController pvc = (PatientViewController)e.getSource();
        Patient patient = (Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        Appointment appointment = (Appointment)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        for (PatientInvoiceViewController vc : patientInvoiceViewControllers){
            if (vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                isPIVCForSamePatientActive = true;
                activeVCForSamePatient = vc;
                break;
            }
        }
        if (!isPIVCForSamePatientActive){
            patientInvoiceViewControllers.add(
                new PatientInvoiceViewController(
                this,
                getDesktopView()));
            pivc = patientInvoiceViewControllers.get(
                patientInvoiceViewControllers.size()-1);
            pivc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
            pivc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
            setView(new View().make(View.Viewer.PATIENT_INVOICE_VIEW,
                pivc, 
                getDesktopView()));         
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }else {
                activeVCForSamePatient.getView().toFront();
            }//do nothing because only one medical condition VC allowed
        }
    }
    
    private void doRequestForPatientMedicalHistoryViewController(ActionEvent e){
        boolean isPMVCForSamePatientActive = false;
        PatientMedicalHistoryViewController activeVCForSamePatient = null;
        PatientMedicalHistoryViewController pmhvc = null;
        PatientViewController pvc = (PatientViewController)e.getSource();
        Patient patient = (Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        //Patient patient = pvc.getDescriptor().getControllerDescription().getPatient();
        if (patient!=null){
            if (patient.getIsKeyDefined()){
                //if (!patient.getKey().equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY)){
                //if (!patient.equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY)){
                if (!patient.equals(new Patient(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY))){
                    for (PatientMedicalHistoryViewController vc : patientMedicalHistoryViewControllers){
                        if (vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                            isPMVCForSamePatientActive = true;
                            activeVCForSamePatient = vc;
                            break;
                        }
                    }
                    if (!isPMVCForSamePatientActive){
                        patientMedicalHistoryViewControllers.add(
                            new PatientMedicalHistoryViewController(
                            this,
                            getDesktopView()));
                        pmhvc = patientMedicalHistoryViewControllers.get(
                            patientMedicalHistoryViewControllers.size()-1);
                        pmhvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                        setView(new View().make(View.Viewer.PATIENT_MEDICAL_HISTORY_VIEW,
                                pmhvc, 
                                getDesktopView()));
                        /*
                        ActionEvent actionEvent = new ActionEvent(
                                this,ActionEvent.ACTION_PERFORMED,
                                DesktopViewController.DesktopViewControllerActionEvent.INITIALISE_VIEW.toString());
                         mcvc.actionPerformed(actionEvent);*/

                        if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                                doSetupDesktopViewMode();
                        }
                    }else {
                        activeVCForSamePatient.getView().toFront();
                    }//do nothing because only one medical condition VC allowed
                    if (getDesktopView().getDeskTop().getAllFrames().length>1){
                        this.firePropertyChangeEvent(
                                Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                                getDesktopView(),
                                this, 
                                null,
                                null
                        );
                    }
                }
            }
        }
    }
    
    private void doPrintNewPatientDetailsRequest(ActionEvent e){
        MedicalConditionViewController mcvc = null;
        medicalConditionViewControllers.add(
                new MedicalConditionViewController(
                        this,getNewTemplatedDescriptor(),getDesktopView()));
        mcvc = medicalConditionViewControllers
                .get(medicalConditionViewControllers.size()-1);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                MedicalConditionViewController.Actions
                        .PRINT_NEW_PATIENT_DETAILS_REQUEST.toString());
        mcvc.actionPerformed(actionEvent);
        /**
         * now close the view controller down after its done its job
         */
        if (!this.medicalConditionViewControllers.remove(mcvc)){
            String message = "Problem arose on attempt to remove a "
                    + "(viewless) medical condition view controller";
            displayErrorMessage(
                    message,"DesktopViewController error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doRequestForMedicalConditionViewController(ActionEvent e){
        MedicalConditionViewController mcvc = null;
        if (medicalConditionViewControllers.isEmpty()){
            medicalConditionViewControllers.add(
                    new MedicalConditionViewController(
                            this,
                            getNewTemplatedDescriptor(),
                            getDesktopView()));
            mcvc = medicalConditionViewControllers.get(medicalConditionViewControllers.size()-1);
            setView(new View().make(View.Viewer.MEDICAL_CONDITION_VIEW,
                        mcvc, 
                        getDesktopView()));
            /*
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.DesktopViewControllerActionEvent.INITIALISE_VIEW.toString());
             mcvc.actionPerformed(actionEvent);
            */

            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
            }
            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                this.firePropertyChangeEvent(
                        Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                        getDesktopView(),
                        this, 
                        null,
                        null
                );
            }
        }else {
            medicalConditionViewControllers.get(0).getView().toFront();
        }//do nothing because only one medical condition VC allowed
    }
    
    private void doRequestForPatientRecallViewController(ActionEvent e){
        DesktopViewController dvc = this;
        PatientRecallViewController prvc = null;
        if (patientRecallViewControllers==null) patientRecallViewControllers = new ArrayList<>(); 
        if (patientRecallViewControllers.isEmpty()){
            patientRecallViewControllers.add(
                    new PatientRecallViewController(
                            this,
                            getDesktopView()));
            prvc = patientRecallViewControllers.get(patientRecallViewControllers.size()-1);
            setView(new View().make(View.Viewer.RECALL_PATIENTS_VIEW,
                        prvc, 
                        getDesktopView()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    Actions.INITIALISE_VIEW_CONTROLLER.toString());
            prvc.actionPerformed(actionEvent);

            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                dvc.firePropertyChangeEvent(
                        Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                        getDesktopView(),
                        this, 
                        null,
                        null
                );
            }
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO))
                doSetupDesktopViewMode();
        }else {
            archivedPatientsViewControllers.get(0).getView().toFront();
        }
    }
    
    private void doRequestForArchivedPatientsViewController(){
        DesktopViewController dvc = this;
        ArchivedPatientsViewController apvc = null;
        if (archivedPatientsViewControllers==null) archivedPatientsViewControllers = new ArrayList<>(); 
        if (archivedPatientsViewControllers.isEmpty()){
            archivedPatientsViewControllers.add(
                    new ArchivedPatientsViewController(
                            this,
                            getNewTemplatedDescriptor(),
                            getDesktopView()));
            apvc = archivedPatientsViewControllers.get(archivedPatientsViewControllers.size()-1);
            setView(new View().make(View.Viewer.ARCHIVED_PATIENTS_VIEW,
                        apvc, 
                        getDesktopView()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    Actions.INITIALISE_VIEW_CONTROLLER.toString());
            apvc.actionPerformed(actionEvent);

            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                dvc.firePropertyChangeEvent(
                        Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                        getDesktopView(),
                        this, 
                        null,
                        null
                );
            }
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO))
                doSetupDesktopViewMode();
        }else {
            archivedPatientsViewControllers.get(0).getView().toFront();
        }
    }  
    
    private void doRequestForUserSystemWideSettingsViewController(ActionEvent e){
        UserSystemWideSettingsViewController anotherViewControllerWithSameControllerViewMode = null;
        ControllerViewMode  controllerViewMode = null;
        /*switch(e.getSource().getClass().getSimpleName()){
            case "DesktopView" ->{
                this.getDescriptor().getControllerDescription().setProperty(Properties.SCHEDULE_VIEW_MODE,ControllerViewMode.SYSTEM);
                controllerViewMode = ControllerViewMode.SYSTEM;
                break;
            }
            case "ScheduleViewController" ->{
                controllerViewMode = (ControllerViewMode)((ScheduleViewController)e.getSource()).getDescriptor().
                        getControllerDescription().getProperty(SystemDefinition.Properties.CONTROLLER_VIEW_MODE);
                break;
            }
        }*/
        UserSystemWideSettingsViewController usvc = null;
        if(userSystemWideSettingsViewControllers.isEmpty()){
            
            userSystemWideSettingsViewControllers.add(
                    new UserSystemWideSettingsViewController(this, getDesktopView()));
            usvc = userSystemWideSettingsViewControllers.get(userSystemWideSettingsViewControllers.size()-1);
            Descriptor descriptor = getNewTemplatedDescriptor();
            usvc.setDescriptor(descriptor);
            setView(new View().make(View.Viewer.USER_SYSTEM_WIDE_SETTINGS_VIEW,
                    usvc, getDesktopView()));
            
            if (getDesktopView().getDeskTop().getAllFrames().length>1){
            firePropertyChangeEvent(
                Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                getDesktopView(),
                this, 
                null,
                null
            );
            }
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO))
                doSetupDesktopViewMode();
        }else{
            displayErrorMessage("Cannot open another user system wide settings view controller because a similar controller is still active",
                    "Desktop view controller error", JOptionPane.WARNING_MESSAGE);
        }

        
    }
    
    private void doRequestForPatientAppointmentDataViewController(){
        
        DesktopViewController dvc = this;
        PatientAppointmentDataViewController padvc = null;
        if (patientAppointmentDataViewControllers==null) patientAppointmentDataViewControllers = new ArrayList<>(); 
        if (patientAppointmentDataViewControllers.isEmpty()){
            patientAppointmentDataViewControllers.add(
                    new PatientAppointmentDataViewController(
                            this,
                            getNewTemplatedDescriptor(),
                            getDesktopView()));
            padvc = patientAppointmentDataViewControllers.get(patientAppointmentDataViewControllers.size()-1);
            setView(new View().make(View.Viewer.PATIENT_APPOINTMENT_DATA_VIEW,
                        padvc, 
                        getDesktopView()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    Actions.INITIALISE_VIEW_CONTROLLER.toString());
            padvc.actionPerformed(actionEvent);

            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                dvc.firePropertyChangeEvent(
                        Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                        getDesktopView(),
                        this, 
                        null,
                        null
                );
            }
            if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO))
                doSetupDesktopViewMode();
        }else {
            patientAppointmentDataViewControllers.get(0).getView().toFront();
        }
    }
    
    private void doRequestForToDoViewController(){
        if (toDoViewControllers.isEmpty()){
            try{
                toDoViewControllers.add(
                                            new ToDoViewController(this,getDesktopView()));
                ToDoViewController tvc = 
                        toDoViewControllers.get(toDoViewControllers.size()-1);
                tvc.setView(new View().make(View.Viewer.TO_DO_VIEW/*SCHEDULE_LIST_VIEW*/,
                        tvc, 
                        getDesktopView()));
                
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                        doSetupDesktopViewMode();
                }
                
                if (getDesktopView().getDeskTop().getAllFrames().length>1){
                    this.firePropertyChangeEvent(
                            Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                            getDesktopView(),
                            this, 
                            null,
                            null
                    );
                }


            }catch (StoreException ex){
                String message = ex.getMessage();
                JOptionPane.showMessageDialog(this.getDesktopView(), 
                        message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
            }
        }else {
            toDoViewControllers.get(0).getView().toFront();
        }//toDoViewControllers not empty, so do nothing because only one patient toDo VC allowed
    }

    /*private void doRequestForNotificationViewController(){
        if (notificationViewControllers.isEmpty()){
            try{
                notificationViewControllers.add(
                                            new NotificationViewController(this,getDesktopView()));
                NotificationViewController nvc = 
                        notificationViewControllers.get(notificationViewControllers.size()-1);
                nvc.setView(new View().make(View.Viewer.NOTIFICATION_VIEW,
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
        if (getDesktopView().getDeskTop().getAllFrames().length>1){
            this.firePropertyChangeEvent(
                    Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                    getDesktopView(),
                    this, 
                    null,
                    null
            );
        }
    }*/
    
    private void doRequestForPatientViewControllerForDefinedPatient(ActionEvent e){
        boolean isSelectedPatientViewActive = false;
        PatientViewController pvc = null;
        PatientViewController patientView = null;
        ScheduleViewController svc = (ScheduleViewController)e.getSource();
        Patient patient = (Patient)svc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        for (PatientViewController _pvc : patientViewControllers){
            if (_pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                isSelectedPatientViewActive = true;
                pvc = _pvc;
                break;
            }
        }
        if (isSelectedPatientViewActive){
            if(pvc!=null) pvc.getView().toFront();
        }else{
            try{
                patientViewControllers.add(
                                        new PatientViewController(this, getNewTemplatedDescriptor(), getDesktopView()));
                pvc = patientViewControllers.get(patientViewControllers.size()-1);
                pvc.getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.PATIENT, patient);
                pvc.getDescriptor().getControllerDescription().
                        setProperty(SystemDefinition.Properties.VIEW_MODE, ViewMode.UPDATE); //signals patient selected in new patient view
                pvc.setView(new View().make(
                    View.Viewer.PATIENT_VIEW,
                    pvc, 
                    getDesktopView()));

                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                        doSetupDesktopViewMode();
                } 
                
                
                if (getDesktopView().getDeskTop().getAllFrames().length>1){
                    this.firePropertyChangeEvent(
                            Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                            getDesktopView(),
                            this, 
                            null,
                            null
                    );
                }
            }
            catch (StoreException ex){
                displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    /**
     * checks if request is for same patient and same document; and if yes place existing image viewwer on top of desktop
     * @param e 
     */
    private void doRequestForImageViewerViewController(ActionEvent e){
        ImageViewerViewController activeVCForSamePatientAndDocument = null;
        ImageViewerViewController ivvc = null;
        PatientDocumentStoreViewController pvc = (PatientDocumentStoreViewController)e.getSource();
        ArrayList<File> documents = 
                (ArrayList<File>)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_DOCUMENT);
        Patient patient = 
                        (Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        if (patient!=null){
            for (ImageViewerViewController vc : imageViewerViewControllers){
                if (vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                    ArrayList<File> pd = (ArrayList<File>)vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_DOCUMENT);
                    if (pd.get(0).equals(documents.get(0))) activeVCForSamePatientAndDocument = vc;
                    break;
                }
            }
            if (activeVCForSamePatientAndDocument==null){
                imageViewerViewControllers.add(
                            new ImageViewerViewController(
                            this,
                            getNewTemplatedDescriptor(),
                            getDesktopView()));
                ivvc = imageViewerViewControllers.get(
                        imageViewerViewControllers.size()-1);
                ivvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                ivvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT_DOCUMENT, documents);
                setView(new View().make(View.Viewer.IMAGE_VIEWER,
                                ivvc, 
                                getDesktopView()));
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
                }
            }else {
                activeVCForSamePatientAndDocument.getView().toFront();
            }//do nothing because only one medical condition VC allowed
            
            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                this.firePropertyChangeEvent(
                        Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                        getDesktopView(),
                        this, 
                        null,
                        null
                );
            }
        }
    }
    
    /**
     * method checks that another PatientDocumentStoreViewController is not active which refers to the same patient already
     * @param e 
     */
    private void doRequestForPatientDocumentStoreViewController(ActionEvent e){
        PatientDocumentStoreViewController activeVCForSamePatient = null;
        PatientDocumentStoreViewController pdsvc = null;
        PatientViewController pvc = (PatientViewController)e.getSource();
        ViewController.ViewMode viewMode = 
                        (ViewController.ViewMode)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.VIEW_MODE);
        Patient patient = 
                        (Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        if (patient!=null){
            for (PatientDocumentStoreViewController vc : patientDocumentStoreViewControllers){
                if (vc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT).equals(patient)){
                    activeVCForSamePatient = vc;
                    break;
                }
            }
            if (activeVCForSamePatient==null){
                patientDocumentStoreViewControllers.add(
                            new PatientDocumentStoreViewController(
                            this,
                            getNewTemplatedDescriptor(),
                            getDesktopView()));
                pdsvc = patientDocumentStoreViewControllers.get(
                        patientDocumentStoreViewControllers.size()-1);
                pdsvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                pdsvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE, viewMode);
                setView(new View().make(View.Viewer.PATIENT_DOCUMENT_STORE_VIEW,
                                pdsvc, 
                                getDesktopView()));
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                    doSetupDesktopViewMode();
                }
            }else {
                activeVCForSamePatient.getView().toFront();
            }//do nothing because only one medical condition VC allowed
            if (getDesktopView().getDeskTop().getAllFrames().length>1){
                this.firePropertyChangeEvent(
                        Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                        getDesktopView(),
                        this, 
                        null,
                        null
                );
            }
        }
    }
    
    private void doRequestForPatientViewController(){
        
        PatientViewController anotherBlankPatientView = null;
        for (PatientViewController pvc : patientViewControllers){
            if (!((Patient)pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT)).getIsKeyDefined()){
                anotherBlankPatientView = pvc;
                break;
            }
        }
        if (anotherBlankPatientView!=null){
            anotherBlankPatientView.getView().toFront();
        }else{
            try{
                
                patientViewControllers.add(
                                        new PatientViewController(this, getNewTemplatedDescriptor(), getDesktopView()));
                PatientViewController pvc = patientViewControllers.get(patientViewControllers.size()-1);
                pvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.VIEW_MODE, ViewMode.CREATE); //signals patient not selected in new patient view
                getDesktopView().getDeskTop().setVisible(true);
                pvc.setView(new View().make(
                    View.Viewer.PATIENT_VIEW,
                    pvc, 
                    getDesktopView()));
                
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO)){
                        doSetupDesktopViewMode();
                } 
                
                if (getDesktopView().getDeskTop().getAllFrames().length>1){
                    this.firePropertyChangeEvent(
                            Properties.CASCADE_DESKTOP_VIEWS.toString(), 
                            getDesktopView(),
                            this, 
                            null,
                            null
                    );
                }
                
            }
            catch (StoreException ex){
                displayErrorMessage(ex.getMessage(),"DesktopViewController error",JOptionPane.WARNING_MESSAGE);
            }  
        }
    }
    
    private void doRequestForViewNotification(){
        System.exit(0);
    }

    private void startBackgroundAppointmentTreatmentProcess(){
        SwingWorker swingWorker = new SwingWorker(){
            @Override
            protected String doInBackground(){
                String result = null;
                int countForAppointmentPidGreaterThan30377 = 0;
                Patient patient = null;
                Appointment appointment = null;
                AppointmentTreatment appointmentTreatment = null;
                try{
                    /**
                     * if patient.isDeleted is true set isArchived true and reset isDeleted to false
                     */
                    this.firePropertyChange("operation", null, "Patient table update");
                    patient = new Patient();
                    patient.setScope(Entity.Scope.DELETED);
                    patient.update();  
                    /**
                     * initialise all appointment::comment to blank
                     */
                    Thread.sleep(2000);
                    this.firePropertyChange("operation", null, "Clearing appointment comments");
                    appointment = new Appointment();
                    appointment.setScope(Entity.Scope.BLANK_COMMENT);
                    appointment.update();
                    /**
                     * fetch total number of appointment treatment records
                     */
                    Thread.sleep(2000);
                    this.firePropertyChange("operation", null, "Counting appointment treatments");
                    appointmentTreatment = new AppointmentTreatment();
                    int total = appointmentTreatment.count().x;


                    /**
                     * copy AppointmentTreatment comments to referenced appointment::comment column
                     * -- note this includes all appointments with a pid greater or equal to 30378
                     */
                    Thread.sleep(2000);
                    this.firePropertyChange("operation", null, "Copy over treatment comments to appointment comments");
                    appointmentTreatment.setScope(Entity.Scope.ALL);
                    appointmentTreatment.read();
                    appointmentTreatment = new AppointmentTreatment();
                    appointmentTreatment.setScope(Entity.Scope.ALL);
                    appointmentTreatment.read();
                    for(AppointmentTreatment _at : appointmentTreatment.get()){
                        
                        if (!_at.getComment().trim().isEmpty()){/*copy at.comment if not empty*/
                            appointment = _at.getAppointment();
                            appointment.setScope(Entity.Scope.SINGLE);
                            appointment = appointment.read();
                            appointment.setComment(unbracketComment(_at.getComment()));
                            appointment.update();                           
                        }
                        Integer percentage = countForAppointmentPidGreaterThan30377++ * 100/total;
                        System.out.println(percentage);
                        setProgress(percentage);
                    }

                    /**
                     * copy all Appointment notes to appointment comments if pid < 30377
                     */
                    Thread.sleep(2000);
                    this.firePropertyChange("operation", null, "Copy notes to comments in old appointments");
                    appointment.setScope(Entity.Scope.COPY_NOTES_TO_COMMENT);
                    appointment.update();

                    /**
                     * change appointment references to patient BANK, HOLIDAY to UNBOOKABLE SLOTS
                     * -- archive patient BANK, HOLIDAY (pid 21367)
                     * -- delete from AppointmentTreatment where treatmentKey = 47
                     * -- delete from Treatment where pid = 47 ("BANK HOLIDAY"
                     */
                    Thread.sleep(2000);
                    this.firePropertyChange("operation", null, "Make UNBOOKABLE 'Holidays', 'ADMIN', 'TRAINING' treatments");
                    appointment.setPatient(new Patient(21367));
                    appointment.setScope(Entity.Scope.BANK_HOLIDAY_TO_UNBOOKABLE);
                    appointment.update();

                    /**
                     * archive patient 21367
                     */
                    patient = new Patient(21367);
                    patient.setScope(Entity.Scope.SINGLE);
                    patient.delete();

                    //archive references to treatment (47 = bank holiday)
                    removeTreatmentReferences(47);

                    /**
                     * update appointments to UNBOOKABLE for pids 32312 and 32438 (ADMIN treatments referencing Janine 19775
                     */
                    Thread.sleep(2000);
                    appointment.setScope(Entity.Scope.ADMIN_TO_UNBOOKABLE);
                    appointment.update();
                    //archive references to treatment (57 = ADMIN)
                    removeTreatmentReferences(57);

                    /**
                     * update appointments to UNBOOKABLE for patientKey = 13814 and appointment.pid = 31828 (TRAINING treatments)
                     */
                    appointment.setScope(Entity.Scope.TRAINING_TO_UNBOOKABLE);
                    appointment.update();
                    //archive references to treatment (53 = TRAINING)
                    removeTreatmentReferences(53);

                    /**
                     * update appointments where 'N/P consultation/Not paid'; treatment key = 14
                     */
                    Thread.sleep(2000);
                    this.firePropertyChange("operation", null, "Change 'paid/notpaid' treatments to appointment comments");
                    appointment.setScope(Entity.Scope.CONSULTATION_NOT_PAID);
                    appointment.update();
                    removeTreatmentReferences(14);
                    /**
                     * update appointments where 'N/P paid £195' is added to comment field in associated appointment
                     */
                    appointment.setScope(Entity.Scope.PAID_195_POUNDS);
                    appointment.update();
                    //archive references to treatment (29 = N/P paid £195)
                    removeTreatmentReferences(29);

                }catch (StoreException ex){
                    String message = ex.getMessage() + "\n";
                    displayErrorMessage(message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
                } catch(NullPointerException ex){
                    String message = ex.getMessage() + "\n";
                    displayErrorMessage(message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
                }catch (InterruptedException ex){
                    String message = ex.getMessage() + "\n";
                    displayErrorMessage(message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
                }
                return result;
            }
            /**
             * Invoked when the doInBackground() method completes
             * -- used to send propertyChangeEvent to the DeskView requesting removal of progress bar from view
             */
            @Override
            protected void done(){
                try{
                    get();
                }catch (InterruptedException |
                        ExecutionException ex){
                     displayErrorMessage(ex.getMessage() + 
                             "\nRaised in SwingWorker.done() method",
                             "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                
            }
        };
        swingWorker.addPropertyChangeListener(getDesktopView());
        swingWorker.execute();
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
    /*
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
                   
                    }else if (entity.getIsAppointment()){
      
                        Invoice invoice = new Invoice(1066);
                        invoice.setAmount(0);
                        invoice.setDescription("");
                        invoice.setIsDeleted(false);
                        Patient patient = new Patient(1);
                        invoice.setPatient(patient);
                        invoice.insert();
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
                    
                }catch (StoreException ex){
                    displayErrorMessage(ex.getMessage(), "Desktop view controller error",
                            JOptionPane.WARNING_MESSAGE);
                }catch (NullPointerException ex){
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

    }*/
   
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
    
    /*
    private void doPropertyChangeEvent(DesktopViewControllerPropertyChangeEvent event,
                                        PropertyChangeListener view){
        pcSupport.removePropertyChangeListener(view);
        pcSupport.addPropertyChangeListener(view);
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(
                this,event.toString(),
                null,getDescriptor());
        pcSupport.firePropertyChange(pcEvent);
        //pcSupport.removePropertyChangeListener(view);
    }*/
    
    /**
     * 10/01/2023 07:29 update
     * -- Store static methods directly accessed to fetch PMS store and CSV import paths
     * -- these now being defined in environment variables
     * @param actionCommand
     * @param source 
     */
    
    /*
    private void doRequestForGetPath(ViewController.DesktopViewControllerActionEvent actionCommand,
                                    Object source){
        String path = null;
        DesktopViewControllerPropertyChangeEvent propertyChangeEvent = null;
        switch(actionCommand){
            case GET_APPOINTMENT_CSV_PATH_REQUEST:
                path = SystemDefinition.getPMSImportedAppointmentData();
                //getDescriptor().getControllerDescription().setPathForAppointmentCSVData(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.APPOINTMENT_CSV_PATH_RECEIVED;
                break;
            case GET_PATIENT_CSV_PATH_REQUEST:
                path = SystemDefinition.getPMSImportedPatientData();
                //getDescriptor().getControllerDescription().setPathForPatientCSVData(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.PATIENT_CSV_PATH_RECEIVED;
                break;
            case GET_PMS_STORE_PATH_REQUEST:
                if (SystemDefinition.getPMSStoreType().equals("ACCESS"))
                    path = SystemDefinition.getPMSStoreAccessURL();
                else path = SystemDefinition.getPMSStorePostgresSQLURL();
                //getDescriptor().getControllerDescription().setPathForPMSStore(path);
                propertyChangeEvent = 
                        DesktopViewControllerPropertyChangeEvent.PMS_STORE_PATH_RECEIVED;
                break;
        }    
        doPropertyChangeEvent(propertyChangeEvent, (PropertyChangeListener)source); 

    }*/
    
/*
    private void notifyMigrationActionCompleted(){
        pcSupport.addPropertyChangeListener(getDesktopView());
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
            DesktopViewController.DesktopViewControllerPropertyChangeEvent.PMS_STORE_PATH_RECEIVED.toString(),
            null,getDescriptor());
        pcSupport.firePropertyChange(pcEvent);
        pcSupport.removePropertyChangeListener(getDesktopView());
    }*/
    
    private void setDesktopViewMode(DesktopViewMode mode){
        this.desktopViewMode = mode;
    }
    
    private DesktopViewMode getDesktopViewMode(){
        return desktopViewMode;
    }
    
    private void doSetupDesktopViewMode(){
        pcSupport.addPropertyChangeListener(getDesktopView());
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
            Properties.SET_DESKTOP_VIEW_MODE.toString(),
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
            PatientSecondaryCondition patientSecondaryCondition = 
                    new PatientSecondaryCondition(new Patient());
            patientSecondaryCondition.setScope(Entity.Scope.ALL);
            patientSecondaryCondition.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            PatientPrimaryCondition patientPrimaryCondition = 
                    new PatientPrimaryCondition(new Patient());
            patientPrimaryCondition.setScope(Entity.Scope.ALL);
            patientPrimaryCondition.delete();
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
            PatientQuestion patientQuestion = new PatientQuestion();
            patientQuestion.setScope(Entity.Scope.ALL);
            patientQuestion.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Question question = new Question();
            question.setScope(Entity.Scope.ALL);
            question.delete();
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Desktop view controller",JOptionPane.WARNING_MESSAGE);
        }
        
        try{
            Invoice invoice = new Invoice();
            invoice.setScope(Entity.Scope.ALL);
            invoice.delete();
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
    
    private String unbracketComment(String comment){
        boolean firstCharacterBracket = false;
        boolean lastCharacterBracket = false;
        if (comment.substring(0).equals("(")) {
            firstCharacterBracket = true;
        }
        if (comment.substring(comment.length()-1).equals(")")) {
            lastCharacterBracket = true;
        }
        if (firstCharacterBracket && lastCharacterBracket) {
            comment = comment.substring(1,comment.length()-2);
        }
        return comment;
    }
    
    private void doExtraConvertTreatmentToCommentAction(){
        System.out.println("doExtraConvertTreatmentToCommentAction() entered");
        Appointment appointment = new Appointment();
        //this.firePropertyChange("operation", null, "Change 'paid/notpaid' treatments to appointment comments");
        
        try{
            appointment.setScope(Entity.Scope.LABWORK_CHECK);
            appointment.update();
            removeTreatmentReferences(65);
            appointment.setScope(Entity.Scope.LABWORK_RECEIVED);
            appointment.update();
            removeTreatmentReferences(64);
            appointment.setScope(Entity.Scope.CHECK_FOR_LABWORK);
            appointment.update();
            removeTreatmentReferences(63);
        }catch(StoreException ex){
            
        }
    }
    
    /** Ensures in Patient table any Patient.isDeleted(true), the Patient.isArchived is set to true and isDeleted set to false
     *  initialises new Appointment::comment (makes blank before initialisation so on entry new appointment.comment is blank)
     *  -- from AppointmentTreatment.comment if appointment.pid > 30378; else appointment.comment copied from appointment.notes
     */
    private void doMigrateCommentsFromNewAppointmentTreatmentCommentAndFromOldAppointmentNotesToNewAppointmentComment(){
        int countForAppointmentPidGreaterThan30377 = 0;
        int countForAppointmentPidLessThan30378 = 0;
        Patient patient = null;
        Appointment appointment = null;
        patient = new Patient();
        patient.setScope(Entity.Scope.DELETED);
        startBackgroundAppointmentTreatmentProcess();
        try{
            /**
             * if patient.isDeleted is true set isArchived true and reset isDeleted to false
             */
            patient.update();  

            /**
             * initialise all appointment::comment to blank
             */
            appointment = new Appointment();
            appointment.setScope(Entity.Scope.BLANK_COMMENT);
            appointment.update();
            
            /*appointment.setScope(Entity.Scope.ALL);
            int total = appointment.getValue().x;*/
            
            /**
             * copy AppointmentTreatment comments to referenced appointment::comment column
             * -- note this includes all appointments with a pid greater or equal to 30378
             */
            AppointmentTreatment at = new AppointmentTreatment();
            at.setScope(Entity.Scope.ALL);
            at.read();
            for(AppointmentTreatment _at : at.get()){
                if (!_at.getComment().trim().isEmpty()){/*copy at.comment if not empty*/
                    appointment = _at.getAppointment();
                    appointment.setScope(Entity.Scope.SINGLE);
                    appointment = appointment.read();
                    appointment.setComment(unbracketComment(_at.getComment()));
                    appointment.update();
                    countForAppointmentPidGreaterThan30377++;
                }
            }

            /**
             * copy all Appointment notes to appointment comments if pid < 30377
             */
            appointment.setScope(Entity.Scope.COPY_NOTES_TO_COMMENT);
            appointment.update();
            
            /**
             * change appointment references to patient BANK, HOLIDAY to UNBOOKABLE SLOTS
             * -- archive patient BANK, HOLIDAY (pid 21367)
             * -- delete from AppointmentTreatment where treatmentKey = 47
             * -- delete from Treatment where pid = 47 ("BANK HOLIDAY"
             */
            appointment.setPatient(new Patient(21367));
            appointment.setScope(Entity.Scope.BANK_HOLIDAY_TO_UNBOOKABLE);
            appointment.update();
            
            /**
             * archive patient 21367
             */
            patient = new Patient(21367);
            patient.setScope(Entity.Scope.SINGLE);
            patient.delete();
            
            //archive references to treatment (47 = bank holiday)
            removeTreatmentReferences(47);
            
            /**
             * update appointments to UNBOOKABLE for pids 32312 and 32438 (ADMIN treatments referencing Janine 19775
             */
            appointment.setScope(Entity.Scope.ADMIN_TO_UNBOOKABLE);
            appointment.update();
            //archive references to treatment (57 = ADMIN)
            removeTreatmentReferences(57);
            
            /**
             * update appointments to UNBOOKABLE for patientKey = 13814 and appointment.pid = 31828 (TRAINING treatments)
             */
            appointment.setScope(Entity.Scope.TRAINING_TO_UNBOOKABLE);
            appointment.update();
            //archive references to treatment (53 = TRAINING)
            removeTreatmentReferences(53);
            
            /**
             * update appointments where 'N/P consultation/Not paid'; treatment key = 14
             */
            appointment.setScope(Entity.Scope.CONSULTATION_NOT_PAID);
            appointment.update();
            removeTreatmentReferences(14);
            /**
             * update appointments where 'N/P paid £195' is added to comment field in associated appointment
             */
            appointment.setScope(Entity.Scope.PAID_195_POUNDS);
            appointment.update();
            //archive references to treatment (29 = N/P paid £195)
            removeTreatmentReferences(29);
            
        }catch (StoreException ex){
            String message = ex.getMessage() + "\n";
            displayErrorMessage(message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        } catch(NullPointerException ex){
            String message = ex.getMessage() + "\n";
            displayErrorMessage(message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void removeTreatmentReferences (Integer key)throws StoreException{
        /**
             * archive appointment treatments for treatment key
             */
            AppointmentTreatment appointmentTreatment = new AppointmentTreatment();
            appointmentTreatment.setScope(Entity.Scope.FOR_TREATMENT);
            appointmentTreatment.setTreatment(new Treatment(key));
            appointmentTreatment.delete();
            
            /**
             * archive Treatment for key
             */
            Treatment treatment = new Treatment(key);
            treatment.setScope(Entity.Scope.SINGLE);
            treatment.delete();
    }
    
    private void doMigrateCommentsFromAppointmentTreatmentToAppointment(){
        int count = 0;
        AppointmentTreatment at = new AppointmentTreatment();
        at.setScope(Entity.Scope.ALL);
        try{
            at.read();
            for(AppointmentTreatment _at : at.get()){
                Appointment appointment = null;
                if (count==97){
                    int test = 0;
                }
                if (!_at.getComment().trim().isEmpty()){
                    appointment = _at.getAppointment();
                    appointment.setScope(Entity.Scope.SINGLE);
                    appointment = appointment.read();
                    if (appointment.getComment().trim().isEmpty()){
                        appointment.setComment(unbracketComment(_at.getComment()));
                    }else{
                        appointment.setComment(appointment.getComment() + "; " + unbracketComment(_at.getComment()));
                    }
                    appointment.update();
                    count++;
                    
                    
                    //System.out.println("number of comment updates = " + count + "; appointment pid = " + appointment.getKey());
                }
            } 
            //System.out.println("number of comment updates = " + count);
        }catch (StoreException ex){
            String message = ex.getMessage() + "\n";
            displayErrorMessage(message, "Desktop View Controller error", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Actions actionCommand = Actions.valueOf(e.getActionCommand());
         switch (actionCommand){
             case COMMENT_MIGRATION_REQUEST ->{
                 //this.doMigrateCommentsFromNewAppointmentTreatmentCommentAndFromOldAppointmentNotesToNewAppointmentComment();
                 //startBackgroundAppointmentTreatmentProcess();
                 doExtraConvertTreatmentToCommentAction();
                 break;
             }
            case VIEW_ACTIVATED_NOTIFICATION ->{
                if (getDesktopViewMode().equals(DesktopViewMode.CLINIC_LOGO))
                    doSetupDesktopViewMode();
                break;
            }
            case VIEW_CONTROLLER_ACTIVATED_NOTIFICATION ->{
                firePropertyChangeEvent(
                            Properties.
                                    DESKTOP_VIEW_CHANGED_NOTIFICATION.toString(),
                            getDesktopView(),
                            this,
                            null,
                            null
                );
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS_REQUEST ->{
                View view = (View)e.getSource();
                ViewController viewController = view.getMyController();
                //ViewController viewController = (ViewController)e.getSource();
                String username = ((Credential)getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL)).getUsername();
                User user = new User(username);
                UserSettings userSettings = new UserSettings(user);
                userSettings.setScope(Entity.Scope.USER_SYSTEM_WIDE_SETTINGS);
                try{
                    userSettings = userSettings.read();
                    for(Map.Entry<SystemDefinition.Properties,Object> entry : userSettings.getSettings().entrySet()){
                        viewController.getDescriptor().getControllerDescription().
                                        setProperty(entry.getKey(), entry.getValue());
                    }
                    /*
                    if (getDesktopView().getDeskTop().getAllFrames().length > 0){
                        for (JInternalFrame frame : getDesktopView().getDeskTop().getAllFrames()){
                            if (frame instanceof View view ) {
                                firePropertyChangeEvent(
                                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                                    USER_SYSTEM_WIDE_SETTINGS_RECEIVED.toString(),
                                            view,
                                            this,
                                            null,
                                            null
                                );
                            }
                        }
                    }*/

                    firePropertyChangeEvent(
                                Properties.
                                        USER_SYSTEM_WIDE_SETTINGS_RECEIVED.toString(),
                                viewController.getView(),
                                this,
                                null,
                                null
                    );
                    break;   
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n";
                    message = message + "Handle in DesktopView|Controller::actionPerformed( case = " + actionCommand + " )";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case USER_SYSTEM_WIDE_FACTORY_SETTINGS_REQUEST ->{
                break;
            }
            default ->{
                String s = e.getSource().getClass().getSimpleName();
                switch(s){
                    case "ArchivedPatientsViewController" ->{
                        doActionEventForArchivedPatientsViewController(e);
                        break;
                    }
                    case "ClinicalNoteViewController" ->{
                        doActionEventForClinicalNoteViewController(e);
                        break;
                    }
                    case "DesktopView" ->{
                        doActionEventForDesktopView(e);
                         break;
                    }
                    case "LoginViewController" ->{
                        doActionEventForLoginViewController(e);
                         break;
                    }
                    case "ImageViewerViewController" ->{
                        doActionForImageViewerViewController(e);
                        break;
                    }
                    case "MedicalConditionViewController" ->{
                        doActionEventForMedicalConditionViewController(e);
                        break;
                    }
                    /*case "NotificationViewController" ->{
                        doActionEventForNotificationViewController(e);
                        break;
                    }*/
                    case "PatientAppointmentDataViewController" ->{
                        doActionEventForPatientAppointmentDataViewController(e);
                        break;  
                    }
                    case "PatientDocumentStoreViewController" ->{
                        doActionEventForPatientDocumentStoreViewController(e);
                    }
                    case "PatientMedicalHistoryViewController" ->{
                        doActionEventForPatientMedicalHistoryViewController(e);
                        break;
                    }
                    case "PatientQuestionnaireViewController" ->{
                        doActionEventForPatientQuestionnaireViewController(e);
                        break;
                    }
                    case "PatientViewController" ->{
                        doActionEventForPatientViewController(e);
                        break;
                    }
                    case "ScheduleViewController" ->{
                        doActionEventForScheduleViewController(e);
                        break;
                    }
                    case "ToDoViewController" ->{
                        doActionEventForToDoViewController(e);
                        break;
                    }
                    case "TreatmentViewController" ->{
                        doActionEventForTreatmentViewController(e);
                        break;
                    }
                    case "UserSystemWideSettingsViewController" ->{
                        doActionEventForUserSystemWideSettingsViewController(e);
                    }
                }
            }

        }
    }
    
    
    /**
     * main() method checks on how the jar file has been launched
     * DesktopViewController called only if
     * -- IF the jar file is run from within Netbeans AND a command line argument exists
     * -- OR the jar file is stand alone (run from outside Netbeans)
     * Else the app exits with an error status
     */
    public static void main(String[] args){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            
        }));
        JarFileCheck(args);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new DesktopViewController(new TemplateReader(), new SystemDefinition(),args[0]);
                new DesktopViewController(args);
            }
        });  
    }
    
    /**
     * checks if app execution is from a standalone jar or from within Netbeans
     * -- if from within Netbeans a further check determines if a command line argument exists
     * -- -- if command line argument does not exist, system exits with an error status
     * @param args 
     */
    private static void JarFileCheck(String[] args){
        boolean result = false;
        if (JarFileFinder.getName().equals("")){
            if(args.length==0){
                SystemDefinition.setSystemExitCode(1);
                systemExitFor(SystemExitCode.MISSING_COMMAND_LINE_ARGUMENT, null);
            }
        }
    }
    
    private void doToDoViewControllerChangeNotification(PropertyChangeEvent e){
        if (e.getSource() instanceof ToDoViewController){/*if ToDoViewController has changed let all active Schedule VCs to do views be refreshed */
            for (ScheduleViewController svc : this.scheduleViewControllers){
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,DesktopViewController.Actions.
                                REFRESH_TO_DO_DISPLAY_REQUEST.toString());
                svc.actionPerformed(actionEvent);
            } 
        }else if (e.getSource() instanceof ScheduleViewController){/* if to do view embedded in calling Schedule VC */
            ScheduleViewController svc = (ScheduleViewController)e.getSource();
            if (!this.toDoViewControllers.isEmpty()){/* refresh ToDo VC's view display*/
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,DesktopViewController.Actions.
                                REFRESH_DISPLAY_REQUEST.toString());
                toDoViewControllers.get(0).actionPerformed(actionEvent);
            }
            
            
        }
    }

    private void doPatientViewControllerChangeNotification(PropertyChangeEvent e){
        for(ScheduleViewController asvc: this.scheduleViewControllers){
             
        }
        
        PatientViewController requestingPVC = 
                    (PatientViewController)e.getSource();
        for(PatientViewController pvc: this.patientViewControllers){
            if (!requestingPVC.equals(pvc)){
                firePropertyChangeEvent(
                    PatientViewController.Properties.
                            PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                    pvc,
                    this,
                    null,
                    getDescriptor()       
                );  
            }
        }
        
        /*for(NotificationViewController pnvc : this.notificationViewControllers){
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.
                            REFRESH_DISPLAY_REQUEST.toString());
            pnvc.actionPerformed(actionEvent); 
        }*/
    }
    
    private void doArchivedPatientsViewControllerChangeNotification(PropertyChangeEvent e){
        ArchivedPatientsViewController apvc = (ArchivedPatientsViewController)e.getSource();
        setDescriptor((Descriptor)e.getNewValue());
        for(PatientViewController pvc: this.patientViewControllers){
            /*pvc.getDescriptor().getControllerDescription().setPatient(
                    apvc.getDescriptor().getControllerDescription().getPatient());*/
            firePropertyChangeEvent(
                    PatientViewController.Properties.
                            PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                    pvc,
                    this,
                    null,
                    getDescriptor()       
            ); 
        }
        if (!this.patientAppointmentDataViewControllers.isEmpty()){
            PatientAppointmentDataViewController padvc = 
                    (PatientAppointmentDataViewController)this.patientAppointmentDataViewControllers.get(0);
            firePropertyChangeEvent(
                    PatientAppointmentDataViewController.Properties.
                            PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                    padvc,
                    this,
                    null,
                    null       
            );
        }
        
    }
    
    private void doPatientAppointmentDataViewControllerChangeNotification(PropertyChangeEvent e){
        Descriptor pvcDescriptor = (Descriptor)e.getNewValue(); 
        for(PatientViewController pvc: this.patientViewControllers){
            if (pvc.getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT)
                    .equals(pvcDescriptor.getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT))){
                pvc.getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.DESCRIPTOR, pvcDescriptor);
                firePropertyChangeEvent(
                        PatientViewController.Properties.PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                        pvc,
                        this,
                        null,
                        null       
                ); 
            }
        }
        for(ArchivedPatientsViewController apvc: this.archivedPatientsViewControllers){
            firePropertyChangeEvent(
                    ArchivedPatientsViewController.Properties.
                            VIEW_CHANGE_NOTIFICATION.toString(),
                    apvc,
                    this,
                    null,
                    getDescriptor()       
            );
        }
    }
    
    /**
     * method fires a property change event to each live patient vc
     * -- the pc contains a newValue = the source schedule vc's descriptor
     * @param e 
     */
    private void doScheduleViewControllerChangeNotification(PropertyChangeEvent e){
        /*Descriptor descriptor = (Descriptor)e.getNewValue();
        Patient patient = (Patient)descriptor.getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        getDescriptor().getControllerDescription().
                setProperty(SystemDefinition.Properties.DESCRIPTOR, (Descriptor)e.getNewValue());*/
        for(PatientViewController pvc: this.patientViewControllers){
            ArrayList<Appointment> appointments = (ArrayList<Appointment>)pvc.getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.APPOINTMENTS);      
            firePropertyChangeEvent(
                    PatientViewController.Properties.
                            PATIENT_VIEW_CHANGE_NOTIFICATION.toString(),
                    pvc,
                    this,
                    null,
                    /*getDescriptor()*/ e.getNewValue()      
            ); 
        }
        ScheduleViewController requestingSVC = 
                    (ScheduleViewController)e.getSource();
        for(ScheduleViewController svc: this.scheduleViewControllers){
            if (!requestingSVC.equals(svc)){
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                REFRESH_DISPLAY_REQUEST.toString());
                svc.actionPerformed(actionEvent); 
            }
        } 
    } 
    
    private void doTreatmentViewControllerChangeNotification(PropertyChangeEvent e){
        setDescriptor((Descriptor)e.getNewValue());
        for(PatientViewController pvc: this.patientViewControllers){
            firePropertyChangeEvent(
                    PatientViewController.Properties.
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
                    DesktopViewController.Actions.
                            REFRESH_DISPLAY_REQUEST.toString());
            svc.actionPerformed(actionEvent); 
        }
    } 

    @Override
    public void propertyChange(PropertyChangeEvent e){
        DesktopViewController.Properties propertyName =
                DesktopViewController.Properties.valueOf(e.getPropertyName());
        switch(propertyName){
            case ARCHIVED_PATIENTS_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doArchivedPatientsViewControllerChangeNotification(e);
                break;
            case PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doPatientAppointmentDataViewControllerChangeNotification(e);
                break;
            case PATIENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doPatientViewControllerChangeNotification(e);
                break;
            case SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doScheduleViewControllerChangeNotification(e);
                break;
            case TO_DO_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doToDoViewControllerChangeNotification(e);
                break;
            case TREATMENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                doTreatmentViewControllerChangeNotification(e);
                break;
                
        }
    }

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