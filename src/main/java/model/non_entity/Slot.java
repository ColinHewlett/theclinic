/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import model.entity.Appointment;
import java.time.Duration;
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
    
    public Boolean getIsFirstSlotOfAppointment(){
        Boolean result = null;
        if (getAppointment().getPatient()!=null) result = (getStart().equals(getAppointment().getStart()));
        else result = null;
        return result;
    }

    public Boolean getIsLastSlotOfAppointment(){
        Boolean result = false;
        int duration = 0;
        LocalDateTime start = null;
        LocalDateTime end = null;
        if(getAppointment().getPatient()!=null){
            duration = (int)getAppointment().getDuration().toMinutes();
            start = getAppointment().getStart();
            end = start.plusMinutes(duration-5);
            result = getStart().equals(end);
        } else result = null;  
        return result;
    }
    
    public Boolean getIsSingleSlotAppointment(){
        Boolean result = null;
        if (getAppointment()!=null){
            result = getAppointment().getDuration().toMinutes() == 5;
        }
        return result;
    }
    
    public Boolean getIsBookable(){
        Boolean result = null;
        if (getAppointment().getPatient()==null) result = true;
        else result = false;
        return result;
    }

    public Boolean getIsBooked(){
        Boolean result = null;
        if (getAppointment().getPatient()!=null) result = true;
        else result = false;
        return result;
    }
}
