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
    
    private void doSaveUnbookableSlotRequest(){
        int OKToSaveAppointment = JOptionPane.YES_OPTION;
        initialiseEntityDescriptorFromView();

        //if (getViewDescriptor().getControllerDescription().getAppointment().getDuration().isZero())
        if (((Appointment)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT)).getDuration().isZero())
            JOptionPane.showMessageDialog(this, "Duration for unbookable slot undefined ((equals zero minutes)");

        //ViewController.ScheduleViewControllerActionEvent action = null;
        ScheduleViewController.Actions action = null;
        switch(getMyController().getDescriptor().
                getControllerDescription().getViewMode()){
            case CREATE:
                action = ScheduleViewController.Actions.
                    UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST;
                break;
            case UPDATE:
                action = ScheduleViewController.Actions.
                    UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST;
                break;
        }
        if (OKToSaveAppointment==JOptionPane.YES_OPTION){
            doActionEventFor(action);
        }
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
        this.btnSaveUnbookableSlot.setActionCommand(Actions.REQUEST_SAVE_UNBOOKABLE_SLOT.toString());
        
        LocalDate day = (LocalDate)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);

        this.setTitle("Unbookable slot editor (" + day.format(ddMMyyyyFormat) + ")");
        Appointment appointment = (Appointment)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT);
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
        else if (getMyController().getDescriptor().getControllerDescription().getViewMode().equals(ViewController.ViewMode.UPDATE)){    
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

        chkIsAllDay = new javax.swing.JCheckBox();
        pnlSlotDetails = new javax.swing.JPanel();
        cmbSelectStartTime = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        spnDurationHours = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,8,1));
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        spnDurationMinutes = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,55,5));
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaNotes = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        btnSaveUnbookableSlot = new javax.swing.JButton();
        btnCloseVew = new javax.swing.JButton();

        chkIsAllDay.setText("Make whole day unbookable");
        chkIsAllDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkIsAllDayActionPerformed(evt);
            }
        });

        pnlSlotDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Slot details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        cmbSelectStartTime.setModel(new javax.swing.DefaultComboBoxModel<>());

        jLabel1.setText("Start time");

        jLabel2.setText("Hours");

        jLabel3.setText("Minutes");

        javax.swing.GroupLayout pnlSlotDetailsLayout = new javax.swing.GroupLayout(pnlSlotDetails);
        pnlSlotDetails.setLayout(pnlSlotDetailsLayout);
        pnlSlotDetailsLayout.setHorizontalGroup(
            pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSlotDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(spnDurationHours, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        pnlSlotDetailsLayout.setVerticalGroup(
            pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlSlotDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12)), "Notes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        txaNotes.setColumns(20);
        txaNotes.setRows(5);
        jScrollPane1.setViewportView(txaNotes);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnSaveUnbookableSlot.setText("Save");

        btnCloseVew.setText("Close view");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnSaveUnbookableSlot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCloseVew)
                .addGap(21, 21, 21))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveUnbookableSlot)
                    .addComponent(btnCloseVew))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSlotDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(chkIsAllDay)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkIsAllDay)
                .addGap(18, 18, 18)
                .addComponent(pnlSlotDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkIsAllDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkIsAllDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkIsAllDayActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseVew;
    private javax.swing.JButton btnSaveUnbookableSlot;
    private javax.swing.JCheckBox chkIsAllDay;
    private javax.swing.JComboBox<LocalDateTime> cmbSelectStartTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlSlotDetails;
    private javax.swing.JSpinner spnDurationHours;
    private javax.swing.JSpinner spnDurationMinutes;
    private javax.swing.JTextArea txaNotes;
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
        if (getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getViewMode().equals(ViewController.ViewMode.CREATE)){
            /*28/03/2024patientNote = new PatientNote();
            getMyController().getDescriptor()
                .getViewDescription()
                .getAppointment()
                .setPatientNote(patientNote);*/
        }   
        /*28/03/2024getMyController().getDescriptor()
                .getViewDescription()
                .getAppointment()
                .getPatientNote()
                .setNote(this.txaNotes.getText());*/
    }
}
