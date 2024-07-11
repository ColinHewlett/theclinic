/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package view.views.non_modal_views;
package patient_view_original_class;
import model.non_entity.SystemDefinition;
import model.non_entity.SystemDefinition.ScheduleSlotType;
import model.non_entity.SystemDefinition.ScheduleViewActionCaption;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.AppointmentsTablePatientRenderer;
import view.views.view_support_classes.renderers.ScheduleTableCellRenderer;
/*28/03/2024import view.views.view_support_classes.renderers.AppointmentsTablePatientNoteRenderer;*/
import view.views.view_support_classes.models.AppointmentScheduleTableModel;
import model.entity.Appointment;
import model.entity.Patient;
/*28/03/2024import model.PatientNote;*/
import controller.ViewController;
import view.views.view_support_classes.AppointmentDateVetoPolicy;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;
import view.views.view_support_classes.models.EmptySlotAvailability2ColumnTableModel;
import view.View;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.HashMap;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JOptionPane;
import javax.swing.JButton; 
import javax.swing.JTable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;



/**
 *
 * @author colin
 */
public class ScheduleViewx extends View 
        implements ActionListener, ListSelectionListener{
    private enum COLUMN{From,Duration,Patient,Notes};
    enum Action{
        REQUEST_CREATE_UPDATE_APPOINTMENT,
        REQUEST_EMERGENCY_APPOINTMENT,
        REQUEST_CANCEL_APPOINTMENT,
        REQUEST_CLINICAL_NOTE,
        REQUEST_UNBOOKABLE_SLOT,
        REQUEST_PRINT_SCHEDULE,
        REQUEST_CLOSE_VIEW
    }
    
    private AppointmentScheduleTableModel tableModel = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private DatePickerSettings settings = null;
    private ArrayList<Appointment> appointments = null;
    //private final DateTimeFormatter emptySlotStartFormat = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm (EEE)");
    private final DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private AppointmentDateVetoPolicy vetoPolicy = null;
    
    private int mGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels = 29;
    private int wGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels = 40;
    private int mAppointmentDayScheduleWidth = 742;
    private int wAppointmentDayScheduleWidth = 752;

    enum UnbookableSlotMode{MARK,CANCEL,NONE};
    private UnbookableSlotMode unbookableSlotMode = null;
    private void setUnbookableSlotMode(UnbookableSlotMode value){
        unbookableSlotMode = value;
        switch (unbookableSlotMode){
            case MARK:
                btnMarkCancelSlotUnbookable.setText(ScheduleViewActionCaption.MARK_CANCEL_UNBOOKABLE_SLOT._1());
                btnMarkCancelSlotUnbookable.setEnabled(true);
                break;
            case CANCEL:
                btnMarkCancelSlotUnbookable.setText(ScheduleViewActionCaption.MARK_CANCEL_UNBOOKABLE_SLOT._2());
                btnMarkCancelSlotUnbookable.setEnabled(true);
                break;
            case NONE:
                btnMarkCancelSlotUnbookable.setEnabled(false);
                break;
                
        }
    }
    private UnbookableSlotMode getUnbookableSlotMode(){
        return unbookableSlotMode;
    }
    
    enum EmergencySlotMode{MAKE,DELETE,NONE};
    private EmergencySlotMode emergencySlotMode = null;
    private void setEmergencySlotMode(EmergencySlotMode value){
        emergencySlotMode = value;
        switch (emergencySlotMode){
            case MAKE:
                btnMakeDeleteEmergencyAppointment.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT._1());
                btnMakeDeleteEmergencyAppointment.setEnabled(true);
                break;
            case DELETE:
                btnMakeDeleteEmergencyAppointment.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT._2());
                btnMakeDeleteEmergencyAppointment.setEnabled(true);
                break;
            case NONE:
                btnMakeDeleteEmergencyAppointment.setEnabled(false);
                break;
                
        }
    }
    private EmergencySlotMode getEmergencySlotMode(){
        return emergencySlotMode;
    }
    
    enum AppointmentMode{
            CREATE,
            UPDATE,
            NONE;
    }
   
    private AppointmentMode appointmentMode = null;
    private void setAppointmentMode(AppointmentMode value){
        appointmentMode = value;
        
        switch(appointmentMode){
            case CREATE:
                btnCreateUpdateAppointment.setText(
                        ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._1());
                btnCreateUpdateAppointment.setEnabled(true);
                break;
            case UPDATE:
                btnCreateUpdateAppointment.setText(
                        ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._2());
                btnCreateUpdateAppointment.setEnabled(true);
                break;
            case NONE:
                btnCreateUpdateAppointment.setEnabled(false);
                break;
        }
    }
    private AppointmentMode getAppointmentMode(){
        return appointmentMode;
    }
    
    
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblAppointments.getSelectedRow();
            if (selectedRow!=-1){
                AppointmentScheduleTableModel model = 
                        (AppointmentScheduleTableModel)tblAppointments.getModel();
                Appointment appointment = model.getElementAt(selectedRow);
                setScheduleSlotType(appointment);
                getMyController().getDescriptor()
                        .getViewDescription().setAppointment(appointment);
                tableValueChangedListenerActivated = true;
                Patient patient = (Patient)tblAppointments.getModel().getValueAt(selectedRow, 0);
                doScheduleTitleRefresh(patient);
                switch(getScheduleSlotType()){
                    case BOOKABLE_SCHEDULE_SLOT:
                        setAppointmentMode(AppointmentMode.CREATE);
                        setUnbookableSlotMode(UnbookableSlotMode.MARK);
                        setEmergencySlotMode(EmergencySlotMode.NONE);
                        btnCancelSelectedAppointment.setEnabled(false);
                        btnClinicalNotesForSelectedAppointment.setEnabled(false);
                        btnSelectTreatmentRequest.setEnabled(false);
                        break;
                    case UNBOOKABLE_SCHEDULE_SLOT:
                        setAppointmentMode(AppointmentMode.NONE);
                        setUnbookableSlotMode(UnbookableSlotMode.CANCEL);
                        setEmergencySlotMode(EmergencySlotMode.NONE);
                        btnCancelSelectedAppointment.setEnabled(false);
                        btnClinicalNotesForSelectedAppointment.setEnabled(false);
                        btnSelectTreatmentRequest.setEnabled(false);
                        break;
                    case EMERGENCY_SCHEDULE_SLOT:
                        setAppointmentMode(AppointmentMode.NONE);
                        setEmergencySlotMode(EmergencySlotMode.DELETE);
                        setUnbookableSlotMode(UnbookableSlotMode.NONE);
                        btnCancelSelectedAppointment.setEnabled(false);
                        btnClinicalNotesForSelectedAppointment.setEnabled(true);
                        btnSelectTreatmentRequest.setEnabled(false);
                        break;
                    case BOOKED_SCHEDULE_SLOT:
                        setAppointmentMode(AppointmentMode.UPDATE);
                        setEmergencySlotMode(EmergencySlotMode.MAKE);
                        setUnbookableSlotMode(UnbookableSlotMode.NONE);
                        btnCancelSelectedAppointment.setEnabled(true);
                        btnClinicalNotesForSelectedAppointment.setEnabled(true);
                        btnSelectTreatmentRequest.setEnabled(true);
                        break;
                }
            }
            else {
                getMyController().getDescriptor()
                        .getViewDescription().setAppointment(null);
                doScheduleTitleRefresh(null);
                
                btnCreateUpdateAppointment.setEnabled(false);
                btnMarkCancelSlotUnbookable.setEnabled(false);
                btnCancelSelectedAppointment.setEnabled(false);
                btnClinicalNotesForSelectedAppointment.setEnabled(false);
                btnSelectTreatmentRequest.setEnabled(false);
                btnMakeDeleteEmergencyAppointment.setEnabled(false);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CREATE_UPDATE_APPOINTMENT:
                switch(getAppointmentMode()){
                    case CREATE:
                        btnCreateAppointmentActionPerformed(e);
                        break;
                    case UPDATE:
                        btnUpdateAppointmentActionPerformed(e);
                        break;
                    case NONE:
                        break;
                }
                break;
            case REQUEST_EMERGENCY_APPOINTMENT:
                switch(getScheduleSlotType()){
                    case EMERGENCY_SCHEDULE_SLOT:
                        deleteEmergencyAppointment();
                        break;
                    case BOOKED_SCHEDULE_SLOT:
                        makeEmergencyAppointment();
                        break;
                }
                break;
            case REQUEST_CANCEL_APPOINTMENT:
                btnCancelSelectedAppointmentActionPerformed(e);
                break;
            case REQUEST_CLINICAL_NOTE:
                doClinicNoteRequest();
                break;
            case REQUEST_UNBOOKABLE_SLOT:
                btnMarkSlotUnbookableActionPerformed(e);
                break;
            case REQUEST_PRINT_SCHEDULE:
                getMyController().getDescriptor().getViewDescription().setScheduleDay(dayDatePicker.getDate());
                ActionEvent actionEvent = new ActionEvent(ScheduleViewx.this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.PRINT_SCHEDULE_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case REQUEST_CLOSE_VIEW:
                btnCloseViewActionPerformed(e);
                break;
        }
    }
    
    /**
     * 
     * @param e PropertyChangeEvent which supports the following properties
     * --
     */ 
    @Override
    public void propertyChange(PropertyChangeEvent e){
        String tableTitleDay = null;
                String tableTitleDuration = null;
        TitledBorder titledBorder = (TitledBorder)this.pnlSlotAvailability.getBorder();
        ViewController.ScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.ScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        //setViewDescriptor((Descriptor)e.getNewValue());
        switch (propertyName){
            case APPOINTMENTS_FOR_DAY_RECEIVED:
                populateAppointmentsForDayTable();
                tblAppointments.clearSelection();
                break;
            case SURGERY_DAYS_ASSIGNMENT_RECEIVED:
                updateDatePickerSettings();
                break;
            case NON_SURGERY_DAY_EDIT_RECEIVED:
                temporarilySuspendDatePickerDateVetoPolicy(getMyController().getDescriptor().getViewDescription().getScheduleDay());
                break;
            case NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED://instruction to clear list
                titledBorder.setTitle("Available appointment slots");
                populateEmptySlotAvailabilityTable(new ArrayList<>());
                break;
            case APPOINTMENT_SLOTS_FROM_DAY_RECEIVED:
                tableTitleDuration = new AppointmentsTableDurationRenderer().renderDuration(
                        getMyController().getDescriptor().getControllerDescription().
                                getEmptySlotMinimumDuration());
                tableTitleDay = getMyController().getDescriptor().getControllerDescription().
                                getEmptySlotFromDay().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        //getAppointment().getStart().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                titledBorder = (TitledBorder)this.pnlSlotAvailability.getBorder();
                titledBorder.setTitle("Slot availability from " + tableTitleDay 
                        + " for a duration of " + tableTitleDuration);
                populateEmptySlotAvailabilityTable(
                        getMyController().getDescriptor().getControllerDescription().getAppointmentSlots());
                /**
                 * without the next lines the appointments table is unconscious of being selected?!)
                 */
                getMyController().getDescriptor().getViewDescription().setScheduleDay(dayDatePicker.getDate());
                //refreshAppointmentTableWithCurrentlySelectedDate();
                break;
            case APPOINTMENT_SCHEDULE_ERROR_RECEIVED:
                populateEmptySlotAvailabilityTable(getMyController().getDescriptor().getControllerDescription().getAppointments());
                break;
                
        }
    }
    
    private void updateDatePickerSettings(){
        DatePickerSettings dps = null;
        //this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().getViewDescription().getSurgeryDaysAssignmentValue());
        this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().
                getControllerDescription().getSurgeryDaysAssignment());
        dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
    }
    
    private void temporarilySuspendDatePickerDateVetoPolicy(LocalDate day){
        DatePickerSettings dps = dayDatePicker.getSettings();
        HashMap<DayOfWeek, Boolean> allDaysSurgeryDays = new HashMap<>();
        allDaysSurgeryDays.put(DayOfWeek.MONDAY, true);
        allDaysSurgeryDays.put(DayOfWeek.TUESDAY, true);
        allDaysSurgeryDays.put(DayOfWeek.WEDNESDAY, true);
        allDaysSurgeryDays.put(DayOfWeek.THURSDAY, true);
        allDaysSurgeryDays.put(DayOfWeek.FRIDAY, true);
        allDaysSurgeryDays.put(DayOfWeek.SATURDAY, true);
        allDaysSurgeryDays.put(DayOfWeek.SUNDAY, true);
        dps.setVetoPolicy(new AppointmentDateVetoPolicy(allDaysSurgeryDays));
        dayDatePicker.setDate(day);
        refreshAppointmentTableWithCurrentlySelectedDate();
        dps.setVetoPolicy(vetoPolicy);
    }
    
    @Override
    public void initialiseView(){
        try{
            initComponentsx();
            addInternalFrameListeners();
        
            setVisible(true);
            setTitle("Appointments");
            setClosable(false);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(true);
            setSelected(true);
            setSize(975
                    ,580);
            toFront();
        }catch (PropertyVetoException ex){
            
        }catch(Exception ex){
            String message = ex.getMessage() + "\n"
                    + "Raised in ScheduleView::initialiseView()";
            ViewController.displayErrorMessage(message, 
                    "Schedule view controller error", JOptionPane.WARNING_MESSAGE);
        }

        setEmptySlotAvailabilityTableListener();
        setAppointmentTableListener();
        
        this.btnClinicalNotesForSelectedAppointment
                .setActionCommand(Action.REQUEST_CLINICAL_NOTE.toString());
        this.btnClinicalNotesForSelectedAppointment.addActionListener(this);
        
        btnCreateUpdateAppointment.setEnabled(false);
        btnMakeDeleteEmergencyAppointment.setEnabled(false);
        btnMarkCancelSlotUnbookable.setEnabled(false);
        btnCancelSelectedAppointment.setEnabled(false);
        btnClinicalNotesForSelectedAppointment.setEnabled(false);
        btnSelectTreatmentRequest.setEnabled(false);
        btnMakeDeleteEmergencyAppointment.setEnabled(false);
        btnCloseView.setEnabled(true);
        
        btnCreateUpdateAppointment.setText(ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._1());
        btnMakeDeleteEmergencyAppointment.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT._1());
        btnMarkCancelSlotUnbookable.setText(ScheduleViewActionCaption.MARK_CANCEL_UNBOOKABLE_SLOT._1());
        btnCancelSelectedAppointment.setText(ScheduleViewActionCaption.CANCEL_APPOINTMENT._1());
        btnClinicalNotesForSelectedAppointment.setText(ScheduleViewActionCaption.CLINICAL_NOTES._1());
        btnSelectTreatmentRequest.setText(ScheduleViewActionCaption.SELECT_TREATMENT._1());       
        
        dayDatePicker.addDateChangeListener(new DayDatePickerChangeListener());
        this.mniCloseView.addActionListener((ActionEvent e) -> mniCloseViewActionPerformed(e));
        this.mniSelectNonSurgeryDay.addActionListener((ActionEvent e) -> mniSelectNonSurgeryDayActionPerformed(e)); 
        TitledBorder titledBorder = (TitledBorder)this.pnlSlotAvailability.getBorder();
        titledBorder.setTitle("Unscheduled appointment slots");

        this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().getControllerDescription().getSurgeryDaysAssignment());
        DatePickerSettings dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
        dps.setHighlightPolicy(new LeaveEditor());
     
        LocalDate day = getMyController().getDescriptor().getControllerDescription().getScheduleDay();
        if (!vetoPolicy.isDateAllowed(day)){
            switch(getMyController().
                    getDescriptor().
                    getControllerDescription().
                    getViewMode()){
            case SCHEDULE_REFERENCED_DESKTOP_VIEW:
                day = this.vetoPolicy.getNextAvailableDateTo(day);
                break;
            case SCHEDULE_REFERENCED_FROM_PATIENT_VIEW:
                HashMap<DayOfWeek, Boolean> allDaysSurgeryDays = new HashMap<>();
                allDaysSurgeryDays.put(DayOfWeek.MONDAY, true);
                allDaysSurgeryDays.put(DayOfWeek.TUESDAY, true);
                allDaysSurgeryDays.put(DayOfWeek.WEDNESDAY, true);
                allDaysSurgeryDays.put(DayOfWeek.THURSDAY, true);
                allDaysSurgeryDays.put(DayOfWeek.FRIDAY, true);
                allDaysSurgeryDays.put(DayOfWeek.SATURDAY, true);
                allDaysSurgeryDays.put(DayOfWeek.SUNDAY, true);
                dps.setVetoPolicy(new AppointmentDateVetoPolicy(allDaysSurgeryDays));
                dayDatePicker.setDate(day);
                //refreshAppointmentTableWithCurrentlySelectedDate();
                dps.setVetoPolicy(vetoPolicy);
        
        //dayDatePicker.setDate(day);
                break;
            }
        }
        dayDatePicker.setDate(day);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dayDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        
        refreshAppointmentTableWithCurrentlySelectedDate();
        
    }  
    
    /**
     * The abstract table model includes a getElementAt(int row) method which returns the element at the row defined index of an ArrayList<Descriptor.Appointment> maintained in the table model
     * @param row selected row in table
     * update 30/07/2021 19:53
     */
    private void initialiseEDRequestFromView(int row){
        getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));    
    }

    /**
     * 
     * @param myViewType; View.Viewer  which identifies the type of view this view is
     * -- enables the ViewController to identify which view is the sender of an ActionEvent to it
     * @param controller; ViewController object responsible for this View
     * -- enables access to the Descriptor settings created by the controller 
     * @param desktopView; DesktopView reference enabling this view 
     * -- to be added to the DesktopView object
     * -- and be centred in the desktopView
     */
    public ScheduleViewx(
            View.Viewer myViewType, 
            ViewController controller, 
            view.views.non_modal_views.DesktopView desktopView) {
        
        setTitle("Appointment schedule");
        this.setMyViewType(myViewType);
        setMyController(controller); 
        setDesktopView(desktopView);
   
    }
    
    private void mniCloseViewActionPerformed(ActionEvent e){
        this.btnCloseViewActionPerformed(e);
    }
    private void mniPrintAppointmentDayListActionPerformed(ActionEvent e){
        //code to output schedule to connected printer
        LocalDate day = dayDatePicker.getDate();
        MessageFormat header = new MessageFormat("Appointment schedule for "  + dayDatePicker.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "\n");
        //String header = MessageFormat.format("Appointment schedule for {0,date,short}", dayDatePicker.getDate());
        try {
            tblAppointments.print(JTable.PrintMode.FIT_WIDTH, header, null);
        } catch (java.awt.print.PrinterException ex) {
            System.err.format("Cannot print %s%n", ex.getMessage());
        }
    }
    private void mniSurgeryDaysEditorActionPerformed(ActionEvent e){
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.SURGERY_DAYS_EDITOR_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }

    private void mniSelectNonSurgeryDayActionPerformed(ActionEvent e){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doClinicNoteRequest(){
        Appointment appointment = getMyController().getDescriptor()
                .getViewDescription().getAppointment();
        if (appointment!=null){
            if(!appointment.getIsKeyDefined())
                appointment  = null;
            if(appointment.getIsUnbookableSlot())
                appointment = null;
        }
        if (appointment!=null){
            getMyController().getDescriptor().getViewDescription()
                    .setAppointment(appointment);
            ViewController.ScheduleViewControllerActionEvent request =
                    ViewController.ScheduleViewControllerActionEvent
                    .CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST;
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    request.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void setEmptySlotAvailabilityTableListener(){
        this.tblSlotAvailability.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblSlotAvailability.getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    doEmptySlotAvailabilityTableRowSelection(selectedRow);
                }
            }
        });
    }

    private ScheduleSlotType scheduleSlotType = null;
    private void setScheduleSlotType(Appointment appointment){
        if (appointment.getPatient()==null) 
            scheduleSlotType = ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
        else if(appointment.getPatient().toString().equals(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark()))
            scheduleSlotType = ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;
        else if(appointment.getIsEmergency())
            scheduleSlotType = ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
        else scheduleSlotType = ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
    }
    private ScheduleSlotType getScheduleSlotType(){
        return scheduleSlotType;
    }
    
    private boolean tableValueChangedListenerActivated = false;
    private void setAppointmentTableListener(){
        this.tblAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblAppointments.getSelectionModel();
        lsm.addListSelectionListener(this);
        tblAppointments.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblAppointments.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblAppointments.isRowSelected(selectedRow))
                    tblAppointments.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        
    }
    private void refreshAppointmentTableWithCurrentlySelectedDate(){
        ActionEvent actionEvent = new ActionEvent(ScheduleViewx.this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
        ScheduleViewx.this.getMyController().actionPerformed(actionEvent);
        SwingUtilities.invokeLater(new Runnable() 
        {
          public void run()
          {
            ScheduleViewx.this.setTitle(ScheduleViewx.this.getMyController().getDescriptor().getViewDescription().getScheduleDay().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
            setIsViewInitialised(true);       
          }
        });
    }
    
    private void doEmptySlotAvailabilityTableRowSelection(int row){
        Appointment appointment = 
                ((EmptySlotAvailability2ColumnTableModel)this.tblSlotAvailability.getModel()).getElementAt(row);
        LocalDate start = appointment.getStart().toLocalDate();
        DatePickerSettings dps = dayDatePicker.getSettings();
        if (!dps.getVetoPolicy().isDateAllowed(start)){
            temporarilySuspendDatePickerDateVetoPolicy(appointment.getStart().toLocalDate());
        }
        dayDatePicker.setDate(start);   
    }
    
    /**
     * configures and populates list of appointments for the selected day
     * -- adds a title for the table that contains the list which signifies
     * ---- the selected date for the list of appointments
     * ---- and the patient appointment reminder summARY
     * 
     */
    private void populateAppointmentsForDayTable(){
        ConfigureScheduleTable();
        doScheduleTitleRefresh(null);
        setTitle(getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
        
        this.pnlAppointmentScheduleForDay.repaint();
    } 

    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleViewx.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleViewx.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }
    
    private void initComponentsx()throws Exception{
        buttonGroup1 = new javax.swing.ButtonGroup();
        
        pnlAppointmentScheduleForDay = new javax.swing.JPanel();
        scrAppointmentsForDayTable = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        
        pnlSlotAvailability = new javax.swing.JPanel();
        scrPanelSlotAvailability = new javax.swing.JScrollPane();
        tblSlotAvailability = new javax.swing.JTable();
        
        pnlAppointmentDaySelection = new javax.swing.JPanel();
        btnSlotAvailabilityScannerRequest = new javax.swing.JButton();
        pnlAppointmentDaySelector = new javax.swing.JPanel();
        btnNowDay = new javax.swing.JButton("now");
        btnNextPracticeDay = new javax.swing.JButton(">>");
        btnPreviousPracticeDay = new javax.swing.JButton("<<");
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        
        pnlScheduleOperations = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnMakeDeleteEmergencyAppointment = new javax.swing.JButton();
        btnClinicalNotesForSelectedAppointment = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnMarkCancelSlotUnbookable = new javax.swing.JButton();
        btnSelectTreatmentRequest = new javax.swing.JButton("Select treatment");
        btnCloseView = new javax.swing.JButton("Close view");
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuOptions = new javax.swing.JMenu();
        mniViewCancelledAppointments = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniSurgeryDaysEditor = new javax.swing.JMenuItem();
        mniSelectNonSurgeryDay = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniPrintSchedule = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        
        
        //pnlSlotAvailability.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Available empty slots", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        //pnlSlotAvailability.setPreferredSize(new java.awt.Dimension(266, 146));
        //scrPanelSlotAvailability.setViewportView(tblSlotAvailability);
        
        pnlSlotAvailability.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Available empty slots", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));// NOI18N
        pnlSlotAvailability.setPreferredSize(new java.awt.Dimension(/*266*/450, 146));
        scrPanelSlotAvailability.setViewportView(tblSlotAvailability);
        
        pnlAppointmentDaySelection.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Appointment day selection", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor()));// NOI18N

        

        pnlAppointmentDaySelector.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlAppointmentDaySelector.setPreferredSize(new java.awt.Dimension(200, 58));

        btnNowDay.setText("now");
        btnNowDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNowDayActionPerformed(evt);
            }
        });

        btnNextPracticeDay.setText(">>");
        btnNextPracticeDay.setMinimumSize(new java.awt.Dimension(60, 23));
        btnNextPracticeDay.setPreferredSize(new java.awt.Dimension(62, 23));
        btnNextPracticeDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextPracticeDayActionPerformed(evt);
            }
        });

        btnPreviousPracticeDay.setText("<<");
        btnPreviousPracticeDay.setPreferredSize(new java.awt.Dimension(62, 23));
        btnPreviousPracticeDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousPracticeDayActionPerformed(evt);
            }
        });

        settings = new DatePickerSettings();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowEmptyDates(false);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);
        
        //btnSlotAvailabilityScannerRequest.setSize(200,23);
        btnSlotAvailabilityScannerRequest.setText("search for empty appointment slots");
        btnSlotAvailabilityScannerRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanForEmptySlotsActionPerformed(evt);
            }
        });
        
        pnlAppointmentScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), 
                "Appointment schedule for ", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));// NOI18N
        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(

        ));
        scrAppointmentsForDayTable.setViewportView(tblAppointments);
        
        pnlScheduleOperations.setBorder(javax.swing.BorderFactory.createTitledBorder(null, 
                "Schedule operations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));// NOI18N
        pnlScheduleOperations.setBackground(new Color(220,220,220));

        btnCreateUpdateAppointment.setText("<html><center>Create</center><center>appointment</center></html>");
        btnCreateUpdateAppointment.setToolTipText("");
        btnCreateUpdateAppointment.setActionCommand(Action.REQUEST_CREATE_UPDATE_APPOINTMENT.toString());
        btnCreateUpdateAppointment.addActionListener(this);
        
        /*
        btnCreateUpdateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAppointmentActionPerformed(evt);
            }
        });
        */
        btnMakeDeleteEmergencyAppointment.setText("<html><center>Emergency</center><center>appointment</center></html>");
        //btnUpdateSelectedAppointment.setToolTipText("update selected appointment");
        
        btnMakeDeleteEmergencyAppointment.setActionCommand(Action.REQUEST_EMERGENCY_APPOINTMENT.toString());
        btnMakeDeleteEmergencyAppointment.addActionListener(this);
        /*
        btnMakeDeleteEmergencyAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAppointmentActionPerformed(evt);
            }
        });
        */
        btnCancelSelectedAppointment.setText("<html><center>Cancel</center><center>appointment</center></html>");
        btnCancelSelectedAppointment.setToolTipText("cancel selected appointment");
        btnCancelSelectedAppointment.setActionCommand(Action.REQUEST_CANCEL_APPOINTMENT.toString());
        btnCancelSelectedAppointment.addActionListener(this);
        /*
        btnCancelSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedAppointmentActionPerformed(evt);
            }
        });
        */
        
        btnClinicalNotesForSelectedAppointment.setText(
                "<html><center>Clinical</center><center>notes</center></html>");

        btnMarkCancelSlotUnbookable.setText("<html><center>Mark slot</center><center>unbookable</center></html>");
        btnMarkCancelSlotUnbookable.setToolTipText("make a slot unbookable");
        btnMarkCancelSlotUnbookable.setActionCommand(Action.REQUEST_UNBOOKABLE_SLOT.toString());
        btnMarkCancelSlotUnbookable.addActionListener(this);
        
        /*
        btnMarkCancelSlotUnbookable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkSlotUnbookableActionPerformed(evt);
            }
        });*/
        
        btnSelectTreatmentRequest.setText("<html><center>Select</center><center>treatment</center></html>");
        btnSelectTreatmentRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectTreatmentActionPerformed(evt);
            }
        });

        btnCloseView.setText("Close view");
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCloseView.addActionListener(this);
        
        /*
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });
        */
        mnuOptions.setText("Actions");

        mniViewCancelledAppointments.setText("View cancelled appointments");
        mniViewCancelledAppointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniViewCancelledAppointmentsActionPerformed(evt);
            }
        });
        mnuOptions.add(mniViewCancelledAppointments);
        mnuOptions.add(jSeparator1);

        mniSurgeryDaysEditor.setText("Define which days are surgery days");
        mniSurgeryDaysEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSurgeryDaysEditorActionPerformed(evt);
            }
        });
        mnuOptions.add(mniSurgeryDaysEditor);

        mniSelectNonSurgeryDay.setText("Open schedule on a non-surgery day");
        mnuOptions.add(mniSelectNonSurgeryDay);
        mnuOptions.add(jSeparator2);

        mniPrintSchedule.setText("Print schedule selection");
        mniPrintSchedule.setActionCommand(Action.REQUEST_PRINT_SCHEDULE.toString());
        mniPrintSchedule.addActionListener(this);
        /*
        mniPrintSchedule.addActionListener(new java.awt.event.ActionListener() {
            
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPrintScheduleActionPerformed(evt);
            }
        });
        */
        mnuOptions.add(mniPrintSchedule);
        mnuOptions.add(jSeparator3);

        mniCloseView.setText("Close view");
        mnuOptions.add(mniCloseView);

        jMenuBar1.add(mnuOptions);

        setJMenuBar(jMenuBar1);
        
        
        
        
        
        
