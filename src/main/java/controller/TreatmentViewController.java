/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ViewController.displayErrorMessage;
import controller.exceptions.TemplateReaderException;
import model.entity.Treatment;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import model.entity.Appointment;
import model.entity.AppointmentTreatment;
import model.entity.Entity;
/*28/03/2024import model.PatientNote;*/
import repository.StoreException;
import view.View;

/*28/03/2024import view.views.non_modal_views.NotesView;*/
import view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class TreatmentViewController extends ViewController{
    
    public TreatmentViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Treatment treatment = getDescriptor().getViewDescription().getTreatment();
        boolean isError = false;
        if (e.getSource() instanceof DesktopViewController){
            //doDesktopViewControllerActionRequest(e);
        }
        else{  
            
            ViewController.TreatmentViewControllerActionEvent actionCommand =
                ViewController.TreatmentViewControllerActionEvent
                        .valueOf(e.getActionCommand());
            switch (actionCommand){
                case VIEW_CLOSE_NOTIFICATION:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
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
                        getDescriptor().getControllerDescription().setError(error);
                        firePropertyChangeEvent(
                            ViewController.TreatmentViewControllerPropertyChangeEvent.
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
                        if (treatment.get().isEmpty()){
                            treatment = extractTreatmentFromTemplate();
                            for (Treatment t : treatment.get()){
                                t.insert();
                            }
                        }  
                        getDescriptor().getControllerDescription()
                                .setTreatment(treatment);
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "TreatmentViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Treatment view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }catch(TemplateReaderException ex){
                        
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
                        ViewController.DesktopViewControllerPropertyChangeEvent.
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
                    getDescriptor().getControllerDescription()
                            .setTreatment(treatment);
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
                        ViewController.TreatmentViewControllerPropertyChangeEvent.
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
