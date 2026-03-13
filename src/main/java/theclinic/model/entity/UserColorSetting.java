/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;


import java.awt.Color;

/**
 *
 * @author colin
 */
public class UserColorSetting extends Entity{
    private User loggedINUser = null;
    private ColorSetting colorSetting = null;
    
    public UserColorSetting(User user, ColorSetting colorSetting){
        
    }
    private Color diaryBookingFirstRowColor = null;
    public void setDiaryBookingFirstRowColor(Color value){
        diaryBookingFirstRowColor = value;
    }
    public Color getDiaryBookingFirstRowColor(){
        return diaryBookingFirstRowColor;
    }
    
    private Color diaryBookingRemainingRowsColor = null;
    public void setDiaryBookingRemainingRowsColor(Color value){
        diaryBookingRemainingRowsColor = value;
    }
    public Color getDiaryBookingRemainingRowsColor(){
        return diaryBookingRemainingRowsColor;
    }
    
    private Color diaryUnbookableSlotColor = null;
    public void setDiaryUnbookableSlotColor(Color value){
        diaryUnbookableSlotColor = value;
    }
    public Color getDiaryUnbookableSlotColor(){
        return diaryUnbookableSlotColor;
    }
    
    private Color diaryScheduleViewFactorySettings = null;
    public void setDiaryScheduleViewFactorySettings(Color value){
        diaryScheduleViewFactorySettings = value;
    }
    public Color getDiaryScheduleViewFactorySettings(){
        return diaryScheduleViewFactorySettings;
    }
    
    private Color listEmergencyBookingTextColor = null;
    public void setListEmergencyBookingTextColor(Color value){
        listEmergencyBookingTextColor = value;
    }
    public Color getListEmergencyBookingTextColor(){
        return listEmergencyBookingTextColor;
    }
    
    private Color listBookableSlotTextColor = null;
    public void setListBookableSlotTextColor(Color value){
        listBookableSlotTextColor = value;
    }
    public Color getListBookableSlotTextColor(){
        return listBookableSlotTextColor;
    }
    
    private Color listScheduleViewFactorySettings = null;
    public void setListScheduleViewFactorySettings(Color value){
        listScheduleViewFactorySettings = value;
    }
    public Color getListScheduleViewFactorySettings(){
        return listScheduleViewFactorySettings;
    }
    
    private Color titledBorderColor = null;
    public void setTitledBorderColor(Color value){
        titledBorderColor = value;
    }
    public Color getTitledBorderColor(){
        return titledBorderColor;
    }
    
    private Color titledBorderFactorySettings = null;
    public void setTitledBorderFactorySettings(Color value){
        titledBorderFactorySettings = value;
    }
    public Color getTitledBorderFactorySettings(){
        return titledBorderFactorySettings;
    }
    
}
