/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.non_modal_views;

import model.SystemDefinition;
import view.views.modal_views.dialogs.Dialog;
import controller.exceptions.TemplateReaderException;
import view.views.view_support_classes.models.Appointments3ColumnTableModel;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.components.FatCheckBox;
import controller.Descriptor;
import controller.ViewController;
import repository.StoreException;
import view.View;
import model.Patient;
import model.Appointment;
import model.Entity;
import view.views.exceptions.CrossCheckErrorException; 
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
//import javax.swing.event.InternalFrameListener;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import model.PatientNote;
import view.views.view_support_classes.renderers.AppointmentsTablePatientNoteRenderer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.File;
import view.views.modal_views.ModalView;


/**
 * 
 * -- The view receives an image of the patient details in the received
 * EntityDescriptor.Patient, which also encapsulates a patient's guardian (if 
 * exists) and appointment history
 * -- The view sends an updated image of the patient in 
 * EntityDescriptor.Selection.Patient 
 * -- 
 * -- The view receives a collection of all patients on the system in the
 * received EntityDescriptor.Collection.Patients
 * @author colin
 */

/**
 *
 * @author colin
 */
public class PatientView extends View implements ActionListener{
    private final String patientRecoveryCaption = "<html><center>Recover patient</center></html>";
    private final String patientCreateCaption = "<html><center>Create</center><center> new patient</center></html>";
    private final String cancelPatientRecoveryCaption = "<html><center>Cancel</center><center>patient</center><center>recovery</center>";
    private final String patientUpdateCaption = "<html><center>Update</center>selected</center><center>patient</center></html>";
    private final String panelPatientSelectionTitle = "Select patient";
    private final String panelPatientRecoveryTitle = "Select patient to recover";
  
    private final String DISPLAY_RECALL_EDITOR_VIEW ="Recall details";
    //28/02/2024 07:45
    private final String DISPLAY_MEDICAL_PROFILE = "Medical profile";
    //private final String DISPLAY_GUARDIAN_EDITOR_VIEW = "Guardian (if patient)";
    private final String DISPLAY_PHONE_EMAIL_EDITOR_VIEW = "Phone/email";
    private final String DISPLAY_PATIENT_NOTES_EDITOR_VIEW = "Patient notes editor";
    
    private enum PatientSelectionMode{ PATIENT_SELECTION, 
                                       PATIENT_RECOVERY}
    private enum BorderTitles { Appointment_history,
                                Contact_details,
                                Guardian_details,
                                Recall_details,
                                Notes}
    private enum TitleItem {Dr,
                            Mr,
                            Miss,
                            Mrs,
                            Ms,
                            Untitled}
    private enum GenderItem {Male,
                             Female,
                             Trans}
    public enum YesNoItem {No,
                            Yes}
    private enum ViewMode {Create_new_patient,
                           Update_patient_details}
    private enum Category{DENTAL, HYGIENE}
    private ViewMode viewMode = null;

    //state variable which support the IView interface
    DateTimeFormatter dmyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter dmyhhmmFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
    DateTimeFormatter recallFormat = DateTimeFormatter.ofPattern("MMMM/yyyy");
    DefaultTableModel appointmentHistoryModel = new DefaultTableModel();
    private ActionListener myController = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private View.Viewer myViewType = null;
    private PatientSelectionMode patientSelectionMode;
    
    private PatientSelectionMode getPatientSelectionMode(){
        return patientSelectionMode;
    }

    
    private void setPatientSelectionMode(PatientSelectionMode value){
        patientSelectionMode = value;
        TitledBorder titledBorder = (TitledBorder)this.pnlPatientSelection.getBorder();
        switch (patientSelectionMode){
            case PATIENT_RECOVERY:
                titledBorder.setTitle(panelPatientRecoveryTitle);
                titledBorder.setTitleColor(Color.RED);
                btnCreateNewPatient.setText(patientRecoveryCaption);
                btnCreateNewPatient.setForeground(Color.RED);
                btnUpdateSelectedPatient.setEnabled(true);
                btnUpdateSelectedPatient.setText(cancelPatientRecoveryCaption);
                btnUpdateSelectedPatient.setForeground(Color.RED);
                
                break;
            case PATIENT_SELECTION:
                titledBorder.setTitle(panelPatientSelectionTitle);
                titledBorder.setTitleColor(getBorderTitleColor());
                btnCreateNewPatient.setText(patientCreateCaption);
                btnCreateNewPatient.setForeground(Color.BLACK);
                btnUpdateSelectedPatient.setText(patientUpdateCaption);
                btnUpdateSelectedPatient.setForeground(Color.BLACK);
                break;
        }
        /**
         * Note: On PATIENT_SELECTION case panel border requires a further nudge to display new title properly
         */
        this.pnlPatientSelection.repaint();
    }

    static int mPatientViewWidth = 905;
    static int wPatientViewWidth = 875;
    static int mPatientViewHeight = 525;
    static int wPatientViewHeight = 515;
    static int mBeforeCreateNewPatientButtonGap = 86; 
    static int wBeforeCreateNewPatientButtonGap = 83;
    static int mBeforeDOBGap = 9;
    static int wBeforeDOBGap = 18;
    static int mBeforeFetchButtonGap = 15;
    static int wBeforeFetchButtonGap = 25; 
    static int mBeforeFrequencyLabelGap = 80;
    static int wBeforeFrequencyLabelGap = 120;
    static int mBelowClearSelection = 27;
    static int wBelowClearSelection = 32;
    static int mBelowPhonesGap = 23;
    static int wBelowPhonesGap = 17;
    static int mBetweenFrequencyLabelAndSpinnerGap = 11;
    static int wBetweenFrequencyLabelAndSpinnerGap = 10;
    static int mBetweenPhonesAndGuardianPanelsGap = 20;
    static int wBetweenPhonesAndGuardianPanelsGap = 34;
    static int mBetweenGuardianAndRecallPanelsGap = 21;
    static int wBetweenGuardianAndRecallPanelsGap = 33;
    static int mDatePickerWidth = 119;
    static int wDatePickerWidth = 114;
    static int mLine2Width = 180;
    static int wLine2Width = 182;
    static int mFurtherDetailsGapWidth = 50;
    static int wFurtherDetailsGapWidth = 15;
    static int mOperationsBottomGap = 322;
    static int wOperationsBottomGap = 313;
    static int mAppointmentHistoryScrollPaneWidth = 710;
    static int wAppointmentHistoryScrollPaneWidth = 683;
    static int mAppointmentHistoryTableWidth = 692;
    static int wAppointmentHistoryTableWidth = 660;
    static int mBetweenAppointmentHistoryAndFetchButtonGap = 33;
    static int wBetweenAppointmentHistoryAndFetchButtonGap = 35;
    
