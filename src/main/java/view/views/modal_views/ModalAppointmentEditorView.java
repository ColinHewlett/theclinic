/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.modal_views;

import view.views.view_support_classes.renderers.SelectStartTimeLocalDateTimeRenderer;
import view.views.non_modal_views.DesktopView;
import controller.Descriptor;
import controller.ViewController;
import view.View;
import model.Patient;
import model.PatientNote;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;


/**
 *
 * @author colin
 */
public class ModalAppointmentEditorView extends ModalView {
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
    //private ActionListener myController = null;
    private ViewController.ViewMode viewMode = null;
    private final String CREATE_BUTTON = "Create appointment";
    private final String UPDATE_BUTTON = "Update appointment";
    private DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalAppointmentEditorView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Appointment configuration view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
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
        boolean isError = false;
        String reply;
        String message;
        Integer minutes;
        LocalDateTime firstSlotStartTime = null;
        if (getMyController().getDescriptor().getControllerDescription().getAppointmentEarlyStart()!=null){
            firstSlotStartTime = getMyController().getDescriptor().
                    getControllerDescription().getAppointmentEarlyStart();
            message ="Specify number of minutes prior to the first "
                + "appointment slot start time for the day";
            reply = JOptionPane.showInternalInputDialog(this, message);
            if (isNumeric(reply)){
                minutes = Integer.valueOf(reply);
                firstSlotStartTime = 
                    firstSlotStartTime.minusMinutes(minutes);
            }else isError = true;
        }else{
            message ="Specify number of minutes prior to the normal first "
                + "appointment slot start time for the day";
            reply = JOptionPane.showInternalInputDialog(this, message);
            if (isNumeric(reply)){
                minutes = Integer.valueOf(reply);
                LocalDate day = getMyController().getDescriptor().getViewDescription().getScheduleDay();
                firstSlotStartTime =  LocalDateTime.of(
                        day,ViewController.FIRST_APPOINTMENT_SLOT).minusMinutes(minutes);  
            }else isError = true;
        }
        if (!isError){
            this.cmbSelectStartTime.
                    insertItemAt(firstSlotStartTime, 0);
            cmbSelectStartTime.setSelectedIndex(0);
        }else{
            JOptionPane.showMessageDialog(this, "an invalid number of minutes specified");
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

    @Override
    public void propertyChange(PropertyChangeEvent e){
        if (e.getPropertyName().equals(
            ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString())){
            //Descriptor ed = (Descriptor)e.getNewValue();
            String error = getMyController().getDescriptor().
                    getControllerDescription().getError();
            ViewController.displayErrorMessage(error,
                                               "Appointment editor dialog error",
                                               JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * method checks if an early appointment slot exists (i.e a slot which starts prior to the FIRST_APPOINTMENT_SLOT start time)
     * -- yes -> enter the start time first in the list displayed
     * -- no -> first start time in list is FIRST_APPOINTMENT_SLOT
     * also checks if a late appointment slot exists (after LAST_APPOINTMENT_SLOT start time)
     * In either event the list of available times at view initialisation can be lengthened in either direction
     * @param day; LocalDate represents the schedule day 
     */
    private void populateSelectStartTime(LocalDate day){
        DefaultComboBoxModel<LocalDateTime> model = new DefaultComboBoxModel<>();
        LocalDateTime value;
        if (getMyController().getDescriptor().
                getControllerDescription().getAppointmentEarlyStart()!=null){
            value = getMyController().getDescriptor().
                    getControllerDescription().getAppointmentEarlyStart();
            do{
                model.addElement(value);
                value = value.plusMinutes(5);   
            }while(value.isBefore(day.atTime(ViewController.FIRST_APPOINTMENT_SLOT)));
        }
        value = day.atTime(ViewController.FIRST_APPOINTMENT_SLOT);
        do{
            model.addElement(value);
            value = value.plusMinutes(5);   
        }while(!value.isAfter(day.atTime(ViewController.LAST_APPOINTMENT_SLOT)));
        if (getMyController().getDescriptor().getControllerDescription().getAppointmentLateStart()!=null){
            if (getMyController().getDescriptor().getControllerDescription().getAppointmentLateStart().
                    isAfter(day.atTime(ViewController.LAST_APPOINTMENT_SLOT))){
                do{
                    model.addElement(value);
                    value = value.plusMinutes(5);  
                }while(!value.isAfter(getMyController().getDescriptor().
                        getControllerDescription().getAppointmentLateStart()));
            }
        }        
        this.cmbSelectStartTime.setModel(model);
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        initialiseViewMode();
        
        this.setVisible(true);
        this.setSize(this.getWidth(), 550);
        makeSelectSettingsMenu();
        mbrView = new JMenuBar();
        mbrView.add(this.mnuSelectSettings);
        setJMenuBar(mbrView);
        
        LocalDate day = getMyController().getDescriptor().getViewDescription().getScheduleDay();
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);
        populatePatientSelector(this.cmbSelectPatient);
        this.cmbSelectPatient.setEditable(false);
        this.cmbSelectStartTime.setSelectedItem(
                getMyController().getDescriptor().getControllerDescription().getAppointment().getStart());
        this.spnDurationHours.setValue(getHoursFromDuration(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.txaNotepad.setText(getMyController().getDescriptor().
                getControllerDescription().getAppointment().getNotes());
        this.cmbSelectPatient.setSelectedItem(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getPatient());

        this.setTitle("Apppointment editor for " + day.format(ddMMyyyyFormat));
        
        this.setLayer(JLayeredPane.MODAL_LAYER);
    }

    private void initialiseViewMode(){
        if (getMyController().getDescriptor().getControllerDescription().getViewMode().
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
        getMyController().getDescriptor().getViewDescription().setAppointment(
                    getMyController().getDescriptor().
                            getControllerDescription().getAppointment());

        getMyController().getDescriptor().getViewDescription().getAppointment().setPatient((Patient)this.cmbSelectPatient.getSelectedItem());
        
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setStart((LocalDateTime)this.cmbSelectStartTime.getSelectedItem());
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setDuration(getDurationFromView());
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setNotes(this.txaNotepad.getText());
        
        PatientNote patientNote = new PatientNote();
        patientNote.setNote(this.txaNotepad.getText());
        patientNote.setDatestamp(getMyController()
                .getDescriptor()
                .getViewDescription()
                .getAppointment().getStart());
        patientNote.setPatient((getMyController()
                .getDescriptor()
                .getViewDescription()
                .getAppointment().getPatient()));
        patientNote.setLastUpdated(LocalDateTime.now());
        
        getMyController().getDescriptor().getViewDescription()
                .getAppointment().setPatientNote(patientNote);
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
        this.spnDurationHours.setValue(getHoursFromDuration(getMyController().getDescriptor().getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(getMyController().getDescriptor().getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.txaNotepad.setText(getMyController().getDescriptor().getControllerDescription().getAppointment().getNotes());
        populatePatientSelector(this.cmbSelectPatient);
        if (getMyController().getDescriptor().getControllerDescription().
                getAppointment().getPatient().getIsKeyDefined()){
            this.cmbSelectPatient.setSelectedItem(getMyController().
                    getDescriptor().getControllerDescription().getAppointment().getPatient());
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlAppointmentDetails = new javax.swing.JPanel();
        lblPatient = new javax.swing.JLabel();
        lblStart = new javax.swing.JLabel();
        pnlDuration = new javax.swing.JPanel();
        pnlScheduleDetails = new javax.swing.JPanel();
        pnlPatient = new javax.swing.JPanel();
        spnDurationHours = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,8,1));
        spnDurationMinutes = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,55,5));
        lblHours = new javax.swing.JLabel();
        lblMinutes = new javax.swing.JLabel();
        cmbSelectPatient = new javax.swing.JComboBox<Patient>();
        cmbSelectStartTime = new javax.swing.JComboBox<LocalDateTime>();
        pnlNotepad = new javax.swing.JPanel();
        scrNotepad = new javax.swing.JScrollPane();
        txaNotepad = new javax.swing.JTextArea();
        pnlOperations = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        pnlAppointmentDetails.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblPatient.setText("Patient");

        lblStart.setText("Start");

        pnlDuration.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Duration"));
        pnlScheduleDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Schedule details"));
        lblHours.setText("hours");

        lblMinutes.setText("minutes");
        
        cmbSelectPatient.setModel(new javax.swing.DefaultComboBoxModel<Patient>());
        cmbSelectPatient.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbSelectStartTime.setModel(new javax.swing.DefaultComboBoxModel<LocalDateTime>());

        pnlNotepad.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes"));

        txaNotepad.setColumns(20);
        txaNotepad.setRows(5);
        txaNotepad.setLineWrap(true);
        scrNotepad.setViewportView(txaNotepad);
        
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
       
        
        
        javax.swing.GroupLayout pnlScheduleDetailsLayout = new javax.swing.GroupLayout(pnlScheduleDetails);
        pnlScheduleDetails.setLayout(pnlScheduleDetailsLayout);
        pnlScheduleDetailsLayout.setHorizontalGroup(
            pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                .addComponent(lblMinutes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScheduleDetailsLayout.createSequentialGroup()
                .addGroup(pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStart)
                    .addComponent(lblHours))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(spnDurationHours))
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlScheduleDetailsLayout.setVerticalGroup(
            pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStart)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHours)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spnDurationMinutes)
                    .addComponent(lblMinutes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlDurationLayout = new javax.swing.GroupLayout(pnlDuration);
        pnlDuration.setLayout(pnlDurationLayout);
        pnlDurationLayout.setHorizontalGroup(
            pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDurationLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(pnlScheduleDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDurationLayout.setVerticalGroup(
            pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDurationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlScheduleDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        
        javax.swing.GroupLayout pnlPatientLayout = new javax.swing.GroupLayout(pnlPatient);
        pnlPatient.setLayout(pnlPatientLayout);
        pnlPatientLayout.setHorizontalGroup(
            pnlPatientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lblPatient)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPatientLayout.setVerticalGroup(
            pnlPatientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPatient)
                    .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        javax.swing.GroupLayout pnlNotepadLayout = new javax.swing.GroupLayout(pnlNotepad);
        pnlNotepad.setLayout(pnlNotepadLayout);
        pnlNotepadLayout.setHorizontalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotepad)
                .addContainerGap())
        );
        pnlNotepadLayout.setVerticalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addComponent(scrNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlAppointmentDetailsLayout = new javax.swing.GroupLayout(pnlAppointmentDetails);
        pnlAppointmentDetails.setLayout(pnlAppointmentDetailsLayout);
        pnlAppointmentDetailsLayout.setHorizontalGroup(
            pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(pnlDuration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlPatient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        pnlAppointmentDetailsLayout.setVerticalGroup(
            pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        
        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnCreateUpdateAppointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addContainerGap())
        );
        pnlOperationsLayout.setVerticalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateUpdateAppointment)
                    .addComponent(btnCancel))
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAppointmentDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAppointmentDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );
        
        /*
        javax.swing.GroupLayout pnlDurationLayout = new javax.swing.GroupLayout(pnlDuration);
        pnlDuration.setLayout(pnlDurationLayout);
        pnlDurationLayout.setHorizontalGroup(pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDurationLayout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDurationLayout.createSequentialGroup()
                        .addComponent(lblMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGap(15)
                        .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDurationLayout.createSequentialGroup()
                        .addComponent(lblHours, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8))
        );
        pnlDurationLayout.setVerticalGroup(pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHours))
                .addGap(18, 18, 18)
                .addGroup(pnlDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMinutes))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        

        javax.swing.GroupLayout pnlNotepadLayout = new javax.swing.GroupLayout(pnlNotepad);
        pnlNotepad.setLayout(pnlNotepadLayout);
        pnlNotepadLayout.setHorizontalGroup(pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotepad)
                .addContainerGap())
        );
        pnlNotepadLayout.setVerticalGroup(pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addComponent(scrNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
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
                        .addComponent(lblPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15,15,15)
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentDetailsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                                .addComponent(lblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                //.addGap(10,10,10)
                                .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(70, 70, 70))
                    .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlAppointmentDetailsLayout.setVerticalGroup(
            pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlAppointmentDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        

        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(33, 33, 33)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlOperationsLayout.setVerticalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAppointmentDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        */
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
        if (getMyController().getDescriptor().getViewDescription().getAppointment().getPatient()== null){
            JOptionPane.showMessageDialog(this, "A patient has not been selected for this appointment");
        }
        else if (getMyController().getDescriptor().getControllerDescription().getAppointment().getDuration().isZero()){
            JOptionPane.showMessageDialog(this, "Defined duration for appointment must be longer than zero minutes");
        }
        else {
            if (getMyController().getDescriptor().getViewDescription().getAppointment().getNotes().isEmpty()){
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
                switch (getMyController().getDescriptor().getControllerDescription().getViewMode()){
                    case CREATE:
                        evt = new ActionEvent(ModalAppointmentEditorView.this,
                            ActionEvent.ACTION_PERFORMED,
                            ViewController.ScheduleViewControllerActionEvent.
                            APPOINTMENT_EDITOR_CREATE_REQUEST.toString());
                        ModalAppointmentEditorView.this.getMyController().actionPerformed(evt);
                        break;
                    case UPDATE:
                        evt = new ActionEvent(ModalAppointmentEditorView.this,
                            ActionEvent.ACTION_PERFORMED,
                            ViewController.ScheduleViewControllerActionEvent.
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
    private javax.swing.JLabel lblHours;
    private javax.swing.JLabel lblMinutes;
    private javax.swing.JPanel pnlNotepad;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlDuration;
    private javax.swing.JPanel pnlScheduleDetails;
    private javax.swing.JPanel pnlPatient;
    private javax.swing.JScrollPane scrNotepad;
    private javax.swing.JLabel lblPatient;
    private javax.swing.JLabel lblStart;
    private javax.swing.JPanel pnlAppointmentDetails;
    private javax.swing.JSpinner spnDurationHours;
    private javax.swing.JSpinner spnDurationMinutes;
    private javax.swing.JTextArea txaNotepad;
    // End of variables declaration//GEN-END:variables
}
