/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views;

import view.views.view_support_classes.renderers.AppointmentsTableDurationRenderer;
import view.views.view_support_classes.renderers.AppointmentsTableLocalDateTimeRenderer;
import view.views.view_support_classes.renderers.AppointmentsTablePatientRenderer;
import view.views.view_support_classes.models.Appointments5ColumnTableModel;
import model.Appointment;
import model.Patient;
import controller.ViewController;
import view.views.view_support_classes.AppointmentDateVetoPolicy;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;
import view.views.view_support_classes.models.EmptySlotAvailability2ColumnTableModel;
import controller.Descriptor;
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
import java.awt.Point;
import java.awt.print.PrinterJob;
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
import javax.swing.table.TableModel;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.SwingUtilities;



/**
 *
 * @author colin
 */
public class AppointmentScheduleView extends View{
    private enum COLUMN{From,Duration,ThePatient,Notes};
    private Appointments5ColumnTableModel tableModel = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private DatePickerSettings settings = null;
    private ArrayList<Appointment> appointments = null;
    //private final DateTimeFormatter emptySlotStartFormat = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm (EEE)");
    private final DateTimeFormatter appointmentScheduleFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy ");
    private AppointmentDateVetoPolicy vetoPolicy = null;
    private boolean displayAppointeeReminderCount = true;
    
    private boolean getDisplayAppointeeReminderCount(){
        return displayAppointeeReminderCount;
    }
    
    private void setDisplayAppointeeReminderCount(boolean value){
        displayAppointeeReminderCount = value;
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
        TitledBorder titledBorder = (TitledBorder)this.SlotAvailabilityPanel.getBorder();
        ViewController.AppointmentScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.AppointmentScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        setViewDescriptor((Descriptor)e.getNewValue());
        switch (propertyName){
            case APPOINTMENTS_FOR_DAY_RECEIVED:
                populateAppointmentsForDayTable();
                break;
            case SURGERY_DAYS_ASSIGNMENT_RECEIVED:
                updateDatePickerSettings();
                break;
            case NON_SURGERY_DAY_EDIT_RECEIVED:
                temporarilySuspendDatePickerDateVetoPolicy(getViewDescriptor().getViewDescription().getDay());
                break;
            case NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED://instruction to clear list
                titledBorder.setTitle("Available appointment slots");
                populateEmptySlotAvailabilityTable(new ArrayList<>());
                break;
            case APPOINTMENT_SLOTS_FROM_DAY_RECEIVED:
                tableTitleDuration = new AppointmentsTableDurationRenderer().renderDuration(
                        getViewDescriptor().getControllerDescription().
                                getEmptySlotMinimumDuration());
                tableTitleDay = getViewDescriptor().getControllerDescription().
                                getEmptySlotFromDay().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        //getAppointment().getStart().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                titledBorder = (TitledBorder)this.SlotAvailabilityPanel.getBorder();
                titledBorder.setTitle("Slot availability from " + tableTitleDay 
                        + " for a duration of " + tableTitleDuration);
                populateEmptySlotAvailabilityTable(getViewDescriptor().getControllerDescription().getAppointmentSlots());
                /**
                 * without the next lines the appointments table is unconscious of being selected?!)
                 */
                getViewDescriptor().getViewDescription().setDay(dayDatePicker.getDate());
                //refreshAppointmentTableWithCurrentlySelectedDate();
                break;
            case APPOINTMENT_SCHEDULE_ERROR_RECEIVED:
                populateEmptySlotAvailabilityTable(getViewDescriptor().getControllerDescription().getAppointments());
                break;
                
        }
    }
    
