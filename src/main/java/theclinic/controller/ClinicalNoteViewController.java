/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.controller;

import theclinic.model.entity.Entity;
import theclinic.model.entity.ClinicalNote;
import theclinic.model.entity.Appointment;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import theclinic.view.views.non_modal_views.DesktopView;
import theclinic.model.repository.StoreException;
import javax.swing.JOptionPane;
import theclinic.view.View;

/**
 *
 * @author colin
 */
public class ClinicalNoteViewController extends ViewController{
    
    public enum Actions{
        CLINICAL_NOTE_FOR_APPOINTMENT_REQUEST,
        CLINICAL_NOTE_DELETE_REQUEST,
        CLINICAL_NOTE_CREATE_REQUEST,
        CLINICAL_NOTE_UPDATE_REQUEST,
        EMPTY_SLOTS_FROM_DAY_REQUEST,
        VIEW_CLOSED_NOTIFICATION,
        INITIALISE_VIEW
    }
    
    public enum Properties{
        CLINICAL_NOTE_RECEIVED,
        CLINICAL_NOTE_ERROR_RECEIVED
    }
    
    public ClinicalNoteViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        String error = null;
        String message = null;
        Appointment appointment = null;
        ClinicalNote clinicalNote = null;
        
        if (e.getSource() instanceof DesktopViewController){
            DesktopViewController.Actions actionCommand = DesktopViewController.Actions.valueOf(e.getActionCommand());
            switch (actionCommand){
                case INITIALISE_VIEW_CONTROLLER:
                    try{
                        doClinicalNoteForAppoinmentRequest(e);
                    }catch(StoreException ex){
                        message = ex.getMessage() + "\n"
                                + "Handled in PatientClinicalNoteViewController::actionPerformed(INITIALISE_VIEW)";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }
        }
        
        Actions actionCommand =
                Actions.valueOf(e.getActionCommand());
            switch (actionCommand){
                case VIEW_CLOSED_NOTIFICATION:
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                case CLINICAL_NOTE_FOR_APPOINTMENT_REQUEST:
                    try{
                        doClinicalNoteForAppoinmentRequest(e);
                    }catch(StoreException ex){
                        message = ex.getMessage() + "\n"
                                + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                                + actionCommand.toString() +")";
                        displayErrorMessage(message,"View controller error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case CLINICAL_NOTE_CREATE_REQUEST:
                    try{
                        appointment = 
                                (Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
                        clinicalNote = (ClinicalNote)getDescriptor()
                                .getViewDescription().getProperty(SystemDefinition.Properties.CLINICAL_NOTE);
                        clinicalNote.insert();
                        doClinicalNoteForAppoinmentRequest(e);
                        if(getDescriptor().getControllerDescription()
                                .getProperty(SystemDefinition.Properties.CLINICAL_NOTE) == null){
                            error = "Attempt to create a new clinical note failed";
                            getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.ERROR, error);
                            firePropertyChangeEvent(
                                ClinicalNoteViewController.Properties
                                        .CLINICAL_NOTE_ERROR_RECEIVED.toString(),
                                (View)e.getSource(),
                                this,
                                null,
                                null
                            );
                        }
                    }catch(StoreException ex){
                        message = ex.getMessage() + "\n"
                                + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                                + actionCommand.toString() +")";
                        displayErrorMessage(message,"View controller error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case CLINICAL_NOTE_DELETE_REQUEST:
                    try{
                        clinicalNote = (ClinicalNote)getDescriptor()
                                    .getViewDescription().getProperty(SystemDefinition.Properties.CLINICAL_NOTE);
                        clinicalNote.setScope(Entity.Scope.SINGLE);
                        clinicalNote.delete();
                        doClinicalNoteForAppoinmentRequest(e);
                    }catch(StoreException ex){
                        message = ex.getMessage() + "\n"
                                + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                                + actionCommand.toString() +")";
                        displayErrorMessage(message,"View controller error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case CLINICAL_NOTE_UPDATE_REQUEST:
                    try{
                        clinicalNote = (ClinicalNote)getDescriptor()
                                    .getViewDescription().getProperty(SystemDefinition.Properties.CLINICAL_NOTE);
                        clinicalNote.update();
                        doClinicalNoteForAppoinmentRequest(e);
                    }catch(StoreException ex){
                        message = ex.getMessage() + "\n"
                                + "Handled in ClinicalNoteViewController.actionPerformed(Scope = "
                                + actionCommand.toString() +")";
                        displayErrorMessage(message,"View controller error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                    
            }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    private void doClinicalNoteForAppoinmentRequest(ActionEvent e)throws StoreException{
        /*Appointment appointment = 
                getDescriptor().getControllerDescription().getAppointment();*/
        Appointment appointment = 
                (Appointment)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.APPOINTMENT);
        ClinicalNote clinicalNote = new ClinicalNote(appointment);
        clinicalNote.setScope(Entity.Scope.FOR_APPOINTMENT);
        clinicalNote = clinicalNote.read();
        if (clinicalNote.get().isEmpty())
            getDescriptor().getControllerDescription()
                    .setProperty(SystemDefinition.Properties.CLINICAL_NOTE, null);
        else getDescriptor().getControllerDescription()
                    .setProperty(SystemDefinition.Properties.CLINICAL_NOTE,clinicalNote.get().get(0));
        firePropertyChangeEvent(
            ClinicalNoteViewController.Properties.CLINICAL_NOTE_RECEIVED.toString(),
            getView(),
            //(View)e.getSource(),
            this,
            null,
            null
        );
    }
}
