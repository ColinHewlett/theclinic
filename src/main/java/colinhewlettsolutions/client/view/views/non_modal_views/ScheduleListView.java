/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import com.bric.colorpicker.listeners.ColorListener;
import com.bric.colorpicker.models.ColorModel;
import com.bric.colorpicker.ColorPickerDialog;
import colinhewlettsolutions.client.controller.ScheduleViewController;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.model.non_entity.Slot;
import colinhewlettsolutions.client.controller.SystemDefinition.ScheduleSlotType;
import colinhewlettsolutions.client.view.support_classes.renderers.AppointmentsTableDurationRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.TableLocalTime24HourFormatCentredRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.ScheduleListTablePatientRenderer;
import colinhewlettsolutions.client.view.support_classes.models.ScheduleListTableModel;
import colinhewlettsolutions.client.view.support_classes.models.ScheduleDiaryTableModel;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.controller.DesktopViewController;
import colinhewlettsolutions.client.view.support_classes.AppointmentDateVetoPolicy;
import colinhewlettsolutions.client.view.View;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import colinhewlettsolutions.client.controller.Descriptor;
import static colinhewlettsolutions.client.controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENTS_FOR_DAY_RECEIVED;
import static colinhewlettsolutions.client.controller.ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED;
import static colinhewlettsolutions.client.controller.ViewController.ViewMode.SCHEDULE_REFERENCED_FROM_PATIENT_VIEW;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.HashMap;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import javax.swing.JButton; 
import javax.swing.JTable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.SwingUtilities;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import colinhewlettsolutions.client.model.non_entity.Captions;
import colinhewlettsolutions.client.view.views.dialog_views.DialogView;
import static colinhewlettsolutions.client.controller.ViewController.ViewMode.SCHEDULE_REQUESTED_FROM_DESKTOP_VIEW;

/**
 *
 * @author colin
 */
