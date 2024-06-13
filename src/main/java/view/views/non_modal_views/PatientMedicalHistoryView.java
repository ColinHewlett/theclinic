/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import model.entity.SecondaryCondition;
import model.entity.PrimaryCondition;
import model.entity.PatientCondition;
import model.entity.Patient;
import model.non_entity.SecondaryConditionWithState;
import model.non_entity.PrimaryConditionWithState;
import model.non_entity.ConditionWithState;
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
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
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
    private final String PANEL_VIEWER_TITLE = "Patient comments on condition";
    private final String CANCEL_COMMENT_MODE_TITLE = 
            "<html><center>Cancel</center><center>upload</center><center>comment</center></html>";;
    private final String EDIT_COMMENT_MODE_TITLE = 
            "<html><center>Edit</center><center>patient</center><center>comment</center></html>";
    enum ToggleMode{PRIMARY,SECONDARY,OTHER}
    enum ConditionViewMode{PRIMARY, SECONDARY};
    enum PatientCommentMode{CANCEL, EDIT};
    enum CommentEditMode{ON,OFF}
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_DELETE_PATIENT_CONDITION_COMMENT, 
        REQUEST_TOGGLE_CONDITION_VIEW,
        REQUEST_CANCEL_EDIT_PATIENT_CONDITION_COMMENT,
        REQUEST_UPLOAD_PATIENT_CONDITION_COMMENT
        };
    
    private CommentEditMode commentEditMode = null;
    private void setCommentEditMode(CommentEditMode value){
        commentEditMode = value;
        if (value.equals(CommentEditMode.ON)) {
            this.btnUploadPatientComment.setEnabled(true);
            this.btnCancelEditPatientComment.setText(CANCEL_COMMENT_MODE_TITLE);
            this.txaComment.setEditable(true);
            setCachedComment(txaComment.getText());
        }else{
            this.btnUploadPatientComment.setEnabled(false);
            this.btnCancelEditPatientComment.setText(this.EDIT_COMMENT_MODE_TITLE);
            this.txaComment.setEditable(false);
        }    
    }
    private CommentEditMode getCommentEditMode(){
        return commentEditMode;
    }
    
    private void setTitledBorderFor(JPanel panel, String heading, String condition){
        String title = null;
        TitledBorder tb = (TitledBorder)panel.getBorder();
        if (heading != null){
            title = "Patient comment on " + heading + " (" + condition + ")";
            //title = title + "       (click 'Edit patient comment' button to edit comment)";
            tb.setTitle(title);
        }
        else tb.setTitle(this.PANEL_VIEWER_TITLE);
        panel.repaint();
    }
    
    private String cachedComment = "";
    private void setCachedComment(String value){
        cachedComment = value;
    }
    private String getCachedComment(){
        return cachedComment;
    }
    
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
        
        TitledBorder tb = (TitledBorder)this.pnlPatientCommentViewer.getBorder();
        tb.setTitleFont(getBorderTitleFont());
        tb.setTitleColor(getBorderTitleColor());
        tb.setTitle(this.PANEL_VIEWER_TITLE);
        
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnDeleteCommentFromCondition.setActionCommand(Action.REQUEST_DELETE_PATIENT_CONDITION_COMMENT.toString());
        this.btnCancelEditPatientComment.setActionCommand(Action.REQUEST_CANCEL_EDIT_PATIENT_CONDITION_COMMENT.toString());
        this.btnToggleConditionView.setActionCommand(Action.REQUEST_TOGGLE_CONDITION_VIEW.toString());
        this.btnUploadPatientComment.setActionCommand(Action.REQUEST_UPLOAD_PATIENT_CONDITION_COMMENT.toString());
        this.btnCloseView.addActionListener(this);
        this.btnDeleteCommentFromCondition.addActionListener(this);
        this.btnCancelEditPatientComment.addActionListener(this);
        this.btnToggleConditionView.addActionListener(this);
        this.btnUploadPatientComment.addActionListener(this);
        
        ListSelectionModel lsm = this.tblCondition.getSelectionModel();
        lsm.addListSelectionListener(this);
        
        ConditionWithState cws = new ConditionWithState();
        cws.setCondition(new PrimaryCondition());
        setConditionWithState(cws);
        setConditionViewMode(ConditionViewMode.PRIMARY);
        setCommentEditMode(CommentEditMode.OFF);
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
            
            case REQUEST_CANCEL_EDIT_PATIENT_CONDITION_COMMENT:
                cws = getSelectedCondition();     
                if (cws!=null){
                    switch(getCommentEditMode()){
                        case ON:
                            setCommentEditMode(CommentEditMode.OFF);
                            txaComment.setText(getCachedComment());
                            break;
                        case OFF:
                            setCommentEditMode(CommentEditMode.ON);
                            break;
                    }
                }
                break;
                
            case REQUEST_DELETE_PATIENT_CONDITION_COMMENT:
                cws = getSelectedCondition();
                if (cws!=null){
                    cws.setComment(null);
                    setConditionWithState(cws);
                    doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                            .PATIENT_CONDITION_COMMENT_UPDATE_REQUEST);
                    this.txaComment.setText("");
                }
                else{
                    isError = true;
                    error = "Patient condition has not been defined;/n"
                                + "Action to delete condition comment aborted";
                }
                break;
                
            case REQUEST_UPLOAD_PATIENT_CONDITION_COMMENT:
                cws = getSelectedCondition();
                if (cws!=null){
                    cws.setComment(txaComment.getText());
                    setConditionWithState(cws);
                    doSendActionEvent(ViewController.PatientMedicalHistoryViewControllerActionEvent
                            .PATIENT_CONDITION_COMMENT_UPDATE_REQUEST);
                    setCommentEditMode(CommentEditMode.OFF);
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
        String heading = null;
        PrimaryCondition pc = null;
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblCondition.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                MedicalConditionWithStateTableModel model = 
                        (MedicalConditionWithStateTableModel)tblCondition.getModel();
                ConditionWithState cws = 
                        (ConditionWithState) model.getElementAt(selectedRow);
                heading = model.getConditionsColumnName();
                if (heading.charAt(heading.length()-1)=='s')
                    heading = heading.substring(0, heading.length()-1);
                
                if (cws.getState()){
                    if (cws.getComment()==null) txaComment.setText("");
                        else txaComment.setText(cws.getComment());
                    if (cws.getComment()!=null){
                        if (!cws.getComment().trim().isEmpty())
                            this.btnDeleteCommentFromCondition.setEnabled(true);
                    }else this.btnDeleteCommentFromCondition.setEnabled(false);
                    switch(getConditionViewMode()){
                        case PRIMARY://fetch secondariews linked to this primary (if any)
                            SecondaryCondition sc = ((PrimaryCondition)cws.getCondition()).getSecondaryCondition();
                            if (sc.get().isEmpty()){
                                this.btnCancelEditPatientComment.setEnabled(true);
                                setTitledBorderFor(pnlPatientCommentViewer,heading, cws.getCondition().getDescription());
                            }else{
                                this.btnCancelEditPatientComment.setEnabled(false);
                                setTitledBorderFor(pnlPatientCommentViewer, null, null);
                            }
                            break;
                        case SECONDARY:
                            this.btnCancelEditPatientComment.setEnabled(true);
                            setTitledBorderFor(pnlPatientCommentViewer,heading, cws.getCondition().getDescription());
                            break;
                    }   
                }else{
                    txaComment.setText("");
                    setTitledBorderFor(pnlPatientCommentViewer,null,null);
                    this.btnDeleteCommentFromCondition.setEnabled(false);
                    this.btnCancelEditPatientComment.setEnabled(false);
                    this.btnCancelEditPatientComment.setEnabled(false);
                }
                switch(getConditionViewMode()){
                    case PRIMARY:
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
                        
                        this.btnToggleConditionView.setEnabled(true);
                        setToggleMode(ToggleMode.PRIMARY);
                        break;
                }
            }
        }
    }
    
    private ConditionWithState doRequestConditionCommentUpdateEditor(ConditionWithState cws){
        ConditionWithState result = null;
        String comment = "";
        comment = JOptionPane.showInternalInputDialog(
                    this,"Enter new comment for '" + cws.getCondition().getDescription() + "'. After 50 characters printed line will get progressively shorter to fit in available space");
            if (comment!=null){
                comment = comment.trim();
                if (!comment.isEmpty()){
                    cws.setComment(comment);
                    result = cws;
                    tblCondition.clearSelection();  
                }else JOptionPane.showInternalMessageDialog(
                                this, "Updated of medical condition comment cannot be blank"); 
            }else JOptionPane.showInternalMessageDialog(
                                this, "Medical condition comment has not been defined");
        return result;
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
                this.btnCancelEditPatientComment.setEnabled(false);
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
                    this.btnCancelEditPatientComment.setEnabled(false);
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
                this.btnCancelEditPatientComment.setEnabled(false);
                this.btnToggleConditionView.setEnabled(false);
                setToggleMode(ToggleMode.SECONDARY);
                MedicalConditionWithStateTableModel.conditionsColumnName = "Condition";
                break;
            case SECONDARY:
                this.btnDeleteCommentFromCondition.setEnabled(false);
                this.btnCancelEditPatientComment.setEnabled(false);
                this.btnToggleConditionView.setEnabled(true);
                setToggleMode(ToggleMode.PRIMARY);
                SecondaryCondition sc = (SecondaryCondition)conditions.get(0).getCondition();
                MedicalConditionWithStateTableModel.conditionsColumnName = 
                        sc.getPrimaryCondition().getDescription() + " conditions";
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

        jScrollPane1 = new javax.swing.JScrollPane();
        scrConditionTable = new javax.swing.JScrollPane();
        tblCondition = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnCancelEditPatientComment = new javax.swing.JButton();
        btnDeleteCommentFromCondition = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnToggleConditionView = new javax.swing.JButton();
        pnlPatientCommentViewer = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaComment = new javax.swing.JTextArea();
        btnUploadPatientComment = new javax.swing.JButton();

        scrConditionTable.setViewportView(tblCondition);
        ViewController.setJTableColumnProperties(
            tblCondition, scrConditionTable.getPreferredSize().width, 20,80);

        pnlActions.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCancelEditPatientComment.setText("<html><center>Edit</center><center>patient</center><center>Comment</center></html>");
        btnCancelEditPatientComment.setMaximumSize(new java.awt.Dimension(87, 25));
        btnCancelEditPatientComment.setMinimumSize(new java.awt.Dimension(87, 25));
        btnCancelEditPatientComment.setPreferredSize(new java.awt.Dimension(87, 25));
        btnCancelEditPatientComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelEditPatientCommentActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancelEditPatientComment, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnDeleteCommentFromCondition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnToggleConditionView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnCancelEditPatientComment, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(btnDeleteCommentFromCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnToggleConditionView, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCancelEditPatientComment.getAccessibleContext().setAccessibleDescription("");

        pnlPatientCommentViewer.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient comment on condition"));

        txaComment.setColumns(20);
        txaComment.setRows(5);
        jScrollPane2.setViewportView(txaComment);
        txaComment.setEditable(false);
        txaComment.setLineWrap(true);
        txaComment.setWrapStyleWord(true);

        btnUploadPatientComment.setText("<html><center>Upload</center><center>patient</center><center>comment</center></html");
        btnUploadPatientComment.setMinimumSize(new java.awt.Dimension(87, 55));
        btnUploadPatientComment.setPreferredSize(new java.awt.Dimension(87, 55));

        javax.swing.GroupLayout pnlPatientCommentViewerLayout = new javax.swing.GroupLayout(pnlPatientCommentViewer);
        pnlPatientCommentViewer.setLayout(pnlPatientCommentViewerLayout);
        pnlPatientCommentViewerLayout.setHorizontalGroup(
            pnlPatientCommentViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPatientCommentViewerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnUploadPatientComment, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        pnlPatientCommentViewerLayout.setVerticalGroup(
            pnlPatientCommentViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPatientCommentViewerLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(pnlPatientCommentViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnUploadPatientComment, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlPatientCommentViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrConditionTable, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrConditionTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(pnlPatientCommentViewer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelEditPatientCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelEditPatientCommentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelEditPatientCommentActionPerformed

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCloseViewActionPerformed

    private void btnToggleConditionViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleConditionViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnToggleConditionViewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelEditPatientComment;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeleteCommentFromCondition;
    private javax.swing.JButton btnToggleConditionView;
    private javax.swing.JButton btnUploadPatientComment;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlPatientCommentViewer;
    private javax.swing.JScrollPane scrConditionTable;
    private javax.swing.JTable tblCondition;
    private javax.swing.JTextArea txaComment;
    // End of variables declaration//GEN-END:variables
}
