/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import theclinic.model.entity.Entity;
import theclinic.model.entity.Appointment;
import java.awt.Point;
import java.util.ArrayList;
import theclinic.model.repository.StoreException;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class Treatment extends Entity implements IEntityRepositoryActions{
    public Treatment(){
        this.setIsTreatment(true);
    }
    
    public Treatment(Integer key){
        this.setIsTreatment(true);
        setKey(key);
    }
    
    public Treatment(Appointment value){
        this.setIsTreatment(true);
        this.appointment = value;
    }
    
    
    private ArrayList<Treatment> collection = new ArrayList<>();
    public ArrayList<Treatment> get(){
        return collection;
    }
    public void set(ArrayList<Treatment> value){
        collection = value;
    }
    
    private Appointment appointment = null;
    public Appointment getAppointment(){
        return appointment;
    }
    public void setAppointment(Appointment value){
        appointment = value;
    }
    
    private String description = null;
    public String getDescription(){
        return description;
    }
    public void setDescription(String value){
        description = value;
    }
    
    @Override
    public boolean equals(Object obj) 
    { 
        // if both the object references are  
        // referring to the same object. 
        if(this == obj) 
            return true; 

        // checks if the comparison involves 2 objecs of the same type 
        /**
         * issue arise if one of the objects is an entity (for example a Patient) and the other object is its delegate sub class
         */
        //if(obj == null || obj.getClass()!= this.getClass()) 
            //return false; 
        if (obj == null) return false;
        // type casting of the argument.  
        Treatment treatment = (Treatment) obj; 

        // comparing the state of argument with  
        // the state of 'this' Object. 
        return (treatment.getKey().equals(this.getKey())); 
    }
    
    
    
    private boolean state = false;
    public boolean getSttate(){
        return state;
    }
    public void setSttate(boolean value){
        state = value;
    }
    
    @Override
    public Point count()throws StoreException{
        return getRepository().count(this);
    }
    
    @Override
    public void create()throws StoreException{
        getRepository().create(this);
    }
    
    @Override
    public void delete()throws StoreException{
        getRepository().delete(this);
    }
    
    @Override
    public void drop()throws StoreException{
        
    }
    
    @Override
    public Integer insert()throws StoreException{
        Integer value = null;
        value = getRepository().insert(this);
        setKey(value);
        return getKey();
    }
    
    @Override
    public Treatment read()throws StoreException{
        /**
         * if scope = FOR_PATIENT
         * -- patientKey property must have been initialised with the patient key value
         */
        return getRepository().read(this);
    }
    
    @Override
    public void update()throws StoreException{
        getRepository().update(this);
    }
    
}
