


/**
 * The following 29 lines are generated code created a previous version of this class
 * -- they are commented out and have no effect on the class operation
 * -- unfortunately I can't delete the code without recreating the class from scratch
 * -- ... which I will do at some point, but not now
 */

/*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 562, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 379, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jMenuItem1;
    // End of variables declaration//GEN-END:variables
*/

    

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.non_modal_views;

//import controller.ViewController.DesktopViewControllerActionEvent;
import controller.DesktopViewController;
import controller.ViewController;
//import controller.DesktopViewController.DesktopViewControllerActionEvent;
//import controller.DesktopViewController.DesktopViewControllerPropertyChangeEvent;
import controller.Descriptor;
import view.View;
import view.views.view_support_classes.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDesktopPane;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author colin
 */
public class DesktopView extends javax.swing.JFrame implements PropertyChangeListener{
//public class DesktopView extends View implements PropertyChangeListener{
    private JMenu activeMenu = null;
    private int topDynamicFrameListDelimiter;
    private HashMap<JMenuItem, JInternalFrame> menuItemFrameMap = null;
    private JDesktopPane desktop;
    private DesktopViewScrollPane desktopScrollPane;
    private JPanel clinicLogoPane;
    private final String clinicLogo = "/xclinic3.jpg";
    private Boolean isPMSStoreDefined = null;
    private Descriptor entityDescriptor = null;
    private final String SELECT_VIEW_MENU_TITLE = "View";
        private final String APPOINTMENT_VIEW_REQUEST_TITLE = "Appointment";
        private final String PATIENT_VIEW_REQUEST_TITLE = "Patient";
        private final String PATIENT_NOTIFICATION_VIEW_REQUEST = "Patient notifications";
        
        private final String EXIT_VIEW_REQUEST_TITLE = "Exit the Clinic practice management system";
    
    private final String MIGRATION_MANAGEMENT_MENU_TITLE = "Migration management";
        
            
        private final String PMS_DATABASE_PROFILE_TITLE = "Database profile";
            private final String PMS_DATABASE_URL = "PMS database URL";
            private final String APPOINTMENT_TABLE_RECORD_COUNT_TITLE = "Appointment table ";
            private final String PATIENT_TABLE_RECORD_COUNT_TITLE = "Patient table ";
            private final String PATIENT_NOTIFICATION_TABLE_RECORD_COUNT_TITLE = "PatientNotification table ";
            private final String SURGERY_DAYS_ASSIGNMENT_TABLE_RECORD_COUNT_TITLE = "SurgeryDaysAssignment table ";    
        private final String CSV_SOURCE_FILES_TITLE = "CSV source files selection";
            private final String APPOINTMENT_CSV_SELECTION_REQUEST_TITLE = "Select appointment CSV file to use";
            private final String PATIENT_CSV_SELECTION_REQUEST_TITLE = "Select patient CSV file to use";
        
        private final String IMPORT_DATA_REQUEST_TITLE = "Import data from CSV files to PMS database";  
        private final String DELETE_DATA_FROM_PMS_DATABASE_REQUEST_TITLE = "Delete all data from PMS database";
        
    private JMenu mnuSelectView = null; 
        private JMenuItem mniAppointmentViewRequest = null;
        private JMenuItem mniPatientViewRequest = null;
        private JMenuItem mniPatientNotificationViewRequest = null;
        private JMenuItem mniExitViewRequest = null;
        
    private JMenu mnuMigrationManagement = null; 
        private JMenu mnuPMSDatabaseProfile = null; 
            private JMenuItem mniPMSDatabaseURL = null;
            private JMenuItem mniAppointmentTableRecordCount = null;
            private JMenuItem mniPatientTableRecordCount = null;
            private JMenuItem mniPatientNotificationTableRecordCount = null;
            private JMenuItem mniSurgeryDaysAssignmentTableRecordCount = null;
        private JMenu mnuCSVSourceFiles = null;
            private JMenuItem mniAppointmentCSVSelectionRequest = null;
            private JMenuItem mniPatientCSVSelectionRequest = null;
        
        private JMenuItem mniImportMigratedDataRequest = null;
        private JMenuItem mniDeleteDataFromPMSDatabaseRequest = null;
        
