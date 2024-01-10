/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.non_modal_views;

import view.views.view_support_classes.models.NotificationView4ColumnTableModel;
import view.views.view_support_classes.renderers.NotificationTableKeyColorsAndLocalDateRenderer;
import view.View;
//import view.views.appontment_schedule_view.AppointmentsTableLocalDateTimeRenderer;
import controller.Descriptor;
import controller.ViewController;
import model.Notification;
import java.awt.Color;
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
public class NotificationView extends View implements ItemListener {
    private View.Viewer myViewType = null;
    private InternalFrameAdapter internalFrameAdapter = null;
    private JTable tblNotifications = null;
    private TableCellRenderer patientNotificationTableDefaultRenderer = null;
    private NotificationTableKeyColorsAndLocalDateRenderer 
            notificationTableKeyColorsAndLocalDateRenderer = null;
    

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
    
    private NotificationTableKeyColorsAndLocalDateRenderer getNotificationTableKeyColorsAndLocalDateRenderer(){
        return notificationTableKeyColorsAndLocalDateRenderer;
    }
    
    private void setNotificationTableKeyColorsAndLocalDateRenderer(NotificationTableKeyColorsAndLocalDateRenderer value){
        notificationTableKeyColorsAndLocalDateRenderer = value;
    }
 
    
    private TableCellRenderer getPatientNotificationTableDefaultRenderer(){
        return patientNotificationTableDefaultRenderer;
    }
    
    private void setNotificationTableDefaultRenderer(TableCellRenderer renderer){
        patientNotificationTableDefaultRenderer = renderer;
    }

    /**
     * 
     * @param myViewType
     * @param myController 
     * @param desktopView
     */
    public NotificationView(View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Outstanding patient notifications");
        this.setMyViewType(myViewType);
        setMyController(myController);  
        setDesktopView(desktopView);
    }
    
