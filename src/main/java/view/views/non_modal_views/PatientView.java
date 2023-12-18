/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.non_modal_views;

import _system_environment_variables.SystemDefinitions;
import view.views.view_support_classes.models.Appointments3ColumnTableModel;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import controller.Descriptor;
import controller.ViewController;
import view.View;
import model.Patient;
import model.Appointment;
import model.Entity;
import view.views.exceptions.CrossCheckErrorException; 
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Font;
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
import java.util.Optional;
//import javax.swing.JInternalFrame;
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
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;


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
public class PatientView extends View{
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
    private enum YesNoItem {No,
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

    static int mPatientViewHeight = 568;
    static int wPatientViewHeight = 550;
    static int mBeforeCreateNewPatientButtonGap = 87; 
    static int wBeforeCreateNewPatientButtonGap = 84;
    static int mBeforeDOBGap = 9;
    static int wBeforeDOBGap = 18;
    static int mBeforeFetchButtonGap = 15;
    static int wBeforeFetchButtonGap = 25; 
    static int mBeforeFrequencyLabelGap = 88;
    static int wBeforeFrequencyLabelGap = 125;
    static int mBelowClearSelection = 27;
    static int wBelowClearSelection = 32;
    static int mBelowPhonesGap = 23;
    static int wBelowPhonesGap = 17;
    static int mBetweenFrequencyLabelAndSpinnerGap = 18;
    static int wBetweenFrequencyLabelAndSpinnerGap = 17;
    static int mBetweenPhonesAndGuardianPanelsGap = 20;
    static int wBetweenPhonesAndGuardianPanelsGap = 34;
    static int mBetweenGuardianAndRecallPanelsGap = 21;
    static int wBetweenGuardianAndRecallPanelsGap = 33;
    static int mDatePickerWidth = 135;
    static int wDatePickerWidth = 131;
    static int mLine2Width = 180;
    static int wLine2Width = 182;
    
