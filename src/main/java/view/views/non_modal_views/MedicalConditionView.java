/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import model.entity.SecondaryCondition;
import model.entity.PrimaryCondition;
import model.entity.Condition;
import model.entity.Treatment;
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
import model.*;
import view.View;
import view.support_classes.models.MedicalConditionTableModel;

/**
 *
 * @author colin
 */
public class MedicalConditionView extends View 
            implements ActionListener, 
                   /*TableModelListener, */
                   ListSelectionListener,
                   PropertyChangeListener {
    private boolean tableValueChangedListenerActivated = false;
    enum ConditionViewMode{PRIMARY,SECONDARY}
    enum Action {
        REQUEST_CLOSE_VIEW,
        REQUEST_CONDITION_CREATE,
        REQUEST_CONDITION_DELETE,
        REQUEST_CONDITION_RENAME,
        REQUEST_VIEW_MODE_CHANGE
    }
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public MedicalConditionView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    } 
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblCondition.getSelectedRow();
                switch(getConditionViewMode()){
                    case PRIMARY:
                        if (selectedRow!=-1){
                            MedicalConditionTableModel model = 
                                    (MedicalConditionTableModel)tblCondition.getModel();
                            PrimaryCondition pc = (PrimaryCondition)model.getElementAt(selectedRow);
                            SecondaryCondition sc = pc.getSecondaryCondition();
                            if (!sc.get().isEmpty())
                                this.btnToggleBetweenPrimaryAndSecondaryViews.setEnabled(true);
                            else
                                this.btnToggleBetweenPrimaryAndSecondaryViews.setEnabled(false);
                            this.btnDeleteMedicalCondition.setEnabled(true);
                            this.btnRenameMedicalCondition.setEnabled(true);
                        }else {
                            this.btnToggleBetweenPrimaryAndSecondaryViews.setEnabled(false);
                            this.btnDeleteMedicalCondition.setEnabled(false);
                            this.btnRenameMedicalCondition.setEnabled(false);
                        }
                        break;
                    case SECONDARY:
                        this.btnToggleBetweenPrimaryAndSecondaryViews.setEnabled(true);
                        if (selectedRow!=-1){
                            this.btnDeleteMedicalCondition.setEnabled(true);
                            this.btnRenameMedicalCondition.setEnabled(true);  
                        }else{
                            this.btnDeleteMedicalCondition.setEnabled(true);
                            this.btnRenameMedicalCondition.setEnabled(true); 
                        }
                        break;  
                }

            
        }
    }
    
    public void actionPerformed(ActionEvent e){
        String ViewError = null;
        String ErrorTitle = null;
        PrimaryCondition primaryCondition = null;
        ViewController.MedicalConditionViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                        .VIEW_CLOSE_NOTIFICATION;
                    doSendActionEvent(actionCommand);
                break;
            case REQUEST_CONDITION_CREATE:{
                setIsErrorMessageReceived(false);
                switch (getConditionViewMode()){
                    case PRIMARY:
                        primaryCondition = doPrepForRequestPrimaryConditionCreate();
                        if (primaryCondition!=null){
                            setPrimaryCondition(primaryCondition);
                            actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                                    .PRIMARY_CONDITION_CREATE_REQUEST;
                            doSendActionEvent(actionCommand);
                        }else{
                            ViewError = "Medical condition '" 
                                    + getRejectedConditionName() + "' is not unique";
                            ErrorTitle = "Primary condition create error";
                        setIsErrorMessageReceived(true);
                        }
                        break;
                    case SECONDARY:
                        primaryCondition = doPrepForRequestSecondaryConditionCreate();
                        if (primaryCondition!=null){
                            setPrimaryCondition(primaryCondition);
                            actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                                    .SECONDARY_CONDITION_CREATE_REQUEST;
                            doSendActionEvent(actionCommand);
                        }else{
                            ViewError = "Medical condition '" 
                                    + getRejectedConditionName() + "' is not unique";
                            ErrorTitle = "Secondary condition create error";
                            setIsErrorMessageReceived(true);
                        }
                        break;
                }
                if (getIsErrorMessageReceived()){
                    JOptionPane.showInternalMessageDialog(this, 
                            ViewError,ErrorTitle,JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case REQUEST_CONDITION_DELETE:
                setIsErrorMessageReceived(false);
                switch(getConditionViewMode()){
                    case PRIMARY:
                        primaryCondition = doPrepForRequestPrimaryConditionDelete();
                        setPrimaryCondition(primaryCondition);
                        actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                                .PRIMARY_CONDITION_DELETE_REQUEST;
                        doSendActionEvent(actionCommand);
                        break;
                    case SECONDARY:
                        primaryCondition = doPrepForRequestSecondaryConditionDelete();
                        setPrimaryCondition(primaryCondition);
                        actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                                .SECONDARY_CONDITION_DELETE_REQUEST;
                        doSendActionEvent(actionCommand);
                        break;
                }
                if(getIsErrorMessageReceived()){
                    JOptionPane.showInternalMessageDialog(this, 
                            getMyController().getDescriptor()
                                    .getControllerDescription().getError(),
                            "Medical condition delete error",JOptionPane.WARNING_MESSAGE);
                }
                setIsErrorMessageReceived(false); //don;'y want this VC derror confusded with View generated errors
                break;
            case REQUEST_CONDITION_RENAME:
                switch(getConditionViewMode()){
                    case PRIMARY:
                        primaryCondition = doPrepForRequestPrimaryConditionRename();
                        if (primaryCondition!=null){
                            setPrimaryCondition(primaryCondition);
                            actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                                    .PRIMARY_CONDITION_RENAME_REQUEST;
                            doSendActionEvent(actionCommand);
                        }else setIsErrorMessageReceived(true);
                        if (getIsErrorMessageReceived())
                            if (getSelectedCondition()!=null){
                                ViewError = "Medical condition '" 
                                        + getRejectedConditionName() + "' is not unique";
                                ErrorTitle = "Primary condition rename error";
                            }else{ 
                                ViewError = "A medical condition has not been selected for renaming";
                                ErrorTitle = "Primary condition rename error";
                            }
                        break;
                    case SECONDARY:
                        primaryCondition = doPrepForRequestSecondaryConditionRename();
                        if (primaryCondition!=null){
                            setPrimaryCondition(primaryCondition);
                            actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                                    .SECONDARY_CONDITION_RENAME_REQUEST;
                            doSendActionEvent(actionCommand);
                        }else setIsErrorMessageReceived(true);
                        if (getIsErrorMessageReceived()){
                            if (getSelectedCondition()!=null){
                                ViewError = "Medical condition '" 
                                        + getRejectedConditionName() + "' is not unique";
                                ErrorTitle = "Secondary condition rename error";
                            }else{
                                ViewError = "A medical condition has not been selected for renaming";
                                ErrorTitle = "Secondary condition rename error";
                            }
                        }
                        break;
                }
                if (getIsErrorMessageReceived()){
                    JOptionPane.showInternalMessageDialog(this, 
                            ViewError,ErrorTitle,JOptionPane.WARNING_MESSAGE);
                }
                break;
            case REQUEST_VIEW_MODE_CHANGE:
                switch(getConditionViewMode()) {
                    case PRIMARY:
                        setConditionViewMode(ConditionViewMode.SECONDARY);
                        break;
                    case SECONDARY:
                        setConditionViewMode(ConditionViewMode.PRIMARY);
                        break;       
                }
                break;   
        }
    }

    @Override 
    public void propertyChange(PropertyChangeEvent e){
        ViewController.MedicalConditionViewControllerPropertyChangeEvent propertyName =
                ViewController.MedicalConditionViewControllerPropertyChangeEvent
                        .valueOf(e.getPropertyName());
        switch (propertyName){
            case MEDICAL_CONDITION_VIEW_CONTROLLER_ERROR_RECEIVED:
                setIsErrorMessageReceived(true);
                /*JOptionPane.showInternalMessageDialog(this, 
                        getMyController().getDescriptor()
                                .getControllerDescription().getError(),
                        "View controller error", JOptionPane.WARNING_MESSAGE);*/
                break;
            case PRIMARY_CONDITION_RECEIVED:
                populateConditionTable();
                break;
        }
    }

    @Override
    public void initialiseView(){
        initComponents();
        this.setClosable(true);
        //setTitle("Medical conditions");
        setVisible(true);
        setSize(414, 463);
        setConditionViewMode(ConditionViewMode.PRIMARY);
        pnlActions.setBackground(new java.awt.Color(220, 220, 220));
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnAddMedicalCondition.setActionCommand(
                Action.REQUEST_CONDITION_CREATE.toString());
        this.btnDeleteMedicalCondition.setActionCommand(
                Action.REQUEST_CONDITION_DELETE.toString());
        this.btnRenameMedicalCondition.setActionCommand(
                Action.REQUEST_CONDITION_RENAME.toString());
        this.btnToggleBetweenPrimaryAndSecondaryViews
                .setActionCommand(Action.REQUEST_VIEW_MODE_CHANGE.toString());
        this.btnToggleBetweenPrimaryAndSecondaryViews.addActionListener(this);
        this.btnAddMedicalCondition.addActionListener(this);
        btnCloseView.addActionListener(this);
        this.btnDeleteMedicalCondition.addActionListener(this);
        this.btnRenameMedicalCondition.addActionListener(this);

        ListSelectionModel lsm = this.tblCondition.getSelectionModel();
        lsm.addListSelectionListener(this);

        /*
        this.tblCondition.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblCondition.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblCondition.isRowSelected(selectedRow))
                    tblCondition.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });*/

        setConditionViewMode(ConditionViewMode.PRIMARY);
    }
    
    private boolean isErrorMessageReceived = false;
    private boolean getIsErrorMessageReceived(){
        return isErrorMessageReceived;
    }
    private void setIsErrorMessageReceived(boolean value){
        isErrorMessageReceived = value;
    }

    private ConditionViewMode conditionViewMode = null;
    private ConditionViewMode getConditionMode(){
        return conditionViewMode;
    }
    private void setConditionViewMode(ConditionViewMode value){
        ViewController.MedicalConditionViewControllerActionEvent actionCommand = null;
        conditionViewMode = value;
        switch(conditionViewMode){
            case PRIMARY:
                setTitle("Medical conditions (primary options)");
                this.btnToggleBetweenPrimaryAndSecondaryViews.setText(
                        "<html><center>View</center><center>secondary</center>"
                                + "<center>conditions</center></html>");
                this.btnDeleteMedicalCondition.setEnabled(false);
                this.btnRenameMedicalCondition.setEnabled(false);
                this.btnRenameMedicalCondition.setEnabled(false);
                this.btnToggleBetweenPrimaryAndSecondaryViews.setEnabled(false);
                actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                        .PRIMARY_CONDITION_READ_REQUEST;
                doSendActionEvent(actionCommand);
                break;
            case SECONDARY:
                PrimaryCondition pc = null;
                setTitle("Medical conditions (secondary options)");
                Condition condition = getSelectedCondition();
                if (condition!=null){
                    pc = (PrimaryCondition)condition;
                    setPrimaryCondition(pc);
                }
                this.btnToggleBetweenPrimaryAndSecondaryViews.setText(
                        "<html><center>View</center><center>primary</center>"
                                + "<center>conditions</center></html>");
                this.btnDeleteMedicalCondition.setEnabled(false);
                this.btnRenameMedicalCondition.setEnabled(false);
                this.btnRenameMedicalCondition.setEnabled(false);
                this.btnToggleBetweenPrimaryAndSecondaryViews.setEnabled(true);
                actionCommand = ViewController.MedicalConditionViewControllerActionEvent
                        .SECONDARY_CONDITION_READ_REQUEST;
                doSendActionEvent(actionCommand);
                break;
        }
    }
    private ConditionViewMode getConditionViewMode(){
        return conditionViewMode;
    }
    
    private void setPrimaryCondition(PrimaryCondition pc){
        getMyController().getDescriptor()
                .getViewDescription().setPrimaryCondition(pc);
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
    
    private PrimaryCondition getPrimaryCondition(){
        PrimaryCondition result = null;
        result = getMyController().getDescriptor()
                .getControllerDescription().getPrimaryCondition();
        return result;
    }
    
    private Boolean isPrimaryConditionDescriptionUnique(String description){
        boolean isUnique = true;
        for (Condition c : getPrimaryCondition().get()){
            if (c.getDescription().equalsIgnoreCase(description)){
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }
    
    private Boolean isSecondaryConditionDescriptionUnique(String description){
        boolean isUnique = true;
        for (Condition c : getPrimaryCondition().getSecondaryCondition().get()){
            if (c.getDescription().equalsIgnoreCase(description)){
                isUnique = false;
                break;
            }
        }
        return isUnique;
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
            ViewController.MedicalConditionViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private String conditionName = null;
    private void setRejectedConditionName(String value){
        conditionName = value;
    }
    private String getRejectedConditionName(){
        return conditionName;
    }
    
    private PrimaryCondition doPrepForRequestPrimaryConditionCreate(){
        PrimaryCondition result = null;
        String reply = null;
        reply = JOptionPane.showInternalInputDialog(this,"Enter new medical condition");
        if (reply!=null){
            reply = reply.trim();
            if (!reply.isEmpty()){
                if(isPrimaryConditionDescriptionUnique(reply)){
                    PrimaryCondition pc = new PrimaryCondition();
                    pc.setDescription(reply);
                    result = pc;
                }else {
                    result = null;
                    setRejectedConditionName(reply);
                }  
            }else JOptionPane.showInternalMessageDialog(
                            this, "Name of medical condition cannot be blank"); 
        }else JOptionPane.showInternalMessageDialog(
                            this, "Name of medica condition has not been defined"); 
        return result;
    }
    
    private PrimaryCondition doPrepForRequestSecondaryConditionCreate(){
        PrimaryCondition result = null;
        String reply = null;
        reply = JOptionPane.showInternalInputDialog(this,"Enter new medical condition");
        if (reply!=null){
            reply = reply.trim();
            if (!reply.isEmpty()){
                if(isSecondaryConditionDescriptionUnique(reply)){
                    SecondaryCondition sc = new SecondaryCondition(getPrimaryCondition());
                    sc.setDescription(reply);
                    PrimaryCondition pc = getPrimaryCondition();
                    pc.setSecondaryCondition(sc);
                    result = pc;
                }else {
                    result = null;
                    setRejectedConditionName(reply);
                }  
            }else JOptionPane.showInternalMessageDialog(
                            this, "Name of medical condition cannot be blank"); 
        }else JOptionPane.showInternalMessageDialog(
                            this, "Name of medica condition has not been defined"); 
        return result;
    }
    
    private PrimaryCondition doPrepForRequestPrimaryConditionDelete(){
        PrimaryCondition pc = null;
        if (this.tblCondition.getSelectedRow()!=-1){
            int selectedRow = tblCondition.getSelectedRow();
            MedicalConditionTableModel model = 
                    (MedicalConditionTableModel)tblCondition.getModel();
            pc = (PrimaryCondition)model.getElementAt(selectedRow);
        }  
        return pc;
    }
    
    private PrimaryCondition doPrepForRequestSecondaryConditionDelete(){
        PrimaryCondition pc = null;
        SecondaryCondition sc = null;
        if (this.tblCondition.getSelectedRow()!=-1){
            int selectedRow = tblCondition.getSelectedRow();
            MedicalConditionTableModel model = 
                    (MedicalConditionTableModel)tblCondition.getModel();
            sc = (SecondaryCondition)model.getElementAt(selectedRow);
            pc = getPrimaryCondition();
            pc.setSecondaryCondition(sc);
        }
        return pc;
    }
    
    private PrimaryCondition doPrepForRequestPrimaryConditionRename(){
        PrimaryCondition pc = null;
        String reply = null;
        if (this.tblCondition.getSelectedRow()!=-1){
            int selectedRow = tblCondition.getSelectedRow();
            MedicalConditionTableModel model = 
                    (MedicalConditionTableModel)tblCondition.getModel();
            pc = (PrimaryCondition)model.getElementAt(selectedRow);
            reply = JOptionPane.showInternalInputDialog(
                    this,"Enter new name for '" + pc.getDescription() + "'");
            if (reply!=null){
                reply = reply.trim();
                if (!reply.isEmpty()){
                    if(isPrimaryConditionDescriptionUnique(reply)){
                        pc.setDescription(reply);
                        tblCondition.clearSelection();
                    }else {
                        pc = null;
                        setRejectedConditionName(reply);
                    }
                }else JOptionPane.showInternalMessageDialog(
                                this, "Name of medical condition cannot be blank"); 
            }else JOptionPane.showInternalMessageDialog(
                                this, "Name of medical condition has not been defined"); 
        }else pc = null;
        return pc;
    }
    
    private Condition getSelectedMedicalCondition(){
        if (this.tblCondition.getSelectedRow()!=-1){
            int selectedRow = tblCondition.getSelectedRow();
            MedicalConditionTableModel model = 
                    (MedicalConditionTableModel)tblCondition.getModel();
            return model.getElementAt(selectedRow);
        }else return null;
    }
    
    private PrimaryCondition doPrepForRequestSecondaryConditionRename(){
        PrimaryCondition pc = null;
        SecondaryCondition sc = null;
        String reply = null;
        if (this.tblCondition.getSelectedRow()!=-1){
            int selectedRow = tblCondition.getSelectedRow();
            MedicalConditionTableModel model = 
                    (MedicalConditionTableModel)tblCondition.getModel();
            sc = (SecondaryCondition)model.getElementAt(selectedRow);
            reply = JOptionPane.showInternalInputDialog(
                    this,"Enter new name for '" + sc.getDescription() + "'");
            if (reply!=null){
                reply = reply.trim();
                if (!reply.isEmpty()){
                    if(isSecondaryConditionDescriptionUnique(reply)){
                        sc.setDescription(reply);
                        pc = getPrimaryCondition();
                        pc.setSecondaryCondition(sc);
                        tblCondition.clearSelection();
                    }else {
                        pc = null;
                        setRejectedConditionName(reply);
                    }  
                }else JOptionPane.showInternalMessageDialog(
                                this, "Name of medical condition cannot be blank"); 
            }else JOptionPane.showInternalMessageDialog(
                                this, "Name of medical condition has not been defined"); 
        }else pc = null;

        return pc;
    }

    private void populateConditionTable(){
        ArrayList<Condition> conditions = new ArrayList<>();
        switch(getConditionViewMode()){
            case PRIMARY:
                conditions = getMyController().getDescriptor()
                        .getControllerDescription()
                        .getPrimaryCondition().get();
                break;
            case SECONDARY:
                conditions = getMyController().getDescriptor()
                        .getControllerDescription()
                        .getPrimaryCondition().getSecondaryCondition().get();
                this.btnDeleteMedicalCondition.setEnabled(false);
                this.btnRenameMedicalCondition.setEnabled(false);
                this.btnRenameMedicalCondition.setEnabled(false);
                break;
        }
        tblCondition = new javax.swing.JTable();
        tblCondition.setModel(new MedicalConditionTableModel());
        scrConditionTable.setViewportView(tblCondition);
        MedicalConditionTableModel model = 
                (MedicalConditionTableModel)this.tblCondition.getModel();
        ListSelectionModel lsm = this.tblCondition.getSelectionModel();
        lsm.addListSelectionListener(this);
        Iterator it = conditions.iterator();
        while(it.hasNext()){
            Condition condition = (Condition)it.next();
            model.addElement(condition);
        }
        tblCondition.clearSelection();
        //tblCondition.repaint();
    }

    private Condition getSelectedCondition(){
        Condition result = null;
        int selectedRow = 0;
        selectedRow = this.tblCondition.getSelectedRow();
        if (selectedRow!=-1){
            MedicalConditionTableModel model = 
                    (MedicalConditionTableModel)tblCondition.getModel();
            result = model.getElementAt(selectedRow);
        }
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

        pnlMainView = new javax.swing.JPanel();
        scrConditionTable = new javax.swing.JScrollPane();
        tblCondition = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnAddMedicalCondition = new javax.swing.JButton();
        btnRenameMedicalCondition = new javax.swing.JButton();
        btnDeleteMedicalCondition = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnToggleBetweenPrimaryAndSecondaryViews = new javax.swing.JButton();

        tblCondition.setModel(new MedicalConditionTableModel());
        //tblCondition.getModel().addTableModelListener(this);
        scrConditionTable.setViewportView(tblCondition);

        pnlActions.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAddMedicalCondition.setText("<html><center>Add new</center><center>medical</center><center>condition</center></html>");

        btnRenameMedicalCondition.setText("<html><center>Rename</center><center>medical</center><center>condition</center></html>");

        btnDeleteMedicalCondition.setText("<html><center>Delete</center><center>medical</center><center>condition</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        btnToggleBetweenPrimaryAndSecondaryViews.setText("<html><center>View</center><center>secondary</center><center>conditions</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAddMedicalCondition)
                    .addComponent(btnRenameMedicalCondition)
                    .addComponent(btnDeleteMedicalCondition)
                    .addComponent(btnCloseView)
                    .addComponent(btnToggleBetweenPrimaryAndSecondaryViews, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(btnAddMedicalCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnRenameMedicalCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnDeleteMedicalCondition, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnToggleBetweenPrimaryAndSecondaryViews, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlMainViewLayout = new javax.swing.GroupLayout(pnlMainView);
        pnlMainView.setLayout(pnlMainViewLayout);
        pnlMainViewLayout.setHorizontalGroup(
            pnlMainViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainViewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrConditionTable, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlMainViewLayout.setVerticalGroup(
            pnlMainViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMainViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrConditionTable, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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
    private javax.swing.JButton btnAddMedicalCondition;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeleteMedicalCondition;
    private javax.swing.JButton btnRenameMedicalCondition;
    private javax.swing.JButton btnToggleBetweenPrimaryAndSecondaryViews;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlMainView;
    private javax.swing.JScrollPane scrConditionTable;
    private javax.swing.JTable tblCondition;
    // End of variables declaration//GEN-END:variables
}
