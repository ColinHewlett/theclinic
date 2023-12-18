/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import com.github.lgooddatepicker.components.DatePickerSettings;
import _system_environment_variables.SystemDefinitions;
import controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;
import model.Appointment;
import model.Entity;
import model.Patient;
import view.View;
import view.views.view_support_classes.models.Appointments3ColumnTableModel;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;

/**
 *
 * @author colin
 */
public class TestPatientView extends View {
    DateTimeFormatter dmyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter dmyhhmmFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
    
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
    
    private int getAge(LocalDate dob){
        return Period.between(dob, LocalDate.now()).getYears();
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
        if(TestPatientView.TitleItem.Dr.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = TestPatientView.TitleItem.Dr.toString();
        }
        else if(TestPatientView.TitleItem.Mr.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = TestPatientView.TitleItem.Mr.toString();
        }
        else if(TestPatientView.TitleItem.Mrs.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = TestPatientView.TitleItem.Mrs.toString();
        }
        else if(TestPatientView.TitleItem.Ms.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = TestPatientView.TitleItem.Ms.toString();
        }
        else if(TestPatientView.TitleItem.Miss.ordinal()==this.cmbTitle.getSelectedIndex()){
            value = TestPatientView.TitleItem.Miss.toString();
        }
 
        return value;
    }
    private void setPatientTitle(String title){
        if (title == null){
            cmbTitle.setSelectedIndex(-1);
        }
        else{
            Integer index = null;
            for(TestPatientView.TitleItem ti: TestPatientView.TitleItem.values()){
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
            for (TestPatientView.GenderItem gi: TestPatientView.GenderItem.values()){
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
        if(TestPatientView.YesNoItem.Yes.ordinal()==this.cmbIsGuardianAPatient.getSelectedIndex()){
            value = true;
        }
        else if(TestPatientView.YesNoItem.No.ordinal()==this.cmbIsGuardianAPatient.getSelectedIndex()){
            value = false;
        }
        return value;
    }
    private void setIsGuardianAPatient(boolean isGuardianAPatient){
        if (isGuardianAPatient){
            cmbIsGuardianAPatient.setSelectedItem(TestPatientView.YesNoItem.Yes);
        }
        else{
            cmbIsGuardianAPatient.setSelectedItem(TestPatientView.YesNoItem.No);
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
    
    private void populateAppointmentsHistoryTable(){
        Appointments3ColumnTableModel tableModel = 
                (Appointments3ColumnTableModel)tblAppointmentHistory.getModel(); 
        tableModel.removeAllElements();
        try{
            /*
            if (patient.getIsKeyDefined()){//if patient data in view has just been cleared  
                patient.setScope(Entity.Scope.FOR_PATIENT);
                Iterator<Appointment> it = patient.getAppointmentHistory().iterator();
                while (it.hasNext()){
                    tableModel.addElement(it.next());
                }
            }
            */
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
    
    /**
     * Creates new form TestPatientView
     */
    public TestPatientView(
            View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Patient view");
        setMyViewType(myViewType);
        setMyController(myController); 
        setDesktopView(desktopView);
    }
    
    @Override
    public void initialiseView(){ 
        initComponentsx();
        tblAppointmentHistory.setModel(new Appointments3ColumnTableModel());
        ViewController.setJTableColumnProperties(
                tblAppointmentHistory, 
                scrAppointmentHistory.getPreferredSize().width, 
                22,22,56);
        populateAppointmentsHistoryTable();
        try{
            setVisible(true);
            setClosable(false);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            setSize(976,getPatientViewHeight());
        }catch(PropertyVetoException ex){
            
        }
    }
    
//<editor-fold defaultstate="Collapsed" desc="Menu event handlers">    
    private void mniCloseViewActionPerformed(java.awt.event.ActionEvent evt) {                                             
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
    }
    
    private void mniDeleteSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {                                                         
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

    private void mniCreateNewPatientActionPerformed(java.awt.event.ActionEvent evt) {                                                    
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
    }                                                   

    private void mniUpdateSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {                                                         
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
    
    private void mniRecoverDeletedPatientActionPerformed(java.awt.event.ActionEvent evt) {                                                         
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
    }
    //</editor-fold>
    
    private void initComponentsx(){ 
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
        cmbIsGuardianAPatient = new javax.swing.JComboBox<TestPatientView.YesNoItem>();
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
        cmbIsGuardianAPatient.setModel(new javax.swing.DefaultComboBoxModel<TestPatientView.YesNoItem>(TestPatientView.YesNoItem.values()));
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
        cmbTitle.setModel(new javax.swing.DefaultComboBoxModel<>(TestPatientView.TitleItem.values()));
        cmbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmbTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTitleActionPerformed(evt);
            }
        });

        cmbGender.addItemListener(itemListener);
        cmbGender.setEditable(true);
        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(TestPatientView.GenderItem.values()));
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
    //<editor-fold defaultstate="collapsed" desc="Variables declaration">
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JButton btnFetchScheduleForSelectedAppointment;
    private javax.swing.JComboBox<TestPatientView.GenderItem> cmbGender;
    private javax.swing.JComboBox<TestPatientView.YesNoItem> cmbIsGuardianAPatient;
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JComboBox<model.Patient> cmbSelectGuardian;
    private javax.swing.JComboBox<TestPatientView.TitleItem> cmbTitle;
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
    // End of variables declaration 
    
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
    //</editor-fold>

    //<editor-fold defaultstate="Collapsed" desc="Event handlers">
    //The DocumentListener tracks any change made to any JTextField on form

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
    
    ItemListener itemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent e){
            setViewStatus(true);
        }
    };

    ItemListener itemSelectGuardianListener = new ItemListener(){
        public void itemStateChanged(ItemEvent e){
            if (String.valueOf(cmbIsGuardianAPatient.getSelectedItem()).equals("Yes")){
                if (cmbIsGuardianAPatient.getSelectedIndex() == -1) setViewStatus(false);
                else setViewStatus(true);
            }
            else setViewStatus(false); 
        }  
    };
    
    private void cmbSelectGuardianActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
    }
    
    private void cmbIsGuardianAPatientActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        if (this.cmbIsGuardianAPatient.getSelectedItem()!=null){
            switch ((TestPatientView.YesNoItem)this.cmbIsGuardianAPatient.getSelectedItem()){
                case Yes:
                    this.cmbSelectGuardian.setEnabled(true);
                    break;
                case No:
                    this.cmbSelectGuardian.setEnabled(false);
                    break;
            }
        }
    } 
    
    private void txtForenamesActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
    }
    
    private void txtAddressLine2ActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
    }                                               

    private void txtAddressLine1ActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
    }
    
    private void txtSurnameActionPerformed(java.awt.event.ActionEvent evt) {                                           
        // TODO add your handling code here:
    }
    
    private void cmbTitleActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (this.cmbTitle.getSelectedItem() != null){
            if (this.cmbTitle.getSelectedItem().equals(TestPatientView.TitleItem.Untitled)){
                this.cmbTitle.setSelectedIndex(-1);
            }
        }
    }
//</editor-fold>
}
/*
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 840, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

*/
