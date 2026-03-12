/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller;

import theclinic.model.entity.SecondaryCondition;
import theclinic.model.entity.PrimaryCondition;
import theclinic.model.entity.Condition;
import theclinic.model.entity.PatientPrimaryCondition;
import theclinic.model.entity.PatientCondition;
import theclinic.model.entity.Patient;
import theclinic.model.entity.Entity;
import theclinic.model.entity.PatientSecondaryCondition;
import theclinic.model.non_entity.ConditionWithState;
import static theclinic.controller.ViewController.displayErrorMessage;
import theclinic.controller.exceptions.TemplateReaderException;
import theclinic.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import theclinic.model.repository.StoreException;
import theclinic.view.View;

/**
 *
 * @author colin
 */
public class PatientMedicalHistoryViewController extends ViewController{
    
    public enum Actions{
        PATIENT_CONDITION_READ_REQUEST,
        PATIENT_CONDITION_CREATE_REQUEST,
        PATIENT_CONDITION_DELETE_REQUEST,
        PATIENT_CONDITION_COMMENT_UPDATE_REQUEST,
        PATIENT_CONDITION_COMMENT_DELETE_REQUEST,
        PRINT_PATIENT_MEDICAL_HISTORY_REQUEST,
        VIEW_CLOSE_NOTIFICATION
    }
    
    public enum Properties{
        CONDITION_WITH_STATE_RECEIVED,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_ERROR_RECEIVED,
        PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_CHANGE_NOTIFICATION
    }
    
    public PatientMedicalHistoryViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ActionEvent actionEvent = null;
        PatientPrimaryCondition ppc = null;
        PatientSecondaryCondition psc = null;
        //PatientSecondaryCondition psc = new PatientSecondaryCondition(pac.getPatient(),new SecondaryCondition());;
        //Condition condition = null;
        boolean isError = false;
        boolean isViewClosed = false;
        String error = null;
        ConditionWithState conditionWithState = null;
        PatientCondition pac = null;
        Patient patient = (Patient)getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        ConditionWithState CWS = (ConditionWithState)getDescriptor()
                .getViewDescription().getProperty(SystemDefinition.Properties.CONDITION_WITH_STATE);       

