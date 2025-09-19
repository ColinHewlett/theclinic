/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.entity;

import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.Entity;
import java.awt.Point;
import java.util.ArrayList;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class PatientCondition extends Entity implements IEntityRepositoryActions{
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

    @Override
    public Point count() throws StoreException{
        return getRepository().count(this);
    }
    
    @Override
    public void create() throws StoreException{
        getRepository().create(this);
    }
    
    @Override
    public void delete() throws StoreException{
        getRepository().delete(this);
    }
    
    @Override
    public void drop() throws StoreException{

    }
    
    @Override
    public Integer insert() throws StoreException{
        return getRepository().insert(this);
    }
    
    @Override
    public PatientCondition read() throws StoreException{
        PatientCondition pc = getRepository().read(this);
        return pc;
    }
    
    @Override
    public void update() throws StoreException{
        getRepository().update(this);
    }
}