    private int getPatientViewWidth()throws StoreException{
        int result = 0;
        
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mPatientViewWidth ;
                break;
            case "Windows":
                result = wPatientViewWidth ;
                break;
        }
        return result;
    }
    
    private int getBetweenAppointmentHistoryAndFetchButtonGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBetweenAppointmentHistoryAndFetchButtonGap ;
                break;
            case "Windows":
                result = wBetweenAppointmentHistoryAndFetchButtonGap ;
                break;
        }
        return result;
    }
    
    private int getAppointmentHistoryScrollPaneWidth()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mAppointmentHistoryScrollPaneWidth ;
                break;
            case "Windows":
                result = wAppointmentHistoryScrollPaneWidth ;
                break;
        }
        return result;
    }
    
    private int getAppointmentHistoryTableWidth()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mAppointmentHistoryTableWidth ;
                break;
            case "Windows":
                result = wAppointmentHistoryTableWidth ;
                break;
        }
        return result;
    }
    
    private int getOperationsHeight()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mOperationsBottomGap ;
                break;
            case "Windows":
                result = wOperationsBottomGap ;
                break;
        }
        return result;
    }
    
    private int getFurtherDetailsGapWidth ()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mFurtherDetailsGapWidth ;
                break;
            case "Windows":
                result = wFurtherDetailsGapWidth ;
                break;
        }
        return result;
    }
    
    private int getPatientViewHeight ()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mPatientViewHeight ;
                break;
            case "Windows":
                result = wPatientViewHeight ;
                break;
        }
        return result;
    }
    
    private int getBeforeCreateNewPatientButtonGap ()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBeforeCreateNewPatientButtonGap ;
                break;
            case "Windows":
                result = wBeforeCreateNewPatientButtonGap ;
                break;
        }
        return result;
    }
    
    private int getBetweenFrequencyLabelAndSpinnerGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBetweenFrequencyLabelAndSpinnerGap;
                break;
            case "Windows":
                result = wBetweenFrequencyLabelAndSpinnerGap;
                break;
        }
        return result;
    }
    
    private int getBeforeFrequencyLabelGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBeforeFrequencyLabelGap;
                break;
            case "Windows":
                result = wBeforeFrequencyLabelGap;
                break;
        }
        return result;
    }
    
    private int getBetweenGuardianAndRecallPanelsGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBetweenGuardianAndRecallPanelsGap;
                break;
            case "Windows":
                result = wBetweenGuardianAndRecallPanelsGap;
                break;
        }
        return result;
    }
    
    private int getBetweenPhonesAndGuardianPanelsGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBetweenPhonesAndGuardianPanelsGap;
                break;
            case "Windows":
                result = wBetweenPhonesAndGuardianPanelsGap;
                break;
        }
        return result;
    }
    
    private int getBelowPhonesGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBelowPhonesGap;
                break;
            case "Windows":
                result = wBelowPhonesGap;
                break;
        }
        return result;
    }
    
    private int getLine2Width()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mLine2Width;
                break;
            case "Windows":
                result = wLine2Width;
                break;
        }
        return result;
    }
    
    private int getDatePickerWidth()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mDatePickerWidth;
                break;
            case "Windows":
                result = wDatePickerWidth;
                break;
        }
        return result;
    }

    private int getDOBGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBeforeDOBGap;
                break;
            case "Windows":
                result = wBeforeDOBGap;
                break;
        }
        return result;
    }
    
    private int getFetchButtonGap()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBeforeFetchButtonGap;
                break;
            case "Windows":
                result = wBeforeFetchButtonGap;
                break;
        }
        return result;
    }
    
    private int getGapBelowClearSelection()throws StoreException{
        int result = 0;
        String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
        switch (lookAndFeel){
            case "Metal":
                result = mBelowClearSelection;
                break;
            case "Windows":
                result = wBelowClearSelection;
                break;
        }
        return result;
    }
    /**
     * 
     * @param myController ActionListener
     * @param ed EntityDescriptor
     */

    /**
     * 
     * @param myViewType; View.Viewer  which identifies the type of view this view is
     * -- enables the ViewController to identify which view is the sender of an ActionEvent to it
     * @param myController; ViewController object responsible for this View
     * -- enables access to the Descriptor settings created by the controller
     * @param desktopView
     */
    public PatientView(
            View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Patient view");
        setMyViewType(myViewType);
        setMyController(myController); 
        setDesktopView(desktopView);
    }
    
    @Override
    public void initialiseView(){ 
        initComponents();
        setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
        try{
            setVisible(true);
            setClosable(false);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            setSize(getPatientViewWidth(),getPatientViewHeight());
        
        
            this.addInternalFrameListeners();
            btnFetchScheduleForSelectedAppointment.setText(
                    "<html><center>Fetch day schedule</center><center>for selected</center><center>appointment</center></html>");
            btnFetchScheduleForSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnFetchScheduleForSelectedAppointmentActionPerformed(evt);
                }
            });

            populatePatientSelector(this.cmbPatientSelector); 
            //populatePatientSelector(this.cmbSelectGuardian);
            this.cmbPatientSelector.addActionListener((ActionEvent e) -> cmbPatientSelectorActionPerformed(e));
            DatePickerSettings settings = new DatePickerSettings();
            dobDatePicker.addDateChangeListener((new PatientView.DOBDatePickerDateChangeListener()));
            //recallDatePicker.addDateChangeListener(new PatientView.RecallDatePickerDateChangeListener()); 

            ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
            JButton datePickerButton = dobDatePicker.getComponentToggleCalendarButton();
            datePickerButton.setText("");
            datePickerButton.setIcon(icon);

            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);

            /**
             * 18/02/2024 07:53 code update to support note taking data entry
             */

            String noteTakingMode = SystemDefinition.getPMSNotesTemplateMode();
            switch(noteTakingMode){
                case "ENABLED":
                    doEnableNoteTaking();
                    break;
                case "DISABLED":
                    doDisableNoteTaking();
                    break;
                default:

            }
        }catch(PropertyVetoException ex){
            
        }catch (Exception exc){
            String message = exc.getMessage() + "\n";
            ViewController.displayErrorMessage(message + "Raised in PatientView::iniialiseView()", 
                    "Patient view error", JOptionPane.WARNING_MESSAGE);
        }
        
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 cmbPatientSelector.requestFocus();
              }
           });
    };

    @Override
    public void actionPerformed(ActionEvent e){
        ViewController.PatientViewControllerActionEvent request = null;
        Patient patient = initialisePatientFromView(getMyController()
                .getDescriptor().getControllerDescription().getPatient());
        getMyController().getDescriptor()
                .getViewDescription().setPatient(patient);
        switch(e.getActionCommand()){
            case DISPLAY_RECALL_EDITOR_VIEW:
                request = ViewController.
                            PatientViewControllerActionEvent.
                            PATIENT_RECALL_EDITOR_VIEW_REQUEST;   
                break;
            /**
             * 28/02/2024 07:45 
            case DISPLAY_GUARDIAN_EDITOR_VIEW:
                request = ViewController.
                        PatientViewControllerActionEvent.
                        PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST;
                break;
            */
            case DISPLAY_MEDICAL_PROFILE:
                doDisplayMedicalProfileRequest();
                break;
            case DISPLAY_PHONE_EMAIL_EDITOR_VIEW:
                request = ViewController.
                            PatientViewControllerActionEvent.
                            PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST;
                break;
            case DISPLAY_PATIENT_NOTES_EDITOR_VIEW:
                request = ViewController.
                            PatientViewControllerActionEvent.
                            PATIENT_NOTES_EDITOR_VIEW_REQUEST;
                rdbGroup.clearSelection();
                break;
        }
        if (request!=null){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                request.toString());
            this.getMyController().actionPerformed(actionEvent);
            actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
            this.getMyController().actionPerformed(actionEvent);
            
        }
    }
    
    /**
     * Establish an InternalFrameListener for when the view is closed 
     * Setting DISPOSE_ON_CLOSE action when the window "X" is clicked, fires
     * InternalFrameEvent.INTERNAL_FRAME_CLOSED event for the listener to let 
     * the view controller know what's happening
     */
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    ItemListener itemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent e){
            setViewStatus(true);
        }
    };
    /*
    private void setAppointmentHistoryTableListener(){
        this.tblAppointmentHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblAppointmentHistory.getSelectionModel();
        
        lsm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
                    int selectedRow = tblAppointmentHistory.getSelectedRow();
                    if (selectedRow!=-1){
                        tableValueChangedListenerActivated = true;
                        //Patient patient = (Patient)tblAppointments.getModel().getValueAt(selectedRow, 0);
                        //doScheduleTitleRefresh(patient);
                    }
                    //else doScheduleTitleRefresh(null);  
                }
            }
        });    
    }
    */
    /**
     * 07/11/2021 11:07 dev. log update
     * Implements appointment double click event which displays appointment schedule day
     * for row in appointment history table that's been double clicked
     * Mouse listener added in the initialisation code for the JTable component 
     * in "initComponents")
     */
    //private boolean tableValueChangedListenerActivated = true;
    MouseAdapter mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() == 2) {     // to detect doble click events
                if (tblAppointmentHistory.getRowCount() > 0){ //ensures there are rows in the table
                    int row = tblAppointmentHistory.getSelectedRow();
                    LocalDate day = ((LocalDateTime)tblAppointmentHistory.getValueAt(row,0)).toLocalDate();
                    getMyController().getDescriptor().getViewDescription().setScheduleDay(day);
                    ActionEvent actionEvent = new ActionEvent(
                            PatientView.this,ActionEvent.ACTION_PERFORMED,
                            ViewController.PatientViewControllerActionEvent.SCHEDULE_VIEW_CONTROLLER_REQUEST.toString());
                    getMyController().actionPerformed(actionEvent);
                }
            }
        }
    };
    
    /**
     * Update logged at 30/10/2021 08:32
     * The ItemListener tracks any change made in cmbSelectGuardian
     * if cmbIsGuardianAPatient has "Yes" selected
     */
    ItemListener itemSelectGuardianListener = new ItemListener(){
        public void itemStateChanged(ItemEvent e){
            /*
            if (String.valueOf(cmbIsGuardianAPatient.getSelectedItem()).equals("Yes")){
                if (cmbIsGuardianAPatient.getSelectedIndex() == -1) setViewStatus(false);
                else setViewStatus(true);
            }
            else setViewStatus(false); 
            */
        }  
    };
    /**
     * Update logged at 30/10/2021 08:32
     * The DocumentListener tracks any change made to any JTextField on form
     */
    DocumentListener documentListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent documentEvent) {
          setViewStatus(true);
        }
        public void insertUpdate(DocumentEvent documentEvent) {
          setViewStatus(true);
        }
        public void removeUpdate(DocumentEvent documentEvent) {
          setViewStatus(true);
        } 
    };
    
    private void setViewTitle(Patient patient){
        this.setTitle (patient.toString()
                + " [phone: " + patient.getPhone1()
                + " email: " + patient.getEmail() +"]");
    }
    
    private void populatePatientSelector(JComboBox<Patient> selector){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                getMyController().getDescriptor().getControllerDescription().getPatients();
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        selector.setModel(model);
        selector.setSelectedIndex(-1);
    }
  
    /**
     * Method processes the PropertyChangeEvent its received from the view
     * controller
     * @param e PropertyChangeEvent
     *   -- PATIENT_RECORDS_RECEIVED the received Descriptor.Collection object 
     *   contains the collection of all the patients recorded on the system
     *   -- PATIENT_RECORD_RECEIVED the new Descriptor.Patient contains the 
     *   full details of a patient as a result of the view controller having
     *   received a request from the view to either create a new patient, update 
     *   an existing patient, or fetch the details of a newly selected patient. 
     */
    @Override
    public void propertyChange(PropertyChangeEvent e){
        initialiseFromControllerViewMode();
        ViewController.PatientViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case PATIENT_EDITOR_VIEW_CLOSED:
                rdbGroup.clearSelection();
                if(getMyController().getDescriptor().getControllerDescription()
                        .getViewMode().equals(ViewController.ViewMode.UPDATE)){
                    setViewTitle(getMyController().
                            getDescriptor().
                            getControllerDescription().
                            getPatient());
                }
                break;
            case PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED:
                //setViewDescriptor((Descriptor)e.getNewValue());
                ViewController.displayErrorMessage(
                        getMyController().getDescriptor().getControllerDescription().getError(),
                        "Patient vew error", JOptionPane.WARNING_MESSAGE);
                break;
            case PATIENT_RECEIVED:
                initialisePatientViewComponentFromED(); 
                Patient patient = getMyController()
                        .getDescriptor()
                        .getControllerDescription()
                        .getPatient();
                setViewTitle(patient);
               
                //this.setTitle(frameTitle);
                /**
                 * disable "Create new patient" menu item
                 * -- this disables attempt to create a new patient whilst an existing patientis selected
                 */
                this.mniCreateNewPatient.setEnabled(false);
                this.mniRecoverDeletedPatient.setEnabled(false);
                this.mniDeleteSelectedPatient.setEnabled(true);
                this.mniUpdateSelectedPatient.setEnabled(true);

                /**
                 * Update logged at 30/10/2021 08:32
                 * inherited view status (set if any changes have been made to form since its initialisation)
                 * is initialised to false
                 */
                setViewStatus(false);
                
                ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.
                                VIEW_CHANGED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case NULL_PATIENT_RECEIVED:
                initialisePatientViewComponentFromED();
                populatePatientSelector(this.cmbPatientSelector);
                this.setTitle("Patient view");
                /**
                 * enable "Create new patient" menu item to allow creation of a new patient
                 */
                this.mniCreateNewPatient.setEnabled(true);
                this.mniRecoverDeletedPatient.setEnabled(true);
                this.mniDeleteSelectedPatient.setEnabled(false);
                this.mniUpdateSelectedPatient.setEnabled(false);
                /**
                 * Update logged at 30/10/2021 08:32
                 * inherited view status (set if any changes have been made to form since its initialisation)
                 * is initialised to false
                 */
                setViewStatus(false);

                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.
                                VIEW_CHANGED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case PATIENTS_RECEIVED:
                //setViewDescriptor((Descriptor)e.getNewValue());
                populatePatientSelector(this.cmbPatientSelector);
                //populatePatientSelector(this.cmbSelectGuardian);
                break;
            //case RECOVERABLE_PATIENTS_RECEIVED:
                //break;
            
        }
        
        /**
         * The view checks the details it requested in the create / update 
         * patient message to the view controller, tally with what it receives
         * back from the controller 
         */
        
        /*
        else if (e.getPropertyName().equals(
                ViewController.PatientViewControllerPropertyChangeEvent.PATIENT_RECEIVED.toString())){
            setViewDescriptor((Descriptor)e.getNewValue());
            Descriptor oldEntity = (Descriptor)e.getOldValue();
            try{
                crossCheck(getViewDescriptor().getControllerDescription().getPatient(),
                        oldEntity.getControllerDescription().getPatient());
                String frameTitle = getViewDescriptor().getControllerDescription().getPatient().toString();
                this.setTitle(frameTitle);
            }
            catch (CrossCheckErrorException ex){
                //UnpecifiedError action
            }
        */
    }
    
    private void initialiseFromControllerViewMode(){
        switch(getMyController().getDescriptor()
                .getControllerDescription().getViewMode()){
            case CREATE:
                this.btnCreateNewPatient.setEnabled(true);
                this.btnUpdateSelectedPatient.setEnabled(false);
                break;
            case UPDATE:
                this.btnCreateNewPatient.setEnabled(false);
                this.btnUpdateSelectedPatient.setEnabled(true);
                break;
        }
    }

    private void crossCheck(Patient newPatientValues, 
            Patient oldPatientValues) throws CrossCheckErrorException {
        String errorMessage = null;
        boolean isCrossCheckError = false;
        String errorType = null;
        ArrayList<String> errorLog = new ArrayList<>();
        boolean isTitle = false;
        boolean isForenames = false;
        boolean isSurname = false;
        boolean isLine1 = false;
        boolean isLine2 = false;
        boolean isTown = false;
        boolean isCounty = false;
        boolean isPostcode = false;
        boolean isPhone1 = false;
        boolean isPhone2 = false;
        boolean isGender = false;
        boolean isDOB = false;
        boolean isGuardianAPatient = false;
        boolean isNotes = false;
        boolean isDentalRecallDate = false;
        boolean isHygieneRecallDate = false;
        boolean isDentalRecallFrequency = false;
        boolean isHygieneRecallFrequency = false;
        boolean isLastDentalAppointment = false;
        boolean isNextDentalAppointment = false;
        boolean isNextHygieneAppointment = false;
         
        for (int index = 0; index < 2; index ++){
            for (Descriptor.PatientField pf: Descriptor.PatientField.values()){
                switch (pf){
                    case TITLE:
                        if (newPatientValues.getName().getTitle().equals(
                            oldPatientValues.getName().getTitle())){isTitle = true;}
                        break;
                    case FORENAMES:
                        if (newPatientValues.getName().getForenames().equals(
                            oldPatientValues.getName().getForenames())){isForenames = true;
                        }
                        break;
                    case SURNAME:
                        if (newPatientValues.getName().getSurname().equals(
                            oldPatientValues.getName().getSurname())){isSurname = true;
                        }
                        break;
                    case LINE1:
                        if (newPatientValues.getAddress().getLine1().equals(
                            oldPatientValues.getAddress().getLine1())){isLine1 = true;
                        }
                        break;
                    case LINE2: 
                        if (newPatientValues.getAddress().getLine2().equals(
                            oldPatientValues.getAddress().getLine2())){isLine2 = true;
                        }
                        break;
                    case TOWN:
                        if (newPatientValues.getAddress().getTown().equals(
                            oldPatientValues.getAddress().getTown())){isTown = true;
                        };
                        break;
                    case COUNTY:
                        if (newPatientValues.getAddress().getCounty().equals(
                            oldPatientValues.getAddress().getCounty())){isCounty = true;
                        }
                        break;
                    case POSTCODE:
                        if (newPatientValues.getAddress().getPostcode().equals(
                            oldPatientValues.getAddress().getPostcode())){isPostcode = true;
                        }
                        break;
                    case PHONE1:
                        if (newPatientValues.getPhone1().equals(
                            oldPatientValues.getPhone1())){isPhone1 = true;
                        }
                        break;
                    case PHONE2:if (newPatientValues.getPhone2().equals(
                            oldPatientValues.getPhone2())){isPhone2 = true;
                    }
                    break;
                    case GENDER:
                        if (newPatientValues.getGender().equals(
                            oldPatientValues.getGender())){isGender = true;
                        }
                        break;
                    case DOB:
                        if ((newPatientValues.getDOB().compareTo(
                            oldPatientValues.getDOB())) == 0){isDOB = true;
                        }
                        break;
                    case IS_GUARDIAN_A_PATIENT:
                        if (newPatientValues.getIsGuardianAPatient() &&
                            oldPatientValues.getIsGuardianAPatient()){isGuardianAPatient = true;
                        }
                        break;
                    case NOTES:
                        if (newPatientValues.getNotes().equals(
                            oldPatientValues.getNotes())){isNotes = true;
                        }
                        break;
                    /*
                    case DENTAL_RECALL_DATE:
                        if (newPatientValues.getRecall().getDentalDate().equals(
                            oldPatientValues.getRecall().getDentalDate())){isDentalRecallDate = true;
                        }
                        break;
                    case HYGIENE_RECALL_DATE:
                        break;
                    case DENTAL_RECALL_FREQUENCY:
                        if (newPatientValues.getRecall().getDentalFrequency()==
                            oldPatientValues.getRecall().getDentalFrequency()){isDentalRecallFrequency = true;
                        }
                        break;
                    case HYGIENE_RECALL_FREQUENCY:
                        break;
                    */
                }
                if (errorType == null){
                    errorType = "patient";
                }
                else {
                    errorType = "guardian";
                }
                
                errorMessage = "Errors in cross check of requested " + errorType + " details and received " + errorType + "details listed below\n";
                if (!isTitle) {errorMessage = errorMessage + errorType + 
                        ".title field\n"; isCrossCheckError = true;} 
                if (!isForenames) {errorMessage = errorMessage + errorType + 
                        ".forenames field\n"; isCrossCheckError = true;} 
                if (!isSurname) {errorMessage = errorMessage + errorType + 
                        ".surname field\n"; isCrossCheckError = true;} 
                if (!isLine1) {errorMessage = errorMessage + errorType + 
                        ".line1 field\n"; isCrossCheckError = true;} 
                if (!isLine2) {errorMessage = errorMessage + errorType + 
                        ".line2 field\n"; isCrossCheckError = true;} 
                if (!isTown) {errorMessage = errorMessage + errorType + 
                        ".town field\n"; isCrossCheckError = true;} 
                if (!isCounty) {errorMessage = errorMessage + errorType + 
                        ".county field\n"; isCrossCheckError = true;}
                if (!isPostcode) {errorMessage = errorMessage + errorType + 
                        ".line1 field\n"; isCrossCheckError = true;} 
                if (!isPhone1) {errorMessage = errorMessage + errorType + 
                        ".phone1 field\n"; isCrossCheckError = true;} 
                if (!isPhone2) {errorMessage = errorMessage + errorType + 
                        ".phone2 field\n"; isCrossCheckError = true;}
                if (!isGender) {errorMessage = errorMessage + errorType + 
                        ".gender field\n"; isCrossCheckError = true;}
                if (!isDOB) {errorMessage = errorMessage + errorType + 
                        ".dob field\n"; isCrossCheckError = true;}
                if (!isGuardianAPatient) {errorMessage = errorMessage + errorType + 
                        ".isGuardianAParent field\n"; isCrossCheckError = true;}
                if (!isNotes) {errorMessage = errorMessage + errorType + 
                        ".notes field\n"; isCrossCheckError = true;}
                if (!isDentalRecallDate) {errorMessage = errorMessage + errorType + 
                        ".dentalRecalldate field\n"; isCrossCheckError = true;}
                if (!isHygieneRecallDate) {errorMessage = errorMessage + errorType + 
                        ".hygieneRecalldate field\n"; isCrossCheckError = true;}
                if (!isDentalRecallFrequency) {errorMessage = errorMessage + errorType + 
                        ".dentalRecallFrequency field\n"; isCrossCheckError = true;}
                if (!isHygieneRecallFrequency) {errorMessage = errorMessage + errorType + 
                        ".hygieneRecallFrequency field\n"; isCrossCheckError = true;}
                if (!isLastDentalAppointment){errorMessage = errorMessage + errorType + 
                        ".lastDentalAppointment field\n"; isCrossCheckError = true;}
                if (!isNextDentalAppointment){errorMessage = errorMessage + errorType + 
                        ".nextDentalAppointment field\n"; isCrossCheckError = true;}
                if (!isNextHygieneAppointment){errorMessage = errorMessage + errorType + 
                        ".NextHygieneAppointment field\n"; isCrossCheckError = true;}
                
            }
            errorLog.add(errorMessage);
            
            /**
             * break process anyway if there is no guardian details to process 
             */
            if (!newPatientValues.getIsGuardianAPatient()){
                break;
            }
            
            //re-initialise error markers to process guardian details
            isTitle = false;
            isForenames = false;
            isSurname = false;
            isLine1 = false;
            isLine2 = false;
            isTown = false;
            isCounty = false;
            isPostcode = false;
            isPhone1 = false;
            isPhone2 = false;
            isGender = false;
            isDOB = false;
            isGuardianAPatient = false;
            isNotes = false;
            isDentalRecallDate = false;
            isHygieneRecallDate = false;
            isDentalRecallFrequency = false;
            isHygieneRecallFrequency = false;
            isLastDentalAppointment = false;
            isNextDentalAppointment = false;
            isNextHygieneAppointment = false;
        }
        if (isCrossCheckError){
            String message = null;
            Iterator<String> it = errorLog.iterator();
            while(it.hasNext()){
                message = it.next();
                message = message + "\n";
            }
            throw new CrossCheckErrorException(message);
        }
    }
    /**
     * The method initialises the guardian component of the view state from the 
     * current entity state
     * -- note update 30/07/2021 09:05 applied
     */
    
    private void initialisePatientGuardianViewComponent(){
        /*
        //Descriptor ed = getViewDescriptor();
        
        this.cmbIsGuardianAPatient.setEnabled(true);
        boolean test = this.cmbIsGuardianAPatient.getSelectedItem().equals(PatientView.YesNoItem.Yes);
        if (this.cmbIsGuardianAPatient.getSelectedItem().equals(PatientView.YesNoItem.Yes)){
            this.cmbIsGuardianAPatient.setSelectedItem(PatientView.YesNoItem.Yes);
            this.cmbSelectGuardian.setEnabled(true);
            
            if (this.cmbSelectGuardian.getSelectedIndex()==-1){
                if (getMyController().getDescriptor().
                        getControllerDescription().getPatient().getIsGuardianAPatient()){
                    this.cmbSelectGuardian.setSelectedItem(
                            getMyController().getDescriptor().getControllerDescription().getPatient().getGuardian());
                }   
            }
        }
        else{//under 18 patient does not have a guardian who is also a patient
            this.cmbIsGuardianAPatient.setSelectedItem(PatientView.YesNoItem.No);
            this.cmbSelectGuardian.setEnabled(false);
        }
        */
    }
    
    private void populateAppointmentsHistoryTable(Patient patient){
        int appointments = 0;
        Appointments3ColumnTableModel tableModel = 
                (Appointments3ColumnTableModel)tblAppointmentHistory.getModel(); 
        tableModel.removeAllElements();
        try{
            if (patient.getIsKeyDefined()){//if patient data in view has just been cleared  
                patient.setScope(Entity.Scope.FOR_PATIENT);
                appointments = patient.getAppointmentHistory().size();
                Iterator<Appointment> it = patient.getAppointmentHistory().iterator();
                while (it.hasNext()){
                    tableModel.addElement(it.next());
                }
            }
            this.tblAppointmentHistory.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
            this.tblAppointmentHistory.setDefaultRenderer(LocalDateTime.class, new AppointmentsTableLocalDateTimeRenderer());;
            this.tblAppointmentHistory.setDefaultRenderer(PatientNote.class, new AppointmentsTablePatientNoteRenderer());
            //this.tblAppointmentHistory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            //this.tblAppointmentHistory.setPreferredScrollableViewportSize(tblAppointmentHistory.getPreferredSize());
            TitledBorder titledBorder =
                    (TitledBorder)this.pnlAppointmentHistory.getBorder();
            
            if (appointments < 3)
                titledBorder.setTitle("Appointment history ");
            else titledBorder.setTitle("Appointment history "
                    + "(top of the list is latest of " + patient.getAppointmentHistory().size() 
                    + " appointments)");
            
            this.pnlAppointmentHistory.repaint();
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Following StoreException raised in PatientView::populateAppointmentsHistoryTable()\n"
                    + ex.getMessage());
        }
        
    }
   
    private int getAge(LocalDate dob){
        return Period.between(dob, LocalDate.now()).getYears();
    }
    
    /**
     * The method initialises the patient component of the view state from the
     * current entity state
     */
    private void initialisePatientViewComponentFromED(){  
        Patient patient = getMyController().getDescriptor().
                getControllerDescription().getPatient();
        if (patient.getIsKeyDefined())
            this.cmbPatientSelector.setSelectedItem(patient);
        this.setTitle(getSurname()); //Internal frame title
        setPatientTitle(patient.getName().getTitle());
        setForenames(patient.getName().getForenames());
        setSurname(patient.getName().getSurname());
        setLine1(patient.getAddress().getLine1());
        setLine2(patient.getAddress().getLine2());
        setTown(patient.getAddress().getTown());
        setCounty(patient.getAddress().getCounty());
        setPostcode(patient.getAddress().getPostcode());
        setGender(patient.getGender());
        setNotes(patient.getNotes());
        setDOB(patient.getDOB());
        populateAppointmentsHistoryTable(patient);
    }
    
    
    private Patient initialisePatientFromView(Patient patient){
        //if (patient== null) patient = new Patient();
        /**
         * 31/1/24 
         * -- there is always a patient object although it might not have a key defined yet
         */
            patient.getAddress().setCounty(getCounty());
            /*
            patient.getRecall().setDentalDate(getDentalRecallDate());
            */
            patient.setDOB(getDOB());
            patient.getName().setForenames(getForenames());
            patient.setGender(getGender());
            /*
            patient.getRecall().setDentalDate(getDentalRecallDate());
            patient.getRecall().setDentalFrequency(getDentalRecallFrequency());
            patient.setIsGuardianAPatient(getIsGuardianAPatient());
            */
            patient.getAddress().setLine1(getLine1());
            patient.getAddress().setLine2(getLine2());
            
            patient.setNotes(getNotes());
            /*
            patient.setPhone1(getPhone1());
            patient.setPhone2(getPhone2());
            */
            patient.getAddress().setPostcode(getPostcode());
            patient.getName().setSurname(getSurname());
            patient.getName().setTitle(getPatientTitle());
            patient.getAddress().setTown(getTown());
            /*
            if (getGuardian() != null){
                patient.setGuardian(getGuardian());
            }
            */
        return patient;
    }

    private String getPatientTitle(){
        String value = "";
        if(PatientView.TitleItem.Dr.ordinal()==this.cmbNameTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Dr.toString();
        }
        else if(PatientView.TitleItem.Mr.ordinal()==this.cmbNameTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Mr.toString();
        }
        else if(PatientView.TitleItem.Mrs.ordinal()==this.cmbNameTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Mrs.toString();
        }
        else if(PatientView.TitleItem.Ms.ordinal()==this.cmbNameTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Ms.toString();
        }
        else if(PatientView.TitleItem.Miss.ordinal()==this.cmbNameTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Miss.toString();
        }
 
        return value;
    }
    private void setPatientTitle(String title){
        if (title == null){
            cmbNameTitle.setSelectedIndex(-1);
        }
        else{
            Integer index = null;
            for(PatientView.TitleItem ti: PatientView.TitleItem.values()){
                if (ti.toString().equals(title)){
                    index = ti.ordinal();
                    break;
                }
            }
            if (index != null){
                cmbNameTitle.setSelectedIndex(index);
            }
            else {
                cmbNameTitle.setSelectedIndex(-1);
            }
        }
    }
    private String getForenames(){
        return this.txtNameForename.getText();
    }
    private void setForenames(String forenames){
        if (forenames == null) this.txtNameForename.setText("");
        else this.txtNameForename.setText(forenames);
    }
    private String getSurname(){
        return this.txtNameSurname.getText();
    }
    private void setSurname(String surname){
        if (surname == null) this.txtNameSurname.setText("");
        else this.txtNameSurname.setText(surname);
    }
    private String getLine1(){
        return this.txtAddressLine1.getText();
    }
    private void setLine1(String line1){
        this.txtAddressLine1.setText(line1);
    }
    private String getLine2(){
        return this.txtAddressLine2.getText();
    }
    private void setLine2(String line2){
        if (line2 == null) this.txtAddressLine2.setText("");
        else this.txtAddressLine2.setText(line2);
    }
    private String getTown(){
        return this.txtAddressTown.getText();
    }
    private void setTown(String town){
        if (town == null) this.txtAddressTown.setText("");
        else this.txtAddressTown.setText(town);
    }
    private String getCounty(){
        return this.txtAddressCounty.getText();
    }
    private void setCounty(String county){
        if (county == null) this.txtAddressCounty.setText("");
        this.txtAddressCounty.setText(county);
    }
    private String getPostcode(){
        return this.txtAddressPostcode.getText();
    }
    private void setPostcode(String postcode){
        if (postcode == null) this.txtAddressPostcode.setText("");
        this.txtAddressPostcode.setText(postcode);
    }
    private String getGender(){
        String result = "";
        if (this.cmbNameGender.getSelectedIndex()!=-1){
            result = this.cmbNameGender.getSelectedItem().toString();
        }
        return result;
    }
    private void setGender(String gender){
        if (gender == null) cmbNameGender.setSelectedIndex(-1);
        else{
            Integer index = null;
            for (PatientView.GenderItem gi: PatientView.GenderItem.values()){
                if (gi.toString().equals(gender)){
                    index = gi.ordinal();
                    break;
                }
            }
            if (index != null){
                cmbNameGender.setSelectedIndex(index);
            }
            else {
                cmbNameGender.setSelectedIndex(-1);
            }
        }
    }
    private LocalDate getDOB(){
        LocalDate value = null;
        if (!this.dobDatePicker.getText().equals("")){
            try{
                value = LocalDate.parse(this.dobDatePicker.getText(),dmyFormat);
            }
            catch (DateTimeParseException e){
                //UnspecifiedErrorAction
            } 
            
        }
        return value;   
    }
    private void setDOB(LocalDate value){
        if (value != null){
            this.dobDatePicker.setDate(value);
            lblNameAge.setText("(" + String.valueOf(getAge(value)) + " yrs)");   
        }
        else{
            this.dobDatePicker.setDate(value);
            lblNameAge.setText("(years)");
        }
    }
    
    /*
    private boolean getIsGuardianAPatient(){
        boolean value = false;
        if(PatientView.YesNoItem.Yes.ordinal()==this.cmbIsGuardianAPatient.getSelectedIndex()){
            value = true;
        }
        else if(PatientView.YesNoItem.No.ordinal()==this.cmbIsGuardianAPatient.getSelectedIndex()){
            value = false;
        }
        return value;
    }
    private void setIsGuardianAPatient(boolean isGuardianAPatient){
        if (isGuardianAPatient){
            cmbIsGuardianAPatient.setSelectedItem(PatientView.YesNoItem.Yes);
        }
        else{
            cmbIsGuardianAPatient.setSelectedItem(PatientView.YesNoItem.No);
        }
    }
    */
    /*
    private Patient getGuardian(){
        if (cmbSelectGuardian.getSelectedIndex() == -1){
            return null;
        }
        else {
            return (Patient)cmbSelectGuardian.getSelectedItem();
        }
    }
    private void setGuardian(Patient guardian){
        if (guardian == null){
            this.cmbSelectGuardian.setSelectedIndex(-1);
            this.cmbSelectGuardian.setEnabled(false);
        }
    }
    private LocalDate getDentalRecallDate(){
        return this.recallDatePicker.getDate();
    }
    private void setRecallDate(LocalDate dentalRecallDate){
        this.recallDatePicker.setDate(dentalRecallDate);
    }
    private Integer getDentalRecallFrequency(){
        return (Integer)this.spnDentalRecallFrequency.getValue();
    }
    private void setDentalRecallFrequency(Integer value){
        if (value == null) this.spnDentalRecallFrequency.setValue(0);
        else this.spnDentalRecallFrequency.setValue(value);
    }
    */
    private String getNotes(){
        return this.txtPatientNotes.getText();
    }
    private void setNotes(String notes){
        if (notes == null) this.txtPatientNotes.setText("");
        else this.txtPatientNotes.setText(notes);
    }
    
    /*
    private String getPhone1(){
        return txtPhone1.getText();
    }
    private void setPhone1(String value){
        if (value == null) txtPhone1.setText("");
        else txtPhone1.setText(value);
    }
    private String getPhone2(){
        return txtPhone2.getText();
    }
    private void setPhone2(String value){
        if (value == null) txtPhone2.setText("");
        else txtPhone2.setText(value);
    }
    */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
        
    private void initComponents(){ 
    //<editor-fold defaultstate="collapsed" desc="Component Definitions">
    //<editor-fold defaultstate="collapsed" desc="Panel definitions">
        pnlOperations = new javax.swing.JPanel();
        pnlAddress = new javax.swing.JPanel();
        pnlAppointmentHistory = new javax.swing.JPanel();
        //pnlGuardianDetails = new javax.swing.JPanel();
        pnlName = new javax.swing.JPanel();
        pnlPatientNotes = new javax.swing.JPanel();
        //pnlPhones = new javax.swing.JPanel();
        //pnlRecall = new javax.swing.JPanel();
    
        pnlPatientSelection = new javax.swing.JPanel();
        pnlPatientSelection.setBackground(new java.awt.Color(220, 220, 220));
        pnlPatientSelection.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Select patient to recover", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N 
        
        pnlAppointmentHistory.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Appointment history (latest apppointment top of list)", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        pnlAppointmentHistory.setBackground(new java.awt.Color(220, 220, 220));

        pnlName.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Name & particulars", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        
        pnlAddress.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Address", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        /*
        pnlPatientNotes.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Patient notes", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(),
                        getBorderTitleColor())); // NOI18N
        */
        
        //pnlPhones.setBorder(
        //        javax.swing.BorderFactory.createTitledBorder(
        //                javax.swing.BorderFactory.createEtchedBorder(), 
        //                "Phone number(s) & email", 
        //                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        //                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        //                getBorderTitleFont(),
        //                getBorderTitleColor())); // NOI18N

        //pnlGuardianDetails.setBorder(
        //        javax.swing.BorderFactory.createTitledBorder(
        //                javax.swing.BorderFactory.createEtchedBorder(), 
        //                "Guardian details (patient < 18)", 
        //                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        //                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        //                getBorderTitleFont(),
        //                getBorderTitleColor())); // NOI18N

        //pnlRecall.setBorder(
        //        javax.swing.BorderFactory.createTitledBorder(
        //                javax.swing.BorderFactory.createEtchedBorder(), 
        //                "Recall details", 
        //                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        //                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        //                getBorderTitleFont(),
        //                getBorderTitleColor())); // NOI18N
        
        pnlOperations.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(),
                        getBorderTitleColor())); // NOI18N
        pnlOperations.setBackground(new java.awt.Color(220, 220, 220));
        
        
    //</editor-fold>
        btnFetchScheduleForSelectedAppointment = new javax.swing.JButton("<html><center>Fetch day schedule</center><center>for selected</center><center>appointment</center></html>");
        
        btnClearSelection = new javax.swing.JButton("Clear patient selection");
        btnClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearSelectionActionPerformed(evt);
            }
        });
        
        btnUpdateSelectedPatient = new javax.swing.JButton("Update selected patient");
        btnUpdateSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSelectedPatientActionPerformed(evt);
            }
        });
        
        btnCloseView = new javax.swing.JButton("Close view");
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });
 
        btnCreateNewPatient = new javax.swing.JButton("Create new patient");
        btnCreateNewPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateNewPatientActionPerformed(evt);
            }
        });
        
        //cmbSelectGuardian = new javax.swing.JComboBox<model.Patient>();
        //cmbIsGuardianAPatient = new javax.swing.JComboBox<PatientView.YesNoItem>();
        cmbPatientSelector = new javax.swing.JComboBox<Patient>();
        cmbNameTitle = new javax.swing.JComboBox<>();
        cmbNameGender = new javax.swing.JComboBox<>();
        
        lblNameAge = new javax.swing.JLabel("age");
        lblNameDOB = new javax.swing.JLabel("DOB");
        lblNameForename = new javax.swing.JLabel("Forename");
        lblNameSurname = new javax.swing.JLabel("Surname");
        lblNameTitle = new javax.swing.JLabel("Title");
        lblNameGender = new javax.swing.JLabel("Gender");
        lblAddressCounty = new javax.swing.JLabel("County");
        lblAddressLine1 = new javax.swing.JLabel("Line 1");
        lblAddressLine2 = new javax.swing.JLabel("Line 2");
        lblAddressTown = new javax.swing.JLabel("Town");
        lblAddressPostcode = new javax.swing.JLabel("Postcode");

        
        //menu bits
        mbaPatientView = new javax.swing.JMenuBar();
        mnuActions = new javax.swing.JMenu();
        mniCreateNewPatient = new javax.swing.JMenuItem();
        mniUpdateSelectedPatient = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniDeleteSelectedPatient = new javax.swing.JMenuItem();
        mniRecoverDeletedPatient = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        tblAppointmentHistory = new javax.swing.JTable();
        tblAppointmentHistory.addMouseListener(mouseListener);
        tblAppointmentHistory.setModel(new Appointments3ColumnTableModel());
        //tblAppointmentHistory.setPreferredSize(new Dimension(getAppointmentHistoryTableWidth(),110));

        try{
            scrAppointmentHistory = new javax.swing.JScrollPane();
            scrAppointmentHistory.setPreferredSize(new Dimension(
                    getAppointmentHistoryScrollPaneWidth(),100));
            scrAppointmentHistory.setRowHeaderView(null);
            scrAppointmentHistory.setViewportView(tblAppointmentHistory);

            ViewController.setJTableColumnProperties(tblAppointmentHistory, 
                    scrAppointmentHistory.getPreferredSize().width, 
                    20,20,60);
            //spnDentalRecallFrequency = new javax.swing.JSpinner();

            txtNameForename = new javax.swing.JTextField();
            txtNameSurname = new javax.swing.JTextField();
            txtAddressLine2 = new javax.swing.JTextField();
            txtAddressLine1 = new javax.swing.JTextField();
            txtAddressTown = new javax.swing.JTextField();
            txtAddressCounty = new javax.swing.JTextField();
            txtPatientNotes = new javax.swing.JTextArea();
            txtAddressPostcode = new javax.swing.JTextField();
            //txtRecallDate = new javax.swing.JTextField();
            //txtPhone1 = new javax.swing.JTextField();
            //txtPhone2 = new javax.swing.JTextField();
            //txtPhonesEmail = new javax.swing.JTextField();

            DatePickerSettings dateSettings = new DatePickerSettings();
            dateSettings.setVisibleDateTextField(false);
            dateSettings.setGapBeforeButtonPixels(0);
            //recallDatePicker = new com.github.lgooddatepicker.components.DatePicker(dateSettings);

            DatePickerSettings settings = new DatePickerSettings();
            settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            settings.setAllowKeyboardEditing(false);
            dobDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);

            cmbNameTitle.addItemListener(itemListener);
            cmbNameTitle.setEditable(true);
            cmbNameTitle.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.TitleItem.values()));
            cmbNameTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            cmbNameGender.addItemListener(itemListener);
            cmbNameGender.setEditable(true);
            cmbNameGender.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.GenderItem.values()));
            cmbNameGender.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            lblNameTitle.setText("Title");
            lblNameForename.setText("Forenames");
            lblNameSurname.setText("Surname");
            lblNameGender.setText("Gender");

            lblNameAge.setText("(age)");

            lblNameDOB.setText("DOB");


            txtAddressLine1.getDocument().addDocumentListener(documentListener);
            txtAddressLine1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtAddressLine1ActionPerformed(evt);
                }
            });

            lblAddressLine1.setText("Line 1");

            lblAddressLine2.setText("Line 2");

            txtAddressLine2.getDocument().addDocumentListener(documentListener);
            txtAddressLine2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtAddressLine2ActionPerformed(evt);
                }
            });

            //pnlPatientNotes.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient notes"));

            txtPatientNotes.setColumns(20);
            txtPatientNotes.setLineWrap(true);
            txtPatientNotes.setRows(5);

            scrPatientNotes = new javax.swing.JScrollPane();
            scrPatientNotes.setViewportView(txtPatientNotes);

            pnlNameContent = new javax.swing.JPanel();
            pnlPatientAddressContent = new javax.swing.JPanel();
            pnlFurtherDetails = new javax.swing.JPanel();

            pnlFurtherDetails.setBorder(
                    javax.swing.BorderFactory.createTitledBorder(
                            javax.swing.BorderFactory.createEtchedBorder(), 
                            "Patient information selectable for viewing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                            getBorderTitleFont(),
                            getBorderTitleColor())); // NOI18N

            //28/02/2024 07:45
            rdbRequestModalPhoneEmailEditorView = new javax.swing.JRadioButton();
            rdbRequestModalRecallEditorView = new javax.swing.JRadioButton();
            //rdbRequestModalGuardianEditorView = new javax.swing.JRadioButton();
            rdbRequestModalNotesEditorView = new javax.swing.JRadioButton();
            rdbRequestModalMedicalProfilePopup = new javax.swing.JRadioButton();
            rdbRequestModalNotesEditorView.setText("Patient notes editor");
            rdbRequestModalPhoneEmailEditorView.setText(DISPLAY_PHONE_EMAIL_EDITOR_VIEW);
            rdbRequestModalPhoneEmailEditorView.setActionCommand(DISPLAY_PHONE_EMAIL_EDITOR_VIEW);
            rdbRequestModalRecallEditorView.setText(DISPLAY_RECALL_EDITOR_VIEW);
            rdbRequestModalRecallEditorView.setActionCommand(DISPLAY_RECALL_EDITOR_VIEW);
            //rdbRequestModalGuardianEditorView.setText(DISPLAY_GUARDIAN_EDITOR_VIEW);
            //rdbRequestModalGuardianEditorView.setActionCommand(DISPLAY_GUARDIAN_EDITOR_VIEW);
            rdbRequestModalMedicalProfilePopup.setText(DISPLAY_MEDICAL_PROFILE);
            rdbRequestModalMedicalProfilePopup.setActionCommand(DISPLAY_MEDICAL_PROFILE);
            rdbGroup = new javax.swing.ButtonGroup();
            rdbGroup.add(rdbRequestModalPhoneEmailEditorView);
            rdbGroup.add(rdbRequestModalRecallEditorView);
            rdbGroup.add(rdbRequestModalGuardianEditorView);
            rdbGroup.add(rdbRequestModalNotesEditorView);
            rdbRequestModalPhoneEmailEditorView.addActionListener(this);
            rdbRequestModalRecallEditorView.addActionListener(this);
            rdbRequestModalGuardianEditorView.addActionListener(this);
            rdbRequestModalNotesEditorView.addActionListener(this);
        }catch (Exception exc){
            String message = exc.getMessage() + "\n";
            ViewController.displayErrorMessage(message + "Raised in PatientView::initComponents()", 
                    "Patient view error", JOptionPane.WARNING_MESSAGE);
        }

