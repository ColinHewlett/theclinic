/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.entity;

import java.awt.Point;
import java.util.ArrayList;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class Question extends Entity implements IEntityRepositoryActions {

    public enum Category{
        ARE_YOU,
        HAVE_YOU,
        YOU_AND_THE_CLINIC;
    }
    
    public Question(){
        this.setIsQuestion(true);
    }
    
    
    public Question(Integer key){
        this.setIsQuestion(true);
        this.setKey(key);
    }
    
    private ArrayList<Question> collection = new ArrayList<>();
    public ArrayList<Question> get(){
        return collection;
    }
    public void set(ArrayList<Question> value){
        collection = value;
    }
    
    private Category category = null;
    public Category getCategory(){
        return category;
    }
    public void setCategory(String value){
        category = Category.valueOf(value);
    }
    
    private String description = null;
    public void setDescription(String value){
        description = value;
    }
    public String getDescription(){
        return description;
    }
    
    private Integer order = null;
    public void setOrder(Integer value){
        order = value;
    }
    public Integer getOrder(){
        return order;
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
    public Question read()throws StoreException{
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
        Question question = (Question) obj; 

        // comparing the state of argument with  
        // the state of 'this' Object. 
        return (question.getKey().equals(this.getKey())); 
    }
}
