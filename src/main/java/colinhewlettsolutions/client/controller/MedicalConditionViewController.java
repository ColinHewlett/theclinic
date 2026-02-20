/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.model.entity.SecondaryCondition;
import colinhewlettsolutions.client.model.entity.PrimaryCondition;
import colinhewlettsolutions.client.model.entity.Condition;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.Entity;
import static colinhewlettsolutions.client.controller.ViewController.*;
import colinhewlettsolutions.client.controller.exceptions.TemplateReaderException;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class MedicalConditionViewController extends ViewController{
    
    public enum Actions{
        PRIMARY_CONDITION_CREATE_REQUEST,
        PRIMARY_CONDITION_DELETE_REQUEST,
        PRIMARY_CONDITION_READ_REQUEST,
        PRIMARY_CONDITION_RENAME_REQUEST,
        PRINT_NEW_PATIENT_DETAILS_REQUEST,
        SECONDARY_CONDITION_CREATE_REQUEST,
        SECONDARY_CONDITION_DELETE_REQUEST,
        SECONDARY_CONDITION_READ_REQUEST,
        SECONDARY_CONDITION_RENAME_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public enum Properties{
        MEDICAL_CONDITION_VIEW_CONTROLLER_ERROR_RECEIVED,
        PRIMARY_CONDITION_RECEIVED,
        CONDITION_ERROR_RECEIVED
    }
    
    public MedicalConditionViewController(DesktopViewController controller,
                               Descriptor descriptor, DesktopView desktopView){
        setMyController(controller);
        setDescriptor(descriptor);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        PrimaryCondition primaryCondition = (PrimaryCondition)getDescriptor()
                .getViewDescription().getProperty(SystemDefinition.Properties.PRIMARY_CONDITION);
        SecondaryCondition secondaryCondition = null;
        Patient patient = null;
        String error = null;
        boolean isError = false;
        /*
        if (e.getSource() instanceof DesktopViewController){
            //doDesktopViewControllerActionRequest(e);
        }
        else{ */ 
        MedicalConditionViewController.Actions actionCommand =
            MedicalConditionViewController.Actions.valueOf(e.getActionCommand());
        switch (actionCommand){
            case PRINT_NEW_PATIENT_DETAILS_REQUEST:
                try{
                    this.doPrintPatientMedicalHistoryQuestionnaireRequest(false);
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
            case VIEW_CLOSE_NOTIFICATION:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    DesktopViewController.Actions.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case PRIMARY_CONDITION_CREATE_REQUEST:
                try{
                    Integer primaryConditionKey = primaryCondition.insert();
                    getAllPrimaryConditions(e);

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
                    getAllSecondaryConditionsFor(e,primaryCondition);    
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
                try{
                    SecondaryCondition sc = new SecondaryCondition(primaryCondition);
                    sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
                    sc = sc.read();
                    if (!sc.get().isEmpty()){
                        isError = true;
                        error = "Cannot delete '" + primaryCondition.getDescription() 
                                + "' because it has associated secondary conditions";
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
                        getAllPrimaryConditions(e);                        
                    }catch(StoreException ex){
                        String message = null;
                        if (ex.getMessage().contains("integrity constraint violation")){
                            error = "Selected condition currently in use; requested deletion aborted";
                            isError = true;
                        }else{
                            message = ex.getMessage() + "\nHandled in "
                                    + "MedicalConditionViewController::actionPerformed("
                                    + actionCommand + ")";
                            displayErrorMessage(message, 
                                    "Medical condition view controller error", 
                                    JOptionPane.WARNING_MESSAGE);
                            isError = true;
                        }
                    }
                }
                break;
            case SECONDARY_CONDITION_DELETE_REQUEST:
                secondaryCondition = primaryCondition.getSecondaryCondition();
                secondaryCondition.setScope(Entity.Scope.SINGLE);
                try{
                    secondaryCondition.delete();
                    getAllSecondaryConditionsFor(e, primaryCondition);
                }catch(StoreException ex){
                    if (ex.getMessage().contains("integrity constraint violation")){
                        error = "medical condition currently in use by one or more patients; requested deletion aborted";
                        isError = true;
                    }else{
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
                    /*if (primaryCondition.get().isEmpty()){
                        primaryCondition = extractMedicalHistoryFromTemplate();
                        for(Condition condition : primaryCondition.get()){
                            PrimaryCondition pCondition = (PrimaryCondition)condition;
                            //pCondition.setPatient(patient);
                            Integer pConditionKey = pCondition.insert();
                            if (!pCondition.getSecondaryCondition().get().isEmpty()){
                                for (Condition c : pCondition.getSecondaryCondition().get()){
                                    SecondaryCondition sCondition = (SecondaryCondition)c;
                                    sCondition.setPrimaryCondition(new PrimaryCondition(pConditionKey));
                                    sCondition.insert();
                                }
                            }
                        }   
                        setExtractedPrimaryConditionFromTemplate(primaryCondition);
                    }*/
                    getAllPrimaryConditions(e);
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
                secondaryCondition.setPrimaryCondition(primaryCondition);
                secondaryCondition.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
                try{
                    secondaryCondition = secondaryCondition.read();
                    secondaryCondition.setPrimaryCondition(primaryCondition);
                    primaryCondition.setSecondaryCondition(secondaryCondition);
                    getDescriptor().getControllerDescription()
                            .setProperty(SystemDefinition.Properties.PRIMARY_CONDITION, primaryCondition);
                    firePropertyChangeEvent(
                        MedicalConditionViewController.Properties.
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
                    getAllPrimaryConditions(e);
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\nHandled in "
                            + "MedicalConditionViewController::actionPerformed("
                            + actionCommand + ")";
                    displayErrorMessage(message, 
                            "Medical condition view controller error", 
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                firePropertyChangeEvent(
                    DesktopViewController.Properties.
                            MEDICAL_CONDITION_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                    (DesktopViewController)getMyController(),
                    this,
                    null,
                    null     
                );
                break;
            case SECONDARY_CONDITION_RENAME_REQUEST:{
                try{
                    secondaryCondition = primaryCondition.getSecondaryCondition();
                    secondaryCondition.update();
                    getAllSecondaryConditionsFor(e,primaryCondition);
                    firePropertyChangeEvent(
                        DesktopViewController.Properties.
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
        }       
        if (isError){
            if (error!=null){
                getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                firePropertyChangeEvent(
                    MedicalConditionViewController.Properties.MEDICAL_CONDITION_VIEW_CONTROLLER_ERROR_RECEIVED
                            .toString(),
                    (View)e.getSource(),
                    this,
                    null,
                    null
                );
            }
        }   
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
 
    private void getAllPrimaryConditions(ActionEvent e)throws StoreException{
        PrimaryCondition primaryCondition = new PrimaryCondition();
        primaryCondition.setScope(Entity.Scope.ALL);
        primaryCondition = primaryCondition.read();
        SecondaryCondition sc = null;
        PrimaryCondition pc = null;
        for(Condition c : primaryCondition.get()){
            pc = (PrimaryCondition)c;
            sc = new SecondaryCondition(pc);
            sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
            sc = sc.read();
            pc.setSecondaryCondition(sc);
        }
        getDescriptor().getControllerDescription()
                .setProperty(SystemDefinition.Properties.PRIMARY_CONDITION, primaryCondition);
        firePropertyChangeEvent(
            MedicalConditionViewController.Properties.PRIMARY_CONDITION_RECEIVED.toString(),
            (View)e.getSource(),
            this,
            null,
            null
        );
        
    }
    
    private void getAllSecondaryConditionsFor(ActionEvent e,PrimaryCondition pc)throws StoreException{
        SecondaryCondition sc = new SecondaryCondition(pc);
        sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
        sc = sc.read();
        pc.setSecondaryCondition(sc);
        getDescriptor().getControllerDescription()
                .setProperty(SystemDefinition.Properties.PRIMARY_CONDITION, pc);
        firePropertyChangeEvent(
            MedicalConditionViewController.Properties.
                    PRIMARY_CONDITION_RECEIVED.toString(),
            (View)e.getSource(),
            this,
            null,
            null
        );
    }
}
