/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import model.entity.Patient;
import model.entity.Entity;
import java.awt.Point;
import java.util.ArrayList;
import model.IEntityStoreActions;
import repository.Repository;
import repository.StoreException;

/**
 *
 * @author colin
 */
public class PatientCondition extends Entity implements IEntityStoreActions{
    Integer key = null;
    private String comment = null;
   
    private ArrayList<PatientCondition> collection = new ArrayList<>();
    public ArrayList<PatientCondition> get(){
        return collection;
    }
    
    public void set(ArrayList<PatientCondition> value){
        collection = value;
    } 
    
    protected Condition condition = null;
    public Condition getCondition(){
        return condition;
    }
    public void setCondition(Condition value){
        condition = value;
    }
    
    protected Patient patient = null;
    public Patient getPatient(){
        return patient;
    }
    
    public void setPatient(Patient value){
        patient = value;
    }
    
    public String getComment(){
        return comment;
    }
    
    public void setComment(String value){
        comment = value;
    }
     public Integer getKey(){
                return key;
    }
    public void setKey(Integer value){
        key = value;
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
    public PatientCondition read() throws StoreException{
        PatientCondition pc = new Repository().read(this);
        return pc;
    }
    
    @Override
    public void update() throws StoreException{
        new Repository().update(this);
    }
}
