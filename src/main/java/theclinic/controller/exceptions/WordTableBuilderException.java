/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller.exceptions;

/**
 *
 * @author colin
 */
public class WordTableBuilderException extends Exception {
    private String s = null;
    
    public WordTableBuilderException(String s, ExceptionType e){
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
        INCORRECT_NUMBER_OF_CELLS_FOUND,
        INCORRECT_NUMBER_OF_TABLES_FOUND;
    }
}
