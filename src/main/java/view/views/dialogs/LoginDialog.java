/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view.views.dialogs;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;
import model.non_entity.SystemDefinition;
import model.non_entity.UserAuthentication;
import model.non_entity.Credential;
import controller.ViewController;
import view.views.non_modal_views.LoginView;
/**
 *
 * @author colin
 */
public class LoginDialog extends javax.swing.JDialog implements PropertyChangeListener, WindowListener{
    private boolean isSucceeded = false;
    private JFrame parentFrame = null;
    
    private LoginView loginView = null;
    private LoginView getLoginView(){
        return loginView;
    }

    
    /**
     * 
     * @param parent
     * @param view
     */
    public LoginDialog(javax.swing.JFrame parent, LoginView view) {
        super(parent, true);
        loginView = view;
        parentFrame = parent;
        parentFrame.setSize(320,260);
        initComponents();
        initialiseView();
    }
    
    public void propertyChange(PropertyChangeEvent e){
        String errorStatus = getLoginView().getMyController().getDescriptor()
                .getControllerDescription().getError();
        Credential loginCredential = getLoginView().getMyController().getDescriptor()
                .getControllerDescription().getLoginCredential();
        Credential newCredential = getLoginView().getMyController().getDescriptor()
                .getControllerDescription().getNewUserCredential();
        ViewController.LoginViewControllerPropertyChangeEvent propertyName =
                ViewController.LoginViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case INCORRECT_PASSWORD_RECEIVED:
                setErrorStatus(errorStatus);
                switch(getLoginViewMode()){
                    case LOGIN:
                        //this.chkUsername.setSelected(loginCredential.getIsUsernameValid());
                        //this.chkPassword.setSelected(loginCredential.getIsPasswordValid());
                        //this.chkPasswordReentry.setSelected(loginCredential.getIsPasswordReentryValid());
                        break;
                    case NEW_USER_OR_PASSWORD:
                        //this.chkUsername.setSelected(newCredential.getIsUsernameValid());
                        //this.chkPassword.setSelected(newCredential.getIsPasswordValid());
                        //this.chkPasswordReentry.setSelected(newCredential.getIsPasswordReentryValid());
                        break;
                }
                break;
            case DUPLICATE_USERNAME_RECEIVED:
                setErrorStatus(errorStatus);
                //this.chkUsername.setSelected(newCredential.getIsUsernameValid());
                //this.chkPassword.setSelected(newCredential.getIsPasswordValid());
                //this.chkPasswordReentry.setSelected(newCredential.getIsPasswordReentryValid());
                break;
            case INCORRECT_USERNAME_RECEIVED:
                setErrorStatus(errorStatus);
                switch(getLoginViewMode()){
                    case LOGIN:
                        //this.chkUsername.setSelected(loginCredential.getIsUsernameValid());
                        //this.chkPassword.setSelected(loginCredential.getIsPasswordValid());
                        //this.chkPasswordReentry.setSelected(loginCredential.getIsPasswordReentryValid());
                        break;
                    case NEW_USER_OR_PASSWORD:
                        //this.chkUsername.setSelected(newCredential.getIsUsernameValid());
                        //this.chkPassword.setSelected(newCredential.getIsPasswordValid());
                        //this.chkPasswordReentry.setSelected(newCredential.getIsPasswordReentryValid());
                        break;
                }
                break;
            case CORRECT_LOGIN_CREDENTIAL_RECEIVED:
                setErrorStatus("");
                this.txtUser.setText(loginCredential.getUsername());
                this.txtPassword.setText(loginCredential.getPassword());
                //this.chkUsername.setSelected(loginCredential.getIsUsernameValid());
                //this.chkPassword.setSelected(loginCredential.getIsPasswordValid());
                //this.chkPasswordReentry.setSelected(loginCredential.getIsPasswordReentryValid());
                setLoginViewMode(SystemDefinition.LoginViewMode.NEW_USER_OR_PASSWORD);
                initialiseLoginViewMode();
                break;
            case CORRECT_NEW_CREDENTIAL_RECEIVED:
                setErrorStatus("");
                //this.chkUsername.setSelected(newCredential.getIsUsernameValid());
                //this.chkPassword.setSelected(newCredential.getIsPasswordValid());
                //this.chkPasswordReentry.setSelected(newCredential.getIsPasswordReentryValid());
                break;
            case PASSWORD_CHANGE_RECEIVED:
                setErrorStatus("password successfully changed");
                break;
            case INCORRECT_PASSWORD_VALIDATION_RECEIVED:
                setErrorStatus(errorStatus);
                //this.chkUsername.setSelected(newCredential.getIsUsernameValid());
                //this.chkPassword.setSelected(newCredential.getIsPasswordValid());
                //this.chkPasswordReentry.setSelected(newCredential.getIsPasswordReentryValid());
                break;
        }
    }
    
    public void setErrorStatus(String value){
        if(value==null){
            this.lblErrorStatus.setText(" ");
        }
        else this.lblErrorStatus.setText(value);
    }
    
    private enum ClosureMode{
        EXIT_APP,
        LAUNCH_APP
    }
    
    private String feedback = "";
    private void setFeedback(String value){
        feedback = value;
    }

    public String getError(){
        return getLoginView().getMyController()
                        .getDescriptor().getViewDescription().getError();
    }
    
    

    public String getUsername(){
        return txtUser.getText();
    }
    
    public String getPassword(){
        String result = null;
        try{
            result = new String(txtPassword.getPassword());
        }catch (NullPointerException ex){
            result = null;
        }
        return result;
    }
    
    public String getPasswordReentry(){
        String result = null;
        try{
            result = new String(txtPasswordReentry.getPassword());
        }catch (NullPointerException ex){
            result = null;
        }
        return result;
    } 

    private Credential credential = null;
    private Credential getControllerCredential(){
        return null;
    }
    
    private ClosureMode closureMode = null;
    private void setClosureMode(ClosureMode value){
        closureMode = value;
        switch (closureMode){
            case EXIT_APP:
                this.btnCloseLaunchApp.setText("<html><center>Close></center><center>view</center></html>");
                break;
            case LAUNCH_APP:
                this.btnCloseLaunchApp.setText("<html><center>Launch></center><center>application</center></html>");
                break;
        }
    }
    private ClosureMode getClosureMode(){
        return closureMode;
    }
    
    private void initialiseView(){
        Border border = null;
        TitledBorder titledBorder = null;
        
        border = pnlCredentials.getBorder();
        titledBorder = (TitledBorder)border;
        titledBorder.setTitleFont(SystemDefinition.TITLED_BORDER_FONT);
        titledBorder.setTitleColor(SystemDefinition.TITLED_BORDER_COLOR);
        
        border = pnlOptions.getBorder();
        titledBorder = (TitledBorder)border;
        titledBorder.setTitleFont(SystemDefinition.TITLED_BORDER_FONT);
        titledBorder.setTitleColor(SystemDefinition.TITLED_BORDER_COLOR);
        
        border = pnlErrorStatus.getBorder();
        titledBorder = (TitledBorder)border;
        titledBorder.setTitleFont(SystemDefinition.TITLED_BORDER_FONT);
        titledBorder.setTitleColor(SystemDefinition.TITLED_BORDER_COLOR);
        
        //credentialMode = SystemDefinition.CredentialMode.LOGIN;
        
        getLoginView().getMyController().getDescriptor().getViewDescription().setLoginDialog(this);
        
        initialiseLoginViewMode();
        
        btnLogin.setActionCommand(SystemDefinition.LoginAction.REQUEST_LOGIN.toString());
        btnCloseLaunchApp.setActionCommand(SystemDefinition.LoginAction.REQUEST_CLOSE_LAUNCH_APP.toString());
        btnNewUser.setActionCommand(SystemDefinition.LoginAction.REQUEST_ADD_NEW_CREDENTIAL.toString());
        btnNewPassword.setActionCommand(SystemDefinition.LoginAction.REQUEST_UPDATE_CREDENTIAL.toString());
        btnLogin.addActionListener(getLoginView());
        btnNewUser.addActionListener(getLoginView());
        btnNewPassword.addActionListener(getLoginView());
        btnCloseLaunchApp.addActionListener(getLoginView());
        
        setTitle("Enter username and password");
        this.toFront();
        this.setVisible(true);
        
        // Create a single instance of the custom DocumentListener
        MyDocumentListener documentListener = new MyDocumentListener();

        // Add each text field to the listener
        documentListener.addTextField(TextControl.USER_NAME, txtUser);
        documentListener.addTextField(TextControl.PASSWORD, txtPassword);
        documentListener.addTextField(TextControl.PASSWORD_REENTRY, txtPasswordReentry);
        
        txtUser.getDocument().addDocumentListener(documentListener);
        txtPassword.getDocument().addDocumentListener(documentListener);
        txtPasswordReentry.getDocument().addDocumentListener(documentListener);
    }
    
    protected enum TextControl{
        USER_NAME,
        PASSWORD,
        PASSWORD_REENTRY;
    }
    
    private void initialiseLoginViewMode(){
        switch(getLoginViewMode()){
            case LOGIN:
                this.btnLogin.setEnabled(true);
                this.btnNewUser.setEnabled(false);
                this.btnNewPassword.setEnabled(false);
                this.lblPasswordReentry.setEnabled(false);
                this.txtPasswordReentry.setEnabled(false);
                break;
            case NEW_USER_OR_PASSWORD:
                this.setTitle("logged in user -> '"
                        + getLoginView().getMyController().getDescriptor()
                                .getControllerDescription().getLoginCredential()
                                .getUsername() + "'");
                this.btnLogin.setEnabled(false);
                this.btnNewUser.setEnabled(true);
                this.btnNewPassword.setEnabled(true);
                //this.txtUser.setText("");
                //this.txtPassword.setText("");
                getLoginView().getMyController().getDescriptor()
                        .getViewDescription().setNewUserCredential(new Credential());
                //this.chkPassword.setSelected(false);
                //this.chkUsername.setSelected(false);
                this.lblPasswordReentry.setEnabled(true);
                this.txtPasswordReentry.setEnabled(true);
                this.btnCloseLaunchApp.setText("<html><center>Open</center><center>theclinic</center></html>");
                break;
        }
    }

    private SystemDefinition.LoginViewMode getLoginViewMode(){
        return getLoginView().getMyController().getDescriptor().getViewDescription().getLoginViewMode();
    }
    
    private void setLoginViewMode(SystemDefinition.LoginViewMode value){
        getLoginView().getMyController().getDescriptor().
                getViewDescription().setLoginViewMode(value);
    }
    
    private Point centreOnScreen(){
        getWidth();
       // Manually center the dialog on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = this.getPreferredSize();
        int x = (screenSize.width - parentFrame.getWidth()) / 2;
        int y = (screenSize.height - parentFrame.getHeight()) / 2; 
        return new Point(x,y);
    }
    
    @Override
    public void windowClosed(WindowEvent e){
        ActionEvent actionEvent = new ActionEvent(
        this,ActionEvent.ACTION_PERFORMED,
                SystemDefinition.LoginAction.REQUEST_CLOSE_LAUNCH_APP.toString());
        getLoginView().actionPerformed(actionEvent);
        
    }
    
    @Override 
    public void windowClosing(WindowEvent e){
;
    }
    
    @Override
    public void windowOpened(WindowEvent e){

    }
    
    @Override
    public void windowActivated(WindowEvent e){
        
    }
    
    @Override
    public void windowDeactivated(WindowEvent e){
        
    }
    
    @Override
    public void windowDeiconified(WindowEvent e){
        
    }
    
    @Override
    public void windowIconified(WindowEvent e){
        
    }

    public void setIsSucceeded(boolean value){
        isSucceeded = value;
    }
    public boolean getIsSucceeded(){
        return isSucceeded;
    }
    
    // Custom DocumentListener that handles multiple text fields
    class MyDocumentListener implements DocumentListener {
        private final Map<TextControl, javax.swing.JTextField> textFields = new HashMap<>();

        // Method to add text fields to the listener
        public void addTextField(TextControl name, javax.swing.JTextField textField) {
            textFields.put(name, textField);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleChange(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleChange(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // Usually not needed for plain text fields
        }

        // Method to handle the changes
        private void handleChange(DocumentEvent e) {
            // Identify which text field triggered the event
            for (Map.Entry<TextControl, javax.swing.JTextField> entry : textFields.entrySet()) {
                if (entry.getValue().getDocument() == e.getDocument()) {
                    if (entry.getKey().equals(TextControl.USER_NAME)) chkUsername.setSelected(false);
                    if (entry.getKey().equals(TextControl.PASSWORD)) chkPassword.setSelected(false);
                    if (entry.getKey().equals(TextControl.PASSWORD_REENTRY)) chkPasswordReentry.setSelected(false);
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox2 = new javax.swing.JCheckBox();
        pnlCredentials = new javax.swing.JPanel();
        lblUser = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        chkUsername = new javax.swing.JCheckBox();
        chkPassword = new javax.swing.JCheckBox();
        txtPasswordReentry = new javax.swing.JPasswordField();
        lblPasswordReentry = new javax.swing.JLabel();
        chkPasswordReentry = new javax.swing.JCheckBox();
        pnlOptions = new javax.swing.JPanel();
        btnLogin = new javax.swing.JButton();
        btnNewPassword = new javax.swing.JButton();
        btnNewUser = new javax.swing.JButton();
        btnCloseLaunchApp = new javax.swing.JButton();
        pnlErrorStatus = new javax.swing.JPanel();
        lblErrorStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(centreOnScreen());

        pnlCredentials.setBorder(javax.swing.BorderFactory.createTitledBorder("Credentials"));

        lblUser.setText("User");

        lblPassword.setText("Password");

        chkUsername.setEnabled(false);

        chkPassword.setEnabled(false);

        txtPasswordReentry.setEnabled(false);

        lblPasswordReentry.setText("Re-enter password");
        lblPasswordReentry.setEnabled(false);

        chkPasswordReentry.setEnabled(false);

        javax.swing.GroupLayout pnlCredentialsLayout = new javax.swing.GroupLayout(pnlCredentials);
        pnlCredentials.setLayout(pnlCredentialsLayout);
        pnlCredentialsLayout.setHorizontalGroup(
            pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCredentialsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCredentialsLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword)
                            .addGroup(pnlCredentialsLayout.createSequentialGroup()
                                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPassword))
                            .addComponent(lblUser)
                            .addGroup(pnlCredentialsLayout.createSequentialGroup()
                                .addComponent(txtPasswordReentry, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkPasswordReentry))
                            .addComponent(lblPasswordReentry, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlCredentialsLayout.createSequentialGroup()
                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUsername)))
                .addGap(12, 12, 12))
        );
        pnlCredentialsLayout.setVerticalGroup(
            pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCredentialsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsername))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPasswordReentry)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCredentialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPasswordReentry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPasswordReentry))
                .addGap(14, 14, 14))
        );

        pnlOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        btnLogin.setText("Login");

        btnNewPassword.setText("<html><center>Change</center><center>password</center></html>");

        btnNewUser.setText("<html><center>Add</center><center>new user</center></html>");

        btnCloseLaunchApp.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlOptionsLayout = new javax.swing.GroupLayout(pnlOptions);
        pnlOptions.setLayout(pnlOptionsLayout);
        pnlOptionsLayout.setHorizontalGroup(
            pnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOptionsLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(pnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloseLaunchApp, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNewUser, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );
        pnlOptionsLayout.setVerticalGroup(
            pnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOptionsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnNewUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnCloseLaunchApp, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlErrorStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Description of error (if any)"));

        lblErrorStatus.setText("  ");

        javax.swing.GroupLayout pnlErrorStatusLayout = new javax.swing.GroupLayout(pnlErrorStatus);
        pnlErrorStatus.setLayout(pnlErrorStatusLayout);
        pnlErrorStatusLayout.setHorizontalGroup(
            pnlErrorStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlErrorStatusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblErrorStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlErrorStatusLayout.setVerticalGroup(
            pnlErrorStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlErrorStatusLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblErrorStatus)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlErrorStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlCredentials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCredentials, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlErrorStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        // If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        // For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoginDialog dialog = new LoginDialog(new javax.swing.JFrame(), true);
                dialog.setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseLaunchApp;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnNewPassword;
    private javax.swing.JButton btnNewUser;
    private javax.swing.JCheckBox chkPassword;
    private javax.swing.JCheckBox chkPasswordReentry;
    private javax.swing.JCheckBox chkUsername;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel lblErrorStatus;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPasswordReentry;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel pnlCredentials;
    private javax.swing.JPanel pnlErrorStatus;
    private javax.swing.JPanel pnlOptions;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPasswordReentry;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