    private int getPatientViewHeight (){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getBeforeCreateNewPatientButtonGap (){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getBetweenFrequencyLabelAndSpinnerGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getBeforeFrequencyLabelGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getBetweenGuardianAndRecallPanelsGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getBetweenPhonesAndGuardianPanelsGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getBelowPhonesGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getLine2Width(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getDatePickerWidth(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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

    private int getDOBGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getFetchButtonGap(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
    
    private int getGapBelowClearSelection(){
        int result = 0;
        String lookAndFeel = SystemDefinitions.getPMSLookAndFeel();
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
        try{
            setVisible(true);
            setClosable(false);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            setSize(968,593);
        }catch(PropertyVetoException ex){
            
        }
        this.addInternalFrameListeners();
        btnFetchScheduleForSelectedAppointment.setText(
                "<html><center>Fetch day schedule</center><center>for selected</center><center>appointment</center></html>");
        //this.spnDentalRecallFrequency.setModel(new SpinnerNumberModel(6,0,12,3));
        tblAppointmentHistory.setModel(new Appointments3ColumnTableModel());
        ViewController.setJTableColumnProperties(
                tblAppointmentHistory, 
                scrAppointmentHistory.getPreferredSize().width, 
                22,22,56);
        populatePatientSelector(this.cmbPatientSelector); 
        populatePatientSelector(this.cmbSelectGuardian);
        this.cmbPatientSelector.addActionListener((ActionEvent e) -> cmbPatientSelectorActionPerformed(e));
        DatePickerSettings settings = new DatePickerSettings();
        dobDatePicker.addDateChangeListener((new PatientView.DOBDatePickerDateChangeListener()));
        recallDatePicker.addDateChangeListener(new PatientView.RecallDatePickerDateChangeListener()); 
        
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dobDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        datePickerButton = recallDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        this.pnlGuardianDetails.setEnabled(false);
        this.cmbIsGuardianAPatient.setEnabled(false);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 cmbPatientSelector.requestFocus();
              }
           });
    };
    
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
    
    /**
     * 07/11/2021 11:07 dev. log update
     * Implements appointment double click event which displays appointment schedule day
     * for row in appointment history table that's been double clicked
     * Mouse listener added in the initialisation code for the JTable component 
     * in "initComponents")
     */
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
            if (String.valueOf(cmbIsGuardianAPatient.getSelectedItem()).equals("Yes")){
                if (cmbIsGuardianAPatient.getSelectedIndex() == -1) setViewStatus(false);
                else setViewStatus(true);
            }
            else setViewStatus(false); 
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
        ViewController.PatientViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED:
                //setViewDescriptor((Descriptor)e.getNewValue());
                ViewController.displayErrorMessage(
                        getMyController().getDescriptor().getControllerDescription().getError(),
                        "Patient vew error", JOptionPane.WARNING_MESSAGE);
                break;
            case PATIENT_RECEIVED:
                //setViewDescriptor((Descriptor)e.getNewValue());
                initialisePatientViewComponentFromED(); 
                String frameTitle = getMyController().getDescriptor().getControllerDescription().getPatient().toString();
                this.setTitle(frameTitle);
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
                //setViewDescriptor((Descriptor)e.getNewValue());
                //setViewMode(PatientView.ViewMode.Create_new_patient);
                initialisePatientViewComponentFromED();
                populatePatientSelector(this.cmbPatientSelector);
                populatePatientSelector(this.cmbSelectGuardian);
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
                populatePatientSelector(this.cmbSelectGuardian);
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
    }
    
    private void populateAppointmentsHistoryTable(Patient patient){
        Appointments3ColumnTableModel tableModel = 
                (Appointments3ColumnTableModel)tblAppointmentHistory.getModel(); 
        tableModel.removeAllElements();
        try{
            if (patient.getIsKeyDefined()){//if patient data in view has just been cleared  
                patient.setScope(Entity.Scope.FOR_PATIENT);
                Iterator<Appointment> it = patient.getAppointmentHistory().iterator();
                while (it.hasNext()){
                    tableModel.addElement(it.next());
                }
            }
            TableColumnModel columnModel = this.tblAppointmentHistory.getColumnModel();
            columnModel.getColumn(1).setPreferredWidth(120);
        this.tblAppointmentHistory.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        this.tblAppointmentHistory.setDefaultRenderer(LocalDateTime.class, new AppointmentsTableLocalDateTimeRenderer());;
        this.tblAppointmentHistory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
            //this.cmbSelectPatient.setSelectedItem(patient);
        this.setTitle(getSurname()); //Internal frame title
        setPatientTitle(patient.getName().getTitle());
        setForenames(patient.getName().getForenames());
        setSurname(patient.getName().getSurname());
        setPhone1(patient.getPhone1());
        setPhone2(patient.getPhone2());
        setLine1(patient.getAddress().getLine1());
        setLine2(patient.getAddress().getLine2());
        setTown(patient.getAddress().getTown());
        setCounty(patient.getAddress().getCounty());
        setPostcode(patient.getAddress().getPostcode());
        setRecallDate(patient.getRecall().getDentalDate());
        setDentalRecallFrequency(patient.getRecall().getDentalFrequency());
        setGender(patient.getGender());
        setNotes(patient.getNotes());
        setDOB(patient.getDOB());
        setIsGuardianAPatient(patient.getIsGuardianAPatient());
        //update 30/07/2021 09:05 applied
        if(getMyController().getDescriptor().getControllerDescription().
                getPatient().getGuardian()!=null)
                this.cmbSelectGuardian.setSelectedItem(getMyController().
                        getDescriptor().getControllerDescription().getPatient().getGuardian());
        else this.cmbSelectGuardian.setSelectedIndex(-1); 
        //following is new statement
        populateAppointmentsHistoryTable(patient);
        //initialisePatientAppointmentHistoryViewFromED(PatientView.Category.DENTAL);
    }
    
    
    private Patient initialisePatientFromView(Patient patient){
        if (patient== null) patient = new Patient();
            patient.getAddress().setCounty(getCounty());
            patient.getRecall().setDentalDate(getDentalRecallDate());
            patient.setDOB(getDOB());
            patient.getName().setForenames(getForenames());
            patient.setGender(getGender());
            patient.getRecall().setDentalDate(getDentalRecallDate());
            patient.getRecall().setDentalFrequency(getDentalRecallFrequency());
            patient.setIsGuardianAPatient(getIsGuardianAPatient());
            patient.getAddress().setLine1(getLine1());
            patient.getAddress().setLine2(getLine2());
            patient.setNotes(getNotes());
            patient.setPhone1(getPhone1());
            patient.setPhone2(getPhone2());
            patient.getAddress().setPostcode(getPostcode());
            patient.getName().setSurname(getSurname());
            patient.getName().setTitle(getPatientTitle());
            patient.getAddress().setTown(getTown());
            if (getGuardian() != null){
                patient.setGuardian(getGuardian());
            }
        return patient;
    }

    private String getPatientTitle(){
        String value = "";
        if(PatientView.TitleItem.Dr.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Dr.toString();
        }
        else if(PatientView.TitleItem.Mr.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Mr.toString();
        }
        else if(PatientView.TitleItem.Mrs.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Mrs.toString();
        }
        else if(PatientView.TitleItem.Ms.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Ms.toString();
        }
        else if(PatientView.TitleItem.Miss.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = PatientView.TitleItem.Miss.toString();
        }
 
        return value;
    }
    private void setPatientTitle(String title){
        if (title == null){
            cmbTitle.setSelectedIndex(-1);
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
                cmbTitle.setSelectedIndex(index);
            }
            else {
                cmbTitle.setSelectedIndex(-1);
            }
        }
    }
    private String getForenames(){
        return this.txtForenames.getText();
    }
    private void setForenames(String forenames){
        if (forenames == null) this.txtForenames.setText("");
        else this.txtForenames.setText(forenames);
    }
    private String getSurname(){
        return this.txtSurname.getText();
    }
    private void setSurname(String surname){
        if (surname == null) this.txtSurname.setText("");
        else this.txtSurname.setText(surname);
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
        if (this.cmbGender.getSelectedIndex()!=-1){
            result = this.cmbGender.getSelectedItem().toString();
        }
        return result;
    }
    private void setGender(String gender){
        if (gender == null) cmbGender.setSelectedIndex(-1);
        else{
            Integer index = null;
            for (PatientView.GenderItem gi: PatientView.GenderItem.values()){
                if (gi.toString().equals(gender)){
                    index = gi.ordinal();
                    break;
                }
            }
            if (index != null){
                cmbGender.setSelectedIndex(index);
            }
            else {
                cmbGender.setSelectedIndex(-1);
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
            lblAge.setText("(" + String.valueOf(getAge(value)) + " yrs)");   
        }
        else{
            this.dobDatePicker.setDate(value);
            lblAge.setText("(years)");
        }
    }
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
    private String getNotes(){
        return this.txtPatientNotes.getText();
    }
    private void setNotes(String notes){
        if (notes == null) this.txtPatientNotes.setText("");
        else this.txtPatientNotes.setText(notes);
    }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
        
    private void initComponents(){ 
    //<editor-fold defaultstate="collapsed" desc="Component Definitions">
    //<editor-fold defaultstate="collapsed" desc="Panel definitions">
        pnlActions = new javax.swing.JPanel();
        pnlAddress = new javax.swing.JPanel();
        pnlAppointmentHistory = new javax.swing.JPanel();
        pnlGuardianDetails = new javax.swing.JPanel();
        pnlName = new javax.swing.JPanel();
        pnlNotes = new javax.swing.JPanel();
        pnlPhones = new javax.swing.JPanel();
        pnlRecall = new javax.swing.JPanel();
    
        pnlSelectablePatients = new javax.swing.JPanel();
        pnlSelectablePatients.setBackground(new java.awt.Color(220, 220, 220));
        pnlSelectablePatients.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Select patient to recover", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12), 
                        new java.awt.Color(0, 0, 204))); // NOI18N 
        
        pnlAppointmentHistory.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Appointment history (latest apppointment top of list)", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12), 
                        new java.awt.Color(0, 0, 204))); // NOI18N
        pnlAppointmentHistory.setBackground(new java.awt.Color(220, 220, 220));

