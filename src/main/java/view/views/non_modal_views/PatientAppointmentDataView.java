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
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import model.entity.Entity;
import model.non_entity.SystemDefinition;
import view.View;
import view.support_classes.models.PatientAppointmentDataTableModel;
import view.support_classes.renderers.AppointmentsTableLocalDateRenderer;
import view.support_classes.renderers.TableLocalDateCentredRenderer;
import view.support_classes.renderers.TableLocalDateTimeToLocalDateAndCentredRenderer;
import view.support_classes.renderers.TableIntegerCenteredRenderer;
import view.support_classes.renderers.TablePatientEmboldenedRenderer;

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
            case REQUEST_PATIENT_WITHOUT_APPOINTMENT:
                pad = new PatientAppointmentData();
                pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT);
                pad.setFromYear(getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData().getFromYear());
                pad.setToYear(getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData().getToYear());
                getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
                doSendActionEvent(ViewController.
                        PatientAppointmentDataViewControllerActionEvent.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
            case REQUEST_SET_TIME_FRAME:
                pad = getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData();
                pad.setFromYear((Integer)this.spnFromYear.getValue());
                pad.setToYear((Integer)this.spnToYear.getValue());
                getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
                doSendActionEvent(ViewController.
                        PatientAppointmentDataViewControllerActionEvent.PATIENT_APPOINTMENT_DATA_REQUEST);
                break;
            case REQUEST_PATIENTS_WITH_APPOINTMENTS:
                pad = getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData();
                pad.setFromYear(getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData().getFromYear());
                pad.setToYear(getMyController().getDescriptor().getControllerDescription().getPatientAppointmentData().getToYear());
                pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT);
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
      
        this.pnlPatientAppointmentData.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(), 
        "Last appointment for each patient", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        SystemDefinition.TITLED_BORDER_FONT, 
        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        this.pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(), 
        "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        SystemDefinition.TITLED_BORDER_FONT, 
        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        this.pnlTimeFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(), 
        "Set time frame", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        SystemDefinition.TITLED_BORDER_FONT, 
        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        this.pnlPatientActivity.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(), 
        "Patient activity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        SystemDefinition.TITLED_BORDER_FONT, 
        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        // Set a NumberEditor without commas
        JSpinner.NumberEditor editor1 = new JSpinner.NumberEditor(spnFromYear, "#");
        JSpinner.NumberEditor editor2 = new JSpinner.NumberEditor(spnToYear, "#");
        DecimalFormat format = editor1.getFormat();
        format.setGroupingUsed(false);  // Disable grouping (no commas)
        format = editor2.getFormat();
        format.setGroupingUsed(false);  // Disable grouping (no commas)
        spnFromYear.setEditor(editor1);
        spnToYear.setEditor(editor2);
        
        this.rdbNoAppointments.setActionCommand(Action.REQUEST_PATIENT_WITHOUT_APPOINTMENT.toString());
        this.rdbSomeAppointments.setActionCommand(Action.REQUEST_PATIENTS_WITH_APPOINTMENTS.toString());
        this.rdbNoAppointments.addActionListener(this);
        this.rdbSomeAppointments.addActionListener(this);
        
        
        this.btnArchiveSelectedPatient.setActionCommand(Action.REQUEST_ARCHIVE_PATIENT.toString());
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnSetTimeFrame.setActionCommand(Action.REQUEST_SET_TIME_FRAME.toString());
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
        // Set the preferred header height to fit two lines
        JTableHeader tableHeader = tblPatientAppointmentData.getTableHeader();
        tableHeader.setPreferredSize(new java.awt.Dimension(tableHeader.getPreferredSize().width, 40));
        this.tblPatientAppointmentData.setDefaultRenderer(LocalDate.class, new TableLocalDateCentredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(LocalDateTime.class, new TableLocalDateTimeToLocalDateAndCentredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(Integer.class, new TableIntegerCenteredRenderer());
        this.tblPatientAppointmentData.setDefaultRenderer(Patient.class, new TablePatientEmboldenedRenderer());
        ViewController.setJTableColumnProperties(tblPatientAppointmentData, this.scrPatientAppointmentDataTable.getPreferredSize().width, 20,7,10,18,19,7,5,7,7);
        
        // Attach the TableRowSorter
        PatientAppointmentDataTableModel model = (PatientAppointmentDataTableModel)this.tblPatientAppointmentData.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblPatientAppointmentData.setRowSorter(sorter);
        //javax.swing.SwingUtilities.invokeLater(() -> {
            PatientAppointmentData pad  = new PatientAppointmentData();        
            pad.setFromYear(1992);
            pad.setToYear(LocalDate.now().getYear() + 1);
            pad.setScope(Entity.Scope.PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT);
            getMyController().getDescriptor().getViewDescription().setPatientAppointmentData(pad);
            
            this.rdbSomeAppointments.setSelected(true);
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
        TitledBorder titledBorder = null;
        PatientAppointmentDataTableModel model = 
                (PatientAppointmentDataTableModel)tblPatientAppointmentData.getModel();
        model.removeAllElements();
        model.setData(pad.get());
        switch (pad.getScope()){
            case PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT:
                titledBorder = (TitledBorder)pnlPatientAppointmentData.getBorder();
                titledBorder.setTitle("Patients who have not yet had an appointment (" + pad.get().size() + ")");
                pnlPatientAppointmentData.repaint();
                break;
            default:
                titledBorder = (TitledBorder)pnlPatientAppointmentData.getBorder();
                titledBorder.setTitle("Last appointment date for each patient (" + pad.get().size() + ")");
                pnlPatientAppointmentData.repaint();
                break;
        }
    }

    private void initialiseViewState(PatientAppointmentData pad){
        this.spnFromYear.setValue(pad.getFromYear());
        this.spnToYear.setValue(pad.getToYear());
        tblPatientAppointmentData.clearSelection();
    }
    
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_ARCHIVE_PATIENT,
        REQUEST_PATIENT_WITHOUT_APPOINTMENT,
        REQUEST_SET_TIME_FRAME,
        REQUEST_PATIENTS_WITH_APPOINTMENTS
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

        rdbGroup.add(rdbSomeAppointments);
        rdbGroup.add(rdbNoAppointments);

        pnlPatientAppointmentData.setBorder(javax.swing.BorderFactory.createTitledBorder("Last appointment for each patient"));

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
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 1192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlPatientAppointmentDataLayout.setVerticalGroup(
            pnlPatientAppointmentDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentDataLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                .addGap(28, 28, 28)
                .addGroup(pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spnToYear)
                    .addComponent(jLabel1)
                    .addComponent(spnFromYear)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSetTimeFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlTimeFrameLayout.setVerticalGroup(
            pnlTimeFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTimeFrameLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnFromYear, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnToYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSetTimeFrame)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        btnArchiveSelectedPatient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchiveSelectedPatientActionPerformed(evt);
            }
        });

        pnlPatientActivity.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient activity"));

        rdbSomeAppointments.setText("1 or more appointments");

        rdbNoAppointments.setText("no appointments");

        javax.swing.GroupLayout pnlPatientActivityLayout = new javax.swing.GroupLayout(pnlPatientActivity);
        pnlPatientActivity.setLayout(pnlPatientActivityLayout);
        pnlPatientActivityLayout.setHorizontalGroup(
            pnlPatientActivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientActivityLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPatientActivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbSomeAppointments, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                    .addComponent(rdbNoAppointments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPatientActivityLayout.setVerticalGroup(
            pnlPatientActivityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientActivityLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(rdbSomeAppointments)
                .addGap(26, 26, 26)
                .addComponent(rdbNoAppointments)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTimeFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArchiveSelectedPatient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPatientActivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTimeFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlPatientActivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnArchiveSelectedPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlPatientAppointmentData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchiveSelectedPatientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchiveSelectedPatientActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnArchiveSelectedPatientActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArchiveSelectedPatient;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnSetTimeFrame;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlPatientActivity;
    private javax.swing.JPanel pnlPatientAppointmentData;
    private javax.swing.JPanel pnlTimeFrame;
    private javax.swing.ButtonGroup rdbGroup;
    private javax.swing.JRadioButton rdbNoAppointments;
    private javax.swing.JRadioButton rdbSomeAppointments;
    private javax.swing.JScrollPane scrPatientAppointmentDataTable;
    private javax.swing.JSpinner spnFromYear;
    private javax.swing.JSpinner spnToYear;
    private javax.swing.JTable tblPatientAppointmentData;
    // End of variables declaration//GEN-END:variables

}
