/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import model.entity.Appointment;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 *
 * @author colin
 */
public class Slot {

    public Slot(Appointment a){
        appointment = a;
    }
    
    private ArrayList<Slot> collection = null;
    public ArrayList<Slot> get(){
        return collection;
    }
    public void set(){
        collection = new ArrayList<Slot>();
        Slot slot = new Slot(getAppointment());
        slot.setStart(getAppointment().getStart());
        int minutes = (int)getAppointment().getDuration().toMinutes();
        int intervals = minutes/5;
        collection.add(slot);
        if (intervals > 1){
           for(int interval = 1; interval < intervals; interval++){
               LocalDateTime start = slot.getStart().plusMinutes(interval*5);
               Slot theSlot = new Slot(slot.getAppointment());
               theSlot.setStart(start);
               collection.add(theSlot);
           }
        }    
    }
    
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
    public void setStart(LocalDateTime value){
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
