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
public class PrimaryCondition extends Condition implements IEntityStoreActions {
    private Integer key = null;
    private SecondaryCondition secondaryCondition = null;
    private ArrayList<PrimaryCondition> collection = new ArrayList<>();
    private String notes = null;
    private Integer patientKey = null;
    private Patient patient = null;
            
    public PrimaryCondition(){
        setIsPrimaryCondition(true);
    }
    
    public PrimaryCondition(Integer key){
        this.key = key;
        setIsPrimaryCondition(true);
    }
    
    public PrimaryCondition(Patient patient){
        this.patient = patient;
        setIsPrimaryCondition(true);
    }

    public ArrayList<PrimaryCondition> get(){
        return collection;
    }
    public void set(ArrayList<PrimaryCondition> value){
        collection = value;
    }
    
    public Integer getKey(){
                return key;
    }
    public void setKey(Integer value){
        key = value;
    }
    
    public Patient getPatient(){
                return patient;
    }
    public void setPatient(Patient value){
        patient = value;
    }
    
    public SecondaryCondition getSecondaryCondition(){
                return secondaryCondition;
    }
    public void setSecondaryCondition(SecondaryCondition value){
        secondaryCondition = value;
    }

    /*
    public Integer getPatientKey(){
        return patientKey;
    }
    protected void setPatientKey(Integer value){
        patientKey = value;
    }
    */
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
        return new Repository().insert(this);
    }
    
    @Override 
    public PrimaryCondition read()throws StoreException{
        return new Repository().read(this);
    }
    
    @Override 
    public void update()throws StoreException{
        new Repository().update(this);
    }
}
