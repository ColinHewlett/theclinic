/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.non_entity;

import theclinic.model.entity.Appointment;
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
    
    private boolean isSelected = false;
    public boolean getIsSelected(){
        return isSelected;
    }
    public void setIsSelected(boolean value){
        isSelected = value;
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
        if (getAppointment().getPatient()!=null){
            if (!getAppointment().getPatient().getIsPatientMarkedUnbookable()){
                result = (getStart().equals(getAppointment().getStart()));
            }else result = false;
        }else result = false;
        return result;
    }

    public Boolean getIsLastSlotOfAppointment(){
        Boolean result = false;
        int duration = 0;
        LocalDateTime start = null;
        LocalDateTime end = null;
        if(getAppointment().getPatient()!=null){
            if (!getAppointment().getPatient().getIsPatientMarkedUnbookable()){
                duration = (int)getAppointment().getDuration().toMinutes();
                start = getAppointment().getStart();
                end = start.plusMinutes(duration-5);
                result = getStart().equals(end);
            }else result = false; //slot is unbookable
        } else result = false; //slot is unbooked 
        return result;
    }
    
    public Boolean getIsSingleSlotAppointment(){
        Boolean result = null;
        if (getAppointment()!=null){
            result = getAppointment().getDuration().toMinutes() == 5;
        }else result = false;
        return result;
    }
    
    public boolean getIsBookable(){
        boolean result = false;
        result = getAppointment().getPatient()==null;
        return result;
    }

    public Boolean getIsBooked(){
        Boolean result = false;
        if (getAppointment().getPatient()!=null) 
            result = !getAppointment().getPatient().getIsPatientMarkedUnbookable();
        else result = false;
        return result;
    }
    
    public Boolean getIsUnbookable(){
        Boolean result = false;
        if (getAppointment().getPatient()!=null)
            result = getAppointment().getPatient().getIsPatientMarkedUnbookable();
        else result = false;
        return result;
    }
}