//</editor-fold>
//<editor-fold defaultstate="Collapsed" desc="Menu configuration">
    mnuActions.setText("Actions");

        mniCreateNewPatient.setText("Create new patient");
        mniCreateNewPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCreateNewPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniCreateNewPatient);

        mniUpdateSelectedPatient.setText("Update selected patient details");
        mniUpdateSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniUpdateSelectedPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniUpdateSelectedPatient);
        mnuActions.add(jSeparator2);

        mniDeleteSelectedPatient.setText("Delete selected patient");
        mniDeleteSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDeleteSelectedPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniDeleteSelectedPatient);

        mniRecoverDeletedPatient.setText("Recover deleted patient");
        mniRecoverDeletedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRecoverDeletedPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniRecoverDeletedPatient);
        mnuActions.add(jSeparator3);

        mniCloseView.setText("Close view");
        mniCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCloseViewActionPerformed(evt);
            }
        });
        mnuActions.add(mniCloseView);
 
        /**
         * 18/02/2024 07:53 code update to support note taking data entry
         */
        //mbaPatientView.add(mnuActions);

        //setJMenuBar(mbaPatientView);
    //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Patient address content panel layout">
    javax.swing.GroupLayout pnlPatientAddressContentLayout = new javax.swing.GroupLayout(pnlPatientAddressContent);
        pnlPatientAddressContent.setLayout(pnlPatientAddressContentLayout);
        pnlPatientAddressContentLayout.setHorizontalGroup(
            pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientAddressContentLayout.createSequentialGroup()
                        .addComponent(lblAddressTown)
                        .addGap(30, 30, 30))
                    .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                        .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblAddressLine1)
                            .addComponent(lblAddressLine2))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientAddressContentLayout.createSequentialGroup()
                        .addComponent(lblAddressPostcode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtAddressLine2)
                        .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                            .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtAddressPostcode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                .addComponent(txtAddressTown, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(18, 18, 18)
                            .addComponent(lblAddressCounty)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtAddressLine1)))
        );
        pnlPatientAddressContentLayout.setVerticalGroup(
            pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressLine1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressLine2))
                .addGap(10, 10, 10)
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAddressCounty)
                    .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressTown)
                    .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressPostcode)))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Address panel layout">
        javax.swing.GroupLayout pnlAddressLayout = new javax.swing.GroupLayout(pnlAddress);
        pnlAddress.setLayout(pnlAddressLayout);
        pnlAddressLayout.setHorizontalGroup(
            pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientAddressContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlAddressLayout.setVerticalGroup(
            pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientAddressContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
//</editor-fold> 
//<editor-fold defaultstate="collapsed" desc="Patient notes panel layout">
        /*
        javax.swing.GroupLayout pnlPatientNotesLayout = new javax.swing.GroupLayout(pnlPatientNotes);
        pnlPatientNotes.setLayout(pnlPatientNotesLayout);
        pnlPatientNotesLayout.setHorizontalGroup(
            pnlPatientNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPatientNotesLayout.setVerticalGroup(
            pnlPatientNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientNotes)
                .addContainerGap())
        );
        */
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Appointment history panel layout">    
        try{
            javax.swing.GroupLayout pnlAppointmentHistoryLayout = new javax.swing.GroupLayout(pnlAppointmentHistory);
            pnlAppointmentHistory.setLayout(pnlAppointmentHistoryLayout);
            pnlAppointmentHistoryLayout.setHorizontalGroup(
                pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 
                            getAppointmentHistoryScrollPaneWidth(), javax.swing.GroupLayout.PREFERRED_SIZE)
                    //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(getBetweenAppointmentHistoryAndFetchButtonGap(),
                            getBetweenAppointmentHistoryAndFetchButtonGap(),
                            getBetweenAppointmentHistoryAndFetchButtonGap())
                    .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    //.addContainerGap())
                    .addGap(7)
                )
            );
            pnlAppointmentHistoryLayout.setVerticalGroup(
                pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentHistoryLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
                    //.addGap(5,5,5))
            );
        }catch (Exception exc){
            String message = exc.getMessage() + "\n";
            ViewController.displayErrorMessage(message + "Raised in PatientView::initComponents()", 
                    "Patient view error", JOptionPane.WARNING_MESSAGE);
        }
        
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Further details panel layout">
        /*
        javax.swing.GroupLayout pnlFurtherDetailsLayout = new javax.swing.GroupLayout(pnlFurtherDetails);
        pnlFurtherDetails.setLayout(pnlFurtherDetailsLayout);
        pnlFurtherDetailsLayout.setHorizontalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                //.addContainerGap()
                .addGap(getFurtherDetailsGapWidth(),
                        getFurtherDetailsGapWidth(),
                        getFurtherDetailsGapWidth())
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbRequestModalGuardianEditorView)
                    .addComponent(rdbRequestModalRecallEditorView)
                    .addComponent(rdbRequestModalPhoneEmailEditorView))
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(getFurtherDetailsGapWidth(),
                        getFurtherDetailsGapWidth(),
                        getFurtherDetailsGapWidth()))
        );
        pnlFurtherDetailsLayout.setVerticalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbRequestModalPhoneEmailEditorView)
                .addGap(17, 17, 17)
                .addComponent(rdbRequestModalRecallEditorView)
                .addGap(18, 18, 18)
                .addComponent(rdbRequestModalGuardianEditorView)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        */
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Further selectable information panel layout">
        rdbRequestModalNotesEditorView.setText(DISPLAY_PATIENT_NOTES_EDITOR_VIEW);

        javax.swing.GroupLayout pnlFurtherDetailsLayout = new javax.swing.GroupLayout(pnlFurtherDetails);
        pnlFurtherDetails.setLayout(pnlFurtherDetailsLayout);
        pnlFurtherDetailsLayout.setHorizontalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                //.addContainerGap()
                .addGap(20,20,20)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    
                    .addComponent(rdbRequestModalPhoneEmailEditorView)
                    .addComponent(rdbRequestModalRecallEditorView))
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(23,23,23)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbRequestModalGuardianEditorView)
                    .addComponent(rdbRequestModalNotesEditorView))
                .addGap(20,20,20))
        );
        pnlFurtherDetailsLayout.setVerticalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestModalPhoneEmailEditorView)
                    .addComponent(rdbRequestModalGuardianEditorView))
                .addGap(28, 28, 28)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestModalRecallEditorView)
                    .addComponent(rdbRequestModalNotesEditorView))
                .addGap(27, 27, 27))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Operations panel layout">
        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                //.addContainerGap(15, Short.MAX_VALUE)
                .addGap(8,8,8)
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdateSelectedPatient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreateNewPatient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                //.addContainerGap())
                //.addGap(15,15,15)
            )
        );
        pnlOperationsLayout.setVerticalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateNewPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55,55,55)
                .addComponent(btnUpdateSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55,55,55)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55,55,55))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Patient selection panel layout">
        javax.swing.GroupLayout pnlPatientSelectionLayout = new javax.swing.GroupLayout(pnlPatientSelection);
        pnlPatientSelection.setLayout(pnlPatientSelectionLayout);
        pnlPatientSelectionLayout.setHorizontalGroup(
            pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbPatientSelector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btnClearSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 58, Short.MAX_VALUE)))
                            
                //.addContainerGap())
                .addGap(20,20,20))
        );
        pnlPatientSelectionLayout.setVerticalGroup(
            pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClearSelection)
                .addGap(36, 36, 36))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Name content panel layout">
        javax.swing.GroupLayout pnlNameContentLayout = new javax.swing.GroupLayout(pnlNameContent);
        pnlNameContent.setLayout(pnlNameContentLayout);
        pnlNameContentLayout.setHorizontalGroup(
            pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameContentLayout.createSequentialGroup()
                .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNameForename)
                    .addComponent(lblNameSurname)
                    .addComponent(lblNameTitle)
                    .addComponent(lblNameDOB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNameSurname)
                    .addComponent(txtNameForename)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNameContentLayout.createSequentialGroup()
                        .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlNameContentLayout.createSequentialGroup()
                                .addComponent(cmbNameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblNameGender))
                            .addGroup(pnlNameContentLayout.createSequentialGroup()
                                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 20, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNameAge)
                            .addComponent(cmbNameGender, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        pnlNameContentLayout.setVerticalGroup(
            pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameContentLayout.createSequentialGroup()
                .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNameForename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNameForename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNameSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNameSurname))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNameGender)
                    .addComponent(cmbNameGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNameTitle)
                    .addComponent(cmbNameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlNameContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNameDOB)
                    .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNameAge))
                .addGap(0, 0, 0))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Name & particulars panel layout">
        javax.swing.GroupLayout pnlNameLayout = new javax.swing.GroupLayout(pnlName);
        pnlName.setLayout(pnlNameLayout);
        pnlNameLayout.setHorizontalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlNameContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlNameLayout.setVerticalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlNameContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
