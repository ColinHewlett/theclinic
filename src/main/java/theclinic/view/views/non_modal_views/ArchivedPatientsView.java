/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.non_modal_views;

import theclinic.controller.ArchivedPatientsViewController;
import theclinic.controller.SystemDefinition.Properties;
import theclinic.controller.ViewController;
import theclinic.model.entity.Patient;
import theclinic.model.entity.PatientAppointmentData;
import theclinic.model.non_entity.Captions;
import theclinic.controller.SystemDefinition;
import theclinic.view.View;
import theclinic.view.support_classes.groupable_table_header_support.GroupableTableHeader;
import theclinic.view.support_classes.table_models.ArchivedPatientsTableModel;
import theclinic.view.support_classes.table_renderers.AppointmentsTableLocalDateRenderer;
import theclinic.view.support_classes.table_renderers.TableLocalDateCentredRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

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
        PatientAppointmentData pad = null;
        ArchivedPatientsViewController.Actions actionCommand = ArchivedPatientsViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case PATIENT_RESTORE_REQUEST ->{
                pad = new PatientAppointmentData();
                pad.set(getSelectedRows());
                if (!pad.get().isEmpty()){
                    getMyController().getDescriptor().getViewDescription().setProperty(Properties.PATIENT_APPOINTMENT_DATA, pad);
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
                        (PatientAppointmentData)getMyController().getDescriptor().
                                getControllerDescription().getProperty(Properties.PATIENT_APPOINTMENT_DATA));
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
        configureTable();       
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
        //this.tblArchivedPatients.setTableHeader(null);
        
        //doSendActionEvent(ViewController.ArchivedPatientsViewControllerActionEvent.ARCHIVED_PATIENT_REQUEST);
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ArchivedPatientsViewController.Actions.ARCHIVED_PATIENTS_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
        
    }
    
    private void configureTable(){
        ArchivedPatientsTableModel model = new ArchivedPatientsTableModel();
        tblArchivedPatients.setModel(model);
        //tblArchivedPatients.setAutoCreateColumnsFromModel(true);
        //tblArchivedPatients.createDefaultColumnsFromModel();
        scrPatientAppointmentDataTable.setViewportView(tblArchivedPatients);
        //scrPatientAppointmentDataTable.setColumnHeaderView(tblArchivedPatients.getTableHeader());
        //set table renderer to render LocalDate data as centred snd formatted as 'dd/MM/yyyy'
        this.tblArchivedPatients.setDefaultRenderer(LocalDate.class, new TableLocalDateCentredRenderer());
        //set column sizes (assumes a fixed sized table width
        ViewController.setJTableColumnProperties(tblArchivedPatients, 294, 70,30);
        //set header height to accommodate dual line captions
        javax.swing.table.JTableHeader header = tblArchivedPatients.getTableHeader();
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 36));
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
    
    private ArrayList<PatientAppointmentData> pads = new ArrayList<>();
    private void setSelectedRows(int[] value){
        pads = new ArrayList<>();
        if (value!=null){
            if (value.length>0){
                for (int selectedRow = 0; value.length-1 >= selectedRow; selectedRow++){
                    ArchivedPatientsTableModel model = (ArchivedPatientsTableModel)tblArchivedPatients.getModel();
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
    
    private void populateArchivedPatientsTable(PatientAppointmentData pad){
        ArchivedPatientsTableModel model = 
                (ArchivedPatientsTableModel)tblArchivedPatients.getModel();
        model.removeAllElements();
        model.setData(pad.get());
        
        TitledBorder titledBorder = (TitledBorder)pnlArchivedPatients.getBorder();
        titledBorder.setTitle(getTableTitle() + " (" + pad.get().size() + ")");
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
        tblArchivedPatients = new javax.swing.JTable();

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
                .addComponent(btnRestoreSelectedPatientFromArchive, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlArchivedPatients.setBorder(javax.swing.BorderFactory.createTitledBorder("Archived patients"));

        tblArchivedPatients.setModel(new javax.swing.table.DefaultTableModel(
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
        scrPatientAppointmentDataTable.setViewportView(tblArchivedPatients);

        javax.swing.GroupLayout pnlArchivedPatientsLayout = new javax.swing.GroupLayout(pnlArchivedPatients);
        pnlArchivedPatients.setLayout(pnlArchivedPatientsLayout);
        pnlArchivedPatientsLayout.setHorizontalGroup(
            pnlArchivedPatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
            .addGroup(pnlArchivedPatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArchivedPatientsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        pnlArchivedPatientsLayout.setVerticalGroup(
            pnlArchivedPatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
            .addGroup(pnlArchivedPatientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlArchivedPatientsLayout.createSequentialGroup()
                    .addComponent(scrPatientAppointmentDataTable, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pnlArchivedPatients, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
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
