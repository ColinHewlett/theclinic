/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.message_transmission_model;

/**
 *
 * @author colin
 */
public class SMSSender implements ISendService{
    @Override
    public void send(RenderedMessage message) throws Exception{
        System.out.println("SMS to "
                + message.getRecipient()
                + ": "
                + message.getBody());
    }
}
