/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import model.SystemDefinition;
import view.views.view_support_classes.models.Appointments3ColumnTableModel;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import controller.Descriptor;
import controller.ViewController;
import view.View;
import model.Patient;
import model.Appointment;
import model.Entity;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import controller.TemplateReader;
import java.awt.Color;
import java.awt.event.*;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import model.PatientNote;
import view.views.view_support_classes.renderers.AppointmentsTablePatientNoteRenderer;
/**
 *
 * @author colin
 */
public class PatientView extends View implements ActionListener{
    private JTextField txtAddressLine2 = null;
    private javax.swing.ButtonGroup rdbGroup = null;
    //private JTable tblAppointmentHistory = null;
    
    private final String cancelPatientRecoveryCaption = "<html><center>Cancel</center><center>patient</center><center>recovery</center>";
    
    private final String panelPatientAppointmentHistoryCaption = "Appointment history";
    private final String panelPatientActionsCaption = "Actions";
    private final String panelPatientAddressCaption = "Address";
    private final String panelPatientDetailsCaption = "Name & particulars";
    private final String panelPatientFurtherDetailsCaption = "Further patient information";
    private final String panelPatientRecoveryTitle = "Select patient to recover";
    private final String panelPatientSelectionCaption = "Select patient";
    
    
    private final String patientFetchScheduleCaption = "<html><center>Schedule</center><center>for selected</center><center>appointment</center></html>";
    private final String patientClearSelectionCaption = "Clear selection";
    private final String patientCloseViewCaption = "Close view";
    private final String patientClinicalNotesCaption = "<html><center>Clinical</center><center>notes for</center><center>patient</center></html>";
    private final String patientCreateCaption = "<html><center>Create</center><center>new</center><center>patient</center></html>";
    private final String patientRecoveryCaption = "<html><center>Recover patient</center></html>";
    private final String patientUpdateCaption = "<html><center>Update</center><center>selected</center><center>patient</center></html>";
    
    
  
    private final String DISPLAY_RECALL_EDITOR_VIEW ="Recall details";
    //28/02/2024 07:45
    private final String DISPLAY_MEDICAL_PROFILE = "Medical history";
    private final String DISPLAY_GUARDIAN_EDITOR_VIEW = "Guardian (if patient)";
    private final String DISPLAY_PHONE_EMAIL_EDITOR_VIEW = "Phone/email";
    private final String DISPLAY_PATIENT_NOTES_EDITOR_VIEW = "Patient notes editor";
    
    private enum PatientSelectionMode{ PATIENT_SELECTION, 
                                       PATIENT_RECOVERY}
    private enum Actions{
        REQUEST_CLOSE_VIEW,
        REQUEST_CLINICAL_NOTES,
        REQUEST_CREATE_RECOVER_PATIENT,
        REQUEST_DOCTOR,
        REQUEST_GUARDIAN_EDITOR_VIEW,
        REQUEST_MEDICAL_HISTORY_POPUP,
        REQUEST_MEDICAL_HISTORY,
        REQUEST_MEDICATION,
        REQUEST_NULL_PATIENT,
        REQUEST_PATIENT,
        REQUEST_PATIENT_DELETE,
        REQUEST_PATIENT_RECOVER,
        REQUEST_PHONE_EMAIL_EDITOR_VIEW,
        REQUEST_RECALL_EDITOR_VIEW,
        REQUEST_SCHEDULE_VIEW_CONTROLLER,
        REQUEST_UNTITLED_NAME,
        REQUEST_UPDATE_RECOVER_PATIENT 
    }
    private enum BorderTitles { APPOINTMENT_HISTORY,
                                ACTIONS,
                                PATIENT_ADDRESS,
                                PATIENT_DETAILS,
                                PATIENT_EXTRA_DETAILS,
                                PATIENT_SELECTION}
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
    
