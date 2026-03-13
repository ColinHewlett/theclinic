/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import java.awt.Point;
import theclinic.model.entity.Entity;
import java.util.ArrayList;
import theclinic.model.repository.StoreException;
/**
 *
 * @author colin
 */
public abstract class Condition extends Entity {
    Integer key = null;
    private String description = null;
    private ArrayList<Condition> collection = new ArrayList<>();

    public ArrayList<Condition> get(){
        return collection;
    }
    public void set(ArrayList<Condition> value){
        collection = value;
    }    
    public String getDescription(){
        return description;
    }
    public void setDescription(String value){
        description = value;
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
        Condition condition = (Condition) obj; 

        // comparing the state of argument with  
        // the state of 'this' Object. 
        return (condition.getKey().equals(this.getKey())); 
    }

}
