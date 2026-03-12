/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller.message_transmission_controllers;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import theclinic.controller.SystemDefinition;
import static theclinic.controller.ViewController.displayErrorMessage;
import theclinic.model.entity.Appointment;
/**
 *
 * @author colin
 */
public class AppointmentReminderSendViewController extends MessageSendViewController{

    @Override
    public void actionPerformed(ActionEvent e){
        Appointment appointment = (Appointment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT);
        Map<String,Object> dataModel = new HashMap<>();
        dataModel.put("appointment", appointment);
        dataModel.put("patient", appointment.getPatient());
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
