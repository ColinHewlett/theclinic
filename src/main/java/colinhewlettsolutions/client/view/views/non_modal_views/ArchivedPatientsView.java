/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.ArchivedPatientsViewController;
import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import colinhewlettsolutions.client.controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.non_entity.Captions;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.support_classes.models.ArchivedPatientsTableModel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author colin
 */
public class ArchivedPatientsView extends View 
        implements ActionListener, 
                   ListSelectionListener{ //View implenents PropertyChangeListener, and an empty proprtyChange method
     
    enum BorderTitles{
        ACTIONS,
        ARCHIVED_PATIENTS
    }
    
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
        ArchivedPatientsViewController.Actions actionCommand = ArchivedPatientsViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case PATIENT_RESTORE_REQUEST ->{
                patient = new Patient();
                patient.set(getSelectedRows());
                if (!patient.get().isEmpty()){
                    getMyController().getDescriptor().getViewDescription().setProperty(Properties.PATIENT, patient);
                    doSendActionEvent(actionCommand);
                }
                break;
            }
            case VIEW_CHANGED_NOTIFICATION ->{
                doSendActionEvent(actionCommand);
            }
            case VIEW_CLOSE_NOTIFICATION ->{
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
            }
        }
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ArchivedPatientsViewController.Properties propertyName =
                ArchivedPatientsViewController.Properties.valueOf(e.getPropertyName());
        switch(propertyName){
            case ARCHIVED_PATIENTS_RECEIVED ->{
                populateArchivedPatientsTable(
                        (Patient)getMyController().getDescriptor().
                                getControllerDescription().getProperty(Properties.PATIENT));
                //doSendActionEvent(ArchivedPatientsViewController.Actions.VIEW_CHANGED_NOTIFICATION);
                break;
            }
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
        setScheduleTitledBorderSettings();
        
        this.btnRestoreSelectedPatientFromArchive.setActionCommand(ArchivedPatientsViewController.Actions.PATIENT_RESTORE_REQUEST.toString());
        this.btnCloseView.setActionCommand(ArchivedPatientsViewController.Actions.VIEW_CLOSE_NOTIFICATION.toString());
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
        
        //doSendActionEvent(ViewController.ArchivedPatientsViewControllerActionEvent.ARCHIVED_PATIENT_REQUEST);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ArchivedPatientsViewController.Actions.ARCHIVED_PATIENTS_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
        
    }
    private String tableTitle = null;
    private void setTableTitle(String value){
        tableTitle = value;
    }
    private String getTableTitle(){
        return tableTitle;
    }
    
    private void setScheduleTitledBorderSettings(){
        setBorderTitles(BorderTitles.ACTIONS,"Actions");
        setBorderTitles(BorderTitles.ARCHIVED_PATIENTS,"Archived patients");
        setTableTitle("Archived patients");
    }
    
    private void setBorderTitles(BorderTitles borderTitles, String caption){
        JPanel panel = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case ACTIONS:
                panel = this.pnlActions;
                isPanelBackgroundDefault = false;
                break;
            case ARCHIVED_PATIENTS:
                panel = this.pnlArchivedPatients;
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
                doSendActionEvent(ArchivedPatientsViewController.Actions.VIEW_CLOSE_NOTIFICATION); 
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                doSendActionEvent(ArchivedPatientsViewController.Actions.VIEW_ACTIVATED_NOTIFICATION); 
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void doSendActionEvent(ArchivedPatientsViewController.Actions actionCommand){
        //if (getMyController().getDescriptor().getViewDescription().getProperty(Properties.PATIENT) != null){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                actionCommand.toString());
            getMyController().actionPerformed(actionEvent);
        //}
        
    }
    
    private void populateArchivedPatientsTable(Patient patient){
        ArchivedPatientsTableModel model = 
                (ArchivedPatientsTableModel)tblArchivedPatients.getModel();
        model.removeAllElements();
        model.setData(patient.get());
        ViewController.setJTableColumnProperties(tblArchivedPatients, this.scrPatientAppointmentDataTable.getPreferredSize().width, 100);
        TitledBorder titledBorder = (TitledBorder)pnlArchivedPatients.getBorder();
        titledBorder.setTitle(getTableTitle() + " (" + patient.get().size() + ")");
        this.pnlArchivedPatients.repaint();
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
        pnlArchivedPatients = new javax.swing.JPanel();
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

        pnlArchivedPatients.setBorder(javax.swing.BorderFactory.createTitledBorder("Archived patients"));

        scrPatientAppointmentDataTable.setPreferredSize(new java.awt.Dimension(905, 402));

        tblArchivedPatients.setModel(new ArchivedPatientsTableModel()
        );
        scrPatientAppointmentDataTable.setViewportView(tblArchivedPatients);

        javax.swing.GroupLayout pnlArchivedPatientsLayout = new javax.swing.GroupLayout(pnlArchivedPatients);
        pnlArchivedPatients.setLayout(pnlArchivedPatientsLayout);
        pnlArchivedPatientsLayout.setHorizontalGroup(
            pnlArchivedPatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlArchivedPatientsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlArchivedPatientsLayout.setVerticalGroup(
            pnlArchivedPatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlArchivedPatientsLayout.createSequentialGroup()
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
                .addComponent(pnlArchivedPatients, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlArchivedPatients, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnRestoreSelectedPatientFromArchive;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlArchivedPatients;
    private javax.swing.JScrollPane scrPatientAppointmentDataTable;
    private javax.swing.JTable tblArchivedPatients;
    // End of variables declaration//GEN-END:variables
}
