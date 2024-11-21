/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import model.entity.Appointment;
import java.awt.Point;
import java.util.ArrayList;
import model.repository.StoreException;
import model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class ClinicalNote extends Entity implements IEntityRepositoryActions{
    
    

    public ClinicalNote(){
        this.setIsClinicNote(true);
    }
    
    public ClinicalNote(Integer value){
        this.setIsClinicNote(true);
        setKey(value);
    }
    
    public ClinicalNote(Appointment appointment){
        this.setIsClinicNote(true);
        setKey(appointment.getKey());
    }
    
    private ArrayList<ClinicalNote> collection = null;
    public ArrayList<ClinicalNote> get(){
        return collection;
    }
    public void set(ArrayList<ClinicalNote> value){
        collection = value;
    }
    
    private String notes = null;
    public String getNotes(){
        return notes;
    }
    public void setNotes(String value){
        notes = value;
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
    public ClinicalNote read()throws StoreException{
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