public class ScheduleListView extends BookingView 
                          implements ActionListener, 
                                     ColorListener,
                                     DateChangeListener,
                                     DateHighlightPolicy,
                                     ListSelectionListener,
                                     MouseListener,
                                     PropertyChangeListener,
                                     TableModelListener
                                     {
    
    //private ScheduleListTableModel tableModel = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private DatePickerSettings settings = null;
    private ArrayList<Appointment> appointments = null;
    private final DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private AppointmentDateVetoPolicy vetoPolicy = null;
    
    enum Action{
        NONE,
        REQUEST_BOOKABLE_SLOT_SCANNER_VIEW,
        REQUEST_CANCEL_APPOINTMENT,
        REQUEST_CANCELLED_APPOINTMENT_VIEW,
        REQUEST_CLINICAL_NOTE_VIEW,
        REQUEST_CLOSE_VIEW,
        REQUEST_COLOUR_PICKER,
        REQUEST_CREATE_UPDATE_APPOINTMENT,
        REQUEST_EARLY_BOOKING_START_TIME,
        REQUEST_LATE_BOOKING_END_TIME,
        REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION,
        REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT,
        REQUEST_NEXT_DAY,
        REQUEST_NON_SURGERY_DAY,
        REQUEST_NOW,
        REQUEST_PREVIOUS_DAY,
        REQUEST_PRINT_SCHEDULE,
        REQUEST_SEARCH_AVAILABLE_SLOTS,
        REQUEST_SURGERY_DAY_EDITOR,
        REQUEST_SWITCH_VIEW,            //SVC know which view tro switch to because the ActionEvent source the type (List/Diary) the caller is
        REQUEST_TO_DO_LIST_VIEW,
        REQUEST_TREATMENT_VIEW,
        REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW,
        REQUEST_USER_SCHEDULE_LIST_COLOR_SETTINGS_VIEW
    }
    
    enum ScheduleViewMode{
        LIST,
        DIARY
    }
    
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
    
    private ScheduleDiaryAction currentScheduleDiaryAction = null;
    private void setCurrentScheduleDiaryAction(ScheduleDiaryAction action){
        currentScheduleDiaryAction = action;
    }
    private ScheduleDiaryAction getCurrentScheduleDiaryAction(){
        return currentScheduleDiaryAction;
    }
    
    private ScheduleDiaryTableModel scheduleDiaryTableModel ;
    private void setScheduleDiaryTableModel(ScheduleDiaryTableModel value){
        scheduleDiaryTableModel = value;
    }
    private ScheduleDiaryTableModel getScheduleDiaryTableModel(){
        return scheduleDiaryTableModel;
    }
    
    private ScheduleListTableModel scheduleListTableModel ;
    private void setScheduleListTableModel(ScheduleListTableModel value){
        scheduleListTableModel = value;
    }
    private ScheduleListTableModel getScheduleListTableModel(){
        return scheduleListTableModel;
    }
    
    enum UserDiaryActionPreference{
        EXTEND,
        SHIFT;
    }
    
    private UserDiaryActionPreference getUserDiaryActionPreference(){
        UserDiaryActionPreference result = null;
        return result;
    }
    
    private int[] selection = null;
    private int[] getSelection(){
        return selection;
    }
    private void setSelection(int[] value){
        selection = value;
    }
    
    private ScheduleDiaryAction getScheduleDiaryAction(){
        ScheduleDiaryAction result = null;
        
        if(!(getIsMoreThanOneAppointmentSelected() || getIsSelectedAppointmentNonContiguous())){
            if (getAppointmentExtendUpAndDown()) result = ScheduleDiaryAction.EXTEND_APPOINTMENT_UP_AND_DOWN;
            else if (getAppointmentExtendDown()) result = ScheduleDiaryAction.EXTEND_APPOINTMENT_DOWN;
            else if (getAppointmentExtendUp()) result = ScheduleDiaryAction.EXTEND_APPOINTMENT_UP;
            else if (getAppointmentShiftDown()) result = ScheduleDiaryAction.SHIFT_APPOINTMENT_DOWN;
            else if (getAppointmentShiftUp()) result = ScheduleDiaryAction.SHIFT_APPOINTMENT_UP;
            else if (getAppointmentCancel()) result = ScheduleDiaryAction.CANCEL_APPOINTMENT;
            else if (getAppointmentCreate()) result = ScheduleDiaryAction.CREATE_APPOINTMENT;
            else if (getAppointmentShortening()) result = ScheduleDiaryAction.SHORTEN_APPOINTMENT;
            else result = ScheduleDiaryAction.NONE;

            if (getIsSingleSlotAppointmentFound()){
                setAppointmentMode(AppointmentMode.UPDATE);
                switch(result){
                    case SHIFT_APPOINTMENT_DOWN:
                        result = ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_DOWN;
                        break;
                    case SHIFT_APPOINTMENT_UP:
                            result = ScheduleDiaryAction.EXTEND_SHIFT_APPOINTMENT_UP;
                        break;   
                }
            }
        }else result = ScheduleDiaryAction.NONE;
        setCurrentScheduleDiaryAction(result);
        return result;  
    }
    
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
    
    private Patient getAppointmentPatientSelected(){
        Patient result = null;
        Patient patient = null;
        for (int row : getSelection()){
            Slot slot = getScheduleDiaryTableModel().getElementAt(row);
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
    
    private Boolean getIsSelectedAppointmentNonContiguous(){
        Boolean result = false;
        boolean isFirstAppointmentSlotFound = false;
        boolean isUnbookableSlotFoundAfterFirstAppointmentFound = false;
        boolean isNonContiguousAppointmentSlotFound = false;
        for(int row : getSelection()){
            Slot slot = getScheduleDiaryTableModel().getElementAt(row);
            if (isFirstAppointmentSlotFound){
                if(isUnbookableSlotFoundAfterFirstAppointmentFound){
                    if(slot.getIsBooked()) isNonContiguousAppointmentSlotFound = true;
                    break;
                }else if(slot.getIsBookable()) isUnbookableSlotFoundAfterFirstAppointmentFound = true;
            }else if(slot.getIsBooked()) isFirstAppointmentSlotFound = true;
        }
        return isNonContiguousAppointmentSlotFound;
    }
    
    private Boolean getIsMoreThanOneAppointmentSelected(){
        Boolean result = false;
        Appointment appointment = null;
        for (int row : getSelection()){
            Slot slot = getScheduleDiaryTableModel().getElementAt(row);
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
            Slot slot = getScheduleDiaryTableModel().getElementAt(row);
            if (!slot.getIsBookable()){
                result = slot.getIsSingleSlotAppointment();
                break;
            }
        }
        return result;
    }
    
    private Slot getFirstSelectedSlot(){
        return getScheduleDiaryTableModel().getElementAt(getSelection()[0]);
    }
    
    private Slot getLastSelectedSlot(){
        return getScheduleDiaryTableModel().getElementAt(getSelection()[getSelection().length-1]);
    }
    
    private Rectangle viewablePortionOfSchedule = null;
    private void setViewablePortionOfSchedule(Rectangle value){
        viewablePortionOfSchedule = value;
    }
    private Rectangle getViewablePortionOfSchedule(){
        return viewablePortionOfSchedule; 
    }
    
    private Boolean getAppointmentCancel(){
        Boolean result = false;
        boolean isFirstSelectedSlotBooked = true;
        boolean isFirstSelectedSlotAppointmentFirstSlot = true;
        boolean isLastSelectedSlotBooked = true;
        boolean isLastSelectedSlotAppointmentLastSlot = true;

        Slot slot = getFirstSelectedSlot();
        if (slot.getIsBookable()) isFirstSelectedSlotBooked = false;
        if(isFirstSelectedSlotBooked){
            if (!slot.getIsFirstSlotOfAppointment()) isFirstSelectedSlotAppointmentFirstSlot = false; 
            if(isFirstSelectedSlotAppointmentFirstSlot){
                slot = getLastSelectedSlot();
                if(slot.getIsBookable()) isLastSelectedSlotBooked = false;
                if(isLastSelectedSlotBooked){
                    if (!slot.getIsLastSlotOfAppointment()) isLastSelectedSlotAppointmentLastSlot = false;
                }
            }  
        }

        return isFirstSelectedSlotBooked
                && isFirstSelectedSlotAppointmentFirstSlot
                && isLastSelectedSlotBooked
                && isLastSelectedSlotAppointmentLastSlot;
    }

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
    
    private Boolean getAppointmentExtendUpAndDown(){
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
    }
    
    private Boolean getAppointmentExtendUp(){
        Boolean result = false;
        boolean isFirstSelectedSlotBookable = true;
        boolean isLastSelectedSlotBooked = true;
        boolean isLastSelectedSlotAppointmentLastSlot = false;
        
        Slot slot = getFirstSelectedSlot();
        if (slot.getIsBooked()) isFirstSelectedSlotBookable = false;
        if(isFirstSelectedSlotBookable){
            slot = getLastSelectedSlot();
            if (slot.getIsBookable()) isLastSelectedSlotBooked = false;
            if(isLastSelectedSlotBooked){
                if (slot.getIsLastSlotOfAppointment()) isLastSelectedSlotAppointmentLastSlot = true;
            }
        }
        

        return isFirstSelectedSlotBookable
                && isLastSelectedSlotBooked
                && !isLastSelectedSlotAppointmentLastSlot;
        
    }
    
    private Boolean getAppointmentExtendDown(){
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
        
        Slot slot = getFirstSelectedSlot();
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
    
    private Boolean getAppointmentShiftDown(){
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
    }
    
    private Boolean getAppointmentShiftUp(){
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
    
    private Boolean getAppointmentShortening(){
        Boolean result = false;
        boolean isFirstSlotBooked = true;
        boolean isFirstSlotAppointmentFirstSlot = false;
        boolean isLastSlotBooked = true;
        boolean isLastSlotAppointmentLastSlot = true;
        
        Slot slot = getFirstSelectedSlot();
        if(slot.getIsBookable()) isFirstSlotBooked = false;
        if(isFirstSlotBooked){
            if (slot.getIsFirstSlotOfAppointment()) isFirstSlotAppointmentFirstSlot = true;
            if (!isFirstSlotAppointmentFirstSlot){
                slot = getLastSelectedSlot();
                if(slot.getIsBookable()) isLastSlotBooked = false;
                if (isLastSlotBooked)
                    if(!slot.getIsLastSlotOfAppointment()) isLastSlotAppointmentLastSlot = false;
            }   
        }
        
        return isFirstSlotBooked
                && !isFirstSlotAppointmentFirstSlot
                && isLastSlotBooked
                && isLastSlotAppointmentLastSlot;
    }
    

    
    enum UnbookableSlotOrMoveOrFetchPatientMode{MARK,CANCEL,MOVE,FETCH_PATIENT,NONE};
    private UnbookableSlotOrMoveOrFetchPatientMode unbookableSlotMode = null;
    private void setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode value){
        unbookableSlotMode = value;
        switch (unbookableSlotMode){
            case MARK:
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(Captions.ScheduleView.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS._1());
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(true);
                break;
            case CANCEL:
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(Captions.ScheduleView.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS._2());
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(true);
                break;
            case MOVE:
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(Captions.ScheduleView.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS._3());
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(true);
                break;
            case FETCH_PATIENT:
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(Captions.ScheduleView.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS._4());
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(true);
                break;
            case NONE:
                btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(false);
                break;
                
        }
    }
    private UnbookableSlotOrMoveOrFetchPatientMode getUnbookableSlotMode(){
        return unbookableSlotMode;
    }
    
    enum EmergencySlotUndoSelectionMode{MAKE,DELETE,UNDO,NONE};
    private EmergencySlotUndoSelectionMode emergencySlotMode = null;
    private void setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode value){
        emergencySlotMode = value;
        switch (emergencySlotMode){
            case MAKE:
                btnMakeDeleteEmergencyAppointmentUndoSelection.setText(Captions.ScheduleView.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._1());
                btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(true);
                break;
            case DELETE:
                btnMakeDeleteEmergencyAppointmentUndoSelection.setText(Captions.ScheduleView.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._2());
                btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(true);
                break;
            case UNDO:
                btnMakeDeleteEmergencyAppointmentUndoSelection.setText(Captions.ScheduleView.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._3());
                btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(true);
                break;
            case NONE:
                btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
                break;
                
        }
    }
    private EmergencySlotUndoSelectionMode getEmergencySlotMode(){
        return emergencySlotMode;
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
                        Captions.ScheduleView.CREATE_UPDATE_APPOINTMENT._1());
                btnCreateUpdateAppointment.setEnabled(true);
                break;
            case UPDATE:
                btnCreateUpdateAppointment.setText(
                        Captions.ScheduleView.CREATE_UPDATE_APPOINTMENT._2());
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
                if (tblAppointments!=null)tblAppointments.setModel(new ScheduleDiaryTableModel());
                this.disableAllScheduleOperationControls();
                this.btnCloseView.setEnabled(true);
                this.setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode.UNDO);
                this.btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
                this.btnCancelSelectedAppointment.setEnabled(false);
                this.btnClinicalNotesForSelectedAppointment.setEnabled(false);
                this.btnCreateUpdateAppointment.setEnabled(false);
                this.btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
                this.setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode.MOVE);
                this.btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(false);
                this.btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(
                        Captions.ScheduleView.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS._3());
                this.btnSelectTreatmentRequest.setEnabled(false);
                break;
            case LIST:
                if (tblAppointments!=null)tblAppointments.setModel(new ScheduleListTableModel() );
                this.disableAllScheduleOperationControls();
                this.btnCloseView.setEnabled(true);
                this.setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode.MAKE);
                this.btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
        }
    }
    private ScheduleViewMode getScheduleViewMode(){
        return scheduleViewMode;
    }
    
    private void setViewDescriptorAppointment(Appointment appointment){
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment);
    }
    private Appointment getViewDescriptorAppointment(){
        return (Appointment)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT);
    }
    
    private Appointment appointmentFromDiaryWithUpdates = null;
    private void setAppointmentFromDiaryWithUpdates(Appointment appointment){
        appointmentFromDiaryWithUpdates = appointment;
    }
    private Appointment getAppointmentFromDiaryWithUpdates(){
        return appointmentFromDiaryWithUpdates;
    }
    
    private Appointment selectedAppointmentFromDiary = null;
    private void setSelectedAppointmentFromDiary(Appointment appointment){
        selectedAppointmentFromDiary = appointment;
    }
    private Appointment getSelectedAppointmentFromDiary(){
        return selectedAppointmentFromDiary;
    }
    
    private int scheduleViewCurrentlySelectedRowFromList = -1;
    private void setScheduleViewCurrentlySelectedRowFromList(int value){
        scheduleViewCurrentlySelectedRowFromList = value;
    }
    private int getScheduleViewCurrentlySelectedRowFromList(){
        return scheduleViewCurrentlySelectedRowFromList;
    }
    
    private int scheduleViewCurrentlySelectedRowFromDiary = -1;
    private void setScheduleViewCurrentlySelectedRowFromDiary(int value){
        scheduleViewCurrentlySelectedRowFromDiary = value;
    }
    private int getScheduleViewCurrentlySelectedRowFromDiary(){
        return scheduleViewCurrentlySelectedRowFromDiary;
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
    
    private Slot currentlySelectedSlot = null;
    private void setCurrentlySelectedSlot(Slot slot){
        currentlySelectedSlot = slot;
    }
    private Slot getCurrentlySelectedSlot(){
        return currentlySelectedSlot;
    }
    
    
    /**
     * 
     * @param myViewType
     * @param controller
     * @param desktopView 
     */
    public ScheduleListView(View.Viewer myViewType, 
            ViewController controller, 
            DesktopView desktopView) {
        setTitle("Appointment schedule");
        this.setMyViewType(myViewType);
        setMyController(controller); 
        setDesktopView(desktopView);
    }
    
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
            case REQUEST_BOOKABLE_SLOT_SCANNER_VIEW:
                doActionEventFor(ScheduleViewController.Actions.BOOKABLE_SLOT_SCANNER_VIEW_REQUEST);
                break;
            case REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW:
                doActionEventFor(ScheduleViewController.Actions.UNBOOKABLE_SLOT_SCANNER_VIEW_REQUEST);
                break;
            case REQUEST_CANCEL_APPOINTMENT:
                switch(getScheduleViewMode()){
                    case LIST:
                        doCancelSelectedAppointmentAction();
                        break;
                }
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
            case REQUEST_COLOUR_PICKER:
                doColourPickerRequest2();
                break;
            case REQUEST_CREATE_UPDATE_APPOINTMENT:
                switch(getAppointmentMode()){
                    case CREATE:
                        switch(getScheduleViewMode()){
                            case LIST:
                                doCreateAppointmentAction();
                                break;
                        }
                        break;
                    case UPDATE:
                        switch(getScheduleViewMode()){
                            case LIST:
                                doUpdateAppointmentAction();
                                break;
                        }
                        break;
                    case NONE:
                        break;
                }
                break;
            case REQUEST_EARLY_BOOKING_START_TIME:
                items = new ArrayList<LocalDateTime>();
                appointment = ((ArrayList<Appointment>)getMyController().getDescriptor().getControllerDescription()
                                .getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT)).get(0);
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
                            .setProperty(SystemDefinition.Properties.EARLY_BOOKING_START_TIME, (LocalDateTime)getDialogView().getSelectedItem());
                    doActionEventFor(ScheduleViewController.Actions.FIRST_APPOINTMENT_START_TIME_REQUEST);
                }
                break;
            case REQUEST_LATE_BOOKING_END_TIME:
                /**
                 * -- finds last appointment
                 * -- is this is not a bookable slot
                 * -- -- finds the start time of the last appointment
                 * -- -- calculates the end time of appointment from its duration 
                 * -- 
                 */
                items = new ArrayList<>();
                int count = ((ArrayList<Appointment>)getMyController().getDescriptor().getControllerDescription()
                                .getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT)).size();
                appointment = ((ArrayList<Appointment>)getMyController().getDescriptor().getControllerDescription()
                                .getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT)).get(count-1);
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
                                .setProperty(SystemDefinition.Properties.LATE_BOOKING_END_TIME, 
                                        (LocalDateTime)getDialogView().getSelectedItem());
                        doActionEventFor(ScheduleViewController.Actions.LAST_APPOINTMENT_END_TIME_REQUEST);
                    }
                //}
                break;
            case REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION:
                switch(getScheduleViewMode()){
                    /*
                    case DIARY:
                        tblAppointments.clearSelection();
                        doScheduleTitleRefresh(null);
                        break;
                    */
                    case LIST:{
                        switch(getScheduleSlotType()){
                            case EMERGENCY_SCHEDULE_SLOT:
                                deleteEmergencyAppointment();
                                break;
                            case BOOKED_SCHEDULE_SLOT:
                                makeEmergencyAppointment();
                                break;
                        }
                        break;
                    }
                }
                break;
            case REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT:
                btnMarkCancelUnbookableSlotOrMoveToAnotherDayOrFetchPatientActionPerformed();
                this.disableAllScheduleOperationControls();
                this.tblAppointments.clearSelection();
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
                doPrintScheduleRequest();
                break;
            case REQUEST_SEARCH_AVAILABLE_SLOTS:
                doSearchAvailableSlotsAction();
                break;
            case REQUEST_SURGERY_DAY_EDITOR:
                mniSurgeryDaysEditorActionPerformed();
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
                doActionEventFor(ScheduleViewController.Actions.SWITCH_VIEW_REQUEST);
                break;
            case REQUEST_TO_DO_LIST_VIEW:
                doActionEventFor(ScheduleViewController.Actions.TO_DO_LIST_VIEW_REQUEST);
                break;
            case REQUEST_TREATMENT_VIEW:
                doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_EDITOR_TREATMENT_VIEW_REQUEST);
                break; 
            case REQUEST_USER_SCHEDULE_LIST_COLOR_SETTINGS_VIEW:
                doActionEventFor(ScheduleViewController.Actions.USER_SCHEDULE_LIST_SETTINGS_EDITOR_VIEW_REQUEST);
                break;
            case NONE:
                Toolkit.getDefaultToolkit().beep();
                if (getSelection().length == 1){
                    Slot slot = getScheduleDiaryTableModel().getElementAt(getSelection()[0]);
                    if (slot.getIsBooked()){
                        if (!slot.getIsFirstSlotOfAppointment()) {
                            tblAppointments.clearSelection();
                        }
                    }else tblAppointments.clearSelection();
                }else {
                    tblAppointments.clearSelection();
                }
                break;
        }
        switch(getScheduleViewMode()){
            case DIARY:
                this.disableAllScheduleOperationControls();
                this.tblAppointments.clearSelection();
                this.btnCloseView.setEnabled(true);
        }
    }
    
    @Override
    public void dateChanged(DateChangeEvent e){
        getMyController().getDescriptor().getViewDescription().
                setProperty(SystemDefinition.Properties.SCHEDULE_DAY, this.dayDatePicker.getDate());
            if (tblAppointments!=null) tblAppointments.clearSelection();
            doActionEventFor(ScheduleViewController.Actions.APPOINTMENTS_FOR_DAY_REQUEST);
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
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerPropertyChangeEvent propertyName = 
                ViewController.DesktopViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
            switch(propertyName){
                case USER_SYSTEM_WIDE_SETTINGS_RECEIVED ->{
                    setScheduleTitledBorderSettings();
                    break;
                }
            }
        }else{
            ViewController.ScheduleViewControllerPropertyChangeEvent propertyName = 
                    ViewController.ScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
            //setViewDescriptor((Descriptor)e.getNewValue());
            switch (propertyName){
                case CLOSE_VIEW_REQUEST_RECEIVED ->{
                    if (getIsViewSwitchPending()) removeInternalFrameListener(internalFrameAdapter);
                    doCloseViewAction();
                    break;
                }
                case APPOINTMENTS_FOR_DAY_RECEIVED ->{
                    switch (getScheduleViewMode()){
                        case LIST ->{
                            //javax.swing.SwingUtilities.invokeLater(new Runnable(){
                                //@Override
                                //public void run(){
                                    //configureScheduleListView();
                                    populateScheduleListView();
                                    tblAppointments.clearSelection();
                                //}
                            //});
                            break;
                        }
                    }
                    break;
                }
                case APPOINTMENT_SCHEDULE_ERROR_RECEIVED ->{
                    if (e.getSource() instanceof DesktopViewController){
                        String message = (String)getMyController().getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.ERROR);
                        JOptionPane.showInternalMessageDialog(this, message, "View error",JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                }
                case USER_SCHEDULE_SETTINGS_RECEIVED ->{
                    populateScheduleListView();
                    break;
                }
            }
        }
    }
    
    @Override
    public void tableChanged(TableModelEvent e){
        if (e.getType() == TableModelEvent.UPDATE) {
            if (e.getColumn() == 0) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                ScheduleListTableModel model =  
                        (ScheduleListTableModel)e.getSource();
                Boolean value = (Boolean)model.getValueAt(row, column);
                Appointment appointment = model.getElementAt(row);
                appointment.setHasPatientBeenContacted(value);
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, value);
                //tblAppointments.clearSelection();
                doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_REMINDED_STATUS_UPDATE_REQUEST);
                tblAppointments.clearSelection();
            }
        }
    }
    
    @Override
    public void colorChanged(ColorModel colorModel){

    }
    
    private void doActionEventFor(ScheduleViewController.Actions action){
        ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                action.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private boolean tableValueChangedListenerActivated = false;
    @Override
    public void valueChanged(ListSelectionEvent e){
        Slot slot = null;
        if (e.getSource().equals(this.lsmForAppointmentsListTable)){
            switch (this.getScheduleViewMode()){
                case LIST:{
                    if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
                        int selectedRow = tblAppointments.getSelectedRow();
                        if (selectedRow!=-1){
                            setScheduleViewCurrentlySelectedRowFromList(tblAppointments.getSelectedRow());
                            ScheduleListTableModel model = 
                                    (ScheduleListTableModel)tblAppointments.getModel();
                            Appointment appointment = model.getElementAt(selectedRow);
                            setScheduleSlotType(appointment);
                            getMyController().getDescriptor()
                                    .getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, slot);
                            tableValueChangedListenerActivated = true;
                            Patient patient = (Patient)tblAppointments.getModel().getValueAt(selectedRow, 1);
                            doScheduleTitleRefresh(patient);
                            switch(getScheduleSlotType()){
                                case BOOKABLE_SCHEDULE_SLOT:
                                    setAppointmentMode(AppointmentMode.CREATE);
                                    setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode.MARK);
                                    setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode.NONE);
                                    btnCancelSelectedAppointment.setEnabled(false);
                                    btnClinicalNotesForSelectedAppointment.setEnabled(false);
                                    btnSelectTreatmentRequest.setEnabled(false);
                                    break;
                                case UNBOOKABLE_SCHEDULE_SLOT:
                                    setAppointmentMode(AppointmentMode.NONE);
                                    setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode.CANCEL);
                                    setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode.NONE);
                                    btnCancelSelectedAppointment.setEnabled(false);
                                    btnClinicalNotesForSelectedAppointment.setEnabled(false);
                                    btnSelectTreatmentRequest.setEnabled(false);
                                    break;
                                case EMERGENCY_SCHEDULE_SLOT:
                                    setAppointmentMode(AppointmentMode.NONE);
                                    setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode.DELETE);
                                    setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode.NONE);
                                    btnCancelSelectedAppointment.setEnabled(false);
                                    btnClinicalNotesForSelectedAppointment.setEnabled(true);
                                    btnSelectTreatmentRequest.setEnabled(false);
                                    break;
                                case BOOKED_SCHEDULE_SLOT:
                                    setAppointmentMode(AppointmentMode.UPDATE);
                                    setEmergencySlotUndoMode(EmergencySlotUndoSelectionMode.MAKE);
                                    setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode.FETCH_PATIENT);
                                    btnCancelSelectedAppointment.setEnabled(true);
                                    btnClinicalNotesForSelectedAppointment.setEnabled(true);
                                    btnSelectTreatmentRequest.setEnabled(true);
                                    /*this.btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(
                                            "<html><center>Move to</center><center>another</center><center>day</center></html>");*/
                                    //btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(true);
                                    break;
                            }
                        }else{//no row is selected; so disable all buttons apart from 'close view'
                            btnCreateUpdateAppointment.setEnabled(false);
                            btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
                            btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(false);
                            btnCancelSelectedAppointment.setEnabled(false);
                            btnClinicalNotesForSelectedAppointment.setEnabled(false);
                            btnSelectTreatmentRequest.setEnabled(false);
                            btnCloseView.setEnabled(true);
                            
                        }
                    }else {
                        getMyController().getDescriptor()
                                .getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, null);
                        doScheduleTitleRefresh(null);

                        btnCreateUpdateAppointment.setEnabled(false);
                        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(false);
                        btnCancelSelectedAppointment.setEnabled(false);
                        btnClinicalNotesForSelectedAppointment.setEnabled(false);
                        btnSelectTreatmentRequest.setEnabled(false);
                        btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
                    }
                    break;
                }
            }
        } 
    }
    
    /**
     * on entry ViewDescriptor.ViewMode initialised appropriately for print request (with or without contact details)
     */
    private void doPrintScheduleRequest(){
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, dayDatePicker.getDate());
        doActionEventFor(ScheduleViewController.Actions.PRINT_SCHEDULE_REQUEST);
        String printFolder = (String)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.PRINT_FOLDER);
        doOpenDocumentForPrinting(printFolder + SystemDefinition.FILENAME_FOR_SCHEDULE);
    }
    
    private boolean isViewSwitchPending = false;
    private void setIsViewSwitchPending(boolean value){
        isViewSwitchPending = value;
    }
    private boolean getIsViewSwitchPending(){
        return isViewSwitchPending;
    }
   
    private ListSelectionModel lsmForAppointmentsListTable = null;
    private void setAppointmentTableListener(){
        this.tblAppointments.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsmForAppointmentsListTable = this.tblAppointments.getSelectionModel();
        lsmForAppointmentsListTable.addListSelectionListener(this); 
        tblAppointments.addMouseListener(this);
    }
    
    /*
    private ListSelectionModel lsmForSloAvailabilityTable = null;
    private void setEmptySlotAvailabilityTableListener(){
        this.tblSlotAvailability.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmForSloAvailabilityTable = this.tblSlotAvailability.getSelectionModel();
        lsmForSloAvailabilityTable.addListSelectionListener(this); 
    }*/
    
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ScheduleListView.this.removeInternalFrameListener(internalFrameAdapter);
                //doActionEventFor(ScheduleViewController.Actions.VIEW_CLOSE_NOTIFICATION);
                //doActionEventFor(ScheduleViewController.Actions.VIEW_CLOSE_NOTIFICATION);
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleListView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_CLOSE_NOTIFICATION.toString());
                ScheduleListView.this.getMyController().actionPerformed(actionEvent);
                
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        ScheduleListView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ScheduleViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                ScheduleListView.this.getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }
    
    
    private javax.swing.JMenuItem  mniColorPicker = null;
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
            /*setSize(1090
                    ,685
            );*/
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
        this.mniCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.mniCloseView.addActionListener(this);
        this.mniEarlyBookingStartTime.setActionCommand(Action.REQUEST_EARLY_BOOKING_START_TIME.toString());
        this.mniEarlyBookingStartTime.addActionListener(this);
        this.mniLateBookingEndTime.setActionCommand(Action.REQUEST_LATE_BOOKING_END_TIME.toString());
        this.mniLateBookingEndTime.addActionListener(this);
        //this.mniColorPicker.setActionCommand(Action.REQUEST_COLOUR_PICKER.toString());
        //this.mniColorPicker.addActionListener(this);
        
        if ((Boolean)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_REQUIRED)){
            this.mniColorPicker = new javax.swing.JMenuItem();
            mniColorPicker.setText("Colour settings");
            mniColorPicker.setActionCommand(ScheduleListView.Action.REQUEST_USER_SCHEDULE_LIST_COLOR_SETTINGS_VIEW.toString());
            mniColorPicker.addActionListener(this);
            mnuOptions.remove(mniCloseView);
            mnuOptions.add(mniColorPicker);
            mnuOptions.add(new javax.swing.JSeparator());
            mnuOptions.add(mniCloseView);
        }
        
        this.btnRequestToDoLIst.setActionCommand(Action.REQUEST_TO_DO_LIST_VIEW.toString());
        this.btnRequestToDoLIst.addActionListener(this);
        
        this.btnSelectBookableSlotsScanner.setEnabled(true);
        this.btnSelectBookableSlotsScanner.setActionCommand(Action.REQUEST_BOOKABLE_SLOT_SCANNER_VIEW.toString());
        this.btnSelectBookableSlotsScanner.addActionListener(this);
        
        this.btnSelectUnbookableSlotsScanner.setActionCommand(Action.REQUEST_UNBOOKABLE_SLOT_SCANNER_VIEW.toString());
        this.btnSelectUnbookableSlotsScanner.addActionListener(this);
        
        btnCreateUpdateAppointment.setEnabled(false);
        btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(false);
        btnCancelSelectedAppointment.setEnabled(false);
        btnClinicalNotesForSelectedAppointment.setEnabled(false);
        btnSelectTreatmentRequest.setEnabled(false);
        btnCloseView.setEnabled(true);

        btnCancelSelectedAppointment.setText(Captions.ScheduleView.CANCEL_APPOINTMENT._1());
        btnClinicalNotesForSelectedAppointment.setText(Captions.ScheduleView.CLINICAL_NOTES._1());
        btnCloseView.setText(Captions.CLOSE_VIEW);
        btnCreateUpdateAppointment.setText(Captions.ScheduleView.CREATE_UPDATE_APPOINTMENT._1());
        btnMakeDeleteEmergencyAppointmentUndoSelection.setText(Captions.ScheduleView.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._1());
        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText(Captions.ScheduleView.MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS._1());
        btnNextDay.setText(Captions.ScheduleView.NEXT_DAY._1());
        btnNow.setText(Captions.ScheduleView.TODAY._1());
        btnPreviousDay.setText(Captions.ScheduleView.PREVIOUS_DAY._1());
        //btnSearchAvailableSlotsRequest.setText(ScheduleViewActionCaption.SEARCH_AVAILABLE_SLOTS._1());
        btnSelectTreatmentRequest.setText(Captions.ScheduleView.SELECT_TREATMENT._1());
        
        
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        
        btnCancelSelectedAppointment.setActionCommand(Action.REQUEST_CANCEL_APPOINTMENT.toString());
        btnClinicalNotesForSelectedAppointment.setActionCommand(Action.REQUEST_CLINICAL_NOTE_VIEW.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCreateUpdateAppointment.setActionCommand(Action.REQUEST_CREATE_UPDATE_APPOINTMENT.toString());
        btnMakeDeleteEmergencyAppointmentUndoSelection.setActionCommand(Action.REQUEST_MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO_SELECTION.toString());
        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setActionCommand(Action.REQUEST_MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT.toString());
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
        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.addActionListener(this);
        btnNextDay.addActionListener(this);
        btnNow.addActionListener(this);
        btnPreviousDay.addActionListener(this);
        //btnSearchAvailableSlotsRequest.addActionListener(this);
        btnSelectTreatmentRequest.addActionListener(this); 
        
        configureScheduleListView();
        
        setScheduleViewMode(ScheduleViewMode.LIST);
        
        
        dayDatePicker.addDateChangeListener(this);
        
        /**
         * Check to see if user login enabled
         * -- yes; send request to controller to fetch settings from store
         * -- no; fetch default settings for titled border settings
         */

/*        ActionEvent actionEvent = null;
        if ((Boolean)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_REQUIRED)){
            doActionEventFor(ScheduleViewController.Actions.USER_SETTINGS_LIST_SETTINGS_REQUEST);
            doActionEventFor(ScheduleViewController.Actions.USER_SYSTEM_WIDE_SETTINGS_REQUEST);
        }
        setScheduleTitledBorderSettings();*/

        this.vetoPolicy = new AppointmentDateVetoPolicy((HashMap<DayOfWeek,Boolean>)getMyController().getDescriptor().
                getControllerDescription().getProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT));
        DatePickerSettings dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
        dps.setHighlightPolicy(this);
     
        LocalDate day = (LocalDate)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.SCHEDULE_DAY);
        if (!vetoPolicy.isDateAllowed(day)){
            /*switch(getMyController().
                    getDescriptor().
                    getControllerDescription().
                    getViewMode()){*/
            switch(getMyController().
                    getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.VIEW_MODE)){
                case SCHEDULE_REQUESTED_FROM_DESKTOP_VIEW ->{
                    day = this.vetoPolicy.getNextAvailableDateTo(day);
                    break;
                }
                case SCHEDULE_REFERENCED_FROM_PATIENT_VIEW ->{
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
                    break;
                }
                default ->{
                    break;
                }
            }
        }
        dayDatePicker.setDate(day);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dayDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        
        //configureScheduleListView(0);
        
        
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, 
                getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.SCHEDULE_DAY));
        
        refreshAppointmentTableWithCurrentlySelectedDate();
        /*
        // Add a component listener to adjust column widths after it is displayed
        this.tblAppointments.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustColumnWidthsAndViewPosition(tblAppointments);
            }
        });*/
    }
    /*
    private void adjustColumnWidthsAndViewPosition(JTable table){
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewController.setRelativeColumnWidths(table, new double[]{0.1,0.2,0.05,0.05,0.15,0.45});
            ViewController.centerInternalFrame(getDesktopView().getDeskTop(), this);
        });
    }*/
    
    /**
     * method called on receipt of USER_SYSTEM_WIDE_SETTINGS_RECEIVED property change event 
     */
    private void setScheduleTitledBorderSettings(){
        setBorderTitles(BorderTitles.ACTION);
        setBorderTitles(BorderTitles.SCHEDULE_DATE_SELECTION);
        setBorderTitles(BorderTitles.SCHEDULE_FOR_DAY);
        setBorderTitles(BorderTitles.SLOT_SCANNER);
        //setBorderTitles(BorderTitles.VIEW_FORMAT);
    }
    
    /**
     * method initialises the schedule diary settings using the DEscriptor based valkues
     */
    private void setScheduleSettings(){
        for( var setting : SystemDefinition.UserScheduleListSettings.values()){
            SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(setting.toString());
            switch(property){
                case LIST_BOOKABLE_SLOT_BACKGROUND ->{
                    break;
                }
                case LIST_BOOKABLE_SLOT_FOREGROUND ->{
                    break;
                }
                case LIST_EMERGENCY_BOOKING_BACKGROUND ->{
                    break;
                }
                case LIST_EMERGENCY_BOOKING_FOREGROUND ->{
                    break;
                }
                case LIST_UNBOOKABLE_SLOT_BACKGROUND ->{
                    break;
                }
                case LIST_UNBOOKABLE_SLOT_FOREGROUND ->{
                    break;
                }
            }
        }
    }
    
    private void setBorderTitles(BorderTitles borderTitles){
        javax.swing.JPanel panel = null;
        String caption = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case ACTION ->{
                panel = pnlScheduleOperations;
                caption = "Actions";
                isPanelBackgroundDefault = false;
                break;
            }
            case SCHEDULE_DATE_SELECTION ->{
                panel = pnlAppointmentDaySelector;
                caption = "Schedule date selection";
                isPanelBackgroundDefault = true;
                break;
            }
            case SCHEDULE_FOR_DAY ->{
                panel = pnlScheduleForDay;
                caption = "Appointments schedule for day";
                isPanelBackgroundDefault = true;
                break;
            }
            case SLOT_SCANNER ->{
                panel = pnlSlotScanner;
                caption = "Slot scanner";
                isPanelBackgroundDefault = true;
                break;
            }
            case VIEW_FORMAT ->{
                panel = pnlView;
                caption = "View format";
                isPanelBackgroundDefault = true;
                break;
            }
        }
        if (panel!=null){
            panel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createEtchedBorder(), 
                    caption, 
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                    (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                    (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));
            if (!isPanelBackgroundDefault)
                panel.setBackground(new java.awt.Color(220, 220, 220));
        }else{
            String message = "Unexpected null value for titled border panel encountered in ScheduleListView::setBorderTitles() method";
            JOptionPane.showMessageDialog(this, message, "View error", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    enum BorderTitles{
        ACTION,
        SCHEDULE_FOR_DAY,
        SCHEDULE_DATE_SELECTION,
        SLOT_SCANNER,
        VIEW_FORMAT   
    }
    
    private void doCreateAppointmentAction() {  
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1) {
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_UNSELECTED);
        }else{
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_SELECTED);
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, model.getElementAt(row));
        }
        doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_CREATE_VIEW_REQUEST);
    }  
    
    private void makeEmergencyAppointment(){
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, model.getElementAt(row));
            doActionEventFor(ScheduleViewController.Actions.SCHEDULE_EDITOR_MAKE_EMERGENCY_APPOINTMENT_REQUEST);
            this.disableAllScheduleOperationControls();
        }
        else{
            JOptionPane.showInternalMessageDialog(this,
                    "An appointment slot has not been selected;\n make emergency appointment action aborted", 
                    "View error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deleteEmergencyAppointment(){
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, model.getElementAt(row));
            doActionEventFor(ScheduleViewController.Actions.SCHEDULE_EDITOR_DELETE_EMERGENCY_APPOINTMENT_REQUEST);
            this.disableAllScheduleOperationControls();
        }
        else{
            JOptionPane.showInternalMessageDialog(this,
                    "An appointment slot has not been selected;\n delete emergency appointment action aborted",  
                    "View error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doUpdateAppointmentActionForDiary(){
        /**
         * on entry view descriptor points to selected appointment in diary
         */
        doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_UPDATE_VIEW_REQUEST);
    }
    
    private void doUpdateAppointmentAction() {
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
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
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, model.getElementAt(row));
            doActionEventFor(ScheduleViewController.Actions.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST);
        }
        else{
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, model.getElementAt(row));
            doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_UPDATE_VIEW_REQUEST);
        }
    }                                                    

    private void doCloseViewAction() {                                             

        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException e){
            
        }
        
    }                                            

    private void doScheduleDiaryActionRequest(ScheduleDiaryAction request){
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
            case CANCEL_APPOINTMENT:
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
                //appointment.setPatient(patient);
                appointment.setStart(start);
                appointment.setDuration(duration);
                setAppointmentFromDiaryWithUpdates(appointment);
                setAppointmentMode(AppointmentMode.CREATE);
                this.setAppointmentFromDiaryWithUpdates(appointment);
                //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
                break;
            case EXTEND_APPOINTMENT_DOWN:
                doExtendAppointmentDown();
                //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
                break;
            case EXTEND_APPOINTMENT_UP:
                doExtendAppointmentUp();
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
                //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
                break;
            case EXTEND_APPOINTMENT_UP_AND_DOWN:
                slot = getFirstSelectedSlot();
                start = slot.getStart();
                slot = getLastSelectedSlot();
                end = slot.getStart().plusMinutes(5);
                duration = Duration.between(start, end);
                for(int row : getSelection()){
                    slot = getScheduleDiaryTableModel().getElementAt(row);
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
                    //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
                }else JOptionPane.showInternalMessageDialog(
                        this, "selected appointment is null!", "View error", JOptionPane.WARNING_MESSAGE);
                break;
            case SHIFT_APPOINTMENT_DOWN:
                doShiftAppointmentDown();
                /**
                 * -- locate last row of appt
                 * -- from this and last selected row calculate minutes appt shifted
                 * -- add these minutes to appt start
                 */
                
            case SHIFT_APPOINTMENT_UP:
                doShiftAppointmentUp();
                /**
                 * locate start of appt
                 * calculate minutes difference with first selected row
                 * adjust appt start time to start at earlier time
                 */
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
                //action = Action.REQUEST_CREATE_UPDATE_APPOINTMENT;
                break;
            case NONE:
                action = Action.NONE;
                break;
        }
        /*
        actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                action.toString());
        this.actionPerformed(actionEvent);
        */
    }

    private void btnScanForEmptySlotsActionPerformed() {                                                     
        // TODO add your handling code here:
        LocalDate searchStartDate = dayDatePicker.getDate();
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, searchStartDate);
        doActionEventFor(ScheduleViewController.Actions.EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST);
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
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, searchStartDate);
        doActionEventFor(ScheduleViewController.Actions.EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST);
    }     
    
    private void doCancelSelectedAppointmentAction() {                                                             
        //DateTimeFormatter format24Hour = DateTimeFormatter.ofPattern("HH:mm");
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
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
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, model.getElementAt(row));
        start = ((Appointment)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT)).getStart();
        from = start.toLocalTime();
        //20/07/2022 08:16 update
        //duration = getViewDescriptor().getViewDescription().getAppointment().getData().getDuration().toMinutes();
        duration = ((Appointment)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT)).getDuration().toMinutes();
        LocalTime to = from.plusMinutes(duration);

        String message;
        if (((Appointment)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.APPOINTMENT)).getPatient().toString().
                equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            message = "Are you sure you want to cancel the unbookable appointment slot";
        }
        else {
            name = ((Appointment)getMyController().getDescriptor().getViewDescription().
                    getProperty(SystemDefinition.Properties.APPOINTMENT)).getPatient().getName().getForenames();
            if (!name.isEmpty())name = name + " ";
            name = name + ((Appointment)getMyController().getDescriptor().getViewDescription().
                    getProperty(SystemDefinition.Properties.APPOINTMENT)).getPatient().getName().getSurname();
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
            doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_CANCEL_REQUEST);
        }
    }
    
    private void btnMarkCancelUnbookableSlotOrMoveToAnotherDayOrFetchPatientActionPerformed() {   
        Patient patient = null;
        switch(getUnbookableSlotMode()){
            case CANCEL:
                doUnbookableSlotInCancelMode();
                break;
            case FETCH_PATIENT:
                //JOptionPane.showMessageDialog(this, "Fetch patient funtion not yet implemented");
                int selectedRow = tblAppointments.getSelectedRow();
                if (selectedRow!=-1){
                    ScheduleListTableModel model = 
                            (ScheduleListTableModel)tblAppointments.getModel();
                    Appointment appointment = model.getElementAt(selectedRow);
                    patient = appointment.getPatient();
                    if (patient!=null){
                        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                    }
                    doActionEventFor(ScheduleViewController.Actions.PATIENT_VIEW_REQUEST);
                }
                break;
            case MARK:
                doUnbookableSlotInMarkMode();
                break;
            case MOVE:
                doMoveBookingToAnotherDay();
                break;
        }
    }
    
    private void mniSurgeryDaysEditorActionPerformed(){
        doActionEventFor(ScheduleViewController.Actions.SURGERY_DAYS_EDITOR_VIEW_REQUEST);
    }

    private void mniSelectNonSurgeryDayActionPerformed(){
        doActionEventFor(ScheduleViewController.Actions.NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST);
    }
    
    private void doCancelledAppointmentsViewAction() {  
        doActionEventFor(ScheduleViewController.Actions.APPOINTMENTS_CANCELLED_VIEW_REQUEST);
    }
    
    private void doUnbookableSlotInCancelMode(){
        doActionEventFor(ScheduleViewController.Actions.APPOINTMENT_CANCEL_REQUEST);
        setUnbookableSlotOrMoveOrFetchPatientMode(UnbookableSlotOrMoveOrFetchPatientMode.MARK);
    }
    
    private void doUnbookableSlotInMarkMode(){
        Appointment appointment = null;
        int row = this.tblAppointments.getSelectedRow();
        if (row != -1){
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_SELECTED);
            appointment = ((ScheduleListTableModel)this.tblAppointments.getModel()).getElementAt(row);
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT, appointment); 
        }
        else {
            getMyController().getDescriptor().getViewDescription().
                    setViewMode(ViewController.ViewMode.SLOT_UNSELECTED);
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.APPOINTMENT,null);
        }
        doActionEventFor(ScheduleViewController.Actions.UNBOOKABLE_APPOINTMENT_SLOT_EDITOR_VIEW_REQUEST);
    }
    
    private void doMoveBookingToAnotherDay(){
        
    }
    
    private void doColourPickerRequest(){
        Color initialColor = Color.RED;
        Color selectedColor = JColorChooser.showDialog(this, "Select a Color", initialColor);
        //if (selectedColor != null) {
            //button.setBackground(selectedColor);
        //}
    }
    
    private void doColourPickerRequest2(){
        Color initialColor = Color.WHITE;
        Color selectedColor = ColorPickerDialog.showDialog(null, initialColor, true);
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
                    .setProperty(SystemDefinition.Properties.APPOINTMENT,appointment);
            doActionEventFor(ScheduleViewController.Actions.CLINICAL_NOTE_VIEW_REQUEST);
        }
    }
    
    private void doClinicNoteRequest(){
        Appointment appointment = (Appointment)getMyController().getDescriptor()
                .getViewDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        if (appointment!=null){
            if(!appointment.getIsKeyDefined()){
                appointment  = null;
            }else if(appointment.getIsUnbookableSlot()){
                appointment = null;
            }
        }
        if (appointment!=null){
            getMyController().getDescriptor().getViewDescription()
                    .setProperty(SystemDefinition.Properties.APPOINTMENT,appointment);
            doActionEventFor(ScheduleViewController.Actions.CLINICAL_NOTE_VIEW_REQUEST);
        }
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
        TitledBorder titledBorder = (TitledBorder)pnlScheduleForDay.getBorder();
        titledBorder.setTitle(tableTitle);
        pnlScheduleForDay.repaint();
    }
    
    private void refreshAppointmentTableWithCurrentlySelectedDate(){
        doActionEventFor(ScheduleViewController.Actions.APPOINTMENTS_FOR_DAY_REQUEST);
        setTitle(((LocalDate)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.SCHEDULE_DAY)).format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "Schedule");
        setIsViewInitialised(true);  
        setTitle(((LocalDate)getMyController().getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.SCHEDULE_DAY)).format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "Schedule");
        setIsViewInitialised(true);  
    }
    
    private void disableAllScheduleOperationControls(){
        this.btnCancelSelectedAppointment.setEnabled(false);
        this.btnClinicalNotesForSelectedAppointment.setEnabled(false);
        //this.btnCloseView.setEnabled(false);
        this.btnCreateUpdateAppointment.setEnabled(false);
        this.btnMakeDeleteEmergencyAppointmentUndoSelection.setEnabled(false);
        this.btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setEnabled(false);
        this.btnSelectTreatmentRequest.setEnabled(false);
        //this.tblAppointments.clearSelection();
    }
    
    private void populateScheduleListView(){
        ScheduleListTableModel model = 
                (ScheduleListTableModel)tblAppointments.getModel();
        model.removeAllElements();
        ArrayList<Appointment> schedule = makeEmergencyAppointmentsFirst(
                (ArrayList<Appointment>)getMyController().getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS_FOR_DAY_IN_LIST_FORMAT));
        model.setData(schedule);
        
        doScheduleTitleRefresh(null);
        setTitle(((LocalDate)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.SCHEDULE_DAY)).
                format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
        doActionEventFor(ScheduleViewController.Actions.VIEW_CHANGED_NOTIFICATION);
        ViewController.setJTableColumnProperties(tblAppointments, scrAppointmentsForDayTable.getPreferredSize().width, 10,20,5,5,15,45);
    }
    
    private JTable tblAppointments = null;
    private void configureScheduleListView(){
        tblAppointments = new JTable(new ScheduleListTableModel());
        scrAppointmentsForDayTable.setViewportView(tblAppointments);
        ScheduleListTableModel model = (ScheduleListTableModel)tblAppointments.getModel();
        model.addTableModelListener(this);
        setAppointmentTableListener();
        
        this.tblAppointments.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        this.tblAppointments.setDefaultRenderer(LocalTime.class, new TableLocalTime24HourFormatCentredRenderer());
        this.tblAppointments.setDefaultRenderer(Patient.class, new ScheduleListTablePatientRenderer(this));
        JTableHeader tableHeader = this.tblAppointments.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true); 
        ViewController.setJTableColumnProperties(tblAppointments, scrAppointmentsForDayTable.getPreferredSize().width, 10,20,5,5,15,45);
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
        this.vetoPolicy = new AppointmentDateVetoPolicy((HashMap<DayOfWeek,Boolean>)getMyController().getDescriptor().
                getControllerDescription().getProperty(SystemDefinition.Properties.SURGERY_DAYS_ASSIGNMENT));
        dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
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
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlScheduleDaySelection = new javax.swing.JPanel();
        pnlAppointmentDaySelector = new javax.swing.JPanel();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        btnNextDay = new javax.swing.JButton();
        btnPreviousDay = new javax.swing.JButton();
        btnNow = new javax.swing.JButton();
        pnlSlotScanner = new javax.swing.JPanel();
        btnSelectBookableSlotsScanner = new javax.swing.JButton();
        btnSelectUnbookableSlotsScanner = new javax.swing.JButton();
        pnlScheduleOperations = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnMakeDeleteEmergencyAppointmentUndoSelection = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails = new javax.swing.JButton();
        btnClinicalNotesForSelectedAppointment = new javax.swing.JButton();
        btnSelectTreatmentRequest = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        pnlScheduleForDay = new javax.swing.JPanel();
        scrAppointmentsForDayTable = new javax.swing.JScrollPane();
        pnlView = new javax.swing.JPanel();
        btnRequestToDoLIst = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuOptions = new javax.swing.JMenu();
        mniPrintSchedule = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniEarlyBookingStartTime = new javax.swing.JMenuItem();
        mniLateBookingEndTime = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlAppointmentDaySelector.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Schedule date selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pnlAppointmentDaySelector.setPreferredSize(new java.awt.Dimension(449, 176));

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
            .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(btnPreviousDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(btnNow, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        pnlAppointmentDaySelectorLayout.setVerticalGroup(
            pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentDaySelectorLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(pnlAppointmentDaySelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPreviousDay)
                    .addComponent(btnNow)
                    .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );

        pnlSlotScanner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Slot scanner", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

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
                .addGap(67, 67, 67)
                .addGroup(pnlSlotScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSelectUnbookableSlotsScanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSelectBookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        pnlSlotScannerLayout.setVerticalGroup(
            pnlSlotScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSlotScannerLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(btnSelectBookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSelectUnbookableSlotsScanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        pnlScheduleOperations.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Action", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        btnCreateUpdateAppointment.setText(Captions.ScheduleView.CREATE_UPDATE_APPOINTMENT._1());
        btnCreateUpdateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateUpdateAppointmentActionPerformed(evt);
            }
        });

        btnMakeDeleteEmergencyAppointmentUndoSelection.setText(Captions.ScheduleView.MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO._1());

        btnCancelSelectedAppointment.setText(Captions.ScheduleView.CANCEL_APPOINTMENT._1());

        btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails.setText("");

        btnClinicalNotesForSelectedAppointment.setText(Captions.ScheduleView.CLINICAL_NOTES._1());
        btnClinicalNotesForSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClinicalNotesForSelectedAppointmentActionPerformed(evt);
            }
        });

        btnSelectTreatmentRequest.setText(Captions.ScheduleView.SELECT_TREATMENT._1());
        btnSelectTreatmentRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectTreatmentRequestActionPerformed(evt);
            }
        });

        btnCloseView.setText(Captions.CLOSE_VIEW);

        javax.swing.GroupLayout pnlScheduleOperationsLayout = new javax.swing.GroupLayout(pnlScheduleOperations);
        pnlScheduleOperations.setLayout(pnlScheduleOperationsLayout);
        pnlScheduleOperationsLayout.setHorizontalGroup(
            pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMakeDeleteEmergencyAppointmentUndoSelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(10, 10, 10))
        );
        pnlScheduleOperationsLayout.setVerticalGroup(
            pnlScheduleOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleOperationsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnMakeDeleteEmergencyAppointmentUndoSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        pnlScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder("Appointment schedule for"));

        scrAppointmentsForDayTable.setMaximumSize(new java.awt.Dimension(849, 371));
        scrAppointmentsForDayTable.setName(""); // NOI18N
        scrAppointmentsForDayTable.setPreferredSize(new java.awt.Dimension(849, 371));

        javax.swing.GroupLayout pnlScheduleForDayLayout = new javax.swing.GroupLayout(pnlScheduleForDay);
        pnlScheduleForDay.setLayout(pnlScheduleForDayLayout);
        pnlScheduleForDayLayout.setHorizontalGroup(
            pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.PREFERRED_SIZE, 849, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlScheduleForDayLayout.setVerticalGroup(
            pnlScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlView.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnRequestToDoLIst.setText("<html><center>Show</center><center>'To Do'</center><center>list</center></html>");

        javax.swing.GroupLayout pnlViewLayout = new javax.swing.GroupLayout(pnlView);
        pnlView.setLayout(pnlViewLayout);
        pnlViewLayout.setHorizontalGroup(
            pnlViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRequestToDoLIst, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlViewLayout.setVerticalGroup(
            pnlViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlViewLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnRequestToDoLIst, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout pnlScheduleDaySelectionLayout = new javax.swing.GroupLayout(pnlScheduleDaySelection);
        pnlScheduleDaySelection.setLayout(pnlScheduleDaySelectionLayout);
        pnlScheduleDaySelectionLayout.setHorizontalGroup(
            pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                        .addComponent(pnlView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlSlotScanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlScheduleForDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlScheduleDaySelectionLayout.setVerticalGroup(
            pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlScheduleOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                        .addGroup(pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlSlotScanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlScheduleDaySelectionLayout.createSequentialGroup()
                                .addGroup(pnlScheduleDaySelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pnlAppointmentDaySelector, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pnlView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addComponent(pnlScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3))
        );

        mnuOptions.setText("Actions");

        mniPrintSchedule.setText("Print schedule");
        mnuOptions.add(mniPrintSchedule);
        mnuOptions.add(jSeparator1);

        mniEarlyBookingStartTime.setText("Define early start time");
        mnuOptions.add(mniEarlyBookingStartTime);

        mniLateBookingEndTime.setText("Define late end Time");
        mnuOptions.add(mniLateBookingEndTime);
        mnuOptions.add(jSeparator2);

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
                .addComponent(pnlScheduleDaySelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlScheduleDaySelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClinicalNotesForSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClinicalNotesForSelectedAppointmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnClinicalNotesForSelectedAppointmentActionPerformed

    private void btnSelectTreatmentRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectTreatmentRequestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSelectTreatmentRequestActionPerformed

    private void btnCreateUpdateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateUpdateAppointmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateUpdateAppointmentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelSelectedAppointment;
    private javax.swing.JButton btnClinicalNotesForSelectedAppointment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateAppointment;
    private javax.swing.JButton btnMakeDeleteEmergencyAppointmentUndoSelection;
    private javax.swing.JButton btnMarkCancelUnbookableSlotOrMoveBookingToAnotherDayOrFetchPatientDetails;
    private javax.swing.JButton btnNextDay;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnPreviousDay;
    private javax.swing.JButton btnRequestToDoLIst;
    private javax.swing.JButton btnSelectBookableSlotsScanner;
    private javax.swing.JButton btnSelectTreatmentRequest;
    private javax.swing.JButton btnSelectUnbookableSlotsScanner;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniEarlyBookingStartTime;
    private javax.swing.JMenuItem mniLateBookingEndTime;
    private javax.swing.JMenuItem mniPrintSchedule;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlAppointmentDaySelector;
    private javax.swing.JPanel pnlScheduleDaySelection;
    private javax.swing.JPanel pnlScheduleForDay;
    private javax.swing.JPanel pnlScheduleOperations;
    private javax.swing.JPanel pnlSlotScanner;
    private javax.swing.JPanel pnlView;
    private javax.swing.JScrollPane scrAppointmentsForDayTable;
    // End of variables declaration//GEN-END:variables
}
