/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.modal_views;

import theclinic.controller.SystemDefinition;
import theclinic.controller.ScheduleViewController;
import theclinic.controller.ViewController;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import theclinic.model.entity.Appointment;
import theclinic.view.support_classes.table_models.EmptySlotAvailability2ColumnTableModel;
import theclinic.view.support_classes.table_renderers.TableHeaderCellBorderRenderer;
import javax.swing.SpinnerNumberModel;
import theclinic.view.View;
import theclinic.view.views.non_modal_views.DesktopView;
import theclinic.view.support_classes.table_renderers.AppointmentsTableDurationRenderer;
import theclinic.view.support_classes.table_renderers.SelectSlotDurationRenderer;

/**
 *
 * @author colin
 */
public class ModalBookableSlotScannerView extends ModalView implements ActionListener,
                                                                       ChangeListener,
                                                                       ItemListener,
                                                                       ListSelectionListener,
                                                                       PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalBookableSlotScannerView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Bookable slot scanner");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_DURATION_SETTING,
        REQUEST_START_SCAN
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        
        setTitle("Bookable slot availability");
        
        this.cmbSelectSlotDuration.setRenderer(new SelectSlotDurationRenderer());
        this.cmbSelectSlotDuration.setActionCommand(Action.REQUEST_DURATION_SETTING.toString());
        this.cmbSelectSlotDuration.addActionListener(this);
        
        this.buttonGroup1.add(rdbSelectMonths);
        this.buttonGroup1.add(rdbSelectWeeks);
        this.rdbSelectWeeks.setSelected(true);
        this.rdbSelectWeeks.addItemListener(this);
        this.rdbSelectMonths.addItemListener(this);
        
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnCloseView.addActionListener(this);
        this.btnStartScan.setActionCommand(Action.REQUEST_START_SCAN.toString());
        this.btnStartScan.addActionListener(this);
        
        this.spnSlotSearchOffset.addChangeListener(this);
        
        this.tblSlotAvailability = new JTable(new EmptySlotAvailability2ColumnTableModel());
        scrPanelSlotAvailability.setViewportView(this.tblSlotAvailability);
        setEmptySlotAvailabilityTableListener();
        
        resetBookableSlotAvailabilityDisplay();

    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_CLOSE_VIEW:
                doCloseViewRequest();
                break;
            case REQUEST_DURATION_SETTING:
                doDurationSettingRequest();
                resetBookableSlotAvailabilityDisplay();
                break;
            case REQUEST_START_SCAN:
                doStartScanRequest();
                break;
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent e){
        resetBookableSlotAvailabilityDisplay();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ScheduleViewController.Properties propertyName = 
                ScheduleViewController.Properties.valueOf(e.getPropertyName());
        switch(propertyName){
            case NO_APPOINTMENT_SLOTS_FROM_DAY_RECEIVED://instruction to clear list
                setTitle("Bookable slot availability");
                populateEmptySlotAvailabilityTable(new ArrayList<>());
                break;
            case APPOINTMENT_SLOTS_FROM_DAY_RECEIVED:
                String tableTitleDuration = new AppointmentsTableDurationRenderer().renderDuration(
                        (Duration)getMyController().getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.EMPTY_SLOT_MINIMUM_DURATION));
                String tableTitleDay = ((LocalDate)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.EMPTY_SLOT_FROM_DAY)).format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        //getAppointment().getStart().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                setTitle("Bookable slot availability from " + tableTitleDay 
                        + " for a duration of " + tableTitleDuration);
                populateEmptySlotAvailabilityTable(
                        (ArrayList<Appointment>)getMyController().getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.APPOINTMENT_SLOTS));
                /**
                 * without the next lines the appointments table is unconscious of being selected?!)
                 */
                //getMyController().getDescriptor().getViewDescription().setScheduleDay(dayDatePicker.getDate());
                break;
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e){
        resetBookableSlotAvailabilityDisplay();
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if(e.getSource().equals(this.lsmForSloAvailabilityTable)){
            if (e.getValueIsAdjusting()) return;

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                int selectedRow = lsm.getMinSelectionIndex();
                doEmptySlotAvailabilityTableRowSelection(selectedRow);
            }
        }
    }
    
    private void resetBookableSlotAvailabilityDisplay(){
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, null);
        ActionEvent actionEvent = new ActionEvent(this,
            ActionEvent.ACTION_PERFORMED,
            ScheduleViewController.Actions.
            EMPTY_SLOTS_FROM_DAY_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    /*private void btnScanForEmptySlotsActionPerformed() {                                                     
        // TODO add your handling code here:
        //LocalDate searchStartDate = dayDatePicker.getDate();
        LocalDate searchStartDate = LocalDate.now();
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED,
                    ViewController.ScheduleViewControllerActionEvent.
                            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }*/
    
    /*private void doSearchAvailableSlotsAction(){
        //LocalDate searchStartDate = dayDatePicker.getDate();
        LocalDate searchStartDate = LocalDate.now();
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, searchStartDate);
        ActionEvent actionEvent = new ActionEvent(this,
            ActionEvent.ACTION_PERFORMED,
            ViewController.ScheduleViewControllerActionEvent.
            EMPTY_SLOT_SCAN_CONFIGURATION_VIEW_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    } */ 
    
    private void doStartScanRequest(){
        Duration duration = (Duration)this.cmbSelectSlotDuration.getSelectedItem();
        /*LocalDate startScanDate = getMyController().getDescriptor().
                getViewDescription().getScheduleDay();*/
        LocalDate startScanDate = LocalDate.now();
        if (!duration.isZero()){
            if(this.rdbSelectWeeks.isSelected()){
                startScanDate = startScanDate.plusWeeks((Integer)this.spnSlotSearchOffset.getValue());
            }
            else startScanDate = startScanDate.plusMonths((Integer)this.spnSlotSearchOffset.getValue());

            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, startScanDate);
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.DURATION, duration);
            ActionEvent actionEvent = new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED,
                ScheduleViewController.Actions.
                EMPTY_SLOTS_FROM_DAY_REQUEST.toString());
            getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void doCloseViewRequest(){
        try{
            setClosed(true);
        }catch (PropertyVetoException e){
            
        }
        
    }
    
    private void doDurationSettingRequest(){
        if ((this.cmbSelectSlotDuration.getSelectedIndex()==0) ||
            (this.cmbSelectSlotDuration.getSelectedIndex()==-1)){
            this.rdbSelectMonths.setEnabled(false);
            this.rdbSelectWeeks.setEnabled(false);
            this.spnSlotSearchOffset.setEnabled(false);
            //this.cmbSelectSlotDuration.setForeground(Color.red);
        }
        else{

            this.rdbSelectMonths.setEnabled(true);
            this.rdbSelectWeeks.setEnabled(true);
            this.spnSlotSearchOffset.setEnabled(true);
            this.cmbSelectSlotDuration.setForeground(Color.black);
        }
    }
     
    private void doEmptySlotAvailabilityTableRowSelection(int row){
        ActionEvent actionEvent = null;
        String actionCommand = null;
        Appointment appointment = 
                ((EmptySlotAvailability2ColumnTableModel)this.tblSlotAvailability.getModel()).getElementAt(row);
        LocalDate start = appointment.getStart().toLocalDate();
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.SCHEDULE_DAY, start);
        /*
        switch((ViewController.ControllerViewMode)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.CONTROLLER_VIEW_MODE)){
            case DIARY:
                actionCommand = ViewController.ScheduleViewControllerActionEvent.SCHEDULE_DIARY_VIEW_CONTROLLER_REQUEST.toString();
                break;
            case LIST:
                actionCommand = ViewController.ScheduleViewControllerActionEvent.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString();
                break;
        }*/
        actionCommand = ScheduleViewController.Actions.SCHEDULE_LIST_VIEW_CONTROLLER_REQUEST.toString();
        actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,actionCommand);
        this.getMyController().actionPerformed(actionEvent);
        this.doCloseViewRequest();
    }
    
    private void populateEmptySlotAvailabilityTable(ArrayList<Appointment> a) {
        if (a == null) {
            a = new ArrayList<>();
        }
        EmptySlotAvailability2ColumnTableModel model;
        /*if (this.tblSlotAvailability!=null){
            scrPanelSlotAvailability.remove(this.tblSlotAvailability);   
        }
        this.tblSlotAvailability = new JTable(new EmptySlotAvailability2ColumnTableModel());
        scrPanelSlotAvailability.setViewportView(this.tblSlotAvailability);
        setEmptySlotAvailabilityTableListener();*/
        model = (EmptySlotAvailability2ColumnTableModel)this.tblSlotAvailability.getModel();
        model.removeAllElements();
        Iterator<Appointment> it = a.iterator();
        while (it.hasNext()){
            //((EmptySlotAvailability2ColumnTableModel)this.tblSlotAvailability.getModel()).addElement(it.next());
            model.addElement(it.next());
        }

        JTableHeader tableHeader = this.tblSlotAvailability.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);
        
        TableColumnModel columnModel = this.tblSlotAvailability.getColumnModel();
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
    }
    
    private ListSelectionModel lsmForSloAvailabilityTable = null;
    private void setEmptySlotAvailabilityTableListener(){
        this.tblSlotAvailability.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmForSloAvailabilityTable = this.tblSlotAvailability.getSelectionModel();
        lsmForSloAvailabilityTable.addListSelectionListener(this); 
    }
    
    private InternalFrameAdapter internalFrameAdapter = null;
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosing(InternalFrameEvent e) {
                ModalBookableSlotScannerView.this.removeInternalFrameListener(internalFrameAdapter);
                ActionEvent actionEvent = new ActionEvent(
                        ModalBookableSlotScannerView.this,ActionEvent.ACTION_PERFORMED,
                        ScheduleViewController.Actions.
                                VIEW_CLOSE_NOTIFICATION.toString());
                ModalBookableSlotScannerView.this.getMyController().actionPerformed(actionEvent);
                
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e){
                ActionEvent actionEvent = new ActionEvent(
                        ModalBookableSlotScannerView.this,ActionEvent.ACTION_PERFORMED,
                        ScheduleViewController.Actions.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                ModalBookableSlotScannerView.this.getMyController().actionPerformed(actionEvent);
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
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlStartDateOffset = new javax.swing.JPanel();
        spnSlotSearchOffset = new javax.swing.JSpinner();
        rdbSelectMonths = new javax.swing.JRadioButton();
        rdbSelectWeeks = new javax.swing.JRadioButton();
        cmbSelectSlotDuration = new javax.swing.JComboBox<Duration>();
        lblDuration = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        scrPanelSlotAvailability = new javax.swing.JScrollPane();
        tblSlotAvailability = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnStartScan = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setPreferredSize(new java.awt.Dimension(266, 144));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Scan configuration"));

        pnlStartDateOffset.setBorder(javax.swing.BorderFactory.createTitledBorder("Start date offset by ..."));

        rdbSelectMonths.setText("month(s)");

        rdbSelectWeeks.setText("week(s)");

        javax.swing.GroupLayout pnlStartDateOffsetLayout = new javax.swing.GroupLayout(pnlStartDateOffset);
        pnlStartDateOffset.setLayout(pnlStartDateOffsetLayout);
        pnlStartDateOffsetLayout.setHorizontalGroup(
            pnlStartDateOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStartDateOffsetLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlStartDateOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbSelectMonths)
                    .addComponent(spnSlotSearchOffset, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbSelectWeeks))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        pnlStartDateOffsetLayout.setVerticalGroup(
            pnlStartDateOffsetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStartDateOffsetLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(spnSlotSearchOffset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rdbSelectWeeks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbSelectMonths)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        spnSlotSearchOffset.setModel(new SpinnerNumberModel(0,0,12,1));

        cmbSelectSlotDuration.setModel(new javax.swing.DefaultComboBoxModel<>(new Duration[] {
            Duration.ofMinutes(5),
            Duration.ofMinutes(10),
            Duration.ofMinutes(15),
            Duration.ofMinutes(20),
            Duration.ofMinutes(25),
            Duration.ofMinutes(30),
            Duration.ofMinutes(35),
            Duration.ofMinutes(40),
            Duration.ofMinutes(45),
            Duration.ofMinutes(50),
            Duration.ofMinutes(55),
            Duration.ofMinutes(60),
            Duration.ofMinutes(75),
            Duration.ofMinutes(90),
            Duration.ofMinutes(105),
            Duration.ofMinutes(120),
            Duration.ofMinutes(180),
            Duration.ofMinutes(240),
            Duration.ofMinutes(300),
            Duration.ofMinutes(360),
            Duration.ofMinutes(420),
            Duration.ofMinutes(480)}));
cmbSelectSlotDuration.setBorder(javax.swing.BorderFactory.createEtchedBorder());

lblDuration.setText("Duration");

javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
jPanel1.setLayout(jPanel1Layout);
jPanel1Layout.setHorizontalGroup(
    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(jPanel1Layout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(cmbSelectSlotDuration, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlStartDateOffset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(15, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(27, 27, 27)
            .addComponent(lblDuration)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cmbSelectSlotDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlStartDateOffset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(22, 22, 22))
    );

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Available slots"));

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

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
            .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(scrPanelSlotAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addContainerGap())
    );

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(15, 15, 15)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );

    jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Action"));

    btnStartScan.setText("<html><center>Start</center>scan</center></html>");

    btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(btnStartScan, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(btnStartScan, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(16, 16, 16))
    );

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
            .addGap(12, 12, 12)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(12, 12, 12))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnStartScan;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<Duration> cmbSelectSlotDuration;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblDuration;
    private javax.swing.JPanel pnlStartDateOffset;
    private javax.swing.JRadioButton rdbSelectMonths;
    private javax.swing.JRadioButton rdbSelectWeeks;
    private javax.swing.JScrollPane scrPanelSlotAvailability;
    private javax.swing.JSpinner spnSlotSearchOffset;
    private javax.swing.JTable tblSlotAvailability;
    // End of variables declaration//GEN-END:variables
}
