/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.view.views;

import clinicpms.view.views.view_support_classes.models.PatientNotificationView4ColumnTableModel;
import clinicpms.view.views.view_support_classes.renderers.PatientNotificationTableOverdueLocalDateRenderer;
import clinicpms.view.View;
//import clinicpms.view.views.appontment_schedule_view.AppointmentsTableLocalDateTimeRenderer;
import clinicpms.controller.Descriptor;
import clinicpms.controller.ViewController;
import clinicpms.model.Notification;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class PatientNotificationView extends View implements ItemListener {
    private View.Viewer myViewType = null;
    private Descriptor entityDescriptor = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private JTable tblPatientNotifications = null;
    private TableCellRenderer patientNotificationTableDefaultRenderer = null;
    

    private final String DISPLAY_UNACTIONED_NOTIFICATIONS_TEXT = "display unactioned notifications";
    private final String DISPLAY_ALL_NOTIFICATIONS_TEXT = "display all notifications";
    private final String UI_UNACTIONED_NOTIFICATIONS_TITLE = "Outstanding patient notifications";
    private final String UI_ALL_NOTIFICATIONS_TITLE = "Patient notifications";
    
    private String UITitle = UI_UNACTIONED_NOTIFICATIONS_TITLE;
    private PatientNotificationViewListState viewListState = null;

    enum PatientNotificationViewListState {ALL_NOTIFICATION_STATE, UNACTIONED_NOTIFICATION_STATE};
    
    private PatientNotificationViewListState getViewListState(){
        return viewListState;
    }
    
    private void setViewListState(PatientNotificationViewListState state){
        viewListState = state;
    }
    
    private String getUITitle(){
        return UITitle;
    }
    private void setUITitle(String title){
        UITitle = title;
    }
   
    private TableCellRenderer getPatientNotificationTableDefaultRenderer(){
        return patientNotificationTableDefaultRenderer;
    }
    
    private void setPatientNotificationTableDefaultRenderer(TableCellRenderer renderer){
        patientNotificationTableDefaultRenderer = renderer;
    }

    /**
     * 
     * @param myViewType
     * @param myController
     * @param value 
     */
    public PatientNotificationView(View.Viewer myViewType, 
            ActionListener myController, 
            Descriptor value, JDesktopPane desktop) {
        super("Outstanding patient notifications");
        this.setMyViewType(myViewType);
        setMyController(myController);
        setViewDescriptor(value);
        initComponents(); 
        //addInternalFrameActivatedListener();
        desktop.add(this);
        centreViewOnDesktop(desktop,this);
        
    }
    
    @Override
    public void itemStateChanged(ItemEvent e){
        ViewController.PatientNotificationViewControllerActionEvent request = null;
        String text = ((JRadioButton)e.getItem()).getText();
        switch(text){
            case DISPLAY_UNACTIONED_NOTIFICATIONS_TEXT:
                if (e.getStateChange() == ItemEvent.SELECTED)
                    request = ViewController.
                            PatientNotificationViewControllerActionEvent.
                            UNACTIONED_PATIENT_NOTIFICATIONS_REQUEST;
                            setUITitle(UI_UNACTIONED_NOTIFICATIONS_TITLE);
                            
                break;
            case DISPLAY_ALL_NOTIFICATIONS_TEXT:
                if (e.getStateChange() == ItemEvent.SELECTED)
                    request = ViewController.
                            PatientNotificationViewControllerActionEvent.
                            PATIENT_NOTIFICATIONS_REQUEST;
                            setUITitle(UI_ALL_NOTIFICATIONS_TITLE);
                break;
        }
        if (request!=null){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                request.toString());
            this.getMyController().actionPerformed(actionEvent);
            actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientNotificationViewControllerActionEvent.
                        VIEW_CHANGED_NOTIFICATION.toString());
            this.getMyController().actionPerformed(actionEvent);
            
        }
    }
    
    /**
     * Establish an InternalFrameListener for when the view is closed 
     * Setting DISPOSE_ON_CLOSE action when the window "X" is clicked, fires
     * InternalFrameEvent.INTERNAL_FRAME_CLOSED event for the listener to let 
     * the view controller know what's happening
     */
    @Override
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientNotificationView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientNotificationViewControllerActionEvent.
                                VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            @Override  
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientNotificationView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientNotificationViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                Object test = getMyController();
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    /**
     * Method processes the PropertyChangeEvent its received from the view
     * controller
     * @param e PropertyChangeEvent 
     */
    @Override
    public void propertyChange(PropertyChangeEvent e){
        setViewDescriptor((Descriptor)e.getNewValue());
        ViewController.
                PatientNotificationViewControllerPropertyChangeEvent
                propertyName = ViewController.
                        PatientNotificationViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch (propertyName){
            case RECEIVED_PATIENT_NOTIFICATIONS:
                populatePatientNotificationTable(
                        getViewDescriptor().getControllerDescription().getPatientNotifications(),false);
                setTitle(getUITitle());
                this.tblPatientNotifications.clearSelection();
                break;
               case RECEIVED_UNACTIONED_NOTIFICATIONS:
                populatePatientNotificationTable(
                        getViewDescriptor().getControllerDescription().getPatientNotifications(),true);
                setTitle(getUITitle());
                this.tblPatientNotifications.clearSelection();
                this.rdbDisplayUnactionedNotifications.setSelected(true);
                break;
        }
    }
    
    /**
     * class assumes this method will be called by the controller; and initialises
     * -- window properties
     * -- unactioned notifications for display
     * -- radio button event listeners 
     * -- notifications table properties
     * -- patient selector control from collection of patients stored in the EntutyDsecriptor
     * -- sends controller request for unactioned notifications on the system
     * 
     */
    @Override
    public void initialiseView(){
        try{
            setVisible(true);
            setTitle(getUITitle());
            setClosable(true);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            setSize(800,530);
            Dimension test1 = this.getPreferredSize();
            test1 = test1;
        }
        catch (PropertyVetoException ex){
            
        }
        this.rdbDisplayUnactionedNotifications.setSelected(true);
        this.rdbDisplayAllNotifications.addItemListener(this);
        this.rdbDisplayUnactionedNotifications.addItemListener(this);
        this.tblPatientNotifications = new JTable(new PatientNotificationView4ColumnTableModel());
        createPatientNotificationTable();
        /*
        setPatientNotificationTableDefaultRenderer(this.tblPatientNotifications.getDefaultRenderer(LocalDate.class));
        scrPatientNotificationView.setViewportView(this.tblPatientNotifications);
        ViewController.setJTableColumnProperties(
                tblPatientNotifications, 
                scrPatientNotificationView.getPreferredSize().width, 
                12,23,15,50);
        this.tblPatientNotifications.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //this.tblPatientNotifications.setAutoCreateRowSorter(true);
        */
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientNotificationViewControllerActionEvent.UNACTIONED_PATIENT_NOTIFICATIONS_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        
    }
 
    private void populatePatientNotificationTable(
            ArrayList<Notification> patientNotifications, boolean toAddRenderer){
        createPatientNotificationTable();
        Iterator<Notification> it = patientNotifications.iterator();
        while (it.hasNext()){
            ((PatientNotificationView4ColumnTableModel)this.tblPatientNotifications.getModel()).addElement(it.next());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        scrPatientNotificationView = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        lblBlack = new javax.swing.JLabel();
        lblRed = new javax.swing.JLabel();
        lblBlue = new javax.swing.JLabel();
        lblGreen = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        rdbDisplayUnactionedNotifications = new javax.swing.JRadioButton();
        rdbDisplayAllNotifications = new javax.swing.JRadioButton();
        btnActionSelectedNotifications = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnAddNewNotification = new javax.swing.JButton();
        btnEditSelectedNotification = new javax.swing.JButton();
        btnDeletePatientNotification = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(762, 530));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(777, 440));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notification colour key", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblBlack.setBackground(new java.awt.Color(255, 255, 0));
        lblBlack.setBackground(java.awt.Color.BLACK);
        lblBlack.setForeground(java.awt.Color.red);
        lblBlack.setOpaque(true);

        lblRed.setBackground(java.awt.Color.yellow);
        lblRed.setBackground(java.awt.Color.RED);
        lblRed.setText(" ");
        lblRed.setOpaque(true);

        lblBlue.setBackground(java.awt.Color.yellow);
        lblBlue.setText(" ");
        lblBlue.setOpaque(true);
        lblBlue.setBackground(java.awt.Color.BLUE);

        lblGreen.setBackground(java.awt.Color.yellow);
        lblGreen.setText(" ");
        lblGreen.setOpaque(true);
        lblGreen.setBackground(java.awt.Color.GREEN);

        jLabel1.setText("patient notifications not yet due");

        jLabel2.setText("patient notifications due today");

        jLabel3.setText("overdue patient notifications");

        jLabel4.setText("patient notifications which have been actioned");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBlack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRed, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(lblBlue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBlack, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRed, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBlue, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGreen, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        buttonGroup1.add(rdbDisplayUnactionedNotifications);
        rdbDisplayUnactionedNotifications.setSelected(true);
        rdbDisplayUnactionedNotifications.setText(DISPLAY_UNACTIONED_NOTIFICATIONS_TEXT);

        buttonGroup1.add(rdbDisplayAllNotifications);
        rdbDisplayAllNotifications.setText(DISPLAY_ALL_NOTIFICATIONS_TEXT);

        btnActionSelectedNotifications.setText("Action selected notifications");
        btnActionSelectedNotifications.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActionSelectedNotificationsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnActionSelectedNotifications)
                    .addComponent(rdbDisplayAllNotifications)
                    .addComponent(rdbDisplayUnactionedNotifications))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbDisplayUnactionedNotifications)
                .addGap(18, 18, 18)
                .addComponent(rdbDisplayAllNotifications)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnActionSelectedNotifications)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrPatientNotificationView, javax.swing.GroupLayout.PREFERRED_SIZE, 749, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrPatientNotificationView, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnCloseView.setText("Close view");
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        btnAddNewNotification.setText("Add a new notification");
        btnAddNewNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewNotificationActionPerformed(evt);
            }
        });

        btnEditSelectedNotification.setText("Edit selected notification");
        btnEditSelectedNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSelectedNotificationActionPerformed(evt);
            }
        });

        btnDeletePatientNotification.setText("Delete selected notification");
        btnDeletePatientNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePatientNotificationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnAddNewNotification)
                .addGap(71, 71, 71)
                .addComponent(btnEditSelectedNotification)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDeletePatientNotification)
                .addGap(56, 56, 56)
                .addComponent(btnCloseView)
                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCloseView)
                    .addComponent(btnAddNewNotification)
                    .addComponent(btnEditSelectedNotification)
                    .addComponent(btnDeletePatientNotification))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }//GEN-LAST:event_btnCloseViewActionPerformed

    /**
     * ViewDescription to action or remove selected notifications from the list of unactioned notifications is sent to the View Controller
 -- an ArrayList<PatientNotification> is included in the EntityDescription::setPatientNotifications() method which identifies the selected notifications
     * @param evt 
     */
    private void btnActionSelectedNotificationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionSelectedNotificationsActionPerformed
        boolean isError = false;
        if (this.tblPatientNotifications.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "no notifications have been selected and so cannot be actioned/removed from the list");
            isError = true;
        }
        if (!isError){
            int response = JOptionPane.NO_OPTION;
            String message ="Are you sure you want to remove (action) all selected notifications from the list?";
            response = JOptionPane.showConfirmDialog(this,message, "Action selected patient notifications", JOptionPane.YES_NO_OPTION);
            if (response==JOptionPane.YES_OPTION){
                PatientNotificationView4ColumnTableModel model = (PatientNotificationView4ColumnTableModel)this.tblPatientNotifications.getModel();
                ArrayList<Notification> patientNotifications = new ArrayList<>();
                int selectedRows[] = tblPatientNotifications.getSelectedRows();
                for (int row : selectedRows){
                    Notification patientNotification = (Notification)model.getPatientNotifications().get(row);
                    patientNotifications.add(patientNotification);
                }
                getViewDescriptor().getViewDescription().setPatientNotifications(patientNotifications);
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientNotificationViewControllerActionEvent.ACTION_PATIENT_NOTIFICATION_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent); 
                message = "selected notification has been actioned";
                JOptionPane.showMessageDialog(this,message);
            }
        }
         
    }//GEN-LAST:event_btnActionSelectedNotificationsActionPerformed

    private void btnAddNewNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewNotificationActionPerformed
        // TODO add your handling code here:
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientNotificationViewControllerActionEvent.CREATE_PATIENT_NOTIFICATION_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        this.tblPatientNotifications.clearSelection();
    }//GEN-LAST:event_btnAddNewNotificationActionPerformed

    private void btnEditSelectedNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSelectedNotificationActionPerformed
        // TODO add your handling code here:
        boolean isError = false;
        if (this.tblPatientNotifications.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "A notifification to edit has not been selected");
            isError = true;
        }
        if (!isError){
            PatientNotificationView4ColumnTableModel model = 
                (PatientNotificationView4ColumnTableModel)this.tblPatientNotifications.getModel();
            getViewDescriptor().getViewDescription().setPatientNotification(
                    model.getElementAt(this.tblPatientNotifications.getSelectedRow()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientNotificationViewControllerActionEvent.UPDATE_PATIENT_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            this.tblPatientNotifications.clearSelection();
        }
        
    }//GEN-LAST:event_btnEditSelectedNotificationActionPerformed

    private void btnDeletePatientNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePatientNotificationActionPerformed
        // TODO add your handling code here:
        boolean isError = false;
        if (this.tblPatientNotifications.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "A notifification to delete has not been selected");
            isError = true;
        }
        if (!isError){
            int row = this.tblPatientNotifications.getSelectedRow();
            PatientNotificationView4ColumnTableModel model = 
                (PatientNotificationView4ColumnTableModel)this.tblPatientNotifications.getModel();
            getViewDescriptor().getViewDescription().setPatientNotification(model.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientNotificationViewControllerActionEvent.
                        DELETE_PATIENT_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_btnDeletePatientNotificationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActionSelectedNotifications;
    private javax.swing.JButton btnAddNewNotification;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeletePatientNotification;
    private javax.swing.JButton btnEditSelectedNotification;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblBlack;
    private javax.swing.JLabel lblBlue;
    private javax.swing.JLabel lblGreen;
    private javax.swing.JLabel lblRed;
    private javax.swing.JRadioButton rdbDisplayAllNotifications;
    private javax.swing.JRadioButton rdbDisplayUnactionedNotifications;
    private javax.swing.JScrollPane scrPatientNotificationView;
    // End of variables declaration//GEN-END:variables

    private void centreViewOnDesktop(Container desktopView, JInternalFrame view){
        //Insets insets = desktopView.getInsets();
        Dimension deskTopViewDimension = desktopView.getSize();
        Dimension myViewDimension = view.getPreferredSize();
        /*
        view.setLocation(new Point(
                (int)(deskTopViewDimension.getWidth() - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()-insets.top) - myViewDimension.getHeight())/2));
        */
        Point point = new Point(
                (int)((deskTopViewDimension.getWidth()) - (myViewDimension.getWidth()))/2,
                (int)((deskTopViewDimension.getHeight()) - myViewDimension.getHeight())/2);
        
        view.setLocation(point);
        System.out.println("Location = " + point);
        System.out.println("Desktop size = " + desktopView.getSize());
        System.out.println("Internal frame size = " + view.getSize());
        System.out.println("2 x point x = " + desktopView.getWidth()+ "-" + view.getWidth());
    }
    
    private void createPatientNotificationTable(){
        this.tblPatientNotifications = null;
        this.tblPatientNotifications = new JTable(new PatientNotificationView4ColumnTableModel());
        setPatientNotificationTableDefaultRenderer(this.tblPatientNotifications.getDefaultRenderer(LocalDate.class));
        PatientNotificationTableOverdueLocalDateRenderer renderer = null;
        scrPatientNotificationView.setViewportView(this.tblPatientNotifications);
        ViewController.setJTableColumnProperties(
                tblPatientNotifications, 
                scrPatientNotificationView.getPreferredSize().width, 
                12,30,30,120);
        this.tblPatientNotifications.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.tblPatientNotifications.setAutoCreateRowSorter(false);
        renderer = new PatientNotificationTableOverdueLocalDateRenderer();
            this.tblPatientNotifications.getColumnModel().getColumn(0).setCellRenderer(renderer);
            this.tblPatientNotifications.getColumnModel().getColumn(1).setCellRenderer(renderer);
            this.tblPatientNotifications.getColumnModel().getColumn(2).setCellRenderer(renderer);
            this.tblPatientNotifications.getColumnModel().getColumn(3).setCellRenderer(renderer);
        PatientNotificationView4ColumnTableModel model = 
                (PatientNotificationView4ColumnTableModel)this.tblPatientNotifications.getModel();
        model.removeAllElements();
    }
}
