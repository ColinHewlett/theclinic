/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.modal_internal_frame_views;

import view.views.view_support_classes.renderers.SelectStartTimeLocalDateTimeRenderer;
import controller.Descriptor;
import controller.ViewController;
import view.View;
import model.Patient;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author colin
 */
public class ModalAppointmentEditorView extends View {
    private final String SETTINGS = "Settings";
    private final String FIRST_APPOINTMENT_START_TIME = "First appointment start time";
    private final String LAST_APPOINTMENT_START_TIME = "Last appointment start time";
    private final String EXIT_VIEW = "Close view";
    private JMenuBar mbrView = null;
    private JMenu mnuSelectSettings = null; 
    private JMenuItem mniFirstAppointmentStartTime = null;
    private JMenuItem mniLastAppointmentStartTime = null;
    private JMenuItem mniExitView = null;
    private View.Viewer myViewType = null;
    private Descriptor entityDescriptor = null;
    private ActionListener myController = null;
    private ViewController.ViewMode viewMode = null;
    private final String CREATE_BUTTON = "Create appointment";
    private final String UPDATE_BUTTON = "Update appointment";
    private DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Creates new form AppointmentEditorInternalFrame
     */
    public ModalAppointmentEditorView(View.Viewer myViewType, ActionListener myController,
            Descriptor entityDescriptor, 
            JDesktopPane desktop) {//ViewMode arg
        super("Appointment configuration view");
        setViewDescriptor(entityDescriptor);
        setMyController(myController);
        setMyViewType(myViewType);
        initComponents(desktop);
        initialiseViewMode();
        
        //desktop.add(this);
        this.setLayer(JLayeredPane.MODAL_LAYER);
        centreViewOnDesktop(desktop.getParent());
        this.initialiseView();
        this.setVisible(true);
        makeSelectSettingsMenu();
        mbrView = new JMenuBar();
        mbrView.add(this.mnuSelectSettings);
        setJMenuBar(mbrView);
        startModal();
    }
    
    private void makeSelectSettingsMenu(){
        this.mnuSelectSettings = new JMenu(SETTINGS);
        this.mniFirstAppointmentStartTime = new JMenuItem(FIRST_APPOINTMENT_START_TIME);
        this.mniLastAppointmentStartTime = new JMenuItem(LAST_APPOINTMENT_START_TIME);
        mniExitView = new JMenuItem(EXIT_VIEW);
        mnuSelectSettings.add(mniFirstAppointmentStartTime);
        mnuSelectSettings.add(mniLastAppointmentStartTime);
        mnuSelectSettings.add(new JSeparator());
        mnuSelectSettings.add(mniExitView);
   
        mniFirstAppointmentStartTime.addActionListener((ActionEvent e) -> mniFirstAppointmentStartTimeActionPerformed());
        mniLastAppointmentStartTime.addActionListener((ActionEvent e) -> mniLastAppointmentStartTimeActionPerformed());
        mniExitView.addActionListener((ActionEvent e) -> mniExitViewActionPerformed());
    }
    
    private boolean isNumeric(String s){
        return s.matches("-?\\d+(\\.\\d+)?");
    }
    
    private void mniFirstAppointmentStartTimeActionPerformed(){
        Integer minutes = null;
        LocalTime firstSlotStartTime = null;
        String message ="Specify number of minutes prior to the normal first "
                + "appointment slot start time for the day";
        String reply = JOptionPane.showInternalInputDialog(this, message);
        if (isNumeric(reply)){
            minutes = Integer.valueOf(reply);
            firstSlotStartTime = 
                    ViewController.FIRST_APPOINTMENT_SLOT.minusMinutes(minutes);
            LocalDate day = getViewDescriptor().getViewDescription().getDay();
            LocalDateTime localDateTime = LocalDateTime.of(day,firstSlotStartTime);
            this.cmbSelectStartTime.
                    insertItemAt(localDateTime, 0);
            cmbSelectStartTime.setSelectedIndex(0);
        }

    }
    
    private void mniLastAppointmentStartTimeActionPerformed(){
        
    }
    
    private void mniExitViewActionPerformed(){
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }
    