// <editor-fold defaultstate="collapsed" desc="AppointmentDaySelector panel layout">  
        javax.swing.GroupLayout pnlAppointmentDaySelectorLayout = new javax.swing.GroupLayout(pnlAppointmentDaySelector);
        pnlAppointmentDaySelector.setLayout(pnlAppointmentDaySelectorLayout);
        pnlAppointmentDaySelectorLayout.setHorizontalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, pnlAppointmentDaySelectorLayout.createSequentialGroup()
                                            .addComponent(btnPreviousPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(28,28,28)
                                            .addComponent(btnNowDay, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(28,28,28)
                                            .addComponent(btnNextPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    )
                                    .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            )
                            .addContainerGap()
                    )
        );
        pnlAppointmentDaySelectorLayout.setVerticalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10,10,10)
                            .addGroup(pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnNextPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnNowDay, javax.swing.GroupLayout.PREFERRED_SIZE, 23,javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnPreviousPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            )
                            .addContainerGap()
                    )
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Appointment day selection panel layout">
        javax.swing.GroupLayout pnlAppointmentDaySelectionLayout = new javax.swing.GroupLayout(pnlAppointmentDaySelection);
        pnlAppointmentDaySelection.setLayout(pnlAppointmentDaySelectionLayout);
        pnlAppointmentDaySelectionLayout.setHorizontalGroup(
                pnlAppointmentDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addGroup(pnlAppointmentDaySelectionLayout.createSequentialGroup()
                                //.addGap(22, 22, 22)
                                //.addContainerGap()
                                .addComponent(btnSlotAvailabilityScannerRequest)
                                //.addContainerGap()
                                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
                        .addGroup(pnlAppointmentDaySelectionLayout.createSequentialGroup()
                                //.addGap(15, 15, 15)
                                .addContainerGap()
                                .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                                .addContainerGap()
                                //.addGap(20, 20, 20)
                        )
        );
        pnlAppointmentDaySelectionLayout.setVerticalGroup(
            pnlAppointmentDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDaySelectionLayout.createSequentialGroup()
                //.addGap(8, 8, 8)
                .addContainerGap()
                .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSlotAvailabilityScannerRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addGap(10, 10, 10)
                .addContainerGap()
            )
        );
