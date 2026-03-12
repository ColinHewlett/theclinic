/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

/**
 *
 * @author colin
 */
public class ColorSetting extends Entity{
    
    public enum ColorSettingItem{
        DIARY_BOOKING_REMAINING_ROWS_COLOR,
        DIARY_BOOKABLE_SLOT_COLOR,
        LIST_EMERGENCY_BOOKING_TEXT_COLOR,
        LIST_BOOKABLE_SLOT_TEXT_COLOR_REQUEST,
        LIST_UNBOOKABLE_SLOT_TEXT_COLOR_REQUEST,
        TITLED_BORDER_COLOR
    }
}
