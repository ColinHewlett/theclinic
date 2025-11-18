/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.DesktopViewController;
import colinhewlettsolutions.client.controller.PatientViewController;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import colinhewlettsolutions.client.model.non_entity.Captions;
import colinhewlettsolutions.client.model.non_entity.Credential;
import colinhewlettsolutions.client.view.views.dialog_views.NativeFileChooser;
import colinhewlettsolutions.client.view.dialogs.CreateInternalDialog;
import colinhewlettsolutions.client.view.support_classes.models.PatientAppointmentHistoryTableModel;
import colinhewlettsolutions.client.view.support_classes.renderers.PatientAppointmentHistoryTableInvoiceRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.AppointmentHistoryTableLocalDateTimeRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.AppointmentHistoryTableDurationRenderer;
import colinhewlettsolutions.client.controller.Descriptor;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.model.entity.Invoice;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.model.entity.Entity;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.io.File;
import java.io.IOException;

import colinhewlettsolutions.client.view.support_classes.renderers.ScheduleTableCellRenderer;
import colinhewlettsolutions.client.view.views.dialog_views.DialogView;
import com.github.lgooddatepicker.components.DatePicker;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
/*28/03/2024import model.PatientNote;*/
/*28/03/2024import view.views.view_support_classes.renderers.AppointmentsTablePatientNoteRenderer;*/
/**
 *
 * @author colin
 */
