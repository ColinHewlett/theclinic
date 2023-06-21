/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import model.Appointment;

/**
 *
 * @author colin
 */
public class AppointmentDelegate extends Appointment{
    
    protected AppointmentDelegate(){
        super();
    }
    
    protected AppointmentDelegate(Appointment appointment){
        super.setDuration(appointment.getDuration());
        super.setNotes(appointment.getNotes());
        super.setPatient(appointment.getPatient());
        super.setStart(appointment.getStart());
        super.setScope(appointment.getScope());
        super.setHasPatientBeenContacted(appointment.getHasPatientBeenContacted());
        super.setIsDeleted(appointment.getIsDeleted());
        super.setIsCancelled(appointment.getIsCancelled());
    }
    
    protected Integer getAppointmentKey(){
        return super.getKey();
    }
    protected void setAppointmentKey(Integer key){
        super.setKey(key);
    }
    
    
    
}
