/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.message_transmission_model;

/**
 *
 * @author colin
 */
public class SendFactory{
        
    public static ISendService createSender(SendType type){
        ISendService result = null;   
        switch(type){
            case EMAIL ->
                result = new EmailSender();
            case LETTER ->
                result = new LetterSender();
            case SMS ->
                result = new SMSSender();
        }
        return result;
    }

}
