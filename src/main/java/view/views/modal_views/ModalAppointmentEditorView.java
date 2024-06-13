/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */

package view.views.modal_views;

import model.entity.Patient;
import view.views.view_support_classes.renderers.SelectStartTimeLocalDateTimeRenderer;
import view.views.non_modal_views.DesktopView;
import controller.Descriptor;
import controller.ViewController;
import view.View;
import model.*;
/*28/03/2024import model.PatientNote;*/
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
import javax.swing.JPanel;
import view.views.non_modal_views.PatientView;

/**
 *
 * @author colin
 */
public class ModalAppointmentEditorView extends ModalView implements ActionListener {
    private final String SETTINGS = "Settings";
    private final String FIRST_APPOINTMENT_START_TIME = "First appointment start time";
    private final String LAST_APPOINTMENT_START_TIME = "Last appointment start time";
    private final String EXIT_VIEW = "Close view";
    private final String CLOSE_CAPTION = "<html><center>Close</center><center>view</center></html>";
    private final String CREATE_CAPTION = "<html><center>Create</center><center>appointment</center></html>";
    private final String UPDATE_CAPTION = "Update start & duration times";
    private final String TREATMENT_CAPTION = "<html><center>Select</center><center>treatment</center></html>";
    private final String PANEL_START_DURATION_CAPTION = "Start & duration";
    private final String PANEL_SCHEDULE_DETAILS_CAPTION = "Schedule details";
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
    /** Creates new form ModalAppointmentCreateView */
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalAppointmentEditorView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg) {
        setTitle("Appointment configuration view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
        //initComponents();
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
            JOptionPane.showInternalMessageDialog(this, 
                    "an invalid number of minutes specified", 
                    "View error",JOptionPane.WARNING_MESSAGE);
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
    
    enum Action{
            REQUEST_CREATE_UPDATE_APPOINTMENT,
            REQUEST_TREATMENT_SELECTION,
            REQUEST_CLOSE_VIEW};
    @Override 
    public void actionPerformed(ActionEvent e){
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CREATE_UPDATE_APPOINTMENT:
                if (doCreateUpdateAppointmentRequest()){
                    switch(getViewMode()){
                        case CREATE:
                            doCreateAppointmentRequest();
                            break;
                        case UPDATE:
                            doUpdateAppointmentRequest();
                            break;
                    }
                }
                break;
            case REQUEST_TREATMENT_SELECTION:
                doTreatmentSelectionRequest();
                break;
            case REQUEST_CLOSE_VIEW:
                int reply = JOptionPane.showInternalConfirmDialog(this, 
                        "<html><center>Any outstanding appointment start and duration "
                                + "changes will not be saved</center>"
                                + "<center>Close view anyway?</center></html>",
                        "Confirm view closure", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                    doCloseViewRequest();
                break;   
        }
    }
    
    private boolean doCreateUpdateAppointmentRequest(){
        //int OKToSaveAppointment = JOptionPane.YES_OPTION;
        //evt = null;
        boolean OKToSaveAppointment = false;
        if (this.cmbSelectPatient.getSelectedIndex()!=-1){
            initialiseEntityDescriptorFromView();
            if (getMyController().getDescriptor().getViewDescription().getAppointment().getPatient()== null){
                JOptionPane.showInternalMessageDialog(this, 
                        "A patient has not been selected for this appointment",
                        "View error", JOptionPane.WARNING_MESSAGE);
            }
            else if (getMyController().getDescriptor().getViewDescription().getAppointment().getDuration().isZero()){
                actionEvent = new ActionEvent(
                        this, ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent
                                .APPOINTMENTS_FOR_DAY_REQUEST.toString());
                getMyController().actionPerformed(actionEvent); 
                JOptionPane.showInternalMessageDialog(this, 
                        "Defined duration for appointment must be longer than zero minutes",
                        "View error", JOptionPane.WARNING_MESSAGE);
            }
            else OKToSaveAppointment = true;
        }  
        else{
            JOptionPane.showInternalMessageDialog(this, 
                    "A patient has not been selected for this appointment",
                    "View error", JOptionPane.WARNING_MESSAGE);
        }
        return OKToSaveAppointment;
    }
    
    private ActionEvent actionEvent = null;
    private void doCreateAppointmentRequest(){
        if (doCreateUpdateAppointmentRequest()){
            actionEvent = new ActionEvent(
                    this, ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            APPOINTMENT_EDITOR_CREATE_REQUEST.toString());
            getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void doUpdateAppointmentRequest(){
        if (doCreateUpdateAppointmentRequest()){
            actionEvent = new ActionEvent(
                    this, ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            APPOINTMENT_EDITOR_UPDATE_REQUEST.toString());
            getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void doTreatmentSelectionRequest(){
        actionEvent = new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void doCloseViewRequest(){
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ScheduleViewControllerPropertyChangeEvent propertyName =
                ViewController.ScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            case APPOINTMENT_SCHEDULE_ERROR_RECEIVED:
                String error = getMyController().getDescriptor().
                        getControllerDescription().getError();
                JOptionPane.showInternalMessageDialog(this,error,
                        "View controller error",JOptionPane.WARNING_MESSAGE);
                break;
            case CLOSE_VIEW_REQUEST_RECEIVED:
                doCloseViewRequest();
                break;
        }
    }
    
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
        this.setClosable(true);
        //initialiseViewMode();
        
        this.setVisible(true);
        this.setSize(this.getWidth(), 400);
        makeSelectSettingsMenu();
        mbrView = new JMenuBar();
        mbrView.add(this.mnuSelectSettings);
        setJMenuBar(mbrView);
        
        setViewMode(getMyController().getDescriptor()
                .getControllerDescription().getViewMode());
        //this.btnCloseView.setText(this.CLOSE_CAPTION);
        
        //this.btnSelectTreatment.setActionCommand(Action.REQUEST_TREATMENT_SELECTION.toString());
        //this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnSaveChanges.setActionCommand(Action.REQUEST_CREATE_UPDATE_APPOINTMENT.toString());
        //this.btnSelectTreatment.setActionCommand(Action.REQUEST_TREATMENT_SELECTION.toString());
        //this.btnCloseView.removeActionListener(this);
        //this.btnCloseView.addActionListener(this);
        this.btnSaveChanges.addActionListener(this);
        //this.btnSelectTreatment.addActionListener(this);
                
        setBorderTitles(BorderTitles.SCHEDULE_DETAILS);        
        setBorderTitles(BorderTitles.START_DURATION); 
        
        LocalDate day = getMyController().getDescriptor().getViewDescription().getScheduleDay();
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);
        
        
        /**
         * 10/02/24
         */
        setViewMode(
                getMyController()
                        .getDescriptor()
                        .getControllerDescription().getViewMode());
        
        populatePatientSelector(this.cmbSelectPatient);
        this.cmbSelectPatient.setEditable(false);
        
        this.cmbSelectStartTime.setSelectedItem(
                getMyController().getDescriptor().getControllerDescription().getAppointment().getStart());
        this.spnDurationHours.setValue(getHoursFromDuration(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getDuration().toMinutes()));
        
        this.cmbSelectPatient.setSelectedItem(getMyController().getDescriptor()
            .getControllerDescription().getAppointment().getPatient());       

        /*28/03/2024PatientNote patientNote = getMyController().getDescriptor().
            getControllerDescription().getAppointment().getPatientNote();*/

        this.setTitle("Apppointment editor for " + day.format(ddMMyyyyFormat));
        
        this.setLayer(JLayeredPane.MODAL_LAYER);
    }
    
    /**
     * the method process
     * -- collects data about appointment (start, duration, notes)
     * -- collects data about appointee (the patient)
     */
    private void initialiseEntityDescriptorFromView(){
        //get the appointment with  which the view was initialised (in particular the appointment key)
        /*28/03/2024PatientNote patientNote = getMyController().getDescriptor().
                            getControllerDescription().getAppointment().getPatientNote();*/
        getMyController().getDescriptor().getViewDescription().setAppointment(
                    getMyController().getDescriptor().
                            getControllerDescription().getAppointment());

        getMyController().getDescriptor()
                .getViewDescription()
                .getAppointment()
                .setPatient((Patient)this.cmbSelectPatient.getSelectedItem());
        
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setStart((LocalDateTime)this.cmbSelectStartTime.getSelectedItem());
        getMyController().getDescriptor().getViewDescription().getAppointment().
                setDuration(getDurationFromView());
        /*getMyController().getDescriptor().getViewDescription().getAppointment().
                setNotes(this.txaNotepad.getText());*/
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
        /*28/03/2024this.txaNotepad.setText(
                getMyController()
                        .getDescriptor()
                        .getControllerDescription()
                        .getAppointment().getPatientNote().getNote());*/
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
        selector.setModel(model);
        if (getViewMode().equals(ViewController.ViewMode.UPDATE))
            model.addElement(getMyController()
                    .getDescriptor()
                    .getControllerDescription().getAppointment().getPatient());
        else{
            ArrayList<Patient> patients = 
                getMyController().getDescriptor().getControllerDescription().getPatients();
            Iterator<Patient> it = patients.iterator();
            while (it.hasNext()){
                Patient patient = it.next();
                model.addElement(patient);
            }
            selector.setSelectedIndex(-1);
        }
    }
    
    private ViewController.ViewMode getViewMode(){
        return viewMode;
    }
    
    enum BorderTitles{
        SCHEDULE_DETAILS,
        START_DURATION
    };
    
    private void setBorderTitles(BorderTitles borderTitles){
        JPanel panel = null;
        String caption = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case START_DURATION:
                panel = pnlStartAndDuration;
                caption = PANEL_START_DURATION_CAPTION;
                isPanelBackgroundDefault = true;
                break;
            case SCHEDULE_DETAILS:
                panel = pnlScheduleDetails;
                caption = this.PANEL_SCHEDULE_DETAILS_CAPTION;
                isPanelBackgroundDefault = true;
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
    
    private void setViewMode(ViewController.ViewMode value){
        viewMode = value;
        switch(viewMode){
            case CREATE:
                this.btnSaveChanges.setText(CREATE_CAPTION);
                //this.btnSelectTreatment.setEnabled(false);
                break;
            case UPDATE:
                this.btnSaveChanges.setText(UPDATE_CAPTION);
               //this.btnSelectTreatment.setEnabled(true);
                break;
        }
        
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cmbSelectPatient = new javax.swing.JComboBox<Patient>();
        pnlScheduleDetails = new javax.swing.JPanel();
        pnlStartAndDuration = new javax.swing.JPanel();
        cmbSelectStartTime = cmbSelectStartTime = new javax.swing.JComboBox<LocalDateTime>();
        spnDurationHours = new javax.swing.JSpinner();
        spnDurationMinutes = new javax.swing.JSpinner(new SpinnerNumberModel(0,0,55,5));
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnSaveChanges = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbSelectPatient.setModel(new javax.swing.DefaultComboBoxModel<Patient>());

        pnlScheduleDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule details"));

        pnlStartAndDuration.setBorder(javax.swing.BorderFactory.createTitledBorder("Start & duration"));

        cmbSelectStartTime.setModel(new javax.swing.DefaultComboBoxModel<LocalDateTime>());

        jLabel2.setText("Start");

        jLabel3.setText("Hours");

        jLabel4.setText("Minutes");

        javax.swing.GroupLayout pnlStartAndDurationLayout = new javax.swing.GroupLayout(pnlStartAndDuration);
        pnlStartAndDuration.setLayout(pnlStartAndDurationLayout);
        pnlStartAndDurationLayout.setHorizontalGroup(
            pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStartAndDurationLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlStartAndDurationLayout.setVerticalGroup(
            pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStartAndDurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSelectStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDurationHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(pnlStartAndDurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnDurationMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlScheduleDetailsLayout = new javax.swing.GroupLayout(pnlScheduleDetails);
        pnlScheduleDetails.setLayout(pnlScheduleDetailsLayout);
        pnlScheduleDetailsLayout.setHorizontalGroup(
            pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(pnlStartAndDuration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        pnlScheduleDetailsLayout.setVerticalGroup(
            pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlStartAndDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Patient");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlScheduleDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 30, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(pnlScheduleDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnSaveChanges.setText("Update start & duration times");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSaveChanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnSaveChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSaveChanges;
    private javax.swing.JComboBox<Patient> cmbSelectPatient;
    private javax.swing.JComboBox<LocalDateTime> cmbSelectStartTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel pnlScheduleDetails;
    private javax.swing.JPanel pnlStartAndDuration;
    private javax.swing.JSpinner spnDurationHours;
    private javax.swing.JSpinner spnDurationMinutes;
    // End of variables declaration//GEN-END:variables

}
