/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.modal_views;

import colinhewlettsolutions.client.controller.PatientAppointmentDataViewController;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.model.entity.PatientAppointmentData;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
/**
 *
 * @author colin
 */
public class ModalProgressView extends ModalView implements ActionListener,
                                                            PropertyChangeListener{
    
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_PROCESS_START_STOP
    }
    
    public enum ViewMode{
        NULL,
        PROCESS_PENDING,
        PROCESS_STARTED,
        PROCESS_STOPPED
    }

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalProgressView(View.Viewer myViewType, ViewController myController,DesktopView desktopView) {
        
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Action actionRequest = Action.valueOf(e.getActionCommand());
        switch(actionRequest){
            case REQUEST_CLOSE_VIEW ->{
                doCloseViewRequest();
                break;
            }
            case REQUEST_PROCESS_START_STOP ->{
                PatientAppointmentDataViewController.ViewMode mode = (PatientAppointmentDataViewController.ViewMode)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.VIEW_MODE);
                switch (mode){
                    case PROCESS_PENDING ->{
                        doSendActionEvent(PatientAppointmentDataViewController.Actions.PROCESS_PENDING_REQUEST);
                        break;
                    }
                    case PROCESS_STARTED ->{
                        doSendActionEvent(PatientAppointmentDataViewController.Actions.PROCESS_STOP_REQUEST);
                        break;
                    }
                    case PROCESS_STOPPED ->{
                        doSendActionEvent(PatientAppointmentDataViewController.Actions.PROCESS_START_REQUEST);
                        break;
                    }
                }
                doSendActionEvent(PatientAppointmentDataViewController.Actions.PROCESS_START_REQUEST);
                break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        PatientAppointmentDataViewController.Properties property = PatientAppointmentDataViewController.Properties.valueOf(e.getPropertyName());
        switch (property){
            case PROCESS_CHANGE_NOTIFICATION ->{
                PatientAppointmentDataViewController.ViewMode mode = (PatientAppointmentDataViewController.ViewMode)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.VIEW_MODE);
                switch (mode){
                    case PROCESS_ENDED ->{
                        this.btnStartStopProcess.setText("<html><center>Start</center></html>");
                        this.btnStartStopProcess.setEnabled(false);
                        doSendActionEvent(PatientAppointmentDataViewController.Actions.PATIENT_APPOINTMENT_DATA_REQUEST);
                    }
                    case PROCESS_PENDING ->{
                        this.btnStartStopProcess.setEnabled(true);
                        break;
                    }
                    case PROCESS_STARTED ->{
                        this.btnStartStopProcess.setText("<html><center>Stop</center></html>");
                        break;
                    }
                    case PROCESS_STOPPED ->{
                        this.btnStartStopProcess.setText("<html><center>Start</center></html>");
                        break;
                    }
                }
                break;
            }
            case PROCESS_UPDATE_NOTIFICATION ->{
                displayItemCounter();
                setItemValue(((Point)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.ITEM_COUNTER)).x);
                setItemTotal(((Point)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.ITEM_COUNTER)).y);
                proProgressBar.setValue(getItemValue()*100/getItemTotal());
                break;
            }
        }
    }
    
    @Override
    /**
     * on entry controller descriptor 
     * -- view mode property is PROCESS_STOPPED 
     * -- ITEM_COUNTER property is initialised with a Point value; thus, x = 0 and y with the total value of items being processed
     */
    public void initialiseView(){
        initComponents();
        displayItemCounter();
        proProgressBar.setStringPainted(true);
        btnStartStopProcess.setActionCommand(Action.REQUEST_PROCESS_START_STOP.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnStartStopProcess.addActionListener(this);
        btnCloseView.addActionListener(this);
    }
    
    private void displayItemCounter(){
        Point itemCounter = (Point)getMyController().getDescriptor().getControllerDescription().getProperty(Properties.ITEM_COUNTER);
        setItemValue(itemCounter.x);
        setItemTotal(itemCounter.y);
        lblItemCounter.setText(String.valueOf(getItemValue()) + " / " + String.valueOf(getItemTotal()));
    }
    
    /**
     * the method closes the modal view which returns control to the view controller that lainched the view in the first place
     */
    private void doCloseViewRequest(){
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
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

    private Integer itemValue = null;
    private void setItemValue(Integer value){
        itemValue = value;
    }
    private Integer getItemValue(){
        return itemValue;
    }
    
    private Integer itemTotal = null;
    private void setItemTotal(Integer value){
        itemTotal = value;
    }
    private Integer getItemTotal(){
        return itemTotal;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlProressMonitor = new javax.swing.JPanel();
        proProgressBar = new javax.swing.JProgressBar(0,100);
        pnlItemCounter = new javax.swing.JPanel();
        lblItemCounter = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        btnStartStopProcess = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        pnlProressMonitor.setBorder(javax.swing.BorderFactory.createTitledBorder("Progress monitor"));

        pnlItemCounter.setBorder(javax.swing.BorderFactory.createTitledBorder("Item counter"));

        lblItemCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblItemCounter.setText("jLabel1");

        javax.swing.GroupLayout pnlItemCounterLayout = new javax.swing.GroupLayout(pnlItemCounter);
        pnlItemCounter.setLayout(pnlItemCounterLayout);
        pnlItemCounterLayout.setHorizontalGroup(
            pnlItemCounterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlItemCounterLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblItemCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );
        pnlItemCounterLayout.setVerticalGroup(
            pnlItemCounterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlItemCounterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblItemCounter)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlProressMonitorLayout = new javax.swing.GroupLayout(pnlProressMonitor);
        pnlProressMonitor.setLayout(pnlProressMonitorLayout);
        pnlProressMonitorLayout.setHorizontalGroup(
            pnlProressMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProressMonitorLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlProressMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlItemCounter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(proProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        pnlProressMonitorLayout.setVerticalGroup(
            pnlProressMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProressMonitorLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(proProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlItemCounter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnStartStopProcess.setText("Start");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStartStopProcess)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(btnStartStopProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(pnlProressMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlProressMonitor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnStartStopProcess;
    private javax.swing.JLabel lblItemCounter;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlItemCounter;
    private javax.swing.JPanel pnlProressMonitor;
    private javax.swing.JProgressBar proProgressBar;
    // End of variables declaration//GEN-END:variables
}
