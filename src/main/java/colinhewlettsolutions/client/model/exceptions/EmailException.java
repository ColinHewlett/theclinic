/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.exceptions;

/**
 *
 * @author colin
 */
public class EmailException extends Exception {
    private String s = null;
    
    public EmailException(String s, ExceptionType e){
        super(s);
        exceptionType = e;
    }
    public void setErrorType(ExceptionType exceptionType){
        this.exceptionType = exceptionType;
    }
    public ExceptionType getErrorType(){
        return this.exceptionType;
    }
    private ExceptionType  exceptionType = null;
    
    public static enum ExceptionType {
        EMAIL_PASSWORD_DECRYPTION_ERROR,
        EMAIL_MESSAGING_EXCEPTION
                };

}