    private int getTopDynamicFrameListDelimiter(){
        return topDynamicFrameListDelimiter;
    }

    private void setTopDynamicFrameListDelimiter(int value){
        topDynamicFrameListDelimiter = value;
    }
    
    private JMenu getActiveMenu(){
        return activeMenu;
    }

    private void setActiveMenu(JMenu value){
        activeMenu = value;
    }
    
    private Boolean getIsPMSStoreDefined(){
        return isPMSStoreDefined;
    }   
    
    private void setIsPMSStoreDefined(Boolean value){
        isPMSStoreDefined = value;
    }
    
    private void makeSelectViewMenu(){
        mnuSelectView = new JMenu(SELECT_VIEW_MENU_TITLE);
        setActiveMenu(mnuSelectView);
        mniAppointmentViewRequest = new JMenuItem(APPOINTMENT_VIEW_REQUEST_TITLE);
        mniPatientViewRequest = new JMenuItem(PATIENT_VIEW_REQUEST_TITLE);
        mniPatientNotificationViewRequest = new JMenuItem(PATIENT_NOTIFICATION_VIEW_REQUEST);
        mniExitViewRequest = new JMenuItem(EXIT_VIEW_REQUEST_TITLE);
        mnuSelectView.add(mniAppointmentViewRequest);
        mnuSelectView.add(mniPatientViewRequest);
        mnuSelectView.add(mniPatientNotificationViewRequest);
        mnuSelectView.add(new JSeparator());
        setTopDynamicFrameListDelimiter(mnuSelectView.getItemCount()-1);
        mnuSelectView.add(mniExitViewRequest);
        
        mniAppointmentViewRequest.addActionListener((ActionEvent e) -> mniAppointmentViewRequestActionPerformed());
        mniPatientViewRequest.addActionListener((ActionEvent e) -> mniPatientViewRequestActionPerformed());
        mniPatientNotificationViewRequest.addActionListener((ActionEvent e) -> mniPatientNotificationViewRequestActionPerformed());
        mniExitViewRequest.addActionListener((ActionEvent e) -> mniExitRequestViewActionPerformed());
    }
    
    private void makeMigrationManagementMenu(){
        mnuMigrationManagement = new JMenu(MIGRATION_MANAGEMENT_MENU_TITLE);
        setActiveMenu(mnuMigrationManagement);
        mnuPMSDatabaseProfile = new JMenu(PMS_DATABASE_PROFILE_TITLE);
        makeMigrationDatabaseContentsPopupMenu();
        mnuCSVSourceFiles = new JMenu(CSV_SOURCE_FILES_TITLE);
        makeCSVSourceFilesPopupMenu();
        
        mniImportMigratedDataRequest = new JMenuItem(IMPORT_DATA_REQUEST_TITLE); 
        mniDeleteDataFromPMSDatabaseRequest = new JMenuItem(DELETE_DATA_FROM_PMS_DATABASE_REQUEST_TITLE);
        mniExitViewRequest = new JMenuItem(EXIT_VIEW_REQUEST_TITLE);
        
        mnuMigrationManagement.add(mnuPMSDatabaseProfile);
        mnuMigrationManagement.add(mnuCSVSourceFiles);
        mnuMigrationManagement.add(new JSeparator());
        mnuMigrationManagement.add(mniImportMigratedDataRequest);
        mnuMigrationManagement.add(mniDeleteDataFromPMSDatabaseRequest);
        mnuMigrationManagement.add(new JSeparator());
        setTopDynamicFrameListDelimiter(mnuMigrationManagement.getItemCount()-1);
        mnuMigrationManagement.add(mniExitViewRequest);
        
        mniImportMigratedDataRequest.addActionListener(
                (ActionEvent e) -> mniImportMigratedDataRequestActionPerformed());
        mniDeleteDataFromPMSDatabaseRequest.addActionListener(
                (ActionEvent e) -> mniDeleteDataFromPMSDatabaseRequestActionPerformed());
        mniExitViewRequest.addActionListener(
                (ActionEvent e) -> mniExitRequestViewActionPerformed());
        
    }
    
    
    private void makeCSVSourceFilesPopupMenu(){
        this.mniAppointmentCSVSelectionRequest = new JMenuItem(APPOINTMENT_CSV_SELECTION_REQUEST_TITLE);
        this.mniPatientCSVSelectionRequest = new JMenuItem(PATIENT_CSV_SELECTION_REQUEST_TITLE);
        mnuCSVSourceFiles.add(mniAppointmentCSVSelectionRequest);
        mnuCSVSourceFiles.add(mniPatientCSVSelectionRequest);
        
        //mniAppointmentCSVSelectionRequest.addActionListener((ActionEvent e) -> mniAppointmentCSVSelectionRequestActionPerformed());
        //mniPatientCSVSelectionRequest.addActionListener((ActionEvent e) -> mniPatientCSVSelectionRequestActionPerformed());
    }
    
