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
    
    private Boolean isAvailable = null;
    public Boolean getIsAvailable(){
        return isAvailable;
    }
    public void setIsAvailable(Boolean value){
        isAvailable = value;
    }

    private Boolean isBooked = null;
    public Boolean getIsBooked(){
        return isBooked;
    }
    public void setIsBooked(Boolean value){
        isBooked = value;
    }
}
