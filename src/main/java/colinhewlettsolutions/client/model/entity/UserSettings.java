/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.entity;

import colinhewlettsolutions.client.controller.Descriptor.ControllerDescription;
import colinhewlettsolutions.client.controller.Descriptor.ViewDescription;
import colinhewlettsolutions.client.controller.SystemDefinition;
import static colinhewlettsolutions.client.model.entity.Entity.Scope.USER_SCHEDULE_DIARY_SETTINGS;
import colinhewlettsolutions.client.model.entity.interfaces.IEntityRepositoryActions;
import colinhewlettsolutions.client.model.repository.StoreException;
import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author colin
 */
public class UserSettings extends Entity implements IEntityRepositoryActions{
    
    public UserSettings(){
        setIsUserSettings(true);
    }
    
    public UserSettings(User user){
        setIsUserSettings(true);
        this.user = user;
    } 
    
    public UserSettings(User user, HashMap<SystemDefinition.Properties,Object> settings){
        this.user = user;
        this.settings = settings;
        setIsUserSettings(true);
    }
    
    private User user = null;
    public User getUser(){
        return user;
    }
    public void setUser(User value){
        user = value;
    }
    
    private Integer red = null;
    public Integer getRed(){
        return red;
    }
    public void setRed(Integer value){
        red = value;
    }
    
    private Integer green = null;
    public Integer getGreen(){
        return green;
    }
    public void setGreen(Integer value){
        green = value;
    }
    
    private Integer blue = null;
    public Integer getBlue(){
        return blue;
    }
    public void setBlue(Integer value){
        blue = value;
    }
    
    private Integer size = null;
    public Integer getSize(){
        return size;
    }
    public void setSize(Integer value){
        size = value;
    }
    
    private String font = null;
    public String getFont(){
        return font;
    }
    public void setFont(String value){
        font = value;
    }
    
    private HashMap<SystemDefinition.Properties,Object> settings = null;
    public HashMap<SystemDefinition.Properties,Object> getSettings(){
        return settings;
    }
    public void setSettings(HashMap<SystemDefinition.Properties,Object> value){
        settings = value;
    }
    
    /*
    private HashMap<SystemDefinition.Properties, Object> settingsFor = null;
    public HashMap<SystemDefinition.Properties, Object> getSettingsFor(Descriptor descriptor, SystemDefinition.Properties property){
        HashMap<SystemDefinition.Properties,Object> userSettingsMap = new HashMap<>();
        switch(property){
            case USER_SCHEDULE_DIARY_SETTINGS->{
                for(SystemDefinition.UserScheduleDiarySettings setting : SystemDefinition.UserScheduleDiarySettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), descriptor.getControllerDescription().
                            getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
            case USER_SCHEDULE_LIST_SETTINGS ->{
                for(SystemDefinition.UserScheduleListSettings setting : SystemDefinition.UserScheduleListSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), descriptor.getControllerDescription().
                            getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS ->{
                for(SystemDefinition.UserSystemWideSettings setting : SystemDefinition.UserSystemWideSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), descriptor.getControllerDescription().
                            getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
            }
        }
        return userSettingsMap;
    }*/
    