    private void makeMigrationDatabaseContentsPopupMenu(){
        this.mniPMSDatabaseURL = new JMenuItem(PMS_DATABASE_PROFILE_TITLE);
        this.mniAppointmentTableRecordCount = new JMenuItem(APPOINTMENT_TABLE_RECORD_COUNT_TITLE);
        this.mniPatientTableRecordCount = new JMenuItem(PATIENT_TABLE_RECORD_COUNT_TITLE);
        this.mniPatientNotificationTableRecordCount = new JMenuItem(PATIENT_NOTIFICATION_TABLE_RECORD_COUNT_TITLE);
        this.mniSurgeryDaysAssignmentTableRecordCount = new JMenuItem(SURGERY_DAYS_ASSIGNMENT_TABLE_RECORD_COUNT_TITLE);
        mnuPMSDatabaseProfile.add(mniPMSDatabaseURL);
        mnuPMSDatabaseProfile.add(mniAppointmentTableRecordCount);
        mnuPMSDatabaseProfile.add(mniPatientTableRecordCount);
        mnuPMSDatabaseProfile.add(mniPatientNotificationTableRecordCount);
        mnuPMSDatabaseProfile.add(mniSurgeryDaysAssignmentTableRecordCount);
    }
  
    private ActionListener controller = null;
    private WindowAdapter windowAdapter = null;  
    private final boolean closeIsEnabled = true;