//</editor-fold>


//<editor-fold defaultstate="collapsed" desc="View layout">
        try{
            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(pnlFurtherDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    //.addComponent(pnlPatientNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    //.addComponent(pnlPatientNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    //.addComponent(pnlFurtherDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGap(10)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(pnlAddress, javax.swing.GroupLayout.DEFAULT_SIZE, /*javax.swing.GroupLayout.DEFAULT_SIZE*/ 150, Short.MAX_VALUE)
                                .addComponent(pnlName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                              .addGap(10)
                            .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, /*javax.swing.GroupLayout.DEFAULT_SIZE*/ 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE))
                        //.addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.DEFAULT_SIZE, /*javax.swing.GroupLayout.DEFAULT_SIZE*/ 500, Short.MAX_VALUE))
                        .addComponent(pnlAppointmentHistory))
                    .addContainerGap())
            );
            layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    //.addContainerGap()
                    .addGap(4,4,4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(pnlPatientSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnlName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(4,4,4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(pnlAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                //.addComponent(pnlPatientNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnlFurtherDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, getOperationsHeight(), javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }catch (Exception exc){
            String message = exc.getMessage() + "\n";
            ViewController.displayErrorMessage(message + "Raised in PatientView::iniialiseView()", 
                    "Patient view error", JOptionPane.WARNING_MESSAGE);
        }
//</editor-fold>    

        pack();

    }
    
    
    /*
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGuardianDetails = new javax.swing.JPanel();
        cmbSelectGuardian = new javax.swing.JComboBox<model.Patient>();
        lblGuardianPatientName = new javax.swing.JLabel();
        lblGuardianIsAPatient = new javax.swing.JLabel();
        cmbIsGuardianAPatient = new javax.swing.JComboBox<YesNoItem>();
        pnlPatientNotes = new javax.swing.JPanel();
        txtPatientNotes = new javax.swing.JTextField();
        pnlAppointmentHistory = new javax.swing.JPanel();
        scrAppointmentHistory = new javax.swing.JScrollPane();
        tblAppointmentHistory = new javax.swing.JTable();
        btnFetchScheduleForSelectedAppointment = new javax.swing.JButton();
        pnlPatientSelection = new javax.swing.JPanel();
        cmbPatientSelector = new javax.swing.JComboBox<Patient>();
        btnClearSelection = new javax.swing.JButton();
        pnlRecall = new javax.swing.JPanel();
        recallDatePicker = null;
        //
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setVisibleDateTextField(false);
        dateSettings.setGapBeforeButtonPixels(0);

        recallDatePicker = new com.github.lgooddatepicker.components.DatePicker(dateSettings);
        txtRecallDate = new javax.swing.JTextField();
        txtRecallDate.getDocument().addDocumentListener(documentListener);
        txtRecallDate.setEditable(false);
        ;
        jLabel2 = new javax.swing.JLabel();
        spnDentalRecallFrequency = new javax.swing.JSpinner();
        pnlPhones = new javax.swing.JPanel();
        txtPhone1 = new javax.swing.JTextField();
        txtPhone2 = new javax.swing.JTextField();
        lblPhone1 = new javax.swing.JLabel();
        lblPhone2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        pnlAddress = new javax.swing.JPanel();
        txtAddressLine1 = new javax.swing.JTextField();
        lblLine1 = new javax.swing.JLabel();
        lblline2 = new javax.swing.JLabel();
        txtAddressLine2 = new javax.swing.JTextField();
        lblTown = new javax.swing.JLabel();
        txtAddressTown = new javax.swing.JTextField();
        lblCounty = new javax.swing.JLabel();
        txtAddressCounty = new javax.swing.JTextField();
        lblPostcode = new javax.swing.JLabel();
        txtAddressPostcode = new javax.swing.JTextField();
        pnlName = new javax.swing.JPanel();
        txtNameForenames = new javax.swing.JTextField();
        txtNameSurname = new javax.swing.JTextField();
        cmbNameTitle = new javax.swing.JComboBox<TitleItem>();
        cmbNameGender = new javax.swing.JComboBox<GenderItem>();
        lblTitle = new javax.swing.JLabel();
        lblGender = new javax.swing.JLabel();
        dobDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dobDatePicker.setSettings(settings);
        ;
        lblNameAge = new javax.swing.JLabel();
        lblGender1 = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        btnCreateNewPatient = new javax.swing.JButton();
        btnUpdateSelectedPatient = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        mbaPatientView = new javax.swing.JMenuBar();
        mnuActions = new javax.swing.JMenu();
        mniCreateNewPatient = new javax.swing.JMenuItem();
        mniUpdateSelectedPatient = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniDeleteSelectedPatient = new javax.swing.JMenuItem();
        mniRecoverDeletedPatient = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlGuardianDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Guardian details (patient < 18)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(51, 0, 204))); // NOI18N

        cmbSelectGuardian.addItemListener(itemSelectGuardianListener);
        cmbSelectGuardian.setEditable(false);
        cmbSelectGuardian.setModel(new DefaultComboBoxModel<model.Patient>());
        cmbSelectGuardian.setMinimumSize(new java.awt.Dimension(175, 22));
        cmbSelectGuardian.setPreferredSize(new java.awt.Dimension(194, 22));
        cmbSelectGuardian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSelectGuardianActionPerformed(evt);
            }
        });

        lblGuardianPatientName.setText("Select guardian");

        lblGuardianIsAPatient.setText("Guardian is a patient?");

        cmbIsGuardianAPatient.addItemListener(itemListener);
        cmbIsGuardianAPatient.setEditable(true);
        cmbIsGuardianAPatient.setModel(new javax.swing.DefaultComboBoxModel<YesNoItem>(YesNoItem.values()));
        cmbIsGuardianAPatient.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmbIsGuardianAPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIsGuardianAPatientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGuardianDetailsLayout = new javax.swing.GroupLayout(pnlGuardianDetails);
        pnlGuardianDetails.setLayout(pnlGuardianDetailsLayout);
        pnlGuardianDetailsLayout.setHorizontalGroup(
            pnlGuardianDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGuardianDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGuardianDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGuardianDetailsLayout.createSequentialGroup()
                        .addComponent(lblGuardianIsAPatient)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbIsGuardianAPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlGuardianDetailsLayout.createSequentialGroup()
                        .addComponent(lblGuardianPatientName)
                        .addGap(18, 18, 18)
                        .addComponent(cmbSelectGuardian, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGuardianDetailsLayout.setVerticalGroup(
            pnlGuardianDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGuardianDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGuardianDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGuardianIsAPatient)
                    .addComponent(cmbIsGuardianAPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlGuardianDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGuardianPatientName)
                    .addComponent(cmbSelectGuardian, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pnlPatientNotes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Notes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        pnlPatientNotes.setPreferredSize(new java.awt.Dimension(351, 115));

        txtPatientNotes.setText("jTextField2");

        javax.swing.GroupLayout pnlPatientNotesLayout = new javax.swing.GroupLayout(pnlPatientNotes);
        pnlPatientNotes.setLayout(pnlPatientNotesLayout);
        pnlPatientNotesLayout.setHorizontalGroup(
            pnlPatientNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPatientNotes)
                .addContainerGap())
        );
        pnlPatientNotesLayout.setVerticalGroup(
            pnlPatientNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientNotesLayout.createSequentialGroup()
                .addComponent(txtPatientNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        pnlAppointmentHistory.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Appointment history (latest apppointment top of list)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        pnlAppointmentHistory.setBackground(new java.awt.Color(220, 220, 220));

        scrAppointmentHistory.setRowHeaderView(null);

        tblAppointmentHistory.addMouseListener(mouseListener);
        scrAppointmentHistory.setViewportView(tblAppointmentHistory);

        btnFetchScheduleForSelectedAppointment.setText("selected appointment");
        btnFetchScheduleForSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFetchScheduleForSelectedAppointmentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAppointmentHistoryLayout = new javax.swing.GroupLayout(pnlAppointmentHistory);
        pnlAppointmentHistory.setLayout(pnlAppointmentHistoryLayout);
        pnlAppointmentHistoryLayout.setHorizontalGroup(
            pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAppointmentHistoryLayout.setVerticalGroup(
            pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlPatientSelection.setBackground(new java.awt.Color(220, 220, 220));
        pnlPatientSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Select patient to recover", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        cmbPatientSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPatientSelectorActionPerformed(evt);
            }
        });

        btnClearSelection.setText("Cancel recovery");
        btnClearSelection.setText("Clear patient selection");
        btnClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearSelectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPatientSelectionLayout = new javax.swing.GroupLayout(pnlPatientSelection);
        pnlPatientSelection.setLayout(pnlPatientSelectionLayout);
        pnlPatientSelectionLayout.setHorizontalGroup(
            pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPatientSelectionLayout.setVerticalGroup(
            pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(btnClearSelection)
                .addGap(28, 28, 28))
        );

        pnlRecall.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Recall date", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        pnlRecall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        txtRecallDate.setPreferredSize(new Dimension(100,20));
        //pnlRecallDatePicker.add(txtRecallDate);
        //pnlRecallDatePicker.setLayout(new FlowLayout());

        txtRecallDate.setText(null);
        txtRecallDate.setPreferredSize(new java.awt.Dimension(85, 20));

        jLabel2.setText("frequency");

        spnDentalRecallFrequency.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spnDentalRecallFrequency.setModel(new SpinnerNumberModel(6,0,12,3));
        spnDentalRecallFrequency.setToolTipText("recall frequency (months)");
        JTextField jtf = ((javax.swing.JSpinner.DefaultEditor)spnDentalRecallFrequency.getEditor()).getTextField();
        jtf.getDocument().addDocumentListener(documentListener);

        javax.swing.GroupLayout pnlRecallLayout = new javax.swing.GroupLayout(pnlRecall);
        pnlRecall.setLayout(pnlRecallLayout);
        pnlRecallLayout.setHorizontalGroup(
            pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlRecallLayout.createSequentialGroup()
                        .addComponent(txtRecallDate, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(spnDentalRecallFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlRecallLayout.createSequentialGroup()
                        .addComponent(recallDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addGap(30, 30, 30))
        );
        pnlRecallLayout.setVerticalGroup(
            pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(recallDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlRecallLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRecallDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnDentalRecallFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtRecallDate.setHorizontalAlignment(JTextField.CENTER);

        pnlPhones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Phone number(s) & email", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        txtPhone1.getDocument().addDocumentListener(documentListener);
        txtPhone1.setText("07582265943");

        txtPhone2.getDocument().addDocumentListener(documentListener);
        txtPhone2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhone2ActionPerformed(evt);
            }
        });

        lblPhone1.setText("[1]");

        lblPhone2.setText("[2]");

        lblEmail.setText("Email");

        javax.swing.GroupLayout pnlPhonesLayout = new javax.swing.GroupLayout(pnlPhones);
        pnlPhones.setLayout(pnlPhonesLayout);
        pnlPhonesLayout.setHorizontalGroup(
            pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPhone1)
                    .addComponent(lblEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlPhonesLayout.createSequentialGroup()
                        .addComponent(txtPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPhone2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtEmail))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPhonesLayout.setVerticalGroup(
            pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPhonesLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPhone1)
                    .addComponent(lblPhone2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail))
                .addGap(21, 21, 21))
        );

        pnlAddress.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Address", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        txtAddressLine1.getDocument().addDocumentListener(documentListener);
        txtAddressLine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressLine1ActionPerformed(evt);
            }
        });

        lblLine1.setText("Line 1");

        lblline2.setText("Line 2");

        txtAddressLine2.getDocument().addDocumentListener(documentListener);
        txtAddressLine2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressLine2ActionPerformed(evt);
            }
        });

        lblTown.setText("Town");

        txtAddressTown.getDocument().addDocumentListener(documentListener);

        lblCounty.setText("County");

        txtAddressCounty.getDocument().addDocumentListener(documentListener);

        lblPostcode.setText("Postcode");

        txtAddressPostcode.getDocument().addDocumentListener(documentListener);
        txtAddressPostcode.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlAddressLayout = new javax.swing.GroupLayout(pnlAddress);
        pnlAddress.setLayout(pnlAddressLayout);
        pnlAddressLayout.setHorizontalGroup(
            pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblline2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTown, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCounty)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPostcode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddressLayout.setVerticalGroup(
            pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLine1)
                    .addComponent(txtAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblline2)
                    .addComponent(txtAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTown)
                    .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCounty)
                    .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPostcode)
                    .addComponent(txtAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pnlName.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Name & particulars", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 0, 204))); // NOI18N

        txtNameForenames.getDocument().addDocumentListener(documentListener);
        txtNameForenames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameForenamesActionPerformed(evt);
            }
        });

        txtNameSurname.getDocument().addDocumentListener(documentListener);
        txtNameSurname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameSurnameActionPerformed(evt);
            }
        });

        cmbNameTitle.addItemListener(itemListener);
        cmbNameTitle.setEditable(true);
        cmbNameTitle.setModel(new javax.swing.DefaultComboBoxModel<>(TitleItem.values()));
        cmbNameTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmbNameTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbNameTitleActionPerformed(evt);
            }
        });

        cmbNameGender.addItemListener(itemListener);
        cmbNameGender.setEditable(true);
        cmbNameGender.setModel(new javax.swing.DefaultComboBoxModel<>(GenderItem.values()));
        cmbNameGender.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTitle.setText("Title");

        lblGender.setText("Gender");

        lblNameAge.setText("(age)");

        lblGender1.setText("DOB");

        javax.swing.GroupLayout pnlNameLayout = new javax.swing.GroupLayout(pnlName);
        pnlName.setLayout(pnlNameLayout);
        pnlNameLayout.setHorizontalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtNameForenames, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtNameSurname, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbNameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblGender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbNameGender, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblGender1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblNameAge)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNameLayout.setVerticalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addGroup(pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNameForenames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNameSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbNameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbNameGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitle)
                    .addComponent(lblGender)
                    .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNameAge)
                    .addComponent(lblGender1))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        pnlActions.setBackground(new java.awt.Color(220, 220, 220));

        btnCreateNewPatient.setText("Update selected patient");

        btnUpdateSelectedPatient.setText("Close patient view");

        btnCloseView.setText("Create new patient");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(141, 141, 141)
                .addComponent(btnCreateNewPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdateSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnUpdateSelectedPatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCreateNewPatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        mnuActions.setText("Actions");

        mniCreateNewPatient.setText("Create new patient");
        mniCreateNewPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCreateNewPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniCreateNewPatient);

        mniUpdateSelectedPatient.setText("Update selected patient details");
        mniUpdateSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniUpdateSelectedPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniUpdateSelectedPatient);
        mnuActions.add(jSeparator2);

        mniDeleteSelectedPatient.setText("Delete selected patient");
        mniDeleteSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniDeleteSelectedPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniDeleteSelectedPatient);

        mniRecoverDeletedPatient.setText("Recover deleted patient");
        mniRecoverDeletedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniRecoverDeletedPatientActionPerformed(evt);
            }
        });
        mnuActions.add(mniRecoverDeletedPatient);
        mnuActions.add(jSeparator3);

        mniCloseView.setText("Close view");
        mniCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCloseViewActionPerformed(evt);
            }
        });
        mnuActions.add(mniCloseView);

        mbaPatientView.add(mnuActions);

        setJMenuBar(mbaPatientView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pnlPatientNotes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)
                        .addComponent(pnlAddress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pnlActions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pnlRecall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlGuardianDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pnlPhones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(pnlName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(pnlPatientNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRecall, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlGuardianDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPhones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    */
    private void cmbSelectGuardianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSelectGuardianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSelectGuardianActionPerformed
/*
    private void cmbIsGuardianAPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIsGuardianAPatientActionPerformed
        if (this.cmbIsGuardianAPatient.getSelectedItem()!=null){
            switch ((YesNoItem)this.cmbIsGuardianAPatient.getSelectedItem()){
                case Yes:
                    this.cmbSelectGuardian.setEnabled(true);
                    break;
                case No:
                    this.cmbSelectGuardian.setEnabled(false);
                    break;
            }
        }
    }//GEN-LAST:event_cmbIsGuardianAPatientActionPerformed
*/
    private void btnFetchScheduleForSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFetchScheduleForSelectedAppointmentActionPerformed
        // TODO add your handling code here:
        if (this.tblAppointmentHistory.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected");
        }
        else{
            int row = this.tblAppointmentHistory.getSelectedRow();
            LocalDate day = ((LocalDateTime)this.tblAppointmentHistory.getValueAt(row,0)).toLocalDate();
            getMyController().getDescriptor().getViewDescription().setScheduleDay(day);
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientViewControllerActionEvent.SCHEDULE_VIEW_CONTROLLER_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_btnFetchScheduleForSelectedAppointmentActionPerformed

    private void mniDeleteSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniDeleteSelectedPatientActionPerformed
        int response;
        Patient patient = getMyController().getDescriptor().getControllerDescription().getPatient();
        getMyController().getDescriptor().getViewDescription().setPatient(patient);
        if (patient.getIsKeyDefined()){
            String message ="Are you sure you want to delete patient " + patient.toString() + "'s details?";
            response = JOptionPane.showConfirmDialog(this,message, "Action selected patient notifications", JOptionPane.YES_NO_OPTION);
            if (response==JOptionPane.YES_OPTION){
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientViewControllerActionEvent.PATIENT_DELETE_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "A patient has not yet been selected for deletion\n"
                    + "Click the 'Select/clear patient details' from the menu options to select a patient");
    }//GEN-LAST:event_mniDeleteSelectedPatientActionPerformed

    private void createNewPatientActionPerformed(){
        
        if (!getMyController().getDescriptor().getControllerDescription().getPatient().getIsKeyDefined()){
            if (this.validateMinimumPatientDetails()){
                getMyController().getDescriptor().getViewDescription()
                        .setPatient(initialisePatientFromView(
                                getMyController().getDescriptor()
                                        .getControllerDescription().getPatient()));
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.PATIENT_CREATE_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "A new patient cannot be created until the currently selected patient is deselected\n"
                    + "Click the 'Select/clear patient details' from the menu options to deselect the selected patient");
    }
    
    private void btnCreateNewPatientActionPerformed(java.awt.event.ActionEvent evt){
        switch (getPatientSelectionMode()){
            case PATIENT_RECOVERY:
                switch(cmbPatientSelector.getSelectedIndex()){
                    case -1:
                        JOptionPane.showMessageDialog(
                                this, 
                                "A patient has not been selected for recovery");
                        break;
                    default:
                        getMyController().getDescriptor().getViewDescription()
                                .setPatient(initialisePatientFromView(
                                        (Patient)cmbPatientSelector.getSelectedItem())
                                );
                        setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                        ActionEvent actionEvent = new ActionEvent(
                                this,ActionEvent.ACTION_PERFORMED,
                                ViewController.PatientViewControllerActionEvent.RECOVER_PATIENT_REQUEST.toString());
                        this.getMyController().actionPerformed(actionEvent);
                        break;
                }
                break;
            case PATIENT_SELECTION:
                //setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                createNewPatientActionPerformed();
                break;
        }
        
    }
    
    private void mniCreateNewPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCreateNewPatientActionPerformed
        createNewPatientActionPerformed();
    }//GEN-LAST:event_mniCreateNewPatientActionPerformed

    private void updateSelectedPatientActionPerformed(){
        Patient patient = null;
        
        if (getMyController().getDescriptor().getControllerDescription().getPatient().getIsKeyDefined()){
            
            if (this.validateMinimumPatientDetails()){
                patient = getMyController().getDescriptor().getControllerDescription().getPatient();
                getMyController().getDescriptor().getViewDescription().setPatient(
                        initialisePatientFromView(patient));
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.PATIENT_UPDATE_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Update operation cannot proceed unless an existing patient is currently selected");
    }
    
    private void btnUpdateSelectedPatientActionPerformed(java.awt.event.ActionEvent evt){
        switch (getPatientSelectionMode()){
            case PATIENT_SELECTION:
                updateSelectedPatientActionPerformed();
                break;
            case PATIENT_RECOVERY:
                setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                btnClearSelectionActionPerformed(evt);
                break;
        }
        
    }
    
    private void mniUpdateSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniUpdateSelectedPatientActionPerformed
        updateSelectedPatientActionPerformed();
    }//GEN-LAST:event_mniUpdateSelectedPatientActionPerformed

    private void closeViewActionPerformed(){
        if (getViewStatus()){
            String[] options = {"Yes", "No"};
            int close = JOptionPane.showOptionDialog(this,
                "Any changes to patient record will be lost. Cancel anyway?",null,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                null);
            if (close == JOptionPane.YES_OPTION){
                try{
                    /**
                    * setClosed will fire INTERNAL_FRAME_CLOSED event for the
                    * listener to send ActionEvent to the view controller
                    */
                    this.setClosed(true);
                }
                catch (PropertyVetoException e){
                    //UnspecifiedError action
                }
            }   
        }
        else {
            try{
                    /**
                    * setClosed will fire INTERNAL_FRAME_CLOSED event for the
                    * listener to send ActionEvent to the view controller
                    */
                    this.setClosed(true);
                }
            catch (PropertyVetoException e){
                //UnspecifiedError action
            }
        }
    }
    
    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt){
        closeViewActionPerformed();
    }
    
    private void mniCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCloseViewActionPerformed
        closeViewActionPerformed();
        
    }//GEN-LAST:event_mniCloseViewActionPerformed

    private void txtPhone2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhone2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhone2ActionPerformed

    private void txtAddressLine2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressLine2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressLine2ActionPerformed

    private void txtAddressLine1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressLine1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressLine1ActionPerformed

    private void cmbNameTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbNameTitleActionPerformed
        if (this.cmbNameTitle.getSelectedItem() != null){
            if (this.cmbNameTitle.getSelectedItem().equals(TitleItem.Untitled)){
                this.cmbNameTitle.setSelectedIndex(-1);
            }
        }
    }//GEN-LAST:event_cmbNameTitleActionPerformed

    private void txtNameForenamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameForenamesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameForenamesActionPerformed

    private void mniRecoverDeletedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRecoverDeletedPatientActionPerformed
        setPatientSelectionMode(PatientSelectionMode.PATIENT_RECOVERY);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.PATIENT_RECOVER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        mniRecoverDeletedPatient.setEnabled(false);
        mniCreateNewPatient.setEnabled(false);
        this.btnUpdateSelectedPatient.setEnabled(true);
    }//GEN-LAST:event_mniRecoverDeletedPatientActionPerformed
    
    
    private void cmbPatientSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPatientSelectorActionPerformed
        ViewController.PatientViewControllerActionEvent event = null; 
        switch(getPatientSelectionMode()){
            case PATIENT_SELECTION:
                event = ViewController.PatientViewControllerActionEvent.PATIENT_REQUEST;
                break;
            case PATIENT_RECOVERY:
                event = ViewController.PatientViewControllerActionEvent.DELETED_PATIENT_REQUEST;
                break;
        }
        if (cmbPatientSelector.getSelectedIndex()!=-1){
            getMyController().getDescriptor().getViewDescription().
                    setPatient((Patient)cmbPatientSelector.getSelectedItem());
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED, 
                    event.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_cmbPatientSelectorActionPerformed

    private void txtNameSurnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameSurnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameSurnameActionPerformed

    private void btnClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSelectionActionPerformed
        getMyController().getDescriptor().getViewDescription().setPatient(new Patient());
        this.cmbPatientSelector.setSelectedIndex(-1);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_btnClearSelectionActionPerformed

    /*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateNewPatient;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JButton btnUpdateSelectedPatient;
    private javax.swing.JComboBox<YesNoItem> cmbIsGuardianAPatient;
    private javax.swing.JComboBox<GenderItem> cmbNameGender;
    private javax.swing.JComboBox<TitleItem> cmbNameTitle;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JComboBox<model.Patient> cmbSelectGuardian;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JLabel lblCounty;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblGender1;
    private javax.swing.JLabel lblGuardianIsAPatient;
    private javax.swing.JLabel lblGuardianPatientName;
    private javax.swing.JLabel lblLine1;
    private javax.swing.JLabel lblNameAge;
    private javax.swing.JLabel lblPhone1;
    private javax.swing.JLabel lblPhone2;
    private javax.swing.JLabel lblPostcode;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTown;
    private javax.swing.JLabel lblline2;
    private javax.swing.JMenuBar mbaPatientView;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniCreateNewPatient;
    private javax.swing.JMenuItem mniDeleteSelectedPatient;
    private javax.swing.JMenuItem mniRecoverDeletedPatient;
    private javax.swing.JMenuItem mniUpdateSelectedPatient;
    private javax.swing.JMenu mnuActions;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlAddress;
    private javax.swing.JPanel pnlAppointmentHistory;
    private javax.swing.JPanel pnlGuardianDetails;
    private javax.swing.JPanel pnlName;
    private javax.swing.JPanel pnlPatientNotes;
    private javax.swing.JPanel pnlPatientSelection;
    private javax.swing.JPanel pnlPhones;
    private javax.swing.JPanel pnlRecall;
    private com.github.lgooddatepicker.components.DatePicker recallDatePicker;
    private javax.swing.JScrollPane scrAppointmentHistory;
    private javax.swing.JSpinner spnDentalRecallFrequency;
    private javax.swing.JTable tblAppointmentHistory;
    private javax.swing.JTextField txtAddressCounty;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressLine2;
    private javax.swing.JTextField txtAddressPostcode;
    private javax.swing.JTextField txtAddressTown;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNameForenames;
    private javax.swing.JTextField txtNameSurname;
    private javax.swing.JTextField txtPatientNotes;
    private javax.swing.JTextField txtPhone1;
    private javax.swing.JTextField txtPhone2;
    private javax.swing.JTextField txtRecallDate;
    // End of variables declaration//GEN-END:variables
   */
    /*
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JComboBox<PatientView.GenderItem> cmbNameGender;
    private javax.swing.JComboBox<PatientView.YesNoItem> cmbIsGuardianAPatient;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JComboBox<model.Patient> cmbSelectGuardian;
    private javax.swing.JComboBox<PatientView.TitleItem> cmbNameTitle;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JButton btnUpdateSelectedPatient;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateNewPatient;
    private javax.swing.JLabel lblPhone1;
    private javax.swing.JLabel lblFrequency;
    private javax.swing.JLabel lblPhone2;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JMenuBar mbaPatientView;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTextField txtPhonesEmail;
    private javax.swing.JLabel lblNameAge;
    private javax.swing.JLabel lblAddressCounty;
    private javax.swing.JLabel lblNameGender;
    private javax.swing.JLabel lblNameDOB;
    private javax.swing.JLabel lblGuardianIsAPatient;
    private javax.swing.JLabel lblGuardianPatientName;
    private javax.swing.JLabel lblAddressLine1;
    private javax.swing.JLabel lblAddressPostcode;
    private javax.swing.JLabel lblNameForename;
    private javax.swing.JLabel lblNameSurname;
    private javax.swing.JLabel lblNameTitle;
    private javax.swing.JLabel lblAddressTown;
    private javax.swing.JLabel lblAddressline2;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniCreateNewPatient;
    private javax.swing.JMenuItem mniDeleteSelectedPatient;
    private javax.swing.JMenuItem mniRecoverDeletedPatient;
    private javax.swing.JMenuItem mniUpdateSelectedPatient;
    private javax.swing.JMenu mnuActions;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlAddress;
    private javax.swing.JPanel pnlAppointmentHistory;
    private javax.swing.JPanel pnlGuardianDetails;
    private javax.swing.JPanel pnlName;
    private javax.swing.JPanel pnlPatientNotes;
    private javax.swing.JPanel pnlPhones;
    private javax.swing.JPanel pnlRecall;
    private javax.swing.JPanel pnlPatientSelection;
    private com.github.lgooddatepicker.components.DatePicker recallDatePicker;
    private javax.swing.JScrollPane scrAppointmentHistory;
    private javax.swing.JSpinner spnDentalRecallFrequency;
    private javax.swing.JTable tblAppointmentHistory;
    private javax.swing.JTextField txtAddressCounty;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressLine2;
    private javax.swing.JTextField txtAddressPostcode;
    private javax.swing.JTextField txtAddressTown;
    private javax.swing.JTextField txtNameForenames;
    private javax.swing.JTextField txtPatientNotes;
    private javax.swing.JTextField txtPhone1;
    private javax.swing.JTextField txtPhone2;
    private javax.swing.JTextField txtRecallDate;
    private javax.swing.JTextField txtNameSurname;
    private DatePicker dobPicker;
    private DatePicker dentalRecallPicker;
    private DatePicker hygieneRecallPicker;
    */
    
    private boolean validateMinimumPatientDetails(){
        boolean errorOnExit = false;
        //
        //if (String.valueOf(cmbIsGuardianAPatient.getSelectedItem()).equals("Yes")){
        //    if (this.cmbSelectGuardian.getSelectedIndex() == -1){
        //        JOptionPane.showMessageDialog(this, "Patient guardian has not been specified");
        //        errorOnExit = true;
        //    }
        //}
        //
        //else 
        if (this.cmbNameGender.getSelectedIndex()==-1){
            JOptionPane.showMessageDialog(this, "Patient gender must be specified");
            errorOnExit = true;
        }
        else if (this.getSurname()==null){
            JOptionPane.showMessageDialog(this, "Patient surname must be specified");
            errorOnExit = true;
        }
        else if (this.getSurname().isEmpty()){
            JOptionPane.showMessageDialog(this, "Patient surname must be specified");
            errorOnExit = true;
        }
        //else if (this.getPhone1()==null){
        //    JOptionPane.showMessageDialog(this, "Patient phone 1 must be specified");
        //    errorOnExit = true;
        //}
        //else if (this.getPhone1().isEmpty()){
        //    JOptionPane.showMessageDialog(this, "Patient phone 1 must be specified");
        //    errorOnExit = true;
        //}
        return !errorOnExit;
    }
    
    // Variables declaration - do not modify  
    private javax.swing.ButtonGroup rdbGroup;
    private javax.swing.JTable tblAppointmentHistory;
    private javax.swing.JMenuBar mbaPatientView;
    private javax.swing.JMenu mnuNotes;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateNewPatient;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JButton btnUpdateSelectedPatient;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<PatientView.GenderItem> cmbNameGender;
    private javax.swing.JComboBox<PatientView.TitleItem> cmbNameTitle;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JScrollPane scrPatientNotes;
    private javax.swing.JLabel lblAddressCounty;
    private javax.swing.JLabel lblAddressLine1;
    private javax.swing.JLabel lblAddressLine2;
    private javax.swing.JLabel lblAddressPostcode;
    private javax.swing.JLabel lblAddressTown;
    private javax.swing.JLabel lblNameAge;
    private javax.swing.JLabel lblNameDOB;
    private javax.swing.JLabel lblNameForename;
    private javax.swing.JLabel lblNameGender;
    private javax.swing.JLabel lblNameSurname;
    private javax.swing.JLabel lblNameTitle;
    private javax.swing.JPanel pnlAddress;
    private javax.swing.JPanel pnlAppointmentHistory;
    private javax.swing.JPanel pnlFurtherDetails;
    private javax.swing.JPanel pnlName;
    private javax.swing.JPanel pnlNameContent;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlPatientAddressContent;
    private javax.swing.JPanel pnlPatientNotes;
    private javax.swing.JPanel pnlPatientSelection;
    private javax.swing.JRadioButton rdbRequestModalMedicalProfilePopup;
    private javax.swing.JRadioButton rdbRequestModalGuardianEditorView;
    private javax.swing.JRadioButton rdbRequestModalRecallEditorView;
    private javax.swing.JRadioButton rdbRequestModalPhoneEmailEditorView;
    private javax.swing.JRadioButton rdbRequestModalNotesEditorView;
    private javax.swing.JScrollPane scrAppointmentHistory;
    private javax.swing.JTextArea txtPatientNotes;
    
    private javax.swing.JTextField txtAddressCounty;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressPostcode;
    private javax.swing.JTextField txtAddressTown;
    private javax.swing.JTextField txtNameForename;
    private javax.swing.JTextField txtAddressLine2;
    private javax.swing.JTextField txtNameSurname;
    private javax.swing.JMenuItem mniCreateNewPatient;
    private javax.swing.JMenuItem mniDeleteSelectedPatient;
    private javax.swing.JMenuItem mniRecoverDeletedPatient;
    private javax.swing.JMenuItem mniUpdateSelectedPatient;
    private javax.swing.JMenu mnuActions;
    // End of variables declaration   

    class DOBDatePickerDateChangeListener implements DateChangeListener {
        @Override
        public void dateChanged(DateChangeEvent event) {
            /**
             * Update logged at 30/10/2021 08:32
             * inherited view status (set if any changes have been made to form since its initialisation)
             * is initialised to true (date changed)
             */
            setViewStatus(true);
            LocalDate date = event.getNewDate();
            if (date != null) {
                lblNameAge.setText("(" + String.valueOf(getAge(date)) + " yrs)");
            }         
        }
    }
    
    private MenuMaker menuMaker = null;
    private void setMenuMaker(MenuMaker value){
        menuMaker = value;
    }
    private MenuMaker getMenuMaker(){
        return menuMaker;
    }
    
    private void doEnableNoteTaking(){
        mbaPatientView.add(mnuActions);
        try{
            setMenuMaker(new MenuMaker(
                    mbaPatientView,
                    "Patient",
                    "Medical history"));
            getMenuMaker().addMenuToViewMenubar();
            setJMenuBar(mbaPatientView);
        }catch(TemplateReaderException ex){
           ViewController.displayErrorMessage(
                   ex.getMessage(), 
                   "Patient view error",
                   JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doDisableNoteTaking(){
        mbaPatientView.add(mnuActions);
        setJMenuBar(mbaPatientView);
    }
       
    class MenuMaker{
        private String viewElementID = null;
        private JMenuBar menuBar = null;
        private String selectedMenuBarID = null;
        private JPopupMenu popupMenu;

        /**
         * 
         * @param menuBarValue JMenubar which is the parent of the first menu element to be processed
         *  -- the first menu element should have a node name = "menu1"
         * @param nextMenuNodeName String constant = "menu1"
         */
        MenuMaker(JMenuBar menuBarValue, 
                String viewElementIDValue,
                String selectedMenuBarIDValue)throws TemplateReaderException{
            viewElementID = viewElementIDValue;
            menuBar = menuBarValue; 
            selectedMenuBarID = selectedMenuBarIDValue;
        }
        
        MenuMaker(JPopupMenu popupMenuValue, String viewElementIDValue){
            viewElementID = viewElementIDValue;
            popupMenu = popupMenuValue;
        }
        
        private JPopupMenu getPopupMenu(){
            return popupMenu;
        }
    
        private JMenu rootMenu = null;
        private JMenu getRootMenu(){
            return rootMenu;
        }
        private void setRootMenu(JMenu value){
            rootMenu = value;
        }
        
        private JMenuBar getMenuBar(){
            return menuBar;
        }
        private void setMenuBar(JMenuBar value){
            menuBar = value;
        }
        
        private String getViewElementID(){
            return viewElementID;
        }
        
        private String getSelectedMenuBarID(){
            return selectedMenuBarID;
        }

        void addMenuToViewMenubar() throws TemplateReaderException{
            Element element = null;
            boolean isElementFound = false;
            Element selectedViewElement = null /*= getSelectedViewElementFromTemplate(getTemplate())*/;
            NodeList nodes = selectedViewElement.getElementsByTagName("menubar");
            if (nodes.getLength() == 0){
                String message = "View element tagged 'menubar' not found\n"
                        + "Raised in getSelectedViewElementFromTemplate() method";
                //throw new TemplateReaderException(message);
            }
            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element)node;
                    if (element.getAttribute("id").equals(getSelectedMenuBarID())){
                        isElementFound = true;
                        break;
                    } 
                }
            }
            if (isElementFound) {
                makeMenuBarMenuFrom(element);
            }
            else{
                String message = "menubar element in template with id = '" 
                        + getSelectedMenuBarID() + "' not found\n"
                        + "Raised in MenuMaker::addMenuToViewMenubar() method";
                //throw new TemplateReaderException(message);
            }
        }
        
        private void makePopupMenu1From(Element element){
            NodeList nodes = element.getElementsByTagName("menu1");
            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element)node;
                    JMenuItem menuItem = new JMenuItem();
                    menuItem.setText(element.getAttribute("id"));
                    getPopupMenu().add(menuItem);
                    makeMenu2CollectionsFrom(element); 
                }
            }
        }
        
        private void makeMenu2CollectionsFrom(Element elementValue){
            Element element = null;
            ArrayList<Element> elements = null;
            NodeList nodes = elementValue.getElementsByTagName("menu2");
            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element)node;
                    elements.add(element);
                }
            }
            switch(element.getAttribute("id")){
                case "Medical conditions":
                    makeMedicalConditionDialog(elements);
                    break;
                case "Prescribed medication":
                    break;
                case "Patient doctor's details":
                    break;
            }
            
        }
        
        private void makeMedicalConditionDialog(ArrayList<Element> elements){
            ArrayList<JCheckBox> checkBoxs = new ArrayList<>();
            Iterator medicalConditionIT = elements.iterator();
            while(medicalConditionIT.hasNext()){
                Element element = (Element)medicalConditionIT.next();
                switch(element.getAttribute("type")){
                    case "option":
                        JCheckBox checkBox = new JCheckBox();
                        checkBox.setText(element.getAttribute("id"));
                        checkBoxs.add(checkBox);
                    case "menu":{
                        ArrayList<Element> menu3Elements = new ArrayList<>();
                        NodeList nodes = element.getElementsByTagName("menu3");
                        for (int temp = 0; temp < nodes.getLength(); temp++) {
                            Node node = nodes.item(temp);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element menu3Element = (Element)node;
                                menu3Elements.add(menu3Element);
                            }
                        }
                        FatCheckBox fatCheckBox = new FatCheckBox(menu3Elements); 
                        fatCheckBox.setText(element.getAttribute("id"));
                        checkBoxs.add(fatCheckBox);
                        break;
                    }
                } 
            }
            new Dialog(getDesktopView(), 
                    true,
                    checkBoxs,
                    getMyController());
        }
        
        private void makeMenuBarMenuFrom(Element element){
            JMenu newMenu = new JMenu();
            newMenu.setText(element.getAttribute("id"));
            setRootMenu(newMenu);
            getMenuBar().add(newMenu);
            NodeList nodes = element.getElementsByTagName("menu1");
            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element)node;
                    makeMenu1From(element); 
                }
            }
        }
        
        private void makeMenu1From(Element element){
            
            switch (element.getAttribute("type")){
                case "menu":
                    JMenu newMenu = new JMenu();
                    newMenu.setText(element.getAttribute("id"));
                    getRootMenu().add(newMenu);
                    NodeList nodes = element.getElementsByTagName("menu2");
                    for (int temp = 0; temp < nodes.getLength(); temp++) {
                        Node node = nodes.item(temp);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            element = (Element)node;
                            makeMenu2From(element, newMenu); 
                        }
                    }
                    break;
                case "action":
                    JMenuItem newMenuItem = new JMenuItem();
                    newMenuItem.setText(element.getAttribute("id"));
                    getRootMenu().add(newMenuItem);
                    break;
                case "option":
                case "exclusive_option":
                    JMenuItem newOptionMenuItem = null;
                    if(element.getAttribute("type").equals("option"))
                        newOptionMenuItem = new JCheckBoxMenuItem();
                    else if(element.getAttribute("type").equals("exclusive_option"))
                        newOptionMenuItem = new JRadioButtonMenuItem();
                    newOptionMenuItem.setText(element.getAttribute("id"));
                    getRootMenu().add(newOptionMenuItem);
                    //addItemListenerFor(newOptionMenuItem);
                    break;
            }
        }
        
        private void makeMenu2CheckBoxListViewFrom(Element element){
            ArrayList<JCheckBox> checkBoxList = new ArrayList<>();
            NodeList nodes = element.getElementsByTagName(("menu2"));
            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setText(element.getAttribute("id"));
                    checkBoxList.add(checkBox); 
                }
            }
            getMyController().setModalView((ModalView)new View().make(
                    View.Viewer.CHECKBOX_LIST_VIEW,
                    getMyController(), 
                    getMyController().getDesktopView()).getModalView());

        }
        
        private void makeMenu2From(Element element, JMenu parentMenu){
            
            switch (element.getAttribute("type")){
                case "menu":
                    JMenu newMenu = new JMenu();
                    newMenu.setText(element.getAttribute("id"));
                    parentMenu.add(newMenu);
                    NodeList nodes = element.getElementsByTagName("menu3");
                    for (int temp = 0; temp < nodes.getLength(); temp++) {
                        Node node = nodes.item(temp);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            element = (Element)node;
                            makeMenu3From(element, newMenu); 
                        }
                    }
                    break;
                case "action":
                    break;
                case "option":
                case "exclusive_option":
                    JMenuItem newOptionMenuItem = null;
                    if(element.getAttribute("type").equals("option"))
                        newOptionMenuItem = new JCheckBoxMenuItem();
                    else if(element.getAttribute("type").equals("exclusive_option"))
                        newOptionMenuItem = new JRadioButtonMenuItem();
                    newOptionMenuItem.setText(element.getAttribute("id"));
                    parentMenu.add(newOptionMenuItem);
                    //addItemListenerFor(newOptionMenuItem);
                    break;
            }
        }
        
        private void makeMenu3From(Element element, JMenu parentMenu){
            
            switch (element.getAttribute("type")){
                case "menu":
                    JMenu newMenu = new JMenu();
                    newMenu.setText(element.getAttribute("id"));
                    parentMenu.add(newMenu);
                    NodeList nodes = element.getElementsByTagName("menu4");
                    for (int temp = 0; temp < nodes.getLength(); temp++) {
                        Node node = nodes.item(temp);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            element = (Element)node;
                            makeMenu4From(element, newMenu); 
                        }
                    }
                    break;
                case "action":
                    break;
                case "option":
                case "exclusive_option":
                    JMenuItem newOptionMenuItem = null;
                    if(element.getAttribute("type").equals("option"))
                        newOptionMenuItem = new JCheckBoxMenuItem();
                    else if(element.getAttribute("type").equals("exclusive_option"))
                        newOptionMenuItem = new JRadioButtonMenuItem();
                    newOptionMenuItem.setText(element.getAttribute("id"));
                    parentMenu.add(newOptionMenuItem);
                    //addItemListenerFor(newOptionMenuItem);
                    break;
            }
        }
        
        private void makeMenu4From(Element element, JMenu parentMenu){
            //JMenu newMenu = new JMenu();
            //newMenu.setText(element.getAttribute("id"));
            //parentMenu.add(newMenu);
            switch (element.getAttribute("type")){
                case "action":
                    break;
                case "option":
                case "exclusive_option":
                    JMenuItem newOptionMenuItem = null;
                    if(element.getAttribute("type").equals("option"))
                        newOptionMenuItem = new JCheckBoxMenuItem();
                    else if(element.getAttribute("type").equals("exclusive_option"))
                        newOptionMenuItem = new JRadioButtonMenuItem();
                    newOptionMenuItem.setText(element.getAttribute("id"));
                    parentMenu.add(newOptionMenuItem);
                    //addItemListenerFor(newOptionMenuItem);
                    break;
            }
        }
    }//class MenuMaker
   
    /*
        private void addActionListenerFor(Element element, javax.swing.JMenuItem menuItem){
            switch (menuItem.getText()){
                case "Medical condition":
                    menuItem.addActionListener((ActionEvent e) ->
                            makeMedicalConditionDialog(element));
                    break;                  
                case "Who recommended The Clinic to you?":
                    menuItem.addActionListener((ActionEvent e) -> 
                            mniEnterWhoRecommendedClinicActionPerformed());
                case "Patient's doctor details":
                    menuItem.addActionListener((ActionEvent e) -> 
                            mniEnterPatientDoctorActionPerformed());  
                
            }
        }
        */
        
    private void menuItemDisplayMenu2Options(){

    }
    
    private void mniEnterAllergyDescriptionActionPerformed(){
        
    }
    
    private void mniEnterRecallFrequencyActionPerformed(){
        
    }
    
    private void mniEnterRecallDateActionPerformed(){
        
    }
    
    private void mniEnterPatientDOBActionPerformed(){
        
    }
    
    private void mniEnterPatientNameActionPerformed(){
        
    }
    
    private void mniEnterWhoRecommendedClinicActionPerformed(){
        
    }
    
    private void mniEnterPatientDoctorActionPerformed(){
        
    }
    
    private String doComboboxDialog(String dialogPurpose){
        return null;
    }
    
    private String doTextEntryDialog(String dialogPurpose){
        return null;
    }      

    private void doDisplayMedicalProfileRequest(){
        JPopupMenu popup = new JPopupMenu();
        MenuMaker popupMenuMaker = new MenuMaker(popup, "Medical history");
    }
}