    private void updateDatePickerSettings(){
        DatePickerSettings dps = null;
        this.vetoPolicy = new AppointmentDateVetoPolicy(getViewDescriptor().getViewDescription().getSurgeryDaysAssignmentValue());
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
        TitledBorder titledBorder = (TitledBorder)this.SlotAvailabilityPanel.getBorder();
        titledBorder.setTitle("Slot availability (appointment yet to be defined)");
        //following action invokes call to controller via DateChange\Listener
        this.vetoPolicy = new AppointmentDateVetoPolicy(getViewDescriptor().getControllerDescription().getSurgeryDaysAssignment());
        DatePickerSettings dps = dayDatePicker.getSettings();
        dps.setVetoPolicy(vetoPolicy);
        dps.setHighlightPolicy(new LeaveEditor());
       
        /**
         * -- Valid date?
         * ---- yes: 
         * ------> proceed as normal
         * ---- no: 
         * ------> temporarily make all days surgery days
         * ----  
         */
        //LocalDate day = vetoPolicy.getNowDateOrClosestAvailableAfterNow();
        LocalDate day = getViewDescriptor().getViewDescription().getDay();
        if (!vetoPolicy.isDateAllowed(day)){
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
        dayDatePicker.setDate(day);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dayDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        
        refreshAppointmentTableWithCurrentlySelectedDate();
    }  
    
    /**
     * adds a suffix to the title border of panel which contains list of appointments for selected day
     * -- suffix of the format "X patients of Y reminded about appointments"
     */
    private void doDisplayAppointeeReminderCount(){
        Point appointeeReminderCount = getViewDescriptor().getControllerDescription().getAppointeeRemindersCountForDay();
        TitledBorder titledBorder = (TitledBorder)this.pnlAppointmentScheduleForDay.getBorder();
        titledBorder.setTitle("Appointment schedule for " 
                + dayDatePicker.getDate().format(appointmentScheduleFormat)
                + " (" + appointeeReminderCount.x + " patients reminded of appointment from " 
                + appointeeReminderCount.y + ")");
        
    }
    
    /**
     * The abstract table model includes a getElementAt(int row) method which returns the element at the row defined index of an ArrayList<Descriptor.Appointment> maintained in the table model
     * @param row selected row in table
     * update 30/07/2021 19:53
     */
    private void initialiseEDRequestFromView(int row){
        getViewDescriptor().getViewDescription().setAppointment(tableModel.getElementAt(row));    
    }

    /**
     * 
     * @param myViewType
     * @param controller
     * @param ed 
     */
    public AppointmentScheduleView(View.Viewer myViewType, ActionListener controller, Descriptor ed) {
        super("Appointment schedule");
        this.setMyViewType(myViewType);
        setMyController(controller);
        this.setViewDescriptor(ed);
        initComponents();
        dayDatePicker.addDateChangeListener(new DayDatePickerChangeListener());
        setEmptySlotAvailabilityTableListener();
        setAppointmentTableListener();
        mniScheduleContactList.addActionListener((ActionEvent e) -> mniScheduleContactListActionPerformed(e));
        //this.mniPrintAppointmentDayList.addActionListener((ActionEvent e) -> mniPrintAppointmentDayListActionPerformed(e));
        this.mniCloseView.addActionListener((ActionEvent e) -> mniCloseViewActionPerformed(e));
        this.mniSelectNonSurgeryDay.addActionListener((ActionEvent e) -> mniSelectNonSurgeryDayActionPerformed(e)); 
        //this.dayDatePicker.setDate(LocalDate.of(2022,9,7));
        //this.dayDatePicker.getSettings().setHighlightPolicy(new LeaveEditor());
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
                    ViewController.AppointmentScheduleViewControllerActionEvent.SURGERY_DAYS_EDITOR_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void mniScheduleContactListActionPerformed(ActionEvent e){
        LocalDate day = this.dayDatePicker.getDate();
        getViewDescriptor().getViewDescription().setDay(day);
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENT_REMINDERS_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void mniSelectNonSurgeryDayActionPerformed(ActionEvent e){
        ActionEvent actionEvent = new ActionEvent(this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.AppointmentScheduleViewControllerActionEvent.NON_SURGERY_DAY_SCHEDULE_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    private void setEmptySlotAvailabilityTableListener(){
        this.tblEmptySlotAvailability.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblEmptySlotAvailability.getSelectionModel();
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
    
    private void setAppointmentTableListener(){
        this.tblAppointments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblAppointments.getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                /*
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    doEmptySlotAvailabilityTableRowSelection(selectedRow);
                }
                */
            }
        });
    }
    private void refreshAppointmentTableWithCurrentlySelectedDate(){
        ActionEvent actionEvent = new ActionEvent(AppointmentScheduleView.this, 
                ActionEvent.ACTION_PERFORMED,
                ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
        AppointmentScheduleView.this.getMyController().actionPerformed(actionEvent);
        SwingUtilities.invokeLater(new Runnable() 
        {
          public void run()
          {
            AppointmentScheduleView.this.setTitle(AppointmentScheduleView.this.getViewDescriptor().getViewDescription().getDay().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
            setIsViewInitialised(true);       
          }
        });
    }
    
    private void doEmptySlotAvailabilityTableRowSelection(int row){
        Appointment appointment = 
                ((EmptySlotAvailability2ColumnTableModel)this.tblEmptySlotAvailability.getModel()).getElementAt(row);
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
        doDisplayAppointmentTableForDayTitle();
        configureAppointmentsForDayTable();
        setTitle(getViewDescriptor().getControllerDescription().getAppointmentScheduleDay().
                format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " Appointment schedule");
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.AppointmentScheduleViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
        
        this.pnlAppointmentScheduleForDay.repaint();
    } 

    @Override
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        AppointmentScheduleView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.AppointmentScheduleViewControllerActionEvent.
                                VIEW_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        AppointmentScheduleView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.AppointmentScheduleViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlAppointmentScheduleForDay = new javax.swing.JPanel();
        scrAppointmentsForDayTable = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnCreateAppointment = new javax.swing.JButton();
        btnUpdateAppointment = new javax.swing.JButton();
        btnCancelSelectedAppointment = new javax.swing.JButton();
        btnCancelSelectedAppointment1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnCloseView = new javax.swing.JButton();
        btnPrintSchedule = new javax.swing.JButton();
        SlotAvailabilityPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmptySlotAvailability = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnScanForEmptySlots = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnNowDay = new javax.swing.JButton();
        btnNextPracticeDay = new javax.swing.JButton();
        btnPreviousPracticeDay = new javax.swing.JButton();
        dayDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuOptions = new javax.swing.JMenu();
        mniScheduleContactList = new javax.swing.JMenuItem();
        mniViewCancelledAppointments = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniSurgeryDaysEditor = new javax.swing.JMenuItem();
        mniSelectNonSurgeryDay = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mniPrintSchedule = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlAppointmentScheduleForDay.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Appointment schedule for ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(

        ));
        scrAppointmentsForDayTable.setViewportView(tblAppointments);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Appointment schedule management", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        btnCreateAppointment.setText("Create");
        btnCreateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAppointmentActionPerformed(evt);
            }
        });

        btnUpdateAppointment.setText("Update");
        btnUpdateAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAppointmentActionPerformed(evt);
            }
        });

        btnCancelSelectedAppointment.setText("Cancel ");
        btnCancelSelectedAppointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedAppointmentActionPerformed(evt);
            }
        });

        btnCancelSelectedAppointment1.setText("Mark slot(s) as unbookable");
        btnCancelSelectedAppointment1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedAppointment1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateAppointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdateAppointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelSelectedAppointment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelSelectedAppointment1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateAppointment)
                    .addComponent(btnUpdateAppointment)
                    .addComponent(btnCancelSelectedAppointment)
                    .addComponent(btnCancelSelectedAppointment1))
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCloseView.setText("Close view");
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        btnPrintSchedule.setText("Print schedule");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(btnCloseView))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(btnPrintSchedule)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnPrintSchedule)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCloseView)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout pnlAppointmentScheduleForDayLayout = new javax.swing.GroupLayout(pnlAppointmentScheduleForDay);
        pnlAppointmentScheduleForDay.setLayout(pnlAppointmentScheduleForDayLayout);
        pnlAppointmentScheduleForDayLayout.setHorizontalGroup(
            pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlAppointmentScheduleForDayLayout.setVerticalGroup(
            pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(scrAppointmentsForDayTable, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(pnlAppointmentScheduleForDayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAppointmentScheduleForDayLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(5, 5, 5))))
        );

        SlotAvailabilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Available empty slots", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        SlotAvailabilityPanel.setPreferredSize(new java.awt.Dimension(266, 146));

        jScrollPane1.setViewportView(tblEmptySlotAvailability);

        javax.swing.GroupLayout SlotAvailabilityPanelLayout = new javax.swing.GroupLayout(SlotAvailabilityPanel);
        SlotAvailabilityPanel.setLayout(SlotAvailabilityPanelLayout);
        SlotAvailabilityPanelLayout.setHorizontalGroup(
            SlotAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SlotAvailabilityPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        SlotAvailabilityPanelLayout.setVerticalGroup(
            SlotAvailabilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SlotAvailabilityPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Appointment day selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        btnScanForEmptySlots.setText("search for empty appointment slots");
        btnScanForEmptySlots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanForEmptySlotsActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setPreferredSize(new java.awt.Dimension(200, 58));

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnNowDay, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPreviousPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNextPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dayDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNextPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPreviousPracticeDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNowDay))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnScanForEmptySlots)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnScanForEmptySlots, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        mnuOptions.setText("Actions");

        mniScheduleContactList.setText("View appointee contact details");
        mniScheduleContactList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniScheduleContactListActionPerformed(evt);
            }
        });
        mnuOptions.add(mniScheduleContactList);

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

        mniPrintSchedule.setText("Print schedule");
        mniPrintSchedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPrintScheduleActionPerformed(evt);
            }
        });
        mnuOptions.add(mniPrintSchedule);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlAppointmentScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SlotAvailabilityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SlotAvailabilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(pnlAppointmentScheduleForDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAppointmentActionPerformed
         ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENT_CREATE_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_btnCreateAppointmentActionPerformed

    private void btnUpdateAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateAppointmentActionPerformed
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        //30/07/2022 09:26
        else if (!((Appointments5ColumnTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient().getIsKeyDefined()){
        //else if (!getViewDescriptor().getAppointments().get(row).getPatient().getIsKeyDefined()){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for update");
        }
        else{
            initialiseEDRequestFromView(row);
            ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENT_UPDATE_VIEW_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_btnUpdateAppointmentActionPerformed

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

    private void btnScanForEmptySlotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanForEmptySlotsActionPerformed
        // TODO add your handling code here:
        LocalDate searchStartDate = dayDatePicker.getDate();
        getViewDescriptor().getViewDescription().setDay(searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.AppointmentScheduleViewControllerActionEvent.
                            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_btnScanForEmptySlotsActionPerformed

    
    private void btnCancelSelectedAppointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelSelectedAppointmentActionPerformed
        //DateTimeFormatter format24Hour = DateTimeFormatter.ofPattern("HH:mm");
        String name = null;
        LocalDateTime start = null;
        LocalTime from = null;
        Long duration;
        int row = this.tblAppointments.getSelectedRow();
        if (row == -1){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
        }
        //30/07/2022 09:26
        else if (!((Appointments5ColumnTableModel)this.tblAppointments.getModel()).getElementAt(row).getPatient().getIsKeyDefined()){
            JOptionPane.showMessageDialog(this, "An appointment has not been selected for cancellation");
        }
        else{
            int OKToCancelAppointment;
            initialiseEDRequestFromView(row);
            start = getViewDescriptor().getViewDescription().getAppointment().getStart();
            from = start.toLocalTime();
            //20/07/2022 08:16 update
            //duration = getViewDescriptor().getViewDescription().getAppointment().getData().getDuration().toMinutes();
            duration = getViewDescriptor().getViewDescription().getAppointment().getDuration().toMinutes();
            LocalTime to = from.plusMinutes(duration);
            /**
             * 09/11/2021 16:53 [1.f]log notes -> update below because getRequest().getPatient() no longer initialised 
             * with appointee details; instead request.appointment.appointee.data is
             */
            //20/07/2022 08:16 update
            //name = getViewDescriptor().getViewDescription().getAppointment().getAppointee().getData().getForenames();
            name = getViewDescriptor().getViewDescription().getAppointment().getPatient().getName().getForenames();
            //name = getViewDescriptor().getViewDescription().getPatient().getData().getForenames();
            if (!name.isEmpty())name = name + " ";
            //20/07/2022 08:16 update
            //name = name + getViewDescriptor().getViewDescription().getAppointment().getAppointee().getData().getSurname();
            name = name + getViewDescriptor().getViewDescription().getAppointment().getPatient().getName().getSurname();
            from.format(DateTimeFormatter.ofPattern("HH:mm"));
            String[] options = {"Yes", "No"};
            OKToCancelAppointment = JOptionPane.showOptionDialog(this,
                            "Are you sure you want to cancel the appointment for patient "
                                    + name + " from " + from.format(DateTimeFormatter.ofPattern("HH:mm")) 
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
                        ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENT_CANCEL_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
    }//GEN-LAST:event_btnCancelSelectedAppointmentActionPerformed

    private void btnNowDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNowDayActionPerformed
        // TODO add your handling code here:
        LocalDate day = this.vetoPolicy.getNowDateOrClosestAvailableAfterNow();
        dayDatePicker.setDate(day);
    }//GEN-LAST:event_btnNowDayActionPerformed
/*
    private void mniSurgeryDaysEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSurgeryDaysEditorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mniSurgeryDaysEditorActionPerformed
*/
/*
    private void mniScheduleContactListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniScheduleContactListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mniScheduleContactListActionPerformed
*/
    private void mniViewCancelledAppointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniViewCancelledAppointmentsActionPerformed
        ActionEvent actionEvent = new ActionEvent(this, 
                        ActionEvent.ACTION_PERFORMED,
                        ViewController.AppointmentScheduleViewControllerActionEvent.
                                APPOINTMENTS_CANCELLED_VIEW_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
    }//GEN-LAST:event_mniViewCancelledAppointmentsActionPerformed

    private void btnCancelSelectedAppointment1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelSelectedAppointment1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelSelectedAppointment1ActionPerformed

    private void mniPrintScheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPrintScheduleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mniPrintScheduleActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SlotAvailabilityPanel;
    private javax.swing.JButton btnCancelSelectedAppointment;
    private javax.swing.JButton btnCancelSelectedAppointment1;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateAppointment;
    private javax.swing.JButton btnNextPracticeDay;
    private javax.swing.JButton btnNowDay;
    private javax.swing.JButton btnPreviousPracticeDay;
    private javax.swing.JButton btnPrintSchedule;
    private javax.swing.JButton btnScanForEmptySlots;
    private javax.swing.JButton btnUpdateAppointment;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.github.lgooddatepicker.components.DatePicker dayDatePicker;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniPrintSchedule;
    private javax.swing.JMenuItem mniScheduleContactList;
    private javax.swing.JMenuItem mniSelectNonSurgeryDay;
    private javax.swing.JMenuItem mniSurgeryDaysEditor;
    private javax.swing.JMenuItem mniViewCancelledAppointments;
    private javax.swing.JMenu mnuOptions;
    private javax.swing.JPanel pnlAppointmentScheduleForDay;
    private javax.swing.JScrollPane scrAppointmentsForDayTable;
    private javax.swing.JTable tblAppointments;
    private javax.swing.JTable tblEmptySlotAvailability;
    // End of variables declaration//GEN-END:variables

    class DayDatePickerChangeListener implements DateChangeListener {
        @Override
        public void dateChanged(DateChangeEvent event) {
            //LocalDate date = event.getNewDate();
            getViewDescriptor().getViewDescription().setDay(AppointmentScheduleView.this.dayDatePicker.getDate());
            ActionEvent actionEvent = new ActionEvent(AppointmentScheduleView.this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.AppointmentScheduleViewControllerActionEvent.APPOINTMENTS_FOR_DAY_REQUEST.toString());
            AppointmentScheduleView.this.getMyController().actionPerformed(actionEvent);
            //getMyController().actionPerformed(actionEvent);
            SwingUtilities.invokeLater(new Runnable() 
            {
              public void run()
              {
                AppointmentScheduleView.this.setTitle(AppointmentScheduleView.this.dayDatePicker.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " schedule");
              }
            });
        }
    }

    private void populateEmptySlotAvailabilityTable(ArrayList<Appointment> a){
        if (a==null) a = new ArrayList<Appointment>();
        EmptySlotAvailability2ColumnTableModel model;
        if (this.tblEmptySlotAvailability!=null){
            jScrollPane1.remove(this.tblEmptySlotAvailability);   
        }
        this.tblEmptySlotAvailability = new JTable(new EmptySlotAvailability2ColumnTableModel());
        jScrollPane1.setViewportView(this.tblEmptySlotAvailability);
        setEmptySlotAvailabilityTableListener();
        model = (EmptySlotAvailability2ColumnTableModel)this.tblEmptySlotAvailability.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = a.iterator();
        while (it.hasNext()){
            ((EmptySlotAvailability2ColumnTableModel)this.tblEmptySlotAvailability.getModel()).addElement(it.next());
        }

        JTableHeader tableHeader = this.tblEmptySlotAvailability.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);
        
        TableColumnModel columnModel = this.tblEmptySlotAvailability.getColumnModel();
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
    }
    
    /**
     * method displays a title for the appointment schedule table for the day
 -- a summary of patient appointment reminders made is added to the title 
 -- the title is only displayed if the following value is not null
 ---- Descriptor::getAppointeeRemindersCountForDay()
 -- the patient appointment reminder total is only included in the title if enabled by 
 ---- AppointmentScheduleView::getViewDescriptor().getAppointeeRemindersCountForDay()
 ---- disabling display of patient appointment reminders is not implemented currently 
     */
    private void doDisplayAppointmentTableForDayTitle(){
        int reminderCount;
        int appointeeCount;
        String patient;
        String has;
        
        String tableTitle = "Appointment schedule for " 
                + dayDatePicker.getDate().format(appointmentScheduleFormat);
        if (getViewDescriptor().getControllerDescription().
                getAppointeeRemindersCountForDay()!=null){
            if (getDisplayAppointeeReminderCount()){
                reminderCount = getViewDescriptor().getControllerDescription().getAppointeeRemindersCountForDay().x;
                appointeeCount = getViewDescriptor().getControllerDescription().getAppointeeRemindersCountForDay().y;
                if (appointeeCount > 0){
                    switch (reminderCount){
                        case 1:
                            patient = "patient";
                            has = "has";
                            break;
                        default:
                            patient = "patients";
                            has = "have";
                    }
                    tableTitle = tableTitle + "       <<" + String.valueOf(reminderCount) + " " + patient + " of " + String.valueOf(appointeeCount) + " " + has + " had an appointment reminder>>";
                }
            }
            TitledBorder titledBorder = (TitledBorder)this.pnlAppointmentScheduleForDay.getBorder();
            titledBorder.setTitle(tableTitle);
        }
        
    }
    
    private void configureAppointmentsForDayTable(){
        if (tableModel == null) tableModel = new Appointments5ColumnTableModel();
        tableModel.removeAllElements();
        Iterator<Appointment> it = getViewDescriptor().getControllerDescription().getAppointmentSlotsForDay().iterator();
        while (it.hasNext()){
            tableModel.addElement(it.next());
        }
        
        this.tblAppointments.setDefaultRenderer(Duration.class, new AppointmentsTableDurationRenderer());
        this.tblAppointments.setDefaultRenderer(LocalDateTime.class, new AppointmentsTableLocalDateTimeRenderer());
        this.tblAppointments.setDefaultRenderer(Patient.class, new AppointmentsTablePatientRenderer());
        this.tblAppointments.setModel(tableModel);
        this.tblAppointments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        TableColumnModel columnModel = this.tblAppointments.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(190);
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setPreferredWidth(60);
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(2).setPreferredWidth(60);
        columnModel.getColumn(2).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(3).setPreferredWidth(105);
        columnModel.getColumn(3).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(4).setMinWidth(283);
        columnModel.getColumn(4).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
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
    
    public static String centreString (int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
    
    public static String leftAlignString(int width, String s){
        return String.format("%-" + width + "s", s);
    }
    
    private void btnPrintScheduleActionPerformed(ActionEvent e){
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
            stringToPrint = getViewDescriptor().getControllerDescription().getAppointmentScheduleDay().
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
  }

