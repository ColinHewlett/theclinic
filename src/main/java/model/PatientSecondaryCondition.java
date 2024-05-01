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
public class PatientSecondaryCondition extends Entity implements IEntityStoreActions{
    private Patient patient = null;
    private String comment = null;
    
    public PatientSecondaryCondition(Patient patient){
        this.patient = patient;
        this.setIsPatientSecondaryCondition(true);
    }
    
    public PatientSecondaryCondition(Patient p, SecondaryCondition sc){
        patient = p;
        secondaryCondition = sc;
        setIsPatientSecondaryCondition(true);
    }
    
    private ArrayList<PatientSecondaryCondition> collection = new ArrayList<>();
    public ArrayList<PatientSecondaryCondition> get(){
        return collection;
    }
    public void set(ArrayList<PatientSecondaryCondition> value){
        collection = value;
    }
    
    public void setComment(String value){
        comment = value;
    }
    public String getComment(){
        return comment;
    }
    
    public void setPatient(Patient value){
        patient = value;
    }
    public Patient getPatient(){
        return patient;
    }
    
    private SecondaryCondition secondaryCondition = null;
    public void setSecondaryCondition(SecondaryCondition p){
        secondaryCondition = p;
    }
    public SecondaryCondition getSecondaryCondition(){
        return secondaryCondition;
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
    public PatientSecondaryCondition read() throws StoreException{
        set(new Repository().read(this).get());
        return this;
    }
    
    @Override
    public void update() throws StoreException{
        //new Repository().update(this);
    }
}
