/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.ViewController;
import com.github.lgooddatepicker.components.DatePickerSettings;
import colinhewlettsolutions.client.controller.Descriptor;
import static colinhewlettsolutions.client.controller.ViewController.NotificationViewControllerPropertyChangeEvent.RECEIVED_PATIENT_NOTIFICATION;
import static colinhewlettsolutions.client.controller.ViewController.NotificationViewControllerPropertyChangeEvent.RECEIVED_PATIENT_NOTIFICATIONS;
import static colinhewlettsolutions.client.controller.ViewController.ViewMode.CREATE;
import static colinhewlettsolutions.client.controller.ViewController.ViewMode.Create;
import static colinhewlettsolutions.client.controller.ViewController.ViewMode.UPDATE;
import static colinhewlettsolutions.client.controller.ViewController.ViewMode.Update;
import java.time.LocalDate;
import javax.swing.ImageIcon; 
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.model.entity.ToDo;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
//import view.views.view_support_classes.renderers.NotificationEditorTableLocalDateRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
/**
 *
 * @author colin
 */
public class ModalToDoEditorView extends ModalView implements ActionListener{
    private ViewController.ViewMode viewMode = null;
    
    private static LocalDate date = null;
    public static LocalDate getDate(){
        return date;
    }
    
    enum Action{
        REQUEST_CREATE_UPDATE_TO_DO,
        REQUEST_CLOSE_VIEW
    }
    public static void setDate(LocalDate value){
        date = value;
    }
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalToDoEditorView(View.Viewer myViewType, 
            ViewController myController, 
            DesktopView desktopView) {
        //ViewMode arg
        setTitle("'To do' list editor");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
        initComponents();
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch(actionCommand){
            case REQUEST_CREATE_UPDATE_TO_DO:{
                doCreateUpdateToDo();
                break;
            }
            case REQUEST_CLOSE_VIEW:
                doCloseView();
                break;
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        //setViewDescriptor((Descriptor)e.getNewValue());
        ViewController.ToDoViewControllerPropertyChangeEvent propertyName =
                ViewController.ToDoViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case RECEIVED_TO_DO:
                doReceivedToDo();
                break;
        }
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        javax.swing.JButton datePickerButton = dpToDoDate.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        setVisible(true);
        setSize(591,222);
        this.btnCloseView.setText("<html><center>Close</center>view</center></html>");
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnCloseView.addActionListener(this);
        this.btnCreateUpdateTask.setText(
                "<html><center>Create</center><center>new task</center></html>");
        this.btnCreateUpdateTask.setActionCommand(Action.REQUEST_CREATE_UPDATE_TO_DO.toString());
        this.btnCreateUpdateTask.addActionListener(this);
        this.btnCreateUpdateTask.setEnabled(true);
        
        switch(getMyController().getDescriptor()
                .getControllerDescription().getViewMode()){
            case CREATE:
                break;
            case UPDATE:
                break;   
        }      
        
        pnlToDoDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "To do list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));
        
        pnlToDoActions.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "'To do' actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));
        
        doReceivedToDo();
    }
    
