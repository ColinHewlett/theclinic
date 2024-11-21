/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import view.View;
import model.entity.Notification;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.non_entity.SystemDefinition;
import view.view_support_classes.models.PatientNotificationViewTableModel;
import view.view_support_classes.renderers.PatientNotificationTableLocalDateRenderer;


/**
 *
 * @author colin
 */
public class PatientNotificationView extends View implements ActionListener,
                                                             TableModelListener,
                                                             ListSelectionListener,
                                                             PropertyChangeListener{

    enum Action{
        REQUEST_ALL_NOTIFICATIONS,
        REQUEST_CANCEL_NOTIFICATION,
        REQUEST_CLOSE_VIEW,
        REQUEST_CREATE_NOTIFICATION,
        REQUEST_UNACTIONED_NOTIFICATIONS,
        REQUEST_UPDATE_NOTIFICATION
    }
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public PatientNotificationView(View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Outstanding patient notifications");
        this.setMyViewType(myViewType);
        setMyController(myController);  
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ViewController.NotificationViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CREATE_NOTIFICATION:
                doCreateNotification();
                break;
            case REQUEST_ALL_NOTIFICATIONS:
                this.doReadAllNotifications();
                break;
            case REQUEST_CANCEL_NOTIFICATION:
                this.doCancelNotification();
                break;
            case REQUEST_CLOSE_VIEW:
                this.doCloseView();
                break;
            case REQUEST_UNACTIONED_NOTIFICATIONS:
                this.doReadUnactionedNotifications();
                break;
            case REQUEST_UPDATE_NOTIFICATION:
                doUpdateNotification();
                break;
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.NotificationViewControllerPropertyChangeEvent
                propertyName = ViewController.NotificationViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        switch (propertyName){
            case RECEIVED_PATIENT_NOTIFICATIONS:
                populatePatientNotificationTable(
                        getMyController().getDescriptor().getControllerDescription().getPatientNotifications());
                setTitle(UI_ALL_NOTIFICATIONS_TITLE);
                this.tblNotifications.clearSelection();
                break;
            case RECEIVED_UNACTIONED_NOTIFICATIONS:
                populatePatientNotificationTable(
                        getMyController().getDescriptor().getControllerDescription().getPatientNotifications());
                setTitle(UI_UNACTIONED_NOTIFICATIONS_TITLE);
                this.tblNotifications.clearSelection();
                this.rdbDisplayUnactionedNotifications.setSelected(true);
                break;
        }
    }  
    
    @Override
    public void tableChanged(TableModelEvent e){
        int row = e.getFirstRow();
        int column = e.getColumn();
        if(column!=-1){
            PatientNotificationViewTableModel model =  
                    (PatientNotificationViewTableModel)e.getSource();
            Boolean value = (Boolean)model.getValueAt(row, column);
            Notification notification = model.getElementAt(row);
            notification.setIsActioned(value);
            getMyController().getDescriptor().getViewDescription().setPatientNotification(notification);
            //tblAppointments.clearSelection();

            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        ACTION_NOTIFICATION_REQUEST.toString());
            getMyController().actionPerformed(actionEvent);
            this.tblNotifications.clearSelection();
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                this.btnEditSelectedNotification.setEnabled(true);
                this.btnCancelSelectedNotification.setEnabled(true);
                this.btnAddNewNotification.setEnabled(false);
                int selectedRow = this.tblNotifications.getSelectedRow();
                PatientNotificationViewTableModel model = 
                        (PatientNotificationViewTableModel)tblNotifications.getModel();
                Notification notification = model.getElementAt(selectedRow);
                getMyController().getDescriptor().getViewDescription()
                        .setPatientNotification(notification);
            }else{
                this.btnEditSelectedNotification.setEnabled(false);
                this.btnCancelSelectedNotification.setEnabled(false);
                this.btnAddNewNotification.setEnabled(true);
            }
    }
    
    private final String UI_UNACTIONED_NOTIFICATIONS_TITLE = "Outstanding patient notifications";
    private final String UI_ALL_NOTIFICATIONS_TITLE = "All patient notifications";
    public void initialiseView(){
        initComponents();
        
        buttonGroup1.add(this.rdbDisplayAllOptions);
        buttonGroup1.add(this.rdbDisplayUnactionedNotifications);
        rdbDisplayAllOptions.setActionCommand(Action.REQUEST_ALL_NOTIFICATIONS.toString());
        rdbDisplayUnactionedNotifications.setActionCommand(Action.REQUEST_UNACTIONED_NOTIFICATIONS.toString());
        rdbDisplayAllOptions.addActionListener(this);
        rdbDisplayUnactionedNotifications.addActionListener(this);
        rdbDisplayUnactionedNotifications.setSelected(true);
        setUITitle(UI_UNACTIONED_NOTIFICATIONS_TITLE);
        
        try{
            setVisible(true);
            setTitle(getUITitle());
            setClosable(true);
            setMaximizable(false);
            setIconifiable(true);
            setResizable(false);
            setSelected(true);
            //setSize(860,520);

        }
        catch (PropertyVetoException ex){
            
        }
        
        this.pnlNotifications.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Patient notifications", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        this.pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "Notification actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        
        this.btnAddNewNotification.setActionCommand(Action.REQUEST_CREATE_NOTIFICATION.toString());
        this.btnAddNewNotification.addActionListener(this);
        this.btnEditSelectedNotification.setActionCommand(Action.REQUEST_UPDATE_NOTIFICATION.toString());
        this.btnEditSelectedNotification.addActionListener(this);
        this.btnCancelSelectedNotification.setActionCommand(Action.REQUEST_CANCEL_NOTIFICATION.toString());
        this.btnCancelSelectedNotification.addActionListener(this);
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnCloseView.addActionListener(this);
        
        addInternalFrameListeners();
        createNotificationTable();
        setNotificationTableListener();
        
        this.btnAddNewNotification.setEnabled(true);
        this.btnCloseView.setEnabled(true);
        this.btnEditSelectedNotification.setEnabled(false);
        this.btnCancelSelectedNotification.setEnabled(false);
        // Add a component listener to adjust column widths after it is displayed
        tblNotifications.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustColumnWidthsAndViewPosition(tblNotifications);
            }
        });
        
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.NotificationViewControllerActionEvent.UNACTIONED_NOTIFICATIONS_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        
    }
    
    private void adjustColumnWidthsAndViewPosition(JTable table){
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewController.setRelativeColumnWidths(table, 794, new double[]{0.1,0.1,0.25,0.55});
            ViewController.centerInternalFrame(getDesktopView().getDeskTop(), this);
        });
    }
    
    private boolean tableValueChangedListenerActivated = false;
    private void setNotificationTableListener(){
        this.tblNotifications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblNotifications.getSelectionModel();
        
        lsm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
                    int selectedRow = tblNotifications.getSelectedRow();
                    if (selectedRow!=-1){
                        tableValueChangedListenerActivated = true;
                        //Patient patient = (Patient)tblAppointments.getModel().getValueAt(selectedRow, 0);
                        //doScheduleTitleRefresh(patient);
                    }
                    //else doScheduleTitleRefresh(null);   
                }
            }
        });
        
        tblNotifications.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblNotifications.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblNotifications.isRowSelected(selectedRow))
                    tblNotifications.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        
    }
    
    
    private void populatePatientNotificationTable(ArrayList<Notification> patientNotifications){
        PatientNotificationViewTableModel model = 
                (PatientNotificationViewTableModel)this.tblNotifications.getModel();
        model.removeAllElements();
        Iterator<Notification> it = patientNotifications.iterator();
        while (it.hasNext()){
            ((PatientNotificationViewTableModel)this.tblNotifications.getModel()).addElement(it.next());
        }
    }
    
    /**
     * Establish an InternalFrameListener for when the view is closed 
     * Setting DISPOSE_ON_CLOSE action when the window "X" is clicked, fires
     * InternalFrameEvent.INTERNAL_FRAME_CLOSED event for the listener to let 
     * the view controller know what's happening
     */
    private InternalFrameAdapter internalFrameAdapter = null;
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientNotificationView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            @Override  
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        PatientNotificationView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void doCancelNotification(){
        boolean isError = false;
        if (this.tblNotifications.getSelectedRow()==-1){
            JOptionPane.showInternalMessageDialog(this, "A notification has not been selected for cancellation");
            isError = true;
        }
        if (!isError){
            String[] options = {"Yes", "No"};
            int reply = JOptionPane.showOptionDialog(this,
                        "Are you sure you want to cancel the selected "
                                + "notificatiom?",null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
            if (reply==JOptionPane.YES_OPTION){
                int row = this.tblNotifications.getSelectedRow();
                PatientNotificationViewTableModel model = 
                    (PatientNotificationViewTableModel)this.tblNotifications.getModel();
                getMyController().getDescriptor().getViewDescription().setPatientNotification(model.getElementAt(row));
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotificationViewControllerActionEvent.
                            CANCEL_NOTIFICATION_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
    }
    
    private void doCloseView(){
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }
    
    private void doCreateNotification(){
        ActionEvent actionEvent = new ActionEvent(
        this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.CREATE_NOTIFICATION_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        this.tblNotifications.clearSelection();
    }
    
    private void doReadAllNotifications(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotificationViewControllerActionEvent.NOTIFICATIONS_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        setUITitle(UI_ALL_NOTIFICATIONS_TITLE);
    }
    
    private void doReadUnactionedNotifications(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotificationViewControllerActionEvent.UNACTIONED_NOTIFICATIONS_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        setUITitle(UI_UNACTIONED_NOTIFICATIONS_TITLE);
    }
    
    private void doUpdateNotification(){
        boolean isError = false;
        if (this.tblNotifications.getSelectedRow()==-1){
            JOptionPane.showInternalMessageDialog(this, "A notification has not been selected", "View error", JOptionPane.WARNING_MESSAGE);
            isError = true;
        }
        if (!isError){
            PatientNotificationViewTableModel model = 
                (PatientNotificationViewTableModel)this.tblNotifications.getModel();
            getMyController().getDescriptor().getViewDescription().setPatientNotification(model.getElementAt(this.tblNotifications.getSelectedRow()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotificationViewControllerActionEvent.UPDATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            this.tblNotifications.clearSelection();
        }
    }
    
    private void createNotificationTable(){
        this.tblNotifications = null;
        this.tblNotifications = new JTable(new PatientNotificationViewTableModel());
        PatientNotificationViewTableModel model = (PatientNotificationViewTableModel)tblNotifications.getModel();
        model.addTableModelListener(this);
        this.tblNotifications.setDefaultRenderer(LocalDate.class,new PatientNotificationTableLocalDateRenderer());
        
        scrNotificationTable.setViewportView(this.tblNotifications);
        ViewController.setRelativeColumnWidths(tblNotifications,794, new double[]{0.1,0.1,0.25,0.55});
        /*
        ViewController.setJTableColumnProperties(tblNotifications, 
                scrNotificationTable.getPreferredSize().width, 
                25,10,15,50);
        */
        this.tblNotifications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblNotifications.getSelectionModel();
        lsm.addListSelectionListener(this);
    }
    
    private TableCellRenderer patientNotificationTableDefaultRenderer = null;
    private TableCellRenderer getPatientNotificationTableDefaultRenderer(){
        return patientNotificationTableDefaultRenderer;
    }
    private void setNotificationTableDefaultRenderer(TableCellRenderer renderer){
        patientNotificationTableDefaultRenderer = renderer;
    }
    
    private String UITitle = null;
    private String getUITitle(){
        return UITitle;
    }
    private void setUITitle(String title){
        UITitle = title;
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
        rdbDisplayUnactionedNotifications = new javax.swing.JRadioButton();
        rdbDisplayAllOptions = new javax.swing.JRadioButton();
        pnlNotifications = new javax.swing.JPanel();
        scrNotificationTable = new javax.swing.JScrollPane();
        tblNotifications = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnAddNewNotification = new javax.swing.JButton();
        btnEditSelectedNotification = new javax.swing.JButton();
        btnCancelSelectedNotification = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        rdbDisplayUnactionedNotifications.setText("only display unactioned notifications");

        rdbDisplayAllOptions.setText("display all notifications");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(157, Short.MAX_VALUE)
                .addComponent(rdbDisplayUnactionedNotifications, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113)
                .addComponent(rdbDisplayAllOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(123, 123, 123))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbDisplayUnactionedNotifications)
                    .addComponent(rdbDisplayAllOptions))
                .addGap(14, 14, 14))
        );

        pnlNotifications.setBorder(javax.swing.BorderFactory.createTitledBorder("Notification list"));

        tblNotifications.setModel(new javax.swing.table.DefaultTableModel(
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
        scrNotificationTable.setViewportView(tblNotifications);

        javax.swing.GroupLayout pnlNotificationsLayout = new javax.swing.GroupLayout(pnlNotifications);
        pnlNotifications.setLayout(pnlNotificationsLayout);
        pnlNotificationsLayout.setHorizontalGroup(
            pnlNotificationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotificationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotificationTable)
                .addContainerGap())
        );
        pnlNotificationsLayout.setVerticalGroup(
            pnlNotificationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNotificationsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotificationTable, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Notification actions"));

        btnAddNewNotification.setText("<html><center>Add</center><center>new</center><center>notification</center></html>");

        btnEditSelectedNotification.setText("<html><center>Update</center><center>selected</center><center>notification</center></html>");

        btnCancelSelectedNotification.setText("<html><center>Cancel</center><center>selected</center><center>notification</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAddNewNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEditSelectedNotification, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelSelectedNotification, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddNewNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnEditSelectedNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(btnCancelSelectedNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlNotifications, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlNotifications, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNewNotification;
    private javax.swing.JButton btnCancelSelectedNotification;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnEditSelectedNotification;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlNotifications;
    private javax.swing.JRadioButton rdbDisplayAllOptions;
    private javax.swing.JRadioButton rdbDisplayUnactionedNotifications;
    private javax.swing.JScrollPane scrNotificationTable;
    private javax.swing.JTable tblNotifications;
    // End of variables declaration//GEN-END:variables
}
