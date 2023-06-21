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
import view.views.DesktopView;
import view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.JOptionPane;
import java.util.ArrayList;

/**
 *
 * @author colin
 */
public class PatientNotificationViewController extends ViewController{
    private ActionListener myController = null;
    private PropertyChangeSupport pcSupportForView = null;
    private PropertyChangeEvent pcEvent = null;
    private View view = null;
    private Descriptor oldEntityDescriptor = new Descriptor();
    private DesktopView desktopView = null;
    private View secondaryView = null;
    
    private PatientNotificationViewListState viewListState = null;

    enum PatientNotificationViewListState {ALL_NOTIFICATION_STATE, UNACTIONED_NOTIFICATION_STATE};
    
    private PatientNotificationViewListState getViewListState(){
        return viewListState;
    }
    
    private void setViewListState(PatientNotificationViewListState state){
        viewListState = state;
    }
    
    private View getSecondaryView(){
        return secondaryView;
    }
    
    private void setSecondaryView(View value){
        secondaryView = value;
    }

    private Descriptor getOldEntityDescriptor(){
        return oldEntityDescriptor;
    
    }
    
    private void setOldEntityDescriptor(Descriptor value){
        oldEntityDescriptor = value;
    }
    
    private void setMyController(ActionListener controller){
        myController = controller;
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
            setEntityDescriptorFromView(the_view.getViewDescriptor());
            switch (the_view.getMyViewType()){
                case PATIENT_NOTIFICATION_VIEW:
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
        ViewController.PatientNotificationViewControllerActionEvent actionCommand =
               ViewController.PatientNotificationViewControllerActionEvent.valueOf(e.getActionCommand());
        try{
            switch (actionCommand){
                case VIEW_ACTIVATED_NOTIFICATION:
                    actionEvent = new ActionEvent(
                            this,ActionEvent.ACTION_PERFORMED,
                            ViewController.DesktopViewControllerActionEvent.
                                    VIEW_CONTROLLER_ACTIVATED_NOTIFICATION.toString());
                    this.myController.actionPerformed(actionEvent);
                    break;
                case VIEW_CHANGED_NOTIFICATION:
                    actionEvent = new ActionEvent(
                           this,ActionEvent.ACTION_PERFORMED,
                           ViewController.DesktopViewControllerActionEvent.
                                   VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                    this.myController.actionPerformed(actionEvent);
                    break;
                case VIEW_CLOSED_NOTIFICATION:
                    actionEvent = new ActionEvent(
                           this,ActionEvent.ACTION_PERFORMED,
                           ViewController.DesktopViewControllerActionEvent.
                                   VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    this.myController.actionPerformed(actionEvent);
                    break;
                case UNACTIONED_PATIENT_NOTIFICATIONS_REQUEST:
                    doUnactionedPatientNotificationsRequest();
                    setViewListState(PatientNotificationViewListState.UNACTIONED_NOTIFICATION_STATE);
                    break;
                case PATIENT_NOTIFICATIONS_REQUEST:
                    doPatientNotificationsRequest();
                    setViewListState(PatientNotificationViewListState.ALL_NOTIFICATION_STATE);
                    break;
                case ACTION_PATIENT_NOTIFICATION_REQUEST:
                    doActionPatientNotificationRequest();                   
                    break;
                case CREATE_PATIENT_NOTIFICATION_REQUEST:
                    doCreatePatientNotificationRequest();
                    break;
                case DELETE_PATIENT_NOTIFICATION_REQUEST:
                        doDeletePatientNotificationRequest();
                        break;
                case UPDATE_PATIENT_NOTIFICATION_REQUEST:
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
        setNewEntityDescriptor(new Descriptor());
        //PatientNotification patientNotification = new Notification();
        //07/08/2022
        //patientNotification.getCollection().setScope(Notification.Scope.UNACTIONED);
        //patientNotification.setScope(Scope.UNACTIONED);
        //getPatientNotificationsFor(patientNotification);
        sendPrimaryViewPatientNotifications(Scope.UNACTIONED);
    }
    
    private void doPatientNotificationsRequest()throws StoreException{
        setNewEntityDescriptor(new Descriptor());
        //PatientNotification patientNotification = new Notification();
        //patientNotification.setScope(Notification.Scope.ALL);
        //getPatientNotificationsFor(patientNotification);
        sendPrimaryViewPatientNotifications(Notification.Scope.ALL);
    }
    
    private void getPatientNotificationsFor(Notification notification){
        try{
            notification.read();
            getControllerDescriptor().getControllerDescription().setPatientNotifications(
                    notification.get());
            pcSupportForView.addPropertyChangeListener(this.view);
            pcEvent = new PropertyChangeEvent(this,
               ViewController.PatientNotificationViewControllerPropertyChangeEvent.RECEIVED_PATIENT_NOTIFICATIONS.toString(),
               getOldEntityDescriptor(),getControllerDescriptor());
            pcSupportForView.firePropertyChange(pcEvent);
            pcSupportForView.removePropertyChangeListener(this.view);
        }catch (StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Patient notification controller error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doActionPatientNotificationRequest(){
        ArrayList<Notification> notifications = 
                getDescriptorFromView().getViewDescription().getPatientNotifications();
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
        setOldEntityDescriptor(getControllerDescriptor());
        setNewEntityDescriptor(new Descriptor());
        getControllerDescriptor().getControllerDescription().setPatientNotifications(new ArrayList<>());

        try{
            Patient patient = new Patient();
            patient.setScope(Scope.ALL);
            patient.read();
            getControllerDescriptor().getControllerDescription().setPatients(patient.get());
            //07/08/2022
            //Patient.Collection  patientCollection = patient.getCollection();
            //patientCollection.read();
            //getNewEntityDescriptor().setThePatients(patientCollection.get());
            View.setViewer(View.Viewer.PATIENT_NOTIFICATION_EDITOR_VIEW);
            secondaryView = View.factory(this, getControllerDescriptor(), desktopView);
            //note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
            this.myController.actionPerformed(actionEvent);
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"PatientNotificaionViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doUpdatePatientNotificationRequest(){
        Notification notification = getDescriptorFromView().getViewDescription().getPatientNotification();
        setOldEntityDescriptor(getControllerDescriptor());
        setNewEntityDescriptor(new Descriptor());
        getControllerDescriptor().getControllerDescription().setPatientNotification(notification);
        try{
            /**
             * send view collection of all patients on system
             * -- in case user wants to select another patient to update(?)
             */
            Patient patient = new Patient();
            //07/08/2022
            patient.setScope(Scope.ALL);
            patient.read();
            getControllerDescriptor().getControllerDescription().setPatients(patient.get());
            /**
             * send view collection of previous notifications for this patient
             */
            Notification patientNotification = new Notification();
            patientNotification.setPatient(notification.getPatient());
            patientNotification.setScope(Scope.FOR_PATIENT);
            patientNotification.read();
            getControllerDescriptor().getControllerDescription().setPatientNotifications(patientNotification.get());
            //PatientNotification.Collection patientNotificationCollection = patientNotification.getCollection();
            //patientNotificationCollection.setScope(Scope.FOR_PATIENT);
            //patientNotificationCollection.read();
            //getNewEntityDescriptor().setPatientNotifications(patientNotificationCollection.get());
            View.setViewer(View.Viewer.PATIENT_NOTIFICATION_EDITOR_VIEW);
            secondaryView = View.factory(this, getControllerDescriptor(), desktopView);
            //note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED.toString());
            this.myController.actionPerformed(actionEvent);
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"PatientNotificaionViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doSecondaryViewActionRequest(ActionEvent e)throws StoreException{
        View the_view = (View)e.getSource();
        setSecondaryView(the_view);
        switch (the_view.getMyViewType()){
            case PATIENT_NOTIFICATION_EDITOR_VIEW:
                ViewController.PatientNotificationViewControllerActionEvent actionCommand =
               ViewController.PatientNotificationViewControllerActionEvent.valueOf(e.getActionCommand());
                switch (actionCommand){
                    case MODAL_VIEWER_ACTIVATED:
                        getSecondaryView().initialiseView();
                        break;
                    case PATIENT_NOTIFICATION_EDITOR_CREATE_NOTIFICATION_REQUEST:
                        doPatientNotificationEditorCreateNotificationRequest();
                        break;
                    case PATIENT_NOTIFICATION_EDITOR_UPDATE_NOTIFICATION_REQUEST:
                        doPatientNotificationEditorUpdateNotificationRequest();
                        break;
                    case PATIENT_NOTIFICATION_EDITOR_CLOSE_VIEW_REQUEST:
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
                getDescriptorFromView().getViewDescription().getPatientNotification();
        if (patientNotification!=null){
            patientNotification.insert();
            closeSecondaryView();
            if (getViewListState().equals(PatientNotificationViewListState.ALL_NOTIFICATION_STATE))
                sendPrimaryViewPatientNotifications(Notification.Scope.ALL);
            else sendPrimaryViewPatientNotifications(Notification.Scope.UNACTIONED);
            
        }
    }
    
    private void doDeletePatientNotificationRequest()throws StoreException{
        Notification patientNotification = 
                getDescriptorFromView().getViewDescription().getPatientNotification();
        if (patientNotification!=null){
            patientNotification.setScope(Scope.SINGLE);
            patientNotification.delete();
            //closeSecondaryView();
            if (getViewListState().equals(PatientNotificationViewListState.ALL_NOTIFICATION_STATE))
                sendPrimaryViewPatientNotifications(Notification.Scope.ALL);
            else sendPrimaryViewPatientNotifications(Notification.Scope.UNACTIONED);
            
        }
    }
    
    private void doPatientNotificationEditorUpdateNotificationRequest()throws StoreException{
        Notification patientNotification = 
                getDescriptorFromView().getViewDescription().getPatientNotification();
        if (patientNotification!=null){
            patientNotification.update();
            closeSecondaryView();
            //sendPrimaryViewPatientNotifications(Notification.Scope.UNACTIONED);
        }
            
    }
    
    private void sendPrimaryViewPatientNotifications(Notification.Scope scope)throws StoreException{
        Notification patientNotification = new Notification();
        //07/08/2022
        patientNotification.setScope(scope);
        //PatientNotification.Collection patientNotificationCollection = maPatientNotification.getCollection();
        //patientNotificationCollection.setScope(scope);
        patientNotification.read();
        setNewEntityDescriptor(new Descriptor());
        getControllerDescriptor().getControllerDescription().setPatientNotifications(patientNotification.get());
        pcSupportForView.addPropertyChangeListener(this.view);
        if (scope.equals(Notification.Scope.UNACTIONED)){
            pcEvent = new PropertyChangeEvent(this,
                    ViewController.PatientNotificationViewControllerPropertyChangeEvent.RECEIVED_UNACTIONED_NOTIFICATIONS.toString(),
                    getOldEntityDescriptor(),getControllerDescriptor());
        }
        else{
            pcEvent = new PropertyChangeEvent(this,
                    ViewController.PatientNotificationViewControllerPropertyChangeEvent.RECEIVED_PATIENT_NOTIFICATIONS.toString(),
                    getOldEntityDescriptor(),getControllerDescriptor());
        }
        
        pcSupportForView.firePropertyChange(pcEvent);
        pcSupportForView.removePropertyChangeListener(this.view);  
    }
    
    private void closeSecondaryView(){
        try{
            getSecondaryView().setClosed(true);
        }catch (PropertyVetoException ex){

        }
    } 
    
    private void doPatientNotificationEditorCloseViewRequest(){
        
    }
    
    private DesktopView getDeskTopView(){
        return desktopView;
    }
    
    private void setDesktopView(DesktopView value){
        desktopView = value;
    }
    
    public PatientNotificationViewController(DesktopViewController controller, 
                                                DesktopView desktopView)
                                                throws StoreException{
        setMyController(controller);
        setDesktopView(desktopView);
        pcSupportForView = new PropertyChangeSupport(this);
        setNewEntityDescriptor(new Descriptor());
        this.oldEntityDescriptor = new Descriptor();
        /**
         * -- construct a PatientNotification object
         * -- initialise its Collection with the stored unactioned notifications on the system
         * -- store the patient notification object in an EntityDescriptor object
         */
        View.setViewer(View.Viewer.PATIENT_NOTIFICATION_VIEW);
        this.view = View.factory(this, getControllerDescriptor(), desktopView);
        this.view.addInternalFrameListeners();
    }
    
    public View getView(){
        return view;
    }
    
}
