/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import colinhewlettsolutions.client.controller.exceptions.TemplateReaderException;
import colinhewlettsolutions.client.model.entity.Treatment;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.model.entity.AppointmentTreatment;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class TreatmentViewController extends ViewController{
    
    public enum Actions{
        TREATMENT_CREATE_REQUEST,
        TREATMENT_DELETE_REQUEST,
        TREATMENTS_READ_REQUEST,
        TREATMENT_RENAME_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public enum Properties{
        TREATMENT_RECEIVED,
        TREATMENT_ERROR_RECEIVED
    }
    
    public TreatmentViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Treatment treatment = (Treatment)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.TREATMENT);
        boolean isError = false;
        if (e.getSource() instanceof DesktopViewController){
            //doDesktopViewControllerActionRequest(e);
        }
        else{  
            
            Actions actionCommand =
                Actions.valueOf(e.getActionCommand());
            switch (actionCommand){
                case VIEW_CLOSE_NOTIFICATION:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
                case TREATMENT_CREATE_REQUEST:
                    try{
                        treatment.insert();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "TreatmentViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Treatment view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    break;
                case TREATMENT_DELETE_REQUEST:
                    //check his treatment is already in use by one or more appinttmentts
                    String error = null;
                    AppointmentTreatment at = new AppointmentTreatment(
                        new Appointment(), treatment);
                        at.setScope(Entity.Scope.FOR_TREATMENT);
                    try{
                        at = at.read();
                        if (!at.get().isEmpty()) {
                            error = "<html><center>'" + treatment.getDescription() +"' "
                                + "treatment is currently selected "
                                + "by one or more appointments</center>"
                                + "<center>Request to delete aborted</center></html>";
                            
                            /*11/04/2024 10:15
                            displayErrorMessage(message, 
                                "Schedule view controller error",
                                JOptionPane.WARNING_MESSAGE);
                            */
                            isError = true;
                        }
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "TreatmentViewController::actionPerformed("
                                + actionCommand + ") after an AppointmentTreatment table read";
                        displayErrorMessage(message, 
                                "Treatment view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    if (!isError){
                        try{
                            treatment.setScope(Entity.Scope.SINGLE);
                            treatment.delete();
                        }catch(StoreException ex){
                            String message = ex.getMessage() + "\nHandled in "
                                    + "TreatmentViewController::actionPerformed("
                                    + actionCommand + ")";
                            displayErrorMessage(message, 
                                    "Treatment view controller error", 
                                    JOptionPane.WARNING_MESSAGE);
                            isError = true;
                        }
                    }
                    else{
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                        firePropertyChangeEvent(
                            TreatmentViewController.Properties.
                                    TREATMENT_ERROR_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                    }
                    break;
                case TREATMENTS_READ_REQUEST:
                    treatment = new Treatment();
                    treatment.setScope(Entity.Scope.ALL);
                    try{
                        treatment = treatment.read();
                        /*if (treatment.get().isEmpty()){
                            treatment = extractTreatmentFromTemplate();
                            for (Treatment t : treatment.get()){
                                t.insert();
                            }
                        } */ 
                        getDescriptor().getControllerDescription()
                                .setProperty(SystemDefinition.Properties.TREATMENT, treatment);
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "TreatmentViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Treatment view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }     
                    break;
                case TREATMENT_RENAME_REQUEST:
                    try{
                        treatment.update();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "TreatmentViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Treatment view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    firePropertyChangeEvent(
                        DesktopViewController.Properties.
                                TREATMENT_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        null     
                );
                    break;
            }
            if (!isError){
                treatment = new Treatment();
                treatment.setScope(Entity.Scope.ALL);
                try{
                    treatment = treatment.read();
                    getDescriptor().getControllerDescription().
                            setProperty(SystemDefinition.Properties.TREATMENT, treatment);
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\nHandled in "
                            + "TreatmentViewController::actionPerformed("
                            + actionCommand + ")";
                    displayErrorMessage(message, 
                            "Treatment view controller error", 
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                } 
                if (!isError){
                    firePropertyChangeEvent(
                        TreatmentViewController.Properties.
                                TREATMENT_RECEIVED.toString(),
                        (View)e.getSource(),
                        this,
                        null,
                        null
                    );
                }
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
 
    
    

}
