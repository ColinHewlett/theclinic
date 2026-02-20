/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.controller.LoginViewController;
import colinhewlettsolutions.client.model.non_entity.Credential;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.view.views.non_modal_views.UserLoginView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author colin
 */
public class LoginNewPasswordEditorView extends View
                                            implements ActionListener, PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public LoginNewPasswordEditorView( 
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        LoginViewController.Actions actionRequestToController = null;
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch (actionCommand){
            case REQUEST_CANCEL ->{
                try{//closes down view and controller
                    this.setClosed(true);
                }catch (PropertyVetoException ex){

                }
                break;
            }
            case REQUEST_NEXT ->{
                switch (getViewMode()){
                    case NEW_PASSWORD_CHECK ->{
                        Credential credential = new Credential();
                        credential.setPassword(this.txtNewPassword.getPassword());
                        credential.setPasswordReentry(this.txtNewPasswordRentry.getPassword());
                        setViewCredential(credential);
                        actionRequestToController = LoginViewController.Actions.NEW_PASSWORD_REQUEST;
                        doSendActionEvent(actionRequestToController);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        LoginViewController.Properties property = 
                LoginViewController.Properties.valueOf(e.getPropertyName());
        switch (property){
            case NEW_CREDENTIAL_VALIDATION_RECEIVED ->{
                Object[] options = {"OK"};
                JOptionPane.showInternalOptionDialog(
                        this, 
                        "Password updated", 
                        "Password validation",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]);
                
                
                try{//closes down view and controller
                    this.setClosed(true);
                }catch (PropertyVetoException ex){

                }
                break;
            }
            case INCORRECT_PASSWORD_RECEIVED ->{
                JOptionPane.showInternalMessageDialog(
                        this,
                        "New password entered invalid (possibly contains blanks)", 
                        "View controller error", JOptionPane.WARNING_MESSAGE);
                break;
            }
            case INCORRECT_PASSWORD_VALIDATION_RECEIVED ->{
                JOptionPane.showInternalMessageDialog(
                        this,
                        "Reentered password not the same as the entered password", 
                        "View controller error", JOptionPane.WARNING_MESSAGE);
                break;
            }
        }
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setVisible(true);
        addInternalFrameListeners();
        this.btnCancelRequest.setActionCommand(Action.REQUEST_CANCEL.toString());
        this.btnCancelRequest.addActionListener(this);
        this.btnNewPasswordRequest.setActionCommand(Action.REQUEST_NEXT.toString());
        this.btnNewPasswordRequest.addActionListener(this);
    }
    
    private ViewController.LoginViewMode getViewMode(){
        return (ViewController.LoginViewMode)getMyController().getDescriptor().
                getControllerDescription().getProperty(Properties.LOGIN_VIEW_MODE);
    }
    
    private Credential getViewCredential(){
        return (Credential)getMyController().getDescriptor().
                getViewDescription().getProperty(Properties.LOGIN_CREDENTIAL);
    }
    private void setViewCredential(Credential value){
        getMyController().getDescriptor().
                getViewDescription().setProperty(Properties.LOGIN_CREDENTIAL, value);
    }
    
    
    private void doSendActionEvent(
        LoginViewController.Actions actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private InternalFrameAdapter internalFrameAdapter = null;
    private void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                /**
                 * NO ACTION REQUIRED; UPDATED CODE AS FOLLOWS
                 * -- on close view request the VIEW_CLOSE_NOTIFICATION is sent to the controller immediately
                 * -- the controller will send the view.setClosed(true) message to close the view before closing the view controller
                 */
                
                ActionEvent actionEvent = new ActionEvent(
                        LoginNewPasswordEditorView.this,ActionEvent.ACTION_PERFORMED,
                        LoginViewController.Actions.VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    enum Action{
        REQUEST_CANCEL,
        REQUEST_NEXT
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlChangePassword = new javax.swing.JPanel();
        txtNewPassword = new javax.swing.JPasswordField();
        txtNewPasswordRentry = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pnlAction = new javax.swing.JPanel();
        btnNewPasswordRequest = new javax.swing.JButton();
        btnCancelRequest = new javax.swing.JButton();

        setTitle("Password editor");

        pnlChangePassword.setBorder(javax.swing.BorderFactory.createTitledBorder("Change password"));

        jLabel1.setText("New password");

        jLabel2.setText("Copy new password");

        javax.swing.GroupLayout pnlChangePasswordLayout = new javax.swing.GroupLayout(pnlChangePassword);
        pnlChangePassword.setLayout(pnlChangePasswordLayout);
        pnlChangePasswordLayout.setHorizontalGroup(
            pnlChangePasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChangePasswordLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlChangePasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(txtNewPasswordRentry, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(txtNewPassword))
                .addGap(12, 12, 12))
        );
        pnlChangePasswordLayout.setVerticalGroup(
            pnlChangePasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChangePasswordLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNewPasswordRentry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        pnlAction.setBorder(javax.swing.BorderFactory.createTitledBorder("Action"));

        btnNewPasswordRequest.setText("Enter");

        btnCancelRequest.setText("Cancel");

        javax.swing.GroupLayout pnlActionLayout = new javax.swing.GroupLayout(pnlAction);
        pnlAction.setLayout(pnlActionLayout);
        pnlActionLayout.setHorizontalGroup(
            pnlActionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlActionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancelRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNewPasswordRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        pnlActionLayout.setVerticalGroup(
            pnlActionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNewPasswordRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnCancelRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        btnNewPasswordRequest.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(pnlChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlChangePassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelRequest;
    private javax.swing.JButton btnNewPasswordRequest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnlAction;
    private javax.swing.JPanel pnlChangePassword;
    private javax.swing.JPasswordField txtNewPassword;
    private javax.swing.JPasswordField txtNewPasswordRentry;
    // End of variables declaration//GEN-END:variables
}
