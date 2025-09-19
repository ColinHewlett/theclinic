/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

/**
 *
 * @author colin
 */

import colinhewlettsolutions.client.model.entity.Patient;
import java.awt.event.ActionEvent;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.repository.StoreException;

public class ArchivedPatientsViewController extends ViewController {
    
    public ArchivedPatientsViewController(
            DesktopViewController controller,
            DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Patient patient = null;
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerActionEvent actionCommand = ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
            switch (actionCommand){
                case INITIALISE_VIEW_CONTROLLER:
                    try{
                        doInitialiseView();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "Handled in PatientAppointmentDataViewController::actionPerformed(INITIALISE_VIEW)";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }
        }else{
            ViewController.ArchivedPatientsViewControllerActionEvent actionCommand =
                    ViewController.ArchivedPatientsViewControllerActionEvent.valueOf(e.getActionCommand());
            switch(actionCommand){
                case ARCHIVED_PATIENT_REQUEST:
                    try{
                        fetchAndSendViewPatientAppointmentData();
                    }catch(StoreException ex){
                        String message = ex.getMessage() +"\n";
                        message = message + "Exception handled in "
                                + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }          
                    break;
                case PATIENT_RESTORE_REQUEST:
                    patient = (Patient)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.PATIENT);
                    if (patient != null){
                        try{
                            for(Patient _patient : patient.get()){
                                _patient.setIsArchived(false);
                                _patient.update();
                                patient = _patient;
                            }
                        }catch(StoreException ex){
                            String message = ex.getMessage() +"\n";
                            message = message + "Exception handled in "
                                    + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                        }
                        try{
                            fetchAndSendViewPatientAppointmentData();
                        }catch(StoreException ex){
                            String message = ex.getMessage() +"\n";
                            message = message + "Exception handled in "
                                    + this.getClass().getSimpleName() + "::actionPerformed)" + actionCommand + "(";
                            displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                        }
                        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
                        getDescriptor().getControllerDescription().setViewMode(ViewMode.PATIENT_RESTORE);
                        firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    ARCHIVED_PATIENTS_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                        );
                    }
                    break;
                case VIEW_CLOSE_NOTIFICATION:
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
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
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ArchivedPatientsViewControllerPropertyChangeEvent event = 
                ArchivedPatientsViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(event){
            case ARCHIVED_PATIENTS_VIEW_CONTROLLER_CHANGE_NOTIFICATION:
                try{
                    fetchAndSendViewPatientAppointmentData();
                }catch(StoreException ex){
                    String message = ex.getMessage() +"\n";
                    message = message + "Exception handled in "
                            + this.getClass().getSimpleName() + "::propertyChange(" + event + ")";
                    displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                }
        }
    }
    
    private void fetchAndSendViewPatientAppointmentData()throws StoreException{
        Patient patient = new Patient();
        patient.setScope(Entity.Scope.ARCHIVED);
        patient = patient.read();
        getDescriptor().getControllerDescription().setProperty(SystemDefinition.Properties.PATIENT, patient);
        firePropertyChangeEvent(
                ViewController.ArchivedPatientsViewControllerPropertyChangeEvent.
                        ARCHIVED_PATIENT_RECEIVED.toString(),
                getView(),
                this,
                null,
                null
        );
    }
    
    private void doInitialiseView() throws StoreException{
        /**
         * -- initialise a PatientAppointmentData object and send to view (currently disabled)
         */

    }
}