public class PatientView extends View 
        implements ActionListener, 
                   ListSelectionListener,
                   PropertyChangeListener{
    //private JTextField txtAddressLine2 = null;
    private javax.swing.ButtonGroup rdbGroup = null;
    //private JTable tblAppointmentHistory = null;
    
    private final String cancelPatientRecoveryCaption = "<html><center>Cancel</center><center>patient</center><center>recovery</center>";
    
    private final String panelPatientAppointmentHistoryCaption = "Appointment history";
    private final String panelPatientActionsCaption = "Actions";
    private final String panelPatientAddressCaption = "Address";
    private final String panelPatientDetailsCaption = "Name & particulars";
    private final String panelPatientFurtherDetailsCaption = "Additional patient information";
    private final String panelPatientRecoveryTitle = "Select patient to recover";
    private final String panelPatientSelectionCaption = "Select patient";
    
    
    //private final String patientFetchScheduleCaption = "<html><center>Schedule</center><center>for selected</center><center>appointment</center></html>";
    private final String patientClearSelectionCaption = "Clear selection";
    //private final String patientCloseViewCaption = "Close view";
    //private final String patientClinicalNotesCaption = "<html><center>Clinical</center><center>notes for</center><center>patient</center></html>";
    //private final String patientCreateCaption = "<html><center>Create</center><center>new</center><center>patient</center></html>";
    //private final String patientRecoveryCaption = "<html><center>Recover patient</center></html>";
    //private final String patientUpdateCaption = "<html><center>Update</center><center>selected</center><center>patient</center></html>";
    
    
  
    private final String DISPLAY_RECALL_EDITOR_VIEW ="Recall details";
    //28/02/2024 07:45
    private final String DISPLAY_MEDICAL_PROFILE = "Medical history";
    private final String DISPLAY_GUARDIAN_EDITOR_VIEW = "Guardian (if patient)";
    private final String DISPLAY_PHONE_EMAIL_EDITOR_VIEW = "Phone/email";
    private final String DISPLAY_PATIENT_NOTES_EDITOR_VIEW = "Patient notes editor";
    
    private enum PatientSelectionMode{ PATIENT_SELECTION, 
                                       PATIENT_RECOVERY}
    private enum Actions{
        REQUEST_ADDITIONAL_NOTES,
        REQUEST_CLOSE_VIEW,
        REQUEST_CLINICAL_NOTES,
        REQUEST_CREATE_RECOVER_PATIENT,
        REQUEST_DOCTOR,
        REQUEST_DOCUMENT_STORE_POPUP,
        REQUEST_GBT_RECALL_EDITOR_VIEW,
        REQUEST_GUARDIAN_EDITOR_VIEW,
        REQUEST_MEDICAL_HISTORY_POPUP,
        REQUEST_MEDICAL_HISTORY,
        REQUEST_MEDICATION,
        REQUEST_NEW_INVOICE,
        REQUEST_NULL_PATIENT,
        REQUEST_PATIENT,
        REQUEST_PATIENT_DELETE,
        REQUEST_PATIENT_DOCUMENT,
        REQUEST_PATIENT_QUESTIONNAIRE,
        REQUEST_PATIENT_RECALLS,
        REQUEST_PATIENT_RECOVER,
        REQUEST_PATIENT_SCAN,
        REQUEST_PHONE_EMAIL_EDITOR_VIEW,
        REQUEST_PRINT_PATIENT_MEDICAL_HISTORY,
        REQUEST_RECALL_EDITOR_VIEW,
        REQUEST_SCHEDULE_VIEW_CONTROLLER,
        REQUEST_SELECT_INVOICE,
        REQUEST_UPLOAD_FILE_TO_PATIENT_DOCUMENT_STORE,
        REQUEST_UPLOAD_SCAN_TO_PATIENT_DOCUMENT_STORE,
        REQUEST_UNTITLED_NAME,
        REQUEST_UPDATE_RECOVER_PATIENT
    }
    enum FileType {DOCUMENT, SCAN}
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
    
    private void setScheduleTitledBorderSettings(){
        setBorderTitles(BorderTitles.ACTIONS);
        setBorderTitles(BorderTitles.APPOINTMENT_HISTORY);
        setBorderTitles(BorderTitles.PATIENT_ADDRESS);
        setBorderTitles(BorderTitles.PATIENT_DETAILS);
        setBorderTitles(BorderTitles.PATIENT_EXTRA_DETAILS);
        setBorderTitles(BorderTitles.PATIENT_SELECTION);
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
                isPanelBackgroundDefault = true;
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
                isPanelBackgroundDefault = true;
                break;
            case PATIENT_SELECTION:
                panel = pnlPatientSelection;
                caption = panelPatientSelectionCaption;
                isPanelBackgroundDefault = true;
                break;        
        }
        
        if (panel!=null){
            panel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createEtchedBorder(), 
                    caption, 
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                    (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                    (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));
            if (!isPanelBackgroundDefault)
                panel.setBackground(new java.awt.Color(220, 220, 220));
        }else{
            String message = "Unexpected null value for titled border panel encountered in PatientView::setBorderTitles() method";
            JOptionPane.showMessageDialog(this, message, "View error", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    private void setPatientSelectionMode(PatientSelectionMode value){
        patientSelectionMode = value;
        TitledBorder titledBorder = (TitledBorder)this.pnlPatientSelection.getBorder();
        switch (patientSelectionMode){
            case PATIENT_RECOVERY:
                titledBorder.setTitle(panelPatientRecoveryTitle);
                titledBorder.setTitleColor(Color.RED);
                btnCreateRecoverPatient.setText(Captions.PatientView.CREATE_RECOVER_PATIENT._2());
                btnCreateRecoverPatient.setForeground(Color.RED);
                btnUpdateRecoverPatient.setEnabled(true);
                btnUpdateRecoverPatient.setText(Captions.PatientView.UPDATE_RECOVER_PATIENT._2());
                btnUpdateRecoverPatient.setForeground(Color.RED);
                
                break;
            case PATIENT_SELECTION:                
                titledBorder.setTitle(panelPatientSelectionCaption);
                titledBorder.setTitleFont((java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT));
                titledBorder.setTitleColor((java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR));
                btnCreateRecoverPatient.setText(Captions.PatientView.CREATE_RECOVER_PATIENT._1());
                btnCreateRecoverPatient.setForeground(Color.BLACK);
                btnUpdateRecoverPatient.setText(Captions.PatientView.UPDATE_RECOVER_PATIENT._1());
                btnUpdateRecoverPatient.setForeground(Color.BLACK);
                break;
        }
        /**
         * Note: On PATIENT_SELECTION case panel border requires a further nudge to display new title properly
         */
        this.pnlPatientSelection.repaint();
    }

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public PatientView(View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Patient view");
        setMyViewType(myViewType);
        setMyController(myController); 
        setDesktopView(desktopView);
        initComponents();
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                this.btnFetchClinicalNotes.setEnabled(true);
                this.btnFetchScheduleForSelectedAppointment.setEnabled(true);
                int selectedRow = this.tblAppointmentHistory.getSelectedRow();
                PatientAppointmentHistoryTableModel model = 
                        (PatientAppointmentHistoryTableModel)tblAppointmentHistory.getModel();
                Appointment appointment = model.getElementAt(selectedRow);
                getMyController().getDescriptor().getViewDescription()
                        .setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
            }else{
                this.btnFetchClinicalNotes.setEnabled(false);
                this.btnFetchScheduleForSelectedAppointment.setEnabled(false);
            }
    }
    
    @Override
    public void initialiseView(){ 
        
        //initComponents();
        //System.out.println("RootPane: " + getRootPane());
        //System.out.println("UI delegate: " + getUI());
        //setVisible(true);
        setClosable(true);
        setMaximizable(false);
        setIconifiable(true);
        setResizable(false);
        setVisible(true);
        //setSize(857,600);
        //System.out.println("PatientView size: " + getSize());
        //System.out.println("PatientView preferred size: " + getPreferredSize());
        //setLocation(20, 20); // ensure it's within the visible area
        //moveToFront();
        /*toFront();
        requestFocusInWindow();
        try{
            setSelected(true);
        }catch(PropertyVetoException ex){
            
        }*/
        rdbGroup = new javax.swing.ButtonGroup();
        rdbGroup.add(rdbRequestPhoneEmailEditorView);
        rdbGroup.add(rdbRequestModalRecallEditorView);
        rdbGroup.add(rdbRequestModalGuardianEditorView);
        rdbGroup.add(rdbRequestModalMedicalProfilePopup);
        rdbGroup.add(this.rdbRequestModalGBTRecallEditorView);
        rdbGroup.add(this.rdbRequestOpenDocumentStorePopup);
        rdbRequestPhoneEmailEditorView.setActionCommand(Actions.REQUEST_PHONE_EMAIL_EDITOR_VIEW.toString());
        rdbRequestModalRecallEditorView.setActionCommand(Actions.REQUEST_RECALL_EDITOR_VIEW.toString());
        rdbRequestModalGuardianEditorView.setActionCommand(Actions.REQUEST_GUARDIAN_EDITOR_VIEW.toString());
        rdbRequestModalMedicalProfilePopup.setActionCommand(Actions.REQUEST_MEDICAL_HISTORY_POPUP.toString());
        rdbRequestOpenDocumentStorePopup.setActionCommand(Actions.REQUEST_DOCUMENT_STORE_POPUP.toString());
        rdbRequestModalGBTRecallEditorView.setActionCommand(Actions.REQUEST_GBT_RECALL_EDITOR_VIEW.toString());
        rdbRequestPhoneEmailEditorView.addActionListener(this);
        rdbRequestModalRecallEditorView.addActionListener(this);
        rdbRequestModalGuardianEditorView.addActionListener(this);
        rdbRequestModalMedicalProfilePopup.addActionListener(this);
        rdbRequestModalGBTRecallEditorView.addActionListener(this);
        rdbRequestOpenDocumentStorePopup.addActionListener(this);
        
        System.out.println("Document store x = " + rdbRequestOpenDocumentStorePopup.getX());
        System.out.println("Document store y = " + rdbRequestOpenDocumentStorePopup.getY());
        
        cmbPatientSelector.setActionCommand(Actions.REQUEST_PATIENT.toString());
        cmbPatientSelector.addActionListener(this);
        
        
        addInternalFrameListeners();
        PatientAppointmentHistoryTableModel model = 
                (PatientAppointmentHistoryTableModel)this.tblAppointmentHistory.getModel();
        //model.addTableModelListener(this);
        
        ActionEvent actionEvent = null;
        if ((Boolean)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_REQUIRED)){
            actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.USER_SYSTEM_WIDE_SETTINGS_REQUEST.toString());
            this.getMyController().getMyController().actionPerformed(actionEvent);
        }
        setScheduleTitledBorderSettings();

        
        doActionEventFor(PatientViewController.Actions.SETTINGS_TITLED_BORDER_REQUEST);
        this.btnClearSelection.setText(Captions.PatientView.CLEAR_SELECTION._1());
        btnClearSelection.setActionCommand(Actions.REQUEST_NULL_PATIENT.toString());
        btnCloseView.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        btnCreateRecoverPatient.setActionCommand(Actions.REQUEST_CREATE_RECOVER_PATIENT.toString());
        btnFetchClinicalNotes.setText(Captions.PatientView.PATIENT_CLINICAL_NOTES._1());
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
        this.mniPatientRecallsRequest.setActionCommand(Actions.REQUEST_PATIENT_RECALLS.toString());
        this.mniRecoverDeletedPatient.setActionCommand(Actions.REQUEST_PATIENT_RECOVER.toString());
        this.mniCloseView.addActionListener(this);
        this.mniCreateNewPatient.addActionListener(this);
        this.mniDeleteSelectedPatient.addActionListener(this);
        this.mniUpdateSelectedPatient.addActionListener(this);
        this.mniPatientRecallsRequest.addActionListener(this);
        this.mniRecoverDeletedPatient.addActionListener(this);
        
        

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
        
        // Add a component listener to adjust column widths after it is displayed
        this.tblAppointmentHistory.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustColumnWidthsAndViewPosition(tblAppointmentHistory);
            }
        });
        
        if (getMyController().getDescriptor().getControllerDescription().getProperty(Properties.VIEW_MODE).equals(ViewController.ViewMode.CREATE)){
            actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    PatientViewController.Actions.NULL_PATIENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }

    }
    
    private void adjustColumnWidthsAndViewPosition(JTable table){
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewController.setRelativeColumnWidths(table, new double[]{0.07,0.1,0.15,0.68});
            //ViewController.centerInternalFrame(getDesktopView().getDeskTop(), this);
        });
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
                lblNameAge.setText("(" + String.valueOf(getAge(date)) + " yrs)");
            }         
        }
    }
    
    MouseAdapter mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent me) {
            Invoice invoice = null;
            if (me.getClickCount() == 2) {     // to detect doble click events
                /*
                if (tblAppointmentHistory.getRowCount() > 0){ //ensures there are rows in the table
                    int row = tblAppointmentHistory.getSelectedRow();
                    PatientAppointmentHistoryTableModel model = 
                            (PatientAppointmentHistoryTableModel)tblAppointmentHistory.getModel();
                    Appointment appointment = (Appointment)model.getElementAt(row);
                    getMyController().getDescriptor().getViewDescription().setAppointment(appointment);
                    ActionEvent actionEvent = new ActionEvent(
                            PatientView.this,ActionEvent.ACTION_PERFORMED,
                            ViewController.PatientViewControllerActionEvent.PATIENT_INVOICE_VIEW_CONTROLLER_REQUEST.toString());
                    getMyController().actionPerformed(actionEvent);
                }*/
            }
        }
    };
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        Patient patient = null;
        initialiseFromControllerViewMode();
        
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
            switch(propertyName){
                case USER_SYSTEM_WIDE_SETTINGS_RECEIVED ->{
                    setScheduleTitledBorderSettings();
                    break;
                }
            }
        }else{
            ViewController.PatientViewControllerPropertyChangeEvent propertyName =
                    ViewController.PatientViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
            switch (propertyName){
                case CLOSE_VIEW_REQUEST_RECEIVED:
                    try{
                        /**
                        * setClosed will fire INTERNAL_FRAME_CLOSED event for the
                        * listener to send ActionEvent to the view controller
                        */
                        this.setClosed(true);
                    }
                    catch (PropertyVetoException ex){
                        //UnspecifiedError action
                    }
                    break;
                case PATIENT_EDITOR_VIEW_CLOSED:
                    rdbGroup.clearSelection();
                    /*
                    if(getMyController().getDescriptor().getControllerDescription()
                            .getViewMode().equals(ViewController.ViewMode.UPDATE)){
                        setViewTitle((Patient)getMyController().
                                getDescriptor().
                                getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT));
                    }*/
                    break;
                case PATIENT_VIEW_CONTROLLER_ERROR_RECEIVED:
                    String message = (String)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.ERROR);
                    JOptionPane.showInternalMessageDialog(this, message, "View error",JOptionPane.WARNING_MESSAGE);
                    break;
                case PATIENT_TO_SELECT_RECEIVED:
                    patient = (Patient)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.PATIENT);
                    this.cmbPatientSelector.setSelectedItem(patient);
                    break;
                case PATIENT_RECEIVED:
                    initialisePatientViewComponentFromED(); 
                    patient = (Patient)getMyController().getDescriptor().getControllerDescription()
                            .getProperty(SystemDefinition.Properties.PATIENT);
                    setViewTitle(patient);

                    this.mniCreateNewPatient.setEnabled(false);
                    this.mniRecoverDeletedPatient.setEnabled(false);
                    this.mniDeleteSelectedPatient.setEnabled(true);
                    this.mniUpdateSelectedPatient.setEnabled(true);

                    this.btnFetchClinicalNotes.setEnabled(false);
                    this.btnFetchScheduleForSelectedAppointment.setEnabled(false);

                    setViewStatus(false);

                    ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                            PatientViewController.Actions.
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

                    this.btnFetchClinicalNotes.setEnabled(false);
                    this.btnFetchScheduleForSelectedAppointment.setEnabled(false);
                    /**
                     * Update logged at 30/10/2021 08:32
                     * inherited view status (set if any changes have been made to form since its initialisation)
                     * is initialised to false
                     */
                    setViewStatus(false);

                    actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            PatientViewController.Actions.
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
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        switch(Actions.valueOf(e.getActionCommand())){
            case REQUEST_ADDITIONAL_NOTES:
                doAdditionalNotesREquest();
                break;
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);
                }catch (PropertyVetoException ex){
            
                }
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
            case REQUEST_DOCUMENT_STORE_POPUP:
                if (this.cmbPatientSelector.getSelectedIndex()!=-1){
                    doDocumentStorePopupRequest();
                }else{
                    JOptionPane.showInternalMessageDialog(this, 
                            "a patient has to be selected before document store options "
                                    + "can be displayed","Patient view error", 
                                    JOptionPane.WARNING_MESSAGE);
                }
                rdbGroup.clearSelection();
                break;
            case REQUEST_GBT_RECALL_EDITOR_VIEW:
                doGBTRecallEditorViewRequest();
                break;
            case REQUEST_GUARDIAN_EDITOR_VIEW:
                doGuardianEditorViewRequest();
                break;
            case REQUEST_MEDICAL_HISTORY:
                //getMyController().sendNoOpMessage(this);
                //doMedicalHistoryRequest();
                doPatientMedicalHistoryViewControllerRequest();
                break;
            case REQUEST_MEDICAL_HISTORY_POPUP:
                if (this.cmbPatientSelector.getSelectedIndex()!=-1){
                    doMedicalHistoryPopupRequest();
                }else{
                    JOptionPane.showInternalMessageDialog(this, 
                            "a patient has to be selected before a medical history "
                                    + "can be displayed","Patient view error", 
                                    JOptionPane.WARNING_MESSAGE);
                }
                rdbGroup.clearSelection();
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
            case REQUEST_PATIENT_DOCUMENT:
                doPatientDocumentRequest();
                rdbGroup.clearSelection();
                break;
            case REQUEST_PATIENT_QUESTIONNAIRE:
                doPatientQuestionnaireViewControllerRequest();
                //JOptionPane.showInternalMessageDialog(this, "Not yet implemented", "View status",JOptionPane.INFORMATION_MESSAGE);
                break;
            case REQUEST_PATIENT_RECALLS:
                getMyController().sendNoOpMessage(this);
                break;
            case REQUEST_PATIENT_RECOVER:
                doPatientRecoverRequest();
                break;
            case REQUEST_PATIENT_SCAN:
                doPatientScanRequest();
                rdbGroup.clearSelection();
                break;
            case REQUEST_PHONE_EMAIL_EDITOR_VIEW:
                doPhoneEmailEditorViewRequest();
                break;
            case REQUEST_PRINT_PATIENT_MEDICAL_HISTORY:
                doActionEventFor(PatientViewController.Actions
                        .PRINT_PATIENT_MEDICAL_HISTORY_REQUEST);
                String printFolder = (String)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.PRINT_FOLDER);
                doOpenDocumentForPrinting(printFolder + SystemDefinition.PATIENT_QUESTIONNAIRE_MEDICAL_HISTORY_FILENAME);
                /*doOpenDocumentForPrinting(SystemDefinition.getPMSPrintFolder() 
                        + SystemDefinition.PATIENT_QUESTIONNAIRE_MEDICAL_HISTORY_FILENAME);*/
                break;
            case REQUEST_RECALL_EDITOR_VIEW:
                doRecallEditorViewRequest();
                break;
            case REQUEST_SCHEDULE_VIEW_CONTROLLER:
                doScheduleViewControllerRequest();
                break;
            case REQUEST_UPLOAD_FILE_TO_PATIENT_DOCUMENT_STORE:
                doFileChooserDialog(ViewController.ViewMode.DOCUMENT);
                rdbGroup.clearSelection();
                break;
            case REQUEST_UPLOAD_SCAN_TO_PATIENT_DOCUMENT_STORE:
                doFileChooserDialog(ViewController.ViewMode.SCAN);
                rdbGroup.clearSelection();
                break;
            case REQUEST_UNTITLED_NAME:
                doUntitledNameRequest();
                break;
            case REQUEST_UPDATE_RECOVER_PATIENT:
                doUpdateRecoverPatientRequest();
                break;     
        }
    }
    
    private void doFileChooserDialog(ViewController.ViewMode viewMode){
        DialogView dialog = null;
        String title = null;
        File selectedFile = null;
        String[] allowedFileExtensions = null;
        ArrayList<File> document = new ArrayList<>();
        switch(viewMode){
            case DOCUMENT ->{
                title = "Select Word file to upload";
                allowedFileExtensions = new String[] {"docx"};
                selectedFile = NativeFileChooser.showOpenDialog(
                    this, title, new File(System.getProperty("user.home")), allowedFileExtensions);
                if (selectedFile!=null) document.add(selectedFile);
                break;
            }
            case SCAN ->{
                title = "Select first page of scan to upload";
                allowedFileExtensions = new String[] {"jpg", "png", "gif"};
                /*
                ArrayList<Integer> items = new ArrayList<>();
                items.add(1);
                items.add(2);
                dialog = new View().make(
                        View.Viewer.COMPOSITE_SCAN_COUNT_DIALOG, 
                        this, 
                        items,
                        "Composite scan file for patient",
                        "Select number of pages to upload").getDialogView();
                if (getDialogView().getSelectedItem()!=null){
                    int index = (Integer)getDialogView().getSelectedItem();*/
                for (int index = 1;index <=2;index++){
                    switch(index){
                        case 1 ->{
                            title = "Select first page of medical history to upload";
                            break;
                        }
                        case 2 ->{
                            title = "Select second page of medical history to upload";
                            break;
                        }
                    }
                    selectedFile = NativeFileChooser.showOpenDialog(
                        this, title, new File(System.getProperty("user.home")), allowedFileExtensions);
                    if (selectedFile!=null){
                        document.add(selectedFile);
                    }    
                }
                break;
            }
        }
        if (!document.isEmpty()){
            getMyController().getDescriptor().getViewDescription().
                    setProperty(Properties.PATIENT_DOCUMENT, document);
            getMyController().getDescriptor().getViewDescription().
                    setProperty(Properties.VIEW_MODE, viewMode);
            doActionEventFor(PatientViewController.Actions.UPLOAD_TO_PATIENT_DOCUMENT_STORE_REQUEST);
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
                                .setProperty(SystemDefinition.Properties.PATIENT, initialisePatientFromView(
                                        (Patient)cmbPatientSelector.getSelectedItem())
                                );
                        setPatientSelectionMode(PatientSelectionMode.PATIENT_SELECTION);
                        ActionEvent actionEvent = new ActionEvent(
                                this,ActionEvent.ACTION_PERFORMED,
                                PatientViewController.Actions.RECOVER_PATIENT_REQUEST.toString());
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
        if (this.tblAppointmentHistory.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected");
        }
        else{
            int row = this.tblAppointmentHistory.getSelectedRow();
            PatientAppointmentHistoryTableModel model = 
                (PatientAppointmentHistoryTableModel)tblAppointmentHistory.getModel();
            Appointment appointment = (Appointment)model.getElementAt(row);
            getMyController().getDescriptor().getViewDescription()
                    .setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    PatientViewController.Actions.CLINICAL_NOTE_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        /*
        ViewController.PatientViewControllerActionEvent request = 
                ViewController.PatientViewControllerActionEvent
                .CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST;
        doActionFor(request);*/
    }
    
    private void doDoctorRequest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_DOCTOR_EDITOR_VIEW_REQUEST;
        doActionEventFor(request);
    }
    private void doGuardianEditorViewRequest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_GUARDIAN_EDITOR_VIEW_REQUEST;
        doActionEventFor(request);
    }
    
    private void doPatientDocumentRequest(){
        getMyController().getDescriptor().getViewDescription().setProperty(Properties.VIEW_MODE, ViewController.ViewMode.DOCUMENT);
        doActionEventFor(PatientViewController.Actions.PATIENT_DOCUMENT_STORE_VIEW_REQUEST);
    }
    
    private void doPatientScanRequest(){
        getMyController().getDescriptor().getViewDescription().setProperty(Properties.VIEW_MODE, ViewController.ViewMode.SCAN);
        doActionEventFor(PatientViewController.Actions.PATIENT_DOCUMENT_STORE_VIEW_REQUEST);
    }
    
    private void doCreatePopupMenuPackages(){
        JMenuItem menuItem = null;
        JLabel title = null;
        JPopupMenu documentStorePopupMenu = new JPopupMenu("Store options");
        title = new JLabel("       Store options"); 
        documentStorePopupMenu.add(title);
        documentStorePopupMenu.addSeparator();
        
        menuItem = documentStorePopupMenu.add("Get Word document");
        menuItem.setActionCommand(
                Actions.REQUEST_PATIENT_DOCUMENT.toString());
        menuItem.addActionListener(this);
        
        menuItem = documentStorePopupMenu.add("Get medical history");
        menuItem.setActionCommand(
                Actions.REQUEST_PATIENT_SCAN.toString());
        menuItem.addActionListener(this);
        
        documentStorePopupMenu.addSeparator();
        menuItem = documentStorePopupMenu.add("Upload document to store");
        menuItem.setActionCommand(Actions.REQUEST_UPLOAD_FILE_TO_PATIENT_DOCUMENT_STORE.toString());
        menuItem.addActionListener(this);
        
        menuItem = documentStorePopupMenu.add("Upload scan to store");
        menuItem.setActionCommand(Actions.REQUEST_UPLOAD_SCAN_TO_PATIENT_DOCUMENT_STORE.toString());
        menuItem.addActionListener(this);
        
        this.rdbRequestOpenDocumentStorePopup.putClientProperty("popup",documentStorePopupMenu);
    }
    
    private void doDocumentStorePopupRequest(){
        Patient patient = (Patient)cmbPatientSelector.getSelectedItem();
        JMenuItem menuItem = null;
        JPopupMenu popup = new JPopupMenu();
        JLabel label = new JLabel("Select option for " + patient);
        label.setForeground((Color)getMyController().getDescriptor().getControllerDescription().
                getProperty(Properties.TITLED_BORDER_COLOR));
        popup.add(label);    
        popup.addSeparator();
        
        popup.setVisible(true);
        
        
        menuItem = popup.add("Get Word document from store");
        menuItem.setActionCommand(
                Actions.REQUEST_PATIENT_DOCUMENT.toString());
        menuItem.addActionListener(this);
        
        menuItem = popup.add("Get medical history from store");
        menuItem.setActionCommand(
                Actions.REQUEST_PATIENT_SCAN.toString());
        menuItem.addActionListener(this);
        
        popup.addSeparator();
        menuItem = popup.add("Upload document to store");
        menuItem.setActionCommand(Actions.REQUEST_UPLOAD_FILE_TO_PATIENT_DOCUMENT_STORE.toString());
        menuItem.addActionListener(this);
        
        menuItem = popup.add("Upload medical history to store");
        menuItem.setActionCommand(Actions.REQUEST_UPLOAD_SCAN_TO_PATIENT_DOCUMENT_STORE.toString());
        menuItem.addActionListener(this);

        /*popup.show(this.rdbRequestOpenDocumentStorePopup, 
                rdbRequestOpenDocumentStorePopup.getX(),
                rdbRequestOpenDocumentStorePopup.getY());*/
        popup.show(this.rdbRequestOpenDocumentStorePopup, 
                7,14);
        System.out.println("Document store x = " + rdbRequestOpenDocumentStorePopup.getX());
        System.out.println("Document store y = " + rdbRequestOpenDocumentStorePopup.getY());
        //doCreatePopupMenuPackages();
        //this.rdbRequestOpenDocumentStorePopup.addMouseListener(clickHandler);
    }
    /*
    MouseAdapter popupHandler = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JRadioButton source = (JRadioButton)e.getSource();
                source.setSelected(true);
                source.repaint();

                JPopupMenu menu = (JPopupMenu)source.getClientProperty("popup");
                menu.show(source, e.getX()-50, e.getY()-50);
            }
        }
    };*/

    // --- Shared mouse listener ---
    /*
    MouseAdapter clickHandler = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                JRadioButton source = (JRadioButton) e.getSource();
                source.setSelected(true);
                
                source.getModel().setArmed(true);
                source.getModel().setPressed(true);
                source.repaint();
                
                SwingUtilities.invokeLater(() ->{
                    showPopupFor(source, e.getX(), e.getY());
                });
            }
        }

        private void showPopupFor(JRadioButton source, int x, int y) {
            JPopupMenu menu = (JPopupMenu) source.getClientProperty("popup");
            if (menu == null) return;

            // Keep button visually "pressed" while popup visible
            //source.getModel().setArmed(true);
            //source.getModel().setPressed(true);
            //source.repaint();

            menu.addPopupMenuListener(new PopupMenuListener() {
                @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
                @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    source.getModel().setArmed(false);
                    source.getModel().setPressed(false);
                    source.repaint();
                }
                @Override public void popupMenuCanceled(PopupMenuEvent e) {}
            });

            menu.show(source, x, y);
        }
    };*/
    
    private void doMedicalHistoryPopupRequest(){
        Patient patient = (Patient)cmbPatientSelector.getSelectedItem();
        JMenuItem menuItem = null;
        JPopupMenu popup = new JPopupMenu();
        JLabel label = new JLabel("Select medical history option for " + patient);
        label.setForeground((Color)getMyController().getDescriptor().getControllerDescription().
                getProperty(Properties.TITLED_BORDER_COLOR));
        popup.add(label);    
        popup.addSeparator();
        
        popup.setVisible(true);
        
        
        /*ArrayList<String> items = 
                TemplateReader.extract(patient.getMedicalHistory());*/
        
        menuItem = popup.add("Doctor");
        menuItem.setActionCommand(
                Actions.REQUEST_DOCTOR.toString());
        menuItem.addActionListener(this);
        
        menuItem = popup.add("Medical history");
        menuItem.setActionCommand(
                Actions.REQUEST_MEDICAL_HISTORY.toString());
        menuItem.addActionListener(this);
        
        menuItem = popup.add("Medication");
        menuItem.setActionCommand(
                Actions.REQUEST_MEDICATION.toString());
        menuItem.addActionListener(this);
        
        menuItem = popup.add("Questionnaire");
        menuItem.setActionCommand(
                Actions.REQUEST_PATIENT_QUESTIONNAIRE.toString());
        menuItem.addActionListener(this);
        
        popup.addSeparator();
        menuItem = popup.add("Print questionnaire & medical history");
        menuItem.setActionCommand(Actions.REQUEST_PRINT_PATIENT_MEDICAL_HISTORY.toString());
        menuItem.addActionListener(this);
        /*popup.show(this.rdbRequestModalMedicalProfilePopup, 
                rdbRequestModalMedicalProfilePopup.getX(),
                rdbRequestModalMedicalProfilePopup.getY());*/
        popup.show(this.rdbRequestModalMedicalProfilePopup, 
                7,14);
    }
    private void doMedicationRequest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_MEDICATION_EDITOR_VIEW_REQUEST;
        doActionEventFor(request);
    }
    private void doNullPatientRequest(){
        getMyController().getDescriptor().
                getViewDescription().setProperty(SystemDefinition.Properties.PATIENT, new Patient());
        this.cmbPatientSelector.setSelectedIndex(-1);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            PatientViewController.Actions
                    .NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    private void doPatientDeleteRequest(){
        int response;
        Patient patient = (Patient)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT,patient);
        if (patient.getIsKeyDefined()){
            String message ="Are you sure you want to delete patient " + patient.toString() + "'s details?";
            response = JOptionPane.showConfirmDialog(this,message, "Action selected patient notifications", JOptionPane.YES_NO_OPTION);
            if (response==JOptionPane.YES_OPTION){
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    PatientViewController.Actions.PATIENT_DELETE_REQUEST.toString());
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
            PatientViewController.Actions.PATIENT_RECOVER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        mniRecoverDeletedPatient.setEnabled(false);
        mniCreateNewPatient.setEnabled(false);
        this.btnUpdateRecoverPatient.setEnabled(true);
    }
    private void doPatientRequest(){
        PatientViewController.Actions event = null; 
        switch(getPatientSelectionMode()){
            case PATIENT_SELECTION:
                event = PatientViewController.Actions.PATIENT_REQUEST;
                break;
            case PATIENT_RECOVERY:
                event = PatientViewController.Actions.DELETED_PATIENT_REQUEST;
                break;
        }
        if (cmbPatientSelector.getSelectedIndex()!=-1){
            getMyController().getDescriptor().getViewDescription().
                    setProperty(SystemDefinition.Properties.PATIENT, (Patient)cmbPatientSelector.getSelectedItem());
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED, 
                    event.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    private void doAdditionalNotesREquest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_ADDITIONAL_NOTES_VIEW_REQUEST;
        doActionEventFor(request);
    }
    private void doPhoneEmailEditorViewRequest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_PHONE_EMAIL_EDITOR_VIEW_REQUEST;
        doActionEventFor(request);
    }
    private void doGBTRecallEditorViewRequest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_GBT_RECALL_EDITOR_VIEW_REQUEST;
        doActionEventFor(request);
    }
    private void doRecallEditorViewRequest(){
        PatientViewController.Actions request = 
                PatientViewController.Actions.
                PATIENT_RECALL_EDITOR_VIEW_REQUEST;
        doActionEventFor(request);
    }
    private void doPatientQuestionnaireViewControllerRequest(){
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                PatientViewController.Actions.PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    private void doPatientMedicalHistoryViewControllerRequest(){
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                PatientViewController.Actions.PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    private void doPrintPatientMedicalHistory(){
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                PatientViewController.Actions.PRINT_PATIENT_MEDICAL_HISTORY_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    private void doScheduleViewControllerRequest(){
        if (this.tblAppointmentHistory.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected");
        }
        else{
            int row = this.tblAppointmentHistory.getSelectedRow();
            LocalDate day = ((LocalDateTime)this.tblAppointmentHistory.getValueAt(row,1)).toLocalDate();
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, day);
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    PatientViewController.Actions.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString());
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
        
        if (((Patient)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PATIENT)).getIsKeyDefined()){
            
            if (this.validateMinimumPatientDetails()){
                patient = (Patient)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT);
                getMyController().getDescriptor().getViewDescription().
                        setProperty(SystemDefinition.Properties.PATIENT, initialisePatientFromView(patient));
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.PATIENT_UPDATE_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Update operation cannot proceed unless an existing patient is currently selected");
    }
    
    private void doActionEventFor(PatientViewController.Actions request){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            request.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    /*
    private void doActionForx(
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
    }*/
    
    private void createNewPatientActionPerformed(){
        
        if (!((Patient)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PATIENT)).getIsKeyDefined()){
            if (this.validateMinimumPatientDetails()){
                getMyController().getDescriptor().getViewDescription()
                        .setProperty(SystemDefinition.Properties.PATIENT, initialisePatientFromView(
                                (Patient)getMyController().getDescriptor()
                                        .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT)));
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.PATIENT_CREATE_REQUEST.toString());
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
                (ArrayList<Patient>)getMyController().getDescriptor().getControllerDescription().
                        getProperty(Properties.PATIENTS);        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        selector.setModel(model);
        selector.setSelectedIndex(-1);
    }
    
    private void populateAppointmentsHistoryTable(Patient patient){
        int appointmentHistoryCount = 0;
        ArrayList<Appointment> appointments = (ArrayList<Appointment>)getMyController().getDescriptor()
                .getControllerDescription().getProperty(Properties.APPOINTMENTS);
        /**
         * PATIENT VIEW TEST 24/04/2024 09:49
         * -- just in case ensure null pointer exception generated
         */
        if (appointments == null) appointments = new ArrayList<>();
        
        //tblAppointmentHistory = new JTable(new PatientAppointmentHistoryTableModel());
        
        PatientAppointmentHistoryTableModel tableModel = 
                (PatientAppointmentHistoryTableModel)tblAppointmentHistory.getModel(); 
        tableModel.removeAllElements();
        
        try{
            if (patient.getIsKeyDefined()){//if patient data in view has just been cleared  
                patient.setScope(Entity.Scope.FOR_PATIENT);
                appointmentHistoryCount = appointments.size();
                Iterator<Appointment> it = appointments.iterator();
                while (it.hasNext()){   
                    Appointment appointment = it.next();
                    if (appointment.getIsEmergency())
                        appointment.setNotes(SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT.mark());
                    tableModel.addElement(appointment);
                }
            }
            this.tblAppointmentHistory.setDefaultRenderer(Invoice.class, new PatientAppointmentHistoryTableInvoiceRenderer());
            this.tblAppointmentHistory.setDefaultRenderer(Duration.class, new AppointmentHistoryTableDurationRenderer());
            this.tblAppointmentHistory.setDefaultRenderer(LocalDateTime.class, new AppointmentHistoryTableLocalDateTimeRenderer());
            
            TableColumnModel columnModel = this.tblAppointmentHistory.getColumnModel();
            columnModel.getColumn(3).setCellRenderer(new ScheduleTableCellRenderer());
            
            //this.tblAppointmentHistory.setPreferredScrollableViewportSize(tblAppointmentHistory.getPreferredSize());
            TitledBorder titledBorder =
                    (TitledBorder)this.pnlAppointmentHistory.getBorder();
            
            if (appointmentHistoryCount < 3)
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
        /*switch(getMyController().getDescriptor()
                .getControllerDescription().getViewMode()){*/
        switch(getMyController().getDescriptor()
                .getControllerDescription().getProperty(Properties.VIEW_MODE)){
            case ViewController.ViewMode.CREATE:
                this.btnCreateRecoverPatient.setEnabled(true);
                this.btnUpdateRecoverPatient.setEnabled(false);
                break;
            case ViewController.ViewMode.UPDATE:
                this.btnCreateRecoverPatient.setEnabled(false);
                this.btnUpdateRecoverPatient.setEnabled(true);
                break;
            default:
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
                /**
                 * NO ACTION REQUIRED; UPDATED CODE AS FOLLOWS
                 * -- on close view request the VIEW_CLOSE_NOTIFICATION is sent to the controller immediately
                 * -- the controller will send the view.setClosed(true) message to close the view before closing the view controller
                 */
                
                ActionEvent actionEvent = new ActionEvent(
                        PatientView.this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientView.this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initialisePatientViewComponentFromED(){  
        Patient patient = (Patient)getMyController().getDescriptor().
                getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        if (patient!=null){
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
            javax.swing.SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run(){
                    populateAppointmentsHistoryTable(patient);
                }
            });
        }else{
            String message = "Unexpected occurrence: selected patient is null";
            JOptionPane.showInternalMessageDialog(this, message, "View error", JOptionPane.WARNING_MESSAGE);
        }
        
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
        if(patient.getEmail()!=null){
            if(patient.getEmail().trim().length()>0){
                this.setTitle (patient.toString()
                        + " [phone: " + patient.getPhone1()
                        + " email: " + patient.getEmail() +"]");
            }else
                this.setTitle (patient.toString()
                        + " [phone: " + patient.getPhone1()
                        + " email: undefined]");
        }else
            this.setTitle (patient.toString()
                        + " [phone: " + patient.getPhone1()
                        + " email: undefined]");
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
    
    /*
    private void doOpenDocumentForPrinting(String filepath){
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
    }*/

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
        txtAddressLine2 = new javax.swing.JTextField();
        lblAddressLine2 = new javax.swing.JLabel();
        lblAddressCounty = new javax.swing.JLabel();
        lblAddressPostcode = new javax.swing.JLabel();
        txtAddressTown = new javax.swing.JTextField();
        txtAddressCounty = new javax.swing.JTextField();
        txtAddressPostcode = new javax.swing.JTextField();
        lblAddressTown = new javax.swing.JLabel();
        pnlAppointmentHistory = new javax.swing.JPanel();
        scrAppointmentHistory = new javax.swing.JScrollPane();
        tblAppointmentHistory = new javax.swing.JTable();
        pnlFurtherDetails = new javax.swing.JPanel();
        rdbRequestPhoneEmailEditorView = new javax.swing.JRadioButton();
        rdbRequestModalRecallEditorView = new javax.swing.JRadioButton();
        rdbRequestModalGuardianEditorView = new javax.swing.JRadioButton();
        rdbRequestModalMedicalProfilePopup = new javax.swing.JRadioButton();
        rdbRequestModalGBTRecallEditorView = new javax.swing.JRadioButton();
        rdbRequestOpenDocumentStorePopup = new javax.swing.JRadioButton();
        mbaPatientView = new javax.swing.JMenuBar();
        mnuActions = new javax.swing.JMenu();
        mniCreateNewPatient = new javax.swing.JMenuItem();
        mniUpdateSelectedPatient = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniPatientRecallsRequest = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
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
                        .addGap(0, 0, Short.MAX_VALUE)))
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

        btnFetchScheduleForSelectedAppointment.setText(Captions.PatientView.SCHEDULE_FOR_APPOINTMENT._1());
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
                .addGap(10, 10, 10)
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
                .addGap(17, 17, 17)
                .addComponent(btnUpdateRecoverPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(btnFetchClinicalNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(btnFetchScheduleForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pnlAddress.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

        lblAddressLine1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAddressLine1.setText("Line 1");

        lblAddressLine2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAddressLine2.setText("Line 2");

        lblAddressCounty.setText("County");

        lblAddressPostcode.setText("Postcode");

        txtAddressTown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressTownActionPerformed(evt);
            }
        });

        lblAddressTown.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAddressTown.setText("Town");

        javax.swing.GroupLayout pnlPatientAddressContentLayout = new javax.swing.GroupLayout(pnlPatientAddressContent);
        pnlPatientAddressContent.setLayout(pnlPatientAddressContentLayout);
        pnlPatientAddressContentLayout.setHorizontalGroup(
            pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAddressLine1)
                    .addComponent(lblAddressLine2)
                    .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblAddressPostcode, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblAddressTown)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientAddressContentLayout.createSequentialGroup()
                        .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtAddressPostcode, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .addComponent(txtAddressTown))
                        .addGap(18, 18, 18)
                        .addComponent(lblAddressCounty)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtAddressLine2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                        .addComponent(txtAddressLine1, javax.swing.GroupLayout.Alignment.TRAILING))))
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
                .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAddressTown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblAddressTown))
                    .addGroup(pnlPatientAddressContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblAddressCounty)
                        .addComponent(txtAddressCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGap(19, 19, 19))
        );

        pnlAppointmentHistory.setBorder(javax.swing.BorderFactory.createTitledBorder("Appointment history"));

        tblAppointmentHistory.setModel(new colinhewlettsolutions.client.view.support_classes.models.PatientAppointmentHistoryTableModel());
        scrAppointmentHistory.setViewportView(tblAppointmentHistory);
        tblAppointmentHistory.addMouseListener(mouseListener);
        ViewController.setJTableColumnProperties(tblAppointmentHistory,
            scrAppointmentHistory.getPreferredSize().width,
            7,10,10,73);
        ListSelectionModel lsm = tblAppointmentHistory.getSelectionModel();
        lsm.addListSelectionListener(this);

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
                .addComponent(scrAppointmentHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFurtherDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Further details"));

        rdbRequestPhoneEmailEditorView.setText("Phone/email");

        rdbRequestModalRecallEditorView.setText("Recall data");

        rdbRequestModalGuardianEditorView.setText("Guardian (if patient)");

        rdbRequestModalMedicalProfilePopup.setText("Medical history");

        rdbRequestModalGBTRecallEditorView.setText("GBT recall data");

        rdbRequestOpenDocumentStorePopup.setText("Document store");

        javax.swing.GroupLayout pnlFurtherDetailsLayout = new javax.swing.GroupLayout(pnlFurtherDetails);
        pnlFurtherDetails.setLayout(pnlFurtherDetailsLayout);
        pnlFurtherDetailsLayout.setHorizontalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbRequestPhoneEmailEditorView)
                    .addComponent(rdbRequestModalRecallEditorView)
                    .addComponent(rdbRequestOpenDocumentStorePopup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbRequestModalGuardianEditorView)
                    .addComponent(rdbRequestModalMedicalProfilePopup)
                    .addComponent(rdbRequestModalGBTRecallEditorView))
                .addGap(12, 12, 12))
        );
        pnlFurtherDetailsLayout.setVerticalGroup(
            pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFurtherDetailsLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestPhoneEmailEditorView)
                    .addComponent(rdbRequestModalGuardianEditorView))
                .addGap(18, 18, 18)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestModalRecallEditorView)
                    .addComponent(rdbRequestModalGBTRecallEditorView))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFurtherDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbRequestModalMedicalProfilePopup)
                    .addComponent(rdbRequestOpenDocumentStorePopup))
                .addGap(16, 16, 16))
        );

        mnuActions.setText("Actions");

        mniCreateNewPatient.setText("Create new patient");
        mnuActions.add(mniCreateNewPatient);

        mniUpdateSelectedPatient.setText("Update selected patient details");
        mnuActions.add(mniUpdateSelectedPatient);
        mnuActions.add(jSeparator1);

        mniPatientRecallsRequest.setText("Patient recalls");
        mnuActions.add(mniPatientRecallsRequest);
        mnuActions.add(jSeparator3);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlPatientSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlFurtherDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addComponent(pnlAppointmentHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/*
    private void btnFetchScheduleForSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFetchScheduleForSelectedAppointmentActionPerformed

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
    private javax.swing.JPopupMenu.Separator jSeparator3;
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
    private javax.swing.JMenuItem mniPatientRecallsRequest;
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
    private javax.swing.JRadioButton rdbRequestModalGBTRecallEditorView;
    private javax.swing.JRadioButton rdbRequestModalGuardianEditorView;
    private javax.swing.JRadioButton rdbRequestModalMedicalProfilePopup;
    private javax.swing.JRadioButton rdbRequestModalRecallEditorView;
    private javax.swing.JRadioButton rdbRequestOpenDocumentStorePopup;
    private javax.swing.JRadioButton rdbRequestPhoneEmailEditorView;
    private javax.swing.JScrollPane scrAppointmentHistory;
    private javax.swing.JTable tblAppointmentHistory;
    private javax.swing.JTextField txtAddressCounty;
    private javax.swing.JTextField txtAddressLine1;
    private javax.swing.JTextField txtAddressLine2;
    private javax.swing.JTextField txtAddressPostcode;
    private javax.swing.JTextField txtAddressTown;
    private javax.swing.JTextField txtNameForename;
    private javax.swing.JTextField txtNameSurname;
    // End of variables declaration//GEN-END:variables

    private void btnFetchScheduleForSelectedAppointmentActionPerformed(ActionEvent evt){
        
    }
}
