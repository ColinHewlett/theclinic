/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import repository.StoreException;//01/03/2023
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author colin
 */
public class Entity implements IStoreClient{
    private String systemDefinitionProperty = null;
    private Boolean isAppointment = false;
    private Boolean isAppointmentDate = false;
    private Boolean isPatient = false;
    private Boolean isPatientNotification = false;
    private Boolean isTableRowValue = false;
    private Boolean isPMSStore = false;
    private Boolean isSurgeryDaysAssignment = false;
    private Boolean isPatientNote = false;
    private Boolean isPrimaryCondition = false;
    private Boolean isSecondaryCondition = false;
    private Boolean isMedication = false;
    private Boolean isDoctor = false;
    private Boolean isClinicNote = false;
    private Boolean isTreatment = false;
    private Boolean isAppointmentTreatment = false;
    private Boolean isPatientPrimaryCondition = false;
    private Boolean isPatientSecondaryCondition = false;
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
        setIsPrimaryCondition(false);
        setIsSecondaryCondition(false);
        setIsMedication(false);
        setIsDoctor(false);
        setIsClinicNote(false);
        setIsTreatment(false);
        setIsAppointmentTreatment(false);
        setIsPatientPrimaryCondition(false);
        setIsPatientSecondaryCondition(false);
    }
    
    /**
     * defines the scope of the Entity.read() operation
     */
    public enum Scope { ALL,
                        APPOINTEE_REMINDERS,    //all entities
                        CANCELLED,              //cancelled appointments
                        FOR_APPOINTMENT,        //clinic note or treatment for appointment
                        FOR_TREATMENT,          //appointment for treatment
                        FOR_DAY,                //appointments for this day
                        FOR_PATIENT,            //appointments/notes for this patient
                        FOR_PRIMARY_CONDITION,  //secondary condition(s) for this primary condition
                        FOR_SECONDARY_CONDITION,
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

    public void setSystemDefinitionProperty(String value){
        systemDefinitionProperty = value;
    }
    public String getSystemDefinitionProperty(){
        return systemDefinitionProperty;
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
    
    public Boolean getIsPrimaryCondition(){
        return isPrimaryCondition;
    }
    
    public Boolean getIsSecondaryCondition(){
        return isSecondaryCondition;
    }
    
    public Boolean getIsMedication(){
        return isMedication;
    }
    
    public Boolean getIsDoctor(){
        return isDoctor;
    }
    
    public Boolean getIsClinicNote(){
        return isClinicNote;
    }
    
    public Boolean getIsTreatment(){
        return isTreatment;
    }
    
    public Boolean getIsAppointmentTreatment(){
        return isAppointmentTreatment;
    }
    
    public Boolean getIsPatientPrimaryCondition(){
        return isPatientPrimaryCondition;
    }
    
    public Boolean getIsPatientSecondaryCondition(){
        return isPatientSecondaryCondition;
    }
    
    protected void setIsPatientSecondaryCondition(Boolean value){
        if (value) resetAll();
        isPatientSecondaryCondition= value;
    }
    
    protected void setIsPatientPrimaryCondition(Boolean value){
        if (value) resetAll();
        isPatientPrimaryCondition= value;
    }
    
    protected void setIsAppointmentTreatment(Boolean value){
        if (value) resetAll();
        isAppointmentTreatment= value;
    }
    
    protected void setIsTreatment(Boolean value){
        if (value) resetAll();
        isTreatment= value;
    }
    
    protected void setIsClinicNote(Boolean value){
        if (value) resetAll();
        isClinicNote= value;
    }
    
    protected void setIsDoctor(Boolean value){
        if (value) resetAll();
        isDoctor= value;
    }
    
    protected void setIsMedication(Boolean value){
        if (value) resetAll();
        isMedication= value;
    }
    public void setIsSecondaryCondition(Boolean value){
        if (value) resetAll();
        isSecondaryCondition= value;
    }
    public void setIsPrimaryCondition(Boolean value){
        if (value) resetAll();
        isPrimaryCondition= value;
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
