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
import model.entity.Appointment;
import model.entity.Patient;
/*28/03/2024import model.PatientNote;*/
import controller.ViewController;
import controller.DesktopViewController;
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
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JColorChooser;
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
    public void actionPerformed(ActionEvent e){
        
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

    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDiary = new javax.swing.JPanel();
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
        btnNextDay1 = new javax.swing.JButton();
        btnNextDay2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnCreateUpdateAppointment = new javax.swing.JButton();
        btnMakeDeleteEmergencyAppointmentUndoSelection = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay = new javax.swing.JButton();
        btnClinicalNotesForSelectedAppointment = new javax.swing.JButton();
        btnSelectTreatmentRequest = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        pnlDiary.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule details"));

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

        javax.swing.GroupLayout pnlDiaryLayout = new javax.swing.GroupLayout(pnlDiary);
        pnlDiary.setLayout(pnlDiaryLayout);
        pnlDiaryLayout.setHorizontalGroup(
            pnlDiaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiaryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrMorningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrAfternoonTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrEveningTable, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDiaryLayout.setVerticalGroup(
            pnlDiaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiaryLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlDiaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(scrMorningTable, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .addComponent(scrAfternoonTable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .addComponent(scrEveningTable, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(125, 125, 125)
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(btnPreviousDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                .addComponent(btnNow, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(121, 121, 121)
                .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPreviousDay)
                    .addComponent(btnNow)
                    .addComponent(btnNextDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Slot scanner"));

        btnNextDay1.setText("Bookable slots");
        btnNextDay1.setMinimumSize(new java.awt.Dimension(60, 23));
        btnNextDay1.setPreferredSize(new java.awt.Dimension(62, 23));

        btnNextDay2.setText("Unbookable slots");
        btnNextDay2.setMinimumSize(new java.awt.Dimension(60, 23));
        btnNextDay2.setPreferredSize(new java.awt.Dimension(62, 23));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(btnNextDay1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(btnNextDay2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextDay1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNextDay2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
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
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMakeDeleteEmergencyAppointmentUndoSelection, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(btnCreateUpdateAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMakeDeleteEmergencyAppointmentUndoSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMarkCancelSlotUnbookableOrMoveBookingToAnotherDay, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClinicalNotesForSelectedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSelectTreatmentRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDiary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(pnlDiary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
    private javax.swing.JButton btnNextDay1;
    private javax.swing.JButton btnNextDay2;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnPreviousDay;
    private javax.swing.JButton btnSelectTreatmentRequest;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel pnlDiary;
    private javax.swing.JScrollPane scrAfternoonTable;
    private javax.swing.JScrollPane scrEveningTable;
    private javax.swing.JScrollPane scrMorningTable;
    private javax.swing.JTable tblScheduleAfternoon;
    private javax.swing.JTable tblScheduleEvening;
    private javax.swing.JTable tblScheduleMorning;
    // End of variables declaration//GEN-END:variables
}
