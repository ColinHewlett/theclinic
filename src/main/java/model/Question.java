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
public class Question extends Entity implements IEntityStoreActions {

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
        this.key = key;
    }
    
    private ArrayList<Question> collection = new ArrayList<>();
    public ArrayList<Question> get(){
        return collection;
    }
    public void set(ArrayList<Question> value){
        collection = value;
    }
    
    private Integer key = null;
    public void setKey(Integer value){
        key = value;
    }
    public Integer getKey(){
        return key;
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
    public Question read()throws StoreException{
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
