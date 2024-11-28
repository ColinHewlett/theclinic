/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JInternalFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import model.entity.Entity;
import static model.entity.Entity.Scope.BY_LAST_APPOINTMENT_DATE;
import static model.entity.Entity.Scope.BY_PATIENT;
import model.entity.Patient;
import model.non_entity.Captions;
import view.View;
import view.view_support_classes.models.ArchivedPatientsTableModel;
import view.view_support_classes.renderers.AppointmentsTableLocalDateRenderer;

/**
 *
 * @author colin
 */
public class ArchivedPatientsView extends View 
        implements ActionListener, 
                   ListSelectionListener,
                   PropertyChangeListener{
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ArchivedPatientsView(
        View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {
        
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Patient patient = null;
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_RESTORE_PATIENT:
                patient = new Patient();
                patient.set(getSelectedRows());
                if (!patient.get().isEmpty()){
                    getMyController().getDescriptor().getViewDescription().setPatient(patient);
                    doSendActionEvent(ViewController.
                        ArchivedPatientsViewControllerActionEvent.PATIENT_RESTORE_REQUEST);
                }
                
                
                break;
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
        }
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ArchivedPatientsViewControllerPropertyChangeEvent propertyName =
                ViewController.ArchivedPatientsViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            case ARCHIVED_PATIENT_RECEIVED:
                populateArchivedPatientsTable(
                        getMyController().getDescriptor().
                                getControllerDescription().getPatient());
                break;
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        int[] selectedRows = null;
        if (!e.getValueIsAdjusting()) {
            selectedRows = tblArchivedPatients.getSelectedRows();
            if(selectedRows!=null){
                if(selectedRows.length>0){
                    setSelectedRows(selectedRows);
                }
            }
        }

    }

    @Override
    public void initialiseView(){

        initComponents();
        setTitle("Archived patients view");
        setVisible(true);
        addInternalFrameListeners();
        
        this.btnRestoreSelectedPatientFromArchive.setActionCommand(Action.REQUEST_RESTORE_PATIENT.toString());
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnRestoreSelectedPatientFromArchive.addActionListener(this);
        this.btnCloseView.addActionListener(this);
        this.btnRestoreSelectedPatientFromArchive.setText(
                Captions.PatientAppointmentDataView.PATIENT_ARCHIVE_RESTORE_REQUEST_CAPTION._2());
        this.btnCloseView.setText(Captions.CLOSE_VIEW);

        //this.tblArchivedPatients.setDefaultRenderer(LocalDate.class, new AppointmentsTableLocalDateRenderer());
        ListSelectionModel lsm = this.tblArchivedPatients.getSelectionModel();
        this.tblArchivedPatients.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(this);
        this.tblArchivedPatients.setTableHeader(null);
        
        doSendActionEvent(ViewController.ArchivedPatientsViewControllerActionEvent.ARCHIVED_PATIENT_REQUEST);
        
    }
    
    private ArrayList<Patient> aps = new ArrayList<>();
    private void setSelectedRows(int[] value){
        aps = new ArrayList<>();
        if (value!=null){
            if (value.length>0){
                for (int selectedRow = 0; value.length-1 >= selectedRow; selectedRow++){
                    ArchivedPatientsTableModel model = (ArchivedPatientsTableModel)tblArchivedPatients.getModel();
                    Patient patient = model.getElementAt(value[selectedRow]);
                    aps.add(patient);
                }
            }
        } 
    }
    private ArrayList<Patient> getSelectedRows (){
        return aps;
    }
    
    
    private InternalFrameAdapter internalFrameAdapter = null;
    private void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
         internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                doSendActionEvent(ViewController.ArchivedPatientsViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION); 
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                doSendActionEvent(ViewController.ArchivedPatientsViewControllerActionEvent.VIEW_ACTIVATED_NOTIFICATION); 
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void doSendActionEvent(ViewController.ArchivedPatientsViewControllerActionEvent actionCommand){
        if (getMyController().getDescriptor().getViewDescription().getPatient() != null){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                actionCommand.toString());
            getMyController().actionPerformed(actionEvent);
        }
        
    }
    
    private void populateArchivedPatientsTable(Patient patient){
        ArchivedPatientsTableModel model = 
                (ArchivedPatientsTableModel)tblArchivedPatients.getModel();
        model.removeAllElements();
        model.setData(patient.get());
        /*
        Iterator<Patient> it = patient.get().iterator();
        while (it.hasNext()){
            Patient _patient = it.next();
            model.addElement(_patient);
        }
        */
        ViewController.setJTableColumnProperties(tblArchivedPatients, this.scrPatientAppointmentDataTable.getPreferredSize().width, 100);
    }

    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_RESTORE_PATIENT
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlActions = new javax.swing.JPanel();
        btnRestoreSelectedPatientFromArchive = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        pnlPatientAppointmentAnalysisTable = new javax.swing.JPanel();
        scrPatientAppointmentDataTable = new javax.swing.JScrollPane();
        tblArchivedPatients = new javax.swing.JTable(new ArchivedPatientsTableModel());

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                    .addComponent(btnRestoreSelectedPatientFromArchive, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRestoreSelectedPatientFromArchive, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlPatientAppointmentAnalysisTable.setBorder(javax.swing.BorderFactory.createTitledBorder("Archived patients"));

        scrPatientAppointmentDataTable.setPreferredSize(new java.awt.Dimension(905, 402));

        tblArchivedPatients.setModel(new ArchivedPatientsTableModel()
        );
        scrPatientAppointmentDataTable.setViewportView(tblArchivedPatients);

        javax.swing.GroupLayout pnlPatientAppointmentAnalysisTableLayout = new javax.swing.GroupLayout(pnlPatientAppointmentAnalysisTable);
        pnlPatientAppointmentAnalysisTable.setLayout(pnlPatientAppointmentAnalysisTableLayout);
        pnlPatientAppointmentAnalysisTableLayout.setHorizontalGroup(
            pnlPatientAppointmentAnalysisTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentAnalysisTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPatientAppointmentAnalysisTableLayout.setVerticalGroup(
            pnlPatientAppointmentAnalysisTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientAppointmentAnalysisTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlPatientAppointmentAnalysisTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlPatientAppointmentAnalysisTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnRestoreSelectedPatientFromArchive;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlPatientAppointmentAnalysisTable;
    private javax.swing.JScrollPane scrPatientAppointmentDataTable;
    private javax.swing.JTable tblArchivedPatients;
    // End of variables declaration//GEN-END:variables
}
