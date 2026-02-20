/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.controller.Descriptor.ControllerDescription;
import colinhewlettsolutions.client.controller.Descriptor.ViewDescription;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.model.entity.User;
import colinhewlettsolutions.client.model.entity.UserSettings;
import colinhewlettsolutions.client.model.non_entity.Credential;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author colin
 */
public class UserSystemWideSettingsViewController extends ViewController {
    
    public enum Actions{
        USER_SCHEDULE_LIST_FACTORY_SETTINGS_REQUEST,
        USER_SCHEDULE_LIST_SETTINGS_UPDATE_REQUEST,
        USER_SYSTEM_WIDE_FACTORY_SETTINGS_REQUEST,
        USER_SYSTEM_WIDE_SETTINGS_REQUEST,
        USER_SYSTEM_WIDE_SETTINGS_UPDATE_REQUEST,
        VIEW_ACTIVATED_NOTIFICATION,
        VIEW_CHANGED_NOTIFICATION,
        VIEW_CLOSED_NOTIFICATION
    }
    
    public enum Properties{
        LIST_BOOKABLE_SLOT_BACKGROUND_CHANGE_RECEIVED,
        LIST_BOOKABLE_SLOT_FOREGROUND_CHANGE_RECEIVED,
        LIST_EMERGENCY_BOOKING_BACKGROUND_CHANGE_RECEIVED,
        LIST_EMERGENCY_BOOKING_FOREGROUND_CHANGE_RECEIVED,
        LIST_UNBOOKABLE_SLOT_BACKGROUND_CHANGE_RECEIVED,
        LIST_UNBOOKABLE_SLOT_FOREGROUND_CHANGE_RECEIVED,
        TITLED_BORDER_COLOR_CHANGE_RECEIVED,
        TITLED_BORDER_FONT_CHANGE_RECEIVED
    }
    
    public UserSystemWideSettingsViewController(DesktopViewController controller, 
                                                DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        ControllerDescription controllerDescription = getDescriptor().getControllerDescription();
        ControllerDescription desktopViewControllerDescription = getMyController().getDescriptor().getControllerDescription();
        Credential credential = (Credential)controllerDescription.getProperty(SystemDefinition.Properties.LOGIN_CREDENTIAL);
        UserSettings userSettings = new UserSettings(new User(credential.getUsername()));
        
        ViewDescription viewDescription = getDescriptor().getViewDescription();
        Actions actionCommand = Actions.valueOf(e.getActionCommand());
        try{
            switch(actionCommand){
                case USER_SCHEDULE_LIST_FACTORY_SETTINGS_REQUEST ->{
                    /*
                    for(SystemDefinition.UserScheduleListSettings settings : SystemDefinition.UserScheduleListSettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        controllerDescription.setProperty(property,desktopViewControllerDescription.getProperty(property));
                    }
                    userSettings.setScope(Entity.Scope.USER_SCHEDULE_LIST_SETTINGS);*/
                    break;
                }
                case USER_SCHEDULE_LIST_SETTINGS_UPDATE_REQUEST ->{
                    /*
                    for(SystemDefinition.UserScheduleListSettings settings : SystemDefinition.UserScheduleListSettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        controllerDescription.setProperty(property,viewDescription.getProperty(property));
                    }
                    userSettings.setScope(Entity.Scope.USER_SCHEDULE_LIST_SETTINGS);*/
                    break;
                }
                case USER_SYSTEM_WIDE_FACTORY_SETTINGS_REQUEST ->{
                    for(SystemDefinition.UserSystemWideSettings settings : SystemDefinition.UserSystemWideSettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        viewDescription.setProperty(property,desktopViewControllerDescription.getProperty(property));
                    }
                    userSettings.setScope(Entity.Scope.USER_SYSTEM_WIDE_SETTINGS);
                    doUpdateSettingsRequest(userSettings, getDescriptor());
                    break;
                }
                case USER_SYSTEM_WIDE_SETTINGS_UPDATE_REQUEST ->{
                    /*
                    for(SystemDefinition.UserSystemWideSettings settings : SystemDefinition.UserSystemWideSettings.values()){
                        SystemDefinition.Properties property = SystemDefinition.Properties.valueOf(settings.toString());
                        controllerDescription.setProperty(property,viewDescription.getProperty(property));
                    }*/
                    userSettings.setScope(Entity.Scope.USER_SYSTEM_WIDE_SETTINGS);
                    doUpdateSettingsRequest(userSettings, getDescriptor());
                    break;
                }
                case VIEW_CLOSED_NOTIFICATION ->{
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        DesktopViewController.Actions.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    break;
                }
                case VIEW_CHANGED_NOTIFICATION ->{

                    break;
                }
                case VIEW_ACTIVATED_NOTIFICATION ->{
                    break;
                }
                default ->{
                    String message = "Invalid case value encountered in UserSettingsViewController( case = " + actionCommand.toString() + " ) method";
                    throw new Exception(message);
                }
            }
            /*
            if (user!=null){
                if(!user.getUsername().equals("guest")){
                    userSettings.setUser(user);
                    userSettings.getSettingsFrom(getDescriptor().getControllerDescription());
                    try{
                        userSettings.update();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n";
                        message = message + "StoreException handled in UserSettingsViewController( case = " + actionCommand.toString() + " ) method";
                        displayErrorMessage(message, "View controller error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }*/
        }catch(Exception ex){
            displayErrorMessage(ex.getMessage(), "View controller error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    private void doUpdateSettingsRequest(UserSettings userSettings, Descriptor descriptor)throws StoreException{
        HashMap<SystemDefinition.Properties,Object> settings = userSettings.getSettingsFrom(descriptor.getViewDescription());
        userSettings.setSettings(settings);
        userSettings.update();
        userSettings = userSettings.read();
        for(Map.Entry<SystemDefinition.Properties,Object> entry : userSettings.getSettings().entrySet()){
            descriptor.getControllerDescription().
                    setProperty((SystemDefinition.Properties)entry.getKey(), entry.getValue());
        }
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            DesktopViewController.Actions.
                    USER_SYSTEM_WIDE_SETTINGS_VIEW_CONTROLLER_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
        /*
        firePropertyChangeEvent(
                    ViewController.DesktopViewControllerPropertyChangeEvent.
                            USER_SYSTEM_WIDE_SETTINGS_RECEIVED.toString(),
                    getView(),
                    this,
                    null,
                    null
        );*/
    }
    
}
