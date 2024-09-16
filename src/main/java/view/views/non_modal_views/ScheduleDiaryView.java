/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ScheduleViewController;
import com.bric.colorpicker.ColorPicker;
import com.bric.colorpicker.listeners.ColorListener;
import com.bric.colorpicker.models.ColorModel;
import com.bric.colorpicker.ColorPickerDialog;
import model.non_entity.SystemDefinition;
import model.non_entity.Slot;
import model.non_entity.SystemDefinition.ScheduleSlotType;
import model.non_entity.SystemDefinition.ScheduleViewActionCaption;
import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.renderers.AppointmentsListTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.ScheduleDiaryTablePatientRenderer;
import view.views.view_support_classes.renderers.AppointmentsListTablePatientRenderer;
import view.views.view_support_classes.renderers.ScheduleDiaryTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.ScheduleTableCellRenderer;
import view.views.view_support_classes.renderers.ScheduleDiaryTableStringRenderer;
import view.views.dialogs.CustomComboBoxDialog;
import view.views.dialogs.CustomComboBoxInternalDialog;
/*28/03/2024import view.views.view_support_classes.renderers.AppointmentsTablePatientNoteRenderer;*/
import view.views.view_support_classes.models.ScheduleListTableModel;
import view.views.view_support_classes.models.ScheduleDiaryTableModel;
import view.views.view_support_classes.models.ScheduleDiaryEmergencyTableModel;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import view.views.view_support_classes.AppointmentDateVetoPolicy;

/**
 *
 * @author colin
 */
