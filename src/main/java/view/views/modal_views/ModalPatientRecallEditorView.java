/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import controller.ViewController;
import java.awt.event.ActionEvent;
import model.Patient;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import view.View;
import view.views.non_modal_views.DesktopView;
import javax.swing.ImageIcon;


/**
 *
 * @author colin
 */
public class ModalPatientRecallEditorView extends ModalView{
    boolean hasRecallDateChanged = false;
    boolean hasRecallFrequencyChanged = false;
    
    private boolean getHasRecallDateChanged(){
        return hasRecallDateChanged;
    }
    
    private void setHasRecallFrequencyChanged(boolean value){
        hasRecallFrequencyChanged = value;
    }
    
    private boolean getHasRecallFrequencyChanged(){
        return hasRecallFrequencyChanged;
    }
    
    private void setHasRecallDateChanged(boolean value){
        hasRecallDateChanged = value;
    }
    
    private LocalDate getRecallDate(){
        return this.dpsRecallDatePicker.getDate();
    }
    private void setRecallDate(LocalDate dentalRecallDate){
        if (dentalRecallDate != null)
            this.dpsRecallDatePicker.setDate(dentalRecallDate);
    }
    private Integer getRecallFrequency(){
        return (Integer)this.spnRecallFrequency.getValue();
    }
    private void setRecallFrequency(Integer value){
        if (value == null) this.spnRecallFrequency.setValue(0);
        else this.spnRecallFrequency.setValue(value);
    }
    
    class RecallDatePickerDateChangeListener implements DateChangeListener {
        @Override
        public void dateChanged(DateChangeEvent event) {
            setHasRecallDateChanged(true);
        }
    }

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientRecallEditorView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setTitle("Patient recall editor");
        setVisible(true);
        Patient patient = getMyController()
                .getDescriptor()
                .getControllerDescription().getPatient();
        setRecallDate(patient.getRecall().getDentalDate());
        setRecallFrequency(patient.getRecall().getDentalFrequency());
        setHasRecallDateChanged(false);
        setHasRecallFrequencyChanged(false);
        dpsRecallDatePicker.addDateChangeListener(
                new ModalPatientRecallEditorView.
                        RecallDatePickerDateChangeListener());
    }
    
    /**
     * Sends an action event to the view controller to save recall data
     * Only called if changes have been made to patients recall data
     */
    private void doSaveRecallChanges(){
        Patient patient = getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getPatient();

        if (getHasRecallDateChanged()){
            patient.getRecall().setDentalDate(getRecallDate());
            getMyController()
                    .getDescriptor()
                    .getViewDescription()
                    .setPatient(patient);
        }
        if (getHasRecallFrequencyChanged()){
            patient.getRecall().setDentalFrequency(getRecallFrequency());
            getMyController()
                    .getDescriptor()
                    .getViewDescription()
                    .setPatient(patient);
        }
        if (getHasRecallDateChanged() || getHasRecallFrequencyChanged()){
            ActionEvent actionEvent = new ActionEvent(
                ModalPatientRecallEditorView.this,ActionEvent.ACTION_PERFORMED, 
                ViewController
                        .PatientViewControllerActionEvent
                        .PATIENT_RECALL_EDITOR_VIEW_CHANGE
                        .toString());
            getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void initComponents() {

        pnlRecallDate = new javax.swing.JPanel();
        dpsRecallDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dpsRecallDatePicker.setSettings(settings);
        pnlRecallFrequency = new javax.swing.JPanel();
        spnRecallFrequency = new javax.swing.JSpinner();
        lblMonths = new javax.swing.JLabel();
        btnSaveDetails = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        this.spnRecallFrequency.setModel(new SpinnerNumberModel(6,0,12,3));
        btnSaveDetails.setText("Save");
        btnCancel.setText("Close");
        
        //event handlers
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try{
                    int reply = 0;
                    if (getHasRecallDateChanged() || getHasRecallFrequencyChanged()){
                        String message = "Save changes before closing?";
                        String[] options = {"Yes", "No"};
                        reply = JOptionPane.showOptionDialog(
                                ModalPatientRecallEditorView.this,
                                message,null,
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                null);
                    }
                    if (reply == JOptionPane.YES_OPTION){
                        doSaveRecallChanges();
                    }
                    ModalPatientRecallEditorView.this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
            }
        });
        
        spnRecallFrequency.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setHasRecallFrequencyChanged(true);
            }    
        });
        
        btnSaveDetails.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveRecallChanges();
                try{
                    ModalPatientRecallEditorView.this.setClosed(true);
                }
                catch (PropertyVetoException ex){

                }
            }
            
        });
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        datePickerButton = this.dpsRecallDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        

        pnlRecallDate.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                "Recall date", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));
        
        pnlRecallFrequency.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                "Recall frequency", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));

        lblMonths.setText("months");
//<editor-fold defaultstate="collapsed" desc="Recall date panel layout">
        javax.swing.GroupLayout pnlRecallDateLayout = new javax.swing.GroupLayout(pnlRecallDate);
        pnlRecallDate.setLayout(pnlRecallDateLayout);
        pnlRecallDateLayout.setHorizontalGroup(
            pnlRecallDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallDateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dpsRecallDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        pnlRecallDateLayout.setVerticalGroup(
            pnlRecallDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallDateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dpsRecallDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Recall frequency panel layout">
        javax.swing.GroupLayout pnlRecallFrequencyLayout = new javax.swing.GroupLayout(pnlRecallFrequency);
        pnlRecallFrequency.setLayout(pnlRecallFrequencyLayout);
        pnlRecallFrequencyLayout.setHorizontalGroup(
            pnlRecallFrequencyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallFrequencyLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(spnRecallFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblMonths)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        pnlRecallFrequencyLayout.setVerticalGroup(
            pnlRecallFrequencyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallFrequencyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecallFrequencyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnRecallFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMonths))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="View layout">        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRecallDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRecallFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSaveDetails)
                .addGap(43, 43, 43)
                .addComponent(btnCancel)
                .addGap(111, 111, 111))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlRecallFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlRecallDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCancel)
                    .addComponent(btnSaveDetails))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnSaveDetails;
    private javax.swing.JButton btnCancel;
    private com.github.lgooddatepicker.components.DatePicker dpsRecallDatePicker;
    private javax.swing.JLabel lblMonths;
    private javax.swing.JPanel pnlRecallDate;
    private javax.swing.JPanel pnlRecallFrequency;
    private javax.swing.JSpinner spnRecallFrequency;
    private javax.swing.JButton datePickerButton;
    // End of variables declaration
}