        pnlName.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Name & particulars", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12), 
                        new java.awt.Color(0, 0, 204))); // NOI18N
        
        pnlAddress.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Address", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12), 
                        new java.awt.Color(0, 0, 204))); // NOI18N
        
        pnlNotes.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Notes", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12),
                        new java.awt.Color(0, 0, 204))); // NOI18N
        
        pnlPhones.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Phone number(s) & email", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12),
                        new java.awt.Color(0, 0, 204))); // NOI18N

        pnlGuardianDetails.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Guardian details (patient < 18)", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12),
                        new java.awt.Color(0, 0, 204))); // NOI18N
        
        pnlRecall.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Recall details", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12),
                        new java.awt.Color(0, 0, 204))); // NOI18N
        
        pnlActions.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        new java.awt.Font("Segoe UI", 1, 12),
                        new java.awt.Color(0, 0, 204))); // NOI18N
        pnlActions.setBackground(new java.awt.Color(220, 220, 220));
        
    //</editor-fold>
        btnFetchScheduleForSelectedAppointment = new javax.swing.JButton("<html><center>Fetch day schedule</center><center>for selected</center><center>appointment</center></html>");
        btnClearSelection = new javax.swing.JButton("Clear patient selection");
        btnUpdateSelectedPatient = new javax.swing.JButton("Update selected patient");
        btnCloseView = new javax.swing.JButton("Close view");
        btnCreatePatient = new javax.swing.JButton("Create new patient");
        
        cmbSelectGuardian = new javax.swing.JComboBox<model.Patient>();
        cmbIsGuardianAPatient = new javax.swing.JComboBox<PatientView.YesNoItem>();
        cmbPatientSelector = new javax.swing.JComboBox<Patient>();
        cmbTitle = new javax.swing.JComboBox<>();
        cmbGender = new javax.swing.JComboBox<>();
        
        lblAge = new javax.swing.JLabel("age");
        lblNameDOB = new javax.swing.JLabel("DOB");
        lblNameForename = new javax.swing.JLabel("Forename");
        lblNameSurname = new javax.swing.JLabel("Surname");
        lblNameTitle = new javax.swing.JLabel("Title");
        lblNameGender = new javax.swing.JLabel("Gender");
        lblAddressCounty = new javax.swing.JLabel("County");
        lblGuardianPatientName = new javax.swing.JLabel("Select guardian");
        lblGuardianIsAPatient = new javax.swing.JLabel("Guardian is a patient?");
        lblAddressLine1 = new javax.swing.JLabel("Line 1");
        lblAddressline2 = new javax.swing.JLabel("Line 2");
        lblAddressTown = new javax.swing.JLabel("Town");
        lblAddressPostcode = new javax.swing.JLabel("Postcode");
        lblPhone1 = new javax.swing.JLabel("[1]");
        lblFrequency = new javax.swing.JLabel("Frequency");
        lblPhone2 = new javax.swing.JLabel("[2]");
        lblEmail = new javax.swing.JLabel("Email");
        
        //menu bits
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuActions = new javax.swing.JMenu();
        mniCreateNewPatient = new javax.swing.JMenuItem();
        mniUpdateSelectedPatient = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniDeleteSelectedPatient = new javax.swing.JMenuItem();
        mniRecoverDeletedPatient = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();
        
        
        
        

        scrAppointmentHistory = new javax.swing.JScrollPane();
        spnDentalRecallFrequency = new javax.swing.JSpinner();
        
        tblAppointmentHistory = new javax.swing.JTable();

        txtForenames = new javax.swing.JTextField();
        txtSurname = new javax.swing.JTextField();
        txtAddressLine2 = new javax.swing.JTextField();
        txtAddressLine1 = new javax.swing.JTextField();
        txtAddressTown = new javax.swing.JTextField();
        txtAddressCounty = new javax.swing.JTextField();
        txtPatientNotes = new javax.swing.JTextField();
        txtAddressPostcode = new javax.swing.JTextField();
        txtRecallDate = new javax.swing.JTextField();
        txtPhone1 = new javax.swing.JTextField();
        txtPhone2 = new javax.swing.JTextField();
        txtPhonesEmail = new javax.swing.JTextField();

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setVisibleDateTextField(false);
        dateSettings.setGapBeforeButtonPixels(0);
        recallDatePicker = new com.github.lgooddatepicker.components.DatePicker(dateSettings);

        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dobDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);

        txtRecallDate.getDocument().addDocumentListener(documentListener);
        txtRecallDate.setEditable(false);

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

        cmbIsGuardianAPatient.addItemListener(itemListener);
        cmbIsGuardianAPatient.setEditable(true);
        cmbIsGuardianAPatient.setModel(new javax.swing.DefaultComboBoxModel<PatientView.YesNoItem>(PatientView.YesNoItem.values()));
        cmbIsGuardianAPatient.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmbIsGuardianAPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIsGuardianAPatientActionPerformed(evt);
            }
        });
        
        txtForenames.getDocument().addDocumentListener(documentListener);
        txtForenames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtForenamesActionPerformed(evt);
            }
        });

        txtSurname.getDocument().addDocumentListener(documentListener);
        txtSurname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSurnameActionPerformed(evt);
            }
        });

        cmbTitle.addItemListener(itemListener);
        cmbTitle.setEditable(true);
        cmbTitle.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.TitleItem.values()));
        cmbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmbTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTitleActionPerformed(evt);
            }
        });

        cmbGender.addItemListener(itemListener);
        cmbGender.setEditable(true);
        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.GenderItem.values()));
        cmbGender.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNameTitle.setText("Title");
        lblNameForename.setText("Forenames");
        lblNameSurname.setText("Surname");
        lblNameGender.setText("Gender");

        lblAge.setText("(age)");

        lblNameDOB.setText("DOB");
        

        txtAddressLine1.getDocument().addDocumentListener(documentListener);
        txtAddressLine1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressLine1ActionPerformed(evt);
            }
        });

        lblAddressLine1.setText("Line 1");

        lblAddressline2.setText("Line 2");

        txtAddressLine2.getDocument().addDocumentListener(documentListener);
        txtAddressLine2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressLine2ActionPerformed(evt);
            }
        });
        
        
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

        jMenuBar1.add(mnuActions);

        setJMenuBar(jMenuBar1);
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Layout Code">
    //<editor-fold defaultstate="collapsed" desc="Frame layout">
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        //layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            //layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSelectablePatients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    )
                    .addGap(9,9,9)
                    .addComponent(pnlName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                      //.addComponent(pnlAddress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)  ))
                    .addComponent(pnlAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)

                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlPhones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(getBetweenPhonesAndGuardianPanelsGap(),
                                getBetweenPhonesAndGuardianPanelsGap(),
                                getBetweenPhonesAndGuardianPanelsGap())
                        .addComponent(pnlGuardianDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        //.addGap(33,33,33)
                        .addGap(getBetweenGuardianAndRecallPanelsGap(),
                                getBetweenGuardianAndRecallPanelsGap(),
                                getBetweenGuardianAndRecallPanelsGap())
                        .addComponent(pnlRecall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    )
                    .addGap(9,9,9)    
                    .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
            )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                //.addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSelectablePatients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                //.addGap(9, 9, 9)
                .addComponent(pnlName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRecall, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlGuardianDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPhones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        
        pack();
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Individual panel layouts">
    //<editor-fold defaultstate="collapsed" desc="Appointment history panel layout">
        javax.swing.GroupLayout pnlAppointmentHistoryLayout = new javax.swing.GroupLayout(pnlAppointmentHistory);
        pnlAppointmentHistory.setLayout(pnlAppointmentHistoryLayout);
        pnlAppointmentHistoryLayout.setHorizontalGroup(
            pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGap(getFetchButtonGap(),getFetchButtonGap(),getFetchButtonGap())
                .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAppointmentHistoryLayout.setVerticalGroup(
            pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                 .addContainerGap()
                .addGroup(pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Patient selection panel layout">
        javax.swing.GroupLayout pnlSelectablePatientsLayout = new javax.swing.GroupLayout(pnlSelectablePatients);
        pnlSelectablePatients.setLayout(pnlSelectablePatientsLayout);
        pnlSelectablePatientsLayout.setHorizontalGroup(
            pnlSelectablePatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectablePatientsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSelectablePatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearSelection, javax.swing.GroupLayout.DEFAULT_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            );
        pnlSelectablePatientsLayout.setVerticalGroup(
            pnlSelectablePatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectablePatientsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(btnClearSelection)
                //.addGap(28, 28, 28))
                //.addGap(32,32,32)
                    
                .addGap(getGapBelowClearSelection(),
                        getGapBelowClearSelection(),
                        getGapBelowClearSelection()))
                    
        );
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Name panel layout">
        javax.swing.GroupLayout pnlNameLayout = new javax.swing.GroupLayout(pnlName);
        pnlName.setLayout(pnlNameLayout);
        pnlNameLayout.setHorizontalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNameForename, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtForenames, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10,10,10)
                .addComponent(lblNameSurname, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10,10,10)
                .addComponent(lblNameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbTitle, javax.swing.GroupLayout.PREFERRED_SIZE,55 , javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10,10,10)
                .addComponent(lblNameGender, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(getDOBGap(),getDOBGap(),getDOBGap())
                .addComponent(lblNameDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, getDatePickerWidth(), javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10,10,10)
                .addComponent(lblAge)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNameLayout.setVerticalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addGroup(pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(this.lblNameForename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtForenames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(this.lblNameForename)
                    .addComponent(this.lblNameSurname)
                    
                    .addComponent(lblNameTitle)
                    .addComponent(lblNameGender)
                    .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAge)
                    .addComponent(lblNameDOB))
                .addGap(0, 4, Short.MAX_VALUE))
        );   
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Address panel layout">> 
        javax.swing.GroupLayout pnlAddressLayout = new javax.swing.GroupLayout(pnlAddress);
        pnlAddress.setLayout(pnlAddressLayout);
        pnlAddressLayout.setHorizontalGroup(
            pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15,15,15)
                .addComponent(lblAddressline2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, getLine2Width(), javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15,15,15)
                .addComponent(lblAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15,15,15)
                .addComponent(lblAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15,15,15)
                .addComponent(lblAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
                );
        pnlAddressLayout.setVerticalGroup(
            pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddressLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pnlAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAddressLine1)
                    //.addGap(0,0,0)
                    .addComponent(txtAddressLine1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressline2)
                    .addComponent(txtAddressLine2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressTown)
                    .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressCounty)
                    .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressPostcode)
                    .addComponent(txtAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 8, Short.MAX_VALUE))
        );
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Notes panel layout">
        javax.swing.GroupLayout pnlNotesLayout = new javax.swing.GroupLayout(pnlNotes);
        pnlNotes.setLayout(pnlNotesLayout);
        pnlNotesLayout.setHorizontalGroup(
            pnlNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPatientNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 909, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlNotesLayout.setVerticalGroup(
            pnlNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotesLayout.createSequentialGroup()
                .addComponent(txtPatientNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Phones panel layout">
    javax.swing.GroupLayout pnlPhonesLayout = new javax.swing.GroupLayout(pnlPhones);
        pnlPhones.setLayout(pnlPhonesLayout);
        pnlPhonesLayout.setHorizontalGroup(
            pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPhone1)
                    .addComponent(lblEmail)
                )
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlPhonesLayout.createSequentialGroup()
                        .addComponent(txtPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPhone2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPhonesEmail)
                )
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPhonesLayout.setVerticalGroup(
            pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPhonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPhone1)
                    .addComponent(lblPhone2)
                )
                .addGap(getBelowPhonesGap())
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhonesEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail)
                )
                .addContainerGap()    
            )
        );
        
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Panel guardian details">
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            )
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
                //.addContainerGap(25, Short.MAX_VALUE)
                  .addContainerGap()
            )
        );
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Recall panel layout">
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
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        //.addGap(125,125,125)
                        .addGap(getBeforeFrequencyLabelGap(),
                                getBeforeFrequencyLabelGap(),
                                getBeforeFrequencyLabelGap())
                        .addComponent(lblFrequency)))
                .addGap(30, 30, 30))
        );
        pnlRecallLayout.setVerticalGroup(
            pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(recallDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    //.addGap(20,20,20)
                    .addGroup(pnlRecallLayout.createSequentialGroup()
                        .addComponent(lblFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    )
                )
                //.addGap(17,17,17)
                .addGap(getBetweenFrequencyLabelAndSpinnerGap(),
                        getBetweenFrequencyLabelAndSpinnerGap(),
                        getBetweenFrequencyLabelAndSpinnerGap())
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRecallLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRecallDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnDentalRecallFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Actions panel layout">
    javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                //.addGap(87,87,87)
                .addGap(getBeforeCreateNewPatientButtonGap(),
                        getBeforeCreateNewPatientButtonGap(),
                        getBeforeCreateNewPatientButtonGap())
                .addComponent(btnCreatePatient, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118)
                .addComponent(btnUpdateSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(118, 118, 118)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btnCreatePatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btnUpdateSelectedPatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                .addContainerGap())
        );
    //</editor-fold>
    // </editor-fold>
    //</editor-fold>
    
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
        pnlNotes = new javax.swing.JPanel();
        txtPatientNotes = new javax.swing.JTextField();
        pnlAppointmentHistory = new javax.swing.JPanel();
        scrAppointmentHistory = new javax.swing.JScrollPane();
        tblAppointmentHistory = new javax.swing.JTable();
        btnFetchScheduleForSelectedAppointment = new javax.swing.JButton();
        pnlSelectablePatients = new javax.swing.JPanel();
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
        txtForenames = new javax.swing.JTextField();
        txtSurname = new javax.swing.JTextField();
        cmbTitle = new javax.swing.JComboBox<TitleItem>();
        cmbGender = new javax.swing.JComboBox<GenderItem>();
        lblTitle = new javax.swing.JLabel();
        lblGender = new javax.swing.JLabel();
        dobDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dobDatePicker.setSettings(settings);
        ;
        lblAge = new javax.swing.JLabel();
        lblGender1 = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        btnCreatePatient = new javax.swing.JButton();
        btnUpdateSelectedPatient = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
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

        pnlNotes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Notes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        pnlNotes.setPreferredSize(new java.awt.Dimension(351, 115));

        txtPatientNotes.setText("jTextField2");

        javax.swing.GroupLayout pnlNotesLayout = new javax.swing.GroupLayout(pnlNotes);
        pnlNotes.setLayout(pnlNotesLayout);
        pnlNotesLayout.setHorizontalGroup(
            pnlNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPatientNotes)
                .addContainerGap())
        );
        pnlNotesLayout.setVerticalGroup(
            pnlNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotesLayout.createSequentialGroup()
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

        pnlSelectablePatients.setBackground(new java.awt.Color(220, 220, 220));
        pnlSelectablePatients.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Select patient to recover", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

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

        javax.swing.GroupLayout pnlSelectablePatientsLayout = new javax.swing.GroupLayout(pnlSelectablePatients);
        pnlSelectablePatients.setLayout(pnlSelectablePatientsLayout);
        pnlSelectablePatientsLayout.setHorizontalGroup(
            pnlSelectablePatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectablePatientsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSelectablePatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSelectablePatientsLayout.setVerticalGroup(
            pnlSelectablePatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectablePatientsLayout.createSequentialGroup()
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

        txtForenames.getDocument().addDocumentListener(documentListener);
        txtForenames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtForenamesActionPerformed(evt);
            }
        });

        txtSurname.getDocument().addDocumentListener(documentListener);
        txtSurname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSurnameActionPerformed(evt);
            }
        });

        cmbTitle.addItemListener(itemListener);
        cmbTitle.setEditable(true);
        cmbTitle.setModel(new javax.swing.DefaultComboBoxModel<>(TitleItem.values()));
        cmbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmbTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTitleActionPerformed(evt);
            }
        });

        cmbGender.addItemListener(itemListener);
        cmbGender.setEditable(true);
        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(GenderItem.values()));
        cmbGender.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTitle.setText("Title");

        lblGender.setText("Gender");

        lblAge.setText("(age)");

        lblGender1.setText("DOB");

        javax.swing.GroupLayout pnlNameLayout = new javax.swing.GroupLayout(pnlName);
        pnlName.setLayout(pnlNameLayout);
        pnlNameLayout.setHorizontalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtForenames, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(lblGender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(lblGender1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblAge)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNameLayout.setVerticalGroup(
            pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNameLayout.createSequentialGroup()
                .addGroup(pnlNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtForenames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitle)
                    .addComponent(lblGender)
                    .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAge)
                    .addComponent(lblGender1))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        pnlActions.setBackground(new java.awt.Color(220, 220, 220));

        btnCreatePatient.setText("Update selected patient");

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
                .addComponent(btnCreatePatient, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(btnCreatePatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jMenuBar1.add(mnuActions);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pnlNotes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 944, Short.MAX_VALUE)
                        .addComponent(pnlAddress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pnlSelectablePatients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(pnlSelectablePatients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(pnlName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void mniCreateNewPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCreateNewPatientActionPerformed
        if (!getMyController().getDescriptor().getControllerDescription().getPatient().getIsKeyDefined()){
            if (this.validateMinimumPatientDetails()){
                getMyController().getDescriptor().getViewDescription().setPatient(initialisePatientFromView(null));
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.PATIENT_CREATE_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "A new patient cannot be created until the currently selected patient is deselected\n"
                    + "Click the 'Select/clear patient details' from the menu options to deselect the selected patient");
    }//GEN-LAST:event_mniCreateNewPatientActionPerformed

    private void mniUpdateSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniUpdateSelectedPatientActionPerformed
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
    }//GEN-LAST:event_mniUpdateSelectedPatientActionPerformed

    private void mniCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCloseViewActionPerformed
        /**
         * update logged at 30/10/2021 08:32 ensures cautionary dialog only displayed 
         * if a change has been made in the view since its launched
         */
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

    private void cmbTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTitleActionPerformed
        if (this.cmbTitle.getSelectedItem() != null){
            if (this.cmbTitle.getSelectedItem().equals(TitleItem.Untitled)){
                this.cmbTitle.setSelectedIndex(-1);
            }
        }
    }//GEN-LAST:event_cmbTitleActionPerformed

    private void txtForenamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtForenamesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtForenamesActionPerformed

    private void mniRecoverDeletedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniRecoverDeletedPatientActionPerformed
        int response;
        Patient patient = getMyController().getDescriptor().
                getControllerDescription().getPatient();
        if (!patient.getIsKeyDefined()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.PATIENT_RECOVER_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        else
            JOptionPane.showMessageDialog(this, "A patient is already selected for recovery which is unexpected\n"
                    + "Patient recovery operation aborted");
    }//GEN-LAST:event_mniRecoverDeletedPatientActionPerformed

    private void cmbPatientSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPatientSelectorActionPerformed
        if (cmbPatientSelector.getSelectedIndex()!=-1){
            getMyController().getDescriptor().getViewDescription().
                    setPatient((Patient)cmbPatientSelector.getSelectedItem());
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED, 
                    ViewController.PatientViewControllerActionEvent.PATIENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_cmbPatientSelectorActionPerformed

    private void txtSurnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSurnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSurnameActionPerformed

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
    private javax.swing.JButton btnCreatePatient;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JButton btnUpdateSelectedPatient;
    private javax.swing.JComboBox<GenderItem> cmbGender;
    private javax.swing.JComboBox<YesNoItem> cmbIsGuardianAPatient;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JComboBox<model.Patient> cmbSelectGuardian;
    private javax.swing.JComboBox<TitleItem> cmbTitle;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JLabel lblAge;
    private javax.swing.JLabel lblCounty;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblGender1;
    private javax.swing.JLabel lblGuardianIsAPatient;
    private javax.swing.JLabel lblGuardianPatientName;
    private javax.swing.JLabel lblLine1;
    private javax.swing.JLabel lblPhone1;
    private javax.swing.JLabel lblPhone2;
    private javax.swing.JLabel lblPostcode;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTown;
    private javax.swing.JLabel lblline2;
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
    private javax.swing.JPanel pnlNotes;
    private javax.swing.JPanel pnlPhones;
    private javax.swing.JPanel pnlRecall;
    private javax.swing.JPanel pnlSelectablePatients;
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
    private javax.swing.JTextField txtForenames;
    private javax.swing.JTextField txtPatientNotes;
    private javax.swing.JTextField txtPhone1;
    private javax.swing.JTextField txtPhone2;
    private javax.swing.JTextField txtRecallDate;
    private javax.swing.JTextField txtSurname;
    // End of variables declaration//GEN-END:variables
   */
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JComboBox<PatientView.GenderItem> cmbGender;
    private javax.swing.JComboBox<PatientView.YesNoItem> cmbIsGuardianAPatient;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JComboBox<model.Patient> cmbSelectGuardian;
    private javax.swing.JComboBox<PatientView.TitleItem> cmbTitle;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JButton btnUpdateSelectedPatient;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreatePatient;
    private javax.swing.JLabel lblPhone1;
    private javax.swing.JLabel lblFrequency;
    private javax.swing.JLabel lblPhone2;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTextField txtPhonesEmail;
    private javax.swing.JLabel lblAge;
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
    private javax.swing.JPanel pnlNotes;
    private javax.swing.JPanel pnlPhones;
    private javax.swing.JPanel pnlRecall;
    private javax.swing.JPanel pnlSelectablePatients;
    private com.github.lgooddatepicker.components.DatePicker recallDatePicker;
    private javax.swing.JScrollPane scrAppointmentHistory;
    private javax.swing.JSpinner spnDentalRecallFrequency;
    private javax.swing.JTable tblAppointmentHistory;
    private javax.swing.JTextField txtAddressCounty;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressLine2;
    private javax.swing.JTextField txtAddressPostcode;
    private javax.swing.JTextField txtAddressTown;
    private javax.swing.JTextField txtForenames;
    private javax.swing.JTextField txtPatientNotes;
    private javax.swing.JTextField txtPhone1;
    private javax.swing.JTextField txtPhone2;
    private javax.swing.JTextField txtRecallDate;
    private javax.swing.JTextField txtSurname;
    private DatePicker dobPicker;
    private DatePicker dentalRecallPicker;
    private DatePicker hygieneRecallPicker;

    class RecallDatePickerDateChangeListener implements DateChangeListener {
        @Override
        public void dateChanged(DateChangeEvent event) {
            LocalDate date = event.getNewDate();
            if (date != null) {
                txtRecallDate.setText(date.format(recallFormat));
            }
            else txtRecallDate.setText("");
        }
    }
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
                lblAge.setText("(" + String.valueOf(getAge(date)) + " yrs)");
                if (getAge(date) > 17){
                    PatientView.this.pnlGuardianDetails.setEnabled(false);
                    PatientView.this.cmbIsGuardianAPatient.setSelectedIndex(-1);
                    PatientView.this.cmbIsGuardianAPatient.setEnabled(false);
                    PatientView.this.cmbSelectGuardian.setEnabled(false);
                }
                else {
                    PatientView.this.pnlGuardianDetails.setEnabled(true);
                    PatientView.this.cmbIsGuardianAPatient.setEnabled(true);
                }
            }
            else{
                PatientView.this.cmbIsGuardianAPatient.setSelectedIndex(-1);
                PatientView.this.cmbIsGuardianAPatient.setEnabled(false);
                PatientView.this.cmbSelectGuardian.setEnabled(false);
            }         
        }
    }
    
    /*
    private void cmbSelectPatientActionPerformed(){
        if (this.cmbSelectPatient.getSelectedItem()!=null){
            Patient patient = 
                    (Patient)this.cmbSelectPatient.getSelectedItem();
            getViewDescriptor().getViewDescription().setPatient(patient);
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientViewControllerActionEvent.PATIENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            setTitle(patient.toString());
        }else setTitle("No patient currently selected");
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    } 
*/
    private boolean validateMinimumPatientDetails(){
        boolean errorOnExit = false;
        if (String.valueOf(cmbIsGuardianAPatient.getSelectedItem()).equals("Yes")){
            if (this.cmbSelectGuardian.getSelectedIndex() == -1){
                JOptionPane.showMessageDialog(this, "Patient guardian has not been specified");
                errorOnExit = true;
            }
        }
        else if (this.cmbGender.getSelectedIndex()==-1){
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
        else if (this.getPhone1()==null){
            JOptionPane.showMessageDialog(this, "Patient phone 1 must be specified");
            errorOnExit = true;
        }
        else if (this.getPhone1().isEmpty()){
            JOptionPane.showMessageDialog(this, "Patient phone 1 must be specified");
            errorOnExit = true;
        }
        return !errorOnExit;
    }

}
