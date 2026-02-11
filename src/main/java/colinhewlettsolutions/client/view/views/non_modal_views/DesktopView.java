/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.Descriptor;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.controller.DesktopViewController;
import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import colinhewlettsolutions.client.model.non_entity.Credential;
import colinhewlettsolutions.client.model.non_entity.DatePickerInDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import colinhewlettsolutions.client.model.non_entity.JarFileFinder;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import javax.swing.JOptionPane;

/**
 *
 * @author colin
 */
public class DesktopView extends javax.swing.JFrame 
                         implements ActionListener, 
                                    PropertyChangeListener{

    /**
     * 
     * @param myController
     */
    public DesktopView(DesktopViewController myController) {
        controller = myController;
        
    }

    public void actionPerformed(ActionEvent e){
        ActionEvent actionEvent = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_ADD_NEW_USER:
                break;
            case REQUEST_ARCHIVED_PATIENTS_VIEW:
                doActionEventRequest(DesktopViewController.Actions.ARCHIVED_PATIENTS_VIEW_CONTROLLER_REQUEST);
                break;
            case REQUEST_CASCADE_VIEWS:
                this.cascadeInternalFrames(CascadeOrder.TOP_TO_FRONT);
                break;
            case REQUEST_CHANGE_USER_PASSWORD:
                doActionEventRequest(DesktopViewController.Actions.CHANGE_USER_PASSWORD_REQUEST);
                break;
            case REQUEST_CLOSE_VIEW:
                doActionEventRequest(DesktopViewController.Actions.VIEW_CLOSE_REQUEST);
                break;
            case REQUEST_COMMENT_MIGRATION:
                this.setupProgressMonitorView();
                do {
                    
                }while(getMyController().getView() == null);
                doSetDesktopViewMode();
                doActionEventRequest(DesktopViewController.Actions.COMMENT_MIGRATION_REQUEST);
                break;
            case REQUEST_LOGOUT: {
                if (this.getDeskTop().getAllFrames().length > 0)
                    JOptionPane.showMessageDialog(this,"Cannot logout while a view is open on the desktop","Log out request", JOptionPane.INFORMATION_MESSAGE);
                        
                else{ 
                    doActionEventRequest(DesktopViewController.Actions.LOGOUT_REQUEST);
                    doRequestUserToLogin();
                }
                break;
            }
            case REQUEST_MEDICAL_CONDITION_VIEW:
                doActionEventRequest(DesktopViewController.Actions.MEDICAL_CONDITION_VIEW_CONTROLLER_REQUEST);
                break;
            /*
            case REQUEST_NOTIFICATION_VIEW:
                doActionEventRequest(DesktopViewController.Actions.NOTIFICATION_VIEW_CONTROLLER_REQUEST);
                break;*/
            case REQUEST_PATIENT_APPOINTMENT_DATA_VIEW:
                doActionEventRequest(DesktopViewController.Actions.PATIENT_APPOINTMENT_DATA_VIEW_CONTROLLER_REQUEST);
                break;
            case REQUEST_PATIENT_VIEW:
                doActionEventRequest(DesktopViewController.Actions.PATIENT_VIEW_CONTROLLER_REQUEST);
                break;
            case REQUEST_PRINT_NEW_PATIENT_DETAILS_VIEW:
                doActionEventRequest(DesktopViewController.Actions.PRINT_NEW_PATIENT_DETAILS_REQUEST);
                String printFolder = ((Path)getMyController().getDescriptor().getControllerDescription().
                        getProperty(Properties.PRINT_FOLDER)).toString();
                doOpenDocumentForPrinting(printFolder + "/" + SystemDefinition.PATIENT_QUESTIONNAIRE_MEDICAL_HISTORY_FILENAME);
                break;
            case REQUEST_PRINT_SCHEDULE:
                doPrintSchedule();
                break;
            case REQUEST_SCHEDULE_LIST_VIEW:
                doActionEventRequest(DesktopViewController.Actions.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST);
                break;
            case REQUEST_TO_DO_VIEW:
                doActionEventRequest(DesktopViewController.Actions.TO_DO_VIEW_CONTROLLER_REQUEST);
                break;
            case REQUEST_TREATMENT_VIEW:
                doActionEventRequest(DesktopViewController.Actions.TREATMENT_VIEW_CONTROLLER_REQUEST);
                break;
            case REQUEST_USER_SYSTEM_WIDE_SETTINGS_VIEW:
                doActionEventRequest(DesktopViewController.Actions.USER_SYSTEM_WIDE_SETTINGS_VIEW_CONTROLLER_REQUEST);
                break;
        }
    }
    
    /**
     * e.propertyName is checked for "state" or "progress" values
     * -- on "state" if SwingWorker.StateValue.DONE; sends doCloseView() message to PROGRESS_MONITOR_VIEW
     * -- on "progress" sends Integer from e.newValue() to PROGRESS_MONITOR_VIEW
     * -- if neither "state" or "progress" assumes e.propertyName is the string value of a DesktopViewController.Properties enum
     * @param e 
     */
    @Override
    public void propertyChange(PropertyChangeEvent e){
        switch(e.getPropertyName()){
            case "progress" ->{
                System.out.println("case progress entered");
                getProgressMonitorView().progress((Integer)e.getNewValue());
                break;
            }
            case "state" ->{
                switch((SwingWorker.StateValue)e.getNewValue()){
                    case DONE ->{
                        getProgressMonitorView().close();
                        break;
                    }
                    case PENDING ->{
                        break;
                    }
                    case STARTED ->{
                        getProgressMonitorView().start();
                        break;
                    }
                }
                break;
            }
            case "operation" ->{
                getProgressMonitorView().title((String)e.getNewValue());
                break;
            }
            default ->{
                DesktopViewController.Properties propertyName = 
                DesktopViewController.Properties.valueOf(e.getPropertyName());
                switch (propertyName){ 
                    case CASCADE_DESKTOP_VIEWS ->{
                        cascadeInternalFrames(CascadeOrder.TOP_TO_FRONT);
                        break;
                    }
                    case DESKTOP_VIEW_CHANGED_NOTIFICATION ->{
                        this.refreshDesktopFrameMenuItems(getActiveMenu());               
                        break;
                    }
                    case PROGRESS_MONITOR_SETUP_NOTIFICATION_RECEIVED ->{
                        this.setupProgressMonitorView();
                        break;
                    }
                    case SET_DESKTOP_VIEW_MODE ->{
                        setViewDescriptor((Descriptor)e.getNewValue());
                        this.doSetDesktopViewMode();
                        break;
                    }
                }
            }
        }   
    }

    private ProgressMonitorView progressMonitorView = null;
    private void setupProgressMonitorView(){
        /*getMyController().setModalView((ModalView)new View().make(
                View.Viewer.PROGRESS_MONITOR_VIEW,getMyController(), this));*/
        getMyController().setView((new View().make(
                View.Viewer.PROGRESS_MONITOR_VIEW,getMyController(), this)));
        progressMonitorView = (ProgressMonitorView)getMyController().getView();
        progressMonitorView.title("Comment migration progress monitor");
    }
    private ProgressMonitorView getProgressMonitorView(){
        return progressMonitorView;
    }
    
    private JMenuItem mniSystemWideSettings = null;
    private JDesktopPane desktop;
    private DesktopViewScrollPane desktopScrollPane;
    private JPanel clinicLogoPane;
    private final String clinicLogo = "/xclinic3.jpg";
    private JMenu mnuUserInfo = null;
    public void initialiseView(){
        initComponents();
        
        initFrameClosure();
        if ((Boolean)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_REQUIRED)){
            this.mnuUserInfo = new javax.swing.JMenu("User info");
            this.mnbDesktop.add(this.mnuUserInfo);
            
            int lastIndex = mnuSettings.getItemCount() - 1;
            mnuSettings.insertSeparator(lastIndex);
            lastIndex = mnuSettings.getItemCount() - 1;
            this.mniSystemWideSettings = new javax.swing.JMenuItem("System wide settings");
            mnuSettings.insert(mniSystemWideSettings,lastIndex-1);
        }
        this.mnuUtilities.setEnabled(
                (Boolean)getMyController().getDescriptor().getControllerDescription().
                        getProperty(Properties.DEBUG_PATIENT_UTILITIES));
        addActionListenersToMenus();
        
        setSize(1800,950);
        setVisible(true);
        
        //prepare for DESKTOP_VIEW
        desktop = new javax.swing.JDesktopPane();
        desktop.setSize(this.getWidth(), this.getHeight()-30);
        desktopScrollPane = new DesktopViewScrollPane(desktop, this);
        desktop.setBackground(Color.BLACK);
        
        

        //prepare for CLINIC_LOGO_VIEW
        ImageIcon icon = new ImageIcon(this.getClass().getResource(clinicLogo));
        
        JLabel label = new JLabel();
        label.setIcon(icon);
        label.setPreferredSize(new Dimension(357, 92));//400 x 122
        clinicLogoPane = new JPanel(new GridBagLayout());
        clinicLogoPane.add(label);
        
        setContentPane(clinicLogoPane);
        getContentPane().setBackground(Color.BLACK);
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
        doActionEventRequest(DesktopViewController.Actions.CLINIC_LOGO_VIEW_MODE_NOTIFICATION);
        setLocationRelativeTo(null);
        
        
        
        if ((Boolean)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_REQUIRED)){
            doRequestUserToLogin();
            
        } 
        
        doSystemMenus();
        //doActionEventRequest(DesktopViewController.Actions.TO_DO_VIEW_CONTROLLER_REQUEST);
        //doActionEventRequest(DesktopViewController.Actions.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST);
    }
    
    public void disableMenus(){
        this.mnuSelectView.setEnabled(false);
        this.mnuSettings.setEnabled(false);  
        this.mnuCascadeViews.setEnabled(false);
        this.mnuToDoListView.setEnabled(false);
        mnuUserInfo.setEnabled(false);
    }
    
    JMenuItem mniCurrentUser = null;
    JMenuItem mniChangePasswordRequest = null;
    JMenuItem mniLogoutRequest = null;
    public void enableMenus(){
        this.mnuSelectView.setEnabled(true);
        this.mnuSettings.setEnabled(true);
        this.mnuCascadeViews.setEnabled(true);
        this.mnuToDoListView.setEnabled(true);
        this.mnuUserInfo.setEnabled(true);
        
        String username = ((Credential)getMyController().getDescriptor().getControllerDescription().
                getProperty(Properties.LOGIN_CREDENTIAL)).getUsername();
        
        if (this.mniChangePasswordRequest==null){
            this.mniCurrentUser = new javax.swing.JMenuItem(username);
            username = username.substring(0,1).toUpperCase() + username.substring(1);
            mniCurrentUser.setText("Current user '" + username + "'");
            this.mnuUserInfo.add(mniCurrentUser);
            this.mnuUserInfo.add(new JSeparator());
        
            this.mniChangePasswordRequest = new JMenuItem("Change password");
            this.mniChangePasswordRequest.setActionCommand(Action.REQUEST_CHANGE_USER_PASSWORD.toString());
            this.mniChangePasswordRequest.addActionListener(this);
            this.mnuUserInfo.add(mniChangePasswordRequest);
            
            this.mniLogoutRequest = new JMenuItem("Log out");
            this.mniLogoutRequest.setActionCommand(Action.REQUEST_LOGOUT.toString());
            this.mniLogoutRequest.addActionListener(this);
            this.mnuUserInfo.add(mniLogoutRequest);
        }else mniCurrentUser.setText("Current user (" + username + ")");
    }
    
    private void doRequestUserToLogin(){
        this.mnuSelectView.setEnabled(false);
        this.mnuSettings.setEnabled(false);
        this.mnuCascadeViews.setEnabled(false);
        this.mnuToDoListView.setEnabled(false);
        this.mnuUserInfo.setEnabled(false);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.LOGIN_VIEW_CONTROLLER_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void addActionListenersToMenus(){
        /**
         * defines separator for the top delimiter of the dynamic display of views on the desktop
         */
        setTopDynamicFrameListDelimiter(mnuSelectView.getItemCount()-2);
        //mniPMSVersion = new JMenuItem(JarFileFinder.getName());
        mniPMSVersion.setText(JarFileFinder.getName());
        
        //View menu
        mniPatientViewRequest.setActionCommand(Action.REQUEST_PATIENT_VIEW.toString());
        mniScheduleViewRequest.setActionCommand(Action.REQUEST_SCHEDULE_LIST_VIEW.toString());
        mniPatientViewRequest.addActionListener(this);
        mniScheduleViewRequest.addActionListener(this);
        mniExitViewRequest.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        mniExitViewRequest.addActionListener(this);
        
        //View Utilities menu
       this.mniArchivedPatientsViewRequest.setActionCommand(Action.REQUEST_ARCHIVED_PATIENTS_VIEW.toString());
        mniPatientAppointmentDataViewRequest.setActionCommand(Action.REQUEST_PATIENT_APPOINTMENT_DATA_VIEW.toString());
        //mniPatientNotificationViewRequest.setActionCommand(Action.REQUEST_NOTIFICATION_VIEW.toString());
        //mniToDoViewRequest.setActionCommand(Action.REQUEST_TO_DO_VIEW.toString());
        mniArchivedPatientsViewRequest.addActionListener(this);
        //mniPatientNotificationViewRequest.addActionListener(this);
        mniPatientAppointmentDataViewRequest.addActionListener(this);
        mniPatientInvoices.setText("Comment migrator");
        mniPatientInvoices.setActionCommand(Action.REQUEST_COMMENT_MIGRATION.toString());
        mniPatientInvoices.addActionListener(this);
        //mniToDoViewRequest.addActionListener(this);
        
        
        if (this.mniSystemWideSettings!=null){
            mniSystemWideSettings.setActionCommand(Action.REQUEST_USER_SYSTEM_WIDE_SETTINGS_VIEW.toString());
            mniSystemWideSettings.addActionListener(this);
        }

        //Settings menu
        mniPrintBlankMedicalHistoryRequest.setActionCommand(Action.REQUEST_PRINT_NEW_PATIENT_DETAILS_VIEW.toString());
        mniMedicalConditionViewRequest.setActionCommand(Action.REQUEST_MEDICAL_CONDITION_VIEW.toString());
        mniTreatmentsViewRequest.setActionCommand(Action.REQUEST_TREATMENT_VIEW.toString());
        mniPrintBlankMedicalHistoryRequest.addActionListener(this);
        mniMedicalConditionViewRequest.addActionListener(this);
        mniTreatmentsViewRequest.addActionListener(this);
        
        //Settings Current user menu
        //mniChangePasswordRequest.setActionCommand(Action.REQUEST_CHANGE_USER_PASSWORD.toString());
        //mniChangePasswordRequest.addActionListener(this);
        //mniLogoutRequest.setActionCommand(Action.REQUEST_LOGOUT.toString());
        //mniLogoutRequest.addActionListener(this);
        
        //Cascade view menu
        mnuCascadeViews.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Perform the desired action
                cascadeInternalFrames(CascadeOrder.TOP_TO_FRONT);
            }
        });
        //Cascade view menu
        mnuToDoListView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Perform the desired action
                ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    Action.REQUEST_TO_DO_VIEW.toString());
            DesktopView.this.actionPerformed(actionEvent);
            }
        });

        setActiveMenu(mnuSelectView);//legacy feature to distinguish normal app behaviour from data migration
    }
    
    private JMenuItem mniDocumentStore = null;
    private JMenuItem mniPrintFolder = null;
    private JMenuItem mniRepository = null;
    private void doSystemMenus(){
        Path documentStoreFolder = (Path)getMyController().getDescriptor().getControllerDescription().
                getProperty(Properties.DOCUMENT_STORE);
        Path printFolder = (Path)getMyController().getDescriptor().getControllerDescription().
                getProperty(Properties.PRINT_FOLDER);
        String repository = (String)getMyController().getDescriptor().getControllerDescription().
                getProperty(Properties.DATABASE_URL);
        mniDocumentStore = new JMenuItem("Document store: " + documentStoreFolder.toString());
        mniPrintFolder = new JMenuItem("Print folder: " + printFolder);
        mniRepository = new JMenuItem("Repository: " + repository);
        this.mnuSystemInformation.add(mniDocumentStore);
        this.mnuSystemInformation.add(mniPrintFolder);
        this.mnuSystemInformation.add(mniRepository);    
    }
    
    /**
     * Cascade rules as follows
     * -- if a modal view active do not include in cascade
     * -- non patient or appointment view first (in any order)
     * -- then patients
     * -- then appointments
     * -- an active modal form locate centrally on top after cascade
     */
    public static PatientView pvView = null;
    
    public enum CascadeOrder {TOP_TO_FRONT, SPECIFIC}
    
    public void cascadeInternalFrames(CascadeOrder cascadeOrder) {
        ArrayList<PatientView> patientViews = new ArrayList<>();
        //ArrayList<PatientView> actualPatientViews = new ArrayList<>();
        ArrayList<BookingView> bookingViews = new ArrayList<>();
        ArrayList<JInternalFrame> inverted = new ArrayList<>();
        ArrayList<JInternalFrame> others = new ArrayList<>();
        ArrayList<LocalDate> dates = new ArrayList<>();
        ArrayList<JInternalFrame> cascadeFrameOrder = new ArrayList<>();
        int x = 20; int y= 20; int offset = 30;
        switch(cascadeOrder){
            case TOP_TO_FRONT ->{
                if (this.getDeskTop().getAllFrames().length > 0){
                    for (JInternalFrame frame : this.getDeskTop().getAllFrames()){
                        if (!frame.isIcon()){
                            others.add(frame);
                        } 
                    }
                    for(int index = others.size()-1;index > -1; index--){
                        cascadeFrameOrder.add(others.get(index));
                    }
                }
                break;
            }
            case SPECIFIC ->{
                if (this.getDeskTop().getAllFrames().length > 0){
                    for (JInternalFrame frame : this.getDeskTop().getAllFrames()){
                        if (!frame.isIcon()){
                            if (frame instanceof BookingView) bookingViews.add((BookingView)frame);
                            else if (frame instanceof PatientView) {
                                patientViews.add((PatientView)frame);
                                pvView = (PatientView)frame;
                            }
                            //else if (frame instanceof ModalView) modalView = frame; 
                            else others.add(frame);
                        }
                    }
                }

                for(JInternalFrame frame : others){
                    cascadeFrameOrder.add(frame);
                }

                for(JInternalFrame frame : patientViews){
                    cascadeFrameOrder.add(frame);
                }

                switch (bookingViews.size()){
                    case 0:
                        break;
                    case 1:
                        cascadeFrameOrder.add(bookingViews.get(0));
                        break;
                    default:{
                        for (BookingView bookingView: bookingViews){
                            LocalDate day = (LocalDate)bookingView.getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
                            dates.add(day);
                        }
                        Collections.sort(dates);
                        for (LocalDate date : dates){
                            for (BookingView bookingView: bookingViews){
                                LocalDate theScheduleDay = (LocalDate)bookingView.getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
                                if(date.isEqual(theScheduleDay)){
                                    cascadeFrameOrder.add(bookingView);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        
        //JInternalFrame modalView = null;
        //System.println.out
        //javax.swing.SwingUtilities.invokeLater(() -> {
            

            Point cascadeStartingLocation = getStartingLocationForCascade(cascadeFrameOrder);
            x = cascadeStartingLocation.x-30;
            y = cascadeStartingLocation.y-30;

            if (cascadeFrameOrder.size()>1){
                for (JInternalFrame frame : cascadeFrameOrder){
                    try{
                        frame.setLocation(x,y);
                        frame.setIcon(false);
                        frame.setSelected(true);
                        
                        x += offset;
                        y += offset;
                    }catch (java.beans.PropertyVetoException e){
                        e.printStackTrace();
                    }
                }
            }             // Force UI update
        //});
        
    }
    
    private void doActionEventRequest(DesktopViewController.Actions action){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                action.toString());
        this.getMyController().actionPerformed(actionEvent);
        
        /*if (!((action.toString().equals("CLINIC_LOGO_VIEW_MODE_NOTIFICATION")) 
                || (action.toString().equals("DESKTOP_VIEW_MODE_NOTIFICATION"))
                || (action.toString().equals("TO_DO_VIEW_CONTROLLER_REQUEST"))
                || (action.toString().equals("SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST"))
                || (action.toString().equals("PATIENT_VIEW_CONTROLLER_REQUEST"))) ){
            System.out.println("doActionEventRequest(" + action.toString() + ")");
            Point location = DesktopView.pvView.getLocation();
            System.out.println("(3)Patient view location = " + String.valueOf(location.x) + ", " +String.valueOf(location.y) );
        }*/
    }
    
    public void doOpenDocumentForPrinting(String filepath){
        File file = new File(filepath);
        
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop API is not supported on this system.");
            return;
        }

        // Get the Desktop instance
        Desktop desktop = Desktop.getDesktop();
        try {
            // Open the document with the default application
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void doPrintSchedule(){
        DatePickerInDialog datePickerInDialog = new DatePickerInDialog(this);
        datePickerInDialog.showDatePickerDialog();
        LocalDate day = datePickerInDialog.getSelectedDate();
        if (day!=null) {
            DesktopViewController myController = (DesktopViewController)getMyController();
            //myController.getDescriptor().getControllerDescription().setScheduleDay(day);
            doActionEventRequest(DesktopViewController.Actions.PRINT_SCHEDULE_REQUEST);
        }
    }
    
    public void doSetClinicLogoViewMode(){
        //resizing X_DesktopView frame forces a frame.repaint()
        setContentPane(clinicLogoPane);
        getContentPane().setBackground(Color.BLACK);
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
        doActionEventRequest(DesktopViewController.Actions.CLINIC_LOGO_VIEW_MODE_NOTIFICATION);
    }
    
    public void doSetDesktopViewMode(){
        //resizing X_DesktopView frame forces a frame.repaint()
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
        setContentPane(desktopScrollPane);
        doActionEventRequest(DesktopViewController.Actions.DESKTOP_VIEW_MODE_NOTIFICATION);
    }
    
    public javax.swing.JDesktopPane getDeskTop(){
        return desktop;
    } 
    
    private JMenu activeMenu;
    private JMenu getActiveMenu(){
        return activeMenu;
    }
    private void setActiveMenu(JMenu value){
        activeMenu = value;
    }
    
    private DesktopViewController controller = null;
    public DesktopViewController getMyController(){
        return controller;
    }
    
    private int topDynamicFrameListDelimiter;
    private int getTopDynamicFrameListDelimiter(){
        return topDynamicFrameListDelimiter;
    }
    private void setTopDynamicFrameListDelimiter(int value){
        topDynamicFrameListDelimiter = value;
    }
    
    private Point getStartingLocationForCascade(ArrayList<JInternalFrame> views){
        int min_x = 0;
        int min_y = 0;
        ArrayList<Point> viewStartingLocations = new ArrayList<>();
        for(JInternalFrame view : views){
            viewStartingLocations.add(getMyController().getViewCentredLocationFor(this, view));
        }
        
        for(Point point : viewStartingLocations){
            if (min_x == 0) min_x = point.x;
            else min_x = Math.min(min_x, point.x);
            if (min_y==0) min_y = point.y;
            else min_y = Math.min(min_y, point.y);
        }
        return new Point(min_x,min_y);
    }
    
    /**
     * refreshes the list of desktop frames currently on the desktop
     * @param menu, JMenu 
     * -- the menu contains menu items which represent by title each of the frames currently on the desktop
     * -- this list of menu items is delimited by separators at the beginning and end of the list
     * 
     */
    private HashMap<JMenuItem, JInternalFrame> menuItemFrameMap = null;
    private void refreshDesktopFrameMenuItems(JMenu menu){
        //ArrayList<JMenuItem> frameMenuItems = new ArrayList<>();
        menuItemFrameMap = new HashMap<>();
        int firstSeparator;
        int secondSeparator;
        
        //firstSeparator = getFirstSeparatorMenuPosition(menu);
        firstSeparator = this.getTopDynamicFrameListDelimiter();

        for(int i = firstSeparator+1; i<menu.getItemCount(); i++ ){
            Component component = menu.getMenuComponent(i);
            if((component instanceof JMenuItem)){
                if (((JMenuItem) component).getText().equals(this.mniExitViewRequest.getText()))
                    break;
                //else menu.remove(component);
                else this.menuItemFrameMap.put((JMenuItem)component,null);
            }else{ 
                menu.remove(component);
                break;
            }
        }
        /**
         * for each collected frame menu item remove it from the menu
         */
        for(Map.Entry<JMenuItem, JInternalFrame> entry : menuItemFrameMap.entrySet()){
            menu.remove(entry.getKey());
        } 
        
        menuItemFrameMap.clear();
        int test3 = menu.getItemCount();
        /**
         * for each frame on the desktop
         * -- construct a new menu item + actionListener + tick if frame top one
         * -- and add to collection
         * ---- adding items to the menu inside iteration would be problematic 
         */
        int test = this.getDeskTop().getAllFrames().length;
        for(JInternalFrame frame : this.getDeskTop().getAllFrames()){
            JCheckBoxMenuItem mnuItem = new JCheckBoxMenuItem(frame.getTitle());
            mnuItem.addActionListener((ActionEvent e) -> mniMnuItemActionPerformed(e));
            if (getDeskTop().getComponentZOrder(frame)==0){
                mnuItem.setSelected(true);
            }else mnuItem.setSelected(false); 
            menuItemFrameMap.put(mnuItem, frame);
        }
        
        /**
         * for each menu item in the collection
         * -- add the collected frame menu items incrementally after the first separator
         * if the collection of frame menu items is not empty 
         * -- add separator to menu to delimit end of added frame menu items
         */
        int nextFrameMenuIemPositionInMenu = ++firstSeparator;
        for (Map.Entry<JMenuItem, JInternalFrame> entry : menuItemFrameMap.entrySet()){
            menu.add(entry.getKey(), nextFrameMenuIemPositionInMenu++);
        }
        if (!menuItemFrameMap.isEmpty()) menu.add(
                new JSeparator(), nextFrameMenuIemPositionInMenu);
    }
    
    /**
     * MenuItem listener places the source on top of all else on the desktop
     * -- this should trigger an activated event on the frame in question
     * @param e 
     */
    private void mniMnuItemActionPerformed(ActionEvent e){
        int firstSeparator;
        for (Map.Entry<JMenuItem, JInternalFrame> entry : menuItemFrameMap.entrySet()){
            if (entry.getKey().equals(e.getSource())){
                entry.getValue().toFront();
                
                for (firstSeparator=0; firstSeparator<this.mnuSelectView.getItemCount(); firstSeparator++){
                    Component component = mnuSelectView.getMenuComponent(firstSeparator);

                    if (component instanceof JSeparator){
                        break;
                    }     
                }
/*
                for(int i = firstSeparator+1; i<mnuSelectView.getItemCount(); i++ ){
                    Component component = mnuSelectView.getMenuComponent(i);
                    if((component instanceof JMenuItem)){
                        if (((JMenuItem) component).getText().equals(EXIT_VIEW_REQUEST_TITLE))
                            break;
                        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)component;
                        if (component instanceof JCheckBoxMenuItem){
                            if (entry.getKey().equals(menuItem)){
                                menuItem.setSelected(true);
                            }else menuItem.setSelected(false);
                        }
                    }
                }
 */               
                for(int i = firstSeparator+1; i<mnuSelectView.getItemCount(); i++ ){
                    Component component = mnuSelectView.getMenuComponent(i);
                    if((component instanceof JMenuItem)){
                        if (((JMenuItem) component).getText().equals(this.mniExitViewRequest.getText()))
                            break;
                        if (component instanceof JCheckBoxMenuItem){
                            if (entry.getKey().equals(component)){
                                ((JCheckBoxMenuItem)component).setSelected(true);
                                
                            }else ((JCheckBoxMenuItem)component).setSelected(false);
                        }
                    }
                }
            }
        }
        this.refreshDesktopFrameMenuItems(mnuSelectView);
    }
    
    private void setContentPaneForInternalFrame(){
        setContentPane(desktop);
    }
    
    private Descriptor entityDescriptor = null;
    public Descriptor getViewDescriptor(){
        return this.entityDescriptor;
    }
    private void setViewDescriptor(Descriptor value){
        this.entityDescriptor = value;
    }
    
    public void doSendViewChangedEvent(){
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.DesktopViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    /**
     * Listener for window closing events (user selecting the window "X" icon).
     * The listener initialised to DO_NOTHING_ON_CLOSE, in order to pass close request message onto the view controller 
     */
    private WindowAdapter windowAdapter = null;
    private final boolean closeIsEnabled = true;
    private void initFrameClosure() {
        this.windowAdapter = new WindowAdapter() {
            // WINDOW_CLOSING event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                /**
                 * viewMenuState variable is checked on receipt of windowClosing event
                 * -- true state indicates the main View menu is operational and closing event message sent to view controller
                 * -- false state indicates the main View menu is currently disabled and therefor no message sent to view controller
                 */
                if (DesktopView.this.closeIsEnabled){
                    /**
                     * When an attempt to close the view (user clicking "X")
                     * the view's controller is notified and will decide whether
                     * to call the view's dispose() method
                     */                   
                    ActionEvent actionEvent = new ActionEvent(DesktopView.this, 
                            ActionEvent.ACTION_PERFORMED,
                            DesktopViewController.DesktopViewControllerActionEvent.VIEW_CLOSE_REQUEST.toString());
                    DesktopView.this.getMyController().actionPerformed(actionEvent);
                }
            }
        };

        // when you press "X" the WINDOW_CLOSING event is called but that is it
        // nothing else happens
        this.setDefaultCloseOperation(DesktopView.DO_NOTHING_ON_CLOSE);
        // don't forget this
        this.addWindowListener(this.windowAdapter);
    }
    
    enum Action{
        REQUEST_ADD_NEW_USER,
        REQUEST_APPOINTMENT_DIARY_VIEW,
        REQUEST_ARCHIVED_PATIENTS_VIEW,
        REQUEST_CASCADE_VIEWS,
        REQUEST_CHANGE_USER_PASSWORD,
        REQUEST_CLOSE_VIEW,
        REQUEST_COMMENT_MIGRATION,
        REQUEST_LOGOUT,
        REQUEST_MEDICAL_CONDITION_VIEW,
        REQUEST_NOTIFICATION_VIEW,
        REQUEST_PATIENT_APPOINTMENT_DATA_VIEW,
        REQUEST_PATIENT_VIEW,
        REQUEST_PATIENT_VIEW_TEST,
        REQUEST_PRINT_NEW_PATIENT_DETAILS_VIEW,
        REQUEST_PRINT_SCHEDULE,
        REQUEST_SCHEDULE_LIST_VIEW,
        REQUEST_TO_DO_VIEW,
        REQUEST_TREATMENT_VIEW,
        REQUEST_USER_SYSTEM_WIDE_SETTINGS_VIEW
    };
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnbDesktop = new javax.swing.JMenuBar();
        mnuSelectView = new javax.swing.JMenu();
        mniScheduleViewRequest = new javax.swing.JMenuItem();
        mniPatientViewRequest = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuUtilities = new javax.swing.JMenu();
        mniPatientAppointmentDataViewRequest = new javax.swing.JMenuItem();
        mniArchivedPatientsViewRequest = new javax.swing.JMenuItem();
        mniPatientInvoices = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniExitViewRequest = new javax.swing.JMenuItem();
        mnuSettings = new javax.swing.JMenu();
        mniPrintBlankMedicalHistoryRequest = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniMedicalConditionViewRequest = new javax.swing.JMenuItem();
        mniTreatmentsViewRequest = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuSystemInformation = new javax.swing.JMenu();
        mniPMSVersion = new javax.swing.JMenuItem();
        mnuCascadeViews = new javax.swing.JMenu();
        mnuToDoListView = new javax.swing.JMenu();

        mnuSelectView.setText("View");

        mniScheduleViewRequest.setText("Appointments");
        mnuSelectView.add(mniScheduleViewRequest);

        mniPatientViewRequest.setText("Patients");
        mnuSelectView.add(mniPatientViewRequest);
        mnuSelectView.add(jSeparator1);

        mnuUtilities.setText("Patient utilities");

        mniPatientAppointmentDataViewRequest.setText("Latest appointment and recall data for each patient");
        mnuUtilities.add(mniPatientAppointmentDataViewRequest);

        mniArchivedPatientsViewRequest.setText("Patient archive");
        mnuUtilities.add(mniArchivedPatientsViewRequest);

        mniPatientInvoices.setText("Patient invoices");
        mnuUtilities.add(mniPatientInvoices);

        mnuSelectView.add(mnuUtilities);
        mnuSelectView.add(jSeparator2);

        mniExitViewRequest.setText("Exit the Clinic practice management system");
        mnuSelectView.add(mniExitViewRequest);

        mnbDesktop.add(mnuSelectView);

        mnuSettings.setText("Settings");

        mniPrintBlankMedicalHistoryRequest.setText("Medical history questionnaire for new patient");
        mniPrintBlankMedicalHistoryRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPrintBlankMedicalHistoryRequestActionPerformed(evt);
            }
        });
        mnuSettings.add(mniPrintBlankMedicalHistoryRequest);
        mnuSettings.add(jSeparator3);

        mniMedicalConditionViewRequest.setText("Medical history items editor");
        mnuSettings.add(mniMedicalConditionViewRequest);

        mniTreatmentsViewRequest.setText("Treatment items editor");
        mnuSettings.add(mniTreatmentsViewRequest);
        mnuSettings.add(jSeparator4);

        mnuSystemInformation.setText("System Information");

        mniPMSVersion.setText("Version:");
        mniPMSVersion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                mniPMSVersionFocusLost(evt);
            }
        });
        mnuSystemInformation.add(mniPMSVersion);

        mnuSettings.add(mnuSystemInformation);

        mnbDesktop.add(mnuSettings);

        mnuCascadeViews.setText("Cascade views");
        mnbDesktop.add(mnuCascadeViews);

        mnuToDoListView.setText("'To Do' list");
        mnbDesktop.add(mnuToDoListView);

        setJMenuBar(mnbDesktop);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 251, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mniPMSVersionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_mniPMSVersionFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_mniPMSVersionFocusLost

    private void mniPrintBlankMedicalHistoryRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPrintBlankMedicalHistoryRequestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mniPrintBlankMedicalHistoryRequestActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JMenuBar mnbDesktop;
    private javax.swing.JMenuItem mniArchivedPatientsViewRequest;
    private javax.swing.JMenuItem mniExitViewRequest;
    private javax.swing.JMenuItem mniMedicalConditionViewRequest;
    private javax.swing.JMenuItem mniPMSVersion;
    private javax.swing.JMenuItem mniPatientAppointmentDataViewRequest;
    private javax.swing.JMenuItem mniPatientInvoices;
    private javax.swing.JMenuItem mniPatientViewRequest;
    private javax.swing.JMenuItem mniPrintBlankMedicalHistoryRequest;
    private javax.swing.JMenuItem mniScheduleViewRequest;
    private javax.swing.JMenuItem mniTreatmentsViewRequest;
    private javax.swing.JMenu mnuCascadeViews;
    private javax.swing.JMenu mnuSelectView;
    private javax.swing.JMenu mnuSettings;
    private javax.swing.JMenu mnuSystemInformation;
    private javax.swing.JMenu mnuToDoListView;
    private javax.swing.JMenu mnuUtilities;
    // End of variables declaration//GEN-END:variables
}
