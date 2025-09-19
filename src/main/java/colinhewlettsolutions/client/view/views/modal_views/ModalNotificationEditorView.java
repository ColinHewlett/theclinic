/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.view.views.modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.view.support_classes.models.PatientNotificationView2ColumnTableModel;
import colinhewlettsolutions.client.view.support_classes.renderers.NotificationEditorTableLocalDateRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.NotificationTableKeyColorsAndLocalDateRenderer;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.Notification;
import colinhewlettsolutions.client.view.support_classes.renderers.TableHeaderCellBorderRenderer;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import com.github.lgooddatepicker.components.DatePickerSettings;
import colinhewlettsolutions.client.controller.Descriptor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author colin
 */
public class ModalNotificationEditorView extends ModalView implements ActionListener{
    private ViewController.ViewMode viewMode = null;
    
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_CREATE_UPDATE_NOTIFICATION,
        REQUEST_PATIENT
    }
    
    public void actionPerformed(ActionEvent e){
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_CLOSE_VIEW:
                doCloseView();
                break;
            case REQUEST_CREATE_UPDATE_NOTIFICATION:
                doRequestCreateUpdateNotification();
                break;
            case REQUEST_PATIENT:
                doRequestPatient();
                break;
            
        }
    }
        
    
    private void populateNotificationHistoryTable(ArrayList<Notification> patientNotifications){
        PatientNotificationView2ColumnTableModel model = 
                (PatientNotificationView2ColumnTableModel)this.tblPatientNotificationHistory.getModel();
        model.removeAllElements();
        Iterator<Notification> it = patientNotifications.iterator();
        while (it.hasNext()){
            ((PatientNotificationView2ColumnTableModel)this.tblPatientNotificationHistory.getModel()).addElement(it.next());
        }
        
    }
    
    private static LocalDate date = null;
    public static LocalDate getDate(){
        return date;
    }
    public static void setDate(LocalDate value){
        date = value;
    }
    
    /**
     * method initialises the patient notification history table with the values contained in the entity descriptor
     */
    private void doReceivedPatientNotifications(){
        ArrayList<Notification> patientNotifications =
                (ArrayList<Notification>)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.PATIENT_NOTIFICATIONS);
        populateNotificationHistoryTable(patientNotifications);
    }
    
    /**
     * On entry method assumes the patient selector has been initialised
     * -- the received patient notification is used to initialise the ui
     * 
     */
    private void doReceivedPatientNotification(){
        Notification patientNotification = (Notification)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.PATIENT_NOTIFICATION);
        if (patientNotification==null) {
            this.cmbSelectPatient.setSelectedIndex(-1);
            this.rdbNotificationUnactioned.setSelected(false);
            setViewMode(ViewController.ViewMode.Create);
        }
        else {
            setViewMode(ViewController.ViewMode.Update);
            cmbSelectPatient.setSelectedItem(patientNotification.getPatient());
            dpNotificationDate.setDate(patientNotification.getNotificationDate());
            txaNotificationText.setText(patientNotification.getNotificationText());
            if (patientNotification.getIsActioned())
                this.rdbNotificationActioned.setSelected(true);
            else this.rdbNotificationUnactioned.setSelected(false);
            this.cmbSelectPatient.setEditable(false);
            this.btnCreateUpdatePatientNotification.setText("<html><center>Update</center><center>patient</center><center>notification</center></html>");
        }
    }
    
    private void populatePatientSelector(JComboBox<Patient> selector){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                (ArrayList<Patient>)getMyController().getDescriptor().
                        getControllerDescription().getProperty(SystemDefinition.Properties.PATIENTS);
        for(Patient patient : patients){
            model.addElement(patient);
        }
        selector.setModel(model);
        selector.setSelectedIndex(-1);   
    }

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalNotificationEditorView(
            View.Viewer myViewType, 
            ViewController myController, 
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient notification editor");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e){
        //setViewDescriptor((Descriptor)e.getNewValue());
        ViewController.NotificationViewControllerPropertyChangeEvent propertyName =
                ViewController.NotificationViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case RECEIVED_PATIENT_NOTIFICATION:
                doReceivedPatientNotification();
                break;
            case RECEIVED_PATIENT_NOTIFICATIONS:
                //doReceivedPatientNotifications();
                this.populateNotificationHistoryTable((ArrayList<Notification>)getMyController()
                        .getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_NOTIFICATIONS));
                break;
        }
    }
    
    @Override
    public void initialiseView(){
        setClosable(true);
        initComponents();
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dpNotificationDate.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        ViewController.setJTableColumnProperties(
                tblPatientNotificationHistory, jScrollPane2.getPreferredSize().width, 20,80);
        setVisible(true);
        populatePatientSelector(this.cmbSelectPatient);
        cmbSelectPatient.setEnabled(false);
        cmbSelectPatient.setActionCommand(Action.REQUEST_PATIENT.toString());
        cmbSelectPatient.addActionListener(this);
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCloseView.addActionListener(this);
        this.btnCreateUpdatePatientNotification.setActionCommand(Action.REQUEST_CREATE_UPDATE_NOTIFICATION.toString());
        btnCreateUpdatePatientNotification.addActionListener(this);
        tblPatientNotificationHistory.setEnabled(false);
        switch(getMyController().getDescriptor()
                .getControllerDescription().getViewMode()){
            case CREATE:
                break;
            case UPDATE:
                break;   
        }
        this.tblPatientNotificationHistory.setDefaultRenderer(LocalDate.class, new NotificationEditorTableLocalDateRenderer());
        this.tblPatientNotificationHistory.setDefaultRenderer(String.class, new NotificationEditorTableLocalDateRenderer());        
        
        doReceivedPatientNotification(); 
        doReceivedPatientNotifications();
        doPanelInitialisation();
    }
    
    private void doPanelInitialisation(){
        pnlPatientSelection.setBorder(javax.swing.BorderFactory
                .createTitledBorder(null, "Select patient", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        
        pnlNotificationDetails.setBorder(javax.swing.BorderFactory
                .createTitledBorder(null, "Notification details", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        
        pnlNotificationHistory.setBorder(javax.swing.BorderFactory
                .createTitledBorder(null, "Notification history", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        
        pnlActions.setBorder(javax.swing.BorderFactory
                .createTitledBorder(null, "Actions", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
    }

    private ViewController.ViewMode getViewMode(){
        return this.viewMode;
    }

    public void setViewMode(ViewController.ViewMode value){
        this.viewMode = value;
        switch (viewMode){
            case Create:
                btnCreateUpdatePatientNotification.
                        setText("<html><center>Create</center><center>patient</center><center>notification</center></html>");
                //this.pnlRadioButtons.setEnabled(true);
                this.rdbNotificationActioned.setEnabled(false);
                this.rdbNotificationUnactioned.setEnabled(false);
                this.cmbSelectPatient.setEnabled(true);
                break;
            case Update:
                btnCreateUpdatePatientNotification.
                        setText("<html><center>Update</center><center>patient</center><center>notification</center></html>"); 
                this.rdbNotificationActioned.setEnabled(false);
                this.rdbNotificationUnactioned.setEnabled(false);
                this.cmbSelectPatient.setEnabled(false);
        }
    }
    
    private void doRequestCreateUpdateNotification() {                                                                   
        if (getViewMode().equals(ViewController.ViewMode.Create))
            doRequestNewPatientNotification();
        else doRequestUpdatePatientNotification();
    }                                                                  

    private void doCloseView() {                                             
        doViewCloseAction();
    }                                            

    private void doRequestPatient() {                                                 
        // TODO add your handling code here:
        if (cmbSelectPatient.getSelectedIndex()!=-1){
            Patient patient = (Patient)cmbSelectPatient.getSelectedItem();
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.NOTIFICATIONS_FOR_PATIENT_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }                                                

    private void doViewCloseAction(){
        /*
        String message = "Are you sure you want to close the notification editor?";
        int response = JOptionPane.showConfirmDialog(
                this, message, "Patient notification editor", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION){*/
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.MODAL_VIEWER_DEACTIVATED.toString());
            this.getMyController().actionPerformed(actionEvent);
        //}
    }
    
    private void doRequestNewPatientNotification(){
        if (doValidatePatientNotificationRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }       
    }
    
    private void doRequestUpdatePatientNotification(){
        if (doValidatePatientNotificationRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }

    private boolean doValidatePatientNotificationRequest(){
        boolean result = true;
        if (this.cmbSelectPatient.getSelectedItem()==null){
            result = false;
            JOptionPane.showInternalMessageDialog(
                    this, "A patient has not been selected", 
                    "View error",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (result){
            if (this.dpNotificationDate.getDate()==null){
                result = false;
                JOptionPane.showInternalMessageDialog(
                    this, "A valid notificaion date has not been defined", 
                    "Patient notification editor error",
                    JOptionPane.WARNING_MESSAGE);
            }    
        }
        if (result){
            if (this.txaNotificationText.getText().isEmpty()){
                result = false;
                JOptionPane.showInternalMessageDialog(
                    this, "No notificaion text has not been defined", 
                    "Patient notification editor error",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        if (result){
            Notification notification;
            if (getViewMode().equals(ViewController.ViewMode.Create))
                notification = new Notification();
            else
                notification = (Notification)getMyController().
                        getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT_NOTIFICATION);
            notification.setPatient(
                    (Patient)this.cmbSelectPatient.getSelectedItem());
            notification.setNotificationDate(
                    this.dpNotificationDate.getDate());
            notification.setNotificationText(
                    this.txaNotificationText.getText());
            notification.setIsActioned(rdbNotificationActioned.isSelected());
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.PATIENT_NOTIFICATION, notification);
        }
        else getMyController().getDescriptor().getViewDescription().
                setProperty(SystemDefinition.Properties.PATIENT_NOTIFICATION,null);
        return result;
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
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jSpinner1 = new javax.swing.JSpinner();
        pnlPatientSelection = new javax.swing.JPanel();
        cmbSelectPatient = new javax.swing.JComboBox<colinhewlettsolutions.client.model.entity.Patient>();
        pnlNotificationDetails = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dpNotificationDate = new com.github.lgooddatepicker.components.DatePicker();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaNotificationText = new javax.swing.JTextArea();
        pnlRadioButtons = new javax.swing.JPanel();
        rdbNotificationUnactioned = new javax.swing.JRadioButton();
        rdbNotificationActioned = new javax.swing.JRadioButton();
        pnlNotificationHistory = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPatientNotificationHistory = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnCreateUpdatePatientNotification = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        buttonGroup1.add(rdbNotificationUnactioned);
        buttonGroup1.add(rdbNotificationActioned);

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList1);

        setTitle("Patient notification editor");

        pnlPatientSelection.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select patient", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        cmbSelectPatient.setModel(new javax.swing.DefaultComboBoxModel<colinhewlettsolutions.client.model.entity.Patient>());

        javax.swing.GroupLayout pnlPatientSelectionLayout = new javax.swing.GroupLayout(pnlPatientSelection);
        pnlPatientSelection.setLayout(pnlPatientSelectionLayout);
        pnlPatientSelectionLayout.setHorizontalGroup(
            pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPatientSelectionLayout.setVerticalGroup(
            pnlPatientSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientSelectionLayout.createSequentialGroup()
                .addComponent(cmbSelectPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE))
        );

        cmbSelectPatient.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pnlNotificationDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notification details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel2.setText("Action by date");

        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dpNotificationDate.setSettings(settings);

        jLabel1.setText("Message");

        txaNotificationText.setColumns(20);
        txaNotificationText.setRows(5);
        txaNotificationText.setLineWrap(true);
        txaNotificationText.setPreferredSize(new java.awt.Dimension(166, 74));
        jScrollPane1.setViewportView(txaNotificationText);

        pnlRadioButtons.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Has been actioned?", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        rdbNotificationUnactioned.setText("no");
        rdbNotificationUnactioned.setSelected(true);

        rdbNotificationActioned.setText("yes");

        javax.swing.GroupLayout pnlRadioButtonsLayout = new javax.swing.GroupLayout(pnlRadioButtons);
        pnlRadioButtons.setLayout(pnlRadioButtonsLayout);
        pnlRadioButtonsLayout.setHorizontalGroup(
            pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRadioButtonsLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbNotificationActioned)
                    .addComponent(rdbNotificationUnactioned))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        pnlRadioButtonsLayout.setVerticalGroup(
            pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRadioButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbNotificationUnactioned)
                .addGap(18, 18, 18)
                .addComponent(rdbNotificationActioned)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlNotificationDetailsLayout = new javax.swing.GroupLayout(pnlNotificationDetails);
        pnlNotificationDetails.setLayout(pnlNotificationDetailsLayout);
        pnlNotificationDetailsLayout.setHorizontalGroup(
            pnlNotificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotificationDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNotificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlNotificationDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dpNotificationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(pnlRadioButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70))
        );
        pnlNotificationDetailsLayout.setVerticalGroup(
            pnlNotificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNotificationDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNotificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlRadioButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlNotificationDetailsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlNotificationDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(dpNotificationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(79, 79, 79))
        );

        pnlNotificationHistory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notification history", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        tblPatientNotificationHistory.setModel(new PatientNotificationView2ColumnTableModel()
        );
        TableColumnModel columnModel = tblPatientNotificationHistory.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(10);
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        jScrollPane2.setViewportView(tblPatientNotificationHistory);

        javax.swing.GroupLayout pnlNotificationHistoryLayout = new javax.swing.GroupLayout(pnlNotificationHistory);
        pnlNotificationHistory.setLayout(pnlNotificationHistoryLayout);
        pnlNotificationHistoryLayout.setHorizontalGroup(
            pnlNotificationHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotificationHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlNotificationHistoryLayout.setVerticalGroup(
            pnlNotificationHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotificationHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnCreateUpdatePatientNotification.setText("Create/Update");
        btnCreateUpdatePatientNotification.setToolTipText("");
        btnCreateUpdatePatientNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateUpdatePatientNotificationActionPerformed(evt);
            }
        });

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");
        btnCloseView.setToolTipText("");
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreateUpdatePatientNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnCreateUpdatePatientNotification, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlNotificationDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPatientSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlNotificationHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlNotificationDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlNotificationHistory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateUpdatePatientNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateUpdatePatientNotificationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateUpdatePatientNotificationActionPerformed

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCloseViewActionPerformed
/*
    private void doViewCloseAction(){
        String message = "Are you sure you want to close the notification editor?";
        int response = JOptionPane.showConfirmDialog(
                this, message, "Patient notification editor", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.MODAL_VIEWER_DEACTIVATED.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    
    private void doRequestNewPatientNotification(){
        if (doValidatePatientNotificationRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }       
    }
    
    private void doRequestUpdatePatientNotification(){
        if (doValidatePatientNotificationRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }

    private boolean doValidatePatientNotificationRequest(){
        boolean result = true;
        if (this.cmbSelectPatient.getSelectedItem()==null){
            result = false;
            JOptionPane.showMessageDialog(
                    this, "A patient has not been selected", 
                    "Patient notification editor error",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (result){
            if (this.dpNotificationDate.getDate()==null){
                result = false;
                JOptionPane.showMessageDialog(
                    this, "A valid notificaion date has not been defined", 
                    "Patient notification editor error",
                    JOptionPane.WARNING_MESSAGE);
            }    
        }
        if (result){
            if (this.txaNotificationText.getText().isEmpty()){
                result = false;
                JOptionPane.showMessageDialog(
                    this, "No notificaion text has not been defined", 
                    "Patient notification editor error",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        if (result){
            Notification notification;
            if (getViewMode().equals(ViewController.ViewMode.Create))
                notification = new Notification();
            else
                notification = getMyController().
                        getDescriptor().getControllerDescription().getPatientNotification();
            notification.setPatient(
                    (Patient)this.cmbSelectPatient.getSelectedItem());
            notification.setNotificationDate(
                    this.dpNotificationDate.getDate());
            notification.setNotificationText(
                    this.txaNotificationText.getText());
            notification.setIsActioned(
                    rdbNotificationActioned.isSelected());
            getMyController().getDescriptor().getViewDescription().
                    setPatientNotification(notification);
        }
        else getMyController().getDescriptor().getViewDescription().
                setPatientNotification(null);
        return result;
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdatePatientNotification;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<colinhewlettsolutions.client.model.entity.Patient> cmbSelectPatient;
    private com.github.lgooddatepicker.components.DatePicker dpNotificationDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlNotificationDetails;
    private javax.swing.JPanel pnlNotificationHistory;
    private javax.swing.JPanel pnlPatientSelection;
    private javax.swing.JPanel pnlRadioButtons;
    private javax.swing.JRadioButton rdbNotificationActioned;
    private javax.swing.JRadioButton rdbNotificationUnactioned;
    private javax.swing.JTable tblPatientNotificationHistory;
    private javax.swing.JTextArea txaNotificationText;
    // End of variables declaration//GEN-END:variables
}
