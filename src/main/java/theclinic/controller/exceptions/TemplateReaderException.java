/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller.exceptions;

import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author colin
 */
public class TemplateReaderException extends Exception{
    private String s = null;
    
    public TemplateReaderException(String s, ExceptionType e){
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
                ELEMENT_NOT_FOUND_IN_TEMPLATE,
                IO_EXCEPTION,
                PARSER_CONFIGURATION_ERROR,
                PATIENT_MEDICAL_HISTORY_ORPHAN,
                SAX_EXCEPTION}

}
