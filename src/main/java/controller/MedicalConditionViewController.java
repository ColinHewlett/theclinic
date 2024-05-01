/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;


import static controller.ViewController.MedicalConditionViewControllerActionEvent.SECONDARY_CONDITION_CREATE_REQUEST;
import static controller.ViewController.MedicalConditionViewControllerActionEvent.SECONDARY_CONDITION_DELETE_REQUEST;
import static controller.ViewController.MedicalConditionViewControllerActionEvent.SECONDARY_CONDITION_READ_REQUEST;
import static controller.ViewController.displayErrorMessage;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import model.*;
import repository.StoreException;
import view.View;
import view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class MedicalConditionViewController extends ViewController{
    
    public MedicalConditionViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        PrimaryCondition primaryCondition = getDescriptor()
                .getViewDescription().getPrimaryCondition();
        SecondaryCondition secondaryCondition = null;
        Patient patient = null;
        String error = null;
        boolean isError = false;
        if (e.getSource() instanceof DesktopViewController){
            //doDesktopViewControllerActionRequest(e);
        }
        else{  
            ViewController.MedicalConditionViewControllerActionEvent actionCommand =
                ViewController.MedicalConditionViewControllerActionEvent
                        .valueOf(e.getActionCommand());
            switch (actionCommand){
                case VIEW_CLOSE_NOTIFICATION:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
                case PRIMARY_CONDITION_CREATE_REQUEST:
                    try{
                        Integer primaryConditionKey = primaryCondition.insert();
                        primaryCondition = new PrimaryCondition(primaryConditionKey);
                        primaryCondition.setScope(Entity.Scope.ALL);
                        primaryCondition = primaryCondition.read();
                        getDescriptor().getControllerDescription()
                                .setPrimaryCondition(primaryCondition);
                        firePropertyChangeEvent(
                            ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    break;
                case SECONDARY_CONDITION_CREATE_REQUEST:
                    try{
                        secondaryCondition = primaryCondition.getSecondaryCondition();
                        secondaryCondition.insert();
                        secondaryCondition.setScope(Entity.Scope.ALL);
                        secondaryCondition = secondaryCondition.read();
                        primaryCondition = new PrimaryCondition(secondaryCondition);
                        getDescriptor().getControllerDescription()
                                    .setPrimaryCondition(primaryCondition);
                        firePropertyChangeEvent(
                            ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                    }catch (StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    break;
                case PRIMARY_CONDITION_DELETE_REQUEST:
                    //check his treatment is already in use by one or more appinttmentts
                    ArrayList<Patient> patientWithMedicalCondition = new ArrayList<>();
                    ArrayList<String> patientConditionDetails = new ArrayList<>();
                    
                    PatientPrimaryCondition ppc = new PatientPrimaryCondition(
                        new Patient(), primaryCondition);
                        ppc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
                    try{
                        ppc = ppc.read();
                        /**
                         * any patients have this condition
                         */
                        if (!ppc.get().isEmpty()) {
                            error = "<html><center>'" + primaryCondition.getDescription() +"' "
                                + "primary condition is currently selected "
                                + "by one or more patients</center>"
                                + "<center>Request to delete aborted</center></html>";
                            isError = true;
                        }
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ") after an PatientPrimaryCondition table read";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    if (!isError){
                        try{
                            primaryCondition.setScope(Entity.Scope.SINGLE);
                            primaryCondition.delete();
                            primaryCondition = new PrimaryCondition();
                            primaryCondition.setScope(Entity.Scope.ALL);
                            primaryCondition = primaryCondition.read();
                            getDescriptor().getControllerDescription()
                                    .setPrimaryCondition(primaryCondition);
                            firePropertyChangeEvent(
                                ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                        PRIMARY_CONDITION_RECEIVED.toString(),
                                (View)e.getSource(),
                                this,
                                null,
                                null
                            );
                        }catch(StoreException ex){
                            String message = ex.getMessage() + "\nHandled in "
                                    + "MedicalConditionViewController::actionPerformed("
                                    + actionCommand + ")";
                            displayErrorMessage(message, 
                                    "Medical condition view controller error", 
                                    JOptionPane.WARNING_MESSAGE);
                            isError = true;
                        }
                    }
                    break;
                case SECONDARY_CONDITION_DELETE_REQUEST:
                    secondaryCondition = primaryCondition.getSecondaryCondition();
                    PatientSecondaryCondition psc = 
                            new PatientSecondaryCondition(new Patient(), secondaryCondition);
                    psc.setScope(Entity.Scope.FOR_SECONDARY_CONDITION);
                    try{
                        psc = psc.read();
                        if (!psc.get().isEmpty()){
                            isError = true;
                            error = "<html><center>'" + secondaryCondition.getDescription() +"' "
                                    + "secondary condition is currently selected "
                                    + "by one or more patients</center>"
                                    + "<center>Request to delete aborted</center></html>";
                        }
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    if (!isError){
                        try{
                            secondaryCondition.setScope(Entity.Scope.SINGLE);
                            secondaryCondition.delete();
                        }catch(StoreException ex){
                            String message = ex.getMessage() + "\nHandled in "
                                    + "MedicalConditionViewController::actionPerformed("
                                    + actionCommand + ")";
                            displayErrorMessage(message, 
                                    "Medical condition view controller error", 
                                    JOptionPane.WARNING_MESSAGE);
                            isError = true;
                        }
                    }
                    break;
                case PRIMARY_CONDITION_READ_REQUEST:
                    primaryCondition = new PrimaryCondition();
                    primaryCondition.setScope(Entity.Scope.ALL);
                    try{
                        primaryCondition = primaryCondition.read();
                        getDescriptor().getControllerDescription()
                                .setPrimaryCondition(primaryCondition);
                        firePropertyChangeEvent(
                            ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }      
                    break;
                case SECONDARY_CONDITION_READ_REQUEST:
                    secondaryCondition = new SecondaryCondition();
                    secondaryCondition.setScope(Entity.Scope.ALL);
                    try{
                        secondaryCondition = secondaryCondition.read();
                        primaryCondition.setSecondaryCondition(secondaryCondition);
                        getDescriptor().getControllerDescription()
                                .setPrimaryCondition(primaryCondition);
                        firePropertyChangeEvent(
                            ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }   
                    break;
                case PRIMARY_CONDITION_RENAME_REQUEST:
                    try{
                        primaryCondition.update();
                        primaryCondition.setScope(Entity.Scope.ALL);
                        primaryCondition = primaryCondition.read();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    getDescriptor().getControllerDescription().setPrimaryCondition(primaryCondition);
                    firePropertyChangeEvent(
                        ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                PRIMARY_CONDITION_RECEIVED.toString(),
                        (View)e.getSource(),
                        this,
                        null,
                        null
                    );
                    firePropertyChangeEvent(
                        ViewController.DesktopViewControllerPropertyChangeEvent.
                                MEDICAL_CONDITION_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                        (DesktopViewController)getMyController(),
                        this,
                        null,
                        null     
                    );
                    break;
                case SECONDARY_CONDITION_RENAME_REQUEST:
                    try{
                        secondaryCondition = primaryCondition.getSecondaryCondition();
                        secondaryCondition.update();
                        secondaryCondition.setScope(Entity.Scope.ALL);
                        secondaryCondition = secondaryCondition.read();
                        primaryCondition.setSecondaryCondition(secondaryCondition);
                        getDescriptor().getControllerDescription().setPrimaryCondition(primaryCondition);
                        firePropertyChangeEvent(
                            ViewController.MedicalConditionViewControllerPropertyChangeEvent.
                                    PRIMARY_CONDITION_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                        );
                        firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    MEDICAL_CONDITION_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            null     
                        );
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\nHandled in "
                                + "MedicalConditionViewController::actionPerformed("
                                + actionCommand + ")";
                        displayErrorMessage(message, 
                                "Medical condition view controller error", 
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                    break;
            }
            if (!isError){
                if (error!=null){
                    firePropertyChangeEvent(
                        ViewController.MedicalConditionViewControllerPropertyChangeEvent
                                .MEDICAL_CONDITION_VIEW_CONTROLLER_ERROR_RECEIVED
                                .toString(),
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
