/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.modal_internal_frame_views;

import controller.Descriptor;
import controller.ViewController;
import model.Appointment;
import _system_environment_variables.SystemDefinitions;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;
import model.Patient;
import view.View;
import view.views.view_support_classes.renderers.SelectStartTimeLocalDateTimeRenderer;

/**
 * 
 * @author colin
 */
public class ModalUnbookableAppointmentSlotEditorView extends View {
    private View.Viewer myViewType = null;
    private DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /**
     * 
     * @param myViewType; either CREATE or UPDATE view mode 
     * @param myController; basses down to view a reference to its view controller
     * @param desktop; parent JDesktopPane to which this internal frame is added
     */
    public ModalUnbookableAppointmentSlotEditorView (
            View.Viewer myViewType, ViewController myController,
            //Descriptor entityDescriptor, 
            JDesktopPane desktop) {
        super("Appointment configuration view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktop(desktop);
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
        /*
        this.setTitle("Unbookable slot editor (" + day.format(ddMMyyyyFormat) + ")");
        Appointment appointment = getMyController().getDescriptor().getControllerDescription().getAppointment();
        if (appointment.getPatient()==null){
            if (appointment.getStart()==null){
                appointment.setStart(LocalDateTime.of(day, LocalTime.of(9,0)));
                appointment.setDuration(Duration.ofHours(8));
                this.chkIsAllDay.setSelected(true);
            }
            else{
                this.chkIsAllDay.setSelected(false);
                this.cmbSelectStartTime.setSelectedItem(appointment.getStart());
                this.spnDurationHours.setValue(getHoursFromDuration(appointment.getDuration().toHours()));
                this.spnDurationMinutes.setValue(getMinutesFromDuration(appointment.getDuration().toMinutes()));
            }

        }  
        else if (getMyController().getDescriptor().getControllerDescription().getViewMode().equals(ViewController.ViewMode.UPDATE)){    
            this.cmbSelectStartTime.setSelectedItem(
                getMyController().getDescriptor().getControllerDescription().getAppointment().getStart());
            this.spnDurationHours.setValue(getHoursFromDuration(getMyController().getDescriptor().
                    getControllerDescription().getAppointment().getDuration().toHours()));
            this.spnDurationMinutes.setValue(getMinutesFromDuration(getMyController().getDescriptor().
                    getControllerDescription().getAppointment().getDuration().toMinutes()));
            this.txaNotes.setText(getMyController().getDescriptor().getControllerDescription().getAppointment().getNotes());
            this.chkIsAllDay.setSelected(false);
        }
        else this.chkIsAllDay.setSelected(true);

        
        desktop.add(this);
        this.setLayer(JLayeredPane.MODAL_LAYER);
        centreViewOnDesktop(desktop.getParent(),this);
        this.setVisible(true);
        
        startModal(this);
        */
    }
    
    private void startModal(JInternalFrame f) {
        // We need to add an additional glasspane-like component directly
        // below the frame, which intercepts all mouse events that are not
        // directed at the frame itself.
        JPanel modalInterceptor = new JPanel();
        modalInterceptor.setOpaque(false);
        JLayeredPane lp = JLayeredPane.getLayeredPaneAbove(f);
        lp.setLayer(modalInterceptor, JLayeredPane.MODAL_LAYER.intValue());
        modalInterceptor.setBounds(0, 0, lp.getWidth(), lp.getHeight());
        modalInterceptor.addMouseListener(new MouseAdapter(){});
        modalInterceptor.addMouseMotionListener(new MouseMotionAdapter(){});
        lp.add(modalInterceptor);
        f.toFront();

        // We need to explicitly dispatch events when we are blocking the event
        // dispatch thread.
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        try {
            while (! f.isClosed())       {
                if (EventQueue.isDispatchThread())    {
                    // The getNextEventMethod() issues wait() when no
                    // event is available, so we don't need do explicitly wait().
                    AWTEvent ev = queue.getNextEvent();
                    // This mimics EventQueue.dispatchEvent(). We can't use
                    // EventQueue.dispatchEvent() directly, because it is
                    // protected, unfortunately.
                    if (ev instanceof ActiveEvent)  ((ActiveEvent) ev).dispatch();
                    else if (ev.getSource() instanceof Component)  ((Component) ev.getSource()).dispatchEvent(ev);
                    else if (ev.getSource() instanceof MenuComponent)  ((MenuComponent) ev.getSource()).dispatchEvent(ev);
                    // Other events are ignored as per spec in
                    // EventQueue.dispatchEvent
                } else  {
                    // Give other threads a chance to become active.
                    Thread.yield();
                }
            }
        }
        catch (InterruptedException ex) {
            // If we get interrupted, then leave the modal state.
        }
        finally {
            // Clean up the modal interceptor.
            lp.remove(modalInterceptor);

            // Remove the internal frame from its parent, so it is no longer
            // lurking around and clogging memory.
            Container parent = f.getParent();
            if (parent != null) parent.remove(f);
        }
    }
    
    private void centreViewOnDesktop(Container desktopView, JInternalFrame view){
        Insets insets = desktopView.getInsets();
        Dimension deskTopViewDimension = desktopView.getSize();
        Dimension myViewDimension = view.getSize();
        /*
        view.setLocation(new Point(
                (int)(deskTopViewDimension.getWidth() - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2));
        */
        Point point = new Point(
                (int)((deskTopViewDimension.getWidth()) - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2);
        
        view.setLocation(point);
        System.out.println("Location = " + point);
        System.out.println("Desktop size = " + desktopView.getSize());
        System.out.println("Internal frame size = " + view.getSize());
        System.out.println("2 x point x = " + desktopView.getWidth()+ "-" + view.getWidth());
    }

    @Override
    public void addInternalFrameListeners(){
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        if (e.getPropertyName().equals(
            ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString())){
            Descriptor ed = (Descriptor)e.getNewValue();
            ViewController.displayErrorMessage(ed.getControllerDescription().getError(),
                                               "Appointment editor dialog error",
                                               JOptionPane.ERROR_MESSAGE);
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
            getMyController().getDescriptor().getControllerDescription().
                    getAppointment().getStart());
        this.spnDurationHours.setValue(getHoursFromDuration(
                getMyController().getDescriptor().
                getControllerDescription().getAppointment().getDuration().toHours()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(
                getMyController().getDescriptor().
                getControllerDescription().getAppointment().getDuration().toMinutes()));
    }
    
    @Override
    public void initialiseView(){
        LocalDate day = getMyController().getDescriptor().getViewDescription().getDay();
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);

        this.setTitle("Unbookable slot editor (" + day.format(ddMMyyyyFormat) + ")");
        Appointment appointment = getMyController().getDescriptor().getControllerDescription().getAppointment();
        if (appointment.getPatient()==null){
            if (appointment.getStart()==null){
                appointment.setStart(LocalDateTime.of(day, LocalTime.of(9,0)));
                appointment.setDuration(Duration.ofHours(8));
                this.chkIsAllDay.setSelected(true);
            }
            else{
                this.chkIsAllDay.setSelected(false);
                this.cmbSelectStartTime.setSelectedItem(appointment.getStart());
                this.spnDurationHours.setValue(getHoursFromDuration(appointment.getDuration().toHours()));
                this.spnDurationMinutes.setValue(getMinutesFromDuration(appointment.getDuration().toMinutes()));
            }

        }  
        else if (getMyController().getDescriptor().getControllerDescription().getViewMode().equals(ViewController.ViewMode.UPDATE)){    
            initialiseStartTimeAndDurationControls();    
            this.chkIsAllDay.setSelected(false);
        }
        else this.chkIsAllDay.setSelected(true);
        
        /*
        if (getMyController().getDescriptor().getControllerDescription().
                getAppointment().getPatient().toString().
                equals(SystemDefinitions.APPOINTMENT_UNBOOKABILITY_MARKER)){
            initialiseStartTimeAndDurationControls();
        }
        this.txaNotes.setText(getMyController().getDescriptor().
                getControllerDescription().getAppointment().getNotes());
        
        */
        
        
        //this.cmbSelectStartTime.setSelectedIndex(0);
        this.setTitle("Unbookable slot editor (" + day.format(ddMMyyyyFormat) + ")");

        
        getDesktop().add(this);
        this.setLayer(JLayeredPane.MODAL_LAYER);
        centreViewOnDesktop(getDesktop().getParent(),this);
        this.setVisible(true);
        
        startModal(this);  
    }
    
    private Integer getHoursFromDuration(long duration){
        return (int)duration / 60;
    }
    
    private Integer getMinutesFromDuration(long duration){
        return (int)duration % 60;
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
        btnSaveUnbookableSlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveUnbookableSlotActionPerformed(evt);
            }
        });

