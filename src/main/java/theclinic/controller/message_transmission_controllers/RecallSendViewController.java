/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller.message_transmission_controllers;

import theclinic.controller.SystemDefinition;
import theclinic.model.entity.Patient;
import theclinic.model.message_transmission_model.SendType;
import theclinic.view.views.modal_views.ModalView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;


/**
 *
 * @author colin
 */
public class RecallSendViewController extends MessageSendViewController{

    @Override
    public void actionPerformed(ActionEvent e){
        Patient patient = (Patient)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT);
        Map<String,Object> dataModel = new HashMap<>();
        dataModel.put("patient", patient);
        dataModel.put("today", LocalDate.now());
        try{
            doReceivedActionRequestFromView(e, MessageType.recall, dataModel);
        }catch (Exception ex){
            String message = ex.getMessage() + "\n";
            message = message + "handled in RecallSendViewController::actionPerformed( case " + e.getActionCommand() + " )";
            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE); 
        }
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
}