//</editor-fold>  
//<editor-fold defaultstate="collapsed" desc="Appointment day schedule panel">
        javax.swing.GroupLayout pnlAppointmentScheduleForDayLayout = new javax.swing.GroupLayout(pnlAppointmentScheduleForDay);
        pnlAppointmentScheduleForDay.setLayout(pnlAppointmentScheduleForDayLayout);
        pnlAppointmentScheduleForDayLayout.setHorizontalGroup(
            pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.PREFERRED_SIZE, getAppointmentDayScheduleTableWidth(), javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlAppointmentScheduleForDayLayout.setVerticalGroup(
            pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.DEFAULT_SIZE, /*186*/getAppointmentDayScheduleTableWidth(), Short.MAX_VALUE)
                .addGap(11,11,11))
//.addContainerGap()

        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Slot availability panel layout">
        javax.swing.GroupLayout SlotAvailabilityPanelLayout = new javax.swing.GroupLayout(pnlSlotAvailability);
        pnlSlotAvailability.setLayout(SlotAvailabilityPanelLayout);
        SlotAvailabilityPanelLayout.setHorizontalGroup(
            SlotAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SlotAvailabilityPanelLayout.createSequentialGroup()
                .addGap(18,18,18)
//.addContainerGap(/*javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE*/)
                .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addContainerGap())
            ));
        SlotAvailabilityPanelLayout.setVerticalGroup(
            SlotAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(SlotAvailabilityPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Schedule operations panel layout">
        javax.swing.GroupLayout pnlScheduleOperationsLayout = new javax.swing.GroupLayout(pnlScheduleOperations);
        pnlScheduleOperations.setLayout(pnlScheduleOperationsLayout);
        pnlScheduleOperationsLayout.setHorizontalGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMakeDeleteEmergencyAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMarkCancelSlotUnbookable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(/*22, Short.MAX_VALUE*/)
            )
        );
        pnlScheduleOperationsLayout.setVerticalGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                /*.addGap(18,18,18)*/.addGap(5,5,5)
                .addComponent(btnMakeDeleteEmergencyAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                /*.addGap(18,18,18)*/.addGap(5,5,5)
                 .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                /*.addGap(18,18,18)*/.addGap(5,5,5)
                .addComponent(btnMarkCancelSlotUnbookable, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                /*.addGap(18,18,18)*/.addGap(5,5,5)
                .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)   
                /*.addGap(18,18,18)*/.addGap(5,5,5)
                .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                /*.addGap(18,18,18)*/.addGap(5,5,5)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
//</editor-fold>

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(/*javax.swing.GroupLayout.Alignment.LEADING*/)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(/*javax.swing.GroupLayout.Alignment.TRAILING, false*/)
                                        .addComponent(pnlAppointmentScheduleForDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(pnlAppointmentDaySelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(getGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels(), 
                                                        getGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels(), 
                                                        getGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels())
                                                .addComponent(pnlSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        )
                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()
                        )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER,false)
                            .addComponent(pnlAppointmentDaySelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlSlotAvailability, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(pnlAppointmentScheduleForDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }

    /**
     * Responsibilities
     * -- initialise the Descriptor.ViewDescription.ViewMode property appropriately
     * -- if SLOT_SELECTED view mode
     * ---- initialise Descriptor.ViewDescription property with details extracted from the selected appointment slot
     * @param evt 
     */
    private void btnCreateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1) {
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_UNSELECTED);
        }else{
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_SELECTED);
            getMyController().getDescriptor().getViewDescription().
                    setAppointment(tableModel.getElementAt(row));
        }
        ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.APPOINTMENT_CREATE_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }  
    
    private void makeEmergencyAppointment(){
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        else{
            JOptionPane.showInternalMessageDialog(this,
                    "An appointment slot has not been selected;\n make emergency appointment action aborted", 
                    "View error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deleteEmergencyAppointment(){
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            SCHEDULE_EDITOR_DELETE_EMERGENCY_APPOINTMENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        else{
            JOptionPane.showInternalMessageDialog(this,
                    "An appointment slot has not been selected;\n delete emergency appointment action aborted",  
                    "View error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void btnEmergencyAppointmentActionPerformed(){
        //shouldn't need any pre-checks on what/is not selected
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }

    private void btnUpdateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if(((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient()==null){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if (!((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient().getIsKeyDefined()){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if (SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark()
                .equals(((AppointmentScheduleTableModel)this.tblAppointments.getModel()).
                            getElementAt(row).getPatient().toString())){
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.UPDATE);
            getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        else{
            initialiseEDRequestFromView(row);
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            APPOINTMENT_UPDATE_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }                                                    

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {                                             

        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException e){
            
        }
    }                                            

    private void btnNextPracticeDayActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getNextAvailableDateTo(day);
        dayDatePicker.setDate(day);         
    }                                                  

    private void btnPreviousPracticeDayActionPerformed(java.awt.event.ActionEvent evt) {                                                       
        // TODO add your handling code here:
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getPreviousAvailableDateTo(day);
        dayDatePicker.setDate(day);
    }                                                      

    private void btnScanForEmptySlotsActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        // TODO add your handling code here:
        LocalDate searchStartDate = dayDatePicker.getDate();
        getMyController().getDescriptor().getViewDescription().setScheduleDay(searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }                                                    

    
    private void btnCancelSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {                                                             
        //DateTimeFormatter format24Hour = DateTimeFormatter.ofPattern("HH:mm");
        boolean isError = false;
        String name = null;
        LocalDateTime start = null;
        LocalTime from = null;
        Long duration;
        int row = this.tblAppointments.getSelectedRow();
        
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
            isError = true;
        }
        //30/07/2022 09:26
        if (!isError){
            if ((((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient()==null)){
                JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
            }   
        }
        int OKToCancelAppointment;
        initialiseEDRequestFromView(row);
        start = getMyController().getDescriptor().getViewDescription().getAppointment().getStart();
        from = start.toLocalTime();
        //20/07/2022 08:16 update
        //duration = getViewDescriptor().getViewDescription().getAppointment().getData().getDuration().toMinutes();
        duration = getMyController().getDescriptor().getViewDescription().getAppointment().getDuration().toMinutes();
        LocalTime to = from.plusMinutes(duration);

        String message;
        if (getMyController().getDescriptor().getViewDescription().getAppointment().getPatient().toString().
                equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            message = "Are you sure you want to cancel the unbookable appointment slot";
        }
        else {
            name = getMyController().getDescriptor().getViewDescription().getAppointment().getPatient().getName().getForenames();
            if (!name.isEmpty())name = name + " ";
            name = name + getMyController().getDescriptor().getViewDescription().getAppointment().getPatient().getName().getSurname();
            message = "Are you sure you want to cancel the appointment for patient " + name; 
        }
        from.format(DateTimeFormatter.ofPattern("HH:mm"));
        String[] options = {"Yes", "No"};
        OKToCancelAppointment = JOptionPane.showInternalOptionDialog(this,
                        message + " from " + from.format(DateTimeFormatter.ofPattern("HH:mm")) 
                                + " to " + to.format(DateTimeFormatter.ofPattern("HH:mm")),
                                "View query",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
        if (OKToCancelAppointment==JOptionPane.YES_OPTION){
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.APPOINTMENT_CANCEL_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }                                                            

    private void btnNowDayActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
        LocalDate day = this.vetoPolicy.getNowDateOrClosestAvailableAfterNow();
        dayDatePicker.setDate(day);
    }                                         
/*
    private void mniSurgeryDaysEditorActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        // TODO add your handling code here:
    }                                                    
*/
/**/
    private void mniViewCancelledAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {                                                             
        ActionEvent actionEvent = new ActionEvent(this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                APPOINTMENTS_CANCELLED_VIEW_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
    }                                                            

    private void btnMarkSlotUnbookableActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        
        switch(getUnbookableSlotMode()){
            case CANCEL:
                doUnbookableSlotInCancelMode();
                break;
            case MARK:
                doUnbookableSlotInMarkMode();
                break;
        }
    } 
    
    private void doUnbookableSlotInCancelMode(){
        ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.APPOINTMENT_CANCEL_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        setUnbookableSlotMode(UnbookableSlotMode.MARK);
    }
    
    private void doUnbookableSlotInMarkMode(){
        Appointment appointment = null;
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_SELECTED);
            appointment = ((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row);
            getMyController().getDescriptor().getViewDescription().setAppointment(appointment); 
        }
        else {
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_UNSELECTED);
            getMyController().getDescriptor().getViewDescription().setAppointment(null);
        }
        ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void mniPrintScheduleActionPerformed(java.awt.event.ActionEvent evt) { 
        getMyController().sendNoOpMessage(this);

    }

    /*
    private void mniPrintScheduleActionPerformed(java.awt.event.ActionEvent evt) {                                                          
        // TODO add your handling code here:
        this.tblAppointments.clearSelection();
    } 
    */

    public static String centreString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
    
    public static String leftAlignString(int width, String s){
        return String.format("%-" + width + "s", s);
    }
    
    
    private void btnSelectTreatmentActionPerformed(java.awt.event.ActionEvent evt) {  
        ActionEvent actionEvent = new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);

        /*
        int PATIENT_WIDTH = 36;
        int FROM_WIDTH = 8;
        int TO_WIDTH = 8;
        int NOTES_WIDTH = 20;
        int patientColumn = 0;
        int fromColumn = 1;
        int toColumn = 2;
        int durationColumn = 3;
        int notesColumn = 4;
        String patient;
        String from;
        String to;
        String duration;
        String contact;
        String stringToPrint;
        if (this.tblAppointments!=null){
            TableModel model = tblAppointments.getModel();
            stringToPrint = getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                    format(DateTimeFormatter.ofPattern("dd/MM/yy"));
            stringToPrint = stringToPrint + " Appointment Schedule" +"\n\n";
            stringToPrint = stringToPrint + String.format(centreString(PATIENT_WIDTH,"Patient") +
                    centreString(FROM_WIDTH,"From") 
                    + centreString(TO_WIDTH, "To")
                    + centreString(NOTES_WIDTH, "Notes"));
            stringToPrint = stringToPrint +"\n";
 
            for (int row = 0; row < tblAppointments.getRowCount(); row++) {
                from = ((LocalTime)model.getValueAt(row, fromColumn)).format(DateTimeFormatter.ofPattern("HH:mm"));
                to = ((LocalTime)model.getValueAt(row, toColumn)).format(DateTimeFormatter.ofPattern("HH:mm"));
                Patient p = (Patient)model.getValueAt(row, patientColumn);
                //String notes = (String)model.getValueAt(row, notesColumn);
                //28/03/2024PatientNote patientNote = (PatientNote)model.getValueAt(row, notesColumn);
                //if (model.getValueAt(row, patientColumn)==null) {
                if (p==null){
                    patient = "AVAILABLE SLOT";
                    stringToPrint = stringToPrint + String.format(
                        centreString(PATIENT_WIDTH, patient) +
                        centreString(FROM_WIDTH, from) +
                        centreString(TO_WIDTH, to) +
                        leftAlignString(NOTES_WIDTH, ""));
                }else if(p.toString().equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_MARKER)){
                    patient = "<< U N B O O K A B L E  S L O T >>";
                    stringToPrint = stringToPrint + String.format(
                        centreString(PATIENT_WIDTH, patient) +
                        centreString(FROM_WIDTH, from) +
                        centreString(TO_WIDTH, to) +
                        leftAlignString(NOTES_WIDTH, /*28/03/2024patientNote.getNote()""));   
                }else {
                    patient = model.getValueAt(row,patientColumn).toString();
                    stringToPrint = stringToPrint + String.format(
                        leftAlignString(PATIENT_WIDTH, patient) +
                        centreString(FROM_WIDTH, from) +
                        centreString(TO_WIDTH, to) +
                        leftAlignString(NOTES_WIDTH, /*28/03/2024patientNote.getNote()""));
                }
                stringToPrint = stringToPrint + "\n";
            }
            
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new OutputPrinter(stringToPrint));
            boolean doPrint = job.printDialog();
            if (doPrint)
            { 
                try 
                {
                    job.print();
                }
                catch (PrinterException ex)
                {
                    int test = 0;
                    // Print job did not complete.
                }
            }
        }
        */
    } 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    /*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlAppointmentScheduleForDay = new javax.swing.JPanel();
        scrAppointmentsForDayTable = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        pnlSlotAvailability = new javax.swing.JPanel();
        scrPanelSlotAvailability = new javax.swing.JScrollPane();
        tblSlotAvailability = new javax.swing.JTable();
        pnlAppointmentDaySelection = new javax.swing.JPanel();
        btnSlotAvailabilityScannerRequest = new javax.swing.JButton();
        pnlAppointmentDaySelector = new javax.swing.JPanel();
        btnNowDay = new javax.swing.JButton();
        btnNextPracticeDay = new javax.swing.JButton();
        btnPreviousPracticeDay = new javax.swing.JButton();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        pnlScheduleOperations = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnMakeDeleteEmergencyAppointment = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnMarkCancelSlotUnbookable = new javax.swing.JButton();
        btnPrintDayScheduleRequest = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuOptions = new javax.swing.JMenu();
        mniViewCancelledAppointments = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniSurgeryDaysEditor = new javax.swing.JMenuItem();
        mniSelectNonSurgeryDay = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniPrintScheduleSelection = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlAppointmentScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Appointment schedule for ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(

        ));
        scrAppointmentsForDayTable.setViewportView(tblAppointments);

        javax.swing.GroupLayout pnlAppointmentScheduleForDayLayout = new javax.swing.GroupLayout(pnlAppointmentScheduleForDay);
        pnlAppointmentScheduleForDay.setLayout(pnlAppointmentScheduleForDayLayout);
        pnlAppointmentScheduleForDayLayout.setHorizontalGroup(
            pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlAppointmentScheduleForDayLayout.setVerticalGroup(
            pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlSlotAvailability.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Available empty slots", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        pnlSlotAvailability.setPreferredSize(new java.awt.Dimension(266, 146));

        scrPanelSlotAvailability.setViewportView(tblSlotAvailability);

        javax.swing.GroupLayout pnlSlotAvailabilityLayout = new javax.swing.GroupLayout(pnlSlotAvailability);
        pnlSlotAvailability.setLayout(pnlSlotAvailabilityLayout);
        pnlSlotAvailabilityLayout.setHorizontalGroup(
            pnlSlotAvailabilityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSlotAvailabilityLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlSlotAvailabilityLayout.setVerticalGroup(
            pnlSlotAvailabilityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotAvailabilityLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlAppointmentDaySelection.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Appointment day selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        btnSlotAvailabilityScannerRequest.setText("search for empty appointment slots");
        btnSlotAvailabilityScannerRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSlotAvailabilityScannerRequestActionPerformed(evt);
            }
        });

        pnlAppointmentDaySelector.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlAppointmentDaySelector.setPreferredSize(new java.awt.Dimension(200, 58));

        btnNowDay.setText("now");
        btnNowDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNowDayActionPerformed(evt);
            }
        });

        btnNextPracticeDay.setText(">>");
        btnNextPracticeDay.setMinimumSize(new java.awt.Dimension(60, 23));
        btnNextPracticeDay.setPreferredSize(new java.awt.Dimension(62, 23));
        btnNextPracticeDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextPracticeDayActionPerformed(evt);
            }
        });

        btnPreviousPracticeDay.setText("<<");
        btnPreviousPracticeDay.setPreferredSize(new java.awt.Dimension(93, 23));
        btnPreviousPracticeDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousPracticeDayActionPerformed(evt);
            }
        });

        settings = new DatePickerSettings();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowEmptyDates(false);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);

        javax.swing.GroupLayout pnlAppointmentDaySelectorLayout = new javax.swing.GroupLayout(pnlAppointmentDaySelector);
        pnlAppointmentDaySelector.setLayout(pnlAppointmentDaySelectorLayout);
        pnlAppointmentDaySelectorLayout.setHorizontalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnPreviousPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnNowDay)
                .addGap(18, 18, 18)
                .addComponent(btnNextPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
            .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAppointmentDaySelectorLayout.setVerticalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreviousPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNowDay, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNextPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlAppointmentDaySelectionLayout = new javax.swing.GroupLayout(pnlAppointmentDaySelection);
        pnlAppointmentDaySelection.setLayout(pnlAppointmentDaySelectionLayout);
        pnlAppointmentDaySelectionLayout.setHorizontalGroup(
            pnlAppointmentDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDaySelectionLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, 259, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addGroup(pnlAppointmentDaySelectionLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(btnSlotAvailabilityScannerRequest)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAppointmentDaySelectionLayout.setVerticalGroup(
            pnlAppointmentDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDaySelectionLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSlotAvailabilityScannerRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        pnlScheduleOperations.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Appointment schedule operations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        btnCreateUpdateAppointment.setText("Create");
        btnCreateUpdateAppointment.setToolTipText("create new appointment");

        btnMakeDeleteEmergencyAppointment.setText("Update");
        btnMakeDeleteEmergencyAppointment.setToolTipText("update selected appointment");
        btnMakeDeleteEmergencyAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMakeDeleteEmergencyAppointmentActionPerformed(evt);
            }
        });

        btnCancelSelectedAppointment.setText("Cancel ");
        btnCancelSelectedAppointment.setToolTipText("cancel selected appointment");
        btnCancelSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedAppointmentActionPerformed(evt);
            }
        });

        btnMarkCancelSlotUnbookable.setText("Mark  slot as unbookable");
        btnMarkCancelSlotUnbookable.setToolTipText("make a slot unbookable");
        btnMarkCancelSlotUnbookable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkCancelSlotUnbookableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlScheduleOperationsLayout = new javax.swing.GroupLayout(pnlScheduleOperations);
        pnlScheduleOperations.setLayout(pnlScheduleOperationsLayout);
        pnlScheduleOperationsLayout.setHorizontalGroup(
            pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMakeDeleteEmergencyAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelSelectedAppointment)
                .addGap(87, 87, 87)
                .addComponent(btnMarkCancelSlotUnbookable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlScheduleOperationsLayout.setVerticalGroup(
            pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateUpdateAppointment)
                    .addComponent(btnMakeDeleteEmergencyAppointment)
                    .addComponent(btnCancelSelectedAppointment)
                    .addComponent(btnMarkCancelSlotUnbookable))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btnPrintDayScheduleRequest.setText("Print schedule");
        btnPrintDayScheduleRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintDayScheduleRequestActionPerformed(evt);
            }
        });

        btnCloseView.setText("Close view");
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        mnuOptions.setText("Actions");

        mniViewCancelledAppointments.setText("View cancelled appointments");
        mniViewCancelledAppointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniViewCancelledAppointmentsActionPerformed(evt);
            }
        });
        mnuOptions.add(mniViewCancelledAppointments);
        mnuOptions.add(jSeparator1);

        mniSurgeryDaysEditor.setText("Define which days are surgery days");
        mniSurgeryDaysEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSurgeryDaysEditorActionPerformed(evt);
            }
        });
        mnuOptions.add(mniSurgeryDaysEditor);

        mniSelectNonSurgeryDay.setText("Open schedule on a non-surgery day");
        mnuOptions.add(mniSelectNonSurgeryDay);
        mnuOptions.add(jSeparator2);

        mniPrintScheduleSelection.setText("Print schedule");
        mniPrintScheduleSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPrintScheduleSelectionActionPerformed(evt);
            }
        });
        mnuOptions.add(mniPrintScheduleSelection);
        mnuOptions.add(jSeparator3);

        mniCloseView.setText("Close view");
        mnuOptions.add(mniCloseView);

        jMenuBar1.add(mnuOptions);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(btnCloseView))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnPrintDayScheduleRequest)))
                        .addGap(17, 17, 17))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlAppointmentScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlAppointmentDaySelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlAppointmentDaySelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSlotAvailability, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(pnlAppointmentScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnPrintDayScheduleRequest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCloseView)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
        */
    /**
     * Responsibilities
     * -- initialise the Descriptor.ViewDescription.ViewMode property appropriately
     * -- if SLOT_SELECTED view mode
     * ---- initialise Descriptor.ViewDescription property with details extracted from the selected appointment slot
     * @param evt 
     */
    /*
    private void btnMakeDeleteEmergencyAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMakeDeleteEmergencyAppointmentActionPerformed
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if(((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient()==null){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if (!((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient().getIsKeyDefined()){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if (SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_MARKER.equals(((AppointmentScheduleTableModel)this.tblAppointments.getModel()).
                            getElementAt(row).getPatient().toString())){
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.UPDATE);
            getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        else{
            initialiseEDRequestFromView(row);
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            APPOINTMENT_UPDATE_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_btnMakeDeleteEmergencyAppointmentActionPerformed

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed

        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException e){
            
        }
    }//GEN-LAST:event_btnCloseViewActionPerformed

    private void btnNextPracticeDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextPracticeDayActionPerformed
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getNextAvailableDateTo(day);
        dayDatePicker.setDate(day);         
    }//GEN-LAST:event_btnNextPracticeDayActionPerformed

    private void btnPreviousPracticeDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousPracticeDayActionPerformed
        // TODO add your handling code here:
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getPreviousAvailableDateTo(day);
        dayDatePicker.setDate(day);
    }//GEN-LAST:event_btnPreviousPracticeDayActionPerformed

    private void btnSlotAvailabilityScannerRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSlotAvailabilityScannerRequestActionPerformed
        // TODO add your handling code here:
        LocalDate searchStartDate = dayDatePicker.getDate();
        getMyController().getDescriptor().getViewDescription().setScheduleDay(searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_btnSlotAvailabilityScannerRequestActionPerformed

    
    private void btnCancelSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelSelectedAppointmentActionPerformed
        //DateTimeFormatter format24Hour = DateTimeFormatter.ofPattern("HH:mm");
        boolean isError = false;
        String name = null;
        LocalDateTime start = null;
        LocalTime from = null;
        Long duration;
        int row = this.tblAppointments.getSelectedRow();
        
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
            isError = true;
        }
        //30/07/2022 09:26
        if (!isError){
            if ((((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient()==null)){
                JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
            }   
        }
        int OKToCancelAppointment;
        initialiseEDRequestFromView(row);
        start = getMyController().getDescriptor().getViewDescription().getAppointment().getStart();
        from = start.toLocalTime();
        //20/07/2022 08:16 update
        //duration = getViewDescriptor().getViewDescription().getAppointment().getData().getDuration().toMinutes();
        duration = getMyController().getDescriptor().getViewDescription().getAppointment().getDuration().toMinutes();
        LocalTime to = from.plusMinutes(duration);

        String message;
        if (getMyController().getDescriptor().getViewDescription().getAppointment().getPatient().toString().
                equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_MARKER)){
            message = "Are you sure you want to cancel the unbookable appointment slot";
        }
        else {
            name = getMyController().getDescriptor().getViewDescription().getAppointment().getPatient().getName().getForenames();
            if (!name.isEmpty())name = name + " ";
            name = name + getMyController().getDescriptor().getViewDescription().getAppointment().getPatient().getName().getSurname();
            message = "Are you sure you want to cancel the appointment for patient " + name; 
        }
        from.format(DateTimeFormatter.ofPattern("HH:mm"));
        String[] options = {"Yes", "No"};
        OKToCancelAppointment = JOptionPane.showOptionDialog(this,
                        message + " from " + from.format(DateTimeFormatter.ofPattern("HH:mm")) 
                                + " to " + to.format(DateTimeFormatter.ofPattern("HH:mm"))
                                + ".",null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
        if (OKToCancelAppointment==JOptionPane.YES_OPTION){
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.APPOINTMENT_CANCEL_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_btnCancelSelectedAppointmentActionPerformed

    private void btnNowDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNowDayActionPerformed
        // TODO add your handling code here:
        LocalDate day = this.vetoPolicy.getNowDateOrClosestAvailableAfterNow();
        dayDatePicker.setDate(day);
    }//GEN-LAST:event_btnNowDayActionPerformed
    */
    /*
    private void mniSurgeryDaysEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSurgeryDaysEditorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mniSurgeryDaysEditorActionPerformed
*/
/*
    private void mniViewCancelledAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniViewCancelledAppointmentsActionPerformed
        ActionEvent actionEvent = new ActionEvent(this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                APPOINTMENTS_CANCELLED_VIEW_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_mniViewCancelledAppointmentsActionPerformed

    private void btnMarkCancelSlotUnbookableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkCancelSlotUnbookableActionPerformed
        Appointment appointment = null;
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_SELECTED);
            appointment = ((AppointmentScheduleTableModel)this.tblAppointments.getModel()).getElementAt(row);
            getMyController().getDescriptor().getViewDescription().setAppointment(appointment); 
        }
        else {
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_UNSELECTED);
            getMyController().getDescriptor().getViewDescription().setAppointment(null);
        }
        ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_btnMarkCancelSlotUnbookableActionPerformed

    private void mniPrintScheduleSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPrintScheduleSelectionActionPerformed
        // TODO add your handling code here:
        this.tblAppointments.clearSelection();
    }//GEN-LAST:event_mniPrintScheduleSelectionActionPerformed

    public static String centreString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
    
    public static String leftAlignString(int width, String s){
        return String.format("%-" + width + "s", s);
    }
    
    private void btnPrintDayScheduleRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintDayScheduleRequestActionPerformed
        int PATIENT_WIDTH = 36;
        int FROM_WIDTH = 8;
        int TO_WIDTH = 8;
        int NOTES_WIDTH = 20;
        int patientColumn = 0;
        int fromColumn = 1;
        int toColumn = 2;
        int durationColumn = 3;
        int notesColumn = 4;
        String patient;
        String from;
        String to;
        String duration;
        String contact;
        String stringToPrint;
        if (this.tblAppointments!=null){
            TableModel model = tblAppointments.getModel();
            stringToPrint = getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                    format(DateTimeFormatter.ofPattern("dd/MM/yy"));
            stringToPrint = stringToPrint + " Appointment Schedule" +"\n\n";
            stringToPrint = stringToPrint + String.format(centreString(PATIENT_WIDTH,"Patient") +
                    centreString(FROM_WIDTH,"From") 
                    + centreString(TO_WIDTH, "To")
                    + centreString(NOTES_WIDTH, "Notes"));
            stringToPrint = stringToPrint +"\n";
 
            for (int row = 0; row < tblAppointments.getRowCount(); row++) {
                from = ((LocalTime)model.getValueAt(row, fromColumn)).format(DateTimeFormatter.ofPattern("HH:mm"));
                to = ((LocalTime)model.getValueAt(row, toColumn)).format(DateTimeFormatter.ofPattern("HH:mm"));
                Patient p = (Patient)model.getValueAt(row, patientColumn);
                String notes = (String)model.getValueAt(row, notesColumn);
                //if (model.getValueAt(row, patientColumn)==null) {
                if (p==null){
                    patient = "AVAILABLE SLOT";
                    stringToPrint = stringToPrint + String.format(
                        centreString(PATIENT_WIDTH, patient) +
                        centreString(FROM_WIDTH, from) +
                        centreString(TO_WIDTH, to) +
                        leftAlignString(NOTES_WIDTH, notes));
                }else if(p.toString().equals(SystemDefinition.UNBOOKABLE_SCHEDULE_SLOT_MARKER)){
                    patient = "<< U N B O O K A B L E  S L O T >>";
                    stringToPrint = stringToPrint + String.format(
                        centreString(PATIENT_WIDTH, patient) +
                        centreString(FROM_WIDTH, from) +
                        centreString(TO_WIDTH, to) +
                        leftAlignString(NOTES_WIDTH, notes));   
                }else {
                    patient = model.getValueAt(row,patientColumn).toString();
                    stringToPrint = stringToPrint + String.format(
                        leftAlignString(PATIENT_WIDTH, patient) +
                        centreString(FROM_WIDTH, from) +
                        centreString(TO_WIDTH, to) +
                        leftAlignString(NOTES_WIDTH, notes));
                }
                stringToPrint = stringToPrint + "\n";
            }
            
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new OutputPrinter(stringToPrint));
            boolean doPrint = job.printDialog();
            if (doPrint)
            { 
                try 
                {
                    job.print();
                }
                catch (PrinterException ex)
                {
                    int test = 0;
                    // Print job did not complete.
                }
            }
        }
    }//GEN-LAST:event_btnPrintDayScheduleRequestActionPerformed
*/
/*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelSelectedAppointment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateAppointment;
    private javax.swing.JButton btnMakeDeleteEmergencyAppointment;
    private javax.swing.JButton btnMarkCancelSlotUnbookable;
    private javax.swing.JButton btnNextPracticeDay;
    private javax.swing.JButton btnNowDay;
    private javax.swing.JButton btnPreviousPracticeDay;
    private javax.swing.JButton btnPrintDayScheduleRequest;
    private javax.swing.JButton btnSlotAvailabilityScannerRequest;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniPrintScheduleSelection;
    private javax.swing.JMenuItem mniSelectNonSurgeryDay;
    private javax.swing.JMenuItem mniSurgeryDaysEditor;
    private javax.swing.JMenuItem mniViewCancelledAppointments;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlAppointmentDaySelection;
    private javax.swing.JPanel pnlAppointmentDaySelector;
    private javax.swing.JPanel pnlAppointmentScheduleForDay;
    private javax.swing.JPanel pnlScheduleOperations;
    private javax.swing.JPanel pnlSlotAvailability;
    private javax.swing.JScrollPane scrAppointmentsForDayTable;
    private javax.swing.JScrollPane scrPanelSlotAvailability;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTable tblSlotAvailability;
    // End of variables declaration//GEN-END:variables
*/
    private javax.swing.JPanel pnlSlotAvailability;
    private javax.swing.JButton btnClinicalNotesForSelectedAppointment;
    private javax.swing.JButton btnCancelSelectedAppointment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateAppointment;
    private javax.swing.JButton btnMarkCancelSlotUnbookable;
    private javax.swing.JButton btnNextPracticeDay;
    private javax.swing.JButton btnNowDay;
    private javax.swing.JButton btnPreviousPracticeDay;
    private javax.swing.JButton btnSelectTreatmentRequest;
    private javax.swing.JButton btnSlotAvailabilityScannerRequest;
    private javax.swing.JButton btnMakeDeleteEmergencyAppointment;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane scrPanelSlotAvailability;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniPrintSchedule;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniSelectNonSurgeryDay;
    private javax.swing.JMenuItem mniSurgeryDaysEditor;
    private javax.swing.JMenuItem mniViewCancelledAppointments;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlAppointmentDaySelection;
    private javax.swing.JPanel pnlAppointmentDaySelector;
    private javax.swing.JPanel pnlAppointmentScheduleForDay;
    private javax.swing.JScrollPane scrAppointmentsForDayTable;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTable tblSlotAvailability;
    private javax.swing.JPanel pnlScheduleOperations;
    // End of variables declaration 
    class DayDatePickerChangeListener implements DateChangeListener {
        @Override
        public void dateChanged(DateChangeEvent event) {
            //LocalDate date = event.getNewDate();
            getMyController().getDescriptor().getViewDescription().setScheduleDay(ScheduleViewx.this.dayDatePicker.getDate());
            tblAppointments.clearSelection();
            ActionEvent actionEvent = new ActionEvent(ScheduleViewx.this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
            ScheduleViewx.this.getMyController().actionPerformed(actionEvent);
            //getMyController().actionPerformed(actionEvent);
            SwingUtilities.invokeLater(new Runnable() 
            {
              public void run()
              {
                ScheduleViewx.this.setTitle(ScheduleViewx.this.dayDatePicker.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " schedule");
              }
            });
        }
    }

    private void populateEmptySlotAvailabilityTable(ArrayList<Appointment> a){
        if (a==null) a = new ArrayList<Appointment>();
        EmptySlotAvailability2ColumnTableModel model;
        if (this.tblSlotAvailability!=null){
            scrPanelSlotAvailability.remove(this.tblSlotAvailability);   
        }
        this.tblSlotAvailability = new JTable(new EmptySlotAvailability2ColumnTableModel());
        scrPanelSlotAvailability.setViewportView(this.tblSlotAvailability);
        setEmptySlotAvailabilityTableListener();
        model = (EmptySlotAvailability2ColumnTableModel)this.tblSlotAvailability.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = a.iterator();
        while (it.hasNext()){
            ((EmptySlotAvailability2ColumnTableModel)this.tblSlotAvailability.getModel()).addElement(it.next());
        }

        JTableHeader tableHeader = this.tblSlotAvailability.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);
        
        TableColumnModel columnModel = this.tblSlotAvailability.getColumnModel();
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
    }
    
    private void doScheduleTitleRefresh(Patient appointee){
        String tableTitle = "Appointment schedule for " 
                + dayDatePicker.getDate().format(appointmentScheduleFormat);
        if (appointee!=null){
            if (!appointee.toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
                    //SystemDefinitions.UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY.toString())){
                tableTitle = tableTitle + "          <"
                    + appointee.getName().getForenames() +  " " 
                    + appointee.getName().getSurname() + "'s contact "
                    + appointee.getPhone1();
                if (!appointee.getPhone2().isEmpty())
                    tableTitle = tableTitle + "; "
                            + appointee.getPhone2(); 
                tableTitle = tableTitle + ">";   
            }
        }
        TitledBorder titledBorder = (TitledBorder)pnlAppointmentScheduleForDay.getBorder();
        titledBorder.setTitle(tableTitle);
        pnlAppointmentScheduleForDay.repaint();
    }
    
    private ArrayList<Appointment> makeEmergencyAppointmentsFirst(ArrayList<Appointment> schedule){
        ArrayList<Appointment> modifiedList = new ArrayList<>();
        for (Appointment a : schedule){
            if (a.getIsEmergency()) modifiedList.add(a);
        }
        for (Appointment a : schedule){
            if (!a.getIsEmergency()) modifiedList.add(a);
        }
        return modifiedList;
    }
    
    private void ConfigureScheduleTable(){
        if (tableModel == null) {
            tableModel = new AppointmentScheduleTableModel();
            tableModel.addTableModelListener(new TableModelListener(){
                Appointment appointment = null;
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    AppointmentScheduleTableModel model =  
                            (AppointmentScheduleTableModel)e.getSource();
                    Boolean value = (Boolean)model.getValueAt(row, column);
                    appointment = model.getElementAt(row);
                    appointment.setHasPatientBeenContacted(value);
                    getMyController().getDescriptor().getViewDescription().setAppointment(appointment);
                    tblAppointments.clearSelection();
                    
                    ActionEvent actionEvent = new ActionEvent(
                        ScheduleViewx.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                APPOINTMENT_REMINDED_STATUS_UPDATE_REQUEST.toString());
                    getMyController().actionPerformed(actionEvent);

                }
            });
            this.tblAppointments.setModel(tableModel);
        }
        tableModel.removeAllElements();
        ArrayList<Appointment> schedule = makeEmergencyAppointmentsFirst(
                getMyController().getDescriptor()
                        .getControllerDescription().getAppointmentSlotsForDay());
        Iterator<Appointment> it = schedule.iterator();
        while (it.hasNext()){
            tableModel.addElement(it.next());
        }
       
        this.tblAppointments.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        this.tblAppointments.setDefaultRenderer(LocalDateTime.class, new AppointmentsTableLocalDateTimeRenderer());
        this.tblAppointments.setDefaultRenderer(Patient.class, new AppointmentsTablePatientRenderer());
        //this.tblAppointments.setDefaultRenderer(Object.class, new ScheduleTableRenderer());
        /*28/03/2024this.tblAppointments.setDefaultRenderer(PatientNote.class, new AppointmentsTablePatientNoteRenderer());*/
        //this.tblAppointments.setModel(tableModel);
        //this.tblAppointments.setRowSelectionAllowed(false);
        this.tblAppointments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
   
        TableColumnModel columnModel = this.tblAppointments.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(185);
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setPreferredWidth(36);
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(2).setPreferredWidth(36);
        columnModel.getColumn(2).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(3).setPreferredWidth(/*96*/110);
        columnModel.getColumn(3).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(4).setMinWidth(278);
        columnModel.getColumn(4).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(4).setCellRenderer(new ScheduleTableCellRenderer() );
        columnModel.getColumn(5).setMinWidth(/*20*/50);
        columnModel.getColumn(5).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        JTableHeader tableHeader = this.tblAppointments.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);  
    }
    

    
    public class OutputPrinter implements Printable {
        private String printData;
    
        public OutputPrinter(String printDataIn){
            this.printData = printDataIn;
        }
    
        @Override
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException
        {
            // Should only have one page, and page # is zero-based.
            if (page > 0)
            {
                return NO_SUCH_PAGE;
            }
        
            // Adding the "Imageable" to the x and y puts the margins on the page.
            // To make it safe for printing.
            Graphics2D g2d = (Graphics2D)g;
            g2d.setFont(new Font("MONOSPACED", Font.PLAIN, 10));
            int x = (int) pf.getImageableX();
            int y = (int) pf.getImageableY();        
            g2d.translate(x, y); 
        
            // Calculate the line height
            Font font = new Font("MONOSPACED", Font.PLAIN, 10);
            FontMetrics metrics = g.getFontMetrics(font);
            int lineHeight = metrics.getHeight();
        
            BufferedReader br = new BufferedReader(new StringReader(printData));
        
            // Draw the page:
            try
            {
                String line;
                // Just a safety net in case no margin was added.
                x += 50;
                y += 50;
                int count= 0;
                while ((line = br.readLine()) != null)
                {
                    switch(count){
                        case 0:
                            g2d.setFont(new Font("MONOSPACED", Font.BOLD, 14));
                            break;
                        case 2:
                            g2d.setFont(new Font("MONOSPACED", Font.BOLD, 11));
                            break;
                        default:
                            g2d.setFont(new Font("MONOSPACED", Font.PLAIN, 11));
                            break;

                    }
                    y += lineHeight;
                    g2d.drawString(line, x, y);
                    count++;
                }
            }
            catch (IOException e)
            {
                // 
            }
        
            return PAGE_EXISTS;
        }
    } 
    
    
    
    private void btnPrintScheduleActionPerformedx(ActionEvent e){
        getMyController().sendNoOpMessage(this);
        /*
        int patientColumn = 0;
        int fromColumn = 1;
        int toColumn = 2;
        int durationColumn = 3;
        int contactColumn = 4;
        String patient;
        String from;
        String to;
        String duration;
        String contact;
        String stringToPrint;
        if (this.tblAppointments!=null){
            TableModel model = tblAppointments.getModel();
            stringToPrint = getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                    format(DateTimeFormatter.ofPattern("dd/MM/yy"));
            stringToPrint = stringToPrint + " Appointment Schedule" +"\n\n";
            stringToPrint = stringToPrint + String.format(centreString(40,"Patient") +
                    centreString(10,"From") +
                    centreString(10, "To"));
            stringToPrint = stringToPrint +"\n";
 
            for (int row = 0; row < tblAppointments.getRowCount(); row++) {
                from = ((LocalTime)model.getValueAt(row, fromColumn)).format(DateTimeFormatter.ofPattern("HH:mm"));
                to = ((LocalTime)model.getValueAt(row, toColumn)).format(DateTimeFormatter.ofPattern("HH:mm"));
                if (model.getValueAt(row, patientColumn)==null) {
                    patient = "UNBOOKED";
                    stringToPrint = stringToPrint + String.format(
                        centreString(40, patient) +
                        centreString(10, from) +
                        centreString(10, to));
                }else {
                    patient = model.getValueAt(row,patientColumn).toString();
                    stringToPrint = stringToPrint + String.format(
                        leftAlignString(40, patient) +
                        centreString(10, from) +
                        centreString(10, to));
                }
                stringToPrint = stringToPrint + "\n";
            }
            
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new OutputPrinter(stringToPrint));
            boolean doPrint = job.printDialog();
            if (doPrint)
            { 
                try 
                {
                    job.print();
                }
                catch (PrinterException ex)
                {
                    // Print job did not complete.
                }
            }
        }
        */
    }
    
    public class LeaveEditor implements DateHighlightPolicy{
        @Override
        public HighlightInformation getHighlightInformationOrNull(LocalDate date) {
            if (date.getDayOfMonth()==7){
                if (date.getMonthValue()==9){
                    if (date.getYear()==2022){
                        return new HighlightInformation(Color.red, null,"holiday");
                    }
                }
            }
            return null;
        }
    }
    
    private int getAppointmentDayScheduleTableWidth(){
        int result = 0;
        try{
            String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
            switch (lookAndFeel){
                case "Metal":
                    result = mAppointmentDayScheduleWidth ;
                    break;
                case "Windows":
                    result = wAppointmentDayScheduleWidth ;
                    break;
            }
        }catch(Exception ex){
            String message = ex.getMessage() + "\n"
                    + "Raised in ScheduleView::getAppointmentDayScheduleTableWidth()";
            ViewController.displayErrorMessage(message, 
                    "Schedule view controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private int getGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels()throws Exception{
        int result = 0;
        try{
            String lookAndFeel = SystemDefinition.getPMSLookAndFeel();
            switch (lookAndFeel){
                case "Metal":
                    result = mGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels ;
                    break;
                case "Windows":
                    result = wGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels ;
                    break;
            }
        }catch(Exception ex){
            String message = ex.getMessage() + "\n"
                    + "Raised in ScheduleView::getGapBetweenAppointmentDaySelectionAndAvailabilitySlotsPanels()";
            ViewController.displayErrorMessage(message, 
                    "Schedule view controller error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
  }

