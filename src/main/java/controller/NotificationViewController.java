/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Notification;
import model.Entity.Scope;
import java.beans.PropertyVetoException;
/**
 * ThePatient is used temporarily to start a refactored and restructured Patient process
 * -- primary difference between Patient & ThePatient is ThePatient.Collection inner class
 * -- this removes the need for a separate Patients class
 * -- thus EntityDescriptor.thePatient is also being updated to an ArrayList<ThePatient>
 */
import model.Patient;

import repository.StoreException;//01/03/2023
import view.views.non_modal_views.DesktopView;
import view.View;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import view.views.modal_views.ModalView;

/**
 *
 * @author colin
 */
public class NotificationViewController extends ViewController{
    
    private PatientNotificationViewListState viewListState = null;

    enum PatientNotificationViewListState {ALL_NOTIFICATION_STATE, UNACTIONED_NOTIFICATION_STATE};
    
    private PatientNotificationViewListState getViewListState(){
        return viewListState;
    }
    
    private void setViewListState(PatientNotificationViewListState state){
        viewListState = state;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() instanceof DesktopViewController){
            doDesktopViewControllerActionRequest(e);
        }
        else{                                    
            View the_view = (View)e.getSource();
            switch (the_view.getMyViewType()){
                case NOTIFICATION_VIEW:
                    doPrimaryViewActionRequest(e);
                    break;
                default:
                    try{
                        doSecondaryViewActionRequest(e);
                    }catch (StoreException ex){
                        String message = "StoreException handled in VC's actionPerformed doSecondaryActionRequest()\n";
                        displayErrorMessage(message + ex.getMessage(), 
                                "Patient Notification VC error", 
                                JOptionPane.WARNING_MESSAGE);
                    }
            }
        }
    }
    
    private void doDesktopViewControllerActionRequest(ActionEvent e){
        ViewController.DesktopViewControllerActionEvent actionCommand =
               ViewController.DesktopViewControllerActionEvent.valueOf(e.getActionCommand());
        try{
            switch(actionCommand){
                case REFRESH_DISPLAY_REQUEST:
                    switch(getViewListState()){
                        case UNACTIONED_NOTIFICATION_STATE:
                            doUnactionedPatientNotificationsRequest();
                            break;
                        case ALL_NOTIFICATION_STATE:
                            doPatientNotificationsRequest();
                            break;
                    }
            }
        }catch(StoreException ex){
            String error = ex.getMessage() +"\n"
                    + "Raised in PatientNotificationViewController."
                    + "doDesktopViewControllerActionRequest";
            displayErrorMessage(error,"Patient notification view controller",
                    JOptionPane.WARNING_MESSAGE);          
        }

    }
    
    private void doPrimaryViewActionRequest(ActionEvent e){
        ActionEvent actionEvent = null;
        ViewController.NotificationViewControllerActionEvent actionCommand =
               ViewController.NotificationViewControllerActionEvent.valueOf(e.getActionCommand());
        try{
            switch (actionCommand){
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
                case UNACTIONED_NOTIFICATIONS_REQUEST:
                    doUnactionedPatientNotificationsRequest();
                    setViewListState(PatientNotificationViewListState.UNACTIONED_NOTIFICATION_STATE);
                    break;
                case NOTIFICATIONS_REQUEST:
                    doPatientNotificationsRequest();
                    setViewListState(PatientNotificationViewListState.ALL_NOTIFICATION_STATE);
                    break;
                case ACTION_NOTIFICATION_REQUEST:
                    doActionPatientNotificationRequest();                   
                    break;
                case CREATE_NOTIFICATION_REQUEST:
                    doCreatePatientNotificationRequest();
                    break;
                case CANCEL_NOTIFICATION_REQUEST:
                        doCancelNotificationRequest();
                        break;
                case UPDATE_NOTIFICATION_REQUEST:
                    doUpdatePatientNotificationRequest();
                    break;
            }
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Patient notification view controller error", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doUnactionedPatientNotificationsRequest()throws StoreException{
        setDescriptor(new Descriptor());
        sendPrimaryViewPatientNotifications(Scope.UNACTIONED);
    }
    
    private void doPatientNotificationsRequest()throws StoreException{
        setDescriptor(new Descriptor());
        sendPrimaryViewPatientNotifications(Notification.Scope.ALL);
    }
    
    private void doNotificationsForPatientRequest()throws StoreException{
        Patient patient = getDescriptor().getViewDescription().getPatient();
        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setScope(Scope.FOR_PATIENT);
        notification.read();
        getDescriptor().getControllerDescription()
                .setPatientNotifications(notification.get());
        firePropertyChangeEvent(
                NotificationViewControllerPropertyChangeEvent
                        .RECEIVED_PATIENT_NOTIFICATIONS.toString(),
                getSecondaryView(),
                this,
                null,
                null
        );
    }
    
    /*
    private void getPatientNotificationsFor(Notification notification){
        try{
            notification.read();
            getDescriptor().getControllerDescription().setPatientNotifications(
                    notification.get());
            
            pcSupportForView.addPropertyChangeListener(this.getView());
            pcEvent = new PropertyChangeEvent(this,
               ViewController.NotificationViewControllerPropertyChangeEvent.RECEIVED_PATIENT_NOTIFICATIONS.toString(),
               null,getDescriptor());
            pcSupportForView.firePropertyChange(pcEvent);
            pcSupportForView.removePropertyChangeListener(this.getView());
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Patient notification controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
*/
    private void doActionPatientNotificationRequest(){
        ArrayList<Notification> notifications = 
                getDescriptor().getViewDescription().getPatientNotifications();
        try{
            for (Notification patientNotification : notifications){
                patientNotification.action();
                doUnactionedPatientNotificationsRequest();
            }
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage(),
                    "Patient notification controller error", 
                    JOptionPane.WARNING_MESSAGE);
        } 
    }
    
    /**
     * method launches the patient notification editor view
 -- and initialises the accompanying Descriptor::patientNotifications as an empty ArrayList
 -- this on basis that the patient notification editor view will then know view is used for creation of a new notification
 -- 
     */
    private void doCreatePatientNotificationRequest(){
        //setOldEntityDescriptor(getDescriptor());
        setDescriptor(new Descriptor());
        getDescriptor().getControllerDescription().setPatientNotifications(new ArrayList<>());
        getDescriptor().getControllerDescription().setViewMode(ViewController.ViewMode.CREATE);

        try{
            Patient patient = new Patient();
            patient.setScope(Scope.ALL);
            patient.read();
            getDescriptor().getControllerDescription().setPatients(patient.get());

            View.setViewer(View.Viewer.NOTIFICATION_EDITOR_VIEW);
            setView((ModalView)new View().make(View.Viewer.NOTIFICATION_EDITOR_VIEW,
                    this, 
                    getDesktopView()).getModalView());
            //note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
            this.getMyController().actionPerformed(actionEvent);
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"PatientNotificaionViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doUpdatePatientNotificationRequest(){
        Notification notification = getDescriptor().getViewDescription().getNotification();
        //setOldEntityDescriptor(getDescriptor());
        //setDescriptor(new Descriptor());
        getDescriptor().getControllerDescription().setPatientNotification(notification);
        try{
            /**
             * send view collection of all patients on system
             * -- in case user wants to select another patient to update(?)
             */
            Patient patient = new Patient();
            //07/08/2022
            patient.setScope(Scope.ALL);
            patient.read();
            getDescriptor().getControllerDescription().setPatients(patient.get());
            /**
             * send view collection of previous notifications for this patient
             */
            Notification patientNotification = new Notification();
            patientNotification.setPatient(notification.getPatient());
            patientNotification.setScope(Scope.FOR_PATIENT);
            patientNotification.read();
            getDescriptor().getControllerDescription()
                    .setPatientNotifications(patientNotification.get());
            getDescriptor().getControllerDescription().
                    setViewMode(ViewController.ViewMode.UPDATE);
            setModalView((ModalView)new View().make(View.Viewer.NOTIFICATION_EDITOR_VIEW,
                    this,
                    getDesktopView()).getModalView());
            //note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
            this.getMyController().actionPerformed(actionEvent);
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"PatientNotificaionViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private ModalView modalView = null;
    private ModalView getSecondaryView(){
        return modalView;
    }
    private void setSecondaryView(ModalView value){
        modalView = value;
    }
    
    private void doSecondaryViewActionRequest(ActionEvent e)throws StoreException{
        setModalView((ModalView)e.getSource());
        setSecondaryView(getModalView());
        switch (getModalView().getMyViewType()){
            case NOTIFICATION_EDITOR_VIEW:
                ViewController.NotificationViewControllerActionEvent actionCommand =
               ViewController.NotificationViewControllerActionEvent.valueOf(e.getActionCommand());
                switch (actionCommand){
                    case MODAL_VIEWER_ACTIVATED:
                        //getSecondaryView().initialiseView();
                        break;
                    case NOTIFICATIONS_FOR_PATIENT_REQUEST:
                        doNotificationsForPatientRequest();
                        break;
                    case NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST:
                        doPatientNotificationEditorCreateNotificationRequest();
                        break;
                    case NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST:
                        doNotificationEditorUpdateNotificationRequest();
                        break;
                    case NOTIFICATION_EDITOR_CLOSE_VIEW_REQUEST:
                        doPatientNotificationEditorCloseViewRequest();
                        break;
                    case MODAL_VIEWER_DEACTIVATED:
                        closeSecondaryView();
                        break;
                }
                break;
                
            default:
                JOptionPane.showMessageDialog(getView(), 
                        "Unrecognised view type specified in PatientNotificationViewController::doSecondaryViewActionRequest()",
                        "Patient Notification View Controller Error", 
                        JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doPatientNotificationEditorCreateNotificationRequest()throws StoreException{
        Notification patientNotification = 
                getDescriptor().getViewDescription().getNotification();
        if (patientNotification!=null){
            patientNotification.insert();
            closeSecondaryView();
            if (getViewListState().equals(PatientNotificationViewListState.ALL_NOTIFICATION_STATE))
                sendPrimaryViewPatientNotifications(Notification.Scope.ALL);
            else sendPrimaryViewPatientNotifications(Notification.Scope.UNACTIONED);
            
        }
    }
    
    private void doCancelNotificationRequest()throws StoreException{
        Notification notification = 
                getDescriptor().getViewDescription().getNotification();
        if (notification!=null){
            notification.cancel();
        if (getViewListState().equals(PatientNotificationViewListState.ALL_NOTIFICATION_STATE))
                sendPrimaryViewPatientNotifications(Notification.Scope.ALL);
            else sendPrimaryViewPatientNotifications(Notification.Scope.UNACTIONED);

        }
    }
    
    private void doNotificationEditorUpdateNotificationRequest()throws StoreException{
        Notification notification = 
                getDescriptor().getViewDescription().getNotification();
        if (notification!=null){
            notification.update();
            closeSecondaryView();
        }
            
    }
    
    private void sendPrimaryViewPatientNotifications(Notification.Scope scope)throws StoreException{
        Notification patientNotification = new Notification();
        patientNotification.setScope(scope);
        patientNotification.read();
        getDescriptor().getControllerDescription().setPatientNotifications(patientNotification.get());
        String pcEventName;
        if(scope.equals(Notification.Scope.UNACTIONED)) pcEventName = 
                NotificationViewControllerPropertyChangeEvent.
                        RECEIVED_UNACTIONED_NOTIFICATIONS.toString();
        else pcEventName = NotificationViewControllerPropertyChangeEvent.
                RECEIVED_PATIENT_NOTIFICATIONS.toString();       
        firePropertyChangeEvent(
                pcEventName,
                getView(),
                this,
                null,
                null
        ); 
    }
    
    private void closeSecondaryView(){
        try{
            getModalView().setClosed(true);
        }catch (PropertyVetoException ex){

        }
        
        int test = 0;
    } 
    
    private void doPatientNotificationEditorCloseViewRequest(){
        
    }
    
    public NotificationViewController(DesktopViewController controller, 
                                                DesktopView desktopView)
                                                throws StoreException{
        setMyController(controller);
        setDesktopView(desktopView);
    }

 
}
