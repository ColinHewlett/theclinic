/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.non_modal_views;

import model.non_entity.SystemDefinition;
import model.non_entity.Credential;
import view.View;
import view.dialogs.LoginDialog;
import controller.ViewController;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 *
 * @author colin
 */
public class LoginView extends View implements ActionListener{
    
    public enum ActionOnClose{
        EXIT_APP,
        RUN_APP
    }
    
    public LoginView(ViewController myController){
        setMyController(myController);
        getMyController().getDescriptor().getViewDescription().setLoginViewMode(SystemDefinition.LoginViewMode.LOGIN);
        initialiseView();
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Credential credential = null;
        SystemDefinition.LoginAction action = SystemDefinition.LoginAction.valueOf(e.getActionCommand());
        switch(action){
            case REQUEST_LOGIN:
                credential = new Credential(getLoginDialog().getUsername(),getLoginDialog().getPassword());
                getMyController().getDescriptor().getViewDescription().setLoginCredential(credential);
                sendActionCommand(ViewController.LoginViewControllerActionEvent.LOGIN_REQUEST);
                break;
            case REQUEST_ADD_NEW_CREDENTIAL:
                credential = new Credential(getLoginDialog().getUsername(),getLoginDialog().getPassword());
                credential.setPasswordReentry(getLoginDialog().getPasswordReentry());
                getMyController().getDescriptor().getViewDescription().setNewUserCredential(credential);
                sendActionCommand(ViewController.LoginViewControllerActionEvent.ADD_NEW_CREDENTIAL_REQUEST);
                break;
            case REQUEST_UPDATE_CREDENTIAL:
                credential = getMyController().getDescriptor().getControllerDescription().getLoginCredential();
                credential.setPassword(getLoginDialog().getPassword());
                credential.setPasswordReentry(getLoginDialog().getPasswordReentry());
                getMyController().getDescriptor().getViewDescription().setNewUserCredential(credential);
                sendActionCommand(ViewController.LoginViewControllerActionEvent.NEW_PASSWORD_REQUEST);
                break;
            case REQUEST_CLOSE_LAUNCH_APP:
                getLoginDialog().dispose();
                break;
        }
    }
    
    /**
     * forward property change event to dialog to handle
     * @param e 
     */
    public void propertyChange(PropertyChangeEvent e){
        getMyController().firePropertyChangeEvent(
                e.getPropertyName(), 
                this.getLoginDialog(), 
                this, 
                null,
                null
        );
    }
    
    @Override
    public void initialiseView(){
        new LoginDialog(new JFrame(), this);
    }
    
    private LoginDialog getLoginDialog(){
        return getMyController().getDescriptor()
                .getViewDescription().getLoginDialog();
    }
    
    private boolean isEntryError = false;
    private void setIsEntryError(boolean value){
        isEntryError = value;
    }
    private boolean getIsEntryError(){
        return isEntryError;
    }
    
    public String username = null;
    public String getUsername(){
        return username;
    }
    public void setUsername(String value){
        username = value;
    }
    
    public String password = null;
    public String getPassword(){
        return password;
    }
    public void setPassword(String value){
        password = value;
    }
    
    
    
    private void sendActionCommand(ViewController.LoginViewControllerActionEvent actionCommand ){
        ActionEvent actionEvent = new ActionEvent(
        this,ActionEvent.ACTION_PERFORMED, actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
}
