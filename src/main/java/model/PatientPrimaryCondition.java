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
public class PatientPrimaryCondition extends Entity implements IEntityStoreActions {
    
    public PatientPrimaryCondition(){
        this.setIsPatientPrimaryCondition(true);
    }
    
    public PatientPrimaryCondition(Patient patient){
        this.patient = patient;
        this.setIsPatientPrimaryCondition(true);
    }
    
    public PatientPrimaryCondition(Patient patient, PrimaryCondition primaryCondition){
        this.patient = patient;
        this.primaryCondition = primaryCondition;
        this.setIsPatientPrimaryCondition(true);
    }
    
    private ArrayList<PatientPrimaryCondition> collection = new ArrayList<>();
    public ArrayList<PatientPrimaryCondition> get(){
        return collection;
    }
    public void set(ArrayList<PatientPrimaryCondition> value){
        collection = value;
    }
    
    private Patient patient = null;
    public Patient getPatient(){
        return patient;
    }
    public void setPatient(Patient value){
        patient = value;
    }
    
    private PrimaryCondition primaryCondition = null;
    public PrimaryCondition getPrimaryCondition(){
        return primaryCondition;
    }
    public void setPrimaryCondition(PrimaryCondition value){
        primaryCondition = value;
    }
    
    @Override
    public Point count() throws StoreException{
        return new Repository().count(this);
    }
    
    @Override
    public void create() throws StoreException{
        new Repository().create(this);
    }
    
    @Override
    public void delete() throws StoreException{
        new Repository().delete(this);
    }
    
    @Override
    public void drop() throws StoreException{

    }
    
    @Override
    public Integer insert() throws StoreException{
        return new Repository().insert(this);
    }
    
    @Override
    public PatientPrimaryCondition read() throws StoreException{
        set(new Repository().read(this).get());
        return this;
    }
    
    @Override
    public void update() throws StoreException{
        //new Repository().update(this);
    }
}
