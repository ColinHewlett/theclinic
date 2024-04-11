/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.modal_views;

import controller.ViewController;
import model.Treatment;
import model.Appointment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JOptionPane;
import model.TreatmentWithState;
import view.View;
import view.views.non_modal_views.DesktopView;
import view.views.view_support_classes.models.MedicalHistoryTableModel;
import view.views.view_support_classes.models.TreatmentWithStateTableModel;
/**
 *
 * @author colin
 */
public class ModalAppointmentTreatmentView extends ModalView 
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
    public ModalAppointmentTreatmentView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    } 
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        this.lblTreatmentNote.setVisible(false);
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblTreatmentWithState.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                
                TreatmentWithStateTableModel model = 
                        (TreatmentWithStateTableModel)tblTreatmentWithState.getModel();
                TreatmentWithState tws = model.getElementAt(selectedRow);
                String comment = tws.getComment();
                if (comment!=null){
                    if(!comment.trim().isEmpty()){
                        this.lblTreatmentNote.setText("Treatment note:- " + comment);
                        this.lblTreatmentNote.setVisible(true);
                    }
                    else {
                        this.lblTreatmentNote.setVisible(false);
                    }
                }else this.lblTreatmentNote.setVisible(false);
            }
        }
    }
    
    public void actionPerformed(ActionEvent e){
        TreatmentWithState tws = null;
        ViewController.ScheduleViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            case REQUEST_TREATMENT_COMMENT_UPDATE:
                tws = doPrepForRequestTreatmentCommentUpdate();
                if (tws!=null){
                    setTreatmentWithState(tws);
                    actionCommand = ViewController.ScheduleViewControllerActionEvent
                        .APPOINTMENT_TREATMENT_COMMENT_UPDATE_REQUEST;
                    doSendActionEvent(actionCommand);
                    try{
                        this.setClosed(true);   
                    }catch (PropertyVetoException ex){

                    }
                }
                break;
            case REQUEST_TREATMENT_CREATE:
                tws = doPrepForRequestTreatmentCreate();
                if (tws!=null){
                    setTreatmentWithState(tws);
                    actionCommand = ViewController.ScheduleViewControllerActionEvent
                        .TREATMENT_CREATE_REQUEST;
                    doSendActionEvent(actionCommand);
                    /*11/04/2024 10:15
                    String message = "Treatment '" 
                            + tws.getTreatment().getDescription() +"' has been created";
                    JOptionPane.showInternalMessageDialog(this, message );
                    
                    try{
                        this.setClosed(true);   
                    }catch (PropertyVetoException ex){

                    }
                    */
                }
                break;
            case REQUEST_TREATMENT_DELETE:
                setIsErrorMessageReceived(false);
                tws = doPrepForRequestTreatmentDelete();
                if (tws != null){
                    setTreatmentWithState(tws);
                    actionCommand = ViewController.ScheduleViewControllerActionEvent
                        .APPOINTMENT_TREATMENT_DELETE_REQUEST;
                    doSendActionEvent(actionCommand);
                    if (getIsErrorMessageReceived()){
                        if (getMyController().getDescriptor()
                                .getControllerDescription().getError()!=null){
                            JOptionPane.showInternalMessageDialog(this,getMyController()
                                    .getDescriptor().getControllerDescription()
                                    .getError(),"Appointment treatment deletion error",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                break;
            case REQUEST_TREATMENT_RENAME:
                tws = doPrepForRequestTreatmentRename();
                if (tws!=null){
                    setTreatmentWithState(tws);
                    actionCommand = ViewController.ScheduleViewControllerActionEvent
                        .APPOINTMENT_TREATMENT_NAME_UPDATE_REQUEST;
                    doSendActionEvent(actionCommand);
                }
                break;
        }
    }
    
    
    @Override 
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ScheduleViewControllerPropertyChangeEvent propertyName =
                ViewController.ScheduleViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case APPOINTMENT_SCHEDULE_ERROR_RECEIVED:
                setIsErrorMessageReceived(true);
                break;
            case APPOINTMENT_TREATMENT_WITH_STATE_RECEIVED:
                populateTreatmentTable();
                break;
        }
    }
    
    public void tableChanged(TableModelEvent e){
        ViewController.ScheduleViewControllerActionEvent actionCommand = null;
        if (e.getSource() instanceof TreatmentWithStateTableModel){
            TreatmentWithState treatmentWithState = null;
            int row = e.getFirstRow();
            int column = e.getColumn();
            TreatmentWithStateTableModel model =  
                    (TreatmentWithStateTableModel)e.getSource();
            Boolean value = (Boolean)model.getValueAt(row, column);
            treatmentWithState = (TreatmentWithState)model.getElementAt(row);
            treatmentWithState.setState(value);
            getMyController().getDescriptor().getViewDescription().setTreatmentWithState(treatmentWithState);
            tblTreatmentWithState.clearSelection();
            if (treatmentWithState.getState())
                actionCommand = ViewController.ScheduleViewControllerActionEvent
                        .APPOINTMENT_TREATMENT_STATE_SET_REQUEST;
            else actionCommand = ViewController.ScheduleViewControllerActionEvent
                        .APPOINTMENT_TREATMENT_STATE_RESET_REQUEST;
            doSendActionEvent(actionCommand);
        }
    }

    @Override
    public void initialiseView(){
        initComponents();
        this.setClosable(true);
        setTitle("Treatment for " 
                + getAppointee() + " on " + getAppointmentDate());
        
        setVisible(true);
        setSize(412, 463);
        
        this.lblTreatmentNote.setVisible(false);
        
        btnClose.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnAddTreatment.setActionCommand(
                Action.REQUEST_TREATMENT_CREATE.toString());
        this.btnAddTreatmentComment.setActionCommand(
                Action.REQUEST_TREATMENT_COMMENT_UPDATE.toString());
        this.btnClose.setActionCommand(
                Action.REQUEST_CLOSE_VIEW.toString());
        this.btnDeleteTreatment.setActionCommand(
                Action.REQUEST_TREATMENT_DELETE.toString());
        this.btnRenameTreatment.setActionCommand(
                Action.REQUEST_TREATMENT_RENAME.toString());
        this.btnAddTreatment.addActionListener(this);
        this.btnAddTreatmentComment.addActionListener(this);
        btnClose.addActionListener(this);
        this.btnDeleteTreatment.addActionListener(this);
        this.btnRenameTreatment.addActionListener(this);
        
        ListSelectionModel lsm = this.tblTreatmentWithState.getSelectionModel();
        lsm.addListSelectionListener(this);
        
        this.tblTreatmentWithState.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblTreatmentWithState.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblTreatmentWithState.isRowSelected(selectedRow))
                    tblTreatmentWithState.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        
        populateTreatmentTable(); 
    }

    private Appointment getAppointment(){
        return getMyController().getDescriptor()
                .getControllerDescription().getAppointment();
    }
    
    
    private String getAppointmentDate(){
        LocalDate date = getMyController().getDescriptor().getControllerDescription()
                .getAppointment().getStart().toLocalDate();
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
    }
    
    private String getAppointee(){
        return getMyController().getDescriptor().getControllerDescription()
                .getAppointment().getAppointeeName();
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
    
    private void setTreatmentWithState(TreatmentWithState tws){
        getMyController().getDescriptor()
                .getViewDescription().setTreatmentWithState(tws);
    }
    
    private TreatmentWithState getTreatmentWithState(){
        TreatmentWithState result = null;
        result = getMyController().getDescriptor()
                .getControllerDescription().getTreatmentWithState();
        return result;
    }
    
    private Boolean isDescriptionUnique(String description){
        boolean isUnique = true;
        for (TreatmentWithState tws : getTreatmentWithState().get()){
            if (tws.getTreatment().getDescription()
                    .equalsIgnoreCase(description)){
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }
    
    private void doSendActionEvent(
            ViewController.ScheduleViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private TreatmentWithState doPrepForRequestTreatmentCommentUpdate(){
        String reply = null;
        TreatmentWithState result = null;
        if (this.tblTreatmentWithState.getSelectedRow()!=-1){
            int selectedRow = tblTreatmentWithState.getSelectedRow();
            TreatmentWithStateTableModel model = 
                    (TreatmentWithStateTableModel)tblTreatmentWithState.getModel();
            result = model.getElementAt(selectedRow);
            if (result.getComment()!=null)
                reply = JOptionPane.showInternalInputDialog(
                        this,"","Enter treatment note",JOptionPane.INFORMATION_MESSAGE);
            else reply = JOptionPane.showInternalInputDialog(
                        this,result.getComment(),"Edit treatment note",JOptionPane.INFORMATION_MESSAGE);
            if (reply!=null)
                result.setComment(reply);
        }
        return result;
    }
    
    private TreatmentWithState doPrepForRequestTreatmentCreate(){
        TreatmentWithState result = null;
        String reply = null;
        reply = JOptionPane.showInternalInputDialog(this,"Enter new treatment");
        if (reply!=null){
            reply = reply.trim();
            if (!reply.isEmpty()){
                if(isDescriptionUnique(reply)){
                    result = new TreatmentWithState();
                    Treatment treatment = new Treatment();
                    treatment.setDescription(reply);
                    result.setTreatment(treatment);
                }else JOptionPane.showInternalMessageDialog(
                            this, "Name of treatment is not unique");  
            }else JOptionPane.showInternalMessageDialog(
                            this, "Name of treatment cannot be blank"); 
        }else JOptionPane.showInternalMessageDialog(
                            this, "Name of treatment has not been defined"); 
        return result;
    }
    
    private TreatmentWithState doPrepForRequestTreatmentDelete(){
        TreatmentWithState result = null;
        if (this.tblTreatmentWithState.getSelectedRow()!=-1){
            int selectedRow = tblTreatmentWithState.getSelectedRow();
            TreatmentWithStateTableModel model = 
                    (TreatmentWithStateTableModel)tblTreatmentWithState.getModel();
            result = model.getElementAt(selectedRow);
            result.getTreatment().setIsDeleted(true);
        }
        return result;
    }
    
    private TreatmentWithState doPrepForRequestTreatmentRename(){
        TreatmentWithState result = null;
        String reply = null;
        if (this.tblTreatmentWithState.getSelectedRow()!=-1){
            reply = JOptionPane.showInternalInputDialog(
                    this,"Enter new name for selected treatment");
            if (reply!=null){
                reply = reply.trim();
                if (!reply.isEmpty()){
                    if(isDescriptionUnique(reply)){
                        int selectedRow = tblTreatmentWithState.getSelectedRow();
                        TreatmentWithStateTableModel model = 
                                (TreatmentWithStateTableModel)tblTreatmentWithState.getModel();
                        result = model.getElementAt(selectedRow);
                        result.getTreatment().setDescription(reply);
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
        ArrayList<TreatmentWithState> treatmentsWithState = new ArrayList<>();
        TreatmentWithStateTableModel model = 
                (TreatmentWithStateTableModel)this.tblTreatmentWithState.getModel();
        model.removeAllElements();
        treatmentsWithState = getMyController().getDescriptor()
                .getControllerDescription().getTreatmentWithState().get();
        Iterator it = treatmentsWithState.iterator();
        while(it.hasNext()){
            TreatmentWithState tws = (TreatmentWithState)it.next();
            model.addElement(tws);
        }
        /*11/04/2024 10:15* fix*/
        model.removeTableModelListener(this);
        model.fireTableDataChanged();
        model.addTableModelListener(this);
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTreatmentTable = new javax.swing.JPanel();
        scrTreatment = new javax.swing.JScrollPane();
        tblTreatmentWithState = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnAddTreatment = new javax.swing.JButton();
        btnDeleteTreatment = new javax.swing.JButton();
        btnRenameTreatment = new javax.swing.JButton();
        btnAddTreatmentComment = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblTreatmentNote = new javax.swing.JLabel();

        tblTreatmentWithState.setModel(new TreatmentWithStateTableModel());
        tblTreatmentWithState.getModel().addTableModelListener(this);
        scrTreatment.setViewportView(tblTreatmentWithState);
        ViewController.setJTableColumnProperties(
            tblTreatmentWithState, scrTreatment.getPreferredSize().width, 20,80);

        btnAddTreatment.setText("<html><center>Add</center><center>new</center><center>treatment</center></html>");
        btnAddTreatment.setVerifyInputWhenFocusTarget(false);

        btnDeleteTreatment.setText("<html><center>Delete</center><center>selected</center><center>treatment</center></html>");

        btnRenameTreatment.setText("<html><center>Rename</center><center>selected</center><center>treatment</center></html>");

        btnAddTreatmentComment.setText("<html><center>Add note</center><center>to selected</center><center>treatment</center></html>");

        btnClose.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddTreatment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRenameTreatment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteTreatment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddTreatmentComment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnAddTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnRenameTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnDeleteTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnAddTreatmentComment, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnRenameTreatment.getAccessibleContext().setAccessibleDescription("");
        btnAddTreatmentComment.getAccessibleContext().setAccessibleDescription("");
        btnClose.getAccessibleContext().setAccessibleDescription("");

        lblTreatmentNote.setText("Treatment note");

        javax.swing.GroupLayout pnlTreatmentTableLayout = new javax.swing.GroupLayout(pnlTreatmentTable);
        pnlTreatmentTable.setLayout(pnlTreatmentTableLayout);
        pnlTreatmentTableLayout.setHorizontalGroup(
            pnlTreatmentTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTreatmentTableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTreatmentTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTreatmentTableLayout.createSequentialGroup()
                        .addComponent(scrTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlTreatmentTableLayout.createSequentialGroup()
                        .addComponent(lblTreatmentNote)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlTreatmentTableLayout.setVerticalGroup(
            pnlTreatmentTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTreatmentTableLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lblTreatmentNote)
                .addGap(7, 7, 7)
                .addGroup(pnlTreatmentTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrTreatment, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlTreatmentTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlTreatmentTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddTreatment;
    private javax.swing.JButton btnAddTreatmentComment;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDeleteTreatment;
    private javax.swing.JButton btnRenameTreatment;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTreatmentNote;
    private javax.swing.JPanel pnlTreatmentTable;
    private javax.swing.JScrollPane scrTreatment;
    private javax.swing.JTable tblTreatmentWithState;
    // End of variables declaration//GEN-END:variables
}
