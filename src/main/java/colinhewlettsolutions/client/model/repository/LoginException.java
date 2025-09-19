/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.repository;

/**
 *
 * @author colin
 */
public class LoginException extends StoreException{
    private LoginExceptionType  loginExceptionType = null;
    public enum LoginExceptionType {
        MATCHING_PASSWORD_NOT_FOUND,
        DUPLICATE_USERNAME_FOUND
    }
    
    public LoginException(String s){
        super(s);
    }
    
    public void setLoginErrorType(LoginExceptionType exceptionType){
        this.loginExceptionType = exceptionType;
    }

    public LoginExceptionType getLoginErrorType(){
        return this.loginExceptionType;
    }
}