public class ScheduleDiaryView extends View 
                          implements ActionListener, 
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
        
        this.btnSwitchView.setActionCommand(ScheduleListView.Action.REQUEST_SWITCH_VIEW.toString());
        this.btnSwitchView.addActionListener(this);
        
        this.btnSelectBookableSlotsScanner.setEnabled(true);
        this.btnSelectBookableSlotsScanner.setActionCommand(ScheduleListView.Action.REQUEST_BOOKABLE_SLOT_SCANNER_VIEW.toString());
        this.btnSelectBookableSlotsScanner.addActionListener(this);
        
        this.btnSelectUnbookableSlotsScanner.setActionCommand(ScheduleListView.Action.REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW.toString());
        this.btnSelectUnbookableSlotsScanner.addActionListener(this);
        
        btnCreateUpdateAppointment.setEnabled(false);
        btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay.setEnabled(false);
        btnCancelSelectedAppointment.setEnabled(false);
        btnClinicalNotesForSelectedAppointment.setEnabled(false);
        btnSelectTreatmentRequest.setEnabled(false);
        btnCloseView.setEnabled(true);

        btnCancelSelectedAppointment.setText(ScheduleViewActionCaption.CANCEL_APPOINTMENT._1());
        btnClinicalNotesForSelectedAppointment.setText(ScheduleViewActionCaption.CLINICAL_NOTES._1());
        btnCloseView.setText(ScheduleViewActionCaption.CLOSE_VIEW._1());
        btnCreateUpdateAppointment.setText(ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._1());
        btnMakeDeleteEmergencyAppointmentUndoSelection.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._1());
        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay.setText(ScheduleViewActionCaption.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY._1());
        btnNextDay.setText(ScheduleViewActionCaption.NEXT_DAY._1());
        btnNow.setText(ScheduleViewActionCaption.TODAY._1());
        btnPreviousDay.setText(ScheduleViewActionCaption.PREVIOUS_DAY._1());
        //btnSearchAvailableSlotsRequest.setText(ScheduleViewActionCaption.SEARCH_AVAILABLE_SLOTS._1());
        btnSelectTreatmentRequest.setText(ScheduleViewActionCaption.SELECT_TREATMENT._1());
        
        
        btnCloseView.setActionCommand(ScheduleListView.Action.REQUEST_CLOSE_VIEW.toString());
        
        btnCancelSelectedAppointment.setActionCommand(Action.REQUEST_CANCEL_APPOINTMENT.toString());
        btnClinicalNotesForSelectedAppointment.setActionCommand(Action.REQUEST_CLINICAL_NOTE_VIEW.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCreateUpdateAppointment.setActionCommand(Action.REQUEST_CREATE_UPDATE_APPOINTMENT.toString());
        btnMakeDeleteEmergencyAppointmentUndoSelection.setActionCommand(Action.REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION.toString());
        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay.setActionCommand(Action.REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY.toString());
        btnNextDay.setActionCommand(Action.REQUEST_NEXT_DAY.toString());
        btnNow.setActionCommand(Action.REQUEST_NOW.toString());
        btnPreviousDay.setActionCommand(Action.REQUEST_PREVIOUS_DAY.toString());
        //btnSearchAvailableSlotsRequest.setActionCommand(Action.REQUEST_SEARCH_AVAILABLE_SLOTS.toString());
        btnSelectTreatmentRequest.setActionCommand(Action.REQUEST_TREATMENT_VIEW.toString());
        
        btnCancelSelectedAppointment.addActionListener(this);
        btnClinicalNotesForSelectedAppointment.addActionListener(this);
        btnCloseView.addActionListener(this);
        btnCreateUpdateAppointment.addActionListener(this);
        btnMakeDeleteEmergencyAppointmentUndoSelection.addActionListener(this);
        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay.addActionListener(this);
        btnNextDay.addActionListener(this);
        btnNow.addActionListener(this);
        btnPreviousDay.addActionListener(this);
        //btnSearchAvailableSlotsRequest.addActionListener(this);
        btnSelectTreatmentRequest.addActionListener(this); 
        
        //scheduleViewer.add(rdbList);
        //scheduleViewer.add(rdbDiary);
        //rdbList.setActionCommand(Action.REQUEST_SCHEDULE_LIST_VIEW.toString());
        //rdbDiary.setActionCommand(Action.REQUEST_SCHEDULE_DIARY_VIEW.toString());
        //rdbList.addActionListener(this);
        //rdbDiary.addActionListener(this);
        //rdbList.setSelected(true);
        //setScheduleViewMode(ScheduleListView.ScheduleViewMode.LIST);
        
        this.mniCloseView.setActionCommand(ScheduleListView.Action.REQUEST_CLOSE_VIEW.toString());
        this.mniCloseView.addActionListener(this);
        this.mniPrintScheduleSelection.setActionCommand(ScheduleListView.Action.REQUEST_PRINT_SCHEDULE.toString());
        this.mniPrintScheduleSelection.addActionListener(this);
        this.mniColorPicker.setActionCommand(ScheduleListView.Action.REQUEST_COLOUR_PICKER.toString());
        this.mniColorPicker.addActionListener(this);
        
        
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
        dayDatePicker.setDate(day);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dayDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        
        dayDatePicker.addDateChangeListener(this);
        
        configureScheduleDiaryView();
    }
    
    private AppointmentDateVetoPolicy vetoPolicy = null;
    
    @Override
    public void actionPerformed(ActionEvent e){
        ActionEvent actionEvent = null;
        Appointment appointment = null;
        Patient patient = null;
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
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
                doClinicNoteRequest();
                break;
            case REQUEST_CLOSE_VIEW:
                doCloseViewAction();
                break;
            case REQUEST_COLOUR_PICKER:
                //doColourPickerRequest2();
                break;
            case REQUEST_CREATE_UPDATE_APPOINTMENT:
                CustomComboBoxDialog dialog = null;
                switch(getAppointmentMode()){
                    case CREATE:
                        dialog = 
                                new CustomComboBoxDialog(new Frame(), "Select patient for new appointment",this);
                        dialog.setVisible(true);
                        if (dialog.isConfirmed()) {
                            patient = dialog.getSelectedValue();
                        } 
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

                    case UPDATE:
                        switch(getCurrentScheduleDiaryAction()){
                            case CANCEL_APPOINTMENT: //confusing but means when all slots of appt are selected

                                doUpdateAppointmentActionForDiary();
                                break;
                            case EXTEND_APPOINTMENT_DOWN:{
                                appointment = getSelectedAppointmentFromDiary();
                                appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                                this.setViewDescriptorAppointment(appointment);

                                break;
                            }
                            case EXTEND_APPOINTMENT_UP:
                            case EXTEND_APPOINTMENT_UP_AND_DOWN:{
                                appointment = getSelectedAppointmentFromDiary();
                                appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                                appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                                this.setViewDescriptorAppointment(appointment);
                                break;
                            }
                            case EXTEND_SHIFT_APPOINTMENT_DOWN:
                                dialog = new CustomComboBoxDialog(
                                        new Frame(), ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_DOWN);
                                dialog.setVisible(true);
                                if (dialog.isConfirmed()){
                                    switch(dialog.getSelectedDiaryAction()){
                                        case EXTEND_APPOINTMENT_DOWN:
                                            doExtendAppointmentDown();
                                            appointment = getSelectedAppointmentFromDiary();
                                            appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                                            this.setViewDescriptorAppointment(appointment);
                                            break;
                                        case SHIFT_APPOINTMENT_DOWN:
                                            doShiftAppointmentDown();
                                            appointment = getSelectedAppointmentFromDiary();
                                            appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                                            this.setViewDescriptorAppointment(appointment);
                                            break;
                                    }
                                }
                                break;
                            case EXTEND_SHIFT_APPOINTMENT_UP:
                                dialog = new CustomComboBoxDialog(
                                        new Frame(), ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_UP);
                                dialog.setVisible(true);
                                if (dialog.isConfirmed()){
                                    switch(dialog.getSelectedDiaryAction()){
                                        case EXTEND_APPOINTMENT_UP:
                                            doExtendAppointmentUp();
                                            appointment = getSelectedAppointmentFromDiary();
                                            appointment.setDuration(this.getAppointmentFromDiaryWithUpdates().getDuration());
                                            appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                                            this.setViewDescriptorAppointment(appointment);
                                            break;
                                        case SHIFT_APPOINTMENT_UP:
                                            doShiftAppointmentUp();
                                            appointment = getSelectedAppointmentFromDiary();
                                            appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                                            this.setViewDescriptorAppointment(appointment);
                                            break;
                                    }
                                }
                                break;
                            case SHIFT_APPOINTMENT_DOWN:
                            case SHIFT_APPOINTMENT_UP:{
                                appointment = getSelectedAppointmentFromDiary();
                                appointment.setStart(this.getAppointmentFromDiaryWithUpdates().getStart());
                                this.setViewDescriptorAppointment(appointment);
                                break;
                            }
                            case SHORTEN_APPOINTMENT:
                                appointment = getSelectedAppointmentFromDiary();
                                appointment.setDuration(getAppointmentFromDiaryWithUpdates().getDuration());
                                this.setViewDescriptorAppointment(appointment);
                        }
                        actionEvent = new ActionEvent(this,
                                ActionEvent.ACTION_PERFORMED,
                                ViewController.ScheduleViewControllerActionEvent.UPDATE_APPOINTMENT_REQUEST.toString());
                        this.getMyController().actionPerformed(actionEvent);
                        this.doScheduleTitleRefresh(null);
                        break;
                    case NONE:
                        break;
                }
            /*
            case REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION:
                tblAppointments.clearSelection();
                doScheduleTitleRefresh(null);
                break;
            case REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY:
                btnMarkSlotUnbookableOrMoveToAnotherDayActionPerformed();
                this.disableAllScheduleOperationControls();
                break;*/
            case REQUEST_NEXT_DAY:
                doNextDayAction();
                break;
            /*
            case REQUEST_NON_SURGERY_DAY:
                mniSelectNonSurgeryDayActionPerformed();
                break;*/
            case REQUEST_NOW:
                doNowAction();
                break;
            case REQUEST_PREVIOUS_DAY:
                doPreviousDayAction();
                break;
            case REQUEST_PRINT_SCHEDULE:
                getMyController().getDescriptor().getViewDescription().setScheduleDay(dayDatePicker.getDate());
                actionEvent = new ActionEvent(this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.PRINT_SCHEDULE_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                doOpenDocumentForPrinting(SystemDefinition.getPMSPrintFolder() 
                        + SystemDefinition.PATIENT_SCHEDULE_FILENAME);
                break;
            /*
            case REQUEST_SCHEDULE_DIARY_VIEW:
                setScheduleViewMode(ScheduleViewMode.DIARY);
                
                actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            case REQUEST_SCHEDULE_LIST_VIEW:
                setScheduleViewMode(ScheduleViewMode.LIST);
                actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;*/
            /*
            case REQUEST_SEARCH_AVAILABLE_SLOTS:
                doSearchAvailableSlotsAction();
                break;
            case REQUEST_SURGERY_DAY_EDITOR:
                mniSurgeryDaysEditorActionPerformed();
                break;*/
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
        this.disableAllScheduleOperationControls();
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
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        
    }
    
    private void doCloseViewAction() {                                             

        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException e){
            
        }
        
    }
    
    private void configureScheduleDiaryView(){
        this.tblScheduleMorning = new JTable(new ScheduleDiaryTableModel());
        this.scrMorningTable.setViewportView(tblScheduleMorning);
        ScheduleDiaryTableModel model = (ScheduleDiaryTableModel)tblScheduleMorning.getModel();
        setScheduleMorningTableListener();
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
        setScheduleAfternoonTableListener();
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
        setScheduleEveningTableListener();
        this.tblScheduleEvening.setDefaultRenderer(LocalDateTime.class, new ScheduleDiaryTableLocalDateTimeRenderer());
        this.tblScheduleEvening.setDefaultRenderer(Patient.class, new ScheduleDiaryTablePatientRenderer());
        tblScheduleEvening.getColumnModel().getColumn(2).setCellRenderer(new ScheduleDiaryTableStringRenderer());
        tableHeader = this.tblScheduleEvening.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true); 
        ViewController.setJTableColumnProperties(tblScheduleEvening, scrEveningTable.getPreferredSize().width, 10,40,50);
    }
    
    private void populateScheduleDiaryView(){
        ScheduleDiaryTableModel modelMorning = null;
        ScheduleDiaryTableModel modelAfternoon  = null;
        ScheduleDiaryTableModel modelEvening = null;
        
        modelMorning = (ScheduleDiaryTableModel)tblScheduleMorning.getModel();
        modelMorning.removeAllElements();
        modelAfternoon = (ScheduleDiaryTableModel)tblScheduleAfternoon.getModel();
        modelAfternoon.removeAllElements();
        modelEvening = (ScheduleDiaryTableModel)tblScheduleEvening.getModel();
        modelEvening.removeAllElements();
        
        ArrayList<Slot> schedule = getMyController().getDescriptor()
                        .getControllerDescription().getAppointmentSlotsForDayInDiaryFormat();
        Iterator<Slot> it = schedule.iterator();
        int rowCount = 0;
        while (it.hasNext()){
            Slot slot = (Slot)it.next();
            for(Slot _slot : slot.get()){
                if (rowCount<36) modelMorning.addElement(_slot);
                else if (rowCount<72) modelAfternoon.addElement(_slot);
                else modelEvening.addElement(_slot);
                rowCount++;
            } 
        }
        this.clearSelectionFromScheduleTable();
        setTitle(getMyController().getDescriptor().getControllerDescription().getScheduleDay().
                format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
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

    private ListSelectionModel lsmForScheduleMorningTable = null;
    private void setScheduleMorningTableListener(){
        this.tblScheduleMorning.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsmForScheduleMorningTable = this.tblScheduleMorning.getSelectionModel();
        lsmForScheduleMorningTable.addListSelectionListener(this); 
        tblScheduleMorning.addMouseListener(this);
    }
    
    private ListSelectionModel lsmForScheduleAfternoonTable = null;
    private void setScheduleAfternoonTableListener(){
        this.tblScheduleAfternoon.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsmForScheduleAfternoonTable = this.tblScheduleAfternoon.getSelectionModel();
        lsmForScheduleAfternoonTable.addListSelectionListener(this); 
        tblScheduleAfternoon.addMouseListener(this);
    }
    
    private ListSelectionModel lsmForScheduleEveningTable = null;
    private void setScheduleEveningTableListener(){
        this.tblScheduleEvening.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsmForScheduleEveningTable = this.tblScheduleEvening.getSelectionModel();
        lsmForScheduleEveningTable.addListSelectionListener(this); 
        tblScheduleEvening.addMouseListener(this);
    }
    
    private void clearSelectionFromScheduleTable(){
        if (tblScheduleMorning!=null) tblScheduleMorning.clearSelection();
        if (this.tblScheduleAfternoon!=null) tblScheduleAfternoon.clearSelection();
        if (tblScheduleMorning!=null) tblScheduleMorning.clearSelection();
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
                    .CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST;
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
   
    private AppointmentMode appointmentMode = null;
    private void setAppointmentMode(AppointmentMode value){
        appointmentMode = value;        switch(appointmentMode){
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
    
    private Appointment appointmentFromDiaryWithUpdates = null;
    private void setAppointmentFromDiaryWithUpdates(Appointment appointment){
        appointmentFromDiaryWithUpdates = appointment;
    }
    private Appointment getAppointmentFromDiaryWithUpdates(){
        return appointmentFromDiaryWithUpdates;
    }
    
    private void doClinicNoteRequest(){
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
                    .CLINICAL_NOTE_VIEW_CONTROLLER_REQUEST;
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
    
    private void doExtendAppointmentDown(){
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
    
    private void doExtendAppointmentUp(){
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
        //appointment = slot.getAppointment();
        appointment = new Appointment();
        appointment.setStart(start);
        appointment.setDuration(duration);
        setAppointmentFromDiaryWithUpdates(appointment);
        setAppointmentMode(AppointmentMode.UPDATE);
    }
    
    private void doShiftAppointmentDown(){
        Slot slot = null;
        Appointment appointment = null;
        LocalDateTime end = null;
        LocalDateTime start = null;
        long minutes;
        
        for(int row : getSelection()){
            slot = getScheduleDiaryTableModel().getElementAt(row);
            if (slot.getIsLastSlotOfAppointment()!=null){
                if (slot.getIsLastSlotOfAppointment()){
                    end = slot.getStart();
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
            //appointment = slot.getAppointment();
            start = slot.getAppointment().getStart();
            appointment.setStart(start.plusMinutes(minutes));
            setAppointmentFromDiaryWithUpdates(appointment);
            setSelectedAppointmentFromDiary(slot.getAppointment());
            setAppointmentMode(AppointmentMode.UPDATE);
            //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
        }else JOptionPane.showInternalMessageDialog(
                this, "selected appointment end is null!", "View error", JOptionPane.WARNING_MESSAGE);
    }
    
    private void doShiftAppointmentUp(){
        Slot slot = null;
        LocalDateTime start = null;
        Appointment appointment;
        long minutes;
        for(int row : getSelection()){
            slot = getScheduleDiaryTableModel().getElementAt(row);
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
            //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
        }else JOptionPane.showInternalMessageDialog(
                this, "selected appointment start is null!", "View error", JOptionPane.WARNING_MESSAGE);
    }
    
    private Slot getFirstSelectedSlot(){
        return getScheduleDiaryTableModel().getElementAt(getSelection()[0]);
    }
    
    private Slot getLastSelectedSlot(){
        return getScheduleDiaryTableModel().getElementAt(getSelection()[getSelection().length-1]);
    }
    
    private ScheduleDiaryTableModel scheduleDiaryTableModel ;
    private void setScheduleDiaryTableModel(ScheduleDiaryTableModel value){
        scheduleDiaryTableModel = value;
    }
    private ScheduleDiaryTableModel getScheduleDiaryTableModel(){
        return scheduleDiaryTableModel;
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
    
    private void disableAllScheduleOperationControls(){
        this.btnCancelSelectedAppointment.setEnabled(false);
        this.btnClinicalNotesForSelectedAppointment.setEnabled(false);
        //this.btnCloseView.setEnabled(false);
        this.btnCreateUpdateAppointment.setEnabled(false);
        this.btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
        this.btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay.setEnabled(false);
        this.btnSelectTreatmentRequest.setEnabled(false);
    }
    
    private int[] selection = null;
    private int[] getSelection(){
        return selection;
    }
    private void setSelection(int[] value){
        selection = value;
    }
    
    private final DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    
    public enum ScheduleDiaryAction{
        CANCEL_APPOINTMENT,
        CREATE_APPOINTMENT,
        EXTEND_APPOINTMENT_DOWN,
        EXTEND_APPOINTMENT_UP,
        EXTEND_APPOINTMENT_UP_AND_DOWN,
        EXTEND_SHIFT_APPOINTMENT_DOWN,  //request ambiguous because case of a single slot appointment
        EXTEND_SHIFT_APPOINTMENT_UP,    //request ambiguous because case of a single slot appointment
        SHIFT_APPOINTMENT_DOWN,
        SHIFT_APPOINTMENT_UP,
        SHORTEN_APPOINTMENT,
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
        REQUEST_CREATE_UPDATE_APPOINTMENT,
        REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION,
        REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY,
        REQUEST_NEXT_DAY,
        REQUEST_NON_SURGERY_DAY,
        REQUEST_NOW,
        REQUEST_PREVIOUS_DAY,
        REQUEST_PRINT_SCHEDULE,
        REQUEST_SEARCH_AVAILABLE_SLOTS,
        REQUEST_SURGERY_DAY_EDITOR,
        REQUEST_SWITCH_VIEW,            //SVC know which view tro switch to because the ActionEvent source the type (List/Diary) the caller is
        REQUEST_TREATMENT_VIEW,
        REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlScheduleForDay = new javax.swing.JPanel();
        scrMorningTable = new javax.swing.JScrollPane();
        tblScheduleMorning = new javax.swing.JTable();
        scrAfternoonTable = new javax.swing.JScrollPane();
        tblScheduleAfternoon = new javax.swing.JTable();
        scrEveningTable = new javax.swing.JScrollPane();
        tblScheduleEvening = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        btnPreviousDay = new javax.swing.JButton();
        btnNow = new javax.swing.JButton();
        btnNextDay = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnSelectBookableSlotsScanner = new javax.swing.JButton();
        btnSelectUnbookableSlotsScanner = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnMakeDeleteEmergencyAppointmentUndoSelection = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay = new javax.swing.JButton();
        btnClinicalNotesForSelectedAppointment = new javax.swing.JButton();
        btnSelectTreatmentRequest = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnSwitchView = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuOptions = new javax.swing.JMenu();
        mniPrintScheduleSelection = new javax.swing.JMenuItem();
        mniColorPicker = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule details"));

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

        javax.swing.GroupLayout pnlScheduleForDayLayout = new javax.swing.GroupLayout(pnlScheduleForDay);
        pnlScheduleForDay.setLayout(pnlScheduleForDayLayout);
        pnlScheduleForDayLayout.setHorizontalGroup(
            pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrMorningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrAfternoonTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrEveningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlScheduleForDayLayout.setVerticalGroup(
            pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrMorningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrAfternoonTable, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrEveningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule day selection"));

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
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
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnNow, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addComponent(btnNextDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnPreviousDay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Slot scanner"));

        btnSelectBookableSlotsScanner.setText("Bookable slots");
        btnSelectBookableSlotsScanner.setMinimumSize(new java.awt.Dimension(60, 23));
        btnSelectBookableSlotsScanner.setPreferredSize(new java.awt.Dimension(62, 23));

        btnSelectUnbookableSlotsScanner.setText("Unbookable slots");
        btnSelectUnbookableSlotsScanner.setMinimumSize(new java.awt.Dimension(60, 23));
        btnSelectUnbookableSlotsScanner.setPreferredSize(new java.awt.Dimension(62, 23));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(btnSelectBookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSelectUnbookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSelectBookableSlotsScanner, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(btnSelectUnbookableSlotsScanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnCreateUpdateAppointment.setText(ScheduleViewActionCaption.CREATE_UPDATE_APPOINTMENT._1());

        btnMakeDeleteEmergencyAppointmentUndoSelection.setText(ScheduleViewActionCaption.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._1());

        btnCancelSelectedAppointment.setText(ScheduleViewActionCaption.CANCEL_APPOINTMENT._1());

        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay.setText("");

        btnClinicalNotesForSelectedAppointment.setText(ScheduleViewActionCaption.CLINICAL_NOTES._1());
        btnClinicalNotesForSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClinicalNotesForSelectedAppointmentActionPerformed(evt);
            }
        });

        btnSelectTreatmentRequest.setText(ScheduleViewActionCaption.SELECT_TREATMENT._1());

        btnCloseView.setText(ScheduleViewActionCaption.CLOSE_VIEW._1());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMakeDeleteEmergencyAppointmentUndoSelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnMakeDeleteEmergencyAppointmentUndoSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("View format"));

        btnSwitchView.setText("<html><center>Switch to</center><center>list view</center></html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnSwitchView, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSwitchView)
                .addContainerGap())
        );

        mnuOptions.setText("Actions");

        mniPrintScheduleSelection.setText("Print schedule");
        mnuOptions.add(mniPrintScheduleSelection);

        mniColorPicker.setText("Colour picker");
        mnuOptions.add(mniColorPicker);
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
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClinicalNotesForSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClinicalNotesForSelectedAppointmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnClinicalNotesForSelectedAppointmentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelSelectedAppointment;
    private javax.swing.JButton btnClinicalNotesForSelectedAppointment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateAppointment;
    private javax.swing.JButton btnMakeDeleteEmergencyAppointmentUndoSelection;
    private javax.swing.JButton btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay;
    private javax.swing.JButton btnNextDay;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnPreviousDay;
    private javax.swing.JButton btnSelectBookableSlotsScanner;
    private javax.swing.JButton btnSelectTreatmentRequest;
    private javax.swing.JButton btnSelectUnbookableSlotsScanner;
    private javax.swing.JButton btnSwitchView;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniColorPicker;
    private javax.swing.JMenuItem mniPrintScheduleSelection;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlScheduleForDay;
    private javax.swing.JScrollPane scrAfternoonTable;
    private javax.swing.JScrollPane scrEveningTable;
    private javax.swing.JScrollPane scrMorningTable;
    private javax.swing.JTable tblScheduleAfternoon;
    private javax.swing.JTable tblScheduleEvening;
    private javax.swing.JTable tblScheduleMorning;
    // End of variables declaration//GEN-END:variables
}
