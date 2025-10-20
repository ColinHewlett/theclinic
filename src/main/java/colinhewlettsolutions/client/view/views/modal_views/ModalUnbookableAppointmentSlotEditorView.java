/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.Descriptor;
import colinhewlettsolutions.client.controller.ScheduleViewController;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.support_classes.renderers.SelectStartTimeLocalDateTimeRenderer;

/**
 * 
 * @author colin
 */
public class ModalUnbookableAppointmentSlotEditorView extends ModalView
                                                      implements ActionListener,
                                                                 PropertyChangeListener{
    
    enum Actions{
        REQUEST_CLOSE_VIEW,
        REQUEST_SAVE_UNBOOKABLE_SLOT,
        REQUEST_CANCEL_UNBOOKABLE_SLOT   
    }
    private final DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /**
     * 
     * @param myViewType; either CREATE or UPDATE view mode 
     * @param myController; basses down to view a reference to its view controller
     * @param desktopView; 
     */
    public ModalUnbookableAppointmentSlotEditorView (
            View.Viewer myViewType, 
            ViewController myController, 
            DesktopView desktopView) {
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Actions action = Actions.valueOf(e.getActionCommand());
        switch(action) {
            case REQUEST_CLOSE_VIEW ->{
                try{
                    this.setClosed(true);
                }
                catch (PropertyVetoException ex){

                }
                break;
            }
            case REQUEST_CANCEL_UNBOOKABLE_SLOT ->{
                doCancelUnbookableSlotRequest();
                break;
            }
            case REQUEST_SAVE_UNBOOKABLE_SLOT ->{
                doSaveUnbookableSlotRequest();
                break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.ScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTMENT_SCHEDULE_ERROR_RECEIVED:
                String error = (String)getMyController().getDescriptor()
                            .getControllerDescription().getProperty(SystemDefinition.Properties.ERROR);
                JOptionPane.showInternalMessageDialog(this, error, 
                        "View controller error", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }
    
    private void doCancelUnbookableSlotRequest(){
        String[] options = {"Yes", "No"};
        String message = "Are you sure you want this unbookable slot cancelled?";
        int close = JOptionPane.showOptionDialog(this,
                        message,null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
        if (close == JOptionPane.YES_OPTION){
            initialiseEntityDescriptorFromView();
            doActionEventFor(ScheduleViewController.
                    Actions.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CANCEL_REQUEST);
        }
    }
    
    private void doSaveUnbookableSlotRequest(){
        //int OKToSaveAppointment = JOptionPane.YES_OPTION;
        initialiseEntityDescriptorFromView();

        //if (getViewDescriptor().getControllerDescription().getAppointment().getDuration().isZero())
        if (((Appointment)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT)).getDuration().isZero())
            JOptionPane.showMessageDialog(this, "Duration for unbookable slot undefined ((equals zero minutes)");

        //ViewController.ScheduleViewControllerActionEvent action = null;
        ScheduleViewController.Actions action = null;
        /*switch(getMyController().getDescriptor().
                getControllerDescription().getViewMode()){*/
        ViewController.ViewMode viewMode = (ViewController.ViewMode)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.VIEW_MODE);
        switch(viewMode){
            case CREATE:
                action = ScheduleViewController.Actions.
                    UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST;
                break;
            case UPDATE:
                action = ScheduleViewController.Actions.
                    UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST;
                break;
        }
        doActionEventFor(action);
    }
    
    private void populateSelectStartTime(LocalDate day){
        DefaultComboBoxModel<LocalDateTime> model = new DefaultComboBoxModel<>();
        
        LocalDateTime value = day.atTime(ViewController.FIRST_APPOINTMENT_SLOT);
        do{
            model.addElement(value);
            value = value.plusMinutes(5);   
        }while(!value.isAfter(day.atTime(ViewController.LAST_APPOINTMENT_SLOT)));
        this.cmbSelectStartTime.setModel(model);
    }
    
    private void initialiseStartTimeAndDurationControls(){
        this.cmbSelectStartTime.setSelectedItem(
            ((Appointment)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.APPOINTMENT)).getStart());
        this.spnDurationHours.setValue(getHoursFromDuration(
                ((Appointment)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.APPOINTMENT)).getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(
                ((Appointment)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.APPOINTMENT)).getDuration().toMinutes()));
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        chkIsAllDay.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()== ItemEvent.SELECTED){
                    spnDurationHours.setValue(8);
                    spnDurationMinutes.setValue(0);
                    spnDurationHours.setEnabled(false);
                    spnDurationMinutes.setEnabled(false);
                    cmbSelectStartTime.setEnabled(false);
                    cmbSelectStartTime.setSelectedIndex(0);
                }
                else{
                    spnDurationHours.setValue(0);
                    spnDurationMinutes.setValue(0);
                    spnDurationHours.setEnabled(true);
                    spnDurationMinutes.setEnabled(true);
                    cmbSelectStartTime.setEnabled(true);
                }
            }
        });
        
        this.btnCloseVew.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        
        this.btnCreateUpdateUnbookableSlot.setActionCommand(Actions.REQUEST_SAVE_UNBOOKABLE_SLOT.toString());
        this.btnCreateUpdateUnbookableSlot.addActionListener(this);
        ViewController.ViewMode viewMode = (ViewController.ViewMode)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.VIEW_MODE);
        switch(viewMode){
            case CREATE ->{
                this.btnCreateUpdateUnbookableSlot.setText("Create");
                this.btnCancelUnbookableSlot.setEnabled(false);
                break;
            }
            case UPDATE ->{
                this.btnCreateUpdateUnbookableSlot.setText("Update");
                this.btnCancelUnbookableSlot.setEnabled(true);
                this.btnCancelUnbookableSlot.setActionCommand(Actions.REQUEST_CANCEL_UNBOOKABLE_SLOT.toString());
                this.btnCancelUnbookableSlot.addActionListener(this);
                break;
            }    
        }
        this.btnCloseVew.addActionListener(this);
        
        LocalDate day = (LocalDate)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);

        this.setTitle("Unbookable slot editor (" + day.format(ddMMyyyyFormat) + ")");
        Appointment appointment = (Appointment)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT);
        if (appointment.getNotes()!=null)
            this.txtReasonForUnbookableSlot.setText(appointment.getNotes());
        if (appointment.getPatient()==null){
            if (appointment.getStart()==null){
                appointment.setStart(LocalDateTime.of(day, LocalTime.of(9,0)));
                appointment.setDuration(Duration.ofHours(8));
                this.chkIsAllDay.setSelected(true);
            }
            else{
                this.chkIsAllDay.setSelected(false);
                this.cmbSelectStartTime.setSelectedItem(appointment.getStart());
                this.spnDurationHours.setValue(appointment.getDuration().toHours());
                //this.spnDurationMinutes.setValue(appointment.getDuration().toMinutes());
                //this.spnDurationHours.setValue(getHoursFromDuration(appointment.getDuration().toHours()));
                this.spnDurationMinutes.setValue(getMinutesFromDuration(appointment.getDuration().toMinutes()));
            }

        }  
        //else if (getMyController().getDescriptor().getControllerDescription().getViewMode().equals(ViewController.ViewMode.UPDATE)){ 
        else if (viewMode.equals(ViewController.ViewMode.UPDATE)){
            initialiseStartTimeAndDurationControls();    
            this.chkIsAllDay.setSelected(false);
            /*28/03/2024this.txaNotes.setText(getMyController()
                    .getDescriptor()
                    .getControllerDescription()
                    .getAppointment().getPatientNote().getNote());*/
        }
        else this.chkIsAllDay.setSelected(true);
        this.setLayer(JLayeredPane.MODAL_LAYER);
        this.setVisible(true); 

    }
    
    private Integer getHoursFromDuration(long duration){
        return (int)duration / 60;
    }
    
    private Integer getMinutesFromDuration(long duration){
        return (int)duration % 60;
    }
    
    private void doActionEventFor(ScheduleViewController.Actions action){
        ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                action.toString());
        this.getMyController().actionPerformed(actionEvent);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSlotDetails = new javax.swing.JPanel();
        cmbSelectStartTime = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        spnDurationHours = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,8,1));
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        spnDurationMinutes = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,55,5));
        chkIsAllDay = new javax.swing.JCheckBox();
        pnlReasonForUnbookableSlot = new javax.swing.JPanel();
        txtReasonForUnbookableSlot = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnCreateUpdateUnbookableSlot = new javax.swing.JButton();
        btnCloseVew = new javax.swing.JButton();
        btnCancelUnbookableSlot = new javax.swing.JButton();

        pnlSlotDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Slot details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        cmbSelectStartTime.setModel(new javax.swing.DefaultComboBoxModel<>());

        jLabel1.setText("Start time");

        jLabel2.setText("Hours");

        jLabel3.setText("Minutes");

        chkIsAllDay.setText("Make whole day unbookable");
        chkIsAllDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIsAllDayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSlotDetailsLayout = new javax.swing.GroupLayout(pnlSlotDetails);
        pnlSlotDetails.setLayout(pnlSlotDetailsLayout);
        pnlSlotDetailsLayout.setHorizontalGroup(
            pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkIsAllDay)
                    .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSlotDetailsLayout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(18, 18, 18))
                                .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(37, 37, 37)))
                            .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(26, 26, 26)))
                        .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))))
                .addContainerGap(82, Short.MAX_VALUE))
        );
        pnlSlotDetailsLayout.setVerticalGroup(
            pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkIsAllDay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        pnlReasonForUnbookableSlot.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)), "Reason for unbookable slot", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        javax.swing.GroupLayout pnlReasonForUnbookableSlotLayout = new javax.swing.GroupLayout(pnlReasonForUnbookableSlot);
        pnlReasonForUnbookableSlot.setLayout(pnlReasonForUnbookableSlotLayout);
        pnlReasonForUnbookableSlotLayout.setHorizontalGroup(
            pnlReasonForUnbookableSlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlReasonForUnbookableSlotLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtReasonForUnbookableSlot)
                .addContainerGap())
        );
        pnlReasonForUnbookableSlotLayout.setVerticalGroup(
            pnlReasonForUnbookableSlotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReasonForUnbookableSlotLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtReasonForUnbookableSlot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnCreateUpdateUnbookableSlot.setText("Create");

        btnCloseVew.setText("<html><center>Close</center><center>view</center></html>");

        btnCancelUnbookableSlot.setText("<html><center>Cancel</centre><center>unbookable</center><center>slot</center></html>");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancelUnbookableSlot)
                    .addComponent(btnCreateUpdateUnbookableSlot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseVew))
                .addGap(12, 12, 12))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateUpdateUnbookableSlot, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelUnbookableSlot, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCloseVew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlReasonForUnbookableSlot, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSlotDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSlotDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlReasonForUnbookableSlot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkIsAllDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIsAllDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkIsAllDayActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelUnbookableSlot;
    private javax.swing.JButton btnCloseVew;
    private javax.swing.JButton btnCreateUpdateUnbookableSlot;
    private javax.swing.JCheckBox chkIsAllDay;
    private javax.swing.JComboBox<LocalDateTime> cmbSelectStartTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel pnlReasonForUnbookableSlot;
    private javax.swing.JPanel pnlSlotDetails;
    private javax.swing.JSpinner spnDurationHours;
    private javax.swing.JSpinner spnDurationMinutes;
    private javax.swing.JTextField txtReasonForUnbookableSlot;
    // End of variables declaration//GEN-END:variables

    private Duration getDurationFromView(){
        Long hours = ((SpinnerNumberModel)spnDurationHours.
                getModel()).getNumber().longValue();
        hours = hours * 60;
        Integer minutes = ((Integer)this.spnDurationMinutes.getValue());
        return Duration.ofMinutes(hours + minutes);
    }
    
    /*28/03/2024private PatientNote patientNote = null;*/
    private void initialiseEntityDescriptorFromView(){
        getMyController().getDescriptor().getViewDescription().
                setProperty(SystemDefinition.Properties.APPOINTMENT, getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.APPOINTMENT));
        ((Appointment)getMyController().getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).
                setStart((LocalDateTime)this.cmbSelectStartTime.getSelectedItem());
        ((Appointment)getMyController().getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).
                setDuration(getDurationFromView());
        ((Appointment)getMyController().getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT)).
                setNotes(this.txtReasonForUnbookableSlot.getText());
        /*
        if (getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getViewMode().equals(ViewController.ViewMode.CREATE)){
            //28/03/2024patientNote = new PatientNote();
            getMyController().getDescriptor()
                .getViewDescription()
                .getAppointment()
                .setPatientNote(patientNote);
        }  */ 
        /*28/03/2024getMyController().getDescriptor()
                .getViewDescription()
                .getAppointment()
                .getPatientNote()
                .setNote(this.txaNotes.getText());*/
    }
}
