/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import model.entity.Patient;
import model.entity.PatientAppointmentData;
import model.non_entity.Captions;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.swing.JInternalFrame;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import model.entity.Entity;
import view.View;
import view.view_support_classes.models.PatientAppointmentDataTableModel;
import view.view_support_classes.renderers.AppointmentsTableLocalDateRenderer;
import view.view_support_classes.renderers.TableLocalDateCentredRenderer;
import view.view_support_classes.renderers.TableLocalDateTimeToLocalDateAndCentredRenderer;
import view.view_support_classes.renderers.TableIntegerCenteredRenderer;
import view.view_support_classes.renderers.TablePatientEmboldenedRenderer;

/**
 *
 * @author colin
 */
public class PatientAppointmentDataView extends View 
        implements ActionListener, 
                   ListSelectionListener,
                   PropertyChangeListener{
    
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
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_ARCHIVE_PATIENT:
                pad = getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData();
                pad.set(getSelectedRows());
                getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
                doSendActionEvent(ViewController.
                        PatientAppointmentDataViewControllerActionEvent.PATIENT_ARCHIVE_REQUEST);
                break;
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
            case REQUEST_SET_TIME_FRAME:
                pad = getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData();
                pad.setFromYear((Integer)this.spnFromYear.getValue());
                pad.setToYear((Integer)this.spnToYear.getValue());
                getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
                doSendActionEvent(ViewController.
                        PatientAppointmentDataViewControllerActionEvent.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
            case REQUEST_SORT_TABLE:
                pad = getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData();
                
                switch(pad.getScope()){
                    case BY_PATIENT:
                        pad.setScope(Entity.Scope.BY_LAST_APPOINTMENT_DATE);
                        break;
                    case BY_LAST_APPOINTMENT_DATE:
                        pad.setScope(Entity.Scope.BY_PATIENT);
                        break;
                }
                getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
                doSendActionEvent(ViewController.
                        PatientAppointmentDataViewControllerActionEvent.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
        }
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.PatientAppointmentDataViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientAppointmentDataViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            case PATIENT_APPOINTMENT_DATA_RECEIVED:
                initialiseViewState(
                        getMyController().getDescriptor().
                                getControllerDescription().getPatientAppointmentData());
                populatePatientAppointmentDataTable(
                        getMyController().getDescriptor().
                                getControllerDescription().getPatientAppointmentData());
                break;
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
        //SpinnerNumberModel yearModel = new SpinnerNumberModel(2024, 1992, 2030, 1);
        //spnFromYear = new JSpinner(yearModel);
        //spnToYear = new JSpinner(yearModel);
        initComponents();
        setTitle("Patient appointment data");
        setVisible(true);
        addInternalFrameListeners();
      
        //spnToYear.setValue(String.valueOf(2024));
        
        // Set a NumberEditor without commas
        JSpinner.NumberEditor editor1 = new JSpinner.NumberEditor(spnFromYear, "#");
        JSpinner.NumberEditor editor2 = new JSpinner.NumberEditor(spnToYear, "#");
        DecimalFormat format = editor1.getFormat();
        format.setGroupingUsed(false);  // Disable grouping (no commas)
        format = editor2.getFormat();
        format.setGroupingUsed(false);  // Disable grouping (no commas)
        spnFromYear.setEditor(editor1);
        spnToYear.setEditor(editor2);
        
        this.btnArchiveSelectedPatient.setActionCommand(Action.REQUEST_ARCHIVE_PATIENT.toString());
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnSetTimeFrame.setActionCommand(Action.REQUEST_SET_TIME_FRAME.toString());
        this.btnSortTable.setActionCommand(Action.REQUEST_SORT_TABLE.toString());
        this.btnArchiveSelectedPatient.addActionListener(this);
        this.btnCloseView.addActionListener(this);
        this.btnSetTimeFrame.addActionListener(this);
        this.btnSortTable.addActionListener(this);
        this.btnArchiveSelectedPatient.setText(
                Captions.PatientAppointmentDataView.PATIENT_ARCHIVE_RESTORE_REQUEST_CAPTION._1());
        this.btnCloseView.setText(Captions.CLOSE_VIEW);
        this.btnSetTimeFrame.setText(
                Captions.PatientAppointmentDataView.PATIENT_TIME_FRAME_REQUEST_CAPTION._1());

        this.tblPatientAppointmentData.setDefaultRenderer(LocalDate.class, new AppointmentsTableLocalDateRenderer());
        ListSelectionModel lsm = this.tblPatientAppointmentData.getSelectionModel();
        this.tblPatientAppointmentData.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(this);
        // Set the preferred header height to fit two lines
        JTableHeader tableHeader = tblPatientAppointmentData.getTableHeader();
        tableHeader.setPreferredSize(new java.awt.Dimension(tableHeader.getPreferredSize().width, 40));
        this.tblPatientAppointmentData.setDefaultRenderer(LocalDate.class, new TableLocalDateCentredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(LocalDateTime.class, new TableLocalDateTimeToLocalDateAndCentredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(Integer.class, new TableIntegerCenteredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(Patient.class, new TablePatientEmboldenedRenderer());
        ViewController.setJTableColumnProperties(tblPatientAppointmentData, this.scrPatientAppointmentDataTable.getPreferredSize().width, 20,7,10,18,19,7,5,7,7);
        
        //javax.swing.SwingUtilities.invokeLater(() -> {
            PatientAppointmentData pad  = new PatientAppointmentData();        
            pad.setFromYear(1992);
            pad.setToYear(LocalDate.now().getYear() + 1);
            pad.setScope(Entity.Scope.BY_LAST_APPOINTMENT_DATE);
            getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
            doSendActionEvent(ViewController.PatientAppointmentDataViewControllerActionEvent.PATIENT_APPOINTMENT_DATA_REQUEST);
        //});
        
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
                doSendActionEvent(ViewController.PatientAppointmentDataViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION); 
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                doSendActionEvent(ViewController.PatientAppointmentDataViewControllerActionEvent.VIEW_ACTIVATED_NOTIFICATION); 
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void doSendActionEvent(ViewController.PatientAppointmentDataViewControllerActionEvent actionCommand){
        if (getMyController().getDescriptor().getViewDescription().getPatientAppointmentData() == null){
            PatientAppointmentData pad = getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData();
                getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
        }
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void populatePatientAppointmentDataTable(PatientAppointmentData pad){
        PatientAppointmentDataTableModel model = 
                (PatientAppointmentDataTableModel)tblPatientAppointmentData.getModel();
        model.removeAllElements();
        model.setData(pad.get());
    }

    private void initialiseViewState(PatientAppointmentData pad){
        this.spnFromYear.setValue(pad.getFromYear());
        this.spnToYear.setValue(pad.getToYear());
        switch(pad.getScope()){
            case BY_LAST_APPOINTMENT_DATE:
                this.btnSortTable.setText(
                        Captions.PatientAppointmentDataView.PATIENT_APPOINTMENT_DATA_SORT_REQUEST_CAPTION._1());
                break;
            case BY_PATIENT:
                this.btnSortTable.setText(
                        Captions.PatientAppointmentDataView.PATIENT_APPOINTMENT_DATA_SORT_REQUEST_CAPTION._2());
                break;
        }
        tblPatientAppointmentData.clearSelection();
    }
    
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_ARCHIVE_PATIENT,
        REQUEST_SET_TIME_FRAME,
        REQUEST_SORT_TABLE
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPatientAppointmentAnalysisTable = new javax.swing.JPanel();
        scrPatientAppointmentDataTable = new javax.swing.JScrollPane();
        tblPatientAppointmentData = new javax.swing.JTable(new PatientAppointmentDataTableModel());
        pnlActions = new javax.swing.JPanel();
        pnlSetTimeFRame = new javax.swing.JPanel();
        spnFromYear = new javax.swing.JSpinner(new SpinnerNumberModel(2024, 1992,2030,1));
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        spnToYear = new javax.swing.JSpinner(new SpinnerNumberModel(2024, 1992,2030,1));
        btnSetTimeFrame = new javax.swing.JButton();
        btnArchiveSelectedPatient = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnSortTable = new javax.swing.JButton();

        pnlPatientAppointmentAnalysisTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Last appointment for each patient"));

        scrPatientAppointmentDataTable.setPreferredSize(new java.awt.Dimension(1192, 402));

        tblPatientAppointmentData.setModel(new PatientAppointmentDataTableModel()
        );
        scrPatientAppointmentDataTable.setViewportView(tblPatientAppointmentData);

        javax.swing.GroupLayout pnlPatientAppointmentAnalysisTableLayout = new javax.swing.GroupLayout(pnlPatientAppointmentAnalysisTable);
        pnlPatientAppointmentAnalysisTable.setLayout(pnlPatientAppointmentAnalysisTableLayout);
        pnlPatientAppointmentAnalysisTableLayout.setHorizontalGroup(
            pnlPatientAppointmentAnalysisTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentAnalysisTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 1192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlPatientAppointmentAnalysisTableLayout.setVerticalGroup(
            pnlPatientAppointmentAnalysisTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentAnalysisTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        pnlSetTimeFRame.setBorder(javax.swing.BorderFactory.createTitledBorder("Time frame"));

        jLabel1.setText("From year");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("To year");

        btnSetTimeFrame.setText("Set time frame");

        javax.swing.GroupLayout pnlSetTimeFRameLayout = new javax.swing.GroupLayout(pnlSetTimeFRame);
        pnlSetTimeFRame.setLayout(pnlSetTimeFRameLayout);
        pnlSetTimeFRameLayout.setHorizontalGroup(
            pnlSetTimeFRameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSetTimeFRameLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(pnlSetTimeFRameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spnToYear)
                    .addComponent(jLabel1)
                    .addComponent(spnFromYear)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlSetTimeFRameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSetTimeFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlSetTimeFRameLayout.setVerticalGroup(
            pnlSetTimeFRameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSetTimeFRameLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnFromYear, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnToYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSetTimeFrame)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        btnSortTable.setText("<html><center>Sort</center><center>table by</center><center>appointment date</center></html>");
        btnSortTable.setActionCommand("");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSetTimeFRame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArchiveSelectedPatient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSortTable))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSetTimeFRame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnArchiveSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSortTable, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlPatientAppointmentAnalysisTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlPatientAppointmentAnalysisTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArchiveSelectedPatient;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnSetTimeFrame;
    private javax.swing.JButton btnSortTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlPatientAppointmentAnalysisTable;
    private javax.swing.JPanel pnlSetTimeFRame;
    private javax.swing.JScrollPane scrPatientAppointmentDataTable;
    private javax.swing.JSpinner spnFromYear;
    private javax.swing.JSpinner spnToYear;
    private javax.swing.JTable tblPatientAppointmentData;
    // End of variables declaration//GEN-END:variables

}
