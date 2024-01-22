/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import repository.StoreException;//01/03/2023
import java.awt.Point;

/**
 *
 * @author colin
 */
public class Entity implements IStoreClient{
    
    private Boolean isAppointment = false;
    private Boolean isAppointmentDate = false;
    private Boolean isPatient = false;
    private Boolean isPatientNotification = false;
    private Boolean isTableRowValue = false;
    private Boolean isPMSStore = false;
    private Boolean isSurgeryDaysAssignment = false;
    private Boolean isPatientNote = false;
    private Scope scope = null;
    private Point value = null;
    protected Boolean isDeleted = false;
    protected Boolean isCancelled = false;
    
    public Boolean getIsCancelled(){
        return isCancelled;
    }
    
    public Boolean getIsDeleted(){
        return isDeleted;
    }
    
    public void setIsCancelled(Boolean value){
        isCancelled = value;
    }
    
    public void setIsDeleted(Boolean value){
        isDeleted = value;
    }
    
    private void resetAll(){
        setIsAppointment(false);
        setIsPatient(false);
        setIsPatientNotification(false);
        setIsPMSStore(false);
        setIsTableRowValue(false); 
        setIsSurgeryDaysAssignment(false);
        setIsPatientNote(false);
    }
    
    /**
     * defines the scope of the Entity.read() operation
     */
    public enum Scope { ALL,
                        APPOINTEE_REMINDERS,    //all entities
                        CANCELLED,              //cancelled appointments
                        FOR_DAY,                //appointments for this day
                        FOR_PATIENT,            //appointments/notes for this patient
                        DELETED_FOR_PATIENT,    //deleted appointments for this patient (when a deleted patient is being recovered)         
                        DELETED,                //deleted patients
                        FROM_DAY,               //appointmens from this day
                        SINGLE,                 // this entity only
                        UNACTIONED,             //unactioned patient notifications
                        }
    
    public static void createPMSDatabase()throws StoreException{
        /*
        IStoreActions store = Store.FACTORY();
        
        store.create();
*/
    }

    
    public Point getValue(){
        return value;
    }
    
    public void setValue(Point v){
        value = v;
    }
    
    public Scope getScope(){
        return scope;
    }
    
    public void setScope(Scope value){
        scope = value;
    }
    
    public Boolean getIsAppointment(){
        return isAppointment;
    }
    public Boolean getIsAppointmentDate(){
        return isAppointmentDate;
    }

    public Boolean getIsPatient(){
        return isPatient;
    }
    public Boolean getIsPatientNotification(){
        return isPatientNotification;
    }

    public final Boolean getIsTableRowValue(){
        return isTableRowValue;
    }
    public final Boolean getIsPMSStore(){
        return isPMSStore;
    }
    public Boolean getIsSurgeryDaysAssignment(){
        return isSurgeryDaysAssignment;
    } 
    
    public Boolean getIsPatientNote(){
        return isPatientNote;
    }
    
    protected void setIsAppointment(Boolean value){
        if (value) resetAll();
        isAppointment= value;
    }
    protected void setIsAppointmentDate(Boolean value){
        if (value) resetAll();
        isAppointmentDate = value;
    }

    protected void setIsPatient(Boolean value){
        if (value) resetAll();
        isPatient = value;
    }
    protected void setIsPatientNotification(Boolean value){
        if (value) resetAll();
        isPatientNotification = value;
    }

    protected final void setIsTableRowValue(Boolean value){
        if (value) resetAll();
        isTableRowValue = value;
    }
    protected final void setIsPMSStore(Boolean value){
        if (value) resetAll();
        isPMSStore = value;
    }
    protected void setIsSurgeryDaysAssignment(Boolean value){
        if (value) resetAll();
        isSurgeryDaysAssignment = value;
    } 
    protected void setIsPatientNote(Boolean value){
        if (value) resetAll();
        isPatientNote = value;
    }
}
