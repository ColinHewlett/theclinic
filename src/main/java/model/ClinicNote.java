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
public class ClinicNote extends Entity implements IEntityStoreActions{

    public ClinicNote(){
        this.setIsClinicNote(true);
    }
    
    public ClinicNote(Integer key){
        this.setIsClinicNote(true);
        this.key = key;
    }
    
    public ClinicNote(Appointment appointment){
        this.setIsClinicNote(true);
        this.appointment = appointment;
    }
    
    private ArrayList<ClinicNote> collection = null;
    public ArrayList<ClinicNote> get(){
        return collection;
    }
    public void set(ArrayList<ClinicNote> value){
        collection = value;
    }
    
    private Integer key = null;
    public Integer getKey(){
        return key;
    }
    public void setKey(Integer value){
        key = value;
    }
    
    private String notes = null;
    public String getNotes(){
        return notes;
    }
    public void setNotes(String value){
        notes = value;
    }
    
    private Appointment appointment = null;
    public Appointment getAppointment(){
        return appointment;
    }
    public void setAppointment(Appointment value){
        appointment = value;
    }
    
    public Integer getPatientKey(){
        return super.getValue().x;
    }
    public void setPatientKey(Integer key){
        super.setValue(new Point(key,0));
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
    public ClinicNote read()throws StoreException{
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