    private void setBorderTitles(BorderTitles borderTitles){
        JPanel panel = null;
        String caption = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case ACTIONS:
                panel = pnlOperations;
                caption = panelPatientActionsCaption;
                isPanelBackgroundDefault = false;
                break;
            case APPOINTMENT_HISTORY:
                panel = pnlAppointmentHistory;
                caption = panelPatientAppointmentHistoryCaption;
                isPanelBackgroundDefault = false;
                break;
            case PATIENT_ADDRESS:
                panel = pnlAddress;
                caption = panelPatientAddressCaption;
                isPanelBackgroundDefault = true;
                break;
            case PATIENT_DETAILS:
                panel = pnlName;
                caption = panelPatientDetailsCaption;
                isPanelBackgroundDefault = true;
                break;
            case PATIENT_EXTRA_DETAILS:
                panel = pnlFurtherDetails;
                caption = panelPatientFurtherDetailsCaption;
                isPanelBackgroundDefault = false;
                break;
            case PATIENT_SELECTION:
                panel = pnlPatientSelection;
                caption = panelPatientSelectionCaption;
                isPanelBackgroundDefault = false;
                break;        
        }
        panel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        caption, 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        if (!isPanelBackgroundDefault)
            panel.setBackground(new java.awt.Color(220, 220, 220));
    }
    
    private void setPatientSelectionMode(PatientSelectionMode value){
        patientSelectionMode = value;
        TitledBorder titledBorder = (TitledBorder)this.pnlPatientSelection.getBorder();
        switch (patientSelectionMode){
            case PATIENT_RECOVERY:
                titledBorder.setTitle(panelPatientRecoveryTitle);
                titledBorder.setTitleColor(Color.RED);
                btnCreateRecoverPatient.setText(patientRecoveryCaption);
                btnCreateRecoverPatient.setForeground(Color.RED);
                btnUpdateRecoverPatient.setEnabled(true);
                btnUpdateRecoverPatient.setText(cancelPatientRecoveryCaption);
                btnUpdateRecoverPatient.setForeground(Color.RED);
                
                break;
            case PATIENT_SELECTION:
                titledBorder.setTitle(panelPatientSelectionCaption);
                titledBorder.setTitleColor(getBorderTitleColor());
                btnCreateRecoverPatient.setText(patientCreateCaption);
                btnCreateRecoverPatient.setForeground(Color.BLACK);
                btnUpdateRecoverPatient.setText(patientUpdateCaption);
                btnUpdateRecoverPatient.setForeground(Color.BLACK);
                break;
        }
        /**
         * Note: On PATIENT_SELECTION case panel border requires a further nudge to display new title properly
         */
        this.pnlPatientSelection.repaint();
    }

    /**
     * Creates new form TestPatientView
     */
    public PatientView(View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Patient view");
        setMyViewType(myViewType);
        setMyController(myController); 
        setDesktopView(desktopView);
    }
    
    @Override
    public void initialiseView(){ 
        initComponents();
        setVisible(true);
        setClosable(false);
        setMaximizable(false);
        setIconifiable(true);
        setResizable(false);
        //setSelected(true);
        //setSize(getPatientViewWidth(),getPatientViewHeight());
        //some loose ends
        txtAddressLine2 = new javax.swing.JTextField();
        rdbGroup = new javax.swing.ButtonGroup();
        rdbGroup.add(rdbRequestPhoneEmailEditorView);
        rdbGroup.add(rdbRequestModalRecallEditorView);
        rdbGroup.add(rdbRequestModalGuardianEditorView);
        rdbGroup.add(rdbRequestModalMedicalProfilePopup);
        rdbRequestPhoneEmailEditorView.setActionCommand(Actions.REQUEST_PHONE_EMAIL_EDITOR_VIEW.toString());
        rdbRequestModalRecallEditorView.setActionCommand(Actions.REQUEST_RECALL_EDITOR_VIEW.toString());
        rdbRequestModalGuardianEditorView.setActionCommand(Actions.REQUEST_GUARDIAN_EDITOR_VIEW.toString());
        rdbRequestModalMedicalProfilePopup.setActionCommand(Actions.REQUEST_MEDICAL_HISTORY_POPUP.toString());
        rdbRequestPhoneEmailEditorView.addActionListener(this);
        rdbRequestModalRecallEditorView.addActionListener(this);
        rdbRequestModalGuardianEditorView.addActionListener(this);
        rdbRequestModalMedicalProfilePopup.addActionListener(this);
        
        cmbPatientSelector.setActionCommand(Actions.REQUEST_PATIENT.toString());
        cmbPatientSelector.addActionListener(this);
        
        
        addInternalFrameListeners();

        
       
        setBorderTitles(BorderTitles.ACTIONS);
        setBorderTitles(BorderTitles.APPOINTMENT_HISTORY);
        setBorderTitles(BorderTitles.PATIENT_ADDRESS);
        setBorderTitles(BorderTitles.PATIENT_DETAILS);
        setBorderTitles(BorderTitles.PATIENT_EXTRA_DETAILS);
        setBorderTitles(BorderTitles.PATIENT_SELECTION);
        this.btnClearSelection.setText(this.patientClearSelectionCaption);
        this.btnCloseView.setText(this.patientCloseViewCaption);
        this.btnCreateRecoverPatient.setText(this.patientCreateCaption);
        this.btnFetchClinicalNotes.setText(this.patientClinicalNotesCaption);
        this.btnFetchScheduleForSelectedAppointment.setText(this.patientFetchScheduleCaption);
        this.btnUpdateRecoverPatient.setText(this.patientUpdateCaption);
        btnClearSelection.setActionCommand(Actions.REQUEST_NULL_PATIENT.toString());
        btnCloseView.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        btnCreateRecoverPatient.setActionCommand(Actions.REQUEST_CREATE_RECOVER_PATIENT.toString());
        btnFetchClinicalNotes.setActionCommand(Actions.REQUEST_CLINICAL_NOTES.toString());
        btnFetchScheduleForSelectedAppointment.setActionCommand(Actions.REQUEST_SCHEDULE_VIEW_CONTROLLER.toString());
        btnUpdateRecoverPatient.setActionCommand(Actions.REQUEST_UPDATE_RECOVER_PATIENT.toString());
        btnClearSelection.addActionListener(this);
        btnCloseView.addActionListener(this);
        btnCreateRecoverPatient.addActionListener(this);
        btnFetchClinicalNotes.addActionListener(this);
        btnFetchScheduleForSelectedAppointment.addActionListener(this);
        btnUpdateRecoverPatient.addActionListener(this);
        
        this.mniCloseView.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        this.mniCreateNewPatient.setActionCommand(Actions.REQUEST_CREATE_RECOVER_PATIENT.toString());
        this.mniDeleteSelectedPatient.setActionCommand(Actions.REQUEST_PATIENT_DELETE.toString());
        this.mniUpdateSelectedPatient.setActionCommand(Actions.REQUEST_UPDATE_RECOVER_PATIENT.toString());
        this.mniCloseView.addActionListener(this);
        this.mniCreateNewPatient.addActionListener(this);
        this.mniDeleteSelectedPatient.addActionListener(this);
        this.mniUpdateSelectedPatient.addActionListener(this);
        
        

        cmbNameTitle.addItemListener(itemListener);
        cmbNameTitle.setEditable(true);
        cmbNameTitle.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.TitleItem.values()));
        cmbNameTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbNameGender.addItemListener(itemListener);
        cmbNameGender.setEditable(true);
        cmbNameGender.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.GenderItem.values()));
        cmbNameGender.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        
        setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
        populatePatientSelector(cmbPatientSelector);
        
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
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
                lblNameAge.setText("<" + String.valueOf(getAge(date)) + " yrs)");
            }         
        }
    }
    
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
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        switch(Actions.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                doCloseViewRequest();
                break;
            case REQUEST_CREATE_RECOVER_PATIENT:
                doCreateRecoverPatientRequest();
                break;
            case REQUEST_CLINICAL_NOTES:
                doClinicalNotesRequest();
                break;
            case REQUEST_DOCTOR:
                doDoctorRequest();
                break;
            case REQUEST_GUARDIAN_EDITOR_VIEW:
                doGuardianEditorViewRequest();
                break;
            case REQUEST_MEDICAL_HISTORY:
                doMedicalHistoryRequest();
                break;
            case REQUEST_MEDICAL_HISTORY_POPUP:
                if (this.cmbPatientSelector.getSelectedIndex()!=-1){
                    doMedicalHistoryPopupRequest();
                }else{
                    JOptionPane.showInternalMessageDialog(this, 
                            "a patient has to be selected before a medical history "
                                    + "can be displayed","Patient view error", 
                                    JOptionPane.WARNING_MESSAGE);
                    rdbGroup.clearSelection();
                }
                break;
            case REQUEST_MEDICATION:
                doMedicationRequest();
                break;
            case REQUEST_NULL_PATIENT:
                doNullPatientRequest();
                break;
            case REQUEST_PATIENT:
                doPatientRequest();
                break;
            case REQUEST_PATIENT_DELETE:
                doPatientDeleteRequest();
                break;
            case REQUEST_PATIENT_RECOVER:
                doPatientRecoverRequest();
                break;
            case REQUEST_PHONE_EMAIL_EDITOR_VIEW:
                doPhoneEmailEditorViewRequest();
                break;
            case REQUEST_RECALL_EDITOR_VIEW:
                doRecallEditorViewRequest();
                break;
            case REQUEST_SCHEDULE_VIEW_CONTROLLER:
                doScheduleViewControllerRequest();
                break;
            case REQUEST_UNTITLED_NAME:
                doUntitledNameRequest();
                break;
            case REQUEST_UPDATE_RECOVER_PATIENT:
                doUpdateRecoverPatientRequest();
                break;     
        }
    }
    
    private void doCloseViewRequest(){
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
    private void doCreateRecoverPatientRequest(){
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
    private void doClinicalNotesRequest(){
        
    }
    private void doDoctorRequest(){
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent.
                PATIENT_DOCTOR_EDITOR_VIEW_REQUEST;
        doActionFor(request);
    }
    private void doGuardianEditorViewRequest(){
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent.
                PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST;
        doActionFor(request);
    }
    private void doMedicalHistoryRequest(){
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent.
                PATIENT_MEDICAL_HISTORY_1_EDITOR_VIEW_REQUEST;
        doActionFor(request);
    }
    private void doMedicalHistoryPopupRequest(){
        JMenuItem menuItem = null;
        JPopupMenu popup = new JPopupMenu("Select option");
        JLabel title = new JLabel("       Select options");
        popup.add(title);    
        popup.addSeparator();
        
        popup.setVisible(true);
        Patient patient = (Patient)cmbPatientSelector.getSelectedItem();
        
        ArrayList<String> items = 
                TemplateReader.extract(patient.getMedicalHistory());
        Iterator itemsIterator = items.iterator();
        while(itemsIterator.hasNext()){
            String item = (String)itemsIterator.next();
            switch(item){
                case "History":
                    menuItem = popup.add(item);
                    menuItem.setActionCommand(
                            Actions.REQUEST_MEDICAL_HISTORY.toString());
                    menuItem.addActionListener(this);
                            /*
                    popup.add(item).addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            mniMedicalHistoryActionPerformed(evt);
                        }
                    });*/
                    break;
                case "Medication":
                    menuItem = popup.add(item);
                    menuItem.setActionCommand(
                            Actions.REQUEST_MEDICATION.toString());
                    menuItem.addActionListener(this);
                    /*
                    popup.add(item).addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            mniMedicationActionPerformed(evt);
                        }
                    });*/
                    break;
                case "Doctor":
                    menuItem = popup.add(item);
                    menuItem.setActionCommand(
                            Actions.REQUEST_DOCTOR.toString());
                    menuItem.addActionListener(this);
                    /*
                    popup.add(item).addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            mniDoctorActionPerformed(evt);
                        }
                    });*/
                    break;        
            }
        }
        popup.show(this.rdbRequestModalMedicalProfilePopup, 
                rdbRequestModalMedicalProfilePopup.getX()-50,
                rdbRequestModalMedicalProfilePopup.getY()-50 );
    }
    private void doMedicationRequest(){
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent.
                PATIENT_MEDICATION_EDITOR_VIEW_REQUEST;
        doActionFor(request);
    }
    private void doNullPatientRequest(){
        getMyController().getDescriptor().
                getViewDescription().setPatient(new Patient());
        this.cmbPatientSelector.setSelectedIndex(-1);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent
                    .NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    private void doPatientDeleteRequest(){
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
    }
    private void doPatientRecoverRequest(){
        setPatientSelectionMode(PatientSelectionMode.PATIENT_RECOVERY);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.PATIENT_RECOVER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        mniRecoverDeletedPatient.setEnabled(false);
        mniCreateNewPatient.setEnabled(false);
        this.btnUpdateRecoverPatient.setEnabled(true);
    }
    private void doPatientRequest(){
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
    }
    private void doPhoneEmailEditorViewRequest(){
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent.
                PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST;
        doActionFor(request);
    }
    private void doRecallEditorViewRequest(){
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent.
                PATIENT_RECALL_EDITOR_VIEW_REQUEST;
        doActionFor(request);
    }
    private void doScheduleViewControllerRequest(){
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
    }
    private void doUntitledNameRequest(){
        if (this.cmbNameTitle.getSelectedItem() != null){
            if (this.cmbNameTitle.getSelectedItem().equals(TitleItem.Untitled)){
                this.cmbNameTitle.setSelectedIndex(-1);
            }
        }
    }
    private void doUpdateRecoverPatientRequest(){
        switch (getPatientSelectionMode()){
            case PATIENT_SELECTION:
                updateSelectedPatientActionPerformed();
                break;
            case PATIENT_RECOVERY:
                setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                doNullPatientRequest();
                break;
        }
    }
    
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
    
    private void doActionFor(
            ViewController.PatientViewControllerActionEvent request){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            request.toString());
        this.getMyController().actionPerformed(actionEvent);
        actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.
                    VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
        rdbGroup.clearSelection();
    }
    
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
    
    private boolean validateMinimumPatientDetails(){
        boolean errorOnExit = false;
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
        return !errorOnExit;
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
            JOptionPane.showMessageDialog(this, "Following Exception raised in PatientView::populateAppointmentsHistoryTable()\n"
                    + ex.getMessage());
        }
        
    }
    
    private void initialiseFromControllerViewMode(){
        switch(getMyController().getDescriptor()
                .getControllerDescription().getViewMode()){
            case CREATE:
                this.btnCreateRecoverPatient.setEnabled(true);
                this.btnUpdateRecoverPatient.setEnabled(false);
                break;
            case UPDATE:
                this.btnCreateRecoverPatient.setEnabled(false);
                this.btnUpdateRecoverPatient.setEnabled(true);
                break;
        }
    }
    
    ItemListener itemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent e){
            setViewStatus(true);
        }
    };
    
    private void addInternalFrameListeners(){
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
        //setNotes(patient.getNotes());
        setDOB(patient.getDOB());
        populateAppointmentsHistoryTable(patient);
    }
    
    private Patient initialisePatientFromView(Patient patient){
            patient.getAddress().setCounty(getCounty());
  
            patient.setDOB(getDOB());
            patient.getName().setForenames(getForenames());
            patient.setGender(getGender());

            patient.getAddress().setLine1(getLine1());
            patient.getAddress().setLine2(getLine2());
            
            //patient.setNotes(getNotes());

            patient.getAddress().setPostcode(getPostcode());
            patient.getName().setSurname(getSurname());
            patient.getName().setTitle(getPatientTitle());
            patient.getAddress().setTown(getTown());

        return patient;
    }
    
    private void setViewTitle(Patient patient){
        this.setTitle (patient.toString()
                + " [phone: " + patient.getPhone1()
                + " email: " + patient.getEmail() +"]");
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
    
    private int getAge(LocalDate dob){
        return Period.between(dob, LocalDate.now()).getYears();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlPatientSelection = new javax.swing.JPanel();
        cmbPatientSelector = new javax.swing.JComboBox<>();
        btnClearSelection = new javax.swing.JButton();
        pnlName = new javax.swing.JPanel();
        pnlNameContent = new javax.swing.JPanel();
        txtNameForename = new javax.swing.JTextField();
        lblNameForename = new javax.swing.JLabel();
        txtNameSurname = new javax.swing.JTextField();
        lblNameSurname = new javax.swing.JLabel();
        lblNameGender = new javax.swing.JLabel();
        cmbNameGender = new javax.swing.JComboBox<>();
        lblNameTitle = new javax.swing.JLabel();
        cmbNameTitle = new javax.swing.JComboBox<>();
        lblNameDOB = new javax.swing.JLabel();
        dobDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dobDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dobDatePicker.setSettings(settings);
        dobDatePicker.addDateChangeListener(new DOBDatePickerDateChangeListener());
        ;
        lblNameAge = new javax.swing.JLabel();
        pnlOperations = new javax.swing.JPanel();
        btnUpdateRecoverPatient = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnCreateRecoverPatient = new javax.swing.JButton();
        btnFetchScheduleForSelectedAppointment = new javax.swing.JButton();
        btnFetchClinicalNotes = new javax.swing.JButton();
        pnlAddress = new javax.swing.JPanel();
        pnlPatientAddressContent = new javax.swing.JPanel();
        txtAddressLine1 = new javax.swing.JTextField();
        lblAddressLine1 = new javax.swing.JLabel();
        txtNameLine2 = new javax.swing.JTextField();
        lblAddressLine2 = new javax.swing.JLabel();
        lblAddressCounty = new javax.swing.JLabel();
        lblAddressTown = new javax.swing.JLabel();
        lblAddressPostcode = new javax.swing.JLabel();
        txtAddressTown = new javax.swing.JTextField();
        txtAddressCounty = new javax.swing.JTextField();
        txtAddressPostcode = new javax.swing.JTextField();
        pnlAppointmentHistory = new javax.swing.JPanel();
        scrAppointmentHistory = new javax.swing.JScrollPane();
        tblAppointmentHistory = new javax.swing.JTable();
        pnlFurtherDetails = new javax.swing.JPanel();
        rdbRequestPhoneEmailEditorView = new javax.swing.JRadioButton();
        rdbRequestModalRecallEditorView = new javax.swing.JRadioButton();
        rdbRequestModalGuardianEditorView = new javax.swing.JRadioButton();
        rdbRequestModalMedicalProfilePopup = new javax.swing.JRadioButton();
        mbaPatientView = new javax.swing.JMenuBar();
        mnuActions = new javax.swing.JMenu();
        mniCreateNewPatient = new javax.swing.JMenuItem();
        mniUpdateSelectedPatient = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniDeleteSelectedPatient = new javax.swing.JMenuItem();
        mniRecoverDeletedPatient = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlPatientSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Select patient"));

        /*
        cmbPatientSelector.setModel(new javax.swing.DefaultComboBoxModel<Patient>());
        */

        btnClearSelection.setText("Clear selection");

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
                .addContainerGap())
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

        pnlName.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));

        lblNameForename.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNameForename.setText("Forenames");

        lblNameSurname.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNameSurname.setText("Surname");

        lblNameGender.setText("Gender");

        cmbNameGender.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.GenderItem.values()));

        lblNameTitle.setText("Title");

        cmbNameTitle.setModel(new javax.swing.DefaultComboBoxModel<>(PatientView.TitleItem.values()));
        cmbNameTitle.setMinimumSize(new java.awt.Dimension(132, 26));
        cmbNameTitle.setPreferredSize(new java.awt.Dimension(132, 26));

        lblNameDOB.setText("DOB");

        lblNameAge.setText("(age)");

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

        pnlOperations.setBorder(javax.swing.BorderFactory.createTitledBorder("Operations"));
        pnlOperations.setPreferredSize(new java.awt.Dimension(142, 500));

        btnUpdateRecoverPatient.setText("<html><center>Update</center><center>selected</center><center>patient</center></html>");
        btnUpdateRecoverPatient.setMaximumSize(new java.awt.Dimension(2147483647, 82));
        btnUpdateRecoverPatient.setMinimumSize(new java.awt.Dimension(99, 82));
        btnUpdateRecoverPatient.setPreferredSize(new java.awt.Dimension(99, 82));

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");
        btnCloseView.setMaximumSize(new java.awt.Dimension(2147483647, 82));
        btnCloseView.setMinimumSize(new java.awt.Dimension(99, 82));
        btnCloseView.setPreferredSize(new java.awt.Dimension(99, 82));

        btnCreateRecoverPatient.setText("<html><center>Create</center><center>new</center><center>patient</center></html>");
        btnCreateRecoverPatient.setMaximumSize(new java.awt.Dimension(2147483647, 82));
        btnCreateRecoverPatient.setMinimumSize(new java.awt.Dimension(99, 82));
        btnCreateRecoverPatient.setPreferredSize(new java.awt.Dimension(99, 82));
        btnCreateRecoverPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateRecoverPatientActionPerformed(evt);
            }
        });

        btnFetchScheduleForSelectedAppointment.setText("<html><center>Schedule</center><center>for selected</center><center>appointment</center></html>");
        btnFetchScheduleForSelectedAppointment.setMaximumSize(new java.awt.Dimension(2147483647, 82));
        btnFetchScheduleForSelectedAppointment.setMinimumSize(new java.awt.Dimension(99, 82));
        btnFetchScheduleForSelectedAppointment.setPreferredSize(new java.awt.Dimension(99, 82));
        btnFetchScheduleForSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFetchScheduleForSelectedAppointmentActionPerformed(evt);
            }
        });

        btnFetchClinicalNotes.setText("<html><center>Clinical</center><center>notes for</center><center>patient</center></html>");
        btnFetchClinicalNotes.setMaximumSize(new java.awt.Dimension(2147483647, 82));
        btnFetchClinicalNotes.setMinimumSize(new java.awt.Dimension(99, 82));
        btnFetchClinicalNotes.setPreferredSize(new java.awt.Dimension(99, 82));
        btnFetchClinicalNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFetchClinicalNotesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdateRecoverPatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFetchClinicalNotes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCreateRecoverPatient, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        pnlOperationsLayout.setVerticalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateRecoverPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnUpdateRecoverPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnFetchClinicalNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pnlAddress.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

        lblAddressLine1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAddressLine1.setText("Line 1");

        lblAddressLine2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAddressLine2.setText("Line 2");

        lblAddressCounty.setText("County");

        lblAddressTown.setText("Town");

        lblAddressPostcode.setText("Postcode");

        txtAddressTown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressTownActionPerformed(evt);
            }
        });

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
                    .addComponent(txtNameLine2)
                    .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                        .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtAddressPostcode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .addComponent(txtAddressTown, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(18, 18, 18)
                        .addComponent(lblAddressCounty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txtNameLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressLine2))
                .addGap(10, 10, 10)
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblAddressCounty)
                        .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblAddressTown)))
                .addGap(9, 9, 9)
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAddressPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddressPostcode)))
        );

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

        pnlAppointmentHistory.setBorder(javax.swing.BorderFactory.createTitledBorder("Appointment history"));

        tblAppointmentHistory.setModel(new Appointments3ColumnTableModel());
        scrAppointmentHistory.setViewportView(tblAppointmentHistory);
        ViewController.setJTableColumnProperties(tblAppointmentHistory,
            scrAppointmentHistory.getPreferredSize().width,
            20,20,60);
        tblAppointmentHistory.addMouseListener(mouseListener);

        javax.swing.GroupLayout pnlAppointmentHistoryLayout = new javax.swing.GroupLayout(pnlAppointmentHistory);
        pnlAppointmentHistory.setLayout(pnlAppointmentHistoryLayout);
        pnlAppointmentHistoryLayout.setHorizontalGroup(
            pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentHistory)
                .addGap(10, 10, 10))
        );
        pnlAppointmentHistoryLayout.setVerticalGroup(
            pnlAppointmentHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );

        pnlFurtherDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Further details"));

        rdbRequestPhoneEmailEditorView.setText("Phone/email");
        rdbRequestPhoneEmailEditorView.setBackground(new java.awt.Color(220, 220, 220));

        rdbRequestModalRecallEditorView.setText("Recall data");
        rdbRequestModalRecallEditorView.setBackground(new java.awt.Color(220, 220, 220));

        rdbRequestModalGuardianEditorView.setText("Guardian (if patient)");
        rdbRequestModalGuardianEditorView.setBackground(new java.awt.Color(220, 220, 220));

        rdbRequestModalMedicalProfilePopup.setText("Medical history");
        rdbRequestModalMedicalProfilePopup.setBackground(new java.awt.Color(220, 220, 220));

        javax.swing.GroupLayout pnlFurtherDetailsLayout = new javax.swing.GroupLayout(pnlFurtherDetails);
        pnlFurtherDetails.setLayout(pnlFurtherDetailsLayout);
        pnlFurtherDetailsLayout.setHorizontalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbRequestPhoneEmailEditorView)
                    .addComponent(rdbRequestModalRecallEditorView))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbRequestModalGuardianEditorView)
                    .addComponent(rdbRequestModalMedicalProfilePopup))
                .addGap(25, 25, 25))
        );
        pnlFurtherDetailsLayout.setVerticalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestPhoneEmailEditorView)
                    .addComponent(rdbRequestModalGuardianEditorView))
                .addGap(28, 28, 28)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestModalRecallEditorView)
                    .addComponent(rdbRequestModalMedicalProfilePopup))
                .addGap(27, 27, 27))
        );

        mnuActions.setText("Actions");

        mniCreateNewPatient.setText("Create new patient");
        mnuActions.add(mniCreateNewPatient);

        mniUpdateSelectedPatient.setText("Update selected patient details");
        mnuActions.add(mniUpdateSelectedPatient);
        mnuActions.add(jSeparator1);

        mniDeleteSelectedPatient.setText("Delete selected patient");
        mnuActions.add(mniDeleteSelectedPatient);

        mniRecoverDeletedPatient.setText("Recover deleted patient");
        mnuActions.add(mniRecoverDeletedPatient);
        mnuActions.add(jSeparator2);

        mniCloseView.setText("Close view");
        mnuActions.add(mniCloseView);

        mbaPatientView.add(mnuActions);

        setJMenuBar(mbaPatientView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlPatientSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlFurtherDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlPatientSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlFurtherDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/*
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
*/
    private void txtAddressTownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressTownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressTownActionPerformed

    private void btnCreateRecoverPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateRecoverPatientActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateRecoverPatientActionPerformed

    private void btnFetchClinicalNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFetchClinicalNotesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFetchClinicalNotesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateRecoverPatient;
    private javax.swing.JButton btnFetchClinicalNotes;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JButton btnUpdateRecoverPatient;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<PatientView.GenderItem> cmbNameGender;
    private javax.swing.JComboBox<PatientView.TitleItem> cmbNameTitle;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
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
    private javax.swing.JMenuBar mbaPatientView;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniCreateNewPatient;
    private javax.swing.JMenuItem mniDeleteSelectedPatient;
    private javax.swing.JMenuItem mniRecoverDeletedPatient;
    private javax.swing.JMenuItem mniUpdateSelectedPatient;
    private javax.swing.JMenu mnuActions;
    private javax.swing.JPanel pnlAddress;
    private javax.swing.JPanel pnlAppointmentHistory;
    private javax.swing.JPanel pnlFurtherDetails;
    private javax.swing.JPanel pnlName;
    private javax.swing.JPanel pnlNameContent;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlPatientAddressContent;
    private javax.swing.JPanel pnlPatientSelection;
    private javax.swing.JRadioButton rdbRequestModalGuardianEditorView;
    private javax.swing.JRadioButton rdbRequestModalMedicalProfilePopup;
    private javax.swing.JRadioButton rdbRequestModalRecallEditorView;
    private javax.swing.JRadioButton rdbRequestPhoneEmailEditorView;
    private javax.swing.JScrollPane scrAppointmentHistory;
    private javax.swing.JTable tblAppointmentHistory;
    private javax.swing.JTextField txtAddressCounty;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressPostcode;
    private javax.swing.JTextField txtAddressTown;
    private javax.swing.JTextField txtNameForename;
    private javax.swing.JTextField txtNameLine2;
    private javax.swing.JTextField txtNameSurname;
    // End of variables declaration//GEN-END:variables

    private void btnFetchScheduleForSelectedAppointmentActionPerformed(ActionEvent evt){
        
    }
}
