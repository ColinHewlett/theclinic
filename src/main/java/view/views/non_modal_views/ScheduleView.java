/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import model.non_entity.SystemDefinition;
import model.non_entity.SystemDefinition.ScheduleSlotType;
import model.non_entity.SystemDefinition.ScheduleViewActionCaption;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.AppointmentsTablePatientRenderer;
import view.views.view_support_classes.renderers.ScheduleTableCellRenderer;
/*28/03/2024import view.views.view_support_classes.renderers.AppointmentsTablePatientNoteRenderer;*/
import view.views.view_support_classes.models.ScheduleListTableModel;
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
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENTS_FOR_DAY_RECEIVED;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SLOTS_FROM_DAY_RECEIVED;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.NON_SURGERY_DAY_EDIT_RECEIVED;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.SURGERY_DAYS_ASSIGNMENT_RECEIVED;
import static controller.ViewController.ViewMode.SCHEDULE_REFERENCED_DESKTOP_VIEW;
import static controller.ViewController.ViewMode.SCHEDULE_REFERENCED_FROM_PATIENT_VIEW;
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
public class ScheduleView extends View 
                          implements ActionListener, 
                                     ListSelectionListener,
                                     MouseListener,
                                     DateChangeListener,
                                     DateHighlightPolicy,
                                     TableModelListener{
    
    private ScheduleListTableModel tableModel = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private DatePickerSettings settings = null;
    private ArrayList<Appointment> appointments = null;
    private final DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private AppointmentDateVetoPolicy vetoPolicy = null;
    
    enum Action{
        REQUEST_CANCEL_APPOINTMENT,
        REQUEST_CANCELLED_APPOINTMENT_VIEW,
        REQUEST_CLINICAL_NOTE_VIEW,
        REQUEST_CLOSE_VIEW,
        REQUEST_CREATE_UPDATE_APPOINTMENT,
        REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT,
        REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT,
        REQUEST_NEXT_DAY,
        REQUEST_NON_SURGERY_DAY,
        REQUEST_NOW,
        REQUEST_PREVIOUS_DAY,
        REQUEST_PRINT_SCHEDULE,
        REQUEST_SCHEDULE_DIARY_VIEW,
        REQUEST_SCHEDULE_LIST_VIEW,
        REQUEST_SEARCH_AVAILABLE_SLOTS,
        REQUEST_SURGERY_DAY_EDITOR,
        REQUEST_TREATMENT_VIEW
    }
    
    enum ScheduleViewMode{
        LIST,
        DIARY
    }
    
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
    
    private ScheduleViewMode scheduleViewMode = null;
    private void setScheduleViewMode(ScheduleViewMode value){
        scheduleViewMode = value;
        switch (scheduleViewMode){
            case DIARY:
                configureScheduleDiaryView();
                break;
            case LIST:
                configureScheduleListView();
                break;
        }
    }
    private ScheduleViewMode getScheduleViewMode(){
        return scheduleViewMode;
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
    
    /**
     * 
     * @param myViewType
     * @param controller
     * @param desktopView 
     */
    public ScheduleView(View.Viewer myViewType, 
            ViewController controller, 
            DesktopView desktopView) {
        setTitle("Appointment schedule");
        this.setMyViewType(myViewType);
        setMyController(controller); 
        setDesktopView(desktopView);
    }
    
    public void actionPerformed(ActionEvent e){
        ViewController.ScheduleViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CANCEL_APPOINTMENT:
                doCancelSelectedAppointmentAction();
                break;
            case REQUEST_CANCELLED_APPOINTMENT_VIEW:
                doCancelledAppointmentsViewAction();
                break;
            case REQUEST_CLINICAL_NOTE_VIEW:
                doClinicNoteRequest();
                break;
            case REQUEST_CLOSE_VIEW:
                doCloseViewAction();
                break;
            case REQUEST_CREATE_UPDATE_APPOINTMENT:
                switch(getAppointmentMode()){
                    case CREATE:
                        doCreateAppointmentAction();
                        break;
                    case UPDATE:
                        doUpdateAppointmentAction();
                        break;
                    case NONE:
                        break;
                }
                break;
            case REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT:
                switch(getScheduleSlotType()){
                    case EMERGENCY_SCHEDULE_SLOT:
                        deleteEmergencyAppointment();
                        break;
                    case BOOKED_SCHEDULE_SLOT:
                        makeEmergencyAppointment();
                        break;
                }
                break;
            case REQUEST_NEXT_DAY:
                doNextDayAction();
                break;
            case REQUEST_NON_SURGERY_DAY:
                mniSelectNonSurgeryDayActionPerformed();
                break;
            case REQUEST_NOW:
                doNowAction();
                break;
            case REQUEST_PREVIOUS_DAY:
                doPreviousDayAction();
                break;
            case REQUEST_PRINT_SCHEDULE:
                getMyController().getDescriptor().getViewDescription().setScheduleDay(dayDatePicker.getDate());
                ActionEvent actionEvent = new ActionEvent(this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.PRINT_SCHEDULE_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case REQUEST_SCHEDULE_DIARY_VIEW:
                setScheduleViewMode(ScheduleViewMode.DIARY);
                break;
            case REQUEST_SCHEDULE_LIST_VIEW:
                setScheduleViewMode(ScheduleViewMode.LIST);
                break;
            case REQUEST_SEARCH_AVAILABLE_SLOTS:
                doSearchAvailableSlotsAction();
                break;
            case REQUEST_SURGERY_DAY_EDITOR:
                mniSurgeryDaysEditorActionPerformed();
                break;
            case REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT:
                btnMarkSlotUnbookableActionPerformed();
                break;
        }
    }
    
    @Override
    public void dateChanged(DateChangeEvent e){
        getMyController().getDescriptor().getViewDescription().setScheduleDay(this.dayDatePicker.getDate());
            tblAppointments.clearSelection();
            ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            /*
            SwingUtilities.invokeLater(new Runnable() 
            {
                @Override
                public void run()
                {
                  setTitle(dayDatePicker.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " schedule");
                }
            });
*/
    }
    
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
    
    @Override
    public void mouseClicked(MouseEvent e){
        if (!tableValueChangedListenerActivated){
            int selectedRow = tblAppointments.rowAtPoint(e.getPoint());
            if (selectedRow!=-1 && tblAppointments.isRowSelected(selectedRow))
            tblAppointments.clearSelection(); // Deselect the clicked row
        }else tableValueChangedListenerActivated = false;
    }
    
    @Override
    public void mouseEntered(MouseEvent e){
        
    }
    
    @Override
    public void mouseExited(MouseEvent e){
        
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        
    }
    
    @Override
    public void mousePressed(MouseEvent e){
        
    }
    
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
                switch (getScheduleViewMode()){
                    case DIARY:
                        break;
                    case LIST:
                        populateScheduleListView();
                        tblAppointments.clearSelection();
                        break;
                }
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
    
    @Override
    public void tableChanged(TableModelEvent e){
        int row = e.getFirstRow();
        int column = e.getColumn();
        ScheduleListTableModel model =  
                (ScheduleListTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        Appointment appointment = model.getElementAt(row);
        appointment.setHasPatientBeenContacted(value);
        getMyController().getDescriptor().getViewDescription().setAppointment(appointment);
        tblAppointments.clearSelection();

        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.ScheduleViewControllerActionEvent.
                    APPOINTMENT_REMINDED_STATUS_UPDATE_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private boolean tableValueChangedListenerActivated = false;
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (e.getSource().equals(this.lsmForAppointmentsTable)){
            if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
                int selectedRow = tblAppointments.getSelectedRow();
                if (selectedRow!=-1){
                    ScheduleListTableModel model = 
                            (ScheduleListTableModel)tblAppointments.getModel();
                    Appointment appointment = model.getElementAt(selectedRow);
                    setScheduleSlotType(appointment);
                    getMyController().getDescriptor()
                            .getViewDescription().setAppointment(appointment);
                    tableValueChangedListenerActivated = true;
                    Patient patient = (Patient)tblAppointments.getModel().getValueAt(selectedRow, 1);
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
        }else if(e.getSource().equals(this.lsmForSloAvailabilityTable)){
            if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    doEmptySlotAvailabilityTableRowSelection(selectedRow);
                }
        }
    }
   
    private ListSelectionModel lsmForAppointmentsTable = null;
    private void setAppointmentTableListener(){
        this.tblAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmForAppointmentsTable = this.tblAppointments.getSelectionModel();
        lsmForAppointmentsTable.addListSelectionListener(this); 
        tblAppointments.addMouseListener(this);
    }
    
    private ListSelectionModel lsmForSloAvailabilityTable = null;
    private void setEmptySlotAvailabilityTableListener(){
        this.tblSlotAvailability.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmForSloAvailabilityTable = this.tblSlotAvailability.getSelectionModel();
        lsmForSloAvailabilityTable.addListSelectionListener(this); 
    }
    
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_CLOSE_NOTIFICATION.toString());
                ScheduleView.this.getMyController().actionPerformed(actionEvent);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                ScheduleView.this.getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        try{
            addInternalFrameListeners();
        
            setVisible(true);
            setTitle("Appointments");
            setClosable(false);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(true);
            setSelected(true);
            setSize(1075
                    ,690);
            toFront();
        }catch (PropertyVetoException ex){
            
        }catch(Exception ex){
            String message = ex.getMessage() + "\n"
                    + "Raised in ScheduleView::initialiseView()";
            ViewController.displayErrorMessage(message, 
                    "Schedule view controller error", JOptionPane.WARNING_MESSAGE);
        }
        setEmptySlotAvailabilityTableListener();

        this.btnClinicalNotesForSelectedAppointment
                .setActionCommand(Action.REQUEST_CLINICAL_NOTE_VIEW.toString());
        this.btnClinicalNotesForSelectedAppointment.addActionListener(this);
        
        btnCreateUpdateAppointment.setEnabled(false);
        btnMakeDeleteEmergencyAppointment.setEnabled(false);
        btnMarkCancelSlotUnbookable.setEnabled(false);
        btnCancelSelectedAppointment.setEnabled(false);
        btnClinicalNotesForSelectedAppointment.setEnabled(false);
        btnSelectTreatmentRequest.setEnabled(false);
        btnCloseView.setEnabled(true);

        btnCancelSelectedAppointment.setText(ScheduleViewActionCaption.CANCEL_APPOINTMENT._1());
        btnClinicalNotesForSelectedAppointment.setText(ScheduleViewActionCaption.CLINICAL_NOTES._1());
        btnCloseView.setText(ScheduleViewActionCaption.CLOSE_VIEW._1());
        btnCreateUpdateAppointment.setText(ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._1());
        btnMakeDeleteEmergencyAppointment.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT._1());
        btnMarkCancelSlotUnbookable.setText(ScheduleViewActionCaption.MARK_CANCEL_UNBOOKABLE_SLOT._1());
        btnSearchAvailableSlotsRequest.setText(ScheduleViewActionCaption.SEARCH_AVAILABLE_SLOTS._1());
        btnSelectTreatmentRequest.setText(ScheduleViewActionCaption.SELECT_TREATMENT._1());
        
        
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        
        btnCancelSelectedAppointment.setActionCommand(Action.REQUEST_CANCEL_APPOINTMENT.toString());
        btnClinicalNotesForSelectedAppointment.setActionCommand(Action.REQUEST_CLINICAL_NOTE_VIEW.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCreateUpdateAppointment.setActionCommand(Action.REQUEST_CREATE_UPDATE_APPOINTMENT.toString());
        btnMakeDeleteEmergencyAppointment.setActionCommand(Action.REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT.toString());
        btnMarkCancelSlotUnbookable.setActionCommand(Action.REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT.toString());
        btnNextDay.setActionCommand(Action.REQUEST_NEXT_DAY.toString());
        btnNow.setActionCommand(Action.REQUEST_NOW.toString());
        btnPreviousDay.setActionCommand(Action.REQUEST_PREVIOUS_DAY.toString());
        btnSearchAvailableSlotsRequest.setActionCommand(Action.REQUEST_SEARCH_AVAILABLE_SLOTS.toString());
        btnSelectTreatmentRequest.setActionCommand(Action.REQUEST_TREATMENT_VIEW.toString());
        
        btnCancelSelectedAppointment.addActionListener(this);
        btnClinicalNotesForSelectedAppointment.addActionListener(this);
        btnCloseView.addActionListener(this);
        btnCreateUpdateAppointment.addActionListener(this);
        btnMakeDeleteEmergencyAppointment.addActionListener(this);
        btnMarkCancelSlotUnbookable.addActionListener(this);
        btnNextDay.addActionListener(this);
        btnNow.addActionListener(this);
        btnPreviousDay.addActionListener(this);
        btnSearchAvailableSlotsRequest.addActionListener(this);
        btnSelectTreatmentRequest.addActionListener(this); 
        
        scheduleViewer.add(rdbList);
        scheduleViewer.add(rdbDiary);
        rdbList.setActionCommand(Action.REQUEST_SCHEDULE_LIST_VIEW.toString());
        rdbDiary.setActionCommand(Action.REQUEST_SCHEDULE_DIARY_VIEW.toString());
        rdbList.addActionListener(this);
        rdbDiary.addActionListener(this);
        rdbList.setSelected(true);
        setScheduleViewMode(ScheduleViewMode.LIST);
        
        this.mniCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.mniCloseView.addActionListener(this);
        this.mniSelectNonSurgeryDay.setActionCommand(Action.REQUEST_NON_SURGERY_DAY.toString());
        this.mniSelectNonSurgeryDay.addActionListener(this);
        this.mniPrintScheduleSelection.setActionCommand(Action.REQUEST_PRINT_SCHEDULE.toString());
        this.mniPrintScheduleSelection.addActionListener(this);
        this.mniSurgeryDaysEditor.setActionCommand(Action.REQUEST_SURGERY_DAY_EDITOR.toString());
        this.mniSurgeryDaysEditor.addActionListener(this);
        this.mniViewCancelledAppointments.setActionCommand(Action.REQUEST_CANCELLED_APPOINTMENT_VIEW.toString());
        dayDatePicker.addDateChangeListener(this);
        
        pnlSlotAvailability.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Unscheduled appointment slots", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        pnlScheduleDaySelection.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Schedule date selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        pnlScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Scheduledate selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        pnlScheduleViewSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Schedule view selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N

        this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().getControllerDescription().getSurgeryDaysAssignment());
        DatePickerSettings dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
        dps.setHighlightPolicy(this);
     
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
    
    
    
    private void doCreateAppointmentAction() {                                                     
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
    
    private void doUpdateAppointmentAction() {
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if(((ScheduleListTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient()==null){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if (!((ScheduleListTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient().getIsKeyDefined()){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else if (SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark()
                .equals(((ScheduleListTableModel)this.tblAppointments.getModel()).
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
            getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            APPOINTMENT_UPDATE_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }                                                    

    private void doCloseViewAction() {                                             

        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException e){
            
        }
    }                                            



    private void btnScanForEmptySlotsActionPerformed() {                                                     
        // TODO add your handling code here:
        LocalDate searchStartDate = dayDatePicker.getDate();
        getMyController().getDescriptor().getViewDescription().setScheduleDay(searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doNowAction(){
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getNowDateOrClosestAvailableAfterNow();
        dayDatePicker.setDate(day);
    }
    
    private void doPreviousDayAction(){
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getPreviousAvailableDateTo(day);
        dayDatePicker.setDate(day);
    }
    
    private void doNextDayAction(){
        LocalDate day = dayDatePicker.getDate();
        day = this.vetoPolicy.getNextAvailableDateTo(day);
        dayDatePicker.setDate(day);
    }
    
    private void doSearchAvailableSlotsAction(){
        LocalDate searchStartDate = dayDatePicker.getDate();
        getMyController().getDescriptor().getViewDescription().setScheduleDay(searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this,
            ActionEvent.ACTION_PERFORMED,
            ViewController.ScheduleViewControllerActionEvent.
            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }     
    
    private void doCancelSelectedAppointmentAction() {                                                             
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
            if ((((ScheduleListTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient()==null)){
                JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
            }   
        }
        int OKToCancelAppointment;
        getMyController().getDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));
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
    
    private void btnMarkSlotUnbookableActionPerformed() {                                                      
        switch(getUnbookableSlotMode()){
            case CANCEL:
                doUnbookableSlotInCancelMode();
                break;
            case MARK:
                doUnbookableSlotInMarkMode();
                break;
        }
    }
    
    private void mniSurgeryDaysEditorActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.SURGERY_DAYS_EDITOR_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }

    private void mniSelectNonSurgeryDayActionPerformed(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doCancelledAppointmentsViewAction() {                                                             
        ActionEvent actionEvent = new ActionEvent(this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                APPOINTMENTS_CANCELLED_VIEW_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
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
            appointment = ((ScheduleListTableModel)this.tblAppointments.getModel()).getElementAt(row);
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
        TitledBorder titledBorder = (TitledBorder)pnlScheduleForDay.getBorder();
        titledBorder.setTitle(tableTitle);
        pnlScheduleForDay.repaint();
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
    
    private void refreshAppointmentTableWithCurrentlySelectedDate(){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run()
            {
              setTitle(getMyController().getDescriptor().getViewDescription().getScheduleDay().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
              setIsViewInitialised(true);       
            }
        });
    }
    
    private void populateEmptySlotAvailabilityTable(ArrayList<Appointment> a){
        if (a==null) a = new ArrayList<>();
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
    /*
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
        
        this.pnlScheduleForDay.repaint();
    }*/
    
    private void populateScheduleListView(){
        ScheduleListTableModel model = 
                (ScheduleListTableModel)tblAppointments.getModel();
        model.removeAllElements();
        ArrayList<Appointment> schedule = makeEmergencyAppointmentsFirst(
                getMyController().getDescriptor()
                        .getControllerDescription().getAppointmentSlotsForDay());
        Iterator<Appointment> it = schedule.iterator();
        while (it.hasNext()){
            model.addElement(it.next());
        }
        
        doScheduleTitleRefresh(null);
        setTitle(getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
        tblAppointments.repaint();
        this.pnlScheduleForDay.repaint();
    }
    
    private JTable tblAppointments = null;
    private void configureScheduleListView(){
        tblAppointments = new JTable(new ScheduleListTableModel());
        scrAppointmentsForDayTable.setViewportView(tblAppointments);
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
        model.addTableModelListener(this);
        setAppointmentTableListener();
        
        this.tblAppointments.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        this.tblAppointments.setDefaultRenderer(LocalDateTime.class, new AppointmentsTableLocalDateTimeRenderer());
        this.tblAppointments.setDefaultRenderer(Patient.class, new AppointmentsTablePatientRenderer());
        JTableHeader tableHeader = this.tblAppointments.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true); 
        ViewController.setJTableColumnProperties(tblAppointments, scrAppointmentsForDayTable.getPreferredSize().width, 10,20,5,5,15,45);
        //populateScheduleListView();
    }
    
    private void configureScheduleDiaryView(){
        
    }
    
    /*
    private void ConfigureScheduleTable(){
        if (tableModel == null) {
            tableModel = new ScheduleListTableModel();
            tableModel.addTableModelListener(new TableModelListener(){
                Appointment appointment = null;
                @Override
                public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    ScheduleListTableModel model =  
                            (ScheduleListTableModel)e.getSource();
                    Boolean value = (Boolean)model.getValueAt(row, column);
                    appointment = model.getElementAt(row);
                    appointment.setHasPatientBeenContacted(value);
                    getMyController().getDescriptor().getViewDescription().setAppointment(appointment);
                    tblAppointments.clearSelection();
                    
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
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
        //28/03/2024this.tblAppointments.setDefaultRenderer(PatientNote.class, new AppointmentsTablePatientNoteRenderer());
        //this.tblAppointments.setModel(tableModel);
        //this.tblAppointments.setRowSelectionAllowed(false);
        //this.tblAppointments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int width = this.scrAppointmentsForDayTable.getPreferredSize().width;
        ViewController.setJTableColumnProperties(tblAppointments, 
                this.scrAppointmentsForDayTable.getPreferredSize().width, 
                10,20,5,5,15,45);
        
        TableColumnModel columnModel = this.tblAppointments.getColumnModel();
        //columnModel.getColumn(0).setPreferredWidth(185);
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        //columnModel.getColumn(1).setPreferredWidth(36);
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        //columnModel.getColumn(2).setPreferredWidth(36);
        columnModel.getColumn(2).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(3).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        //columnModel.getColumn(4).setMinWidth(350);
        columnModel.getColumn(4).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        //columnModel.getColumn(4).setCellRenderer(new ScheduleTableCellRenderer() );

        columnModel.getColumn(5).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        JTableHeader tableHeader = this.tblAppointments.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);  
    }*/
    
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

    private void updateDatePickerSettings(){
        DatePickerSettings dps = null;
        //this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().getViewDescription().getSurgeryDaysAssignmentValue());
        this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().
                getControllerDescription().getSurgeryDaysAssignment());
        dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scheduleViewer = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnMakeDeleteEmergencyAppointment = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnMarkCancelSlotUnbookable = new javax.swing.JButton();
        btnClinicalNotesForSelectedAppointment = new javax.swing.JButton();
        btnSelectTreatmentRequest = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        pnlScheduleDaySelection = new javax.swing.JPanel();
        pnlAppointmentDaySelector = new javax.swing.JPanel();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        btnNextDay = new javax.swing.JButton();
        btnPreviousDay = new javax.swing.JButton();
        btnNow = new javax.swing.JButton();
        pnlSlotAvailability = new javax.swing.JPanel();
        scrPanelSlotAvailability = new javax.swing.JScrollPane();
        tblSlotAvailability = new javax.swing.JTable();
        btnSearchAvailableSlotsRequest = new javax.swing.JButton();
        pnlScheduleForDay = new javax.swing.JPanel();
        scrAppointmentsForDayTable = new javax.swing.JScrollPane();
        pnlScheduleViewSelection = new javax.swing.JPanel();
        rdbList = new javax.swing.JRadioButton();
        rdbDiary = new javax.swing.JRadioButton();
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule operations"));

        btnCreateUpdateAppointment.setText(ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._1());

        btnMakeDeleteEmergencyAppointment.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT._1());

        btnCancelSelectedAppointment.setText(ScheduleViewActionCaption.CANCEL_APPOINTMENT._1());

        btnMarkCancelSlotUnbookable.setText(ScheduleViewActionCaption.MARK_CANCEL_UNBOOKABLE_SLOT._1());

        btnClinicalNotesForSelectedAppointment.setText(ScheduleViewActionCaption.CLINICAL_NOTES._1());
        btnClinicalNotesForSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClinicalNotesForSelectedAppointmentActionPerformed(evt);
            }
        });

        btnSelectTreatmentRequest.setText(ScheduleViewActionCaption.SELECT_TREATMENT._1());

        btnCloseView.setText(ScheduleViewActionCaption.CLOSE_VIEW._1());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMarkCancelSlotUnbookable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMakeDeleteEmergencyAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMakeDeleteEmergencyAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnMarkCancelSlotUnbookable, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlScheduleDaySelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule date selection"));

        pnlAppointmentDaySelector.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlAppointmentDaySelector.setPreferredSize(new java.awt.Dimension(200, 58));

        settings = new DatePickerSettings();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowEmptyDates(false);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);

        btnNextDay.setText("Tomorrow");
        btnNextDay.setMinimumSize(new java.awt.Dimension(60, 23));
        btnNextDay.setPreferredSize(new java.awt.Dimension(62, 23));

        btnPreviousDay.setText("Yesterday");

        btnNow.setText("Today");

        javax.swing.GroupLayout pnlAppointmentDaySelectorLayout = new javax.swing.GroupLayout(pnlAppointmentDaySelector);
        pnlAppointmentDaySelector.setLayout(pnlAppointmentDaySelectorLayout);
        pnlAppointmentDaySelectorLayout.setHorizontalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(btnPreviousDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnNow, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlAppointmentDaySelectorLayout.setVerticalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreviousDay)
                    .addComponent(btnNow)
                    .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlScheduleDaySelectionLayout = new javax.swing.GroupLayout(pnlScheduleDaySelection);
        pnlScheduleDaySelection.setLayout(pnlScheduleDaySelectionLayout);
        pnlScheduleDaySelectionLayout.setHorizontalGroup(
            pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScheduleDaySelectionLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlScheduleDaySelectionLayout.setVerticalGroup(
            pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pnlSlotAvailability.setBorder(javax.swing.BorderFactory.createTitledBorder("Available appointment slots"));

        tblSlotAvailability.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrPanelSlotAvailability.setViewportView(tblSlotAvailability);

        btnSearchAvailableSlotsRequest.setText("search for empty appointment slots");
        btnSearchAvailableSlotsRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchAvailableSlotsRequestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSlotAvailabilityLayout = new javax.swing.GroupLayout(pnlSlotAvailability);
        pnlSlotAvailability.setLayout(pnlSlotAvailabilityLayout);
        pnlSlotAvailabilityLayout.setHorizontalGroup(
            pnlSlotAvailabilityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSlotAvailabilityLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnSearchAvailableSlotsRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlSlotAvailabilityLayout.setVerticalGroup(
            pnlSlotAvailabilityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotAvailabilityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSlotAvailabilityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSearchAvailableSlotsRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder("Appointment schedule for"));

        scrAppointmentsForDayTable.setMaximumSize(new java.awt.Dimension(835, 333));
        scrAppointmentsForDayTable.setPreferredSize(new java.awt.Dimension(835, 333));

        javax.swing.GroupLayout pnlScheduleForDayLayout = new javax.swing.GroupLayout(pnlScheduleForDay);
        pnlScheduleForDay.setLayout(pnlScheduleForDayLayout);
        pnlScheduleForDayLayout.setHorizontalGroup(
            pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlScheduleForDayLayout.setVerticalGroup(
            pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlScheduleViewSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule view selection"));

        rdbList.setText("List view");

        rdbDiary.setText("Diary view");

        javax.swing.GroupLayout pnlScheduleViewSelectionLayout = new javax.swing.GroupLayout(pnlScheduleViewSelection);
        pnlScheduleViewSelection.setLayout(pnlScheduleViewSelectionLayout);
        pnlScheduleViewSelectionLayout.setHorizontalGroup(
            pnlScheduleViewSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleViewSelectionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rdbList)
                .addGap(47, 47, 47)
                .addComponent(rdbDiary)
                .addGap(103, 103, 103))
        );
        pnlScheduleViewSelectionLayout.setVerticalGroup(
            pnlScheduleViewSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleViewSelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleViewSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbDiary)
                    .addComponent(rdbList))
                .addGap(15, 15, 15))
        );

        mnuOptions.setText("Actions");

        mniViewCancelledAppointments.setText("View cancelled appointments");
        mnuOptions.add(mniViewCancelledAppointments);
        mnuOptions.add(jSeparator1);

        mniSurgeryDaysEditor.setText("Define which days are surgery days");
        mnuOptions.add(mniSurgeryDaysEditor);

        mniSelectNonSurgeryDay.setText("Open schedule on a non-surgery day");
        mnuOptions.add(mniSelectNonSurgeryDay);
        mnuOptions.add(jSeparator2);

        mniPrintScheduleSelection.setText("Print schedule");
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlScheduleDaySelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlScheduleViewSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addComponent(pnlSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnlScheduleDaySelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlScheduleViewSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(pnlSlotAvailability, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchAvailableSlotsRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchAvailableSlotsRequestActionPerformed
        // TODO add your handling code here:
        /*
        LocalDate searchStartDate = dayDatePicker.getDate();
        getMyController().getDescriptor().getViewDescription().setScheduleDay(searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this,
            ActionEvent.ACTION_PERFORMED,
            ViewController.ScheduleViewControllerActionEvent.
            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
*/
    }//GEN-LAST:event_btnSearchAvailableSlotsRequestActionPerformed

    private void btnClinicalNotesForSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClinicalNotesForSelectedAppointmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnClinicalNotesForSelectedAppointmentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelSelectedAppointment;
    private javax.swing.JButton btnClinicalNotesForSelectedAppointment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateAppointment;
    private javax.swing.JButton btnMakeDeleteEmergencyAppointment;
    private javax.swing.JButton btnMarkCancelSlotUnbookable;
    private javax.swing.JButton btnNextDay;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnPreviousDay;
    private javax.swing.JButton btnSearchAvailableSlotsRequest;
    private javax.swing.JButton btnSelectTreatmentRequest;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniPrintScheduleSelection;
    private javax.swing.JMenuItem mniSelectNonSurgeryDay;
    private javax.swing.JMenuItem mniSurgeryDaysEditor;
    private javax.swing.JMenuItem mniViewCancelledAppointments;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlAppointmentDaySelector;
    private javax.swing.JPanel pnlScheduleDaySelection;
    private javax.swing.JPanel pnlScheduleForDay;
    private javax.swing.JPanel pnlScheduleViewSelection;
    private javax.swing.JPanel pnlSlotAvailability;
    private javax.swing.JRadioButton rdbDiary;
    private javax.swing.JRadioButton rdbList;
    private javax.swing.ButtonGroup scheduleViewer;
    private javax.swing.JScrollPane scrAppointmentsForDayTable;
    private javax.swing.JScrollPane scrPanelSlotAvailability;
    private javax.swing.JTable tblSlotAvailability;
    // End of variables declaration//GEN-END:variables
}
