/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ViewController.displayErrorMessage;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import model.entity.Entity;
import model.entity.ToDo;
import model.entity.Patient;
import repository.StoreException;
import view.View;
import static view.View.Viewer.NOTIFICATION_EDITOR_VIEW;
import static view.View.Viewer.NOTIFICATION_VIEW;
import view.views.modal_views.ModalView;
import view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class ToDoViewController extends ViewController{
    
    private ToDoViewListState viewListState = null;

    enum ToDoViewListState {ALL_TO_DO_STATE, UNACTIONED_TO_DO_STATE};
    
    private ToDoViewListState getViewListState(){
        return viewListState;
    }
    
    private void setViewListState(ToDoViewListState state){
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
                case TO_DO_VIEW:
                    /**
                     * 13/02/24 bug fixing move
                     */
                    setView(the_view);
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
                        case UNACTIONED_TO_DO_STATE:
                            doUnactionedToDosRequest();
                            break;
                        case ALL_TO_DO_STATE:
                            doToDosRequest();
                            break;
                    }
            }
        }catch(StoreException ex){
            String error = ex.getMessage() +"\n"
                    + "Raised in ToDoViewController."
                    + "doDesktopViewControllerActionRequest";
            displayErrorMessage(error,"Patient notification view controller",
                    JOptionPane.WARNING_MESSAGE);          
        }

    }
    
    private void doPrimaryViewActionRequest(ActionEvent e){
        ActionEvent actionEvent = null;
        ViewController.ToDoViewControllerActionEvent actionCommand =
               ViewController.ToDoViewControllerActionEvent.valueOf(e.getActionCommand());
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
                case UNACTIONED_TO_DO_REQUEST:
                    doUnactionedToDosRequest();
                    setViewListState(ToDoViewListState.UNACTIONED_TO_DO_STATE);
                    break;
                case TO_DOs_REQUEST:
                    doToDosRequest();
                    setViewListState(ToDoViewListState.ALL_TO_DO_STATE);
                    break;
                case ACTION_TO_DO_REQUEST:
                    doActionToDoRequest();
                    sendPrimaryViewToDos(Entity.Scope.UNACTIONED);
                    break;
                case CREATE_TO_DO_REQUEST:
                    doCreateToDoRequest();
                    break;
                case CANCEL_TO_DO_REQUEST:
                        doCancelNotificationRequest();
                        break;
                case UPDATE_TO_DO_REQUEST:
                    doUpdateToDoRequest();
                    break;
            }
        }catch(StoreException ex){
            displayErrorMessage(ex.getMessage(),
                    "Patient notification view controller error", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doUnactionedToDosRequest()throws StoreException{
        setDescriptor(new Descriptor());
        sendPrimaryViewToDos(Entity.Scope.UNACTIONED);
    }
    
    private void doToDosRequest()throws StoreException{
        setDescriptor(new Descriptor());
        sendPrimaryViewToDos(ToDo.Scope.ALL);
    }
    
    private void doNotificationsForPatientRequest()throws StoreException{
        /*
        Patient patient = getDescriptor().getViewDescription().getPatient();
        Notification notification = new Notification();
        notification.setPatient(patient);
        notification.setScope(Entity.Scope.FOR_PATIENT);
        notification.read();
        getDescriptor().getControllerDescription()
                .setToDos(notification.get());
        firePropertyChangeEvent(
                NotificationViewControllerPropertyChangeEvent
                        .RECEIVED_PATIENT_TO_DOS.toString(),
                getSecondaryView(),
                this,
                null,
                null
        );
        */
    }
    
    /*
    private void getToDosFor(Notification notification){
        try{
            notification.read();
            getDescriptor().getControllerDescription().setToDos(
                    notification.get());
            
            pcSupportForView.addPropertyChangeListener(this.getView());
            pcEvent = new PropertyChangeEvent(this,
               ViewController.NotificationViewControllerPropertyChangeEvent.RECEIVED_PATIENT_TO_DOS.toString(),
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
    private void doActionToDoRequest(){
        /**
         * 25/08/2024 change
         * -- previous version of patient notification logic allowed more than one notification to be action at the same time
         * -- hence the update uses a singly selected notification object in the Descriptor and not a collection
         */
        ToDo toDo = getDescriptor().getViewDescription().getToDo();
        try{
            toDo.update();
            doUnactionedToDosRequest();
        }catch (StoreException ex){
            String message = ex.getMessage() +  "\n"
                    + "Hasndle in NotificationViewController::doActionToDoRequest()";
            displayErrorMessage(message, "View contgroller error", JOptionPane.WARNING_MESSAGE);
        }
        /*
        ArrayList<Notification> notifications = 
                getDescriptor().getViewDescription().getToDos();
        try{
            for (Notification patientNotification : notifications){
                patientNotification.action();
                doUnactionedToDosRequest();
            }
        }catch (StoreException ex){
            displayErrorMessage(
                    ex.getMessage(),
                    "Patient notification controller error", 
                    JOptionPane.WARNING_MESSAGE);
        } 
        */
    }
    
    /**
     * method launches the patient notification editor view
 -- and initialises the accompanying Descriptor::patientNotifications as an empty ArrayList
 -- this on basis that the patient notification editor view will then know view is used for creation of a new notification
 -- 
     */
    private void doCreateToDoRequest(){
        //setOldEntityDescriptor(getDescriptor());
        setDescriptor(new Descriptor());
        getDescriptor().getControllerDescription().setToDos(new ArrayList<>());
        getDescriptor().getControllerDescription().setViewMode(ViewController.ViewMode.CREATE);

        try{
            Patient patient = new Patient();
            patient.setScope(Entity.Scope.ALL);
            patient.read();
            getDescriptor().getControllerDescription().setPatients(patient.get());

            View.setViewer(View.Viewer.TO_DO_EDITOR_VIEW);
            setView((ModalView)new View().make(View.Viewer.TO_DO_EDITOR_VIEW,
                    this, 
                    getDesktopView()).getModalView());
            //note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED_NOTIFICATION.toString());
            this.getMyController().actionPerformed(actionEvent);
        }catch (StoreException ex){
            String message = ex.getMessage();
            displayErrorMessage(message,"PatientNotificaionViewController error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doUpdateToDoRequest(){
        ToDo toDo = getDescriptor().getViewDescription().getToDo();
        //setOldEntityDescriptor(getDescriptor());
        //setDescriptor(new Descriptor());
        getDescriptor().getControllerDescription().setToDo(toDo);
        try{
            /**
             * send view collection of all patients on system
             * -- in case user wants to select another patient to update(?)
             */
            Patient patient = new Patient();
            //07/08/2022
            patient.setScope(Entity.Scope.ALL);
            patient.read();
            getDescriptor().getControllerDescription().setPatients(patient.get());
            /**
             * send view collection of previous notifications for this patient
             */
            /*
            Notification patientNotification = new Notification();
            patientNotification.setPatient(notification.getPatient());
            patientNotification.setScope(Entity.Scope.FOR_PATIENT);
            patientNotification.read();
            getDescriptor().getControllerDescription()
                    .setToDos(patientNotification.get());*/
            getDescriptor().getControllerDescription().
                    setViewMode(ViewController.ViewMode.UPDATE);
            setModalView((ModalView)new View().make(View.Viewer.TO_DO_EDITOR_VIEW,
                    this,
                    getDesktopView()).getModalView());
            //note: View.factory when opening a modal JInternalFrame does not return until the JInternalFrame has been closed
            ActionEvent actionEvent = new ActionEvent(
                   this,ActionEvent.ACTION_PERFORMED,
                   DesktopViewController.DesktopViewControllerActionEvent.MODAL_VIEWER_CLOSED_NOTIFICATION.toString());
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
            case TO_DO_EDITOR_VIEW:
                ViewController.ToDoViewControllerActionEvent actionCommand =
               ViewController.ToDoViewControllerActionEvent.valueOf(e.getActionCommand());
                switch (actionCommand){
                    case MODAL_VIEWER_ACTIVATED:
                        //getSecondaryView().initialiseView();
                        break;
                        /*
                    case TO_DOS_FOR_PATIENT_REQUEST:
                        doNotificationsForPatientRequest();
                        break;*/
                    case TO_DO_EDITOR_CREATE_TO_DO_REQUEST:
                        doToDoEditorCreateNotificationRequest();
                        break;
                    case TO_DO_EDITOR_UPDATE_TO_DO_REQUEST:
                        doNotificationEditorUpdateNotificationRequest();
                        break;
                    case TO_DO_EDITOR_CLOSE_VIEW_REQUEST:
                        doToDoEditorCloseViewRequest();
                        break;
                    case MODAL_VIEWER_DEACTIVATED:
                        closeSecondaryView();
                        break;
                }
                break;
                
            default:
                JOptionPane.showMessageDialog(getView(), 
                        "Unrecognised view type specified in ToDoViewController::doSecondaryViewActionRequest()",
                        "Patient Notification View Controller Error", 
                        JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doToDoEditorCreateNotificationRequest()throws StoreException{
        ToDo toDo = 
                getDescriptor().getViewDescription().getToDo();
        if (toDo!=null){
            toDo.insert();
            closeSecondaryView();
            if (getViewListState().equals(ToDoViewListState.ALL_TO_DO_STATE))
                sendPrimaryViewToDos(ToDo.Scope.ALL);
            else sendPrimaryViewToDos(ToDo.Scope.UNACTIONED);
            
        }
    }
    
    private void doCancelNotificationRequest()throws StoreException{
        ToDo toDo = 
                getDescriptor().getViewDescription().getToDo();
        if (toDo!=null){
            toDo.cancel();
        if (getViewListState().equals(ToDoViewListState.ALL_TO_DO_STATE))
                sendPrimaryViewToDos(ToDo.Scope.ALL);
            else sendPrimaryViewToDos(ToDo.Scope.UNACTIONED);

        }
    }
    
    private void doNotificationEditorUpdateNotificationRequest()throws StoreException{
        ToDo toDo = 
                getDescriptor().getViewDescription().getToDo();
        if (toDo!=null){
            toDo.update();
            closeSecondaryView();
        }
            
    }
    
    private void sendPrimaryViewToDos(ToDo.Scope scope)throws StoreException{
        /*
        Notification patientNotification = new Notification();
        patientNotification.setScope(scope);
        patientNotification.read();
        getDescriptor().getControllerDescription().setToDos(patientNotification.get());
        String pcEventName;
        if(scope.equals(Notification.Scope.UNACTIONED)) pcEventName = 
                NotificationViewControllerPropertyChangeEvent.
                        RECEIVED_UNACTIONED_TO_DOS.toString();
        else pcEventName = NotificationViewControllerPropertyChangeEvent.
                RECEIVED_PATIENT_TO_DOS.toString();       
        firePropertyChangeEvent(
                pcEventName,
                getView(),
                this,
                null,
                null
        ); */
    }
    
    private void closeSecondaryView(){
        try{
            getModalView().setClosed(true);
        }catch (PropertyVetoException ex){

        }
        
        int test = 0;
    } 
    
    private void doToDoEditorCloseViewRequest(){
        
    }
    
    public ToDoViewController(DesktopViewController controller, 
                                                DesktopView desktopView)
                                                throws StoreException{
        setMyController(controller);
        setDesktopView(desktopView);
    }

 
}
