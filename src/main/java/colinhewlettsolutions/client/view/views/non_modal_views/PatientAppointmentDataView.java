/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.Descriptor;
import colinhewlettsolutions.client.controller.PatientAppointmentDataViewController;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.PatientAppointmentData;
import colinhewlettsolutions.client.model.non_entity.Captions;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JInternalFrame;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelListener;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.support_classes.groupable_table_header_support.*;
import colinhewlettsolutions.client.view.support_classes.models.PatientAppointmentDataTableModel;
import colinhewlettsolutions.client.view.support_classes.renderers.AppointmentsTableLocalDateRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.TableLocalDateCentredRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.TableLocalDateTimeToLocalDateAndCentredRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.TableIntegerCenteredRenderer;
//import colinhewlettsolutions.client.view.support_classes.renderers.TablePatientEmboldenedRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.PatientWithCancelledAppointmentRenderer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author colin
 */
public class PatientAppointmentDataView extends View 
        implements ActionListener, 
                   ListSelectionListener,
                   PropertyChangeListener,
                   TableModelListener{
    
    enum Actions{
        REQUEST_CLOSE_VIEW,
        REQUEST_ARCHIVE_PATIENTS,
        //REQUEST_MODAL_PROGRESS_VIEW,
        REQUEST_PATIENT_RECALLER_VIEW,
        REQUEST_PATIENTS_WITH_APPOINTMENTS,
        REQUEST_PATIENT_WITHOUT_APPOINTMENT,
        REQUEST_SET_TIME_FRAME,
        
    }
    
    enum BorderTitles{
        ACTIONS,
        PAD_TABLE,
        PATIENT_ACTIVITY,
        RECALL_CRITERIA,
        SET_TIME_FRAME  
    }
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public PatientAppointmentDataView(
        View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {
        
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        PatientAppointmentData pad = null;
        Actions actionCommand = Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_ARCHIVE_PATIENTS ->{
                pad = (PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                pad.set(getSelectedRows());
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                doSendActionEvent(PatientAppointmentDataViewController.Actions.PATIENT_ARCHIVE_REQUEST);
                break;
            }
            case REQUEST_CLOSE_VIEW ->{
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
            }
            case REQUEST_PATIENT_RECALLER_VIEW ->{
                String message = "Patient recaller facility not yet implemented; \n"
                        + "awaiting requirement spec. from the Clinic";
                JOptionPane.showInternalMessageDialog(this, message, "Status message",JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            case REQUEST_PATIENT_WITHOUT_APPOINTMENT ->{
                pad = new PatientAppointmentData();
                pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT);
                pad.setFromYear(((PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getFromYear());
                pad.setToYear(((PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getToYear());
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                doSendActionEvent(PatientAppointmentDataViewController.Actions.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
            }
            case REQUEST_SET_TIME_FRAME ->{
                pad = (PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                pad.setFromYear((Integer)this.spnFromYear.getValue());
                pad.setToYear((Integer)this.spnToYear.getValue());
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                doSendActionEvent(PatientAppointmentDataViewController.Actions.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
            }
            case REQUEST_PATIENTS_WITH_APPOINTMENTS ->{
                pad = (PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                pad.setFromYear(((PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getFromYear());
                pad.setToYear(((PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA)).getToYear());
                pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT);
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                doSendActionEvent(PatientAppointmentDataViewController.Actions.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        PatientAppointmentDataViewController.Properties propertyName =
                PatientAppointmentDataViewController.Properties.valueOf(e.getPropertyName());
        switch(propertyName){
            case PATIENT_APPOINTMENT_DATA_RECEIVED ->{
                initialiseViewState(
                        (PatientAppointmentData)getMyController().getDescriptor().
                                getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA));
                populatePatientAppointmentDataTable(
                        (PatientAppointmentData)getMyController().getDescriptor().
                                getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA));
                break;
            }
        }
    }
    
    @Override
    public void tableChanged(TableModelEvent e){
        if (e.getType() == TableModelEvent.UPDATE) {
            if (e.getColumn() == 0 || e.getColumn() == 1) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                PatientAppointmentData _pad = (PatientAppointmentData)getMyController().
                        getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                PatientAppointmentDataTableModel model =  
                        (PatientAppointmentDataTableModel)e.getSource();
                //Boolean value = (Boolean)model.getValueAt(row, column);
                PatientAppointmentData pad = model.getElementAt(row);
                //pad.getPatient().setIsRequestToSendPatientGBTRecallPending(value);
                pad.setScope(_pad.getScope());
                pad.setFromYear(_pad.getFromYear());
                pad.setToYear(_pad.getToYear());
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
                //getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT, pad.getPatient());
                //tblAppointments.clearSelection();

                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientAppointmentDataViewControllerActionEvent.
                            PATIENT_RECALL_ACTIVITY_STATUS_CHANGE.toString());
                getMyController().actionPerformed(actionEvent);
                this.tblPatientAppointmentData.clearSelection();
            }
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        int[] selectedRows = null;
        if (!e.getValueIsAdjusting()) {
            selectedRows = tblPatientAppointmentData.getSelectedRows();
            if(selectedRows!=null){
                if(selectedRows.length>0){
                    setSelectedRows(selectedRows);
                }
            }
        }

    }

    @Override
    public void initialiseView(){
        Descriptor descriptor = getMyController().getMyController().getDescriptor();
        System.out.println(String.valueOf((Boolean)descriptor.getControllerDescription().getProperty(SystemDefinition.Properties.LOGIN_REQUIRED)));
        initComponents();
        //setTitle("Patient appointment data");
        setVisible(true);
        addInternalFrameListeners();
        setScheduleTitledBorderSettings();

        // Set a NumberEditor without commas
        JSpinner.NumberEditor editor1 = new JSpinner.NumberEditor(spnFromYear, "#");
        JSpinner.NumberEditor editor2 = new JSpinner.NumberEditor(spnToYear, "#");
        DecimalFormat format = editor1.getFormat();
        format.setGroupingUsed(false);  // Disable grouping (no commas)
        format = editor2.getFormat();
        format.setGroupingUsed(false);  // Disable grouping (no commas)
        spnFromYear.setEditor(editor1);
        spnToYear.setEditor(editor2);

        this.rdbNoAppointments.setActionCommand(Actions.REQUEST_PATIENT_WITHOUT_APPOINTMENT.toString());
        this.rdbSomeAppointments.setActionCommand(Actions.REQUEST_PATIENTS_WITH_APPOINTMENTS.toString());
        this.rdbNoAppointments.addActionListener(this);
        this.rdbSomeAppointments.addActionListener(this);
        
        
        this.btnArchiveSelectedPatient.setActionCommand(Actions.REQUEST_ARCHIVE_PATIENTS.toString());
        this.btnCloseView.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        this.btnSetTimeFrame.setActionCommand(Actions.REQUEST_SET_TIME_FRAME.toString());
        this.btnArchiveSelectedPatient.addActionListener(this);
        this.btnCloseView.addActionListener(this);
        this.btnSetTimeFrame.addActionListener(this);
        
        this.btnArchiveSelectedPatient.setText(
                Captions.PatientAppointmentDataView.PATIENT_ARCHIVE_RESTORE_REQUEST_CAPTION._1());
        this.btnCloseView.setText(Captions.CLOSE_VIEW);
        this.btnSetTimeFrame.setText(
                Captions.PatientAppointmentDataView.PATIENT_TIME_FRAME_REQUEST_CAPTION._1());

        this.tblPatientAppointmentData.setDefaultRenderer(LocalDate.class, new AppointmentsTableLocalDateRenderer());
        ListSelectionModel lsm = this.tblPatientAppointmentData.getSelectionModel();
        this.tblPatientAppointmentData.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(this);

        this.tblPatientAppointmentData.setDefaultRenderer(LocalDate.class, new TableLocalDateCentredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(LocalDateTime.class, new TableLocalDateTimeToLocalDateAndCentredRenderer());
        //this.tblPatientAppointmentData.setDefaultRenderer(Integer.class, new TableIntegerCenteredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(Patient.class, new PatientWithCancelledAppointmentRenderer());
        ViewController.setJTableColumnProperties(tblPatientAppointmentData, this.scrPatientAppointmentDataTable.getPreferredSize().width, 5,5,15,6,10,18,10,6,6,6,6);
        
        tblPatientAppointmentData.getModel().addTableModelListener(this);
        
        TableColumnModel cm = tblPatientAppointmentData.getColumnModel();
        GroupableTableHeader header = new GroupableTableHeader(cm);   
        tblPatientAppointmentData.setTableHeader(header);
        header.setUI(new GroupableTableHeaderUI());
        scrPatientAppointmentDataTable.setViewportView(tblPatientAppointmentData);
        scrPatientAppointmentDataTable.setColumnHeaderView(header);       
        header.setReorderingAllowed(false);
        TableHeaderUI ui = tblPatientAppointmentData.getTableHeader().getUI();   
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tblPatientAppointmentData.getModel());
        tblPatientAppointmentData.setRowSorter(sorter);
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 40));
        
        // Get the default header renderer
        TableCellRenderer defaultHeaderRenderer = tblPatientAppointmentData.getTableHeader().getDefaultRenderer();

        // Wrap it so we keep sort arrows and add our own header customisation
        tblPatientAppointmentData.getTableHeader().setDefaultRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            // Render the header using the default renderer
            Component c = defaultHeaderRenderer.getTableCellRendererComponent(
                tbl, value, isSelected, hasFocus, row, column
            );

            // If you have special styling for grouped columns, apply it here
            // Example: bold text for grouped columns
            if (c instanceof JLabel label) {
                int modelIndex = tbl.convertColumnIndexToModel(column);
                if (modelIndex >= 6 && modelIndex <= 9) { // your grouped range
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                } else {
                    label.setFont(label.getFont().deriveFont(Font.PLAIN));
                }
            }

            // ✅ Default renderer still draws the sort arrow as needed
            return c;
        });
        
        javax.swing.SwingUtilities.invokeLater(() -> { 
            
            ColumnGroup actionRecallGroup = new ColumnGroup("Send recall to patient?");
            actionRecallGroup.add(cm.getColumn(0)); 
            actionRecallGroup.add(cm.getColumn(1));

            ColumnGroup recallDatesGroup = new ColumnGroup("Date to send recall");
            recallDatesGroup.add(cm.getColumn(7)); 
            recallDatesGroup.add(cm.getColumn(8)); 

            ColumnGroup dateRecallSentGroup = new ColumnGroup("Date recall sent");
            dateRecallSentGroup.add(cm.getColumn(9)); 
            dateRecallSentGroup.add(cm.getColumn(10)); 

            // ✅ Now safely add groups
            header.addColumnGroup(recallDatesGroup);
            header.addColumnGroup(dateRecallSentGroup);
            header.addColumnGroup(actionRecallGroup);
            header.revalidate();
            header.repaint();
                
        });
        
        PatientAppointmentData pad  = new PatientAppointmentData();        
        pad.setFromYear(1992);
        pad.setToYear(LocalDate.now().getYear() + 1);
        pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT);
        getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);

        this.rdbSomeAppointments.setSelected(true);
        doSendActionEvent(PatientAppointmentDataViewController.Actions.PATIENT_APPOINTMENT_DATA_REQUEST);
        
        this.pnlRecallCriteria.setEnabled(false);
        this.btnArchiveSelectedPatient.setEnabled(false);
        this.dobDatePicker.setEnabled(false);
        this.rdbSelectGBTRecalls.setEnabled(false);
        this.rdbSelectNonGBTRecalls.setEnabled(false);
        
        this.btnFetchPatientRecaller.setActionCommand(Actions.REQUEST_PATIENT_RECALLER_VIEW.toString());
        this.btnFetchPatientRecaller.addActionListener(this);
        
    }
    
    private void setScheduleTitledBorderSettings(){
        setBorderTitles(BorderTitles.ACTIONS,"Actions");
        setBorderTitles(BorderTitles.PAD_TABLE,getTableTitle());
        setBorderTitles(BorderTitles.PATIENT_ACTIVITY,"Patient activity");
        setBorderTitles(BorderTitles.RECALL_CRITERIA, "Recall criteria");
        setBorderTitles(BorderTitles.SET_TIME_FRAME, "Set time frame");
    }
    
    private void setBorderTitles(BorderTitles borderTitles, String caption){
        JPanel panel = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case ACTIONS:
                panel = this.pnlActions;
                isPanelBackgroundDefault = false;
                break;
            case PAD_TABLE:
                panel = this.pnlPatientAppointmentData;
                isPanelBackgroundDefault = true;
                break;
            case PATIENT_ACTIVITY:
                panel = this.pnlPatientActivity;
                isPanelBackgroundDefault = true;
                break;
            case RECALL_CRITERIA:
                panel = this.pnlRecallCriteria;
                isPanelBackgroundDefault = true;
                break;
            case SET_TIME_FRAME:
                panel = this.pnlTimeFrame;
                isPanelBackgroundDefault = true;
                break;       
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
            String message = "Unexpected null value for titled border panel encountered in PatientView::setBorderTitles() method";
            JOptionPane.showMessageDialog(this, message, "View error", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    private String tableTitle = null;
    private void setTableTitle(String value){
        tableTitle = value;
    }
    private String getTableTitle(){
        return tableTitle;
    }
    
    private int findColumnIndexByHeader(TableColumnModel columnModel, String headerName) {
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            if (headerName.equals(column.getHeaderValue())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column with header '" + headerName + "' not found.");
    }
    
    private ArrayList<PatientAppointmentData> pads = new ArrayList<>();
    private void setSelectedRows(int[] value){
        pads = new ArrayList<>();
        if (value!=null){
            if (value.length>0){
                for (int selectedRow = 0; value.length-1 >= selectedRow; selectedRow++){
                    PatientAppointmentDataTableModel model = (PatientAppointmentDataTableModel)tblPatientAppointmentData.getModel();
                    PatientAppointmentData pad = model.getElementAt(value[selectedRow]);
                    pads.add(pad);
                }
            }
        } 
    }
    private ArrayList<PatientAppointmentData> getSelectedRows (){
        return pads;
    }
    
    
    private InternalFrameAdapter internalFrameAdapter = null;
    private void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
         internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                doSendActionEvent(PatientAppointmentDataViewController.Actions.VIEW_CLOSE_NOTIFICATION); 
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                doSendActionEvent(PatientAppointmentDataViewController.Actions.VIEW_ACTIVATED_NOTIFICATION); 
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void doSendActionEvent(PatientAppointmentDataViewController.Actions actionCommand){
        if (getMyController().getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA) == null){
            PatientAppointmentData pad = (PatientAppointmentData)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA);
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_APPOINTMENT_DATA, pad);
        }
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void populatePatientAppointmentDataTable(PatientAppointmentData pad){
        TitledBorder titledBorder = null;
        PatientAppointmentDataTableModel model = 
                (PatientAppointmentDataTableModel)tblPatientAppointmentData.getModel();
        model.removeAllElements();
        model.setData(pad.get());
        switch (pad.getScope()){
            case PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT ->{
                setTableTitle("Inactive patients, had no appointments yet");
                break;
            }
            default ->{
                setTableTitle("Active patients, had at least one appopintment");
                break;
            }

        }
        titledBorder = (TitledBorder)pnlPatientAppointmentData.getBorder();
        titledBorder.setTitle(getTableTitle() + " (" + pad.get().size() + ")");
        pnlPatientAppointmentData.repaint();
    }

    private void initialiseViewState(PatientAppointmentData pad){
        this.spnFromYear.setValue(pad.getFromYear());
        this.spnToYear.setValue(pad.getToYear());
        tblPatientAppointmentData.clearSelection();
    }
    
    private LocalDate fromRecallDate = null;
    private void setFromRecallDate(LocalDate value){
        fromRecallDate = value;
    }
    private LocalDate getFromRecallDate(){
        return fromRecallDate;
    }
    
    class DOBDatePickerDateChangeListener implements DateChangeListener {
        @Override
        public void dateChanged(DateChangeEvent event) {
            /**
             * Update logged at 30/10/2021 08:32
             * inherited view status (set if any changes have been made to form since its initialisation)
             * is initialised to true (date changed)
             */
            setViewStatus(true);
            LocalDate date = event.getNewDate();
            setFromRecallDate(date);
            /*
            if (date != null) {
                lblNameAge.setText("(" + String.valueOf(getAge(date)) + " yrs)");
            } 
            */
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdbGroup = new javax.swing.ButtonGroup();
        pnlPatientAppointmentData = new javax.swing.JPanel();
        scrPatientAppointmentDataTable = new javax.swing.JScrollPane();
        tblPatientAppointmentData = new javax.swing.JTable(new PatientAppointmentDataTableModel());
        pnlActions = new javax.swing.JPanel();
        pnlTimeFrame = new javax.swing.JPanel();
        spnFromYear = new javax.swing.JSpinner(new SpinnerNumberModel(2024, 1992,2030,1));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        spnToYear = new javax.swing.JSpinner(new SpinnerNumberModel(2024, 1992,2030,1));
        btnSetTimeFrame = new javax.swing.JButton();
        btnArchiveSelectedPatient = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        pnlPatientActivity = new javax.swing.JPanel();
        rdbSomeAppointments = new javax.swing.JRadioButton();
        rdbNoAppointments = new javax.swing.JRadioButton();
        btnFetchPatientRecaller = new javax.swing.JButton();
        pnlRecallCriteria = new javax.swing.JPanel();
        rdbSelectGBTRecalls = new javax.swing.JRadioButton();
        rdbSelectNonGBTRecalls = new javax.swing.JRadioButton();
        dobDatePicker = new com.github.lgooddatepicker.components.DatePicker();
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dobDatePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dobDatePicker.setSettings(settings);
        dobDatePicker.addDateChangeListener(new DOBDatePickerDateChangeListener());
        ;

        rdbGroup.add(rdbSomeAppointments);
        rdbGroup.add(rdbNoAppointments);

        setTitle("Latest appointment and recall data for each patient");

        pnlPatientAppointmentData.setBorder(javax.swing.BorderFactory.createTitledBorder(getTableTitle()));

        scrPatientAppointmentDataTable.setPreferredSize(new java.awt.Dimension(1192, 402));

        tblPatientAppointmentData.setModel(new PatientAppointmentDataTableModel()
        );
        scrPatientAppointmentDataTable.setViewportView(tblPatientAppointmentData);

        javax.swing.GroupLayout pnlPatientAppointmentDataLayout = new javax.swing.GroupLayout(pnlPatientAppointmentData);
        pnlPatientAppointmentData.setLayout(pnlPatientAppointmentDataLayout);
        pnlPatientAppointmentDataLayout.setHorizontalGroup(
            pnlPatientAppointmentDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.DEFAULT_SIZE, 1240, Short.MAX_VALUE))
        );
        pnlPatientAppointmentDataLayout.setVerticalGroup(
            pnlPatientAppointmentDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentDataLayout.createSequentialGroup()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        pnlTimeFrame.setBorder(javax.swing.BorderFactory.createTitledBorder("Time frame"));

        jLabel1.setText("From year");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("To year");

        btnSetTimeFrame.setText("Set time frame");

        javax.swing.GroupLayout pnlTimeFrameLayout = new javax.swing.GroupLayout(pnlTimeFrame);
        pnlTimeFrame.setLayout(pnlTimeFrameLayout);
        pnlTimeFrameLayout.setHorizontalGroup(
            pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                        .addComponent(btnSetTimeFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                        .addGroup(pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(spnFromYear))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spnToYear)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16))))
        );
        pnlTimeFrameLayout.setVerticalGroup(
            pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnFromYear))
                    .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnToYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(btnSetTimeFrame)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        btnArchiveSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchiveSelectedPatientActionPerformed(evt);
            }
        });

        pnlPatientActivity.setBorder(javax.swing.BorderFactory.createTitledBorder("Patients with "));

        rdbSomeAppointments.setText("1 or more appointments");

        rdbNoAppointments.setText("no appointments");

        javax.swing.GroupLayout pnlPatientActivityLayout = new javax.swing.GroupLayout(pnlPatientActivity);
        pnlPatientActivity.setLayout(pnlPatientActivityLayout);
        pnlPatientActivityLayout.setHorizontalGroup(
            pnlPatientActivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientActivityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientActivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbSomeAppointments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rdbNoAppointments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPatientActivityLayout.setVerticalGroup(
            pnlPatientActivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientActivityLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbSomeAppointments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbNoAppointments)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnFetchPatientRecaller.setText("<html><center>Fetch recaller</center><center>for selected patients</center></html>");
        btnFetchPatientRecaller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFetchPatientRecallerActionPerformed(evt);
            }
        });

        pnlRecallCriteria.setBorder(javax.swing.BorderFactory.createTitledBorder("Recall criteria"));

        rdbSelectGBTRecalls.setText("GBT recalls");

        rdbSelectNonGBTRecalls.setText("non-GBT recalls");

        javax.swing.GroupLayout pnlRecallCriteriaLayout = new javax.swing.GroupLayout(pnlRecallCriteria);
        pnlRecallCriteria.setLayout(pnlRecallCriteriaLayout);
        pnlRecallCriteriaLayout.setHorizontalGroup(
            pnlRecallCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallCriteriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlRecallCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbSelectNonGBTRecalls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rdbSelectGBTRecalls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlRecallCriteriaLayout.createSequentialGroup()
                        .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlRecallCriteriaLayout.setVerticalGroup(
            pnlRecallCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRecallCriteriaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(dobDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbSelectGBTRecalls)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbSelectNonGBTRecalls)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlTimeFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPatientActivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFetchPatientRecaller, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlRecallCriteria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArchiveSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTimeFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlPatientActivity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlRecallCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnArchiveSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnFetchPatientRecaller, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlPatientAppointmentData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlPatientAppointmentData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchiveSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchiveSelectedPatientActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnArchiveSelectedPatientActionPerformed

    private void btnFetchPatientRecallerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFetchPatientRecallerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFetchPatientRecallerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArchiveSelectedPatient;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnFetchPatientRecaller;
    private javax.swing.JButton btnSetTimeFrame;
    private com.github.lgooddatepicker.components.DatePicker dobDatePicker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlPatientActivity;
    private javax.swing.JPanel pnlPatientActivity1;
    private javax.swing.JPanel pnlPatientAppointmentData;
    private javax.swing.JPanel pnlRecallCriteria;
    private javax.swing.JPanel pnlTimeFrame;
    private javax.swing.ButtonGroup rdbGroup;
    private javax.swing.JRadioButton rdbNoAppointments;
    private javax.swing.JRadioButton rdbNoAppointments1;
    private javax.swing.JRadioButton rdbSelectGBTRecalls;
    private javax.swing.JRadioButton rdbSelectNonGBTRecalls;
    private javax.swing.JRadioButton rdbSomeAppointments;
    private javax.swing.JRadioButton rdbSomeAppointments1;
    private javax.swing.JScrollPane scrPatientAppointmentDataTable;
    private javax.swing.JSpinner spnFromYear;
    private javax.swing.JSpinner spnToYear;
    private javax.swing.JTable tblPatientAppointmentData;
    // End of variables declaration//GEN-END:variables

}
