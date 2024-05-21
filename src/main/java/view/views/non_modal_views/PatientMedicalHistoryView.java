/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import model.*;
import view.View;
import view.views.view_support_classes.models.MedicalConditionWithStateTableModel;

/**
 *
 * @author colin
 */
public class PatientMedicalHistoryView extends View 
        implements ActionListener, 
                   TableModelListener, 
                   ListSelectionListener,
                   PropertyChangeListener{
    enum ToggleMode{PRIMARY,SECONDARY,OTHER}
    enum ConditionViewMode{PRIMARY, SECONDARY};
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_DELETE_PATIENT_CONDITION_COMMENT, 
        REQUEST_TOGGLE_CONDITION_VIEW,
        REQUEST_UPDATE_PATIENT_CONDITION_COMMENT,
        };
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public PatientMedicalHistoryView(
        View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
        
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setVisible(true);
        pnlActions.setBackground(new Color(220,220,220));
        setTitle(getPatient().toString() + " medical history");
        
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnDeleteCommentFromCondition.setActionCommand(Action.REQUEST_DELETE_PATIENT_CONDITION_COMMENT.toString());
        this.btnEditCommentForCondition.setActionCommand(Action.REQUEST_UPDATE_PATIENT_CONDITION_COMMENT.toString());
        this.btnToggleConditionView.setActionCommand(Action.REQUEST_TOGGLE_CONDITION_VIEW.toString());
        this.btnCloseView.addActionListener(this);
        this.btnDeleteCommentFromCondition.addActionListener(this);
        this.btnEditCommentForCondition.addActionListener(this);
        this.btnToggleConditionView.addActionListener(this);
        
        ListSelectionModel lsm = this.tblCondition.getSelectionModel();
        lsm.addListSelectionListener(this);
        
        ConditionWithState cws = new ConditionWithState();
        cws.setCondition(new PrimaryCondition());
        setConditionWithState(cws);
        setConditionViewMode(ConditionViewMode.PRIMARY);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        boolean isError = false;
        String error = null;
        ActionEvent actionEvent = null;
        ConditionWithState cws = null;
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                doSendActionEvent(ViewController.
                        PatientMedicalHistoryViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION);
                break;
            
            case REQUEST_TOGGLE_CONDITION_VIEW:
                switch (getConditionViewMode()){
                    case PRIMARY:
                        if(getSelectedConditionWithState()!=null)
                            setConditionViewMode(ConditionViewMode.SECONDARY);
                        else JOptionPane.showInternalMessageDialog(this, 
                                "A primary condition has not been selected",
                                "View error", JOptionPane.WARNING_MESSAGE);
                        break;
                    case SECONDARY:
                        setConditionViewMode(ConditionViewMode.PRIMARY);
                        break;
                }
                break;
            
            case REQUEST_UPDATE_PATIENT_CONDITION_COMMENT:
                cws = getSelectedCondition();     
                if (cws!=null){
                    setConditionWithState(cws);
                    doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                            .PATIENT_CONDITION_COMMENT_UPDATE_REQUEST);
                }else{
                    isError = true;
                    error = "Patient condition has not been defined;/n"
                                + "Action to update condition comment aborted";
                }
                break;
                
            case REQUEST_DELETE_PATIENT_CONDITION_COMMENT:
                cws = getSelectedCondition();
                if (cws!=null){
                    cws.setComment(null);
                    setConditionWithState(cws);
                    doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                            .PATIENT_CONDITION_COMMENT_UPDATE_REQUEST);
                }
                else{
                    isError = true;
                    error = "Patient condition has not been defined;/n"
                                + "Action to delete condition comment aborted";
                }
                break;
        }
        if(getIsViewControllerErrorReceived()){
            String message = getMyController().getDescriptor()
                    .getControllerDescription().getError();
            JOptionPane.showInternalMessageDialog(this,message, 
                    "View controller error", JOptionPane.WARNING_MESSAGE);
            setIsViewControllerErrorReceived(false);
        }else if(isError){
            JOptionPane.showInternalMessageDialog(this,error, 
                    "View error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){    
        ViewController.PatientMedicalHistoryViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientMedicalHistoryViewControllerPropertyChangeEvent
                        .valueOf(e.getPropertyName());
        switch (propertyName){
            case CONDITION_WITH_STATE_RECEIVED:
                ConditionWithState cws = getMyController().getDescriptor()
                        .getControllerDescription().getConditionWithState();
                populateConditionsTable(cws);
                break;
            case PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_ERROR_RECEIVED:
                setIsViewControllerErrorReceived(true);
                break;
        }
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
        ActionEvent actionEvent = null;
        ConditionWithState cws = null;
        int row = e.getFirstRow();
        int column = e.getColumn();
        MedicalConditionWithStateTableModel model =  
                (MedicalConditionWithStateTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        cws = (ConditionWithState)model.getElementAt(row);
        setConditionWithState(cws);
        tblCondition.clearSelection();
        
        if (value) //add the updated condition to the patient's medical history
            doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                    .PATIENT_CONDITION_CREATE_REQUEST);
        else // remove the selected condition from the patient's medical history
            doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                    .PATIENT_CONDITION_DELETE_REQUEST);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        PrimaryCondition pc = null;
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblCondition.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                MedicalConditionWithStateTableModel model = 
                        (MedicalConditionWithStateTableModel)tblCondition.getModel();
                ConditionWithState cws = 
                        (ConditionWithState) model.getElementAt(selectedRow);
                switch(getConditionViewMode()){
                    case PRIMARY:
                        this.btnDeleteCommentFromCondition.setEnabled(true);
                        this.btnEditCommentForCondition.setEnabled(true);
                        pc = (PrimaryCondition)cws.getCondition();
                        if (!pc.getSecondaryCondition().get().isEmpty()){
                            setPrimaryCondition(pc);
                            setToggleMode(ToggleMode.OTHER);
                            this.btnToggleConditionView.setEnabled(true);
                        }else {
                            this.btnToggleConditionView.setEnabled(false);
                            setToggleMode(ToggleMode.SECONDARY);
                        }
                        break;
                    case SECONDARY:
                        this.btnDeleteCommentFromCondition.setEnabled(true);
                        this.btnEditCommentForCondition.setEnabled(true);
                        this.btnToggleConditionView.setEnabled(true);
                        setToggleMode(ToggleMode.SECONDARY);
                        break;
                }
            }
        }
    }
    
    private ConditionWithState doRequestConditionCommentUpdateEditor(ConditionWithState cws){
        String comment = "";
        comment = JOptionPane.showInternalInputDialog(
                    this,"Enter new comment for '" + cws.getComment() + "'");
            if (comment!=null){
                comment = comment.trim();
                if (!comment.isEmpty()){
                    cws.setComment(comment);
                    tblCondition.clearSelection();  
                }else JOptionPane.showInternalMessageDialog(
                                this, "Updated of medical condition comment cannot be blank"); 
            }else JOptionPane.showInternalMessageDialog(
                                this, "Medical condition comment has not been defined");
        return cws;
    }
    
    private void setConditionWithState(ConditionWithState cws){
        getMyController().getDescriptor()
                .getViewDescription().setConditionWithState(cws);
    }
    private ConditionWithState getConditionWithState(){
        return getMyController().getDescriptor()
                .getControllerDescription().getConditionWithState();
    }
    
    private ConditionWithState getSelectedCondition(){
        ConditionWithState result = null;
        int selectedRow = 0;
        selectedRow = this.tblCondition.getSelectedRow();
        if (selectedRow!=-1){
            MedicalConditionWithStateTableModel model = 
                    (MedicalConditionWithStateTableModel)tblCondition.getModel();
            result = model.getElementAt(selectedRow);
        }
        return result;
    }
    
    private void doSendActionEvent(
            ViewController.PatientMedicalHistoryViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private ConditionViewMode getConditionViewMode(){
        return conditionViewMode;
    }
    
    private ConditionViewMode conditionViewMode = null;
    private void setConditionViewMode(ConditionViewMode value){
        conditionViewMode = value;
        boolean isError = false;
        ConditionWithState cws = null;
        SecondaryCondition sc = null;
        ViewController.PatientMedicalHistoryViewControllerActionEvent actionCommand = null;
        switch(conditionViewMode){
            case PRIMARY://currently display of secondary conditions active
                cws = new PrimaryConditionWithState(new PrimaryCondition());
                setConditionWithState(cws);
                //setTitle("Medical conditions (primary options)");
                setToggleMode(ToggleMode.SECONDARY);
                this.btnEditCommentForCondition.setEnabled(false);
                this.btnDeleteCommentFromCondition.setEnabled(false);
                this.btnToggleConditionView.setEnabled(false);
                break;
            case SECONDARY://current view mode is PRIMARY
                cws = getSelectedConditionWithState();
                if (cws!=null){
                    sc = new SecondaryCondition();
                    sc.setPrimaryCondition((PrimaryCondition)cws.getCondition());
                    ConditionWithState conditionWithState =
                            new SecondaryConditionWithState(sc);
                    setConditionWithState(conditionWithState);
                    setToggleMode(ToggleMode.PRIMARY);
                    this.btnDeleteCommentFromCondition.setEnabled(false);
                    this.btnEditCommentForCondition.setEnabled(false);
                    this.btnToggleConditionView.setEnabled(true);

                }else{
                    isError = true;
                    JOptionPane.showInternalMessageDialog(this,
                            "A primary condition is not selected", "View error",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;
        }
        if(!isError)
            doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                            .PATIENT_CONDITION_READ_REQUEST);
    }
    
    private void populateConditionsTable(ConditionWithState cws){
        ArrayList<ConditionWithState> conditions = cws.get();

        ListSelectionModel lsm = this.tblCondition.getSelectionModel();
        lsm.removeListSelectionListener(this);
        lsm.addListSelectionListener(this);

        
        tblCondition.clearSelection();
        
        switch(getConditionViewMode()){
            case PRIMARY:
                this.btnDeleteCommentFromCondition.setEnabled(false);
                this.btnEditCommentForCondition.setEnabled(false);
                this.btnToggleConditionView.setEnabled(false);
                setToggleMode(ToggleMode.SECONDARY);
                MedicalConditionWithStateTableModel.conditionsColumnName = "Condition";
                break;
            case SECONDARY:
                this.btnDeleteCommentFromCondition.setEnabled(false);
                this.btnEditCommentForCondition.setEnabled(false);
                this.btnToggleConditionView.setEnabled(true);
                setToggleMode(ToggleMode.PRIMARY);
                SecondaryCondition sc = (SecondaryCondition)conditions.get(0).getCondition();
                MedicalConditionWithStateTableModel.conditionsColumnName = 
                        sc.getPrimaryCondition().getDescription() + " condition";
                break;
        }
        tblCondition.setModel(new MedicalConditionWithStateTableModel());
        MedicalConditionWithStateTableModel model = 
                (MedicalConditionWithStateTableModel)this.tblCondition.getModel();
        model.removeTableModelListener(this);
        model.addTableModelListener(this);
        
        Iterator it = conditions.iterator();
        while(it.hasNext()){
            ConditionWithState conditionWithState = (ConditionWithState)it.next();
            model.addElement(conditionWithState);
        }
        ViewController.setJTableColumnProperties(
            tblCondition, scrConditionTable.getPreferredSize().width, 20,80);
    }

    private boolean tableValueChangedListenerActivated = false;
    private void initialiseTable(){
        tblCondition.getModel().addTableModelListener(this);
        ViewController.setJTableColumnProperties(
                tblCondition, scrConditionTable.getPreferredSize().width, 20,70);
        this.tblCondition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblCondition.getSelectionModel();
        lsm.addListSelectionListener(this);
        
        tblCondition.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblCondition.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblCondition.isRowSelected(selectedRow))
                    tblCondition.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
    }
    
    private boolean isViewControllerErrorReceived = false;
    private boolean getIsViewControllerErrorReceived(){
        return isViewControllerErrorReceived;
    }
    private void setIsViewControllerErrorReceived(boolean value){
        isViewControllerErrorReceived = value;
    }
    
    private PatientCondition patientCondition = null;
    private PatientCondition getPatientCondition(){
        return getMyController().getDescriptor()
                .getControllerDescription().getPatientCondition();
    }
    private void setPatientCondition(PatientCondition value){
        getMyController().getDescriptor()
                .getViewDescription().setPatientCondition(value);
    }
    
    private Patient patient = null;
    private Patient getPatient(){
        return getMyController().getDescriptor()
                .getControllerDescription().getPatient();
    }
    private void setPatient(Patient value){
        getMyController().getDescriptor()
                .getViewDescription().setPatient(value);
    }
    
    private ToggleMode toggleMode = null;
    private void setToggleMode(ToggleMode value){
        toggleMode = value;
        switch (getToggleMode()){
            case PRIMARY:
                this.btnToggleConditionView.setText (
                        "<html><center>View</center><center>primary</center>"
                                + "<center>conditions</center></html>");
                break;
            case SECONDARY:
                this.btnToggleConditionView.setText (
                        "<html><center>View</center><center>secondary</center>"
                                + "<center>conditions</center></html>");
                break;
            case OTHER:
                this.btnToggleConditionView.setText (
                        "<html><center>View</center><center>" 
                                + getPrimaryCondition().getDescription() 
                                + "</center><center>conditions</center></html>");
                break;
        }
    }
    private ToggleMode getToggleMode(){
        return toggleMode;
    }
    
    private PrimaryCondition primaryCondition = null;
    private void setPrimaryCondition(PrimaryCondition value){
        primaryCondition = value; 
    }
    private PrimaryCondition getPrimaryCondition(){
        return primaryCondition;
    }
    
    private ConditionWithState getSelectedConditionWithState(){
        ConditionWithState cws = null;
        int selectedRow = 0;
        selectedRow = tblCondition.getSelectedRow();
        if (selectedRow!=-1){
            MedicalConditionWithStateTableModel model = 
                    (MedicalConditionWithStateTableModel)tblCondition.getModel();
            cws = model.getElementAt(selectedRow);
        }
        return cws;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrConditionTable = new javax.swing.JScrollPane();
        tblCondition = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnEditCommentForCondition = new javax.swing.JButton();
        btnDeleteCommentFromCondition = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnToggleConditionView = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        scrConditionTable.setViewportView(tblCondition);
        ViewController.setJTableColumnProperties(
            tblCondition, scrConditionTable.getPreferredSize().width, 20,80);

        pnlActions.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEditCommentForCondition.setText("<html><center>Update</center><center>condition</center><center>notes</center></html>");
        btnEditCommentForCondition.setMaximumSize(new java.awt.Dimension(87, 25));
        btnEditCommentForCondition.setMinimumSize(new java.awt.Dimension(87, 25));
        btnEditCommentForCondition.setPreferredSize(new java.awt.Dimension(87, 25));
        btnEditCommentForCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditCommentForConditionActionPerformed(evt);
            }
        });

        btnDeleteCommentFromCondition.setText("<html><center>Delete</center><center>condition </center><center>notes</center></html>");
        btnDeleteCommentFromCondition.setMaximumSize(new java.awt.Dimension(87, 25));
        btnDeleteCommentFromCondition.setMinimumSize(new java.awt.Dimension(87, 25));
        btnDeleteCommentFromCondition.setPreferredSize(new java.awt.Dimension(87, 25));

        btnCloseView.setText("<html><center>Close</center><center>view</center></html");
        btnCloseView.setMaximumSize(new java.awt.Dimension(87, 25));
        btnCloseView.setMinimumSize(new java.awt.Dimension(87, 25));
        btnCloseView.setPreferredSize(new java.awt.Dimension(87, 25));
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        btnToggleConditionView.setText("<html><center>View</center><center>infectious disease</center><center>conditions</center></html>");
        btnToggleConditionView.setMaximumSize(new java.awt.Dimension(87, 25));
        btnToggleConditionView.setMinimumSize(new java.awt.Dimension(87, 25));
        btnToggleConditionView.setPreferredSize(new java.awt.Dimension(87, 25));
        btnToggleConditionView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleConditionViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(btnToggleConditionView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditCommentForCondition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteCommentFromCondition, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnEditCommentForCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(btnDeleteCommentFromCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnToggleConditionView, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnEditCommentForCondition.getAccessibleContext().setAccessibleDescription("");

        jLabel1.setText("Notes on condition: ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrConditionTable, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrConditionTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditCommentForConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditCommentForConditionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditCommentForConditionActionPerformed

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCloseViewActionPerformed

    private void btnToggleConditionViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleConditionViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnToggleConditionViewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeleteCommentFromCondition;
    private javax.swing.JButton btnEditCommentForCondition;
    private javax.swing.JButton btnToggleConditionView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JScrollPane scrConditionTable;
    private javax.swing.JTable tblCondition;
    // End of variables declaration//GEN-END:variables
}
