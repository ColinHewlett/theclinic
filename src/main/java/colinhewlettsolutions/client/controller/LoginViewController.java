/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import static colinhewlettsolutions.client.controller.ViewController.LoginViewMode;
import colinhewlettsolutions.client.model.entity.User;
import colinhewlettsolutions.client.model.entity.UserSettings;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.view.views.non_modal_views.UserLoginView;
import colinhewlettsolutions.client.model.non_entity.Credential;

import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.model.repository.LoginException;
import static colinhewlettsolutions.client.model.repository.LoginException.LoginExceptionType.MATCHING_PASSWORD_NOT_FOUND;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
/**
 *
 * @author colin
 */
public class LoginViewController extends ViewController{
    
    public LoginViewController(DesktopViewController controller, DesktopView desktopView ){
        setMyController(controller);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ViewController.LoginViewMode viewMode = (ViewController.LoginViewMode)getDescriptor().
                getControllerDescription().getProperty(Properties.LOGIN_VIEW_MODE);
        ViewController.LoginViewControllerPropertyChangeEvent pce = null;
        
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerActionEvent desktopViewControllerActionCommand = 
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());        
            switch(desktopViewControllerActionCommand){

                case INITIALISE_VIEW_CONTROLLER ->{
                    initialiseView();
                    break;                   
                }
                default -> {
                    break;
                }
            }
        }else{
            ViewController.LoginViewControllerActionEvent loginViewActionCommand = 
                    ViewController.LoginViewControllerActionEvent.valueOf(e.getActionCommand());  
            switch(loginViewActionCommand){
                case VIEW_CLOSED_NOTIFICATION ->{
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                }
                case LOGIN_REQUEST ->{
                    doLoginAction(e);
                    break;
                }
                case NEW_PASSWORD_REQUEST ->{
                    doNewPasswordAction(e);
                    break;
                }
                case OLD_PASSWORD_CHECK_REQUEST ->{
                    doOldPasswordAction(e);
                }
                default ->{
                    break;
                }
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){

    }
    
    public void sendPropertyChangeEvent(ViewController.LoginViewControllerPropertyChangeEvent pce){
        firePropertyChangeEvent(
                pce.toString(),
                getView(),
                this,
                null,
                null
        );
    }
    
    private Credential credential = null;
    private Credential getCredential(){
        return credential;
    }
    private void setCredential(Credential value){
        if (value==null) value = new Credential();
        credential = value;
    }
    
    private void doNewPasswordAction(ActionEvent e){
        boolean isCheckFinished = false;
        LoginViewControllerPropertyChangeEvent pce = null;
        String currentUser =((Credential)getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_CREDENTIAL)).getUsername();
        setCredential((Credential)getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL));
        getCredential().setUsername(currentUser);
        switch(ViewController.LoginViewControllerActionEvent.valueOf(e.getActionCommand())){
            case NEW_PASSWORD_REQUEST ->{
                pce = checkPasswordForBlanks();
                if (pce != null) {
                    sendPropertyChangeEvent(pce);
                    isCheckFinished = true;
                }
                if(!isCheckFinished){
                    pce = isPasswordReentryOK();
                    if(pce!=null){
                        sendPropertyChangeEvent(pce);
                        isCheckFinished = true;
                    }
                }
                if(!isCheckFinished){
                    try{
                        pce = updateLoginCredential();
                        sendPropertyChangeEvent(pce);
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n";
                        message = message + "StoreException handled in LoginViewController::updateLoginCredential()";
                        displayErrorMessage(message, "Repository error", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                break;
            }
            case VIEW_CLOSED_NOTIFICATION ->{
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            } 
        }
    }
    
    private void doOldPasswordAction(ActionEvent e){
        boolean isCheckFinished = false;
        LoginViewControllerPropertyChangeEvent pce = null;
        String currentUser =((Credential)getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_CREDENTIAL)).getUsername();
        setCredential((Credential)getDescriptor().getViewDescription().
                getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL));
        getCredential().setUsername(currentUser);
        switch(ViewController.LoginViewControllerActionEvent.valueOf(e.getActionCommand())){
            case OLD_PASSWORD_CHECK_REQUEST ->{ 
                pce = checkPasswordForBlanks();
                if (pce != null) {
                    sendPropertyChangeEvent(pce);
                    isCheckFinished = true;
                }
                if(!isCheckFinished){
                    pce = checkLoginCredential();
                    switch (pce){
                        case CORRECT_LOGIN_CREDENTIAL_RECEIVED ->{
                            sendPropertyChangeEvent(pce);
                            getDescriptor().getControllerDescription().
                                    setProperty(Properties.LOGIN_VIEW_MODE, LoginViewMode.NEW_PASSWORD_CHECK);
                            initialiseView();
                            break;
                        }
                        case INCORRECT_PASSWORD_RECEIVED ->{
                            sendPropertyChangeEvent(pce);
                            break;
                        }
                    }
                }
                break;
            }
            case VIEW_CLOSED_NOTIFICATION ->{
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            }
        }
    }
    
    private LoginViewControllerPropertyChangeEvent updateLoginCredential()throws StoreException{
        LoginViewControllerPropertyChangeEvent pce = null;
        User user = new User();
        user.setUsername(getCredential().getUsername());
        user.setPassword(new String(getCredential().getPassword()));
        user.update();
        pce = ViewController.LoginViewControllerPropertyChangeEvent.NEW_CREDENTIAL_VALIDATION_RECEIVED;
        return pce;
    } 
    
    private LoginViewControllerPropertyChangeEvent checkLoginCredential(){
        LoginViewControllerPropertyChangeEvent pce = null;
        User user = new User();
        user.setUsername(getCredential().getUsername());
        user.setPassword(new String(getCredential().getPassword()));
        try{
            user.setScope(Entity.Scope.WITH_CREDENTIAL);
            user = user.read();
            getCredential().setIsUsernameValid(true);
            getCredential().setIsPasswordValid(true);
            getCredential().setUsername(user.getUsername());
            getCredential().setPassword(user.getPassword().toCharArray());
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL, credential);
            pce = ViewController.LoginViewControllerPropertyChangeEvent.CORRECT_LOGIN_CREDENTIAL_RECEIVED;
            //sendPropertyChangeEvent(pce);

            //SystemDefinition.setActiveUser(new User(credential.getUsername()));
        }catch(StoreException ex){
            if (ex instanceof LoginException){
                switch(((LoginException)ex).getLoginErrorType()){
                    case MATCHING_PASSWORD_NOT_FOUND:
                        String error = "incorrect password specified";
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                        pce = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
                        //sendPropertyChangeEvent(pce);
                        break;
                }
            }else{
                String message = ex.getMessage() + "\n"
                        + "Handled in LoginViewController::actionPerformed(LOGIN_REQUEST) "
                        + "arising from a user.read(Scope = WITH_NAME) action";
                displayErrorMessage(message, 
                        "LoginViewController error",JOptionPane.WARNING_MESSAGE);
            }
        }
        return pce;
    }
    
    private void doLoginAction(ActionEvent e){
        Credential credential = null;
        boolean isCheckFinished = false;
        LoginViewControllerPropertyChangeEvent pce = null;
        switch(ViewController.LoginViewControllerActionEvent.valueOf(e.getActionCommand())){
            case LOGIN_REQUEST ->{
                setCredential((Credential)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL));
                pce = checkUsernameForBlanks();
                if (pce!=null) {
                    sendPropertyChangeEvent(pce);
                    isCheckFinished = true;
                }
                if(!isCheckFinished){
                    pce = checkPasswordForBlanks();
                    if (pce != null) {
                        sendPropertyChangeEvent(pce);
                        isCheckFinished = true;
                    }
                }
                if(!isCheckFinished){
                    pce = isUsernameFound();
                    if(pce!=null) {
                        sendPropertyChangeEvent(pce);
                        isCheckFinished = true;
                    }
                }
                if(!isCheckFinished){
                    setCredential((Credential)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL));
                    pce = checkLoginCredential();
                    if (pce!=null){
                        switch(pce){
                            case INCORRECT_PASSWORD_RECEIVED ->{
                                sendPropertyChangeEvent(pce);
                                break;
                            }
                            case CORRECT_LOGIN_CREDENTIAL_RECEIVED ->{
                                ActionEvent actionEvent = new ActionEvent(
                                    this,ActionEvent.ACTION_PERFORMED,
                                    ViewController.LoginViewControllerActionEvent.USER_LOGIN_NOTIFICATION.toString());
                                this.getMyController().actionPerformed(actionEvent);
                                sendPropertyChangeEvent(pce);
                                break;
                            }
                        }
                    }
                    
                }
                break;
            }
            case VIEW_CLOSED_NOTIFICATION ->{
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            }       
            default ->{
                break;
            }
        }
    }

    private ViewController.LoginViewControllerPropertyChangeEvent checkUsernameForBlanks(){
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        if (getCredential().getUsername()==null) getCredential().setUsername("");
        getCredential().setUsername(getCredential().getUsername().trim());
        if (getCredential().getUsername().isEmpty()){
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED;
        }else if(getCredential().getUsername().contains(" "))
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED;
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent checkPasswordForBlanks(){
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        try{
            getCredential().setPassword(new String(getCredential().getPassword()).trim().toCharArray());
            if(getCredential().getPassword().length == 0){
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
            }
            else if(new String(getCredential().getPassword()).contains(" ")){
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
            }
        }catch(NullPointerException ex){
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
        }
        
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isPasswordReentryOK(){
        String username = null;
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        Credential credential = (Credential)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL);
        credential.setPassword(new String(credential.getPassword()).trim().toCharArray());
        credential.setPasswordReentry(new String(credential.getPasswordReentry()).trim().toCharArray());
        if (!Arrays.equals(credential.getPassword(), credential.getPasswordReentry()))
           result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_VALIDATION_RECEIVED; 
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isPasswordValidated(){
        String error = null;
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        Credential credential = (Credential)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.NEW_USER_CREDENTIAL);
        if (!credential.getPassword().equals(credential.getPasswordReentry())){
            error = "the two passwords entered are not the same";
            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_VALIDATION_RECEIVED;  
        }
        return result;
    } 
    
    private ViewController.LoginViewControllerPropertyChangeEvent isDuplicateUsernameFound(){ 
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        String error = null;
        User user = null;
        Credential credential = (Credential)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.NEW_USER_CREDENTIAL);
        user = new User();
        user.setUsername(credential.getUsername());
        user.setScope(Entity.Scope.WITH_NAME);
        try{
            user = user.read();
            if (user!=null){
                error = "Selected user name already exists";
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED; 
            }
        }catch(StoreException ex){
            String message = ex.getMessage() +"\n"
                    + "StoreException raised in isDuplicateUsernameFound()";
            displayErrorMessage(message, "LoginViewController error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isUsernameFound(){ 
        String error =null;
        User user = null;
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        user = new User();
        user.setUsername(getCredential().getUsername());
        user.setScope(Entity.Scope.WITH_NAME);
        try{
            user = user.read();
            if (user==null){
               getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
               result = ViewController.LoginViewControllerPropertyChangeEvent.USERNAME_NOT_FOUND; 
            }
        }catch(StoreException ex){
            String message = ex.getMessage() +"\n"
                    + "StoreException raised in isUserNameFound() in case";
            displayErrorMessage(message, "LoginViewController error", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    /**
     * view controller initialisation as follows
     * -- for each user name not found in repository
     * ---- insert a new user with that name and associated default password (same as the user name)
     * -- therafter in subsequent instances of pms all the users currently defined offline will be stored in the repository
     * -- additional check determines the presence in the repository of a any user who is no longer defined offline
     * ---- if any such users they will be deleted from the repository
     */
    private void initialiseView(){
        boolean userExists = false;
        User user = null;
        ViewController.LoginViewMode viewMode = 
                (ViewController.LoginViewMode)getDescriptor().getControllerDescription().getProperty(Properties.LOGIN_VIEW_MODE);
        switch (viewMode){
            case LOGIN_CHECK ->{//creates view which enables user to login to system
                ArrayList<String> users = (ArrayList<String>)getDescriptor().getControllerDescription().getProperty(Properties.USERS);
                for(String username : users){
                    try{
                        user = new User(username);
                        user.setPassword(username);
                        user.insert();
                    }catch(StoreException ex){
                        userExists = true;
                    }
                    if (!userExists){
                        /**
                         * if this is a new user (never logged in before)
                         * -- initialise user settings for this user for each category of settings
                         */
                        setUserDefaultSettings(user);
                    }
                }
                setView((View)new View().make(View.Viewer.LOGIN_VIEW,
                            this, getDesktopView()));
                break;
            }
            case OLD_PASSWORD_CHECK ->{
                setView(new View().make(
                        View.Viewer.LOGIN_OLD_PASSWORD_CHECK_VIEW,
                        this, 
                        this.getDesktopView()));
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_ACTIVATED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            }
            case NEW_PASSWORD_CHECK ->{
                setView(new View().make(
                        View.Viewer.LOGIN_NEW_PASSWORD_EDITOR_VIEW,
                        this, 
                        this.getDesktopView()));
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_ACTIVATED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            }
            /*
            case OLD_PASSWORD_CHECK ->{//creates view which validates current p'word & than validates new p'word
                setView(new View().make(
                        View.Viewer.LOGIN_NEW_PASSWORD_EDITOR_VIEW,
                        this, 
                        this.getDesktopView()));
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_ACTIVATED_NOTIFICATION.toString());
                this.getMyController().actionPerformed(actionEvent);
                break;
            }
            default ->{
                String message = "Unexpected LOGIN_VIEW_MODE_property encountered.\n";
                message = message + "Error handled in LoginViewController::initialise() method";
                displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
            }*/
        }
    }
    /**
     * method fetches factory settings by each category of properties
     * -- initialises UserSettings settings property with fetched values
     * -- sends message to update() method of UserSettings object
     * Note this method is called only if the current user has logged in for the first time
     * @param user ; the user to whom the settings collection belongs
     */
    private void setUserDefaultSettings(User user){
        HashMap<SystemDefinition.Properties, Object> settings = null;
        UserSettings userSettings = new UserSettings();
        userSettings.setUser(user);
        try{
            userSettings.setScope(Entity.Scope.USER_SCHEDULE_DIARY_SETTINGS);
            settings = userSettings.getSettingsFrom(getMyController().getDescriptor().getControllerDescription());
            userSettings.setSettings(settings);
            userSettings.insert();
            userSettings.setScope(Entity.Scope.USER_SCHEDULE_LIST_SETTINGS);
            settings = userSettings.getSettingsFrom(getMyController().getDescriptor().getControllerDescription());
            userSettings.setSettings(settings);
            userSettings.insert();
            userSettings.setScope(Entity.Scope.USER_SYSTEM_WIDE_SETTINGS);
            settings = userSettings.getSettingsFrom(getMyController().getDescriptor().getControllerDescription());
            userSettings.setSettings(settings);
            userSettings.insert();
        }catch (StoreException ex){
            String message = ex.getMessage() + "\n"
                    + " Exception handled in LoginViewController::setUserDefaultSettings('" + user.getUsername() +"')";
            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
        }
        
    }
}
