/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller.message_transmission_controllers;

import theclinic.controller.ViewController;
import theclinic.model.message_transmission_model.SendType;
import theclinic.model.message_transmission_model.TemplateRenderer;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.swing.JOptionPane;
import theclinic.controller.SystemDefinition;
import static theclinic.controller.ViewController.displayErrorMessage;
import theclinic.view.views.modal_views.ModalView;

/**
 *
 * @author colin
 */
public abstract class MessageSendViewController extends ViewController {
    
    enum Actions{
        PREVIEW_EMAIL_REQUEST,
        PREVIEW_LETTER_REQUEST,
        PREVIEW_SMS_REQUEST,
        SEND_EMAIL_REQUEST,
        SEND_LETTER_REQUEST,
        SEND_SMS_REQUEST
    }
    
    protected enum MessageType{
        invoice,
        recall,
        reminder
    }
    
    enum Properties{
        PREVIEW_RECEIVED,
    }
    
    protected void doReceivedActionRequestFromView(
            ActionEvent e, MessageType messageType, Map<String,Object>dataModel )throws Exception{
        String message = null;
        Actions actionCommand = Actions.valueOf(e.getActionCommand());
        switch(actionCommand){
            case PREVIEW_EMAIL_REQUEST ->{
                message = getPreview(messageType, SendType.EMAIL, dataModel);
            }
            case PREVIEW_LETTER_REQUEST ->{
                message = getPreview(messageType, SendType.LETTER, dataModel);
            }
            case PREVIEW_SMS_REQUEST ->{
                message = getPreview(messageType, SendType.SMS, dataModel);

            }
            case SEND_EMAIL_REQUEST ->{

            }
            case SEND_LETTER_REQUEST ->{

            }
            case SEND_SMS_REQUEST ->{

            }
        }
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.MESSAGE, message);
        this.firePropertyChangeEvent(Properties.PREVIEW_RECEIVED.toString(), (ModalView)e.getSource(), this, null, null);
        
    }

    protected String getPreview(MessageType messageType, SendType sendType, Map<String,Object>dataModel) throws Exception{
        String templateName = messageType.toString() + sendType.getTemplateName();
        TemplateRenderer templateRenderer = 
                (TemplateRenderer)getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TEMPLATE_RENDERER);
        String result = templateRenderer.renderTemplate(templateName, dataModel);
        return result;
    }
}
