/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import view.support_classes.models.ScheduleDiaryTableModel;
import view.support_classes.renderers.ScheduleDiaryTablePatientRenderer;
import view.support_classes.renderers.ScheduleDiaryTableLocalDateTimeRenderer;
import view.views.dialog_views.DialogView;
import model.non_entity.SystemDefinition;
import model.non_entity.Slot;
import model.non_entity.Captions;
import model.non_entity.SystemDefinition.ScheduleSlotType;
import view.support_classes.renderers.ScheduleDiaryTableStringRenderer;
import view.dialogs.CustomComboBoxDialog;
import model.entity.Appointment;
import model.entity.Patient;
/*28/03/2024import model.PatientNote;*/
import controller.ViewController;
import controller.DesktopViewController;
import view.View;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENTS_FOR_DAY_RECEIVED;
import static controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED;
import static controller.ViewController.ViewMode.SCHEDULE_REFERENCED_DESKTOP_VIEW;
import static controller.ViewController.ViewMode.SCHEDULE_REFERENCED_FROM_PATIENT_VIEW;
import java.awt.Color;
import java.awt.Frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import view.support_classes.AppointmentDateVetoPolicy;

/**
 *
 * @author colin
 */
public class ScheduleDiaryView extends BookingView 
                                        implements  ActionListener, 
                                                    ListSelectionListener,
                                                    MouseListener,
                                                    DateChangeListener,
                                                    DateHighlightPolicy{

    /**
     * 
     * @param myViewType
     * @param controller
     * @param desktopView 
     */
    public ScheduleDiaryView(View.Viewer myViewType, 
            ViewController controller, 
            DesktopView desktopView) {
        setTitle("Appointment schedule");
        this.setMyViewType(myViewType);
        setMyController(controller); 
        setDesktopView(desktopView);
    }
    
    @Override
    public void initialiseView(){
        LocalDate day = getMyController().getDescriptor().getControllerDescription().getScheduleDay();
        initComponents();
        try{
            addInternalFrameListeners();
        
            setVisible(true);
            setClosable(false);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(true);
            setSelected(true);
            toFront();
            
        }catch (PropertyVetoException ex){
            
        }catch(Exception ex){
            String message = ex.getMessage() + "\n"
                    + "Raised in ScheduleView::initialiseView()";
            ViewController.displayErrorMessage(message, 
                    "Schedule view controller error", JOptionPane.WARNING_MESSAGE);
        }
        //setEmptySlotAvailabilityTableListener();
        
        mniPrintSchedule.setActionCommand(Action.REQUEST_PRINT_SCHEDULE.toString());
        mniPrintSchedule.addActionListener(this);
        mniEarlyBookingOption.setActionCommand(Action.REQUEST_EARLY_BOOKING_START_TIME.toString());
        mniEarlyBookingOption.addActionListener(this);
        mniLateBookingOption.setActionCommand(Action.REQUEST_LATE_BOOKING_END_TIME.toString());
        mniLateBookingOption.addActionListener(this);
        this.mniCloseView.setActionCommand(ScheduleListView.Action.REQUEST_CLOSE_VIEW.toString());
        this.mniCloseView.addActionListener(this);
        //this.mniColorPicker.setActionCommand(ScheduleListView.Action.REQUEST_COLOUR_PICKER.toString());
        //this.mniColorPicker.addActionListener(this);
        
        this.btnSwitchView.setActionCommand(ScheduleListView.Action.REQUEST_SWITCH_VIEW.toString());
        this.btnSwitchView.addActionListener(this);
        
        this.btnSelectBookableSlotsScanner.setEnabled(true);
        this.btnSelectBookableSlotsScanner.setActionCommand(ScheduleListView.Action.REQUEST_BOOKABLE_SLOT_SCANNER_VIEW.toString());
        this.btnSelectBookableSlotsScanner.addActionListener(this);
        
        this.btnSelectUnbookableSlotsScanner.setActionCommand(ScheduleListView.Action.REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW.toString());
        this.btnSelectUnbookableSlotsScanner.addActionListener(this);
        
        this.btnUndoCurrentSelectionRequest.setActionCommand(Action.REQUEST_UNDO_CURRENT_SELECTION.toString());
        this.btnUndoCurrentSelectionRequest.addActionListener(this);
        
        disableAllScheduleActionControlsExceptCloseView();
        
        //initialise captions for each control
        btnCancelAppointmentRequest.setText(Captions.ScheduleView.CANCEL_APPOINTMENT._1());
        btnClinicalNotesForAppointmentRequest.setText(Captions.ScheduleView.CLINICAL_NOTES._1());
        btnCloseView.setText(Captions.CLOSE_VIEW);
        btnCreateAppointmentRequest.setText(Captions.ScheduleView.CREATE_UPDATE_APPOINTMENT._1());
        btnExtendAppointmentEarlierLaterBothRequest.setText(Captions.ScheduleView.EXTEND_BOOKING._1());
        btnMoveBookingRequest.setText(Captions.ScheduleView.MOVE_BOOKING._1());
        btnShiftAppointmentEarlierLaterRequest.setText(Captions.ScheduleView.SHIFT_BOOKING._1());
        btnShortenAppointmentRequest.setText(Captions.ScheduleView.SHORTEN_BOOKING._1());
        btnTreatmentForAppointmentRequest.setText(Captions.ScheduleView.SELECT_TREATMENT._1());
        
        btnNextDay.setText(Captions.ScheduleView.NEXT_DAY._1());
        btnNow.setText(Captions.ScheduleView.TODAY._1());
        btnPreviousDay.setText(Captions.ScheduleView.PREVIOUS_DAY._1());

        btnCloseView.setActionCommand(ScheduleListView.Action.REQUEST_CLOSE_VIEW.toString());
        
        btnCancelAppointmentRequest.setActionCommand(Action.REQUEST_CANCEL_APPOINTMENT.toString());
        btnClinicalNotesForAppointmentRequest.setActionCommand(Action.REQUEST_CLINICAL_NOTE_VIEW.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCreateAppointmentRequest.setActionCommand(Action.REQUEST_CREATE_APPOINTMENT.toString());
        btnExtendAppointmentEarlierLaterBothRequest.setActionCommand(Action.REQUEST_EXTEND_APPOINTMENT.toString());
        btnMoveBookingRequest.setActionCommand(Action.REQUEST_MOVE_BOOKING.toString());
        btnShiftAppointmentEarlierLaterRequest.setActionCommand(Action.REQUEST_SHIFT_APPOINTMENT.toString());
        btnShortenAppointmentRequest.setActionCommand(Action.REQUEST_SHORTEN_APPOINTMENT.toString());
        btnTreatmentForAppointmentRequest.setActionCommand(Action.REQUEST_TREATMENT_VIEW.toString());
        
        btnNextDay.setActionCommand(Action.REQUEST_NEXT_DAY.toString());
        btnNow.setActionCommand(Action.REQUEST_NOW.toString());
        btnPreviousDay.setActionCommand(Action.REQUEST_PREVIOUS_DAY.toString());
        //btnSearchAvailableSlotsRequest.setActionCommand(Action.REQUEST_SEARCH_AVAILABLE_SLOTS.toString());
        
        
        btnCancelAppointmentRequest.addActionListener(this);
        btnClinicalNotesForAppointmentRequest.addActionListener(this);
        btnCloseView.addActionListener(this);
        btnCreateAppointmentRequest.addActionListener(this);
        btnExtendAppointmentEarlierLaterBothRequest.addActionListener(this);
        btnShiftAppointmentEarlierLaterRequest.addActionListener(this);
        btnShortenAppointmentRequest.addActionListener(this);
        btnMoveBookingRequest.addActionListener(this);
        btnNextDay.addActionListener(this);
        btnNow.addActionListener(this);
        btnPreviousDay.addActionListener(this);
        //btnSearchAvailableSlotsRequest.addActionListener(this);
        btnTreatmentForAppointmentRequest.addActionListener(this); 

        this.vetoPolicy = new AppointmentDateVetoPolicy(getMyController().getDescriptor().getControllerDescription().getSurgeryDaysAssignment());
        DatePickerSettings dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
        dps.setHighlightPolicy(this);
        
        day = getMyController().getDescriptor().getControllerDescription().getScheduleDay();
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
        //dayDatePicker.setDate(day);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dayDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        
        dayDatePicker.addDateChangeListener(this);
        
        pnlViewSwitch.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "View format", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        pnlViewSwitch.setBackground(new java.awt.Color(220,220,220));
        
        pnlScheduleDaySelection.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Schedule date selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        pnlScheduleDaySelection.setBackground(new java.awt.Color(220,220,220));
        
        pnlScheduleTables.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Schedule details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        //pnlScheduleTables.setBackground(new java.awt.Color(220,220,220));
        
        pnlSlotScanner.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Slot scanner", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        pnlSlotScanner.setBackground(new java.awt.Color(220,220,220));
        
        pnlScheduleOperations.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Action", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        pnlScheduleOperations.setBackground(new java.awt.Color(220,220,220));
        
        pnlUndoSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                ".", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        pnlUndoSelection.setBackground(new java.awt.Color(220,220,220));
        
        pnlUndoSelection.setBackground(new java.awt.Color(220,220,220));
        
        configureScheduleDiaryView();
        this.clearSelectionFromScheduleTable();
        this.disableAllScheduleActionControlsExceptCloseView();
        dayDatePicker.setDate(day);
        //refreshAppointmentTableWithCurrentlySelectedDate();
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
              setTitle(getMyController().getDescriptor().getViewDescription().getScheduleDay().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "Schedule");
              setIsViewInitialised(true);       
            }
        });
    }
    
    
    private AppointmentDateVetoPolicy vetoPolicy = null;
    
    @Override
    public void actionPerformed(ActionEvent e){
        DialogView dialog = null;
        List<LocalDateTime> items = null;
        LocalDateTime timeStart = null;
        LocalDateTime timeEnd = null;
        ActionEvent actionEvent = null;
        Appointment appointment = null;
        Patient patient = null;
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_EARLY_BOOKING_START_TIME:
                items = new ArrayList<LocalDateTime>();
                appointment = (Appointment)getMyController().getDescriptor().getControllerDescription()
                                .getAppointmentSlotsForDayInListFormat().get(0);
                timeEnd = appointment.getStart();
                timeStart = timeEnd.minusHours(1);
                for (LocalDateTime item = timeStart;item.isBefore(timeEnd); item=item.plusMinutes(5)){
                    items.add(item);
                }
                dialog = new View().make(
                        View.Viewer.EARLY_BOOKING_START_EDITOR_DIALOG, 
                        this, 
                        items,
                        "Early booking start time editor",
                        "Select start time from list").getDialogView();
                if (getDialogView().getSelectedItem()!=null){
                    getMyController().getDescriptor().getViewDescription()
                            .setEarlyBookingStartTime((LocalDateTime)getDialogView().getSelectedItem());
                    actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                            ViewController.ScheduleViewControllerActionEvent.FIRST_APPOINTMENT_START_TIME_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent); 
                }
                break;
            case REQUEST_LATE_BOOKING_END_TIME:
                items = new ArrayList<>();
                int count = getMyController().getDescriptor().getControllerDescription()
                                .getAppointmentSlotsForDayInListFormat().size();
                appointment = (Appointment)getMyController().getDescriptor().getControllerDescription()
                                .getAppointmentSlotsForDayInListFormat().get(count-1);
                //if (appointment.getIsKeyDefined()){
                    timeEnd = appointment.getStart().plusMinutes(appointment.getDuration().toMinutes());
                    timeStart = timeEnd;
                    timeEnd = timeEnd.plusHours(1);
                    for (LocalDateTime item = timeStart;item.isBefore(timeEnd.plusMinutes(5)); item=item.plusMinutes(5)){
                        items.add(item);
                    }
                    dialog = new View().make(
                        View.Viewer.LATE_BOOKING_END_EDITOR_DIALOG, 
                        this, 
                        items,
                        "Late booking end time editor",
                        "Select end time from list").getDialogView();
                    if (getDialogView().getSelectedItem()!=null){
                        getMyController().getDescriptor().getViewDescription()
                                .setLateBookingEndTime((LocalDateTime)getDialogView().getSelectedItem());
                        actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                ViewController.ScheduleViewControllerActionEvent.LAST_APPOINTMENT_END_TIME_REQUEST.toString());
                        this.getMyController().actionPerformed(actionEvent); 
                    }
                //}
                break;
            case REQUEST_BOOKABLE_SLOT_SCANNER_VIEW:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                BOOKABLE_SLOT_SCANNER_VIEW_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW:
                actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                UNBOOKABLE_SLOT_SCANNER_VIEW_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case REQUEST_UNDO_CURRENT_SELECTION:
                this.clearSelectionFromScheduleTable();
                break;
            case REQUEST_CANCEL_APPOINTMENT:
                if (doAppointmentCancelConfirmation()==JOptionPane.YES_OPTION){
                    this.setViewDescriptorAppointment(this.getSelectedAppointmentFromDiary());
                    actionEvent = new ActionEvent(this, 
                            ActionEvent.ACTION_PERFORMED,
                            ViewController.ScheduleViewControllerActionEvent.APPOINTMENT_CANCEL_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }
                break;
            case REQUEST_CLINICAL_NOTE_VIEW:
                this.setViewDescriptorAppointment(this.getSelectedAppointmentFromDiary());
                doClinicalNoteRequest();
                break;
            case REQUEST_CLOSE_VIEW:
                doCloseViewAction();
                break;
            case REQUEST_COLOUR_PICKER:
                //doColourPickerRequest2();
                break;
            case REQUEST_CREATE_APPOINTMENT:               
                ArrayList<Patient> patients = getMyController().getDescriptor().
                        getControllerDescription().getPatients();
                
                dialog = new View().make(
                        View.Viewer.PATIENT_SELECTION_DIALOG, 
                        this, 
                        patients,
                        "Patient editor",
                        "Select patient from list").getDialogView();
                patient = (Patient)getDialogView().getSelectedItem();
                if (patient!=null){
                    appointment = getAppointmentFromDiaryWithUpdates();
                    appointment.setPatient(patient);
                    setViewDescriptorAppointment(appointment);
                    actionEvent = new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.CREATE_APPOINTMENT_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }
                break;
            case REQUEST_EXTEND_APPOINTMENT:
                switch (getExtendAction()){
                    case LATER:
                        appointment = getSelectedAppointmentFromDiary();
                        appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                        this.setViewDescriptorAppointment(appointment);
                        break;
                    case EARLIER:
                        appointment = getSelectedAppointmentFromDiary();
                        appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                        appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                        this.setViewDescriptorAppointment(appointment);
                        break;
                    case BOTH:
                        appointment = getSelectedAppointmentFromDiary();
                        appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                        appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                        this.setViewDescriptorAppointment(appointment);
                }
                doUpdateAppointmentequest();
                break;
            case REQUEST_MOVE_BOOKING:
                getMyController().getDescriptor().getViewDescription()
                        .setAppointment(getSelectedAppointmentFromDiary());
                doUpdateAppointmentActionForDiary();
                break;
            case REQUEST_NEXT_DAY:
                doNextDayAction();
                break;
            case REQUEST_NOW:
                doNowAction();
                break;
            case REQUEST_PREVIOUS_DAY:
                doPreviousDayAction();
                break;
            case REQUEST_PRINT_SCHEDULE:
                doPrintScheduleRequest();
                break;
            case REQUEST_SHIFT_APPOINTMENT:
                switch(getShiftAction()){
                    case LATER:
                        appointment = getSelectedAppointmentFromDiary();
                        appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                        this.setViewDescriptorAppointment(appointment);
                        break;
                    case EARLIER:
                        appointment = getSelectedAppointmentFromDiary();
                        appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                        this.setViewDescriptorAppointment(appointment);
                        break;
                }
                doUpdateAppointmentequest();
                break;
            case REQUEST_SHORTEN_APPOINTMENT:
                appointment = getSelectedAppointmentFromDiary();
                appointment.setDuration(getAppointmentFromDiaryWithUpdates().getDuration());
                this.setViewDescriptorAppointment(appointment);
                doUpdateAppointmentequest();
                break;
            case REQUEST_SWITCH_VIEW:
                /**
                 * flagging a view switch is pending because
                 * -- the VC will send this view a CLOSE_VIEW_REQUEST_RECEIVED property change event 
                 * -- and its important that in this pending state the internalFrameClosing listener is removed
                 * -- else the listener view will send a VIEW_CLOSED_NOTIFICATION to the VC on the view's closure 
                 * -- and the VC would then request to be removed which is not required
                 * On closure of the view the VC will open a new ScheduleDiaryView in its place
                 */
                setIsViewSwitchPending(true);
                actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.SWITCH_VIEW_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case REQUEST_TREATMENT_VIEW:
                this.setViewDescriptorAppointment(this.getSelectedAppointmentFromDiary());
                actionEvent = new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break; 
            case NONE:
                Toolkit.getDefaultToolkit().beep();
                if (getSelection().length == 1){
                    Slot slot = getScheduleDiaryTableModel().getElementAt(getSelection()[0]);
                    if (slot.getIsBooked()){
                        if (!slot.getIsFirstSlotOfAppointment()) {
                            this.clearSelectionFromScheduleTable();
                        }
                    }else clearSelectionFromScheduleTable();
                }else {
                    clearSelectionFromScheduleTable();
                }
                break;

        }
        this.disableAllScheduleActionControlsExceptCloseView();
        clearSelectionFromScheduleTable();
        this.btnCloseView.setEnabled(true);

    }
    
    @Override
    public void dateChanged(DateChangeEvent e){
        getMyController().getDescriptor().getViewDescription().setScheduleDay(this.dayDatePicker.getDate());
            clearSelectionFromScheduleTable();
            ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
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
    
    private boolean tableValueChangedListenerActivated = false;
    @Override
    public void mouseClicked(MouseEvent e){
        if (e.getSource() == tblScheduleMorning){
            if (!tableValueChangedListenerActivated){
                int selectedRow = tblScheduleMorning.rowAtPoint(e.getPoint());
                if (selectedRow!=-1 && tblScheduleMorning.isRowSelected(selectedRow))
                tblScheduleMorning.clearSelection(); // Deselect the clicked row
            }else tableValueChangedListenerActivated = false;
        }else if (e.getSource() == tblScheduleAfternoon){
            if (!tableValueChangedListenerActivated){
                int selectedRow = tblScheduleAfternoon.rowAtPoint(e.getPoint());
                if (selectedRow!=-1 && tblScheduleAfternoon.isRowSelected(selectedRow))
                tblScheduleAfternoon.clearSelection(); // Deselect the clicked row
            }else tableValueChangedListenerActivated = false;
        }else if (e.getSource() == tblScheduleEvening){
            if (!tableValueChangedListenerActivated){
                int selectedRow = tblScheduleEvening.rowAtPoint(e.getPoint());
                if (selectedRow!=-1 && tblScheduleEvening.isRowSelected(selectedRow))
                tblScheduleEvening.clearSelection(); // Deselect the clicked row
            }else tableValueChangedListenerActivated = false;
        }
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
        ViewController.ScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.ScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        //setViewDescriptor((Descriptor)e.getNewValue());
        switch (propertyName){
            case CLOSE_VIEW_REQUEST_RECEIVED:
                if (getIsViewSwitchPending()) removeInternalFrameListener(internalFrameAdapter);
                doCloseViewAction();
                break;
            case APPOINTMENTS_FOR_DAY_RECEIVED:
                
                //javax.swing.SwingUtilities.invokeLater(new Runnable(){
                //    @Override
                //    public void run(){
                        populateScheduleDiaryView();
                //    }
                //});
                break;
            case APPOINTMENT_SCHEDULE_ERROR_RECEIVED:
                if (e.getSource() instanceof DesktopViewController){
                    String message = getMyController().getDescriptor().getControllerDescription().getError();
                    JOptionPane.showInternalMessageDialog(this, message, "View error",JOptionPane.WARNING_MESSAGE);
                }
                /*else populateEmptySlotAvailabilityTable(
                        getMyController().getDescriptor().getControllerDescription().getAppointments());*/
                break;
                
        }
    }
    
    public void valueChanged(ListSelectionEvent e){
        ActionEvent actionEvent = null;
        Appointment appointment = null;
        int row = 0;
        JTable sourceTable = null;
        int[] selectedRows = null;
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            
            if (e.getSource().equals(this.lsmForScheduleMorningTable)){
                if(this.tblScheduleMorning.isFocusOwner()){
                    selectedRows = tblScheduleMorning.getSelectedRows();
                }
                if(selectedRows!=null){
                    if(selectedRows.length>0){
                        setSelection(selectedRows,tblScheduleMorning);
                    }else setSelection(null,tblScheduleMorning);
                }

            }else if (e.getSource().equals(this.lsmForScheduleAfternoonTable)){
                if(this.tblScheduleAfternoon.isFocusOwner()){
                    selectedRows = tblScheduleAfternoon.getSelectedRows();
                }
                if(selectedRows!=null){
                    if(selectedRows.length>0){
                        setSelection(selectedRows,tblScheduleAfternoon);
                    }else setSelection(null,tblScheduleAfternoon);
                }
                
            }else if (e.getSource().equals(this.lsmForScheduleEveningTable)){
                if(this.tblScheduleEvening.isFocusOwner()){
                    selectedRows = tblScheduleEvening.getSelectedRows();
                }
                if(selectedRows!=null){
                    if(selectedRows.length>0){
                        setSelection(selectedRows,tblScheduleEvening);
                    }else setSelection(null,tblScheduleEvening);
                }
            }
            if (getSelection()!=null){
                if (getSelection().length > 0){
                    Slot slot = getScheduleSlotModel().get(getSelection()[0]);
                    setScheduleSlotType(slot);
                    setCurrentScheduleDiaryAction(getScheduleDiaryAction());
                    doScheduleDiaryActionRequest(getCurrentScheduleDiaryAction());
                    disableAllScheduleActionControlsExceptCloseView();
                    this.btnCloseView.setEnabled(true);
                    if ((!(getScheduleSlotType().equals(ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT))) &&
                                    (!(getScheduleSlotType().equals(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT)))){
                        switch(getCurrentScheduleDiaryAction()){
                            case CLINICAL_NOTES_TREATMENT:
                                    this.btnClinicalNotesForAppointmentRequest.setEnabled(true);
                                    this.btnTreatmentForAppointmentRequest.setEnabled(true);
                                    this.doScheduleTitleRefresh(getSelectedAppointmentFromDiary().getPatient());
                                break;
                            case CANCEL_MOVE_APPOINTMENT:
                                this.btnCancelAppointmentRequest.setEnabled(true);
                                this.setAppointmentMode(AppointmentMode.UPDATE);
                                this.btnMoveBookingRequest.setEnabled(true);
                                if (getSelection().length==1){
                                    this.btnClinicalNotesForAppointmentRequest.setEnabled(true);
                                    this.btnTreatmentForAppointmentRequest.setEnabled(true);
                                }
                                this.doScheduleTitleRefresh(null);
                                break;
                            case CREATE_APPOINTMENT:
                                this.btnCreateAppointmentRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case SINGLE_SLOT_EXTEND_EARLIER_ACTION:
                                setExtendAction(ExtendAction.EARLIER);
                                actionEvent = new ActionEvent(this, 
                                        ActionEvent.ACTION_PERFORMED,
                                        Action.REQUEST_EXTEND_APPOINTMENT.toString());
                                this.actionPerformed(actionEvent);
                                break;
                            case SINGLE_SLOT_EXTEND_LATER_ACTION:
                                setExtendAction(ExtendAction.LATER);
                                actionEvent = new ActionEvent(this, 
                                        ActionEvent.ACTION_PERFORMED,
                                        Action.REQUEST_EXTEND_APPOINTMENT.toString());
                                this.actionPerformed(actionEvent);
                                break;
                            case SINGLE_SLOT_SHIFT_EARLIER_ACTION:
                                setShiftAction(ShiftAction.EARLIER);
                                actionEvent = new ActionEvent(this, 
                                        ActionEvent.ACTION_PERFORMED,
                                        Action.REQUEST_SHIFT_APPOINTMENT.toString());
                                this.actionPerformed(actionEvent);
                                break;
                            case SINGLE_SLOT_SHIFT_LATER_ACTION:
                                setShiftAction(ShiftAction.LATER);
                                actionEvent = new ActionEvent(this, 
                                        ActionEvent.ACTION_PERFORMED,
                                        Action.REQUEST_SHIFT_APPOINTMENT.toString());
                                this.actionPerformed(actionEvent);
                                break;
                            case EXTEND_APPOINTMENT_LATER:
                                setExtendAction(ExtendAction.LATER);
                                this.btnExtendAppointmentEarlierLaterBothRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case EXTEND_APPOINTMENT_EARLIER:
                                setExtendAction(ExtendAction.EARLIER);
                                this.btnExtendAppointmentEarlierLaterBothRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case EXTEND_APPOINTMENT_EARLIER_AND_LATER:
                                this.btnExtendAppointmentEarlierLaterBothRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case SHIFT_APPOINTMENT_LATER:
                                setShiftAction(ShiftAction.LATER);
                                this.btnShiftAppointmentEarlierLaterRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case SHIFT_APPOINTMENT_EARLIER:
                                setShiftAction(ShiftAction.EARLIER);
                                this.btnShiftAppointmentEarlierLaterRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case EXTEND_SHIFT_APPOINTMENT_LATER:
                                setExtendAction(ExtendAction.LATER);
                                this.btnExtendAppointmentEarlierLaterBothRequest.setEnabled(true);
                                setShiftAction(ShiftAction.LATER);
                                this.btnShiftAppointmentEarlierLaterRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case EXTEND_SHIFT_APPOINTMENT_EARLIER:
                                setExtendAction(ExtendAction.EARLIER);
                                this.btnExtendAppointmentEarlierLaterBothRequest.setEnabled(true);
                                setShiftAction(ShiftAction.EARLIER);
                                this.btnShiftAppointmentEarlierLaterRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case SHORTEN_APPOINTMENT:
                                this.btnShortenAppointmentRequest.setEnabled(true);
                                this.doScheduleTitleRefresh(null);
                                break;
                            case RESET_SELECTION:
                                clearSelectionFromScheduleTable();

                                break;

                        }
                    }else{//not emergency or unbookable slot
                        String message = null;
                        switch(getScheduleSlotType()){
                            case EMERGENCY_SCHEDULE_SLOT:
                                message = "Editing emergency appointments can only be managed in the LIST format of the schedule view";
                                JOptionPane.showInternalMessageDialog(this,message,"View error",JOptionPane.WARNING_MESSAGE);
                                break;
                            case UNBOOKABLE_SCHEDULE_SLOT:
                                message = "Editing unbookable slots can only be managed in the LIST format of the schedule view";
                                JOptionPane.showInternalMessageDialog(this,message,"View error",JOptionPane.WARNING_MESSAGE);
                                break;
                        }
                        this.clearSelectionFromScheduleTable();
                    }
                }
            }
        }
    }

    private void doCloseViewAction() {                                             

        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException e){
            
        }
        
    }
    
    /**
     * on entry ViewDescriptor.ViewMode initialised appropriately for print request (with or without contact details)
     */
    private void doPrintScheduleRequest(){
        getMyController().getDescriptor().getViewDescription().setScheduleDay(dayDatePicker.getDate());
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.PRINT_SCHEDULE_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
        doOpenDocumentForPrinting(SystemDefinition.getPMSPrintFolder() 
                + SystemDefinition.FILENAME_FOR_SCHEDULE);
    }
    
    private void configureScheduleDiaryView(){
        this.tblScheduleMorning = new JTable(new ScheduleDiaryTableModel());
        this.scrMorningTable.setViewportView(tblScheduleMorning);
        ScheduleDiaryTableModel model = (ScheduleDiaryTableModel)tblScheduleMorning.getModel();
        //addScheduleMorningTableListeners();
        this.tblScheduleMorning.setDefaultRenderer(LocalDateTime.class, new ScheduleDiaryTableLocalDateTimeRenderer());
        this.tblScheduleMorning.setDefaultRenderer(Patient.class, new ScheduleDiaryTablePatientRenderer());
        tblScheduleMorning.getColumnModel().getColumn(2).setCellRenderer(new ScheduleDiaryTableStringRenderer());
        JTableHeader tableHeader = this.tblScheduleMorning.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true); 
        ViewController.setJTableColumnProperties(tblScheduleMorning, scrMorningTable.getPreferredSize().width, 10,40,50);
        
        this.tblScheduleAfternoon = new JTable(new ScheduleDiaryTableModel());
        this.scrAfternoonTable.setViewportView(tblScheduleAfternoon);
        model = (ScheduleDiaryTableModel)tblScheduleAfternoon.getModel();
        //addScheduleAfternoonTableListeners();
        this.tblScheduleAfternoon.setDefaultRenderer(LocalDateTime.class, new ScheduleDiaryTableLocalDateTimeRenderer());
        this.tblScheduleAfternoon.setDefaultRenderer(Patient.class, new ScheduleDiaryTablePatientRenderer());
        tblScheduleAfternoon.getColumnModel().getColumn(2).setCellRenderer(new ScheduleDiaryTableStringRenderer());
        tableHeader = this.tblScheduleAfternoon.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true); 
        ViewController.setJTableColumnProperties(tblScheduleAfternoon, scrAfternoonTable.getPreferredSize().width, 10,40,50);
        
        this.tblScheduleEvening = new JTable(new ScheduleDiaryTableModel());
        this.scrEveningTable.setViewportView(tblScheduleEvening);
        model = (ScheduleDiaryTableModel)tblScheduleEvening.getModel();
        //addScheduleEveningTableListeners();
        this.tblScheduleEvening.setDefaultRenderer(LocalDateTime.class, new ScheduleDiaryTableLocalDateTimeRenderer());
        this.tblScheduleEvening.setDefaultRenderer(Patient.class, new ScheduleDiaryTablePatientRenderer());
        tblScheduleEvening.getColumnModel().getColumn(2).setCellRenderer(new ScheduleDiaryTableStringRenderer());
        tableHeader = this.tblScheduleEvening.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true); 
        ViewController.setJTableColumnProperties(tblScheduleEvening, scrEveningTable.getPreferredSize().width, 10,40,50);
    }
    
    /**
     * setScheduleSlotData() method
     * -- fetches each type of appointment object (unbookable, bookable, booked)from the collection of appointment data in the ControllerDescription
     * -- unpacks the appointment type into a contiguous set of 5 minute intervals (slots), each interval being represented by a row in the table
     * -- initialises column 0 of the first row of the table using the start time of the first appointment object in the collection sent from the controller
     * -- populating subsequent rows in the table from the previous row's start time plus 5 minutes
     * -- the resulting table of data is then divided into 3 'tables' of data
     * ---- each 'table' representing the contents of
     * ------ the 'morning' table
     * ------ the 'afternoon' table
     * ------ the 'evening' table
     */
    private ArrayList<Slot> scheduleSlotData = null;
    private ArrayList<Slot> morningSlotData = null;
    private ArrayList<Slot> afternoonSlotData = null;
    private ArrayList<Slot> eveningSlotData = null;
    private ArrayList<ArrayList> slotData = null;
    private void setScheduleSlotData(){
        scheduleSlotData = new ArrayList<Slot>();
        morningSlotData = new ArrayList<Slot>();
        afternoonSlotData = new ArrayList<Slot>();
        eveningSlotData = new ArrayList<Slot>();
        slotData = new ArrayList<ArrayList>();
        ArrayList<Slot> schedule = getMyController().getDescriptor()
                        .getControllerDescription().getAppointmentSlotsForDayInDiaryFormat();
        Iterator<Slot> it = schedule.iterator();
        int rowCount = 0;
        while (it.hasNext()){
            Slot slot = (Slot)it.next();
            for(Slot _slot : slot.get()){
                scheduleSlotData.add(_slot);
                rowCount++;
            }
        }
        LocalDateTime start = scheduleSlotData.get(0).getStart(); //first slot start time
        for (int row=rowCount; row<108; row++){
        /** 30/12/2024 code update*/
        //for (int row=rowCount; row<scheduleSlotData.size(); row++){
            Slot slot = new Slot(new Appointment());
            slot.setStart(start.plusMinutes(row*5));
            scheduleSlotData.add(slot);
        } 

        it = scheduleSlotData.iterator();
        rowCount = 0;
        while (it.hasNext()){
            Slot slot = (Slot)it.next();
            if (rowCount<36) morningSlotData.add(slot);
            else if (rowCount<72) afternoonSlotData.add(slot);
            else eveningSlotData.add(slot);
            rowCount++;
        }
        slotData.add(morningSlotData);
        slotData.add(afternoonSlotData);
        slotData.add(eveningSlotData); 
    }
    private ArrayList<ArrayList> getScheduleSlotData(){
        return slotData;
    }
    
    private ArrayList<Slot> getScheduleSlotModel(){
        return scheduleSlotData;
    }
    private void populateScheduleDiaryView(){
        int MORNING = 0;
        int AFTERNOON = 1;
        int EVENING = 2;
        ScheduleDiaryTableModel modelMorning = null;
        ScheduleDiaryTableModel modelAfternoon  = null;
        ScheduleDiaryTableModel modelEvening = null;
        
        setScheduleSlotData();
        
        modelMorning = (ScheduleDiaryTableModel)tblScheduleMorning.getModel();
        modelMorning.removeAllElements();
        modelMorning.setData(getScheduleSlotData().get(MORNING));
        
        modelAfternoon = (ScheduleDiaryTableModel)tblScheduleAfternoon.getModel();
        modelAfternoon.removeAllElements();
        modelAfternoon.setData(getScheduleSlotData().get(AFTERNOON));
        
        modelEvening = (ScheduleDiaryTableModel)tblScheduleEvening.getModel();
        modelEvening.removeAllElements();
        modelEvening.setData(getScheduleSlotData().get(EVENING));
        setScheduleSlotData();

        this.clearSelectionFromScheduleTable();
        setTitle(getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
        this.doScheduleTitleRefresh(null);
        repaint();
    }
    
    private boolean isViewSwitchPending = false;
    private void setIsViewSwitchPending(boolean value){
        isViewSwitchPending = value;
    }
    private boolean getIsViewSwitchPending(){
        return isViewSwitchPending;
    }
    
    private InternalFrameAdapter internalFrameAdapter = null;
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ScheduleDiaryView.this.removeInternalFrameListener(internalFrameAdapter);
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleDiaryView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_CLOSE_NOTIFICATION.toString());
                ScheduleDiaryView.this.getMyController().actionPerformed(actionEvent);
                
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleDiaryView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                ScheduleDiaryView.this.getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }
    
    private void addListSelectionAndMouseListenersToTables(){
        addScheduleMorningTableListeners();
        addScheduleAfternoonTableListeners();
        addScheduleEveningTableListeners();
    }
    
    private void removeListSelectionAndMouseListenersFromTables(){
        removeScheduleMorningTableListeners();
        removeScheduleAfternoonTableListeners();
        removeScheduleEveningTableListeners();
    }
    
    private ListSelectionModel lsmForScheduleMorningTable = null;
    private void addScheduleMorningTableListeners(){
        this.tblScheduleMorning.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblScheduleMorning.setRowSelectionAllowed(true);
        tblScheduleMorning.setColumnSelectionAllowed(false);
        lsmForScheduleMorningTable = this.tblScheduleMorning.getSelectionModel();
        lsmForScheduleMorningTable.addListSelectionListener(this); 
        //tblScheduleMorning.addMouseListener(this);
    }
    private ListSelectionModel lsmForScheduleAfternoonTable = null;
    private void addScheduleAfternoonTableListeners(){
        this.tblScheduleAfternoon.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblScheduleAfternoon.setRowSelectionAllowed(true);
        tblScheduleAfternoon.setColumnSelectionAllowed(false);
        lsmForScheduleAfternoonTable = this.tblScheduleAfternoon.getSelectionModel();
        lsmForScheduleAfternoonTable.addListSelectionListener(this); 
        //tblScheduleAfternoon.addMouseListener(this);
    }
    private ListSelectionModel lsmForScheduleEveningTable = null;
    private void addScheduleEveningTableListeners(){
        this.tblScheduleEvening.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblScheduleEvening.setRowSelectionAllowed(true);
        tblScheduleEvening.setColumnSelectionAllowed(false);
        lsmForScheduleEveningTable = this.tblScheduleEvening.getSelectionModel();
        lsmForScheduleEveningTable.addListSelectionListener(this); 
        //tblScheduleEvening.addMouseListener(this);
    }
    
    private void removeScheduleMorningTableListeners(){
        lsmForScheduleMorningTable = this.tblScheduleMorning.getSelectionModel();
        lsmForScheduleMorningTable.removeListSelectionListener(this);
        //tblScheduleMorning.removeMouseListener(this);
    }
    private void removeScheduleAfternoonTableListeners(){
        lsmForScheduleAfternoonTable = this.tblScheduleAfternoon.getSelectionModel();
        lsmForScheduleAfternoonTable.removeListSelectionListener(this);
        //tblScheduleAfternoon.removeMouseListener(this);
    }
    private void removeScheduleEveningTableListeners(){
        lsmForScheduleEveningTable = this.tblScheduleEvening.getSelectionModel();
        lsmForScheduleEveningTable.removeListSelectionListener(this);
        //tblScheduleEvening.removeMouseListener(this);
    }

    
    
    private void clearSelectionFromScheduleTable(){
        this.removeListSelectionAndMouseListenersFromTables();
        if (tblScheduleMorning!=null) tblScheduleMorning.clearSelection();
        if (this.tblScheduleAfternoon!=null) tblScheduleAfternoon.clearSelection();
        if (tblScheduleEvening!=null) tblScheduleEvening.clearSelection();
        this.addListSelectionAndMouseListenersToTables();
        //re-initialise all slots in the row selection mode
        if (getScheduleSlotModel() != null){
            for(Slot slot : getScheduleSlotModel()){
                slot.setIsSelected(false);
            }
        }
        this.doScheduleTitleRefresh(null);
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
    
    private int doAppointmentCancelConfirmation(){
        boolean isError = false;
        String name = null;
        LocalDateTime start = null;
        LocalTime from = null;
        Long duration;
       
        int OKToCancelAppointment;
        start = getSelectedAppointmentFromDiary().getStart();
        //start = getMyController().getDescriptor().getViewDescription().getAppointment().getStart();
        from = start.toLocalTime();
        //20/07/2022 08:16 update
        //duration = getViewDescriptor().getViewDescription().getAppointment().getData().getDuration().toMinutes();
        //duration = getMyController().getDescriptor().getViewDescription().getAppointment().getDuration().toMinutes();
        
        duration = getSelectedAppointmentFromDiary().getDuration().toMinutes();
        LocalTime to = from.plusMinutes(duration);

        String message;
        if (getSelectedAppointmentFromDiary().getPatient().toString().
                equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            message = "Are you sure you want to cancel the unbookable appointment slot";
        }else {
            name = getSelectedAppointmentFromDiary().getPatient().getName().getForenames();
            if (!name.isEmpty())name = name + " ";
            name = name + getSelectedAppointmentFromDiary().getPatient().getName().getSurname();
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
        return OKToCancelAppointment;
    }
    
    private Appointment selectedAppointmentFromDiary = null;
    private void setSelectedAppointmentFromDiary(Appointment appointment){
        selectedAppointmentFromDiary = appointment;
    }
    private Appointment getSelectedAppointmentFromDiary(){
        return selectedAppointmentFromDiary;
    }
    
    private void setViewDescriptorAppointment(Appointment appointment){
        getMyController().getDescriptor().getViewDescription().setAppointment(appointment);
    }
    private Appointment getViewDescriptorAppointment(){
        return getMyController().getDescriptor().getViewDescription().getAppointment();
    }
    
    private void doClinicNoteRequest(Slot slot){
        Appointment appointment = slot.getAppointment();
        if (appointment!=null){
            if(!appointment.getIsKeyDefined()){
                appointment  = null;
            }else if(appointment.getIsUnbookableSlot()){
                appointment = null;
            }
        }
        if (appointment!=null){
            getMyController().getDescriptor().getViewDescription()
                    .setAppointment(appointment);
            ViewController.ScheduleViewControllerActionEvent request =
                    ViewController.ScheduleViewControllerActionEvent
                    .CLINICAL_NOTE_VIEW_REQUEST;
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    request.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    
    enum AppointmentMode{
            CREATE,
            UPDATE,
            NONE;
    }
    
    private ExtendAction extendAction = null;
    private void setExtendAction(ExtendAction value){
        extendAction = value;
        switch(value){
            case EARLIER:
                this.btnExtendAppointmentEarlierLaterBothRequest.setText(Captions.ScheduleView.EXTEND_BOOKING._1());
                break;
            case LATER:
                this.btnExtendAppointmentEarlierLaterBothRequest.setText(Captions.ScheduleView.EXTEND_BOOKING._2());
                break;
            case BOTH:
                this.btnExtendAppointmentEarlierLaterBothRequest.setText(Captions.ScheduleView.EXTEND_BOOKING._3());
                break;
        }
    }
    private ExtendAction getExtendAction(){
        return extendAction;
    }
    
    private ShiftAction shiftAction = null;
    private void setShiftAction(ShiftAction value){
        shiftAction = value;
        switch(value){
            case EARLIER:
                this.btnShiftAppointmentEarlierLaterRequest.setText(Captions.ScheduleView.SHIFT_BOOKING._1());
                break;
            case LATER:
                this.btnShiftAppointmentEarlierLaterRequest.setText(Captions.ScheduleView.SHIFT_BOOKING._2());
                break;
        }
    }
    private ShiftAction getShiftAction(){
        return shiftAction;
    }
   
    private AppointmentMode appointmentMode = null;
    private void setAppointmentMode(AppointmentMode value){
        appointmentMode = value;        switch(appointmentMode){
            case CREATE:
                btnCreateAppointmentRequest.setText(
                        Captions.ScheduleView.CREATE_UPDATE_APPOINTMENT._1());
                //btnCreateAppointmentRequest.setEnabled(true);
                break;
            case UPDATE:
                /*btnCreateAppointmentRequest.setText(
                        ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._2());
                btnCreateAppointmentRequest.setEnabled(true);*/
                break;
            case NONE:
                btnCreateAppointmentRequest.setEnabled(false);
                break;
        }
    }
    private AppointmentMode getAppointmentMode(){
        return appointmentMode;
    }
    
    private Appointment appointmentFromDiaryWithUpdates = null;
    private void setAppointmentFromDiaryWithUpdates(Appointment appointment){
        appointmentFromDiaryWithUpdates = appointment;
    }
    private Appointment getAppointmentFromDiaryWithUpdates(){
        return appointmentFromDiaryWithUpdates;
    }
    
    private void doClinicalNoteRequest(){
        Appointment appointment = getMyController().getDescriptor()
                .getViewDescription().getAppointment();
        if (appointment!=null){
            if(!appointment.getIsKeyDefined()){
                appointment  = null;
            }else if(appointment.getIsUnbookableSlot()){
                appointment = null;
            }
        }
        if (appointment!=null){
            getMyController().getDescriptor().getViewDescription()
                    .setAppointment(appointment);
            ViewController.ScheduleViewControllerActionEvent request =
                    ViewController.ScheduleViewControllerActionEvent
                    .CLINICAL_NOTE_VIEW_REQUEST;
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    request.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    
    private ScheduleDiaryAction currentScheduleDiaryAction = null;
    private void setCurrentScheduleDiaryAction(ScheduleDiaryAction action){
        currentScheduleDiaryAction = action;
    }
    private ScheduleDiaryAction getCurrentScheduleDiaryAction(){
        return currentScheduleDiaryAction;
    }
    
    private void doUpdateAppointmentequest(){
        ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.UPDATE_APPOINTMENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doUpdateAppointmentActionForDiary(){
        /**
         * on entry view descriptor points to selected appointment in diary
         */
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.ScheduleViewControllerActionEvent.
                        APPOINTMENT_UPDATE_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doExtendAppointmentLater(){
        Slot slot = null;
        Appointment appointment = null;
        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration duration = null;
        slot = getFirstSelectedSlot();
        start = slot.getAppointment().getStart();
        setSelectedAppointmentFromDiary(slot.getAppointment());
        //appointment = slot.getAppointment();
        appointment = new Appointment();
        slot = getLastSelectedSlot();
        end = slot.getStart();
        duration = Duration.between(start, end.plusMinutes(5)); //+5 because end slot is 5 minutes long
        appointment.setDuration(duration);
        setAppointmentFromDiaryWithUpdates(appointment);
        setAppointmentMode(AppointmentMode.UPDATE);
    }
    
    private void doExtendAppointmentEarlier(){
        Slot slot = null;
        Appointment appointment = null;
        LocalDateTime start = null;
        Duration duration = null;
        slot = getFirstSelectedSlot();
        start = slot.getStart();
        slot = getLastSelectedSlot();
        setSelectedAppointmentFromDiary(slot.getAppointment());
        LocalDateTime oldStartTime = slot.getAppointment().getStart();
        duration = Duration.between(start, oldStartTime).plus(slot.getAppointment().getDuration());
        appointment = new Appointment();
        appointment.setStart(start);
        appointment.setDuration(duration);
        setAppointmentFromDiaryWithUpdates(appointment);
        setAppointmentMode(AppointmentMode.UPDATE);
    }
    
    private void doShiftAppointmentLater(){
        Slot slot = null;
        Appointment appointment = null;
        LocalDateTime end = null;
        LocalDateTime start = null;
        Duration duration = null;
        long minutes;
        
        setSelectedAppointmentFromDiary(getFirstSelectedSlot().getAppointment());
        for(int row : getSelection()){
            slot = getScheduleSlotModel().get(row);
            //slot = getScheduleDiaryTableModel().getElementAt(row);
            if (slot.getIsLastSlotOfAppointment()!=null){
                if (slot.getIsLastSlotOfAppointment()){
                    end = slot.getStart();
                    //duration = slot.getAppointment().getDuration();
                    
                    break;
                }
            }
        }
        if(end!=null){
            slot = getLastSelectedSlot();
            LocalDateTime newEnd = slot.getStart();
            minutes = Duration.between(end, newEnd).toMinutes();
            slot = getFirstSelectedSlot();
            appointment = new Appointment();
            start = slot.getAppointment().getStart();
            appointment.setStart(start.plusMinutes(minutes));
            //appointment.setDuration(duration);
            setAppointmentFromDiaryWithUpdates(appointment);
            //setSelectedAppointmentFromDiary(slot.getAppointment());
            setAppointmentMode(AppointmentMode.UPDATE);
            //action = Action.REQUEST_CREATE_APPOINTMENT;
        }else JOptionPane.showInternalMessageDialog(
                this, "selected appointment end is null!", "View error", JOptionPane.WARNING_MESSAGE);
    }
    
    private void doShiftAppointmentEarlier(){
        Slot slot = null;
        LocalDateTime start = null;
        Appointment appointment;
        long minutes;
        for(int row : getSelection()){
            slot = getScheduleSlotModel().get(row);
            //slot = getScheduleDiaryTableModel().getElementAt(row);
            if ((slot.getIsFirstSlotOfAppointment())!=null){
                if (slot.getIsFirstSlotOfAppointment()){
                    setSelectedAppointmentFromDiary(slot.getAppointment());
                    start = slot.getStart();
                    break;
                }
            }
        }
        if(start!=null){
            appointment = new Appointment();
            //appointment = slot.getAppointment();
            slot = getFirstSelectedSlot();
            LocalDateTime newStart = slot.getStart();
            minutes = Duration.between(newStart,start).toMinutes();
            appointment.setStart(start.minusMinutes(minutes));
            setAppointmentFromDiaryWithUpdates(appointment);
            setAppointmentMode(AppointmentMode.UPDATE);
        }else JOptionPane.showInternalMessageDialog(
                this, "selected appointment start is null!", "View error", JOptionPane.WARNING_MESSAGE);
    }
    
    private Slot getFirstSelectedSlot(){
        return getScheduleSlotModel().get(getSelection()[0]);
    }
    
    private Slot getLastSelectedSlot(){
        return getScheduleSlotModel().get(getSelection()[getSelection().length-1]);
    }
    /*private Slot getFirstSelectedSlot(){
        return getScheduleDiaryTableModel().getElementAt(getSelection()[0]);
    }
    
    private Slot getLastSelectedSlot(){
        return getScheduleDiaryTableModel().getElementAt(getSelection()[getSelection().length-1]);
    }*/
    
    private ScheduleDiaryTableModel scheduleDiaryTableModel ;
    private void setScheduleDiaryTableModel(ScheduleDiaryTableModel value){
        scheduleDiaryTableModel = value;
    }
    private ScheduleDiaryTableModel getScheduleDiaryTableModel(){
        return scheduleDiaryTableModel;
    }

    private void doScheduleTitleRefresh(Patient appointee){
        String contacts = null;
        String tableTitle = "Appointment schedule for " 
                + dayDatePicker.getDate().format(appointmentScheduleFormat);
        if (appointee!=null){
            if (!appointee.toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
                switch (appointee.getPhoneStatus()){
                    case NO_PHONE:
                        contacts = "";
                        break;
                    case PHONE_1:
                        contacts = "phone " + appointee.getPhone1().trim();
                        break;
                    case PHONE_2:
                        contacts = "phone " + appointee.getPhone2().trim();
                        break;
                    case TWO_PHONES:
                        contacts = "phones " + appointee.getPhone1().trim() + " & " + appointee.getPhone1().trim();
                        break;
                }
                switch (appointee.getEmailStatus()){
                    case NO_EMAIL:
                        contacts = contacts + "; no email";
                        break;
                    case HAS_EMAIL:
                        contacts = contacts + "; " + appointee.getEmail().trim();
                        break;
                }
                tableTitle = tableTitle + "          <"
                    + appointee.getName().getForenames() +  " " 
                    + appointee.getName().getSurname() + "'s contact -> " + contacts;
                tableTitle = tableTitle + ">";   
            }
        }
        TitledBorder titledBorder = (TitledBorder)pnlScheduleTables.getBorder();
        titledBorder.setTitle(tableTitle);
        pnlScheduleTables.repaint();
    }
    
    /**
     * disables all schedule action controls the close view actionj
     */
    private void disableAllScheduleActionControlsExceptCloseView(){
        btnCreateAppointmentRequest.setEnabled(false);
        btnCancelAppointmentRequest.setEnabled(false);
        btnClinicalNotesForAppointmentRequest.setEnabled(false);
        this.btnExtendAppointmentEarlierLaterBothRequest.setEnabled(false);
        btnMoveBookingRequest.setEnabled(false);
        this.btnShiftAppointmentEarlierLaterRequest.setEnabled(false);
        btnShortenAppointmentRequest.setEnabled(false);
        btnTreatmentForAppointmentRequest.setEnabled(false);
    }
    
    private Patient getAppointmentPatientSelected(){
        Patient result = null;
        Patient patient = null;
        for (int row : getSelection()){
            Slot slot = getScheduleSlotModel().get(row);
            if(slot.getIsBooked()){
                patient = slot.getAppointment().getPatient();
                break;
            }
        }
        if (patient!=null){
            if(!patient.getIsPatientMarkedUnbookable())
                result = patient;
        }else result = null;
        return result;
    }
    
    private boolean getIsMoreThanOneBookingSelected(){
        boolean result = false;
        boolean bookingLastSlotFound = false;
        boolean bookingFirstSlotFound = false;
        for(int _row : getSelection()){
            Slot slot = getScheduleSlotModel().get(_row);
            if (bookingLastSlotFound){
                if (slot.getIsFirstSlotOfAppointment())result = true;
            }else if (slot.getIsLastSlotOfAppointment()) bookingLastSlotFound = true;
        }
        return result;
    }
    
    private boolean getIsSelectionNonContiguous(){
        boolean result = false; //assume contiguous selection
        ArrayList<Integer> selectedRows = new ArrayList<>();
        /**30/12/2024 code update */
        for(int row = 0; row < 108; row++){
        //for(int row = 0; row < getScheduleSlotData().size(); row++){
            Slot slot = getScheduleSlotModel().get(row);
            if (slot.getIsSelected()) selectedRows.add(row);
        }
        //check for contiguity of selection (no gaps)
        Integer lastSelection = null;
        for (Integer selection : selectedRows){
            if (lastSelection != null){            
                if (Math.abs(lastSelection - selection) == 1) {
                    lastSelection = selection;
                    
                }
                else {
                    result = true;
                    break;
                }
            }else lastSelection = selection;
        }
        return result;
    }
    
    private Boolean getIsMoreThanOneAppointmentSelected(){
        Boolean result = false;
        Appointment appointment = null;
        for (int row : getSelection()){
            Slot slot = getScheduleSlotModel().get(row);
            //Slot slot = getScheduleDiaryTableModel().getElementAt(row);
            if(!slot.getIsBookable()){
                if (appointment==null) appointment = slot.getAppointment();
                else if(!appointment.equals(slot.getAppointment())){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    private Boolean getIsSingleSlotAppointmentFound(){
        Boolean result = false;
        for (int row : getSelection()){
            Slot slot = getScheduleSlotModel().get(row);
            //Slot slot = getScheduleDiaryTableModel().getElementAt(row);
            if (!slot.getIsBookable()){
                result = slot.getIsSingleSlotAppointment();
                break;
            }
        }
        return result;
    }
    
    private int scheduleViewCurrentlySelectedRowFromDiary = -1;
    private void setScheduleViewCurrentlySelectedRowFromDiary(int value){
        scheduleViewCurrentlySelectedRowFromDiary = value;
    }
    private int getScheduleViewCurrentlySelectedRowFromDiary(){
        return scheduleViewCurrentlySelectedRowFromDiary;
    }
    
    private int[] selection = null;
    private int getSelection(int table){
        int result = 0;
        switch (table){
            case 0:
                for(int row = table; row<36; row++){
                    Slot slot = getScheduleSlotModel().get(row);
                    if (slot.getIsSelected()) result++;
                }
                break;
            case 36:
                for(int row = table; row<72; row++){
                    Slot slot = getScheduleSlotModel().get(row);
                    if (slot.getIsSelected()) result++;
                }
                break;
            case 72:
                /**30/12/2024 code update */
                for(int row = table; row<108; row++){
                //for(int row = table; row < getScheduleSlotData().size(); row++){
                    Slot slot = getScheduleSlotModel().get(row);
                    if (slot.getIsSelected()) result++;
                }
                break;
        }
        return result;
    }
    /**
     *
     * @return first contiguous set of selected slots
     */
    private int[] getSelection(){ 
        int[] result = null;
        int selectedSlotCount = 0;
        int startRowOfSelection = 0;
        for (Slot slot : this.getScheduleSlotModel()){
            if (slot.getIsSelected()){ 
                selectedSlotCount++;
            }else if (selectedSlotCount>0) break; //if non-contiguousa exit
        }
        if (selectedSlotCount > 0){ //any rows selected?
            result = new int[selectedSlotCount];
            for (Slot slot : this.getScheduleSlotModel()){
                if (slot.getIsSelected()) break;
                startRowOfSelection++;
            }
            selectedSlotCount = 0;
            /**30/12/2024 code update */
            if (startRowOfSelection < 108){
                for(int row = startRowOfSelection; row < 108; row++){
            //if (startRowOfSelection < getScheduleSlotData().size()){
                //for(int row = startRowOfSelection; row < getScheduleSlotData().size(); row++){
                    Slot slot = getScheduleSlotModel().get(row);
                    if (slot.getIsSelected()) result[++selectedSlotCount - 1] = row;
                    else break;
                }
            }
        }
        return result;
    }
    
    /**
     * resets slot 'isSelect' property to false in the scheduleSlotdata collection
     * @param slotIndex indicates which slot to start the reset action from (fixed number, 108, slots in the collection)
     */
    private void resetSlotSelections(int slotIndex){
        int result = 0;
        switch (slotIndex){
            case 0:
                for(int row = 0; row < 36; row++){
                    Slot slot = getScheduleSlotModel().get(row);
                    slot.setIsSelected(false);
                }
                /*for (Slot slot : getScheduleSlotModel()){
                    if (slotIndex < 36){
                        slot.setIsSelected(false);
                    }else break;
                    slotIndex++;
                }
                break;*/
                break;
            case 36:
                for (int row = 36; row < 72; row++){
                    Slot slot = getScheduleSlotModel().get(row);
                    slot.setIsSelected(false);
                }
                /*
                for (Slot slot : getScheduleSlotModel()){
                    if (slotIndex >= 36){
                        if (slotIndex < 72){
                            slot.setIsSelected(false);
                        }else break;
                    }
                    slotIndex++;
               }*/
               break; 
            case 72:
                /**30/12/2024 code update */
                for (int row = 72; row < 108; row++ ){
                //for (int row = 72; row < getScheduleSlotData().size(); row++ ){
                    Slot slot = getScheduleSlotModel().get(row);
                    slot.setIsSelected(false);
                }
                /*
                for (Slot slot : getScheduleSlotModel()){
                    if (slotIndex > 71 && slotIndex < 108){
                        slot.setIsSelected(false);
                    }else break;
                    slotIndex++;
                }*/
                
                break;
        } 
        slotIndex = 0;
        for (Slot slot : getScheduleSlotModel()){
            if (slot.getIsSelected()) {
                result++;
                if (result > 0){
                    int test = 0;
                }
            }
            slotIndex++;
        }
    }
    
    private void setSlotSelections(int slotIndex, int[] value){
        int slotRow = slotIndex + value[0];
        int row = 0;
        for (Slot slot : this.getScheduleSlotModel()){
            if (slotRow == row){
                for (int _row : value){
                    Slot _slot = getScheduleSlotModel().get(slotRow);
                    _slot.setIsSelected(true);
                    slotRow++;
                }
                break;
            }
            row++;
        }
    }
    
    private void setSelection(int[] value, JTable table){
        int slotIndex = 0;
        if (table==this.tblScheduleMorning)slotIndex = 0;
        else if(table==this.tblScheduleAfternoon) slotIndex = 36;
        else if(table==this.tblScheduleEvening) slotIndex = 72;
        resetSlotSelections(slotIndex);
        if (value!=null)setSlotSelections(slotIndex,value);
    }
    
    private ScheduleSlotType scheduleSlotType = null;
    /*private void setScheduleSlotType(Appointment appointment){
        if (appointment.getPatient()==null) 
            scheduleSlotType = ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
        else if(appointment.getPatient().toString().equals(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark()))
            scheduleSlotType = ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;
        else if(appointment.getIsEmergency())
            scheduleSlotType = ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
        else scheduleSlotType = ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
    }*/
    private void setScheduleSlotType(Slot slot){
        if (slot.getAppointment().getPatient()==null) 
            scheduleSlotType = ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
        else if(slot.getAppointment().getPatient().toString().equals(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark()))
            scheduleSlotType = ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;
        else if(slot.getAppointment().getIsEmergency())
            scheduleSlotType = ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
        else scheduleSlotType = ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
    }
    private ScheduleSlotType getScheduleSlotType(){
        return scheduleSlotType;
    }
    
    
    
    private ScheduleDiaryAction getScheduleDiaryAction(){
        ScheduleDiaryAction result = null;
        
        if(!getIsMoreThanOneBookingSelected() && !getIsSelectionNonContiguous()){
            if (getBookingExtendEarlierAndLater()) result = ScheduleDiaryAction.EXTEND_APPOINTMENT_EARLIER_AND_LATER;
            else if (getBookingExtendLater()) result = ScheduleDiaryAction.EXTEND_APPOINTMENT_LATER;
            else if (getBookingExtendEarlier()) result = ScheduleDiaryAction.EXTEND_APPOINTMENT_EARLIER;
            else if (getBookingShiftLater()) result = ScheduleDiaryAction.SHIFT_APPOINTMENT_LATER;
            else if (getBookingShiftEarlier()) result = ScheduleDiaryAction.SHIFT_APPOINTMENT_EARLIER;
            else if (getBookingCancelMove()) result = ScheduleDiaryAction.CANCEL_MOVE_APPOINTMENT;
            else if (getBookingCreate()) result = ScheduleDiaryAction.CREATE_APPOINTMENT;
            else if (getBookingShorten()) result = ScheduleDiaryAction.SHORTEN_APPOINTMENT;
            else if (getFirstSlotOfAppointmentOnlySelected()) {
                result = ScheduleDiaryAction.CLINICAL_NOTES_TREATMENT;
            }
            else result = ScheduleDiaryAction.NONE;

            if (getIsSingleSlotAppointmentFound()){
                setAppointmentMode(AppointmentMode.UPDATE);
                switch(result){
                    case EXTEND_APPOINTMENT_LATER:
                        result = ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_LATER;
                        break;
                    case EXTEND_APPOINTMENT_EARLIER:
                        result = ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_EARLIER;
                        break;
                    case SHIFT_APPOINTMENT_LATER:
                        result = ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_LATER;
                        break;
                    case SHIFT_APPOINTMENT_EARLIER:
                            result = ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_EARLIER;
                        break;   
                }
            }
        }else result = ScheduleDiaryAction.RESET_SELECTION;
        setCurrentScheduleDiaryAction(result);
        return result;  
    }
    
    private ScheduleDiaryAction lastScheduleDiaryAction = null;
    private void setLastScheduleDiaryAction(ScheduleDiaryAction value){
        lastScheduleDiaryAction = value;
    }
    private ScheduleDiaryAction getLastScheduleDiaryAction(){
        return lastScheduleDiaryAction;
    }
    
    private void doScheduleDiaryActionRequest(ScheduleDiaryAction request){
        DialogView dialog = null;
        List<ScheduleDiaryAction> actions = null;
        Appointment appointment = null;
        Action action = null;
        ActionEvent actionEvent = null;
        Slot slot = null;
        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration duration = null;
        Duration selectedSlotsDurationToShortenAppointmentBy = null;
        long minutes = 0;
        int slots = 0;
        switch (request){
            case CLINICAL_NOTES_TREATMENT:
                slot = getFirstSelectedSlot();
                setSelectedAppointmentFromDiary(slot.getAppointment());
                break;
            case CANCEL_MOVE_APPOINTMENT:
                slot = getFirstSelectedSlot();
                setSelectedAppointmentFromDiary(slot.getAppointment());
                //action = Action.REQUEST_CANCEL_APPOINTMENT;
                break;
            case CREATE_APPOINTMENT:
                Patient patient = null;
                slot = getFirstSelectedSlot();
                start = slot.getStart();
                slots = getSelection().length;
                duration = Duration.ofMinutes(slots*5);
                appointment = new Appointment();
                appointment.setStart(start);
                appointment.setDuration(duration);
                setAppointmentFromDiaryWithUpdates(appointment);
                setAppointmentMode(AppointmentMode.CREATE);
                this.setAppointmentFromDiaryWithUpdates(appointment);
                break;
            case EXTEND_APPOINTMENT_LATER:
                doExtendBookingLater();
                /*doExtendAppointmentLater();*/
                //action = Action.REQUEST_CREATE_APPOINTMENT;
                break;
            case EXTEND_APPOINTMENT_EARLIER:
                doExtendBookingEarlier();
                break;
            case EXTEND_APPOINTMENT_EARLIER_AND_LATER:
                slot = getFirstSelectedSlot();
                start = slot.getStart();
                slot = getLastSelectedSlot();
                end = slot.getStart().plusMinutes(5);
                duration = Duration.between(start, end);
                for(int row : getSelection()){
                    //slot = getScheduleDiaryTableModel().getElementAt(row);
                    slot = getScheduleSlotModel().get(row);
                    if(slot.getIsBooked()){
                        setSelectedAppointmentFromDiary(slot.getAppointment());
                        break;
                    }
                }
                if(getSelectedAppointmentFromDiary()!=null){ 
                    appointment = new Appointment();
                    appointment.setStart(start);
                    appointment.setDuration(duration);
                    setAppointmentFromDiaryWithUpdates(appointment);
                    setAppointmentMode(AppointmentMode.UPDATE);
                    //action = Action.REQUEST_CREATE_APPOINTMENT;
                }else JOptionPane.showInternalMessageDialog(
                        this, "selected appointment is null!", "View error", JOptionPane.WARNING_MESSAGE);
                break;
            case EXTEND_SHIFT_APPOINTMENT_EARLIER:
                actions = new ArrayList<ScheduleDiaryAction>();
                actions.add(ScheduleDiaryAction.EXTEND_APPOINTMENT_EARLIER);
                actions.add(ScheduleDiaryAction.SHIFT_APPOINTMENT_EARLIER);
                dialog = new View().make(
                        View.Viewer.EXTEND_SHIFT_BOOKING_DIALOG, 
                        this, 
                        actions,
                        "Extend/shift appointment editor",
                        "Select required action").getDialogView();
                if (getDialogView().getSelectedItem()!=null){
                    switch((ScheduleDiaryAction)getDialogView().getSelectedItem()){
                        case EXTEND_APPOINTMENT_EARLIER:
                            this.setCurrentScheduleDiaryAction(ScheduleDiaryAction.SINGLE_SLOT_EXTEND_EARLIER_ACTION);
                            doExtendBookingEarlier();
                            break;
                        case SHIFT_APPOINTMENT_EARLIER:
                            this.setCurrentScheduleDiaryAction(ScheduleDiaryAction.SINGLE_SLOT_SHIFT_EARLIER_ACTION);
                            doShiftBookingEarlier();
                            break;
                    } 
                }
                break;
            case EXTEND_SHIFT_APPOINTMENT_LATER:
                actions = new ArrayList<ScheduleDiaryAction>();
                actions.add(ScheduleDiaryAction.EXTEND_APPOINTMENT_LATER);
                actions.add(ScheduleDiaryAction.SHIFT_APPOINTMENT_LATER);
                dialog = new View().make(
                        View.Viewer.EXTEND_SHIFT_BOOKING_DIALOG, 
                        this, 
                        actions,
                        "Extend/shift appointment editor",
                        "Select required action").getDialogView();
                if (getDialogView().getSelectedItem()!=null){
                    switch((ScheduleDiaryAction)getDialogView().getSelectedItem()){
                        case EXTEND_APPOINTMENT_LATER:
                            this.setCurrentScheduleDiaryAction(ScheduleDiaryAction.SINGLE_SLOT_EXTEND_LATER_ACTION);
                            doExtendBookingLater();
                            break;
                        case SHIFT_APPOINTMENT_LATER:
                            this.setCurrentScheduleDiaryAction(ScheduleDiaryAction.SINGLE_SLOT_SHIFT_LATER_ACTION);
                            doShiftBookingLater();
                            break;
                    } 
                }
                break;    
            case SHIFT_APPOINTMENT_LATER:
                doShiftBookingLater();
                
                break;
            case SHIFT_APPOINTMENT_EARLIER:
                doShiftBookingEarlier();
                break;
            case SHORTEN_APPOINTMENT:
                slots = getSelection().length;
                selectedSlotsDurationToShortenAppointmentBy = Duration.ofMinutes(slots*5);
                slot = getFirstSelectedSlot();
                setSelectedAppointmentFromDiary(slot.getAppointment());
                appointment = new Appointment();
                appointment.setStart(slot.getAppointment().getStart());
                appointment.setDuration(slot.getAppointment().getDuration().minus(selectedSlotsDurationToShortenAppointmentBy));
                setAppointmentFromDiaryWithUpdates(appointment);
                setAppointmentMode(AppointmentMode.UPDATE);
                //action = Action.REQUEST_CREATE_APPOINTMENT;
                break;
            case NONE:
                action = Action.NONE;
                break;
        }
    }
 
    private void doExtendBookingEarlier(){
        Slot slot = null;
        LocalDateTime start = null;
        Duration duration = null;
        Appointment appointment = null;
        doExtendAppointmentEarlier();
        slot = getFirstSelectedSlot();
        start = slot.getStart();
        slot = getLastSelectedSlot();
        setSelectedAppointmentFromDiary(slot.getAppointment());
        LocalDateTime oldStartTime = slot.getAppointment().getStart();
        duration = Duration.between(start, oldStartTime).plus(slot.getAppointment().getDuration());
        //appointment = slot.getAppointment();
        appointment = new Appointment();
        appointment.setStart(start);
        appointment.setDuration(duration);
        setAppointmentFromDiaryWithUpdates(appointment);
        setAppointmentMode(AppointmentMode.UPDATE);
    }
    
    private void doExtendBookingLater(){
        doExtendAppointmentLater();
    }
    
    private void doShiftBookingEarlier(){
        Slot slot = null;
        LocalDateTime start = null;
        Appointment appointment;
        Long minutes;
        doShiftAppointmentEarlier();
        /**
         * locate start of appt
         * calculate minutes difference with first selected row
         * adjust appt start time to start at earlier time
         */
        for(int row : getSelection()){
            //slot = getScheduleDiaryTableModel().getElementAt(row);
            slot = this.getScheduleSlotModel().get(row);
            if ((slot.getIsFirstSlotOfAppointment())!=null){
                if (slot.getIsFirstSlotOfAppointment()){
                    setSelectedAppointmentFromDiary(slot.getAppointment());
                    start = slot.getStart();
                    break;
                }
            }
        }
        if(start!=null){
            appointment = new Appointment();
            //appointment = slot.getAppointment();
            slot = getFirstSelectedSlot();
            LocalDateTime newStart = slot.getStart();
            minutes = Duration.between(newStart,start).toMinutes();
            appointment.setStart(start.minusMinutes(minutes));
            //appointment.setDuration(slot.getAppointment().getDuration());
            setAppointmentFromDiaryWithUpdates(appointment);
            //setSelectedAppointmentFromDiary(slot.getAppointment());
            setAppointmentMode(AppointmentMode.UPDATE);
            //action = Action.REQUEST_CREATE_APPOINTMENT;
        }else JOptionPane.showInternalMessageDialog(
                this, "selected appointment start is null!", "View error", JOptionPane.WARNING_MESSAGE);
    }
    
    private void doShiftBookingLater(){
        doShiftAppointmentLater();
        /**
         * -- locate last row of appt
         * -- from this and last selected row calculate minutes appt shifted
         * -- add these minutes to appt start
         */
    }
    
    private boolean getBookingExtendEarlierAndLater(){
        boolean result = false;
        Slot slot = getFirstSelectedSlot();
        if (slot.getIsBookable()){
            slot = getLastSelectedSlot();
            if (slot.getIsBookable()) {
                for (int row = getSelection()[0]; row < getSelection()[getSelection().length - 1]; row++){
                    if (getScheduleSlotModel().get(row).getIsBooked()){
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    /*
    private Boolean getAppointmentExtendEarlerAndLater(){
        Boolean result = false;
        boolean isFirstSelectedSlotBookable = true;
        boolean areSelectedSlotsAllBookable = false;
        boolean isLastSelectedSlotBookable = true;

        boolean[] requiredValues = {
            isFirstSelectedSlotBookable,
            !areSelectedSlotsAllBookable,
            isLastSelectedSlotBookable};
        Criteria criteria = new Criteria(requiredValues);
 
        Slot slot = getFirstSelectedSlot();
        if (!slot.getIsBookable()) isFirstSelectedSlotBookable = false;
        if (slot.getIsBookable()){
            areSelectedSlotsAllBookable = true;
            for (int row : getSelection()){
                slot = getScheduleDiaryTableModel().getElementAt(row);
                if (slot.getIsBooked()){
                    areSelectedSlotsAllBookable = false;
                    break;
                }
            }
        }
        if (!areSelectedSlotsAllBookable){
            slot = getLastSelectedSlot();
            if (!slot.getIsBookable()) isLastSelectedSlotBookable = false;
        }
        
        criteria.setActual(0,isFirstSelectedSlotBookable);
        criteria.setActual(1,!areSelectedSlotsAllBookable);
        criteria.setActual(2,isLastSelectedSlotBookable);

        return criteria.check();
    }*/
    
    private Boolean getBookingExtendEarlier(){
        boolean result = false;
        Slot slot = getFirstSelectedSlot();
        if (slot.getIsBookable()) {
            slot = getLastSelectedSlot();
            result = slot.getIsFirstSlotOfAppointment();
        }else result = false;
        return result;
    }
    
    /*
    private Boolean getAppointmentExtendEarlier(){
        Boolean result = false;
        
        boolean isFirstSelectedSlotBookable = true;
        boolean isLastSelectedSlotAppointmentFirstSlot = false;
        
        Slot slot = getFirstSelectedSlot();
        if (slot.getIsBookable()) {
            slot = getLastSelectedSlot();
            result = slot.getIsFirstSlotOfAppointment();
        }else result = false;
        return result;
    }*/
    
    private Boolean getBookingExtendLater(){
        boolean result = false;
        Slot slot = getFirstSelectedSlot();
        if (slot.getIsLastSlotOfAppointment()){
            slot = getLastSelectedSlot();
            result = slot.getIsBookable();
        }
        return result;
    }
   
    /*
    private Boolean getAppointmentExtendLater(){
        Boolean result = false;
        boolean isFirstSelectedSlotBooked = true;
        boolean isFirstSelectedSlotAppointmentFirstSlot = false;
        boolean isLastSelectedSlotBookable = true;
        
        boolean[] requiredValues = {
            isFirstSelectedSlotBooked,
            !isFirstSelectedSlotAppointmentFirstSlot,
            isLastSelectedSlotBookable   
        };
        Criteria criteria = new Criteria(requiredValues);
        
        
        if (slot.getIsBookable()) isFirstSelectedSlotBooked = false;
        if(isFirstSelectedSlotBooked){
            if(slot.getIsFirstSlotOfAppointment()) 
                isFirstSelectedSlotAppointmentFirstSlot = true;
            if(!isFirstSelectedSlotAppointmentFirstSlot){
                slot = getLastSelectedSlot();
                if(slot.getIsBooked()) isLastSelectedSlotBookable = false;
            }
        }
        
        criteria.setActual(0, isFirstSelectedSlotBooked);
        criteria.setActual(1, !isFirstSelectedSlotAppointmentFirstSlot);
        criteria.setActual(2, isLastSelectedSlotBookable);
        return criteria.check();
    }
    */
    
    private Boolean getBookingShiftLater(){
        Boolean result = null;
        Slot slot = null;
        slot = getFirstSelectedSlot();
        if (slot.getIsFirstSlotOfAppointment()){
            slot = getLastSelectedSlot();
            result = slot.getIsBookable();
        }else result = false;
        return result;
    }
    
    /*
    private Boolean getAppointmentShiftLater(){
        Boolean result = false;
        boolean isFirstSelectedSlotBooked = true;
        boolean isFirstSelectedSlotAppointmentFirstSlot = true;
        boolean isLastSelectedSlotBookable = true;
        
        Slot slot = getFirstSelectedSlot();
        if(slot.getIsBookable()) isFirstSelectedSlotBooked = false;
        if (isFirstSelectedSlotBooked){
            if (!slot.getIsFirstSlotOfAppointment()) isFirstSelectedSlotAppointmentFirstSlot = false;
            if(isFirstSelectedSlotAppointmentFirstSlot){
                slot = getLastSelectedSlot();
                if(slot.getIsBooked()) isLastSelectedSlotBookable = false;
            }
        }

        return isFirstSelectedSlotBooked
                && isFirstSelectedSlotAppointmentFirstSlot
                && isLastSelectedSlotBookable;
    }*/
    
    private boolean getBookingShiftEarlier(){
        boolean result = false;
        Slot slot = getLastSelectedSlot();
        if (slot.getIsLastSlotOfAppointment()){
            slot=getFirstSelectedSlot();
            result = slot.getIsBookable();
        }else result = false;
        return result;
    }
    
    /*
    private Boolean getAppointmentShiftEarlier(){
        Boolean result = false;
        boolean isFirstSelectedSlotBookable = true;
        boolean isLastSelectedSlotBooked = true;
        boolean isLastSelectedSlotAppointmentLastSlot = true;
        
        Slot slot = getFirstSelectedSlot();
        if(slot.getIsBooked()) isFirstSelectedSlotBookable = false;
        if(isFirstSelectedSlotBookable){
            slot = getLastSelectedSlot();
            if(slot.getIsBookable()) isLastSelectedSlotBooked = false;
            if(isLastSelectedSlotBooked){
                if(!slot.getIsLastSlotOfAppointment()) isLastSelectedSlotAppointmentLastSlot = false;
            }
        }
        
        
        return isFirstSelectedSlotBookable 
                && isLastSelectedSlotBooked
                && isLastSelectedSlotAppointmentLastSlot;
    }
    */
    private boolean getFirstSlotOfAppointmentOnlySelected(){
        return (getSelection().length == 1) && (getFirstSelectedSlot().getIsFirstSlotOfAppointment());
    }
    private boolean getBookingShorten(){
        boolean result = false;
        Slot slot = getFirstSelectedSlot();
        if (slot.getIsBooked()){
            slot = getLastSelectedSlot();
            result = slot.getIsLastSlotOfAppointment();
        }
        return result;
    }
    
    
    
    private boolean getBookingCancelMove(){
        boolean result = false;
        Slot slot = getFirstSelectedSlot();
        if(slot.getIsFirstSlotOfAppointment()){
            slot = getLastSelectedSlot();
            result = slot.getIsLastSlotOfAppointment();
        }
        return result;
    }

    private boolean getBookingCreate(){
        boolean result = true;
        for (int row : getSelection()){
            Slot slot = this.getScheduleSlotModel().get(row);
            if (slot.getIsBooked()){
                result = false;
                break;
            }
        }
        return result;
    }
    
    /*
    private Boolean getAppointmentCreate(){     
        boolean areSelectedSlotsAllBookable = true;
        boolean[] values = {areSelectedSlotsAllBookable};
        Criteria criteria = new Criteria(values);
        
        for (int row : getSelection()){
            Slot slot = getScheduleDiaryTableModel().getElementAt(row);
            if (slot.getIsBooked()){
                areSelectedSlotsAllBookable = false;
                break;
            }
        }
        criteria.setActual(0,areSelectedSlotsAllBookable);
        
        return criteria.check();
    }
    */
    class Criteria{
       public Criteria(boolean[] values){
           required = values;
           actual = new boolean[required.length];
       }
       
       private boolean[] required = null;
       public boolean[] getRequired(){
           return required;
       }
       
       private boolean[] actual = null;
       public void setActual(int index, boolean value){
           actual[index] = value;
       }
       public boolean[] getActual(){
           return actual;
       }
       
       public boolean check(){
           boolean result = true;
           int index = 0;
           for (boolean value : getRequired()){
               if(!(value && getActual()[index])){
                   result = false;
                   break;
               }
               index++;
           }
           return result;
       }
    }
    
    private final DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    
    enum ExtendAction{
        EARLIER,
        LATER,
        BOTH
    };
    
    enum ShiftAction{
        EARLIER,
        LATER
    };
    
    public enum ScheduleDiaryAction{
        CANCEL_MOVE_APPOINTMENT,
        CLINICAL_NOTES_TREATMENT,
        CREATE_APPOINTMENT,
        EXTEND_APPOINTMENT_LATER,
        EXTEND_APPOINTMENT_EARLIER,
        EXTEND_APPOINTMENT_EARLIER_AND_LATER,
        EXTEND_SHIFT_APPOINTMENT_LATER,         //request ambiguous because case of a single slot appointment
        EXTEND_SHIFT_APPOINTMENT_EARLIER,       //request ambiguous because case of a single slot appointment
        SHIFT_APPOINTMENT_LATER,
        SHIFT_APPOINTMENT_EARLIER,
        SHORTEN_APPOINTMENT,
        RESET_SELECTION,                        //more than one appointment selected
        SINGLE_SLOT_EXTEND_EARLIER_ACTION,
        SINGLE_SLOT_EXTEND_LATER_ACTION,
        SINGLE_SLOT_SHIFT_EARLIER_ACTION,
        SINGLE_SLOT_SHIFT_LATER_ACTION,
        NONE
    }
    
    enum Action{
        NONE,
        REQUEST_BOOKABLE_SLOT_SCANNER_VIEW,
        REQUEST_CANCEL_APPOINTMENT,
        REQUEST_CANCELLED_APPOINTMENT_VIEW,
        REQUEST_CLINICAL_NOTE_VIEW,
        REQUEST_CLOSE_VIEW,
        REQUEST_COLOUR_PICKER,
        REQUEST_CREATE_APPOINTMENT,
        REQUEST_EARLY_BOOKING_START_TIME,
        REQUEST_EXTEND_APPOINTMENT,
        REQUEST_LATE_BOOKING_END_TIME,
        REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION,
        REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY,
        REQUEST_MOVE_BOOKING,
        REQUEST_NEXT_DAY,
        REQUEST_NON_SURGERY_DAY,
        REQUEST_NOW,
        REQUEST_PREVIOUS_DAY,
        REQUEST_PRINT_SCHEDULE,
        REQUEST_SEARCH_AVAILABLE_SLOTS,
        REQUEST_SHIFT_APPOINTMENT,
        REQUEST_SHORTEN_APPOINTMENT,
        REQUEST_SURGERY_DAY_EDITOR,
        REQUEST_SWITCH_VIEW,            //SVC know which view tro switch to because the ActionEvent source the type (List/Diary) the caller is
        REQUEST_TREATMENT_VIEW,
        REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW,
        REQUEST_UNDO_CURRENT_SELECTION;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlScheduleTables = new javax.swing.JPanel();
        scrMorningTable = new javax.swing.JScrollPane();
        tblScheduleMorning = new javax.swing.JTable();
        scrAfternoonTable = new javax.swing.JScrollPane();
        tblScheduleAfternoon = new javax.swing.JTable();
        scrEveningTable = new javax.swing.JScrollPane();
        tblScheduleEvening = new javax.swing.JTable();
        pnlScheduleDaySelection = new javax.swing.JPanel();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        btnPreviousDay = new javax.swing.JButton();
        btnNow = new javax.swing.JButton();
        btnNextDay = new javax.swing.JButton();
        pnlSlotScanner = new javax.swing.JPanel();
        btnSelectBookableSlotsScanner = new javax.swing.JButton();
        btnSelectUnbookableSlotsScanner = new javax.swing.JButton();
        pnlScheduleOperations = new javax.swing.JPanel();
        btnCreateAppointmentRequest = new javax.swing.JButton();
        btnShortenAppointmentRequest = new javax.swing.JButton();
        btnCancelAppointmentRequest = new javax.swing.JButton();
        btnMoveBookingRequest = new javax.swing.JButton();
        btnClinicalNotesForAppointmentRequest = new javax.swing.JButton();
        btnTreatmentForAppointmentRequest = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnShiftAppointmentEarlierLaterRequest = new javax.swing.JButton();
        btnExtendAppointmentEarlierLaterBothRequest = new javax.swing.JButton();
        pnlViewSwitch = new javax.swing.JPanel();
        btnSwitchView = new javax.swing.JButton();
        pnlUndoSelection = new javax.swing.JPanel();
        btnUndoCurrentSelectionRequest = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuOptions = new javax.swing.JMenu();
        mniPrintSchedule = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniEarlyBookingOption = new javax.swing.JMenuItem();
        mniLateBookingOption = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlScheduleTables.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule details"));

        tblScheduleMorning.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrMorningTable.setViewportView(tblScheduleMorning);

        tblScheduleAfternoon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrAfternoonTable.setViewportView(tblScheduleAfternoon);

        tblScheduleEvening.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblScheduleEvening.setMaximumSize(new java.awt.Dimension(2147483647, 700));
        tblScheduleEvening.setMinimumSize(new java.awt.Dimension(60, 700));
        scrEveningTable.setViewportView(tblScheduleEvening);

        javax.swing.GroupLayout pnlScheduleTablesLayout = new javax.swing.GroupLayout(pnlScheduleTables);
        pnlScheduleTables.setLayout(pnlScheduleTablesLayout);
        pnlScheduleTablesLayout.setHorizontalGroup(
            pnlScheduleTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleTablesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrMorningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrAfternoonTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrEveningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlScheduleTablesLayout.setVerticalGroup(
            pnlScheduleTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleTablesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleTablesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrMorningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrAfternoonTable, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrEveningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlScheduleDaySelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule day selection"));

        DatePickerSettings settings = new DatePickerSettings();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker(settings);

        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowEmptyDates(false);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);

        btnPreviousDay.setText("Yesterday");

        btnNow.setText("Today");

        btnNextDay.setText("Tomorrow");
        btnNextDay.setMinimumSize(new java.awt.Dimension(60, 23));
        btnNextDay.setPreferredSize(new java.awt.Dimension(62, 23));

        javax.swing.GroupLayout pnlScheduleDaySelectionLayout = new javax.swing.GroupLayout(pnlScheduleDaySelection);
        pnlScheduleDaySelection.setLayout(pnlScheduleDaySelectionLayout);
        pnlScheduleDaySelectionLayout.setHorizontalGroup(
            pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97)
                .addComponent(btnPreviousDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(btnNow, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99)
                .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        pnlScheduleDaySelectionLayout.setVerticalGroup(
            pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnNow, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(btnNextDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnPreviousDay, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pnlSlotScanner.setBorder(javax.swing.BorderFactory.createTitledBorder("Slot scanner"));

        btnSelectBookableSlotsScanner.setText("Bookable slots");
        btnSelectBookableSlotsScanner.setMinimumSize(new java.awt.Dimension(60, 23));
        btnSelectBookableSlotsScanner.setPreferredSize(new java.awt.Dimension(62, 23));

        btnSelectUnbookableSlotsScanner.setText("Unbookable slots");
        btnSelectUnbookableSlotsScanner.setMinimumSize(new java.awt.Dimension(60, 23));
        btnSelectUnbookableSlotsScanner.setPreferredSize(new java.awt.Dimension(62, 23));

        javax.swing.GroupLayout pnlSlotScannerLayout = new javax.swing.GroupLayout(pnlSlotScanner);
        pnlSlotScanner.setLayout(pnlSlotScannerLayout);
        pnlSlotScannerLayout.setHorizontalGroup(
            pnlSlotScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotScannerLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(btnSelectBookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(btnSelectUnbookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        pnlSlotScannerLayout.setVerticalGroup(
            pnlSlotScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotScannerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSlotScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectBookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelectUnbookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlScheduleOperations.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnCreateAppointmentRequest.setText("<html><center>Create</center><center>new</center><center>appointment</center></html>");

        btnShortenAppointmentRequest.setText("<html><center>Shorten</center><center>selected</center><center>appointment</center></html>");

        btnCancelAppointmentRequest.setText("<html><center>Cancel</center><center>selected</center><center>appointment</center></html>");

        btnMoveBookingRequest.setText("<html><center>Move</center><center>appointment</center><center>to another day</center></html>");

        btnClinicalNotesForAppointmentRequest.setText("<html><center>Clinical note</center><center>for selected</center><center>appointment</center></html>");
        btnClinicalNotesForAppointmentRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClinicalNotesForAppointmentRequestActionPerformed(evt);
            }
        });

        btnTreatmentForAppointmentRequest.setText("<html><center>Treatment</center><center>for selected</center<center>appointment</center></html>");
        btnTreatmentForAppointmentRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTreatmentForAppointmentRequestActionPerformed(evt);
            }
        });

        btnCloseView.setText(Captions.CLOSE_VIEW);

        btnShiftAppointmentEarlierLaterRequest.setText("<html><center>Shift earlier</center><center>selected</center><center>appointment</center></html>");

        btnExtendAppointmentEarlierLaterBothRequest.setText("<html><center>Extend</center><center>earlier & later</center><center>appointment</center></html>");

        javax.swing.GroupLayout pnlScheduleOperationsLayout = new javax.swing.GroupLayout(pnlScheduleOperations);
        pnlScheduleOperations.setLayout(pnlScheduleOperationsLayout);
        pnlScheduleOperationsLayout.setHorizontalGroup(
            pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTreatmentForAppointmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClinicalNotesForAppointmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnMoveBookingRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnExtendAppointmentEarlierLaterBothRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCreateAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnShiftAppointmentEarlierLaterRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnShortenAppointmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        pnlScheduleOperationsLayout.setVerticalGroup(
            pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnCancelAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnExtendAppointmentEarlierLaterBothRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnMoveBookingRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnShiftAppointmentEarlierLaterRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnShortenAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnClinicalNotesForAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnTreatmentForAppointmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlViewSwitch.setBorder(javax.swing.BorderFactory.createTitledBorder("View format"));

        btnSwitchView.setText("<html><center>Switch to</center><center>list view</center></html>");

        javax.swing.GroupLayout pnlViewSwitchLayout = new javax.swing.GroupLayout(pnlViewSwitch);
        pnlViewSwitch.setLayout(pnlViewSwitchLayout);
        pnlViewSwitchLayout.setHorizontalGroup(
            pnlViewSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlViewSwitchLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnSwitchView, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        pnlViewSwitchLayout.setVerticalGroup(
            pnlViewSwitchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlViewSwitchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSwitchView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlUndoSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(".."));

        btnUndoCurrentSelectionRequest.setText("<html><center>Undo current</center<center>selection</center></html>");

        javax.swing.GroupLayout pnlUndoSelectionLayout = new javax.swing.GroupLayout(pnlUndoSelection);
        pnlUndoSelection.setLayout(pnlUndoSelectionLayout);
        pnlUndoSelectionLayout.setHorizontalGroup(
            pnlUndoSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUndoSelectionLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(btnUndoCurrentSelectionRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        pnlUndoSelectionLayout.setVerticalGroup(
            pnlUndoSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUndoSelectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUndoCurrentSelectionRequest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mnuOptions.setText("Actions");

        mniPrintSchedule.setText("Print schedule");
        mnuOptions.add(mniPrintSchedule);
        mnuOptions.add(jSeparator3);

        mniEarlyBookingOption.setText("Defining an early appointment");
        mnuOptions.add(mniEarlyBookingOption);

        mniLateBookingOption.setText("Defining a late appointment");
        mnuOptions.add(mniLateBookingOption);
        mnuOptions.add(jSeparator1);

        mniCloseView.setText("Close view");
        mnuOptions.add(mniCloseView);

        jMenuBar1.add(mnuOptions);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlScheduleTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(pnlViewSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlScheduleDaySelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlSlotScanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlUndoSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pnlScheduleDaySelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlUndoSelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlSlotScanner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlViewSwitch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlScheduleTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClinicalNotesForAppointmentRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClinicalNotesForAppointmentRequestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnClinicalNotesForAppointmentRequestActionPerformed

    private void btnTreatmentForAppointmentRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTreatmentForAppointmentRequestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTreatmentForAppointmentRequestActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelAppointmentRequest;
    private javax.swing.JButton btnClinicalNotesForAppointmentRequest;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateAppointmentRequest;
    private javax.swing.JButton btnExtendAppointmentEarlierLaterBothRequest;
    private javax.swing.JButton btnMoveBookingRequest;
    private javax.swing.JButton btnNextDay;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnPreviousDay;
    private javax.swing.JButton btnSelectBookableSlotsScanner;
    private javax.swing.JButton btnSelectUnbookableSlotsScanner;
    private javax.swing.JButton btnShiftAppointmentEarlierLaterRequest;
    private javax.swing.JButton btnShortenAppointmentRequest;
    private javax.swing.JButton btnSwitchView;
    private javax.swing.JButton btnTreatmentForAppointmentRequest;
    private javax.swing.JButton btnUndoCurrentSelectionRequest;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniEarlyBookingOption;
    private javax.swing.JMenuItem mniLateBookingOption;
    private javax.swing.JMenuItem mniPrintSchedule;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlScheduleDaySelection;
    private javax.swing.JPanel pnlScheduleOperations;
    private javax.swing.JPanel pnlScheduleTables;
    private javax.swing.JPanel pnlSlotScanner;
    private javax.swing.JPanel pnlUndoSelection;
    private javax.swing.JPanel pnlViewSwitch;
    private javax.swing.JScrollPane scrAfternoonTable;
    private javax.swing.JScrollPane scrEveningTable;
    private javax.swing.JScrollPane scrMorningTable;
    private javax.swing.JTable tblScheduleAfternoon;
    private javax.swing.JTable tblScheduleEvening;
    private javax.swing.JTable tblScheduleMorning;
    // End of variables declaration//GEN-END:variables
}


