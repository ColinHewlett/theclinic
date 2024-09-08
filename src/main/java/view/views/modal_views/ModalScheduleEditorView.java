/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */

package view.views.modal_views;

import com.github.lgooddatepicker.components.DatePickerSettings;
import model.entity.Patient;
import model.entity.Appointment;
import view.views.view_support_classes.renderers.SelectStartTimeLocalDateTimeRenderer;
import view.views.non_modal_views.DesktopView;
import controller.Descriptor;
import controller.ViewController;
import static controller.ViewController.ViewMode.EMERGENCY;
import view.View;
/*28/03/2024import model.PatientNote;*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import view.views.view_support_classes.AppointmentDateVetoPolicy;

/**
 *
 * @author colin
 */
public class ModalScheduleEditorView extends ModalView implements ActionListener {
    private final String SETTINGS = "Settings";
    private final String FIRST_APPOINTMENT_START_TIME = "First appointment start time";
    private final String LAST_APPOINTMENT_START_TIME = "Last appointment start time";
    private final String EXIT_VIEW = "Close view";
    private final String CLOSE_CAPTION = "<html><center>Close</center><center>view</center></html>";
    private final String CREATE_CAPTION = "<html><center>Create</center><center>appointment</center></html>";
    private final String EMERGENCY_CAPTION = "<html>Create emergency appointment</html>";
    private final String UPDATE_CAPTION = "<html><center>Update</center><center>appointment</center></html>";
    private final String TREATMENT_CAPTION = "<html><center>Select</center><center>treatment</center></html>";
    private final String PANEL_START_DURATION_CAPTION = "Start & duration";
    private final String PANEL_SCHEDULE_DETAILS_CAPTION = "Schedule details";
    private JMenuBar mbrView = null;
    private JMenu mnuScheduleEditorMenu = null; 
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
    public ModalScheduleEditorView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg) {
        setTitle("Appointment configuration view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
        //initComponents();
    }
    
    private void makeSettingsAndCloseViewMenu(){
        switch(getViewMode()){
            case CREATE:
            case UPDATE:
                this.mnuScheduleEditorMenu = new JMenu("Settings & close view");
                this.mniFirstAppointmentStartTime = new JMenuItem(FIRST_APPOINTMENT_START_TIME);
                this.mniLastAppointmentStartTime = new JMenuItem(LAST_APPOINTMENT_START_TIME);
                mniFirstAppointmentStartTime.setActionCommand(Action.REQUEST_FIRST_APPOINTMENT_START_TIME_UPDATE.toString());
                mniLastAppointmentStartTime.setActionCommand(Action.REQUEST_LAST_APPOINTMENT_START_TIME_UPDATE.toString());
                mniFirstAppointmentStartTime.addActionListener(this);
                mniLastAppointmentStartTime.addActionListener(this);
                mnuScheduleEditorMenu.add(mniFirstAppointmentStartTime);
                mnuScheduleEditorMenu.add(mniLastAppointmentStartTime);
                mnuScheduleEditorMenu.addSeparator();
                break;
            case EMERGENCY:
                this.mnuScheduleEditorMenu = new JMenu("Close view");
                break;
        }
        mniExitView = new JMenuItem(EXIT_VIEW);
        mniExitView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        mniExitView.addActionListener(this);
        mnuScheduleEditorMenu.add(mniExitView);
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
            REQUEST_CREATE_UPDATE_EMERGENCY_APPOINTMENT,
            REQUEST_FIRST_APPOINTMENT_START_TIME_UPDATE,
            REQUEST_LAST_APPOINTMENT_START_TIME_UPDATE,
            REQUEST_TREATMENT_SELECTION,
            REQUEST_CLOSE_VIEW};
    
    @Override 
    public void actionPerformed(ActionEvent e){
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CREATE_UPDATE_EMERGENCY_APPOINTMENT:
                if (isOKToSendAppointmentRequest()){
                    switch(getViewMode()){
                        case CREATE:
                            doCreateAppointmentRequest();
                            break;
                        case UPDATE:
                            doUpdateAppointmentRequest();
                            break;
                        case EMERGENCY:
                            doEmergencyAppointmentRequest();
                            break;
                    }
                }
                break;
            case REQUEST_FIRST_APPOINTMENT_START_TIME_UPDATE:
                mniFirstAppointmentStartTimeActionPerformed();
                break;
            case REQUEST_LAST_APPOINTMENT_START_TIME_UPDATE:
                mniLastAppointmentStartTimeActionPerformed();
                break;
            case REQUEST_CLOSE_VIEW:
                /*
                int reply = JOptionPane.showInternalConfirmDialog(this, 
                        "Outstanding appointment changes will not be saved.\n"
                                + "Close view anyway?",
                        "Confirm view closure", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                    doCloseViewRequest();
                */
                doCloseViewRequest();
                break;   
        }
    }
    
    private boolean isOKToSendAppointmentRequest(){
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
            else {
                
                OKToSaveAppointment = true;
            }
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
        if (isOKToSendAppointmentRequest()){
            actionEvent = new ActionEvent(
                    this, ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            SCHEDULE_EDITOR_CREATE_APPOINTMENT_REQUEST.toString());
            getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void doUpdateAppointmentRequest(){
        actionEvent = new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        SCHEDULE_EDITOR_UPDATE_APPOINTMENT_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void doEmergencyAppointmentRequest(){
        actionEvent = new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    /*
    private void doTreatmentSelectionRequest(){
        actionEvent = new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }*/

    
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
        this.setVisible(true);
        this.setSize(this.getWidth(), 400);
        
        setViewMode(getMyController().getDescriptor()
                .getControllerDescription().getViewMode());
        
        makeSettingsAndCloseViewMenu();
        mbrView = new JMenuBar();
        mbrView.add(this.mnuScheduleEditorMenu);
        setJMenuBar(mbrView);
        
        

        this.btnCreateUpdateChanges.setActionCommand(Action.REQUEST_CREATE_UPDATE_EMERGENCY_APPOINTMENT.toString());
        this.btnCreateUpdateChanges.addActionListener(this);
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnCloseView.addActionListener(this);
                
        setBorderTitles(BorderTitles.SCHEDULE_DETAILS);        
        setBorderTitles(BorderTitles.START_DURATION); 
        
        LocalDate day = getMyController().getDescriptor().getViewDescription().getScheduleDay();
        this.cmbSelectStartTime.setMaximumRowCount(20);
        this.cmbSelectStartTime.setRenderer(new SelectStartTimeLocalDateTimeRenderer());
        populateSelectStartTime(day);
        
        populatePatientSelector(this.cmbSelectPatient);
        this.cmbSelectPatient.setEditable(false);
        
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dayDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        
        DatePickerSettings dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(new AppointmentDateVetoPolicy(getMyController().getDescriptor().getControllerDescription().getSurgeryDaysAssignment()));
        dayDatePicker.setDate(getMyController().getDescriptor().getControllerDescription().getAppointment().getStart().toLocalDate());
        this.cmbSelectStartTime.setSelectedItem(
                getMyController().getDescriptor().getControllerDescription().getAppointment().getStart());
        this.spnDurationHours.setValue(getHoursFromDuration(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getDuration().toMinutes()));
        this.spnDurationMinutes.setValue(getMinutesFromDuration(getMyController().getDescriptor()
                .getControllerDescription().getAppointment().getDuration().toMinutes()));
        
        this.cmbSelectPatient.setSelectedItem(getMyController().getDescriptor()
            .getControllerDescription().getAppointment().getPatient());       
        
        
        this.setLayer(JLayeredPane.MODAL_LAYER);
    }
    
    private void setAppointment(){
        Appointment appointment = null;
        switch (getViewMode()){
            case CREATE:
            case EMERGENCY:
                appointment = new Appointment();
                appointment.setPatient((Patient)this.cmbSelectPatient.getSelectedItem());
                break;
            case UPDATE:
                appointment = getMyController().getDescriptor().
                            getControllerDescription().getAppointment();
                break;
        }
        
        LocalTime time = ((LocalDateTime)this.cmbSelectStartTime.getSelectedItem()).toLocalTime();
        LocalDateTime start = LocalDateTime.of(dayDatePicker.getDate(),time);
        appointment.setStart(start);
        appointment.setDuration(getDurationFromView());
        getMyController().getDescriptor().getViewDescription()
                .setAppointment(appointment);
    }
    
    /**
     * the method process
     * -- collects data about appointment (start, duration, notes)
     * -- collects data about appointee (the patient)
     * 
     */
    private void initialiseEntityDescriptorFromView(){
        /**
         * 26/06/2024 05:32 update
         */
        setAppointment();
        /* setAppointment() replaces initialiseEntityDescriptorFromView() contents
        //get the appointment with  which the view was initialised (in particular the appointment key)
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
                setDuration(getDurationFromView());*/
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
        LocalDate day = getMyController().getDescriptor()
                .getViewDescription().getScheduleDay();
        viewMode = value;
        switch(viewMode){
            case CREATE:
                this.btnCreateUpdateChanges.setText(CREATE_CAPTION);
                this.setTitle("Create new apppointment editor for " + day.format(ddMMyyyyFormat));
                break;
            case UPDATE:
                this.btnCreateUpdateChanges.setText(UPDATE_CAPTION);
                this.setTitle("Update apppointment editor for " + day.format(ddMMyyyyFormat));
                break;
            case EMERGENCY:
                this.btnCreateUpdateChanges.setText(EMERGENCY_CAPTION);
                this.setTitle("Emergency apppointment editor for " + day.format(ddMMyyyyFormat));
                break;
        }
        
    }
    private ViewController.ViewMode getViewMode(){
        return viewMode;
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
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnCreateUpdateChanges = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

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
                .addContainerGap())
        );

        DatePickerSettings settings = new DatePickerSettings();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowEmptyDates(false);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);

        javax.swing.GroupLayout pnlScheduleDetailsLayout = new javax.swing.GroupLayout(pnlScheduleDetails);
        pnlScheduleDetails.setLayout(pnlScheduleDetailsLayout);
        pnlScheduleDetailsLayout.setHorizontalGroup(
            pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScheduleDetailsLayout.createSequentialGroup()
                .addGroup(pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlScheduleDetailsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(pnlStartAndDuration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18))
        );
        pnlScheduleDetailsLayout.setVerticalGroup(
            pnlScheduleDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScheduleDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlStartAndDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                        .addGap(0, 10, Short.MAX_VALUE)
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
                .addGap(12, 12, 12)
                .addComponent(pnlScheduleDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCreateUpdateChanges.setText("<html><center>Update</center><center>schedule</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreateUpdateChanges, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnCreateUpdateChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateChanges;
    private javax.swing.JComboBox<Patient> cmbSelectPatient;
    private javax.swing.JComboBox<LocalDateTime> cmbSelectStartTime;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlScheduleDetails;
    private javax.swing.JPanel pnlStartAndDuration;
    private javax.swing.JSpinner spnDurationHours;
    private javax.swing.JSpinner spnDurationMinutes;
    // End of variables declaration//GEN-END:variables

}
