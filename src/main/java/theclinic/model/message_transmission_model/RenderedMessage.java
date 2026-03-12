/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.message_transmission_model;

/**
 *
 * @author colin
 */
public class RenderedMessage {
    private String recipient;
    private String subject;
    private String body;
     
    public String getRecipient(){
        return recipient;
    }
    public void setRecipient(String value){
        recipient = value;
    }
    
    public String getSubject(){
        return subject;
    }
    public void setSubject(String value){
        subject = value;
    }
    
    public String getBody(){
        return body;
    }
    public void setBody(String value){
        body = value;
    }      
}
