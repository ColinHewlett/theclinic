/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.modal_views;

import controller.ViewController;
import com.github.lgooddatepicker.components.DatePickerSettings;
import static controller.ViewController.ViewMode.CREATE;
import static controller.ViewController.ViewMode.Create;
import static controller.ViewController.ViewMode.UPDATE;
import static controller.ViewController.ViewMode.Update;
import java.time.LocalDate;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import model.non_entity.SystemDefinition;
import view.View;
import view.views.non_modal_views.DesktopView;
//import view.views.view_support_classes.renderers.NotificationEditorTableLocalDateRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public void initialiseView(){
        initComponents();
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = dpToDoDate.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        setVisible(true);
        
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
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N;
        
        pnlToDoActions.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(), 
                "'To do' actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                SystemDefinition.TITLED_BORDER_FONT, 
                SystemDefinition.TITLED_BORDER_COLOR));// NOI18N;
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
                        setText("<html><center>Create</center><center>new task</center></html>"); 
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
                ViewController.NotificationViewControllerActionEvent.
                        NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }       
    }
    
    private void doRequestUpdateToDo(){
        if (doValidateToDoRequest()){
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotificationViewControllerActionEvent.
                        NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }
    
    private boolean doValidateToDoRequest(){
        boolean result = true;
        if (this.dpToDoDate.getDate()==null){
            result = false;
            JOptionPane.showInternalMessageDialog(
                this, "A valid date has not been defined", 
                "View error error",
                JOptionPane.WARNING_MESSAGE);
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

        pnlToDoActions = new javax.swing.JPanel();
        btnCreateUpdateTask = new javax.swing.JButton();
        btnCloseTask = new javax.swing.JButton();
        pnlToDoDetails = new javax.swing.JPanel();
        dpToDoDate = new com.github.lgooddatepicker.components.DatePicker();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        pnlRadioButtons = new javax.swing.JPanel();
        rdbToDoUnactioned = new javax.swing.JRadioButton();
        rdbToDoActioned = new javax.swing.JRadioButton();

        pnlToDoActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnCreateUpdateTask.setText("jButton1");

        btnCloseTask.setText("jButton1");

        javax.swing.GroupLayout pnlToDoActionsLayout = new javax.swing.GroupLayout(pnlToDoActions);
        pnlToDoActions.setLayout(pnlToDoActionsLayout);
        pnlToDoActionsLayout.setHorizontalGroup(
            pnlToDoActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToDoActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlToDoActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreateUpdateTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseTask, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlToDoActionsLayout.setVerticalGroup(
            pnlToDoActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToDoActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCreateUpdateTask, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseTask, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlToDoDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("'to do' details"));

        settings = new DatePickerSettings();
        dpToDoDate = new com.github.lgooddatepicker.components.DatePicker(settings);
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        settings.setAllowEmptyDates(false);
        //settings.setVetoPolicy(new AppointmentDateVetoPolicy());
        settings.setAllowKeyboardEditing(false);

        jLabel1.setText("Action by date");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel2.setText("Task description");

        pnlRadioButtons.setBorder(javax.swing.BorderFactory.createTitledBorder("Has been actioned"));

        rdbToDoUnactioned.setText("no");

        rdbToDoActioned.setText("yes");

        javax.swing.GroupLayout pnlRadioButtonsLayout = new javax.swing.GroupLayout(pnlRadioButtons);
        pnlRadioButtons.setLayout(pnlRadioButtonsLayout);
        pnlRadioButtonsLayout.setHorizontalGroup(
            pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRadioButtonsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(pnlRadioButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbToDoUnactioned, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rdbToDoActioned, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(49, Short.MAX_VALUE))
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
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlRadioButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlToDoDetailsLayout.setVerticalGroup(
            pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlToDoDetailsLayout.createSequentialGroup()
                .addGroup(pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlToDoDetailsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlRadioButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlToDoDetailsLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(pnlToDoDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dpToDoDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlToDoDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlToDoActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlToDoDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlToDoActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseTask;
    private javax.swing.JButton btnCreateUpdateTask;
    private com.github.lgooddatepicker.components.DatePicker dpToDoDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel pnlRadioButtons;
    private javax.swing.JPanel pnlToDoActions;
    private javax.swing.JPanel pnlToDoDetails;
    private javax.swing.JRadioButton rdbToDoActioned;
    private javax.swing.JRadioButton rdbToDoUnactioned;
    // End of variables declaration//GEN-END:variables
}