    /**
     * On entry method assumes the patient selector has been initialised
     * -- the received patient notification is used to initialise the ui
     * 
     */
    private void doReceivedToDo(){
        ToDo toDo = (ToDo)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.TO_DO);
        if (toDo==null) {
            setViewMode(ViewController.ViewMode.Create);
            this.btnCreateUpdateTask.setText("<html><center>Create</center><center>new task</center></html>");
        }
        else {
            setViewMode(ViewController.ViewMode.Update);
            dpToDoDate.setDate(toDo.getDate());
            this.txaToDoDescription.setText(toDo.getDescription());
            if (toDo.getIsActioned())
                this.rdbToDoActioned.setSelected(true);
            else this.rdbToDoUnactioned.setSelected(false);
            this.btnCreateUpdateTask.setText("<html><center>Update</center><center>task</center></html>");
        }
    }
    
    private ViewController.ViewMode getViewMode(){
        return this.viewMode;
    }

    public void setViewMode(ViewController.ViewMode value){
        this.viewMode = value;
        switch (viewMode){
            case Create:
                this.btnCreateUpdateTask.
                        setText("<html><center>Create</center><center>new task</center></html>");
                //this.pnlRadioButtons.setEnabled(true);
                this.rdbToDoActioned.setEnabled(false);
                this.rdbToDoUnactioned.setEnabled(false);
                break;
            case Update:
                btnCreateUpdateTask.
                        setText("<html><center>Update</center><center>task</center></html>"); 
                this.rdbToDoActioned.setEnabled(false);
                this.rdbToDoUnactioned.setEnabled(false);
        }
    }
    
    private void doCreateUpdateToDo(){
        if (getViewMode().equals(ViewController.ViewMode.Create))
            doRequestNewToDo();
        else doRequestUpdateToDo();
    }
    
    private void doCloseView(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.NotificationViewControllerActionEvent.MODAL_VIEWER_DEACTIVATED.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void doRequestNewToDo(){
        if (this.doValidateToDoRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.ToDoViewControllerActionEvent.TO_DO_EDITOR_CREATE_TO_DO_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }       
    }
    
    private void doRequestUpdateToDo(){
        if (doValidateToDoRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.ToDoViewControllerActionEvent.
                        TO_DO_EDITOR_UPDATE_TO_DO_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    
    private boolean doValidateToDoRequest(){
        boolean result = true;
        if (this.dpToDoDate.getDate()==null){
            result = false;
            JOptionPane.showInternalMessageDialog(
                this, "A valid notificaion date has not been defined", 
                "View error",
                JOptionPane.WARNING_MESSAGE);
        }
        if (result){
            if (this.txaToDoDescription.getText().trim().isEmpty()){
                result = false;
                JOptionPane.showInternalMessageDialog(
                    this, "A 'to do' task has not been defined", 
                    "View error",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        if (result){
            ToDo toDo;
            if (getViewMode().equals(ViewController.ViewMode.Create))
                toDo = new ToDo();
            else
                toDo = (ToDo)getMyController().
                        getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.TO_DO);
            toDo.setDate(this.dpToDoDate.getDate());
            toDo.setDescription(
                    this.txaToDoDescription.getText());
            toDo.setIsActioned(rdbToDoActioned.isSelected());
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.TO_DO, toDo);
        }
        else getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.TO_DO, null);
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

        pnlToDoActions = new javax.swing.JPanel();
        btnCreateUpdateTask = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        pnlToDoDetails = new javax.swing.JPanel();
        dpToDoDate = new com.github.lgooddatepicker.components.DatePicker();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaToDoDescription = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        pnlRadioButtons = new javax.swing.JPanel();
        rdbToDoUnactioned = new javax.swing.JRadioButton();
        rdbToDoActioned = new javax.swing.JRadioButton();

        pnlToDoActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnCreateUpdateTask.setText("jButton1");

        btnCloseView.setText("jButton1");
        btnCloseView.setToolTipText("");

        javax.swing.GroupLayout pnlToDoActionsLayout = new javax.swing.GroupLayout(pnlToDoActions);
        pnlToDoActions.setLayout(pnlToDoActionsLayout);
        pnlToDoActionsLayout.setHorizontalGroup(
            pnlToDoActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToDoActionsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlToDoActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreateUpdateTask, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        pnlToDoActionsLayout.setVerticalGroup(
            pnlToDoActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToDoActionsLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnCreateUpdateTask, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlToDoDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("'to do' details"));

        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowKeyboardEditing(false);
        dpToDoDate.setSettings(settings);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);

        jLabel1.setText("Action by date");

        txaToDoDescription.setColumns(20);
        txaToDoDescription.setLineWrap(true);
        txaToDoDescription.setRows(5);
        txaToDoDescription.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaToDoDescription);

        jLabel2.setText("Task description");

        pnlRadioButtons.setBorder(javax.swing.BorderFactory.createTitledBorder("Has been actioned?"));

        rdbToDoUnactioned.setText("no");

        rdbToDoActioned.setText("yes");

        javax.swing.GroupLayout pnlRadioButtonsLayout = new javax.swing.GroupLayout(pnlRadioButtons);
        pnlRadioButtons.setLayout(pnlRadioButtonsLayout);
        pnlRadioButtonsLayout.setHorizontalGroup(
            pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRadioButtonsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbToDoActioned)
                    .addComponent(rdbToDoUnactioned))
                .addGap(49, 49, 49))
        );
        pnlRadioButtonsLayout.setVerticalGroup(
            pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRadioButtonsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(rdbToDoUnactioned)
                .addGap(18, 18, 18)
                .addComponent(rdbToDoActioned)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlToDoDetailsLayout = new javax.swing.GroupLayout(pnlToDoDetails);
        pnlToDoDetails.setLayout(pnlToDoDetailsLayout);
        pnlToDoDetailsLayout.setHorizontalGroup(
            pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToDoDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlToDoDetailsLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dpToDoDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlRadioButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlToDoDetailsLayout.setVerticalGroup(
            pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlToDoDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlRadioButtons, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlToDoDetailsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dpToDoDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(12, 12, 12)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlToDoDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlToDoActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlToDoActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlToDoDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateTask;
    private com.github.lgooddatepicker.components.DatePicker dpToDoDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlRadioButtons;
    private javax.swing.JPanel pnlToDoActions;
    private javax.swing.JPanel pnlToDoDetails;
    private javax.swing.JRadioButton rdbToDoActioned;
    private javax.swing.JRadioButton rdbToDoUnactioned;
    private javax.swing.JTextArea txaToDoDescription;
    // End of variables declaration//GEN-END:variables
}
