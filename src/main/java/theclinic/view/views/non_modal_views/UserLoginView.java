/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.non_modal_views;

import theclinic.controller.SystemDefinition.Properties;
import theclinic.controller.ViewController;
import theclinic.controller.PatientViewController;
import theclinic.controller.LoginViewController;
import theclinic.model.non_entity.Credential;
import theclinic.view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.KeyStroke;

/**
 *
 * @author colin
 */
public class UserLoginView extends View 
                           implements ActionListener, PropertyChangeListener {

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public UserLoginView(
            View.Viewer myViewType, ViewController myController, DesktopView desktopView ) {
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        switch(Action.valueOf(e.getActionCommand())){
            case FORGOT_PASSWORD ->{
                Credential credential = new Credential(this.txtUsername.getText(), this.txtPassword.getPassword());
                JOptionPane.showInternalMessageDialog(this, "A temporary password has been emailed to the clinic admin", 
                        "Forgotten password", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            case REQUEST_LOGIN ->{
                Credential credential = new Credential(this.txtUsername.getText(), this.txtPassword.getPassword());
                credential.setUsername(credential.getUsername().substring(0,1).toUpperCase() + credential.getUsername().substring(1));
                getMyController().getDescriptor().getViewDescription().
                        setProperty(Properties.LOGIN_CREDENTIAL, credential);
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        LoginViewController.Actions.LOGIN_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        String s = e.getSource().getClass().getSimpleName();
        LoginViewController.Properties property = 
                LoginViewController.Properties.valueOf(e.getPropertyName());
        switch(property){
            case CORRECT_LOGIN_CREDENTIAL_RECEIVED ->{
                try{
                    this.setClosed(true);
                }catch (PropertyVetoException ex){
                    ViewController.displayErrorMessage("Error in UserLoginView::propetyChasnge() method", "View error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
            case INCORRECT_PASSWORD_RECEIVED ->{
                JOptionPane.showInternalMessageDialog(
                        this, 
                        "Password invalid (possibly contains blanks)", 
                        "View controller error", 
                        JOptionPane.WARNING_MESSAGE);
                break;
            }
            case INCORRECT_USERNAME_RECEIVED ->{
                JOptionPane.showInternalMessageDialog(
                        this, 
                        "Username missing or contains blanks", 
                        "View cxontroller error", 
                        JOptionPane.WARNING_MESSAGE);
                break;
            }
            case USERNAME_NOT_FOUND ->{
                JOptionPane.showInternalMessageDialog(
                        this, 
                        "Username not found", 
                        "View controller error", 
                        JOptionPane.WARNING_MESSAGE);
                break;
            }
            case VIEW_CLOSE_RECEIVED ->{
                doCloseView();
                break;
            }
        }
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setVisible(true);
        this.btnForgotPassword.setActionCommand(Action.FORGOT_PASSWORD.toString());
        this.btnForgotPassword.addActionListener(this);
        this.btnEnterDetails.setActionCommand(Action.REQUEST_LOGIN.toString());
        this.btnEnterDetails.addActionListener(this);
        addInternalFrameListeners();
        getMyController().getDescriptor().getViewDescription().setProperty(Properties.LOGIN_CREDENTIAL, null);
        
        // Make Enter key trigger the Enter button
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "pressEnter");
        actionMap.put("pressEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEnterDetails.doClick();
            }
        });
    }
    
    private void doCloseView(){
        try{
            this.setClosed(true);
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    LoginViewController.Actions.VIEW_CLOSED_NOTIFICATION.toString());
            getMyController().actionPerformed(actionEvent);
        }
        catch (PropertyVetoException ex){

        }
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
                        UserLoginView.this,ActionEvent.ACTION_PERFORMED,
                        LoginViewController.Actions.VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        UserLoginView.this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }

    enum Action{
        REQUEST_LOGIN,
        FORGOT_PASSWORD
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        btnEnterDetails = new javax.swing.JButton();
        btnForgotPassword = new javax.swing.JButton();

        setTitle("User login");
        setToolTipText("");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 204, 255)));

        jLabel1.setText("User name");

        jLabel2.setText("Password");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(txtUsername)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 204, 255)));

        btnEnterDetails.setText("<html><center>Enter</center><center>details</center></html>");

        btnForgotPassword.setText("<html><center>Forgot</center><center>password?</center></html>");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnForgotPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(btnEnterDetails))
                .addGap(12, 12, 12))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(btnEnterDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnForgotPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnterDetails;
    private javax.swing.JButton btnForgotPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