        //appointmentTreatment.setScope(Entity.Scope.SINGLE);
        Actions actionCommand =
            Actions.valueOf(e.getActionCommand());
        try{
            switch (actionCommand){
                case PRINT_PATIENT_MEDICAL_HISTORY_REQUEST:
                    boolean isExisingPatient = true;
                    doPrintPatientMedicalHistoryQuestionnaireRequest(isExisingPatient);
                    
                    /**
                     * once print operation complete 
                     * -- drop through to VIEW_CLOSE_NOTIFICATION action command
                     * -- because current instance of VC is 'view-less'
                     * -- this ensures Desktop VC closes down the instance anyway
                     */
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    isViewClosed = true;
                    break;
                case VIEW_CLOSE_NOTIFICATION:
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    isViewClosed = true;
                    break;
                case PATIENT_CONDITION_COMMENT_UPDATE_REQUEST:
                    if (CWS!=null){
                        if (CWS.getCondition().getIsPrimaryCondition())
                            pac = new PatientPrimaryCondition(patient, 
                                    (PrimaryCondition)CWS.getCondition());
                        else if(CWS.getCondition().getIsSecondaryCondition())
                            pac = new PatientSecondaryCondition(patient, 
                                    (SecondaryCondition)CWS.getCondition());
                        else {
                            isError = true;
                            error = "Received patient condition not defined correctly/n"
                                    + "Actiom to update comment for condition aborted";
                        }
                    }else{
                        isError = true;
                        error = "Patient condition has not been defined;/n"
                                + "Action to update condition comment aborted";
                    }
                    if(!isError){
                        pac.setComment(CWS.getComment());
                        pac.update();
                        conditionWithState = getConditionWithState(pac);
                    }
                    break;
                case PATIENT_CONDITION_READ_REQUEST:
                    if (CWS==null) {
                        isError = true;
                        error = "Patient condition has not been defined;/n"
                                + "Patient condition read requewst aborted";
                    }else{
                        if(CWS.getCondition().getIsPrimaryCondition()){
                            ppc = new PatientPrimaryCondition(patient, (PrimaryCondition)CWS.getCondition());
                            conditionWithState = getConditionWithState(ppc);
                        }else if(CWS.getCondition().getIsSecondaryCondition()){
                            psc = new PatientSecondaryCondition(patient, (SecondaryCondition)CWS.getCondition());
                            conditionWithState = getConditionWithState(psc);
                        }else{
                            isError = true;
                            error = "The view's patient condition has not been properly defined/n"
                                    + "Patient secondary condition read request aborted";
                        }
                    }

                    break;
                case PATIENT_CONDITION_CREATE_REQUEST:
                    if (CWS==null) {
                        isError = true;
                        error = "A condition with state has not been defined;/n"
                                + "Action to add to patient conditions aborted";
                    }else{
                        if (CWS.getCondition().getIsPrimaryCondition())
                            pac = new PatientPrimaryCondition(
                                    patient,(PrimaryCondition)CWS.getCondition());
                        else if(CWS.getCondition().getIsSecondaryCondition())
                            pac = new PatientSecondaryCondition(
                                    patient,(SecondaryCondition)CWS.getCondition());
                        else{
                            isError = true;
                            error = "Patient condition has not been defined properly (primary or secondary?)/n"
                                    + "Action to add to patient conditions aborted";
                        }
                        pac.insert();
                        
                        synchPrimaryConditionStateWithItsSecondaries(pac);
                        conditionWithState = getConditionWithState(pac);
                    }
                    break;
                case PATIENT_CONDITION_DELETE_REQUEST:
                    isError = false;
                    if (CWS==null) {
                        isError = true;
                        error = "Patient condition has not been defined;/n"
                                + "Action to remove a patient condition aborted";
                    }else{
                        if (CWS.getCondition().getIsPrimaryCondition())
                            pac = new PatientPrimaryCondition(
                                    patient,(PrimaryCondition)CWS.getCondition());
                        else if(CWS.getCondition().getIsSecondaryCondition())
                            pac = new PatientSecondaryCondition(
                                    patient,(SecondaryCondition)CWS.getCondition());
                        else{
                            isError = true;
                            error = "Patient condition has not been defined properly (primary or secondary?)/n"
                                    + "Action to add to delete patient condition aborted";
                        }
                        if (!isError){
                            pac.setScope(Entity.Scope.SINGLE);
                            pac.delete();

                            synchPrimaryConditionStateWithItsSecondaries(pac);
                            conditionWithState = getConditionWithState(pac);
                        }
                    }
                    break;
            }
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Exception raised in "
                    + "PatientMedicalHistoryViewController.actionPerformed"
                    + "(" + actionCommand + ")";
            displayErrorMessage(message, "View controller error", 
                    JOptionPane.WARNING_MESSAGE);
            isError = true;
        }
        if((!actionCommand.equals(Actions.VIEW_CLOSE_NOTIFICATION))&&
                (!actionCommand.equals(
                Actions.PRINT_PATIENT_MEDICAL_HISTORY_REQUEST))){//handles case idf VC is about to close 
            if (!isError){
                if (error == null){ // ensures upstream StoreException error is not handled again
                    getDescriptor().getControllerDescription()
                            .setProperty(SystemDefinition.Properties.CONDITION_WITH_STATE, conditionWithState);
                    firePropertyChangeEvent(
                            Properties.CONDITION_WITH_STATE_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    firePropertyChangeEvent(
                            DesktopViewController.Properties.
                                    PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }else{//error != null; view controller error message on its way
                    getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                }
            }
        }
    }
    
    private void fetchMedicalConditionsOnSystem(){
        PrimaryCondition primaryCondition = new PrimaryCondition();
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
            getFatPrimaryConditions();
        }catch(StoreException ex){
            String message = ex.getMessage() ;
            displayErrorMessage(message, 
                    "Medical condition view controller error", 
                    JOptionPane.WARNING_MESSAGE);
 
        }   
    }
    
    private void getFatPrimaryConditions()throws StoreException{
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
    }
}
