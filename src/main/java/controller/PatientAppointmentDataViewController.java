/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.entity.Entity;
import model.entity.Patient;
import model.entity.PatientAppointmentData;
import java.awt.event.ActionEvent;
import view.views.non_modal_views.DesktopView;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import model.repository.StoreException;

/**
 *
 * @author colin
 */
public class PatientAppointmentDataViewController extends ViewController{
    
    public PatientAppointmentDataViewController(
            DesktopViewController controller,
            DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Patient patient = null;
        PatientAppointmentData pad = null;
        if (e.getSource() instanceof DesktopViewController){
            ViewController.DesktopViewControllerActionEvent actionCommand = ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
            switch (actionCommand){
                case INITIALISE_VIEW:
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
            ViewController.PatientAppointmentDataViewControllerActionEvent actionCommand =
                    ViewController.PatientAppointmentDataViewControllerActionEvent.valueOf(e.getActionCommand());
            switch(actionCommand){
                case PATIENT_APPOINTMENT_DATA_REQUEST:
                    try{
                        fetchAndSendViewPatientAppointmentData();
                    }catch(StoreException ex){
                        String message = ex.getMessage() +"\n";
                        message = message + "Exception handled in "
                                + this.getClass().getSimpleName() + "::actionPerformed(" + actionCommand + "(";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }          
                    break;
                case PATIENT_ARCHIVE_REQUEST:
                    pad = getDescriptor().getViewDescription().getPatientAppointmentData();
                    try{
                        for(PatientAppointmentData _pad : pad.get()){
                            patient = _pad.getPatient();
                            patient.setIsArchived(true);
                            patient.update();
                        }
                    }catch(StoreException ex){
                        String message = ex.getMessage() +"\n";
                        message = message + "Exception handled in "
                                + this.getClass().getSimpleName() + "::actionPerformed(" + actionCommand + "(";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }
                    try{
                        pad.setFromYear(getDescriptor().getControllerDescription().getPatientAppointmentData().getFromYear());
                        pad.setToYear(getDescriptor().getControllerDescription().getPatientAppointmentData().getToYear());
                        pad.setScope(getDescriptor().getControllerDescription().getPatientAppointmentData().getScope());
                        getDescriptor().getViewDescription().setPatientAppointmentData(pad);
                        fetchAndSendViewPatientAppointmentData();
                    }catch(StoreException ex){
                        String message = ex.getMessage() +"\n";
                        message = message + "Exception handled in "
                                + this.getClass().getSimpleName() + "::actionPerformed(" + actionCommand + "(";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
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
        
    }
    
    private void fetchAndSendViewPatientAppointmentData()throws StoreException{
        /**
         * [1] on entry ViewDescription::PatientAppointmentData (pad) is initialsed; thus
         * -- pad.FromYear
         * -- pad.ToYear
         * -- pad.Scope
         * [2] these values are copied over to the new PatientAppointmentData (_pad) object after its been read from store
         * [3] the new PatientAppointmentData (_pad) is then saved to the ControllerDescription 
         * -- thus the view knows the values (from/to year and sort order(scope)) used to produce the PatientAutomationData collection 
         */
        PatientAppointmentData pad = null;
        PatientAppointmentData _pad = null;
        pad = getDescriptor().getViewDescription().getPatientAppointmentData();
        //pad.delete();
        //pad.insert();
        _pad = pad.read();
        if (_pad==null) _pad = new PatientAppointmentData();
        _pad.setFromYear(pad.getFromYear());
        _pad.setToYear(pad.getToYear());
        _pad.setScope(pad.getScope());
        getDescriptor().getControllerDescription().setPatientAppointmentData(_pad);
        firePropertyChangeEvent(
                ViewController.PatientAppointmentDataViewControllerPropertyChangeEvent.
                        PATIENT_APPOINTMENT_DATA_RECEIVED.toString(),
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
        PatientAppointmentData pad = new PatientAppointmentData();
        pad.setFromYear(1992);
        pad.setToYear(LocalDate.now().getYear() + 1);
        pad.setScope(Entity.Scope.BY_LAST_APPOINTMENT_DATE);
        getDescriptor().getViewDescription().setPatientAppointmentData(pad);
        //fetchAndSendViewPatientAppointmentData();
    }
}
