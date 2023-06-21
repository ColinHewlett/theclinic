/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ViewController.displayErrorMessage;
import model.Appointment;
import model.Entity;
import view.views.DesktopView;
import repository.StoreException;//01/03/2023
import view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
/**
 *
 * @author colin
 */
public class AppointmentRemindersViewController extends ViewController{
    private ActionListener myController = null;
    private DesktopView desktopView = null;
    private Descriptor oldEntityDescriptor = null;
    private View view = null;
    private final PropertyChangeSupport pcSupport = null;
    private LocalDate appointmentScheduleDay = null;

    private LocalDate getAppointmentScheduleDay(){
        return appointmentScheduleDay;
    }
    
    private void setAppointmentScheduleDay(LocalDate day){
        appointmentScheduleDay = day;
    }
  
    private void setView(View view){
        this.view  = view;
    }
    
    private ActionListener getMyController(){
        return myController;
    }
    
    private void setMyController(ActionListener controller){
        myController = controller;
    }

    private Descriptor getOldEntityDescriptor(){
        return oldEntityDescriptor;
    }
    
    private void setOldEntityDescriptor(Descriptor ed){
        oldEntityDescriptor = ed;
    }
    
    private void initialiseView(){
        
    }
    
    public void propertyChange(PropertyChangeEvent e){
        ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent propertyName = 
                ViewController.AppointeeContactDetailsForScheduleViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        //?setNewEntityDescriptor((Descriptor)e.getNewValue());
        setControllerDescriptor((Descriptor)e.getNewValue());
        //getControllerDescriptor().setViewDescription(((Descriptor)e.getNewValue()).getViewDescription());
        switch (propertyName){
            case APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_REFRESH_RECEIVED:
                if (getAppointmentScheduleDay() == null)
                    setAppointmentScheduleDay(getControllerDescriptor().getControllerDescription().getAppointmentScheduleDay());
                if (getAppointmentScheduleDay().isEqual(
                        getControllerDescriptor().getControllerDescription().getAppointmentScheduleDay())){
                    firePropertyChangeEvent(
                            propertyName.toString(),
                            getView(),
                            this,
                            null,
                            getControllerDescriptor()
                    );
                }
                break;
        }
    }
    
    public AppointmentRemindersViewController(
            ActionListener controller, DesktopView desktopView, Descriptor entityDescriptor)throws StoreException{
        Appointment appointment = null;
        setMyController(controller);
        this.desktopView = desktopView;
        //pcSupport = new PropertyChangeSupport(this);
        //setNewEntityDescriptor(new Descriptor());
        setNewEntityDescriptor(entityDescriptor);
        setOldEntityDescriptor(new Descriptor());
        View.setViewer(View.Viewer.APPOINTMENT_REMINDERS_VIEW);
        setView(View.factory(this, getControllerDescriptor(), desktopView));
        this.desktopView.getDeskTop().add(getView());
        getView().initialiseView();
        super.centreViewOnDesktop(desktopView, view);   
    }

    public View getView(){
        return view;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Appointment appointment;
        //?setEntityDescriptorFromView(getView().getViewDescriptor());
        getControllerDescriptor().setViewDescription(((Descriptor)getView().
                getViewDescriptor()).getViewDescription());
        ViewController.AppointmentRemindersViewControllerActionEvent actionCommand =
                ViewController.AppointmentRemindersViewControllerActionEvent.valueOf(e.getActionCommand());        
         switch (actionCommand){
             case VIEW_CHANGED_NOTIFICATION:
                 ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CHANGED_NOTIFICATION.toString());
                this.myController.actionPerformed(actionEvent);
                 break;
             case VIEW_CLOSE_NOTIFICATION:
                 actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                 this.myController.actionPerformed(actionEvent);
                 break;
             case APPOINTMENT_SCHEDULE_VIEW_CLOSE_NOTIFICATION:
                 try{
                     getView().setClosed(true);
                 }catch(PropertyVetoException ex ){
                     
                 }
                 
                 break;
             case APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CHANGE_NOTIFICATION: {
                 try{
                    //?appointment = getDescriptorFromView().getViewDescription().getAppointment();
                    appointment = getControllerDescriptor().getViewDescription().getAppointment();
                    appointment.update();
                    this.getControllerDescriptor().getControllerDescription().setAppointment(appointment);
                    this.getControllerDescriptor().getControllerDescription().setAppointmentScheduleDay(
                            appointment.getStart().toLocalDate()); 
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    APPOINTEE_CONTACT_DETAILS_FOR_SCHEDULE_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            this.getControllerDescriptor()       
                    );
                 }catch(StoreException ex){
                     displayErrorMessage(ex.getMessage() + "\n" 
                             + "Raised in AppointmentScheduleViewController::doScheduleDetailsViewAction()",
                             "View Controller error", JOptionPane.WARNING_MESSAGE);
                 }
                 break;
             }
         }
    }
}
