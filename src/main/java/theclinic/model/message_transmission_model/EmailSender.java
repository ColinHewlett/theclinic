/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.message_transmission_model;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
/**
 *
 * @author colin
 */
public class EmailSender implements ISendService{
    
    @Override
    public void send(RenderedMessage message) throws Exception{
        String url =
                "https://mail.google.com/mail/?view=cm&fs=1"
                + "&to= " + URLEncoder.encode(message.getRecipient(), "UTF-8")
                + "&su= " + URLEncoder.encode(message.getSubject(), "UTF-8")
                + "&body= " + URLEncoder.encode(message.getBody(), "UTF-8");
        Desktop.getDesktop().browse(new URI(url));
    }
}
