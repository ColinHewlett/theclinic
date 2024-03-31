/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ViewController.displayErrorMessage;
import controller.ViewController.NotesViewControllerActionEvent;
import java.awt.Dimension;
import model.Patient;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import model.Entity;
/*28/03/2024import model.PatientNote;*/
import repository.StoreException;
import view.View;
import view.views.modal_views.ModalView;
/*28/03/2024import view.views.non_modal_views.NotesView;*/
import view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class NotesViewController extends ViewController{
    private Patient patient = null;
    
    public NotesViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerActionRequest(e);
        }
        else{                                    
            View the_view = (View)e.getSource();
            switch (the_view.getMyViewType()){
                case NOTES_VIEW:
                    doPrimaryViewActionRequest(e);
                    break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    private void initialiseView()throws StoreException{       
        if (getDescriptor().getControllerDescription().getPatient()==null){
            patient = new Patient();
            patient.setScope(Entity.Scope.ALL);
            patient.read();
            Patient patients[] = new Patient[patient.get().size()];
            for (int i = 0; i < patient.get().size(); i++){
                patients[i] = patient.get().get(i);
            }
            Patient patient = (Patient)JOptionPane.showInternalInputDialog(
                    getDesktopView().getContentPane(),"","Select patient",
                    JOptionPane.OK_CANCEL_OPTION,
                    null,
                    patients,
                    patients[0]);
            if (patient!=null){
                getDescriptor()
                        .getControllerDescription().setPatient(patient);
                setView(new View().make(View.Viewer.NOTES_VIEW,
                        this, 
                        getDesktopView()));
            }
            /**
             * 12/02/2024 code logic update
             * -- required because when patient = null, desktop vc has to be informed 
             * -- in order to remove the notes view vc from its collection of active VCs
             */
            else{
                ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                 this.getMyController().actionPerformed(actionEvent);
            }
        }else{
                setView(new View().make(View.Viewer.NOTES_VIEW,
                        this, 
                        getDesktopView()));
        }
    }
    
    private void doDesktopViewControllerActionRequest(ActionEvent e){
        try{
            if (e.getActionCommand().equals(
                    ViewController.DesktopViewControllerActionEvent
                            .INITIALISE_VIEW.toString()))
                initialiseView();
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage() + "/n"
                    + "Raised in NotesViewController::"
                    + "doDesktopViewControllerActionRequest(INITIALISE_VIEW",
                    "Notes view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doPrimaryViewActionRequest(ActionEvent e){
        /*28/03/2024
        ActionEvent actionEvent = null;
        boolean isPatientSpecified = true;
        Patient patient = new Patient();
        PatientNote patientNote = getDescriptor()
                .getViewDescription().getPatientNote();;
        ViewController.NotesViewControllerActionEvent actionCommand =
                ViewController.NotesViewControllerActionEvent.valueOf(e.getActionCommand());
        try{
            switch(actionCommand){ 
                case VIEW_ACTIVATED_NOTIFICATION:
                    actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            ViewController.DesktopViewControllerActionEvent.
                                    VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString());
                    this.getMyController().actionPerformed(actionEvent);
                    break;  
                case VIEW_CHANGED_NOTIFICATION:
                    actionEvent = new ActionEvent(
                           this,ActionEvent.ACTION_PERFORMED,
                           ViewController.DesktopViewControllerActionEvent.
                                   VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                    this.getMyController().actionPerformed(actionEvent);
                    break;
                case VIEW_CLOSED_NOTIFICATION:
                    actionEvent = new ActionEvent(
                           this,ActionEvent.ACTION_PERFORMED,
                           ViewController.DesktopViewControllerActionEvent.
                                   VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    this.getMyController().actionPerformed(actionEvent);
                    break;
                case NOTES_FOR_PATIENT_REQUEST:
                    break;
                case NOTES_FOR_PATIENT_CHANGE_REQUEST:
                    switch(getDescriptor().getViewDescription().getViewMode()){
                        case CREATE:
                            patientNote.insert();
                            break;
                        case UPDATE:
                            patientNote.update();
                            break;
                    }
                    break;
                case NOTES_PATIENT_SELECTION_REQUEST:
                    patient = new Patient();
                    patient.setScope(Entity.Scope.ALL);
                    patient.read(); 
                    getDescriptor().getControllerDescription().setPatients(patient.get());
                    setModalView((ModalView)new View().make(
                            View.Viewer.PATIENT_SELECTION_VIEW,
                            this, 
                            this.getDesktopView()).getModalView());
                    
                    if (getDescriptor().getViewDescription().getPatient()==null){
                        JOptionPane.showMessageDialog(getDesktopView(), 
                                "cannot display a patient's notes "
                                        + "unless a patient is specified");
                        isPatientSpecified = false;
                    }else {
                        isPatientSpecified = true;
                        getDescriptor().getControllerDescription()
                                .setPatient(getDescriptor()
                                        .getViewDescription().getPatient());
                    }
                    break;       
            }
            if (isPatientSpecified){
                patientNote = new PatientNote(
                        getDescriptor().getControllerDescription().getPatient());
                patientNote.setScope(Entity.Scope.FOR_PATIENT);
                patientNote.read();
                getDescriptor().getControllerDescription()
                        .setPatientNotes(patientNote.get());
                firePropertyChangeEvent(
                           ViewController.NotesViewControllerPropertyChangeEvent
                                   .NOTES_FOR_PATIENT_RECEIVED.toString(),
                            (NotesView)e.getSource(),
                            this,
                            null,
                            getDescriptor()
                );
            }
            
            
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage() + "\n"
                    + "Raised in PatientViewController::doPatientNotesEditorViewChange()",
                    "Patient view controller error",
                    JOptionPane.WARNING_MESSAGE);
        }*/
    }
    
    private void doSecondaryViewActionRequest(ActionEvent e){
        
    }
    
    /*
    public void initialiseView(){
       firePropertyChangeEvent(
                           ViewController.NotesViewControllerPropertyChangeEvent.
                            PATIENT_SELECTION_REQUESTED.toString(),
                            getView(),
                            this,
                            null,
                            null
       ); 
    }
*/
}