    @Override
    public void itemStateChanged(ItemEvent e){
        ViewController.NotificationViewControllerActionEvent request = null;
        String text = ((JRadioButton)e.getItem()).getText();
        switch(text){
            case DISPLAY_UNACTIONED_NOTIFICATIONS_TEXT:
                if (e.getStateChange() == ItemEvent.SELECTED){
                    request = ViewController.
                            NotificationViewControllerActionEvent.
                            UNACTIONED_NOTIFICATIONS_REQUEST;
                            setUITitle(UI_UNACTIONED_NOTIFICATIONS_TITLE);
                }
                            
                break;
            case DISPLAY_ALL_NOTIFICATIONS_TEXT:
                if (e.getStateChange() == ItemEvent.SELECTED){
                    request = ViewController.
                            NotificationViewControllerActionEvent.
                            NOTIFICATIONS_REQUEST;
                            setUITitle(UI_ALL_NOTIFICATIONS_TITLE);
                }
                break;
        }
        if (request!=null){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                request.toString());
            this.getMyController().actionPerformed(actionEvent);
            actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
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
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        NotificationView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            @Override  
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        NotificationView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
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
        //setViewDescriptor((Descriptor)e.getNewValue());
        ViewController.
                NotificationViewControllerPropertyChangeEvent
                propertyName = ViewController.
                        NotificationViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch (propertyName){
            case RECEIVED_PATIENT_NOTIFICATIONS:
                populatePatientNotificationTable(
                        getMyController().getDescriptor().getControllerDescription().getPatientNotifications(),false);
                setTitle(getUITitle());
                this.tblNotifications.clearSelection();
                break;
               case RECEIVED_UNACTIONED_NOTIFICATIONS:
                populatePatientNotificationTable(
                        getMyController().getDescriptor().getControllerDescription().getPatientNotifications(),true);
                setTitle(getUITitle());
                this.tblNotifications.clearSelection();
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
        initComponents();
        try{
            setVisible(true);
            setTitle(getUITitle());
            setClosable(true);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            setSize(860,520);
            
            addInternalFrameListeners();
        }
        catch (PropertyVetoException ex){
            
        }
        this.rdbDisplayUnactionedNotifications.setSelected(true);
        this.rdbDisplayAllNotifications.addItemListener(this);
        this.rdbDisplayUnactionedNotifications.addItemListener(this);
        this.tblNotifications = new JTable(new NotificationView4ColumnTableModel());
        createNotificationTable();

        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.NotificationViewControllerActionEvent.UNACTIONED_NOTIFICATIONS_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        
    }
 
    private void populatePatientNotificationTable(
            ArrayList<Notification> patientNotifications, boolean toAddRenderer){
        NotificationView4ColumnTableModel model = 
                (NotificationView4ColumnTableModel)this.tblNotifications.getModel();
        model.removeAllElements();
        Iterator<Notification> it = patientNotifications.iterator();
        while (it.hasNext()){
            ((NotificationView4ColumnTableModel)this.tblNotifications.getModel()).addElement(it.next());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    /*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        scrNotificationTable = new javax.swing.JScrollPane();
        pnColourKey = new javax.swing.JPanel();
        lblNotDueYetNotificationColour = new javax.swing.JLabel();
        lblOverdueNotification = new javax.swing.JLabel();
        lblOverdueNotificationColour = new javax.swing.JLabel();
        lblActionedNotificationColour = new javax.swing.JLabel();
        lblNotDueYetNotification = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblActionedNotification = new javax.swing.JLabel();
        pnlDisplayControls = new javax.swing.JPanel();
        rdbDisplayUnactionedNotifications = new javax.swing.JRadioButton();
        rdbDisplayAllNotifications = new javax.swing.JRadioButton();
        btnActionSelectedNotifications = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnAddNewNotification = new javax.swing.JButton();
        btnEditSelectedNotification = new javax.swing.JButton();
        btnCancelSelectedNotification = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(762, 530));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(777, 440));

        pnColourKey.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notification colour key", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lblNotDueYetNotificationColour.setBackground(new java.awt.Color(255, 255, 0));
        lblNotDueYetNotificationColour.setBackground(java.awt.Color.BLACK);
        lblNotDueYetNotificationColour.setForeground(java.awt.Color.red);
        lblNotDueYetNotificationColour.setOpaque(true);

        lblOverdueNotification.setBackground(java.awt.Color.yellow);
        lblOverdueNotification.setBackground(java.awt.Color.RED);
        lblOverdueNotification.setText(" ");
        lblOverdueNotification.setOpaque(true);

        lblOverdueNotificationColour.setBackground(java.awt.Color.yellow);
        lblOverdueNotificationColour.setText(" ");
        lblOverdueNotificationColour.setOpaque(true);
        lblOverdueNotificationColour.setBackground(java.awt.Color.BLUE);

        lblActionedNotificationColour.setBackground(java.awt.Color.yellow);
        lblActionedNotificationColour.setText(" ");
        lblActionedNotificationColour.setOpaque(true);
        lblActionedNotificationColour.setBackground(java.awt.Color.GREEN);

        lblNotDueYetNotification.setText("patient notifications not yet due");

        jLabel2.setText("patient notifications due today");

        jLabel3.setText("overdue patient notifications");

        lblActionedNotification.setText("patient notifications which have been actioned");

        javax.swing.GroupLayout pnColourKeyLayout = new javax.swing.GroupLayout(pnColourKey);
        pnColourKey.setLayout(pnColourKeyLayout);
        pnColourKeyLayout.setHorizontalGroup(
            pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnColourKeyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNotDueYetNotificationColour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblOverdueNotification, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                    .addComponent(lblOverdueNotificationColour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblActionedNotificationColour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNotDueYetNotification)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(lblActionedNotification))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        pnColourKeyLayout.setVerticalGroup(
            pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnColourKeyLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNotDueYetNotificationColour, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNotDueYetNotification, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOverdueNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOverdueNotificationColour, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblActionedNotificationColour, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblActionedNotification))
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

        javax.swing.GroupLayout pnlDisplayControlsLayout = new javax.swing.GroupLayout(pnlDisplayControls);
        pnlDisplayControls.setLayout(pnlDisplayControlsLayout);
        pnlDisplayControlsLayout.setHorizontalGroup(
            pnlDisplayControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayControlsLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(pnlDisplayControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnActionSelectedNotifications)
                    .addComponent(rdbDisplayAllNotifications)
                    .addComponent(rdbDisplayUnactionedNotifications))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        pnlDisplayControlsLayout.setVerticalGroup(
            pnlDisplayControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayControlsLayout.createSequentialGroup()
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
                .addGap(32, 32, 32)
                .addComponent(pnlDisplayControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(pnColourKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotificationTable)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotificationTable, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pnColourKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlDisplayControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        btnCancelSelectedNotification.setText("Delete selected notification");
        btnCancelSelectedNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedNotificationActionPerformed(evt);
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
                .addComponent(btnCancelSelectedNotification)
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
                    .addComponent(btnCancelSelectedNotification))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
*/
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
        if (this.tblNotifications.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "no notifications have been selected and so cannot be actioned/removed from the list");
            isError = true;
        }
        if (!isError){
            int response = JOptionPane.NO_OPTION;
            String message ="Are you sure you want to remove (action) all selected notifications from the list?";
            response = JOptionPane.showConfirmDialog(this,message, "Action selected patient notifications", JOptionPane.YES_NO_OPTION);
            if (response==JOptionPane.YES_OPTION){
                NotificationView4ColumnTableModel model = (NotificationView4ColumnTableModel)this.tblNotifications.getModel();
                ArrayList<Notification> patientNotifications = new ArrayList<>();
                int selectedRows[] = tblNotifications.getSelectedRows();
                for (int row : selectedRows){
                    Notification patientNotification = (Notification)model.getPatientNotifications().get(row);
                    patientNotifications.add(patientNotification);
                }
                getMyController().getDescriptor().getViewDescription().setPatientNotifications(patientNotifications);
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotificationViewControllerActionEvent.ACTION_NOTIFICATION_REQUEST.toString());
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
                ViewController.NotificationViewControllerActionEvent.CREATE_NOTIFICATION_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        this.tblNotifications.clearSelection();
    }//GEN-LAST:event_btnAddNewNotificationActionPerformed

    private void btnEditSelectedNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSelectedNotificationActionPerformed
        // TODO add your handling code here:
        boolean isError = false;
        if (this.tblNotifications.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "A notifification to edit has not been selected");
            isError = true;
        }
        if (!isError){
            NotificationView4ColumnTableModel model = 
                (NotificationView4ColumnTableModel)this.tblNotifications.getModel();
            getMyController().getDescriptor().getViewDescription().setPatientNotification(model.getElementAt(this.tblNotifications.getSelectedRow()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotificationViewControllerActionEvent.UPDATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            this.tblNotifications.clearSelection();
        }
        
    }//GEN-LAST:event_btnEditSelectedNotificationActionPerformed

    private void btnCancelSelectedNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelSelectedNotificationActionPerformed
        // TODO add your handling code here:
        boolean isError = false;
        if (this.tblNotifications.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "A notifification to bed cancelled has not been selected");
            isError = true;
        }
        if (!isError){
            int row = this.tblNotifications.getSelectedRow();
            NotificationView4ColumnTableModel model = 
                (NotificationView4ColumnTableModel)this.tblNotifications.getModel();
            getMyController().getDescriptor().getViewDescription().setPatientNotification(model.getElementAt(row));
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        CANCEL_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }//GEN-LAST:event_btnCancelSelectedNotificationActionPerformed

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnActionSelectedNotifications;
    private javax.swing.JButton btnAddNewNotification;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCancelSelectedNotification;
    private javax.swing.JButton btnEditSelectedNotification;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel lblNotDueYetNotification;
    private javax.swing.JLabel lblOverdueNotification;
    private javax.swing.JLabel lblActionedNotification;
    private javax.swing.JLabel lblNotDueYetNotificationColour;
    private javax.swing.JLabel lblOverdueNotificationColour;
    private javax.swing.JLabel lblActionedNotificationColour;
    private javax.swing.JPanel pnlColourKey;
    private javax.swing.JPanel pnlDisplayControls;
    private javax.swing.JPanel pnlNotificationTable;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlDisplayControlsAndColourKey;
    private javax.swing.JRadioButton rdbDisplayAllNotifications;
    private javax.swing.JRadioButton rdbDisplayUnactionedNotifications;
    private javax.swing.JScrollPane scrNotificationTable;
    // End of variables declaration
    
/*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActionSelectedNotifications;
    private javax.swing.JButton btnAddNewNotification;
    private javax.swing.JButton btnCancelSelectedNotification;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnEditSelectedNotification;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblActionedNotification;
    private javax.swing.JLabel lblActionedNotificationColour;
    private javax.swing.JLabel lblNotDueYetNotification;
    private javax.swing.JLabel lblNotDueYetNotificationColour;
    private javax.swing.JLabel lblOverdueNotification;
    private javax.swing.JLabel lblOverdueNotificationColour;
    private javax.swing.JPanel pnColourKey;
    private javax.swing.JPanel pnlDisplayControls;
    private javax.swing.JRadioButton rdbDisplayAllNotifications;
    private javax.swing.JRadioButton rdbDisplayUnactionedNotifications;
    private javax.swing.JScrollPane scrNotificationTable;
    // End of variables declaration//GEN-END:variables
*/
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
    
    private void createNotificationTable(){
        this.tblNotifications = null;
        this.tblNotifications = new JTable(new NotificationView4ColumnTableModel());
        setNotificationTableDefaultRenderer(this.tblNotifications.getDefaultRenderer(LocalDate.class));
        
        scrNotificationTable.setViewportView(this.tblNotifications);
        ViewController.setJTableColumnProperties(tblNotifications, 
                scrNotificationTable.getPreferredSize().width, 
                10,25,25,40);
        this.tblNotifications.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.tblNotifications.setAutoCreateRowSorter(false);
        setNotificationTableKeyColorsAndLocalDateRenderer(
                new NotificationTableKeyColorsAndLocalDateRenderer());
        
        lblOverdueNotificationColour.setBackground(
                getNotificationTableKeyColorsAndLocalDateRenderer()
                        .getOverdueNotificationColour());
        lblNotDueYetNotificationColour.setBackground(
                getNotificationTableKeyColorsAndLocalDateRenderer()
                        .getNotDueYetNotificationColour());
        lblActionedNotificationColour.setBackground(
                getNotificationTableKeyColorsAndLocalDateRenderer()
                        .getActionedNotificationColour());
        
        this.tblNotifications.getColumnModel().getColumn(0).setCellRenderer(getNotificationTableKeyColorsAndLocalDateRenderer());
        this.tblNotifications.getColumnModel().getColumn(1).setCellRenderer(getNotificationTableKeyColorsAndLocalDateRenderer());
        this.tblNotifications.getColumnModel().getColumn(2).setCellRenderer(getNotificationTableKeyColorsAndLocalDateRenderer());
        this.tblNotifications.getColumnModel().getColumn(3).setCellRenderer(getNotificationTableKeyColorsAndLocalDateRenderer());
    }
    

        
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        
        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlOperations = new javax.swing.JPanel();
        btnEditSelectedNotification = new javax.swing.JButton();
        btnCancelSelectedNotification = new javax.swing.JButton();
        btnActionSelectedNotifications = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnAddNewNotification = new javax.swing.JButton();
        pnlDisplayControlsAndColourKey = new javax.swing.JPanel();
        pnlColourKey = new javax.swing.JPanel();
        lblNotDueYetNotificationColour = new javax.swing.JLabel();
        lblOverdueNotificationColour = new javax.swing.JLabel();
        lblActionedNotificationColour = new javax.swing.JLabel();
        lblNotDueYetNotification = new javax.swing.JLabel();
        lblOverdueNotification = new javax.swing.JLabel();
        lblActionedNotification = new javax.swing.JLabel();
        pnlDisplayControls = new javax.swing.JPanel();
        rdbDisplayUnactionedNotifications = new javax.swing.JRadioButton();
        rdbDisplayAllNotifications = new javax.swing.JRadioButton();
        pnlNotificationTable = new javax.swing.JPanel();
        scrNotificationTable = new javax.swing.JScrollPane();

        pnlOperations.setBorder(javax.swing.BorderFactory.createTitledBorder("Operations"));

        btnEditSelectedNotification.setText("<html><center>Update</center><center>selected</center><center>notification</center></html>");

        btnCancelSelectedNotification.setText("<html><center>Cancel</center><center>selected</center><center>notification</center></html>");

        btnActionSelectedNotifications.setText("<html><center>Action</center><center>selected</center><center>notification(s)</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        btnAddNewNotification.setText("<html><center>Add</center><center>notification</center>");
        
        pnlColourKey.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notification colour key", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        
        lblNotDueYetNotificationColour.setText("");
        lblNotDueYetNotificationColour.setOpaque(true);

        lblOverdueNotificationColour.setText(" ");
        lblOverdueNotificationColour.setOpaque(true);
        
        

        lblActionedNotificationColour.setText(" ");
        lblActionedNotificationColour.setOpaque(true);
        

        lblNotDueYetNotification.setText("notification not yet due");
        lblOverdueNotification.setText("notification overdue");
        lblActionedNotification.setText("notification has been actioned");
        
        buttonGroup1.add(rdbDisplayUnactionedNotifications);
        rdbDisplayUnactionedNotifications.setSelected(true);
        rdbDisplayUnactionedNotifications.setText(DISPLAY_UNACTIONED_NOTIFICATIONS_TEXT);

        buttonGroup1.add(rdbDisplayAllNotifications);
        rdbDisplayAllNotifications.setText(DISPLAY_ALL_NOTIFICATIONS_TEXT);
        
        scrNotificationTable.setPreferredSize(new java.awt.Dimension(645, 750));
        
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });


        btnAddNewNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewNotificationActionPerformed(evt);
            }
        });


        btnEditSelectedNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSelectedNotificationActionPerformed(evt);
            }
        });


        btnCancelSelectedNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelSelectedNotificationActionPerformed(evt);
            }
        });

//<editor-fold defaultstate="collapsed" desc="Colour key panel layout">
        javax.swing.GroupLayout pnlColourKeyLayout = new javax.swing.GroupLayout(pnlColourKey);
        pnlColourKey.setLayout(pnlColourKeyLayout);
        pnlColourKeyLayout.setHorizontalGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlColourKeyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNotDueYetNotificationColour, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(lblOverdueNotificationColour, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(lblActionedNotificationColour, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNotDueYetNotification)
                    .addComponent(lblOverdueNotification)
                    .addComponent(lblActionedNotification))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        pnlColourKeyLayout.setVerticalGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlColourKeyLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlColourKeyLayout.createSequentialGroup()
                        .addGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNotDueYetNotificationColour, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNotDueYetNotification, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(46, 46, 46))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlColourKeyLayout.createSequentialGroup()
                        .addGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblOverdueNotificationColour, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblOverdueNotification))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblActionedNotificationColour, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblActionedNotification))))
                .addGap(26, 26, 26))
        );
//</editor-fold> 
//<editor-fold defaultstate="collapsed" desc="Display controls panel layout">
        javax.swing.GroupLayout pnlDisplayControlsLayout = new javax.swing.GroupLayout(pnlDisplayControls);
        pnlDisplayControls.setLayout(pnlDisplayControlsLayout);
        pnlDisplayControlsLayout.setHorizontalGroup(
            pnlDisplayControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayControlsLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlDisplayControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbDisplayAllNotifications)
                    .addComponent(rdbDisplayUnactionedNotifications))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        pnlDisplayControlsLayout.setVerticalGroup(
            pnlDisplayControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayControlsLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(rdbDisplayUnactionedNotifications)
                .addGap(18, 18, 18)
                .addComponent(rdbDisplayAllNotifications)
                .addGap(24, 24, 24))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Display controls & Colour key panel layout">
        javax.swing.GroupLayout pnlDisplayControlsAndColourKeyLayout = new javax.swing.GroupLayout(pnlDisplayControlsAndColourKey);
        pnlDisplayControlsAndColourKey.setLayout(pnlDisplayControlsAndColourKeyLayout);
        pnlDisplayControlsAndColourKeyLayout.setHorizontalGroup(pnlDisplayControlsAndColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayControlsAndColourKeyLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlDisplayControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30,30,30)
                .addComponent(pnlColourKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30,30,30))
        );
        pnlDisplayControlsAndColourKeyLayout.setVerticalGroup(pnlDisplayControlsAndColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDisplayControlsAndColourKeyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDisplayControlsAndColourKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlColourKey, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlDisplayControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Notification table panel layout">
        javax.swing.GroupLayout pnlNotificationTableLayout = new javax.swing.GroupLayout(pnlNotificationTable);
        pnlNotificationTable.setLayout(pnlNotificationTableLayout);
        pnlNotificationTableLayout.setHorizontalGroup(pnlNotificationTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotificationTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotificationTable, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNotificationTableLayout.setVerticalGroup(pnlNotificationTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotificationTableLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(scrNotificationTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Operations panel layout">
        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnActionSelectedNotifications)
                    .addComponent(btnCloseView)
                    .addComponent(btnCancelSelectedNotification)
                    .addComponent(btnEditSelectedNotification)
                    .addComponent(btnAddNewNotification/*, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE*/))
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        pnlOperationsLayout.setVerticalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddNewNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnEditSelectedNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnCancelSelectedNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnActionSelectedNotifications, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
//</editor-fold> 
//<editor-fold defaultstate="collapsed" desc="View layout">
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlNotificationTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDisplayControlsAndColourKey, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlNotificationTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20,20,20)
                        .addComponent(pnlDisplayControlsAndColourKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)))//.addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                
                .addContainerGap())
        );
//</editor-fold>
        pack();
    }// </editor-fold>                       

}