    public HashMap<SystemDefinition.Properties, Object> getSettingsFrom(ControllerDescription controllerDescription){
        HashMap<SystemDefinition.Properties,Object> userSettingsMap = new HashMap<>();
        switch(getScope()){
            case USER_SCHEDULE_DIARY_SETTINGS ->{
                for(SystemDefinition.UserScheduleDiarySettings setting : SystemDefinition.UserScheduleDiarySettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), 
                            controllerDescription.getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
            case USER_SCHEDULE_LIST_SETTINGS ->{
                Color color = null;
                SystemDefinition.Properties property = null; 
                for(SystemDefinition.UserScheduleListSettings setting : SystemDefinition.UserScheduleListSettings.values() ){
                    property = SystemDefinition.Properties.valueOf(setting.toString());
                    color = (Color)controllerDescription.getProperty(property);
                    userSettingsMap.put(property, color);
                }
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS ->{
                for(SystemDefinition.UserSystemWideSettings setting : SystemDefinition.UserSystemWideSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), 
                            controllerDescription.getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
        }
        return userSettingsMap;
    }
    
    public HashMap<SystemDefinition.Properties, Object> getSettingsFrom(ViewDescription viewDescription){
        HashMap<SystemDefinition.Properties,Object> userSettingsMap = new HashMap<>();
        switch(getScope()){
            case USER_SCHEDULE_DIARY_SETTINGS ->{
                for(SystemDefinition.UserScheduleDiarySettings setting : SystemDefinition.UserScheduleDiarySettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), 
                            viewDescription.getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
            case USER_SCHEDULE_LIST_SETTINGS ->{
               /* Color color0 = null;
                Color color1 = null;
                Color color2 = null;
                SystemDefinition.Properties property = null; 
                Color color_LIST_BOOKABLE_SLOT_BACKGROUND = (Color)viewDescription.getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND);
                Color color_LIST_BOOKABLE_SLOT_FOREGROUND = (Color)viewDescription.getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND);
                Color color_LIST_EMERGENCY_BOOKING_BACKGROUND = (Color)viewDescription.getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND);
                Color color_LIST_EMERGENCY_BOOKING_FOREGROUND = (Color)viewDescription.getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND);
                */
                for(SystemDefinition.UserScheduleListSettings setting : SystemDefinition.UserScheduleListSettings.values() ){
                    /*
                    property = SystemDefinition.Properties.valueOf(setting.toString());
                    color1 = (Color)viewDescription.getProperty(property);
                    userSettingsMap.put(property, color1);
                    color2 = (Color)userSettingsMap.get(property);*/
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), 
                            viewDescription.getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS ->{
                for(SystemDefinition.UserSystemWideSettings setting : SystemDefinition.UserSystemWideSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), 
                            viewDescription.getProperty(SystemDefinition.Properties.valueOf(setting.toString())));
                }
                break;
            }
        }
        return userSettingsMap;
    }
    
    public void setNullSettings(){
        HashMap<SystemDefinition.Properties,Object> userSettingsMap = new HashMap<>();
        switch(getScope()){
            case USER_SCHEDULE_DIARY_SETTINGS ->{
                for(SystemDefinition.UserScheduleDiarySettings setting : SystemDefinition.UserScheduleDiarySettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), null);
                }
                break;
            }
            case USER_SCHEDULE_LIST_SETTINGS ->{
                for(SystemDefinition.UserScheduleListSettings setting : SystemDefinition.UserScheduleListSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), null);
                }
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS ->{
                for(SystemDefinition.UserSystemWideSettings setting : SystemDefinition.UserSystemWideSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), null);
                }
            }
        }
        setSettings(userSettingsMap);
    } 
    
    @Override
    public Point count()throws StoreException{
        return getRepository().count(this);
    }
    
    @Override
    public void create()throws StoreException{
        getRepository().create(this);
    }
    
    @Override
    public void delete()throws StoreException{
        getRepository().delete(this);
    }
    
    @Override
    public void drop()throws StoreException{
        
    }
    
    @Override
    public Integer insert()throws StoreException{
        getRepository().insert(this);
        return 0;
    }
    
    @Override
    /**
     * on entry 
     * -- the user property is initialised with the user to whom the settings belong
     * -- an empty settings collection is constructed to contain the read values
     * in the current version all settings are
     */
    public UserSettings read()throws StoreException{
        HashMap<SystemDefinition.Properties,Object> userSettingsMap = new HashMap<>();
        switch(getScope()){
            case USER_SCHEDULE_DIARY_SETTINGS ->{
                for(SystemDefinition.UserScheduleDiarySettings setting : SystemDefinition.UserScheduleDiarySettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), null);
                }
                break;
            }
            case USER_SCHEDULE_LIST_SETTINGS ->{
                for(SystemDefinition.UserScheduleListSettings setting : SystemDefinition.UserScheduleListSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), null);
                }
                break;
            }
            case USER_SYSTEM_WIDE_SETTINGS ->{
                for(SystemDefinition.UserSystemWideSettings setting : SystemDefinition.UserSystemWideSettings.values() ){
                    userSettingsMap.put(SystemDefinition.Properties.valueOf(setting.toString()), null);
                }
            }
        }
        setSettings(userSettingsMap);
        return getRepository().read(this);
    }

    @Override 
    public void update()throws StoreException{
        getRepository().update(this);
    }
}
