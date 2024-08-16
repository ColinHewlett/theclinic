/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import view.views.non_modal_views.LoginView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import model.non_entity.Credential;
import model.non_entity.SystemDefinition;
import model.entity.User;
import model.entity.Entity;
import repository.StoreException;
import repository.LoginException;
/**
 *
 * @author colin
 */
public class LoginViewController extends ViewController{
    
    public LoginViewController(DesktopViewController controller ){
        setMyController(controller);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Credential credential = null;
        boolean isCheckFinished = false;
        ViewController.LoginViewControllerPropertyChangeEvent pce = null;
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerActionEvent actionCommand = 
                    ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());        
            switch(actionCommand){
                case INITIALISE_VIEW:
                    initialiseView();
                    break;
            }
        }else{
            setLoginView((LoginView)e.getSource());
            ViewController.LoginViewControllerActionEvent actionCommand = 
                    ViewController.LoginViewControllerActionEvent.valueOf(e.getActionCommand()); 
            switch(actionCommand){
                case ADD_NEW_CREDENTIAL_REQUEST:
                    getDescriptor().getControllerDescription().setNewUserCredential(new Credential());
                    pce = isBlankCheckedUsernameOK();
                    if (pce!=null) {
                        sendPropertyChangeEvent(pce);
                        isCheckFinished = true;
                    }
                    if(!isCheckFinished){
                        pce = isBlankCheckedPasswordOK();
                        if (pce != null) {
                            sendPropertyChangeEvent(pce);
                            isCheckFinished = true;
                        }
                    }
                    if(!isCheckFinished){
                        pce = isDuplicateUsernameFound();
                        if(pce!=null) {
                            sendPropertyChangeEvent(pce);
                            isCheckFinished = true;
                        }
                    }
                    if(!isCheckFinished){
                        pce = isBlankCheckedPasswordReentryOK();
                        if (pce!=null){
                           sendPropertyChangeEvent(pce); 
                           isCheckFinished = true;
                        }
                    }
                    if(!isCheckFinished){
                        pce = isPasswordValidated();
                        if (pce!=null){
                           sendPropertyChangeEvent(pce); 
                           isCheckFinished = true;  
                        }
                    }
                    if(!isCheckFinished){
                        credential = getDescriptor().getViewDescription().getNewUserCredential();
                        try{
                            User user = new User();
                            user.setUsername(credential.getUsername());
                            user.setPassword(credential.getPassword());
                            user.insert();
                        }catch(StoreException ex){
                            
                        }
                    }
                    break;
                case LOGIN_REQUEST:
                    Credential test = getDescriptor().getViewDescription().getLoginCredential();
                    getDescriptor().getControllerDescription().setLoginCredential(new Credential());
                    if (!isRoot()){
                        pce = isBlankCheckedUsernameOK();
                        if (pce!=null) {
                            sendPropertyChangeEvent(pce);
                            isCheckFinished = true;
                        }
                        if(!isCheckFinished){
                            pce = isBlankCheckedPasswordOK();
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
                            credential = getDescriptor().getViewDescription().getLoginCredential();
                            User user = new User();
                            user.setUsername(credential.getUsername());
                            user.setPassword(credential.getPassword());
                            try{
                                user.setScope(Entity.Scope.WITH_CREDENTIAL);
                                user = user.read();
                                credential.setIsUsernameValid(true);
                                credential.setIsPasswordValid(true);
                                credential.setUsername(user.getUsername());
                                credential.setPassword(user.getPassword());
                                getDescriptor().getControllerDescription().setLoginCredential(credential);
                                pce = ViewController.LoginViewControllerPropertyChangeEvent.CORRECT_LOGIN_CREDENTIAL_RECEIVED;
                                sendPropertyChangeEvent(pce);
                            }catch(StoreException ex){
                                if (ex instanceof LoginException){
                                    switch(((LoginException)ex).getLoginErrorType()){
                                        case MATCHING_PASSWORD_NOT_FOUND:
                                            String error = "incorrect password specified";
                                            getDescriptor().getControllerDescription().setError(error);
                                            pce = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
                                            sendPropertyChangeEvent(pce);
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
                        }
                    }else {
                        credential = new Credential();
                        credential.setIsUsernameValid(true);
                        credential.setIsPasswordValid(true);
                        credential.setUsername("root");
                        getLoginView().getMyController().getDescriptor().getControllerDescription().setLoginCredential(credential);
                        pce = ViewController.LoginViewControllerPropertyChangeEvent.CORRECT_LOGIN_CREDENTIAL_RECEIVED;
                        sendPropertyChangeEvent(pce);  
                    }
                    break;
                case NEW_PASSWORD_REQUEST:
                    pce = isBlankCheckedPasswordOK();
                    if (pce != null) {
                        sendPropertyChangeEvent(pce);
                        isCheckFinished = true;
                    }
                    if(!isCheckFinished){
                        pce = isPasswordValidated();
                        if(pce!=null){
                            sendPropertyChangeEvent(pce);
                            isCheckFinished = true;
                        }
                    }
                    if(!isCheckFinished){
                        credential = getDescriptor().getControllerDescription().getLoginCredential();
                        User user = new User();
                        user.setUsername(credential.getUsername());
                        credential = getDescriptor().getViewDescription().getNewUserCredential();
                        user.setScope(Entity.Scope.WITH_NAME);
                        try{
                            user = user.read();
                            user.setPassword(credential.getPassword());
                            user.setScope(Entity.Scope.WITH_CREDENTIAL);
                            user.update();
                            pce = ViewController.LoginViewControllerPropertyChangeEvent.PASSWORD_CHANGE_RECEIVED;
                            sendPropertyChangeEvent(pce);
                        }catch(StoreException ex){
                            String message = ex.getMessage() + "\n"
                                    + "Handled in LoginViewController::actionPerformed(NEW_PASSWORD_REQUEST) "
                                            + "arising from a user.update() action";
                                    displayErrorMessage(message, 
                                            "LoginViewController error",JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){

    }
    
    public void sendPropertyChangeEvent(ViewController.LoginViewControllerPropertyChangeEvent pce){
        firePropertyChangeEvent(
                pce.toString(),
                getLoginView(),
                this,
                null,
                null
        );
    }
    
    private boolean isRoot(){
        Credential credential = getDescriptor().getViewDescription().getLoginCredential();
        String password = credential.getPassword().trim();
        return password.equals("root");
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isBlankCheckedUsernameOK(){
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        boolean isError = false;
        Credential credential = null;
        SystemDefinition.LoginViewMode loginViewMode  = getDescriptor().getViewDescription().getLoginViewMode();
        switch (loginViewMode){
            case LOGIN:
                credential = getDescriptor().getViewDescription().getLoginCredential();
                break;
            case NEW_USER_OR_PASSWORD:
                credential = getDescriptor().getViewDescription().getNewUserCredential();
                break;
        }
        credential.setUsername(credential.getUsername().trim());
        if (credential.getUsername().isEmpty()){
            String message = "user name cannot be blank";
            getDescriptor().getControllerDescription().setError(message);
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED;
            isError = true;
        }else if(credential.getUsername().contains(" ")){
            String message = "user name cannot contain a blank";
            getDescriptor().getControllerDescription().setError(message);
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED;
            isError = true;
        }
        
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isBlankCheckedPasswordOK(){
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        
        boolean isError = false;
        Credential credential = null;
        SystemDefinition.LoginViewMode loginViewMode  = getDescriptor().getViewDescription().getLoginViewMode();
        switch (loginViewMode){
            case LOGIN:
                credential = getDescriptor().getViewDescription().getLoginCredential();
                break;
            case NEW_USER_OR_PASSWORD:
                credential = getDescriptor().getViewDescription().getNewUserCredential();
                break;
        }
        try{
            credential.setPassword(credential.getPassword().trim());
            if(credential.getPassword().isEmpty()){
                String message = "password cannot be blank";
                getDescriptor().getControllerDescription().setError(message);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
                isError = true;
            }
            else if(credential.getPassword().contains(" ")){
                String message = "password cannot contain a blank";
                getDescriptor().getControllerDescription().setError(message);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
                isError = true;
            }
        }catch(NullPointerException ex){
            String message = "password cannot be blank";
            getDescriptor().getControllerDescription().setError(message);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_RECEIVED;
                isError = true;
        }
        
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isBlankCheckedPasswordReentryOK(){
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        boolean isError = false;
        Credential credential = getDescriptor().getViewDescription().getNewUserCredential();;
        try{
            credential.setPasswordReentry(credential.getPasswordReentry().trim());
            if(credential.getPasswordReentry().isEmpty()){
                String message = "re-entered password cannot be blank";
                getDescriptor().getControllerDescription().setError(message);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_VALIDATION_RECEIVED;
                isError = true;
            }
            else if(credential.getPasswordReentry().contains(" ")){
                String message = "re-entered password cannot contain a blank";
                getDescriptor().getControllerDescription().setError(message);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_VALIDATION_RECEIVED;
                isError = true;
            }
        }catch(NullPointerException ex){
            String message = "password cannot be blank";
            getDescriptor().getControllerDescription().setError(message);
                result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_VALIDATION_RECEIVED;
                isError = true;
        }
        return result;
    }
    
    private ViewController.LoginViewControllerPropertyChangeEvent isPasswordValidated(){
        String error = null;
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        Credential credential = getDescriptor().getViewDescription().getNewUserCredential();
        if (!credential.getPassword().equals(credential.getPasswordReentry())){
            error = "the two passwords entered are not the same";
            getDescriptor().getControllerDescription().setError(error);
            result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_PASSWORD_VALIDATION_RECEIVED;  
        }
        return result;
    } 
    
    private ViewController.LoginViewControllerPropertyChangeEvent isDuplicateUsernameFound(){ 
        ViewController.LoginViewControllerPropertyChangeEvent result = null;
        String error = null;
        User user = null;
        Credential credential = getDescriptor().getViewDescription().getNewUserCredential();
        user = new User();
        user.setUsername(credential.getUsername());
        user.setScope(Entity.Scope.WITH_NAME);
        try{
            user = user.read();
            if (user!=null){
                error = "Selected user name already exists";
                getDescriptor().getControllerDescription().setError(error);
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
        Credential credential = null;
        SystemDefinition.LoginViewMode loginViewMode  = getDescriptor().getViewDescription().getLoginViewMode();
        switch (loginViewMode){
            case LOGIN:
                credential = getDescriptor().getViewDescription().getLoginCredential();
                user = new User();
                user.setUsername(credential.getUsername());
                user.setScope(Entity.Scope.WITH_NAME);
                try{
                    user = user.read();
                    if (user==null){
                       error = "User name not found";
                       getDescriptor().getControllerDescription().setError(error);
                       result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED; 
                    }
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n"
                            + "StoreException raised in isUserNameFound() in case '" + loginViewMode + "'";
                    displayErrorMessage(message, "LoginViewController error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case NEW_USER_OR_PASSWORD:
                credential = getDescriptor().getViewDescription().getNewUserCredential();
                user = new User();
                user.setUsername(credential.getUsername());
                user.setScope(Entity.Scope.WITH_NAME);
                try{
                    user = user.read();
                    if (user==null){
                       error = "User name not found";
                       getDescriptor().getControllerDescription().setError(error);
                       result = ViewController.LoginViewControllerPropertyChangeEvent.INCORRECT_USERNAME_RECEIVED; 
                    }
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n"
                            + "StoreException raised in isUserNameFound() in case '" + loginViewMode + "'";
                    displayErrorMessage(message, "LoginViewController error", JOptionPane.WARNING_MESSAGE);
                }
                break;
        }
        
        
        return result;
    }
    
    private void initialiseView(){
        new LoginView(this);
    }
    
    private LoginView loginView = null;
    private void setLoginView(LoginView view){
        this.loginView = view;
    }
    private LoginView getLoginView(){
        return loginView;
    }
    
}
