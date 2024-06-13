/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import model.entity.Treatment;
import view.View;
import view.views.view_support_classes.models.TreatmentTableModel;

/**
 *
 * @author colin
 */
public class TreatmentView extends View 
            implements ActionListener, 
                   TableModelListener, 
                   ListSelectionListener,
                   PropertyChangeListener {
    private boolean tableValueChangedListenerActivated = false;
    enum Action {
        REQUEST_CLOSE_VIEW,
        REQUEST_TREATMENT_COMMENT_UPDATE,
        REQUEST_TREATMENT_CREATE,
        REQUEST_TREATMENT_DELETE,
        REQUEST_TREATMENT_RENAME
    }
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public TreatmentView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    } 
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblTreatment.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                
                TreatmentTableModel model = 
                        (TreatmentTableModel)tblTreatment.getModel();
                Treatment tws = model.getElementAt(selectedRow);
                
            }
        }
    }
    
    public void actionPerformed(ActionEvent e){
        Treatment tws = null;
        ViewController.TreatmentViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                actionCommand = ViewController.TreatmentViewControllerActionEvent
                        .VIEW_CLOSE_NOTIFICATION;
                    doSendActionEvent(actionCommand);
                break;
            case REQUEST_TREATMENT_CREATE:
                tws = doPrepForRequestTreatmentCreate();
                if (tws!=null){
                    setTreatment(tws);
                    actionCommand = ViewController.TreatmentViewControllerActionEvent
                        .TREATMENT_CREATE_REQUEST;
                    doSendActionEvent(actionCommand);
                    /*
                    if (!getIsErrorMessageReceived()){
                        String message = "Treatment '" 
                                + tws.getDescription() +"' has been created";
                        JOptionPane.showInternalMessageDialog(this, message );

                        try{
                            this.setClosed(true);   
                        }catch (PropertyVetoException ex){

                        }
                        actionCommand = ViewController.TreatmentViewControllerActionEvent
                                .VIEW_CLOSE_NOTIFICATION;
                            doSendActionEvent(actionCommand);
                    }else{
                        String error = getMyController().getDescriptor()
                                .getControllerDescription().getError();
                        if (error!=null){
                            JOptionPane.showInternalMessageDialog(this,
                                    error, "Treatment conroller error",
                                    JOptionPane.WARNING_MESSAGE);
                        }

                    }
                    */
                }
                break;
            case REQUEST_TREATMENT_DELETE:
                setIsErrorMessageReceived(false);
                tws = doPrepForRequestTreatmentDelete();
                if (tws != null){
                    setTreatment(tws);
                    actionCommand = ViewController.TreatmentViewControllerActionEvent
                        .TREATMENT_DELETE_REQUEST;
                    doSendActionEvent(actionCommand);
                    
                    if(getIsErrorMessageReceived()){
                        JOptionPane.showInternalMessageDialog(this, 
                                getMyController().getDescriptor()
                                        .getControllerDescription().getError(),
                                "Treatment delete error",JOptionPane.WARNING_MESSAGE);
                    }
                    /*11/04/2024 10:15
                    if(!getIsErrorMessageReceived()){
                        String message = "Treatment '" 
                                + tws.getDescription() +"' has been deleted";
                        JOptionPane.showInternalMessageDialog(this, message );
                    }
                    
                        try{
                            this.setClosed(true);   
                        }catch (PropertyVetoException ex){

                        }
                        actionCommand = ViewController.TreatmentViewControllerActionEvent
                                .VIEW_CLOSE_NOTIFICATION;
                            doSendActionEvent(actionCommand);
                    }
                    */
                }
                break;
            case REQUEST_TREATMENT_RENAME:
                tws = doPrepForRequestTreatmentRename();
                if (tws!=null){
                    setTreatment(tws);
                    actionCommand = ViewController.TreatmentViewControllerActionEvent
                        .TREATMENT_RENAME_REQUEST;
                    doSendActionEvent(actionCommand);
                }
                break;
        }
    }

    @Override 
    public void propertyChange(PropertyChangeEvent e){
        ViewController.TreatmentViewControllerPropertyChangeEvent propertyName =
                ViewController.TreatmentViewControllerPropertyChangeEvent
                        .valueOf(e.getPropertyName());
        switch (propertyName){
            case TREATMENT_ERROR_RECEIVED:
                setIsErrorMessageReceived(true);
                break;
            case TREATMENT_RECEIVED:
                populateTreatmentTable();
                break;
        }
    }
    
    public void tableChanged(TableModelEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand = null;
        Treatment treatment = null;
        int row = e.getFirstRow();
        int column = e.getColumn();
        TreatmentTableModel model =  
                (TreatmentTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        treatment = (Treatment)model.getElementAt(row);
        //treatment.setState(value);
        getMyController().getDescriptor().getViewDescription().setTreatment(treatment);
        tblTreatment.clearSelection();
        /*
        if (treatmentWithState.getState())
            actionCommand = ViewController.ScheduleViewControllerActionEvent
                    .APPOINTMENT_TREATMENT_STATE_SET_REQUEST;
        else actionCommand = ViewController.ScheduleViewControllerActionEvent
                    .APPOINTMENT_TREATMENT_STATE_RESET_REQUEST;
        doSendActionEvent(actionCommand);
        */
    }

    @Override
    public void initialiseView(){
        initComponents();
        this.setClosable(true);
        setTitle("Treatment options");
        
        setVisible(true);
        setSize(414, 463);
        pnlActions.setBackground(new java.awt.Color(220, 220, 220));
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnAddTreatment.setActionCommand(
                Action.REQUEST_TREATMENT_CREATE.toString());
        this.btnDeleteTreatment.setActionCommand(
                Action.REQUEST_TREATMENT_DELETE.toString());
        this.btnRenameTreatment.setActionCommand(
                Action.REQUEST_TREATMENT_RENAME.toString());
        this.btnAddTreatment.addActionListener(this);
        btnCloseView.addActionListener(this);
        this.btnDeleteTreatment.addActionListener(this);
        this.btnRenameTreatment.addActionListener(this);
        
        ListSelectionModel lsm = this.tblTreatment.getSelectionModel();
        lsm.addListSelectionListener(this);

        this.tblTreatment.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblTreatment.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblTreatment.isRowSelected(selectedRow))
                    tblTreatment.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });

        doSendActionEvent(ViewController
                .TreatmentViewControllerActionEvent.TREATMENTS_READ_REQUEST);
        
        //populateTreatmentTable(); 
    }
    
    private boolean isErrorMessageReceived = false;
    private boolean getIsErrorMessageReceived(){
        return isErrorMessageReceived;
    }
    private void setIsErrorMessageReceived(boolean value){
        isErrorMessageReceived = value;
    }

    private void setTreatment(Treatment treatment){
        getMyController().getDescriptor()
                .getViewDescription().setTreatment(treatment);
    }

    private Treatment getTreatment(){
        Treatment result = null;
        result = getMyController().getDescriptor()
                .getControllerDescription().getTreatment();
        return result;
    }
    
    private Boolean isDescriptionUnique(String description){
        boolean isUnique = true;
        for (Treatment tws : getTreatment().get()){
            if (tws.getDescription()
                    .equalsIgnoreCase(description)){
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }
    
    private void doSendActionEvent(
            ViewController.TreatmentViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private Treatment doPrepForRequestTreatmentCreate(){
        Treatment result = null;
        String reply = null;
        reply = JOptionPane.showInternalInputDialog(this,"Enter new treatment");
        if (reply!=null){
            reply = reply.trim();
            if (!reply.isEmpty()){
                if(isDescriptionUnique(reply)){
                    Treatment treatment = new Treatment();
                    treatment.setDescription(reply);
                    result = treatment;
                }else JOptionPane.showInternalMessageDialog(
                            this, "Name of treatment is not unique");  
            }else JOptionPane.showInternalMessageDialog(
                            this, "Name of treatment cannot be blank"); 
        }else JOptionPane.showInternalMessageDialog(
                            this, "Name of treatment has not been defined"); 
        return result;
    }
    
    private Treatment doPrepForRequestTreatmentDelete(){
        Treatment result = null;
        if (this.tblTreatment.getSelectedRow()!=-1){
            int selectedRow = tblTreatment.getSelectedRow();
            TreatmentTableModel model = 
                    (TreatmentTableModel)tblTreatment.getModel();
            result = model.getElementAt(selectedRow);
            result.setIsDeleted(true);
        }
        return result;
    }
    
    private Treatment doPrepForRequestTreatmentRename(){
        Treatment treatment = null;
        TreatmentTableModel model = 
                (TreatmentTableModel)tblTreatment.getModel();
        Treatment result = null;
        String reply = null;
        if (this.tblTreatment.getSelectedRow()!=-1){
            int selectedRow = tblTreatment.getSelectedRow();
            treatment = model.getElementAt(selectedRow);
            reply = JOptionPane.showInternalInputDialog(
                    this,"Enter new name for '" + treatment.getDescription() + "'");
            if (reply!=null){
                reply = reply.trim();
                if (!reply.isEmpty()){
                    if(isDescriptionUnique(reply)){
                        treatment.setDescription(reply);
                        result = treatment;
                        tblTreatment.clearSelection();
                    }else JOptionPane.showInternalMessageDialog(
                                this, "Name of treatment is not unique");  
                }else JOptionPane.showInternalMessageDialog(
                                this, "Name of treatment cannot be blank"); 
            }else JOptionPane.showInternalMessageDialog(
                                this, "Name of treatment has not been defined"); 
        }else JOptionPane.showInternalMessageDialog(
                                this, "Treatment to be renamed has not been selected");
        return result;
    }
    
    
    
    private void populateTreatmentTable(){
        ArrayList<Treatment> treatments = new ArrayList<>();
        TreatmentTableModel model = 
                (TreatmentTableModel)this.tblTreatment.getModel();
        model.removeAllElements();
        treatments = getMyController().getDescriptor()
                .getControllerDescription().getTreatment().get();
        Iterator it = treatments.iterator();
        while(it.hasNext()){
            Treatment tws = (Treatment)it.next();
            model.addElement(tws);
        }
        model.fireTableDataChanged();
        tblTreatment.clearSelection();
        pnlMainView.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMainView = new javax.swing.JPanel();
        scrTreatmentTable = new javax.swing.JScrollPane();
        tblTreatment = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnAddTreatment = new javax.swing.JButton();
        btnRenameTreatment = new javax.swing.JButton();
        btnDeleteTreatment = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        tblTreatment.setModel(new TreatmentTableModel());
        tblTreatment.getModel().addTableModelListener(this);
        scrTreatmentTable.setViewportView(tblTreatment);

        pnlActions.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAddTreatment.setText("<html><center>Add</center><center>new</center><center>treatment</center></html>");

        btnRenameTreatment.setText("<html><center>Rename</center><center>selected</center><center>treatment</center></html>");

        btnDeleteTreatment.setText("<html><center>Delete</center><center>selected</center><center>treatment</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRenameTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnAddTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRenameTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlMainViewLayout = new javax.swing.GroupLayout(pnlMainView);
        pnlMainView.setLayout(pnlMainViewLayout);
        pnlMainViewLayout.setHorizontalGroup(
            pnlMainViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrTreatmentTable, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlMainViewLayout.setVerticalGroup(
            pnlMainViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMainViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrTreatmentTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMainView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMainView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddTreatment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeleteTreatment;
    private javax.swing.JButton btnRenameTreatment;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlMainView;
    private javax.swing.JScrollPane scrTreatmentTable;
    private javax.swing.JTable tblTreatment;
    // End of variables declaration//GEN-END:variables
}
