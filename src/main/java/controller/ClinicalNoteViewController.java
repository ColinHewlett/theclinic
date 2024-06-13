/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.entity.Entity;
import model.entity.ClinicalNote;
import model.entity.Appointment;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import view.views.non_modal_views.DesktopView;
import model.*;
import repository.StoreException;
import javax.swing.JOptionPane;
import view.View;

/**
 *
 * @author colin
 */
public class ClinicalNoteViewController extends ViewController{
    
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
        
        ViewController.ClinicalNoteViewControllerActionEvent actionCommand =
                ViewController.ClinicalNoteViewControllerActionEvent
                        .valueOf(e.getActionCommand());
            switch (actionCommand){
                case VIEW_CLOSED_NOTIFICATION:
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
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
                                getDescriptor().getControllerDescription().getAppointment();
                        clinicalNote = getDescriptor()
                                .getViewDescription().getClinicalNote();
                        clinicalNote.insert();
                        doClinicalNoteForAppoinmentRequest(e);
                        if(getDescriptor().getControllerDescription()
                                .getClinicalNote() == null){
                            error = "Attempt to create a new clinical note failed";
                            getDescriptor().getControllerDescription().setError(error);
                            firePropertyChangeEvent(
                                ViewController.ClinicalNoteViewControllerPropertyChangeEvent
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
                        clinicalNote = getDescriptor()
                                    .getViewDescription().getClinicalNote();
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
                        clinicalNote = getDescriptor()
                                    .getViewDescription().getClinicalNote();
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
        Appointment appointment = 
                getDescriptor().getControllerDescription().getAppointment();
        ClinicalNote clinicalNote = new ClinicalNote(appointment);
        clinicalNote.setScope(Entity.Scope.FOR_APPOINTMENT);
        clinicalNote = clinicalNote.read();
        if (clinicalNote.get().isEmpty())
            getDescriptor().getControllerDescription()
                    .setClinicalNote(null);
        else getDescriptor().getControllerDescription()
                    .setClinicalNote(clinicalNote.get().get(0));
        firePropertyChangeEvent(
            ViewController.ClinicalNoteViewControllerPropertyChangeEvent
                    .CLINICAL_NOTE_RECEIVED.toString(),
            (View)e.getSource(),
            this,
            null,
            null
        );
    }
}