    private void startModal() {
        // We need to add an additional glasspane-like component directly
        // below the frame, which intercepts all mouse events that are not
        // directed at the frame itself.
        JPanel modalInterceptor = new JPanel();
        modalInterceptor.setOpaque(false);
        JLayeredPane lp = JLayeredPane.getLayeredPaneAbove(this);
        lp.setLayer(modalInterceptor, JLayeredPane.MODAL_LAYER.intValue());
        modalInterceptor.setBounds(0, 0, lp.getWidth(), lp.getHeight());
        modalInterceptor.addMouseListener(new MouseAdapter(){});
        modalInterceptor.addMouseMotionListener(new MouseMotionAdapter(){});
        lp.add(modalInterceptor);
        this.toFront();

        // We need to explicitly dispatch events when we are blocking the event
        // dispatch thread.
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        try {
            while (! this.isClosed())       {
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
            Container parent = this.getParent();
            if (parent != null) parent.remove(this);
        }
    }
    
    private void centreViewOnDesktop(Container desktopView){
        JInternalFrame view = this;
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
    
    @Override
    public void initialiseView(){
        LocalDate day = getViewDescriptor().getViewDescription().getDay();
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);
        populatePatientSelector(this.cmbSelectPatient);
        this.cmbSelectPatient.setEditable(false);
        this.cmbSelectStartTime.setSelectedItem(
                getViewDescriptor().getControllerDescription().getAppointment().getStart());
        this.spnDurationHours.setValue(getHoursFromDuration(getViewDescriptor().getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(getViewDescriptor().getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.txaNotes.setText(getViewDescriptor().getControllerDescription().getAppointment().getNotes());
        this.cmbSelectPatient.setSelectedItem(getViewDescriptor().getControllerDescription().getAppointment().getPatient());

        this.setTitle("Apppointment editor for " + day.format(ddMMyyyyFormat));
    }

    private void initialiseViewMode(){
        if (getViewDescriptor().getControllerDescription().getViewMode().
                equals(ViewController.ViewMode.UPDATE))
            this.btnCreateUpdateAppointment.setText(UPDATE_BUTTON);
        else this.btnCreateUpdateAppointment.setText(CREATE_BUTTON);
    }
    /**
     * the method process
     * -- collects data about appointment (start, duration, notes)
     * -- collects data about appointee (the patient)
     */
    private void initialiseEntityDescriptorFromView(){
        //get the appointment with  which the view was initialised (in particular the appointment key)
        getViewDescriptor().getViewDescription().setAppointment(
                    getViewDescriptor().getControllerDescription().getAppointment());
        //update this from current state of view
        
        //24/07/2022 13:42 (1c)
        /*
        getViewDescriptor().getViewDescription().setThePatient(
                (Patient)this.cmbSelectPatient.getSelectedItem());
        */
        getViewDescriptor().getViewDescription().getAppointment().setPatient((Patient)this.cmbSelectPatient.getSelectedItem());
        
        getViewDescriptor().getViewDescription().getAppointment().
                setStart((LocalDateTime)this.cmbSelectStartTime.getSelectedItem());
        getViewDescriptor().getViewDescription().getAppointment().
                setDuration(getDurationFromView());
        getViewDescriptor().getViewDescription().getAppointment().
                setNotes(this.txaNotes.getText());
    }
    private Duration getDurationFromView(){
        return Duration.ofMinutes(
                ((int)this.spnDurationHours.getValue() * 60) + 
                ((int)this.spnDurationMinutes.getValue()));
    }
    /**
     * On entry the local Descriptor.Appointment is initialised 
     */
    private void initialiseViewFromED(){
        DateTimeFormatter hhmmFormat = DateTimeFormatter.ofPattern("HH:mm");
        //this.spnStartTime.setValue(getViewDescriptor().getAppointment().getData().getStart().format(hhmmFormat)); 
        this.spnDurationHours.setValue(getHoursFromDuration(getViewDescriptor().getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(getViewDescriptor().getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.txaNotes.setText(getViewDescriptor().getControllerDescription().getAppointment().getNotes());
        populatePatientSelector(this.cmbSelectPatient);
        if (getViewDescriptor().getControllerDescription().getAppointment().getPatient().getIsKeyDefined()){
            this.cmbSelectPatient.setSelectedItem(getViewDescriptor().getControllerDescription().getAppointment().getPatient());
        }
    }
    private Integer getHoursFromDuration(long duration){
        return (int)duration / 60;
    }
    private Integer getMinutesFromDuration(long duration){
        return (int)duration % 60;
    }
    private void populatePatientSelector(JComboBox<Patient> selector){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                getViewDescriptor().getControllerDescription().getPatients();
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        selector.setModel(model);
        selector.setSelectedIndex(-1);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents(JDesktopPane desktop) {
        desktop.add(this);
        pnlAppointmentDetails = new javax.swing.JPanel();
        lblDialogForAppointmentDefinitionTitle1 = new javax.swing.JLabel();
        lblDialogForAppointmentDefinitionTitle2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        spnDurationHours = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,8,1));
        spnDurationMinutes = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,55,5));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbSelectPatient = new javax.swing.JComboBox<Patient>();
        cmbSelectStartTime = new javax.swing.JComboBox<LocalDateTime>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaNotes = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        pnlAppointmentDetails.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblDialogForAppointmentDefinitionTitle1.setText("Patient");

        lblDialogForAppointmentDefinitionTitle2.setText("Start time");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Duration"));

        jLabel1.setText("hours");

        jLabel2.setText("minutes");
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        cmbSelectPatient.setModel(new javax.swing.DefaultComboBoxModel<Patient>());
        cmbSelectPatient.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbSelectStartTime.setModel(new javax.swing.DefaultComboBoxModel<LocalDateTime>());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes"));

        txaNotes.setColumns(20);
        txaNotes.setRows(5);
        jScrollPane2.setViewportView(txaNotes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlAppointmentDetailsLayout = new javax.swing.GroupLayout(pnlAppointmentDetails);
        pnlAppointmentDetails.setLayout(pnlAppointmentDetailsLayout);
        pnlAppointmentDetailsLayout.setHorizontalGroup(
            pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentDetailsLayout.createSequentialGroup()
                        .addComponent(lblDialogForAppointmentDefinitionTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentDetailsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                                .addComponent(lblDialogForAppointmentDefinitionTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(70, 70, 70))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlAppointmentDetailsLayout.setVerticalGroup(
            pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDialogForAppointmentDefinitionTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDialogForAppointmentDefinitionTitle2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCreateUpdateAppointment.setText("Update appointment");
        btnCreateUpdateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateUpdateAppointmentActionPerformed(evt);
            }
        });

        btnCancel.setText("Close view");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(33, 33, 33)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateUpdateAppointment)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAppointmentDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAppointmentDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateUpdateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateUpdateAppointmentActionPerformed
        int OKToSaveAppointment = JOptionPane.YES_OPTION;
        evt = null;
        initialiseEntityDescriptorFromView();
        /**
        * check if an appointee has been defined
        * -- note this is defined in ed.getRequest().getPatient()
        * -- appointee for appointment is not defined in ed.getRequest().getAppointment().getAppointee()!!
        * check if a non zero duration value has been defined
        * check if no notes have been defined if still ok to save appointment
        */
        if (getViewDescriptor().getViewDescription().getAppointment().getPatient()== null){
            JOptionPane.showMessageDialog(this, "A patient has not been selected for this appointment");
        }
        else if (getViewDescriptor().getControllerDescription().getAppointment().getDuration().isZero()){
            JOptionPane.showMessageDialog(this, "Defined duration for appointment must be longer than zero minutes");
        }
        else {
            if (getViewDescriptor().getViewDescription().getAppointment().getNotes().isEmpty()){
                String[] options = {"Yes", "No"};
                OKToSaveAppointment = JOptionPane.showOptionDialog(this,
                    "No notes defined for appointment. Save anyway?",null,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    null);
            }
            if (OKToSaveAppointment==JOptionPane.YES_OPTION){
                switch (getViewDescriptor().getControllerDescription().getViewMode()){
                    case CREATE:
                        evt = new ActionEvent(ModalAppointmentEditorView.this,
                            ActionEvent.ACTION_PERFORMED,
                            ViewController.AppointmentScheduleViewControllerActionEvent.
                            APPOINTMENT_EDITOR_CREATE_REQUEST.toString());
                        ModalAppointmentEditorView.this.getMyController().actionPerformed(evt);
                        break;
                    case UPDATE:
                        evt = new ActionEvent(ModalAppointmentEditorView.this,
                            ActionEvent.ACTION_PERFORMED,
                            ViewController.AppointmentScheduleViewControllerActionEvent.
                            APPOINTMENT_EDITOR_UPDATE_REQUEST.toString());
                        ModalAppointmentEditorView.this.getMyController().actionPerformed(evt);
                        break;
                }
            }
        }
    }//GEN-LAST:event_btnCreateUpdateAppointmentActionPerformed

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }//GEN-LAST:event_btnCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreateUpdateAppointment;
    private javax.swing.JComboBox<Patient> cmbSelectPatient;
    private javax.swing.JComboBox<LocalDateTime> cmbSelectStartTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDialogForAppointmentDefinitionTitle1;
    private javax.swing.JLabel lblDialogForAppointmentDefinitionTitle2;
    private javax.swing.JPanel pnlAppointmentDetails;
    private javax.swing.JSpinner spnDurationHours;
    private javax.swing.JSpinner spnDurationMinutes;
    private javax.swing.JTextArea txaNotes;
    // End of variables declaration//GEN-END:variables
}
