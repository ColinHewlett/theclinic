/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import model.entity.Appointment;
import java.time.LocalDateTime;

/**
 *
 * @author colin
 */
public class Slot {
    private Appointment appointment = null;
    public Appointment getAppointment(){
        return appointment;
    }
    public void setApppointment(Appointment value){
        appointment = value;
    }
    
    private LocalDateTime localDateTime = null;
    public LocalDateTime getStart(){
        return localDateTime;
    }
    public void setApppointment(LocalDateTime value){
        localDateTime = value;
    }
    
    private Boolean isFirst = null;
    public Boolean getIsFirst(){
        return isFirst;
    }
    public void setIsFirst(Boolean value){
        isFirst = value;
    }
    
    private Boolean isIsAvailable = null;
    public Boolean getIsIsAvailable(){
        return isIsAvailable;
    }
    public void setIsAvailable(Boolean value){
        isIsAvailable = value;
    }
    
    private Boolean isIsUnbookable = null;
    public Boolean getIsIsUnbookable(){
        return isIsUnbookable;
    }
    public void setIsUnbookable(Boolean value){
        isIsUnbookable = value;
    }
}
