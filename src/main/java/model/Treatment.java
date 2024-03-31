/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Point;
import java.util.ArrayList;
import repository.Repository;
import repository.StoreException;

/**
 *
 * @author colin
 */
public class Treatment extends Entity implements IEntityStoreActions{
    public Treatment(){
        this.setIsTreatment(true);
    }
    
    public Treatment(Integer key){
        this.setIsTreatment(true);
        this.key = key;
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
    
    private Integer key = null;
    public Integer getKey(){
        return key;
    }
    public void setKey(Integer value){
        key = value;
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
    
    private boolean state = false;
    public boolean getSttate(){
        return state;
    }
    public void setSttate(boolean value){
        state = value;
    }
    
    @Override
    public Point count()throws StoreException{
        return new Repository().count(this);
    }
    
    @Override
    public void create()throws StoreException{
        new Repository().create(this);
    }
    
    @Override
    public void delete()throws StoreException{
        new Repository().delete(this);
    }
    
    @Override
    public void drop()throws StoreException{
        
    }
    
    @Override
    public Integer insert()throws StoreException{
        Integer value = null;
        value = new Repository().insert(this);
        setKey(value);
        return getKey();
    }
    
    @Override
    public Treatment read()throws StoreException{
        /**
         * if scope = FOR_PATIENT
         * -- patientKey property must have been initialised with the patient key value
         */
        return new Repository().read(this);
    }
    
    @Override
    public void update()throws StoreException{
        new Repository().update(this);
    }
    
}