    /**
     * Listener for window closing events (user selecting the window "X" icon).
     * The listener initialised to DO_NOTHING_ON_CLOSE, in order to pass close request message onto the view controller 
     */
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
                    DesktopView.this.getController().actionPerformed(actionEvent);
                }
            }
        };

        // when you press "X" the WINDOW_CLOSING event is called but that is it
        // nothing else happens
        this.setDefaultCloseOperation(DesktopView.DO_NOTHING_ON_CLOSE);
        // don't forget this
        this.addWindowListener(this.windowAdapter);
    }
    
    /**
     * 
     * @param controller
     * @param isDataMigrationEnabled
     * @param ed 
     */
    public DesktopView(ActionListener controller, Boolean isDataMigrationEnabled, Descriptor ed) { 
        this.controller = controller;
        this.entityDescriptor = ed;
        initComponents();
        /**
         * initialise frame closure actions
         */
        initFrameClosure();
        if (isDataMigrationEnabled!=null){
            if (isDataMigrationEnabled){
                makeMigrationManagementMenu();
                mnbDesktop.add(mnuMigrationManagement); 

            }
            else{
                makeSelectViewMenu();
                mnbDesktop.add(mnuSelectView);
            }
        }
        else{
            makeSelectViewMenu();
                mnbDesktop.add(mnuSelectView);        
        }
        setSize(1004,650);
        Dimension test1 = getPreferredSize();
        setVisible(true);
        
        //prepare for DESKTOP_VIEW
        
        desktop = new javax.swing.JDesktopPane();
        desktop.setSize(this.getWidth(), this.getHeight()-30);
        //System.out.println("desktopView = " + this.getWidth());
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
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.CLINIC_LOGO_VIEW_MODE_NOTIFICATION);
    }
    
    public ActionListener getMyController(){
        return this.controller;
    }
    
    public void doSetClinicLogoViewMode(){
        //resizing X_DesktopView frame forces a frame.repaint()
        setContentPane(clinicLogoPane);
        getContentPane().setBackground(Color.BLACK);
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.CLINIC_LOGO_VIEW_MODE_NOTIFICATION);
    }
    
    public void doSetDesktopViewMode(){
        //resizing X_DesktopView frame forces a frame.repaint()
        setSize(getWidth()+1,getHeight());
        setSize(getWidth()-1,getHeight());
        setContentPane(desktopScrollPane);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.DESKTOP_VIEW_MODE_NOTIFICATION);
    }
    
    public void initialiseView(){
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.GET_APPOINTMENT_CSV_PATH_REQUEST);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.GET_PATIENT_CSV_PATH_REQUEST);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.GET_PMS_STORE_PATH_REQUEST);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.COUNT_APPOINTMENT_TABLE_REQUEST);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.COUNT_PATIENT_TABLE_REQUEST);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST);
        doActionEventRequest(ViewController.DesktopViewControllerActionEvent.COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST);
    }
    
    private void doActionEventRequest(DesktopViewController.DesktopViewControllerActionEvent action){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                action.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        String propertyName = e.getPropertyName();
        ViewController.DesktopViewControllerPropertyChangeEvent propertyType = 
                ViewController.DesktopViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyType){           
            case DESKTOP_VIEW_CHANGED_NOTIFICATION:
                this.refreshDesktopFrameMenuItems(getActiveMenu());
                break;
            case SET_DESKTOP_VIEW_MODE:
                setViewDescriptor((Descriptor)e.getNewValue());
                this.doSetDesktopViewMode();
                break;
            case APPOINTMENT_CSV_PATH_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doAppointmentCSVPathReceived();
                break;
            case PATIENT_CSV_PATH_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doPatientCSVPathReceived();
                break;
            case PMS_STORE_PATH_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doPMSStorePathReceived();
                break;
            case APPOINTMENT_TABLE_COUNT_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doCountAppointmentTableReceived();
                break;
            case PATIENT_TABLE_COUNT_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doCountPatientTableReceived();
                break;
            case PATIENT_NOTIFICATION_TABLE_COUNT_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doCountPatientNotificationTableReceived();
                break;
            case SURGERY_DAYS_ASSIGNMENT_TABLE_COUNT_RECEIVED:
                setViewDescriptor((Descriptor)e.getNewValue());
                doCountSurgeryDaysAssignmentTableReceived();
                break;
            case MIGRATION_ACTION_COMPLETE:
                setViewDescriptor((Descriptor)e.getNewValue());
                doMigrationActionComplete();
                break;     
        }
    }
    
    public Descriptor getViewDescriptor(){
        return this.entityDescriptor;
    }
    
    private void setViewDescriptor(Descriptor value){
        this.entityDescriptor = value;
    }
    
    /*
    private void doPostMigrationActionPropertyChange(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.SET_PATIENT_CSV_PATH_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    */
    public javax.swing.JDesktopPane getDeskTop(){
        return desktop;
    } 
    private void setContentPaneForInternalFrame(){
        setContentPane(desktop);
    }
    
    public ActionListener getController(){
        return controller;
    }
    public void setController(DesktopViewController value){
        controller = value;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        mnbDesktop = new javax.swing.JMenuBar();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(mnbDesktop);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 562, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 378, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuBar mnbDesktop;
    // End of variables declaration                   
 
   private void mniAppointmentViewRequestActionPerformed() {                                        
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.APPOINTMENT_VIEW_CONTROLLER_REQUEST.toString());
        String s;
        s = actionEvent.getSource().getClass().getSimpleName();
        this.getController().actionPerformed(actionEvent);
    }
  /*
    private void mniAppointmentCSVSelectionRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.SET_APPOINTMENT_CSV_PATH_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniPatientCSVSelectionRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.SET_PATIENT_CSV_PATH_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
  */  
    private void mniPatientViewRequestActionPerformed() {                                                      
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PATIENT_VIEW_CONTROLLER_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniPatientNotificationViewRequestActionPerformed() {                                                      
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PATIENT_NOTIFICATION_VIEW_CONTROLLER_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
   /* 
    private void mniRenamePMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PMS_STORE_RENAME_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniCopyPMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PMS_STORE_COPY_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniSelectPMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PMS_STORE_SELECTION_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniCreatePMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PMS_STORE_CREATION_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
   
    private void mniCreateNonPMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.NON_PMS_STORE_CREATION_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniDeletePMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.PMS_STORE_DELETION_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
    
    private void mniDeleteNonPMSStoreRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.NON_PMS_STORE_DELETION_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
    }
*/
    private void mniExitRequestViewActionPerformed() {  
        /**
         * Menu request to close view is routed to the view controller
         */
        
        ImageIcon icon = new ImageIcon(this.getClass().getResource(clinicLogo));
        new MailMerger();
        new Emailer("abc");
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.VIEW_CLOSE_REQUEST.toString());
        DesktopView.this.getController().actionPerformed(actionEvent);
    }

    private void mniDeleteDataFromPMSDatabaseRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                /**
                 * 11/01/2023 10:05 update (rescinded)
                 */
                //DesktopViewController.DesktopViewControllerActionEvent.PMS_STORE_DELETION_REQUEST.toString());
                DesktopViewController.DesktopViewControllerActionEvent.DELETE_DATA_FROM_PMS_DATABASE_REQUEST.toString());
        DesktopView.this.getController().actionPerformed(actionEvent);
    }
    
    private void mniImportMigratedDataRequestActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.IMPORT_DATA_FROM_SOURCE.toString());
        DesktopView.this.getController().actionPerformed(actionEvent);
    }
    
    private void doCountAppointmentTableReceived(){
        Point count = getViewDescriptor().getControllerDescription().getTableRowCount();
        if (count!=null)
            this.mniAppointmentTableRecordCount.setText(this.APPOINTMENT_TABLE_RECORD_COUNT_TITLE
                + "(records = " + String.valueOf(count.x) + "; deletions = " + count.y + ")");
        else
            this.mniAppointmentTableRecordCount.setText(this.APPOINTMENT_TABLE_RECORD_COUNT_TITLE
                + "(missing table)");       
    }
    
    private void doCountPatientTableReceived(){
        Point count = getViewDescriptor().getControllerDescription().getTableRowCount();
        if (count!=null)
            this.mniPatientTableRecordCount.setText(this.PATIENT_TABLE_RECORD_COUNT_TITLE
                + "(records = " + String.valueOf(count.x) + "; deletions = " + count.y + ")");
        else
            this.mniPatientTableRecordCount.setText(this.PATIENT_TABLE_RECORD_COUNT_TITLE
                + "(missing table)");
    }
    
    private void doCountPatientNotificationTableReceived(){
        Point count = getViewDescriptor().getControllerDescription().getTableRowCount();
        if (count!=null)
            this.mniPatientNotificationTableRecordCount.setText(this.PATIENT_NOTIFICATION_TABLE_RECORD_COUNT_TITLE
                + "(records = " + String.valueOf(count.x) + "; deletions = " + count.y + ")");
        else
            this.mniPatientNotificationTableRecordCount.setText(this.PATIENT_NOTIFICATION_TABLE_RECORD_COUNT_TITLE
                + "(missing table)");

    }
    
    private void doCountSurgeryDaysAssignmentTableReceived(){
        Point count = getViewDescriptor().getControllerDescription().getTableRowCount();
        if (count!=null)
            this.mniSurgeryDaysAssignmentTableRecordCount.setText(this.SURGERY_DAYS_ASSIGNMENT_TABLE_RECORD_COUNT_TITLE
                + "(records = " + String.valueOf(count.x) + "; deletions = " + count.y + ")");
        else
            this.mniSurgeryDaysAssignmentTableRecordCount.setText(this.SURGERY_DAYS_ASSIGNMENT_TABLE_RECORD_COUNT_TITLE
                + "(missing table)");
    }
    
    private void doMigrationActionComplete(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.GET_APPOINTMENT_CSV_PATH_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
        
        actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.GET_PATIENT_CSV_PATH_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
        
        actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.GET_PMS_STORE_PATH_REQUEST.toString());
        this.getController().actionPerformed(actionEvent);
        
        if (getIsPMSStoreDefined()){
            actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                DesktopViewController.DesktopViewControllerActionEvent.COUNT_APPOINTMENT_TABLE_REQUEST.toString());
            this.getController().actionPerformed(actionEvent);

            actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.DesktopViewControllerActionEvent.COUNT_PATIENT_TABLE_REQUEST.toString());
            this.getController().actionPerformed(actionEvent);

            actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.DesktopViewControllerActionEvent.COUNT_PATIENT_NOTIFICATION_TABLE_REQUEST.toString());
            this.getController().actionPerformed(actionEvent);

            actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.DesktopViewControllerActionEvent.COUNT_SURGERY_DAYS_ASSIGNMENT_TABLE_REQUEST.toString());
            this.getController().actionPerformed(actionEvent);
        }else{
            this.mniAppointmentTableRecordCount.setText(
                    this.APPOINTMENT_TABLE_RECORD_COUNT_TITLE
                    + "(PMS store undefined)");
            this.mniPatientTableRecordCount.setText(
                    this.PATIENT_TABLE_RECORD_COUNT_TITLE
                    + "(PMS store undefined)");
            this.mniPatientNotificationTableRecordCount.setText(
                    this.PATIENT_NOTIFICATION_TABLE_RECORD_COUNT_TITLE
                    + "(PMS store undefined)");
            this.mniSurgeryDaysAssignmentTableRecordCount.setText(
                    this.SURGERY_DAYS_ASSIGNMENT_TABLE_RECORD_COUNT_TITLE
                    + "(PMS store undefined)");
        }
        
    }
    
    private void doAppointmentCSVPathReceived(){
        String currentSelection = null;
        String path = getViewDescriptor().getControllerDescription().getPathForApppointmentCSVData();
        if (path==null){
            currentSelection = "undefined";
        }
        else if (FilenameUtils.getBaseName(path).isEmpty()){
            currentSelection = "undefined";
        }
        else currentSelection = path;
        this.mniAppointmentCSVSelectionRequest.setText(
                "Appointment data -> " + currentSelection);
    }
    
    private void doPatientCSVPathReceived(){
        String currentSelection = null;
        String path = getViewDescriptor().getControllerDescription().getPathForPatientCSVData();
        if (path==null){
            currentSelection = "undefined";
        }
        else if (FilenameUtils.getBaseName(path).isEmpty()){
            currentSelection = "undefined";
        }
        else currentSelection = path;
        this.mniPatientCSVSelectionRequest.setText(
                "Patient data -> " + currentSelection);
    }
    
    private void doPMSStorePathReceived(){
        String currentPMSStoreSelection = null;
        String path = getViewDescriptor().getControllerDescription().getPathForPMSStore();
        if (path == null){
            currentPMSStoreSelection = "undefined";
            setIsPMSStoreDefined(false);
        }
        else if (FilenameUtils.getBaseName(path).isEmpty()){
            currentPMSStoreSelection = "undefined";
            setIsPMSStoreDefined(false);
        }
        else {
            currentPMSStoreSelection = path;
            setIsPMSStoreDefined(true);
        }
        
       
        this.mniPMSDatabaseURL.setText("PMS database -> " + currentPMSStoreSelection);
        
        
    } 
    
    private int getFirstSeparatorMenuPosition(JMenu menu){
        int firstSeparator;
        for (firstSeparator=0; firstSeparator<menu.getItemCount(); firstSeparator++){
            Component component = menu.getMenuComponent(firstSeparator);
            
            if (component instanceof JSeparator){
                break;
            }     
        }
        return firstSeparator;
    }
    /**
     * refreshes the list of desktop frames currently on the desktop
     * @param menu, JMenu 
     * -- the menu contains menu items which represent by title each of the frames currently on the desktop
     * -- this list of menu items is delimited by separators at the beginning and end of the list
     * 
     */
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
                if (((JMenuItem) component).getText().equals(EXIT_VIEW_REQUEST_TITLE))
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

                for(int i = firstSeparator+1; i<mnuSelectView.getItemCount(); i++ ){
                    Component component = mnuSelectView.getMenuComponent(i);
                    if((component instanceof JMenuItem)){
                        if (((JMenuItem) component).getText().equals(EXIT_VIEW_REQUEST_TITLE))
                            break;
                        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)component;
                        if (entry.getKey().equals(menuItem)){
                            menuItem.setSelected(true);
                        }else menuItem.setSelected(false);
                    }
                }
            }
        }
        this.refreshDesktopFrameMenuItems(mnuSelectView);
    }

    public void doSendViewChangedEvent(){
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.DesktopViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    public void addInternalFrameListeners(){
        
    }
}