        btnCloseVew.setText("Close view");
        btnCloseVew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseVewActionPerformed(evt);
            }
        });

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlSlotDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    private void btnSaveUnbookableSlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveUnbookableSlotActionPerformed
        int OKToSaveAppointment = JOptionPane.YES_OPTION;
        evt = null;
        initialiseEntityDescriptorFromView();

        //if (getViewDescriptor().getControllerDescription().getAppointment().getDuration().isZero())
        if (getMyController().getDescriptor().getViewDescription().
                getAppointment().getDuration().isZero())
            JOptionPane.showMessageDialog(this, "Duration for unbookable slot undefined ((equals zero minutes)");
        else {
            if (getMyController().getDescriptor().
                    getViewDescription().getAppointment().getNotes().isEmpty()){
                String[] options = {"Yes", "No"};
                OKToSaveAppointment = JOptionPane.showOptionDialog(this,
                    "No notes defined for appointment. Save anyway?",null,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    null);
            }
        }
        ViewController.AppointmentScheduleViewControllerActionEvent action = null;
        switch(getMyController().getDescriptor().
                getControllerDescription().getViewMode()){
            case CREATE:
                action = ViewController.AppointmentScheduleViewControllerActionEvent.
                    UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_CREATE_REQUEST;
                break;
            case UPDATE:
                action = ViewController.AppointmentScheduleViewControllerActionEvent.
                    UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_UPDATE_REQUEST;
                break;
        }
        if (OKToSaveAppointment==JOptionPane.YES_OPTION){
                    evt = new ActionEvent(ModalUnbookableAppointmentSlotEditorView.this,
                                ActionEvent.ACTION_PERFORMED,
                                action.toString());
                            ModalUnbookableAppointmentSlotEditorView.this.getMyController().actionPerformed(evt);
        }
    }//GEN-LAST:event_btnSaveUnbookableSlotActionPerformed

    private void btnCloseVewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseVewActionPerformed
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }//GEN-LAST:event_btnCloseVewActionPerformed


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
        return Duration.ofMinutes(
                ((int)this.spnDurationHours.getValue() * 60) + 
                ((int)this.spnDurationMinutes.getValue()));
    }
    
    private void initialiseEntityDescriptorFromView(){
        getMyController().getDescriptor().getViewDescription().setAppointment(
                    getMyController().getDescriptor().getControllerDescription().
                            getAppointment());
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setStart((LocalDateTime)this.cmbSelectStartTime.getSelectedItem());
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setDuration(getDurationFromView());
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setNotes(this.txaNotes.getText());
    }
}
